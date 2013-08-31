/*
 * Copyright (C) 2010 Christopher Chong, Oliver Sinnen and others.
 * 
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or 
 * combining it with Eclipse (or a modified version of that library), 
 * containing parts covered by the terms of the Eclipse Public License - v1.0, 
 * the licensors of this Program grant you additional permission to 
 * convey the resulting work. {Corresponding Source for a non-source form 
 * of such a combination shall include the source code for the parts 
 * of Eclipse used as well as that of the covered work.}
 * 
 */
package nz.ac.auckland.ptjava.builder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nz.ac.auckland.ptjava.PTJavaLog;
import nz.ac.auckland.ptjava.PTJavaPlugin;
import nz.ac.auckland.ptjava.internal.builder.BuildDeltaVisitor;
import nz.ac.auckland.ptjava.internal.builder.BuildVisitor;
import nz.ac.auckland.ptjava.internal.builder.StreamGobbler;
import nz.ac.auckland.ptjava.preferences.PreferenceConstants;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import pt.compiler.ParaTaskParser;

/**
 * A builder for .ptjava files. Implements the builder extension.
 *
 */
public class PTJavaFileBuilder extends IncrementalProjectBuilder {
	
	/**
	 * where the PTJava compiler jar file is
	 */
	private static String fCompilerPath;
	
	/**
	 * whether ptjava files should be built or not
	 */
	private static boolean fCanBuild;
	
	/**
	 * whether ptjava files should be compiled using a custom compiler or the default one
	 */
	private static boolean fUseCustomCompiler;
	
	/**
	 * Listener for when options are changed in project preferences
	 */
	private final IPropertyChangeListener fPropertyChangeListener = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			//  check if event change is for the compiler path
			if (event.getProperty().equals(PreferenceConstants.PTJAVA_COMPILER_PATH)) {
				//  change compiler path
				IPreferenceStore prefs = PTJavaPlugin.getDefault().getPreferenceStore();
				fCompilerPath = prefs.getString(PreferenceConstants.PTJAVA_COMPILER_PATH);
				//System.out.println("file");
			}
			if (event.getProperty().equals(PreferenceConstants.PTJAVA_ASSOCIATE_NATURE)) {
				//  add or remove builder from project
				IPreferenceStore prefs = PTJavaPlugin.getDefault().getPreferenceStore();
				fCanBuild = prefs.getBoolean(PreferenceConstants.PTJAVA_ASSOCIATE_NATURE);
			}
			if (event.getProperty().equals(PreferenceConstants.PTJAVA_USE_CUSTOM_COMPILER)) {
				IPreferenceStore prefs = PTJavaPlugin.getDefault().getPreferenceStore();
				fUseCustomCompiler = prefs.getBoolean(PreferenceConstants.PTJAVA_USE_CUSTOM_COMPILER);
			}
		}
	};
	/**
	 * The Constructor.
	 * Initializes builder with preferences.
	 * Adds a property changed listener for updating when preferences change.
	 */
	public PTJavaFileBuilder() {
		super();
		
		IPreferenceStore prefs= PTJavaPlugin.getDefault().getPreferenceStore(); 
		
		//  set the compiler path
		fCompilerPath= prefs.getString(PreferenceConstants.PTJAVA_COMPILER_PATH);
	
		//  set whether we should build
		fCanBuild= prefs.getBoolean(PreferenceConstants.PTJAVA_ASSOCIATE_NATURE);
		
		//  set whether we should use a custom compiler
		fUseCustomCompiler= prefs.getBoolean(PreferenceConstants.PTJAVA_USE_CUSTOM_COMPILER); 
		
		//  add a property change listener when preferences change
		prefs.addPropertyChangeListener(fPropertyChangeListener);
	}
	/**
	 * Destructor equivalent.
	 * Current implementation removes the class' property change listener from the PTJava Preference page. 
	 */
	public void dispose() {
		//  remove the property change listener before class dies
		PTJavaPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(fPropertyChangeListener);
	}

	/**
	 * Builds the resources in a project. Invokes a {@link BuildVisitor} for full builds, or a ({@link BuildDeltaVisitor} for
	 * incremental and auto builds.
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		
		//  check if user wants ptjava builds
		if (!fCanBuild)
			return null;
		
		//  determine what type of build was requested
		if (kind == FULL_BUILD) {
			//  rebuild all .ptjava files in project
			fullBuild(monitor);
		}
		else {
			IResourceDelta delta = getDelta(getProject());
			if( delta == null) {
				//  delta returns null if builder has not been invoked before,
				//  or if builder has not been invoked for a very long time
				fullBuild(monitor);
			}
			else {
				//  changes detected, build only changed .ptjava resources
				incrementalBuild(delta, monitor);
			}
		}
		
		new Job("Project Build") {
			protected IStatus run(IProgressMonitor monitor) {

				try {
					//  refresh project
					getProject().refreshLocal(IProject.DEPTH_INFINITE, monitor);
					//  invoke a full build
					getProject().build(
							IncrementalProjectBuilder.INCREMENTAL_BUILD,
							JavaCore.BUILDER_ID,
							null, monitor);
					
					//  Getting Markers for test purposes
					IMarker[] markers= getProject().findMarkers(IMarker.PROBLEM, true, IProject.DEPTH_INFINITE);
					for (IMarker i : markers) {
						MarkerManager.getInstance().handleJavaMarker(i);
					}
				}
				catch (CoreException exception) {
					PTJavaLog.logError(exception);
				}
				return Status.OK_STATUS;
			}
		}.schedule();
		return null;
	}

	/**
	 * Current implementation removes all PTJava Problem markers from the project.
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#clean(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void clean(IProgressMonitor monitor) {
		MarkerManager.getInstance().deletePTJavaMarkers(getProject());
	}
	
	/**
	 * Perform a full build. Invoke a BuildVisitor to visit all resources in the project 
	 * @param monitor The progress monitor to show progress to the user.
	 */
	private void fullBuild(IProgressMonitor monitor) {
		if (!MarkerManager.getInstance().deletePTJavaMarkers(getProject()))
			return;
		
		try {
			//  create a BuildVisitor to visit resource and resources children.
			//  since this is called on the project, we visit every resource in 
			//  the workspace
			getProject().accept(new BuildVisitor());
			
			//getProject().refreshLocal(IProject.DEPTH_INFINITE, monitor);
		}
		catch (CoreException e) {
		}
	}

	/**
	 * Perform an incremental build (or auto-build) for the project
	 * 
	 * @param delta The root of the tree representing the changes to the project
	 * @param monitor The progress monitor to show progress to the user.
	 */
	private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
		try {
			//  delta is the root of the tree representing changes in the project
			//  we will traverse the tree to evaluate all changes
			delta.accept(new BuildDeltaVisitor());
			
			//getProject().refreshLocal(IProject.DEPTH_INFINITE, monitor);
		}
		catch (CoreException e) {
		}
	}
	
	//--------------------------------------------------------------------------
	//  COMPILER RELATED METHODS
	/**
	 * Stream that reads in bytes and stores them. Used when invoking the default compiler
	 */
	private static class StringOutputStream extends OutputStream {
		private StringBuffer fBuffer;
		public StringOutputStream(StringBuffer buf) {
			super();
			Assert.isNotNull(buf);
			fBuffer= buf;
		}
		@Override
		public void write(int b) throws IOException {
			fBuffer.append((char)b);
		}
	}
	
	/**
	 * Calls the PTJava Compiler on a source (.ptjava) file. Compilation procedure varies depending on Preferences:
	 * <br/>
	 * <ul>
	 * 	<li>If the Preference store has specified to use the default compiler, a new thread is created to run the default compiler that is packaged with the plug-in.</li>
	 * 	<li>If the Preference store has specified to use a custom compiler, a new JVM is invoked to run the specified custom compiler jar.</li>
	 * </ul>
	 * 
	 * Both cases will capture output from {@link System#err} and report PTJava compiler errors using {@link MarkerManager#reportProblem(IResource, MarkerInfo)}.  
	 * 
	 * 
	 * @param toCompile the full path name to the file to compile.
	 * @param resource the resource that represents the file to compile
	 */
	public static void invokeCompiler(final String toCompile, IResource resource) {
		
		//  check if User wanted to use their own custom compiler
		if (fUseCustomCompiler) {
			if (fCompilerPath.isEmpty()) {
				PTJavaLog.logError("Compiler jar has not been specified in PTJava Preferences.", new Exception());
				return;
			}
			
			//  build string to execute
			String cmd = new String("java -cp ") + fCompilerPath
			+ new String(" paratask.compiler.ParaTaskParser ") + toCompile;
			
			Runtime rt = Runtime.getRuntime();
			try {
				//  execute command
				Process proc = rt.exec(cmd);
				
				//  establish objects to read output of executed program
				StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
				StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
				
				errorGobbler.start();
				outputGobbler.start();
				
				//  block until compiler finished
				proc.waitFor();
				
				errorGobbler.join();
				outputGobbler.join();
				
				//System.out.println("errorGobbler.getMsg(): " + errorGobbler.getMsg());
				
				if (!errorGobbler.getMsg().isEmpty()) {
					//  error stream was not empty .: some error occured
					//  the error message is stored in errorGobbler.getMsg()
					//  NOTE: the error message is truncated from what the compiler
					//  puts out, because there's a lot of junk that is irrelevant
					String s= errorGobbler.getMsg();
					MarkerInfo m= createMarkerFromErrorString(s);
					MarkerManager.getInstance().reportProblem(resource, m);
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			//  User chose to use the default compiler
			
			//  for invoking from the same JVM using a different thread
			//  This way requires PTCompiler.jar to be specified on the class path in
			//  the manifest file, and means the user does not ever specify the jar file
			Runnable r= new Runnable() {
				@Override
				public void run() {
					File start= new File(toCompile);
					try {
						ParaTaskParser.parse(start);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			
			Thread t= new Thread(r);

			StringBuffer sb= new StringBuffer(4096);
			PrintStream printStream= new PrintStream(new StringOutputStream(sb));
			try {
				//  redirecting error stream while compiler runs
				System.setErr(printStream);
			}
			catch (SecurityException e) {
				e.printStackTrace();
			}
			
			t.run();
			try {
				t.join();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			String errString= sb.toString();
			
			try {
				//  return error stream
				System.setErr(System.err);
			}
			catch (SecurityException e) {
				e.printStackTrace();
			}
			if (!errString.isEmpty() &&
					errString.startsWith("********* Failed to parse")) {
				//  parse the error string
				
				MarkerInfo m= createMarkerFromErrorString(errString);
				MarkerManager.getInstance().reportProblem(resource, m);
			}
		}
	}
	/**
	 * Creates a marker from the given error string.
	 * @param errorString The error string. Should be similar to: <code>japa.parser.ParseException: Encountered "<" at line 6, column 23.</code>
	 * @return The MarkerInfo object that represents the error
	 */
	private static MarkerInfo createMarkerFromErrorString(String errorString) {
		Pattern pattern= Pattern.compile("paratask\\.compiler\\.parser\\.ParseException: Encountered \".*\" at (line (\\d+)), column (\\d)+\\.");
		Matcher matcher= pattern.matcher(errorString);
		
		MarkerInfo m = new MarkerInfo();
		if (matcher.find()) {
			//m.fMsg= matcher.group();
			
			m.fMsg= errorString.substring(0, errorString.indexOf("at paratask.compiler.parser.JavaParser"));
			
			m.fLocation= matcher.group(1);
			m.fLineNumber= Integer.parseInt(matcher.group(2));
		}
		else {
			m.fMsg= errorString;
		}
		return m;
	}
	 
	//--------------------------------------------------------------------------
	//  UTILITY METHODS
	
	/**
	 * The ID for the builder.
	 */
	public static final String BUILDER_ID = PTJavaPlugin.PLUGIN_ID + ".ptjavaFileBuilder";
	
	/**
	 * Associates the PTJava builder with the given project.
	 * @param project The project to add the builder to
	 */
	public static void addBuilderToProject(IProject project) {
		//  cannot modify closed projects
		if (!project.isOpen())
			return;
		
		//  get the description
		IProjectDescription description;
		try {
			description = project.getDescription();
		}
		catch (CoreException exception) {
			PTJavaLog.logError(exception);
			return;
		}
		
		ICommand[] cmds = description.getBuildSpec();
		int defaultBuilderId = -1;
		for (int j = 0; j < cmds.length; j++) {
			if (cmds[j].getBuilderName().equals(BUILDER_ID))
				return;
			if (cmds[j].getBuilderName().equals(JavaCore.BUILDER_ID))
				defaultBuilderId = j;
		}
		
		// Associate builder with project.
	    ICommand newCmd = description.newCommand();
	    newCmd.setBuilderName(BUILDER_ID);
	    List<ICommand> newCmds = new ArrayList<ICommand>();
	    newCmds.addAll(Arrays.asList(cmds));
	    
	    if (defaultBuilderId < 0)
	    	newCmds.add(newCmd);
	    else
	    	newCmds.add(defaultBuilderId, newCmd);
	    
	    description.setBuildSpec((ICommand[]) newCmds.toArray(new ICommand[newCmds.size()]));
	    
	    //  set the project description
	    try {
	    	project.setDescription(description, null);
	    }
	    catch (CoreException e) {
	    	PTJavaLog.logError(e);
	    }
	}
	
	/**
	 * Removes the builder from the given project. Does nothing if the project is not open 
	 * or does not have the builder associated with it.
	 * @param project The project to remove the builder from
	 */
	public static void removeBuilderFromProject(IProject project) {

		// Cannot modify closed projects.
		if (!project.isOpen())
			return;

	    // Get the description.
	    IProjectDescription description;
	    try {
	    	description = project.getDescription();
	    }
	    catch (CoreException e) {
	    	PTJavaLog.logError(e);
	        return;
	    }

	    // Look for builder.
	    int index = -1;
	    ICommand[] cmds = description.getBuildSpec();
	    for (int j = 0; j < cmds.length; j++) {
	    	if (cmds[j].getBuilderName().equals(BUILDER_ID)) {
	    		index = j;
	    		break;
	    	}
	    }
	    if (index == -1)
	    	return;

	    // Remove builder from project.
	    List<ICommand> newCmds = new ArrayList<ICommand>();
	    newCmds.addAll(Arrays.asList(cmds));
	    newCmds.remove(index);
	    description.setBuildSpec((ICommand[]) newCmds.toArray(new ICommand[newCmds.size()]));
	    try {
	    	project.setDescription(description, null);
	    }
	    catch (CoreException e) {
	    	PTJavaLog.logError(e);
	    }
	}
}

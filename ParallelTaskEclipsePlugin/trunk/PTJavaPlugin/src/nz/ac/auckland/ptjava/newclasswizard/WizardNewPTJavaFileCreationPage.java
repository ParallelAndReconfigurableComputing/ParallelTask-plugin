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
package nz.ac.auckland.ptjava.newclasswizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

/**
 * Page that is shown the the User to create a new PTJava class file.
 *
 */
public class WizardNewPTJavaFileCreationPage extends WizardNewFileCreationPage {
	//  class for organizing SWT components on page
	private final class CodeCreationGroup {
		private Group fGroup;
		private Button fPackageButton;
		private Button fClassButton;

		public CodeCreationGroup(Composite parent) {
			//  Group for code generation
			fGroup = new Group(parent, SWT.NONE);
			GridLayout gridLayout = new GridLayout(); 
			fGroup.setLayout(gridLayout);
			fGroup.setText("Code Generation Options");
	        fGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			//  create a check button for generating package declaration	
			fPackageButton = new Button(fGroup, SWT.CHECK);
			fPackageButton.setText("Generate package declaration");
			fPackageButton.setSelection(true);
			
			//  create a check box for generating class name
			fClassButton = new Button(fGroup, SWT.CHECK);
			fClassButton.setText("Generate class declaration");
			fClassButton.setSelection(true);
		}
		public boolean getGeneratePackage() {
			return fPackageButton.getSelection();
		}
		public boolean getGenerateClass() {
			return fClassButton.getSelection();
		}
	}
	
	private CodeCreationGroup fCodeCreationGroup;
	private IWorkbench fWorkbench;
	
	/**
	 * The Constructor.
	 * @param pageName The name of the page
	 * @param workbench The workbench to open up created class files for editing
	 * @param selection The current resource selection
	 */
	public WizardNewPTJavaFileCreationPage(String pageName,
			IWorkbench workbench,
			IStructuredSelection selection) {
		super(pageName, selection);
		fWorkbench = workbench;
	}

	/**
	 * Creates the contents to display on the page.
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		this.setFileExtension("ptjava");
		
		Composite composite = (Composite)getControl();
        fCodeCreationGroup = new CodeCreationGroup(composite);
	}
	
	/**
	 * Creates the new PTJava file and opens it in the workbench.
	 * @return <code>true</code> if the file is successfully opened, <code>false</code> otherwise
	 */
	public boolean finish() {
		//  create the file
		IFile file = createNewFile();
		
		//  display the file
		IWorkbenchWindow window = fWorkbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		try {
			IDE.openEditor(page, file, true);
		} catch (PartInitException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Creates the initial contents to be placed inside the newly created PTJava class file.
	 * Current version will generate package declaration and basic class declaration if
	 * those options are checked on the wizard page.
	 */
	@Override
	protected InputStream getInitialContents() {
		//  String buffer for appending to file
		StringBuffer sb = new StringBuffer();
		if (fCodeCreationGroup.getGeneratePackage()) {
			IPath containerFullPath= getContainerFullPath();
			
			//  the original string to the container path
			String pathString= containerFullPath.toString();
			//System.out.println(pathString);
			
			int slashIndex= pathString.indexOf('/', 1);
			String truncatedPath= null;
			
			if (slashIndex > 0)
				truncatedPath= pathString.substring(0, slashIndex);
			else
				truncatedPath= pathString;
			
			//  find the project src folder path
			IProject project= JavaPlugin.getWorkspace().getRoot().getProject(truncatedPath);
			IJavaProject javaProject= JavaCore.create(project);
			if (javaProject != null) {
				try {
					String srcPath= null; 
					IClasspathEntry[] cp= javaProject.getRawClasspath();
					for (IClasspathEntry i : cp) {
						//System.out.println("i:"+i);
						if (i.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
							//  src entry found!
							srcPath= i.getPath().toString();
							//System.out.println("srcPath: " + srcPath);
							break;
						}
					}
					if (srcPath != null) {
						if (pathString.equals(srcPath)) {
							//  do nothing
						}
						else {
							String s= null;
							//  omit src path if part of path string
							if (pathString.startsWith(srcPath))
								s= pathString.substring(srcPath.length()+1);  
							//  the +1 is to remove the first "/" after the src path
							else
								s= pathString;
			
							//  currently looks like "some/path/to/file", we want "some.path.to.file"
							s= s.replace('/', '.');
							
							//  append it
							//  should be something like "package some.path;"
							sb.append("package ");
							sb.append(s);
							sb.append(";");
							sb.append("\n\n");
							//System.out.println("s: " + s);
						}
					}
				}
				catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
		}
			
		if (fCodeCreationGroup.getGenerateClass()) {
			sb.append("public class ");
			String filename= getFileName();
			if (filename.contains("."))
				filename= filename.substring(0, filename.indexOf('.'));
			sb.append(filename);
			sb.append(" {\n\t\n}");
		}
		if (sb.length() == 0)
			return null;
		return new ByteArrayInputStream(sb.toString().getBytes());
	}
}

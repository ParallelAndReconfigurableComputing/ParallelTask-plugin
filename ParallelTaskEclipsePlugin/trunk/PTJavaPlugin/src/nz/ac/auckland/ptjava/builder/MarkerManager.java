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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nz.ac.auckland.ptjava.PTJavaLog;
import nz.ac.auckland.ptjava.PTJavaPlugin;
import nz.ac.auckland.ptjava.internal.resources.FileResourceManager;
import nz.ac.auckland.ptjava.preferences.PreferenceConstants;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Singleton for handling the PTJava markers and the translation of Java markers to PTJava markers in the ptjava source
 *
 */
public class MarkerManager {
	public static final String MARKER_ID = PTJavaPlugin.PLUGIN_ID + ".ptjavamarker";
	
	public static MarkerManager getInstance() {
		if (fgManager == null)
			fgManager= new MarkerManager();
		return fgManager;
	}
	private static MarkerManager fgManager;  //  the singleton instance of the MarkerManager
	
	private Pattern fLinePattern;	//  regex pattern for determining line numbers for Java-to-PTJava marker translations

	/**
	 * Private constructor
	 */
	private MarkerManager() {
		
		fLinePattern= Pattern.compile("####\\[(\\d+)\\]####");
		//  NOTE: the parentheses are needed to identify matcher groups
	}
	
	
	/**
	 * Handles the translation of a Java Problem marker into a PTJava Problem marker.
	 * <br/><br/>
	 * For example, if the given marker is associated with a resource named <b>"Example.java"</b>,
	 * this method will look for a resource named <b>"Example.ptjava"</b> in the same location as
	 * <b>"Example.java"</b>. It will then proceed to create a new problem marker for <b>"Example.ptjava"</b>,
	 * based on the given marker.
	 * <br/><br/>
	 * Uses the <code>"####[\d]####"</code> comments in the java file to determine the line number to place the
	 * PTJava Problem marker.
	 * 
	 * Does nothing if any of the following conditions are true:
	 * <ul>
	 * 	<li>The given marker's resource does not have the java file extension: "java"</li>
	 * 	<li>There is no equivalent PTJava file to place the marker.</li>
	 * 	<li>The given marker does not have an IMarker.CHAR_START attribute</li>
	 * 	<li>The given marker does not have an IMarker.CHAR_END attribute</li>
	 * 	<li>A string matching the regular expression: "####[\d]####" cannot be found in the Java file</li>
	 *  <li>A string matching the statement that caused the error cannot be found on the given line number of the PTJava file</li>
	 * </ul>
	 * @param marker The marker to translate.
	 */
	public void handleJavaMarker(IMarker marker) {
		
		IResource resource= marker.getResource();
		String resourceExtension = resource.getFileExtension();
		if (resourceExtension == null || !resourceExtension.equals("java"))
			return;
		
		int charStart= marker.getAttribute(IMarker.CHAR_START, -1);
		if (charStart < 0)
			return;
		int charEnd= marker.getAttribute(IMarker.CHAR_END, -1);
		if (charEnd < 0)
			return;
		
		//  get the ptjava resource that generated this resource
		IResource ptResource= findPTFile(resource);
		if (ptResource == null)
			return;
		
		//  get the source
		String source= FileResourceManager.getInstance().loadSource(resource);
		if (source.isEmpty())
			return;
		
		//  get the pt source
		String ptSource= FileResourceManager.getInstance().loadSource(ptResource);
		if (ptSource.isEmpty())
			return;
		
		//  find the line number
		
		Matcher lineMatcher= fLinePattern.matcher(source);
		
		try {
			if (lineMatcher.find(charStart)) {
				int lineNum= Integer.parseInt(lineMatcher.group(1));
				int offsetStart= findOffsetOfLine(ptSource, lineNum);
				if (offsetStart < 0)
					return;
				
				// NOTE:
				// original code tries to find the first semicolon from error offset
				// (to account for whitespace?) but it appears compiler will mark 
				// the line number even if a single statement is spread over many
				// lines, so for now we'll just look at the line specified by the 
				// PTJava compiler. otherwise unrelated code may be highlighted.
				int offsetEnd = findOffsetOfLine(ptSource, lineNum + 1);
				if (offsetEnd < 0)
					offsetEnd = ptSource.length();
				
				/*
				int offsetEnd= offsetStart;
				boolean foundStatementEnd= false;
				while (offsetEnd < ptSource.length()) {
					char c= ptSource.charAt(offsetEnd);
					if (c == '\n') {
						if (foundStatementEnd)
							break;
					}
					else if (ptSource.charAt(offsetEnd) == ';') {
						foundStatementEnd= true;
					}
					offsetEnd++;
				}
				*/
					
				String line= ' ' + ptSource.substring(offsetStart, offsetEnd) + '\n';
				//  NOTE: the ' ' at the start is to allow the matcher to match expression that begins
				//    at the start of a line
				//  NOTE: the +1 to offsetEnd is to include the \n character
				//System.out.println(" line["+lineNum+"]: "+line);
				
				//  get the statement that caused the error
				String err= source.substring(charStart, charEnd);

				int prevOccurances= 0;
				try {
					String srcLine= source.substring(findOffsetOfLine(source, (Integer)marker.getAttribute(IMarker.LINE_NUMBER)), charStart);
					Pattern srcErrPattern= Pattern.compile("(\\Q"+err+"\\E)[^\\w]+");
					Matcher srcErrMatcher= srcErrPattern.matcher(srcLine);
					while(srcErrMatcher.find()) {
						prevOccurances++;
					}
				}
				catch(CoreException e) {
					e.printStackTrace();
					return;
				}
				
				//  look for special case where the error begins with __pt__
				//  NOTE: not really a fantastic solution =)
				if (err.startsWith("__pt__"))
					err= err.substring("__pt__".length(), err.length());
	
				Pattern errorStatementPattern= Pattern.compile("[^\\w]+(\\Q"+err+"\\E)[^\\w]+");
				Matcher matcher= errorStatementPattern.matcher(line);
				int findCount= 0;
				while (matcher.find()) {
					if (findCount == prevOccurances) {
						//  fill in data
						MarkerInfo m= new MarkerInfo();
						m.fMsg= marker.getAttribute(IMarker.MESSAGE, "Unknown Error");
						m.fCharStart= new Integer(offsetStart + matcher.start(1) - 1);
						//  NOTE: the -1 is for the extra character we added into the sample line
						m.fCharEnd= new Integer(m.fCharStart + err.length());
						m.fLocation= new String("line "+lineNum);
						m.fSeverity= marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
						if (!resourcePTMarkerExists(ptResource, m)) {
							reportProblem(ptResource, m);
							
							// remove problem marker in .java file based on setting in preferences
							IPreferenceStore prefs= PTJavaPlugin.getDefault().getPreferenceStore();
							if (!prefs.getBoolean(PreferenceConstants.PTJAVA_SHOW_HIDDEN_ERRORS)) {
								try {
									marker.delete();
								} catch (CoreException e) {
									PTJavaLog.logError(e);
								}
							}
						}
						break;
					}	
					findCount++;
				}
			}
		}
		catch (IndexOutOfBoundsException e) {
			PTJavaLog.logError(e);
		}
	}
	/**
	 * Returns the IResource representing the PTJava file used to generate the given resource.
	 * Returns null if the given resource is null, does not exist, or does not have the file extension "java" 
	 * @param resource The resource that was generated by the desired PTJava file. Should be a file with 
	 * a ".java" extension.
	 * @return The resource corresponding to the PTJava file or null.
	 */
	private IResource findPTFile(IResource resource) {
		if (resource ==null || !resource.exists())
			return null;
		
		IPath path= resource.getProjectRelativePath();
		if (path == null)
			return null;
		//System.out.println("path:"+path);
		StringBuffer sb= new StringBuffer(path.toString());
		sb.delete(sb.length()-5, sb.length());
		sb.append(".ptjava");
		String s= sb.toString();
		//System.out.println(s);
		
		IProject project= resource.getProject();
		IResource ptResource= project.findMember(new Path(s));
		if (ptResource == null)
			return null;
		
		//System.out.println("ptResource: "+ptResource);
			
		return ptResource;
	}
	
	/**
	 * Finds the char offset of a given line.
	 * @param source The source file
	 * @param line The line number
	 * @return The zero relative char offset of the given line, or -1
	 */
	private int findOffsetOfLine(String source, int line) {
		if (line < 1)
			return -1;
		int count= 1;
		int index= 0;
		try {
			while (count < line) {
				if (source.charAt(index) == '\n')
					count++;
				index++;
			}
			return index;
		}
		catch (IndexOutOfBoundsException e) {
			return -1;
		}
	}
	/**
	 * Creates a Problem Marker for the given resource
	 * @param resource The resource to create the marker on
	 * @param data The data representing the marker
	 */
	public void reportProblem(IResource resource, MarkerInfo data) {
		try {
			IMarker marker= resource.createMarker(MARKER_ID);
			marker.setAttribute(IMarker.MESSAGE, data.fMsg);
			if (data.fSeverity != null) {
				marker.setAttribute(IMarker.SEVERITY, data.fSeverity);
			}
			else {
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			}
			if (data.fCharStart != null)
				marker.setAttribute(IMarker.CHAR_START, data.fCharStart);
			if (data.fCharEnd != null)
				marker.setAttribute(IMarker.CHAR_END, data.fCharEnd);
			if (data.fLineNumber != null)
				marker.setAttribute(IMarker.LINE_NUMBER, data.fLineNumber);
			if (data.fLocation != null)
				marker.setAttribute(IMarker.LOCATION, data.fLocation);
		}
		catch (CoreException e) {
			PTJavaLog.logError(e);
		}
	}
	/**
	 * Determines whether a PTJava Problem Marker with the same info already exists on the resource.
	 * @param resource The resource to search for markers on.
	 * @param data The marker data to find a similar math to.
	 * @return <code>true</code> if a similar marker exists on the resource, <code>false</code> otherwise.
	 */
	private boolean resourcePTMarkerExists(IResource resource, MarkerInfo data) {
		try {
			IMarker[] markers= resource.findMarkers(MARKER_ID, true, IResource.DEPTH_ZERO); 
			for (IMarker marker : markers) {
				if ((data.fMsg == null || data.fMsg.equals(marker.getAttribute(IMarker.MESSAGE))) &&
					(data.fCharStart == null || data.fCharStart.equals(marker.getAttribute(IMarker.CHAR_START))) &&
					(data.fCharEnd == null || data.fCharEnd.equals(marker.getAttribute(IMarker.CHAR_END))) &&
					(data.fLineNumber == null || data.fLineNumber.equals(marker.getAttribute(IMarker.LINE_NUMBER))) &&
					(data.fLocation == null || data.fLocation.equals(marker.getAttribute(IMarker.LOCATION))) &&
					(data.fSeverity == null || data.fSeverity.equals(marker.getAttribute(IMarker.SEVERITY)))) {
					return true;
				}
			}
			return false;
		}
		catch (CoreException e) {
			PTJavaLog.logError(e);
		}
		return true;
	}
	
	//--------------------------------------------------------------------------
	//  MARKER METHODS

	/**
	 * Removes markers from the given resource.
	 * @param resource The resource to remove the markers from.
	 * @return <code>true</code> if markers removed successfully, <code>false</code> otherwise.
	 */
	public boolean deletePTJavaMarkers(IResource resource) {
		try {
			resource.deleteMarkers(MARKER_ID, false, IResource.DEPTH_INFINITE);
			return true;
		}
		catch (CoreException e) {
			PTJavaLog.logError(e);
			return false;
		}
	}

}

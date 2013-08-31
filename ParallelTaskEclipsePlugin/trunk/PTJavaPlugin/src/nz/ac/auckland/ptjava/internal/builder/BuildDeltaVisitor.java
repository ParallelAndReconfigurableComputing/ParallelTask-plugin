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
package nz.ac.auckland.ptjava.internal.builder;

import nz.ac.auckland.ptjava.builder.MarkerManager;
import nz.ac.auckland.ptjava.builder.PTJavaFileBuilder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * The visitor for incremental project builds. Instances of this can be used to 
 * visit all files that have been modified since the last save.
 *
 */
public class BuildDeltaVisitor implements IResourceDeltaVisitor {
	/**
	 * The Constructor. Currently does nothing.
	 */
	public BuildDeltaVisitor() {
		
	}
	/**
	 * Visits each resource node in the delta tree, starting at the workspace root.
	 * If the node in the tree is a .ptjava file, invokes {@link PTJavaFileBuilder#invokeCompiler(String, IResource)}
	 * on the file.
	 * 
	 * Does not visit the Java output location (default: bin/) if such a location is specified in the Java Project 
	 * by {@link IJavaProject#getOutputLocation()}.
	 * 
	 */
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		
		//  get the resource this change represented
		IResource resource= delta.getResource();

		String relPath= resource.getFullPath().toString();
		
		int index= relPath.lastIndexOf('/');
		if (index == -1) {
			return false;
		}
		relPath= relPath.substring(0, index);
		
		//  After removing the last '/', we should be left with a path like:
		//  "/project_name" or "/project_name/bin", depending on whether the User wanted 
		//  separate src and bin folders or not.
		//  Test if we have the first, or second case
		if (relPath.lastIndexOf('/') > 0) {
			//  latter case
			IJavaProject javaProj= JavaCore.create(resource.getProject());
			if (javaProj == null) {
				return false;
			}
			String outputPath= javaProj.getOutputLocation().toString();
			
			//  .ptjava files tend to get created in the bin directory when
			//  Java builder is invoked.
			//  we don't want to build anything in the bin directory, or subdirectories
			if (relPath.equals(outputPath)) {
				return false;
			}
		}
		
		//  find the absolute file path to the resource
		//  this way, files can be built even if they are not in workspace directory
		String fullPath= resource.getLocationURI().getPath();

		if (fullPath.endsWith(".ptjava")) {
			//  delete the resource markers
			MarkerManager.getInstance().deletePTJavaMarkers(resource);
			
			//  invoke the compiler
			PTJavaFileBuilder.invokeCompiler(fullPath, delta.getResource());
		}
		//  return true to visit children as well
		return true;
	}
}

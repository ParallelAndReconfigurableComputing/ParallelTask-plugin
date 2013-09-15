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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nz.ac.auckland.ptjava.PTJavaLog;
import nz.ac.auckland.ptjava.PTJavaPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Class for handling the PTJava nature (for associating the PTJava builder with the project).
 * 
 * When the nature is added to the project, this class is 
 * instantiated and setProject() is called, followed by
 * configure(). deconfigure() is called when the nature
 * is removed from the project
 *
 */

public class PTJavaFileBuilderNature implements IProjectNature {

	/**
	 * The ID for this nature.
	 */
	public static final String NATURE_ID = PTJavaPlugin.PLUGIN_ID + ".ptjavaFileBuilderNature";
	//  NOTE: this must be the same as the declared name for the nature in MANIFEST.MF
	
	private IProject fProject;

	/**
	 * Configures the nature for this project.
	 * Current implementation adds the PTJava file builder to the project,
	 * then orders a full build as a background Job.
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	@Override
	public void configure() throws CoreException {
		//  associate this builder with the project
		PTJavaFileBuilder.addBuilderToProject(fProject);
		
		//  issue order to rebuild project
		new Job("PTJava File Build") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					//  invoke a full build
					fProject.build(
							PTJavaFileBuilder.FULL_BUILD,
							PTJavaFileBuilder.BUILDER_ID,
							null, monitor);
				}
				catch (CoreException exception) {
					PTJavaLog.logError(exception);
				}
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	/**
	 * Deconfigures the nature for this project.
	 * The current implementation removes the PTJava builder from the project.
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	@Override
	public void deconfigure() throws CoreException {
		PTJavaFileBuilder.removeBuilderFromProject(fProject);
		//  TO DO: delete markers here
	}


	/**
	 * Returns the project to which this nature belongs.
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	@Override
	public IProject getProject() {
		return fProject;
	}

	/**
	 * Sets the project to which this nature belongs.
	 *  
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	@Override
	public void setProject(IProject project) {
		this.fProject = project;
	}

	
	/**
    * Add the nature to the specified project if it does not already have it.
    * 
    * @param project the project to be modified
    */
	public static void addNature(IProject project) {
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

		// Determine if the project already has the nature.
		List<String> newIds = new ArrayList<String>();
		newIds.addAll(Arrays.asList(description.getNatureIds()));
		int index = newIds.indexOf(NATURE_ID);
		if (index != -1)
			return;
	
		// Add the nature
		newIds.add(NATURE_ID);
		description.setNatureIds(newIds.toArray(new String[newIds.size()]));
	
		// Save the description.
		try {
			project.setDescription(description, null);
		}
		catch (CoreException e) {
			PTJavaLog.logError(e);
		}
	}

   /**
    * Determine if the specified project has the receiver's nature associated
    * with it.
    * 
    * @param project the project to be tested
    * @return <code>true</code> if the specified project has the receiver's
    *         nature, else <code>false</code>
    */
	public static boolean hasNature(IProject project) {
		try {
			return project.isOpen() && project.hasNature(NATURE_ID);
		}
		catch (CoreException e) {
			PTJavaLog.logError(e);
			return false;
		}
	}

	/**
	 * Remove the nature from the specified project if it has the nature
	 * associated.
	 * 
	 * @param project the project to be modified
	 */
	public static void removeNature(IProject project) {

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

		// Determine if the project has the nature.
		List<String> newIds = new ArrayList<String>();
		newIds.addAll(Arrays.asList(description.getNatureIds()));
		int index = newIds.indexOf(NATURE_ID);
		if (index == -1)
			return;
      
		// Remove the nature
		newIds.remove(index);
		description.setNatureIds(newIds.toArray(new String[newIds.size()]));

		// Save the description.
		try {
			project.setDescription(description, null);
		}
		catch (CoreException e) {
			PTJavaLog.logError(e);
		}
	}
}

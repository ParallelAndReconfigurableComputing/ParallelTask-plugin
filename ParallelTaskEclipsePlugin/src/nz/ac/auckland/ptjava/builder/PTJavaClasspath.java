/*
 * Copyright (C) 2013 Haoming Ma, Oliver Sinnen and others.
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
import java.util.List;

import nz.ac.auckland.ptjava.PTJavaPlugin;
import nz.ac.auckland.ptjava.preferences.PTJavaPreferencePage;
import nz.ac.auckland.ptjava.preferences.PreferenceConstants;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;

public class PTJavaClasspath {

	/**
	 * Add the PTRuntime.jar to the classpath of javaProject
	 * 
	 * see Platform Plug-in developers guide > Programmer's Guide > Resources overview > Modifying the workspace
	 * @param javaProject
	 * @throws CoreException
	 */
	public static void addPTRuntimeToClasspath(final IJavaProject javaProject) throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) {
				addPTRuntimeToClasspath(javaProject, monitor);
			}
		};

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.run(runnable, null);
	}

	/**
	 * Add the PTRuntime.jar to the classpath of javaProject
	 * 
	 * @param javaProject
	 * @param monitor
	 */
	public static void addPTRuntimeToClasspath(IJavaProject javaProject,
			IProgressMonitor monitor) {
		try {
			IClasspathEntry entries[] = javaProject.getRawClasspath();
			List<IClasspathEntry> list = new ArrayList<IClasspathEntry>(
					entries.length + 1);
			for (int i = 0; i < entries.length; i++) {
				list.add(entries[i]);
			}

			// add the java runtime library
			IPath path = null;
			IPreferenceStore prefs = PTJavaPlugin.getDefault()
					.getPreferenceStore();
			if (prefs.getBoolean(PreferenceConstants.PTJAVA_USE_CUSTOM_RUNTIME)) {
				path = new Path(
						prefs.getString(PreferenceConstants.PTJAVA_RUNTIME_PATH));
			} else {
				path = PTJavaPreferencePage.getDefaultRuntimeJarPath();
			}
			list.add(JavaCore.newLibraryEntry(path, null, null));

			IClasspathEntry updatedEntries[] = new IClasspathEntry[list.size()];
			list.toArray(updatedEntries);

			javaProject.setRawClasspath(updatedEntries, monitor);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
}

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
package nz.ac.auckland.ptjava.commands;

import nz.ac.auckland.ptjava.PTJavaLog;
import nz.ac.auckland.ptjava.builder.PTJavaClasspath;
import nz.ac.auckland.ptjava.builder.PTJavaFileBuilderNature;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class AddingPTNatureHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// get workbench window
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		// set selection service
		ISelectionService service = window.getSelectionService();
		// set structured selection
		IStructuredSelection structured = (IStructuredSelection) service
				.getSelection();

		IProject project = null;
		IJavaProject javaProject = null;

		if (structured.getFirstElement() instanceof IJavaProject) {
			javaProject = (IJavaProject) structured.getFirstElement();
			project = javaProject.getProject();
		} else if (structured.getFirstElement() instanceof IProject) {
			project = (IProject) structured.getFirstElement();
			// project.isNatureEnabled("org.eclipse.jdt.core.javanature")
			try {
				if (project.hasNature(JavaCore.NATURE_ID)) {
					javaProject = JavaCore.create(project);
				}
			} catch (CoreException e) {
				PTJavaLog.logError(e);
			}
		}

		if (project != null && javaProject != null) {
			PTJavaFileBuilderNature.addNature(project);
			
			try {
				PTJavaClasspath.addPTRuntimeToClasspath(javaProject);
			} catch (CoreException e) {
				PTJavaLog.logError(e);
			}
			PTJavaLog.logInfo("ParaTask Nature added to project: " + project.getName());
		}
		
		return null;
	}
}

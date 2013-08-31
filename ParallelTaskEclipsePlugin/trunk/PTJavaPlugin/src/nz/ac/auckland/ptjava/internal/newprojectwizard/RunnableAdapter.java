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
package nz.ac.auckland.ptjava.internal.newprojectwizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * An adapter for running an IWorkspaceRunnable as an IRunnableWithProgress
 *
 */
public class RunnableAdapter implements IRunnableWithProgress {
	
	private IWorkspaceRunnable fWorkspaceRunnable;
	private ISchedulingRule fRule;
	
	/**
	 * The Constructor.
	 * Equivalent to RunnableAdapter(workspaceRunnable, ResourcesPlugin.getWorkspace().getRoot())
	 * @param workspaceRunnable the IWorkspaceRunnable to run.
	 */
	public RunnableAdapter(IWorkspaceRunnable workspaceRunnable) {
		this(workspaceRunnable, ResourcesPlugin.getWorkspace().getRoot());
	}
	
	/**
	 * The Constructor.
	 * @param workspaceRunnable the IWorkspaceRunnable to run.
	 * @param rule the scheduling rule to prevent concurrent resource conflicts
	 */
	public RunnableAdapter(IWorkspaceRunnable workspaceRunnable, ISchedulingRule rule) {
		fWorkspaceRunnable = workspaceRunnable;
		fRule = rule;
	}
	
	public ISchedulingRule getSchedulingRule() {
		return fRule;
	}
	/**
	 * Runs the given IWorkspaceRunnable, using the given scheduling rule.
	 * Note that the code is not run in the UI thread.
	 * @param monitor The progress monitor to notify User of progress.
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		try {
			JavaCore.run(fWorkspaceRunnable, fRule, monitor);
		} 
		catch (OperationCanceledException e) {
			throw new InterruptedException(e.getMessage());
		} 
		catch (CoreException e) {
			throw new InvocationTargetException(e);
		}
	}
}

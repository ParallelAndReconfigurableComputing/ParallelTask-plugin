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
package nz.ac.auckland.ptjava.newprojectwizard;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import nz.ac.auckland.ptjava.builder.PTJavaFileBuilderNature;
import nz.ac.auckland.ptjava.internal.newprojectwizard.RunnableAdapter;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard to create a new PTJava Project. A PTJava project is simply a Java Project,
 * with a PTJava nature associated with the project (so that the PTJava builder can
 * be used to compile .ptjava files). Therefore the New Java Project wizard pages
 * are also included with this wizard.
 * 
 * 
 *
 */
public class NewProjectWizard extends Wizard implements INewWizard {

	private NewJavaProjectWizardPageOne fFirstPage;
	private NewJavaProjectWizardPageTwo fSecondPage;
	private PTJavaWizardPage fThirdPage;
	
	/**
	 * The Constructor. Equivalent to NewProjectWizard(null,null,null).
	 */
	public NewProjectWizard() {
		this(null, null, null);
	}
	/**
	 * The Constructor. Creates the wizard with the three given pages.
	 * 
	 * @param firstPage The first page of the wizard. This is the same as the first page of the New Java Project wizard.
	 * @param secondPage The second page of the wizard. This is the same as the second page of the New Java Project wizard.
	 * @param thirdPage The thrid page of the wizard. This is a custom PTJava page.
	 */
	public NewProjectWizard(NewJavaProjectWizardPageOne firstPage, NewJavaProjectWizardPageTwo secondPage, PTJavaWizardPage thirdPage) {
		fFirstPage = firstPage;
		fSecondPage = secondPage;
		fThirdPage = thirdPage;
	}

	/**
	 * Performs the finish actions. Current implementation calls NewJavaProjectWizardPageTwo.performFinish(), 
	 * then PTJavaWizardPage.performFinish().
	 * 
	 * Note: this method tends to get called from the non-UI thread 
	 * 
	 * @param monitor The progress monitor to display progress to the user.
	 * @throws InterruptedException
	 * @throws CoreException
	 */
	public void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException{
		fSecondPage.performFinish(monitor);
		
		// must pass java project manually to the page
		fThirdPage.setJavaProject(fSecondPage.getJavaProject());
		fThirdPage.performFinish(monitor);
	}
	
	/**
	 * Performs actions to create the new project and add the PTJava nature to the project.
	 */
	@Override
	public boolean performFinish() {
		IWorkspaceRunnable op= new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
				try {
					finishPage(monitor);
				} catch (InterruptedException e) {
					throw new OperationCanceledException(e.getMessage());
				}
			}
		};
		
		try {
			//  see Platform Plug-in developers guide > Programmer's Guide > Resources overview > Modifying the workspace
			ISchedulingRule rule= null;
			Job job= Job.getJobManager().currentJob();
			if (job != null)
				rule= job.getRule();
			IRunnableWithProgress runnable= null;
			if (rule != null)
				runnable= new RunnableAdapter(op, rule);
			else
				runnable= new RunnableAdapter(op, getSchedulingRule());
			getContainer().run(true, true, runnable);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return false;
		} catch  (InterruptedException e) {
			return false;
		}
		
		//  add PTJava builder nature to project
		IJavaProject project = fSecondPage.getJavaProject();
		PTJavaFileBuilderNature.addNature(project.getProject());
		
		// set up compiler to not copy .ptjava files to output
		Map options = project.getOptions(false);
		options.put(JavaCore.CORE_JAVA_BUILD_RESOURCE_COPY_FILTER, "*.ptjava");
		project.setOptions(options);
		
		//  accept finish request
		return true;
	}

	/**
	 * Initializes the wizard.
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		//  do nothing
	}
	
	/**
	 * Add pages to the wizard. Current implementation adds an instances of:
	 * {@link NewJavaProjectWizardPageOne}, 
	 * {@link NewJavaProjectWizardPageTwo},
	 * {@link PTJavaWizardPage}, to the wizard if they haven't already been added.
	 */
	@Override
	public void addPages() {
		if (fFirstPage == null)
			fFirstPage= new NewJavaProjectWizardPageOne();
		//  set the title
		fFirstPage.setTitle("New PTJava Project - Java Settings");
		addPage(fFirstPage);

		if (fSecondPage == null)
			fSecondPage= new NewJavaProjectWizardPageTwo(fFirstPage);
		fSecondPage.setTitle("New PTJava Project - Java Settings");
		addPage(fSecondPage);
		
		if (fThirdPage == null)
			fThirdPage = new PTJavaWizardPage();
		addPage(fThirdPage);
	}
	
	/**
	 * Returns the scheduling rule for creating the element.
	 * @return returns the scheduling rule
	 */
	protected ISchedulingRule getSchedulingRule() {
		return ResourcesPlugin.getWorkspace().getRoot(); // look all by default
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	public boolean performCancel() {
		fSecondPage.performCancel();
		return super.performCancel();
	}
}

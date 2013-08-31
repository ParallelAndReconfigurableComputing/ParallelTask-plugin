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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * A Wizard for creating a new PTJava class file. The wizard contains a single page:
 * {@link WizardNewPTJavaFileCreationPage}
 * 
 *
 */
public class NewPTJavaClassWizard extends Wizard implements INewWizard {

	private WizardNewPTJavaFileCreationPage fPage;
	private IWorkbench fWorkbench;
	private IStructuredSelection fSelection;
	
	/**
	 * Currently calls {@link WizardNewPTJavaFileCreationPage#finish()} to create
	 * the new PTJava class file.
	 */
	@Override
	public boolean performFinish() {
		return fPage.finish();
	}
	
	/**
	 * Currently creates and adds an instance of {@link WizardNewPTJavaFileCreationPage}
	 */
	@Override
	public void addPages() {
		fPage = new WizardNewPTJavaFileCreationPage("PTJAVA", fWorkbench, fSelection);
		addPage(fPage);
	}

	/**
	 * Initializes the page. Current implementation stores the given workbench and selection.
	 * 
	 * @param workbench The current workbench.
	 * @param selection The current object selection.
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.fWorkbench = workbench;
		this.fSelection = selection;
		//  set window title
		//  set page image descriptor
	}
}

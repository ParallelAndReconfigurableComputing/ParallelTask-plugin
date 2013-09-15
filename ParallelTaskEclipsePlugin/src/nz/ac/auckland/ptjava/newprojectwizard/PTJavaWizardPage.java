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

import java.util.ArrayList;
import java.util.List;

import nz.ac.auckland.ptjava.PTJavaPlugin;
import nz.ac.auckland.ptjava.preferences.PTJavaPreferencePage;
import nz.ac.auckland.ptjava.preferences.PreferenceConstants;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * The page for specifying PTJava specific settings of a PTJava project.
 *
 */
public class PTJavaWizardPage extends WizardPage {
	/**
	 * Must be set manually before performFinish() is called.
	 */
	private IJavaProject fJavaProject = null;
	
	private PTGroup fPTGroup;

	
	//------------------------------------------------------------------------------------------------
	/**
	 * class for managing the PTJava options in wizard page.
	 */
	private final class PTGroup implements SelectionListener {
		private Link fPreferenceLink;
		private Group fGroup;
		
		public PTGroup() {
			
		}
		@Override
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			String prefID = PTJavaPreferencePage.ID;
			PreferencesUtil.createPreferenceDialogOn(getShell(), prefID, new String[] {prefID}, null).open();
		}
		public Control createControl(Composite composite) {
			fGroup = new Group(composite, SWT.NONE);
			fGroup.setFont(composite.getFont());
			int span = 3;
			GridLayout g = new GridLayout(span, false);
			g.horizontalSpacing = 7;
			g.verticalSpacing = 12;
			g.marginWidth = 12;
			g.marginHeight = 15;
			
			fGroup.setLayout(g);
			fGroup.setText("PTJava Compiler options");
			
			{				
				//  set the preference link
				fPreferenceLink= new Link(fGroup, SWT.NONE);
				fPreferenceLink.setFont(fGroup.getFont());
				fPreferenceLink.setText("Specify the PTJava options <A>Here</A>");
				//  NOTE: must have <A> tags for link to be selectable
				GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
				gridData.horizontalSpan = SWT.FILL;
				fPreferenceLink.setLayoutData(gridData);
				fPreferenceLink.addSelectionListener(this);
				
				updateEnableState();
			}
			return fGroup;
		}
		
		//  enables the widgets in PTGroup
		public void updateEnableState() {
			if (fPreferenceLink != null) {
				fPreferenceLink.setEnabled(true);
			}
			if (fGroup != null) {
				fGroup.setEnabled(true);
			}
		}
	}
	
	/**
	 * The Constructor.
	 * @param firstPageRef A reference to the first page of the New PTJava Project wizard.
	 * @param secondPageRef A reference to the second page of the New PTJava Project wizard.
	 */
	public PTJavaWizardPage() {
		super("ptjavapage");
		setTitle("PTJava Settings");
		setDescription("Select the PTJava settings");
		
		fPTGroup = new PTGroup();
	}
	
	/**
	 * Creates the top-level control for this dialog page under the given parent composite.
	 * Effectively creates the widgets to be displayed on the page. 
	 * 
	 * @param parent The parent composite
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		container.setLayout(gridLayout);
		setControl(container);
		
		Control ptGroupControl = fPTGroup.createControl(container);
		ptGroupControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
	}
	
	public void setJavaProject(IJavaProject project) {
		fJavaProject = project; 
	}
	
	/**
	 * Finish routine called when User selects the Finish button. Current inplementation 
	 * adds the specified PTRuntime.jar file to the Java class path.
	 * 
	 * Must set fJavaProject before calling.
	 * 
	 * @param monitor The progress monitor for displaying progress to user. Currently not used 
	 */
	public void performFinish(IProgressMonitor monitor) {
		try {
			IClasspathEntry entries[] = fJavaProject.getRawClasspath();
			List<IClasspathEntry> list = new ArrayList<IClasspathEntry>(entries.length + 1);
			for (int i = 0; i < entries.length; i++) {
				list.add(entries[i]);
			}
			
			//  add the java runtime library
			IPath path= null;
			IPreferenceStore prefs= PTJavaPlugin.getDefault().getPreferenceStore(); 
			if (prefs.getBoolean(PreferenceConstants.PTJAVA_USE_CUSTOM_RUNTIME)) {
				path= new Path(prefs.getString(PreferenceConstants.PTJAVA_RUNTIME_PATH));
			}
			else {
				path= PTJavaPreferencePage.getDefaultRuntimeJarPath();
			}
			list.add(JavaCore.newLibraryEntry(path, null, null));
			
			IClasspathEntry updatedEntries[] = new IClasspathEntry[list.size()];
			list.toArray(updatedEntries);
			
			fJavaProject.setRawClasspath(updatedEntries, monitor);
		}
		catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
}

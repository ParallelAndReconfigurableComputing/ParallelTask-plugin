package nz.ac.auckland.ptjava.newprojectwizard;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import nz.ac.auckland.ptjava.PTJavaPlugin;
import nz.ac.auckland.ptjava.builder.PTJavaFileBuilderNature;
import nz.ac.auckland.ptjava.preferences.PTJavaPreferencePage;
import nz.ac.auckland.ptjava.preferences.PreferenceConstants;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;

import com.android.ide.eclipse.adt.internal.wizards.templates.NewProjectWizard;

public class NewAndroidProjectWizard extends NewProjectWizard {
	
	private PTJavaWizardPage fPTWizardPage;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
        fPTWizardPage = new PTJavaWizardPage();
	}
	
	@Override
	public boolean performFinish(final IProgressMonitor monitor) throws InvocationTargetException {
		boolean superResult = super.performFinish(monitor);
		if (!superResult)
			return false;

		IProject project = getProject();
		
		// set java project manually and set libraries
		try {
			IFile ptjarFile = project.getFile(new Path("libs/PTRuntime.jar"));
			IPath ptjar = null;
			IPreferenceStore prefs= PTJavaPlugin.getDefault().getPreferenceStore(); 
			if (prefs.getBoolean(PreferenceConstants.PTJAVA_USE_CUSTOM_RUNTIME)) {
				ptjar = new Path(prefs.getString(PreferenceConstants.PTJAVA_RUNTIME_PATH));
			}
			else {
				ptjar = PTJavaPreferencePage.getDefaultRuntimeJarPath();
			}
			ptjarFile.create(new FileInputStream(ptjar.toFile()), true, monitor);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		// add pt compiler
		PTJavaFileBuilderNature.addNature(project);

		return true;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		addPage(fPTWizardPage);
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == fPTWizardPage)
			return null;
		
		IWizardPage nextPage = super.getNextPage(page);
		if (nextPage == null)
			return fPTWizardPage;
		
		return nextPage;
	} 
}

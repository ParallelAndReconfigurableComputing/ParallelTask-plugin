package nz.ac.auckland.ptjava.commands;

import java.util.Map;

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
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		// set selection service
		ISelectionService service = window.getSelectionService();
		// set structured selection
		IStructuredSelection structured = (IStructuredSelection) service.getSelection();
	 
		//check if it is an IProject
		if (structured.getFirstElement() instanceof IProject) {
			// get the selected file
			IProject project = (IProject) structured.getFirstElement();
			try {
				
				//project.isNatureEnabled("org.eclipse.jdt.core.javanature")
				if (project.hasNature(JavaCore.NATURE_ID)) {
			        IJavaProject javaProject = JavaCore.create(project);
			        PTJavaFileBuilderNature.addNature(project);
			        
			        // set up compiler to not copy .ptjava files to output
					Map options = javaProject.getOptions(false);
					options.put(JavaCore.CORE_JAVA_BUILD_RESOURCE_COPY_FILTER, "*.ptjava");
					javaProject.setOptions(options);
			    }
			} catch (CoreException e) {
				e.printStackTrace();
			}
			System.out.println("Selected project name:" + project.getName());
		}
		
		return null;
	}
}

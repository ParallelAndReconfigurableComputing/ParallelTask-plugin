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
package nz.ac.auckland.ptjava.views;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nz.ac.auckland.ptjava.PTJavaPlugin;
import nz.ac.auckland.ptjava.internal.resources.FileResourceManager;
import nz.ac.auckland.ptjava.internal.views.BasicTask;
import nz.ac.auckland.ptjava.internal.views.InteractiveTask;
import nz.ac.auckland.ptjava.internal.views.PTTask;
import nz.ac.auckland.ptjava.internal.views.TaskManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * View for showing TASK and IO_TASK methods in a PTJava file.
 * The view is connected to the model using a content provider.
 * Listens for selection changes so that source file can
 * be parsed if it is a .ptjava file, to rebuild the model.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view.
 * <p>
 */
public class PTJavaView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "nz.ac.auckland.ptjava.views.PTJavaView";
	
	private TableViewer fViewer;
	private Action fDoubleClickAction;
	private TaskManager fTaskManager;
	private ISelectionListener fSelectionListener;
	private Listener fSortListener;
	/**
	 * Provides the content for the viewer
	 *
	 */
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			List<PTTask> l= fTaskManager.getTasks();
			if (l.size() > 0)
				return l.toArray();
			return new Object[] {};
		}
	}
	/**
	 * Provides the labels for the viewer
	 *
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		private Image fBasicTaskImage;
		private Image fInteractiveTaskImage;
		private Image fMultiBasicTaskImage;
		private Image fMultiInteractiveTaskImage;
		
		/**
		 * Constructor.
		 * Loads the images for the labels.
		 */
		public ViewLabelProvider() {
			super();
			fBasicTaskImage= loadImage("icons/full/etool16/task_icon.gif");
			fInteractiveTaskImage= loadImage("icons/full/etool16/interactive_task_icon.gif");
			
			fMultiBasicTaskImage= loadImage("icons/full/etool16/multi_task_icon.gif");
			fMultiInteractiveTaskImage= loadImage("icons/full/etool16/multi_interactive_task_icon.gif");
		}
		
		/**
		 * Disposes of this label provider. Current implementation disposes of loaded images.
		 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
		 */
		@Override
		public void dispose() {
			fBasicTaskImage.dispose();
			fInteractiveTaskImage.dispose();
			
			fMultiBasicTaskImage.dispose();
			fMultiInteractiveTaskImage.dispose();
		}
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object obj, int index) {
			if (obj instanceof PTTask) {
				switch (index) {
				case 0:	//  name
					return ((PTTask)obj).getAttribute(PTTask.NAME);
				case 1:	//  arguments
					return ((PTTask)obj).getAttribute(PTTask.ARGUMENTS);
				case 2:	//  return type
					return ((PTTask)obj).getAttribute(PTTask.RETURN_TYPE);
				case 3:
					return Integer.toString(((PTTask)obj).getStart());
				}	
			}
			return getText(obj);
		}
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object obj, int index) {
			if (index == 0)
				return getImage(obj);
			return null;
		}
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object obj) {
			if (obj instanceof InteractiveTask) {
				InteractiveTask t= (InteractiveTask)obj;
				return (t.getMultiTask() ? fMultiInteractiveTaskImage : fInteractiveTaskImage);
			}	
			else if (obj instanceof BasicTask) {
				BasicTask t= (BasicTask)obj;
				return (t.getMultiTask() ? fMultiBasicTaskImage : fBasicTaskImage);
			}
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object obj) {
			return obj.toString();
		}
		/**
		 * Loads an image located within the PTJava plug-in.
		 * @param relativeFileName The path to the image, relative to the root of the plug-in
		 * @return The Image, or null if the image cannot be found or created.
		 */
		private Image loadImage(String relativeFileName) {
			ImageDescriptor imageDescriptor= PTJavaPlugin.imageDescriptorFromPlugin(PTJavaPlugin.PLUGIN_ID, relativeFileName);
			if (imageDescriptor == null)
				return null;
			return imageDescriptor.createImage();
		}
	}

	/**
	 * Class that implements custom sorting of the view. Sorting is dependant on what column is current,
	 * which can be set using {@link ViewComparatorProvider#setColumn(String)}.
	 *
	 */
	class ViewComparatorProvider extends ViewerComparator {
		public static final String LOCATION= "location";
		
		private String fColumn;
		private int fSortDirection;
		
		public ViewComparatorProvider(String column, int sortDirection) {
			fColumn= column;
			fSortDirection= sortDirection;
		}
		
		public void setColumn(String value) {
			fColumn= value;
		}
		
		public void setSortDirection(int value) {
			fSortDirection= value;
		}
		
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 instanceof PTTask &&
					e1 instanceof PTTask ) {
				int res= 0;
				if (fColumn.equals(LOCATION))
					res= ((PTTask)e2).getStart() - ((PTTask)e1).getStart();
				else
					res= ((PTTask)e1).getAttribute(fColumn).compareTo(((PTTask)e2).getAttribute(fColumn));
				
				if (fSortDirection == SWT.DOWN)
					res= res*-1;
				
				return res;
			}
			return super.compare(viewer, e1, e2);
		}
	}
	/**
	 * Class that listens for column selection events and updates the ViewComparatorProvider with
	 * information on which column was selected (for sorting by that column).  
	 *
	 */
	class ViewSortListener implements Listener {
		private ViewComparatorProvider fComparatorProvider;
		/**
		 * The Constructor
		 * @param comparatorProvider The ViewComparatorProvider to update on selection.
		 */
		public ViewSortListener(ViewComparatorProvider comparatorProvider) {
			fComparatorProvider= comparatorProvider;
		}

		@Override
		public void handleEvent(Event event) {
			TableColumn sortColumn= fViewer.getTable().getSortColumn();
			TableColumn currentColumn= (TableColumn)event.widget;
			int dir= (fViewer.getTable().getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP);
			
			if (sortColumn != currentColumn)
				fViewer.getTable().setSortColumn(currentColumn);
			
			String id= null;
			if (currentColumn == fViewer.getTable().getColumn(0))
				id= PTTask.NAME;
			else if (currentColumn == fViewer.getTable().getColumn(1))
				id= PTTask.ARGUMENTS;
			else if (currentColumn == fViewer.getTable().getColumn(2))
				id= PTTask.RETURN_TYPE;
			else if (currentColumn == fViewer.getTable().getColumn(3))
				id= ViewComparatorProvider.LOCATION;
			
			if (id != null) {
				fViewer.getTable().setSortDirection(dir);
				
				fComparatorProvider.setColumn(id);
				fComparatorProvider.setSortDirection(dir);
								
				fViewer.setComparator(fComparatorProvider);
				fViewer.refresh();
			}
		}
	};
	
	/**
	 * Job to update the PTJava View
	 *
	 */
	class UpdateViewJob extends Job {
		IFile fFile;
		public UpdateViewJob(String name, IFile file) {
			super(name);
			fFile= file;
		}
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if (monitor != null)
				monitor.beginTask(getName(), 3);
			
			fTaskManager.removeTasks();
			if (monitor != null)
				monitor.worked(1);
			
			scanForTasks(fFile);
			
			if (monitor != null)
				monitor.worked(1);
			
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					//  if we kill the view, a SelectionChanged event fires just before disposing
					//  so we only want to update the viewer if we are still listening for events
					if (fSelectionListener != null)
						fViewer.refresh();	
				}
			});
			
			if (monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}
	}
	
	/**
	 * The constructor. Currently creates a {@link TaskManager} to store task data.
	 */
	public PTJavaView() {
		fTaskManager= new TaskManager();
	}	
	
	/**
	 * Creates the SWT controls for this workbench part.
	 * Current implementation creates the viewer and double click action, and associates a selection listener to listen for
	 * page selection changes.
	 */
	public void createPartControl(Composite parent) {
		fViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		fSortListener= new ViewSortListener(new ViewComparatorProvider(ViewComparatorProvider.LOCATION, SWT.DOWN));
		{
			//  set up table viewer
			String[] titles= {"Method", "Arguments", "Returns", "Location"};
			//  NOTE: if the order of elements in this array changes, must update
			//  ViewSortListener as well!
			int[] bounds= {120, 120, 120, 50};
			for (int i= 0; i < 4; i++) {
				TableViewerColumn column= new TableViewerColumn(fViewer, SWT.NONE);
				column.getColumn().setText(titles[i]);
				column.getColumn().setWidth(bounds[i]);
				column.getColumn().setResizable(true);
				column.getColumn().setMoveable(true);
				
				//  add listener for sorting:
				column.getColumn().addListener(SWT.Selection, fSortListener);
			}
			Table table= fViewer.getTable();
			table.setHeaderVisible(true);
		}	 
		
		fViewer.setContentProvider(new ViewContentProvider());
		fViewer.setLabelProvider(new ViewLabelProvider());
		fViewer.setInput(getViewSite());
		
		makeActions();
		hookDoubleClickAction();
		hookPageSelection();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (fSelectionListener != null) {
			getSite().getPage().removePostSelectionListener(fSelectionListener);
			fSelectionListener= null;
		}
	}
	
	private void hookPageSelection() {
		fSelectionListener= new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				if (getActiveEditor() == null) {
					fTaskManager.removeTasks();
					fViewer.refresh();
					return;
				}
					
				if (part instanceof nz.ac.auckland.ptjava.internal.editors.PTJavaTextEditor) {
					IEditorInput input= ((nz.ac.auckland.ptjava.internal.editors.PTJavaTextEditor) part).getEditorInput();
					if (input instanceof IFileEditorInput) {
						IFile file= ((IFileEditorInput)input).getFile();
						Job job= new UpdateViewJob("Parse Active Editor for Tasks", file);
						job.setRule(file);
						job.setPriority(Job.DECORATE);
						job.schedule();
					}
				}
				else if (part instanceof org.eclipse.ui.IViewPart) {
					//  do nothing
				}
				else {
					fTaskManager.removeTasks();
					fViewer.refresh();
				}
			}
		};
		getSite().getPage().addPostSelectionListener(fSelectionListener);
	}

	private void makeActions() {
		fDoubleClickAction = new Action() {
			public void run() {
				ISelection selection = fViewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				//showMessage("Double-click detected on "+obj.toString());
				if (obj instanceof nz.ac.auckland.ptjava.internal.views.PTTask) {
					PTTask task= (PTTask)obj;
					IEditorPart part= getActiveEditor();
					if (part instanceof ITextEditor) {
						((ITextEditor) part).setHighlightRange(task.getStart(), task.getEnd()-task.getStart(), true);
					}
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		fViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				fDoubleClickAction.run();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		fViewer.getControl().setFocus();
	}
	
	/**
	 * Returns the active editor in the workbench
	 * @return The active editor, or null if none.
	 */
	public static IEditorPart getActiveEditor() {
		IWorkbenchWindow window= PTJavaPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page= window.getActivePage();
			if (page != null) {
				return page.getActiveEditor();
			}
		}
		return null;
	}
	
	private void scanForTasks(IFile file) {
		//if (!(file.getCharset().equalsIgnoreCase("UTF-8")))
		//	return;
		
		if (!file.getFileExtension().equals("ptjava"))
			return;
		
		String sourceString= FileResourceManager.getInstance().loadSource(file);
		if (sourceString.isEmpty())
			return;

		//  pattern matching
		String field= new String("[a-z[A-Z]_]\\w*");
		String type= new String("[a-z[A-Z]_]\\w*(<[\\w\\s_\\.]+>)?");
		String scope= new String("public|private|protected");
		
		{
			Pattern pattern= Pattern.compile("(INTERACTIVE_)?TASK(\\((([a-z[A-Z]]\\w*(\\.[a-z[A-Z]]\\w*)*)|\\*|[0-9]+)\\))?(\\s+" + scope + ")?(\\s+static)?(\\s+final)?\\s+(" + type + ")\\s+(" + field + ")\\(((.)*)\\)");
			Matcher matcher= pattern.matcher(sourceString);
			
			while (matcher.find()) {		
				String found= sourceString.substring(matcher.start(), matcher.end());
				
				String methodName= matcher.group(11).trim();
				//  NOTE: group 18 of matched expression corresponds to the method name 
				String returnType= matcher.group(9).trim();
				//  NOTE: group 13 of matched expression corresponds to the return type
				
				//  determine whether task is a multi-task
				boolean isMultiTask= (matcher.group(2) != null ? true : false);
				
				String arguments= matcher.group(12).trim();
				//  NOTE: group 12 of matched expression corresponds to the arguments
				
				if (found.startsWith("IO_TASK")) { 
					PTTask task= new InteractiveTask(methodName, file.getName(), matcher.start(), matcher.start() + methodName.length(), isMultiTask);
					task.setAttribute(PTTask.RETURN_TYPE, returnType);
					task.setAttribute(PTTask.ARGUMENTS, arguments);
					fTaskManager.addTask(task);
				}
				else { 
					PTTask task= new BasicTask(methodName, file.getName(), matcher.start(), matcher.start() + methodName.length(), isMultiTask);
					task.setAttribute(PTTask.RETURN_TYPE, returnType);
					task.setAttribute(PTTask.ARGUMENTS, arguments);
					fTaskManager.addTask(task);
				}
			}
		}
	}
}

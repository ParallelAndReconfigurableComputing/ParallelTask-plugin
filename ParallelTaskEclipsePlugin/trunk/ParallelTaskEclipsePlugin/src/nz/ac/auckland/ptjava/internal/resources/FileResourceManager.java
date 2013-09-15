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
package nz.ac.auckland.ptjava.internal.resources;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Singleton class for handling the loading of file resources for classes
 * that parse source code.
 * 
 * Listens for resource change events and updates a cache of source files
 * that have been requested via the loadSource() method.
 *
 */
public class FileResourceManager implements IResourceChangeListener {
	private static FileResourceManager fgManager;	//  singleton instance
	private Map<String, String> fMap;	//  map for storing source code
	
	/**
	 * Returns the singleton instance of the FileResourceManager
	 * @return the instance
	 */
	public static FileResourceManager getInstance() {
		if (fgManager == null)
			fgManager= new FileResourceManager();
		return fgManager;
	}
	
	/**
	 * Handler for resource changed events. Current implementation uses a ResourceDeltaVisitor to find changed
	 * resources and update cached resources as necessary.
	 * 
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		switch (event.getType()) {
		case IResourceChangeEvent.POST_CHANGE:
			try {
				event.getDelta().accept(new ResourceDeltaVisitor());
			} 
			catch (CoreException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
	/**
	 * Visitor class for traversing the tree of resource changes.
	 * This class explicitly looks for CHANGED and REMOVED events on the resources
	 *
	 */
	private class ResourceDeltaVisitor implements IResourceDeltaVisitor {
		/**
		 * Job for loading a file into the source cache
		 *
		 */
		private class LoadFileJob extends Job {
			IFile fFile;
			/**
			 * Constructor
			 * @param name The anme of the Job
			 * @param file The file whose contents are to be loaded into the cache
			 */
			public LoadFileJob(String name, IFile file) {
				super(name);
				fFile= file;
			}
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				fMap.put(fFile.getFullPath().toString(), readFile(fFile));
				return Status.OK_STATUS;
			}
		}
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource= delta.getResource();
			if (resource instanceof IFile) {
				if (fMap.containsKey(resource.getFullPath().toString())) {
					switch (delta.getKind()) {
					case IResourceDelta.CHANGED:
						//  update the cached source for the resource (run in a background thread)
						Job job= new LoadFileJob("Load File", (IFile)resource);
						job.setRule(resource);
						job.setPriority(Job.SHORT);
						job.schedule();
						break;
					case IResourceDelta.REMOVED:
						//  remove the cached resource
						fMap.remove(resource.getFullPath().toString());
						break;
					default:
						break;
					}
				}
			}
			return true;	//  don't visit the children
		}
	}
	
	/**
	 * Private Constructor
	 */
	private FileResourceManager() {
		fMap= new Hashtable<String,String>();
	
		//  register the resource change listener
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}
	/**
	 * Returns the source code for the given resource if the resource is of type IFile.
	 * If this is the first time loadSource has been called for the given resource,
	 * the resource contents is cached before it is returned.
	 * @param resource The resource whose contents are desired
	 * @return The source code for the given resource, or null if the resource is not of type IFile
	 */
	public String loadSource(IResource resource) {
		if (resource instanceof IFile) {
			String ret= null;
			if (fMap.containsKey(resource.getFullPath().toString())) {
				ret= fMap.get(resource.getFullPath().toString());
			}
			else {
				ret= readFile((IFile)resource);
				fMap.put(resource.getFullPath().toString(), ret);
			}
			return ret;
		}	
		return null;
	}
	/**
	 * Reads the contents of a file. 
	 * Note: This could be a long running operation.
	 * @param file The file to read from
	 * @return A string representing the contents of a file.
	 */
	public String readFile(IFile file) {
		StringBuffer sb= new StringBuffer();
		BufferedInputStream bIStream= null;
		try {
			int read;
			byte[] buffer = new byte[4096];
			bIStream= new BufferedInputStream(file.getContents(false));
			//  read data from file
			while((read = bIStream.read(buffer)) != -1) {
				String s= new String(buffer, 0, read);
				sb.append(s);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (bIStream != null)
					bIStream.close();				
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}

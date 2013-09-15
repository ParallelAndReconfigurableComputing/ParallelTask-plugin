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
package nz.ac.auckland.ptjava;

import java.io.IOException;
import java.net.URL;

import nz.ac.auckland.ptjava.editors.PTJavaTextTools;
import nz.ac.auckland.ptjava.preferences.PTJavaPreferencePage;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PTJavaPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "nz.ac.auckland.ptjava";

	// The shared instance
	private static PTJavaPlugin plugin;
	
	private PTJavaTextTools fPTJavaTextTools;
	
	//  the folder path to the plugin
	private static URL fPluginFolderPath;
	
	/**
	 * The constructor. Currently does nothing
	 */
	public PTJavaPlugin() {
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		PTJavaPreferencePage.loadRuntimeJarFile();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		
		if (fPTJavaTextTools != null) {
			fPTJavaTextTools.dispose();
			fPTJavaTextTools= null;
		}
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PTJavaPlugin getDefault() {
		return plugin;
	}
	 
	public synchronized PTJavaTextTools getPTJavaTextTools() {
		if (fPTJavaTextTools == null)
			fPTJavaTextTools= new PTJavaTextTools();
		return fPTJavaTextTools;
	}
	
	/**
	 * Returns the URL representing the plug-in directory
	 * @return The URL representing the path to the folder where the plug-in is installed
	 */
	public static URL getFolderPath() {
		if (fPluginFolderPath == null) {
			URL url = Platform.getBundle(PLUGIN_ID).getEntry("/");
			
			try {
				fPluginFolderPath = FileLocator.resolve(url);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fPluginFolderPath;
	}
}

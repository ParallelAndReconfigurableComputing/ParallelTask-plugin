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
package nz.ac.auckland.ptjava.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import nz.ac.auckland.ptjava.PTJavaPlugin;

/**
 * Class used to initialize default PTJava preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
	
	/**
	 * The Constructor. Currently does nothing.
	 */
	public PreferenceInitializer() {
		
	}

	/**
	 * Initializes values in preference store to default values. 
	 * 
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = PTJavaPlugin.getDefault().getPreferenceStore();
		
		store.setDefault(PreferenceConstants.PTJAVA_SHOW_HIDDEN_ERRORS, true);
		
		store.setDefault(PreferenceConstants.PTJAVA_COMPILER_PATH, new String());
		store.setDefault(PreferenceConstants.PTJAVA_ASSOCIATE_NATURE, true);
		store.setDefault(PreferenceConstants.PTJAVA_USE_CUSTOM_COMPILER, false);
		
		store.setDefault(PreferenceConstants.PTJAVA_USE_CUSTOM_RUNTIME, false);
		store.setDefault(PreferenceConstants.PTJAVA_RUNTIME_PATH, new String());
	}

}

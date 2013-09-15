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

/**
 * Constant definitions for PTJava plug-in preferences
 */
public class PreferenceConstants {

	/**
	 * The key for the checkbox for showing errors in compiled PTJava files.
	 */
	public static final String PTJAVA_SHOW_HIDDEN_ERRORS = "ptjava.showhiddenerrors";
	
	/**
	 * The key for the PTJava compiler path
	 */
	public static final String PTJAVA_COMPILER_PATH= "ptjava.compilerpath";
	/**
	 * The key for the associate nature check box in the PTJava Preferences. This determines
	 * whether or not the PTJava builder should be invoked on build requests.
	 */
	public static final String PTJAVA_ASSOCIATE_NATURE= "ptjava.associatenature";
	/**
	 * The key for the use custom compiler check box
	 */
	public static final String PTJAVA_USE_CUSTOM_COMPILER= "ptjava.usecustomcompiler";
	/**
	 * The key for the PTJava runtime path
	 */
	public static final String PTJAVA_RUNTIME_PATH= "ptjava.runtimepath";
	/**
	 * The key for the use custom runtime check box
	 */
	public static final String PTJAVA_USE_CUSTOM_RUNTIME= "ptjava.usecustomruntime"; 
}

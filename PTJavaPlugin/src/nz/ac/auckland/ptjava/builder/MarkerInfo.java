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
package nz.ac.auckland.ptjava.builder;
/**
 * Structure for holding data related to PTJava Problem Markers
 *
 */
public class MarkerInfo {
	/**
	 * The Constructor. Currently sets all data fields to <code>null</code>.
	 */
	public MarkerInfo() {
		fCharStart= null;
		fCharEnd= null;
		fMsg= null;
		fLocation= null;
		fLineNumber= null;
		fSeverity= null;
	}
	/**
	 * The zero-relative character offset to the start location of the marker in the text file. 
	 */
	public Integer fCharStart;
	/**
	 * The zero-relative character offset to the end location of the marker in the text file. 
	 */
	public Integer fCharEnd;
	/**
	 * The message the marker contains
	 */
	public String fMsg;
	/**
	 * The 1-relative line location of the marker (as a {@link java.lang.String})
	 */
	public String fLocation;
	/**
	 * The 1-relative line number the marker is located on.
	 */
	public Integer fLineNumber;
	/**
	 * The severity of the marker. Can be one of:
	 * <ul>
	 * 	<li>{@link org.eclipse.core.resources.IMarker#SEVERITY_INFO}</li>
	 *  <li>{@link org.eclipse.core.resources.IMarker#SEVERITY_WARNING}</li>
	 *  <li>{@link org.eclipse.core.resources.IMarker#SEVERITY_ERROR}</li>
	 * </ul>
	 */
	public Integer fSeverity;
}

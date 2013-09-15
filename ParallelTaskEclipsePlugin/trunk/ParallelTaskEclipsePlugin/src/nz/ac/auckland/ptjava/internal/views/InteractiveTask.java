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
package nz.ac.auckland.ptjava.internal.views;


/**
 * Represents an IO_TASK method
 *
 */
public class InteractiveTask extends PTTask {
	/**
	 * The Constructor. Fills data with given parameters
	 * 
	 * @param name The name of the task
	 * @param fileName The file name in which this task was found 
	 * @param start The zero-relative character offset to the start of the task
	 * @param end The zero-relative character offset to the end of the task
	 * @param multiTask Whether this task is a multi-task or not
	 */
	public InteractiveTask(String name, String fileName, int start, int end, boolean multiTask) {
		super(name, fileName, start, end, multiTask);
	}
}

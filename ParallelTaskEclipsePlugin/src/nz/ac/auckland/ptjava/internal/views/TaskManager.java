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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Manager for task data. Holds a linked list of task data.
 *
 */
public class TaskManager {
	LinkedList<PTTask> fTasks;
	public TaskManager() {
		fTasks= new LinkedList<PTTask>();
	}
	/**
	 * Appends the given task to the linked list
	 * @param task The task data to append to the linked list.
	 */
	public void addTask(PTTask task) {
		fTasks.add(task);
	}
	/**
	 * Returns the task data as a List
	 * @return The list of task data
	 */
	public List getTasks() {
		return fTasks;
	}
	/**
	 * Iterates through the linked list and removes tasks whose file name attribute
	 * matches that of the given file name.
	 * 
	 * @param fileName The String to match against the task data's {@link PTTask#FILENAME} attribute. If the match succeeds, the task is removed form the list. 
	 */
	public void removeTasksWithFileName(String fileName) {
		for (Iterator<PTTask> iter= fTasks.iterator(); iter.hasNext();) {
			PTTask p= iter.next();
			//System.out.println(p);
			if (p.getAttribute(PTTask.FILENAME).equals(fileName)) {
				//System.out.println("delete");
				iter.remove();
			}
		}
	}
	/**
	 * Removes all tasks from the linked list.
	 */
	public void removeTasks() {
		fTasks.clear();
	}
}

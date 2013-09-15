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

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for Task data for use in PTJavaView.
 *
 */
public abstract class PTTask {
	/**
	 * The name of the task.
	 */
	public static final String NAME= "name";
	
	/**
	 * The name of the file to which this task was found in.
	 */
	public static final String FILENAME= "filename";
	
	/**
	 * The return type of the task method.
	 */
	public static final String RETURN_TYPE= "return_type";
	
	/**
	 * The arguments of the method.
	 */
	public static final String ARGUMENTS= "arguments";
	
	private Map<String,String> fMap; 
	private int fStart;
	private int fEnd;
	private boolean fMultiTask;
	
	/**
	 * Default constructor. Equivalent to PTTask(null, null, 0, 0, false)
	 */
	public PTTask() {
		this(null, null, 0, 0, false);
	}
	/**
	 * Constructor. Equivalent to PTTask(name, fileName, 0, 0, false)
	 * @param name The name of the task method
	 * @param fileName The name of the file in which this task method is found
	 */
	public PTTask(String name, String fileName) {
		this(name, fileName, 0, 0, false);
	}
	/**
	 * Constructor. Equivalent to PTTask(name, fileName, start, end, false) 
	 * @param name The name of the task method
	 * @param fileName The name of the file in which this task method is found
	 * @param start The zero relative character position in the file where the start of the method is found
	 * @param end The zero relative character position in the file where the end of the method is found
	 */
	public PTTask(String name, String fileName, int start, int end) {
		this(name, fileName, start, end, false);
	}
	/**
	 * Constructor. 
	 * @param name The name of the task method
	 * @param fileName The name of the file in which this task method is found
	 * @param start The zero relative character position in the file where the start of the method is found
	 * @param end The zero relative character position in the file where the end of the method is found
	 * @param multiTask Whether or not this Task is a multi-task.
	 */
	public PTTask(String name, String fileName, int start, int end, boolean multiTask) {
		initializeMap(name, fileName);
		fStart= start;
		fEnd= end;
		fMultiTask= multiTask;
	}
	
	private void initializeMap(String name, String fileName) {
		fMap= new HashMap<String,String>();
		//  NOTE: HashMap is unsynchronised and therefore not thread safe!
		fMap.put(NAME, name);
		fMap.put(FILENAME, fileName);
	}
	/**
	 * Returns the value associated with the attribute key.
	 * @param attribute The attribute key
	 * @return The value associated with the key
	 */
	public String getAttribute(String attribute) {
		return fMap.get(attribute);
	}
	/**
	 * Sets the key-value pair
	 * @param key The key to associate with the value
	 * @param value The value
	 */
	public void setAttribute(String key, String value) {
		fMap.put(key, value);
	}
	/**
	 * Returns the start location of the Task definition in the text file
	 * @return The zero-based character offset to the start of the Task definition.
	 */
	public int getStart() {
		return fStart;
	}
	/**
	 * Sets the start location of the Task in the text file
	 * @param value The zero-based character offset to the start of the Task definition.
	 */
	public void setStart(int value) {
		fStart= value;
	}
	/**
	 * Returns the end location of the Task definition in the text file
	 * @return The zero-based character offset to the end of the Task definition.
	 */
	public int getEnd() {
		return fEnd;
	}
	/**
	 * Sets the end location of the Task in the text file
	 * @param value The zero-based character offset to the end of the Task definition.
	 */
	public void setEnd(int value) {
		fEnd= value;
	}
	/**
	 * Returns whether or not this Task is a multi-task. i.e. TASK(*) or IO_TASK(*) 
	 * @return true if Task is a multi-task, false otherwise
	 */
	public boolean getMultiTask() {
		return fMultiTask;
	}
}

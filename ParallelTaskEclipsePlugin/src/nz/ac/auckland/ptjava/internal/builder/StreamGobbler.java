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
package nz.ac.auckland.ptjava.internal.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class for reading from InputStreams. Used for handling output from invoking PTJava compiler
 *
 */
public class StreamGobbler extends Thread {
	private InputStream fInputStream;
	private String fType;
	
	private String fMsg;
	/**
	 * The Constructor.
	 * @param inputStream The stream to read from
	 * @param type An identifier for this StreamGobbler
	 */
	public StreamGobbler(InputStream inputStream, String type) {
		this.fInputStream = inputStream;
		this.fType = type;
	}
	
	/**
	 * Reads from the input stream and stores the read characters in internal buffer. 
	 * The internal buffer can be read using {@link StreamGobbler#getMsg()}.
	 */
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(fInputStream);
			BufferedReader buf = new BufferedReader(isr);
			String line = null;
			
			StringBuffer sb = new StringBuffer();
			
			while ((line = buf.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			fMsg = sb.toString();
		}
		catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	/**
	 * Get the message that was read from the stream
	 * @return The message that was read from the stream
	 */
	public String getMsg() {
		return fMsg;
	}
	/**
	 * Get the identifier for this StreamGobbler
	 * @return The identifier, specified in the constructor of the StreamGobbler
	 */
	public String getType() {
		return fType;
	}
}

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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * The logger of convenience for the PTJava plug-in.
 */
public class PTJavaLog {
	  /**
	    * Log the specified information.
	    * 
	    * @param message A human-readable message, localized to the
	    *           current locale.
	    */
		public static void logInfo(String message) {
			log(IStatus.INFO, IStatus.OK, message, null);
		}

	   /**
	    * Log the specified error.
	    * 
	    * @param exception A low-level exception.
	    */
		public static void logError(Throwable exception) {
			logError("Unexpected Exception", exception);
		}

	   /**
	    * Log the specified error.
	    * 
	    * @param message A human-readable message, localized to the
	    *           current locale.
	    * @param exception A low-level exception, or <code>null</code>
	    *           if not applicable.
	    */
		public static void logError(String message, Throwable exception) {
			log(IStatus.ERROR, IStatus.OK, message, exception);
		}

	   /**
	    * Log the specified information.
	    * 
	    * @param severity the severity; one of the following:
	    *           <code>IStatus.OK</code>,
	    *           <code>IStatus.ERROR</code>,
	    *           <code>IStatus.INFO</code>, or
	    *           <code>IStatus.WARNING</code>.
	    * @param code the plug-in-specific status code, or
	    *           <code>OK</code>.
	    * @param message a human-readable message, localized to the
	    *           current locale.
	    * @param exception a low-level exception, or <code>null</code>
	    *           if not applicable.
	    */
		public static void log(int severity, int code, String message,
				Throwable exception) {
			log(createStatus(severity, code, message, exception));
		}

	   /**
	    * Create a status object representing the specified information.
	    * 
	    * @param severity the severity; one of the following:
	    *           <code>IStatus.OK</code>,
	    *           <code>IStatus.ERROR</code>,
	    *           <code>IStatus.INFO</code>, or
	    *           <code>IStatus.WARNING</code>.
	    * @param code the plug-in-specific status code, or
	    *           <code>OK</code>.
	    * @param message a human-readable message, localized to the
	    *           current locale.
	    * @param exception a low-level exception, or <code>null</code>
	    *           if not applicable.
	    * @return the status object (not <code>null</code>).
	    */
	   public static IStatus createStatus(int severity, int code, String message,
				Throwable exception) {
			return new Status(severity, PTJavaPlugin.PLUGIN_ID, code,
					message, exception);
		}

	   /**
	    * Log the given status.
	    * 
	    * @param status the status to log.
	    */
		public static void log(IStatus status) {
			PTJavaPlugin.getDefault().getLog().log(status);
		}
}

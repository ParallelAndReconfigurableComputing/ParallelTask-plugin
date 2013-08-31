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
package nz.ac.auckland.ptjava.internal.editors;


import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

/**
 * The class defining the editor and implementing org.eclipse.ui.IEditorPart.
 * The current version extends the JDT text editor. Future versions should
 * be based off the JDT editor source code
 *
 */
public class PTJavaTextEditor extends CompilationUnitEditor {
	/**
	 * The Constructor. Current implementation does nothing.
	 */
	public PTJavaTextEditor() {
		super();
	}
	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#setPreferenceStore(org.eclipse.jface.preference.IPreferenceStore)
	 * @since 3.0
	 */
	protected void setPreferenceStore(IPreferenceStore store) {
		super.setPreferenceStore(store);
		SourceViewerConfiguration sourceViewerConfiguration= getSourceViewerConfiguration();
		if (sourceViewerConfiguration == null || sourceViewerConfiguration instanceof JavaSourceViewerConfiguration) {
			JavaTextTools textTools= JavaPlugin.getDefault().getJavaTextTools();
			setSourceViewerConfiguration(new PTJavaSourceViewerConfiguration(textTools.getColorManager(), store, this, IJavaPartitions.JAVA_PARTITIONING));
		}

	}
}

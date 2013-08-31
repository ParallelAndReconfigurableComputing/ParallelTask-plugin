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
package nz.ac.auckland.ptjava.editors;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * Manages the installation and removal of global
 * menus, menu items, and tool bar items for
 * PTJavaTextEditor (and possibly other editors
 * in the future)
 * 
 * @deprecated Currently not used as there has been no menu actions to add to the text editor.
 */
public class PTJavaTextEditorContributor extends EditorActionBarContributor {
	/**
	 * The Constructor.
	 * 
	 * @see org.eclipse.ui.part.EditorActionBarContributor
	 */
	public PTJavaTextEditorContributor() {
		super();
	}

	/**
	 * Automatically called when the contributor is no longer needed.
	 * 
	 * Current implementation calls {@link org.eclipse.ui.part.EditorActionBarContributor#dispose()}.
	 */
	@Override
	public void dispose() {
		super.dispose();
	}

	/**
	 * Called when the contributor is first created.
	 * 
	 * The current implementation calls {@link org.eclipse.ui.part.EditorActionBarContributor#init(IActionBars, IWorkbenchPage)}.
	 */
	@Override
	public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);
	}
	
	/**
	 *   Called when an associated editor becomes active or inactive.
	 *   The contributor should insert or remove menus and tool bar
	 *   buttons as appropriate.
	 *   
	 *   The current implementation calls {@link org.eclipse.ui.part.EditorActionBarContributor#setActiveEditor(IEditorPart)}.
	 */
	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		super.setActiveEditor(targetEditor);
	}
}

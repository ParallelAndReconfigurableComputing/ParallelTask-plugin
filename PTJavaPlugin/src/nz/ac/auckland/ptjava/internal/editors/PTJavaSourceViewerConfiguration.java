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


import nz.ac.auckland.ptjava.internal.editors.syntax.PTJavaCodeScanner;
import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * The source viewer configuration for the PTJava text editor. This class controls
 * features such as syntax highlighting, code completion and text hovers for the 
 * editor.
 * 
 * The current version extends the Java source viewer configuration.
 *
 */
public class PTJavaSourceViewerConfiguration extends
		JavaSourceViewerConfiguration {
	
	//  The code scanners (for each document partition)
	private AbstractJavaScanner fCodeScanner;
	
	/**
	 * Creates a new Java source viewer configuration for viewers in the given editor
	 * using the given preference store, the color manager and the specified document partitioning.
	 *
	 * @param colorManager the color manager
	 * @param preferenceStore the preference store, can be read-only
	 * @param editor the editor in which the configured viewer(s) will reside, or <code>null</code> if none
	 * @param partitioning the document partitioning for this configuration, or <code>null</code> for the default partitioning
	 */
	public PTJavaSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
		initializeScanners();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration#getCodeScanner()
	 */
	@Override
	protected RuleBasedScanner getCodeScanner() {
		return this.fCodeScanner;
	}
	
	/**
	 * Initializes the scanners.
	 * Note: independent from JavaSourceViewerConfiguration.initializeScanners()
	 *
	 * @since 3.0
	 */
	private void initializeScanners() {
		//  get the color provider from PTJavaPlugin
		//PTJavaColorProvider provider = PTJavaPlugin.getDefault().getPTJavaColorProvider();
		
		//  create the scanners
		//  Scanner for DEFAULT partitions (i.e. the ones that don't get recognized)
		fCodeScanner= new PTJavaCodeScanner(getColorManager(), fPreferenceStore);
		
		//  NOTE: the other scanners are initialized in JavaSourceViewerConfiguration constructor
	}

	/*
	 * @see SourceViewerConfiguration#getPresentationReconciler(ISourceViewer)
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

		PresentationReconciler reconciler= new PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr= new DefaultDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr= new DefaultDamagerRepairer(getJavaDocScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_DOC);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_DOC);

		dr= new DefaultDamagerRepairer(getMultilineCommentScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);

		dr= new DefaultDamagerRepairer(getSinglelineCommentScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);

		dr= new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_STRING);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_STRING);

		dr= new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_CHARACTER);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_CHARACTER);

		return reconciler;
	}
	/*
	 * @see org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration#handlePropertyChangeEvent(PropertyChangeEvent)
	 */
	@Override
	public void handlePropertyChangeEvent(PropertyChangeEvent event) {
		//  to handle when someone changes the java editor properties
		super.handlePropertyChangeEvent(event);
		if (fCodeScanner.affectsBehavior(event))
			fCodeScanner.adaptToPreferenceChange(event);
	}
}

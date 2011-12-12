/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent
 * Systems) For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2010 OSTIS
 *
 * OSTIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OSTIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OSTIS. If not, see <http://www.gnu.org/licenses/>.
 */
package net.ostis.scpdev.editors.scs;

import net.ostis.scpdev.editors.ColorManager;
import net.ostis.scpdev.editors.NonRuleBasedDamagerRepairer;
import net.ostis.scpdev.editors.SingleTokenScanner;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public class SCsSourceViewerConfiguration extends TextSourceViewerConfiguration {

    protected SCsDoubleClickStrategy doubleClickStrategy;

    protected RuleBasedScanner scanner;

    protected ColorManager colorManager;

    public SCsSourceViewerConfiguration(ColorManager colorManager) {
        this.colorManager = colorManager;
    }

    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] {
                IDocument.DEFAULT_CONTENT_TYPE, SCsPartitionScanner.SCs_COMMENT, SCsPartitionScanner.SCs_INCLUDE
        };
    }

    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
        if (doubleClickStrategy == null) {
            doubleClickStrategy = new SCsDoubleClickStrategy();
        }

        return doubleClickStrategy;
    }

    protected RuleBasedScanner getDefaultScanner() {
        if (scanner == null) {
            scanner = new SCsCodeScanner(colorManager);
            scanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager
                    .getColor(ISCsColorConstants.SCS_DEFAULT))));
        }

        return scanner;
    }

    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getDefaultScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        dr = new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(
                colorManager.getColor(ISCsColorConstants.SCS_COMMENT))));
        reconciler.setDamager(dr, SCsPartitionScanner.SCs_COMMENT);
        reconciler.setRepairer(dr, SCsPartitionScanner.SCs_COMMENT);

        NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(
                colorManager.getColor(ISCsColorConstants.SCS_INCLUDE)));
        reconciler.setDamager(ndr, SCsPartitionScanner.SCs_INCLUDE);
        reconciler.setRepairer(ndr, SCsPartitionScanner.SCs_INCLUDE);

        return reconciler;
    }

}

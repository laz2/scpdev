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
package net.ostis.scpdev.m4scp.editors;

import net.ostis.scpdev.ScpdevPlugin;
import net.ostis.scpdev.editors.ColorManager;
import net.ostis.scpdev.editors.scs.ISCsColorConstants;
import net.ostis.scpdev.editors.scs.SCsSourceViewerConfiguration;
import net.ostis.scpdev.m4scp.M4ScpPlugin;
import net.ostis.scpdev.m4scp.editors.templates.M4ScpContextType;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Dmitry Lazurkin
 */
public class M4ScpSourceViewerConfiguration extends SCsSourceViewerConfiguration {

    public M4ScpSourceViewerConfiguration(ColorManager colorManager) {
        super(colorManager);
    }

    private final static Image templateImage = ScpdevPlugin.getImageDescriptor("icons/template.gif").createImage();

    @Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        ContentAssistant ca = new ContentAssistant();
        IContentAssistProcessor cap = new TemplateCompletionProcessor() {

            @Override
            protected Template[] getTemplates(String contextTypeId) {
                return M4ScpPlugin.getTemplateStore().getTemplates(contextTypeId);
            }

            @Override
            protected Image getImage(Template template) {
                return templateImage;
            }

            @Override
            protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
                return M4ScpPlugin.getContextTypeRegistry().getContextType(M4ScpContextType.M4SCP_CONTEXT_TYPE);
            }
        };
        ca.setContentAssistProcessor(cap, IDocument.DEFAULT_CONTENT_TYPE);
        ca.setInformationControlCreator(getInformationControlCreator(sourceViewer));
        return ca;
    }

    @Override
    protected RuleBasedScanner getDefaultScanner() {
        if (scanner == null) {
            scanner = new M4ScpCodeScanner(colorManager);
            scanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager
                    .getColor(ISCsColorConstants.SCS_DEFAULT))));
        }

        return scanner;
    }

}

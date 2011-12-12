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
import net.ostis.scpdev.editors.IScTextEditor;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * @author Dmitry Lazurkin
 */
public class SCsSourceEditor extends TextEditor implements IScTextEditor {
    private ColorManager colorManager;

    public SCsSourceEditor() {
        super();
        colorManager = new ColorManager();
        setSourceViewerConfiguration(new SCsSourceViewerConfiguration(colorManager));
        setDocumentProvider(new SCsDocumentProvider());
    }

    public void dispose() {
        colorManager.dispose();
        super.dispose();
    }

    @Override
    public ISourceViewer getScSourceViewer() {
        return getScSourceViewer();
    }

}

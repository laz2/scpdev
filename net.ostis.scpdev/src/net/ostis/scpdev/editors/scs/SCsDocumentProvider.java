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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

/**
 * @author Dmitry Lazurkin
 */
public class SCsDocumentProvider extends FileDocumentProvider {
    protected IDocument createDocument(Object element) throws CoreException {
        IDocument document = super.createDocument(element);

        if (document != null) {
            IDocumentPartitioner partitioner = new FastPartitioner(new SCsPartitionScanner(), new String[] {
                    SCsPartitionScanner.SCs_COMMENT, SCsPartitionScanner.SCs_INCLUDE
            });
            partitioner.connect(document);
            document.setDocumentPartitioner(partitioner);
        }

        return document;
    }
}

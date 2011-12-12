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
package net.ostis.scpdev.ui.dialogs;

import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

/**
 * For selection SC-segment or SC-directory. Use filesystem representation of
 * SC-memory - binary root folder.
 *
 * @author Dmitry Lazurkin
 */
public class SegmentOrDirectorySelectionDialog extends ElementTreeSelectionDialog {

    private static final Object dummy = new Object();

    public SegmentOrDirectorySelectionDialog(Shell parent, final IFolder binroot) {
        super(parent, new WorkbenchLabelProvider(), new WorkbenchContentProvider() {
            @Override
            public Object[] getElements(Object element) {
                if (element == dummy) {
                    return new Object[] { binroot };
                } else {
                    return super.getElements(element);
                }
            }
        });

        setAllowMultiple(false);
        setInput(dummy);
        setComparator(new ResourceComparator(ResourceComparator.TYPE));
    }

}

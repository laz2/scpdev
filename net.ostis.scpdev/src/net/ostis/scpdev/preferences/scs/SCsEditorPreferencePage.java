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
package net.ostis.scpdev.preferences.scs;

import net.ostis.scpdev.ScpdevPlugin;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Dmitry Lazurkin
 */
public class SCsEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public SCsEditorPreferencePage() {
        super(GRID);
        setPreferenceStore(ScpdevPlugin.getDefault().getPreferenceStore());
    }

    protected void createFieldEditors() {
//        final Composite parent = getFieldEditorParent();
//
//        colorFieldEditors = new ArrayList<FieldEditor>();
//
//        colorFieldEditors.add(new ColorFieldEditor(SCsPreferenceConstants.P_SCS_COMMENT_COLOR, "Comment", parent));
//        colorFieldEditors.add(new ColorFieldEditor(SCsPreferenceConstants.P_SCS_STRING_COLOR, "String", parent));
//        colorFieldEditors.add(new ColorFieldEditor(SCsPreferenceConstants.P_SCS_DEFAULT_COLOR, "Default", parent));
//
//        for (int i = 0; i < colorFieldEditors.size(); i++) {
//            addField((FieldEditor) colorFieldEditors.get(i));
//        }
    }

    public boolean performOk() {
//        for (int i = 0; i < colorFieldEditors.size(); i++) {
//            ((FieldEditor) colorFieldEditors.get(i)).store();
//        }
//
//        final IPreferenceStore store = getPreferenceStore();
//
//        ISCsColorConstants.SCS_COMMENT = PreferenceConverter
//                .getColor(store, SCsPreferenceConstants.P_SCS_COMMENT_COLOR);
//        ISCsColorConstants.SCS_CONTENT_STRING = PreferenceConverter.getColor(store,
//                SCsPreferenceConstants.P_SCS_STRING_COLOR);
//        ISCsColorConstants.SCS_DEFAULT = PreferenceConverter
//                .getColor(store, SCsPreferenceConstants.P_SCS_DEFAULT_COLOR);

        // reloadEditors();

        return false;
    }

    public void init(IWorkbench workbench) {
    }

//    private void reloadEditors() {
//        final IWorkbenchPage activePage = ((ScpdevPlugin.getDefault().getWorkbench()).getActiveWorkbenchWindow())
//                .getActivePage();
//        final IEditorReference[] er = activePage.getEditorReferences();
//
//        for (int i = 0; i < er.length; i++) {
//            final IEditorPart editorPart = (er[i]).getEditor(false);
//            final IEditorInput editorInput = editorPart.getEditorInput();
//
//            editorPart.doSave(null);
//            activePage.closeEditor(editorPart, true);
//
//            try {
//                activePage.openEditor(editorInput, "org.gamma.scp.editors.M4SCPEditor");
//            } catch (PartInitException ex) {
//            }
//        }
//    }
}

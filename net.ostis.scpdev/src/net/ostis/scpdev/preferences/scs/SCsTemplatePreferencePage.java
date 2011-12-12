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

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;

/**
 * @author Dmitry Lazurkin
 */
public class SCsTemplatePreferencePage extends TemplatePreferencePage implements IWorkbenchPreferencePage {

    public static final String PREFERENCE_PAGE_ID = "net.ostis.scpdev.preferences.scs_template_page";

    public SCsTemplatePreferencePage() {
        setPreferenceStore(ScpdevPlugin.getDefault().getPreferenceStore());
        setTemplateStore(ScpdevPlugin.getTemplateStore());
        setContextTypeRegistry(ScpdevPlugin.getContextTypeRegistry());
        setDescription("Templates for SCs source files");
    }

    protected boolean isShowFormatterSetting() {
        return true;
    }

    public boolean performOk() {
        boolean ok = super.performOk();

        ScpdevPlugin.getDefault().savePluginPreferences();

        return ok;
    }

}

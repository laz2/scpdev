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
package net.ostis.scpdev.m4scp.editors.templates;

import net.ostis.scpdev.ScpdevPlugin;
import net.ostis.scpdev.m4scp.M4ScpPlugin;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;

/**
 * @author Dmitry Lazurkin
 */
public class M4ScpTemplatePreferencePage extends TemplatePreferencePage implements IWorkbenchPreferencePage {

    public static final String PREFERENCE_PAGE_ID = "net.ostis.scpdev.preferences.m4scp_template_page";

    public M4ScpTemplatePreferencePage() {
        setPreferenceStore(ScpdevPlugin.getDefault().getPreferenceStore());
        setTemplateStore(M4ScpPlugin.getTemplateStore());
        setContextTypeRegistry(M4ScpPlugin.getContextTypeRegistry());
        setDescription("Templates for M4SCP source files");
    }

    protected boolean isShowFormatterSetting() {
        return true;
    }

    public boolean performOk() {
        boolean ok = super.performOk();

        M4ScpPlugin.getDefault().savePluginPreferences();

        return ok;
    }

}

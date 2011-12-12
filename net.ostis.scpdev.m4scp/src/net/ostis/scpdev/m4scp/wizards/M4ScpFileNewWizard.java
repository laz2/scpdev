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
package net.ostis.scpdev.m4scp.wizards;

import net.ostis.scpdev.m4scp.M4ScpPlugin;
import net.ostis.scpdev.m4scp.editors.templates.M4ScpContextType;
import net.ostis.scpdev.m4scp.editors.templates.M4ScpTemplatePreferencePage;
import net.ostis.scpdev.wizards.AbstractScFileNewWizard;
import net.ostis.scpdev.wizards.AbstractScFileNewWizardPage;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Dmitry Lazurkin
 */
public class M4ScpFileNewWizard extends AbstractScFileNewWizard {

    private class M4ScpFileNewWizardPage extends AbstractScFileNewWizardPage {

        public M4ScpFileNewWizardPage(IStructuredSelection selection) {
            super("New M4SCP Source File Wizard", selection);
            setTitle("New M4SCP Source File");
            setDescription("This wizard creates a new M4SCP source file.");
        }

        @Override
        public TemplateStore getTemplateStore() {
            return M4ScpPlugin.getTemplateStore();
        }

        @Override
        public ContextTypeRegistry getContextTypeRegistry() {
            return M4ScpPlugin.getContextTypeRegistry();
        }

        @Override
        protected String[] getContextTypes() {
            return new String[] { M4ScpContextType.M4SCP_FILES_CONTEXT_TYPE };
        }

        @Override
        protected String getTemplatePreferencePageId() {
            return M4ScpTemplatePreferencePage.PREFERENCE_PAGE_ID;
        }

        @Override
        protected String getFileExtension() {
            return "m4scp";
        }

    }

    @Override
    protected AbstractScFileNewWizardPage createFilePage() {
        return new M4ScpFileNewWizardPage(selection);
    }

}

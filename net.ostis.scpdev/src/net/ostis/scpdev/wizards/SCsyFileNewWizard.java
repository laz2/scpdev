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
package net.ostis.scpdev.wizards;

import net.ostis.scpdev.ScpdevPlugin;
import net.ostis.scpdev.editors.templates.SCsContextType;
import net.ostis.scpdev.preferences.scs.SCsTemplatePreferencePage;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Dmitry Lazurkin
 */
public class SCsyFileNewWizard extends AbstractScFileNewWizard {

    private class SCsyFileNewWizardPage extends AbstractScFileNewWizardPage {

        public SCsyFileNewWizardPage(IStructuredSelection selection) {
            super("New SCsy Source File Wizard", selection);
            setTitle("New SCsy Source File");
            setDescription("This wizard creates a new SCsy source file.");
        }

        @Override
        public TemplateStore getTemplateStore() {
            return ScpdevPlugin.getTemplateStore();
        }

        @Override
        public ContextTypeRegistry getContextTypeRegistry() {
            return ScpdevPlugin.getContextTypeRegistry();
        }

        @Override
        protected String[] getContextTypes() {
            return new String[] { SCsContextType.SCS_FILES_CONTEXT_TYPE };
        }

        @Override
        protected String getTemplatePreferencePageId() {
            return SCsTemplatePreferencePage.PREFERENCE_PAGE_ID;
        }

        @Override
        protected String getFileExtension() {
            return "scsy";
        }

    }

    @Override
    protected AbstractScFileNewWizardPage createFilePage() {
        return new SCsyFileNewWizardPage(selection);
    }

}

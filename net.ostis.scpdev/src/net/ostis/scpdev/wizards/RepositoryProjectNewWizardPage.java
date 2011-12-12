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

import java.net.URI;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * @author Dmitry Lazurkin
 */
public class RepositoryProjectNewWizardPage extends WizardNewProjectCreationPage {

    private ExistingRepositoryGroup existingRepositoryGroup;

    public RepositoryProjectNewWizardPage(String pageName) {
        super(pageName);
    }

    public void createControl(Composite parent) {
        super.createControl(parent);
        existingRepositoryGroup = new ExistingRepositoryGroup(getErrorReporter(), (Composite) getControl(), true);
        Dialog.applyDialogFont(getControl());
    }

    /**
     * Get an error reporter for the receiver.
     */
    private IErrorMessageReporter getErrorReporter() {
        return new IErrorMessageReporter() {
            public void reportError(String errorMessage, boolean infoOnly) {
                if (infoOnly) {
                    setMessage(errorMessage, IStatus.INFO);
                    setErrorMessage(null);
                } else {
                    setErrorMessage(errorMessage);
                }

                boolean valid = errorMessage == null;
                if (valid) {
                    valid = validatePage();
                }

                setPageComplete(valid);
            }
        };
    }

    public URI getRepositoryURI() {
        return existingRepositoryGroup.getProjectLocationURI();
    }

    @Override
    protected boolean validatePage() {
        boolean valid = super.validatePage();

        if (valid) {
            String message = existingRepositoryGroup.checkValidLocation();
            if (message != null) {
                setErrorMessage(message);
                return false;
            }
        }

        return valid;
    }

}

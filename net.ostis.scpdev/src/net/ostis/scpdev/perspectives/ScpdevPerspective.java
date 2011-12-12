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

package net.ostis.scpdev.perspectives;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

/**
 * @author Dmitry Lazurkin
 */
public class ScpdevPerspective implements IPerspectiveFactory {
    private IPageLayout factory;

    public ScpdevPerspective() {
        super();
    }

    public void createInitialLayout(IPageLayout factory) {
        this.factory = factory;
        addViews();
        addActionSets();
        addNewWizardShortcuts();
        addPerspectiveShortcuts();
        addViewShortcuts();
    }

    private void addViews() {
        // Creates the overall folder layout.
        // Note that each new Folder uses a percentage of the remaining
        // EditorArea.

        IFolderLayout bottom = factory.createFolder("bottomRight", // NON-NLS-1
                IPageLayout.BOTTOM, 0.75f, factory.getEditorArea());
        bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
        bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);

        IFolderLayout topLeft = factory.createFolder("topRight", // NON-NLS-1
                IPageLayout.LEFT, 0.25f, factory.getEditorArea());
        topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
    }

    private void addActionSets() {
        factory.addActionSet("org.eclipse.debug.ui.launchActionSet"); // NON-NLS-1
        factory.addActionSet("org.eclipse.debug.ui.debugActionSet"); // NON-NLS-1
        factory.addActionSet("org.eclipse.debug.ui.profileActionSet"); // NON-NLS-1
        factory.addActionSet("org.eclipse.jdt.debug.ui.JDTDebugActionSet"); // NON-NLS-1
        factory.addActionSet("org.eclipse.jdt.junit.JUnitActionSet"); // NON-NLS-1
        factory.addActionSet("org.eclipse.team.ui.actionSet"); // NON-NLS-1
        factory.addActionSet("org.eclipse.team.cvs.ui.CVSActionSet"); // NON-NLS-1
        factory.addActionSet("org.eclipse.ant.ui.actionSet.presentation"); // NON-NLS-1
        factory.addActionSet(JavaUI.ID_ACTION_SET);
        factory.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
        factory.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET); // NON-NLS-1
    }

    private void addPerspectiveShortcuts() {
        factory.addPerspectiveShortcut("org.eclipse.team.ui.TeamSynchronizingPerspective"); // NON-NLS-1
        factory.addPerspectiveShortcut("org.eclipse.team.cvs.ui.cvsPerspective"); // NON-NLS-1
        factory.addPerspectiveShortcut("org.eclipse.ui.resourcePerspective"); // NON-NLS-1
    }

    private void addNewWizardShortcuts() {
        factory.addNewWizardShortcut("org.eclipse.team.cvs.ui.newProjectCheckout");// NON-NLS-1
        factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");// NON-NLS-1
        factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");// NON-NLS-1
    }

    private void addViewShortcuts() {
        factory.addShowViewShortcut("org.eclipse.ant.ui.views.AntView"); // NON-NLS-1
        factory.addShowViewShortcut("org.eclipse.team.ccvs.ui.AnnotateView"); // NON-NLS-1
        factory.addShowViewShortcut("org.eclipse.pde.ui.DependenciesView"); // NON-NLS-1
        factory.addShowViewShortcut("org.eclipse.jdt.junit.ResultView"); // NON-NLS-1
        factory.addShowViewShortcut("org.eclipse.team.ui.GenericHistoryView"); // NON-NLS-1
        factory.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
        factory.addShowViewShortcut(JavaUI.ID_PACKAGES);
        factory.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        factory.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
        factory.addShowViewShortcut(IPageLayout.ID_OUTLINE);
    }

}

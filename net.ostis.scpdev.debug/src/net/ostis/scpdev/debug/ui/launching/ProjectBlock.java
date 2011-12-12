/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent
 * Systems) For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2011 OSTIS
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
package net.ostis.scpdev.debug.ui.launching;

import java.util.ArrayList;

import net.ostis.scpdev.StatusUtils;
import net.ostis.scpdev.core.ScNature;
import net.ostis.scpdev.debug.core.IDebugCoreConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * A block for selecting a scp project.
 *
 * @author Dmitry Lazurkin
 */
public class ProjectBlock extends AbstractLaunchConfigurationBlock {

    public static final String PROP_PROJECT_PATH = "PROP_PROJECT_PATH";

    private static final Log log = LogFactory.getLog(ProjectBlock.class);

    private Text projectText;
    private Button projectBrowseButton;

    public ProjectBlock() {
    }

    @Override
	public void createControl(Composite parent) {
        Font font = parent.getFont();
        Group group = new Group(parent, SWT.NONE);
        group.setText("Project");
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);
        group.setFont(font);

        // Project chooser
        projectText = new Text(group, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        projectText.setLayoutData(gd);
        projectText.setFont(font);
        projectText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText(ModifyEvent evt) {
                updateLaunchConfigurationDialog();
            }
        });

        projectBrowseButton = createPushButton(group, "Browse...", null); //$NON-NLS-1$
        projectBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
			public void widgetSelected(SelectionEvent e) {

                // Filter out project by python nature
                IWorkspace workspace = ResourcesPlugin.getWorkspace();
                IProject[] projects = workspace.getRoot().getProjects();
                ArrayList<IProject> scpProjects = new ArrayList<IProject>();
                for (IProject project : projects) {
                    try {
                        if (project.isOpen() && project.hasNature(ScNature.NATURE_ID)) {
                            scpProjects.add(project);
                        }
                    } catch (CoreException ex) {
                        log.error("Unexpected error", ex);
                    }

                }
                projects = scpProjects.toArray(new IProject[scpProjects.size()]);

                // Only allow the selection of projects, do not present
                // directories
                ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new WorkbenchLabelProvider());
                dialog.setTitle("Project selection");
                dialog.setMessage("Choose a project for the run");
                dialog.setElements(projects);

                dialog.open();

                Object object = dialog.getFirstResult();
                if ((object != null) && (object instanceof IProject)) {
                    IProject project = (IProject) object;
                    ScNature nature = ScNature.getScNature(project);
                    if (nature == null) {
                        // The project does not have an associated scp nature...
                        String msg = "The selected project must have the scp nature associated.";
                        String title = "Invalid project (no scp nature associated).";
                        ErrorDialog.openError(getShell(), title, msg,
                                StatusUtils.makeStatus(IStatus.WARNING, title, null));
                    }

                    String projectName = project.getName();
                    projectText.setText(projectName);
                }
                updateLaunchConfigurationDialog();
            }
        });
    }

    @Override
	public String getName() {
        return "Project";
    }

    @Override
	public void initializeFrom(ILaunchConfiguration configuration) {
        String projectName = "";
        try {
            projectName = configuration.getAttribute(IDebugCoreConstants.ATTR_PROJECT, "");
        } catch (CoreException e) {
        }
        projectText.setText(projectName);
    }

    @Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        String value = projectText.getText().trim();
        setAttribute(configuration, IDebugCoreConstants.ATTR_PROJECT, value);
    }

    @Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        // No defaults to set
    }

    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        boolean result = super.isValid(launchConfig);

        if (result) {
            setErrorMessage(null);
            setMessage(null);

            String projectName = projectText.getText();
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IResource resource = workspace.getRoot().findMember(projectName);

            if (resource == null || !(resource instanceof IProject)) {
                setErrorMessage("Invalid project");
                result = false;
            } else {
                IProject project = (IProject) resource;
                ScNature nature = ScNature.getScNature(project);
                if (nature == null) {
                    setErrorMessage("Invalid project (no scp nature associated).");
                    result = false;
                }
            }
        }

        if (result) {
            firePropertyChangeEvent(PROP_PROJECT_PATH, null, projectText.getText());
        } else {
            firePropertyChangeEvent(PROP_PROJECT_PATH, null, "");
        }

        return result;
    }

}

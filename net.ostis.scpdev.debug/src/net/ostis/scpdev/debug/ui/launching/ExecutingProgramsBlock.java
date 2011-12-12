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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ostis.scpdev.core.ScNature;
import net.ostis.scpdev.debug.core.IDebugCoreConstants;
import net.ostis.scpdev.ui.dialogs.ProgramSignSelectionDialog;
import net.ostis.scpdev.ui.dialogs.SegmentOrDirectorySelectionDialog;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
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
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * Configuration block for selecting run mode.
 *
 * @author Dmitry Lazurkin
 */
public class ExecutingProgramsBlock extends AbstractLaunchConfigurationBlock implements IPropertyChangeListener {

    private static final Log log = LogFactory.getLog(ExecutingProgramsBlock.class);

    private static final String NAME = "Programs for executing";

    private static final Pattern PROGRAM_URI_PATTERN = Pattern.compile("^/(.+/)+[^/]+$");

    private static final Pattern TESTSUITE_URI_PATTERN = Pattern.compile("^/.*$");

    private Button execStartupButton;

    private Button execProgramButton;

    private Group programPathGroup;

    private Text programPathText;

    private Button browseProgramButton;

    private Button execTestsuiteButton;

    private Group testsuitePathGroup;

    private Text testsuitePathText;

    private Button browseTestsuiteButton;

    private String projectName;

    @Override
    public void createControl(Composite parent) {
        Font font = parent.getFont();
        Group group = new Group(parent, SWT.NONE);
        group.setText(NAME);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        group.setLayout(layout);
        group.setFont(font);

        execStartupButton = new Button(group, SWT.RADIO);
        execStartupButton.setText("Run programs from startup");
        execStartupButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                caclucateEnabled();
                updateLaunchConfigurationDialog();
            }
        });

        execProgramButton = new Button(group, SWT.RADIO);
        execProgramButton.setText("Run program from path");
        execProgramButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                caclucateEnabled();
                updateLaunchConfigurationDialog();
            }
        });

        programPathGroup = new Group(group, SWT.NONE);
        programPathGroup.setText("Full URI to program node");
        gd = new GridData(GridData.FILL_HORIZONTAL);
        programPathGroup.setLayoutData(gd);
        layout = new GridLayout();
        layout.numColumns = 2;
        programPathGroup.setLayout(layout);
        programPathGroup.setFont(font);

        programPathText = new Text(programPathGroup, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        programPathText.setLayoutData(gd);
        programPathText.setFont(font);
        programPathText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                updateLaunchConfigurationDialog();
            }
        });
        browseProgramButton = new Button(programPathGroup, SWT.PUSH);
        browseProgramButton.setText("Browse...");
        browseProgramButton.setFont(font);
        browseProgramButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                IProject project = root.getProject(projectName);

                assert project != null;

                ScNature nature = ScNature.getScNature(project);

                if (nature != null) {
                    IFolder binroot = nature.getBinaryRoot();
                    if (binroot.exists()) {
                        ProgramSignSelectionDialog dialog = new ProgramSignSelectionDialog(getShell(), binroot);
                        if (dialog.open() == Dialog.OK) {
                            Object[] result = dialog.getResult();
                            if (result.length == 1) {
                                programPathText.setText(result[0].toString());
                            }
                        }
                    }
                }
            }
        });

        execTestsuiteButton = new Button(group, SWT.RADIO);
        execTestsuiteButton.setText("Run testsuite from path");
        execTestsuiteButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                caclucateEnabled();
                updateLaunchConfigurationDialog();
            }
        });

        testsuitePathGroup = new Group(group, SWT.NONE);
        testsuitePathGroup.setText("Full URI to testsuite's segment or testsuites directory");
        gd = new GridData(GridData.FILL_HORIZONTAL);
        testsuitePathGroup.setLayoutData(gd);
        layout = new GridLayout();
        layout.numColumns = 2;
        testsuitePathGroup.setLayout(layout);
        testsuitePathGroup.setFont(font);

        testsuitePathText = new Text(testsuitePathGroup, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        testsuitePathText.setLayoutData(gd);
        testsuitePathText.setFont(font);
        testsuitePathText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                updateLaunchConfigurationDialog();
            }
        });

        browseTestsuiteButton = new Button(testsuitePathGroup, SWT.PUSH);
        browseTestsuiteButton.setText("Browse...");
        browseTestsuiteButton.setFont(font);
        browseTestsuiteButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                IProject project = root.getProject(projectName);

                assert project != null;

                ScNature nature = ScNature.getScNature(project);

                if (nature != null) {
                    IFolder binroot = nature.getBinaryRoot();
                    if (binroot.exists()) {
                        SelectionDialog dialog = new SegmentOrDirectorySelectionDialog(getShell(), binroot);
                        if (dialog.open() == Dialog.OK) {
                            Object[] result = dialog.getResult();
                            if (result.length == 1) {
                                IResource resource = (IResource) result[0];
                                testsuitePathText.setText("/"
                                        + resource.getFullPath().makeRelativeTo(binroot.getFullPath()).toString());
                            }
                        }
                    }
                }
            }
        });

        caclucateEnabled();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(ProjectBlock.PROP_PROJECT_PATH)) {
            projectName = (String) event.getNewValue();
            caclucateEnabled();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(IDebugCoreConstants.ATTR_RUN_MODE, IDebugCoreConstants.RUN_MODE_DEFAULT);
        configuration.setAttribute(IDebugCoreConstants.ATTR_PROGRAM_PATH, "");
        configuration.setAttribute(IDebugCoreConstants.ATTR_TESTSUITE_PATH, "");
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            String runMode = configuration.getAttribute(IDebugCoreConstants.ATTR_RUN_MODE, IDebugCoreConstants.RUN_MODE_DEFAULT);
            if (runMode.equals(IDebugCoreConstants.RUN_MODE_DEFAULT)) {
                execStartupButton.setSelection(true);
            } else if (runMode.equals(IDebugCoreConstants.RUN_MODE_PROGRAM_FROM_PATH)) {
                execProgramButton.setSelection(true);
            } else if (runMode.equals(IDebugCoreConstants.RUN_MODE_TESTSUITE_FROM_PATH)) {
                execTestsuiteButton.setSelection(true);
            }

            programPathText.setText(configuration.getAttribute(IDebugCoreConstants.ATTR_PROGRAM_PATH, ""));
            testsuitePathText.setText(configuration.getAttribute(IDebugCoreConstants.ATTR_TESTSUITE_PATH, ""));

            projectName = configuration.getAttribute(IDebugCoreConstants.ATTR_PROJECT, "");

            caclucateEnabled();
        } catch (CoreException e) {
            log.error("Error while initialize from launch configuration " + configuration, e);
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        if (execStartupButton.getSelection()) {
            configuration.setAttribute(IDebugCoreConstants.ATTR_RUN_MODE, IDebugCoreConstants.RUN_MODE_DEFAULT);
        } else if (execProgramButton.getSelection()) {
            configuration.setAttribute(IDebugCoreConstants.ATTR_RUN_MODE, IDebugCoreConstants.RUN_MODE_PROGRAM_FROM_PATH);
        } else if (execTestsuiteButton.getSelection()) {
            configuration.setAttribute(IDebugCoreConstants.ATTR_RUN_MODE, IDebugCoreConstants.RUN_MODE_TESTSUITE_FROM_PATH);
        }

        configuration.setAttribute(IDebugCoreConstants.ATTR_PROGRAM_PATH, programPathText.getText());
        configuration.setAttribute(IDebugCoreConstants.ATTR_TESTSUITE_PATH, testsuitePathText.getText());
    }

    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        boolean result = super.isValid(launchConfig);

        if (result) {
            setErrorMessage(null);
            setMessage(null);

            if (execProgramButton.getSelection()) {
                String programURI = programPathText.getText().trim();
                Matcher matcher = PROGRAM_URI_PATTERN.matcher(programURI);

                if (matcher.matches() == false) {
                    setErrorMessage("Bad URI to program node");
                    result = false;
                }
            }

            if (execTestsuiteButton.getSelection()) {
                String programURI = testsuitePathText.getText().trim();
                Matcher matcher = TESTSUITE_URI_PATTERN.matcher(programURI);

                if (matcher.matches() == false) {
                    setErrorMessage("Bad URI for testsuite");
                    result = false;
                }
            }
        }

        return result;
    }

    private void caclucateEnabled() {
        boolean availableProject = StringUtils.isNotEmpty(projectName);

        boolean enabled = execProgramButton.getSelection() && availableProject;
        programPathGroup.setEnabled(enabled);
        programPathText.setEnabled(enabled);
        browseProgramButton.setEnabled(enabled);

        enabled = execTestsuiteButton.getSelection() && availableProject;
        testsuitePathGroup.setEnabled(enabled);
        testsuitePathText.setEnabled(enabled);
        browseTestsuiteButton.setEnabled(enabled);
    }

}

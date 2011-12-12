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
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;
import org.eclipse.ui.internal.ide.filesystem.FileSystemConfiguration;
import org.eclipse.ui.internal.ide.filesystem.FileSystemSupportRegistry;

/**
 * @author Dmitry Lazurkin
 */
public final class ExistingRepositoryGroup {
    private Button useExistingCheckbox;

    private Label locationLabel;

    private Text locationPathField;

    private static final int SIZING_TEXT_FIELD_WIDTH = 250;

    private Button browseButton;

    private IErrorMessageReporter errorReporter;

    public ExistingRepositoryGroup(IErrorMessageReporter errorReporter, Composite composite, boolean defaultEnabled) {
        this.errorReporter = errorReporter;

        Group group = new Group(composite, SWT.NONE);
        group.setFont(composite.getFont());
        group.setText("Existing repository location");
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        group.setLayout(new GridLayout(1, false));

        createContent(group);
    }

    public boolean isUseExisting() {
        return useExistingCheckbox.getSelection();
    }

    /**
     * Get the URI for the location field if possible.
     *
     * @return URI or <code>null</code> if it is not valid.
     */
    public URI getProjectLocationURI() {
        FileSystemConfiguration configuration = FileSystemSupportRegistry.getInstance().getDefaultConfiguration();
        return configuration.getContributor().getURI(locationPathField.getText());
    }

    /**
     * Check if the entry in the widget location is valid. If it is valid return
     * null. Otherwise return a string that indicates the problem.
     */
    public String checkValidLocation() {
        String locationFieldContents = locationPathField.getText();
        if (locationFieldContents.length() == 0) {
            return "Existing repository location field is empty";
        }

        URI newPath = getProjectLocationURI();
        if (newPath == null) {
            return "Invalid existing repository path";
        }

        return null;
    }

    private void createContent(Composite parent) {
        int columns = 4;

        Composite locationGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = columns;
        locationGroup.setLayout(layout);
        locationGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        useExistingCheckbox = new Button(locationGroup, SWT.CHECK | SWT.RIGHT);
        useExistingCheckbox.setText("Use existing repository");
        GridData buttonData = new GridData();
        buttonData.horizontalSpan = columns;
        useExistingCheckbox.setLayoutData(buttonData);
        useExistingCheckbox.setEnabled(false);

        createLocationArea(locationGroup);

        useExistingCheckbox.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                boolean useExisting = useExistingCheckbox.getSelection();

                if (useExisting) {
                    updateLocationField(locationPathField.getText());
                }

                String error = checkValidLocation();
                errorReporter.reportError(error, error != null && error.equals("Existing repository location is empty"));
                setUserAreaEnabled(!useExisting);
            }
        });

        useExistingCheckbox.setSelection(true);
    }

    private void createLocationArea(Composite composite) {
        // location label
        locationLabel = new Label(composite, SWT.NONE);
        locationLabel.setText("Location");

        // project location entry field
        locationPathField = new Text(composite, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        data.horizontalSpan = 2;
        locationPathField.setLayoutData(data);

        // browse button
        browseButton = new Button(composite, SWT.PUSH);
        browseButton.setText("Browse...");
        browseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                handleLocationBrowseButtonPressed();
            }
        });

        locationPathField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                errorReporter.reportError(checkValidLocation(), false);
            }
        });
    }

    /**
     * Set the enablement state of the receiver.
     *
     * @param enabled
     */
    private void setUserAreaEnabled(boolean enabled) {
        locationLabel.setEnabled(enabled);
        locationPathField.setEnabled(enabled);
        browseButton.setEnabled(enabled);
    }

    /**
     * Open an appropriate directory browser
     */
    private void handleLocationBrowseButtonPressed() {
        String selectedDirectory = null;

        String dirName = getPathFromLocationField();
        if (StringUtils.isNotEmpty(dirName)) {
            IFileInfo info = IDEResourceInfoUtils.getFileInfo(dirName);

            if (info == null || !info.exists())
                dirName = "";
        } else {
            dirName = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
        }

        DirectoryDialog dialog = new DirectoryDialog(locationPathField.getShell(), SWT.SHEET);
        dialog.setMessage(IDEWorkbenchMessages.ProjectLocationSelectionDialog_directoryLabel);
        dialog.setFilterPath(dirName);
        selectedDirectory = dialog.open();

        if (selectedDirectory != null) {
            updateLocationField(selectedDirectory);
        }
    }

    /**
     * Update the location field based on the selected path.
     *
     * @param selectedPath
     */
    private void updateLocationField(String selectedPath) {
        locationPathField.setText(TextProcessor.process(selectedPath));
    }

    /**
     * Return the path on the location field.
     */
    private String getPathFromLocationField() {
        URI fieldURI;
        try {
            fieldURI = new URI(locationPathField.getText());
        } catch (URISyntaxException e) {
            return locationPathField.getText();
        }
        return fieldURI.getPath();
    }

}

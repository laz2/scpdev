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

import java.util.ArrayList;
import java.util.TreeMap;

import net.ostis.scpdev.ui.dialogs.ScSourceFolderSelectionDialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * @author Dmitry Lazurkin
 */
public abstract class AbstractScFileNewWizardPage extends WizardPage {

    private static final Log log = LogFactory.getLog(AbstractScFileNewWizardPage.class);

    private Text sourceFolderText;

    private Text fileText;

    private IStructuredSelection selection;

    private List templateList;

    private TreeMap<String, TemplatePersistenceData> name2templateData;

    public AbstractScFileNewWizardPage(String pageName, IStructuredSelection selection) {
        super(pageName);
        this.selection = selection;
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;

        createSourceFolderSelect(container);
        createFileSelect(container);
        createTemplateSelect(container);

        initialize();
        dialogChanged();
        setControl(container);
    }

    private void createSourceFolderSelect(Composite parent) {
        Label label = new Label(parent, SWT.NULL);
        label.setText("&Source Folder:");

        sourceFolderText = new Text(parent, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        sourceFolderText.setLayoutData(gd);
        sourceFolderText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        Button button = new Button(parent, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ContainerSelectionDialog dialog = new ScSourceFolderSelectionDialog(getShell(), ResourcesPlugin
                        .getWorkspace().getRoot(), false, "Select new file container");
                if (dialog.open() == Dialog.OK) {
                    Object[] result = dialog.getResult();
                    if (result.length == 1) {
                        sourceFolderText.setText(((Path) result[0]).toString());
                    }
                }
            }
        });
    }

    private void createFileSelect(Composite parent) {
        Label label = new Label(parent, SWT.NULL);
        label.setText("&File name:");

        fileText = new Text(parent, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        fileText.setLayoutData(gd);
        fileText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
    }

    public abstract TemplateStore getTemplateStore();

    public abstract ContextTypeRegistry getContextTypeRegistry();

    protected abstract String[] getContextTypes();

    protected abstract String getTemplatePreferencePageId();

    protected abstract String getFileExtension();

    /**
     * @return the data for the selected template or null if not available.
     */
    public TemplatePersistenceData getSelectedTemplate() {
        if (templateList != null && name2templateData != null) {
            String[] sel = templateList.getSelection();
            if (sel != null && sel.length > 0) {
                return name2templateData.get(sel[0]);
            }
        }
        return null;
    }

    private void createTemplateSelect(Composite parent) {
        final TemplateStore templateStore = getTemplateStore();
        if (templateStore != null) {
            TemplatePersistenceData[] templateData = templateStore.getTemplateData(false);
            if (templateData != null && templateData.length > 0) {
                // create the template selection
                Label label = new Label(parent, SWT.NONE);
                label.setText("Template");

                templateList = new List(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
                GridData gd = new GridData(GridData.FILL_HORIZONTAL);
                gd.verticalSpan = 2;
                templateList.setLayoutData(gd);

                fillTemplateOptions(templateData, templateList);

                // if in the text, pressing down should go to the templates list
                fileText.addKeyListener(new KeyListener() {

                    public void keyPressed(KeyEvent e) {
                    }

                    public void keyReleased(KeyEvent e) {
                        if (e.keyCode == SWT.ARROW_DOWN) {
                            templateList.setFocus();
                        }
                    }
                });

                Link link = new Link(parent, SWT.NONE);
                link.setText("<a>Config...</a>");

                link.addSelectionListener(new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null,
                                getTemplatePreferencePageId(), null, null);
                        dialog.open();
                        // Fill it after having the settings edited.
                        TemplatePersistenceData[] templateData = templateStore.getTemplateData(false);
                        if (templateData != null && templateData.length > 0) {
                            fillTemplateOptions(templateData, templateList);
                        } else {
                            fillTemplateOptions(new TemplatePersistenceData[0], templateList);
                        }
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                    }
                });
            }
        }
    }

    /**
     * Sets the template options in the passed list (swt)
     */
    private void fillTemplateOptions(TemplatePersistenceData[] templateData, List list) {
        name2templateData = new TreeMap<String, TemplatePersistenceData>();

        String[] neededContextTypes = getContextTypes();

        for (TemplatePersistenceData data : templateData) {
            for (int i = 0; i < neededContextTypes.length; i++) {
                if (data.getTemplate().getContextTypeId().equals(neededContextTypes[i])) {
                    String name = data.getTemplate().getName();
                    name2templateData.put(name, data);
                    break;
                }
            }
        }

        ArrayList<String> lst = new ArrayList<String>(name2templateData.keySet());
        list.setItems(lst.toArray(new String[lst.size()]));
        list.setSelection(0);
    }

    private void initialize() {
        if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            if (ssel.size() > 1)
                return;
            Object obj = ssel.getFirstElement();
            if (obj instanceof IResource) {
                IContainer container;
                if (obj instanceof IContainer)
                    container = (IContainer) obj;
                else
                    container = ((IResource) obj).getParent();
                sourceFolderText.setText(container.getFullPath().toString());
            }
        }
        fileText.setText("new_file." + getFileExtension());
    }

    private void dialogChanged() {
        IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getSourceFolderName()));
        String fileName = getFileName();

        if (getSourceFolderName().length() == 0) {
            updateStatus("Source folder must be specified");
            return;
        }

        if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
            updateStatus("Source folder must exist");
            return;
        }

        if (!container.isAccessible()) {
            updateStatus("Project must be writable");
            return;
        }

        if (fileName.length() == 0) {
            updateStatus("File name must be specified");
            return;
        }

        if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
            updateStatus("File name must be valid");
            return;
        }

        int dotLoc = fileName.lastIndexOf('.');
        if (dotLoc != -1) {
            String ext = fileName.substring(dotLoc + 1);
            if (ext.equalsIgnoreCase(getFileExtension()) == false) {
                updateStatus("File extension must be \"" + getFileExtension() + "\"");
                return;
            }
        }

        updateStatus(null);
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getSourceFolderName() {
        return sourceFolderText.getText();
    }

    public String getFileName() {
        return fileText.getText();
    }

}

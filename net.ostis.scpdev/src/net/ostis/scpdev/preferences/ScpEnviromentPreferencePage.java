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
package net.ostis.scpdev.preferences;

import java.io.File;

import net.ostis.scpdev.ScpdevPlugin;
import net.ostis.scpdev.external.ScCoreModule;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preferences page for executing enviroment of sc-repository.
 *
 * @author Dmitry Lazurkin
 */
public class ScpEnviromentPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private BooleanFieldEditor useScCoreHomeField;

    private DirectoryFieldEditor sccoreDirField;

    private FileFieldEditor pythonExecutableField;

    private FileFieldEditor systemUIStartFileField;

    public ScpEnviromentPreferencePage() {
        super(GRID);
        setPreferenceStore(ScpdevPlugin.getDefault().getPreferenceStore());
        setDescription("Selecting SCP enviroment tools and modules paths");
    }

    private class DirectoryFieldEditorOnKeyStroke extends DirectoryFieldEditor {

        public DirectoryFieldEditorOnKeyStroke(String name, String labelText, Composite parent) {
            super(name, labelText, parent);
        }

        @Override
        public Text getTextControl(org.eclipse.swt.widgets.Composite parent) {
            setValidateStrategy(VALIDATE_ON_KEY_STROKE); // FIXME: find another
                                                         // way for setting
                                                         // validate strategy =(
            return super.getTextControl(parent);
        };

    }

    protected void createFieldEditors() {
        useScCoreHomeField = new BooleanFieldEditor(PreferenceConstants.P_USE_SC_CORE_HOME,
                "Use enviroment variable SC_CORE_HOME for searching sc-core", getFieldEditorParent());
        sccoreDirField = new DirectoryFieldEditorOnKeyStroke(PreferenceConstants.P_SC_CORE_PATH,
                "SC-core root directory:", getFieldEditorParent());
        systemUIStartFileField = new FileFieldEditor(PreferenceConstants.P_SYSTEM_UI_PATH, "UI system path to start script:",
                true, StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent());
        pythonExecutableField = new FileFieldEditor(PreferenceConstants.P_PYTHON_EXECUTABLE, "Path to python executable:",
                true, StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent());
        addField(useScCoreHomeField);
        addField(sccoreDirField);
        addField(systemUIStartFileField);
        addField(pythonExecutableField);
    }

    @Override
    protected void initialize() {
        super.initialize();
        calculateEnabled();
    }

    private void calculateEnabled() {
        if (useScCoreHomeField.getBooleanValue()) {
            sccoreDirField.setEnabled(false, getFieldEditorParent());
        } else {
            sccoreDirField.setEnabled(true, getFieldEditorParent());
        }
    }

    @Override
    protected void checkState() {
        super.checkState();

        if (!isValid())
            return;

        if (useScCoreHomeField.getBooleanValue()) {
            String errorMessage = ScCoreModule.validateHomeVariable();
            setValid(errorMessage == null);
            if (errorMessage != null) {
                setErrorMessage("While validating SC_CORE_HOME: " + errorMessage);
            } else {
                setErrorMessage(null);
            }
        } else {
            File dir = new File(sccoreDirField.getStringValue());
            String errorMessage = ScCoreModule.validateRoot(dir);
            setValid(errorMessage == null);
            if (errorMessage != null) {
                setErrorMessage("While validating specified sc-core root: " + errorMessage);
            } else {
                setErrorMessage(null);
            }
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        super.propertyChange(event);

        if (event.getProperty().equals(FieldEditor.VALUE)) {
            calculateEnabled();
            checkState();
        }
    }

    public void init(IWorkbench workbench) {
    }

}

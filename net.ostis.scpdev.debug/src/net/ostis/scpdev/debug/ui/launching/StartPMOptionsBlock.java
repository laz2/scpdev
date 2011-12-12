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

import java.util.HashMap;
import java.util.Map;

import net.ostis.scpdev.debug.core.IDebugCoreConstants;

import org.apache.commons.lang3.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * Configuration block for selecting switch arguments for start-pm utility.
 *
 * @author Dmitry Lazurkin
 */
public class StartPMOptionsBlock extends AbstractLaunchConfigurationBlock {

    private static final Log log = LogFactory.getLog(StartPMOptionsBlock.class);

    private Map<Button, Pair<String, Boolean>> optionsMapping;

    @Override
    public void createControl(Composite parent) {
        Font font = parent.getFont();
        Group group = new Group(parent, SWT.NONE);
        group.setText("Options");
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        group.setLayout(layout);
        group.setFont(font);

        optionsMapping = new HashMap<Button, Pair<String, Boolean>>();

        createCheckbox(group, "Verbose execution (Print executed operators) (--verbose)", IDebugCoreConstants.ATTR_VERBOSE_EXEC, false);
        createCheckbox(group, "Include diagnostic informantion in output (--diagnostic)",
                IDebugCoreConstants.ATTR_DIAGNOSTIC_EXEC, false);
        createCheckbox(group, "Infinite scheduler loop (--stay)", IDebugCoreConstants.ATTR_STAY_EXEC, false);
        createCheckbox(group, "Stop executing when interpreter encounters error (--stop-on-error)",
                IDebugCoreConstants.ATTR_STOP_ON_ERROR, true);
    }

    @Override
    public String getName() {
        return "Options";
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        for (Map.Entry<Button, Pair<String, Boolean>> entry : optionsMapping.entrySet()) {
            Button checkbox = entry.getKey();
            Pair<String, Boolean> pair = entry.getValue();

            try {
                checkbox.setSelection(configuration.getAttribute(pair.left, pair.right));
            } catch (CoreException e) {
                log.error("Error while retrieving configuration attr " + entry.getValue(), e);
            }
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        for (Map.Entry<Button, Pair<String, Boolean>> entry : optionsMapping.entrySet()) {
            Button checkbox = entry.getKey();
            Pair<String, Boolean> pair = entry.getValue();

            configuration.setAttribute(pair.left, checkbox.getSelection());
        }
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(IDebugCoreConstants.ATTR_DIAGNOSTIC_EXEC, false);
        configuration.setAttribute(IDebugCoreConstants.ATTR_VERBOSE_EXEC, false);
        configuration.setAttribute(IDebugCoreConstants.ATTR_STAY_EXEC, false);
        configuration.setAttribute(IDebugCoreConstants.ATTR_STOP_ON_ERROR, true);
    }

    private Button createCheckbox(Composite parent, String text, String attr, boolean checkDefault) {
        Button checkbox = new Button(parent, SWT.CHECK);
        checkbox.setText(text);
        checkbox.setSelection(checkDefault);
        checkbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateLaunchConfigurationDialog();
            }
        });
        optionsMapping.put(checkbox, new Pair<String, Boolean>(attr, checkDefault));
        return checkbox;
    }

}

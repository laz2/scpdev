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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Launch configuration tab which is set of blocks. Each block must implement
 * ILaunchConfigurationTab.
 *
 * @author Dmitry Lazurkin
 */
public abstract class AbstractLaunchConfigurationBlocksTab extends AbstractLaunchConfigurationTab {

    private List<ILaunchConfigurationTab> blocks = new LinkedList<ILaunchConfigurationTab>();

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        setControl(composite);
        GridLayout gridLayout = new GridLayout();
        composite.setLayout(gridLayout);

        for (ILaunchConfigurationTab block : blocks) {
            block.createControl(composite);
        }
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        for (ILaunchConfigurationTab block : blocks) {
            block.initializeFrom(configuration);
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        for (ILaunchConfigurationTab block : blocks) {
            block.performApply(configuration);
        }
    }

    @Override
    public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
        super.setLaunchConfigurationDialog(dialog);

        for (ILaunchConfigurationTab block : blocks) {
            block.setLaunchConfigurationDialog(dialog);
        }
    }

    @Override
    public String getErrorMessage() {
        String result = super.getErrorMessage();

        if (result == null) {
            for (ILaunchConfigurationTab block : blocks) {
                result = block.getErrorMessage();

                if (result != null) {
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public String getMessage() {
        String result = super.getMessage();

        if (result == null) {
            for (ILaunchConfigurationTab block : blocks) {
                result = block.getMessage();

                if (result != null) {
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        boolean result = super.isValid(launchConfig);

        if (result) {
            for (ILaunchConfigurationTab block : blocks) {
                result = block.isValid(launchConfig);

                if (result == false) {
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        // No defaults to set
    }

    /**
     * Add block to tab.
     */
    protected void addBlock(ILaunchConfigurationTab block) {
        blocks.add(block);
    }

}

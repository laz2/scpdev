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

import org.eclipse.swt.graphics.Image;

/**
 * @author Dmitry Lazurkin
 */
public class StartPMMainTab extends AbstractLaunchConfigurationBlocksTab {

    private ProjectBlock projectBlock;

    private StartPMOptionsBlock optionsBlock;

    private ExecutingProgramsBlock execProgramsBlock;

    public StartPMMainTab() {
        projectBlock = new ProjectBlock();
        optionsBlock = new StartPMOptionsBlock();
        execProgramsBlock = new ExecutingProgramsBlock();

        projectBlock.addPropertyChangeListener(execProgramsBlock);

        addBlock(projectBlock);
        addBlock(execProgramsBlock);
        addBlock(optionsBlock);
    }

    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public String getName() {
        return "Main";
    }
}

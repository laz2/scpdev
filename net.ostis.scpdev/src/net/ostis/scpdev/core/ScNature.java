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
package net.ostis.scpdev.core;

import net.ostis.scpdev.builder.ScRepositoryBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * Natur fo SC-projects. Ensure that project has fs_repo_src resource folder.
 *
 * @author Dmitry Lazurkin
 */
public class ScNature implements IScNature {

    public static final String NATURE_ID = "net.ostis.scpdev.sc_nature";

    private static final Log log = LogFactory.getLog(ScNature.class);

    private IProject project;

    public void configure() throws CoreException {
        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();

        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].getBuilderName().equals(ScRepositoryBuilder.BUILDER_ID)) {
                return;
            }
        }

        ICommand[] newCommands = new ICommand[commands.length + 1];
        System.arraycopy(commands, 0, newCommands, 0, commands.length);
        ICommand command = desc.newCommand();
        command.setBuilderName(ScRepositoryBuilder.BUILDER_ID);
        newCommands[newCommands.length - 1] = command;
        desc.setBuildSpec(newCommands);
        project.setDescription(desc, null);
    }

    public void deconfigure() throws CoreException {
        IProjectDescription description = getProject().getDescription();
        ICommand[] commands = description.getBuildSpec();
        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].getBuilderName().equals(ScRepositoryBuilder.BUILDER_ID)) {
                ICommand[] newCommands = new ICommand[commands.length - 1];
                System.arraycopy(commands, 0, newCommands, 0, i);
                System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
                description.setBuildSpec(newCommands);
                project.setDescription(description, null);
                return;
            }
        }
    }

    public IProject getProject() {
        return project;
    }

    public void setProject(IProject project) {
        this.project = project;
    }

    public static synchronized ScNature getScNature(IProject project) {
        if (project != null && project.isOpen()) {
            try {
                IProjectNature n = project.getNature(NATURE_ID);
                if (n instanceof ScNature) {
                    return (ScNature) n;
                }
            } catch (CoreException e) {
                log.info("Exception while retrieving nature of project", e);
            }
        }

        return null;
    }

    @Override
    public IFolder getSourceRoot() throws CoreException {
        return getProject().getFolder("fs_repo_src");
    }

    @Override
    public IFolder getBinaryRoot() {
        return getProject().getFolder("fs_repo");
    }

    @Override
    public IFolder getBinaryRootAndCreate() throws CoreException {
        IFolder binroot = getBinaryRoot();

        if (binroot.exists() == false) {
            binroot.create(true, true, null);
            binroot.setDerived(true, null);
        }

        return binroot;
    }

}

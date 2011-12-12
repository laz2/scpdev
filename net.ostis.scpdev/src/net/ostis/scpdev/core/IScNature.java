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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * Nature for SC-projects.
 *
 * @author Dmitry Lazurkin
 */
public interface IScNature extends IProjectNature {

    /**
     * @return source root of SC-project.
     * @throws CoreException if source root doesn't exist.
     */
    public IFolder getSourceRoot() throws CoreException;

    /**
     * Not check existence of binaru root folder.
     * @return binary repository root folder.
     */
    public IFolder getBinaryRoot();

    /**
     * Create binary root folder if it doesn't exist.
     * @return binary repository root folder.
     */
    public IFolder getBinaryRootAndCreate() throws CoreException;

}

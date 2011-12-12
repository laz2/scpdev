/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent Systems)
 * For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2011 OSTIS
 *
 * OSTIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OSTIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OSTIS.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ostis.scpdev.debug.core.launching;

import net.ostis.scpdev.core.ScNature;
import net.ostis.scpdev.debug.core.IDebugCoreConstants;

import org.apache.commons.lang3.Validate;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;

/**
 * Compute the default source lookup path for a PM launch configuration.
 *
 * @author Dmitry Lazurkin
 */
public class PMSourcePathComputerDelegate implements ISourcePathComputerDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.sourcelookup.ISourcePathComputerDelegate#computeSourceContainers(org.eclipse.debug.core.ILaunchConfiguration, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor)
			throws CoreException {
		String path = configuration.getAttribute(IDebugCoreConstants.ATTR_PROJECT, (String) null);
		Validate.notNull(path);

		IProject project = (IProject) ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(path));
		ISourceContainer sourceContainer = new FolderSourceContainer(ScNature.getScNature(project).getSourceRoot(), false);

		return new ISourceContainer[] {
			sourceContainer
		};
	}

}

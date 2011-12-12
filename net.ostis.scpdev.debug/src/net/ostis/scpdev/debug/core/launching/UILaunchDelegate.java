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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.ostis.scpdev.ScpdevPlugin;
import net.ostis.scpdev.debug.core.IDebugCoreConstants;
import net.ostis.scpdev.preferences.PreferenceConstants;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

/**
 * Launch system and suit with specified options.
 *
 * @author Dmitry Lazurkin
 */
public class UILaunchDelegate extends LaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		List<String> args = new LinkedList<String>();

		String systemStart = ScpdevPlugin.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.P_SYSTEM_UI_PATH);
		String pythonExecutable = ScpdevPlugin.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.P_PYTHON_EXECUTABLE);

		if (StringUtils.isEmpty(systemStart) || StringUtils.isEmpty(pythonExecutable))
			throw new CoreException(new Status(IStatus.ERROR, ScpdevPlugin.PLUGIN_ID,
					"Cannot start repository with UI because bad configuration of python or UI system"));

		args.add(pythonExecutable);

		args.add(systemStart);
		addFsRepoArg(configuration, args);

		String argsStr = "";
		for (String arg : args)
			argsStr = argsStr + " " + arg;

		String[] commandLine = (String[]) args.toArray(new String[args.size()]);
		Process process = DebugPlugin.exec(commandLine, new File(systemStart).getParentFile().getAbsoluteFile());
		DebugPlugin.newProcess(launch, process, pythonExecutable);
	}

	private void addFsRepoArg(ILaunchConfiguration configuration, List<String> args) throws CoreException {
		String projectPath = configuration.getAttribute(IDebugCoreConstants.ATTR_PROJECT, "");
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectPath);
		String fsrepoPath = project.getFolder("fs_repo").getLocation().toOSString();

		args.add("--fsrepo \"" + fsrepoPath + "\"");
	}

}

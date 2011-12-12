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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ostis.scpdev.debug.core.IDebugCoreConstants;
import net.ostis.scpdev.debug.core.model.PMDebugTarget;
import net.ostis.scpdev.external.ScCoreModule;

import org.apache.commons.lang3.Validate;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

/**
 * Launch start-pm utility with specified options.
 *
 * @author Dmitry Lazurkin
 */
public class StartPMLaunchDelegate extends LaunchConfigurationDelegate {

	private static Map<String, String> switchOptions = new HashMap<String, String>();

    static {
        switchOptions.put(IDebugCoreConstants.ATTR_VERBOSE_EXEC, "-v");
        switchOptions.put(IDebugCoreConstants.ATTR_DIAGNOSTIC_EXEC, "-d");
        switchOptions.put(IDebugCoreConstants.ATTR_STAY_EXEC, "--stay");
        switchOptions.put(IDebugCoreConstants.ATTR_STOP_ON_ERROR, "--stop-on-error");
    }

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
            throws CoreException {
        List<String> args = new LinkedList<String>();

        args.add(ScCoreModule.getStartPM());

        for (Map.Entry<String, String> entry : switchOptions.entrySet()) {
            boolean value = configuration.getAttribute(entry.getKey(), false);
            if (value)
                args.add(entry.getValue());
        }

//        args.add("--no-fsrepo");
        addFsRepoArg(configuration, args);
        addProgramRun(configuration, args);

		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			args.add("--with-rgp-server");
			args.add("--with-debugger");
//			args.add("--stay");
		}

		String[] commandLine = (String[]) args.toArray(new String[args.size()]);
		Process process = DebugPlugin.exec(commandLine, null);
		IProcess p = DebugPlugin.newProcess(launch, process, ScCoreModule.getStartPM());
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			IDebugTarget target = new PMDebugTarget(launch, p);
			launch.addDebugTarget(target);
		}
    }

    private void addProgramRun(ILaunchConfiguration configuration, List<String> args) throws CoreException {
        String runMode = configuration.getAttribute(IDebugCoreConstants.ATTR_RUN_MODE, "");

        Validate.notEmpty(runMode);

        if (runMode.equals(IDebugCoreConstants.RUN_MODE_DEFAULT)) {
            // Do nothing
        } else if (runMode.equals(IDebugCoreConstants.RUN_MODE_PROGRAM_FROM_PATH)) {
            args.add(String.format("--run-program \"%s\"", configuration.getAttribute(IDebugCoreConstants.ATTR_PROGRAM_PATH, "")));
        } else if (runMode.equals(IDebugCoreConstants.RUN_MODE_TESTSUITE_FROM_PATH)) {
            args.add(String.format("--run-testsuite \"%s\"", configuration.getAttribute(IDebugCoreConstants.ATTR_TESTSUITE_PATH, "")));
        }
    }

    private void addFsRepoArg(ILaunchConfiguration configuration, List<String> args) throws CoreException {
        String projectPath = configuration.getAttribute(IDebugCoreConstants.ATTR_PROJECT, "");
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject(projectPath);
        String fsrepoPath = project.getFolder("fs_repo").getLocation().toOSString();

        args.add("--fsrepo \"" + fsrepoPath + "\"");
    }

}

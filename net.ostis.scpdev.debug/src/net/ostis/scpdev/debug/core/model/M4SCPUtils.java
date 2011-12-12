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
package net.ostis.scpdev.debug.core.model;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ostis.scpdev.StatusUtils;
import net.ostis.scpdev.core.ScNature;
import net.ostis.scpdev.debug.core.IDebugCoreConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.Validate;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;

/**
 * Utils for working with m4scp source files.
 *
 * @author Dmitry Lazurkin
 */
public class M4SCPUtils {

    private static Pattern programStartDeclrPattern = Pattern.compile("\\s*(program|procedure)\\((\\w+),.?");

    private static Pattern programEndDeclrPattern = Pattern.compile("(\\s|\\))*end");

	/**
	 * Iterate over line in source file to line with lineNumber
	 * and find program or procedure declaration with less or equal line number.
	 * @return program name if success else null.
	 */
	public static String getProgramNameFromLine(final IFile file, final int lineNumber) throws CoreException {
		try {
			LineIterator iter = FileUtils.lineIterator(file.getLocation().toFile(), file.getCharset());
			String curProgram = null;
			int curLineNumber = 0;
			while (iter.hasNext() && curLineNumber < lineNumber) {
				++curLineNumber;
				String line = iter.nextLine();

				// Find start of declaration program or procedure in line.
				Matcher startMatcher = programStartDeclrPattern.matcher(line);
				if (startMatcher.matches())
					curProgram = startMatcher.group(2);

				// Find end of declaration program or procedure in line.
				Matcher endMatcher = programEndDeclrPattern.matcher(line);
				if (endMatcher.matches())
					curProgram = null;
			}

			return curProgram;
		} catch (IOException e) {
			throw new CoreException(StatusUtils.makeStatus(IStatus.ERROR, "Error find m4scp program name", e));
		}
	}

	public static String getUriFromSource(final IFile file) throws CoreException {
		IFolder srcroot = ScNature.getScNature(file.getProject()).getSourceRoot();
		IPath pathSrc = file.getFullPath().makeRelativeTo(srcroot.getFullPath());
		IPath pathBin = pathSrc.removeFileExtension();
		return pathBin.makeAbsolute().toString();
	}

	public static String getProgramUriFromLine(final IFile file, final int lineNumber) throws CoreException {
		String programName = getProgramNameFromLine(file, lineNumber);
		if (programName != null) {
			return getUriFromSource(file) + "/" + programName;
		} else {
			return null;
		}
	}

	public static IFile getSourceFromUri(final IProject project, final String uri) throws CoreException {
		IFolder srcroot = ScNature.getScNature(project).getSourceRoot();
		IFile source = srcroot.getFile(new Path(uri + ".m4scp"));
		Validate.isTrue(source.exists());
		return source;
	}

	public static boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return breakpoint.getModelIdentifier().equals(IDebugCoreConstants.ID_DEBUG_MODEL);
	}

	public static SCPLineBreakpoint findBreakpoint(final IFile source, int lineNumber) {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(IDebugCoreConstants.ID_DEBUG_MODEL);

		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			if (supportsBreakpoint(breakpoint) && breakpoint.getMarker().getResource().equals(source)) {
				if (breakpoint instanceof SCPLineBreakpoint) {
					SCPLineBreakpoint lineBreakpoint = (SCPLineBreakpoint) breakpoint;
					try {
						if (lineBreakpoint.getLineNumber() == lineNumber)
							return lineBreakpoint;
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return null;
	}

	public static IFile findProgramSourceFromUri(ILaunch launch, String uri) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		String projectName = launch.getLaunchConfiguration().getAttribute(IDebugCoreConstants.ATTR_PROJECT, "");
		IProject project = (IProject) workspace.getRoot().findMember(projectName);
		return getSourceFromUri(project, uri);
	}

	public static IFile findProgramSource(ILaunch launch, SCPProgram program) throws CoreException {
		return findProgramSourceFromUri(launch, program.getSign().getSegment().getURI());
	}
}

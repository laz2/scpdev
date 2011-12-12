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

import net.ostis.scpdev.debug.core.IDebugCoreConstants;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;

/**
 * Processor Module line breakpoint. From code of examples PDA debugger plugin
 * "org.eclipse.debug.examples.core".
 *
 * @author Dmitry Lazurkin
 */
public class SCPLineBreakpoint extends LineBreakpoint {

	public static final String ID_MARKER = "net.ostis.scpdev.debug.core.lineBreakpoint.marker";

	/**
	 * Default constructor is required for the breakpoint manager to re-create persisted
	 * breakpoints. After instantiating a breakpoint, the <code>setMarker(...)</code> method is
	 * called to restore this breakpoint's attributes.
	 */
	public SCPLineBreakpoint() {
	}

	/**
	 * Constructs a line breakpoint on the given resource at the given line number.
	 */
	public SCPLineBreakpoint(final IResource resource, final String programName, final int lineNumber)
			throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker(ID_MARKER);
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.MESSAGE, "SCP Line Breakpoint: " + resource.getName() + " [line: "
						+ lineNumber + "]");
			}
		};
		run(getMarkerRule(resource), runnable);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IBreakpoint#getModelIdentifier()
	 */
	@Override
	public String getModelIdentifier() {
		return IDebugCoreConstants.ID_DEBUG_MODEL;
	}
}

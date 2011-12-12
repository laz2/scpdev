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
package net.ostis.scpdev.debug.ui.model;

import net.ostis.scpdev.debug.core.IDebugCoreConstants;
import net.ostis.scpdev.debug.core.model.M4SCPUtils;
import net.ostis.scpdev.debug.core.model.SCPLineBreakpoint;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Adapter to create breakpoints in SCP source files.
 *
 * @author Dmitry Lazurkin
 */
public class SCPLineBreakpointAdapter implements IToggleBreakpointsTarget {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleLineBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		ITextEditor textEditor = getEditor(part);
		if (textEditor != null) {
			IResource resource = (IResource) textEditor.getEditorInput().getAdapter(IResource.class);
			ITextSelection textSelection = (ITextSelection) selection;
			int lineNumber = textSelection.getStartLine();
			IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
					.getBreakpoints(IDebugCoreConstants.ID_DEBUG_MODEL);
			for (int i = 0; i < breakpoints.length; i++) {
				IBreakpoint breakpoint = breakpoints[i];
				if (resource.equals(breakpoint.getMarker().getResource())) {
					if (((ILineBreakpoint) breakpoint).getLineNumber() == (lineNumber + 1)) {
						// remove
						breakpoint.delete();
						return;
					}
				}
			}
			// if exist m4scp program declaration on that line then
			// create line breakpoint (doc line numbers start at 0)
			String programName = M4SCPUtils.getProgramNameFromLine((IFile) resource, lineNumber + 1);
			if (programName != null) {
				SCPLineBreakpoint lineBreakpoint = new SCPLineBreakpoint(resource, programName, lineNumber + 1);
				DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(lineBreakpoint);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleLineBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public boolean canToggleLineBreakpoints(IWorkbenchPart part, ISelection selection) {
		return getEditor(part) != null;
	}

	/**
	 * Returns the editor being used to edit a PDA file, associated with the given part, or
	 * <code>null</code> if none.
	 *
	 * @param part workbench part
	 * @return the editor being used to edit a PDA file, associated with the given part, or
	 *         <code>null</code> if none
	 */
	private ITextEditor getEditor(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			ITextEditor editorPart = (ITextEditor) part;
			IResource resource = (IResource) editorPart.getEditorInput().getAdapter(IResource.class);
			if (resource != null) {
				String extension = resource.getFileExtension();
				if (extension != null && extension.equals("m4scp")) {
					return editorPart;
				}
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleMethodBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void toggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleMethodBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public boolean canToggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleWatchpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleWatchpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public boolean canToggleWatchpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}
}

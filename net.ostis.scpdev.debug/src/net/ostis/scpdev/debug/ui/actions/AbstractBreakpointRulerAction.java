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
package net.ostis.scpdev.debug.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.ostis.scpdev.debug.core.IDebugCoreConstants;
import net.ostis.scpdev.debug.core.model.SCPLineBreakpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 * Inspired by:
 *
 * @see org.python.pydev.debug.ui.actions.AbstractBreakpointRulerAction
 *
 * @author Dmitry Lazurkin
 */
public abstract class AbstractBreakpointRulerAction extends Action implements IUpdate {

	private static final Log log = LogFactory.getLog(AbstractBreakpointRulerAction.class);

	protected IVerticalRulerInfo fInfo;
	protected ITextEditor fTextEditor;
	private IBreakpoint fBreakpoint;

	protected IBreakpoint getBreakpoint() {
		return fBreakpoint;
	}

	protected void setBreakpoint(IBreakpoint breakpoint) {
		fBreakpoint = breakpoint;
	}

	protected ITextEditor getTextEditor() {
		return fTextEditor;
	}

	protected void setTextEditor(ITextEditor textEditor) {
		fTextEditor = textEditor;
	}

	protected IVerticalRulerInfo getInfo() {
		return fInfo;
	}

	protected void setInfo(IVerticalRulerInfo info) {
		fInfo = info;
	}

	protected IBreakpoint determineBreakpoint() {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(IDebugCoreConstants.ID_DEBUG_MODEL);
		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			if (breakpoint instanceof SCPLineBreakpoint) {
				SCPLineBreakpoint pmBreakpoint = (SCPLineBreakpoint) breakpoint;
				try {
					if (breakpointAtRulerLine(pmBreakpoint)) {
						return pmBreakpoint;
					}
				} catch (CoreException ce) {
					log.error(ce.getLocalizedMessage(), ce);
					continue;
				}
			}
		}
		return null;
	}

	/**
	 * @return the document of the editor's input
	 */
	protected IDocument getDocument() {
		IDocumentProvider provider = fTextEditor.getDocumentProvider();
		return provider.getDocument(fTextEditor.getEditorInput());
	}

	protected boolean breakpointAtRulerLine(SCPLineBreakpoint pyBreakpoint) throws CoreException {
		IDocument doc = getDocument();
		IMarker marker = pyBreakpoint.getMarker();
		Position position = getMarkerPosition(doc, marker);
		if (position != null) {
			try {
				int markerLineNumber = doc.getLineOfOffset(position.getOffset());
				if (getResourceForDebugMarkers() instanceof IFile) {
					// workspace file
					int rulerLine = getInfo().getLineOfLastMouseButtonActivity();
					if (rulerLine == markerLineNumber) {
						if (getTextEditor().isDirty()) {
							return pyBreakpoint.getLineNumber() == markerLineNumber + 1;
						}
						return true;
					}
				}
			} catch (BadLocationException x) {
			}
		}

		return false;
	}

	protected IResource getResourceForDebugMarkers() {
		return getResourceForDebugMarkers(fTextEditor);
	}

	/**
	 * @return the position for a marker.
	 */
	public static Position getMarkerPosition(IDocument document, IMarker marker) {
		int start = MarkerUtilities.getCharStart(marker);
		int end = MarkerUtilities.getCharEnd(marker);

		if (start > end) {
			end = start + end;
			start = end - start;
			end = end - start;
		}

		if (start == -1 && end == -1) {
			// marker line number is 1-based
			int line = MarkerUtilities.getLineNumber(marker);
			if (line > 0 && document != null) {
				try {
					start = document.getLineOffset(line - 1);
					end = start;
				} catch (BadLocationException x) {
				}
			}
		}

		if (start > -1 && end > -1)
			return new Position(start, end - start);

		return null;
	}

	/**
	 * @return the resource for which to create the marker or <code>null</code>
	 *
	 *         If the editor maps to a workspace file, it will return that file. Otherwise, it will
	 *         return the workspace root (so, markers from external files will be created in the
	 *         workspace root).
	 */
	public static IResource getResourceForDebugMarkers(ITextEditor textEditor) {
		IEditorInput input = textEditor.getEditorInput();
		IResource resource = (IResource) input.getAdapter(IFile.class);
		if (resource == null) {
			resource = (IResource) input.getAdapter(IResource.class);
		}
		if (resource == null) {
			resource = ResourcesPlugin.getWorkspace().getRoot();
		}
		return resource;
	}

	/**
	 * Checks whether a position includes the ruler's line of activity.
	 *
	 * @param position the position to be checked
	 * @param document the document the position refers to
	 * @return <code>true</code> if the line is included by the given position
	 */
	public static boolean includesRulerLine(Position position, IDocument document, int lastLineActivity) {
		if (position != null && lastLineActivity >= 0 && document != null) {
			try {
				int markerLine = document.getLineOfOffset(position.getOffset());
				if (lastLineActivity == markerLine) {
					return true;
				}
			} catch (BadLocationException x) {
			}
		}
		return false;
	}

	/**
	 * @param resource may be the file open in the editor or the workspace root (if it is an
	 *            external file)
	 * @param document is the document opened in the editor
	 * @param externalFileEditorInput is not-null if this is an external file
	 * @param info is the vertical ruler info (only used if this is not an external file)
	 * @param onlyIncludeLastLineActivity if only the markers that are in the last mouse-click
	 *            should be included
	 *
	 * @return the markers that correspond to the markers from the current editor.
	 */
	public static List<IMarker> getMarkersFromEditorResource(IResource resource, IDocument document,
			int lastLineActivity, boolean onlyIncludeLastLineActivity) {

		List<IMarker> breakpoints = new ArrayList<IMarker>();

		try {
			List<IMarker> markers = new ArrayList<IMarker>();

			markers.addAll(Arrays.asList(resource.findMarkers(SCPLineBreakpoint.ID_MARKER, true,
					IResource.DEPTH_INFINITE)));

			IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
			for (IMarker marker : markers) {
				if (marker == null) {
					continue;
				}
				IBreakpoint breakpoint = breakpointManager.getBreakpoint(marker);
				if (breakpoint != null && breakpointManager.isRegistered(breakpoint)) {
					Position pos = getMarkerPosition(document, marker);

					if (!onlyIncludeLastLineActivity) {
						breakpoints.add(marker);
					} else if (includesRulerLine(pos, document, lastLineActivity)) {
						breakpoints.add(marker);
					}
				}
			}
		} catch (CoreException x) {
			log.error("Unexpected getMarkers error (recovered properly)", x);
		}

		return breakpoints;
	}

}

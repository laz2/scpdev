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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ostis.scpdev.debug.core.IDebugCoreConstants;
import net.ostis.scpdev.debug.core.model.SCPLineBreakpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;

/**
 * Setting/removing breakpoints in the ruler.
 *
 * Inspired by:
 *
 * @see org.eclipse.jdt.internal.debug.ui.actions.ManageBreakpointRulerAction
 * @see org.python.pydev.debug.ui.actions.BreakpointRulerAction
 *
 * @author Dmitry Lazurkin
 */
public class BreakpointRulerAction extends AbstractBreakpointRulerAction {

	private static final Log log = LogFactory.getLog(BreakpointRulerAction.class);

	private List<IMarker> fMarkers;

	private String fAddLabel;

	private String fRemoveLabel;

	public BreakpointRulerAction(ITextEditor editor, IVerticalRulerInfo ruler) {
		setInfo(ruler);
		setTextEditor(editor);
		setText("Breakpoint &Properties...");
		fAddLabel = "Add Breakpoint";
		fRemoveLabel = "Remove Breakpoint";
	}

	/**
	 * @see IUpdate#update()
	 */
	@Override
	public void update() {
		fMarkers = getMarkersFromCurrentFile();
		setText(fMarkers.isEmpty() ? fAddLabel : fRemoveLabel);
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		if (fMarkers.isEmpty()) {
			addMarker();
		} else {
			removeMarkers(fMarkers);
		}
	}

	public static List<IMarker> getMarkersFromCurrentFile(ITextEditor edit, int line) {
		return getMarkersFromEditorResource(getResourceForDebugMarkers(edit),
				edit.getDocumentProvider().getDocument(null), line, true);
	}

	protected List<IMarker> getMarkersFromCurrentFile() {
		return getMarkersFromEditorResource(getResourceForDebugMarkers(), getDocument(), getInfo()
				.getLineOfLastMouseButtonActivity(), true);
	}

	/**
	 * This is the function that actually adds the marker to the Eclipse structure.
	 */
	protected void addMarker() {
		IDocument document = getDocument();
		int rulerLine = getInfo().getLineOfLastMouseButtonActivity();
		addBreakpointMarker(document, rulerLine + 1, fTextEditor);
	}

	public static void addBreakpointMarker(IDocument document, int lineNumber, ITextEditor textEditor) {
		try {
			if (lineNumber < 0)
				return;

			// just to validate it
			try {
				document.getLineInformation(lineNumber - 1);
			} catch (Exception e) {
				return; // ignore
			}
			final IResource resource = getResourceForDebugMarkers(textEditor);

			// The map containing the marker attributes
			final Map<String, Object> map = new HashMap<String, Object>();

			map.put(IMarker.MESSAGE, "PM breakpoint");
			map.put(IMarker.LINE_NUMBER, new Integer(lineNumber));
			map.put(IBreakpoint.ENABLED, new Boolean(true));
			map.put(IBreakpoint.ID, IDebugCoreConstants.ID_DEBUG_MODEL);

			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					IMarker marker = resource.createMarker(SCPLineBreakpoint.ID_MARKER);
					marker.setAttributes(map);
					SCPLineBreakpoint br = new SCPLineBreakpoint();
					br.setMarker(marker);
					IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
					breakpointManager.addBreakpoint(br);
				}
			};

			resource.getWorkspace().run(runnable, null);
		} catch (Exception e) {
			log.error("Unexpected exception", e);
		}
	}

	/**
	 * @param markers the markers that will be removed in this function (they may be in any editor,
	 *            not only in the current one)
	 */
	public static void removeMarkers(List<IMarker> markers) {
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		try {
			Iterator<IMarker> e = markers.iterator();
			while (e.hasNext()) {
				IBreakpoint breakpoint = breakpointManager.getBreakpoint((IMarker) e.next());
				breakpointManager.removeBreakpoint(breakpoint, true);
			}
		} catch (CoreException e) {
			log.error("Error removing markers", e);
		}
	}

}

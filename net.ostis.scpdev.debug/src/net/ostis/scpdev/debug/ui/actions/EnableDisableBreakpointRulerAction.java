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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Action to enable/disable a breakpoint in the ruler.
 *
 * @author Fabio
 * @author Dmitry Lazurkin
 */
public class EnableDisableBreakpointRulerAction extends AbstractBreakpointRulerAction {

	private static final Log log = LogFactory.getLog(EnableDisableBreakpointRulerAction.class);

	public EnableDisableBreakpointRulerAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
		fInfo = rulerInfo;
		fTextEditor = editor;
	}

	@Override
	public void update() {
		setBreakpoint(determineBreakpoint());
		if (getBreakpoint() == null) {
			setEnabled(false);
			return;
		}
		setEnabled(true);
		try {
			boolean enabled = getBreakpoint().isEnabled();
			setText(enabled ? "&Disable Breakpoint" : "&Enable Breakpoint");
		} catch (CoreException ce) {
			log.error(ce.getLocalizedMessage(), ce);
		}
	}

	@Override
	public void run() {

		if (getBreakpoint() != null) {
			new Job("Enabling / Disabling Breakpoint") { //$NON-NLS-1$
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						getBreakpoint().setEnabled(!getBreakpoint().isEnabled());
						return Status.OK_STATUS;
					} catch (final CoreException e) {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								ErrorDialog.openError(getTextEditor().getEditorSite().getShell(),
										"Enabling/disabling breakpoints",
										"Exceptions occurred enabling disabling the breakpoint", e.getStatus());
							}
						});
					}
					return Status.CANCEL_STATUS;
				}
			}.schedule();
		}
	}
}

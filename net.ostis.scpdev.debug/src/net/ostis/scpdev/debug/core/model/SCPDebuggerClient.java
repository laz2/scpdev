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

import net.ostis.sc.memory.SCActivity;
import net.ostis.sc.memory.SCAddr;
import net.ostis.sc.memory.SCSegment;
import net.ostis.sc.memory.SCSession;
import net.ostis.sc.memory.SCContentInMemory;
import net.ostis.sc.memory.SCKeynodes;
import net.ostis.sc.memory.SCType;
import net.ostis.sc.memory.TupUtils;

import org.apache.commons.lang3.Validate;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Class for interaction with scp-debugger.
 *
 * @author Dmitry Lazurkin
 */
public class SCPDebuggerClient {

	private SCSession session;

	private SCSegment workingSeg;
	private SCAddr sign;

	private boolean closed = false;

	private IEventListener listener = null;

	public interface IEventListener {

		public void breakpointChangedEvent(SCAddr program, int lineNumber, int newLineNumber);

		public void suspenedEvent(SCAddr process);

		public void resumedEvent(SCAddr process);

		public void stepOverEvent(SCAddr process);

		public void stepIntoEvent(SCAddr process);

		public void stepReturnEvent(SCAddr process);

		public void stepEndEvent(SCAddr process, int lineNumber);

		public void breakpointEvent(SCAddr process, int lineNumber);

		public void terminatedEvent(SCAddr process);

		public void childEvent(SCAddr father, SCAddr child);

		public void callEvent(SCAddr process);

		public void returnEvent(SCAddr process);

	}

	public SCPDebuggerClient(SCSession session) {
		SCPDebuggerKeynodes.init(session);
		this.session = session;

		workingSeg = session.createSegment("/proc/scpdbg_client");
		sign = session.createElement(workingSeg, SCType.NODE_CONST);
		session.setIdtf(sign, "scpdbg_client");

		reciever = new EventsReciever();
		session.reimplement(sign, reciever);

		register();
	}

	public void setListener(IEventListener listener) {
		this.listener = listener;
	}

	public void close() {
		if (closed)
			throw new RuntimeException("Session with scp-debugger already closed");
		unregister();
		session.reimplement(sign, null);
		session.eraseElement(sign);
	}

	public void setBreakpoint(SCAddr program, int line) {
		SCAddr nodeWithLine = session.createElement(workingSeg, SCType.NODE_CONST);
		session.setContent(nodeWithLine, new SCContentInMemory(line));
		try {
			session.activate(SCPDebuggerKeynodes.Debugger, SCPDebuggerKeynodes.SetBreakpointCommand, program,
					nodeWithLine);
		} finally {
			session.eraseElement(nodeWithLine);
		}
	}

	public void resume(final SCAddr process) {
		Job job = new Job("Resume " + ((process == null) ? "PM" : process)) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				session.activate(SCPDebuggerKeynodes.Debugger, SCPDebuggerKeynodes.ResumeCommand, process, null);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public void suspend(final SCAddr process) {
		Job job = new Job("Suspend " + ((process == null) ? "PM" : process)) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				session.activate(SCPDebuggerKeynodes.Debugger, SCPDebuggerKeynodes.SuspendCommand, process, null);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public void terminate(final SCAddr process) {
		Job job = new Job("Terminate " + ((process == null) ? "PM" : process)) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				session.activate(SCPDebuggerKeynodes.Debugger, SCPDebuggerKeynodes.TerminateCommand, process, null);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public void stepOver(final SCAddr process) {
		Job job = new Job("Step over " + process) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				session.activate(SCPDebuggerKeynodes.Debugger, SCPDebuggerKeynodes.StepOverCommand, process, null);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public void stepInto(final SCAddr process) {
		Job job = new Job("Step into " + process) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				session.activate(SCPDebuggerKeynodes.Debugger, SCPDebuggerKeynodes.StepIntoCommand, process, null);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public void stepReturn(final SCAddr process) {
		Job job = new Job("Step return " + process) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				session.activate(SCPDebuggerKeynodes.Debugger, SCPDebuggerKeynodes.StepReturnCommand, process, null);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private class EventsReciever implements SCActivity {

		@Override
		public boolean activate(SCSession session, SCAddr this_, SCAddr... params) {
			Validate.isTrue(params.length > 0);
			Validate.notNull(params[0]);

			if (params[0].equals(SCPDebuggerKeynodes.BreapointChangedEvent)) {
				SCAddr program = params[1];
				SCAddr lineNumberEl = TupUtils.get(session, params[2], SCKeynodes.n1_);
				SCAddr newLineNumberEl = TupUtils.get(session, params[2], SCKeynodes.n2_);

				int lineNumber = session.getContent(lineNumberEl).getInteger();
				int newLineNumber = session.getContent(newLineNumberEl).getInteger();

				if (listener != null)
					listener.breakpointChangedEvent(program, lineNumber, newLineNumber);
			} else if (params[0].equals(SCPDebuggerKeynodes.SuspenedEvent)) {
				if (listener != null)
					listener.suspenedEvent(params[1]);
			} else if (params[0].equals(SCPDebuggerKeynodes.ResumedEvent)) {
				if (listener != null)
					listener.resumedEvent(params[1]);
			} else if (params[0].equals(SCPDebuggerKeynodes.StepOverEvent)) {
				if (listener != null)
					listener.stepOverEvent(params[1]);
			} else if (params[0].equals(SCPDebuggerKeynodes.StepIntoEvent)) {
				if (listener != null)
					listener.stepIntoEvent(params[1]);
			} else if (params[0].equals(SCPDebuggerKeynodes.StepReturnEvent)) {
				if (listener != null)
					listener.stepReturnEvent(params[1]);
			} else if (params[0].equals(SCPDebuggerKeynodes.BreapointEvent)) {
				SCAddr process = params[1];
				SCAddr lineNumberEl = params[2];

				int lineNumber = session.getContent(lineNumberEl).getInteger();

				if (listener != null)
					listener.breakpointEvent(process, lineNumber);
			} else if (params[0].equals(SCPDebuggerKeynodes.StepEndEvent)) {
				SCAddr process = params[1];
				SCAddr lineNumberEl = params[2];

				int lineNumber = session.getContent(lineNumberEl).getInteger();

				if (listener != null)
					listener.stepEndEvent(process, lineNumber);
			} else if (params[0].equals(SCPDebuggerKeynodes.TerminatedEvent)) {
				SCAddr process = params[1];
				if (listener != null)
					listener.terminatedEvent(process);
			} else if (params[0].equals(SCPDebuggerKeynodes.ChildEvent)) {
				SCAddr father = params[1];
				SCAddr child = params[2];
				if (listener != null)
					listener.childEvent(father, child);
			} else if (params[0].equals(SCPDebuggerKeynodes.CallEvent)) {
				if (listener != null)
					listener.callEvent(params[1]);
			} else if (params[0].equals(SCPDebuggerKeynodes.ReturnEvent)) {
				if (listener != null)
					listener.returnEvent(params[1]);
			} else {
				throw new RuntimeException("Unknown event from scp-debugger: " + session.getIdtf(params[0]));
			}

			return false;
		}
	}

	private EventsReciever reciever;

	private void register() {
		session.activate(SCPDebuggerKeynodes.Debugger, SCPDebuggerKeynodes.RegisterCommand, sign, null);
	}

	private void unregister() {
		session.activate(SCPDebuggerKeynodes.Debugger, SCPDebuggerKeynodes.UnregisterCommand, sign, null);
	}

}

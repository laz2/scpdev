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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import net.ostis.sc.memory.SCAddr;
import net.ostis.sc.memory.SCKeynodes;
import net.ostis.sc.memory.SCPKeynodes;
import net.ostis.sc.memory.SCSession;
import net.ostis.sc.memory.TupUtils;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

/**
 * A Processor Module thread. From code of examples PDA debugger plugin
 * "org.eclipse.debug.examples.core".
 *
 * @author Dmitry Lazurkin
 */
public class SCPProcess extends PMDebugElement implements IThread {

	private static final Log log = LogFactory.getLog(SCPProcess.class);

	private SCAddr sign;
	private String name;

	private SCPStackFrame topFrame = null;
	private LinkedList<SCPStackFrame> stack = new LinkedList<SCPStackFrame>();
	private SCPStackFrame[] stackAsArray = null;

	private boolean suspended;

	private boolean stepping_over;
	private boolean stepping_into;
	private boolean stepping_return;

	private boolean terminated;

	private IBreakpoint[] breakpoints = null;

	// ===================================================================================
	// START Cache of scp-processes.
	// ===================================================================================

	private static Map<SCAddr, SCPProcess> cache = new TreeMap<SCAddr, SCPProcess>();

	/**
	 * Cache supplied scp-process.
	 */
	public static void putCache(SCPProcess process) {
		synchronized (cache) {
			if (!cache.containsKey(process.getSign()))
				cache.put(process.getSign(), process);
		}
	}

	/**
	 * Return from cached scp-process, which's sign equals to supplied.
	 * If no cache scp-process for supplied sign then return null.
	 */
	public static SCPProcess getCache(SCAddr sign) {
		synchronized (cache) {
			return cache.get(sign);
		}
	}

	/**
	 * Remove from cache scp-process with supplied sign.
	 */
	public static void removeCache(SCAddr sign) {
		synchronized (cache) {
			cache.remove(sign);
		}
	}

	/**
	 * @return collection of all cached scp-processes.
	 */
	public static Collection<SCPProcess> getAllCached() {
		synchronized (cache) {
			return cache.values();
		}
	}

	/**
	 * Remove all cached scp-processes.
	 */
	public static void clearCache() {
		synchronized (cache) {
			cache.clear();
		}
	}

	// ===================================================================================
	// END Cache of scp-processes.
	// ===================================================================================

	/**
	 * Construct a new scp-process for the given target and cache it.
	 * Fetch all scp-process info from sc-memory.
	 */
	public SCPProcess(PMDebugTarget target, SCAddr sign) {
		super(target);
		this.sign = sign;
		suspended = false;
		terminated = false;
		stepping_over = stepping_into = stepping_return = false;
		fetch();
		putCache(this);
	}

	/**
	 * Notification the process has terminated.
	 */
	public void terminatedEvent() {
		terminated = true;
		suspended = false;
		stepping_over = stepping_into = stepping_return = false;
		breakpoints = null;
		removeCache(sign);
		fireTerminateEvent();
	}

	/**
	 * Notification the process has resumed for the given reason.
	 *
	 * @param detail reason for the resume
	 */
	public void resumedEvent(int detail) {
		suspended = false;
		breakpoints = null;

		if (detail == DebugEvent.CLIENT_REQUEST) {
			//
			// Invalidate stack.
			//
			stack.clear();
			topFrame = null;
			refreshStackAsArray();
		}

		fireResumeEvent(detail);
	}

	/**
	 * Notification the process has suspended for the given reason.
	 *
	 * @param detail reason for the suspend
	 */
	public void suspendedEvent(int detail) {
		suspended = true;
		fireSuspendEvent(detail);
	}

	public void stepOverEvent() {
		stepping_over = true;
		resumedEvent(DebugEvent.STEP_OVER);
	}

	public void stepIntoEvent() {
		stepping_into = true;
		resumedEvent(DebugEvent.STEP_INTO);
	}

	public void stepReturnEvent() {
		stepping_return = true;
		resumedEvent(DebugEvent.STEP_RETURN);
	}

	public void stepEndEvent() {
		stepping_over = stepping_into = stepping_return = false;
		fetchStack();
		suspendedEvent(DebugEvent.STEP_END);
	}

	public void callEvent() {
		//
		// Fetch new created stack frame.
		//
		SCPStackFrame oldTopFrame = topFrame;
		SCAddr oldTopFrameNewSign = TupUtils.get(getSession(), sign, SCPKeynodes.processStack_);
		oldTopFrame.setSign(oldTopFrameNewSign);

		topFrame = new SCPStackFrame(this, sign);
		stack.addFirst(topFrame);

		refreshStackAsArray();
	}

	public void returnEvent() {
		//
		// Pop top stack frame.
		//
		stack.pollFirst();
		topFrame = stack.peekFirst();
		topFrame.setSign(sign);
		refreshStackAsArray();
	}

	/**
	 * Notification the process has suspended by breakpoint.
	 */
	public void breakpointEvent(int line) {
		fetchStack();

		try {
			IFile source = M4SCPUtils.findProgramSource(getLaunch(), getCurrentProgram());
			SCPLineBreakpoint breakpoint = M4SCPUtils.findBreakpoint(source, line);
			breakpoints = new IBreakpoint[1];
			breakpoints[0] = breakpoint;
			suspendedEvent(DebugEvent.BREAKPOINT);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void fetch() {
		name = getSession().getIdtf(sign);
		fetchStack();
	}

	private void fetchStack() {
		if (topFrame == null) {
			SCSession session = getSession();

			// First stack frames sign for scp-process is scp-process sign.
			topFrame = new SCPStackFrame(this, sign);
			stack.add(topFrame);

			//
			// Read rest of process stack.
			//
			SCAddr listElement = TupUtils.get(session, sign, SCPKeynodes.processStack_);
			while (listElement != null) {
				SCAddr frameSign = TupUtils.get(session, listElement, SCKeynodes.list_value_);

				Validate.notNull(frameSign);

				stack.add(new SCPStackFrame(this, frameSign));
				listElement = TupUtils.get(session, listElement, SCKeynodes.list_next_);
			}

			refreshStackAsArray();

			if (log.isDebugEnabled()) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("Fetched " + toString());
				buffer.append("\n\tStack trace:\n");
				for (int i = 0; i < stackAsArray.length; ++i) {
					buffer.append("\t\t");
					buffer.append(stackAsArray[i]);
					if (i != stackAsArray.length - 1)
						buffer.append("\n");
				}
				log.debug(buffer.toString());
			}
		} else {
			topFrame.update();
		}
	}

	private void refreshStackAsArray() {
		stackAsArray = (SCPStackFrame[]) stack.toArray(new SCPStackFrame[stack.size()]);
	}

	public SCAddr getSign() {
		return sign;
	}

	public SCPProgram getCurrentProgram() {
		return topFrame.getProgram();
	}

	// ===================================================================================
	// START org.eclipse.debug.core.model.IThread
	// ===================================================================================

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getStackFrames()
	 */
	@Override
	public IStackFrame[] getStackFrames() throws DebugException {
		if (isSuspended()) {
			return stackAsArray;
		} else {
			return new IStackFrame[0];
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#hasStackFrames()
	 */
	@Override
	public boolean hasStackFrames() throws DebugException {
		return isSuspended() && topFrame != null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getPriority()
	 */
	@Override
	public int getPriority() throws DebugException {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getTopStackFrame()
	 */
	@Override
	public IStackFrame getTopStackFrame() throws DebugException {
		return topFrame;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getName()
	 */
	@Override
	public String getName() throws DebugException {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getBreakpoints()
	 */
	@Override
	public IBreakpoint[] getBreakpoints() {
		if (breakpoints == null) {
			return new IBreakpoint[0];
		} else {
			return breakpoints;
		}
	}

	// ===================================================================================
	// END org.eclipse.debug.core.model.IThread
	// ===================================================================================

	// ===================================================================================
	// START org.eclipse.debug.core.model.ISuspendResume
	// ===================================================================================

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	@Override
	public boolean canResume() {
		return isSuspended();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	@Override
	public boolean canSuspend() {
		return !isSuspended();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	@Override
	public boolean isSuspended() {
		return suspended;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	@Override
	public void resume() throws DebugException {
		suspended = false;
		getDebuggerClient().resume(sign);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	@Override
	public void suspend() throws DebugException {
		suspended = true;
		getDebuggerClient().suspend(sign);
	}

	// ===================================================================================
	// END org.eclipse.debug.core.model.ISuspendResume
	// ===================================================================================

	// ===================================================================================
	// START org.eclipse.debug.core.model.IStep
	// ===================================================================================

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	@Override
	public boolean canStepInto() {
		return isSuspended() && topFrame != null && topFrame.canStepInto();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	@Override
	public boolean canStepOver() {
		return isSuspended();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	@Override
	public boolean canStepReturn() {
		return isSuspended() && stackAsArray!= null && stackAsArray.length > 1;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	@Override
	public boolean isStepping() {
		return stepping_over || stepping_into || stepping_return;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	@Override
	public void stepInto() throws DebugException {
		getDebuggerClient().stepInto(sign);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	@Override
	public void stepOver() throws DebugException {
		getDebuggerClient().stepOver(sign);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	@Override
	public void stepReturn() throws DebugException {
		getDebuggerClient().stepReturn(sign);
	}

	// ===================================================================================
	// END org.eclipse.debug.core.model.IStep
	// ===================================================================================

	// ===================================================================================
	// START org.eclipse.debug.core.model.ITerminate
	// ===================================================================================

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	@Override
	public boolean canTerminate() {
		return !isTerminated();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	@Override
	public boolean isTerminated() {
		return terminated;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	@Override
	public void terminate() throws DebugException {
		getDebuggerClient().terminate(sign);
	}

	// ===================================================================================
	// END org.eclipse.debug.core.model.ITerminate
	// ===================================================================================

	@Override
	public String toString() {
		return MessageFormat.format("< SCP-PROCESS {0} >", name);
	}
}

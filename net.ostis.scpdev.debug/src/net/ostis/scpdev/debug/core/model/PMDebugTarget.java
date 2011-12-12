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

import java.net.URI;
import java.text.MessageFormat;
import java.util.Collection;

import net.ostis.sc.memory.SCAddr;
import net.ostis.sc.memory.SCConstraintBase;
import net.ostis.sc.memory.SCIterator;
import net.ostis.sc.memory.SCKeynodes;
import net.ostis.sc.memory.SCMemory;
import net.ostis.sc.memory.SCMemoryHelper;
import net.ostis.sc.memory.SCPKeynodes;
import net.ostis.sc.memory.SCSession;
import net.ostis.sc.memory.SCType;
import net.ostis.sc.memory.STDConstraints;
import net.ostis.sc.memory.URIUtils;
import net.ostis.scpdev.debug.core.IDebugCoreConstants;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;

/**
 * Processor Module Debug Target. From code of examples PDA debugger plugin
 * "org.eclipse.debug.examples.core".
 *
 * @author Dmitry Lazurkin
 */
public class PMDebugTarget extends PMDebugElement implements IDebugTarget, SCPDebuggerClient.IEventListener {

	private static final Log log = LogFactory.getLog(PMDebugTarget.class);

	/** associated PM process */
	private IProcess process;

	// containing launch object
	private ILaunch launch;

	private SCMemory memory;
	private SCSession session;

	private boolean started = false;

	private boolean suspended = false;
	private boolean terminated = false;

	// threads
	private IThread[] threads = null;

	private SCPDebuggerClient debuggerClient;

	/**
	 * Constructs a new debug target in the given launch for the associated Processor Module.
	 *
	 * @param launch containing launch
	 * @param process Processor Module
	 * @param requestPort port to send requests to the VM
	 * @param eventPort port to read events from
	 * @exception CoreException if unable to connect to host
	 */
	public PMDebugTarget(ILaunch launch, IProcess process) throws CoreException {
		super(null);

		this.launch = launch;
		this.target = this;
		this.process = process;

		try {
			memory = SCMemoryHelper.createMemory(new URI("rgp://localhost:47805"));
			session = memory.login();

			SCKeynodes.init(session);
			SCPKeynodes.init(session);

			debuggerClient = new SCPDebuggerClient(session);
			debuggerClient.setListener(this);
		} catch (Exception e) {
			abort("Exception", e);
		}

		initialFetchProcesses();

		installDeferredBreakpoints();

		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);

		debuggerClient.resume(null);
	}

	// ===================================================================================
	// START net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener
	// ===================================================================================

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener#breakpointChanged(net.ostis.sc.memory.IScAddr, int, int)
	 */
	@Override
	public void breakpointChangedEvent(SCAddr programSign, int lineNumber, int newLineNumber) {
		SCPProgram program = SCPProgram.getCacheOrNew(session, programSign);

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("--> BreakpointChangedEvent ( {0}, {1}, {2} );", program, lineNumber,
					newLineNumber));
		}

		try {
			IFile source = M4SCPUtils.findProgramSource(launch, program);
			SCPLineBreakpoint breakpoint = M4SCPUtils.findBreakpoint(source, lineNumber);
			breakpoint.getMarker().setAttribute(IMarker.LINE_NUMBER, newLineNumber);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener#suspenedEvent(net.ostis.sc.memory.IScAddr)
	 */
	@Override
	public void suspenedEvent(SCAddr processSign) {
		SCPProcess process = null;

		if (processSign != null) {
			process = SCPProcess.getCache(processSign);
			Validate.notNull(process);
		}

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("--> SuspenedEvent ( {0} );", process));
		}

		if (process == null) {
			//
			// Suspened whole debug target.
			//
			for (SCPProcess p : SCPProcess.getAllCached())
				p.suspendedEvent(DebugEvent.CLIENT_REQUEST);
			suspended(DebugEvent.CLIENT_REQUEST);
		} else {
			process.suspendedEvent(DebugEvent.CLIENT_REQUEST);
		}
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener#resumedEvent(net.ostis.sc.memory.IScAddr)
	 */
	@Override
	public void resumedEvent(SCAddr processSign) {
		SCPProcess process = null;

		if (processSign != null) {
			process = SCPProcess.getCache(processSign);
			Validate.notNull(process);
		}

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("--> ResumedEvent ( {0} );", process));
		}

		if (process == null) {
			//
			// Resumed whole debug target.
			//
			resumed(DebugEvent.CLIENT_REQUEST);
			for (SCPProcess p : SCPProcess.getAllCached())
				p.resumedEvent(DebugEvent.CLIENT_REQUEST);
		} else {
			process.resumedEvent(DebugEvent.CLIENT_REQUEST);
		}
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener#breakpointHit(net.ostis.sc.memory.IScAddr, int)
	 */
	@Override
	public void breakpointEvent(SCAddr processSign, int lineNumber) {
		SCPProcess process = SCPProcess.getCache(processSign);
		Validate.notNull(process);

		if (log.isDebugEnabled())
			log.debug(MessageFormat.format("--> BreapointEvent ( {0}, {1} );", process, lineNumber));

		process.breakpointEvent(lineNumber);
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener#steppingEvent(net.ostis.sc.memory.IScAddr)
	 */
	@Override
	public void stepOverEvent(SCAddr processSign) {
		SCPProcess process = SCPProcess.getCache(processSign);
		Validate.notNull(process);

		if (log.isDebugEnabled())
			log.debug(MessageFormat.format("--> StepOverEvent ( {0} );", process));

		process.stepOverEvent();
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener#steppingEvent(net.ostis.sc.memory.IScAddr)
	 */
	@Override
	public void stepIntoEvent(SCAddr processSign) {
		SCPProcess process = SCPProcess.getCache(processSign);
		Validate.notNull(process);

		if (log.isDebugEnabled())
			log.debug(MessageFormat.format("--> StepIntoEvent ( {0} );", process));

		process.stepIntoEvent();
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener#steppingEvent(net.ostis.sc.memory.IScAddr)
	 */
	@Override
	public void stepReturnEvent(SCAddr processSign) {
		SCPProcess process = SCPProcess.getCache(processSign);
		Validate.notNull(process);

		if (log.isDebugEnabled())
			log.debug(MessageFormat.format("--> StepReturnEvent ( {0} );", process));

		process.stepReturnEvent();
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener#step(net.ostis.sc.memory.IScAddr, int)
	 */
	@Override
	public void stepEndEvent(SCAddr processSign, int lineNumber) {
		SCPProcess process = SCPProcess.getCache(processSign);
		Validate.notNull(process);

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("--> StepEndEvent ( {0}, {1} );", process, lineNumber));
		}

		process.stepEndEvent();
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener#death(net.ostis.sc.memory.IScAddr)
	 */
	@Override
	public void terminatedEvent(SCAddr processSign) {
		SCPProcess process = null;

		if (processSign != null) {
			process = SCPProcess.getCache(processSign);
			Validate.notNull(process);
		}

		if (log.isDebugEnabled())
			log.debug(MessageFormat.format("--> DeathEvent ( {0} );", process));

		if (process != null) {
			process.terminatedEvent();
		} else {
			for (SCPProcess p : SCPProcess.getAllCached())
				p.terminatedEvent();
			terminated();
		}
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener#child(net.ostis.sc.memory.IScAddr, net.ostis.sc.memory.IScAddr)
	 */
	@Override
	public void childEvent(SCAddr fatherSign, SCAddr childSign) {
		SCPProcess father = SCPProcess.getCache(fatherSign);
		Validate.notNull(father);

		SCPProcess child = new SCPProcess(this, childSign);

		if (log.isDebugEnabled())
			log.debug(MessageFormat.format("--> ChildEvent ( {0}, {1} );", father, child));

		refreshThreadsArray();
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener#callEvent(net.ostis.sc.memory.IScAddr)
	 */
	@Override
	public void callEvent(SCAddr processSign) {
		SCPProcess process = SCPProcess.getCache(processSign);
		Validate.notNull(process);

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("--> CallEvent ( {0} );", process));
		}

		process.callEvent();
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener#returnEvent(net.ostis.sc.memory.IScAddr)
	 */
	@Override
	public void returnEvent(SCAddr processSign) {
		SCPProcess process = SCPProcess.getCache(processSign);
		Validate.notNull(process);

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("--> ReturnEvent ( {0} );", process));
		}

		process.returnEvent();
	}

	// ===================================================================================
	// END net.ostis.scpdev.debug.core.model.ScpDebuggerClient.IEventListener
	// ===================================================================================

	@Override
	protected SCSession getSession() {
		return session;
	}

	@Override
	protected SCPDebuggerClient getDebuggerClient() {
		return debuggerClient;
	}

	private void initialFetchProcesses() {
		SCIterator it = session.createIterator(new SCConstraintBase(STDConstraints.CONSTR_3_f_a_a,
				SCPKeynodes.processSCP, SCType.ARC_CONST_POS, SCType.NODE_CONST));
		try {
			for (; !it.isOver(); it.next())
				new SCPProcess(this, it.getValue(2));
		} finally {
			it.erase();
		}

		refreshThreadsArray();
	}

	private void refreshThreadsArray() {
		Collection<SCPProcess> values = SCPProcess.getAllCached();
		threads = (IThread[]) values.toArray(new IThread[values.size()]);
	}

	// ===================================================================================
	// START org.eclipse.debug.core.model.IDebugTarget
	// ===================================================================================

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getProcess()
	 */
	@Override
	public IProcess getProcess() {
		return process;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getThreads()
	 */
	@Override
	public IThread[] getThreads() throws DebugException {
		return threads;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#hasThreads()
	 */
	@Override
	public boolean hasThreads() throws DebugException {
		return threads.length != 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getName()
	 */
	@Override
	public String getName() throws DebugException {
		return "SC Processor Module";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#supportsBreakpoint(org.eclipse.debug.core.model.IBreakpoint)
	 */
	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			String projectName;
			projectName = launch.getLaunchConfiguration().getAttribute(IDebugCoreConstants.ATTR_PROJECT, "");
			IProject project = (IProject) workspace.getRoot().findMember(projectName);
			return M4SCPUtils.supportsBreakpoint(breakpoint) && breakpoint.getMarker().getResource().getProject().equals(project);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	// ===================================================================================
	// END org.eclipse.debug.core.model.IDebugTarget
	// ===================================================================================

	// ===================================================================================
	// START org.eclipse.debug.core.model.IDebugElement
	// ===================================================================================

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	@Override
	public IDebugTarget getDebugTarget() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	// ===================================================================================
	// END org.eclipse.debug.core.model.IDebugElement
	// ===================================================================================

	// ===================================================================================
	// START org.eclipse.debug.core.model.ITerminate
	// ===================================================================================

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	@Override
	public boolean canTerminate() {
		return getProcess().canTerminate();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	@Override
	public boolean isTerminated() {
		return terminated && getProcess().isTerminated();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	@Override
	public void terminate() throws DebugException {
		getDebuggerClient().terminate(null);
	}

	// ===================================================================================
	// END org.eclipse.debug.core.model.ITerminate
	// ===================================================================================

	// ===================================================================================
	// START org.eclipse.debug.core.model.ISuspendResume
	// ===================================================================================

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	@Override
	public boolean canResume() {
		return !isTerminated() && isSuspended();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	@Override
	public boolean canSuspend() {
		return !isTerminated() && !isSuspended();
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
		debuggerClient.resume(null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	@Override
	public void suspend() throws DebugException {
		debuggerClient.suspend(null);
	}

	// ===================================================================================
	// END org.eclipse.debug.core.model.ISuspendResume
	// ===================================================================================

	/**
	 * Notification the target has resumed for the given reason.
	 *
	 * @param detail reason for the resume
	 */
	private void resumed(int detail) {
		suspended = false;
		if (started) {
			fireResumeEvent(detail);
		} else {
			started = true;
			fireCreationEvent();
		}
	}

	/**
	 * Notification the target has suspended for the given reason.
	 *
	 * @param detail reason for the suspend
	 */
	private void suspended(int detail) {
		suspended = true;
		fireSuspendEvent(detail);
	}

	/**
	 * Notification the target has terminated.
	 */
	private void terminated() {
		terminated = true;
		suspended = false;
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);

		SCPProcess.clearCache();
		SCPProgram.clearCache();

		try {
			memory.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fireTerminateEvent();
	}

	// ===================================================================================
	// START org.eclipse.debug.core.IBreakpointListener
	// ===================================================================================

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointAdded(org.eclipse.debug.core.model.IBreakpoint)
	 */
	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
		if (supportsBreakpoint(breakpoint)) {
			try {
				if (breakpoint.isEnabled()) {
					IFile programFile = (IFile) breakpoint.getMarker().getResource();
					int lineNumber = ((ILineBreakpoint) breakpoint).getLineNumber();

					String programUri = M4SCPUtils.getProgramUriFromLine(programFile, lineNumber);
					SCAddr programSign = URIUtils.findElementByURI(session, programUri);
					debuggerClient.setBreakpoint(programSign, lineNumber);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointRemoved(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint)) {
			// TODO: implement
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointChanged(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint)) {
			// try {
			// if (breakpoint.isEnabled()) {
			// breakpointAdded(breakpoint);
			// } else {
			// breakpointRemoved(breakpoint, null);
			// }
			// } catch (CoreException e) {
			// }
			// TODO: implement
		}
	}

	// ===================================================================================
	// END org.eclipse.debug.core.IBreakpointListener
	// ===================================================================================

	// ===================================================================================
	// START org.eclipse.debug.core.model.IDisconnect
	// ===================================================================================

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#canDisconnect()
	 */
	@Override
	public boolean canDisconnect() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
	 */
	@Override
	public void disconnect() throws DebugException {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#isDisconnected()
	 */
	@Override
	public boolean isDisconnected() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#supportsStorageRetrieval()
	 */
	@Override
	public boolean supportsStorageRetrieval() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#getMemoryBlock(long, long)
	 */
	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		return null;
	}

	// ===================================================================================
	// END org.eclipse.debug.core.model.IDisconnect
	// ===================================================================================

	/**
	 * Install breakpoints that are already registered with the breakpoint manager.
	 */
	private void installDeferredBreakpoints() {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(IDebugCoreConstants.ID_DEBUG_MODEL);
		for (int i = 0; i < breakpoints.length; i++)
			breakpointAdded(breakpoints[i]);
	}

}

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

import net.ostis.sc.memory.SCAddr;
import net.ostis.sc.memory.SCKeynodesBase;
import net.ostis.sc.memory.SCKeynodesBase.DefaultSegment;
import net.ostis.sc.memory.SCSegment;
import net.ostis.sc.memory.SCSession;

/**
 * Keynodes of scp-debugger.
 *
 * @author Dmitry Lazurkin
 */
@DefaultSegment("/proc/scp_debugger/keynode")
public class SCPDebuggerKeynodes extends SCKeynodesBase {

	public static void init(SCSession session) {
		SCKeynodesBase.init(session, SCPDebuggerKeynodes.class);
	}

	public static SCSegment defaultSegment;

	/** Debugger sign. */
	@Keynode
	public static SCAddr Debugger;

	//
	// Commands from reciever to debugger
	//

	@Keynode
	public static SCAddr RegisterCommand;

	@Keynode
	public static SCAddr SetBreakpointCommand;

	@Keynode
	public static SCAddr UnsetBreapointCommand;

	@Keynode
	public static SCAddr ResumeCommand;

	@Keynode
	public static SCAddr SuspendCommand;

	@Keynode
	public static SCAddr TerminateCommand;

	@Keynode
	public static SCAddr StepOverCommand;

	@Keynode
	public static SCAddr StepIntoCommand;

	@Keynode
	public static SCAddr StepReturnCommand;

	@Keynode
	public static SCAddr UnregisterCommand;

	//
	// Events from debugger to reciever
	//

	@Keynode
	public static SCAddr BreapointChangedEvent;

	@Keynode
	public static SCAddr SuspenedEvent;

	@Keynode
	public static SCAddr ResumedEvent;

	@Keynode
	public static SCAddr StepOverEvent;

	@Keynode
	public static SCAddr StepIntoEvent;

	@Keynode
	public static SCAddr StepReturnEvent;

	@Keynode
	public static SCAddr StepEndEvent;

	@Keynode
	public static SCAddr BreapointEvent;

	@Keynode
	public static SCAddr TerminatedEvent;

	@Keynode
	public static SCAddr ChildEvent;

	@Keynode
	public static SCAddr CallEvent;

	@Keynode
	public static SCAddr ReturnEvent;
}

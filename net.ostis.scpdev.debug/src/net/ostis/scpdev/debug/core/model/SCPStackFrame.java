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
import java.util.Set;

import net.ostis.sc.memory.SCAddr;
import net.ostis.sc.memory.SCPKeynodes;
import net.ostis.sc.memory.TupUtils;

import org.apache.commons.lang3.Validate;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

/**
 * Processor Module stack frame. From code of examples PDA debugger plugin
 * "org.eclipse.debug.examples.core".
 *
 * @author Dmitry Lazurkin
 */
public class SCPStackFrame extends PMDebugElement implements IStackFrame {

	private SCPProcess process;

	private SCAddr sign;

	private SCPProgram program;
	private SCAddr varValue;
	private SCPOperator activeOp;
	private SCPVariable[] variables = null;

	public SCPStackFrame(SCPProcess process, SCAddr sign) {
		super(process.getCastedTarget());
		this.process = process;
		this.sign = sign;
		fetch();
	}

	/**
	 * @return true if state changed else false.
	 */
	public boolean update() {
		SCAddr newActiveOpSign = TupUtils.get(getSession(), sign, SCPKeynodes.active_);

		if (!newActiveOpSign.equals(activeOp.getSign())) {
			activeOp = program.getOperator(newActiveOpSign);
			return true;
		} else {
			return false;
		}
	}

	public SCPProcess getProcess() {
		return process;
	}

	public SCAddr getSign() {
		return sign;
	}

	public void setSign(SCAddr sign) {
		Validate.notNull(sign);
		this.sign = sign;
	}

	public SCPProgram getProgram() {
		return program;
	}

	public SCPOperator getActiveOp() {
		return activeOp;
	}

	public SCAddr getVarValue() {
		return varValue;
	}

	private boolean isActiveCallReturn() {
		return activeOp.getType().equals("callReturn");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#getThread()
	 */
	@Override
	public IThread getThread() {
		return process;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#getVariables()
	 */
	@Override
	public IVariable[] getVariables() throws DebugException {
		return variables;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#hasVariables()
	 */
	@Override
	public boolean hasVariables() throws DebugException {
		return program.hasVariables();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#getLineNumber()
	 */
	@Override
	public int getLineNumber() throws DebugException {
		return activeOp.getLine();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#getCharStart()
	 */
	@Override
	public int getCharStart() throws DebugException {
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#getCharEnd()
	 */
	@Override
	public int getCharEnd() throws DebugException {
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#getName()
	 */
	@Override
	public String getName() throws DebugException {
		return toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#getRegisterGroups()
	 */
	@Override
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#hasRegisterGroups()
	 */
	@Override
	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	@Override
	public boolean canStepInto() {
		return isSuspended() && isActiveCallReturn();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	@Override
	public boolean canStepOver() {
		return getThread().canStepOver();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	@Override
	public boolean canStepReturn() {
		return getThread().canStepReturn();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	@Override
	public boolean isStepping() {
		return getThread().isStepping();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	@Override
	public void stepInto() throws DebugException {
		getThread().stepInto();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	@Override
	public void stepOver() throws DebugException {
		getThread().stepOver();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	@Override
	public void stepReturn() throws DebugException {
		getThread().stepReturn();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	@Override
	public boolean canResume() {
		return getThread().canResume();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	@Override
	public boolean canSuspend() {
		return getThread().canSuspend();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	@Override
	public boolean isSuspended() {
		return getThread().isSuspended();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	@Override
	public void resume() throws DebugException {
		getThread().resume();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	@Override
	public void suspend() throws DebugException {
		getThread().suspend();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	@Override
	public boolean canTerminate() {
		return getThread().canTerminate();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	@Override
	public boolean isTerminated() {
		return getThread().isTerminated();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	@Override
	public void terminate() throws DebugException {
		getThread().terminate();
	}

	/**
	 * Fetch stack frame information ffrom sc-memory.
	 * @return true if stack frame changed else false.
	 */
	private boolean fetch() {
		SCAddr[] components = TupUtils.unpack(getSession(), sign, SCPKeynodes.program_, SCPKeynodes.varValue_,
				SCPKeynodes.active_);
		Validate.noNullElements(components);

		program = SCPProgram.getCache(components[0]);
		if (program == null)
			program = new SCPProgram(getSession(), components[0]);

		varValue = components[1];
		activeOp = program.getOperator(components[2]);

		//
		// Create SCPVariable's array from scp-program variables.
		// This array is unmutable.
		//
		Set<SCAddr> variables = program.getVariables();
		this.variables = new SCPVariable[variables.size()];
		int index = 0;
		for (SCAddr variable : variables) {
			this.variables[index] = new SCPVariable(this, variable, program.getVariableName(variable));
			++index;
		}
		return true;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0}::{1} ({2}, line {3})", program.getFullName(), activeOp.getShortName(),
				activeOp.getType(), activeOp.getLine());
	}
}

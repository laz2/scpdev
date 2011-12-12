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

import net.ostis.sc.memory.RelUtils;
import net.ostis.sc.memory.SCAddr;
import net.ostis.scpdev.debug.core.model.values.SCElementValue;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * A variable in a Processor Module SCP Process stack frame. From code of examples PDA debugger
 * plugin "org.eclipse.debug.examples.core".
 *
 * @author Dmitry Lazurkin
 */
public class SCPVariable extends PMDebugElement implements IVariable, IValue {

	private final SCPStackFrame frame;
	private final SCAddr sign;
	private final String name;
	private SCAddr value = null;
	private IValue scvalue = null;
	private boolean fetched = false;

	/**
	 * Constructs a variable contained in the given scp stack frame.
	 */
	public SCPVariable(SCPStackFrame frame, SCAddr sign, String name) {
		super(frame.getCastedTarget());
		this.frame = frame;
		this.sign = sign;
		this.name = name;
		this.scvalue = new SCElementValue(getCastedTarget(), value);
	}

	/**
	 * Fetch value of variable for current scp-process from sc-memory.
	 * @return true if value changed else false.
	 */
	public boolean update() {
		boolean changed = false;

		if (!fetched) {
			SCAddr valueNew = RelUtils.getBinOrd2(frame.getSession(), frame.getVarValue(), sign);
			fetched = true;

			// value may be null if variable isn't assigned.
			changed = valueNew != value;

			value = valueNew;

			if (changed)
				scvalue = new SCElementValue(getCastedTarget(), value);

			return changed;
		}

		return changed;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getValue()
	 */
	@Override
	public IValue getValue() throws DebugException {
		update();
		fetched = false;
		return scvalue;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getName()
	 */
	@Override
	public String getName() throws DebugException {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getReferenceTypeName()
	 */
	@Override
	public String getReferenceTypeName() throws DebugException {
		return "sc-element";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#hasValueChanged()
	 */
	@Override
	public boolean hasValueChanged() throws DebugException {
		return update();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String expression) throws DebugException {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(org.eclipse.debug.core.model.IValue)
	 */
	@Override
	public void setValue(IValue value) throws DebugException {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#supportsValueModification()
	 */
	@Override
	public boolean supportsValueModification() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(java.lang.String)
	 */
	@Override
	public boolean verifyValue(String expression) throws DebugException {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(org.eclipse.debug.core.model.IValue)
	 */
	@Override
	public boolean verifyValue(IValue value) throws DebugException {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	@Override
	public String getValueString() throws DebugException {
		//
		// If we here then variable hasn't value.
		// @see getValue
		//
		return "NIL";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	@Override
	public boolean isAllocated() throws DebugException {
		return true;
	}

	private static IVariable[] dummyArray = new IVariable[0];

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */
	@Override
	public IVariable[] getVariables() throws DebugException {
		return dummyArray;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */
	@Override
	public boolean hasVariables() throws DebugException {
		return false;
	}
}

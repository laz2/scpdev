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
package net.ostis.scpdev.debug.core.model.values;

import java.util.Collection;

import net.ostis.scpdev.debug.core.model.PMDebugTarget;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

/**
 * This entity is collection of sc-elements.
 *
 * @author Dmitry Lazurkin
 */
public class SCCollectionEntityValue extends SCEntityValue {

	private final Collection<IVariable> values;
	private IVariable[] entitiesArray = null;

	public SCCollectionEntityValue(PMDebugTarget target, IVariable parent, String name, Collection<IVariable> values) {
		super(target, parent, name);
		this.values = values;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getReferenceTypeName()
	 */
	@Override
	public String getReferenceTypeName() throws DebugException {
		return "sc-set";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	@Override
	public String getValueString() throws DebugException {
		return "";
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.values.SCEntityValue#hasVariables()
	 */
	@Override
	public boolean hasVariables() throws DebugException {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.debug.core.model.values.SCEntityValue#getVariables()
	 */
	@Override
	public IVariable[] getVariables() throws DebugException {
		if (entitiesArray == null)
			entitiesArray = (IVariable[]) values.toArray(new IVariable[values.size()]);
		return entitiesArray;
	}
}

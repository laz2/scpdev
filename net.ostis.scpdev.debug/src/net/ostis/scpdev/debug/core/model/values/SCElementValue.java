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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.ostis.sc.memory.SCAddr;
import net.ostis.sc.memory.SCConstraintBase;
import net.ostis.sc.memory.SCContent;
import net.ostis.sc.memory.SCIterator;
import net.ostis.sc.memory.SCPKeynodes;
import net.ostis.sc.memory.SCSession;
import net.ostis.sc.memory.SCType;
import net.ostis.sc.memory.STDConstraints;
import net.ostis.sc.memory.SetUtils;
import net.ostis.sc.memory.URIUtils;
import net.ostis.scpdev.debug.core.model.PMDebugElement;
import net.ostis.scpdev.debug.core.model.PMDebugTarget;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * Represent undf sc-element as value of scp-variable.
 *
 * @author Dmitry Lazurkin
 */
public class SCElementValue extends PMDebugElement implements IVariable, IValue {

	protected List<SCAddr> attrs = null;
	protected String attrURI = "";

	protected final SCAddr value;
	protected final String idtf;
	protected final String uri;
	protected final SCType type;

	protected SCContent content;
	protected List<IVariable> memberOf;
	protected List<IVariable> members;

	protected List<IVariable> entities = new LinkedList<IVariable>();
	protected IVariable[] entitiesArray = null;

	public SCElementValue(PMDebugTarget target, SCAddr value) {
		super(target);
		this.value = value;
		if (value != null) {
			this.type = getSession().getType(value);
			this.idtf = getSession().getIdtf(value);
			this.uri = URIUtils.getFullURI(getSession(), value);
		} else {
			this.type = null;
			this.idtf = "NIL";
			this.uri = "NIL";
		}
	}

	public SCElementValue(PMDebugTarget target, List<SCAddr> attrs, SCAddr value) {
		this(target, value);
		if (attrs != null && !attrs.isEmpty()) {
			this.attrs = attrs;
			for (SCAddr attr : attrs)
				this.attrURI += URIUtils.getFullURI(getSession(), attr) + ": ";
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */
	@Override
	public String getReferenceTypeName() throws DebugException {
		return type.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	@Override
	public String getValueString() throws DebugException {
		if (value != null) {
			return uri + ", " + type;
		} else {
			return uri;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	@Override
	public boolean isAllocated() throws DebugException {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */
	@Override
	public IVariable[] getVariables() throws DebugException {
		if (entitiesArray == null) {
			fetch();
			entitiesArray = (IVariable[]) entities.toArray(new IVariable[entities.size()]);
		}
		return entitiesArray;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */
	@Override
	public boolean hasVariables() throws DebugException {
		return value != null;
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
	 * @see org.eclipse.debug.core.model.IVariable#getValue()
	 */
	@Override
	public IValue getValue() throws DebugException {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getName()
	 */
	@Override
	public String getName() throws DebugException {
		return attrURI + " " + idtf;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#hasValueChanged()
	 */
	@Override
	public boolean hasValueChanged() throws DebugException {
		return false;
	}

	protected void addPlainEntity(String name, Object value) {
		entities.add(new SCPlainEntityValue(getCastedTarget(), this, name, value));
	}

	protected void addCollectionEntity(String name, Collection<IVariable> values) {
		entities.add(new SCCollectionEntityValue(getCastedTarget(), this, name, values));
	}

	private void fetchMembers() {
		SCIterator iter = getSession().createIterator(
				new SCConstraintBase(STDConstraints.CONSTR_3_f_a_a, value, SCType.EMPTY, SCType.EMPTY));
		try {
			members = new LinkedList<IVariable>();
			for (; !iter.isOver(); iter.next()) {
				SCAddr arc = iter.getValue(1);

				List<SCAddr> attrs = new ArrayList<SCAddr>();

				SCIterator attrIter = getSession().createIterator(
						new SCConstraintBase(STDConstraints.CONSTR_3_a_a_f, SCType.EMPTY, SCType.EMPTY, arc));
				try {
					for (; !attrIter.isOver(); attrIter.next()) {
						SCAddr attr = attrIter.getValue(0);
						if (getSession().getIdtf(attr).endsWith("_"))
							attrs.add(attr);
					}
				} finally {
					attrIter.erase();
				}

				members.add(new SCElementValue(getCastedTarget(), attrs, iter.getValue(2)));
			}
		} finally {
			iter.erase();
		}
	}

	private void fetch() {
		SCSession session = getSession();

		content = session.getContent(value);
		if (!content.isEmpty())
			addPlainEntity("content", content);

		memberOf = new ArrayList<IVariable>();
		List<SCAddr> memberOfAddrs = new ArrayList<SCAddr>();
		SetUtils.memberOf(session, value, memberOfAddrs);
		for (SCAddr set : memberOfAddrs) {
			if (set.getSegment() != SCPKeynodes.coreSegment)
				memberOf.add(new SCElementValue(getCastedTarget(), set));
		}

		if (memberOf.size() != 0)
			addCollectionEntity("input", memberOf);

		fetchMembers();
		if (members.size() != 0)
			addCollectionEntity("output", members);
	}

}

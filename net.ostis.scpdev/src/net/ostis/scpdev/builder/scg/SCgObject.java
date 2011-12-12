/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent
 * Systems) For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2010 OSTIS
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
package net.ostis.scpdev.builder.scg;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * @author Dmitry Lazurkin
 */
public abstract class SCgObject implements IGwfDeserializable, ISCsSerializable {
	public static final String ID = "id";
	public static final String PARENT = "parent";

	protected SCgIdentity identity = null;

	protected String systemId = null;
	protected SCgObject parent = null;

	protected List<SCgObject> childs = null;

	public SCgObject(SCgIdentity identity) {
		this.identity = identity;
	}

	public SCgIdentity getIdentity() {
		return identity;
	}

	public void setIdentity(SCgIdentity identity) {
		this.identity = identity;
	}

	public String getSystemId() {
		return systemId;
	}

	public SCgObject getParent() {
		return parent;
	}

	public List<SCgObject> getChilds() {
		return childs;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public void setParent(SCgObject parent) {
		this.parent = parent;
	}

	public void addChild(SCgObject object) {
		if (childs == null)
			childs = new LinkedList<SCgObject>();
		childs.add(object);
	}

	public void removeChild(SCgObject object) {
		if (childs != null)
			childs.remove(object);
	}

	public void setChilds(List<SCgObject> childs) {
		this.childs = childs;
	}

	protected void addReference(String name, Element el, Map<String, String> references) {
		references.put(name, el.getAttribute(name));
	}

	public void readState(Element el, Map<String, String> references) {
		systemId = el.getAttribute(ID);
		addReference(PARENT, el, references);
	}

	@Override
	public void setReferences(Map<String, SCgObject> neededReferences) {
		parent = neededReferences.get(PARENT);
		if (parent != null)
			parent.addChild(this);
	}

	protected abstract void encodeImpl(SCsWriter writer) throws IOException;

	@Override
	public void encode(SCsWriter writer) throws IOException {
		if (identity.isWrited() == false) {
			identity.setWrited(true);
			encodeImpl(writer);
		} else if (childs != null && childs.size() != 0) {
			encodeImpl(writer);
		}
	}

	@Override
	public boolean isWrited() {
		return identity.isWrited();
	}

	@Override
	public List<String> getGeneratedIds() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if ((obj instanceof SCgObject) == false) {
			return false;
		}

		SCgObject other = (SCgObject) obj;

		return this.identity == other.identity;
	}

}

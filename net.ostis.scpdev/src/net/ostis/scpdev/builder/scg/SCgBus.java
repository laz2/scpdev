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
import java.util.Map;

import org.w3c.dom.Element;

/**
 * @author Dmitry Lazurkin
 */
public class SCgBus extends SCgObject {

	public static final String OWNER = "owner";

	private SCgObject owner = null;

	public SCgBus(SCgIdentity identity) {
		super(identity);
	}

	public SCgObject getOwner() {
		return owner;
	}

	@Override
	public void readState(Element el, Map<String, String> references) {
		super.readState(el, references);

		String ownerId = el.getAttribute(OWNER);
		if (ownerId.equals("0") == false)
			references.put(OWNER, el.getAttribute(OWNER));
	}

	@Override
	public void setReferences(Map<String, SCgObject> neededReferences) {
		super.setReferences(neededReferences);
		owner = neededReferences.get(OWNER);
		identity = owner.getIdentity();
	}

	@Override
	protected void encodeImpl(SCsWriter writer) throws IOException {
		writer.comment("<bus>");
		writer.incTab();

		if (owner != null) {
			owner.encode(writer);
		} else {
			String mainId = writer.node(null, SCsWriter.SC_CONST);
			identity.setMainId(mainId);
		}

		writer.decTab();
		writer.comment("</bus>");
	}

}

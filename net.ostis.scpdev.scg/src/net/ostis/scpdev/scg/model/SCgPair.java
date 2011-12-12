/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent Systems)
 * For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2010 OSTIS
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
package net.ostis.scpdev.scg.model;

import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.w3c.dom.Element;

/**
 * @author Dmitry Lazurkin
 */
public class SCgPair extends SCgObject {

	private static final long serialVersionUID = 1;

	public static final String TYPE = "type";
	public static final String BEGIN = "id_b";
	public static final String END = "id_e";

	/** True, if the connection is attached to its endpoints. */
	private boolean isConnected;

	/** SCg-pair's begin. */
	private SCgObject begin;

	/** SCg-pair's end. */
	private SCgObject end;

	private PosType posType;

	private boolean orient;

	public boolean isOrient() {
		return orient;
	}

	public void setOrient(boolean orient) {
		this.orient = orient;
	}

	public PosType getPosType() {
		return posType;
	}

	public void setPosType(PosType posType) {
		this.posType = posType;
	}

	private static final String POS_TYPE_PROP = "SCgPair.posType";

	static {
	}

	public SCgPair(SCgIdentity identity) {
		super(identity);
		posType = PosType.Unknown;
	}

	@Override
	public void readState(Element el, Map<String, String> references) {
		super.readState(el, references);
		String type = el.getAttribute("type");

		if (type.indexOf("pair") != -1) {
			orient = false;
		} else if (type.indexOf("arc") != -1) {
			orient = true;
		} else {
			throw new UnsupportedOperationException("Type must have arc or pair specificator, but it is \"" + type + "\"");
		}

		if (type.indexOf("const") != -1) {
			setConstType(ConstType.Const);
		} else if (type.indexOf("var") != -1) {
			setConstType(ConstType.Var);
		} else if (type.indexOf("meta") != -1) {
			setConstType(ConstType.Meta);
		}

		if (orient) {
			if (type.indexOf("pos") != -1) {
				setPosType(PosType.Positive);
			} else if (type.indexOf("neg") != -1) {
				setPosType(PosType.Negative);
			} else if (type.indexOf("fuz") != -1) {
				setPosType(PosType.Fuzzy);
			} else {
				throw new UnsupportedOperationException("Type for arc must have pos or neg or fuz specificator, but it is \"" + type + "\"");
			}
		}

		addReference("id_b", el, references);
		addReference("id_e", el, references);
	}

	@Override
	public void setReferences(Map<String, SCgObject> neededReferences) {
		super.setReferences(neededReferences);
		begin = neededReferences.get(BEGIN);
		end = neededReferences.get(END);
	}

	/**
	 * Disconnect this connection from the shapes it is attached to.
	 */
	public void disconnect() {
		if (isConnected) {
			begin.removeConnectedObject(this);
			end.removeConnectedObject(this);
			isConnected = false;
		}
	}

	public Object getPropertyValue(Object id) {
		return super.getPropertyValue(id);
	}

	/**
	 * Returns the begin of this pair.
	 *
	 * @return a non-null SCgObject instance
	 */
	public SCgObject getBegin() {
		return begin;
	}

	/**
	 * Returns the end of this pair.
	 *
	 * @return a non-null SCgObject instance
	 */
	public SCgObject getEnd() {
		return end;
	}

	/**
	 * Reconnect this pair. The connection will reconnect with the SCg-objects it was previously
	 * attached to.
	 */
	public void reconnect() {
		if (!isConnected) {
			begin.addConnectedObject(this);
			end.addConnectedObject(this);
			isConnected = true;
		}
	}

	/**
	 * Reconnect to a different source and/or target shape. The connection will disconnect from its
	 * current attachments and reconnect to the new source and target.
	 *
	 * @param newBegin a new source endpoint for this connection (non null)
	 * @param newEnd a new target endpoint for this connection (non null)
	 * @throws IllegalArgumentException if any of the paramers are null or newSource == newTarget
	 */
	public void reconnect(SCgObject newBegin, SCgObject newEnd) {
		Validate.notNull(newBegin);
		Validate.notNull(newEnd);

		disconnect();
		this.begin = newBegin;
		this.end = newEnd;
		reconnect();
	}

	public void setPropertyValue(Object id, Object value) {
		super.setPropertyValue(id, value);
	}

	@Override
	public String toString() {
		return "<SCgPair, " + posType + ", " + getConstType() + ">";
	}
}

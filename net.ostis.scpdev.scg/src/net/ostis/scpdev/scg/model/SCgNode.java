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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Dmitry Lazurkin
 */
public class SCgNode extends SCgObject {

	public static final String TYPE = "type";
	public static final String CONTENT = "content";

	private static final Map<String, NodeStructType> structMapping = new HashMap<String, NodeStructType>();

	static {
		structMapping.put("not_define", NodeStructType.NotDefine);
		structMapping.put("general_node", NodeStructType.General);
		structMapping.put("predmet", NodeStructType.Predmet);
		structMapping.put("nopredmet", NodeStructType.NoPredmet);
		structMapping.put("symmetry", NodeStructType.Symmetry);
		structMapping.put("asymmetry", NodeStructType.Asymmetry);
		structMapping.put("attribute", NodeStructType.Attribute);
		structMapping.put("relation", NodeStructType.Relation);
		structMapping.put("atom", NodeStructType.Atom);
		structMapping.put("group", NodeStructType.Group);
	}

	public SCgNode(SCgIdentity identity) {
		super(identity);
	}

	private static final long serialVersionUID = 1;

	public static final String STRUCT_TYPE_PROP = "SCgNode.strucType";

	private NodeStructType structType = NodeStructType.StructUnknown;

	public NodeStructType getStructType() {
		return structType;
	}

	public void setStructType(NodeStructType structType) {
		NodeStructType old = this.structType;
		this.structType = structType;
		firePropertyChange(STRUCT_TYPE_PROP, old, structType);
	}

	public void readState(Element el, Map<String, String> references) {
		super.readState(el, references);
		String type = el.getAttribute(TYPE);

		if (type.indexOf("const") != -1) {
			setConstType(ConstType.Const);
		} else if (type.indexOf("var") != -1) {
			setConstType(ConstType.Var);
		} else if (type.indexOf("meta") != -1) {
			setConstType(ConstType.Meta);
		}

		for (Map.Entry<String, NodeStructType> entry : structMapping.entrySet()) {
			if (type.indexOf(entry.getKey()) != -1) {
				setStructType(entry.getValue());
				break;
			}
		}

		String x = el.getAttribute("x");
		String y = el.getAttribute("y");

		setLocation(new Point(new Double(x), new Double(y)));

		NodeList contentTags = el.getElementsByTagName("content");
		if (contentTags != null && contentTags.getLength() > 0) {
			// content = new SCgContent();
			// content.readState((Element) contentTags.item(0), references);
		}
	}

	public Object getPropertyValue(Object propertyId) {
		if (STRUCT_TYPE_PROP.equals(propertyId)) {
			return structType.toString();
		}

		return super.getPropertyValue(propertyId);
	}

	public void setPropertyValue(Object propertyId, Object value) {
		super.setPropertyValue(propertyId, value);
	}

	public String toString() {
		return "<SCgNode, " + structType + ", " + getConstType() + ">";
	}
}

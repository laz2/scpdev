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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.w3c.dom.Element;

/**
 * @author Dmitry Lazurkin
 */
public abstract class SCgObject extends ModelElement implements ISCgSerializable {

	/**
	 * A static array of property descriptors. There is one IPropertyDescriptor entry per editable
	 * property.
	 *
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	private static IPropertyDescriptor[] descriptors;

	/** Property ID to use when the location of this shape is modified. */
	public static final String LOCATION_PROP = "SCgObject.Location";

	private static final long serialVersionUID = 1;

	/** Property ID to use when the list of connected objects is modified. */
	public static final String SOURCE_CONNECTIONS_PROP = "SCgObject.SourceConn";

	public static final String TARGET_CONNECTIONS_PROP = "SCgObject.TargetConn";

	/**
	 * ID for the X property value (used for by the corresponding property descriptor).
	 */
	private static final String XPOS_PROP = "SCgObject.xPos";

	/**
	 * ID for the Y property value (used for by the corresponding property descriptor).
	 */
	private static final String YPOS_PROP = "SCgObject.yPos";

	private static final String CONST_TYPE_PROP = "SCgObject.constType";

	protected SCgIdentity identity = null;

	public static final String ID = "id";
	public static final String PARENT = "parent";

	public SCgObject(SCgIdentity identity) {
		this.identity = identity;
	}

	public SCgIdentity getIdentity() {
		return identity;
	}

	public void setIdentity(SCgIdentity identity) {
		this.identity = identity;
	}

	protected String systemId = null;
	protected SCgObject parent = null;

	public SCgObject getParent() {
		return parent;
	}

	public void setParent(SCgObject parent) {
		this.parent = parent;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	protected void addReference(String name, Element el, Map<String, String> references) {
		references.put(name, el.getAttribute(name));
	}

	public void readState(Element el, Map<String, String> references) {
		systemId = el.getAttribute(ID);
		// addReference(PARENT, el, references);
	}

	@Override
	public void setReferences(Map<String, SCgObject> neededReferences) {
		// parent = neededReferences.get(PARENT);
		// if (parent != null)
		// parent.addChild(this);
	}

	static {
		descriptors = new IPropertyDescriptor[] {
				new TextPropertyDescriptor(XPOS_PROP, "X"), // id and description pair
				new TextPropertyDescriptor(YPOS_PROP, "Y")
		};
		// use a custom cell editor validator for all four array entries
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
				public String isValid(Object value) {
					int intValue = -1;
					try {
						intValue = Integer.parseInt((String) value);
					} catch (NumberFormatException exc) {
						return "Not a number";
					}
					return (intValue >= 0) ? null : "Value must be >=  0";
				}
			});
		}
	} // static

	/** Location of this shape. */
	private Point location = new Point(0, 0);

	/** List of connected objects. */
	private List<SCgObject> sourceConnections = new LinkedList<SCgObject>();

	private List<SCgObject> targetConnections = new LinkedList<SCgObject>();

	private ConstType constType;

	public ConstType getConstType() {
		return constType;
	}

	public void setConstType(ConstType constType) {
		ConstType old = constType;
		this.constType = constType;
		firePropertyChange(SOURCE_CONNECTIONS_PROP, old, constType);
	}

	/**
	 * Add an incoming or outgoing connection to this shape.
	 *
	 * @param conn a non-null connection instance
	 * @throws IllegalArgumentException if the connection is null or has not distinct endpoints
	 */
	void addConnectedObject(SCgPair object) {
		Validate.notNull(object);
		if (object.getBegin() == this) {
			sourceConnections.add(object);
			firePropertyChange(SOURCE_CONNECTIONS_PROP, null, object);
		} else if (object.getEnd() == this) {
			targetConnections.add(object);
			firePropertyChange(TARGET_CONNECTIONS_PROP, null, object);
		}
	}

	/**
	 * Return the Location of this shape.
	 *
	 * @return a non-null location instance
	 */
	public Point getLocation() {
		return location.getCopy();
	}

	/**
	 * Returns an array of IPropertyDescriptors for this shape.
	 * <p>
	 * The returned array is used to fill the property view, when the edit-part corresponding to
	 * this model element is selected.
	 * </p>
	 *
	 * @see #descriptors
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	/**
	 * Return the property value for the given propertyId, or null.
	 * <p>
	 * The property view uses the IDs from the IPropertyDescriptors array to obtain the value of the
	 * corresponding properties.
	 * </p>
	 *
	 * @see #descriptors
	 * @see #getPropertyDescriptors()
	 */
	public Object getPropertyValue(Object propertyId) {
		if (XPOS_PROP.equals(propertyId)) {
			return Integer.toString(location.x);
		}
		if (YPOS_PROP.equals(propertyId)) {
			return Integer.toString(location.y);
		}
		return super.getPropertyValue(propertyId);
	}

	/**
	 * Return a List of outgoing Connections.
	 */
	public List<SCgObject> getSourceConnections() {
		return new ArrayList<SCgObject>(sourceConnections);
	}

	/**
	 * Return a List of incoming Connections.
	 */
	public List<SCgObject> getTargetConnections() {
		return new ArrayList<SCgObject>(targetConnections);
	}

	/**
	 * Return a List of outgoing Connections.
	 */
	public List<SCgObject> getConnectedObjects() {
		return new ArrayList<SCgObject>(sourceConnections);
	}

	public int getInputCount() {
		return sourceConnections.size();
	}

	public int getOutputCount() {
		return targetConnections.size();
	}

	/**
	 * Remove an incoming or outgoing connection from this shape.
	 *
	 * @param object a non-null connection instance
	 * @throws IllegalArgumentException if the parameter is null
	 */
	void removeConnectedObject(SCgPair object) {
		Validate.notNull(object);
		if (object.getBegin() == this) {
			sourceConnections.remove(object);
			firePropertyChange(SOURCE_CONNECTIONS_PROP, null, object);
		} else if (object.getEnd() == this) {
			targetConnections.remove(object);
			firePropertyChange(TARGET_CONNECTIONS_PROP, null, object);
		}
	}

	/**
	 * Set the Location of this shape.
	 *
	 * @param newLocation a non-null Point instance
	 * @throws IllegalArgumentException if the parameter is null
	 */
	public void setLocation(Point newLocation) {
		if (newLocation == null) {
			throw new IllegalArgumentException();
		}
		location.setLocation(newLocation);
		firePropertyChange(LOCATION_PROP, null, location);
	}

	/**
	 * Set the property value for the given property id. If no matching id is found, the call is
	 * forwarded to the superclass.
	 * <p>
	 * The property view uses the IDs from the IPropertyDescriptors array to set the values of the
	 * corresponding properties.
	 * </p>
	 *
	 * @see #descriptors
	 * @see #getPropertyDescriptors()
	 */
	public void setPropertyValue(Object propertyId, Object value) {
		if (XPOS_PROP.equals(propertyId)) {
			int x = Integer.parseInt((String) value);
			setLocation(new Point(x, location.y));
		} else if (YPOS_PROP.equals(propertyId)) {
			int y = Integer.parseInt((String) value);
			setLocation(new Point(location.x, y));
		} else {
			super.setPropertyValue(propertyId, value);
		}
	}
}

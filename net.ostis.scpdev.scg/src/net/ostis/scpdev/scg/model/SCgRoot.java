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

import java.util.LinkedList;
import java.util.List;

/**
 * @author Dmitry Lazurkin
 */
public class SCgRoot extends ModelElement {

	/** Property ID to use when a child is added to this diagram. */
	public static final String CHILD_ADDED_PROP = "SCgRoot.ChildAdded";

	/** Property ID to use when a child is removed from this diagram. */
	public static final String CHILD_REMOVED_PROP = "SCgRoot.ChildRemoved";

	private static final long serialVersionUID = 1;

	private List<SCgObject> objects = new LinkedList<SCgObject>();

	/**
	 * Add a child SCg-object.
	 *
	 * @param object a non-null SCg-object instance
	 * @return true, if the SCg-object was added, false otherwise
	 */
	public boolean addChild(SCgObject object) {
		if (object != null && objects.add(object)) {
			firePropertyChange(CHILD_ADDED_PROP, null, object);
			return true;
		}

		return false;
	}

	/**
	 * Return a List of SCg-objects in this diagram. The returned List should not be modified.
	 */
	public List<SCgObject> getChildren() {
		return objects;
	}

	/**
	 * Remove a child SCg-object.
	 *
	 * @param s a non-null SCg-object instance;
	 * @return true, if the SCg-object was removed, false otherwise
	 */
	public boolean removeChild(SCgObject s) {
		if (s != null && objects.remove(s)) {
			firePropertyChange(CHILD_REMOVED_PROP, null, s);
			return true;
		}

		return false;
	}
}

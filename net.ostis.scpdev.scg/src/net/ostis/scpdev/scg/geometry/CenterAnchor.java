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
package net.ostis.scpdev.scg.geometry;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Dmitry Lazurkin
 */
public class CenterAnchor extends AbstractConnectionAnchor {

	/**
	 * Constructs a new CenterAnchor.
	 */
	protected CenterAnchor() {
	}

	/**
	 * Constructs a CenterAnchor with the given <i>owner</i> figure.
	 *
	 * @param owner
	 *            The owner figure
	 */
	public CenterAnchor(IFigure owner) {
		super(owner);
	}

	/**
	 * Gets a Rectangle from {@link #getBox()} and returns the center Point of owner.
	 *
	 * @param reference
	 *            The reference point
	 * @return The anchor location
	 */
	public Point getLocation(Point reference) {
		Rectangle r = Rectangle.SINGLETON;
		r.setBounds(getBox());
		r.translate(-1, -1);
		r.resize(1, 1);

		getOwner().translateToAbsolute(r);
		float centerX = r.x + 0.5f * r.width;
		float centerY = r.y + 0.5f * r.height;

		return new Point(Math.round(centerX), Math.round(centerY));
	}

	/**
	 * Returns the bounds of this CenterAnchor's owner. Subclasses can override
	 * this method to adjust the box the anchor can be placed on. For instance,
	 * the owner figure may have a drop shadow that should not be included in
	 * the box.
	 *
	 * @return The bounds of this CenterAnchor's owner
	 */
	protected Rectangle getBox() {
		return getOwner().getBounds();
	}

	/**
	 * Returns the anchor's reference point. In the case of the CenterAnchor,
	 * this is the center of the anchor's owner.
	 *
	 * @return The reference point
	 */
	public Point getReferencePoint() {
		Point ref = getBox().getCenter();
		getOwner().translateToAbsolute(ref);
		return ref;
	}

	/**
	 * Returns <code>true</code> if the other anchor has the same owner and box.
	 *
	 * @param obj
	 *            the other anchor
	 * @return <code>true</code> if equal
	 */
	public boolean equals(Object obj) {
		if (obj instanceof ChopboxAnchor) {
			CenterAnchor other = (CenterAnchor) obj;
			return other.getOwner() == getOwner()
					&& other.getBox().equals(getBox());
		}
		return false;
	}

	/**
	 * The owning figure's hashcode is used since equality is approximately
	 * based on the owner.
	 *
	 * @return the hash code.
	 */
	public int hashCode() {
		if (getOwner() != null)
			return getOwner().hashCode();
		else
			return super.hashCode();
	}
}

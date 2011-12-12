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

import net.ostis.scpdev.scg.model.NodeStructType;
import net.ostis.scpdev.scg.model.SCgNode;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.LineAttributes;

/**
 * @author Dmitry Lazurkin
 */
public class SCgNodeShape extends Shape {

	private final SCgNode model;

	private static final LineAttributes notDefineAttrs = new LineAttributes(2, SWT.CAP_SQUARE, SWT.JOIN_MITER,
			SWT.LINE_SOLID, null, 0, 0);

	private static final LineAttributes otherAttrs = new LineAttributes(4, SWT.CAP_SQUARE, SWT.JOIN_MITER,
			SWT.LINE_SOLID, null, 0, 0);

	private static final LineAttributes structAttrs = new LineAttributes(2, SWT.CAP_FLAT, SWT.JOIN_MITER,
			SWT.LINE_SOLID, null, 0, 0);

	public SCgNodeShape(SCgNode model) {
		this.model = model;
		setAntialias(SWT.ON);

		if (model.getStructType() == NodeStructType.NotDefine) {
			setLineAttributes(notDefineAttrs);
		} else {
			setLineAttributes(otherAttrs);
		}
	}

	@Override
	protected void fillShape(Graphics graphics) {
	}

	protected void paintStruct(Graphics graphics, Rectangle bounds) {
		graphics.setLineAttributes(structAttrs);

		switch (getModel().getStructType()) {
			case NoPredmet: {
				Point center = bounds.getCenter();
				int w = bounds.width / 6;
				int h = bounds.height / 6;
				graphics.drawOval(center.x - w / 2, center.y - h / 2, w, h);
				break;
			}
			case Predmet: {
				graphics.setLineWidthFloat(0.5f);
				int x1 = bounds.x;
				int x2 = bounds.right();

				for (int y = bounds.y + 2; y < bounds.bottom(); y += 2)
					graphics.drawLine(x1, y, x2, y);
				break;
			}
			case Symmetry: {
				Point c = bounds.getCenter();
				Point d = new Point(0, bounds.height / 2.0);
				graphics.drawLine(c.x - d.x, c.y - d.y, c.x + d.x, c.y + d.y);
				break;
			}
			case Asymmetry: {
				Point c = bounds.getCenter();
				Point d = new Point(bounds.width / 2.0, 0.f);
				graphics.drawLine(c.x - d.x, c.y - d.y, c.x + d.x, c.y + d.y);
				break;
			}
			case Attribute: {
				Point c = bounds.getCenter();
				Point d = new Point(bounds.width / 2.0, 0.f);
				graphics.drawLine(c.x - d.x, c.y - d.y, c.x + d.x, c.y + d.y);
				d = new Point(0.f, bounds.height / 2.0);
				graphics.drawLine(c.x - d.x, c.y - d.y, c.x + d.x, c.y + d.y);
				break;
			}
			case Relation: {
				graphics.drawLine(bounds.getTopLeft(), bounds.getBottomRight());
				graphics.drawLine(bounds.getTopRight(), bounds.getBottomLeft());
				break;
			}
			case Atom: {
				break;
			}
			case Group: {
				graphics.drawLine(bounds.getTopLeft(), bounds.getBottomRight());
				graphics.drawLine(bounds.getTopRight(), bounds.getBottomLeft());
				graphics.drawLine(bounds.getLeft().x, bounds.getCenter().y, bounds.getRight().x, bounds.getCenter().y);
				break;
			}
			default:
				break;
		}
	}

	@Override
	protected void outlineShape(Graphics g) {
		try {
			g.pushState();

			Rectangle bounds = new Rectangle().setBounds(getBounds());

			bounds = bounds.crop(new Insets(2, 2, 2, 2));

			if (getModel().getStructType() == NodeStructType.NotDefine) {
				Point center = bounds.getCenter();
				int w = (int) Math.ceil(bounds.width * 0.3);
				int h = (int) Math.ceil(bounds.height * 0.3);
				bounds = new Rectangle(center.x - w / 2, center.y - h / 2, w, h);
			}

			switch (getModel().getConstType()) {
				case Const:
					g.drawOval(bounds);
					paintStruct(g, bounds);
					break;
				case Var:
					g.drawRectangle(bounds);
					g.setClip(bounds);
					paintStruct(g, bounds);
					break;
				case Meta:
					g.rotate(45);
					g.drawRectangle(bounds);

					paintStruct(g, bounds);
					g.rotate(-45);
					break;
				default:
					break;
			}
		} finally {
			g.popState();
		}
	}

	public SCgNode getModel() {
		return model;
	}
}

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

import net.ostis.scpdev.scg.model.ConstType;
import net.ostis.scpdev.scg.model.PosType;
import net.ostis.scpdev.scg.model.SCgPair;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;

/**
 * @author Dmitry Lazurkin
 */
public class SCgPairConnection extends PolylineConnection {

	private final SCgPair model;

	public SCgPairConnection(SCgPair model) {
		this.model = model;
		setAntialias(SWT.ON);
		setLineWidth(2);
	}

	@Override
	protected void outlineShape(Graphics g) {
		if (getModel().getPosType() == PosType.Positive && getModel().getConstType() == ConstType.Const) {
			super.outlineShape(g);
		} else {

		}
	}

	public SCgPair getModel() {
		return model;
	}
}

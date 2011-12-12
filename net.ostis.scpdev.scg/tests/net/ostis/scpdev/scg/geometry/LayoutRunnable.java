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
package net.ostis.scpdev.scg.geometry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ostis.scpdev.scg.model.SCgObject;
import net.ostis.scpdev.scg.model.SCgPair;

import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.widgets.Display;

public class LayoutRunnable implements Runnable {

	private IFigure root;

	private Point rootCenter;

	private List<SCgNodeShape> nodes = new LinkedList<SCgNodeShape>();
	private List<SCgPairConnection> lines = new LinkedList<SCgPairConnection>();
	private Map<SCgObject, IFigure> obj2figure = new HashMap<SCgObject, IFigure>();

	private RealVector nullVector = new ArrayRealVector(new double[] {
			0, 0
	});

	private double gravity = 0.01;
	private double max_rep_length = 12.0;
	private double repulsion = 2.7;
	private double length = 5.5;

	private double rigidity = 7.1;

	private boolean needLayout = true;

	private double maxForce = 0.7;
	private double stepMaxSize = 0.1;
	private double stepMinSize = 0.0125;
	private double stepSize = 0.1;

	private final Display d;

	public LayoutRunnable(Display d, IFigure root) {
		this.d = d;
		this.root = root;
		nodes.clear();

		for (Object object : root.getChildren()) {
			if (object instanceof SCgNodeShape) {
				SCgNodeShape child = (SCgNodeShape) object;
				nodes.add(child);
				obj2figure.put(child.getModel(), child);
			} else if (object instanceof SCgPairConnection) {
				SCgPairConnection child = (SCgPairConnection) object;
				lines.add(child);
				obj2figure.put(child.getModel(), child);
			}
		}
	}

	private void calculateForces() {
		int n = nodes.size();

		RealVector[] forces = new RealVector[n];
		for (int i = 0; i < n; ++i)
			forces[i] = new ArrayRealVector(nullVector);

		Map<IFigure, Integer> obj_f = new HashMap<IFigure, Integer>();

		//
		// Calculation repulsion forces.
		//
		for (int idx = 0; idx < n; ++idx) {
			SCgNodeShape obj = nodes.get(idx);

			obj_f.put(obj, idx);

			Point p1 = translateToCenter(obj.getBounds().getLocation());

			RealVector p1v = new ArrayRealVector(2);
			p1v.setEntry(0, p1.x);
			p1v.setEntry(1, p1.y);

			double l = nullVector.getDistance(p1v);

			RealVector f = p1v.mapMultiply(gravity * (l - 3.0));

			forces[idx] = forces[idx].subtract(f);

			for (int jdx = idx + 1; jdx < n; ++jdx) {
				Point p2 = translateToCenter(nodes.get(jdx).getBounds().getLocation());

				RealVector p2v = new ArrayRealVector(2);
				p2v.setEntry(0, p2.x);
				p2v.setEntry(1, p2.y);

				l = p1v.getDistance(p2v) / 50;

				if (l > max_rep_length)
					continue;

				if (l > 0.5) {
					f = p1v.subtract(p2v).mapMultiply(repulsion / l / l);
				} else {
					f = new ArrayRealVector(new double[] {
							Math.cos(0.17 * idx) * length * 7, Math.sin(0.17 * (idx + 1)) * length * 7
					});
				}

				forces[idx] = forces[idx].add(f);
				forces[jdx] = forces[jdx].subtract(f);
			}
		}

		//
		// Calculation springs.
		//
		for (SCgPairConnection line : lines) {
			SCgPair pair = line.getModel();

			SCgObject begin = pair.getBegin();
			SCgObject end = pair.getEnd();

			if (begin == null || end == null)
				continue;

			IFigure beginFigure = obj2figure.get(begin);
			IFigure endFigure = obj2figure.get(end);

			Point p1 = translateToCenter(beginFigure.getBounds().getLocation());
			Point p2 = translateToCenter(endFigure.getBounds().getLocation());

			RealVector p1v = new ArrayRealVector(2);
			p1v.setEntry(0, p1.x);
			p1v.setEntry(1, p1.y);

			RealVector p2v = new ArrayRealVector(2);
			p2v.setEntry(0, p2.x);
			p2v.setEntry(1, p2.y);

			double l = p1v.getDistance(p2v) / 50;

			RealVector f = null;

			if (l > 0) {
				RealVector pv = p2v.subtract(p1v);
				pv.unitize();

				int cnt = begin.getInputCount() + end.getOutputCount();
				f = pv.mapMultiply(rigidity * (l - Math.max(length, cnt / 3.0)) / l);

				if (nullVector.getDistance(f) > 10)
					f = pv.mapMultiply(rigidity / l);
			} else {
				f = new ArrayRealVector(nullVector);
			}

			if (obj_f.containsKey(beginFigure)) {
				int index = obj_f.get(beginFigure);
				forces[index] = forces[index].add(f);
			}

			if (obj_f.containsKey(endFigure)) {
				int index = obj_f.get(endFigure);
				forces[index] = forces[index].subtract(f);
			}
		}


		double maxf = 0.0;

		for (int idx = 0; idx < n; ++idx) {
			RealVector f = forces[idx];
			f.mapMultiplyToSelf(stepSize);

			maxf = Math.max(maxf, nullVector.getDistance(f));

			IFigure node = nodes.get(idx);
			Point location = translateToCenter(node.getBounds().getLocation());
			location.x += f.getEntry(0);
			location.y += f.getEntry(1);
			node.setLocation(translateFromCenter(location));
		}

		if (maxf > maxForce) {
			stepSize = stepMaxSize;
		} else {
			stepSize *= 0.97;
		}

		needLayout = stepSize > stepMinSize;
	}

	private void calculateRootCenter() {
		rootCenter = root.getBounds().getCenter();
	}

	private Point translateToCenter(Point p) {
		Point copy = p.getCopy();
		copy.x -= rootCenter.x;
		copy.y = rootCenter.y - copy.y;
		return copy;
	}

	private Point translateFromCenter(Point p) {
		Point copy = p.getCopy();
		copy.x += rootCenter.x;
		copy.y = rootCenter.y - copy.y;
		return copy;
	}

	public void init() {
		if (needLayout) {
			d.timerExec(300, this);
		} else {
			System.out.println("Layouting ended");
		}
	}

	@Override
	public void run() {
		calculateRootCenter();
		calculateForces();
		root.validate();
		init();
	}
}

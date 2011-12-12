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

import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.Subgraph;

public class GraphLayout extends AbstractLayout {

	private final IFigure root;
	private Subgraph subgraph;

	private List<IFigure> nodes = new LinkedList<IFigure>();
	private List<IFigure> lines = new LinkedList<IFigure>();

	private RealVector nullVector = new ArrayRealVector(new double[] {
			0, 0
	});

	private double gravity = 0.01;
	private double max_rep_length = 12.0;
	private double repulsion = 2.7;
	private double length = 5.5;
	private double stepSize = 0.1;

	static final Insets PADDING = new Insets(8, 6, 8, 6);
	static final Insets INNER_PADDING = new Insets(0);

	boolean first = true;

	public GraphLayout(IFigure root) {
		this.root = root;
	}

	private void calculateForces() {
		int n = nodes.size();

		RealVector[] forces = new RealVector[n];
		for (int i = 0; i < n; ++i)
			forces[i] = new ArrayRealVector(new double[] {
					0, 0
			});

		Map<IFigure, Integer> obj_f = new HashMap<IFigure, Integer>();
		Point[] o_pos = new Point[n];

		//
		// Calculation repulsion forces.
		//
		for (int idx = 0; idx < n; ++idx) {
			obj_f.put(nodes.get(idx), idx);

			Point p1 = nodes.get(idx).getBounds().getLocation();

			RealVector p1v = new ArrayRealVector(2);
			p1v.setEntry(0, p1.x);
			p1v.setEntry(0, p1.y);

			double l = nullVector.getDistance(p1v);

			RealVector f = p1v.mapMultiply(gravity * (l - 3.0));

			forces[idx].subtract(f);

			for (int jdx = idx + 1; jdx < n; ++jdx) {
				Point p2 = nodes.get(idx).getBounds().getLocation();

				RealVector p2v = new ArrayRealVector(2);
				p2v.setEntry(0, p2.x);
				p2v.setEntry(0, p2.y);

				l = p1v.getDistance(p2v);

				if (l > max_rep_length)
					continue;

				if (l > 0.5) {
					f = p1v.subtract(p2v).mapMultiply(repulsion / l / l);
				} else {
					f = new ArrayRealVector(new double[] {
							Math.cos(0.17 * idx) * length * 7, Math.sin(0.17 * (idx + 1)) * length * 7
					});
				}

				forces[idx].add(f);
				forces[jdx].subtract(f);
			}
		}

		for (int idx = 0; idx < n; ++idx) {
			RealVector f = forces[idx];
			f.mapMultiply(stepSize);
			nodes.get(idx).setLocation(new Point(f.getEntry(0), f.getEntry(1)));
		}
	}

	private boolean step() {
		calculateForces();
		return false;
	}

	protected void applyGraphResults(CompoundDirectedGraph graph, IFigure container, Map<IFigure, Node> map) {
		for (Object object : container.getChildren()) {
			if (object instanceof SCgNodeShape) {
				SCgNodeShape child = (SCgNodeShape) object;
				Node n = map.get(child);
				child.setBounds(new Rectangle(n.x, n.y, n.width, n.height));
			}
		}
	}

	public void contributeToGraph(CompoundDirectedGraph graph, IFigure container, Map<IFigure, Node> map) {
		for (Object object : container.getChildren()) {
			if (object instanceof SCgNodeShape) {
				SCgNodeShape child = (SCgNodeShape) object;
				Node n = new Node(child, subgraph);
				n.outgoingOffset = 9;
				n.incomingOffset = 9;
				n.width = child.getPreferredSize().width;
				n.height = child.getPreferredSize().height;
				n.setPadding(new Insets(10, 8, 10, 12));
				map.put(child, n);
				graph.nodes.add(n);
			}
		}
	}

	@Override
	public void layout(IFigure container) {
		// GraphAnimation.recordInitialState(container);
		// if (GraphAnimation.playbackState(container))
		// return;
		//
		// if (first) {
		// Map<IFigure, Node> figureToNodes = new HashMap<IFigure, Node>();
		//
		// CompoundDirectedGraph graph = new CompoundDirectedGraph();
		// subgraph = new Subgraph(container, null);
		// subgraph.outgoingOffset = 5;
		// subgraph.incomingOffset = 5;
		// subgraph.innerPadding = INNER_PADDING;
		// subgraph.setPadding(PADDING);
		// figureToNodes.put(container, subgraph);
		// graph.nodes.add(subgraph);
		//
		// contributeToGraph(graph, container, figureToNodes);
		// new CompoundDirectedGraphLayout().visit(graph);
		// applyGraphResults(graph, container, figureToNodes);
		// first = false;
		// }

		nodes.clear();
		for (Object object : container.getChildren()) {
			if (object instanceof SCgNodeShape) {
				SCgNodeShape child = (SCgNodeShape) object;
				nodes.add(child);
			}
		}

		step();
	}

	public void validate() {
		IFigure prevRoot = null;
		IFigure nextRoot = root;
		while (nextRoot != null) {
			prevRoot = nextRoot;
			nextRoot = nextRoot.getParent();
		}

		prevRoot.validate();
	}

	@Override
	protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint) {
		container.validate();

		@SuppressWarnings("unchecked")
		List<IFigure> children = container.getChildren();

		Rectangle result = new Rectangle().setLocation(container.getClientArea().getLocation());
		for (int i = 0; i < children.size(); i++)
			result.union(children.get(i).getBounds());
		result.resize(container.getInsets().getWidth(), container.getInsets().getHeight());
		return result.getSize();
	}

}

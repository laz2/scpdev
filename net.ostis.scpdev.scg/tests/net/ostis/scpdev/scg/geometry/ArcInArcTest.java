/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent Systems)
 * For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2011 OSTIS
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import net.ostis.scpdev.scg.model.ConstType;
import net.ostis.scpdev.scg.model.NodeStructType;
import net.ostis.scpdev.scg.model.PosType;
import net.ostis.scpdev.scg.model.SCgIdentity;
import net.ostis.scpdev.scg.model.SCgNode;
import net.ostis.scpdev.scg.model.SCgPair;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Dmitry Lazurkin
 */
public class ArcInArcTest {

	static class Highliter extends MouseMotionListener.Stub {
		public void mouseEntered(MouseEvent me) {
			((IFigure) me.getSource()).setForegroundColor(ColorConstants.lightGray);
		}

		public void mouseExited(MouseEvent me) {
			((IFigure) me.getSource()).setForegroundColor(ColorConstants.black);
		}
	}

	static class Dragger extends MouseMotionListener.Stub implements MouseListener {
		public Dragger(IFigure figure) {
			figure.addMouseMotionListener(this);
			figure.addMouseListener(this);
		}

		Point last;

		public void mouseEntered(MouseEvent me) {
			((IFigure) me.getSource()).setForegroundColor(ColorConstants.lightGray);
		}

		public void mouseExited(MouseEvent me) {
			((IFigure) me.getSource()).setForegroundColor(ColorConstants.black);
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseDoubleClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			last = e.getLocation();
			e.consume();
		}

		public void mouseDragged(MouseEvent e) {
			Point p = e.getLocation();
			Dimension delta = p.getDifference(last);
			last = p;
			Figure f = ((Figure) e.getSource());
			f.setBounds(f.getBounds().getTranslated(delta.width, delta.height));
		}
	}

	public static void main(String args[]) throws IOException {
		Properties props = new Properties();
		props.load(new FileInputStream("log4j.properties"));
		PropertyConfigurator.configure(props);

		Display d = Display.getDefault();
		final Shell shell = new Shell(d, SWT.SHELL_TRIM);
		shell.setBackground(ColorConstants.white);
		shell.setSize(600, 600);
		shell.setText("SCgNodeShape Test");

		LightweightSystem lws = new LightweightSystem(shell);
		Figure contents = new Figure();
		contents.setBackgroundColor(ColorConstants.white);

		SCgNode node1 = new SCgNode(new SCgIdentity("node1"));
		node1.setConstType(ConstType.Const);
		node1.setStructType(NodeStructType.General);
		IFigure node1Figure = new SCgNodeShape(node1);
		new Dragger(node1Figure);
		node1Figure.setBounds(new Rectangle(new Point(30, 30), new Dimension(20, 20)));
		contents.add(node1Figure);

		SCgNode node2 = new SCgNode(new SCgIdentity("node2"));
		node2.setConstType(ConstType.Const);
		node2.setStructType(NodeStructType.General);
		IFigure node2Figure = new SCgNodeShape(node2);
		new Dragger(node2Figure);
		node2Figure.setBounds(new Rectangle(new Point(30, 200), new Dimension(20, 20)));
		contents.add(node2Figure);

		SCgNode attr = new SCgNode(new SCgIdentity("attr_"));
		attr.setConstType(ConstType.Const);
		attr.setStructType(NodeStructType.Attribute);
		IFigure attrFigure = new SCgNodeShape(attr);
		new Dragger(attrFigure);
		attrFigure.setBounds(new Rectangle(new Point(200, 115), new Dimension(20, 20)));
		contents.add(attrFigure);

		SCgPair pair1 = new SCgPair(new SCgIdentity());
		pair1.setPosType(PosType.Positive);
		pair1.setConstType(ConstType.Const);
		SCgPairConnection pair1Conn = new SCgPairConnection(pair1);
		pair1Conn.setSourceAnchor(new ChopboxAnchor(node1Figure));
		pair1Conn.setTargetAnchor(new ChopboxAnchor(node2Figure));
		pair1Conn.setTargetDecoration(new PolygonDecoration());
		pair1Conn.addMouseMotionListener(new Highliter());
		contents.add(pair1Conn);

		SCgPair pair2 = new SCgPair(new SCgIdentity());
		pair2.setPosType(PosType.Positive);
		pair2.setConstType(ConstType.Const);
		SCgPairConnection pair2Conn = new SCgPairConnection(pair2);
		pair2Conn.setSourceAnchor(new ChopboxAnchor(attrFigure));
		pair2Conn.setTargetAnchor(new CenterAnchor(pair1Conn));
		pair2Conn.setTargetDecoration(new PolygonDecoration());
		pair2Conn.addMouseMotionListener(new Highliter());
		contents.add(pair2Conn);

		//new LayoutRunnable(d, (IFigure) contents).init();

		lws.setContents(contents);
		shell.open();
		while (!shell.isDisposed())
			while (!d.readAndDispatch())
				d.sleep();
	}

}

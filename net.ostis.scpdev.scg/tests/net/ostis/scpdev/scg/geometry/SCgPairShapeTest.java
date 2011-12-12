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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.ostis.scpdev.scg.model.SCgNode;
import net.ostis.scpdev.scg.model.SCgObject;
import net.ostis.scpdev.scg.model.SCgPair;
import net.ostis.scpdev.scg.model.SCgRoot;
import net.ostis.scpdev.scg.model.gwf.GwfReader;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Dmitry Lazurkin
 */
public class SCgPairShapeTest {

	private static Map<SCgObject, IFigure> obj2figure = new HashMap<SCgObject, IFigure>();

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
		XYLayout contentsLayout = new XYLayout();
		contents.setLayoutManager(contentsLayout);

		GwfReader reader = new GwfReader(SCgPairShapeTest.class.getClassLoader().getResourceAsStream("arcs.gwf"));
		SCgRoot root = reader.read();

		for (SCgObject object : root.getChildren()) {
			if (object instanceof SCgNode) {
				SCgNode node = (SCgNode) object;
				SCgNodeShape shape = new SCgNodeShape(node);
				contentsLayout.setConstraint(shape, new Rectangle(node.getLocation(), new Dimension(20, 20)));
				contents.add(shape);
				obj2figure.put(node, shape);
			} else if (object instanceof SCgPair) {
				SCgPair pair = (SCgPair) object;
				SCgPairConnection c = new SCgPairConnection(pair);
				ChopboxAnchor sourceAnchor = new ChopboxAnchor(obj2figure.get(pair.getBegin()));
				ChopboxAnchor targetAnchor = new ChopboxAnchor(obj2figure.get(pair.getEnd()));
				c.setSourceAnchor(sourceAnchor);
				c.setTargetAnchor(targetAnchor);
				c.setTargetDecoration(new PolygonDecoration());
				contents.add(c);
			}
		}

		lws.setContents(contents);
		shell.open();
		while (!shell.isDisposed())
			while (!d.readAndDispatch())
				d.sleep();
	}

}

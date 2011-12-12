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

import java.io.IOException;
import java.io.InputStream;

import net.ostis.scpdev.scg.model.SCgNode;
import net.ostis.scpdev.scg.model.SCgObject;
import net.ostis.scpdev.scg.model.SCgRoot;
import net.ostis.scpdev.scg.model.gwf.GwfReader;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Dmitry Lazurkin
 */
public abstract class BaseSCgShapesTest {

	protected abstract InputStream getTestsInputStream();

	public static void main(String[] args) throws IOException {

		Display d = new Display();
		final Shell shell = new Shell(d);
		shell.setSize(400, 400);
		shell.setText("SCgNodeShape Test");

		LightweightSystem lws = new LightweightSystem(shell);
		Figure contents = new Figure();
		XYLayout contentsLayout = new XYLayout();
		contents.setLayoutManager(contentsLayout);

		GwfReader reader = new GwfReader(SCgNodeShapeTest.class.getClassLoader().getResourceAsStream("nodes.gwf"));
		SCgRoot root = reader.read();

		for (SCgObject object : root.getChildren()) {
			SCgNode node = (SCgNode) object;
			SCgNodeShape shape = new SCgNodeShape(node);
			contentsLayout.setConstraint(shape, new Rectangle(node.getLocation(), new Dimension(19,
					19)));
			contents.add(shape);
		}

		lws.setContents(contents);
		shell.open();
		while (!shell.isDisposed())
			while (!d.readAndDispatch())
				d.sleep();
	}

}

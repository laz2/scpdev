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

import net.ostis.scpdev.scg.model.SCgNode;
import net.ostis.scpdev.scg.model.SCgObject;
import net.ostis.scpdev.scg.model.SCgRoot;
import net.ostis.scpdev.scg.model.gwf.GwfReader;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Dmitry Lazurkin
 */
public class SCgNodeShapeTest {

	public static void main(String args[]) throws IOException {
		Display d = Display.getDefault();
		final Shell shell = new Shell(d, SWT.SHELL_TRIM);
		shell.setBackground(ColorConstants.white);
		shell.setSize(500, 500);
		shell.setText("SCgNodeShape Test");

		LightweightSystem lws = new LightweightSystem(shell);
		final Figure contents = new Figure();
		contents.setBackgroundColor(ColorConstants.white);

		GwfReader reader = new GwfReader(SCgNodeShapeTest.class.getClassLoader().getResourceAsStream("nodes.gwf"));
		SCgRoot root = reader.read();

		for (SCgObject object : root.getChildren()) {
			SCgNode node = (SCgNode) object;
			SCgNodeShape shape = new SCgNodeShape(node);
			shape.setBounds(new Rectangle(node.getLocation(), new Dimension(20, 20)));
			contents.add(shape);
		}

		//new LayoutRunnable(d, (IFigure) contents).init();

		lws.setContents(contents);
		shell.open();
		while (!shell.isDisposed())
			while (!d.readAndDispatch()) {
				d.sleep();
			}
	}

}

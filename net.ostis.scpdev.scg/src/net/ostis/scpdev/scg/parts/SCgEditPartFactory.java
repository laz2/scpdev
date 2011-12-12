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
package net.ostis.scpdev.scg.parts;

import net.ostis.scpdev.scg.model.SCgObject;
import net.ostis.scpdev.scg.model.SCgPair;
import net.ostis.scpdev.scg.model.SCgRoot;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * Factory that maps model elements to edit parts.
 *
 * @author Dmitry Lazurkin
 */
public class SCgEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object modelElement) {
		// get EditPart for model element
		EditPart part = getPartForElement(modelElement);
		// store model element in EditPart
		part.setModel(modelElement);
		return part;
	}

	/**
	 * Maps an object to an EditPart.
	 *
	 * @throws RuntimeException if no match was found
	 */
	private EditPart getPartForElement(Object modelElement) {
		if (modelElement instanceof SCgRoot) {
			return new SCgRootEditPart();
		} else if (modelElement instanceof SCgPair) {
			return new SCgPairEditPart();
		} else if (modelElement instanceof SCgObject) {
			return new SCgNodeEditPart();
		} else {
			throw new RuntimeException("Can't create part for model element: "
					+ ((modelElement != null) ? modelElement.getClass().getName() : "null"));
		}
	}

}

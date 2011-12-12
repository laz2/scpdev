/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent
 * Systems) For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2010 OSTIS
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
package net.ostis.scpdev.builder.scg;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Dmitry Lazurkin
 */
public class SCgContour extends SCgObject {

	private boolean has1_ = false;
	private boolean has2_ = false;

	public SCgContour(SCgIdentity identity) {
		super(identity);
	}

	@Override
	protected void encodeImpl(SCsWriter writer) throws IOException {
		String headerComment;
		if (identity.hasIdtf()) {
			headerComment = String.format("<contour idtf=\"%s\">", identity.getIdtf());
		} else {
			headerComment = String.format("<contour id=\"%s\">", systemId);
		}

		writer.comment(headerComment);
		writer.incTab();

		// May be this object is global and already writed
		if (StringUtils.isEmpty(identity.getMainId())) {
			String mainId = writer.node(identity.getIdtf(), SCsWriter.SC_CONST);
			identity.setMainId(mainId);
		}

		if (childs != null) {
			for (SCgObject child : childs) {
				writer.comment("<child>");
				writer.incTab();

				child.encode(writer);

				writer.arc(identity.getMainId(), null, SCsWriter.ARC_CONST_POS, child.getIdentity().getMainId());

				List<String> childIds = child.getGeneratedIds();
				if (childIds != null) {
					for (String childId : childIds) {
						if (childId.equals("1_")) {
							if (has1_) {
								continue;
							} else {
								has1_ = true;
							}
						}

						if (childId.equals("2_")) {
							if (has2_) {
								continue;
							} else {
								has2_ = true;
							}
						}

						writer.arc(identity.getMainId(), null, SCsWriter.ARC_CONST_POS, childId);
					}
				}

				writer.decTab();
				writer.comment("</child>");
			}
		}

		writer.decTab();
		writer.comment("</contour>");
	}

}

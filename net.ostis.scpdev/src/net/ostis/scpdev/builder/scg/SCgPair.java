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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * @author Dmitry Lazurkin
 */
public class SCgPair extends SCgObject {

	public static final String TYPE = "type";
	public static final String BEGIN = "id_b";
	public static final String END = "id_e";

	protected String type;
	protected SCgObject begin;
	protected SCgObject end;

	protected List<String> generatedIds;

	public SCgPair(SCgIdentity sign) {
		super(sign);
	}

	@Override
	public void readState(Element el, Map<String, String> references) {
		super.readState(el, references);
		type = el.getAttribute(TYPE);
		addReference(BEGIN, el, references);
		addReference(END, el, references);
	}

	@Override
	public void setReferences(Map<String, SCgObject> neededReferences) {
		super.setReferences(neededReferences);
		begin = neededReferences.get(BEGIN);
		end = neededReferences.get(END);
	}

	private void encodeArc(SCsWriter writer) throws IOException {
		String textEnd = null;
		if (type.indexOf("const") != -1) {
			textEnd = SCsWriter.CONST_ARC_END;
		} else if (type.indexOf("var") != -1) {
			textEnd = SCsWriter.VAR_ARC_END;
		} else if (type.indexOf("meta") != -1) {
			textEnd = SCsWriter.METAVAR_ARC_END;
		}

		String textBegin = null;
		if (type.indexOf("pos") != -1) {
			textBegin = SCsWriter.POS_ARC_BEG;
		} else if (type.indexOf("neg") != -1) {
			textBegin = SCsWriter.NEG_ARC_BEG;
		} else if (type.indexOf("fuz") != -1) {
			textBegin = SCsWriter.FUZ_ARC_BEG;
		}

		String arcText = textBegin + textEnd;

		String mainId = writer.arc(begin.getIdentity().getMainId(), identity.getIdtf(), arcText, end.getIdentity()
				.getMainId(), true);
		identity.setMainId(mainId);
	}

	private void encodePair(SCsWriter writer) throws IOException {
		String arcText = null;
		int sctype = 0;

		if (type.indexOf("const") != -1) {
			arcText = SCsWriter.ARC_CONST_POS;
			sctype = SCsWriter.SC_CONST;
		} else if (type.indexOf("var") != -1) {
			arcText = SCsWriter.ARC_VAR_POS;
			sctype = SCsWriter.SC_VAR;
		} else if (type.indexOf("meta") != -1) {
			arcText = SCsWriter.ARC_METAVAR_POS;
			sctype = SCsWriter.SC_METAVAR;
		}

		boolean orient = false;
		if (type.indexOf("orient") != -1) {
			generatedIds = new ArrayList<String>(7);
			orient = true;
		} else {
			generatedIds = new ArrayList<String>(3);
		}

		String mainId = writer.node(identity.getMainId(), sctype);

		String arc1 = writer.arc(mainId, null, arcText, begin.getIdentity().getMainId(), true);
		String arc2 = writer.arc(mainId, null, arcText, end.getIdentity().getMainId(), true);

		generatedIds.add(arc1);
		generatedIds.add(arc2);

		if (orient) {
			String arc1attr = writer.arc("1_", null, arcText, arc1, true);
			String arc2attr = writer.arc("2_", null, arcText, arc2, true);

			generatedIds.add(arc1attr);
			generatedIds.add(arc2attr);
			generatedIds.add("1_");
			generatedIds.add("2_");
		}
	}

	@Override
	public List<String> getGeneratedIds() {
		return generatedIds;
	}

	@Override
	protected void encodeImpl(SCsWriter writer) throws IOException {
		writer.comment("<" + type + ">");
		writer.incTab();

		if (begin.isWrited() == false) {
			writer.comment("<begin>");
			writer.incTab();
			begin.encode(writer);
			writer.decTab();
			writer.comment("</begin>");
		}

		if (end.isWrited() == false) {
			writer.comment("<end>");
			writer.incTab();
			end.encode(writer);
			writer.decTab();
			writer.comment("</end>");
		}

		if (type.startsWith("arc")) {
			encodeArc(writer);
		} else if (type.startsWith("pair")) {
			encodePair(writer);
		}

		writer.decTab();
		writer.comment("</" + type + ">");
	}

}

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
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Dmitry Lazurkin
 */
public class SCgNode extends SCgObject {

	public static final String TYPE = "type";
	public static final String CONTENT = "content";

	private static Map<String, String> stypesMap = new HashMap<String, String>();;

	private static Map<String, String> formatMap = new HashMap<String, String>();

	static {
		stypesMap.put("predmet", "stype_ext_obj_abstract");
		stypesMap.put("relation", "stype_bin_orient_norole_rel");
		stypesMap.put("attribute", "stype_bin_orient_role_rel");
		stypesMap.put("nopredmet", "stype_struct");
		stypesMap.put("group", "stype_concept_norel");
		stypesMap.put("predmet", "stype_ext_obj_abstract");

		formatMap.put("x-ms-bmp", "bmp");
	}

	private String type = null;
	private SCgContent content = null;

	public SCgNode(SCgIdentity identity) {
		super(identity);
	}

	public String getType() {
		return type;
	}

	public SCgContent getContent() {
		return content;
	}

	public void readState(Element el, Map<String, String> references) {
		super.readState(el, references);
		type = el.getAttribute(TYPE);

		NodeList contentTags = el.getElementsByTagName("content");
		if (contentTags != null && contentTags.getLength() > 0) {
			content = new SCgContent();
			content.readState((Element) contentTags.item(0), references);
		}
	}

	@Override
	protected void encodeImpl(SCsWriter writer) throws IOException {
		writer.comment("<" + type + ">");
		writer.incTab();

		int sctype = 0;

		if (type.indexOf("const") != -1) {
			sctype = SCsWriter.SC_CONST;
		} else if (type.indexOf("var") != -1) {
			sctype = SCsWriter.SC_VAR;
		} else if (type.indexOf("meta") != -1) {
			sctype = SCsWriter.SC_METAVAR;
		}

		String mainId = writer.node(identity.getIdtf(), sctype);
		identity.setMainId(mainId);

		if (content.getContentType().equals("1") || content.getContentType().equals("2")
				|| content.getContentType().equals("3") || content.getContentType().equals("4")) {
			String format = "unknown";
			String b64 = "";
			String format_a = "";
			String cnt = "";
			if (content.getContentType().equals("4")) {
				format = content.getMimeType();
				int idx = content.getMimeType().lastIndexOf("/");
				if (idx != -1) {
					format = format.substring(idx + 1, format.length() - 1).toUpperCase();
				}

				if (formatMap.containsKey(format))
					format = formatMap.get(format);

				format_a = "=";
				b64 = "b64";
				cnt = content.getContent();
				cnt = cnt.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "");
			} else if (content.getContentType().equals("2")) {
				format = "int";
				format_a = "=n=";
			} else if (content.getContentType().equals("3")) {
				format = "real";
				format_a = "=n=";
			} else if (content.getContentType().equals("1")) {
				format = "string";
				format_a = "=c=";

				cnt = content.getContent();
				cnt = cnt.trim();
				while (cnt.endsWith("\n"))
					cnt = cnt.substring(0, cnt.length() - 2);

				cnt = cnt.replaceAll("\"", "\\\"");
			}

			if (content.getContentType().equals("2") || content.getContentType().equals("3")) {
				cnt = content.getContent().replaceAll("\n", "");
				writer.write(String.format("%s %s %s;\n", mainId, format_a, cnt));
			} else {
				writer.write(String.format("%s %s %s/\"%s\"/;\n", mainId, format_a, b64, cnt));
			}

			writer.arc(String.format("ui_format_%s", format.toLowerCase()), null, SCsWriter.ARC_CONST_POS, mainId);
		}

		for (Map.Entry<String, String> entry : stypesMap.entrySet()) {
			if (type.indexOf(entry.getKey()) != -1) {
				writer.arc(entry.getValue(), null, SCsWriter.ARC_CONST_POS, mainId);
			}
		}

		writer.decTab();
		writer.comment("</" + type + ">");
	}

}

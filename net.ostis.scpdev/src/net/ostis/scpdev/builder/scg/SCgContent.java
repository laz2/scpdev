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

import java.util.Map;

import org.w3c.dom.Element;

/**
 * @author Dmitry Lazurkin
 */
public class SCgContent implements IGwfDeserializable {

	private static final String CONTENT_TYPE = "type";
	private static final String MIME_TYPE = "mime_type";

	private String contentType;
	private String mimeType;
	private String content;

	public String getMimeType() {
		return mimeType;
	}

	public String getContentType() {
		return contentType;
	}

	public String getContent() {
		return content;
	}

	@Override
	public void readState(Element el, Map<String, String> references) {
		contentType = el.getAttribute(CONTENT_TYPE);
		mimeType = el.getAttribute(MIME_TYPE);
		if (!contentType.equals("0"))
			content = el.getFirstChild().getTextContent();
	}

	@Override
	public void setReferences(Map<String, SCgObject> neededReferences) {
		// Content haven't references
	}

}

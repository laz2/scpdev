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
package net.ostis.scpdev.scg.model.gwf;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.ostis.scpdev.scg.model.ISCgReaderWriter;
import net.ostis.scpdev.scg.model.SCgIdentity;
import net.ostis.scpdev.scg.model.SCgNode;
import net.ostis.scpdev.scg.model.SCgObject;
import net.ostis.scpdev.scg.model.SCgPair;
import net.ostis.scpdev.scg.model.SCgRoot;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Dmitry Lazurkin
 */
public class GwfReader implements ISCgReaderWriter {

	private static final Log log = LogFactory.getLog(GwfReader.class);

	/** Map from idtf to SCg-identity. */
	private Map<String, SCgIdentity> idtf2identity = new HashMap<String, SCgIdentity>();

	private static Map<String, Class<? extends SCgObject>> tag2klass = new HashMap<String, Class<? extends SCgObject>>();

	public static final String STATIC_SECTOR_TAG = "staticSector";

	static {
		tag2klass.put("node", SCgNode.class);
		// tag2klass.put("bus", SCgBus.class);
		// tag2klass.put("contour", SCgContour.class);
		tag2klass.put("arc", SCgPair.class);
		tag2klass.put("pair", SCgPair.class);
	}

	private InputStream in;

	public GwfReader(InputStream in) {
		this.in = in;

	}

	@Override
	public SCgRoot read() throws IOException {
		return read(in);
	}

	@Override
	public SCgRoot read(InputStream in) throws IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(in);

			Element root = dom.getDocumentElement();
			Element staticSector = (Element) root.getElementsByTagName(STATIC_SECTOR_TAG).item(0);

			Map<SCgObject, Map<String, String>> object2references = new HashMap<SCgObject, Map<String, String>>();
			Map<String, SCgObject> id2object = new HashMap<String, SCgObject>();
			SCgRoot rootObject = new SCgRoot();

			// Read all SCg objects from GWF file
			for (Map.Entry<String, Class<? extends SCgObject>> entry : tag2klass.entrySet()) {
				//
				// Find constructor for current SCg-object type.
				//
				Constructor<? extends SCgObject> constructor = null;
				try {
					constructor = entry.getValue().getConstructor(SCgIdentity.class);
				} catch (Exception e) {
					log.error("Really unexpected exception", e);
					return null;
				}

				//
				// Iterate over tags of current SCg-object type
				//
				NodeList tags = staticSector.getElementsByTagName(entry.getKey());
				if (tags != null && tags.getLength() > 0) {
					for (int i = 0; i < tags.getLength(); i++) {
						Element el = (Element) tags.item(i);

						String idtf = el.getAttribute("idtf");

						//
						// If idtf is empty then create new SCg-identity without
						// idtf,
						// else search existence SCg-identity or create new with
						// idtf.
						//
						SCgIdentity identity;
						if (StringUtils.isEmpty(idtf)) {
							identity = new SCgIdentity();
						} else {
							identity = idtf2identity.get(idtf);
							if (identity == null) {
								identity = new SCgIdentity(idtf);
								idtf2identity.put(idtf, identity);
							}
						}

						SCgObject object = constructor.newInstance(identity);
						rootObject.addChild(object);
						Map<String, String> references = new HashMap<String, String>();

						// Read SCg-object state from XML
						object.readState(el, references);
						object2references.put(object, references);
						id2object.put(object.getSystemId(), object);
					}
				}
			}

			//
			// Set needed references for SCg objects
			//
			for (Map.Entry<SCgObject, Map<String, String>> entry : object2references.entrySet()) {
				Map<String, String> neededReferences = entry.getValue();
				Map<String, SCgObject> references = new HashMap<String, SCgObject>();

				for (Map.Entry<String, String> ref : neededReferences.entrySet())
					references.put(ref.getKey(), id2object.get(ref.getValue()));

				entry.getKey().setReferences(references);
			}

			return rootObject;
		} catch (Exception e) {
			log.error("Exception while parsing gwf file ", e);
		}

		return null;
	}

}

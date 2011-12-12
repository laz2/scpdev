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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.ostis.scpdev.StatusUtils;
import net.ostis.scpdev.builder.SCsFileBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Builder for files with extension ".scg".
 *
 * @author Dmitry Lazurkin
 */
public class SCgFileBuilder extends SCsFileBuilder {

	private static final Log log = LogFactory.getLog(SCgFileBuilder.class);

	/** List of scg file include lines (each starts with "#include"). */
	private List<String> includes = new LinkedList<String>();

	/** List of gwf files for current scg file. */
	private List<IFile> gwfs = new LinkedList<IFile>();

	private List<SCgObject> localObjects;

	/** Map from idtf to SCg-identity. */
	private Map<String, SCgIdentity> idtf2identity = new HashMap<String, SCgIdentity>();

	private static Map<String, Class<? extends SCgObject>> tag2klass = new HashMap<String, Class<? extends SCgObject>>();

	public static final String STATIC_SECTOR_TAG = "staticSector";

	static {
		tag2klass.put("node", SCgNode.class);
		tag2klass.put("bus", SCgBus.class);
		tag2klass.put("contour", SCgContour.class);
		tag2klass.put("arc", SCgPair.class);
		tag2klass.put("pair", SCgPair.class);
	}

	public SCgFileBuilder(IFolder srcroot, IFile source) {
		super(srcroot, source);
	}

	@Override
	public void buildImpl(IFolder binroot) throws CoreException {
		// 1. Parse scg file
		if (parseSCgFile() == false)
			return;

		File converted = null;

		try {
			try {
				if (log.isDebugEnabled()) {
					IFolder folder = source.getProject().getFolder("converted_scs");
					if (folder.exists() == false)
						folder.create(true, true, null);

					IFile convertedFile = folder.getFile(source.getName() + ".scs");
					converted = convertedFile.getRawLocation().toFile();
				} else {
					converted = File.createTempFile("scg", "builder");
				}

				FileOutputStream out = FileUtils.openOutputStream(converted);
				SCsWriter writer = new SCsWriter(out);
				try {
					// Write includes from SCg-file.
					writer.write("\n");
					for (String include : includes) {
						writer.write(include);
						writer.write("\n");
					}

					for (IFile gwfFile : gwfs) {
						// 2. Parse each gwf file
						if (parseGwfFile(gwfFile.getRawLocation().toFile()) == false) {
							// If parsing for one fails then skip other gwfs
							return;
						}

						writer.comment("////////////////////////////////////////////////");
						writer.comment(String.format("File '%s'", gwfFile.getFullPath().toString()));
						writer.write("\n");

						// 3. Use list 'objects' encode all not writed
						// scg-objects
						encodeToSCs(writer);
					}
				} finally {
					writer.close();
				}

				InputStream errorStream = convertSCsSource(converted.getAbsolutePath(), binroot);
				if (errorStream != null)
					applySCsErrors(source, errorStream);
			} finally {
				if (log.isDebugEnabled()) {
					source.getProject().getFolder("converted_scs").refreshLocal(IResource.DEPTH_INFINITE, null);
				} else {
					if (converted != null) {
						FileUtils.forceDelete(converted);
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CoreException(StatusUtils.makeStatus(IStatus.ERROR, e.getMessage(), e));
		}
	}

	/**
	 * @return true - if success, else - false.
	 */
	private boolean parseSCgFile() throws CoreException {
		IContainer parent = source.getParent();

		boolean success = true;

		try {
			LineIterator iter = FileUtils.lineIterator(new File(source.getRawLocation().toOSString()), source
					.getProject().getDefaultCharset());
			int lineNumber = 0;
			while (iter.hasNext()) {
				lineNumber++;
				String line = iter.nextLine();

				if (line.startsWith("#include")) {
					includes.add(line);
				} else {
					line = line.trim();
					if (line.endsWith(".gwf")) {
						IFile gwfFile = parent.getFile(new Path(line));
						if (gwfFile.exists() == false) {
							success = false;
							addMarker(String.format("GWF File '%s' not found ", line), lineNumber, IStatus.ERROR);
						} else {
							gwfs.add(gwfFile);
						}
					}
				}
			}
		} catch (IOException e) {
			log.error("Error while parse scg file " + source, e);
			throw new CoreException(StatusUtils.makeStatus(IStatus.ERROR, e.getMessage(), e));
		}

		return success;
	}

	/**
	 * @return true - if success, else - false.
	 */
	private boolean parseGwfFile(File gwf) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(gwf);

			Element root = dom.getDocumentElement();
			Element staticSector = (Element) root.getElementsByTagName(STATIC_SECTOR_TAG).item(0);

			Map<SCgObject, Map<String, String>> object2references = new HashMap<SCgObject, Map<String, String>>();
			Map<String, SCgObject> id2object = new HashMap<String, SCgObject>();
			localObjects = new LinkedList<SCgObject>();

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
					return false;
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
						localObjects.add(object);
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

			return true;
		} catch (Exception e) {
			log.error("Exception while parsing gwf file " + gwf, e);
		}

		return false;
	}

	/**
	 * Encode SCg-objects in SCs-form with SCs-writer.
	 */
	private void encodeToSCs(SCsWriter writer) throws IOException {
		for (SCgObject local : localObjects)
			local.encode(writer);
	}

	public static void main(String[] args) throws IOException {
		SCgFileBuilder b = new SCgFileBuilder(null, null);
		b.parseGwfFile(new File("d:\\test1.gwf"));
		b.parseGwfFile(new File("d:\\test2.gwf"));
		// b.encodeToSCs(new File("d:\\test.scs"));
	}

}

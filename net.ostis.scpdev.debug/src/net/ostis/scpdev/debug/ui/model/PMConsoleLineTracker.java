/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent
 * Systems) For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2011 OSTIS
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
package net.ostis.scpdev.debug.ui.model;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.IHyperlink;

/**
 * Add hyperlink for files in Processor Module stack trace.
 *
 * @see org.python.pydev.debug.ui.PythonConsoleLineTracker
 *
 * @author Dmitry Lazurkin
 */
public class PMConsoleLineTracker implements IConsoleLineTracker {

	private static final Log log = LogFactory.getLog(PMConsoleLineTracker.class);

	private IConsole console; // console we are attached to
	/** pattern for detecting error lines */
	private static Pattern errorLinePattern = Pattern.compile("\\t*File \\\"([^\\\"]*)\\\", line (\\d*).*");

	@Override
	public void init(IConsole console) {
		this.console = console;
	}

	@Override
	public void lineAppended(IRegion line) {
		int lineOffset = line.getOffset();
		int lineLength = line.getLength();
		try {
			String text = console.getDocument().get(lineOffset, lineLength);

			Matcher errorMatcher = errorLinePattern.matcher(text);
			if (errorMatcher.matches()) {
				String fileName = errorMatcher.group(1);
				String lineNumber = errorMatcher.group(2);
				int fileStart = 1; // The beginning of the line, "File  "
				// hyperlink if we found something
				if (fileName != null) {
					int num = -1;
					try {
						num = lineNumber != null ? Integer.parseInt(lineNumber) : 0;
					} catch (NumberFormatException e) {
						num = 0;
					}
					IHyperlink link = null;
					File file = new File(fileName);

					IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(file.toURI());
					if (files.length > 0 && files[0].exists()) {
						link = new FileLink(files[0], null, -1, -1, num);
					} else {
						// files outside of the workspace
						// skip
					}
					if (link != null)
						console.addLink(link, lineOffset + fileStart, lineLength - fileStart);
				}
			}
		} catch (BadLocationException e) {
			log.error("Unexpected exceptin", e);
		}
	}

	@Override
	public void dispose() {
	}

}

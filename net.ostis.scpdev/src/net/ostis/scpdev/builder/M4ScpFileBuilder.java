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
package net.ostis.scpdev.builder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import net.ostis.scpdev.ScpdevPlugin;
import net.ostis.scpdev.external.ScCoreModule;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Dmitry Lazurkin
 */
public class M4ScpFileBuilder extends SCsFileBuilder {

    private static final Log log = LogFactory.getLog(M4ScpFileBuilder.class);

    public static final String MARKER_TYPE = "net.ostis.scpdev.m4scp_build_problem";

    public M4ScpFileBuilder(IFolder srcroot, IFile source) {
        super(srcroot, source);
    }

    @Override
    protected String getMarkerType() {
        return MARKER_TYPE;
    }

    @Override
    public void buildImpl(IFolder binroot) throws CoreException {
        String m4 = ScCoreModule.getM4Path();
        String m4scp = ScCoreModule.getM4ScpPath();
        String sourceOsPath = source.getLocation().toOSString();
        String includeOsPath = source.getParent().getLocation().toOSString();

        try {
            Process processM4Scp2SCs = Runtime.getRuntime().exec(
                    String.format("\"%s\" -I\"%s\" \"%s\" \"%s\"", m4, includeOsPath, m4scp, sourceOsPath), null,
                    new File(includeOsPath));

            File converted = File.createTempFile(source.getName(), "");
            try {
                OutputStream out = new FileOutputStream(converted);
                try {
                    IOUtils.copy(processM4Scp2SCs.getInputStream(), out);
                } finally {
                    out.close();
                }

                if (processM4Scp2SCs.waitFor() != 0) {
                    System.err.println(IOUtils.toString(processM4Scp2SCs.getErrorStream()));
                } else {
                    InputStream errorStream = convertSCsSource(converted.getAbsolutePath(), binroot);
                    if (errorStream != null)
                        applySCsErrors(source, errorStream);
                }
            } finally {
                converted.delete();
            }
        } catch (Exception e) {
            log.error("Unexpected exception", e);
            throw new CoreException(new Status(IStatus.ERROR, ScpdevPlugin.PLUGIN_ID, "Unexpected exception", e));
        }
    }

}

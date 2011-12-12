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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ostis.scpdev.ScpdevPlugin;
import net.ostis.scpdev.StatusUtils;
import net.ostis.scpdev.external.ScCoreModule;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Dmitry Lazurkin
 */
public class SCsFileBuilder extends AbstractFileBuilder {

    private static final Log log = LogFactory.getLog(SCsFileBuilder.class);

    public static final String MARKER_TYPE = "net.ostis.scpdev.scs_build_problem";

    /**
     * First error pattern for scs2tgf.
     */
    private static final Pattern ERROR_TYPE_1_PATTERN = Pattern.compile("^(yyerror: .+) at .+ line number (\\d+)$");

    /**
     * Second error pattern for scs2tgf.
     */
    private static final Pattern ERROR_TYPE_2_PATTERN = Pattern.compile("^(rule failed) at file .+ line (\\d+)$");

    public SCsFileBuilder(IFolder srcroot, IFile source) {
        super(srcroot, source);
    }

    @Override
    protected String getMarkerType() {
        return MARKER_TYPE;
    }

    @Override
    public void buildImpl(IFolder binroot) throws CoreException {
        InputStream errorStream = convertSCsSource(source.getRawLocation().toOSString(), binroot);
        if (errorStream != null) {
            try {
                applySCsErrors(source, errorStream);
            } catch (IOException e) {
                throw new CoreException(StatusUtils.makeStatus(IStatus.WARNING, "Error while reading compiler errors",
                        e));
            }
        }
    }

    protected InputStream convertSCsSource(String sourceRawPath, IFolder binroot) throws CoreException {
        String scs2tgf = ScCoreModule.getSCsCompilerPath();
        String includeOsPath = srcroot.getFolder("include").getLocation().toOSString();
        String binOsPath = getRawBinaryPath(binroot, source);

        try {
            Process ps = Runtime.getRuntime()
                    .exec(String.format("\"%s\" -nc -I\"%s\" \"%s\" \"%s\"", scs2tgf, includeOsPath, sourceRawPath,
                            binOsPath), null, new File(source.getParent().getLocation().toOSString()));
            if (ps.waitFor() != 0)
                return ps.getErrorStream();
        } catch (Exception e) {
            log.error("Unexpected exception", e);
            throw new CoreException(new Status(IStatus.ERROR, ScpdevPlugin.PLUGIN_ID, "Unexpected exception", e));
        }

        return null;
    }

    protected void applySCsErrors(IFile source, InputStream errorStream) throws IOException {
        @SuppressWarnings("unchecked")
        List<String> errorLines = IOUtils.readLines(errorStream);
        if (errorLines.isEmpty())
            return;

        if (errorLines.size() != 1) {
            errorLines.remove(errorLines.size() - 1);

            for (String errorLine : errorLines) {
                Matcher matcher = ERROR_TYPE_1_PATTERN.matcher(errorLine);
                if (matcher.matches()) {
                    addMarker(matcher.group(1), Integer.parseInt(matcher.group(2)), IMarker.SEVERITY_ERROR);
                } else {
                    matcher = ERROR_TYPE_2_PATTERN.matcher(errorLine);
                    if (matcher.matches()) {
                        addMarker(matcher.group(1), Integer.parseInt(matcher.group(2)), IMarker.SEVERITY_ERROR);
                    } else {
                        addMarker(errorLine, 1, IMarker.SEVERITY_ERROR);
                    }
                }
            }
        } else {
            addMarker(errorLines.get(0), 1, IMarker.SEVERITY_ERROR);
        }
    }

}

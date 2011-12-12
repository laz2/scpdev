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

import net.ostis.scpdev.util.ResourceHelper;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * @author Dmitry Lazurkin
 */
public abstract class AbstractElementBuilder implements IElementBuilder {

    protected final IFolder srcroot;

    public AbstractElementBuilder(IFolder srcroot) {
        this.srcroot = srcroot;
    }

    protected String getMarkerType() {
        return ScRepositoryBuilder.BUILD_PROBLEM_MARKER;
    }

    protected void addMarker(String message, int lineNumber, int severity) {
        ResourceHelper.addMarker(getResource(), getMarkerType(), message, lineNumber, severity);
    }

    protected void deleteMarkers() {
        try {
            getResource().deleteMarkers(getMarkerType(), false, IResource.DEPTH_ZERO);
        } catch (CoreException ce) {
        }
    }

    public String getRawBinaryPath(IFolder binroot, IResource source) {
        return getRawBinaryPath(srcroot, binroot, source);
    }

    public static String getRawBinaryPath(IFolder srcroot, IFolder binroot, IResource source) {
        String rawBinaryPath = binroot.getLocation().toOSString()
                + source.getLocation().toOSString().substring(srcroot.getLocation().toOSString().length());

        if (StringUtils.isNotEmpty(source.getFileExtension()))
            rawBinaryPath = rawBinaryPath.substring(0, rawBinaryPath.length() - source.getFileExtension().length() - 1);

        return rawBinaryPath;
    }

    public static IResource getBinaryResource(IFolder srcroot, IFolder binroot, IResource source) {
        IPath binaryRelative = source.getFullPath().makeRelativeTo(srcroot.getFullPath()).removeFileExtension();

        if (source instanceof IFile) {
            return binroot.getFile(binaryRelative);
        } else if (source instanceof IFolder) {
            return binroot.getFolder(binaryRelative);
        } else {
            throw new RuntimeException("Not handled source type " + source);
        }
    }

}

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
package net.ostis.scpdev.util;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Dmitry Lazurkin
 */
public class ResourceHelper {

    /**
     * Recursively creates a folder path.
     *
     * @see java.io.File#mkdirs()
     */
    public static void createFolderHelper(IFolder folder, IProgressMonitor monitor) throws CoreException {
        if (folder.exists() == false) {
            IContainer parent = folder.getParent();
            if (parent instanceof IFolder && (!((IFolder) parent).exists()))
                createFolderHelper((IFolder) parent, monitor);
            folder.create(false, true, monitor);
        }
    }

    /**
     * Add marker to resource. Suppress exceptions.
     */
    public static void addMarker(IResource resource, String markerType, String message, int lineNumber, int severity) {
        try {
            IMarker marker = resource.createMarker(markerType);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.SEVERITY, severity);
            if (lineNumber == -1)
                lineNumber = 1;
            marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
        } catch (CoreException e) {
        }
    }

    /**
     * Add marker to first line of resource. Suppress exceptions.
     */
    public static void addMarker(IResource resource, String markerType, String message, int severity) {
        addMarker(resource, markerType, message, 1, severity);
    }

    /**
     * Delete markers of specified type from resource. Suppress exceptions.
     */
    public static void deleteMarkers(IResource resource, String markerType) {
        try {
            resource.deleteMarkers(markerType, false, IResource.DEPTH_ZERO);
        } catch (CoreException ce) {
        }
    }

    public static void refreshLocal(IResource resource, int depth) {
        try {
            resource.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException e) {
            // suppress
        }
    }

}

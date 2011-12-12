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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * @author Dmitry Lazurkin
 */
public abstract class AbstractFileBuilder extends AbstractElementBuilder {

    protected final IFile source;

    public AbstractFileBuilder(IFolder srcroot, IFile source) {
        super(srcroot);
        this.source = source;
    }

    protected abstract void buildImpl(IFolder binroot)  throws CoreException;

    @Override
    public void build(IFolder binroot) throws CoreException {
        String parentBinPath = getRawBinaryPath(binroot, source.getParent());
        IFolder bindir = binroot.getFolder(new Path(parentBinPath).makeRelativeTo(binroot.getLocation()));
        ResourceHelper.createFolderHelper(bindir, null);

        deleteMarkers();
        buildImpl(binroot);
    }

    @Override
    public IResource getResource() {
        return source;
    }

}

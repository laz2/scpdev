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
package net.ostis.scpdev.editors;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * In memory storage implementation. Hold and return {@link InputStream
 * InputStream} instance.
 *
 * @author Dmitry Lazurkin
 */
public class InMemoryStorage implements IStorage {

    private String data;

    /**
     * @param data Hold and return this input stream
     */
    public InMemoryStorage(String data) {
        this.data = data;
    }

    @Override
    public InputStream getContents() throws CoreException {
        return new ByteArrayInputStream(data.getBytes());
    }

    @Override
    public IPath getFullPath() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

}

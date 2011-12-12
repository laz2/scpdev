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
package net.ostis.scpdev.internal.core.model.repo;

import org.apache.commons.lang3.Validate;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import net.ostis.scpdev.core.model.repo.IRepoEntry;
import net.ostis.scpdev.core.model.repo.ISCRepository;

/**
 * Common implementation of {@link IRepoEntry}.
 *
 * @author Dmitry Lazurkin
 */
public abstract class AbstractRepoEntry implements IRepoEntry {

	protected ISCRepository repository;
	protected final IRepoEntry parent;
	protected final IResource resource;

	public AbstractRepoEntry(IRepoEntry parent, IResource resource) {
		Validate.notNull(resource);
		if (parent != null)
			this.repository = parent.getRepository();
		this.parent = parent;
		this.resource = resource;
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.core.model.repo.IRepoEntry#exists()
	 */
	@Override
	public boolean exists() {
		return resource.exists();
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.core.model.repo.IRepoEntry#getParent()
	 */
	@Override
	public IRepoEntry getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.core.model.repo.IRepoEntry#getResource()
	 */
	@Override
	public IResource getResource() {
		return resource;
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.core.model.repo.IRepoEntry#getPath()
	 */
	@Override
	public IPath getPath() {
		return resource.getFullPath();
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.core.model.repo.IRepoEntry#getConverted()
	 */
	@Override
	public IRepoEntry getConverted() {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.core.model.repo.IRepoEntry#getRepository()
	 */
	@Override
	public ISCRepository getRepository() {
		return repository;
	}

	public void setRepository(ISCRepository repository) {
		this.repository = repository;
	}

}

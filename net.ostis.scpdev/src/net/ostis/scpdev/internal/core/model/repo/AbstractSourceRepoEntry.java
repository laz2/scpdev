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

import net.ostis.scpdev.core.model.repo.IRepoEntry;

import org.eclipse.core.resources.IResource;

/**
 * Common implementation of {@link ISourceRepoEntry}.
 *
 * @author Dmitry Lazurkin
 */
public abstract class AbstractSourceRepoEntry extends AbstractRepoEntry{

	protected IRepoEntry binaryEntry = null;

	public AbstractSourceRepoEntry(IRepoEntry parent, IResource resource) {
		super(parent, resource);
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.core.model.repo.IRepoEntry#getConverted()
	 */
	@Override
	public IRepoEntry getConverted() {
		return binaryEntry;
	}

}

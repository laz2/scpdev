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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import net.ostis.scpdev.core.model.repo.IParent;
import net.ostis.scpdev.core.model.repo.IParentRepoEntry;
import net.ostis.scpdev.core.model.repo.IRepoEntry;

/**
 * Common implementation of {@link IRepoEntry}, which implements {@link IParent}.
 *
 * @author Dmitry Lazurkin
 */
public abstract class AbstractParentRepoEntry extends AbstractRepoEntry implements IParentRepoEntry {

	protected final List<IRepoEntry> children = new ArrayList<IRepoEntry>();
	protected final List<IRepoEntry> unmodifiableChildren = Collections.unmodifiableList(children);

	public AbstractParentRepoEntry(IRepoEntry parent, IResource resource) {
		super(parent, resource);
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.core.model.repo.IParent#getChildren()
	 */
	@Override
	public List<IRepoEntry> getChildren() throws CoreException {
		return unmodifiableChildren;
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.core.model.repo.IParent#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return !unmodifiableChildren.isEmpty();
	}

	public void addChild(IRepoEntry child) {
		children.add(child);
	}

	public void removeChild(IRepoEntry child) {
		children.remove(child);
	}

}

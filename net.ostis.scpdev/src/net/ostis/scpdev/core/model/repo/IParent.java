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
package net.ostis.scpdev.core.model.repo;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * Common protocol for sc-repository entries elements that contain other sc-repository entries.
 *
 * @author Dmitry Lazurkin
 */
public interface IParent {

	/**
	 * Return the immediate children of this entry.
	 * The children are in no particular order.
	 * List is unmodifiable.
	 */
	public List<IRepoEntry> getChildren() throws CoreException;

	/**
	 * Return whether this entry has one or more immediate children.
	 * This is a convenience method, and may be more efficient than
	 * testing whether <code>getChildren</code> returns an empty array.
	 */
	public boolean hasChildren();
}

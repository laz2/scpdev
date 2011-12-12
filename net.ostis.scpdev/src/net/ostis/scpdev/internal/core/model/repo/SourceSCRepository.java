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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.ostis.scpdev.core.model.repo.IParentRepoEntry;
import net.ostis.scpdev.core.model.repo.IRepoEntry;
import net.ostis.scpdev.core.model.repo.ISCRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * @see ISCRepository
 * @author Dmitry Lazurkin
 */
public class SourceSCRepository implements ISCRepository {

	private static final Log log = LogFactory.getLog(SourceSCRepository.class);

	private final SourceFolder root;

	private static Map<String, Class<? extends IRepoEntry>> modelRegistry = new HashMap<String, Class<? extends IRepoEntry>>();

	private Map<IResource, IRepoEntry> cache = new TreeMap<IResource, IRepoEntry>();

	static {
		// modelRegistry.put("scs", value);
		// addFileBuilder(, SCsFileBuilder.class);
		// addFileBuilder("scg", SCgFileBuilder.class);
		// addFileBuilder("m4scp", M4ScpFileBuilder.class);
	}

	public SourceSCRepository(IFolder root) throws CoreException {
		CollectModelVisitor visitor = new CollectModelVisitor(root);
		root.accept(visitor);
		this.root = visitor.getModelRoot();
	}

	/* (non-Javadoc)
	 * @see net.ostis.scpdev.core.model.repo.ISourceSCRepository#getRoot()
	 */
	@Override
	public final IParentRepoEntry getRoot() {
		return root;
	}

	private IRepoEntry getSourceFileModel(IRepoEntry parentModel, IFile source) {
		Class<? extends IRepoEntry> klass = modelRegistry.get(source.getFileExtension());

		if (klass != null) {
			try {
				Constructor<? extends IRepoEntry> constructor = klass.getConstructor(IRepoEntry.class, IResource.class);
				return constructor.newInstance(parentModel, source);
			} catch (Exception e) {
				log.error(klass + " haven't constructor thats receive IRepoEntry and IResource", e);
			}
		}

		return null;
	}

	private class CollectModelVisitor implements IResourceVisitor {

		private final IFolder root;

		private SourceFolder modelRoot = null;

		public CollectModelVisitor(IFolder root) {
			this.root = root;

			modelRoot = new SourceFolder(null, root);
			modelRoot.setRepository(SourceSCRepository.this);

			cache.clear();
			cache.put(root, modelRoot);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
		 */
		@Override
		public boolean visit(IResource resource) throws CoreException {
			if (resource.isAccessible()) {

				AbstractParentRepoEntry parentModel = (AbstractParentRepoEntry) cache.get(resource.getParent());

				if (resource instanceof IFolder) {
					if (!resource.equals(root)) {
						IFolder source = (IFolder) resource;

						SourceFolder model = new SourceFolder(parentModel, source);
						parentModel.addChild(model);
						cache.put(source, model);
					}
				} else if (resource instanceof IFile) {
					IFile source = (IFile) resource;
					IRepoEntry model = getSourceFileModel(parentModel, source);
					parentModel.addChild(model);
					cache.put(source, model);
				}

				return true;
			} else {
				return false;
			}
		}

		public final SourceFolder getModelRoot() {
			return modelRoot;
		}

	}

}

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ostis.scpdev.ScpdevPlugin;
import net.ostis.scpdev.builder.scg.SCgFileBuilder;
import net.ostis.scpdev.core.ScNature;
import net.ostis.scpdev.external.ScCoreModule;
import net.ostis.scpdev.util.ResourceHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

/**
 * Builder of SC source repository.
 *
 * @author Dmitry Lazurkin
 */
public class ScRepositoryBuilder extends IncrementalProjectBuilder {

    public static final String BUILDER_ID = "net.ostis.scpdev.sc_rbuilder";

    public static final String BUILD_PROBLEM_MARKER = "net.ostis.scpdev.build_problem";

    private static final Log log = LogFactory.getLog(ScRepositoryBuilder.class);

    private List<IElementBuilder> buildQueue;

    private static Map<String, Class<? extends IElementBuilder>> buildersRegistry = new HashMap<String, Class<? extends IElementBuilder>>();

    private static Set<Pattern> skipPatterns = new HashSet<Pattern>();

    private static Set<String> skipExtensions = new HashSet<String>();

    private static Set<Path> specialFolders = new HashSet<Path>();

    static {
        addFileBuilder("scs", SCsFileBuilder.class);
        addFileBuilder("scg", SCgFileBuilder.class);
        addFileBuilder("m4scp", M4ScpFileBuilder.class);

        specialFolders.add(new Path("tmp"));
        specialFolders.add(new Path("proc"));

        skipExtensions.add("gwf");
        skipExtensions.add("scsy");

        skipPatterns.add(Pattern.compile("^\\..+$")); // Starts with dot
    }

    public static synchronized void addFileBuilder(String type, Class<? extends IElementBuilder> builderKlass) {
        if (log.isDebugEnabled())
            log.debug("Register builder '" + builderKlass + "' for extension '" + type + "'");
        buildersRegistry.put(type, builderKlass);
    }

    protected void checkCancel(IProgressMonitor monitor) {
        if (monitor.isCanceled()) {
            forgetLastBuiltState();// not always necessary
            throw new OperationCanceledException();
        }
    }

    private boolean addFileBuilder(IFolder srcroot, IFile source) {
        Class<? extends IElementBuilder> klass = buildersRegistry.get(source.getFileExtension());

        boolean result = false;

        if (klass != null) {
            try {
                Constructor<? extends IElementBuilder> constructor = klass.getConstructor(IFolder.class, IFile.class);
                buildQueue.add(constructor.newInstance(srcroot, source));
                result = true;
            } catch (Exception e) {
                log.error(klass + " haven't constructor thats receive IFolder and IResource", e);
            }
        } else {
            if (skipExtensions.contains(source.getFileExtension()) == false)
                ResourceHelper.addMarker(source, BUILD_PROBLEM_MARKER,
                        "File type isn't handled, but why file in source repository tree?", IMarker.SEVERITY_WARNING);
        }

        return result;
    }

    /**
     * Create META-file in binary folder.
     */
    private void createMetaFile(IFolder binary) throws CoreException {
        IFile meta = binary.getFile("META");

        if (log.isDebugEnabled())
            log.debug("Create META for " + binary);

        String scs2tgf = ScCoreModule.getSCsCompilerPath();

        try {
            Process ps = Runtime.getRuntime().exec(
                    String.format("\"%s\" -nc - \"%s\"", scs2tgf, meta.getLocation().toOSString()));

            BufferedWriter outStream2 = new BufferedWriter(new OutputStreamWriter(ps.getOutputStream()));
            outStream2.write("\"/info/dirent\" = {META={}");

            // TODO: remove bad solution with filesystem raw working
            File binaryFolder = binary.getRawLocation().toFile();
            for (String n : binaryFolder.list()) {
                outStream2.write(",\"" + n + "\"={}");
            }

            outStream2.write("};");
            outStream2.close();

            if (ps.waitFor() != 0) {
                System.err.println(IOUtils.toString(ps.getErrorStream()));
            }
        } catch (Exception e) {
            String msg = "Error while creating META-file for binary folder " + binary;
            log.error(msg, e);
            throw new CoreException(new Status(IStatus.ERROR, ScpdevPlugin.PLUGIN_ID, msg, e));
        }
    }

    /**
     * @return if resource not for build - true, else - false.
     */
    private boolean isSkipedResource(IResource resource) {
        String name = resource.getName();

        if (skipExtensions.contains(resource.getFileExtension()))
            return true;

        for (Pattern p : skipPatterns) {
            Matcher matcher = p.matcher(name);
            if (matcher.matches())
                return true;
        }

        return false;
    }

    public class CollectForBuildAllVisitor implements IResourceVisitor {

        private final IFolder srcroot;

        public CollectForBuildAllVisitor(IFolder srcroot) {
            this.srcroot = srcroot;
        }

        @Override
        public boolean visit(IResource resource) throws CoreException {
            if (resource.isAccessible()) {
                boolean skipped = isSkipedResource(resource);

                if (skipped)
                    return false;

                if (resource instanceof IFile) {
                    IFile source = (IFile) resource;
                    addFileBuilder(srcroot, source);
                }

                return true;
            } else {
                return false;
            }
        }

    }

    private class CollectForBuildDeltaVisitor implements IResourceDeltaVisitor {

        private final IFolder srcroot;

        private final IFolder binroot;

        public CollectForBuildDeltaVisitor(IFolder srcroot, IFolder binroot) {
            this.srcroot = srcroot;
            this.binroot = binroot;
        }

        @Override
        public boolean visit(IResourceDelta delta) throws CoreException {
            boolean skipped = isSkipedResource(delta.getResource());

            if (log.isDebugEnabled())
                log.debug("Resource is changed : " + delta.getResource());

            if (skipped)
                return false;

            switch (delta.getKind()) {
            case IResourceDelta.ADDED: {
                IResource addedResource = delta.getResource();

                if (addedResource instanceof IFile) {
                    IFile source = (IFile) addedResource;
                    addFileBuilder(srcroot, source);
                }

                break;
            }
            case IResourceDelta.REMOVED: {
                String rawBinaryPath = AbstractElementBuilder.getRawBinaryPath(srcroot, binroot, delta.getResource());
                try {
                    FileUtils.forceDelete(new File(rawBinaryPath));
                } catch (IOException e) {
                    log.error("Source resource " + delta.getResource()
                            + " was deleted. Catch exception while deleting binary resource " + rawBinaryPath, e);
                }
                // TODO: remove all empty parent binary folders
                break;
            }
            case IResourceDelta.CHANGED: {
                IResource changed = delta.getResource();

                if (delta.getFlags() == IResourceDelta.CONTENT) {
                    addFileBuilder(srcroot, (IFile) changed);
                }

                break;
            }
            }

            return true;
        }

    }

    /**
     * If at least one scsy is modified then return true else - false.
     */
    private boolean isIncludesChanged(IFolder binroot, IResourceDelta delta) {
        final long binrootTimeStamp = binroot.getLocalTimeStamp();

        try {
            delta.accept(new IResourceDeltaVisitor() {
                @Override
                public boolean visit(IResourceDelta delta) throws CoreException {
                    IResource resource = delta.getResource();
                    String ext = resource.getFileExtension();

                    if (ext != null && ext.equals("scsy")) {
                        if (delta.getResource().getLocalTimeStamp() >= binrootTimeStamp) {
                            if (log.isDebugEnabled())
                                log.debug("Include " + delta.getResource() + " is changed. Forcing rebuild.");
                            throw new RuntimeException("Include changed, force rebuild");
                        }
                    }

                    return !isSkipedResource(resource);
                }
            });
        } catch (Exception e) {
            return true;
        }

        return false;
    }

    /**
     * Create special folder in binary repository with specified path.
     */
    private void createSpecialFolder(IFolder binroot, IPath path) throws CoreException {
        IFolder folder = binroot.getFolder(path);
        if (folder.exists() == false)
            folder.create(true, true, null);
    }

    /**
     * Create all registred special folders in binary repository.
     */
    private void createSpecialFolders(IFolder binroot) throws CoreException {
        for (Path p : specialFolders)
            createSpecialFolder(binroot, p);
    }

    @SuppressWarnings("rawtypes")
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        buildQueue = new LinkedList<IElementBuilder>();

        ScNature scnature = ScNature.getScNature(getProject());

        IFolder srcroot = scnature.getSourceRoot();
        IFolder binroot = scnature.getBinaryRootAndCreate();

        if (kind == FULL_BUILD) {
            fullBuild(srcroot, binroot, monitor);
        } else {
            IResourceDelta delta = getDelta(getProject());
            if (delta == null || isIncludesChanged(binroot, delta)) {
                fullBuild(srcroot, binroot, monitor);
            } else {
                if (delta.getResource().equals(getProject())) {
                    IResourceDelta srcrootDelta = delta.findMember(srcroot.getProjectRelativePath());
                    if (srcrootDelta != null)
                        incrementalBuild(srcroot, binroot, srcrootDelta, monitor);
                }
            }
        }

        return null;
    }

    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException {
        IFolder binRoot = getProject().getFolder("fs_repo");
        if (binRoot.exists())
            binRoot.delete(true, monitor);
    }

    /**
     * Evaluate all build goals from build queue.
     */
    private void evaluateBuilders(IFolder srcroot, IFolder binroot, final IProgressMonitor monitor)
            throws CoreException {
        try {
            monitor.beginTask("Building source repository", buildQueue.size());

            while (buildQueue.isEmpty() != true) {
                IElementBuilder curBuilder = buildQueue.remove(0);
                monitor.setTaskName("Building " + curBuilder.getResource().toString());

                checkCancel(monitor);

                curBuilder.build(binroot);
                monitor.worked(1);
            }

            binroot.refreshLocal(IResource.DEPTH_INFINITE, monitor);
        } finally {
            monitor.done();
        }
    }

    /**
     * Create META-file for folders in binary repository.
     */
    private class MetaCreatorVisitor implements IResourceVisitor {

        private boolean fullBuild;

        /**
         * @param fullBuild true - if need full rebuild of META-files.
         */
        public MetaCreatorVisitor(boolean fullBuild) {
            this.fullBuild = fullBuild;
        }

        @Override
        public boolean visit(IResource resource) throws CoreException {
            if (resource.isAccessible()) {
                if (resource instanceof IFolder) {
                    IFolder binary = (IFolder) resource;
                    if (fullBuild) {
                        createMetaFile(binary);
                    } else {
                        IFile metaFile = binary.getFile("META");
                        // If META-file doesn't exist or it older then binary
                        // folder, then recreate META-file
                        if (metaFile.exists() == false
                                || binary.getRawLocation().toFile().lastModified() > metaFile.getRawLocation().toFile()
                                        .lastModified()) {
                            createMetaFile(binary);
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    protected void fullBuild(IFolder srcroot, IFolder binroot, final IProgressMonitor monitor) throws CoreException {
        // Collect all goals for build.
        srcroot.accept(new CollectForBuildAllVisitor(srcroot));
        evaluateBuilders(srcroot, binroot, monitor);
        // Create META-file for all binary folders.
        binroot.accept(new MetaCreatorVisitor(true));
        binroot.refreshLocal(IResource.DEPTH_INFINITE, monitor);
        createSpecialFolders(binroot);
    }

    protected void incrementalBuild(IFolder srcroot, IFolder binroot, IResourceDelta delta, IProgressMonitor monitor)
            throws CoreException {
        // Collect all goals for build.
        delta.accept(new CollectForBuildDeltaVisitor(srcroot, binroot));
        evaluateBuilders(srcroot, binroot, monitor);
        // Create META-file only for changed binary folders.
        binroot.accept(new MetaCreatorVisitor(false));
        binroot.refreshLocal(IResource.DEPTH_INFINITE, monitor);
        createSpecialFolders(binroot);
    }

}

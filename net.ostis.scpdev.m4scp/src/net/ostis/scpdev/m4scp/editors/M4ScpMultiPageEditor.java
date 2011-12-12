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
package net.ostis.scpdev.m4scp.editors;

import java.io.ByteArrayOutputStream;

import net.ostis.scpdev.builder.M4ScpFileBuilder;
import net.ostis.scpdev.editors.IScTextEditor;
import net.ostis.scpdev.editors.InMemoryStorage;
import net.ostis.scpdev.editors.StorageEditorInput;
import net.ostis.scpdev.editors.scs.SCsDocumentProvider;
import net.ostis.scpdev.editors.scs.SCsSourceEditor;
import net.ostis.scpdev.external.ScCoreModule;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

/**
 * An example showing how to create a multi-page editor. This example has 3
 * pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * </ul>
 *
 * @author Dmitry Lazurkin
 */
public class M4ScpMultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener, IGotoMarker, IScTextEditor {

    /** The m4scp text editor used in page 0. */
    private M4ScpSourceEditor m4scpEditor;

    /** The scs text editor used in page 1. */
    private TextEditor scsEditor;

    public M4ScpMultiPageEditor() {
        super();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    @Override
    public ISourceViewer getScSourceViewer() {
        return m4scpEditor.getScSourceViewer();
    }

    private class InternalM4ScpTextEditor extends M4ScpSourceEditor {

        public InternalM4ScpTextEditor() {
            setDocumentProvider(new SCsDocumentProvider() {
                @Override
                protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
                    if (element instanceof IFileEditorInput) {
                        IFileEditorInput input = (IFileEditorInput) element;

                        return new ResourceMarkerAnnotationModel(input.getFile()) {
                            @Override
                            protected Position createPositionFromMarker(IMarker marker) {
                                String markerType = MarkerUtilities.getMarkerType(marker);
                                Position position = super.createPositionFromMarker(marker);

                                if (position != null)
                                    if (M4ScpFileBuilder.MARKER_TYPE.equals(markerType))
                                        position.setOffset(1);

                                return position;
                            }
                        };
                    }

                    return super.createAnnotationModel(element);
                }
            });
        }

    }

    void createPage0() {
        try {
            m4scpEditor = new InternalM4ScpTextEditor();
            int index = addPage(m4scpEditor, getEditorInput());
            setPageText(index, "M4SCP source code");
        } catch (PartInitException e) {
            ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
        }
    }

    private class ConvertedSCsEditor extends SCsSourceEditor {

        public ConvertedSCsEditor() {
            setDocumentProvider(new SCsDocumentProvider() {
                @Override
                protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
                    return super.createAnnotationModel(M4ScpMultiPageEditor.this.getEditorInput());
                }
            });
        }

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public boolean isSaveAsAllowed() {
            return false;
        }

    }

    void createPage1() {
        try {
            scsEditor = new ConvertedSCsEditor();
            int index = addPage(scsEditor, getEditorInput());
            setPageText(index, "Generated SCs code");
        } catch (PartInitException e) {
            ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
        }
    }

    protected void createPages() {
        createPage0();
        createPage1();
    }

    /**
     * The <code>MultiPageEditorPart</code> implementation of this
     * <code>IWorkbenchPart</code> method disposes all nested editors.
     * Subclasses may extend.
     */
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        super.dispose();
    }

    /**
     * Saves the multi-page editor's document.
     */
    public void doSave(IProgressMonitor monitor) {
        getEditor(0).doSave(monitor);
    }

    /**
     * Saves the multi-page editor's document as another file. Also updates the
     * text for page 0's tab, and updates this multi-page editor's input to
     * correspond to the nested editor's.
     */
    public void doSaveAs() {
        IEditorPart editor = getEditor(0);
        editor.doSaveAs();
        setPageText(0, editor.getTitle());
        setInput(editor.getEditorInput());
    }

    @Override
    public void gotoMarker(IMarker marker) {
        setActivePage(1);
        IDE.gotoMarker(getEditor(1), marker);
    }

    /**
     * The <code>MultiPageEditorExample</code> implementation of this method
     * checks that the input is an instance of <code>IFileEditorInput</code>.
     */
    public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
        if (!(editorInput instanceof IFileEditorInput))
            throw new PartInitException("Invalid Input: Must be IFileEditorInput");
        setPartName(editorInput.getName());
        super.init(site, editorInput);
    }

    public boolean isSaveAsAllowed() {
        return true;
    }

    /**
     * Calculates the contents of page 1 when the it is activated.
     */
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);

        if (newPageIndex == 1) {
            Validate.isInstanceOf(IFileEditorInput.class, m4scpEditor.getEditorInput());
            final IFileEditorInput m4scpInput = (IFileEditorInput) m4scpEditor.getEditorInput();
            final IFile m4scpSource = m4scpInput.getFile();

            final ByteArrayOutputStream out = new ByteArrayOutputStream();

            final Job convertJob = new Job("Convert m4scp to scs") {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    String m4 = ScCoreModule.getM4Path();
                    String m4scp = ScCoreModule.getM4ScpPath();

                    ProcessBuilder psb = new ProcessBuilder(m4, m4scp, m4scpSource.getRawLocation().toOSString());

                    try {
                        Process ps = psb.start();
                        IOUtils.copy(ps.getInputStream(), out);

                        if (ps.waitFor() == 0)
                            return Status.OK_STATUS;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return Status.CANCEL_STATUS;
                }
            };

            convertJob.setPriority(Job.INTERACTIVE);
            convertJob.schedule();

            try {
                convertJob.join();
                if (convertJob.getResult() == Status.OK_STATUS)
                    scsEditor.setInput(new StorageEditorInput(new InMemoryStorage(out.toString())));
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes all project files on project close.
     */
    public void resourceChanged(final IResourceChangeEvent event) {
        if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
                    for (int i = 0; i < pages.length; i++) {
                        if (((FileEditorInput) m4scpEditor.getEditorInput()).getFile().getProject()
                                .equals(event.getResource())) {
                            IEditorPart editorPart = pages[i].findEditor(m4scpEditor.getEditorInput());
                            pages[i].closeEditor(editorPart, true);
                        }
                    }
                }
            });
        }
    }

}

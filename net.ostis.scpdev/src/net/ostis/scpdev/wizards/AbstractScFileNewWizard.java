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
package net.ostis.scpdev.wizards;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;

import net.ostis.scpdev.ScpdevPlugin;
import net.ostis.scpdev.editors.IScTextEditor;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * Base class for SC-file wizards.
 *
 * @author Dmitry Lazurkin
 */
public abstract class AbstractScFileNewWizard extends Wizard implements INewWizard {

    private AbstractScFileNewWizardPage filePage;

    protected IStructuredSelection selection;

    protected IWorkbench workbench;

    public AbstractScFileNewWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    public void addPages() {
        filePage = createFilePage();
        addPage(filePage);
    }

    protected abstract AbstractScFileNewWizardPage createFilePage();

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    public boolean performFinish() {
        final String containerName = filePage.getSourceFolderName();
        final String fileName = filePage.getFileName();
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    doFinish(containerName, fileName, monitor);
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            getContainer().run(true, false, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }

    private void doFinish(String containerName, String fileName, IProgressMonitor monitor) throws CoreException {
        // create a sample file
        monitor.beginTask("Creating " + fileName, 2);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(new Path(containerName));
        if (!resource.exists() || !(resource instanceof IContainer)) {
            throwCoreException("Source folder \"" + containerName + "\" does not exist.");
        }
        IContainer container = (IContainer) resource;
        final IFile file = container.getFile(new Path(fileName));
        if (!file.exists()) {
            file.create(new ByteArrayInputStream(new byte[0]), true, monitor);
        }
        monitor.worked(1);
        monitor.setTaskName("Opening file for editing...");
        getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                    IEditorPart editor = IDE.openEditor(page, file, true);

                    IScTextEditor textEditor = (IScTextEditor) editor;

                    TemplatePersistenceData selectedTemplate = filePage.getSelectedTemplate();
                    if (selectedTemplate == null) {
                        return; // no template selected, nothing to apply!
                    }

                    Template template = selectedTemplate.getTemplate();

                    ISourceViewer viewer = textEditor.getScSourceViewer();

                    DocumentTemplateContext context = new DocumentTemplateContext(filePage.getContextTypeRegistry()
                            .getContextType(template.getContextTypeId()), viewer.getDocument(), 0, 0);

                    TemplateProposal templateProposal = new TemplateProposal(template, context, new Region(0, 0), null);
                    templateProposal.apply(textEditor.getScSourceViewer(), '\n', 0, 0);

                } catch (PartInitException e) {
                }
            }
        });
        monitor.worked(1);
    }

    private void throwCoreException(String message) throws CoreException {
        IStatus status = new Status(IStatus.ERROR, ScpdevPlugin.PLUGIN_ID, IStatus.OK, message, null);
        throw new CoreException(status);
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
    }

}

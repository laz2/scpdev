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
package net.ostis.scpdev.ui.dialogs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ostis.scpdev.ScpdevPlugin;
import net.ostis.scpdev.core.ScNature;
import net.ostis.scpdev.ui.model.AggregatetWorkbenchLabelProvider;
import net.ostis.scpdev.util.ResourceHelper;
import net.ostis.tgf.TGFCommand;
import net.ostis.tgf.TGFCommandListener;
import net.ostis.tgf.TGFReader;
import net.ostis.tgf.TGFType;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

/**
 * @author Dmitry Lazurkin
 */
class TGFElement {

    private final IFile parent;
    private final String program;

    public IFile getParent() {
        return parent;
    }

    public String getProgram() {
        return program;
    }

    public TGFElement(IFile parent, String program) {
        this.parent = parent;
        this.program = program;
    }

    @Override
	public String toString() {
        IProject project = parent.getProject();
        ScNature nature = ScNature.getScNature(project);
        IFolder binroot = nature.getBinaryRoot();

        return "/" + parent.getFullPath().makeRelativeTo(binroot.getFullPath()).append(program).toString();
    }

}

/**
 * @author Dmitry Lazurkin
 */
class TGFProgramSearcher implements TGFCommandListener {

    private int genProgramIndex = -1;

    private boolean keynodeWasDeclared = false;

    private Map<Integer, String> possiblePrograms = new HashMap<Integer, String>();

    private Set<Integer> possibleArcs = new HashSet<Integer>();

    private Set<String> programs = new HashSet<String>();

    private final IFile parent;

    private TGFElement[] result = null;

    public TGFProgramSearcher(IFile parent) {
        this.parent = parent;
    }

    public TGFElement[] getPrograms() {
        if (result == null) {
            result = new TGFElement[programs.size()];
            int i = 0;
            for (String program : programs) {
                result[i] = new TGFElement(parent, program);
                i++;
            }
        }

        return result;
    }

    @Override
    public void processCommand(TGFCommand command) {
        switch (command.getType()) {
        case TGFCommand.DECLARE_SEGMENT: {
            String uri = (String) command.getArgument(0).getValue();
            if (uri.equals("/proc/keynode")) {
                keynodeWasDeclared = true;
            }
            break;
        }
        case TGFCommand.GENEL: {
            String id = (String) command.getArgument(0).getValue();
            TGFType type = (TGFType) command.getArgument(1).getValue();
            if (keynodeWasDeclared && genProgramIndex == -1) {
                if (id.equals("programSCP")) {
                    genProgramIndex = command.getIndex();
                }
            } else {
                if (type.equals(TGFType.NODE_CONST)) {
                    possiblePrograms.put(command.getIndex(), id);
                } else if (type.equals(TGFType.ARC_CONST_POS)) {
                    possibleArcs.add(command.getIndex());
                }
            }
            break;
        }
        case TGFCommand.SETBEG: {
            int arc = (Integer) command.getArgument(0).getValue();
            int beg = (Integer) command.getArgument(1).getValue();

            if (keynodeWasDeclared && genProgramIndex != -1) {
                if (beg != genProgramIndex) {
                    possibleArcs.remove(arc);
                }
            }
            break;
        }
        case TGFCommand.SETEND: {
            int arc = (Integer) command.getArgument(0).getValue();
            int end = (Integer) command.getArgument(1).getValue();

            if (keynodeWasDeclared && genProgramIndex != -1) {
                if (possibleArcs.contains(arc)) {
                    programs.add(possiblePrograms.get(end));
                }
            }
            break;
        }
        default:
            break;
        }
    }

}

/**
 * @author Dmitry Lazurkin
 */
public class ProgramSignSelectionDialog extends ElementTreeSelectionDialog {

    private static class ProgramSignContentProvider extends WorkbenchContentProvider {

        private IFolder binroot;

        private Map<IFile, TGFElement[]> obj2tgf = new HashMap<IFile, TGFElement[]>();

        public ProgramSignContentProvider(IFolder binroot) {
            this.binroot = binroot;
        }

        private TGFElement[] getPrograms(IFile tgffile) {
            TGFElement[] result = obj2tgf.get(tgffile);

            if (result == null) {
                TGFProgramSearcher searcher = new TGFProgramSearcher(tgffile);
                try {
                    new TGFReader(tgffile.getContents()).processAllCommand(searcher);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result = searcher.getPrograms();
                obj2tgf.put(tgffile, result);
            }

            return result;
        }

        @Override
		public Object[] getChildren(Object element) {
            if (element instanceof IFile) {
                return getPrograms((IFile) element);
            }

            return super.getChildren(element);
        }

        @Override
		public Object[] getElements(Object element) {
            if (element == null) {
                return super.getElements(binroot);
            }

            return super.getElements(element);
        }

        @Override
		public Object getParent(Object element) {
            if (element instanceof TGFElement) {
                return ((TGFElement) element).getParent();
            }

            return super.getParent(element);
        }

        @Override
		public boolean hasChildren(Object element) {
            if (element instanceof IFile) {
                return getPrograms((IFile) element).length != 0;
            }

            if (element instanceof TGFElement) {
                return false;
            }

            return super.hasChildren(element);
        }
    }

    private static class ProgramSignLabelProvider extends AggregatetWorkbenchLabelProvider {

        private final static Image programImage = ScpdevPlugin.getImageDescriptor("icons/scp_perspective.gif").createImage();

        @Override
		public Image getImage(Object element) {
            if (element instanceof TGFElement) {
                return programImage;
            }

            return super.getImage(element);
        }

        @Override
		public String getText(Object element) {
            if (element instanceof TGFElement) {
                return ((TGFElement) element).getProgram();
            }

            return super.getText(element);
        }
    }

    public ProgramSignSelectionDialog(Shell parent, IFolder binroot) {
        super(parent, new ProgramSignLabelProvider(), new ProgramSignContentProvider(binroot));

        ResourceHelper.refreshLocal(binroot, IResource.DEPTH_INFINITE);

        setInput(binroot);
        setAllowMultiple(false);
        setComparator(new ResourceComparator(ResourceComparator.TYPE));
        setValidator(new ISelectionStatusValidator() {
            @Override
			public IStatus validate(Object selection[]) {
                if(selection.length == 1) {
                    if(selection[0] instanceof TGFElement) {
                        TGFElement sign = (TGFElement) selection[0];
                        return new Status(IStatus.OK, ScpdevPlugin.PLUGIN_ID,
                                IStatus.OK, "Program sign  " + sign + " selected", null);
                    }
                }
                return new Status(IStatus.ERROR, ScpdevPlugin.PLUGIN_ID,
                        IStatus.ERROR, "No program sign selected", null);

            }
        });
        setComparator(new ResourceComparator(ResourceComparator.TYPE));
    }

}

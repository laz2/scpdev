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
package net.ostis.scpdev.debug.ui.launching;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.IPropertyChangeNotifier;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * @author Dmitry Lazurkin
 */
public abstract class AbstractLaunchConfigurationBlock extends AbstractLaunchConfigurationTab implements
        IPropertyChangeNotifier {

    protected AbstractLaunchConfigurationBlocksTab parentTab;

    private List<IPropertyChangeListener> linteners;

    /**
     * Sets attributes in the working copy
     *
     * @param configuration The configuration to set the attribute in
     * @param name Name of the attribute to set
     * @param value Value to set
     */
    protected void setAttribute(ILaunchConfigurationWorkingCopy configuration, String name, String value) {
        if (value == null || value.length() == 0) {
            configuration.setAttribute(name, (String) null);
        } else {
            configuration.setAttribute(name, value);
        }
    }

    @Override
    public void addPropertyChangeListener(IPropertyChangeListener listener) {
        if (linteners == null) {
            linteners = new ArrayList<IPropertyChangeListener>(2);
        }

        linteners.add(listener);
    }

    @Override
    public void removePropertyChangeListener(IPropertyChangeListener listener) {
        if (linteners != null) {
            linteners.remove(listener);
        }
    }

    protected void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
        if (linteners != null) {
            PropertyChangeEvent event = new PropertyChangeEvent(this, name, oldValue, newValue);
            for (IPropertyChangeListener l : linteners)
                l.propertyChange(event);
        }
    }

}

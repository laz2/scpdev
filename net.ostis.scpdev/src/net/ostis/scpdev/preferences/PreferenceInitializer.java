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
package net.ostis.scpdev.preferences;

import net.ostis.scpdev.ScpdevPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 *
 * @author Dmitry Lazurkin
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    public void initializeDefaultPreferences() {
        IPreferenceStore store = ScpdevPlugin.getDefault().getPreferenceStore();
        store.setDefault(PreferenceConstants.P_USE_SC_CORE_HOME, true);
        store.setDefault(PreferenceConstants.P_SC_CORE_PATH, "");
        store.setDefault(PreferenceConstants.P_PYTHON_EXECUTABLE, "");
        store.setDefault(PreferenceConstants.P_SYSTEM_UI_PATH, "");
    }

}

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
package net.ostis.scpdev.preferences.scs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import net.ostis.scpdev.ScpdevPlugin;

/**
 * @author Dmitry Lazurkin
 */
public class SCsPreferenceInitializer extends AbstractPreferenceInitializer {

    public void initializeDefaultPreferences() {
        IPreferenceStore store = ScpdevPlugin.getDefault().getPreferenceStore();
        PreferenceConverter.setDefault(store, SCsPreferenceConstants.P_SCS_COMMENT_COLOR, new RGB(0, 128, 128));
        PreferenceConverter.setDefault(store, SCsPreferenceConstants.P_SCS_STRING_COLOR, new RGB(0, 128, 0));
        PreferenceConverter.setDefault(store, SCsPreferenceConstants.P_SCS_DEFAULT_COLOR, new RGB(0, 0, 0));
    }

}

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
package net.ostis.scpdev.external;

import java.io.File;

import net.ostis.scpdev.ScpdevPlugin;
import net.ostis.scpdev.preferences.PreferenceConstants;

/**
 * @author Dmitry Lazurkin
 */
public class ScCoreModule {

    private static final String SC_CORE_HOME = "SC_CORE_HOME";

    public static String validateHomeVariable() {
        String path = System.getenv(SC_CORE_HOME);
        if (path != null) {
            return validateRoot(new File(path));
        } else {
            return "Variable SC_CORE_HOME not defined in system";
        }
    }

    public static String validateRoot(File root) {
        File binDir = new File(root.getAbsolutePath() + File.separator + "bin");

        if (!binDir.exists()) {
            return "Not found 'bin' directory";
        }

        File scs2tgf = new File(binDir.getAbsolutePath() + File.separator + "scs2tgf.exe");
        if (!scs2tgf.exists() || !scs2tgf.isFile()) {
            return "Not found 'bin/scs2tgf.exe'";
        }

        File startpm = new File(binDir.getAbsolutePath() + File.separator + "start-pm.exe");
        if (!startpm.exists() || !startpm.isFile()) {
            return "Not found 'bin/startpm.exe'";
        }

        File shareDir = new File(root.getAbsolutePath() + File.separator + "share" + File.separator + "sc-core");

        if (!shareDir.exists()) {
            return "Not found 'share' directory";
        }

        File m4scp = new File(shareDir.getAbsolutePath() + File.separator + "m4scp.m4");
        if (!m4scp.exists() || !m4scp.isFile()) {
            return "Not found 'share/m4scp.m4'";
        }

        File m4 = new File(binDir.getAbsolutePath() + File.separator + "m4.exe");
        if (!m4.exists() || !m4.isFile()) {
            return "Not found 'bin/m4.exe'";
        }

        return null;
    }

    public static String getScCorePath() {
        boolean useHome = ScpdevPlugin.getDefault().getPreferenceStore()
                .getBoolean(PreferenceConstants.P_USE_SC_CORE_HOME);
        String root;
        if (useHome) {
            root = System.getenv(SC_CORE_HOME);
        } else {
            root = ScpdevPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_SC_CORE_PATH);
        }

        return (validateRoot(new File(root)) == null) ? root : null;
    }

    public static String getSCsCompilerPath() {
        String root = getScCorePath();
        if (root != null) {
            return root + File.separator + "bin" + File.separator + "scs2tgf.exe";
        } else {
            return null;
        }
    }

    public static String getM4Path() {
        String root = getScCorePath();
        if (root != null) {
            return root + File.separator + "bin" + File.separator + "m4.exe";
        } else {
            return null;
        }
    }

    public static String getM4ScpPath() {
        String root = getScCorePath();
        if (root != null) {
            return root + File.separator + "share" + File.separator + "sc-core" + File.separator + "m4scp.m4";
        } else {
            return null;
        }
    }

    public static String getStartPM() {
        String root = getScCorePath();
        if (root != null) {
            return root + File.separator + "bin" + File.separator + "start-pm.exe";
        } else {
            return null;
        }
    }

}

/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent Systems)
 * For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2011 OSTIS
 *
 * OSTIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OSTIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OSTIS.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ostis.scpdev.debug;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * Plugin class for scpdev debug core.
 *
 * @author Dmitry Lazurkin
 */
public class ScpdevDebugPlugin extends Plugin {

    /**
     * The plug-in identifier (value <code>"net.ostis.scpdev.debug"</code>).
     */
    public static final String PLUGIN_ID = "net.ostis.scpdev.debug" ; //$NON-NLS-1$

    /**
     * The shared instance.
     */
    private static ScpdevDebugPlugin plugin;

    public ScpdevDebugPlugin() {
        super();
    }

    /**
     * Returns the shared instance.
     *
     * @return the shared instance
     */
    public static ScpdevDebugPlugin getDefault() {
        return plugin;
    }

    /**
     * Convenience method which returns the unique identifier of this plugin.
     *
     * @return the unique identifier of this plugin
     */
    public static String getUniqueIdentifier() {
        return PLUGIN_ID;
    }

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/log4j.properties"));
        PropertyConfigurator.configure(props);
    }

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

}

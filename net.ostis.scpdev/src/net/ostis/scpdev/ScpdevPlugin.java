/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent Systems)
 * For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2010 OSTIS
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
package net.ostis.scpdev;

import java.io.IOException;
import java.util.Properties;

import net.ostis.scpdev.editors.templates.SCsContextType;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Dmitry Lazurkin
 */
public class ScpdevPlugin extends AbstractUIPlugin {

    public static final String version = "0.1.0";

    // The plug-in ID
    public static final String PLUGIN_ID = "net.ostis.scpdev";

    // The shared instance
    private static ScpdevPlugin plugin;

    /** The context type registry. */
    private static ContributionContextTypeRegistry registry;

    /** The template store. */
    private static TemplateStore store;

    /** Key to store custom templates. */
    public static final String CUSTOM_TEMPLATES_KEY = "net.ostis.scpdev.editors.templates.scs_custom";

    /**
     * The constructor
     */
    public ScpdevPlugin() {
        super();
    }

    @Override
	public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/log4j.properties"));
        PropertyConfigurator.configure(props);
    }

    @Override
	public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ScpdevPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public static ContextTypeRegistry getContextTypeRegistry() {
        if (registry == null) {
            // create an configure the contexts available in the template editor
            registry = new ContributionContextTypeRegistry();
            registry.addContextType(SCsContextType.SCS_CONTEXT_TYPE);
            registry.addContextType(SCsContextType.SCS_FILES_CONTEXT_TYPE);
        }
        return registry;
    }

    /**
     * Returns this plug-in's template store.
     *
     * @return the template store of this plug-in instance
     */
    public static TemplateStore getTemplateStore() {
        if (store == null) {
            store = new ContributionTemplateStore(getContextTypeRegistry(), getDefault().getPreferenceStore(),
                    CUSTOM_TEMPLATES_KEY);
            try {
                store.load();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return store;
    }

}

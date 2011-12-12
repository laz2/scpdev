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
package net.ostis.scpdev.m4scp;

import java.io.IOException;

import net.ostis.scpdev.m4scp.editors.templates.M4ScpContextType;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Dmitry Lazurkin
 */
public class M4ScpPlugin extends AbstractUIPlugin {
    public static final String version = "0.1.0";

    // The plug-in ID
    public static final String PLUGIN_ID = "net.ostis.scpdev.m4scp";

    // The shared instance
    private static M4ScpPlugin plugin;

    /** The context type registry. */
    private static ContributionContextTypeRegistry registry;

    /** The template store. */
    private static TemplateStore store;

    /** Key to store custom templates. */
    public static final String CUSTOM_TEMPLATES_KEY = "org.python.pydev.editor.templates.m4scp_custom";

    /**
     * The constructor
     */
    public M4ScpPlugin() {
        super();
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static M4ScpPlugin getDefault() {
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
            registry.addContextType(M4ScpContextType.M4SCP_CONTEXT_TYPE);
            registry.addContextType(M4ScpContextType.M4SCP_FILES_CONTEXT_TYPE);
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
            store = new ContributionTemplateStore(getContextTypeRegistry(), M4ScpPlugin.getDefault()
                    .getPreferenceStore(), CUSTOM_TEMPLATES_KEY);
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

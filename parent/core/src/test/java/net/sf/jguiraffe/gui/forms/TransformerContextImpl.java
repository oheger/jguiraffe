/*
 * Copyright 2006-2022 The JGUIraffe Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jguiraffe.gui.forms;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.resources.ResourceManager;
import net.sf.jguiraffe.resources.impl.ResourceManagerImpl;
import net.sf.jguiraffe.resources.impl.bundle.BundleResourceLoader;
import net.sf.jguiraffe.transform.DefaultValidationMessageHandler;
import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.ValidationMessageHandler;

import org.apache.commons.jelly.JellyContext;
import org.easymock.EasyMock;

/**
 * A very simple implementation of the {@code TransformerContext} interface used
 * for testing Jelly tags.
 *
 * @author Oliver Heger
 * @version $Id: TransformerContextImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TransformerContextImpl implements TransformerContext
{
    /** The locale used by this context. */
    private static final Locale LOCALE = Locale.ENGLISH;

    /** The name of the default resource group. */
    private static final String DEF_RES_GRP = "testformbuilderresources";

    /** The resource manager. */
    private final ResourceManager resMan = new ResourceManagerImpl(
            new BundleResourceLoader());

    public Locale getLocale()
    {
        return LOCALE;
    }

    public ResourceManager getResourceManager()
    {
        return resMan;
    }

    public Map<String, Object> properties()
    {
        return Collections.emptyMap();
    }

    public <T> T getTypedProperty(Class<T> propCls)
    {
        return null;
    }

    public ValidationMessageHandler getValidationMessageHandler()
    {
        return new DefaultValidationMessageHandler();
    }

    /**
     * Creates a Jelly context and initializes a {@code TransformerContext} for
     * it. This method creates a {@code ComponentBuilderData} object that is
     * initialized with a new instance of this class. It is stored in a Jelly
     * context. This method is especially useful for test cases for Jelly tag
     * handler classes that need access to the objects wrapped by a {@code
     * TransformerContext}, e.g. a resource manager.
     *
     * @return the Jelly context
     */
    public static JellyContext setUpTransformerCtxInJelly()
    {
        JellyContext context = new JellyContext();
        ComponentBuilderData data = new ComponentBuilderData();
        data.put(context);
        TransformerContextImpl tctx = new TransformerContextImpl();
        tctx.getResourceManager().setDefaultResourceGroup(DEF_RES_GRP);
        data.initializeForm(tctx, EasyMock
                .createNiceMock(BindingStrategy.class));
        return context;
    }
}

/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.transform;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jguiraffe.resources.ResourceManager;

/**
 * <p>
 * A specialized implementation of the <code>TransformerContext</code>
 * interface that allows wrapping an existing context and extending its
 * properties.
 * </p>
 * <p>
 * One important feature of a <code>TransformerContext</code> is to provide a
 * set of properties that can be queried by transformers or validators. While
 * these properties are typically global, in some situations it is useful to
 * override or extend them on a local basis, e.g. to define some special
 * properties for a single transformer only.
 * </p>
 * <p>
 * This class supports exactly this use case: An instance is created by
 * specifying a <code>TransformerContext</code> to wrap and a map with
 * (additional) properties. The <code>properties()</code> method is
 * implemented to construct a union from the properties of the original context
 * and the map of this instance. The remaining methods simply delegate to the
 * wrapped context.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TransformerContextPropertiesWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TransformerContextPropertiesWrapper implements TransformerContext
{
    /** Stores the wrapped context. */
    private TransformerContext wrappedContext;

    /** Stores the properties for this object. */
    private Map<String, Object> properties;

    /**
     * Creates a new instance of
     * <code>TransformerContextPropertiesWrapper</code> and initializes it.
     *
     * @param orgCtx the context to be wrapped (must not be <b>null</b>)
     * @param newProps the new properties (must not be <b>null</b>)
     * @throws IllegalArgumentException if the original context or the map with
     * the new properties is <b>null</b>
     */
    public TransformerContextPropertiesWrapper(TransformerContext orgCtx,
            Map<String, Object> newProps)
    {
        if (orgCtx == null)
        {
            throw new IllegalArgumentException(
                    "Original context must not be null!");
        }
        if (newProps == null)
        {
            throw new IllegalArgumentException(
                    "New properties must not be null!");
        }

        wrappedContext = orgCtx;
        properties = newProps;
    }

    /**
     * Returns the wrapped context.
     *
     * @return the wrapped context
     */
    public TransformerContext getWrappedContext()
    {
        return wrappedContext;
    }

    /**
     * Returns the current locale. This implementation delegates to the wrapped
     * context.
     *
     * @return the current locale
     */
    public Locale getLocale()
    {
        return getWrappedContext().getLocale();
    }

    /**
     * Returns the resource manager. This implementation delegates to the
     * wrapped context.
     *
     * @return the resource manager
     */
    public ResourceManager getResourceManager()
    {
        return getWrappedContext().getResourceManager();
    }

    /**
     * Returns a map with properties. This implementation constructs a union
     * from the properties of the wrapped context and the properties stored in
     * this object.
     *
     * @return a map with properties
     */
    public Map<String, Object> properties()
    {
        Map<String, Object> props = new HashMap<String, Object>(
                getWrappedContext().properties());
        props.putAll(properties);
        return props;
    }

    /**
     * Returns the <code>ValidationMessageHandler</code>. This implementation
     * delegates to the wrapped context.
     *
     * @return the <code>ValidationMessageHandler</code>
     */
    public ValidationMessageHandler getValidationMessageHandler()
    {
        return getWrappedContext().getValidationMessageHandler();
    }

    /**
     * Returns the value of the specified typed property. This implementation
     * delegates to the wrapped context.
     *
     * @param <T> the type of the property
     * @param propCls the property class
     * @return the value of this property
     */
    public <T> T getTypedProperty(Class<T> propCls)
    {
        return getWrappedContext().getTypedProperty(propCls);
    }
}

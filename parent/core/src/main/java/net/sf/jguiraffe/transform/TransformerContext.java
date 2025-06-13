/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import java.util.Locale;
import java.util.Map;

import net.sf.jguiraffe.resources.ResourceManager;

/**
 * <p>
 * Definition of an interface for accessing data needed by transformers.
 * </p>
 * <p>
 * This interface defines a set of methods for accessing system information like
 * the current {@code Locale} or the resource manager. A {@link Transformer}
 * object is passed an implementation of this interface, so it can make use of
 * the methods defined here to obtain the data it needs.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TransformerContext.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface TransformerContext
{
    /**
     * Returns the current {@code Locale}.
     *
     * @return the {@code Locale} to use
     */
    Locale getLocale();

    /**
     * Returns the current {@code ResourceManager}. This object can be
     * used to access resources.
     *
     * @return the {@code ResourceManager}
     */
    ResourceManager getResourceManager();

    /**
     * Returns a map with properties. Transformers or validators can use this
     * method for obtaining specific properties that may influence their
     * behavior.
     *
     * @return a map with properties assigned to this context
     */
    Map<String, Object> properties();

    /**
     * Returns the value of the property with the given type. Objects that are
     * passed a {@code TransformerContext} have access to a set of properties
     * which can impact their behavior. Using this method properties can be
     * queried in a type-safe way. This is in contrast to the properties
     * available through the {@link #properties()} method. While the standard
     * transformer implementations shipped with this library typically use the
     * plain properties, this method is intended to be used by high-level custom
     * transformers that need access to certain application-global objects. For
     * instance, an application may store information about the currently edited
     * object in its context. A {@code Validator} implementation may then access
     * this data to perform specific checks. The type-safe properties available
     * through this method are typically disjunct to the the ones provided by
     * {@link #properties()}.An implementation should check whether a property
     * of the specified type is available and return it. Otherwise, the method
     * should return <b>null</b>.
     *
     * @param <T> the type of the property
     * @param propCls the property class
     * @return the value of this property or <b>null</b> if it is not set
     */
    <T> T getTypedProperty(Class<T> propCls);

    /**
     * Returns the {@code ValidationMessageHandler} associated with this
     * context. This object can be used for obtaining validation messages. It is
     * used for instance by concrete {@link Validator}
     * implementations.
     *
     * @return the {@code ValidationMessageHandler} for this context
     */
    ValidationMessageHandler getValidationMessageHandler();
}

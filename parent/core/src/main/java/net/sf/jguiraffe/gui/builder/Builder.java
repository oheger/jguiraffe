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
package net.sf.jguiraffe.gui.builder;

import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.locators.Locator;

/**
 * <p>
 * The main builder interface.
 * </p>
 * <p>
 * A builder is an object that can produce artifacts of the GUI from builder
 * scripts. So the GUI is no longer constructed in the application's code, but
 * can be defined in external resources, which are easier to maintain. This also
 * allows for dynamic GUIs, e.g. when different builder scripts are processed
 * based on some condition or even when builder scripts are generated at
 * runtime.
 * </p>
 * <p>
 * The builder interface itself is not too complex. There is one very generic
 * <code>build()</code> method that can be used to build arbitrary GUI
 * elements. Then there are a few convenience methods that are suitable for
 * specific elements like windows.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Builder.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface Builder
{
    /**
     * A generic builder method. This method initializes the builder with the
     * passed in parameter object and then executes the given builder script.
     * Results of the builder operation are stored in the parameter object, from
     * which they can be obtained using the
     * <code>{@link BuilderData#getProperty(String)}</code> method with
     * appropriate keys.
     *
     * @param script the script to be executed
     * @param data the parameter object
     * @throws BuilderException if an error occurs
     */
    void build(Locator script, BuilderData data) throws BuilderException;

    /**
     * A convenience method for building windows like top-level frames or
     * dialogs. This method behaves similar to the generic <code>build()</code>
     * method, but directly returns the resulting window object.
     *
     * @param script the script to be executed
     * @param data the parameter object
     * @return the result window of the builder operation
     * @throws BuilderException if an error occurs
     */
    Window buildWindow(Locator script, BuilderData data)
            throws BuilderException;

    /**
     * A convenience method for constructing the GUI of the given passed in
     * container. This method is useful if an application already has a
     * reference to an (empty) window or panel, which now should be filled. It
     * sets the container as the builder's root container and then invokes the
     * specified script.
     *
     * @param script the script to be executed
     * @param data the parameter object
     * @param container the container to be filled with components
     * @throws BuilderException if an error occurs
     */
    void buildContainer(Locator script, BuilderData data, Object container)
            throws BuilderException;

    /**
     * Releases the specified {@code BuilderData} object. This will free all
     * resources associated with this data object. Especially the {@code
     * BeanContext} created by the {@code Builder} will be closed, and on all
     * {@code BeanProvider} objects found in the {@code BeanStore}s that are
     * part of the {@code BeanBuilderResult} the {@code shutdown()} method is
     * invoked. Clients of the builder should call this method when the results
     * of a builder operation are no more needed, e.g. when closing a window
     * created by a builder.
     *
     * @param data the {@code BuilderData} object used for a builder operation
     *        (must not be <b>null</b>)
     * @throws IllegalArgumentException if the passed in object is <b>null</b>
     *         or invalid
     */
    void release(BuilderData data);
}

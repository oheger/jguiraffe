/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.MutableBeanStore;
import net.sf.jguiraffe.locators.Locator;

/**
 * <p>
 * Definition of an interface for processing scripts with bean definitions.
 * </p>
 * <p>
 * A <em>bean builder</em> is able to create and populate
 * {@link net.sf.jguiraffe.di.BeanStore BeanStore} objects to be used by the
 * <em>dependency injection</em> framework. After a successful build operation
 * the defined beans can be easily accessed.
 * </p>
 * <p>
 * This interface is pretty lean. It defines methods for executing a script with
 * bean definitions. The return value is a data object, from which the
 * initialized <code>BeanStore</code> instances can be queried. Another method
 * can be called to free all resources used by produced beans. This method
 * should be called if the builder results are no longer needed.
 * </p>
 * <p>
 * For obtaining concrete implementations of this interface, usually a
 * {@link BeanBuilderFactory} is used.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanBuilder.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface BeanBuilder
{
    /**
     * Executes a script with bean definitions.
     *
     * @param script points to the script to be executed (must not be
     *        <b>null</b>)
     * @param rootStore the root <code>BeanStore</code> object; if defined, the
     *        processed bean definitions will be added to this store unless
     *        otherwise specified; if <b>null</b>, a new bean store will be
     *        created
     * @param loaderProvider an object with information about registered class
     *        loaders; can be <b>null</b>, then a default class loader provider
     *        will be used
     * @return an object providing access to the <code>BeanStore</code>
     *         instances created or populated during the builder operation
     * @throws BuilderException if an error occurs
     * @throws IllegalArgumentException if the script is <b>null</b>
     */
    BeanBuilderResult build(Locator script, MutableBeanStore rootStore,
            ClassLoaderProvider loaderProvider) throws BuilderException;

    /**
     * Executes a script with bean definitions and the specified helper objects.
     * With this method all helper objects taking part in the builder operation
     * can be specified. Passing the {@link InvocationHelper} can be useful if
     * the script defines special beans which require custom data type
     * converters. These converters can be configured in the
     * {@link net.sf.jguiraffe.di.ConversionHelper ConversionHelper} instance
     * contained in the {@link InvocationHelper}. It is possible to pass
     * <b>null</b> references for the helper objects. In this case default
     * objects are created.
     *
     * @param script points to the script to be executed (must not be
     *        <b>null</b>)
     * @param rootStore the root <code>BeanStore</code> object; if defined, the
     *        processed bean definitions will be added to this store unless
     *        otherwise specified; if <b>null</b>, a new bean store will be
     *        created
     * @param loaderProvider an object with information about registered class
     *        loaders; can be <b>null</b>, then a default class loader provider
     *        will be used
     * @param invHlp a helper object for reflection operations; can be
     *        <b>null</b>, then a default helper object will be used
     * @return an object providing access to the <code>BeanStore</code>
     *         instances created or populated during the builder operation
     * @throws BuilderException if an error occurs
     * @throws IllegalArgumentException if the script is <b>null</b>
     */
    BeanBuilderResult build(Locator script, MutableBeanStore rootStore,
            ClassLoaderProvider loaderProvider, InvocationHelper invHlp)
            throws BuilderException;

    /**
     * Releases the specified {@code BeanBuilderResult} object. This frees all
     * resources associated with that data object. Especially on all
     * {@code BeanProvider} objects found in one of the {@code BeanStore}s
     * referenced by the {@code BeanBuilderResult} object the {@code shutdown()}
     * method is called. When the data produced by a {@code BeanBuilder} is no
     * more needed clients should invoke this method to ensure a proper clean
     * up.
     *
     * @param result the {@code BeanBuilderResult} object to be released (must
     *        not be <b>null</b>)
     * @throws IllegalArgumentException if the data object is <b>null</b>
     */
    void release(BeanBuilderResult result);
}

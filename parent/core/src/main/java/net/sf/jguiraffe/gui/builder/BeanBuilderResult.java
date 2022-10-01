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
package net.sf.jguiraffe.gui.builder;

import java.util.Set;

import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.InvocationHelper;

/**
 * <p>
 * Definition of an interface for accessing the results of a {@link BeanBuilder}.
 * </p>
 * <p>
 * A <em>bean builder</em> processes a script with bean definitions, creates
 * {@link net.sf.jguiraffe.di.BeanProvider BeanProvider} objects from them and
 * stores these providers in {@link BeanStore} objects. An arbitrary number of
 * <code>BeanStore</code> objects may be created during a builder operation,
 * which can be organized in a hierarchical structure.
 * </p>
 * <p>
 * This interface allows access to the <code>BeanStore</code> objects created by
 * the bean builder. They can be listed or queried by name. A client can thus
 * obtain exactly the store objects it needs. Further, there is some information
 * available about helper objects that have been used during processing of the
 * builder script.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanBuilderResult.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface BeanBuilderResult
{
    /**
     * Returns a set with the names of the existing bean stores.
     *
     * @return a set with the names of the bean stores
     */
    Set<String> getBeanStoreNames();

    /**
     * Returns the <code>BeanStore</code> with the given name. The name can be
     * <b>null</b>, then the root <code>BeanStore</code> of the builder
     * operation is returned.
     *
     * @param name the name of the desired <code>BeanStore</code>
     * @return the <code>BeanStore</code> with this name
     * @throws java.util.NoSuchElementException if there is no such
     *         <code>BeanStore</code>
     */
    BeanStore getBeanStore(String name);

    /**
     * Returns the {@link ClassLoaderProvider} that was used by the builder
     * during script processing.
     *
     * @return the {@link ClassLoaderProvider}
     */
    ClassLoaderProvider getClassLoaderProvider();

    /**
     * Returns the {@link InvocationHelper} object that was used by builder
     * during script processing. This object also contains the
     * {@link net.sf.jguiraffe.di.ConversionHelper ConversionHelper} with all
     * registered type converters. So this information may be of interest for a
     * client. It is also required for releasing a builder result.
     *
     * @return the {@link InvocationHelper} used by the builder
     */
    InvocationHelper getInvocationHelper();
}

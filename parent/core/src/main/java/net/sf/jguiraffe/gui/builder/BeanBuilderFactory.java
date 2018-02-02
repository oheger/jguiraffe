/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

/**
 * <p>
 * Definition of an interface for querying new <code>{@link BeanBuilder}</code>
 * objects.
 * </p>
 * <p>
 * This interface provides a generic means for obtaining a new
 * <code>BeanBuilder</code> instance that can be used for processing a script
 * with bean definitions. It is a typical factory interface decoupling its
 * clients from the concrete builder implementation.
 * </p>
 * <p>
 * <em>Note:</em> An implementation is intended to be thread-safe, so that it
 * can be shared between multiple threads. For the returned
 * <code>BeanBuilder</code> instances however, this is not the case. They
 * should be used by a single thread only.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanBuilderFactory.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface BeanBuilderFactory
{
    /**
     * Returns a <code>BeanBuilder</code> object for processing a script with
     * bean definitions. The returned object is fully initialized and can be
     * used immediately.
     *
     * @return a new <code>BeanBuilder</code> instance
     * @throws BuilderException if an error occurs
     */
    BeanBuilder getBeanBuilder() throws BuilderException;
}

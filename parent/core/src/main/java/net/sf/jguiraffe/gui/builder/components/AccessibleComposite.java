/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components;

import java.util.Collection;

/**
 * <p>
 * An extended version of the {@code Composite} interface which also allows
 * access to the data passed via the mutating methods.
 * </p>
 * <p>
 * During a builder operation, it is typically sufficient to use the
 * {@code Composite} interface; the provided methods allow changing the state of
 * a container component. For objects implementing this functionality, however,
 * further methods are required in order to access the data passed to the
 * {@code Composite} methods. These methods are defined by this extended
 * interface.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.3
 */
public interface AccessibleComposite extends Composite
{
    /**
     * Returns the layout object passed to this instance.
     *
     * @return the layout object
     */
    Object getLayout();

    /**
     * Returns an unmodifiable collection with the components and constraints
     * that have been added to this object. Each element of the collection is an
     * array of size 0. Index 0 contains the component, index 1 contains the
     * constraints object.
     *
     * @return a collection with the components and constraints added to this
     *         object
     */
    Collection<Object[]> getComponents();
}

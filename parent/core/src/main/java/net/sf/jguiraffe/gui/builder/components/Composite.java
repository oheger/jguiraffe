/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

/**
 * <p>
 * Definition of an interface for components that can contain other components.
 * </p>
 * <p>
 * This interface is used during the process of constructing the GUI. Some
 * container tags exists whose content is defined by nested tags. These nested
 * tags created concrete GUI elements and then ensure that the newly created
 * elements are added to the container. To support this a <code>Composite</code>
 * must define a method for adding sub components.
 * </p>
 * <p>
 * Another important point is that containers are typically associated with a
 * layout. This interface supports layouts by defining a method for setting such
 * an object.
 * </p>
 * <p>
 * In the form builder framework not only specific container tags implement this
 * interface. There is also a special implementation for the top level or root
 * container (which is the container object to which all components created by
 * the builder are added). All tags that are not nested inside a container tag
 * will be added to this root container.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Composite.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface Composite
{
    /**
     * Adds the specified component to this container using the given
     * constraints.
     *
     * @param comp the component to add
     * @param constraints the constraints (may be <b>null </b>)
     */
    void addComponent(Object comp, Object constraints);

    /**
     * Sets the layout object for this container.
     *
     * @param layout the layout object
     */
    void setLayout(Object layout);

    /**
     * Returns the concrete container component that is wrapped by this object.
     * The returned container will be specific for the used GUI library.
     *
     * @return the GUI library specific container
     */
    Object getContainer();
}

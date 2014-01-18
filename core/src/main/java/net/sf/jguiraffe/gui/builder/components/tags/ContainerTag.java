/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.Composite;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specific tag handler implementation for creating container tags.
 * </p>
 * <p>
 * Container tags can contain an arbitrary number of other components, which are
 * defined as child elements of this tag. During the builder process the
 * component tags look for their enclosing container tag and add the newly
 * created component to it. A container can also have a layout object.
 * </p>
 * <p>
 * This base class already implements the major part of the functionality needed
 * for containers. Concrete sub classes only have to define a method which
 * creates the specific container object.
 * </p>
 * <p>
 * A container is a usual component, so all component properties like a font or
 * constraints also apply to containers.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ContainerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class ContainerTag extends SimpleComponentTag implements
        Composite
{
    /** Stores the components that have been added to this container. */
    private Collection<Object[]> components;

    /** Stores a reference to the layout object for this container. */
    private Object layout;

    /**
     * Creates a new instance of <code>ContainerTag</code>.
     */
    public ContainerTag()
    {
        components = new LinkedList<Object[]>();
    }

    /**
     * Adds the specified component to this container using the given
     * constraints. This method will be called by component tags defined in the
     * body of this tag.
     *
     * @param comp the component to add
     * @param constraints the constraints for this component
     */
    public void addComponent(Object comp, Object constraints)
    {
        components.add(new Object[] {
                comp, constraints
        });
    }

    /**
     * Returns the layout for this container.
     *
     * @return the layout
     */
    public Object getLayout()
    {
        return layout;
    }

    /**
     * Sets the layout object for this container. This method is called by a
     * layout tag in the body of this tag.
     *
     * @param layout the layout to set
     */
    public void setLayout(Object layout)
    {
        this.layout = layout;
    }

    /**
     * Returns the concrete container component. This is the newly created
     * component.
     *
     * @return the container component
     */
    public Object getContainer()
    {
        return getComponent();
    }

    /**
     * Creates and initializes the container. This implementation takes care of
     * adding the components to the container widget and setting the layout. Sub
     * classes only need to ensure that the correct container widget gets
     * created.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the new container widget
     * @throws JellyTagException if the tag is incorrectly used
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected Object createComponent(ComponentManager manager, boolean create)
            throws JellyTagException, FormBuilderException
    {
        Object container = createContainer(manager, create, components);
        if (!create)
        {
            if (getLayout() != null)
            {
                manager.setContainerLayout(container, getLayout());
            }
            addComponents(manager, container, components);
        }
        return container;
    }

    /**
     * Adds the components of this container to the container widget. This
     * method is called by <code>createComponent()</code>.
     *
     * @param manager the component manager
     * @param container the (newly created) container widget
     * @param comps the collection with the components to add; the elements in
     * this collection are of type <code>Object[]</code>; each array has two
     * elements: the component at index 0 and the assoziated layout constraint
     * object at index 1
     * @throws FormBuilderException if an error occurs
     */
    protected void addComponents(ComponentManager manager, Object container,
            Collection<Object[]> comps) throws FormBuilderException
    {
        for (Iterator<Object[]> it = comps.iterator(); it.hasNext();)
        {
            Object[] compData = it.next();
            manager.addContainerComponent(container, compData[0], compData[1]);
        }
    }

    /**
     * Creates the container widget. This method is called by the implementation
     * of <code>{@link #createComponent(ComponentManager)}</code>. Concrete
     * sub classes must define it to return the specific GUI container widget.
     * The passed in collection with the child components can be used when
     * needed for initialization. In all cases later the
     * <code>{@link #addComponents(ComponentManager, Object, Collection) addComponents()}</code>
     * method will be called to add the children automatically (so if the
     * children are already processed, this method should be overwritten with an
     * empty implementation).
     *
     * @param manager the component manager
     * @param create the create flag
     * @param components a collection with the container's children
     * @return the container widget
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is incorrectly used
     */
    protected abstract Object createContainer(ComponentManager manager,
            boolean create, Collection<Object[]> components) throws FormBuilderException,
            JellyTagException;
}

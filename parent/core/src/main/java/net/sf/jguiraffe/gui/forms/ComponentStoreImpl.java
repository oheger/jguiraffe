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
package net.sf.jguiraffe.gui.forms;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * A fully functional default implementation of the <code>ComponentStore</code>
 * interface.
 * </p>
 * <p>
 * This implementation keeps the managed components and handlers in maps where
 * they can directly be accessed. The components of the stored component
 * handlers are also put in the map for the components, so the name spaces of
 * these entities are not disjunct.
 * </p>
 * <p>
 * For field handlers situation is similar: The component handlers associated to
 * the added field handlers will be added to the map with the component
 * handlers, too (and their components will in turn be added to the component
 * map). So access to the stored entities is somewhat hierarchical.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComponentStoreImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ComponentStoreImpl implements ComponentStore
{
    /** Stores the components. */
    private Map<String, Object> components;

    /** Stores the component handlers. */
    private Map<String, ComponentHandler<?>> componentHandlers;

    /** Stores the field handlers. */
    private Map<String, FieldHandler> fieldHandlers;

    /**
     * Creates a new instance of <code>ComponentStoreImpl</code> and
     * initializes it.
     */
    public ComponentStoreImpl()
    {
        components = new HashMap<String, Object>();
        componentHandlers = new HashMap<String, ComponentHandler<?>>();
        fieldHandlers = new HashMap<String, FieldHandler>();
    }

    /**
     * Adds a new component to this store.
     *
     * @param name the name of the component
     * @param component the component to be added
     * @throws IllegalArgumentException if either name or component are <b>null</b>
     */
    public void add(String name, Object component)
    {
        if (component == null)
        {
            throw new IllegalArgumentException(
                    "Component to be added must not be null!");
        }
        checkName(name);
        components.put(name, component);
    }

    /**
     * Adds a new component handler to this store.
     *
     * @param name the name of the handler
     * @param handler the handler to be added
     * @throws IllegalArgumentException if either name or component handler are
     * <b>null</b>
     */
    public void addComponentHandler(String name, ComponentHandler<?> handler)
    {
        if (handler == null)
        {
            throw new IllegalArgumentException(
                    "Component handler must not be null!");
        }
        checkName(name);
        componentHandlers.put(name, handler);
        Object comp = handler.getComponent();
        if (comp != null)
        {
            add(name, comp);
        }
    }

    /**
     * Adds a new field handler to this store.
     *
     * @param name the name of the field handler
     * @param fldHandler the handler to be added
     * @throws IllegalArgumentException if either name or handler are <b>null</b>
     */
    public void addFieldHandler(String name, FieldHandler fldHandler)
    {
        if (fldHandler == null)
        {
            throw new IllegalArgumentException(
                    "Field handler must not be null!");
        }
        checkName(name);
        fieldHandlers.put(name, fldHandler);
        ComponentHandler<?> compHandler = fldHandler.getComponentHandler();
        if (compHandler != null)
        {
            addComponentHandler(name, compHandler);
        }
    }

    /**
     * Searches the component with the specified name.
     *
     * @param name the name
     * @return the component with this name or <b>null</b> if it cannot be
     * found
     */
    public Object findComponent(String name)
    {
        return components.get(name);
    }

    /**
     * Searches the component handler with the specified name.
     *
     * @param name the name
     * @return the component handler with this name or <b>null</b> if it cannot
     * be found
     */
    public ComponentHandler<?> findComponentHandler(String name)
    {
        return componentHandlers.get(name);
    }

    /**
     * Searches the field handler with the specified name.
     *
     * @param name the name
     * @return the field handler with this name or <b>null</b> if it cannot be
     * found
     */
    public FieldHandler findFieldHandler(String name)
    {
        return fieldHandlers.get(name);
    }

    /**
     * Returns a set with the names of all stored component handlers.
     *
     * @return the names of the stored component handlers
     */
    public Set<String> getComponentHandlerNames()
    {
        return componentHandlers.keySet();
    }

    /**
     * Returns a set with the names of all stored components.
     *
     * @return the names of the stored components
     */
    public Set<String> getComponentNames()
    {
        return components.keySet();
    }

    /**
     * Returns a set with the names of all stored field handlers.
     *
     * @return the names of the stored field handlers
     */
    public Set<String> getFieldHandlerNames()
    {
        return fieldHandlers.keySet();
    }

    /**
     * Clears the content of this store. After that this object is exactly like
     * a newly created one.
     */
    public void clear()
    {
        components.clear();
        componentHandlers.clear();
        fieldHandlers.clear();
    }

    /**
     * Checks the name of a newly added entity. The name must not be <b>null</b>.
     *
     * @param name the name to check
     * @throws IllegalArgumentException if the name is <b>null</b>
     */
    private void checkName(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Name must not be null!");
        }
    }
}

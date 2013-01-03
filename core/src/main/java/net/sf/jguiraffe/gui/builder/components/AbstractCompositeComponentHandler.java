/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.lang.ObjectUtils;

/**
 * <p>
 * An abstract base class for concrete implementations of the
 * {@link CompositeComponentHandler} interface.
 * </p>
 * <p>
 * This base class already provides functionality for managing a list of child
 * {@link ComponentHandler} objects. It also has base implementations for most
 * of the methods defined by the {@link CompositeComponentHandler} interface.
 * Concrete sub classes mainly have to implement the methods for accessing the
 * data of this handler. Here the idea is that the data of the child component
 * handlers must be converted into an object that is the data of this composite
 * handler. For the base implementations of the other interface methods refer to
 * the method documentation.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AbstractCompositeComponentHandler.java 205 2012-01-29 18:29:57Z oheger $
 * @param <T> the type of the data of this component handler
 * @param <S> the type of the child handlers
 */
public abstract class AbstractCompositeComponentHandler<T, S> implements
        CompositeComponentHandler<T, S>
{
    /** A map for storing the child handlers and their names. */
    private final Map<String, ComponentHandler<S>> childHandlers;

    /** A list of the child handlers for accessing a child by its index. */
    private final List<ComponentHandler<S>> childHandlerList;

    /** The data class. */
    private final Class<T> dataClass;

    /**
     * Creates a new instance of {@code AbstractCompositeComponentHandler} and
     * initializes it with the data type class.
     *
     * @param dataType the type of the data managed by this handler
     */
    protected AbstractCompositeComponentHandler(Class<T> dataType)
    {
        dataClass = dataType;
        childHandlers = new LinkedHashMap<String, ComponentHandler<S>>();
        childHandlerList = new ArrayList<ComponentHandler<S>>();
    }

    /**
     * Adds the specified child {@code ComponentHandler} to this {@code
     * CompositeComponentHandler}.
     *
     * @param name the name of the child handler
     * @param handler the child handler to be added (must not be <b>null</b>)
     * @throws IllegalArgumentException if the handler is <b>null</b> or a child
     *         handler with this name already exists
     */
    public void addHandler(String name, ComponentHandler<S> handler)
    {
        if (handler == null)
        {
            throw new IllegalArgumentException(
                    "Child handler must not be null!");
        }
        if (childHandlers.containsKey(name))
        {
            throw new IllegalArgumentException(
                    "There is already a child handler with this name: " + name);
        }

        childHandlers.put(name, handler);
        childHandlerList.add(handler);
    }

    /**
     * Returns the component this handler is associated with. This base
     * implementation always returns <b>null</b> because there is no underlying
     * component.
     *
     * @return the associated component
     */
    public Object getComponent()
    {
        return null;
    }

    /**
     * Returns the outer component this handler is associated with. This base
     * implementation always returns <b>null</b> because this handler is not
     * associated with a concrete component.
     *
     * @return the outer component
     */
    public Object getOuterComponent()
    {
        return null;
    }

    /**
     * Returns the data type of this component handler. This base implementation
     * returns the class that was passed to the constructor.
     *
     * @return the data type of this handler
     */
    public Class<?> getType()
    {
        return dataClass;
    }

    /**
     * Returns a flag whether this {@code ComponentHandler} is enabled. This
     * implementation returns <b>true</b> if and only if all child handlers are
     * enabled.
     *
     * @return a flag whether this {@code ComponentHandler} is enabled
     */
    public boolean isEnabled()
    {
        for (ComponentHandler<?> ch : childHandlerList)
        {
            if (!ch.isEnabled())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Changes the enabled state of this {@code ComponentHandler}. This
     * implementation calls {@code setEnabled()} on all child handlers passing
     * in the argument.
     *
     * @param f the new enabled state
     */
    public void setEnabled(boolean f)
    {
        for (ComponentHandler<?> ch : childHandlerList)
        {
            ch.setEnabled(f);
        }
    }

    /**
     * Returns the number of child {@code ComponentHandler}s that have been
     * added to this {@code CompositeComponentHandler}.
     *
     * @return the number of child handlers
     */
    public final int getChildHandlerCount()
    {
        return childHandlerList.size();
    }

    /**
     * Returns a list with all child handlers managed by this {@code
     * CompositeComponentHandler}. Derived classes can call this method to
     * access the child handlers. Note: the list must not be modified.
     *
     * @return a list with all child handlers
     */
    protected List<ComponentHandler<S>> getChildHandlers()
    {
        return childHandlerList;
    }

    /**
     * Returns a set with the names of the child handlers that have been added
     * to this handler. The order of the names in this set corresponds to the
     * order in which the child handlers have been added. Note: the set must not
     * be modified.
     *
     * @return a set with the names of the child handlers
     */
    protected Set<String> getChildHandlerNames()
    {
        return childHandlers.keySet();
    }

    /**
     * Returns the child handler with the specified name. If the handler cannot
     * be found, <b>null</b> is returned.
     *
     * @param name the name of the child handler in question
     * @return the corresponding child {@code ComponentHandler} or <b>null</b>
     */
    protected ComponentHandler<S> getChildHandler(String name)
    {
        return childHandlers.get(name);
    }

    /**
     * Returns the index for the child handler with the given name. Indices are
     * 0-based. If the name cannot be resolved, this method returns -1.
     *
     * @param name the name of the child handler in question
     * @return the index of this child handler
     */
    protected int getChildHandlerIndex(String name)
    {
        int index = 0;
        for (String handlerName : childHandlers.keySet())
        {
            if (ObjectUtils.equals(name, handlerName))
            {
                return index;
            }
            index++;
        }

        return -1;
    }

    /**
     * Returns the name of the child handler at the specified index. The index
     * can run from 0 to the number of child handlers minus 1. The handlers are
     * indexed in the order they have been added to this {@code
     * CompositeComponentHandler}. If the index passed in is invalid, this
     * method returns <b>null</b>.
     *
     * @param index the index of the desired child handler
     * @return the corresponding child {@code ComponentHandler} or <b>null</b>
     */
    protected String getChildHandlerNameAt(int index)
    {
        if (index < 0 || index >= getChildHandlerCount())
        {
            return null;
        }

        Iterator<String> it = childHandlers.keySet().iterator();
        for (int i = 0; i < index; i++)
        {
            it.next();
        }
        return it.next();
    }
}

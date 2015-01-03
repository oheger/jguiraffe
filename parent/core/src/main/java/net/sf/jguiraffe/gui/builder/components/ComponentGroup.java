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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.jelly.JellyContext;

/**
 * <p>
 * This class represents a group of components.
 * </p>
 * <p>
 * The form builder library supports adding components to logical groups.
 * Logical in this context means that these groups do not have a direct
 * representation on the generated GUI. They exist only during the builder
 * process and can be used to reference components. Examples include
 * constructing radio groups or composite component handlers.
 * </p>
 * <p>
 * A <code>ComponentGroup</code> instance does not contain the components
 * themselves, but rather their names. For obtaining the corresponding
 * components access to a {@link ComponentBuilderData} object is required.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComponentGroup.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ComponentGroup
{
    /**
     * Constant for the prefix under which groups are stored in the jelly
     * context.
     */
    private static final String GROUP_PREFIX = "group/";

    /** Stores the names of the components that belong to this group. */
    private final Set<String> componentNames;

    /**
     * Creates a new instance of <code>ComponentGroup</code>.
     */
    public ComponentGroup()
    {
        componentNames = new LinkedHashSet<String>();
    }

    /**
     * Returns a set with the names of all components contained in this group.
     * The order of elements in the set corresponds to the order in which the
     * components have been added to this group.
     *
     * @return a set with the names of all contained components
     */
    public Set<String> getComponentNames()
    {
        return Collections.unmodifiableSet(componentNames);
    }

    /**
     * Adds a component to this group.
     *
     * @param name the name of the component to add
     */
    public void addComponent(String name)
    {
        componentNames.add(name);
    }

    /**
     * Returns a map with all components this group refers to. This method looks
     * up all component names in the specified {@code ComponentBuilderData}
     * object. The resulting map contains the names of the components as keys
     * and the corresponding component objects as values. If a component name
     * cannot be resolved, a {@code FormBuilderException} exception is thrown.
     *
     * @param data the {@code ComponentBuilderData} object (must not be
     *        <b>null</b>)
     * @return a map with all components that belong to this group
     * @throws FormBuilderException if a component cannot be resolved
     * @throws IllegalArgumentException if the {@code ComponentBuilderData}
     *         object is <b>null</b>
     */
    public Map<String, Object> getComponents(ComponentBuilderData data)
            throws FormBuilderException
    {
        if (data == null)
        {
            throw new IllegalArgumentException(
                    "ComponentBuilderData must not be null!");
        }
        Map<String, Object> comps = new LinkedHashMap<String, Object>();

        for (String name : componentNames)
        {
            Object comp = data.getComponent(name);
            if (comp == null)
            {
                throw new FormBuilderException("Component cannot be resolved: "
                        + name);
            }
            comps.put(name, comp);
        }

        return comps;
    }

    /**
     * Returns a map with the {@code ComponentHandler} objects of the components
     * this group refers to. This method works like
     * {@link #getComponents(ComponentBuilderData)}, but the resulting map
     * contains the {@code ComponentHandler}s rather than the components
     * themselves.
     *
     * @param data the {@code ComponentBuilderData} object (must not be
     *        <b>null</b>)
     * @return a map with all component handlers that belong to this group
     * @throws FormBuilderException if a component cannot be resolved
     * @throws IllegalArgumentException if the {@code ComponentBuilderData}
     *         object is <b>null</b>
     */
    public Map<String, ComponentHandler<?>> getComponentHandlers(
            ComponentBuilderData data) throws FormBuilderException
    {
        if (data == null)
        {
            throw new IllegalArgumentException(
                    "ComponentBuilderData must not be null!");
        }
        Map<String, ComponentHandler<?>> handlers =
                new LinkedHashMap<String, ComponentHandler<?>>();

        for (String name : componentNames)
        {
            ComponentHandler<?> handler = data.getComponentHandler(name);
            if (handler == null)
            {
                throw new FormBuilderException(
                        "ComponentHandler cannot be resolved: " + name);
            }
            handlers.put(name, handler);
        }

        return handlers;
    }

    /**
     * Changes the enabled state of this {@code ComponentGroup}. This method
     * obtains the {@code ComponentHandler} objects for all components that
     * belong to this group. Then it calls the {@code setEnabled()} method on
     * all these handlers.
     *
     * @param data the {@code ComponentBuilderData} object (must not be
     *        <b>null</b>)
     * @param enabled the new enabled flag
     * @see #getComponentHandlers(ComponentBuilderData)
     * @throws FormBuilderException if a component cannot be resolved
     * @throws IllegalArgumentException if the {@code ComponentBuilderData}
     *         object is <b>null</b>
     */
    public void enableGroup(ComponentBuilderData data, boolean enabled)
            throws FormBuilderException
    {
        for (ComponentHandler<?> handler : getComponentHandlers(data).values())
        {
            handler.setEnabled(enabled);
        }
    }

    /**
     * Fetches the group with the given name from the specified jelly context.
     * If the group does not exist, an exception will be thrown.
     *
     * @param context the jelly context (must not be <b>null</b>
     * @param name the name of the desired group
     * @return the group with this name
     * @throws IllegalArgumentException if the context is <b>null</b>
     * @throws NoSuchElementException if there is no such group
     */
    public static ComponentGroup fromContext(JellyContext context, String name)
            throws NoSuchElementException
    {
        ComponentGroup result = fetchGroup(context, name);
        if (result == null)
        {
            throw new NoSuchElementException("No group with name '" + name
                    + "' found!");
        }
        return result;
    }

    /**
     * Tests whether a group with the specified name exists in the given jelly
     * context. Note that {@code ComponentGroup} objects are not stored under
     * their name in the context, but a specific prefix is used. So always this
     * method has to be used to check the existence of a group rather than
     * testing the context directly.
     *
     * @param context the jelly context
     * @param name the name of the group
     * @return <b>true </b> if there is such a group, <b>false </b> otherwise
     * @throws IllegalArgumentException if the context is <b>null</b>
     */
    public static boolean groupExists(JellyContext context, String name)
    {
        return fetchGroup(context, name) != null;
    }

    /**
     * Stores a component group in the jelly context under a given name.
     *
     * @param context the context
     * @param name the group's name
     * @param group the group to store (if <b>null </b>, the group will be
     *        removed if it exists)
     */
    public static void storeGroup(JellyContext context, String name,
            ComponentGroup group)
    {
        checkContext(context);
        if (group == null)
        {
            context.removeVariable(GROUP_PREFIX + name);
        }
        else
        {
            context.setVariable(GROUP_PREFIX + name, group);
        }
    }

    /**
     * Creates a new {@code ComponentGroup} instance and stores it in the
     * specified context. This is a convenience method which performs the
     * following steps:
     * <ol>
     * <li>It checks whether already a group with the specified name exists in
     * the context. If this is the case, an exception is thrown.</li>
     * <li>Otherwise a new {@code ComponentGroup} instance is created.</li>
     * <li>The new instance is stored in the context under the specified name.</li>
     * <li>The newly created instance is returned.</li>
     * </ol>
     *
     * @param context the Jelly context (must not be <b>null</b>
     * @param name the name of the new {@code ComponentGroup} (must not be
     *        <b>null</b>)
     * @return the newly created {@code ComponentGroup} instance
     * @throws FormBuilderException if a group with this name already exists
     * @throws IllegalArgumentException if the group name or the context is
     *         <b>null</b>
     */
    public static ComponentGroup createGroup(JellyContext context, String name)
            throws FormBuilderException
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Group name must not be null!");
        }
        if (groupExists(context, name))
        {
            throw new FormBuilderException(String.format(
                    "A group with the name %s already exists!", name));
        }

        ComponentGroup group = new ComponentGroup();
        storeGroup(context, name, group);
        return group;
    }

    /**
     * Obtains the {@code ComponentGroup} with the specified name from the given
     * {@code BeanContext}. This method is similar to
     * {@link #fromContext(JellyContext, String)}, but the {@code
     * ComponentGroup} is resolved from a {@code BeanContext} object. This can
     * be useful if the group is to be obtained after a builder operation. In
     * this case, the Jelly context may not be available directly, but it can be
     * accessed through the {@code BeanContext} returned by the builder.
     *
     * @param context the {@code BeanContext}
     * @param groupName the name of the group to be obtained
     * @return the corresponding {@code ComponentGroup} instance
     * @throws IllegalArgumentException if the {@code BeanContext} is
     *         <b>null</b>
     * @throws net.sf.jguiraffe.di.InjectionException if the group cannot be resolved
     */
    public static ComponentGroup fromBeanContext(BeanContext context,
            String groupName)
    {
        checkContext(context);
        return (ComponentGroup) context.getBean(GROUP_PREFIX + groupName);
    }

    /**
     * Tests whether a group with the specified name exists in the given {@code
     * BeanContext}. Works like {@link #groupExists(JellyContext, String)}, but
     * checks the given {@code BeanContext}.
     *
     * @param context the {@code BeanContext}
     * @param groupName the name of the group in question
     * @return a flag whether this {@code ComponentGroup} can be found in this
     *         {@code BeanContext}
     * @throws IllegalArgumentException if the {@code BeanContext} is
     *         <b>null</b>
     */
    public static boolean groupExistsInBeanContext(BeanContext context,
            String groupName)
    {
        checkContext(context);
        return context.containsBean(GROUP_PREFIX + groupName);
    }

    /**
     * Helper method for fetching a component group from the jelly context.
     *
     * @param context the context
     * @param name the group's name
     * @return the found group or <b>null </b> if it does not exist
     * @throws IllegalArgumentException if the context is <b>null</b>
     */
    private static ComponentGroup fetchGroup(JellyContext context, String name)
    {
        checkContext(context);
        return (ComponentGroup) context.getVariable(GROUP_PREFIX + name);
    }

    /**
     * Tests the passed in context object. This method throws an exception if
     * the context is <b>null</b>.
     *
     * @param context the context
     * @throws IllegalArgumentException if no context is passed
     */
    private static void checkContext(Object context)
    {
        if (context == null)
        {
            throw new IllegalArgumentException("Context must not be null!");
        }
    }
}

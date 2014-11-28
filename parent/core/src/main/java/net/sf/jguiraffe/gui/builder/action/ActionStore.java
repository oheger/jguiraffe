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
package net.sf.jguiraffe.gui.builder.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * A class for maintaining action objects.
 * </p>
 * <p>
 * This class provides access to the actions available in an application or for
 * a certain component. Actions can be queried by their name, or lists of all
 * existing actions can be requested. It is also possible to organize actions in
 * groups. Then for example all actions that belong to a group can be disabled
 * at once.
 * </p>
 * <p>
 * An <code>ActionStore</code> object also holds a reference to a parent
 * store. If defined, all actions in the parent store can be accessed by this
 * store, too. This provides for a hierarchical action architecture. E.g. there
 * may be a central <code>ActionStore</code> holding the global actions of the
 * application. Sub components of the application (e.g. internal frames or
 * dialogs) can define their own <code>ActionStore</code> with their specific
 * set of actions, but through the parent reference have also access to the
 * global actions.
 * </p>
 * <p>
 * Note: The operations of this class are thread-safe.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionStore.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ActionStore
{
    /** Stores the defined actions in this store. */
    private final Map<String, FormAction> actions;

    /** Holds information about the defined action groups. */
    private final Map<String, Set<String>> groups;

    /** Stores the reference to this store's parent. */
    private volatile ActionStore parent;

    /**
     * Creates a new empty instance of <code>ActionStore</code>.
     */
    public ActionStore()
    {
        this(null);
    }

    /**
     * Creates a new instance of <code>ActionStore</code> and initializes the
     * parent reference.
     *
     * @param parent this action store's parent
     */
    public ActionStore(ActionStore parent)
    {
        actions = new ConcurrentHashMap<String, FormAction>();
        groups = new HashMap<String, Set<String>>();
        setParent(parent);
    }

    /**
     * Adds the specified action to this store.
     *
     * @param action the action to add (must not be <b>null</b>)
     */
    public void addAction(FormAction action)
    {
        if (action == null)
        {
            throw new IllegalArgumentException("Action must not be null!");
        }
        if (action.getName() == null)
        {
            throw new IllegalArgumentException(
                    "The action's name must not be null!");
        }

        actions.put(action.getName(), action);
    }

    /**
     * Removes the action with the specified name from this store. This method
     * only removes actions that belong to this store; actions in the parent
     * store are not removed.
     *
     * @param name the name of the action to remove
     * @return the removed action or <b>null</b> if it was not found
     */
    public FormAction removeAction(String name)
    {
        return actions.remove(name);
    }

    /**
     * Returns the action with the given name. If this action does not exist, an
     * exception will be thrown.
     *
     * @param name the name of the desired action
     * @return the action with this name
     * @throws NoSuchElementException if no such action exists
     */
    public FormAction getAction(String name)
    {
        FormAction action = (name != null) ? actions.get(name) : null;
        if (action != null)
        {
            return action;
        }
        else
        {
            if (getParent() == null)
            {
                throw new NoSuchElementException("Action " + name
                        + " could not be found!");
            }
            else
            {
                return getParent().getAction(name);
            }
        }
    }

    /**
     * Returns a flag whether the specified action is contained in this store or
     * in the parent store.
     *
     * @param name the action's name
     * @return a flag whether this action exists
     */
    public boolean hasAction(String name)
    {
        return (name != null) && (actions.containsKey(name)
                || (getParent() != null && getParent().hasAction(name)));
    }

    /**
     * Returns a collection with the names of the actions stored in this
     * <code>ActionStore</code>. The returned collection will not contain the
     * names of the actions that are stored in the parent
     * <code>ActionStore</code>.
     *
     * @return a collection with the names of the actions directly stored in
     * this <code>ActionStore</code>
     */
    public Set<String> getActionNames()
    {
        return actions.keySet();
    }

    /**
     * Returns a collection with the names of all actions stored in this
     * <code>ActionStore</code> or in one of its parents. With this method
     * really all names can be found out that can be passed to the
     * {@link #getAction(String)} method. Note: the set returned
     * by this method is a snapshot reflecting the state of this action time at
     * the time it was created. It is not connected to this action store, so
     * later updates are not visible.
     *
     * @return a collection with all defined action names
     */
    public Set<String> getAllActionNames()
    {
        Set<String> result = new HashSet<String>(getActionNames());
        ActionStore store = getParent();

        while (store != null)
        {
            result.addAll(store.getAllActionNames());
            store = store.getParent();
        }

        return result;
    }

    /**
     * Returns all action objects whose names are specified in the given
     * collection. This is a convenience method for easily accessing groups of
     * actions. If one of the requested actions does not exist, a
     * <code>NoSuchElementException</code> exception will be thrown. If an
     * action cannot be found in this store, the parent store (if it is
     * defined), is also searched.
     *
     * @param names a collection with the names of the desired actions
     * @return the corresponding actions
     * @throws NoSuchElementException if one of the actions cannot be resolved
     */
    public Collection<FormAction> getActions(Collection<String> names)
    {
        if (names == null)
        {
            return Collections.emptyList();
        }

        Collection<FormAction> result = new ArrayList<FormAction>(names.size());
        for (String name : names)
        {
            result.add(getAction(name));
        }

        return result;
    }

    /**
     * Adds the specified action to the given group. This establishes a logical
     * connection between this action and the group. If no group with this name
     * exists, it is created now. If the action is unknown in this store or in
     * the parent stores, a <code>NoSuchElementException</code> exception will
     * be thrown.
     *
     * @param actionName the name of the action
     * @param groupName the name of the group (must not be <b>null</b>)
     */
    public void addActionToGroup(String actionName, String groupName)
    {
        if (!hasAction(actionName))
        {
            throw new NoSuchElementException("Unknown action " + actionName);
        }
        if (groupName == null)
        {
            throw new IllegalArgumentException("Group name must not be null!");
        }

        synchronized (groups)
        {
            Set<String> groupSet = groups.get(groupName);
            if (groupSet == null)
            {
                groupSet = new HashSet<String>();
                groups.put(groupName, groupSet);
            }

            groupSet.add(actionName);
        }
    }

    /**
     * Removes the specified action from the given group. If the group becomes
     * empty after this operation, it is removed itself.
     *
     * @param actionName the action's name
     * @param groupName the group's name
     * @return a flag if the action was removed (<b>false</b> if either the
     * action did not belong to this group or the group does not exist)
     */
    public boolean removeActionFromGroup(String actionName, String groupName)
    {
        synchronized (groups)
        {
            Set<String> group = groups.get(groupName);
            boolean found = (group == null) ? false : group.remove(actionName);
            if (found && group.isEmpty())
            {
                // remove empty group
                groups.remove(groupName);
            }
            return found;
        }
    }

    /**
     * Checks if the specified action belongs to the given group.
     *
     * @param actionName the action's name
     * @param groupName the group's name
     * @return a flag if the action belongs to this group
     */
    public boolean isActionInGroup(String actionName, String groupName)
    {
        synchronized (groups)
        {
            Set<String> group = groups.get(groupName);
            return group != null && group.contains(actionName);
        }
    }

    /**
     * Removes the group with the specified name. This does not affect any
     * actions in this group; only the actions' associations to this group are
     * removed.
     *
     * @param groupName the name of the group to remove
     * @return a flag if the group existed
     */
    public boolean removeGroup(String groupName)
    {
        synchronized (groups)
        {
            return groups.remove(groupName) != null;
        }
    }

    /**
     * Returns a set with the names of all actions that belong to the given
     * group. If the group does not exist, the returned set is empty. The
     * returned set is only a copy, so modifications won't have any effect on
     * the groups of this store.
     *
     * @param groupName the name of the group
     * @return a set with the names of all actions in this group
     */
    public Set<String> getActionNamesForGroup(String groupName)
    {
        Set<String> result = new HashSet<String>();
        synchronized (groups)
        {
            Set<String> group = groups.get(groupName);
            if (group != null)
            {
                result.addAll(group);
            }
        }
        return result;
    }

    /**
     * Returns the names of all defined groups. Groups are a means for logically
     * grouping actions. With the set returned here a client can iterate over
     * all existing groups. Note that groups defined in one
     * <code>ActionStore</code> are completely independent on the parent's
     * groups, i.e. this method won't return any groups defined in the parent
     * store.
     *
     * @return a collection with the names of the defined action groups
     */
    public Set<String> getGroupNames()
    {
        synchronized (groups)
        {
            return groups.keySet();
        }
    }

    /**
     * Sets the enabled flag for all actions in the specified group. If the
     * group cannot be found, this method has no effect.
     *
     * @param groupName the name of the group
     * @param enabled the value of the enabled flag
     */
    public void enableGroup(String groupName, boolean enabled)
    {
        ActionHelper.enableActions(ActionHelper.fetchActionsInGroup(this,
                groupName), enabled);
    }

    /**
     * Returns the parent store.
     *
     * @return the parent store (can be <b>null</b>)
     */
    public ActionStore getParent()
    {
        return parent;
    }

    /**
     * Sets the parent store. Some of the methods also include a parent store in
     * look up operations.
     *
     * @param parent the parent store
     */
    public void setParent(ActionStore parent)
    {
        this.parent = parent;
    }
}

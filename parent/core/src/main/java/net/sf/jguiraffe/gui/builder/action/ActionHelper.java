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
package net.sf.jguiraffe.gui.builder.action;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.event.BuilderEvent;

/**
 * <p>
 * A helper class for dealing with actions.
 * </p>
 * <p>
 * This class provides some utility methods that can be used when working with
 * actions and their tasks.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionHelper.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class ActionHelper
{
    /**
     * Private constructor. No instances can be created. All utility methods are
     * static.
     */
    private ActionHelper()
    {
    }

    /**
     * Returns a flag whether the specified object is a valid task for an
     * action. This method tests whether the object can be casted to a type
     * supported by actions.
     *
     * @param task the task object to be inspected
     * @return a flag whether this is an allowed task for an action
     */
    public static boolean isValidActionTask(Object task)
    {
        return task instanceof ActionTask || task instanceof Runnable;
    }

    /**
     * Tests whether the specified object is a valid task for an action. This
     * method delegates to <code>isValidActionTask()</code>. If the result is
     * <b>false</b>, an <code>IllegalArgumentException</code> exception will
     * be thrown.
     *
     * @param task the task to be inspected
     * @throws IllegalArgumentException if the task is not supported by actions
     */
    public static void checkActionTask(Object task)
    {
        if (!isValidActionTask(task))
        {
            throw new IllegalArgumentException("Invalid action task: " + task);
        }
    }

    /**
     * Invokes the specified action task. This method tries to cast the passed
     * in task object to one of the supported types and then invoke then correct
     * method. If the passed in task is invalid, an exception will be thrown.
     *
     * @param task the task to be invoked
     * @param action the corresponding action
     * @param event the event that triggered the action
     * @throws IllegalArgumentException if the task cannot be invoked
     */
    public static void invokeActionTask(Object task, FormAction action,
            BuilderEvent event)
    {
        if (task instanceof ActionTask)
        {
            ((ActionTask) task).run(action, event);
        }
        else if (task instanceof Runnable)
        {
            ((Runnable) task).run();
        }
        else
        {
            throw new IllegalArgumentException("Invalid action task: " + task);
        }
    }

    /**
     * Returns the names of all groups defined for the specified
     * <code>ActionStore</code> and its parents. This method navigates through
     * the hierarchy of stores collecting all existing groups.
     *
     * @param actionStore the <code>ActionStore</code> to start with
     * @return a set with the names of all groups existing in this hierarchy; if
     * the passed in action store is <b>null</b>, an empty set is returned
     */
    public static Set<String> getAllGroupNames(ActionStore actionStore)
    {
        if (actionStore == null)
        {
            return Collections.emptySet();
        }

        Set<String> result = new HashSet<String>(actionStore.getGroupNames());
        result.addAll(getAllGroupNames(actionStore.getParent()));
        return result;
    }

    /**
     * Returns all actions that belong to the specified group in the given
     * <code>ActionStore</code>. If the action store is <b>null</b> or the
     * group does not exist, an empty collection is returned.
     *
     * @param actionStore the action store
     * @param groupName the name of the desired group
     * @return a collection with the actions of this group
     */
    public static Collection<FormAction> fetchActionsInGroup(
            ActionStore actionStore, String groupName)
    {
        if (actionStore == null)
        {
            return Collections.emptySet();
        }
        else
        {
            return actionStore.getActions(actionStore
                    .getActionNamesForGroup(groupName));
        }
    }

    /**
     * Returns all actions that belong to the specified group in the given
     * <code>ActionStore</code> or one of its parents. The
     * <code>distinct</code> parameter determines how actions with the same
     * names are to be treated. If set to <b>true</b>, actions in a parent
     * store are not added to the results collection if there are already
     * actions with the same name in a child store. If the parameter is <b>false</b>,
     * all actions are added.
     *
     * @param actionStore the actionStore to start with (can be <b>null</b>,
     * then an empty collection will be returned)
     * @param groupName the name of the group
     * @param distinct the distinct flag
     * @return a collection with the actions in this group
     */
    public static Collection<FormAction> fetchAllActionsInGroup(
            ActionStore actionStore, String groupName, boolean distinct)
    {
        Collection<FormAction> result = new LinkedHashSet<FormAction>();
        Set<String> names = distinct ? new HashSet<String>() : null;
        ActionStore current = actionStore;

        while (current != null)
        {
            Set<String> currentNames = current
                    .getActionNamesForGroup(groupName);
            if (distinct)
            {
                currentNames.removeAll(names);
            }
            for (String n : currentNames)
            {
                result.add(current.getAction(n));
                if (distinct)
                {
                    names.add(n);
                }
            }

            current = current.getParent();
        }

        return result;
    }

    /**
     * Sets the enabled flag for all actions in the specified collection. With
     * this method a set of actions can be enabled or disabled at once.
     *
     * @param actions a collection with the involved actions (can be <b>null</b>,
     * then this operation has no effect)
     * @param enabled the enabled flag
     */
    public static void enableActions(Collection<FormAction> actions,
            boolean enabled)
    {
        if (actions != null)
        {
            for (FormAction action : actions)
            {
                action.setEnabled(enabled);
            }
        }
    }
}

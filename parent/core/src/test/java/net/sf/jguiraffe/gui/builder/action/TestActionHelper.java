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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.event.BuilderEvent;

import org.easymock.EasyMock;

import junit.framework.TestCase;

/**
 * Test class for ActionHelper.
 *
 * @author Oliver Heger
 * @version $Id: TestActionHelper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestActionHelper extends TestCase
{
    /** Constant for the action name prefix. */
    private static final String ACTION_PREFIX = "Action";

    /** Constant for the group prefix. */
    private static final String GROUP_PREFIX = "Group";

    /**
     * Tests whether a runnable is accepted as an action task.
     */
    public void testIsValidActionTaskRunnable()
    {
        checkIsValidActionTask(Runnable.class);
    }

    /**
     * Tests whether an ActionTask object is accepted as an action task.
     */
    public void testIsValidActionTaskActionTask()
    {
        checkIsValidActionTask(ActionTask.class);
    }

    /**
     * Checks whether an instance of the specified class is accepted as a valid
     * action task.
     *
     * @param taskCls the class of the task
     */
    private void checkIsValidActionTask(Class<?> taskCls)
    {
        Object task = EasyMock.createMock(taskCls);
        EasyMock.replay(task);
        assertTrue("Task not accepted: " + taskCls, ActionHelper
                .isValidActionTask(task));
        EasyMock.verify(task);
    }

    /**
     * Tests that a null reference is not accepted as an action task.
     */
    public void testIsValidActionTaskNull()
    {
        assertFalse("Null accepted as action task", ActionHelper
                .isValidActionTask(null));
    }

    /**
     * Tests that an arbitrary object is not accepted as an action task.
     */
    public void testIsValidActionTaskInvalid()
    {
        assertFalse("Object accepted as action task", ActionHelper
                .isValidActionTask("test"));
    }

    /**
     * Tests the checkActionTask() method for a valid action task.
     */
    public void testCheckActionTaskAccepted()
    {
        Object task = EasyMock.createMock(ActionTask.class);
        EasyMock.replay(task);
        ActionHelper.checkActionTask(task);
        EasyMock.verify(task);
    }

    /**
     * Tests the checkActionTask() method for an invalid action task.
     */
    public void testCheckActionTaskNotAccepted()
    {
        try
        {
            ActionHelper.checkActionTask(null);
            fail("Invalid task was not detected!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests invoking a task that implements the ActionTask interface.
     */
    public void testInvokeActionTaskActionTask()
    {
        FormAction mockAction = EasyMock.createMock(FormAction.class);
        ActionTask mockTask = EasyMock.createMock(ActionTask.class);
        BuilderEvent event = new BuilderEvent(this);
        mockTask.run(mockAction, event);
        EasyMock.replay(mockAction, mockTask);
        ActionHelper.invokeActionTask(mockTask, mockAction, event);
        EasyMock.verify(mockAction, mockTask);
    }

    /**
     * Tests invoking a task that implements the Runnable interface.
     */
    public void testInvokeActionTaskRunnable()
    {
        FormAction mockAction = EasyMock.createMock(FormAction.class);
        Runnable mockTask = EasyMock.createMock(Runnable.class);
        BuilderEvent event = new BuilderEvent(this);
        mockTask.run();
        EasyMock.replay(mockAction, mockTask);
        ActionHelper.invokeActionTask(mockTask, mockAction, event);
        EasyMock.verify(mockAction, mockTask);
    }

    /**
     * Tests invoking a task that implements both the Runnable and the
     * ActionTask interfaces. In this case, ActionTask should take precedence.
     */
    public void testInvokeActionTaskRunnableAndActionTask()
    {
        FormAction mockAction = EasyMock.createMock(FormAction.class);
        ActionTaskAndRunnable mockTask = EasyMock
                .createMock(ActionTaskAndRunnable.class);
        BuilderEvent event = new BuilderEvent(this);
        mockTask.run(mockAction, event);
        EasyMock.replay(mockAction, mockTask);
        ActionHelper.invokeActionTask(mockTask, mockAction, event);
        EasyMock.verify(mockAction, mockTask);
    }

    /**
     * Tests invoking a task that is not supported for actions.
     */
    public void testInvokeActionTaskInvalid()
    {
        checkInvokeActionTaskInvalid("InvalidTask");
    }

    /**
     * Tries to invoke a null task. This should cause an exception.
     */
    public void testInvokeActionTaskNull()
    {
        checkInvokeActionTaskInvalid(null);
    }

    /**
     * Helper method for testing the invocation of an invalid task. This should
     * cause an exception.
     *
     * @param task the task to be invoked
     */
    private void checkInvokeActionTaskInvalid(Object task)
    {
        FormAction mockAction = EasyMock.createMock(FormAction.class);
        EasyMock.replay(mockAction);
        try
        {
            ActionHelper.invokeActionTask("InvalidTask", mockAction,
                    new BuilderEvent(this));
            fail("Could invoke invalid task!");
        }
        catch (IllegalArgumentException iex)
        {
            EasyMock.verify(mockAction);
        }
    }

    /**
     * Creates and populates some test action stores. This data is used for
     * testing recursive methods operating on action stores and their groups.
     *
     * @return the test action store
     */
    private ActionStore setUpStores()
    {
        final int storeCount = 3;
        final int overlapCount = 2;
        final int actionCount = 5;

        ActionStore parent = null;
        ActionStore store = null;
        int actionIdx = 0;
        for (int i = 0; i < storeCount; i++)
        {
            // create a store and fill in some actions
            store = new ActionStore(parent);
            for (int j = 0; j < actionCount; j++, actionIdx++)
            {
                FormActionImpl action = new FormActionImpl(ACTION_PREFIX
                        + actionIdx);
                action.setData(String.valueOf(i));
                store.addAction(action);
            }
            // ensure that there are actions with same names in the next store
            actionIdx -= overlapCount;
            parent = store;
        }

        // add some groups
        store.addActionToGroup(ACTION_PREFIX + "6", GROUP_PREFIX);
        store.addActionToGroup(ACTION_PREFIX + "7", GROUP_PREFIX);
        store.getParent().addActionToGroup(ACTION_PREFIX + "6", GROUP_PREFIX);
        store.getParent().addActionToGroup(ACTION_PREFIX + "5", GROUP_PREFIX);
        store.getParent().getParent().addActionToGroup(ACTION_PREFIX + "0",
                GROUP_PREFIX + "test");

        return store;
    }

    /**
     * Tests retrieving all groups in the hierarchy.
     */
    public void testGetAllGroupNames()
    {
        ActionStore store = setUpStores();
        Set<String> groups = ActionHelper.getAllGroupNames(store);
        assertEquals("Wrong number of groups", 2, groups.size());
        assertTrue("Group not found: " + GROUP_PREFIX, groups
                .contains(GROUP_PREFIX));
        assertTrue("Group not found: " + GROUP_PREFIX + "test", groups
                .contains(GROUP_PREFIX + "test"));
    }

    /**
     * Tests retrieving all groups when the passed in store is null.
     */
    public void testGetAllGroupNamesNull()
    {
        assertTrue("Wrong result for null store", ActionHelper
                .getAllGroupNames(null).isEmpty());
    }

    /**
     * Tests retrieving all actions that belong to a given group.
     */
    public void testFetchActionsInGroup()
    {
        ActionStore store = setUpStores();
        for (FormAction action : ActionHelper.fetchActionsInGroup(store,
                GROUP_PREFIX))
        {
            FormActionImpl ai = (FormActionImpl) action;
            assertTrue("Unexpected action: " + ai, ai.getName().equals(
                    ACTION_PREFIX + "6")
                    || ai.getName().equals(ACTION_PREFIX + "7"));
            assertEquals("Action from wrong store", "2", ai.getData());
        }
    }

    /**
     * Tries to retrieve the actions from an unknown group. This should result
     * in an empty collection.
     */
    public void testFetchActionsInGroupUnknownGroup()
    {
        assertTrue("Wrong result for unknown group", ActionHelper
                .fetchActionsInGroup(setUpStores(), "Unknown group").isEmpty());
    }

    /**
     * Tries to fetch actions from a group when the store is null. An empty
     * collection should be returned.
     */
    public void testFetchActionsInGroupNullStore()
    {
        assertTrue("Wrong result for null store", ActionHelper
                .fetchActionsInGroup(null, GROUP_PREFIX).isEmpty());
    }

    /**
     * Tests recursively fetching actions in a group.
     */
    public void testFetchAllActionsInGroup()
    {
        ActionStore store = setUpStores();
        Collection<FormAction> actions = ActionHelper.fetchAllActionsInGroup(
                store, GROUP_PREFIX, false);
        assertEquals("Wrong number of actions", 4, actions.size());
        findAction(actions, 6, 2);
        findAction(actions, 7, 2);
        findAction(actions, 6, 1);
        findAction(actions, 5, 1);
    }

    /**
     * Tests recursively fetching actions in a group with the distinct flag.
     */
    public void testFetchAllActionsInGroupDistinct()
    {
        ActionStore store = setUpStores();
        Collection<FormAction> actions = ActionHelper.fetchAllActionsInGroup(
                store, GROUP_PREFIX, true);
        assertEquals("Wrong number of actions", 3, actions.size());
        findAction(actions, 6, 2);
        findAction(actions, 7, 2);
        findAction(actions, 5, 1);
    }

    /**
     * Searches for a certain action.
     *
     * @param actions the actions collection
     * @param no the index of this action
     * @param store the store number
     */
    private void findAction(Collection<FormAction> actions, int no, int store)
    {
        boolean found = false;

        for (FormAction action : actions)
        {
            String actionName = ACTION_PREFIX + no;
            if (actionName.equals(action.getName()))
            {
                if (String.valueOf(store).equals(
                        ((FormActionImpl) action).getData()))
                {
                    found = true;
                    break;
                }
            }
        }

        assertTrue("Cannot find action " + no + " in store " + store, found);
    }

    /**
     * Tests fetching all actions in a group when the store is null.
     */
    public void testFetchAllActionsInGroupNullStore()
    {
        assertTrue("Wrong result for null store", ActionHelper
                .fetchAllActionsInGroup(null, GROUP_PREFIX, false).isEmpty());
    }

    /**
     * Tests whether duplicate references to actions are resolved when fetching
     * the actions of a group. (It is possible that one and the same action is
     * added to a group in multiple stores.)
     */
    public void testFetchAllActionsInGroupDuplicate()
    {
        ActionStore store = setUpStores();
        store.addActionToGroup(ACTION_PREFIX + "5", GROUP_PREFIX);
        Collection<FormAction> actions = ActionHelper.fetchAllActionsInGroup(
                store, GROUP_PREFIX, false);
        assertEquals("Wrong number of found actions", 4, actions.size());
    }

    /**
     * Tests enabling a group of actions.
     */
    public void testEnableActions()
    {
        final int actionCount = 10;
        Collection<FormAction> actions = new ArrayList<FormAction>(actionCount);
        for (int i = 0; i < actionCount; i++)
        {
            FormAction action = EasyMock.createMock(FormAction.class);
            action.setEnabled(true);
            action.setEnabled(false);
            actions.add(action);
        }
        EasyMock.replay((Object[]) actions.toArray());
        ActionHelper.enableActions(actions, true);
        ActionHelper.enableActions(actions, false);
        EasyMock.verify((Object[]) actions.toArray());
    }

    /**
     * Tests enabling a null collection. This should have no effect.
     */
    public void testEnableActionsNull()
    {
        ActionHelper.enableActions(null, true);
        // no exception should be thrown
    }

    /**
     * An interface that combines Runnable and ActionTask. This is used for
     * testing action tasks that implement both interfaces.
     */
    private interface ActionTaskAndRunnable extends Runnable, ActionTask
    {
    }
}

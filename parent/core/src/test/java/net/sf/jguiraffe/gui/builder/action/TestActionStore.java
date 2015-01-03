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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Test class for ActionStore.
 *
 * @author Oliver Heger
 * @version $Id: TestActionStore.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestActionStore extends TestCase
{
    /** An array with the names of some test actions. */
    private static final String[] TEST_ACTIONS =
    { "FileOpen", "FileSave", "FileSaveAs", "Exit" };

    /** Constant for the name of a test group. */
    private static final String FILE_GROUP = "FileGroup";

    /** The action store to be tested. */
    private ActionStore store;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        store = new ActionStore();
        setUpActions(store);
    }

    /**
     * Tests creation of new action store objects.
     */
    public void testInitialize()
    {
        store = new ActionStore();
        assertNull("Store has a parent", store.getParent());
        checkInitProperties(store);
        ActionStore child = new ActionStore(store);
        assertSame("Wrong parent", store, child.getParent());
        checkInitProperties(child);
    }

    /**
     * Tests fetching actions.
     */
    public void testGetAction()
    {
        checkFetchAction("FileOpen");
        checkFetchAction("FileSaveAs");
        checkFetchAction("Exit");
    }

    /**
     * Tests accessing an unknown action. This should cause an exception.
     */
    public void testGetActionUnknown()
    {
        try
        {
            store.getAction("SomeAction");
            fail("Could access non existing action!");
        }
        catch (NoSuchElementException nex)
        {
            // ok
        }
    }

    /**
     * Tests accessing a null action. This should cause an exception.
     */
    public void testGetActionNull()
    {
        try
        {
            store.getAction(null);
            fail("Could access null action!");
        }
        catch (NoSuchElementException nex)
        {
            // ok
        }
    }

    /**
     * Tests adding new actions.
     */
    public void testAddAction()
    {
        FormAction action = new FormActionImpl("TestAction");
        store.addAction(action);
        assertEquals("Action not added", action, store.getAction("TestAction"));
    }

    /**
     * Tries adding an null exception. This should cause an exception.
     */
    public void testAddActionNull()
    {
        try
        {
            store.addAction(null);
            fail("Could add a null action!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tries adding an action without a name. This should cause an exception.
     */
    public void testAddActionNullName()
    {
        try
        {
            store.addAction(new FormActionImpl());
            fail("Could add action with null name!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests the hasAction() method.
     */
    public void testHasAction()
    {
        assertTrue("FileOpen not found", store.hasAction("FileOpen"));
        assertTrue("FileSave not found", store.hasAction("FileSave"));
        assertTrue("Exit not found", store.hasAction("Exit"));
        assertFalse("Found null action", store.hasAction(null));
        assertFalse("Found unknown action", store.hasAction("SomeAction"));
    }

    /**
     * Tests removing actions from the store.
     */
    public void testRemoveAction()
    {
        int cnt = store.getActionNames().size();
        FormAction action = store.removeAction("FileSave");
        assertNotNull("Wrong result when removing known action", action);
        assertEquals("Wrong action name", "FileSave", action.getName());
        assertEquals("Wrong number of actions", cnt - 1, store.getActionNames()
                .size());
        assertNull("Action can be removed twice", store
                .removeAction("FileSave"));
    }

    /**
     * Tests whether removing an action affects the parent store.
     */
    public void testRemoveActionInParent()
    {
        assertNotNull("Wrong result when removing known action", store
                .removeAction("FileSave"));
        ActionStore parent = new ActionStore();
        parent.addAction(new FormActionImpl("FileSave"));
        store.setParent(parent);
        assertTrue("Action not found in parent", store.hasAction("FileSave"));
        assertNull("Could remove action in parent", store
                .removeAction("FileSave"));
        assertTrue("Action no more found", store.hasAction("FileSave"));
    }

    /**
     * Tests accessing the actions' names.
     */
    public void testGetActionNames()
    {
        checkActionNames();
        ActionStore parent = new ActionStore();
        setUpActions(parent, "Parent");
        store.setParent(parent);
        checkActionNames();
    }

    /**
     * Tests accessing actions.
     */
    public void testGetActions()
    {
        Collection<String> names = new ArrayList<String>();
        names.add(TEST_ACTIONS[0]);
        names.add(TEST_ACTIONS[TEST_ACTIONS.length - 1]);

        Collection<FormAction> actions = store.getActions(names);
        assertEquals("Wrong action size", names.size(), actions.size());
        Iterator<String> it = names.iterator();
        Iterator<FormAction> it2 = actions.iterator();
        while (it.hasNext())
        {
            FormAction action = it2.next();
            assertEquals("Wrong action name", it.next(), action.getName());
        }
    }

    /**
     * Tests obtaining actions from a null collection with names. Result should
     * be an empty list.
     */
    public void testGetActionsNull()
    {
        assertTrue(store.getActions(null).isEmpty());
    }

    /**
     * Tests obtaining actions when a name is unknown. This should cause an
     * exception.
     */
    public void testGetActionsUnexisting()
    {
        Collection<String> names = new ArrayList<String>();
        names.add(TEST_ACTIONS[0]);
        names.add("unexistingAction");
        try
        {
            store.getActions(names);
            fail("Could fetch unexisting action!");
        }
        catch (NoSuchElementException nex)
        {
            // ok
        }
    }

    /**
     * Tests the behavior of some methods if a parent store is defined.
     */
    public void testParent()
    {
        ActionStore as = new ActionStore(store);
        as.addAction(new FormActionImpl("EditCut"));
        as.addAction(new FormActionImpl("EditPaste"));

        assertTrue("Action not found", as.hasAction("EditPaste"));
        for (int i = 0; i < TEST_ACTIONS.length; i++)
        {
            assertTrue("Parent action not found: " + TEST_ACTIONS[i], as
                    .hasAction(TEST_ACTIONS[i]));
            assertNotNull("Cannot access parent action: " + TEST_ACTIONS[i], as
                    .getAction(TEST_ACTIONS[i]));
        }

        Collection<String> names = new ArrayList<String>();
        names.add(TEST_ACTIONS[1]);
        names.add(TEST_ACTIONS[2]);
        names.add("EditCut");
        assertEquals("Wrong number of found actions", names.size(), as
                .getActions(names).size());

        store.removeAction(TEST_ACTIONS[1]);
        assertFalse("Action not removed", as.hasAction(TEST_ACTIONS[1]));
        try
        {
            as.getAction(TEST_ACTIONS[1]);
            fail("Could fetch non existing action!");
        }
        catch (NoSuchElementException nex)
        {
            // ok
        }
    }

    /**
     * Tests the getAllActionNames() method.
     */
    public void testGetAllActionNames()
    {
        assertEquals(TEST_ACTIONS.length, store.getAllActionNames().size());

        ActionStore parent = new ActionStore();
        parent.addAction(new FormActionImpl("EditCut"));
        parent.addAction(new FormActionImpl(TEST_ACTIONS[0]));
        store.setParent(parent);
        ActionStore grandParent = new ActionStore();
        grandParent.addAction(new FormActionImpl("ViewRefresh"));
        grandParent.addAction(new FormActionImpl(TEST_ACTIONS[2]));
        parent.setParent(grandParent);

        Set<String> names = store.getAllActionNames();
        assertEquals("Wrong number of action names", TEST_ACTIONS.length + 2,
                names.size());
        for (int i = 0; i < TEST_ACTIONS.length; i++)
        {
            assertTrue("Action not found: " + TEST_ACTIONS[i], names
                    .contains(TEST_ACTIONS[i]));
        }
        assertTrue("View action not found", names.contains("ViewRefresh"));
    }

    /**
     * Tests adding actions to groups.
     */
    public void testAddActionToGroup()
    {
        store.addActionToGroup(TEST_ACTIONS[0], FILE_GROUP);
        store.addActionToGroup(TEST_ACTIONS[1], FILE_GROUP);
        Set<String> groups = store.getGroupNames();
        assertEquals("Wrong number of groups", 1, groups.size());
        assertTrue("Group not found", groups.contains(FILE_GROUP));
        assertTrue("Action not found in group", store.isActionInGroup(
                TEST_ACTIONS[0], FILE_GROUP));
        assertFalse("Found unknown action in group", store.isActionInGroup(
                TEST_ACTIONS[2], FILE_GROUP));
    }

    /**
     * Tries creating a null group. This should cause an exception.
     */
    public void testAddActionToGroupNullGroup()
    {
        try
        {
            store.addActionToGroup(TEST_ACTIONS[2], null);
            fail("Could use null as group name!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tries to add an unknown action to a group. This should cause an
     * exception.
     */
    public void testAddActionToGroupUnknownAction()
    {
        try
        {
            store.addActionToGroup("unknownAction", FILE_GROUP);
            fail("Could assign unknown action to group!");
        }
        catch (NoSuchElementException nex)
        {
            // ok
        }
    }

    /**
     * Tests whether an action is in the null group.
     */
    public void testIsActionInGroupNullGroup()
    {
        assertFalse("Found action in null group", store.isActionInGroup(
                TEST_ACTIONS[0], null));
    }

    /**
     * Tests whether an action is an unknown group.
     */
    public void testIsActionInGroupUnknownGroup()
    {
        assertFalse("Found action in unknown group", store.isActionInGroup(
                TEST_ACTIONS[0], "unknownGroup"));
    }

    /**
     * Tests whether a null action is in a group.
     */
    public void testIsActionInGroupNullAction()
    {
        store.addActionToGroup(TEST_ACTIONS[0], FILE_GROUP);
        assertFalse("Found null action in group", store.isActionInGroup(null,
                FILE_GROUP));
    }

    /**
     * Tests removing actions from groups.
     */
    public void testRemoveActionFromGroup()
    {
        store.addActionToGroup(TEST_ACTIONS[0], FILE_GROUP);
        store.addActionToGroup(TEST_ACTIONS[2], FILE_GROUP);
        assertTrue("Cannot remove action from group", store
                .removeActionFromGroup(TEST_ACTIONS[2], FILE_GROUP));
        assertFalse("Action still in group", store.isActionInGroup(
                TEST_ACTIONS[2], FILE_GROUP));
        store.addActionToGroup(TEST_ACTIONS[0], FILE_GROUP);
        assertTrue("Action not in group", store.isActionInGroup(
                TEST_ACTIONS[0], FILE_GROUP));
        assertTrue("Cannot remove action from group (2)", store
                .removeActionFromGroup(TEST_ACTIONS[0], FILE_GROUP));
        assertFalse("Action still in group (2)", store.isActionInGroup(
                TEST_ACTIONS[0], FILE_GROUP));
        assertTrue("Groups are not empty: " + store.getGroupNames(), store
                .getGroupNames().isEmpty());
    }

    /**
     * Tests removing actions from groups when either the action or the group
     * does not exist.
     */
    public void testRemoveActionFromGroupNonExisting()
    {
        assertFalse("Can remove null action from null group", store
                .removeActionFromGroup(null, null));
        assertFalse("Can remove null action from group", store
                .removeActionFromGroup(null, FILE_GROUP));
        assertFalse("Can remove action from null group", store
                .removeActionFromGroup(TEST_ACTIONS[0], null));
        assertFalse("Can remove action from non existing group", store
                .removeActionFromGroup(TEST_ACTIONS[0], FILE_GROUP));
    }

    /**
     * Tests removing a group.
     */
    public void testRemoveGroup()
    {
        store.addActionToGroup(TEST_ACTIONS[0], FILE_GROUP);
        assertTrue("Cannot remove group", store.removeGroup(FILE_GROUP));
        assertFalse("Can remove group twice", store.removeGroup(FILE_GROUP));
    }

    /**
     * Tries to remove the null group.
     */
    public void testRemoveGroupNull()
    {
        assertFalse("Could remove null group", store.removeGroup(null));
    }

    /**
     * Tries to remove an unknown group.
     */
    public void testRemoveGroupUnknown()
    {
        assertFalse("Could remove unknown group", store.removeGroup(FILE_GROUP));
    }

    /**
     * Tests accessing the names of the actions in a group.
     */
    public void testNamesInGroups()
    {
        for (int i = 0; i < TEST_ACTIONS.length; i++)
        {
            store.addActionToGroup(TEST_ACTIONS[i], FILE_GROUP);
        }
        store.addAction(new FormActionImpl("EditCut"));
        store.addActionToGroup("EditCut", "EditGroup");
        assertEquals("Wrong number of groups", 2, store.getGroupNames().size());

        Set<String> actions = store.getActionNamesForGroup(FILE_GROUP);
        assertEquals("Wrong number of group elements", TEST_ACTIONS.length,
                actions.size());
        for (String name : actions)
        {
            assertTrue("Action not found in group: " + name, store
                    .isActionInGroup(name, FILE_GROUP));
        }

        assertTrue("Cannot remove action", actions.remove(TEST_ACTIONS[0]));
        assertTrue("Action no more inn group", store.isActionInGroup(
                TEST_ACTIONS[0], FILE_GROUP));
        assertTrue("Cannot remove action from group", store
                .removeActionFromGroup(TEST_ACTIONS[0], FILE_GROUP));
        assertEquals("Wrong number of group elements (2)",
                TEST_ACTIONS.length - 1, store.getActionNamesForGroup(
                        FILE_GROUP).size());
    }

    /**
     * Tests the names in the null group.
     */
    public void testNamesInGroupsNullGroup()
    {
        assertTrue("Null group not empty", store.getActionNamesForGroup(null)
                .isEmpty());
    }

    /**
     * Tests the names in an unknown group.
     */
    public void testNamesInGroupsUnknown()
    {
        assertTrue(store.getActionNamesForGroup(FILE_GROUP).isEmpty());
    }

    /**
     * Tests enabling or disabling the actions in a group.
     */
    public void testEnableActions()
    {
        store.enableGroup(null, true);
        store.enableGroup(FILE_GROUP, false);

        for (int i = 0; i < TEST_ACTIONS.length; i++)
        {
            assertFalse("Action already enabled: " + TEST_ACTIONS[i], store
                    .getAction(TEST_ACTIONS[i]).isEnabled());
            store.addActionToGroup(TEST_ACTIONS[i], FILE_GROUP);
        }

        store.enableGroup(FILE_GROUP, true);
        for (int i = 0; i < TEST_ACTIONS.length; i++)
        {
            assertTrue("Action not enabled: " + TEST_ACTIONS[i], store
                    .getAction(TEST_ACTIONS[i]).isEnabled());
        }
    }

    /**
     * Adds some test actions to the given action store.
     *
     * @param as the store
     */
    private void setUpActions(ActionStore as)
    {
        setUpActions(as, null);
    }

    /**
     * Adds some test actions to the given action store using the specified
     * prefix.
     *
     * @param as the store
     * @param prefix the prefix to use for all actions
     */
    private void setUpActions(ActionStore as, String prefix)
    {
        for (int i = 0; i < TEST_ACTIONS.length; i++)
        {
            String name = (prefix == null) ? TEST_ACTIONS[i] : prefix
                    + TEST_ACTIONS[i];
            as.addAction(new FormActionImpl(name));
        }
    }

    /**
     * Helper method for checking access to an action.
     *
     * @param name the name of the action to check
     */
    private void checkFetchAction(String name)
    {
        FormAction action = store.getAction(name);
        assertNotNull("Cannot find action " + name, action);
        assertEquals("Wrong action name", name, action.getName());
    }

    /**
     * Helper method for testing the properties of a newly created action store.
     *
     * @param as the store to check
     */
    private void checkInitProperties(ActionStore as)
    {
        assertFalse("An action was found", as.hasAction("someAction"));
        try
        {
            as.getAction("someAction");
            fail("Could access non existing action!");
        }
        catch (NoSuchElementException nse)
        {
            // ok
        }
        assertTrue("Action names not empty", as.getActionNames().isEmpty());
        assertTrue("Group names not empty", as.getGroupNames().isEmpty());
        assertNull("Can remove action", as.removeAction("someAction"));
        assertFalse("Can remove action from group", as.removeActionFromGroup(
                "someAction", "someGroup"));
    }

    /**
     * Helper method for checking the results of a getActionNames() call.
     */
    private void checkActionNames()
    {
        Set<String> names = store.getActionNames();
        assertEquals("Wrong number of actions", TEST_ACTIONS.length, names
                .size());
        for (String s : TEST_ACTIONS)
        {
            assertTrue("Action not found: " + s, names.contains(s));
        }
    }
}

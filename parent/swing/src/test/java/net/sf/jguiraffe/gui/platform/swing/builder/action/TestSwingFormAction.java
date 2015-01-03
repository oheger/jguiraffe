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
package net.sf.jguiraffe.gui.platform.swing.builder.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import net.sf.jguiraffe.gui.builder.action.ActionTask;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.event.BuilderEvent;

import org.easymock.EasyMock;

import junit.framework.TestCase;

/**
 * Test class for SwingFormAction.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingFormAction.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingFormAction extends TestCase
{
    /** Constant for the name of the test action. */
    private static final String ACTION_NAME = "MyTestAction";

    /** The action to be tested. */
    private SwingFormAction action;

    /** Stores the task for the action. */
    private Object task;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        task = EasyMock.createMock(Runnable.class);
        action = new SwingFormAction(ACTION_NAME, (Runnable) task);
    }

    /**
     * Tests a newly created object.
     */
    public void testInit()
    {
        assertEquals("Wrong action name", ACTION_NAME, action.getName());
        assertFalse("Action is checked", action.isChecked());
        assertTrue("Action is not enabled", action.isEnabled());
        assertNull("Action has an icon", action.getValue(Action.SMALL_ICON));
        assertNull("Action has a name property", action.getValue(Action.NAME));
        assertSame("Wrong task", task, action.getTask());
    }

    /**
     * Tests setting and accessing properties of the action.
     */
    public void testSetProperties()
    {
        action.putValue(Action.NAME, "Action Text");
        assertEquals("Value could not be set", "Action Text", action
                .getValue(Action.NAME));
        assertEquals("Wrong action name", ACTION_NAME, action.getName());
        action.setChecked(true);
        assertTrue("Could not set checked state", action.isChecked());
        action.setEnabled(false);
        assertFalse("Could not disable action", action.isEnabled());
    }

    /**
     * Tests creating an action without a name. This should cause an exception.
     */
    public void testCreateNoName()
    {
        try
        {
            action = new SwingFormAction(null, task);
            fail("Could create action without a name!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests creating an action without a task. This should cause an exception.
     */
    public void testCreateErr()
    {
        try
        {
            action = new SwingFormAction(ACTION_NAME, null);
            fail("Could create action without a task!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests setting the task to an invalid object. This should cause an
     * exception.
     */
    public void testSetTaskInvalid()
    {
        try
        {
            action.setTask(new Object());
            fail("Could set invalid task!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests if the action's task gets executed if it is a Runnable.
     */
    public void testExecuteRunnable()
    {
        ((Runnable) action.getTask()).run();
        EasyMock.replay(action.getTask());
        action.actionPerformed(new ActionEvent(this, 42, "TestCommand"));
        EasyMock.verify(action.getTask());
    }

    /**
     * Tests if the action's task gets executed if it is an ActionTask.
     */
    public void testExecuteActionTask()
    {
        ActionTaskTestImpl t = new ActionTaskTestImpl();
        ActionEvent event = new ActionEvent(this, 42, "TestCommand");
        action.setTask(t);
        action.actionPerformed(event);
        t.verify(action, event);
    }

    /**
     * A test implementation of the ActionTask interface for testing whether the
     * task object is correctly invoked.
     */
    private static class ActionTaskTestImpl implements ActionTask
    {
        /** Stores the action. */
        private FormAction action;

        /** Stores the received event. */
        private BuilderEvent event;

        public void run(FormAction action, BuilderEvent event)
        {
            this.action = action;
            this.event = event;
        }

        /**
         * Tests whether the correct objects have been passed.
         *
         * @param expectedAction the expected action
         * @param expectedEvent the expected event object
         */
        public void verify(FormAction expectedAction, ActionEvent expectedEvent)
        {
            assertEquals("Wrong action", expectedAction, action);
            assertEquals("Wrong action event", expectedEvent, event.getSource());
        }
    }
}

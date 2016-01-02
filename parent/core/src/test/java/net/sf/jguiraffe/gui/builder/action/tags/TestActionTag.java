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
package net.sf.jguiraffe.gui.builder.action.tags;

import java.util.EnumSet;
import java.util.Set;

import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.impl.DefaultBeanContext;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.gui.builder.action.Accelerator;
import net.sf.jguiraffe.gui.builder.action.ActionData;
import net.sf.jguiraffe.gui.builder.action.ActionStore;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.event.Modifiers;

/**
 * Test class for ActionTag.
 *
 * @author Oliver Heger
 * @version $Id: TestActionTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestActionTag extends AbstractActionTagTest
{
    /** Constant for the name of the test script.*/
    private static final String SCRIPT = "action";

    /** Constant for an accelerator associated with an action. */
    private static final Accelerator ACCELERATOR = Accelerator.getInstance('s',
            EnumSet.of(Modifiers.CONTROL));

    /** Constant for the name of the accelerator variable. */
    private static final String VAR_ACC = "varAccelerator";

    /**
     * Performs special setup for the Jelly context. This implementation inserts
     * a bean context with some tasks.
     */
    @Override
    protected void setUpJelly()
    {
        super.setUpJelly();
        DefaultBeanStore store = new DefaultBeanStore();
        store.addBeanProvider(TEST_TASK, ConstantBeanProvider
                .getInstance(new MyActionTask()));
        store.addBeanProvider(VAR_ACC, ConstantBeanProvider
                .getInstance(ACCELERATOR));
        builderData.setBeanContext(new DefaultBeanContext(store));
    }

    /**
     * Tests processing valid action tags.
     */
    public void testCreateActions() throws Exception
    {
        executeScript(SCRIPT);

        FormAction action1 = checkAction(
                "Action Action1 {  TEXT = MyAction ICON = ICON [ "
                        + iconLocatorString()
                        + " ] TOOLTIP = An action MNEMO = M "
                        + "TASK = class net.sf.jguiraffe.gui.builder.action.tags.TestActionTag$MyActionTask }",
                "Action1");
        checkAction(
                "Action FileOpen {  TEXT = File open TOOLTIP = Opens a file "
                        + "MNEMO = o ACC = CONTROL o "
                        + "TASK = class net.sf.jguiraffe.gui.builder.action.tags.TestActionTag$MyActionTask }",
                "FileOpen");
        FormAction saveAction = checkAction(
                "Action FileSave {  TEXT = File save TOOLTIP = Saves the "
                        + "current file MNEMO = s ACC = " + ACCELERATOR
                        + " TASK = class net.sf.jguiraffe.gui.builder.action.tags.TestActionTag$MyActionTask }",
                "FileSave");
        checkAction(
                "Action FileClose {  TEXT = Close "
                        + "TASK = class net.sf.jguiraffe.gui.builder.action.tags.TestActionTag$MyActionTask }",
                "FileClose");

        assertTrue("Action not per default enabled", action1.isEnabled());
        assertFalse("Action not disabled", saveAction.isEnabled());
    }

    /**
     * Tests an action without a name.
     */
    public void testNoName() throws Exception
    {
        errorScript(SCRIPT, "ERR_NO_NAME", "Could create action without name!");
    }

    /**
     * Tests an undefined action (without text or icon).
     */
    public void testUndefined() throws Exception
    {
        errorScript(SCRIPT, "ERR_UNDEF", "Could create undefined action!");
    }

    /**
     * Tests an action without a task definition.
     */
    public void testNoTask() throws Exception
    {
        errorScript(SCRIPT, "ERR_NO_TASK", "Could create action without task!");
    }

    /**
     * Tests an action with an invalid task name.
     */
    public void testInvalidTask() throws Exception
    {
        builderData.setBuilderName("ERR_INV_TASK");
        executeScript(SCRIPT);
        try
        {
            builderData.invokeCallBacks();
            fail("Invalid task not detected!");
        }
        catch (InjectionException iex)
        {
            // ok
        }
    }

    /**
     * Tests an action with an invalid task class.
     */
    public void testInvalidTaskClass() throws Exception
    {
        errorScript(SCRIPT, "ERR_INV_TASKCLS",
                "Could create action with invalid task class!");
    }

    /**
     * Tests an action with multiple task definitions.
     */
    public void testMultiTasks() throws Exception
    {
        errorScript(SCRIPT, "ERR_MULTI_TASK",
                "Could create action with multiple tasks!");
    }

    /**
     * Tests using a task class that cannot be instantiated.
     */
    public void testInstantiationEx() throws Exception
    {
        errorScript(SCRIPT, "ERR_TASK_INST", "Could create Integer task!");
    }

    /**
     * Tests using a task class that cannot be accessed.
     */
    public void testAccessEx() throws Exception
    {
        errorScript(SCRIPT, "ERR_TASK_ACCESS",
                "Could create task with private constructor!");
    }

    /**
     * Tests using an incorrectly nested task tag.
     */
    public void testTaskTagNested() throws Exception
    {
        errorScript(SCRIPT, "ERR_TASKTAG_NESTED",
                "Could process incorrectly nested task tag!");
    }

    /**
     * Tests an invalid accelerator definition.
     */
    public void testAcceleratorDefInvalid() throws Exception
    {
        errorScript(SCRIPT, "ERR_ACC_INV",
                "Could process invalid accelerator definition!");
    }

    /**
     * Tests a non existing accelerator reference.
     */
    public void testAcceleratorRefNonExisting() throws Exception
    {
        errorScript(SCRIPT, "ERR_ACC_NONEX",
                "Could process non existing accelerator reference!");
    }

    /**
     * Tests an action with too many accelerator specifications.
     */
    public void testTooManyAccelerators() throws Exception
    {
        errorScript(SCRIPT, "ERR_ACC_BOTH",
                "Could process ambigous accelerator definitions!");
    }

    /**
     * Tests whether actions can be added to groups.
     */
    public void testActionGroup() throws Exception
    {
        executeScript(SCRIPT);
        ActionStore store = actionBuilder.getActionStore();
        final String groupName = "fileActions";
        assertTrue("fileActions group not found", store.getGroupNames()
                .contains(groupName));
        assertEquals("Too many groups", 1, store.getGroupNames().size());
        Set<String> actNames = store.getActionNamesForGroup(groupName);
        assertTrue("Action not in group: FileOpen", actNames
                .contains("FileOpen"));
        assertTrue("Action not in group: FileSave", actNames
                .contains("FileSave"));
    }

    /**
     * Tests whether an action data object can be created.
     */
    public void testCreateActionData() throws Exception
    {
        executeScript(SCRIPT);
        ActionData data = (ActionData) context.getVariable("actionData");
        assertEquals("Wrong name", "Action1", data.getName());
        assertEquals("Wrong tool tip", "An action", data.getToolTip());
        assertEquals("Wrong text", "MyAction", data.getText());
        assertNotNull("No icon", data.getIcon());
        assertNull("Got a task", data.getTask());
    }

    /**
     * Tests an action data tag without a var attribute.
     */
    public void testCreateActionDataNoVar() throws Exception
    {
        errorScript(SCRIPT, "ERR_ACTDATA_NOVAR",
                "Missing var attribute not detected!");
    }

    /**
     * A dummy task class for testing purposes.
     */
    public static class MyActionTask implements Runnable
    {
        public void run()
        {
        }
    }

    /**
     * A task class with a private constructor for testing illegal access
     * exceptions when creating task instances by class.
     */
    public static class MyIllegalAccessTask extends MyActionTask
    {
        private MyIllegalAccessTask()
        {
            super();
        }
    }
}

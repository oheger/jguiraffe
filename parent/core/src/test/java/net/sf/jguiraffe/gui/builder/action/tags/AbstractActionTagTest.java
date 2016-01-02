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
package net.sf.jguiraffe.gui.builder.action.tags;

import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionManagerImpl;
import net.sf.jguiraffe.gui.builder.action.ActionStore;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.components.tags.AbstractTagTest;

/**
 * <p>
 * A base class for testing Jelly scripts with action tags.
 * </p>
 * <p>
 * This class serves as a base class for tests of the action builder component.
 * It plays an analogous role as the ancestor class for the form builder
 * library. The inherited functionality is extended to deal with the action tag
 * library, too.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AbstractActionTagTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractActionTagTest extends AbstractTagTest
{
    /** Constant for the name of the test task in the task factory. */
    public static final String TEST_TASK = "testTask";

    /** Stores the action builder instance. */
    protected ActionBuilder actionBuilder;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        setUpActionBuilder();
    }

    /**
     * Sets up the Jelly context and related instances. This implementation also
     * takes the action tag library into account.
     */
    @Override
    protected void setUpJelly()
    {
        super.setUpJelly();
        context.registerTagLibrary("actionBuilder",
                new ActionBuilderTagLibrary());
    }

    /**
     * Initializes an action builder instance and stores it in the Jelly
     * context.
     */
    protected void setUpActionBuilder()
    {
        actionBuilder = new ActionBuilder();
        actionBuilder.setActionManager(new ActionManagerImpl());
        actionBuilder.setActionStore(new ActionStore());
        actionBuilder.put(context);
    }

    /**
     * Compares the action's string representation with the expected value.
     *
     * @param expected the expected value
     * @param action the action to check
     */
    protected void checkAction(String expected, FormAction action)
    {
        assertEquals("Comparing action " + action.getName() + "\n" + expected
                + "\n" + action, expected, action.toString());
    }

    /**
     * Compares the string representation of an action with an expected value.
     * The action is fetched from the current action store.
     *
     * @param expected the expected text
     * @param name the name of the action
     * @return the action that has been checked
     */
    protected FormAction checkAction(String expected, String name)
    {
        assertTrue(actionBuilder.getActionStore().hasAction(name));
        FormAction action = actionBuilder.getActionStore().getAction(name);
        assertNotNull(action);
        checkAction(expected, action);
        return action;
    }
}

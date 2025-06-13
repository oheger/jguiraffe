/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.enablers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionStore;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.action.FormActionImpl;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@link ActionGroupEnabler}.
 *
 * @author Oliver Heger
 * @version $Id: TestActionGroupEnabler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestActionGroupEnabler
{
    /** Constant for the prefix of an action name. */
    private static final String ACT_PREFIX = "TestAction";

    /** Constant for the name of the action group. */
    private static final String GROUP = "TestGroup";

    /** Constant for the number of test actions. */
    private static final int COUNT = 12;

    /**
     * Tests creating an instance without a group name. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoGroupName()
    {
        new ActionGroupEnabler(null);
    }

    /**
     * Tests the enabled state of all actions of the test group.
     *
     * @param store the action store
     * @param expectedState the expected enabled state
     */
    private void checkActionGroup(ActionStore store, boolean expectedState)
    {
        for (int i = 0; i < COUNT; i++)
        {
            FormAction action = store.getAction(ACT_PREFIX + i);
            assertEquals("Wrong enabled state for action " + i, expectedState,
                    action.isEnabled());
        }
    }

    /**
     * Tests setting the enabled state of a group.
     */
    @Test
    public void testSetEnabledState() throws FormBuilderException
    {
        BeanContext context = EasyMock.createMock(BeanContext.class);
        ActionStore store = new ActionStore();
        EasyMock.expect(context.getBean(ActionBuilder.KEY_ACTION_STORE))
                .andReturn(store).times(2);
        EasyMock.replay(context);
        for (int i = 0; i < COUNT; i++)
        {
            FormActionImpl action = new FormActionImpl(ACT_PREFIX + i);
            action.setEnabled(true);
            store.addAction(action);
            store.addActionToGroup(ACT_PREFIX + i, GROUP);
        }
        FormActionImpl anotherAction = new FormActionImpl(ACT_PREFIX);
        anotherAction.setEnabled(true);
        store.addAction(anotherAction);
        ComponentBuilderData compData = new ComponentBuilderData();
        compData.setBeanContext(context);
        ActionGroupEnabler enabler = new ActionGroupEnabler(GROUP);
        enabler.setEnabledState(compData, false);
        checkActionGroup(store, false);
        assertTrue("Other action was changed (1)", anotherAction.isEnabled());
        enabler.setEnabledState(compData, true);
        checkActionGroup(store, true);
        assertTrue("Other action was changed (2)", anotherAction.isEnabled());
    }
}

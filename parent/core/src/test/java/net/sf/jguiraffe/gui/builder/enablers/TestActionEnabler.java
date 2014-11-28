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
package net.sf.jguiraffe.gui.builder.enablers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@link ActionEnabler}.
 *
 * @author Oliver Heger
 * @version $Id: TestActionEnabler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestActionEnabler
{
    /** Constant for the name of a test action. */
    private static final String ACTION_NAME = "TestAction";

    /**
     * Tests creating an instance without an action name. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoActionName()
    {
        new ActionEnabler(null);
    }

    /**
     * Tests setting the enabled state on an existing action.
     */
    @Test
    public void testSetEnabledState() throws FormBuilderException
    {
        BeanContext context = EasyMock.createMock(BeanContext.class);
        FormAction action = EasyMock.createStrictMock(FormAction.class);
        EasyMock.expect(
                context.getBean(ActionBuilder.KEY_ACTION_PREFIX + ACTION_NAME))
                .andReturn(action).times(2);
        action.setEnabled(true);
        action.setEnabled(false);
        EasyMock.replay(context, action);
        ComponentBuilderData compData = new ComponentBuilderData();
        compData.setBeanContext(context);
        ActionEnabler enabler = new ActionEnabler(ACTION_NAME);
        enabler.setEnabledState(compData, true);
        enabler.setEnabledState(compData, false);
        EasyMock.verify(context, action);
    }

    /**
     * Tests the enabler's behavior when the action cannot be resolved. In this
     * case an exception should be thrown.
     */
    @Test
    public void testSetEnabledStateUnknownAction()
    {
        BeanContext context = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(
                context.getBean(ActionBuilder.KEY_ACTION_PREFIX + ACTION_NAME))
                .andThrow(new InjectionException());
        EasyMock.replay(context);
        ComponentBuilderData compData = new ComponentBuilderData();
        compData.setBeanContext(context);
        ActionEnabler enabler = new ActionEnabler(ACTION_NAME);
        try
        {
            enabler.setEnabledState(compData, true);
            fail("Exception not thrown!");
        }
        catch (FormBuilderException fex)
        {
            EasyMock.verify(context);
        }
    }

    /**
     * Tests querying the name of the associated action.
     */
    @Test
    public void testGetActionName()
    {
        ActionEnabler enabler = new ActionEnabler(ACTION_NAME);
        assertEquals("Wrong action name", ACTION_NAME, enabler.getActionName());
    }
}

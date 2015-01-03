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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.builder.action.ActionHelper;
import net.sf.jguiraffe.gui.builder.action.ActionTask;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.event.BuilderEvent;
import net.sf.jguiraffe.gui.forms.BindingStrategy;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * A test class for {@code ActionTag} that tests the handling of deferred action
 * tasks. These are tasks that cannot be created immediately due to dependencies
 * to other beans.
 *
 * @author Oliver Heger
 * @version $Id: TestActionTagDeferredTasks.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestActionTagDeferredTasks
{
    /** Constant for the name of the task bean. */
    private static final String BEAN_NAME = "myTestTaskBean";

    /** The component builder data object. */
    private ComponentBuilderData builderData;

    /** The tag to be tested. */
    private ActionTag tag;

    @Before
    public void setUp() throws Exception
    {
        JellyContext context = new JellyContext();
        builderData = new ComponentBuilderData();
        builderData.put(context);
        BeanContext bc = EasyMock.createMock(BeanContext.class);
        builderData.setBeanContext(bc);
        builderData.initializeForm(new TransformerContextImpl(), EasyMock
                .createNiceMock(BindingStrategy.class));
        tag = new ActionTag();
        tag.setContext(context);
    }

    /**
     * Tests whether the task bean specified by name is returned if it is
     * available immediately.
     */
    @Test
    public void testCreateTaskBeanNameAvailable() throws JellyTagException,
            FormBuilderException
    {
        BeanContext bc = builderData.getBeanContext();
        Runnable task = EasyMock.createMock(Runnable.class);
        EasyMock.expect(bc.containsBean(BEAN_NAME)).andReturn(Boolean.TRUE);
        EasyMock.expect(bc.getBean(BEAN_NAME)).andReturn(task);
        EasyMock.replay(bc, task);
        assertEquals("Wrong task bean", task, tag.createTask(BEAN_NAME, null,
                null));
        EasyMock.verify(bc, task);
    }

    /**
     * Tests whether a task bean specified by its class is returned if it is
     * available immediately.
     */
    @Test
    public void testCreateTaskBeanClassAvailable() throws JellyTagException,
            FormBuilderException
    {
        BeanContext bc = builderData.getBeanContext();
        ActionTask task = EasyMock.createMock(ActionTask.class);
        EasyMock.expect(bc.containsBean(ActionTask.class)).andReturn(
                Boolean.TRUE);
        EasyMock.expect(bc.getBean(ActionTask.class)).andReturn(task);
        EasyMock.replay(bc, task);
        assertEquals("Wrong task bean", task, tag.createTask(null,
                ActionTask.class, null));
        EasyMock.verify(bc, task);
    }

    /**
     * Tests task creation for a bean specified by name that is not available.
     */
    @Test
    public void testCreateTaskBeanNameDeferred() throws JellyTagException,
            FormBuilderException
    {
        BeanContext bc = builderData.getBeanContext();
        EasyMock.expect(bc.containsBean(BEAN_NAME)).andReturn(Boolean.FALSE);
        EasyMock.replay(bc);
        assertTrue("Wrong task",
                tag.createTask(BEAN_NAME, null, null) instanceof ActionTask);
        EasyMock.verify(bc);
    }

    /**
     * Tests task creation for a bean specified by its class that is not
     * available.
     */
    @Test
    public void testCreateTaskBeanClassDeferred() throws JellyTagException,
            FormBuilderException
    {
        BeanContext bc = builderData.getBeanContext();
        EasyMock.expect(bc.containsBean(ActionTask.class)).andReturn(
                Boolean.FALSE);
        EasyMock.replay(bc);
        assertTrue(
                "Wrong task",
                tag.createTask(null, ActionTask.class, null) instanceof ActionTask);
        EasyMock.verify(bc);
    }

    /**
     * Tests whether a task for a deferred bean specified by its name can be
     * correctly invoked.
     */
    @Test
    public void testCreateTaskBeanNameInvoke() throws JellyTagException,
            FormBuilderException
    {
        BeanContext bc = builderData.getBeanContext();
        Runnable task = EasyMock.createMock(Runnable.class);
        EasyMock.expect(bc.containsBean(BEAN_NAME)).andReturn(Boolean.FALSE);
        EasyMock.expect(bc.getBean(BEAN_NAME)).andReturn(task);
        task.run();
        EasyMock.replay(bc, task);
        Object actTask = tag.createTask(BEAN_NAME, null, null);
        builderData.invokeCallBacks();
        ActionHelper.invokeActionTask(actTask, null, null);
        EasyMock.verify(bc, task);
    }

    /**
     * Tests whether a task for a deferred bean specified by its class can be
     * correctly invoked.
     */
    @Test
    public void testCreateTaskBeanClassInvoke() throws JellyTagException,
            FormBuilderException
    {
        BeanContext bc = builderData.getBeanContext();
        ActionTask task = EasyMock.createMock(ActionTask.class);
        FormAction act = EasyMock.createMock(FormAction.class);
        BuilderEvent event = new BuilderEvent(this);
        EasyMock.expect(bc.containsBean(ActionTask.class)).andReturn(
                Boolean.FALSE);
        EasyMock.expect(bc.getBean(ActionTask.class)).andReturn(task);
        task.run(act, event);
        EasyMock.replay(bc, task, act);
        Object actTask = tag.createTask(null, ActionTask.class, null);
        builderData.invokeCallBacks();
        ActionHelper.invokeActionTask(actTask, act, event);
        EasyMock.verify(bc, task, act);
    }

    /**
     * Tests the behavior of the action task wrapper if an invalid task is
     * specified. This should cause an exception when invoking the call backs.
     */
    @Test
    public void testCreateTaskInvokeInvalid() throws JellyTagException,
            FormBuilderException
    {
        BeanContext bc = builderData.getBeanContext();
        EasyMock.expect(bc.containsBean(BEAN_NAME)).andReturn(Boolean.FALSE);
        EasyMock.expect(bc.getBean(BEAN_NAME)).andReturn(this);
        EasyMock.replay(bc);
        tag.createTask(BEAN_NAME, null, null);
        try
        {
            builderData.invokeCallBacks();
            fail("Invalid task not detected!");
        }
        catch (FormBuilderException fex)
        {
            EasyMock.verify(bc);
        }
    }
}

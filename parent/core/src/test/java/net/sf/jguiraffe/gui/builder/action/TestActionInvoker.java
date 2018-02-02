/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

import static org.junit.Assert.assertNull;
import net.sf.jguiraffe.gui.builder.event.BuilderEvent;
import net.sf.jguiraffe.gui.builder.event.FormActionEvent;
import net.sf.jguiraffe.gui.builder.event.FormActionListener;
import net.sf.jguiraffe.gui.builder.event.filter.EventFilter;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;
import net.sf.jguiraffe.gui.builder.window.WindowImpl;
import net.sf.jguiraffe.gui.builder.window.WindowListener;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for ActionInvoker.
 *
 * @author Oliver Heger
 * @version $Id: TestActionInvoker.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestActionInvoker
{
    /** Constant for the test event. */
    static final BuilderEvent EVENT = new BuilderEvent(TestActionInvoker.class);

    /** Constant for the test action event. */
    static final FormActionEvent ACT_EVENT = new FormActionEvent(
            TestActionInvoker.class, null, null, "TEST");

    /**
     * Helper method for initializing the action mock. The action expects that
     * its enabled property is checked, then the execute() method should be
     * invoked.
     *
     * @param event the event to be passed to the action
     * @return the initialized action control
     */
    private FormAction setUpActionMock(BuilderEvent event)
    {
        return setUpActionMock(new BuilderEvent[]
        { event });
    }

    /**
     * Initializes the action mock for being called for multiple events.
     *
     * @param events an array with the expected events
     * @return the initialized action mock control
     */
    private FormAction setUpActionMock(BuilderEvent[] events)
    {
        FormAction action = EasyMock.createMock(FormAction.class);
        for (int i = 0; i < events.length; i++)
        {
            EasyMock.expect(action.isEnabled()).andReturn(Boolean.TRUE);
            action.execute(events[i]);
        }
        EasyMock.replay(action);
        return action;
    }

    /**
     * Tests a "normal" invocation when an event object is passed.
     */
    @Test
    public void testInvokeWithEvent() throws Throwable
    {
        FormAction mockAction = setUpActionMock(EVENT);
        EventFilter mockFilter = EasyMock.createMock(EventFilter.class);
        EasyMock.expect(mockFilter.accept(EVENT)).andReturn(Boolean.TRUE);
        EasyMock.replay(mockFilter);

        ActionInvoker invoker = new ActionInvoker(mockAction, mockFilter);
        Object[] args = new Object[]
        { EVENT };
        assertNull("Wrong result of invoke", invoker.invoke(this, null, args));
        EasyMock.verify(mockAction, mockFilter);
    }

    /**
     * Tests an invocation when a method without parameters was called.
     *
     * @param args the parameters array
     */
    private void checkInvokeWithNoParams(Object[] args) throws Throwable
    {
        FormAction mockAction = setUpActionMock((BuilderEvent) null);
        EventFilter mockFilter = EasyMock.createMock(EventFilter.class);
        EasyMock.expect(mockFilter.accept(null)).andReturn(Boolean.TRUE);
        EasyMock.replay(mockFilter);
        ActionInvoker invoker = new ActionInvoker(mockAction, mockFilter);
        assertNull("Wrong result of invoke", invoker.invoke(this, null, args));
        EasyMock.verify(mockAction, mockFilter);
    }

    /**
     * Tests an invocation when a parameter array with 0 elements is passed.
     */
    @Test
    public void testInvokeWithNoParameters() throws Throwable
    {
        checkInvokeWithNoParams(new Object[0]);
    }

    /**
     * Tests an invocation when a null parameter array is passed.
     */
    @Test
    public void testInvokeWithNullParameters() throws Throwable
    {
        checkInvokeWithNoParams(null);
    }

    /**
     * Tests an invocation when the parameter is not an event.
     */
    @Test
    public void testInvokeWithNoEvent() throws Throwable
    {
        FormAction mockAction = setUpActionMock((BuilderEvent) null);
        ActionInvoker invoker = new ActionInvoker(mockAction);
        invoker.invoke(this, null, new Object[]
        { "test" });
        EasyMock.verify(mockAction);
    }

    /**
     * Tests creating an invoker with a null action. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullAction()
    {
        ActionInvoker.create(FormActionListener.class, null);
    }

    /**
     * Tests an invocation with no filter. Then all events should trigger the
     * action.
     */
    @Test
    public void testCreateWithNoFilter()
    {
        FormAction mockAction = setUpActionMock(ACT_EVENT);
        FormActionListener l = (FormActionListener) ActionInvoker.create(
                FormActionListener.class, mockAction);
        l.actionPerformed(ACT_EVENT);
        EasyMock.verify(mockAction);
    }

    /**
     * Tests an invocation when a filter is provided, which accepts the event.
     */
    @Test
    public void testCreateWithFilter()
    {
        FormAction mockAction = setUpActionMock(ACT_EVENT);
        EventFilter mockFilter = EasyMock.createMock(EventFilter.class);
        EasyMock.expect(mockFilter.accept(ACT_EVENT)).andReturn(Boolean.TRUE);
        EasyMock.replay(mockFilter);
        FormActionListener l = (FormActionListener) ActionInvoker.create(
                FormActionListener.class, mockAction, mockFilter);
        l.actionPerformed(ACT_EVENT);
        EasyMock.verify(mockAction, mockFilter);
    }

    /**
     * Tests an invocation when the filter does not accept the event.
     */
    @Test
    public void testCreateWithNonAcceptingFilter()
    {
        FormAction mockAction = EasyMock.createMock(FormAction.class);
        EventFilter mockFilter = EasyMock.createMock(EventFilter.class);
        EasyMock.expect(mockFilter.accept(ACT_EVENT)).andReturn(Boolean.FALSE);
        EasyMock.replay(mockAction, mockFilter);
        FormActionListener l = (FormActionListener) ActionInvoker.create(
                FormActionListener.class, mockAction, mockFilter);
        l.actionPerformed(ACT_EVENT);
        EasyMock.verify(mockAction, mockFilter);
    }

    /**
     * Tests an invocation when the action is disabled.
     */
    @Test
    public void testDisabledAction()
    {
        FormAction mockAction = EasyMock.createMock(FormAction.class);
        EasyMock.expect(mockAction.isEnabled()).andReturn(Boolean.FALSE);
        EasyMock.replay(mockAction);
        FormActionListener l = (FormActionListener) ActionInvoker.create(
                FormActionListener.class, mockAction);
        l.actionPerformed(ACT_EVENT);
        EasyMock.verify(mockAction);
    }

    /**
     * Tests creating an invoker that implements multiple interfaces.
     */
    @Test
    public void testCreateMultipleInterfaces()
    {
        WindowEvent we = new WindowEvent(this, new WindowImpl(),
                WindowEvent.Type.WINDOW_OPENED);
        FormAction mockAction = setUpActionMock(new BuilderEvent[]
        { ACT_EVENT, we });
        FormActionListener l = (FormActionListener) ActionInvoker.create(
                new Class[]
                { FormActionListener.class, WindowListener.class },
                mockAction, null);
        l.actionPerformed(ACT_EVENT);
        WindowListener wl = (WindowListener) l;
        wl.windowOpened(we);
        EasyMock.verify(mockAction);
    }

    /**
     * Tests creating an invoker when no class array is specified. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullInterfaces()
    {
        FormAction mockAction = EasyMock.createMock(FormAction.class);
        ActionInvoker.create((Class[]) null, mockAction, null);
    }
}

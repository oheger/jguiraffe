/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.EventListener;

import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionStore;
import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.builder.action.FormActionImpl;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.model.TreeExpansionListener;
import net.sf.jguiraffe.gui.builder.event.FormActionListener;
import net.sf.jguiraffe.gui.builder.event.FormMouseListener;
import net.sf.jguiraffe.gui.builder.event.filter.ClassEventFilter;
import net.sf.jguiraffe.gui.builder.event.filter.EventFilter;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for EventListenerTag. Tests the basic functionality provided by
 * this abstract base class.
 *
 * @author Oliver Heger
 * @version $Id: TestEventListenerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestEventListenerTag
{
    /** Constant for the name of the test action. */
    private static final String TEST_ACTION = "TEST_ACTION";

    /** Constant for the name of the test filter. */
    private static final String TEST_FILTER = "TEST_FILTER";

    /** Constant for a test variable name. */
    private static final String VARIABLE = "myTestVariable";

    /** The tag to be tested. */
    private EventListenerTagImpl tag;

    /** Stores the Jelly context. */
    private JellyContext context;

    @Before
    public void setUp() throws Exception
    {
        context = new JellyContext();
        ActionBuilder builder = new ActionBuilder();
        ActionStore store = new ActionStore();
        builder.setActionStore(store);
        FormActionImpl action = new FormActionImpl(TEST_ACTION);
        store.addAction(action);
        builder.put(context);

        tag = new EventListenerTagImpl();
        tag.setContext(context);
    }

    /**
     * Tests creating an event listener for the specified action.
     */
    @Test
    public void testCreateEventListener() throws JellyTagException,
            FormBuilderException
    {
        tag.setActionName(TEST_ACTION);
        assertTrue("Wrong listener proxy", tag
                .createEventListener(new Class<?>[] {
                    FormActionListener.class
                }) instanceof FormActionListener);
    }

    /**
     * Tests whether a listener supporting multiple interfaces is possible.
     */
    @Test
    public void testCreateEventListenerClasses() throws JellyTagException,
            FormBuilderException
    {
        final Class<?>[] listenerClasses = {
                FormActionListener.class, FormMouseListener.class,
                TreeExpansionListener.class
        };
        tag.setActionName(TEST_ACTION);
        Object proxy = tag.createEventListener(listenerClasses);
        for (Class<?> c : listenerClasses)
        {
            assertTrue("Interface not implemented: " + c, c.isInstance(proxy));
        }
    }

    /**
     * Tests behavior if no action is specified. This should cause an exception.
     */
    @Test(expected = MissingAttributeException.class)
    public void testCreateEventListenerMissingActionAttribute()
            throws JellyTagException, FormBuilderException
    {
        tag.createEventListener(new Class<?>[] {
            FormActionListener.class
        });
    }

    /**
     * Tries to create an event listener if the action name is wrong.
     */
    @Test(expected = FormActionException.class)
    public void testCreateEventListenerInvalidAction()
            throws JellyTagException, FormBuilderException
    {
        tag.setActionName("non existing action");
        tag.createEventListener(new Class<?>[] {
            FormActionListener.class
        });
    }

    /**
     * Tests fetching a filter if none is defined.
     */
    @Test
    public void testFetchFilterNull() throws JellyTagException
    {
        assertNull("Filter was returned", tag.fetchFilter());
    }

    /**
     * Tests if a filter set previously is reset.
     */
    @Test
    public void testFetchFilterReset() throws JellyTagException,
            FormBuilderException
    {
        context.setVariable(EventFilterTag.CURRENT_FILTER,
                new ClassEventFilter());
        tag.processBeforeBody();
        assertNull("Current filter was not removed", tag.fetchFilter());
    }

    /**
     * Tests fetching a filter from the current filter variable.
     */
    @Test
    public void testFetchCurrentFilter() throws JellyTagException,
            FormBuilderException
    {
        tag.processBeforeBody();
        EventFilter filter = new ClassEventFilter();
        context.setVariable(EventFilterTag.CURRENT_FILTER, filter);
        assertSame("Current filter was not found", filter, tag.fetchFilter());

        tag.setEventFilter("a filter name");
        assertSame("Current filter was not found when filter name is set",
                filter, tag.fetchFilter());
    }

    /**
     * Tests fetching a filter defined by the event filter attribute.
     */
    @Test
    public void testFetchFilterByName() throws JellyTagException,
            FormBuilderException
    {
        tag.process();
        EventFilter filter = new ClassEventFilter();
        context.setVariable(TEST_FILTER, filter);
        tag.setEventFilter(TEST_FILTER);
        assertSame("Named filter was not found", filter, tag.fetchFilter());
    }

    /**
     * Tests fetching a non existing filter. This should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testFetchFilterNonExisting() throws JellyTagException,
            FormBuilderException
    {
        tag.process();
        tag.setEventFilter("non existing filter");
        tag.fetchFilter();
    }

    /**
     * Tests the process() implementation.
     */
    @Test
    public void testProcess() throws JellyTagException, FormBuilderException
    {
        tag.process();
        assertTrue("registerListener() was not invoked", tag.invoked);
    }

    /**
     * Tests process() if no target can be determined.
     */
    @Test(expected = JellyTagException.class)
    public void testProcessNoTarget() throws JellyTagException,
            FormBuilderException
    {
        tag.registrationResult = false;
        tag.process();
    }

    /**
     * Tests whether the event listener can be assigned to a variable.
     */
    @Test
    public void testVariable() throws JellyTagException, FormBuilderException
    {
        EventListener listener = EasyMock.createMock(EventListener.class);
        EasyMock.replay(listener);
        tag.listener = listener;
        tag.setVar(VARIABLE);
        tag.process();
        assertSame("Listener not bound to variable", listener, context
                .getVariable(VARIABLE));
        EasyMock.verify(listener);
    }

    /**
     * Test implementation of the event listener tag.
     */
    private static class EventListenerTagImpl extends EventListenerTag
    {
        /** A listener object to be returned by createListener(). */
        EventListener listener;

        /** A flag whether createAndRegisterListener() was invoked. */
        boolean invoked;

        /** A flag about the result of the registration (true per default). */
        boolean registrationResult = true;

        /**
         * Returns the mock event listener.
         */
        @Override
        protected EventListener createEventListener() throws JellyTagException,
                FormBuilderException
        {
            return listener;
        }

        /**
         * Records this invocation and returns the test listener.
         */
        @Override
        protected boolean registerListener(EventListener listener)
                throws JellyTagException, FormBuilderException
        {
            invoked = true;
            return registrationResult;
        }
    }
}

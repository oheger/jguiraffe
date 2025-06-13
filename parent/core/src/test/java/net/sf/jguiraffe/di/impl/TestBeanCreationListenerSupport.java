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
package net.sf.jguiraffe.di.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanCreationEvent;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.DependencyProvider;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link BeanCreationListenerSupport}.
 *
 * @author Oliver Heger
 * @version $Id: TestBeanCreationListenerSupport.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestBeanCreationListenerSupport
{
    /** Constant for a test bean. */
    private static final Object TEST_BEAN = new Object();

    /** A mock for the associated bean context. */
    private BeanContext context;

    /** The support object to be tested. */
    private BeanCreationListenerSupport support;

    @Before
    public void setUp() throws Exception
    {
        context = EasyMock.createNiceMock(BeanContext.class);
        support = new BeanCreationListenerSupport(context);
    }

    /**
     * Tests a newly created instance.
     */
    @Test
    public void testInit()
    {
        assertEquals("Wrong associated context", context, support.getContext());
        assertTrue("Already listeners registered", support
                .getCreationListeners().isEmpty());
    }

    /**
     * Tests adding a null bean creation listener. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBeanCreationListenerNull()
    {
        support.addBeanCreationListener(null);
    }

    /**
     * Tests adding a bean creation listener.
     */
    @Test
    public void testAddBeanCreationListener()
    {
        BeanCreationListenerTestImpl l = new BeanCreationListenerTestImpl();
        support.addBeanCreationListener(l);
        assertEquals("Wrong number of listeners", 1, support
                .getCreationListeners().size());
        assertEquals("Wrong listener", l, support.getCreationListeners().get(0));
    }

    /**
     * Tests removing a bean creation listener.
     */
    @Test
    public void testRemoveBeanCreationListener()
    {
        BeanCreationListenerTestImpl l = new BeanCreationListenerTestImpl();
        support.addBeanCreationListener(l);
        support.addBeanCreationListener(new BeanCreationListenerTestImpl());
        assertTrue("Cannot remove listener", support
                .removeBeanCreationListener(l));
        assertEquals("Wrong number of listeners", 1, support
                .getCreationListeners().size());
        assertFalse("Listener not removed", support.getCreationListeners()
                .contains(l));
    }

    /**
     * Tests removing an unknown bean listener.
     */
    @Test
    public void testRemoveBeanCreationListenerUnknown()
    {
        support.addBeanCreationListener(new BeanCreationListenerTestImpl());
        assertFalse("Wrong result for unknown listener", support
                .removeBeanCreationListener(new BeanCreationListenerTestImpl()));
        assertEquals("Wrong number of listeners", 1, support
                .getCreationListeners().size());
    }

    /**
     * Tests firing a bean creation event.
     */
    @Test
    public void testFireBeanCreationEvent()
    {
        BeanProvider beanProvider = EasyMock.createMock(BeanProvider.class);
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(beanProvider, depProvider);
        BeanCreationListenerTestImpl l1 = new BeanCreationListenerTestImpl();
        BeanCreationListenerTestImpl l2 = new BeanCreationListenerTestImpl();
        support.addBeanCreationListener(l1);
        support.addBeanCreationListener(l2);
        support.fireBeanCreationEvent(beanProvider, depProvider, TEST_BEAN);
        l1.checkNextEvent(context, TEST_BEAN, beanProvider, depProvider);
        l2.checkNextEvent(context, TEST_BEAN, beanProvider, depProvider);
        assertFalse("Too many events (1)", l1.hasMoreEvents());
        assertFalse("Too many events (2)", l2.hasMoreEvents());
        EasyMock.verify(beanProvider, depProvider);
    }

    /**
     * Tests whether a listener that was removed does not receive more events.
     */
    @Test
    public void testFireBeanCreationEventAndRemove()
    {
        BeanProvider beanProvider = EasyMock.createMock(BeanProvider.class);
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(beanProvider, depProvider);
        BeanCreationListenerTestImpl l1 = new BeanCreationListenerTestImpl();
        BeanCreationListenerTestImpl l2 = new BeanCreationListenerTestImpl();
        support.addBeanCreationListener(l1);
        support.addBeanCreationListener(l2);
        support.fireBeanCreationEvent(beanProvider, depProvider, TEST_BEAN);
        support.removeBeanCreationListener(l2);
        support.fireBeanCreationEvent(null, depProvider, "Another bean");
        l1.checkNextEvent(context, TEST_BEAN, beanProvider, depProvider);
        l2.checkNextEvent(context, TEST_BEAN, beanProvider, depProvider);
        assertTrue("Too few events for l1", l1.hasMoreEvents());
        assertFalse("Too many events for l2", l2.hasMoreEvents());
        EasyMock.verify(beanProvider, depProvider);
    }

    /**
     * Tests the beanCreated() method. When a bean creation event is received,
     * the event has to be transformed to the associated context and propagated
     * to the registered listeners.
     */
    @Test
    public void testBeanCreated()
    {
        BeanContext anotherContext = EasyMock.createMock(BeanContext.class);
        BeanProvider beanProvider = EasyMock.createMock(BeanProvider.class);
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        depProvider.setCreationBeanContext(context);
        EasyMock.replay(beanProvider, depProvider, anotherContext);
        BeanCreationListenerTestImpl l1 = new BeanCreationListenerTestImpl();
        BeanCreationListenerTestImpl l2 = new BeanCreationListenerTestImpl();
        support.addBeanCreationListener(l1);
        support.addBeanCreationListener(l2);
        BeanCreationEvent event = new BeanCreationEvent(anotherContext,
                beanProvider, depProvider, TEST_BEAN);
        support.beanCreated(event);
        l1.checkNextEvent(context, TEST_BEAN, beanProvider, depProvider);
        l2.checkNextEvent(context, TEST_BEAN, beanProvider, depProvider);
        assertFalse("Too many events (1)", l1.hasMoreEvents());
        assertFalse("Too many events (2)", l2.hasMoreEvents());
        EasyMock.verify(beanProvider, depProvider, anotherContext);
    }
}

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
package net.sf.jguiraffe.gui.builder.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Random;

import net.sf.jguiraffe.gui.builder.components.model.TreeExpansionListener;
import net.sf.jguiraffe.gui.builder.components.model.TreeHandler;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;
import net.sf.jguiraffe.gui.forms.ComponentStore;
import net.sf.jguiraffe.gui.forms.ComponentStoreImpl;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for FormEventManager.
 *
 * @author Oliver Heger
 * @version $Id: TestFormEventManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFormEventManager
{
    /** Constant for the name test field. */
    private static final String FLD_NAME = "name";

    /** Constant for the first name test field. */
    private static final String FLD_FIRSTNAME = "firstName";

    /** Constant for the birth date test field. */
    private static final String FLD_BIRTHDATE = "birthDate";

    /** Constant for the salary test field. */
    private static final String FLD_SALARY = "salary";

    /** Constant for the thread sleep time. */
    private static final int SLEEP_TIME = 100;

    /** Stores the event manager to be tested. */
    private FormEventManager eventManager;

    /** The used platform event manager. */
    private PlatformEventManagerImpl platformEventManager;

    /** The store with the test fields. */
    private ComponentStore store;

    @Before
    public void setUp() throws Exception
    {
        platformEventManager = new PlatformEventManagerImpl();
        eventManager = new FormEventManager(platformEventManager);

        setUpComponents();
    }

    /**
     * Inserts some components into the event manager.
     */
    protected void setUpComponents()
    {
        store = new ComponentStoreImpl();
        store.addComponentHandler(FLD_NAME, new ComponentHandlerImpl());
        store.addComponentHandler(FLD_FIRSTNAME, new ComponentHandlerImpl());
        store.addComponentHandler(FLD_BIRTHDATE, new ComponentHandlerImpl());
        store.addComponentHandler(FLD_SALARY, new ComponentHandlerImpl());
        eventManager.setComponentStore(store);
    }

    /**
     * Tests if components can be accessed.
     */
    @Test
    public void testGetComponent()
    {
        for (String name : store.getComponentHandlerNames())
        {
            assertSame("Cannot obtain component handler for " + name, store
                    .findComponentHandler(name), eventManager
                    .getComponentHandler(name));
        }
    }

    /**
     * Tests accessing a non existing component. Null should be returned.
     */
    @Test
    public void testGetComponentNonExisting()
    {
        assertNull("Found non existing component", eventManager
                .getComponentHandler("NonExistingComponent"));
    }

    /**
     * Tests accessing a component handler when no component store is set. This
     * should cause an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetComponentNoStore()
    {
        eventManager.setComponentStore(null);
        eventManager.getComponentHandler(FLD_NAME);
    }

    /**
     * Tests adding of event listeners.
     */
    @Test
    public void testAddListeners()
    {
        eventManager.addActionListener(FLD_NAME, new TestActionListener());
        eventManager.addChangeListener(FLD_NAME, new TestChangeListener());
        eventManager.addFocusListener(FLD_NAME, new TestFocusListener());
        assertEquals("Wrong registration data",
                "name -> ACTION, name -> CHANGE, name -> FOCUS",
                platformEventManager.getRegistrationData());
    }

    /**
     * Tests adding multiple event listeners for a component. Registration with
     * the platform event manager should be performed only once.
     */
    @Test
    public void testAddEventListenerMultipleTimes()
    {
        eventManager.addActionListener(FLD_NAME, new TestActionListener());
        eventManager.addActionListener(FLD_NAME, new TestActionListener());
        assertEquals("Wrong registration data", "name -> ACTION",
                platformEventManager.getRegistrationData());
    }

    /**
     * Tests adding an all listener for an event type. Here each component
     * should be registered exactly once, too.
     */
    @Test
    public void testAddAllEventListener()
    {
        eventManager.addActionListener(FLD_NAME, new TestActionListener());
        eventManager.addActionListener(new TestActionListener());
        checkRegisteredAllListener(FormListenerType.ACTION);
    }

    /**
     * Tests whether an all listener is registered. In this case for each
     * component that belongs to the store exactly one listener for the given
     * type must be registered.
     *
     * @param type the listener type
     */
    private void checkRegisteredAllListener(FormListenerType type)
    {
        for (String name : store.getComponentHandlerNames())
        {
            assertEquals("Wrong number of registered listeners", 1,
                    platformEventManager.getNumberOf(name, type));
        }
    }

    /**
     * Tries to add an event listener for an unknown component. This should
     * cause an exception.
     */
    @Test(expected = NoSuchElementException.class)
    public void testAddEventListenerUnknownComponent()
    {
        eventManager.addChangeListener("UnknownComponent",
                new TestChangeListener());
    }

    /**
     * Tests firing a event without further complications.
     */
    @Test
    public void testFireEventNamed()
    {
        TestActionListener alName = new TestActionListener();
        eventManager.addActionListener(FLD_NAME, alName);
        FormActionEvent ae = new FormActionEvent(this, getHandler(FLD_NAME),
                FLD_NAME, "NAME_ACTION");
        eventManager.fireEvent(ae, FormListenerType.ACTION);
        assertEquals("Wrong number of events received", 1, alName.count);
        assertSame("Wrong event received", ae, alName.event);
        assertEquals("Wrong command", "NAME_ACTION", alName.event.getCommand());
        assertSame("Wrong handler", getHandler(FLD_NAME), alName.event
                .getHandler());
    }

    /**
     * Fires an event for a component and tests whether a listener that is
     * registered for a different component won't be notified.
     */
    @Test
    public void testFireEventNamedDifferent()
    {
        TestActionListener alName = new TestActionListener();
        eventManager.addActionListener(FLD_NAME, alName);
        TestActionListener alFirstName = new TestActionListener();
        eventManager.addActionListener(FLD_FIRSTNAME, alFirstName);
        FormActionEvent ae = new FormActionEvent(this, getHandler(FLD_NAME),
                FLD_NAME, "NAME_ACTION");
        eventManager.fireEvent(ae, FormListenerType.ACTION);
        assertEquals("Wrong number of events received", 1, alName.count);
        assertEquals("Different handler was triggered, too", 0,
                alFirstName.count);
    }

    /**
     * Tests event notifications for registered all listeners.
     */
    @Test
    public void testFireEventAllListener()
    {
        TestActionListener alName = new TestActionListener();
        eventManager.addActionListener(FLD_NAME, alName);
        TestActionListener alAll = new TestActionListener();
        eventManager.addActionListener(alAll);
        FormActionEvent ae = new FormActionEvent(this, getHandler(FLD_NAME),
                FLD_NAME, "NAME_ACTION");
        eventManager.fireEvent(ae, FormListenerType.ACTION);
        assertEquals("Wrong number of events received for named listener", 1,
                alName.count);
        assertEquals("Wrong number of events received for all listener", 1,
                alAll.count);
        assertSame("Wrong event received for all listener", ae, alAll.event);
    }

    /**
     * Tests registering and invoking focus handlers. It is also checked that
     * listeners of other types are not affected.
     */
    @Test
    public void testFireEventFocusListener()
    {
        TestActionListener alAll = new TestActionListener();
        eventManager.addActionListener(alAll);
        TestFocusListener flBirth = new TestFocusListener();
        eventManager.addFocusListener(FLD_BIRTHDATE, flBirth);
        TestFocusListener flAll = new TestFocusListener();
        eventManager.addFocusListener(flAll);
        FormFocusEvent fe = new FormFocusEvent(this, getHandler(FLD_BIRTHDATE),
                FLD_BIRTHDATE, FormFocusEvent.Type.FOCUS_GAINED);
        eventManager.fireEvent(fe, FormListenerType.FOCUS);
        fe = new FormFocusEvent(this, getHandler(FLD_BIRTHDATE), FLD_BIRTHDATE,
                FormFocusEvent.Type.FOCUS_LOST);
        eventManager.fireEvent(fe, FormListenerType.FOCUS);
        assertEquals("Named listener got no focus gained", 1,
                flBirth.gainedCount);
        assertEquals("Named listener got no focus lost", 1, flBirth.lostCount);
        assertEquals("All listener got not focus gained", 1, flAll.gainedCount);
        assertEquals("All listener got no focus lost", 1, flAll.lostCount);
        assertSame("Wrong event received", fe, flAll.event);
        assertEquals("Action listener was invoked", 0, alAll.count);
    }

    /**
     * Tests registering and invoking change listeners.
     */
    @Test
    public void testFireEventChangeListener()
    {
        TestChangeListener clSalary = new TestChangeListener();
        eventManager.addChangeListener(FLD_SALARY, clSalary);
        TestChangeListener clName = new TestChangeListener();
        eventManager.addChangeListener(FLD_NAME, clName);
        TestChangeListener clAll = new TestChangeListener();
        eventManager.addChangeListener(clAll);
        FormChangeEvent ce = new FormChangeEvent(this, getHandler(FLD_SALARY),
                FLD_SALARY);
        eventManager.fireEvent(ce, FormListenerType.CHANGE);
        assertEquals("Named listener was not invoked", 1, clSalary.count);
        assertEquals("Other named listener was invoked", 0, clName.count);
        assertEquals("All listener was not invoked", 1, clAll.count);
    }

    /**
     * Helper method for creating a mouse event.
     *
     * @param type the type of the event
     * @return the event
     */
    private FormMouseEvent createMouseEvent(FormMouseEvent.Type type)
    {
        return new FormMouseEvent(this, getHandler(FLD_NAME), FLD_NAME, type,
                1, 2, FormMouseEvent.BUTTON1, null);
    }

    /**
     * Tests whether mouse pressed events can be fired. (This is actually a test
     * for the FormListenerType class.)
     */
    @Test
    public void testFireEventMouseListenerPressed()
    {
        FormMouseListener l = EasyMock.createMock(FormMouseListener.class);
        FormMouseEvent ev = createMouseEvent(FormMouseEvent.Type.MOUSE_PRESSED);
        l.mousePressed(ev);
        EasyMock.replay(l);
        eventManager.addMouseListener(FLD_NAME, l);
        eventManager.fireEvent(ev, FormListenerType.MOUSE);
        EasyMock.verify(l);
    }

    /**
     * Tests whether mouse release events can be fired. (This is actually a test
     * for the FormListenerType class.)
     */
    @Test
    public void testFireEventMouseListenerReleased()
    {
        FormMouseListener l = EasyMock.createMock(FormMouseListener.class);
        FormMouseEvent ev = createMouseEvent(FormMouseEvent.Type.MOUSE_RELEASED);
        l.mouseReleased(ev);
        EasyMock.replay(l);
        eventManager.addMouseListener(l);
        eventManager.fireEvent(ev, FormListenerType.MOUSE);
        EasyMock.verify(l);
    }

    /**
     * Tests whether mouse click events can be fired. (This is actually a test
     * for the FormListenerType class.)
     */
    @Test
    public void testFireEventMouseListenerClicked()
    {
        FormMouseListener l = EasyMock.createMock(FormMouseListener.class);
        FormMouseEvent ev = createMouseEvent(FormMouseEvent.Type.MOUSE_CLICKED);
        l.mouseClicked(ev);
        EasyMock.replay(l);
        eventManager.addMouseListener(FLD_NAME, l);
        eventManager.fireEvent(ev, FormListenerType.MOUSE);
        EasyMock.verify(l);
    }

    /**
     * Tests whether mouse double click events can be fired. (This is actually a
     * test for the FormListenerType class.)
     */
    @Test
    public void testFireEventMouseListenerDoubleClicked()
    {
        FormMouseListener l = EasyMock.createMock(FormMouseListener.class);
        FormMouseEvent ev = createMouseEvent(FormMouseEvent.Type.MOUSE_DOUBLE_CLICKED);
        l.mouseDoubleClicked(ev);
        EasyMock.replay(l);
        eventManager.addMouseListener(l);
        eventManager.fireEvent(ev, FormListenerType.MOUSE);
        EasyMock.verify(l);
    }

    /**
     * Tests whether mouse enter events can be fired. (This is actually a test
     * for the FormListenerType class.)
     */
    @Test
    public void testFireEventMouseListenerEntered()
    {
        FormMouseListener l = EasyMock.createMock(FormMouseListener.class);
        FormMouseEvent ev = createMouseEvent(FormMouseEvent.Type.MOUSE_ENTERED);
        l.mouseEntered(ev);
        EasyMock.replay(l);
        eventManager.addMouseListener(FLD_NAME, l);
        eventManager.fireEvent(ev, FormListenerType.MOUSE);
        EasyMock.verify(l);
    }

    /**
     * Tests whether mouse exit events can be fired. (This is actually a test
     * for the FormListenerType class.)
     */
    @Test
    public void testFireEventMouseListenerExited()
    {
        FormMouseListener l = EasyMock.createMock(FormMouseListener.class);
        FormMouseEvent ev = createMouseEvent(FormMouseEvent.Type.MOUSE_EXITED);
        l.mouseExited(ev);
        EasyMock.replay(l);
        eventManager.addMouseListener(l);
        eventManager.fireEvent(ev, FormListenerType.MOUSE);
        EasyMock.verify(l);
    }

    /**
     * Tests removing registered action listeners.
     */
    @Test
    public void testRemoveActionListener()
    {
        TestActionListener lName = new TestActionListener();
        TestActionListener lAll = new TestActionListener();
        eventManager.addActionListener(FLD_NAME, lName);
        eventManager.addActionListener(lAll);
        FormActionEvent ae = new FormActionEvent(this, getHandler(FLD_NAME),
                FLD_NAME, "NAME_ACTION");
        eventManager.fireEvent(ae, FormListenerType.ACTION);
        eventManager.removeActionListener(FLD_NAME, lName);
        checkRegisteredAllListener(FormListenerType.ACTION);
        eventManager.fireEvent(ae, FormListenerType.ACTION);
        eventManager.removeActionListener(lAll);
        assertEquals("Wrong registration data", "", platformEventManager
                .getRegistrationData());
        eventManager.fireEvent(ae, FormListenerType.ACTION);
        assertEquals("Wrong number of named events", 1, lName.count);
        assertEquals("Wrong number of all events", 2, lAll.count);
    }

    /**
     * Tests removing registered change listeners.
     */
    @Test
    public void testRemoveChangeListener()
    {
        TestChangeListener lName = new TestChangeListener();
        TestChangeListener lAll = new TestChangeListener();
        eventManager.addChangeListener(lAll);
        eventManager.addChangeListener(FLD_FIRSTNAME, lName);
        FormChangeEvent ce = new FormChangeEvent(this,
                getHandler(FLD_FIRSTNAME), FLD_FIRSTNAME);
        eventManager.fireEvent(ce, FormListenerType.CHANGE);
        eventManager.removeChangeListener(lAll);
        assertEquals("Wrong registration data", "firstName -> CHANGE",
                platformEventManager.getRegistrationData());
        eventManager.fireEvent(ce, FormListenerType.CHANGE);
        eventManager.removeChangeListener(FLD_FIRSTNAME, lName);
        eventManager.fireEvent(ce, FormListenerType.CHANGE);
        assertEquals("Wrong number of named events", 2, lName.count);
        assertEquals("Wrong number of all events", 1, lAll.count);
    }

    /**
     * Tests removing registered focus listeners.
     */
    @Test
    public void testRemoveFocusListener()
    {
        TestFocusListener lName = new TestFocusListener();
        TestFocusListener lAll = new TestFocusListener();
        eventManager.addFocusListener(lAll);
        eventManager.addFocusListener(FLD_BIRTHDATE, lName);
        FormFocusEvent fe = new FormFocusEvent(this, getHandler(FLD_BIRTHDATE),
                FLD_BIRTHDATE, FormFocusEvent.Type.FOCUS_GAINED);
        eventManager.fireEvent(fe, FormListenerType.FOCUS);
        eventManager.removeFocusListener(FLD_BIRTHDATE, lName);
        eventManager.removeFocusListener(lAll);
        eventManager.fireEvent(fe, FormListenerType.FOCUS);
        assertEquals("Wrong number of named events", 1, lName.gainedCount);
        assertEquals("Wrong number of all events", 1, lAll.gainedCount);
    }

    /**
     * Tests whether mouse listeners can be removed.
     */
    @Test
    public void testRemoveMouseListener()
    {
        FormMouseListener l1 = EasyMock.createMock(FormMouseListener.class);
        FormMouseListener l2 = EasyMock.createMock(FormMouseListener.class);
        FormMouseListener l3 = EasyMock.createMock(FormMouseListener.class);
        FormMouseEvent ev1 = createMouseEvent(FormMouseEvent.Type.MOUSE_CLICKED);
        FormMouseEvent ev2 = createMouseEvent(FormMouseEvent.Type.MOUSE_DOUBLE_CLICKED);
        l1.mouseClicked(ev1);
        l2.mouseClicked(ev1);
        l3.mouseClicked(ev1);
        l3.mouseDoubleClicked(ev2);
        EasyMock.replay(l1, l2, l3);
        eventManager.addMouseListener(FLD_NAME, l1);
        eventManager.addMouseListener(l2);
        eventManager.addMouseListener(FLD_NAME, l3);
        eventManager.fireEvent(ev1, FormListenerType.MOUSE);
        eventManager.removeMouseListener(FLD_NAME, l1);
        eventManager.removeMouseListener(l2);
        eventManager.fireEvent(ev2, FormListenerType.MOUSE);
        EasyMock.verify(l1, l2, l3);
    }

    /**
     * Tests adding null all listeners. This should be a noop.
     */
    @Test
    public void testAddNullAllListener()
    {
        eventManager.addActionListener(null);
        eventManager.addChangeListener(null);
        eventManager.addFocusListener(null);
        assertEquals("Wrong registration data", "", platformEventManager
                .getRegistrationData());
    }

    /**
     * Tests adding null named listeners. This should be a noop.
     */
    @Test
    public void testAddNullNamedListener()
    {
        eventManager.addActionListener(FLD_BIRTHDATE, null);
        eventManager.addChangeListener(FLD_FIRSTNAME, null);
        eventManager.addFocusListener(FLD_NAME, null);
        assertEquals("Wrong registration data", "", platformEventManager
                .getRegistrationData());
    }

    /**
     * Tests removing an unregistered listener. This should be a noop.
     */
    @Test
    public void testRemoveNonExistingListener()
    {
        TestActionListener l = new TestActionListener();
        eventManager.addActionListener(FLD_BIRTHDATE, l);
        eventManager.removeActionListener(l);
        eventManager.removeActionListener(FLD_FIRSTNAME, l);
        assertEquals("Wrong registration data", "birthDate -> ACTION",
                platformEventManager.getRegistrationData());
    }

    /**
     * Tries to remove a null event listener. This should be a noop.
     */
    @Test
    public void testRemoveNullListener()
    {
        eventManager.addActionListener(FLD_NAME, new TestActionListener());
        eventManager.removeActionListener(FLD_NAME, null);
        eventManager.removeActionListener(null);
        assertEquals("Wrong registration data", "name -> ACTION",
                platformEventManager.getRegistrationData());
    }

    /**
     * Tries to remove a listener from a non existing component. This should
     * have no effect.
     */
    @Test
    public void testRemoveListenerNonExistingComponent()
    {
        FormActionListener l = new TestActionListener();
        eventManager.addActionListener(FLD_NAME, l);
        eventManager.removeActionListener("an unknown component!", l);
        assertEquals("Wrong registration data", "name -> ACTION",
                platformEventManager.getRegistrationData());
    }

    /**
     * Tests adding a listener during event processing. This should be possible,
     * but the newly added listener will not receive the current event.
     */
    @Test
    public void testAddListenerInEvent()
    {
        final TestActionListener l = new TestActionListener();
        FormActionListener lAdd = new FormActionListener()
        {
            public void actionPerformed(FormActionEvent e)
            {
                eventManager.addActionListener(FLD_NAME, l);
            }
        };
        eventManager.addActionListener(FLD_NAME, lAdd);
        FormActionEvent event = new FormActionEvent(this, getHandler(FLD_NAME),
                FLD_NAME, "ACTION_NAME");
        eventManager.fireEvent(event, FormListenerType.ACTION);
        eventManager.removeActionListener(FLD_NAME, lAdd);
        assertEquals("Wrong registration data", "name -> ACTION",
                platformEventManager.getRegistrationData());
        eventManager.fireEvent(event, FormListenerType.ACTION);
        assertEquals("Wrong number of received events", 1, l.count);
    }

    /**
     * Tests removing an event listener during event processing. This should be
     * possible.
     */
    @Test
    public void testRemoveListenerInEvent()
    {
        FormActionListener l = new FormActionListener()
        {
            public void actionPerformed(FormActionEvent e)
            {
                eventManager.removeActionListener(this);
            }
        };
        TestActionListener l2 = new TestActionListener();
        eventManager.addActionListener(l);
        eventManager.addActionListener(FLD_NAME, l2);
        FormActionEvent event = new FormActionEvent(this, getHandler(FLD_NAME),
                FLD_NAME, "ACTION_NAME");
        eventManager.fireEvent(event, FormListenerType.ACTION);
        assertEquals("Wrong registration data", "name -> ACTION",
                platformEventManager.getRegistrationData());
        assertEquals("Wrong number of received events", 1, l2.count);
    }

    /**
     * Tests registering event listeners and firing events in multiple threads.
     * This should be possible. A number of test threads is created that
     * register a listener, fire an event, and deregister the listener again.
     * After that the listener should have received at least one event (more
     * events are also possible).
     */
    @Test
    public void testListenerRegistrationMultiThreads()
            throws InterruptedException
    {
        final int threadCount = 20;
        final int loopCount = 50;
        FormActionEvent actionEvent = new FormActionEvent(this,
                getHandler(FLD_NAME), FLD_NAME, "ACTION_NAME");
        FormChangeEvent changeEvent = new FormChangeEvent(this,
                getHandler(FLD_NAME), FLD_NAME);
        Collection<RegistrationTestThread> threads = new ArrayList<RegistrationTestThread>(
                2 * threadCount);
        for (int i = 0; i < threadCount; i++)
        {
            RegistrationTestThread t = new RegistrationTestThread(eventManager,
                    actionEvent, TestActionListener.class,
                    FormListenerType.ACTION, loopCount);
            t.start();
            threads.add(t);
            t = new RegistrationTestThread(eventManager, changeEvent,
                    TestChangeListener.class, FormListenerType.CHANGE,
                    loopCount);
            t.start();
            threads.add(t);
        }
        for (RegistrationTestThread t : threads)
        {
            t.join();
            assertTrue("Thread test not successfull", t.ok);
        }
    }

    /**
     * Helper method for testing an addEventListener() invocation that is
     * expected to yield no results.
     *
     * @param count the return value of addEventListener()
     */
    private void checkNoRegistration(int count)
    {
        assertEquals("Got registered listeners", 0, count);
        assertEquals("Got registration data", "", platformEventManager
                .getRegistrationData());
    }

    /**
     * Tries to add a null event listener. This should have no effect.
     */
    @Test
    public void testAddEventListenerNull()
    {
        checkNoRegistration(eventManager.addEventListener(null,
                FormListenerType.ACTION.name(), null));
    }

    /**
     * Tests whether a standard all listener can be registered.
     */
    @Test
    public void testAddEventListenerStdAll()
    {
        assertEquals("Wrong result", store.getComponentHandlerNames().size(),
                eventManager.addEventListener(null, FormListenerType.ACTION
                        .name(), new TestActionListener()));
        checkRegisteredAllListener(FormListenerType.ACTION);
    }

    /**
     * Tests whether a standard event listener can be registered for a specific
     * component.
     */
    @Test
    public void testAddEventListenerStdName()
    {
        assertEquals("Wrong result", 1, eventManager.addEventListener(
                FLD_FIRSTNAME, FormListenerType.CHANGE.name().toLowerCase(
                        Locale.ENGLISH), new TestChangeListener()));
        assertEquals("Wrong registration data", "firstName -> CHANGE",
                platformEventManager.getRegistrationData());
    }

    /**
     * Tries to add a standard event listener for an unknown component.
     */
    @Test
    public void testAddEventListenerStdNameUnknown()
    {
        checkNoRegistration(eventManager.addEventListener("unknown component",
                FormListenerType.FOCUS.name(), new TestFocusListener()));
    }

    /**
     * Tries to pass an event listener that is not compatible with the specified
     * standard event listener type.
     */
    @Test
    public void testAddEventListenerStdInvalidListener()
    {
        checkNoRegistration(eventManager.addEventListener(FLD_NAME,
                FormListenerType.ACTION.name(), new TestChangeListener()));
    }

    /**
     * Tries to add a null event listener for a non-standard type.
     */
    @Test
    public void testAddEventListenerSpecNull()
    {
        checkNoRegistration(eventManager.addEventListener(FLD_FIRSTNAME,
                "test", null));
    }

    /**
     * Tries to add a non-standard event listener for an unknown component.
     */
    @Test
    public void testAddEventListenerSpecNameUnknown()
    {
        EventListener l = EasyMock.createMock(EventListener.class);
        EasyMock.replay(l);
        checkNoRegistration(eventManager.addEventListener("unknown component",
                "Test", l));
        EasyMock.verify(l);
    }

    /**
     * Tries to add a non-supported listener to an existing component.
     */
    @Test
    public void testAddEventListenerSpecNameInvalidListener()
    {
        EventListener l = EasyMock.createMock(EventListener.class);
        EasyMock.replay(l);
        checkNoRegistration(eventManager.addEventListener(FLD_NAME, "Foo", l));
        EasyMock.verify(l);
    }

    /**
     * Tries to add an event listener to all components that is not supported.
     */
    @Test
    public void testAddEventListenerSpecAllInvalidListener()
    {
        TreeExpansionListener l = EasyMock
                .createMock(TreeExpansionListener.class);
        EasyMock.replay(l);
        checkNoRegistration(eventManager.addEventListener(null, "Expansion", l));
        EasyMock.verify(l);
    }

    /**
     * Helper method for creating a non-standard component handler mock.
     *
     * @param <T> the type of the mock
     * @param cls the class of the mock
     * @return the mock object
     */
    private static <T extends ComponentHandler<?>> T createHandlerMock(
            Class<T> cls)
    {
        T mock = EasyMock.createMock(cls);
        EasyMock.expect(mock.getComponent()).andReturn(null);
        return mock;
    }

    /**
     * Tries to add a non-standard event listener to a component if the listener
     * object is not compatible with the listener type.
     */
    @Test
    public void testAddEventListenerSpecNameInvalidListenerObj()
    {
        TreeHandler handler = createHandlerMock(TreeHandler.class);
        EventListener l = EasyMock.createMock(EventListener.class);
        EasyMock.replay(l, handler);
        store.addComponentHandler(FLD_NAME, handler);
        checkNoRegistration(eventManager.addEventListener(FLD_NAME,
                "Expansion", l));
        EasyMock.verify(l, handler);
    }

    /**
     * Tests whether a non-standard event listener can be added to a specific
     * component.
     */
    @Test
    public void testAddEventListenerSpecName()
    {
        TreeHandler handler = createHandlerMock(TreeHandler.class);
        TreeExpansionListener l = EasyMock
                .createMock(TreeExpansionListener.class);
        handler.addExpansionListener(l);
        EasyMock.replay(l, handler);
        store.addComponentHandler(FLD_NAME, handler);
        assertEquals("Wrong result", 1, eventManager.addEventListener(FLD_NAME,
                "Expansion", l));
        EasyMock.verify(l, handler);
    }

    /**
     * Tests whether a non-standard event listener can be added to all
     * components.
     */
    @Test
    public void testAddEventListenerSpecAll()
    {
        TreeHandler h1 = createHandlerMock(TreeHandler.class);
        TreeHandler h2 = createHandlerMock(TreeHandler.class);
        TreeExpansionListener l = EasyMock
                .createMock(TreeExpansionListener.class);
        h1.addExpansionListener(l);
        h2.addExpansionListener(l);
        EasyMock.replay(l, h1, h2);
        store.addComponentHandler(FLD_FIRSTNAME, h1);
        store.addComponentHandler(FLD_NAME, h2);
        assertEquals("Wrong result", 2, eventManager.addEventListener(null,
                "Expansion", l));
        EasyMock.verify(l, h1, h2);
    }

    /**
     * Tests whether a standard event listener can be removed.
     */
    @Test
    public void testRemoveEventListenerStd()
    {
        FormActionListener l = new TestActionListener();
        eventManager.addActionListener(FLD_FIRSTNAME, l);
        eventManager.addChangeListener(FLD_NAME, new TestChangeListener());
        assertEquals("Wrong result", 1, eventManager.removeEventListener(
                FLD_FIRSTNAME, FormListenerType.ACTION.name(), l));
        assertEquals("Wrong registration data", FLD_NAME + " -> CHANGE",
                platformEventManager.getRegistrationData());
    }

    /**
     * Tries to remove a named standard listener that is not registered for a
     * component.
     */
    @Test
    public void testRemoveEventListenerStdNamedUnknown()
    {
        eventManager.addActionListener(FLD_NAME, new TestActionListener());
        assertEquals("Wrong result", 0, eventManager.removeEventListener(
                FLD_NAME, FormListenerType.ACTION.name(),
                new TestActionListener()));
    }

    /**
     * Tries to remove a standard all listener that is not registered.
     */
    @Test
    public void testRemoveEventListenerStdAllUnknown()
    {
        eventManager.addActionListener(new TestActionListener());
        assertEquals("Wrong result", 0, eventManager.removeEventListener(null,
                FormListenerType.ACTION.name(), new TestActionListener()));
    }

    /**
     * Tests whether a non-standard event listener can be removed.
     */
    @Test
    public void testRemoveEventListenerSpec()
    {
        TreeHandler handler = createHandlerMock(TreeHandler.class);
        TreeExpansionListener l = EasyMock
                .createMock(TreeExpansionListener.class);
        handler.removeExpansionListener(l);
        EasyMock.replay(l, handler);
        store.addComponentHandler(FLD_NAME, handler);
        assertEquals("Wrong result", 1, eventManager.removeEventListener(
                FLD_NAME, "Expansion", l));
        EasyMock.verify(l, handler);
    }

    /**
     * Tests whether an event listener can be added to a specified object.
     */
    @Test
    public void testAddEventListenerToObject()
    {
        TreeHandler handler = EasyMock.createMock(TreeHandler.class);
        TreeExpansionListener l = EasyMock
                .createMock(TreeExpansionListener.class);
        handler.addExpansionListener(l);
        EasyMock.replay(l, handler);
        assertTrue("Wrong result", eventManager.addEventListenerToObject(handler,
                "Expansion", l));
        EasyMock.verify(l, handler);
    }

    /**
     * Tries to add a null listener to an object.
     */
    @Test
    public void testAddEventListenerToObjectNull()
    {
        TreeHandler handler = EasyMock.createMock(TreeHandler.class);
        EasyMock.replay(handler);
        assertFalse("Wrong result for null listener", eventManager
                .addEventListenerToObject(handler, "Expansion", null));
        EasyMock.verify(handler);
    }

    /**
     * Tries to add a listener to a null object.
     */
    @Test
    public void testAddEventListenerToObjectNullObj()
    {
        EventListener l = EasyMock.createMock(EventListener.class);
        EasyMock.replay(l);
        assertFalse("Wrong result for null object", eventManager
                .addEventListenerToObject(null, "Expansion", l));
        EasyMock.verify(l);
    }

    /**
     * Tries to add an invalid listener to an object.
     */
    @Test
    public void testAddEventListenerToObjectInvalidListener()
    {
        TreeExpansionListener l = EasyMock
                .createMock(TreeExpansionListener.class);
        EasyMock.replay(l);
        assertFalse("Wrong result for invalid listener", eventManager
                .addEventListenerToObject(this, "Expansion", l));
        EasyMock.verify(l);
    }

    /**
     * Tests whether an event listener can be removed from a specific object.
     */
    @Test
    public void testRemoveEventListenerFromObject()
    {
        TreeHandler handler = EasyMock.createMock(TreeHandler.class);
        TreeExpansionListener l = EasyMock
                .createMock(TreeExpansionListener.class);
        handler.removeExpansionListener(l);
        EasyMock.replay(l, handler);
        assertTrue("Wrong result", eventManager.removeEventListenerFromObject(
                handler, "Expansion", l));
        EasyMock.verify(l, handler);
    }

    /**
     * Tries to remove a null listener from an object.
     */
    @Test
    public void testRemoveEventListenerFromObjectNull()
    {
        TreeHandler handler = EasyMock.createMock(TreeHandler.class);
        EasyMock.replay(handler);
        assertFalse("Wrong result for null listener", eventManager
                .removeEventListenerFromObject(handler, "Expansion", null));
        EasyMock.verify(handler);
    }

    /**
     * Helper method for returning a component handler.
     *
     * @param name the component's name
     * @return the corresponding handler
     */
    protected ComponentHandler<?> getHandler(String name)
    {
        return store.findComponentHandler(name);
    }

    /**
     * An extended listener interface used for testing. It defines an additional
     * method for retrieving the number of received events.
     */
    static interface CountingListener extends FormEventListener
    {
        int getEventCount();
    }

    /**
     * Test implementation of the action listener interface.
     */
    static class TestActionListener implements FormActionListener,
            CountingListener
    {
        public int count;

        public FormActionEvent event;

        public void actionPerformed(FormActionEvent e)
        {
            event = e;
            count++;
        }

        public int getEventCount()
        {
            return count;
        }
    }

    /**
     * Test implementation of the change listener interface.
     */
    static class TestChangeListener implements FormChangeListener,
            CountingListener
    {
        public int count;

        public FormChangeEvent event;

        public void elementChanged(FormChangeEvent e)
        {
            event = e;
            count++;
        }

        public int getEventCount()
        {
            return count;
        }
    }

    /**
     * Test implementation of the focus listener interface.
     */
    static class TestFocusListener implements FormFocusListener
    {
        public FormFocusEvent event;

        public int gainedCount;

        public int lostCount;

        public void focusGained(FormFocusEvent e)
        {
            event = e;
            gainedCount++;
        }

        public void focusLost(FormFocusEvent e)
        {
            event = e;
            lostCount++;
        }
    }

    /**
     * A test thread class for testing listener registration and event firing in
     * multiple threads.
     */
    static class RegistrationTestThread extends Thread
    {
        /** Stores the event manager. */
        private FormEventManager eventManager;

        /** The event to be fired. */
        private FormEvent event;

        /** The class of the test listener. */
        private Class<? extends CountingListener> listenerClass;

        /** Stores the listener type. */
        private FormListenerType listenerType;

        /** The number of iterations. */
        private int loopCount;

        /** Stores a flag whether everything is ok. */
        boolean ok;

        /**
         * Creates a new instance of <code>RegistrationTestThread</code> and
         * initializes it.
         */
        public RegistrationTestThread(FormEventManager evMan, FormEvent ev,
                Class<? extends CountingListener> tstLstnCls,
                FormListenerType lstnType, int cnt)
        {
            eventManager = evMan;
            event = ev;
            listenerClass = tstLstnCls;
            listenerType = lstnType;
            loopCount = cnt;
        }

        @Override
        public void run()
        {
            ok = true;
            Random rand = new Random();
            try
            {
                for (int i = 0; i < loopCount; i++)
                {
                    try
                    {
                        sleep(rand.nextInt(SLEEP_TIME));
                    }
                    catch (InterruptedException iex)
                    {
                        // ignore
                    }
                    CountingListener l = listenerClass.newInstance();
                    eventManager.addListener(listenerType, FLD_NAME, l);
                    eventManager.fireEvent(event, listenerType);
                    eventManager.removeListener(listenerType, FLD_NAME, l);
                    if (l.getEventCount() < 1)
                    {
                        ok = false;
                    }
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
                ok = false;
            }
        }
    }
}

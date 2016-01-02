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
package net.sf.jguiraffe.gui.builder.window.tags;

import java.util.ArrayList;
import java.util.EventListener;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.gui.app.Application;
import net.sf.jguiraffe.gui.app.ApplicationContextImpl;
import net.sf.jguiraffe.gui.builder.action.FormActionImpl;
import net.sf.jguiraffe.gui.builder.components.TreeHandlerImpl;
import net.sf.jguiraffe.gui.builder.components.model.TreeExpansionListener;
import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManager;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManagerImpl;
import net.sf.jguiraffe.gui.builder.window.WindowImpl;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.easymock.EasyMock;

/**
 * Test class for several concrete event listener tags.
 *
 * @author Oliver Heger
 * @version $Id: TestEventListenerTags.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestEventListenerTags extends AbstractWindowTagTest
{
    /** Constant for the test action. */
    private static final FormActionImpl TEST_ACTION = new FormActionImpl(
            "TEST_ACTION");

    /** Constant for the test bean. */
    private static final Object TEST_BEAN = new Object();

    /** Constant for the name of the test bean. */
    private static final String BEAN_NAME = "targetBean";

    /** Constant for a variable name. */
    private static final String VAR_NAME = "varListener";

    /** Constant for the name of the test script. */
    private static final String SCRIPT = "eventlisteners";

    /** Constant for the name of the form component. */
    private static final String COMP_NAME = "text1";

    /** Constant for the name of the tree component. */
    private static final String TREE_NAME = "testTree";

    /** Constant for the name of the tree model in the Jelly context. */
    private static final String TREE_MODEL = "treeModel";

    /** Constant for the name of the focus builder. */
    private static final String FOCUS_BUILDER = "FOCUS_BUILDER";

    /** Constant for the name of the action builder. */
    private static final String ACTION_BUILDER = "ACTION_BUILDER";

    /** Constant for the name of the change builder. */
    private static final String CHANGE_BUILDER = "CHANGE_BUILDER";

    /** Constant for the name of the mouse builder. */
    private static final String MOUSE_BUILDER = "MOUSE_BUILDER";

    /** Constant for the name of the form incorrectly nested builder. */
    private static final String ERR_FORM_NESTED_BUILDER = "ERR_FORM_NESTED";

    /** Constant for the name of the window listener builder. */
    private static final String WINDOW_BUILDER = "WINDOW_BUILDER";

    /** Constant for the name of the incorrectly nested window listener builder. */
    private static final String ERR_WINDOW_NESTED_BUILDER = "ERR_WINDOW_NESTED";

    /** Constant for the name of the test table events builder. */
    private static final String TEST_TABLEEVENTS_BUILDER = "TEST_TABLEEVENTS";

    /** Constant for the name of the test event types builder. */
    private static final String EVENT_TYPE_BUILDER = "EVENTTYPE_BUILDER";

    /** Constant for the name of the test custom event builder. */
    private static final String CUSTOM_EVENT_BUILDER = "CUSTOMEVENT_BUILDER";

    /** Constant for the name of the single bean registration builder. */
    private static final String SINGLE_BEANREG_BUILDER = "SINGLE_BEANREG_BUILDER";

    /** Constant for the name of the window bean registration builder. */
    private static final String WINDOW_BEANREG_BUILDER = "WINDOW_BEANREG_BUILDER";

    /** Constant for the name of the multiple bean registration builder. */
    private static final String MULTI_BEANREG_BUILDER = "MULTI_BEANREG_BUILDER";

    /** Constant for the name of the mixed registration builder. */
    private static final String MIXED_BEANREG_BUILDER = "MIXED_BEANREG_BUILDER";

    /**
     * Initializes the action builder related data structures. This
     * implementation adds the test action to the action store.
     */
    @Override
    protected void setUpActionBuilder()
    {
        super.setUpActionBuilder();
        actionBuilder.getActionStore().addAction(TEST_ACTION);
    }

    /**
     * {@inheritDoc} This implementation adds the central application bean to
     * the parent bean store.
     */
    @Override
    protected BeanStore createParentBeanStore()
    {
        DefaultBeanStore store = new DefaultBeanStore();
        Application app = new Application();
        app.setApplicationContext(new ApplicationContextImpl());
        store.addBeanProvider(Application.BEAN_APPLICATION,
                ConstantBeanProvider.getInstance(Application.class, app));
        return store;
    }

    /**
     * Checks which listeners have been registered at the form event manager.
     *
     * @param expected the expected string
     * @see net.sf.jguiraffe.gui.builder.event.PlatformEventManagerImpl
     */
    private void checkFormEventRegistration(String expected)
    {
        PlatformEventManagerImpl eventMan = (PlatformEventManagerImpl) builderData
                .getEventManager().getPlatformEventManager();
        assertEquals("Wrong event listener registration", expected, eventMan
                .getRegistrationData());
    }

    /**
     * Checks if a form event listener was correctly registered.
     *
     * @param builderName the name of the builder
     * @param listenerType the listener type
     * @throws Exception if an error occurs
     */
    private void checkFormEventListener(String builderName, String listenerType)
            throws Exception
    {
        builderData.setBuilderName(builderName);
        executeScript(SCRIPT);
        builderData.invokeCallBacks();
        StringBuilder buf = new StringBuilder(COMP_NAME);
        buf.append(" -> ").append(listenerType);
        checkFormEventRegistration(buf.toString());
    }

    /**
     * Tests registering a focus listener.
     */
    public void testFocusListener() throws Exception
    {
        checkFormEventListener(FOCUS_BUILDER, "FOCUS");
    }

    /**
     * Tests registering an action listener.
     */
    public void testActionListener() throws Exception
    {
        checkFormEventListener(ACTION_BUILDER, "ACTION");
    }

    /**
     * Tests registering a change listener.
     */
    public void testChangeListener() throws Exception
    {
        checkFormEventListener(CHANGE_BUILDER, "CHANGE");
    }

    /**
     * Tests whether a mouse listener can be registered.
     */
    public void testMouseListener() throws Exception
    {
        checkFormEventListener(MOUSE_BUILDER, "MOUSE");
    }

    /**
     * Tests a form event listener tag that is not nested inside a component
     * tag.
     */
    public void testIncorrectlyNestedFormListenerTag() throws Exception
    {
        errorScript(SCRIPT, ERR_FORM_NESTED_BUILDER,
                "Could execute incorrectly nested form listener tag!");
    }

    /**
     * Tests registering a window listener.
     */
    public void testWindowListener() throws Exception
    {
        WindowImpl window = (WindowImpl) fetchWindow(SCRIPT, WINDOW_BUILDER);
        assertEquals("Window listener was not registered", 1, window
                .getWindowListeners().size());
    }

    /**
     * Tests a window listener tag that is not nested inside a window tag.
     */
    public void testIncorrectlyNestedWindowListenerTag() throws Exception
    {
        errorScript(SCRIPT, ERR_WINDOW_NESTED_BUILDER,
                "Could execute incorrectly nested window listener tag!");
    }

    /**
     * Tests registering event listeners in a sub form constructed by a table
     * table.
     */
    public void testEventListenersInSubForm() throws Exception
    {
        builderData.setBuilderName(TEST_TABLEEVENTS_BUILDER);
        context.setVariable("tabModel", new ArrayList<Object>());
        executeScript(SCRIPT);
        builderData.invokeCallBacks();
        checkFormEventRegistration("firstName -> CHANGE, lastName -> ACTION, "
                + "firstName -> CHANGE");
    }

    /**
     * Helper method for testing the registration of custom event type
     * listeners.
     *
     * @param builderName the name of the builder
     * @param expectedRegistration the expected registration string
     * @throws Exception if an error occurs
     */
    private void checkEventTypeRegistration(String builderName,
            String expectedRegistration) throws Exception
    {
        builderData.setBuilderName(builderName);
        context.setVariable(TREE_MODEL, new HierarchicalConfiguration());
        executeScript(SCRIPT);
        builderData.invokeCallBacks();
        checkFormEventRegistration(expectedRegistration);
        TreeHandlerImpl treeHandler = (TreeHandlerImpl) builderData
                .getComponentHandler(TREE_NAME);
        TreeExpansionListener[] expansionListeners = treeHandler
                .getExpansionListeners();
        assertEquals("Wrong number of expansion listeners", 1,
                expansionListeners.length);
    }

    /**
     * Tests whether additional event types can be registered.
     */
    public void testEventAdditionalTypes() throws Exception
    {
        checkEventTypeRegistration(EVENT_TYPE_BUILDER,
                "testTree -> MOUSE, testTree -> CHANGE");
    }

    /**
     * Tests whether a custom event listener can be registered.
     */
    public void testCustomEventListener() throws Exception
    {
        checkEventTypeRegistration(CUSTOM_EVENT_BUILDER, "");
    }

    /**
     * Prepares a test for registering a listener at a bean.
     *
     * @param expType the expected listener type
     * @return the mock event manager
     */
    private FormEventManagerTestImpl prepareBeanRegistrationTest(
            String... expType)
    {
        FormEventManagerTestImpl evMan = new FormEventManagerTestImpl(
                builderData.getEventManager().getPlatformEventManager(),
                expType);
        evMan.setComponentStore(builderData.getEventManager()
                .getComponentStore());
        builderData.setEventManager(evMan);
        BeanContext bctx = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(bctx.getBean(BEAN_NAME)).andReturn(TEST_BEAN).times(
                expType.length);
        EasyMock.replay(bctx);
        builderData.setBeanContext(bctx);
        return evMan;
    }

    /**
     * Performs a test with a bean registration.
     *
     * @param builderName the builder name
     * @param expTypes the expected listener types
     * @throws Exception if an error occurs
     */
    private void checkBeanRegistration(String builderName, String... expTypes)
            throws Exception
    {
        FormEventManagerTestImpl evMan = prepareBeanRegistrationTest(expTypes);
        builderData.setBuilderName(builderName);
        executeScript(SCRIPT);
        builderData.invokeCallBacks();
        Object listener = context.findVariable(VAR_NAME);
        assertNotNull("No event listener", listener);
        evMan.verify(listener);
    }

    /**
     * Tests whether a listener can be registered at a bean.
     */
    public void testRegisterBeanSingle() throws Exception
    {
        checkBeanRegistration(SINGLE_BEANREG_BUILDER, "Action");
        checkFormEventRegistration("");
    }

    /**
     * Tests whether a window listener can be registered at a bean. (This is a
     * different tag, so we test it separately.)
     */
    public void testRegisterBeanWindow() throws Exception
    {
        checkBeanRegistration(WINDOW_BEANREG_BUILDER, "Window");
    }

    /**
     * Tests whether multiple listener types can be registered at a bean.
     */
    public void testRegisterBeanMulti() throws Exception
    {
        checkBeanRegistration(MULTI_BEANREG_BUILDER, "Expansion", "Action",
                "Focus");
    }

    /**
     * Tests whether a listener can be registered at a component and a bean in
     * parallel.
     */
    public void testRegisterBeanMixed() throws Exception
    {
        checkBeanRegistration(MIXED_BEANREG_BUILDER, "Action");
        checkFormEventRegistration("textBeanReg -> ACTION");
    }

    /**
     * A specialized event manager implementation that is used for testing event
     * listener registrations at beans.
     */
    private static class FormEventManagerTestImpl extends FormEventManager
    {
        /** The expected event listener types. */
        private final String[] expectedListenerTypes;

        /** The listener to be registered. */
        private EventListener listener;

        /** The current index into the listener types array. */
        private int listenerTypeIndex;

        /**
         * Creates a new instance of {@code FormEventManagerTestImpl}.
         *
         * @param platformEventManager the platform event manager
         * @param expTypes the expected listener types
         */
        public FormEventManagerTestImpl(
                PlatformEventManager platformEventManager, String... expTypes)
        {
            super(platformEventManager);
            expectedListenerTypes = expTypes;
        }

        /**
         * Checks whether all expected invocation actually took place and
         * whether the expected event listener was set.
         *
         * @param expListener the expected listener
         */
        public void verify(Object expListener)
        {
            assertEquals("Wrong number of invocations", listenerTypeIndex,
                    expectedListenerTypes.length);
            assertSame("Wrong listener", expListener, listener);
        }

        /**
         * {@inheritDoc} Mocks this call. Checks the parameters and records the
         * listener. For each of the expected listener types set in the
         * constructor a corresponding invocation is expected.
         */
        @Override
        public boolean addEventListenerToObject(Object target,
                String listenerType, EventListener l)
        {
            assertTrue("Too many registrations",
                    listenerTypeIndex < expectedListenerTypes.length);
            assertNotNull("No listener", l);
            assertSame("Wrong target object", TEST_BEAN, target);
            assertEquals("Wrong listener type",
                    expectedListenerTypes[listenerTypeIndex++], listenerType);
            if (listener != null)
            {
                assertSame("Wrong listener", listener, l);
            }
            listener = l;
            return true;
        }
    }
}

/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.impl.DefaultBeanContext;
import net.sf.jguiraffe.di.impl.SimpleBeanStoreImpl;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManager;
import net.sf.jguiraffe.gui.forms.BindingStrategy;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;
import net.sf.jguiraffe.gui.forms.ComponentStore;
import net.sf.jguiraffe.gui.forms.DefaultFieldHandler;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.transform.TransformerContext;

import org.apache.commons.jelly.JellyContext;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ComponentBuilderData.
 *
 * @author Oliver Heger
 * @version $Id: TestComponentBuilderData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestComponentBuilderData
{
    /** Constant for the name of the test builder. */
    private static final String BUILDER_NAME = "TEST_BUILDER";

    /** Constant for the name of the test component. */
    private static final String COMP_NAME = "testCompName";

    /** Constant for the test component. */
    private static final Object TEST_COMP = "ATestComponent";

    /** Constant for the number of test components. */
    private static final int TEST_COMP_COUNT = 15;

    /** Stores the test transformer context.*/
    private TransformerContext tctx;

    /** Stores the test binding strategy.*/
    private BindingStrategy bindingStrategy;

    /** The instance to be tested. */
    private ComponentBuilderData data;

    @Before
    public void setUp() throws Exception
    {
        data = new ComponentBuilderData();
        data.setBuilderName(BUILDER_NAME);
        data.setComponentManager(new ComponentManagerImpl());
        data.setRootContainer(new Container("ROOT"));
        tctx = new TransformerContextImpl();
        bindingStrategy = new BeanBindingStrategy();
    }

    /**
     * Initializes the form of the data object.
     */
    private void initForm()
    {
        data.initializeForm(tctx, bindingStrategy);
    }

    /**
     * Tests a new, uninitialized object.
     */
    @Test
    public void testUninitialized()
    {
        data = new ComponentBuilderData();
        assertNull("Got a root container", data.getRootContainer());
        assertNull("Got a builder name", data.getBuilderName());
        assertNull("Got a component manager", data.getComponentManager());
    }

    /**
     * Tests accessing the form before it has been initialized. This should cause
     * an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetFormUninitialized()
    {
        data.getForm();
    }

    /**
     * Tests initialization of the form.
     */
    @Test
    public void testInitializeForm()
    {
        initForm();
        Form frm = data.getForm();
        assertEquals("Wrong transformer context", tctx, frm
                .getTransformerContext());
        assertEquals("Wrong binding strategy", bindingStrategy, frm
                .getBindingStrategy());
    }

    /**
     * Tests obtaining an instance from a Jelly context.
     */
    @Test
    public void testGet()
    {
        JellyContext ctx = new JellyContext();
        data.put(ctx);
        assertSame("Cannot obtain instance from context", data,
                ComponentBuilderData.get(ctx));
    }

    /**
     * Tests obtaining an instance from a context where none is stored.
     */
    @Test
    public void testGetNonExisting()
    {
        assertNull("Wrong result for non existing instance",
                ComponentBuilderData.get(new JellyContext()));
    }

    /**
     * Tests obtaining an instance from a null context.
     */
    @Test
    public void testGetNullCtx()
    {
        assertNull("Wrong result for null context", ComponentBuilderData
                .get(null));
    }

    /**
     * Tries putting an instance into a null context. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPutNullCtx()
    {
        data.put(null);
    }

    /**
     * Tries to add a component if no root container has been set. This should
     * cause an exception.
     */
    @Test(expected = FormBuilderRuntimeException.class)
    public void testAddComponentNoRoot()
    {
        data.setRootContainer(null);
        data.addComponent("TestComponent", null);
    }

    /**
     * Tries setting a layout object when no root container is set. This should
     * cause an exception.
     */
    @Test(expected = FormBuilderRuntimeException.class)
    public void testSetLayoutNoRoot()
    {
        data.setRootContainer(null);
        data.setLayout("TestLayout");
    }

    /**
     * Tests the implementation of the Composite interface.
     */
    @Test
    public void testComposite()
    {
        data.setLayout("TestLayout");
        data.addComponent("Component1", "Constraints1");
        data.addComponent("Component2", "Constraints2");

        assertEquals(
                "Container: ROOT [ LAYOUT = TestLayout ] { Component1 (Constraints1), "
                        + "Component2 (Constraints2) }", data
                        .getRootContainer().toString());
    }

    /**
     * Tests storing and accessing components and component handlers.
     */
    @Test
    public void testStoreComponents()
    {
        initForm();
        data.storeComponent("component1", "Component1");
        data.storeComponent("component2", "Component2");
        ComponentHandlerImpl ch = new ComponentHandlerImpl();
        ch.setType(String.class);
        ch.setComponent("Component3");
        data.storeComponentHandler("component3", ch);

        assertEquals("Component1", data.getComponent("component1"));
        assertEquals("Component2", data.getComponent("component2"));
        assertEquals("Component3", data.getComponent("component3"));
        assertSame(ch, data.getComponentHandler("component3"));
        assertNull(data.getComponentHandler("component2"));
    }

    /**
     * Tests storing and accessing field handlers.
     */
    @Test
    public void testStoreFieldHandler()
    {
        initForm();
        DefaultFieldHandler fh = new DefaultFieldHandler();
        ComponentHandlerImpl ch = new ComponentHandlerImpl();
        ch.setType(String.class);
        ch.setComponent("Component");
        fh.setComponentHandler(ch);
        data.storeFieldHandler("comp", fh);
        data.storeComponent("comp2", "Component2");

        assertSame(fh, data.getFieldHandler("comp"));
        assertSame(fh, data.getForm().getField("comp"));
        assertSame(ch, data.getComponentHandler("comp"));
        assertEquals("Component", data.getComponent("comp"));
        assertNull(data.getFieldHandler("comp2"));
    }

    /**
     * Tests registering and invocation of call backs.
     */
    @Test
    public void testCallBacks() throws FormBuilderException
    {
        initForm();
        ArrayList<TestCallBack> lst = new ArrayList<TestCallBack>(10);
        for (int i = 0; i < 10; i++)
        {
            TestCallBack cb = new TestCallBack();
            lst.add(cb);
            data.addCallBack(cb, new Integer(i));
        }

        data.invokeCallBacks();
        for (int i = 0; i < 10; i++)
        {
            TestCallBack cb = (TestCallBack) lst.get(i);
            assertSame(data, cb.getBuilderData());
            assertEquals(new Integer(i), cb.getParams());
        }
    }

    /**
     * Tests call backs that throw exceptions.
     */
    @Test
    public void testCallBackException()
    {
        initForm();
        for (int i = 0; i < 5; i++)
        {
            data.addCallBack(new TestCallBack(), null);
        }
        data.addCallBack(new TestCallBackEx(), null);
        data.addCallBack(new TestCallBack(), "Test");

        try
        {
            data.invokeCallBacks();
            fail("Exception was not thrown!");
        }
        catch (FormBuilderException fex)
        {
            assertEquals("Wrong exception message",
                    "TestCallBackEx exception!", fex.getMessage());
        }
    }

    /**
     * Tests pushing a null component store. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPushComponentStoreNull()
    {
        data.pushComponentStore(null);
    }

    /**
     * Tests adding a component to an alternative component store.
     */
    @Test
    public void testPushComponentStoreAddComponent()
            throws FormBuilderException
    {
        initForm();
        ComponentStore mockStore = EasyMock.createMock(ComponentStore.class);
        mockStore.add(COMP_NAME, TEST_COMP);
        EasyMock.expect(mockStore.findComponent(COMP_NAME))
                .andReturn(TEST_COMP);
        EasyMock.replay(mockStore);
        data.pushComponentStore(mockStore);
        data.storeComponent(COMP_NAME, TEST_COMP);
        assertEquals("Wrong component returned", TEST_COMP, data
                .getComponent(COMP_NAME));
        assertEquals("Wrong stored popped", mockStore, data.popComponentStore());
        data.storeComponent(COMP_NAME, TEST_COMP);
        assertEquals("Wrong component returned from default store", TEST_COMP,
                data.getComponent(COMP_NAME));
        EasyMock.verify(mockStore);
    }

    /**
     * Tests adding a component handler to an alternative component store.
     */
    @Test
    public void testPushComponentStoreAddComponentHandler()
            throws FormBuilderException
    {
        initForm();
        ComponentHandlerImpl handler = new ComponentHandlerImpl();
        handler.setComponent(TEST_COMP);
        ComponentStore mockStore = EasyMock
                .createStrictMock(ComponentStore.class);
        mockStore.addComponentHandler(COMP_NAME, handler);
        mockStore.findComponentHandler(COMP_NAME);
        EasyMock.expectLastCall().andReturn(handler);
        EasyMock.expect(mockStore.findComponent(COMP_NAME)).andReturn(null);
        mockStore.findComponentHandler(COMP_NAME);
        EasyMock.expectLastCall().andReturn(handler);
        EasyMock.replay(mockStore);
        data.pushComponentStore(mockStore);
        data.storeComponentHandler(COMP_NAME, handler);
        assertEquals("Wrong handler returned", handler, data
                .getComponentHandler(COMP_NAME));
        assertEquals("Wrong component returned", TEST_COMP, data
                .getComponent(COMP_NAME));
        assertEquals("Wrong store popped", mockStore, data.popComponentStore());
        assertNull("Handler found in default store", data
                .getComponentHandler(COMP_NAME));
        data.storeComponentHandler(COMP_NAME, handler);
        assertEquals("Wrong handler returned from default store", handler, data
                .getComponentHandler(COMP_NAME));
        EasyMock.verify(mockStore);
    }

    /**
     * Tests adding a field handler to an alternative component store.
     */
    @Test
    public void testPushComponentStoreAddFieldHandler()
            throws FormBuilderException
    {
        initForm();
        DefaultFieldHandler mockFieldHandler = new DefaultFieldHandler();
        ComponentHandlerImpl chandler = new ComponentHandlerImpl();
        chandler.setComponent(TEST_COMP);
        mockFieldHandler.setComponentHandler(chandler);
        ComponentStore mockStore = EasyMock.createMock(ComponentStore.class);
        mockStore.addFieldHandler(COMP_NAME, mockFieldHandler);
        mockStore.addComponentHandler(COMP_NAME, chandler);
        EasyMock.expect(mockStore.findFieldHandler(COMP_NAME)).andReturn(
                mockFieldHandler);
        EasyMock.replay(mockStore);
        data.pushComponentStore(mockStore);
        data.storeFieldHandler(COMP_NAME, mockFieldHandler);
        assertEquals("Wrong field handler returned", mockFieldHandler, data
                .getFieldHandler(COMP_NAME));
        assertEquals("Wrong store popped", mockStore, data.popComponentStore());
        assertNull("Field handler found in default store", data
                .getFieldHandler(COMP_NAME));
        data.storeFieldHandler(COMP_NAME, mockFieldHandler);
        assertEquals("Wrong field handler returned from default store",
                mockFieldHandler, data.getFieldHandler(COMP_NAME));
        EasyMock.verify(mockStore);
    }

    /**
     * Tests calling popComponentStore() too often. This should cause an
     * exception.
     */
    @Test(expected = EmptyStackException.class)
    public void testPopComponentStoreTooMany() throws FormBuilderException
    {
        initForm();
        data.popComponentStore();
        data.popComponentStore();
    }

    /**
     * Tests the return value of pushComponentStore().
     */
    @Test
    public void testPushComponentStoreReturn()
    {
        ComponentStore mockStore1 = EasyMock.createMock(ComponentStore.class);
        ComponentStore mockStore2 = EasyMock.createMock(ComponentStore.class);
        data.pushComponentStore(mockStore1);
        assertEquals("Wrong active store returned", mockStore1, data
                .pushComponentStore(mockStore2));
    }

    /**
     * Tests whether call backs are correctly treated in their context. This
     * also means that they are invoked when the context changes.
     */
    @Test
    public void testPushComponentStoreCallBacksInContext()
            throws FormBuilderException
    {
        ComponentBuilderCallBack mockCb = EasyMock
                .createMock(ComponentBuilderCallBack.class);
        ComponentStore mockStore = EasyMock.createMock(ComponentStore.class);
        mockCb.callBack(data, null);
        EasyMock.replay(mockStore, mockCb);
        data.pushComponentStore(mockStore);
        data.addCallBack(mockCb, null);
        assertEquals("Wrong store popped", mockStore, data.popComponentStore());
        EasyMock.verify(mockStore, mockCb);
    }

    /**
     * Tests that call backs registered for a different context won't get
     * executed when the context changes.
     */
    @Test
    public void testPushComponentStoreCallBacksInDifferentContext()
            throws FormBuilderException
    {
        initForm();
        ComponentBuilderCallBack mockCb = EasyMock
                .createMock(ComponentBuilderCallBack.class);
        ComponentStore mockStore = EasyMock.createMock(ComponentStore.class);
        EasyMock.replay(mockStore, mockCb);
        data.addCallBack(mockCb, null);
        data.pushComponentStore(mockStore);
        assertEquals("Wrong store popped", mockStore, data.popComponentStore());
        EasyMock.verify(mockStore, mockCb);
    }

    /**
     * Tests obtaining the event manager for the main form.
     */
    @Test
    public void testGetEventManagerForMainForm()
    {
        initForm();
        assertSame("Wrong event manager for main form", data.getEventManager(),
                data.getEventManagerForForm(data.getForm()));
    }

    /**
     * Tests overriding the default event manager.
     */
    @Test
    public void testSetEventManager()
    {
        initForm();
        PlatformEventManager pem = EasyMock
                .createMock(PlatformEventManager.class);
        FormEventManager evMan = new FormEventManager(pem);
        data.setEventManager(evMan);
        assertSame("Wrong event manager set", evMan, data.getEventManager());
        assertNotSame("Wrong event manager associated with form", evMan, data
                .getEventManagerForForm(data.getForm()));
    }

    /**
     * Tests setting the event manager to null. This should cause the default
     * event manager to be set.
     */
    @Test
    public void testSetEventManagerNull()
    {
        initForm();
        FormEventManager evMan = data.getEventManager();
        FormEventManager evMan2 = new FormEventManager(EasyMock
                .createMock(PlatformEventManager.class));
        data.setEventManager(evMan2);
        assertSame("Event manager was not set", evMan2, data.getEventManager());
        data.setEventManager(null);
        assertSame("Default event manager not set", evMan, data
                .getEventManager());
    }

    /**
     * Tests obtaining an event manager for a different form.
     */
    @Test
    public void testGetEventManagerForDifferentForm()
    {
        initForm();
        Form f = new Form(tctx, bindingStrategy);
        FormEventManager evMan = data.getEventManagerForForm(f);
        assertNotSame("Root event manager returned", data.getEventManager(),
                evMan);
        assertSame("Different platform event manager used", data
                .getEventManager().getPlatformEventManager(), evMan
                .getPlatformEventManager());
        assertNotSame("No new event manager created", evMan, data
                .getEventManagerForForm(new Form(tctx, bindingStrategy)));
        assertSame("Multiple event managers created for form", evMan, data
                .getEventManagerForForm(f));
    }

    /**
     * Tests obtaining the context form if no new context has been created.
     * Result should be the main form.
     */
    @Test
    public void testGetContextFormRoot()
    {
        initForm();
        assertSame("Wrong context form on root level", data.getForm(), data
                .getContextForm());
    }

    /**
     * Tests installing a new form context.
     */
    @Test
    public void testPushFormContext()
    {
        initForm();
        Form mainForm = data.getForm();
        Form ctxForm = new Form(tctx, bindingStrategy);
        FormEventManager mainEvMan = data.getEventManager();
        data.pushFormContext(ctxForm);
        assertSame("Main form was changed", mainForm, data.getForm());
        assertSame("Wrong context form", ctxForm, data.getContextForm());
        assertNotSame("No new event manager installed", mainEvMan, data
                .getEventManager());
        assertSame("Wrong event manager installed", data.getEventManager(),
                data.getEventManagerForForm(ctxForm));
        assertSame("Component store not changed", ctxForm.getComponentStore(),
                data.getComponentStore());
    }

    /**
     * Tests pushing and popping a form context. The old settings should be
     * restored.
     */
    @Test
    public void testPopFormContext() throws FormBuilderException
    {
        initForm();
        Form mainForm = data.getForm();
        Form ctxForm = new Form(tctx, bindingStrategy);
        ComponentStore mainStore = data.getComponentStore();
        FormEventManager mainEvMan = data.getEventManager();
        data.pushFormContext(ctxForm);
        assertSame("Wrong form instance popped", ctxForm, data.popFormContext());
        assertSame("Main form was not restored", mainForm, data
                .getContextForm());
        assertSame("Component store was not restored", mainStore, data
                .getComponentStore());
        assertSame("Event manager not restored", mainEvMan, data
                .getEventManager());
    }

    /**
     * Tests pushing a null form context. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPushFormContextNull()
    {
        data.pushFormContext(null);
    }

    /**
     * Tries to remove the root context. This should cause an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testPopFormContextRoot() throws FormBuilderException
    {
        data.popFormContext();
    }

    /**
     * Tests whether call backs are invoked when a form context is closed.
     */
    @Test
    public void testPushFormContextCallBacks() throws FormBuilderException
    {
        initForm();
        ComponentBuilderCallBack mockCb = EasyMock
                .createMock(ComponentBuilderCallBack.class);
        mockCb.callBack(data, null);
        EasyMock.replay(mockCb);
        Form ctxForm = new Form(tctx, bindingStrategy);
        data.pushFormContext(ctxForm);
        data.addCallBack(mockCb, null);
        data.popFormContext();
        EasyMock.verify(mockCb);
    }

    /**
     * Tests querying a WidgetHandler object for an existing component.
     */
    @Test
    public void testGetWidgetHandlerExisting()
    {
        initForm();
        data.storeComponent(COMP_NAME, TEST_COMP);
        WidgetHandlerImpl handler = new WidgetHandlerImpl(TEST_COMP);
        ComponentManager mockManager = EasyMock
                .createMock(ComponentManager.class);
        data.setComponentManager(mockManager);
        EasyMock.expect(mockManager.getWidgetHandlerFor(TEST_COMP)).andReturn(
                handler);
        EasyMock.replay(mockManager);
        assertSame("Wrong widget handler returned", handler, data
                .getWidgetHandler(COMP_NAME));
        EasyMock.verify(mockManager);
    }

    /**
     * Tests querying a WidgetHandler for a component that does not exist.
     * Result should be null.
     */
    @Test
    public void testGetWidgetHandlerNonExisting()
    {
        initForm();
        ComponentManager mockManager = EasyMock
                .createMock(ComponentManager.class);
        EasyMock.replay(mockManager);
        data.setComponentManager(mockManager);
        assertNull("Non null result for non existing component", data
                .getWidgetHandler(COMP_NAME));
        EasyMock.verify(mockManager);
    }

    /**
     * Tests whether a once obtained widget handler is cached.
     */
    @Test
    public void testGetWidgetHandlerCached()
    {
        initForm();
        data.storeComponent(COMP_NAME, TEST_COMP);
        WidgetHandlerImpl handler = new WidgetHandlerImpl(TEST_COMP);
        ComponentManager mockManager = EasyMock
                .createMock(ComponentManager.class);
        data.setComponentManager(mockManager);
        EasyMock.expect(mockManager.getWidgetHandlerFor(TEST_COMP)).andReturn(
                handler);
        EasyMock.replay(mockManager);
        assertSame("Wrong widget handler returned", handler, data
                .getWidgetHandler(COMP_NAME));
        assertSame("Wrong cached handler returned", handler, data
                .getWidgetHandler(COMP_NAME));
        EasyMock.verify(mockManager);
    }

    /**
     * Tests obtaining a widget handler for a given component.
     */
    @Test
    public void testGetWidgetHandlerForComponent()
    {
        ComponentManager mockManager = EasyMock
                .createMock(ComponentManager.class);
        WidgetHandler handler = new WidgetHandlerImpl(TEST_COMP);
        EasyMock.expect(mockManager.getWidgetHandlerFor(TEST_COMP)).andReturn(
                handler);
        EasyMock.replay(mockManager);
        data.setComponentManager(mockManager);
        assertSame("Wrong widget handler returned", handler, data
                .getWidgetHandlerForComponent(TEST_COMP));
        EasyMock.verify(mockManager);
    }

    /**
     * Tests obtaining a widget handler for a component multiple times. The
     * handler should then be obtained from the cache.
     */
    @Test
    public void testGetWidgetHandlerForComponentCached()
    {
        ComponentManager mockManager = EasyMock
                .createMock(ComponentManager.class);
        WidgetHandler handler = new WidgetHandlerImpl(TEST_COMP);
        EasyMock.expect(mockManager.getWidgetHandlerFor(TEST_COMP)).andReturn(
                handler);
        EasyMock.replay(mockManager);
        data.setComponentManager(mockManager);
        assertSame("Wrong widget handler returned", handler, data
                .getWidgetHandlerForComponent(TEST_COMP));
        assertSame("Wrong cached handler returned", handler, data
                .getWidgetHandlerForComponent(TEST_COMP));
        EasyMock.verify(mockManager);
    }

    /**
     * Tests obtaining a widget handler for a null component. Result should be
     * null.
     */
    @Test
    public void testGetWidgetHandlerForComponentNull()
    {
        ComponentManager mockManager = EasyMock
                .createMock(ComponentManager.class);
        EasyMock.replay(mockManager);
        data.setComponentManager(mockManager);
        assertNull("Non null handler for null component", data
                .getWidgetHandlerForComponent(null));
        EasyMock.verify(mockManager);
    }

    /**
     * Tests whether the names of the stored beans are correctly returned.
     */
    @Test
    public void testBeanNames()
    {
        setUpTestComponents();
        Set<String> names = new HashSet<String>();
        data.beanNames(names);
        int elemCount = 0;
        for (int i = 0; i < TEST_COMP_COUNT; i++, elemCount += 2)
        {
            String compName = COMP_NAME + i;
            assertTrue("Component name not found: " + compName, names
                    .contains(compName));
            assertTrue("Widget not found: " + compName, names
                    .contains("widget:" + compName));
            if (hasComponentHandler(i))
            {
                elemCount++;
                assertTrue("ComponentHandler not found: " + compName, names
                        .contains("comp:" + compName));
            }
            if (hasFieldHandler(i))
            {
                elemCount++;
                assertTrue("FieldHandler not found: " + compName, names
                        .contains("field:" + compName));
            }
        }
        assertTrue("Key for form not found", names
                .contains(ComponentBuilderData.KEY_FORM));
        assertTrue("Key for data not found", names
                .contains(ComponentBuilderData.KEY_COMPONENT_BUILDER_DATA));
        assertEquals("Wrong number of elements", elemCount + 2, names.size());
    }

    /**
     * Tests whether the beans for the stored components can be obtained.
     */
    @Test
    public void testGetBeanComponents()
    {
        setUpTestComponents();
        for (int i = 0; i < TEST_COMP_COUNT; i++)
        {
            assertEquals("Wrong bean returned for " + i, getTestComponent(i),
                    data.getBean(COMP_NAME + i));
        }
    }

    /**
     * Tests whether the component handlers can be queried from the bean store.
     */
    @Test
    public void testGetBeanComponentHandlers()
    {
        setUpTestComponents();
        for (int i = 0; i < TEST_COMP_COUNT; i++)
        {
            String compName = "comp:" + COMP_NAME + i;
            Object bean = data.getBean(compName);
            if (hasComponentHandler(i))
            {
                assertNotNull("No bean found for " + compName, bean);
                ComponentHandler<?> ch = (ComponentHandler<?>) bean;
                assertEquals("Wrong component handler for " + compName,
                        getTestComponent(i), ch.getComponent());
            }
            else
            {
                assertNull("Found component handler for " + compName, bean);
            }
        }
    }

    /**
     * Tests whether the field handlers can be queried from the bean store.
     */
    @Test
    public void testGetBeanFieldHandlers()
    {
        setUpTestComponents();
        for (int i = 0; i < TEST_COMP_COUNT; i++)
        {
            String compName = "field:" + COMP_NAME + i;
            Object bean = data.getBean(compName);
            if (hasFieldHandler(i))
            {
                assertNotNull("No bean found for " + compName, bean);
                FieldHandler fh = (FieldHandler) bean;
                assertEquals("Wrong field handler for " + compName,
                        getTestComponent(i), fh.getComponentHandler()
                                .getComponent());
            }
            else
            {
                assertNull("Found field handler for " + compName, bean);
            }
        }
    }

    /**
     * Tests whether the widget handlers can be queried from the bean store.
     */
    @Test
    public void testGetBeanWidgets()
    {
        setUpTestComponents();
        for (int i = 0; i < TEST_COMP_COUNT; i++)
        {
            String compName = "widget:" + COMP_NAME + i;
            Object bean = data.getBean(compName);
            assertNotNull("No bean found for " + compName, bean);
            WidgetHandler wh = (WidgetHandler) bean;
            assertEquals("Wrong widget handler for " + compName,
                    getTestComponent(i), wh.getWidget());
        }
    }

    /**
     * Tests querying a bean provider for a component with a prefixed name.
     */
    @Test
    public void testGetBeanProviderPrefixedName()
    {
        setUpTestComponents();
        final String name = "comp:CompWithStrangeName";
        final Object comp = "StrangeTestComponent";
        data.storeComponent(name, comp);
        assertEquals("Wrong bean provider", comp, data.getBean(name));
    }

    /**
     * Tests querying the bean context before it was fully initialized.
     */
    @Test
    public void testGetBeanContextNoInit()
    {
        initForm();
        BeanContext ctx = data.getBeanContext();
        assertTrue("Wrong context implementation " + ctx,
                ctx instanceof DefaultBeanContext);
        Set<String> names = ctx.beanNames();
        assertTrue("Form not found", names
                .contains(ComponentBuilderData.KEY_FORM));
        assertTrue("Instance not found", names
                .contains(ComponentBuilderData.KEY_COMPONENT_BUILDER_DATA));
    }

    /**
     * Tests querying the form through the bean context.
     */
    @Test
    public void testQueryForm()
    {
        initForm();
        Form form = (Form) data.getBeanContext().getBean(
                ComponentBuilderData.KEY_FORM);
        assertSame("Wrong form returned", data.getForm(), form);
    }

    /**
     * Tests querying the builder data object itself through the bean context.
     */
    @Test
    public void testQueryBuilderData()
    {
        assertSame("Wrong builder data returned", data, data.getBeanContext()
                .getBean(ComponentBuilderData.KEY_COMPONENT_BUILDER_DATA));
    }

    /**
     * Tests querying the bean context from the bean context.
     */
    @Test
    public void testQueryContext()
    {
        assertSame("Wrong bean context returned", data.getBeanContext(), data
                .getBeanContext().getBean(
                        ComponentBuilderData.KEY_CURRENT_CONTEXT));
    }

    /**
     * Tests initializing the bean context.
     */
    @Test
    public void testInitBeanStore()
    {
        setUpTestComponents();
        SimpleBeanStoreImpl store = new SimpleBeanStoreImpl();
        data.initBeanStore(store);
        Set<String> names = store.providerNames();
        assertTrue("Comp builder bean not found", names
                .contains(ComponentBuilderData.KEY_COMPONENT_BUILDER_DATA));
        for (int i = 0; i < TEST_COMP_COUNT; i++)
        {
            ConstantBeanProvider provider = (ConstantBeanProvider) store
                    .getBeanProvider(COMP_NAME + i);
            assertEquals("Wrong bean returned for " + i, getTestComponent(i),
                    provider.getBean());
        }
    }

    /**
     * Tests whether a default tool tip manager is created on demand.
     */
    @Test
    public void testGetToolTipManagerDefault()
    {
        ToolTipManager ttm = data.getToolTipManager();
        assertTrue("Wrong default tool tip manager: " + ttm,
                ttm instanceof DefaultToolTipManager);
        assertEquals("Wrong associated data", data,
                ((DefaultToolTipManager) ttm).getComponentBuilderData());
    }

    /**
     * Tests whether an alternative tool tip manager can be set.
     */
    @Test
    public void testSetToolTipManager()
    {
        ToolTipManager ttm = EasyMock.createMock(ToolTipManager.class);
        EasyMock.replay(ttm);
        data.setToolTipManager(ttm);
        assertEquals("Wrong tool tip manager", ttm, data.getToolTipManager());
        EasyMock.verify(ttm);
    }

    /**
     * Adds some test components and their handlers to the test builder data
     * object. A number of test components (defined by the
     * <code>TEST_COMP_COUNT</code> constant is added. For components whose
     * index can be divided by 2, a <code>ComponentHandler</code> is created.
     * If the index can even be divided by 3, a <code>FieldHandler</code> is
     * added.
     */
    private void setUpTestComponents()
    {
        initForm();
        for (int i = 0; i < TEST_COMP_COUNT; i++)
        {
            String compName = COMP_NAME + i;
            Object comp = getTestComponent(i);
            if (hasComponentHandler(i))
            {
                ComponentHandlerImpl ch = new ComponentHandlerImpl();
                ch.setComponent(comp);
                if (hasFieldHandler(i))
                {
                    DefaultFieldHandler fh = new DefaultFieldHandler();
                    fh.setComponentHandler(ch);
                    data.storeFieldHandler(compName, fh);
                }
                else
                {
                    data.storeComponentHandler(compName, ch);
                }
            }
            else
            {
                data.storeComponent(compName, comp);
            }
        }
    }

    /**
     * Tests whether the component with the given index has a component handler.
     *
     * @param idx the index
     * @return a flag whether this component has a component handler
     */
    private boolean hasComponentHandler(int idx)
    {
        return (idx % 2 == 0) || (idx % 3 == 0);
    }

    /**
     * Tests whether the component with the given index has a field handler.
     *
     * @param idx the index
     * @return a flag whether this component has a field handler
     */
    private boolean hasFieldHandler(int idx)
    {
        return idx % 3 == 0;
    }

    /**
     * Returns the test component with the given name.
     *
     * @param i the index of the test component
     * @return the test component with this index
     */
    private Object getTestComponent(int i)
    {
        return String.valueOf(TEST_COMP) + i;
    }

    /**
     * Test implementation of the call back interface.
     */
    static class TestCallBack implements ComponentBuilderCallBack
    {
        private ComponentBuilderData builderData;

        private Object params;

        public ComponentBuilderData getBuilderData()
        {
            return builderData;
        }

        public Object getParams()
        {
            return params;
        }

        public void callBack(ComponentBuilderData builderData, Object params)
                throws FormBuilderException
        {
            this.builderData = builderData;
            this.params = params;
        }
    }

    /**
     * A test call back implementation that throws an exception.
     */
    static class TestCallBackEx implements ComponentBuilderCallBack
    {
        public void callBack(ComponentBuilderData builderData, Object params)
                throws FormBuilderException
        {
            throw new FormBuilderException("TestCallBackEx exception!");
        }
    }
}

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
package net.sf.jguiraffe.gui.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanCreationEvent;
import net.sf.jguiraffe.di.BeanCreationListener;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.impl.DefaultBeanContext;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.di.impl.DefaultClassLoaderProvider;
import net.sf.jguiraffe.gui.builder.Builder;
import net.sf.jguiraffe.gui.builder.BuilderData;
import net.sf.jguiraffe.gui.builder.action.ActionStore;
import net.sf.jguiraffe.gui.builder.action.FormActionImpl;
import net.sf.jguiraffe.gui.builder.utils.GUISynchronizer;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowImpl;
import net.sf.jguiraffe.gui.cmd.CommandQueueImpl;
import net.sf.jguiraffe.gui.forms.BindingStrategy;
import net.sf.jguiraffe.gui.forms.FormValidator;
import net.sf.jguiraffe.resources.Message;
import net.sf.jguiraffe.resources.ResourceManager;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ApplicationContextImpl.
 *
 * @author Oliver Heger
 * @version $Id: TestApplicationContextImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestApplicationContextImpl
{
    /** Constant for a resource group used for testing. */
    private static final Object RES_GRP = "myResourceGroup";

    /** Constant for a resource ID used for testing. */
    private static final Object RES_ID = "myResourceID";

    /** Constant for a test resource. */
    private static final String RESOURCE = "This is my test resource!";

    /** Constant for the locale used for the tests. */
    private static final Locale LOCALE = Locale.US;

    /** Stores the application context to be tested. */
    private ApplicationContextImplTestImpl appCtx;

    @Before
    public void setUp() throws Exception
    {
        appCtx = new ApplicationContextImplTestImpl();
    }

    /**
     * Prepares a test for querying resource. Creates a resource manager mock
     * and initializes some properties of the context.
     *
     * @return the resource manager mock
     */
    private ResourceManager setUpResourceTest()
    {
        ResourceManager resMan = EasyMock.createMock(ResourceManager.class);
        appCtx.setResourceManager(resMan);
        appCtx.setLocale(LOCALE);
        return resMan;
    }

    /**
     * Tries to install a null bean store at a context. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInstallBeanStoreNullStore()
    {
        ApplicationContextImpl.installBeanStore(new DefaultBeanContext(), null);
    }

    /**
     * Tries installing a bean store at a null context. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInstallBeanStoreNullContext()
    {
        ApplicationContextImpl.installBeanStore(null, new DefaultBeanStore());
    }

    /**
     * Tests the constructor with multiple initialization parameters.
     */
    @Test
    public void testInitWithProperties()
    {
        ResourceManager resMan = EasyMock.createMock(ResourceManager.class);
        EasyMock.replay(resMan);
        final Locale loc = Locale.CANADA_FRENCH;
        appCtx = new ApplicationContextImplTestImpl(loc, resMan);
        assertEquals("Wrong locale", loc, appCtx.getLocale());
        assertEquals("Wrong resource manager", resMan, appCtx
                .getResourceManager());
        EasyMock.verify(resMan);
    }

    /**
     * Tests resolving a resource when the group and the ID are specified.
     */
    @Test
    public void testGetResourceGroupID()
    {
        ResourceManager resMan = setUpResourceTest();
        EasyMock.expect(resMan.getResource(LOCALE, RES_GRP, RES_ID)).andReturn(
                RESOURCE);
        EasyMock.replay(resMan);
        assertEquals("Wrong resource returned", RESOURCE, appCtx.getResource(
                RES_GRP, RES_ID));
        EasyMock.verify(resMan);
    }

    /**
     * Tests resolving the resource text when the group and the ID are
     * specified.
     */
    @Test
    public void testGetResourceTextGroupID()
    {
        ResourceManager resMan = setUpResourceTest();
        EasyMock.expect(resMan.getText(LOCALE, RES_GRP, RES_ID)).andReturn(
                RESOURCE);
        EasyMock.replay(resMan);
        assertEquals("Wrong resource returned", RESOURCE, appCtx
                .getResourceText(RES_GRP, RES_ID));
        EasyMock.verify(resMan);
    }

    /**
     * Tests the getResource() method when a plain object is passed in.
     */
    @Test
    public void testGetResourceObject()
    {
        ResourceManager resMan = setUpResourceTest();
        EasyMock.expect(resMan.getResource(LOCALE, null, RES_ID)).andReturn(
                RESOURCE);
        EasyMock.replay(resMan);
        assertEquals("Wrong resource returned", RESOURCE, appCtx
                .getResource(RES_ID));
        EasyMock.verify(resMan);
    }

    /**
     * Tests the getResourceText() method when a plain object is passed in.
     */
    @Test
    public void testGetResourceTextObject()
    {
        ResourceManager resMan = setUpResourceTest();
        EasyMock.expect(resMan.getText(LOCALE, null, RES_ID)).andReturn(
                RESOURCE);
        EasyMock.replay(resMan);
        assertEquals("Wrong resource returned", RESOURCE, appCtx
                .getResourceText(RES_ID));
        EasyMock.verify(resMan);
    }

    /**
     * Tests the getResource() method when a message object is provided.
     */
    @Test
    public void testGetResourceMessage()
    {
        ResourceManager resMan = setUpResourceTest();
        EasyMock.replay(resMan);
        MessageTestImpl msg = new MessageTestImpl();
        assertEquals("Wrong resource returned", RESOURCE, appCtx
                .getResource(msg));
        EasyMock.verify(resMan);
        msg.verify(appCtx);
    }

    /**
     * Tests the getResourceText() method when a message object is provided.
     */
    @Test
    public void testGetResourceTextMessage()
    {
        ResourceManager resMan = setUpResourceTest();
        EasyMock.replay(resMan);
        MessageTestImpl msg = new MessageTestImpl();
        assertEquals("Wrong resource returned", RESOURCE, appCtx
                .getResourceText(msg));
        EasyMock.verify(resMan);
        msg.verify(appCtx);
    }

    /**
     * Tests the getResource(Object) method when the passed in object is a
     * Message.
     */
    @Test
    public void testGetResourceObjectMessage()
    {
        ResourceManager resMan = setUpResourceTest();
        EasyMock.replay(resMan);
        MessageTestImpl msg = new MessageTestImpl();
        assertEquals("Wrong resource returned", RESOURCE, appCtx
                .getResource((Object) msg));
        EasyMock.verify(resMan);
        msg.verify(appCtx);
    }

    /**
     * Tests the getResourceText(Object) method when the passed in object is a
     * Message.
     */
    @Test
    public void testGetResourceTextObjectMessage()
    {
        ResourceManager resMan = setUpResourceTest();
        EasyMock.replay(resMan);
        MessageTestImpl msg = new MessageTestImpl();
        assertEquals("Wrong resource returned", RESOURCE, appCtx
                .getResourceText((Object) msg));
        EasyMock.verify(resMan);
        msg.verify(appCtx);
    }

    /**
     * Tries to resolve a null message. This should cause an exception.
     */
    @Test
    public void testGetResourceTextMessageNull()
    {
        ResourceManager resMan = setUpResourceTest();
        EasyMock.replay(resMan);
        try
        {
            appCtx.getResourceText((Message) null);
            fail("Could resolve null message!");
        }
        catch (IllegalArgumentException iex)
        {
            EasyMock.verify(resMan);
        }
    }

    /**
     * Tests the messageBox() method.
     */
    @Test
    public void testMessageBox()
    {
        MessageOutput out = EasyMock.createMock(MessageOutput.class);
        Window wnd = EasyMock.createMock(Window.class);
        ResourceManager resMan = setUpResourceTest();
        final String titleRes = "myTitleResource";
        final String title = "MsgBoxTitle";
        Message msg = new Message(RES_GRP, titleRes);
        EasyMock.expect(resMan.getResource(LOCALE, null, RES_ID)).andReturn(
                RESOURCE);
        EasyMock.expect(resMan.getText(LOCALE, RES_GRP, titleRes)).andReturn(
                title);
        EasyMock.expect(
                out.show(wnd, RESOURCE, title, MessageOutput.MESSAGE_INFO,
                        MessageOutput.BTN_OK_CANCEL)).andReturn(
                MessageOutput.RET_OK);
        appCtx.setMainWindow(wnd);
        appCtx.setMessageOutput(out);
        EasyMock.replay(resMan, out, wnd);
        assertEquals("Wrong message box result", MessageOutput.RET_OK, appCtx
                .messageBox(RES_ID, msg, MessageOutput.MESSAGE_INFO,
                        MessageOutput.BTN_OK_CANCEL));
        EasyMock.verify(resMan, out, wnd);
    }

    /**
     * Tests obtaining a new builder instance.
     */
    @Test
    public void testNewBuilder()
    {
        BeanContext context = EasyMock.createMock(BeanContext.class);
        Builder builder = EasyMock.createMock(Builder.class);
        EasyMock.expect(context.getBean(Application.BEAN_BUILDER)).andReturn(
                builder);
        EasyMock.replay(context, builder);
        appCtx.setBeanContext(context);
        assertEquals("Wrong builder returned", builder, appCtx.newBuilder());
        EasyMock.verify(context, builder);
    }

    /**
     * Tests obtaining an action store from a newly created object. An empty
     * store should be returned.
     */
    @Test
    public void testGetActionStoreInit()
    {
        ActionStore store = appCtx.getActionStore();
        assertNotNull("No action store returned", store);
        assertTrue("Store contains actions", store.getActionNames().isEmpty());
        assertNull("Store has a parent", store.getParent());
    }

    /**
     * Tests creating a builder data object.
     */
    @Test
    public void testInitBuilderData()
    {
        ClassLoaderProvider clp = EasyMock
                .createMock(ClassLoaderProvider.class);
        MessageOutput output = EasyMock.createMock(MessageOutput.class);
        ResourceManager resMan = setUpResourceTest();
        EasyMock.expect(resMan.getDefaultResourceGroup()).andReturn(RES_GRP);
        Configuration config = new HierarchicalConfiguration();
        config.setProperty(Application.PROP_BUILDER_MENU_ICON, Boolean.TRUE);
        config.setProperty(Application.PROP_BUILDER_TOOLBAR_TEXT, Boolean.TRUE);
        Application app = prepareInitBuilderDataTest();
        appCtx.setConfiguration(config);
        Window mainWnd = new WindowImpl();
        appCtx.setMainWindow(mainWnd);
        appCtx.setMessageOutput(output);
        EasyMock.replay(resMan, appCtx.getBeanContext(), clp, output);

        BuilderData builderData = appCtx.initBuilderData();
        assertEquals("Wrong resource group", RES_GRP, builderData
                .getDefaultResourceGroup());
        assertNull("Form bean has a value", builderData.getFormBean());
        assertEquals("Wrong action store", appCtx.getActionStore(), builderData
                .getActionStore());
        assertEquals("Wrong transformer ctx", appCtx, builderData
                .getTransformerContext());
        assertTrue("Wrong menu icon flag", builderData.isMenuIcon());
        assertTrue("Wrong toolbar text flag", builderData.isToolbarText());
        assertEquals("Wrong parent window", mainWnd, builderData
                .getParentWindow());
        assertEquals("Wrong bean context", appCtx.getBeanContext(), builderData
                .getParentContext());
        assertEquals("Wrong message output", output, builderData
                .getMessageOutput());
        assertEquals("Wrong command queue", app.getCommandQueue(), builderData
                .getCommandQueue());
        assertNull("Builder context already set", builderData
                .getBuilderContext());
        assertNull("Builder result already set", builderData
                .getBeanBuilderResult());
        assertNull("Builder reference already set", builderData.getBuilder());
        assertNull("Got a builder name", builderData.getBuilderName());
        assertNull("Got an invocaton helper", builderData.getInvocationHelper());
        assertNotNull("No binding strategy", builderData.getBindingStrategy());
        assertEquals("Wrong calls of fetchBindingStrategy", 1,
                appCtx.fetchBindingStrategyCalls);
        assertNull("Got a form validator", builderData.getFormValidator());
        assertTrue("No auto release", builderData.isAutoRelease());
        ApplicationBuilderData appData = (ApplicationBuilderData) builderData;
        appData.setAutoRelease(false);
        assertFalse("Cannot change auto release", builderData.isAutoRelease());
        FormValidator val = EasyMock.createMock(FormValidator.class);
        EasyMock.replay(val);
        appData.setFormValidator(val);
        assertEquals("FormValidator cannot be set", val, builderData
                .getFormValidator());
        EasyMock.verify(resMan, appCtx.getBeanContext(), clp, output, val);
    }

    /**
     * Tests initializing a builder data object when there is already an
     * initialized action store. In this case a new instance should be created.
     */
    @Test
    public void testInitBuilderDataInitializedActionStore()
    {
        ResourceManager resMan = setUpResourceTest();
        EasyMock.expect(resMan.getDefaultResourceGroup()).andReturn(RES_GRP);
        prepareInitBuilderDataTest();
        EasyMock.replay(resMan, appCtx.getBeanContext());
        appCtx.setConfiguration(new HierarchicalConfiguration());
        appCtx.getActionStore().addAction(new FormActionImpl("TestAction"));
        BuilderData builderData = appCtx.initBuilderData();
        assertNotSame("No new action store created", appCtx.getActionStore(),
                builderData.getActionStore());
        assertSame("Parent store not set", appCtx.getActionStore(), builderData
                .getActionStore().getParent());
        EasyMock.verify(resMan, appCtx.getBeanContext());
    }

    /**
     * Tests initializing a builder data object when the action store is null.
     * In this case a new should have been created.
     */
    @Test
    public void testInitBuilderDataNullActionStore()
    {
        ResourceManager resMan = setUpResourceTest();
        EasyMock.expect(resMan.getDefaultResourceGroup()).andReturn(RES_GRP);
        prepareInitBuilderDataTest();
        EasyMock.replay(resMan, appCtx.getBeanContext());
        appCtx.setConfiguration(new HierarchicalConfiguration());
        appCtx.setActionStore(null);
        BuilderData builderData = appCtx.initBuilderData();
        assertNotNull("No action store created", builderData.getActionStore());
        EasyMock.verify(resMan, appCtx.getBeanContext());
    }

    /**
     * Tests whether the builder data object created by the context contains a
     * bean creation listener that can inject the application instance.
     */
    @Test
    public void testInitBuilderDataBeanCreationListener()
    {
        ApplicationClient client = EasyMock.createMock(ApplicationClient.class);
        ResourceManager resMan = setUpResourceTest();
        EasyMock.expect(resMan.getDefaultResourceGroup()).andReturn(RES_GRP);
        Application app = prepareInitBuilderDataTest();
        client.setApplication(app);
        EasyMock.replay(resMan, appCtx.getBeanContext(), client);
        appCtx.setConfiguration(new HierarchicalConfiguration());
        BuilderData builderData = appCtx.initBuilderData();
        assertEquals("Wrong number of bean creation listeners", 1, builderData
                .getBeanCreationListeners().size());
        BeanCreationListener bcl = builderData.getBeanCreationListeners()
                .iterator().next();
        bcl.beanCreated(new BeanCreationEvent(appCtx.getBeanContext(), null,
                null, client));
        EasyMock.verify(resMan, appCtx.getBeanContext(), client);
    }

    /**
     * Prepares a test for initializing the builder data. Creates the bean
     * context and prepares it to expect a query for the application object. The
     * application object is returned.
     *
     * @return the application
     */
    private Application prepareInitBuilderDataTest()
    {
        BeanContext beanContext = EasyMock.createMock(BeanContext.class);
        Application app = new Application();
        app.setCommandQueue(new CommandQueueImpl(EasyMock
                .createNiceMock(GUISynchronizer.class)));
        EasyMock.expect(beanContext.getBean(Application.BEAN_APPLICATION))
                .andReturn(app);
        EasyMock.expect(beanContext.getBean(Application.BEAN_BINDING_STRATEGY))
                .andReturn(EasyMock.createNiceMock(BindingStrategy.class));
        appCtx.setBeanContext(beanContext);
        return app;
    }

    /**
     * Tests whether the binding strategy is correctly fetched.
     */
    @Test
    public void testFetchBindingStrategy()
    {
        BeanContext ctx = EasyMock.createMock(BeanContext.class);
        BindingStrategy strat = EasyMock.createMock(BindingStrategy.class);
        EasyMock.expect(ctx.getBean(Application.BEAN_BINDING_STRATEGY))
                .andReturn(strat);
        EasyMock.replay(ctx, strat);
        appCtx.setBeanContext(ctx);
        assertEquals("Wrong binding strategy", strat, appCtx
                .fetchBindingStrategy());
        EasyMock.verify(ctx, strat);
    }

    /**
     * Tests obtaining the GUI synchronizer.
     */
    @Test
    public void testGetGUISynchronizer()
    {
        BeanContext mockContext = EasyMock.createMock(BeanContext.class);
        GUISynchronizer mockSync = EasyMock.createMock(GUISynchronizer.class);
        EasyMock.expect(mockContext.getBean(Application.BEAN_GUI_SYNCHRONIZER))
                .andReturn(mockSync);
        EasyMock.replay(mockContext, mockSync);
        appCtx.setBeanContext(mockContext);
        assertEquals("Wrong GUISynchronizer", mockSync, appCtx
                .getGUISynchronizer());
        EasyMock.verify(mockContext, mockSync);
    }

    /**
     * Tries setting the validation message handler to null. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetValidationMessageHandlerNull()
    {
        appCtx.setValidationMessageHandler(null);
    }

    /**
     * Tests whether a typed property can be set.
     */
    @Test
    public void testSetTypedProperty()
    {
        appCtx.setTypedProperty(TestApplicationContextImpl.class, this);
        assertEquals("Wrong property value", this, appCtx
                .getTypedProperty(TestApplicationContextImpl.class));
    }

    /**
     * Tests whether the value of a typed property can be changed.
     */
    @Test
    public void testSetTypedPropertyOverride()
    {
        appCtx.setTypedProperty(Integer.class, 41);
        appCtx.setTypedProperty(Integer.class, 42);
        assertEquals("Wrong property value", Integer.valueOf(42), appCtx
                .getTypedProperty(Integer.class));
    }

    /**
     * Tests whether a typed property can be removed again.
     */
    @Test
    public void testSetTypedPropertyClear()
    {
        appCtx.setTypedProperty(Integer.class, 100);
        appCtx.setTypedProperty(Integer.class, null);
        assertNull("Got a property value", appCtx
                .getTypedProperty(Integer.class));
    }

    /**
     * Tries to set a typed property without a property class. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetTypedPropertyNoClass()
    {
        appCtx.setTypedProperty(null, 100);
    }

    /**
     * Tries to obtain a value for the typed property null.
     */
    @Test
    public void testGetTypedPropertyNullClass()
    {
        assertNull("Got a value", appCtx.getTypedProperty(null));
    }

    /**
     * Tests whether the class loader provider can be set and whether this
     * affects the bean context.
     */
    @Test
    public void testSetClassLoaderProvider()
    {
        ClassLoaderProvider clp = new DefaultClassLoaderProvider();
        BeanContext bctx = new DefaultBeanContext();
        appCtx.setBeanContext(bctx);
        appCtx.setClassLoaderProvider(clp);
        assertSame("CLP was not set", clp, appCtx.getClassLoaderProvider());
        assertSame("CLP not set in bean context", clp,
                bctx.getClassLoaderProvider());
    }

    /**
     * Tests setClassLoaderProvider() if there is no bean context.
     */
    @Test(expected = IllegalStateException.class)
    public void testSetClassLoaderProviderNoBeanCtx()
    {
        appCtx.setClassLoaderProvider(new DefaultClassLoaderProvider());
    }

    /**
     * Tests whether the class loader provider can be queried and whether it is
     * the same as the one used by the bean context.
     */
    @Test
    public void testGetClassLoaderProvider()
    {
        BeanContext bctx = new DefaultBeanContext();
        appCtx.setBeanContext(bctx);
        ClassLoaderProvider clp = appCtx.getClassLoaderProvider();
        assertNotNull("No CLP", clp);
        assertSame("Wrong CLP", bctx.getClassLoaderProvider(), clp);
    }

    /**
     * Tests the behavior of getClassLoaderProvider() if no bean context is set.
     */
    @Test
    public void testGetClassLoaderProviderNoBeanCtx()
    {
        assertNull("Got a CLP", appCtx.getClassLoaderProvider());
    }

    /**
     * A specialized Message implementation that can be used for testing.
     */
    static class MessageTestImpl extends Message
    {
        /** Stores the resource manager. */
        private ResourceManager resourceManager;

        /** Stores the locale. */
        private Locale currentLocale;

        /** A flag whether this message was resolved. */
        private boolean resolved;

        public MessageTestImpl()
        {
            super(RES_GRP, RES_ID);
        }

        @Override
        public String resolve(ResourceManager resMan, Locale locale)
        {
            // record this invocation
            resourceManager = resMan;
            currentLocale = locale;
            resolved = true;
            return RESOURCE;
        }

        /**
         * Tests whether the message was correctly resolved.
         *
         * @param appCtx the application context
         */
        public void verify(ApplicationContextImpl appCtx)
        {
            assertTrue("Message was not resolved", resolved);
            assertSame("Wrong resource manager", appCtx.getResourceManager(),
                    resourceManager);
            assertEquals("Wrong locale", appCtx.getLocale(), currentLocale);
        }
    }

    /**
     * A test implementation of ApplicationContextImpl.
     */
    private static class ApplicationContextImplTestImpl extends
            ApplicationContextImpl
    {
        /** The number of invocations of fetchBindingStrategy(). */
        int fetchBindingStrategyCalls;

        public ApplicationContextImplTestImpl()
        {
            super();
        }

        public ApplicationContextImplTestImpl(Locale locale,
                ResourceManager resMan)
        {
            super(locale, resMan);
        }

        /**
         * Counts this invocation.
         */
        @Override
        protected BindingStrategy fetchBindingStrategy()
        {
            fetchBindingStrategyCalls++;
            return super.fetchBindingStrategy();
        }
    }
}

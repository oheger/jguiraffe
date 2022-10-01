/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanCreationListener;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.ConversionHelper;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.MutableBeanStore;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.BeanContextWrapper;
import net.sf.jguiraffe.di.impl.CombinedBeanStore;
import net.sf.jguiraffe.di.impl.DefaultBeanContext;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.di.impl.DefaultClassLoaderProvider;
import net.sf.jguiraffe.di.impl.SimpleBeanStoreImpl;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.gui.builder.AutoReleaseListener;
import net.sf.jguiraffe.gui.builder.BeanBuilderResult;
import net.sf.jguiraffe.gui.builder.Builder;
import net.sf.jguiraffe.gui.builder.BuilderData;
import net.sf.jguiraffe.gui.builder.BuilderException;
import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionManager;
import net.sf.jguiraffe.gui.builder.action.ActionManagerImpl;
import net.sf.jguiraffe.gui.builder.action.ActionStore;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.action.FormActionImpl;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderCallBack;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.ComponentManagerImpl;
import net.sf.jguiraffe.gui.builder.components.Container;
import net.sf.jguiraffe.gui.builder.components.DefaultFieldHandlerFactory;
import net.sf.jguiraffe.gui.builder.components.FieldHandlerFactory;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.FormContextListener;
import net.sf.jguiraffe.gui.builder.enablers.ElementEnabler;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowImpl;
import net.sf.jguiraffe.gui.builder.window.WindowManager;
import net.sf.jguiraffe.gui.builder.window.WindowManagerImpl;
import net.sf.jguiraffe.gui.cmd.CommandQueue;
import net.sf.jguiraffe.gui.forms.BindingStrategy;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.forms.FormValidator;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.locators.ClassPathLocator;
import net.sf.jguiraffe.locators.FileLocator;
import net.sf.jguiraffe.locators.Locator;
import net.sf.jguiraffe.locators.LocatorConverter;
import net.sf.jguiraffe.transform.TransformerContext;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.jelly.JellyContext;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for Jelly Builder. This class also implements the BuilderData
 * interface to pass well defined test parameters to the builder object.
 *
 * @author Oliver Heger
 * @version $Id: TestJellyBuilder.java 211 2012-07-10 19:49:13Z oheger $
 */
public class TestJellyBuilder implements BuilderData
{
    /** Constant for the builder name. */
    private static final String BUILDER_NAME = "TestBuilder";

    /** Constant for the name of the container builder. */
    private static final String CONTAINER_BUILDER = "container";

    /** Constant for the default resource group. */
    private static final String DEF_RES_GRP = "DefaultResourceGroup";

    /** Constant for the name of the test script. */
    private static final String SCRIPT = "/jelly_scripts/builder.jelly";

    /** Constant for the locator to the test script. */
    private static final Locator SCRIPT_LOCATOR = ClassPathLocator.getInstance(SCRIPT);

    /** Constant for the transformer context. */
    private static final TransformerContext TRANSFORMER_CONTEXT = new TransformerContextImpl();

    /** Constant for the action store. */
    private static final ActionStore ACTION_STORE = new ActionStore();

    /** Constant for the parent window. */
    private static final WindowImpl PARENT_WINDOW = new WindowImpl(
            "ParentWindow");

    /** Constant for the menu icon flag. */
    private static final boolean MENU_ICON = true;

    /** Constant for the toolbar text flag. */
    private static final boolean TOOLBAR_TEXT = true;

    /** Constant for the form bean. */
    private static final String FORM_BEAN = "FormBean";

    /** Constant for the binding strategy. */
    private static final BindingStrategy BINDING_STRATEGY = new BeanBindingStrategy();

    /** Constant for the form validator. */
    private static final FormValidator VALIDATOR = EasyMock
            .createNiceMock(FormValidator.class);

    /** Constant for the classes for which test converters exist. */
    private static final Class<?>[] CONVERTER_CLASSES = {
            Locator.class, Collection.class, List.class, Set.class,
            ElementEnabler.class
    };

    /** Constant for the test file with the builder script. */
    static final File SCRIPT_FILE = new File(new File("target"),
            "testscript.jelly");

    /** Stores the parent bean context. */
    private BeanContext parentBeanContext;

    /** Stores the context used by the builder. */
    private BeanContext builderContext;

    /** Stores the invocation helper. */
    private InvocationHelper invocationHelper;

    /** Stores the bean builder result. */
    private BeanBuilderResult beanBuilderResult;

    /** Stores the builder object to be tested. */
    private JellyBuilderTestImpl builder;

    /** Stores the action store to be returned by the builder data. */
    private ActionStore actionStore;

    /**
     * A collection with bean creation listeners to be returned by the builder
     * data.
     */
    private Collection<BeanCreationListener> beanCreationListeners;

    /** A map with additional properties. */
    private Map<String, Object> properties;

    /** The builder reference set by the builder itself.*/
    private Builder processingBuilder;

    /** The auto release flag.*/
    private boolean autoRelease;

    /** The builder name.*/
    private String builderName;

    @Before
    public void setUp() throws Exception
    {
        builder = new JellyBuilderTestImpl();
        parentBeanContext = new DefaultBeanContext(new DefaultBeanStore());
        actionStore = ACTION_STORE;
    }

    public Object getDefaultResourceGroup()
    {
        return DEF_RES_GRP;
    }

    public TransformerContext getTransformerContext()
    {
        return TRANSFORMER_CONTEXT;
    }

    public ActionStore getActionStore()
    {
        return actionStore;
    }

    public boolean isMenuIcon()
    {
        return MENU_ICON;
    }

    public boolean isToolbarText()
    {
        return TOOLBAR_TEXT;
    }

    public Object getFormBean()
    {
        return FORM_BEAN;
    }

    public BindingStrategy getBindingStrategy()
    {
        return BINDING_STRATEGY;
    }

    public FormValidator getFormValidator()
    {
        return VALIDATOR;
    }

    public Window getParentWindow()
    {
        return PARENT_WINDOW;
    }

    public BeanBuilderResult getBeanBuilderResult()
    {
        return beanBuilderResult;
    }

    public BeanContext getParentContext()
    {
        return parentBeanContext;
    }

    public BeanStore getRootStore()
    {
        return beanBuilderResult.getBeanStore(null);
    }

    public void setBeanBuilderResult(BeanBuilderResult res)
    {
        beanBuilderResult = res;
    }

    public BeanContext getBuilderContext()
    {
        return builderContext;
    }

    public void setBuilderContext(BeanContext ctx)
    {
        builderContext = ctx;
    }

    public InvocationHelper getInvocationHelper()
    {
        return invocationHelper;
    }

    public CommandQueue getCommandQueue()
    {
        return null;
    }

    public MessageOutput getMessageOutput()
    {
        return null;
    }

    public Collection<BeanCreationListener> getBeanCreationListeners()
    {
        return beanCreationListeners;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public Builder getBuilder()
    {
        return processingBuilder;
    }

    public String getBuilderName()
    {
        return builderName;
    }

    public boolean isAutoRelease()
    {
        return autoRelease;
    }

    public void setBuilder(Builder bldr)
    {
        processingBuilder = bldr;
    }

    /**
     * Tests the name space URIs of a newly created instance. They should have
     * default values.
     */
    @Test
    public void testInitNameSpaces()
    {
        assertEquals("Wrong name space for beans",
                JellyBeanBuilderFactory.NSURI_DI_BUILDER, builder
                        .getDiBuilderNameSpaceURI());
        assertEquals("Wrong name space for components",
                JellyBuilder.NSURI_COMPONENT_BUILDER, builder
                        .getComponentBuilderNamespace());
        assertEquals("Wrong name space for actions",
                JellyBuilder.NSURI_ACTION_BUILDER, builder
                        .getActionBuilderNamespace());
        assertEquals("Wrong name space for windows",
                JellyBuilder.NSURI_WINDOW_BUILDER, builder
                        .getWindowBuilderNamespace());
    }

    /**
     * Tests a fresh instance.
     */
    @Test
    public void testInit()
    {
        assertNull("Name already set", builder.getName());
        assertNull("ComponentManager already set", builder
                .getComponentManager());
        assertNull("ActionManager already set", builder.getActionManager());
        assertNull("WindowManager already set", builder.getWindowManager());
        assertNotNull("No FieldHandlerFactory set", builder
                .getFieldHandlerFactory());
        assertTrue("Got default converters", builder.getDefaultConverters()
                .isEmpty());
        assertTrue("Got default base class converters", builder
                .getDefaultBaseClassConverters().isEmpty());
    }

    /**
     * Prepares a test for creating a component builder data object.
     *
     * @return the data object created by the builder
     */
    private ComponentBuilderData prepareCreateCompBuilderDataTest()
    {
        ComponentManager manager = new ComponentManagerImpl();
        return prepareCreateCompBuilderDataTest(manager);
    }

    /**
     * Prepares a test for creating a component builder data object based on the
     * passed in {@code ComponentManager}.
     *
     * @param manager the component manager to be used
     * @return the data object created by the builder
     */
    private ComponentBuilderData prepareCreateCompBuilderDataTest(
            ComponentManager manager)
    {
        builder.setComponentManager(manager);
        FieldHandlerFactory fhFactory = new DefaultFieldHandlerFactory();
        builder.setFieldHandlerFactory(fhFactory);
        Object container = "root";
        ComponentBuilderData data =
                builder.createComponentBuilderData(this, container);
        assertSame("Wrong field handler factory", fhFactory,
                data.getFieldHandlerFactory());
        assertSame("Wrong component manager", manager,
                data.getComponentManager());
        assertSame("Wrong root container", container, data.getRootContainer());
        assertSame("Wrong transformer context", TRANSFORMER_CONTEXT,
                data.getTransformerContext());
        assertSame("Wrong resource group", DEF_RES_GRP,
                data.getDefaultResourceGroup());
        return data;
    }

    /**
     * Tests if the component builder data object is correctly initialized and
     * is given the correct builder name.
     */
    @Test
    public void testCreateComponentBuilderData()
    {
        builder.setName(BUILDER_NAME);
        ComponentBuilderData data = prepareCreateCompBuilderDataTest();
        assertEquals("Wrong builder name", BUILDER_NAME, data.getBuilderName());
        Form form = data.getForm();
        assertSame("Wrong binding strategy", BINDING_STRATEGY, form
                .getBindingStrategy());
        assertSame("Wrong form validator", VALIDATOR, form.getFormValidator());
    }

    /**
     * Tests whether a builder name in the builder data object overrides the
     * default name.
     */
    @Test
    public void testCreateComponentBuilderDataOverrideName()
    {
        builder.setName("Some test name that will be overridded");
        builderName = BUILDER_NAME;
        ComponentBuilderData data = prepareCreateCompBuilderDataTest();
        assertEquals("Wrong builder name", BUILDER_NAME, data.getBuilderName());
    }

    /**
     * Tests whether the component builder data object can be instantiated from
     * the parent context.
     */
    @Test
    public void testCreateComponentBuilderDataFromParentContext()
    {
        BeanContext bc = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(bc.containsBean("jguiraffe.componentBuilderData"))
                .andReturn(Boolean.TRUE);
        ComponentBuilderData data = new ComponentBuilderData();
        EasyMock.expect(bc.getBean("jguiraffe.componentBuilderData"))
                .andReturn(data);
        EasyMock.replay(bc);
        parentBeanContext = bc;
        builder.setName(BUILDER_NAME);
        Object rootContainer = new Object();
        assertSame("Wrong component builder data", data, builder
                .createComponentBuilderData(this, rootContainer));
        assertEquals("Wrong builder name", BUILDER_NAME, data.getBuilderName());
        assertEquals("Root container not set", rootContainer, data
                .getRootContainer());
        EasyMock.verify(bc);
    }

    /**
     * Tests whether the component manager is automatically registered as form
     * context listener if it implements this interface.
     */
    @Test
    public void testCreateComponentBuilderDataFormContextListener()
    {
        ComponentManagerFormContextListener manager =
                EasyMock.createMock(ComponentManagerFormContextListener.class);
        Form form = new Form(TRANSFORMER_CONTEXT, BINDING_STRATEGY);
        manager.formContextCreated(EasyMock.anyObject(Form.class),
                EasyMock.anyObject());
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.replay(manager);
        ComponentBuilderData builderData =
                prepareCreateCompBuilderDataTest(manager);

        builderData.pushFormContext(form);
        EasyMock.verify(manager);
    }

    /**
     * Tests whether the root store is correctly created and linked into the
     * hierarchy of bean stores.
     */
    @Test
    public void testInitBuilderBeanContextHierarchy()
    {
        JellyContext ctx = prepareRootStoreTest();
        MutableBeanStore rootStore =
                builder.initBuilderBeanContext(this, ctx,
                        new InvocationHelper());
        BeanContext beanContext =
                ComponentBuilderData.get(ctx).getBeanContext();
        assertTrue("Wrong parent of root store",
                rootStore.getParent() instanceof SimpleBeanStoreImpl);
        assertTrue("BeanContext not initialized",
                beanContext.getDefaultBeanStore() instanceof CombinedBeanStore);
        assertEquals("Wrong parent of component data", getParentContext()
                .getDefaultBeanStore(), rootStore.getParent().getParent());
        assertTrue("Wrong context class " + beanContext,
                beanContext instanceof BeanContextWrapper);
        CombinedBeanStore cbs =
                (CombinedBeanStore) beanContext.getDefaultBeanStore();
        assertEquals("Wrong number of child stores", 2, cbs.size());
        assertTrue("Wrong class of 1st child store " + cbs.getChildStore(0),
                cbs.getChildStore(0) instanceof MutableBeanStore);
        assertTrue("Wrong class of 2nd child store",
                cbs.getChildStore(1) instanceof JellyContextBeanStore);
        assertEquals("Builder context has not been set", beanContext,
                getBuilderContext());
    }

    /**
     * Tests whether the correct beans can be accessed from the initialized bean
     * context.
     */
    @Test
    public void testInitBuilderBeanContextBeanAccess()
    {
        MutableBeanStore parentStore =
                (MutableBeanStore) getParentContext().getDefaultBeanStore();
        parentStore.addBeanProvider("parentBean",
                ConstantBeanProvider.getInstance("parentBean"));
        JellyContext ctx = prepareRootStoreTest();
        ctx.setVariable("jellyBean", "jellyBean");
        ctx.setVariable("rootBean", "a different bean");
        MutableBeanStore rootStore =
                builder.initBuilderBeanContext(this, ctx,
                        new InvocationHelper());
        rootStore.addBeanProvider("rootBean",
                ConstantBeanProvider.getInstance("rootBean"));
        BeanContext beanContext =
                ComponentBuilderData.get(ctx).getBeanContext();
        checkBean(beanContext, "parentBean");
        checkBean(beanContext, "rootBean");
        checkBean(beanContext, "jellyBean");
    }

    /**
     * Tests whether components can be accessed from the initialized bean
     * context.
     */
    @Test
    public void testInitBuilderBeanContextBeanAccessComponents()
    {
        JellyContext ctx = prepareRootStoreTest();
        ComponentBuilderData compData = ComponentBuilderData.get(ctx);
        ComponentHandlerImpl ch = new ComponentHandlerImpl();
        ch.setComponent("component");
        compData.storeComponentHandler("component", ch);
        builder.initBuilderBeanContext(this, ctx, new InvocationHelper());
        BeanContext beanContext =
                ComponentBuilderData.get(ctx).getBeanContext();
        checkBean(beanContext, "component");
        ComponentHandler<?> compHandler =
                (ComponentHandler<?>) beanContext.getBean("comp:component");
        assertEquals("Wrong component handler", "component",
                compHandler.getComponent());
    }

    /**
     * Tests whether actions can be accessed from the initialized bean context.
     */
    @Test
    public void testInitBuilderBeanContextBeanAccessActions()
    {
        JellyContext ctx = prepareRootStoreTest();
        ActionBuilder actData = ActionBuilder.get(ctx);
        actionStore = new ActionStore();
        FormAction action = new FormActionImpl("TestAction");
        actionStore.addAction(action);
        actData.setActionStore(actionStore);
        builder.initBuilderBeanContext(this, ctx, new InvocationHelper());
        assertSame("Wrong action returned", action, getBuilderContext()
                .getBean("action:TestAction"));
        assertSame("Wrong action builder", actData, getBuilderContext()
                .getBean(ActionBuilder.KEY_ACTION_BUILDER));
    }

    /**
     * Tests whether objects related to the window builder can be accessed from
     * the initialized bean context.
     */
    @Test
    public void testInitBuilderBeanContextBeanAccessWindows()
    {
        JellyContext ctx = prepareRootStoreTest();
        WindowBuilderData wndData = WindowBuilderData.get(ctx);
        final Window resultWindow = new WindowImpl();
        wndData.setResultWindow(resultWindow);
        wndData.setFormBean(this);
        builder.initBuilderBeanContext(this, ctx, new InvocationHelper());
        assertSame("Wrong result window", resultWindow, getBuilderContext()
                .getBean(WindowBuilderData.KEY_CURRENT_WINDOW));
        assertSame("Wrong form bean", this,
                getBuilderContext().getBean(WindowBuilderData.KEY_FORM_BEAN));
        assertSame("Wrong window builder data", wndData, getBuilderContext()
                .getBean(WindowBuilderData.KEY_WINDOW_BUILDER_DATA));
    }

    /**
     * Tests whether objects stored in the Jelly context can be accessed from
     * the bean context.
     */
    @Test
    public void testInitBuilderBeanContextBeanAccessJellyContext()
    {
        JellyContext ctx = prepareRootStoreTest();
        final Object testBean = new Object();
        ctx.setVariable("testBean", testBean);
        builder.initBuilderBeanContext(this, ctx, new InvocationHelper());
        assertSame("Wrong test bean", testBean,
                getBuilderContext().getBean("testBean"));
        assertSame(
                "Wrong builder data",
                this,
                getBuilderContext().getBean(
                        ComponentBuilderData.KEY_BUILDER_DATA));
    }

    /**
     * Tests whether bean creation listeners are registered when the bean
     * context is initialized.
     */
    @Test
    public void testInitBuilderBeanContextRegisterBeanCreationListeners()
    {
        JellyContext ctx = prepareRootStoreTest();
        builder.initBuilderBeanContext(this, ctx, new InvocationHelper());
        assertEquals("Wrong builder data", this,
                builder.registerListenersBuilderData);
        assertEquals("Wrong builder context", getBuilderContext(),
                builder.registerListenersContext);
    }

    /**
     * Performs initialization for a test for creating the root bean store.
     *
     * @return the Jelly context to be used for the test
     */
    private JellyContext prepareRootStoreTest()
    {
        builder.setName(BUILDER_NAME);
        builder.setComponentManager(new ComponentManagerImpl());
        JellyContext context = new JellyContext();
        ComponentBuilderData cdata = builder.createComponentBuilderData(this,
                new Object());
        cdata.put(context);
        ActionBuilder actionData = builder.createActionBuilderData(this);
        actionData.put(context);
        WindowBuilderData windowData = builder.createWindowBuilderData(this);
        windowData.put(context);
        return context;
    }

    /**
     * Tests registering bean creation listeners at the builder bean context.
     */
    @Test
    public void testRegisterBeanCreationListeners()
    {
        BeanContext context = EasyMock.createMock(BeanContext.class);
        final int count = 5;
        beanCreationListeners = new ArrayList<BeanCreationListener>(count);
        for (int i = 0; i < count; i++)
        {
            BeanCreationListener l = EasyMock
                    .createMock(BeanCreationListener.class);
            beanCreationListeners.add(l);
            context.addBeanCreationListener(l);
        }
        EasyMock.replay(beanCreationListeners.toArray());
        EasyMock.replay(context);
        builder.registerBeanCreationListeners(context, this);
        EasyMock.verify(beanCreationListeners.toArray());
        EasyMock.verify(context);
    }

    /**
     * Tests registering bean creation listeners when the collection is null.
     */
    @Test
    public void testRegisterBeanCreationListenersNull()
    {
        BeanContext context = EasyMock.createMock(BeanContext.class);
        EasyMock.replay(context);
        builder.registerBeanCreationListeners(context, this);
        EasyMock.verify(context);
    }

    /**
     * Helper method for accessing a bean from the test context. This method
     * expects that the bean is a string with the same value as the bean name.
     *
     * @param ctx the context
     * @param name the name
     */
    private void checkBean(BeanContext ctx, String name)
    {
        assertEquals("Wrong bean: " + name, name, ctx.getBean(name));
    }

    /**
     * Tests if the action builder data object is correctly initialized.
     */
    @Test
    public void testCreateActionBuilderData()
    {
        ActionManager manager = new ActionManagerImpl();
        builder.setActionManager(manager);
        ActionBuilder data = builder.createActionBuilderData(this);
        assertSame("Wrong action manager", manager, data.getActionManager());
        assertSame("Wrong action store", ACTION_STORE, data.getActionStore());
        assertEquals("Wrong menu icon flag", MENU_ICON, data.isMenuIcon());
        assertEquals("Wrong toolbar text flag", TOOLBAR_TEXT, data
                .isToolbarText());
    }

    /**
     * Tests whether the action builder data object can be created from the
     * parent bean context.
     */
    @Test
    public void testCreateActionBuilderDataFromParentContext()
    {
        BeanContext bc = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(bc.containsBean("jguiraffe.actionBuilder")).andReturn(
                Boolean.TRUE);
        ActionBuilder data = new ActionBuilder();
        EasyMock.expect(bc.getBean("jguiraffe.actionBuilder")).andReturn(data);
        EasyMock.replay(bc);
        parentBeanContext = bc;
        ActionManager manager = new ActionManagerImpl();
        builder.setActionManager(manager);
        assertSame("Wrong action data", data, builder
                .createActionBuilderData(this));
        assertSame("Wrong action manager", manager, data.getActionManager());
        EasyMock.verify(bc);
    }

    /**
     * Tests if the window builder data object is correctly initialized.
     */
    @Test
    public void testCreateWindowBuilderData()
    {
        WindowManager manager = new WindowManagerImpl();
        builder.setWindowManager(manager);
        WindowBuilderData data = builder.createWindowBuilderData(this);
        assertSame("Wrong window manager", manager, data.getWindowManager());
        assertSame("Wrong form bean", FORM_BEAN, data.getFormBean());
        assertSame("Wrong parent window", PARENT_WINDOW, data.getParentWindow());
    }

    /**
     * Tests whether the window builder data object can be created from the
     * parent bean context.
     */
    @Test
    public void testCreateWindowBuilderDataFromParentContext()
    {
        BeanContext bc = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(bc.containsBean("jguiraffe.windowBuilderData"))
                .andReturn(Boolean.TRUE);
        WindowBuilderData data = new WindowBuilderData();
        EasyMock.expect(bc.getBean("jguiraffe.windowBuilderData")).andReturn(
                data);
        EasyMock.replay(bc);
        WindowManager manager = new WindowManagerImpl();
        builder.setWindowManager(manager);
        parentBeanContext = bc;
        assertSame("Wrong window builder data", data, builder
                .createWindowBuilderData(this));
        assertSame("Wrong window manager", manager, data.getWindowManager());
        EasyMock.verify(bc);
    }

    /**
     * Tests registering the tag libraries with non default names paces.
     */
    @Test
    public void testRegisterTagLibrariesNonDefault()
    {
        JellyContext context = new JellyContext();
        builder.setComponentBuilderNamespace("TestComponents");
        builder.setActionBuilderNamespace("TestActions");
        builder.setWindowBuilderNamespace("TestWindows");
        builder.setDiBuilderNameSpaceURI("TestBeans");
        builder.registerExtendedTagLibraries(context, this);
        assertTrue("Component builder library not registered", context
                .isTagLibraryRegistered("TestComponents"));
        assertTrue("Action builder library not registered", context
                .isTagLibraryRegistered("TestActions"));
        assertTrue("Window builder library not registered", context
                .isTagLibraryRegistered("TestWindows"));
        assertTrue("Bean builder library not registered", context
                .isTagLibraryRegistered("TestBeans"));
    }

    /**
     * Tests whether the extended Jelly context is correctly set up.
     */
    @Test
    public void testSetUpExtendedJellyContext()
    {
        final Object rootContainer = new Object();
        initManagers();
        JellyContext context = builder.setUpExtendedJellyContext(this,
                rootContainer);

        assertNotNull("No component data found", ComponentBuilderData
                .get(context));
        assertNotNull("No action builder data found", ActionBuilder
                .get(context));
        assertNotNull("No window builder data found", WindowBuilderData
                .get(context));
        assertTrue("Component builder library not registered", context
                .isTagLibraryRegistered(JellyBuilder.NSURI_COMPONENT_BUILDER));
        assertTrue("Action builder library not registered", context
                .isTagLibraryRegistered(JellyBuilder.NSURI_ACTION_BUILDER));
        assertTrue("Window builder library not registered", context
                .isTagLibraryRegistered(JellyBuilder.NSURI_WINDOW_BUILDER));
        assertTrue(
                "Bean builder library not registered",
                context
                        .isTagLibraryRegistered(JellyBeanBuilderFactory.NSURI_DI_BUILDER));
    }

    /**
     * Tests whether additional properties are added to the Jelly context.
     */
    @Test
    public void testSetUpExtendedJellyContextWithProps()
    {
        initManagers();
        Map<String, Object> props = new HashMap<String, Object>();
        final int count = 8;
        final String key = "testProperty";
        final String val = "propertyValue";
        for (int i = 0; i < count; i++)
        {
            props.put(key + i, val + i);
        }
        properties = props;
        JellyContext context = builder.setUpExtendedJellyContext(this,
                new Object());
        for (int i = 0; i < count; i++)
        {
            String prop = key + i;
            assertEquals("Wrong value for property " + prop, val + i, context
                    .getVariable(prop));
        }
    }

    /**
     * Tests a build operation when no component manager is set.
     */
    @Test
    public void testCheckStateNoComponentManager() throws BuilderException
    {
        initManagers();
        builder.setComponentManager(null);
        checkBuildWithMissingProperties("ComponentManager");
    }

    /**
     * Tests a build operation when no action manager is set.
     */
    @Test
    public void testCheckStateNoActionManager() throws BuilderException
    {
        initManagers();
        builder.setActionManager(null);
        checkBuildWithMissingProperties("ActionManager");
    }

    /**
     * Tests a build operation when no window manager is set.
     */
    @Test
    public void testCheckStateNoWindowManager() throws BuilderException
    {
        initManagers();
        builder.setWindowManager(null);
        checkBuildWithMissingProperties("WindowManager");
    }

    /**
     * Tests a build operation when the builder has not been fully initialized.
     * This should cause an exception.
     *
     * @param propName the name of the missing property
     */
    private void checkBuildWithMissingProperties(String propName)
            throws BuilderException
    {
        try
        {
            builder.build(SCRIPT_LOCATOR, this);
            fail("Missing " + propName + " was not detected!");
        }
        catch (IllegalStateException istex)
        {
            // ok
        }
    }

    /**
     * Executes a build operation. Invokes the builder on the test script.
     *
     * @throws BuilderException if an exception occurs
     */
    private void executeBuild() throws BuilderException
    {
        initManagers();
        builder.build(SCRIPT_LOCATOR, this);
    }

    /**
     * Tests the generic build() method.
     */
    @Test
    public void testBuild() throws BuilderException
    {
        executeBuild();
        checkResults();
        assertFalse("Found bean in container", getBuilderContext()
                .containsBean("containerBean"));
    }

    /**
     * Tests a build() operation if no parent context is set.
     */
    @Test(expected = BuilderException.class)
    public void testBuildNoParentContext() throws BuilderException
    {
        parentBeanContext = null;
        executeBuild();
    }

    /**
     * Tests whether the builder sets a reference to itself in the builder data
     * object.
     */
    @Test
    public void testBuildSetReference() throws BuilderException
    {
        executeBuild();
        assertEquals("Wrong builder reference", builder, processingBuilder);
    }

    /**
     * Tests the buildContainer() method.
     */
    @Test
    public void testBuildContainer() throws Exception
    {
        initManagers();
        Container rootContainer = new Container();
        builder.setName(CONTAINER_BUILDER);
        builder.buildContainer(SCRIPT_LOCATOR, this, rootContainer);
        checkResults();
        String cnt = rootContainer.toString();
        assertTrue("Label not found in container data: " + cnt, cnt
                .indexOf("A label") > 0);
        assertTrue("Bean in container not found", getBuilderContext()
                .containsBean("containerBean"));
        assertEquals("Wrong builder reference", builder, processingBuilder);
    }

    /**
     * Tests the buildWindow() method.
     */
    @Test
    public void testBuildWindow() throws Exception
    {
        initManagers();
        Window window = builder.buildWindow(SCRIPT_LOCATOR, this);
        checkResults();
        assertEquals("Wrong window title", "A window", window.getTitle());
        WindowImpl wnd = (WindowImpl) window;
        assertNotNull("Window has not menu bar", wnd.getMenuBar());
        String cnt = wnd.getRootContainer().toString();
        assertTrue("Label text not found: " + cnt, cnt.indexOf("A label") > 0);
        assertNotNull("No icon found", wnd.getIcon());
        assertEquals("Wrong builder reference", builder, processingBuilder);
    }

    /**
     * Tests if call back objects are correctly handled.
     */
    @Test
    public void testBuildWithCallback() throws Exception
    {
        testBuild();
        String cnt = getBuilderContext().getBean(BuilderData.KEY_RESULT_WINDOW)
                .toString();
        assertTrue("Label was not linked: " + cnt, cnt.indexOf("<linked>") > 0);
    }

    /**
     * Tests the build() method when a null locator is specified.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithNullLocator() throws BuilderException
    {
        initManagers();
        builder.build(null, this);
    }

    /**
     * Tests the build method when the parameter object is null.
     */
    @Test(expected = BuilderException.class)
    public void testBuildWithNullData() throws BuilderException
    {
        initManagers();
        builder.build(FileLocator.getInstance(SCRIPT_FILE), null);
    }

    /**
     * Tests calling the builder when a Jelly exception is thrown.
     */
    @Test
    public void testBuildWithJellyException() throws Exception
    {
        initManagers();
        try
        {
            // should cause a file not found exception
            builder.build(FileLocator.getInstance(SCRIPT_FILE), this);
            fail("Jelly exception was not thrown!");
        }
        catch (BuilderException bex)
        {
            assertEquals("Wrong script URL", bex.getScriptURL(), SCRIPT_FILE
                    .toURI().toURL());
        }
    }

    /**
     * Tests the bean builder operation.
     */
    @Test
    public void testBuildBeans() throws BuilderException
    {
        initManagers();
        DefaultBeanStore rootStore = new DefaultBeanStore();
        builder.build(SCRIPT_LOCATOR, rootStore, null);
        assertNotNull("Root bean not found", rootStore
                .getBeanProvider("rootBean"));
        assertNotNull("Container bean not found", rootStore
                .getBeanProvider("containerBean"));
    }

    /**
     * Tests whether the class loader provider is correctly set.
     */
    @Test
    public void testBuildWithClassLoaderProvider() throws BuilderException
    {
        ClassLoaderProvider clp = new DefaultClassLoaderProvider();
        parentBeanContext.setClassLoaderProvider(clp);
        executeBuild();
        assertSame("Class loader provider not set", clp,
                builder.classLoaderProvider);
    }

    /**
     * Tests whether a passed in invocation helper is correctly installed.
     */
    @Test
    public void testBuildWithInvocationHelper() throws BuilderException
    {
        invocationHelper = new InvocationHelper();
        executeBuild();
        assertSame("Invocation helper not set", invocationHelper,
                builder.invocationHelper);
    }

    /**
     * Tests whether a correct default invocation helper is set.
     */
    @Test
    public void testBuildWithDefaultInvocationHelper() throws BuilderException
    {
        ConversionHelper ch = new ConversionHelper();
        ((DefaultBeanStore) parentBeanContext.getDefaultBeanStore())
                .setConversionHelper(ch);
        executeBuild();
        ConversionHelper currentConvHelper =
                builder.invocationHelper.getConversionHelper();
        assertSame("Parent conversion helper not set", ch,
                currentConvHelper.getParent());
        assertSame("Conversion helper not installed in bean store",
                currentConvHelper, getRootStore().getConversionHelper());
    }

    /**
     * Tests whether default converters are automatically registered if a
     * default invocation helper is created.
     */
    @Test
    public void testBuildWithDefaultInvocationHelperDefaultConverters()
            throws BuilderException
    {
        builder.addDefaultBaseClassConverter(new LocatorConverter(),
                Locator.class);
        executeBuild();
        ConversionHelper currentConvHelper =
                builder.invocationHelper.getConversionHelper();
        assertNotNull("Conversion failed", currentConvHelper.convert(
                Locator.class, "classpath:test.resource"));
    }

    /**
     * Tests whether a default converter can be added to the builder.
     */
    @Test
    public void testAddDefaultConverter()
    {
        Converter conv = EasyMock.createMock(Converter.class);
        EasyMock.replay(conv);
        builder.addDefaultConverter(conv, getClass());
        Map<Class<?>, Converter> convs = builder.getDefaultConverters();
        assertEquals("Wrong number of default converters", 1, convs.size());
        assertSame("Wrong converter", conv, convs.get(getClass()));
        EasyMock.verify(conv);
    }

    /**
     * Tries to add a default converter for a null class.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDefaultConverterNullClass()
    {
        builder.addDefaultConverter(EasyMock.createNiceMock(Converter.class),
                null);
    }

    /**
     * Tries to add a default null converter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDefaultConverterNullConverter()
    {
        builder.addDefaultConverter(null, getClass());
    }

    /**
     * Tests whether a map with default converters can be added.
     */
    @Test
    public void testAddDefaultConverters()
    {
        Map<Class<?>, Converter> map = createConvertersMap();
        builder.addDefaultConverters(map);
        Map<Class<?>, Converter> defConvs = builder.getDefaultConverters();
        assertNotSame("Got same map", map, defConvs);
        equalsOrderedMap(map, defConvs);
    }

    /**
     * Tries to add a null map with converters.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDefaultConvertersNull()
    {
        builder.addDefaultConverters(null);
    }

    /**
     * Tries to add a map with default converters containing a null entry.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDefaultConvertersNullEntry()
    {
        Map<Class<?>, Converter> map = createConvertersMap();
        map.put(getClass(), null);
        builder.addDefaultConverters(map);
    }

    /**
     * Tests whether the default converters can be cleared.
     */
    @Test
    public void testClearDefaultConverters()
    {
        builder.addDefaultConverters(createConvertersMap());
        builder.clearDefaultConverters();
        assertTrue("Still got default converters", builder
                .getDefaultConverters().isEmpty());
    }

    /**
     * Tests whether the map with default converters cannot be modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetDefaultConvertersModify()
    {
        builder.addDefaultConverters(createConvertersMap());
        Iterator<Class<?>> it =
                builder.getDefaultConverters().keySet().iterator();
        it.next();
        it.remove();
    }

    /**
     * Tests whether a default base class converter can be added to the builder.
     */
    @Test
    public void testAddDefaultBaseClassConverter()
    {
        Converter conv = EasyMock.createMock(Converter.class);
        EasyMock.replay(conv);
        builder.addDefaultBaseClassConverter(conv, getClass());
        Map<Class<?>, Converter> convs =
                builder.getDefaultBaseClassConverters();
        assertEquals("Wrong number of default base class converters", 1,
                convs.size());
        assertSame("Wrong converter", conv, convs.get(getClass()));
        EasyMock.verify(conv);
    }

    /**
     * Tries to add a default base class converter for a null class.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDefaultBaseClassConverterNullClass()
    {
        builder.addDefaultBaseClassConverter(
                EasyMock.createNiceMock(Converter.class), null);
    }

    /**
     * Tries to add a default null base class converter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDefaultBaseClassConverterNullConverter()
    {
        builder.addDefaultBaseClassConverter(null, getClass());
    }

    /**
     * Tests whether a map with default base class converters can be added.
     */
    @Test
    public void testAddDefaultBaseClassConverters()
    {
        Map<Class<?>, Converter> map = createConvertersMap();
        builder.addDefaultBaseClassConverters(map);
        Map<Class<?>, Converter> defConvs =
                builder.getDefaultBaseClassConverters();
        assertNotSame("Got same map", map, defConvs);
        equalsOrderedMap(map, defConvs);
    }

    /**
     * Tries to add a null map with base class converters.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDefaultBaseClassConvertersNull()
    {
        builder.addDefaultConverters(null);
    }

    /**
     * Tries to add a map with default base class converters containing a null
     * entry.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDefaultBaseClassConvertersNullEntry()
    {
        Map<Class<?>, Converter> map = createConvertersMap();
        map.put(getClass(), null);
        builder.addDefaultBaseClassConverters(map);
    }

    /**
     * Tests whether a map with default converter class names can be added.
     */
    @Test
    public void testAddDefaultBaseClassConvertersCLP()
    {
        ClassLoaderProvider clp =
                EasyMock.createMock(ClassLoaderProvider.class);
        for (Class<?> cls : CONVERTER_CLASSES)
        {
            clp.loadClass(cls.getName(), null);
            EasyMock.expectLastCall().andReturn(cls);
        }
        EasyMock.replay(clp);
        builder.addDefaultBaseClassConverters(createConverterNamesMap(), clp);
        assertEquals("Wrong converter classes", createConvertersMap().keySet(),
                builder.getDefaultBaseClassConverters().keySet());
        EasyMock.verify(clp);
    }

    /**
     * Tries to add a map with converter class names which is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDefaultBaseClassConvertersCLPNullMap()
    {
        builder.addDefaultBaseClassConverters(null,
                EasyMock.createNiceMock(ClassLoaderProvider.class));
    }

    /**
     * Tries to add a map with converter class names without a class loader
     * provider.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDefaultBaseClassConvertersCLPNullCLP()
    {
        builder.addDefaultBaseClassConverters(createConverterNamesMap(), null);
    }

    /**
     * Tests whether the default base class converters can be cleared.
     */
    @Test
    public void testClearDefaultBaseClassConverters()
    {
        builder.addDefaultBaseClassConverters(createConvertersMap());
        builder.clearDefaultBaseClassConverters();
        assertTrue("Still got default converters", builder
                .getDefaultBaseClassConverters().isEmpty());
    }

    /**
     * Tests whether the map with default base class converters cannot be
     * modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetDefaultBaseClassConvertersModify()
    {
        builder.addDefaultBaseClassConverters(createConvertersMap());
        Iterator<Class<?>> it =
                builder.getDefaultBaseClassConverters().keySet().iterator();
        it.next();
        it.remove();
    }

    /**
     * Tests whether default converters can be added to a conversion helper.
     */
    @Test
    public void testRegisterDefaultConverters()
    {
        Converter cCol = EasyMock.createMock(Converter.class);
        Converter cList = EasyMock.createMock(Converter.class);
        Converter cLoc = EasyMock.createMock(Converter.class);
        Collection<?> col = EasyMock.createMock(Collection.class);
        List<?> list = EasyMock.createMock(List.class);
        Locator loc = EasyMock.createMock(Locator.class);
        final String sCol = "Collection";
        final String sList = "List";
        final String sLoc = "Locator";
        EasyMock.expect(cCol.convert(Collection.class, sCol)).andReturn(col);
        EasyMock.expect(cList.convert(List.class, sList)).andReturn(list);
        EasyMock.expect(cLoc.convert(Locator.class, sLoc)).andReturn(loc);
        EasyMock.replay(cCol, cList, cLoc, col, list, loc);
        builder.addDefaultBaseClassConverter(cCol, Collection.class);
        builder.addDefaultBaseClassConverter(cList, List.class);
        builder.addDefaultConverter(cLoc, Locator.class);
        ConversionHelper convHlp = new ConversionHelper();
        builder.registerDefaultConverters(convHlp);
        assertSame("Wrong collection", col,
                convHlp.convert(Collection.class, sCol));
        assertSame("Wrong list", list, convHlp.convert(List.class, sList));
        assertSame("Wrong locator", loc, convHlp.convert(Locator.class, sLoc));
        EasyMock.verify(cCol, cList, cLoc, col, list, loc);
    }

    /**
     * Tries to pass a null object to registerDefaultConverters().
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterDefaultConvertersNull()
    {
        builder.registerDefaultConverters(null);
    }

    /**
     * Creates a map with some mock converters.
     *
     * @return the map with the converters
     */
    private static Map<Class<?>, Converter> createConvertersMap()
    {
        Map<Class<?>, Converter> map = new LinkedHashMap<Class<?>, Converter>();
        for (Class<?> c : CONVERTER_CLASSES)
        {
            map.put(c, EasyMock.createNiceMock(Converter.class));
        }
        return map;
    }

    /**
     * Creates a map with mock converters and class names.
     *
     * @return the map with the converters
     */
    private static Map<String, Converter> createConverterNamesMap()
    {
        Map<String, Converter> map = new LinkedHashMap<String, Converter>();
        for (Class<?> c : CONVERTER_CLASSES)
        {
            map.put(c.getName(), EasyMock.createNiceMock(Converter.class));
        }
        return map;
    }

    /**
     * Helper method for comparing two maps. (We cannot use equals here as the
     * maps may be of different types.)
     *
     * @param <K> the type of the keys
     * @param <V> the type of the values
     * @param map1 the first map
     * @param map2 the second map
     */
    private static <K, V> void equalsOrderedMap(Map<K, V> map1, Map<K, V> map2)
    {
        assertEquals("Different sizes", map1.size(), map2.size());
        Iterator<Map.Entry<K, V>> it1 = map1.entrySet().iterator();
        Iterator<Map.Entry<K, V>> it2 = map2.entrySet().iterator();
        while (it1.hasNext())
        {
            Map.Entry<K, V> e1 = it1.next();
            Map.Entry<K, V> e2 = it2.next();
            assertEquals("Different keys", e1.getKey(), e2.getKey());
            assertEquals("Different values", e1.getValue(), e2.getValue());
        }
    }

    /**
     * Tests fetching the builder results when invoking the call backs causes an
     * exception.
     */
    @Test
    public void testFetchResultsCallBackException() throws FormBuilderException
    {
        ComponentBuilderCallBack mockCallBack = EasyMock
                .createMock(ComponentBuilderCallBack.class);
        ComponentBuilderData cd = new ComponentBuilderData();
        cd.initializeForm(TRANSFORMER_CONTEXT, BINDING_STRATEGY);
        JellyContext context = new JellyContext();
        cd.put(context);
        mockCallBack.callBack(cd, null);
        EasyMock.expectLastCall().andThrow(
                new FormBuilderException("Test error!"));
        EasyMock.replay(mockCallBack);
        cd.addCallBack(mockCallBack, null);
        try
        {
            builder.fetchResults(context, this, null, SCRIPT_LOCATOR);
            fail("Exception in call back not detected!");
        }
        catch (BuilderException bex)
        {
            EasyMock.verify(mockCallBack);
        }
    }

    /**
     * Tests a bean definition with complex initialization.
     */
    @Test
    public void testBuildBeanWithInitialization() throws BuilderException
    {
        testBuild();
        JellyBuilderTestBean bean = (JellyBuilderTestBean) getBuilderContext()
                .getBean("testBean");
        assertNotNull("ComponentBuilderData not set", bean.getCompData());
        assertNotNull("Text component not set", bean.getTextField());
    }

    /**
     * Tests releasing a builder data object.
     */
    @Test
    public void testRelease()
    {
        checkRelease(false);
    }

    /**
     * Tests the release() method when the name of the root store is explicitly
     * contained in the collection with store names.
     */
    @Test
    public void testReleaseRootStoreNameContained()
    {
        checkRelease(true);
    }

    /**
     * Helper method for testing the release() implementation.
     *
     * @param withRootNameContained a flag whether the name of the root store is
     *        contained in the set with the store names
     */
    private void checkRelease(boolean withRootNameContained)
    {
        final int storeCount = 5;
        final int providerCount = 8;
        BeanBuilderResult builderResult = EasyMock
                .createMock(BeanBuilderResult.class);
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        List<BeanProvider> providers = new ArrayList<BeanProvider>();
        List<BeanStore> stores = new ArrayList<BeanStore>();
        Set<String> storeNames = new HashSet<String>();
        for (int i = 1; i <= storeCount; i++)
        {
            String storeName = "beanStore" + i;
            storeNames.add(storeName);
            BeanStore store = prepareBeanStoreMock(storeName + "_", i
                    * providerCount, depProvider, providers);
            EasyMock.expect(builderResult.getBeanStore(storeName)).andReturn(
                    store);
            stores.add(store);
        }
        BeanStore root = prepareBeanStoreMock("rootProvider_", 11, depProvider,
                providers);
        stores.add(root);
        EasyMock.expect(builderResult.getBeanStore(null)).andReturn(root);
        if (withRootNameContained)
        {
            storeNames.add(null);
        }
        EasyMock.expect(builderResult.getBeanStoreNames())
                .andReturn(storeNames);
        BeanContext builderContext = EasyMock.createMock(BeanContext.class);
        builderContext.close();
        EasyMock.replay(builderContext, builderResult);
        EasyMock.replay(stores.toArray());
        EasyMock.replay(providers.toArray());
        setBeanBuilderResult(builderResult);
        setBuilderContext(builderContext);
        builder.releaseDepProvider = depProvider;
        builder.release(this);
        EasyMock.verify(builderContext, builderResult);
        EasyMock.verify(stores.toArray());
        EasyMock.verify(providers.toArray());
    }

    /**
     * Creates a mock for a bean store that contains the specified number of
     * bean providers.
     *
     * @param providerPrefix the prefix for the names of the providers
     * @param count the number of bean providers
     * @param depProvider the dependency provider
     * @param providers a list where the new provider mocks are added
     * @return the bean store mock
     */
    private BeanStore prepareBeanStoreMock(String providerPrefix, int count,
            DependencyProvider depProvider, List<BeanProvider> providers)
    {
        BeanStore store = EasyMock.createMock(BeanStore.class);
        Set<String> providerNames = new HashSet<String>();
        for (int j = 1; j <= count; j++)
        {
            String providerName = providerPrefix + j;
            providerNames.add(providerName);
            BeanProvider provider = EasyMock.createMock(BeanProvider.class);
            EasyMock.expect(store.getBeanProvider(providerName)).andReturn(
                    provider);
            providers.add(provider);
            provider.shutdown(depProvider);
        }
        EasyMock.expect(store.providerNames()).andReturn(providerNames);
        return store;
    }

    /**
     * Tests releasing a null data object. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReleaseNullData()
    {
        builder.release((BuilderData) null);
    }

    /**
     * Tests releasing a data object that does not contain a builder result
     * object. This should cause an exception.
     */
    @Test
    public void testReleaseNoBuilderResult()
    {
        BeanContext ctx = EasyMock.createMock(BeanContext.class);
        EasyMock.replay(ctx);
        setBuilderContext(ctx);
        try
        {
            builder.release(this);
            fail("Could release data object with null builder result!");
        }
        catch (IllegalArgumentException iex)
        {
            EasyMock.verify(ctx);
        }
    }

    /**
     * Tests releasing a data object that does not contain a builder bean
     * context. This should cause an exception.
     */
    @Test
    public void testReleaseNoBuilderContext()
    {
        BeanBuilderResult result = EasyMock.createMock(BeanBuilderResult.class);
        EasyMock.replay(result);
        setBeanBuilderResult(result);
        try
        {
            builder.release(this);
            fail("Could release data object with null bean context!");
        }
        catch (IllegalArgumentException iex)
        {
            EasyMock.verify(result);
        }
    }

    /**
     * Tests whether an auto release listener is registered at the window when
     * auto release is enabled.
     */
    @Test
    public void testFetchResultsAutoRelease() throws BuilderException
    {
        initManagers();
        JellyContext context = builder.setUpExtendedJellyContext(this, null);
        WindowImpl window = new WindowImpl();
        WindowBuilderData wndData = WindowBuilderData.get(context);
        wndData.setResultWindow(window);
        autoRelease = true;
        processingBuilder = builder;
        builder.fetchResults(context, this, EasyMock
                .createNiceMock(BeanBuilderResult.class), SCRIPT_LOCATOR);
        assertEquals("Wrong number of window listeners", 1, window
                .getWindowListeners().size());
        assertTrue("No auto release listener", window.getWindowListeners()
                .iterator().next() instanceof AutoReleaseListener);
    }

    /**
     * Helper method for initializing the manager objects in the builder
     * instance.
     */
    private void initManagers()
    {
        builder.setComponentManager(new ComponentManagerImpl());
        builder.setActionManager(new ActionManagerImpl());
        builder.setWindowManager(new WindowManagerImpl());
    }

    /**
     * Checks if all result objects were returned.
     */
    private void checkResults()
    {
        BeanContext bc = getBuilderContext();
        assertNotNull("No builder context set", bc);
        assertTrue("No component data object found", bc
                .containsBean(ComponentBuilderData.KEY_COMPONENT_BUILDER_DATA));
        assertTrue("No form found", bc
                .containsBean(ComponentBuilderData.KEY_FORM));
        Object bean = bc.getBean("rootBean");
        assertTrue("Wrong bean instance: " + bean,
                bean instanceof ReflectionTestClass);
    }

    /**
     * A test implementation of the JellyBuilder class allowing access to some
     * internals. This is useful for testing.
     */
    private static class JellyBuilderTestImpl extends JellyBuilder
    {
        /** Stores the class loader provider used when executing a script. */
        private ClassLoaderProvider classLoaderProvider;

        /** Stores the invocation helper passed when executing a script. */
        private InvocationHelper invocationHelper;

        /**
         * The dependency provider to be returned by
         * createReleaseDependencyProvider().
         */
        private DependencyProvider releaseDepProvider;

        /** The bean context passed to registerBeanCreationListeners().*/
        BeanContext registerListenersContext;

        /** The builder data passed to registerBeanCreationListeners().*/
        BuilderData registerListenersBuilderData;

        /**
         * Executes the script. This implementation remembers the passed in
         * helper objects so that they can be checked later.
         */
        @Override
        protected BeanBuilderResult executeScript(Locator script,
                JellyContext context, MutableBeanStore rootStore,
                ClassLoaderProvider loaderProvider, InvocationHelper invHlp)
                throws BuilderException
        {
            classLoaderProvider = loaderProvider;
            invocationHelper = invHlp;
            return super.executeScript(script, context, rootStore,
                    loaderProvider, invHlp);
        }

        /**
         * Either returns the mock dependency provider or calls the super
         * method.
         */
        @Override
        protected DependencyProvider createReleaseDependencyProvider(
                BeanBuilderResult result)
        {
            return (releaseDepProvider != null) ? releaseDepProvider : super
                    .createReleaseDependencyProvider(result);
        }

        /**
         * Records this invocation.
         */
        @Override
        protected void registerBeanCreationListeners(BeanContext context,
                BuilderData data)
        {
            registerListenersContext = context;
            registerListenersBuilderData = data;
            super.registerBeanCreationListeners(context, data);
        }
    }

    /**
     * A simple bean class that will be instantiated by the test script. It is
     * used for testing advanced dependency injection features.
     */
    public static class JellyBuilderTestBean
    {
        /** Stores a reference to the component builder data object. */
        private ComponentBuilderData compData;

        /** Stores the text component. */
        private Object textField;

        public ComponentBuilderData getCompData()
        {
            return compData;
        }

        public void setCompData(ComponentBuilderData compData)
        {
            this.compData = compData;
        }

        public Object getTextField()
        {
            return textField;
        }

        public void setTextField(Object textField)
        {
            this.textField = textField;
        }
    }

    /**
     * A combined interface for a component manager and a form context listener.
     */
    private static interface ComponentManagerFormContextListener extends
            ComponentManager, FormContextListener
    {
    }
}

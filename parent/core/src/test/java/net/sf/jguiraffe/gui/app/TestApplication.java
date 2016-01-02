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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.MutableBeanStore;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.DefaultBeanContext;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.di.impl.DefaultClassLoaderProvider;
import net.sf.jguiraffe.gui.builder.BeanBuilder;
import net.sf.jguiraffe.gui.builder.BeanBuilderFactory;
import net.sf.jguiraffe.gui.builder.BeanBuilderResult;
import net.sf.jguiraffe.gui.builder.Builder;
import net.sf.jguiraffe.gui.builder.BuilderException;
import net.sf.jguiraffe.gui.builder.impl.JellyBeanBuilderFactory;
import net.sf.jguiraffe.gui.builder.utils.GUIRuntimeException;
import net.sf.jguiraffe.gui.builder.utils.GUISynchronizer;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowImpl;
import net.sf.jguiraffe.gui.cmd.CommandBase;
import net.sf.jguiraffe.gui.cmd.CommandQueue;
import net.sf.jguiraffe.gui.cmd.CommandQueueEvent;
import net.sf.jguiraffe.gui.cmd.CommandQueueImpl;
import net.sf.jguiraffe.gui.cmd.CommandQueueListener;
import net.sf.jguiraffe.locators.ClassPathLocator;
import net.sf.jguiraffe.locators.Locator;
import net.sf.jguiraffe.locators.URLLocator;
import net.sf.jguiraffe.resources.Message;
import net.sf.jguiraffe.resources.impl.ResourceManagerImpl;
import net.sf.jguiraffe.resources.impl.bundle.BundleResourceLoader;
import net.sf.jguiraffe.transform.DefaultValidationMessageHandler;
import net.sf.jguiraffe.transform.ValidationMessageHandler;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for application.
 *
 * @author Oliver Heger
 * @version $Id: TestApplication.java 211 2012-07-10 19:49:13Z oheger $
 */
public class TestApplication
{
    /** Constant for the name of the minimum configuration file. */
    private static final String CONFIG_MIN = "testappconfigfactorymin.xml";

    /** Constant for the name of the maximum configuration file. */
    private static final String CONFIG_MAX = "testappconfigfactorymax.xml";

    /** A locator for test platform-specific bean declarations. */
    private static final Locator PLATFORM_BEANS = ClassPathLocator
            .getInstance("testplatformbeans.jelly");

    /** Constant for the name of the sub dir that stores the user config. */
    private static final String USER_CONF_DIR = ".testapp";

    /** Constant for the name of the user config file. */
    private static final String USER_CONF = "testappconfig.xml";

    /** Constant for the resource group. */
    private static final String RES_GRP = "testresources";

    /** Constant for a test parameter. */
    private static final String TEST_PARAM = "/test";

    /** Constant for the prefix for the Jelly scripts. */
    private static final String SCRIPT_PREFIX = "/jelly_scripts/";

    /** Constant for the name of the test script with bean definitions. */
    private static final String SCRIPT = SCRIPT_PREFIX
            + "applicationbeans.jelly";

    /** Constant for the locator to the test script. */
    private static final Locator SCRIPT_LOCATOR = ClassPathLocator.getInstance(SCRIPT);

    /** The application object to test. */
    private ApplicationTestImpl app;

    /** The file with the user configuration. */
    private File usrConfFile;

    @Before
    public void setUp() throws Exception
    {
        app = new ApplicationTestImpl();
    }

    @After
    public void tearDown() throws Exception
    {
        removeUserConfig();
        if (app.getApplicationContext() != null
                && app.getApplicationContext().getMainWindow() != null)
        {
            app.getApplicationContext().getMainWindow().close(true);
        }
        app = null;
    }

    /**
     * Tests to obtain the configuration when the data is invalid.
     */
    @Test(expected = ApplicationRuntimeException.class)
    public void testFetchConfigURLInvalid()
    {
        app.setConfigURL("This is an invalid URL!");
        app.fetchConfigURL();
    }

    /**
     * Tests parsing an invalid locale.
     */
    @Test(expected = ApplicationRuntimeException.class)
    public void testParseLocaleInvalid()
    {
        HierarchicalConfiguration config = new HierarchicalConfiguration();
        config.addProperty(Application.PROP_LOCALE, "AnInvalidLocale_Yes");
        app.parseLocale(config);
    }

    /**
     * Tries to load a non existing configuration.
     */
    @Test(expected = ApplicationRuntimeException.class)
    public void testCreateConfigurationNonExisting() throws IOException
    {
        File f = new File("nonExistingConfig.xml");
        app.createConfiguration(f.toURI().toURL());
    }

    /**
     * Tests the default locator for platform-specific beans.
     */
    @Test
    public void testGetPlatformBeansLocatorDefault()
    {
        app.setOverrideGetPlatformBeansLocator(false);
        ClassPathLocator loc = (ClassPathLocator) app.getPlatformBeansLocator();
        assertEquals("Wrong default name", "platformbeans.jelly",
                loc.getResourceName());
    }

    /**
     * Tests to create the application context with a minimum configuration.
     */
    @Test
    public void testCreateAppCtx()
    {
        app.setConfigResourceName(CONFIG_MIN);
        ApplicationContext ctx = app.createApplicationContext();
        assertNotNull("No context has been created", ctx);
        assertEquals("Wrong implementation class",
                ApplicationContextImpl.class, ctx.getClass());
        assertNotNull("Configuration not set", ctx.getConfiguration());
        assertEquals("Wrong locale set", Locale.getDefault(), ctx.getLocale());
        assertNotNull("No bean context set", ctx.getBeanContext());
        assertTrue("Bean context not initialized", ctx.getBeanContext()
                .containsBean(Application.BEAN_APPLICATION));
    }

    /**
     * Tests the default resource manager when creating the application context
     * from a minimum configuration.
     */
    @Test
    public void testCreateAppCtxResManager()
    {
        app.setConfigResourceName(CONFIG_MIN);
        ApplicationContext ctx = app.createApplicationContext();
        assertNotNull("No resource manager set", ctx.getResourceManager());
        assertEquals("Wrong resource manager class", ResourceManagerImpl.class,
                ctx.getResourceManager().getClass());
        assertNotNull("No resource loader set", ctx.getResourceManager()
                .getResourceLoader());
        assertNull("Default resource group was set", ctx.getResourceManager()
                .getDefaultResourceGroup());
    }

    /**
     * Tests to create an application context from a configuration file that
     * overrides some of the default properties.
     */
    @Test
    public void testCreateAppCtxMax()
    {
        app.setConfigResourceName(CONFIG_MAX);
        ApplicationContext ctx = app.createApplicationContext();
        assertNotNull("No context created", ctx);
        Locale loc = ctx.getLocale();
        assertNotNull("No locale set", loc);
        assertEquals("Wrong country in locale", "US", loc.getCountry());
        assertEquals("Wrong language in locale", "en", loc.getLanguage());
    }

    /**
     * Tests whether default beans can be overridden by custom bean definitions.
     */
    @Test
    public void testCreateAppCtxOverride()
    {
        app.setConfigResourceName(CONFIG_MAX);
        ApplicationContext ctx = app.createApplicationContext();
        assertNotNull("No resource manager set", ctx.getResourceManager());
        assertEquals("Resource loader was not overridden",
                TestResourceLoader.class, ctx.getResourceManager()
                        .getResourceLoader().getClass());
        assertEquals("Default resource group not set", "testresources", ctx
                .getResourceManager().getDefaultResourceGroup());
    }

    /**
     * Tests creating the application context with an undefined configuration.
     * This should cause an exception.
     */
    @Test(expected = ApplicationRuntimeException.class)
    public void testCreateAppCtxUnexistingConfig()
    {
        app.setConfigResourceName("an unexisting configuration");
        app.createApplicationContext();
    }

    /**
     * Tests creation of the message output object.
     */
    @Test
    public void testCreateAppCtxMessageOutput() throws ApplicationException
    {
        app.setConfigResourceName(CONFIG_MIN);
        ApplicationContext ctx = app.createApplicationContext();
        MessageOutput mo = ctx.getMessageOutput();
        assertNotNull("No message output set", mo);
    }

    /**
     * Tests the creation of the validation message handler.
     */
    @Test
    public void testCreateAppCtxValidationMessageHandler()
    {
        app.setConfigResourceName(CONFIG_MIN);
        ApplicationContext ctx = app.createApplicationContext();
        ValidationMessageHandler handler = ctx.getValidationMessageHandler();
        assertNotNull("No validation message handler created", handler);
        DefaultValidationMessageHandler mh = (DefaultValidationMessageHandler) handler;
        assertEquals("Wrong default resource group",
                DefaultValidationMessageHandler.DEFAULT_RESOURCE_GROUP_NAME, mh
                        .getDefaultResourceGroup());
        assertNull("Alternative resource groups set", mh
                .getAlternativeResourceGroups());
    }

    /**
     * Tests whether the CLP is correctly initialized when creating the context.
     */
    @Test
    public void testCreateAppCtxInitCLP()
    {
        app.setConfigResourceName(CONFIG_MIN);
        ApplicationContext ctx = app.createApplicationContext();
        assertNotNull("initCLP() not called", app.clpInit);
        assertEquals("Wrong CLP for processBeanDefinitions()", app.clpInit,
                app.clpProcess);
        assertEquals("Wrong CLP for context", app.clpInit,
                ctx.getClassLoaderProvider());
        assertTrue("Wrong type of CLP",
                app.clpInit instanceof DefaultClassLoaderProvider);
        assertSame("Different CLP in bean context", app.clpInit, ctx
                .getBeanContext().getClassLoaderProvider());
    }

    /**
     * Tests whether initClassLoaderProvider() can override the CLP.
     */
    @Test
    public void testCreateAppCtxlOverrideCLP()
    {
        app.clpOverride = new DefaultClassLoaderProvider();
        app.setConfigResourceName(CONFIG_MIN);
        ApplicationContext ctx = app.createApplicationContext();
        assertEquals("Wrong CLP for processBeanDefinitions()", app.clpOverride,
                app.clpProcess);
        assertEquals("Wrong CLP for context", app.clpOverride, ctx
                .getClassLoaderProvider());
        assertSame("Different CLP in bean context", app.clpOverride, ctx
                .getBeanContext().getClassLoaderProvider());
    }

    /**
     * Tests creating the command queue.
     */
    @Test
    public void testCreateCommandQueue()
    {
        app.setConfigResourceName(CONFIG_MIN);
        app.setPlatformBeansLocator(PLATFORM_BEANS);
        ApplicationContext ctx = app.createApplicationContext();
        CommandQueue q = app.createCommandQueue(ctx);
        assertNotNull("No command queue created", q);
        GUISynchronizer sync = q.getGUISynchronizer();
        assertTrue("Wrong GUI synchronizer: " + sync,
                sync instanceof GUISynchronizerTestImpl);
    }

    /**
     * Tests obtaining the GUI synchronizer.
     */
    @Test
    public void testGetGUISynchronizer()
    {
        GUISynchronizer sync = EasyMock.createMock(GUISynchronizer.class);
        CommandQueue q = EasyMock.createMock(CommandQueue.class);
        EasyMock.expect(q.getGUISynchronizer()).andReturn(sync);
        EasyMock.replay(sync, q);
        app.setCommandQueue(q);
        assertEquals("Wrong GUI synchronizer", sync, app.getGUISynchronizer());
        EasyMock.verify(sync, q);
    }

    /**
     * Tests setting the GUI synchronizer.
     */
    @Test
    public void testSetGUISynchronizer()
    {
        GUISynchronizer sync = EasyMock.createMock(GUISynchronizer.class);
        CommandQueue q = EasyMock.createMock(CommandQueue.class);
        q.setGUISynchronizer(sync);
        EasyMock.replay(sync, q);
        app.setCommandQueue(q);
        app.setGUISynchronizer(sync);
        EasyMock.verify(q, sync);
    }

    /**
     * Tests setting the GUI synchronizer to null. This should not be allowed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetGUISynchronizerNull()
    {
        CommandQueueImpl q = new CommandQueueImpl(EasyMock
                .createNiceMock(GUISynchronizer.class));
        app.setCommandQueue(q);
        app.setGUISynchronizer(null);
    }

    /**
     * Tests setting the command queue to null. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCommandQueueNull()
    {
        app.setCommandQueue(null);
    }

    /**
     * Tests the run() method and some of the helper methods.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testRun() throws Exception
    {
        app.setConfigResourceName(CONFIG_MAX);
        app.mockInitGUI = true;
        app.run();
        checkRunningApp(app);
    }

    /**
     * Tests the startup() method.
     */
    @Test
    public void testStartup() throws Exception
    {
        String[] args =
        { "/hello", "-t", "Q:42", TEST_PARAM, "xyz.txt" };
        try
        {
            System.setProperty(Application.PROP_CONFIG_NAME, CONFIG_MAX);
            TestApp myapp = new TestApp();
            myapp.mockInitGUI = true;
            Application.startup(myapp, args);
            assertTrue("Parameter not found", myapp.isParamFound());
            checkRunningApp(myapp);
            assertTrue("Window not displayed", myapp.windowShown);
        }
        finally
        {
            System.clearProperty(Application.PROP_CONFIG_NAME);
        }
    }

    /**
     * Tests displaying the main window.
     */
    @Test
    public void testShowMainWindow()
    {
        Window window = EasyMock.createMock(Window.class);
        window.open();
        EasyMock.replay(window);
        app.mockShowWindow = false;
        app.showMainWindow(window);
        EasyMock.verify(window);
    }

    /**
     * Tests handling of the user configuration.
     */
    @Test
    public void testUserConfig() throws Exception
    {
        setUpUserConfig(false);
        app.setConfigResourceName(CONFIG_MAX);
        app.setApplicationContext(app.createApplicationContext());
        assertTrue("Wrong type of user configuration", app
                .getUserConfiguration() instanceof XMLConfiguration);
        WindowImpl window = new WindowImpl();
        window.setBounds(100, 101, 320, 200);
        app.getApplicationContext().setMainWindow(window);
        app.onShutdown();

        assertTrue(usrConfFile.exists());
        XMLConfiguration conf = new XMLConfiguration(usrConfFile);
        assertEquals(100, conf.getInt(Application.PROP_XPOS));
        assertEquals(101, conf.getInt(Application.PROP_YPOS));
        assertEquals(320, conf.getInt(Application.PROP_WIDTH));
        assertEquals(200, conf.getInt(Application.PROP_HEIGHT));
    }

    /**
     * Tests accessing the user configuration if it is not defined.
     */
    @Test(expected = ApplicationRuntimeException.class)
    public void testUserConfigNonExisting() throws ApplicationException
    {
        app.setConfigResourceName(CONFIG_MIN);
        app.setApplicationContext(app.createApplicationContext());
        app.getUserConfiguration();
    }

    /**
     * Tests if the user configuration is correctly loaded at startup.
     */
    @Test
    public void testUserConfigLoad() throws Exception
    {
        setUpUserConfig(true);
        PrintWriter out = null;
        // Create a user config file
        try
        {
            out = new PrintWriter(new FileWriter(usrConfFile));
            out.print("<config><user>true</user></config>");
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }

        app.setConfigResourceName(CONFIG_MAX);
        app.setApplicationContext(app.createApplicationContext());
        assertTrue("Key from user config not found", app.getUserConfiguration()
                .getBoolean("user"));
    }

    /**
     * Tests saving a non file-based user configuration. This should cause an
     * exception.
     */
    @Test(expected = ApplicationException.class)
    public void testSaveUserConfigNotFileBased() throws ApplicationException
    {
        CombinedConfiguration cc = new CombinedConfiguration();
        cc.addConfiguration(new HierarchicalConfiguration(),
                Application.USRCONF_NAME);
        ApplicationContextImpl ctx = new ApplicationContextImpl();
        ctx.setConfiguration(cc);
        app.setApplicationContext(ctx);
        app.saveUserConfiguration();
    }

    /**
     * Tests whether the shutdown listeners are called.
     */
    @Test
    public void testShutdownListeners() throws Exception
    {
        TestShutdownListener l = new TestShutdownListener();
        app.addShutdownListener(l);
        app.setConfigResourceName(CONFIG_MAX);
        app.mockInitGUI = true;
        app.run();
        app.shutdown("test1", "test1");
        assertTrue("canShutdown not called", l.isCanShutdownCalled());
        assertFalse("shutdown was called", l.isShutdownCalled());
    }

    /**
     * Tests the fireShutdown() method.
     */
    @Test
    public void testFireShutdown()
    {
        ApplicationShutdownListener mockListener = EasyMock
                .createMock(ApplicationShutdownListener.class);
        mockListener.shutdown(app);
        EasyMock.replay(mockListener);

        app.addShutdownListener(mockListener);
        app.fireShutdown();
        app.removeShutdownListener(mockListener);
        app.fireShutdown();
        EasyMock.verify(mockListener);
    }

    /**
     * Tests whether a correct root bean store is created.
     */
    @Test
    public void testCreateRootStore()
    {
        DependencyProvider mockProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(mockProvider);
        Configuration config = new PropertiesConfiguration();
        config.setProperty(Application.PROP_LOCALE, "en_us");
        config.setProperty(Application.PROP_DEFRESGROUP, "myResGroup");
        MutableBeanStore rootStore = app.createRootStore(config);
        BeanProvider p = rootStore
                .getBeanProvider(Application.BEAN_CONFIGURATION);
        assertNotNull("Configuration bean not found", p);
        assertSame("Wrong configuration bean", config, p.getBean(mockProvider));
        p = rootStore.getBeanProvider(Application.BEAN_APPLICATION);
        assertNotNull("Application bean not found", p);
        assertSame("Wrong application bean", app, p.getBean(mockProvider));
        p = rootStore.getBeanProvider(Application.BEAN_DEF_RES_GROUP);
        assertNotNull("No default resource group found", p);
        assertEquals("Wrong default resource group", "myResGroup", p
                .getBean(mockProvider));
        p = rootStore.getBeanProvider(Application.BEAN_LOCALE);
        assertNotNull("No locale found", p);
        assertEquals("Wrong locale", Locale.US, p.getBean(mockProvider));
        EasyMock.verify(mockProvider);
    }

    /**
     * Tests creating a bean builder factory if it is defined in the
     * configuration.
     */
    @Test
    public void testCreateBeanBuilderFactory()
    {
        Configuration config = new HierarchicalConfiguration();
        config
                .addProperty(Application.PROP_BEAN_BUILDER_FACTORY
                        + "[@config-class]", BeanBuilderFactoryTestImpl.class
                        .getName());
        config.addProperty(Application.PROP_BEAN_BUILDER_FACTORY
                + "[@diBuilderNameSpaceURI]", "myBeans");
        BeanBuilderFactoryTestImpl factory = (BeanBuilderFactoryTestImpl) app
                .createBeanBuilderFactory(config);
        assertEquals("Name space was not set", "myBeans", factory
                .getDiBuilderNameSpaceURI());
    }

    /**
     * Tests creating a bean factory if none is defined in the configuration.
     */
    @Test
    public void testCreateBeanBuilderFactoryDefault()
    {
        JellyBeanBuilderFactory factory = (JellyBeanBuilderFactory) app
                .createBeanBuilderFactory(new HierarchicalConfiguration());
        assertEquals("Wrong name space",
                JellyBeanBuilderFactory.NSURI_DI_BUILDER, factory
                        .getDiBuilderNameSpaceURI());
    }

    /**
     * Tests loading a set of bean definitions.
     */
    @Test
    public void testProcessBeanDefinitions()
    {
        app.setBeanBuilderFactory(new JellyBeanBuilderFactory());
        DefaultBeanStore rootStore = new DefaultBeanStore();
        DefaultBeanContext context = new DefaultBeanContext(rootStore);
        Collection<Locator> scripts = new ArrayList<Locator>();
        scripts.add(ClassPathLocator.getInstance(SCRIPT_PREFIX + "di.jelly"));
        scripts.add(SCRIPT_LOCATOR);
        app.processBeanDefinitions(scripts, context, null);
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean("rootBean");
        assertEquals("Wrong property value", "test", bean.getStringProp());
        BeanStore store = context.getDefaultBeanStore().getParent();
        assertNotNull("Bean not found in parent context", store
                .getBeanProvider("rootBean"));
        assertSame("Wrong hierarchy of stores", rootStore, store.getParent());
    }

    /**
     * Tests processing bean definitions if there are none. This should be a
     * noop.
     */
    @Test
    public void testProcessBeanDefinitionsNull()
    {
        BeanBuilderFactory mockFactory = EasyMock
                .createMock(BeanBuilderFactory.class);
        EasyMock.replay(mockFactory);
        app.setBeanBuilderFactory(mockFactory);
        DefaultBeanContext context = new DefaultBeanContext();
        app.processBeanDefinitions(null, context, null);
        assertNull("Default bean store was changed", context
                .getDefaultBeanStore());
        EasyMock.verify(mockFactory);
    }

    /**
     * Tests processing a script when an exception is thrown.
     */
    @Test(expected = ApplicationRuntimeException.class)
    public void testProcessBeanDefinitionEx() throws BuilderException
    {
        BeanBuilderFactory mockFactory = EasyMock
                .createMock(BeanBuilderFactory.class);
        BuilderException bex = new BuilderException("Error!");
        EasyMock.expect(mockFactory.getBeanBuilder()).andThrow(bex);
        EasyMock.replay(mockFactory);
        app.setBeanBuilderFactory(mockFactory);
        app.processBeanDefinition(SCRIPT_LOCATOR, new DefaultBeanContext(), null);
    }

    /**
     * Tests reading the builder scripts from the configuration.
     */
    @Test
    public void testFindBeanDefinitions()
    {
        BeanContext bctx = EasyMock.createMock(BeanContext.class);
        EasyMock.replay(bctx);
        Configuration config = new HierarchicalConfiguration();
        final String[] scripts =
        { SCRIPT, "anotherScript", "moreScripts" };
        config.addProperty(Application.PROP_BEAN_DEFS, scripts);
        app.setPlatformBeansLocator(null);
        Collection<Locator> locs = app.findBeanDefinitions(config, bctx);
        assertEquals("Wrong number of scripts", scripts.length, locs.size());
        int idx = 0;
        for (Locator loc : locs)
        {
            ClassPathLocator cpLoc = (ClassPathLocator) loc;
            assertEquals("Wrong resource name at " + idx, scripts[idx++],
                    cpLoc.getResourceName());
            assertEquals("Wrong class loader", app.getClass().getClassLoader(),
                    cpLoc.getClassLoader());
        }
    }

    /**
     * Tests whether the LocatorConverter is used to convert textual locator
     * representations.
     */
    @Test
    public void testFindBeanDefinitionsConvert()
    {
        ClassLoader cl = EasyMock.createMock(ClassLoader.class);
        BeanContext bctx = EasyMock.createMock(BeanContext.class);
        DefaultClassLoaderProvider clp = new DefaultClassLoaderProvider();
        EasyMock.expect(bctx.getBean(ClassLoaderProvider.class)).andReturn(clp);
        EasyMock.replay(bctx, cl);
        Configuration config = new HierarchicalConfiguration();
        final String resource = "test.properties";
        final String loader = "MySpecialClassLoader";
        final String url = "http://sf.jguiraffe.net";
        clp.registerClassLoader(loader, cl);
        config.addProperty(Application.PROP_BEAN_DEFS, "classpath:" + resource
                + ";" + loader);
        config.addProperty(Application.PROP_BEAN_DEFS, "url:" + url);
        app.setPlatformBeansLocator(null);
        Collection<Locator> locs = app.findBeanDefinitions(config, bctx);
        assertEquals("Wrong number of scripts", 2, locs.size());
        Iterator<Locator> it = locs.iterator();
        ClassPathLocator loc1 = (ClassPathLocator) it.next();
        assertEquals("Wrong resource", resource, loc1.getResourceName());
        assertSame("Wrong class loader", cl, loc1.getClassLoader());
        URLLocator loc2 = (URLLocator) it.next();
        assertEquals("Wrong URL", url, loc2.getURL().toExternalForm());
        EasyMock.verify(bctx);
    }

    /**
     * Test findBeanDefinitions() if no additional bean definition exist.
     */
    @Test
    public void testFindBeanDefinitionsNull()
    {
        BeanContext bctx = EasyMock.createMock(BeanContext.class);
        EasyMock.replay(bctx);
        app.setPlatformBeansLocator(null);
        assertTrue("Got bean definitions",
                app.findBeanDefinitions(new HierarchicalConfiguration(), bctx)
                        .isEmpty());
    }

    /**
     * Tests obtaining a locator for the main builder script.
     */
    @Test
    public void testLocatorForMainScript()
    {
        Configuration config = new HierarchicalConfiguration();
        config.addProperty(Application.PROP_BUILDER_MAIN_SCRIPT, SCRIPT);
        ClassPathLocator loc = (ClassPathLocator) app
                .locatorForMainScript(config);
        assertEquals("Wrong resource name", SCRIPT, loc.getResourceName());
        assertEquals("Wrong class loader", app.getClass().getClassLoader(),
                loc.getClassLoader());
    }

    /**
     * Tests obtaining the locator for the main builder script when none was
     * set.
     */
    @Test
    public void testLocatorForMainScriptUndefined()
    {
        assertNull("Found a locator", app
                .locatorForMainScript(new HierarchicalConfiguration()));
    }

    /**
     * Tests the initMainWindow() method if no position data is defined in the
     * configuration.
     */
    @Test
    public void testInitMainWindowNoData()
    {
        Window mockWindow = EasyMock.createMock(Window.class);
        EasyMock.replay(mockWindow);
        app.initMainWindow(mockWindow, new HierarchicalConfiguration());
        EasyMock.verify(mockWindow);
    }

    /**
     * Tests initializing the main window with the position information found in
     * the configuration.
     */
    @Test
    public void testInitMainWindow()
    {
        Window mockWindow = EasyMock.createMock(Window.class);
        Configuration config = prepareInitWindowTest(mockWindow);
        EasyMock.replay(mockWindow);
        app.initMainWindow(mockWindow, config);
        EasyMock.verify(mockWindow);
    }

    /**
     * Initializes a mock window object for a test of the initWindow() method. A
     * configuration is created that defines some window properties. The mock is
     * initialized to expect the corresponding calls.
     *
     * @param mockWindow the mock to be initialized
     * @return the configuration with the properties
     */
    private Configuration prepareInitWindowTest(Window mockWindow)
    {
        final int x = 25;
        final int y = 40;
        final int width = 320;
        final int height = 222;
        Configuration config = new HierarchicalConfiguration();
        config.addProperty(Application.PROP_XPOS, x);
        config.addProperty(Application.PROP_YPOS, y);
        config.addProperty(Application.PROP_WIDTH, width);
        config.addProperty(Application.PROP_HEIGHT, height);
        EasyMock.expect(mockWindow.getXPos()).andReturn(0);
        EasyMock.expect(mockWindow.getYPos()).andReturn(0);
        EasyMock.expect(mockWindow.getWidth()).andReturn(100);
        EasyMock.expect(mockWindow.getHeight()).andReturn(50);
        mockWindow.setBounds(x, y, width, height);
        return config;
    }

    /**
     * Tests initialization of the main GUI when a script is defined.
     */
    @Test
    public void testInitGUI() throws BuilderException
    {
        ApplicationContext mockCtx = EasyMock
                .createMock(ApplicationContext.class);
        Window mockWindow = EasyMock.createMock(Window.class);
        Builder mockBuilder = EasyMock.createMock(Builder.class);
        ApplicationBuilderData mockData = new ApplicationBuilderData();
        Configuration config = prepareInitWindowTest(mockWindow);
        config.addProperty(Application.PROP_BUILDER_MAIN_SCRIPT, SCRIPT);
        EasyMock.expect(mockCtx.getConfiguration()).andReturn(config);
        EasyMock.expect(mockCtx.newBuilder()).andReturn(mockBuilder);
        EasyMock.expect(mockCtx.initBuilderData()).andReturn(mockData);
        EasyMock.expect(
                mockBuilder.buildWindow((Locator) EasyMock.anyObject(),
                        EasyMock.same(mockData))).andReturn(mockWindow);
        mockCtx.setMainWindow(mockWindow);
        EasyMock.replay(mockCtx, mockWindow, mockBuilder);
        app.initGUI(mockCtx);
        EasyMock.verify(mockCtx, mockWindow, mockBuilder);
    }

    /**
     * Tests initializing the GUI when the builder does not return a main
     * window.
     */
    @Test
    public void testInitGUINoMainWindow() throws BuilderException
    {
        ApplicationContext mockCtx = EasyMock
                .createMock(ApplicationContext.class);
        Builder mockBuilder = EasyMock.createMock(Builder.class);
        ApplicationBuilderData mockData = new ApplicationBuilderData();
        Configuration config = new HierarchicalConfiguration();
        config.addProperty(Application.PROP_BUILDER_MAIN_SCRIPT, SCRIPT);
        EasyMock.expect(mockCtx.getConfiguration()).andReturn(config);
        EasyMock.expect(mockCtx.newBuilder()).andReturn(mockBuilder);
        EasyMock.expect(mockCtx.initBuilderData()).andReturn(mockData);
        EasyMock.expect(
                mockBuilder.buildWindow((Locator) EasyMock.anyObject(),
                        EasyMock.same(mockData))).andReturn(null);
        EasyMock.replay(mockCtx, mockBuilder);
        app.initGUI(mockCtx);
        EasyMock.verify(mockCtx, mockBuilder);
    }

    /**
     * Tests the initGUI() method when the builder throws an exception.
     */
    @Test
    public void testInitGUIBuilderException() throws BuilderException
    {
        ApplicationContext mockCtx = EasyMock
                .createMock(ApplicationContext.class);
        Builder mockBuilder = EasyMock.createMock(Builder.class);
        ApplicationBuilderData mockData = new ApplicationBuilderData();
        Configuration config = new HierarchicalConfiguration();
        config.addProperty(Application.PROP_BUILDER_MAIN_SCRIPT, SCRIPT);
        EasyMock.expect(mockCtx.getConfiguration()).andReturn(config);
        EasyMock.expect(mockCtx.newBuilder()).andReturn(mockBuilder);
        EasyMock.expect(mockCtx.initBuilderData()).andReturn(mockData);
        EasyMock.expect(
                mockBuilder.buildWindow((Locator) EasyMock.anyObject(),
                        EasyMock.same(mockData))).andThrow(
                new BuilderException("TestError!"));
        EasyMock.replay(mockCtx, mockBuilder);
        try
        {
            app.initGUI(mockCtx);
        }
        catch (ApplicationRuntimeException arex)
        {
            EasyMock.verify(mockCtx, mockBuilder);
        }
    }

    /**
     * Tests the initGUI() method when no main build script is defined.
     */
    @Test
    public void testInitGUIWithoutScript()
    {
        ApplicationContext mockCtx = EasyMock
                .createMock(ApplicationContext.class);
        Configuration config = new HierarchicalConfiguration();
        EasyMock.expect(mockCtx.getConfiguration()).andReturn(config);
        EasyMock.replay(mockCtx);
        app.initGUI(mockCtx);
        EasyMock.verify(mockCtx);
    }

    /**
     * Tests that builder results that need to be released are stored
     * internally.
     */
    @Test
    public void testGetInitializedBuilderResults()
    {
        app.setConfigResourceName(CONFIG_MAX);
        app.createApplicationContext();
        assertEquals("Wrong number of builder results", 3, app
                .getIninitializedBuilderResults().size());
    }

    /**
     * Creates a mock for a GUISynchronizer object.
     *
     * @return the synchronizer object
     */
    private static GUISynchronizer createSynchronizer()
    {
        GUISynchronizer sync = EasyMock.createNiceMock(GUISynchronizer.class);
        EasyMock.replay(sync);
        return sync;
    }

    /**
     * Test an aborted shutdown operation.
     */
    @Test
    public void testShutdownQuit() throws Exception
    {
        app.setConfigResourceName(CONFIG_MAX);
        app.setApplicationContext(app.createApplicationContext());
        app.setCommandQueue(new CommandQueueImpl(createSynchronizer())
        {
            @Override
            public synchronized boolean isPending()
            {
                return true;
            }
        });
        MessageOutput mockOut = EasyMock.createMock(MessageOutput.class);
        EasyMock.expect(
                mockOut.show(app.getApplicationContext().getMainWindow(), "OK",
                        "Hello", MessageOutput.MESSAGE_QUESTION,
                        MessageOutput.BTN_YES_NO)).andReturn(
                MessageOutput.RET_NO);
        EasyMock.replay(mockOut);
        app.getApplicationContext().setMessageOutput(mockOut);

        app.shutdown(new Message(RES_GRP, "test2"), new Message(RES_GRP,
                "test1"));
        EasyMock.verify(mockOut);
    }

    /**
     * Prepares a test of the shutdown operation. Initializes required mock
     * objects.
     *
     * @param checkPending a flag whether the queue should be checked for
     *        pending tasks
     */
    private void prepareShutdownTest(boolean checkPending)
            throws BuilderException
    {
        CommandQueue queue = EasyMock.createMock(CommandQueue.class);
        BeanBuilderFactory factory = EasyMock
                .createMock(BeanBuilderFactory.class);
        BeanBuilder builder = EasyMock.createMock(BeanBuilder.class);
        if (checkPending)
        {
            EasyMock.expect(queue.isPending()).andReturn(Boolean.FALSE);
        }
        queue.shutdown(true);
        app.setConfigResourceName(CONFIG_MAX);
        app.setApplicationContext(app.createApplicationContext());
        EasyMock.expect(factory.getBeanBuilder()).andStubReturn(builder);
        for (BeanBuilderResult result : app.getIninitializedBuilderResults())
        {
            builder.release(result);
        }
        EasyMock.replay(queue, factory, builder);
        app.setCommandQueue(queue);
        app.mockFactory = factory;
        app.setExitHandler(new TestExitHandler(app));
    }

    /**
     * Verifies all mocks involved in a test of a shutdown operation.
     */
    private void verifyShutdownTest() throws BuilderException
    {
        TestExitHandler eh = (TestExitHandler) app.getExitHandler();
        assertEquals("Wrong exit code set", 0, eh.getExitCode());
        BeanBuilder builder = app.mockFactory.getBeanBuilder();
        EasyMock.verify(app.getCommandQueue(), app.mockFactory, builder);
    }

    /**
     * Tests a complete shutdown() operation.
     */
    @Test
    public void testShutdown() throws BuilderException
    {
        prepareShutdownTest(true);
        app.shutdown(new Message(RES_GRP, "test2"), new Message(RES_GRP,
                "test1"));
        verifyShutdownTest();
    }

    /**
     * Tests a shutdown() operation if background tasks are to be ignored.
     */
    @Test
    public void testShutdownForced() throws BuilderException
    {
        prepareShutdownTest(false);
        app.shutdown();
        verifyShutdownTest();
    }

    /**
     * Tests a shutdown operation when the bean builder factory throws an
     * exception. This exception should be ignored.
     */
    @Test
    public void testShutdownBuilderFactoryEx() throws BuilderException
    {
        BeanBuilderFactory factory = EasyMock
                .createMock(BeanBuilderFactory.class);
        app.setConfigResourceName(CONFIG_MAX);
        app.setApplicationContext(app.createApplicationContext());
        EasyMock.expect(factory.getBeanBuilder()).andThrow(
                new BuilderException("Test exception!"));
        EasyMock.replay(factory);
        app.setCommandQueue(new CommandQueueImpl(createSynchronizer()));
        app.mockFactory = factory;
        TestExitHandler eh = new TestExitHandler(app);
        app.setExitHandler(eh);
        app.shutdown(new Message(RES_GRP, "test2"), new Message(RES_GRP,
                "test1"));
        assertEquals("exitApplication() not called", 0, eh.getExitCode());
        EasyMock.verify(factory);
    }

    /**
     * Tests whether there is a default exit handler.
     */
    @Test
    public void testDefaultExitHandler()
    {
        Runnable defHandler = app.getExitHandler();
        assertNotNull("No default exit handler", defHandler);
        app.setExitHandler(new TestExitHandler(app));
        app.setExitHandler(null);
        assertSame("Different exit handler", defHandler, app.getExitHandler());
    }

    /**
     * Tests obtaining the Application instance from a bean context.
     */
    @Test
    public void testGetInstance()
    {
        BeanContext context = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(context.getBean(Application.BEAN_APPLICATION))
                .andReturn(app);
        EasyMock.replay(context);
        assertSame("Wrong application instance", app, Application
                .getInstance(context));
        EasyMock.verify(context);
    }

    /**
     * Tests obtaining the application instance if it cannot be found in the
     * context.
     */
    @Test(expected = InjectionException.class)
    public void testGetInstanceUnknown()
    {
        Application.getInstance(new DefaultBeanContext());
    }

    /**
     * Tests the getInstance() method when a null context is passed in.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceNullCtx()
    {
        Application.getInstance(null);
    }

    /**
     * Tests whether the class loader provider is correctly initialized.
     */
    @Test
    public void testInitClassLoaderProvider()
    {
        ClassLoaderProvider clp =
                EasyMock.createMock(ClassLoaderProvider.class);
        clp.registerClassLoader(Application.CLASS_LOADER, app.getClass()
                .getClassLoader());
        clp.setDefaultClassLoaderName(Application.CLASS_LOADER);
        EasyMock.replay(clp);
        app.initClassLoaderProvider(clp);
        EasyMock.verify(clp);
    }

    /**
     * Helper method for testing a running application.
     *
     * @param app the application to be tested
     * @throws Exception if an error occurs
     */
    protected void checkRunningApp(ApplicationTestImpl app) throws Exception
    {
        ApplicationContext appCtx = app.getApplicationContext();
        assertNotNull("No application context set", appCtx);
        assertNotNull("No resource manager set", appCtx.getResourceManager());
        assertEquals("Cannot resolve resource", "Hello", appCtx.getResource(
                RES_GRP, "test1"));

        assertEquals("UI not initialized", 1, app.initGUICount);
        assertNotNull("No command queue set", app.getCommandQueue());
        TestQueueListener l = new TestQueueListener();
        app.getCommandQueue().addQueueListener(l);
        TestCommand cmd = new TestCommand();
        app.execute(cmd);
        l.waitForIdleEvent();
        assertTrue("No queue event sent (1)", l.isIdleEventReceived());
        assertTrue("Command not executed (1)", cmd.isExecuted());
        l.setIdleEventReceived(false);
        TestCommandApp cmdApp = new TestCommandApp();
        app.execute(cmdApp);
        l.waitForIdleEvent();
        assertTrue("No queue event sent (2)", l.isIdleEventReceived());
        assertTrue("Command not executed (2)", cmdApp.isExecuted());
        assertSame("Application not set in command", app, cmdApp
                .getApplication());
    }

    /**
     * Helper method for removing the user configuration file.
     */
    protected void removeUserConfig()
    {
        if (usrConfFile != null && usrConfFile.exists())
        {
            assertTrue("User config file could not be removed", usrConfFile
                    .delete());
            File parent = usrConfFile.getParentFile();
            assertTrue("User config dir could not be removed", parent.delete());
        }
    }

    /**
     * Initializes the user config file.
     *
     * @param mkdir a flag if the configuration directory is to be created
     */
    private void setUpUserConfig(boolean mkdir)
    {
        File usrDir = new File(System.getProperty("user.home"));
        File usrConfDir = new File(usrDir, USER_CONF_DIR);
        usrConfFile = new File(usrConfDir, USER_CONF);
        if (mkdir && !usrConfDir.exists())
        {
            assertTrue("Cannot create user config dir " + usrConfDir,
                    usrConfDir.mkdir());
        }
    }

    /**
     * Test resource loader class to be created.
     */
    public static class TestResourceLoader extends BundleResourceLoader
    {
    }

    /**
     * Test command class for testing the execute method.
     */
    static class TestCommand extends CommandBase
    {
        private boolean executed;

        public void execute() throws Exception
        {
            executed = true;
        }

        public boolean isExecuted()
        {
            return executed;
        }
    }

    /**
     * A test command class that also implements the ApplicationClient
     * interface.
     */
    static class TestCommandApp extends TestCommand implements
            ApplicationClient
    {
        private Application application;

        public void setApplication(Application app)
        {
            application = app;
        }

        public Application getApplication()
        {
            return application;
        }
    }

    /**
     * Test listener class to find out when the test command has been executed.
     */
    static class TestQueueListener implements CommandQueueListener
    {
        private boolean idleEvent;

        public void commandQueueChanged(CommandQueueEvent e)
        {
            if (CommandQueueEvent.Type.QUEUE_IDLE == e.getType())
            {
                idleEvent = true;
            }
        }

        public boolean isIdleEventReceived()
        {
            return idleEvent;
        }

        public void setIdleEventReceived(boolean f)
        {
            idleEvent = f;
        }

        public void waitForIdleEvent()
        {
            int cnt = 0;
            while (!isIdleEventReceived() && cnt < 6)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException iex)
                {
                    iex.printStackTrace();
                }
                cnt++;
            }
        }
    }

    /**
     * A test class derived from Application to test if the command line is
     * parsed.
     */
    private static class TestApp extends ApplicationTestImpl
    {
        private boolean paramFound;

        @Override
        public void processCommandLine(String[] args)
                throws ApplicationException
        {
            super.processCommandLine(args);
            for (int i = 0; i < args.length; i++)
            {
                if (TEST_PARAM.equalsIgnoreCase(args[i]))
                {
                    paramFound = true;
                }
            }
        }

        public boolean isParamFound()
        {
            return paramFound;
        }
    }

    /**
     * A specialized Application implementation that is easier to test.
     */
    private static class ApplicationTestImpl extends Application
    {
        /** A flag whether the main window was shown.*/
        boolean windowShown;

        /** A flag whether showMainWindow() should be mocked. True by default!*/
        boolean mockShowWindow = true;

        /** A counter for initGUI() invocations. */
        int initGUICount;

        /** A flag whether initGUI() should be mocked. */
        boolean mockInitGUI;

        /** The CLP passed to initClassLoaderProvider().*/
        ClassLoaderProvider clpInit;

        /** The CLP to be returned by initClassLoaderProvider().*/
        ClassLoaderProvider clpOverride;

        /** The CLP passed to processBeanDefintions().*/
        ClassLoaderProvider clpProcess;

        /** A mock factory to be returned by getBeanBuilderFactory().*/
        BeanBuilderFactory mockFactory;

        /** A locator to be returned for platform-specific beans. */
        private Locator platformBeansLocator = PLATFORM_BEANS;

        /**
         * A flag whether platform-specific beans should be handled by this
         * class.
         */
        private boolean overrideGetPlatformBeansLocator = true;

        /**
         * Returns the locator for platform-specific bean declarations.
         *
         * @return the {@code Locator} for platform beans
         */
        @Override
        public Locator getPlatformBeansLocator()
        {
            return isOverrideGetPlatformBeansLocator() ? platformBeansLocator
                    : super.getPlatformBeansLocator();
        }

        /**
         * Sets the locator for platform-specific bean declarations. This
         * locator is returned by the overridden
         * {@link #getPlatformBeansLocator()} method.
         *
         * @param platformBeansLocator the {@code Locator} for platform beans
         */
        public void setPlatformBeansLocator(Locator platformBeansLocator)
        {
            this.platformBeansLocator = platformBeansLocator;
        }

        /**
         * Returns a flag whether an alternative locator for platform beans
         * should be returned.
         *
         * @return the overrideGetPlatformBeansLocator flag
         */
        public boolean isOverrideGetPlatformBeansLocator()
        {
            return overrideGetPlatformBeansLocator;
        }

        /**
         * Sets a flag whether an alternative locator for platform beans should
         * be used. If set to <b>true</b>, {@link #getPlatformBeansLocator()}
         * returns the locator set by {@link #setPlatformBeansLocator(Locator)};
         * otherwise, the super method is called.
         *
         * @param overrideGetPlatformBeansLocator the
         *        overrideGetPlatformBeansLocator flag
         */
        public void setOverrideGetPlatformBeansLocator(
                boolean overrideGetPlatformBeansLocator)
        {
            this.overrideGetPlatformBeansLocator =
                    overrideGetPlatformBeansLocator;
        }

        /**
         * Records this invocation. Optionally calls the super method.
         */
        @Override
        protected void showMainWindow(Window window)
        {
            windowShown = true;
            if (!mockShowWindow)
            {
                super.showMainWindow(window);
            }
        }

        /**
         * Records this invocation. Optionally calls the super method.
         */
        @Override
        protected void initGUI(ApplicationContext appCtx)
        {
            initGUICount++;
            if (!mockInitGUI)
            {
                super.initGUI(appCtx);
            }
            else
            {
                appCtx.setMainWindow(EasyMock.createMock(Window.class));
            }
        }

        /**
         * Records this invocation. Optionally returns a different CLP.
         */
        @Override
        protected ClassLoaderProvider initClassLoaderProvider(
                ClassLoaderProvider clp)
        {
            clpInit = clp;
            ClassLoaderProvider superClp = super.initClassLoaderProvider(clp);
            return (clpOverride != null) ? clpOverride : superClp;
        }

        /**
         * Stores the CLP passed to this method.
         */
        @Override
        void processBeanDefinitions(Collection<Locator> defs,
                BeanContext context, ClassLoaderProvider clp)
        {
            clpProcess = clp;
            super.processBeanDefinitions(defs, context, clp);
        }

        /**
         * Either returns a mock factory or calls the super method.
         */
        @Override
        public BeanBuilderFactory getBeanBuilderFactory()
        {
            return (mockFactory != null) ? mockFactory : super
                    .getBeanBuilderFactory();
        }
    }

    /**
     * A test shutdown listener implementation.
     */
    static class TestShutdownListener implements ApplicationShutdownListener
    {
        private boolean shutdownCalled;

        private boolean canShutdownCalled;

        public boolean canShutdown(Application app)
        {
            canShutdownCalled = true;
            return false;
        }

        public void shutdown(Application app)
        {
            shutdownCalled = true;
        }

        public boolean isCanShutdownCalled()
        {
            return canShutdownCalled;
        }

        public boolean isShutdownCalled()
        {
            return shutdownCalled;
        }
    }

    /**
     * A test exit handler implementation. It allows checking whether the
     * application actually exited and which exit code was used.
     */
    private static class TestExitHandler implements Runnable
    {
        /** A reference to the current application. */
        private final Application app;

        /** The exit code of the application. */
        private int exitCode;

        public TestExitHandler(Application application)
        {
            app = application;
            exitCode = -1;
        }

        /**
         * Returns the exit code provided by the application
         *
         * @return the exit code
         */
        public int getExitCode()
        {
            return exitCode;
        }

        public void run()
        {
            exitCode = app.getExitCode();
        }
    }

    /**
     * A test bean builder factory implementation.
     */
    public static class BeanBuilderFactoryTestImpl extends
            JellyBeanBuilderFactory
    {
    }

    /**
     * A test implementation of a GUISynchronizer. This is just used to test
     * whether a corresponding bean declaration is correctly evaluated.
     */
    public static class GUISynchronizerTestImpl implements GUISynchronizer
    {
        public void asyncInvoke(Runnable runnable)
        {
            runnable.run();
        }

        public void syncInvoke(Runnable runnable) throws GUIRuntimeException
        {
            runnable.run();
        }

        public boolean isEventDispatchThread()
        {
            throw new UnsupportedOperationException("Unexpected method call!");
        }
    }

    /**
     * A test implementation of a MessageOutput. This class is referenced by
     * the script with test platform bean definitions.
     */
    public static class MessageOutputTestImpl implements MessageOutput
    {
        public int show(Window parent, Object message, String title,
                int messageType, int buttonType)
        {
            throw new UnsupportedOperationException("Unexpected method call!");
        }
    }
}

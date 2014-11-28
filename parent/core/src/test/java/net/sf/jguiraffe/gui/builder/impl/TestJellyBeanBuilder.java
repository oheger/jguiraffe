/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanContextClient;
import net.sf.jguiraffe.di.BeanCreationEvent;
import net.sf.jguiraffe.di.BeanCreationListener;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.MutableBeanStore;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.DefaultBeanContext;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.di.impl.DefaultClassLoaderProvider;
import net.sf.jguiraffe.di.impl.RestrictedDependencyProvider;
import net.sf.jguiraffe.di.impl.providers.TestBeanProviders;
import net.sf.jguiraffe.gui.builder.BeanBuilderResult;
import net.sf.jguiraffe.gui.builder.BuilderException;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;
import net.sf.jguiraffe.locators.ByteArrayLocator;
import net.sf.jguiraffe.locators.ClassPathLocator;
import net.sf.jguiraffe.locators.FileLocator;
import net.sf.jguiraffe.locators.Locator;
import net.sf.jguiraffe.locators.LocatorException;
import net.sf.jguiraffe.locators.LocatorUtils;

import org.apache.commons.jelly.JellyContext;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * Test class for JellyBeanBuilder. This class also tests the corresponding
 * factory implementation. By executing a complex script with bean definitions
 * the tag handler classes are also tested.
 *
 * @author Oliver Heger
 * @version $Id: TestJellyBeanBuilder.java 207 2012-02-09 07:30:13Z oheger $
 */
public class TestJellyBeanBuilder
{
    /** An array with the names of the stores defined by the test script. */
    private static final String[] STORE_NAMES = {
            "beans", "const", "special"
    };

    /** An array with the elements produced by collection tags. */
    private static final Object[] COL_ELEMENTS = {
            1, 2, 1000L
    };

    /** Constant for the name of the test script. */
    private static final String SCRIPT = "/jelly_scripts/di.jelly";

    /** Constant for the name of the error script. */
    private static final String ERROR_SCRIPT = "/jelly_scripts/di_error.jelly";

    /** Constant for the locator to the test script. */
    private static final Locator SCRIPT_LOCATOR = ClassPathLocator
            .getInstance(SCRIPT);

    /** Constant for the locator to the error script. */
    private static final Locator ERROR_SCRIPT_LOCATOR = ClassPathLocator
            .getInstance(ERROR_SCRIPT);

    /** Constant for the the value of the string property. */
    private static final String STR_PROP = "Test";

    /** Constant for the windows-style line endings. */
    private static final String WND_LF = "\r\n";

    /** Constant for the CR character. */
    private static final char CR = '\r';

    /** Constant for the LF character. */
    private static final char LF = '\n';

    /** Constant for the value of the int property. */
    private static final int INT_PROP = 42;

    /** Constant for the number of iterations to run in some tests. */
    private static final int COUNT = 12;

    /** Stores the builder factory. */
    private JellyBeanBuilderFactory factory;

    /** Stores the builder to be tested. */
    private JellyBeanBuilder builder;

    /** Stores the builder result object. */
    private BeanBuilderResult result;

    @Before
    public void setUp() throws Exception
    {
        factory = new JellyBeanBuilderFactory();
    }

    /**
     * Creates a new Jelly bean builder instance.
     *
     * @return the newly created instance
     * @throws BuilderException in case of an error
     */
    private JellyBeanBuilder setUpBuilder() throws BuilderException
    {
        return (JellyBeanBuilder) factory.getBeanBuilder();
    }

    /**
     * Processes the test script and returns the builder results.
     *
     * @return the results of the build operation
     * @throws BuilderException if an error occurs
     */
    private BeanBuilderResult build() throws BuilderException
    {
        builder = setUpBuilder();
        return builder.build(SCRIPT_LOCATOR, null, null);
    }

    /**
     * Processes the test builder script and creates a context object for the
     * results. The store with the specified name will become the default store
     * of this context object.
     *
     * @param storeName the name of the default store
     * @return the context object
     * @throws BuilderException if an error occurs
     */
    private BeanContext setUpContext(String storeName) throws BuilderException
    {
        result = build();
        return new DefaultBeanContext(result.getBeanStore(storeName));
    }

    /**
     * Creates a bean context for the test script using the default bean store.
     *
     * @return the context object
     * @throws BuilderException if an error occurs
     */
    private BeanContext setUpContext() throws BuilderException
    {
        return setUpContext(STORE_NAMES[0]);
    }

    /**
     * Tests the properties of a test object.
     *
     * @param obj the object to be checked
     */
    private void checkTestObject(Object obj)
    {
        ReflectionTestClass c = (ReflectionTestClass) obj;
        assertEquals("Wrong string property", STR_PROP, c.getStringProp());
        assertEquals("Wrong int property", INT_PROP, c.getIntProp());
    }

    /**
     * Tests accessing a bean from the test bean definitions that is a
     * singleton. The bean is accessed multiple times, always checking that the
     * same instance is returned.
     *
     * @param beanName the name of the singleton bean
     * @throws BuilderException if an error occurs
     */
    private void checkSingleton(String beanName) throws BuilderException
    {
        BeanContext context = setUpContext();
        Object bean = context.getBean(beanName);
        checkTestObject(bean);
        for (int i = 1; i < COUNT; i++)
        {
            assertSame("Multiple instances of bean " + beanName + " returned",
                    bean, context.getBean(beanName));
        }
    }

    /**
     * Tests accessing a bean from the test bean definitions that is a factory
     * bean (i.e. no singleton). The bean is accessed multiple times, always
     * ensuring that different instances are returned.
     *
     * @param beanName the name of the factory bean
     * @throws BuilderException if an error occurs
     */
    private void checkFactory(String beanName) throws BuilderException
    {
        BeanContext context = setUpContext();
        Set<Object> instances = new HashSet<Object>();
        for (int i = 0; i < COUNT; i++)
        {
            Object bean = context.getBean(beanName);
            checkTestObject(bean);
            assertTrue("Same instance returned for bean " + beanName, instances
                    .add(bean));
        }
    }

    /**
     * Tests whether changing the factory's name space URI also affects the
     * created builders.
     */
    @Test
    public void testFactoryNameSpaceURI() throws BuilderException
    {
        builder = setUpBuilder();
        assertEquals("Wrong name space URI for builder",
                JellyBeanBuilderFactory.NSURI_DI_BUILDER, builder
                        .getDiBuilderNameSpaceURI());
        final String nsURI = "newNSURI";
        factory.setDiBuilderNameSpaceURI(nsURI);
        JellyBeanBuilder b2 = (JellyBeanBuilder) factory.getBeanBuilder();
        assertNotSame("Same instance returned", builder, b2);
        assertEquals("Wrong changes name space URI for builder", nsURI, b2
                .getDiBuilderNameSpaceURI());
    }

    /**
     * Tests the result object returned by a builder operation. We check whether
     * it contains the expected stores.
     */
    @Test
    public void testBuilderResultStores() throws BuilderException
    {
        BeanBuilderResult res = build();
        assertEquals("Wrong number of store objects", STORE_NAMES.length, res
                .getBeanStoreNames().size());
        for (String storeName : STORE_NAMES)
        {
            assertTrue("Store not found: " + storeName, res.getBeanStoreNames()
                    .contains(storeName));
            assertNotNull("NULL returned for store: " + storeName, res
                    .getBeanStore(storeName));
        }
    }

    /**
     * Tests whether the builder data object is correctly created and
     * initialized.
     */
    @Test
    public void testCreateBuilderData() throws BuilderException
    {
        JellyContext ctx = new JellyContext();
        MutableBeanStore store = EasyMock.createMock(MutableBeanStore.class);
        ClassLoaderProvider clp =
                EasyMock.createMock(ClassLoaderProvider.class);
        InvocationHelper invHlp = new InvocationHelper();
        EasyMock.replay(store, clp);
        DIBuilderData data =
                setUpBuilder().createBuilderData(ctx, store, clp, invHlp);
        assertSame("Wrong root store", store, data.getRootBeanStore());
        assertSame("Wrong class loader provider", clp,
                data.getClassLoaderProvider());
        assertSame("Wrong invocation helper", invHlp,
                data.getInvocationHelper());
        assertSame("Data not stored in context", data, DIBuilderData.get(ctx));
    }

    /**
     * Tests the stores returned by the builder result object when a root store
     * was provided.
     */
    @Test
    public void testBuilderResultRootStore() throws BuilderException
    {
        builder = setUpBuilder();
        DefaultBeanStore myStore = new DefaultBeanStore();
        BeanBuilderResult res = builder.build(SCRIPT_LOCATOR, myStore,
                new DefaultClassLoaderProvider());
        assertSame("Wrong root store returned", myStore, res.getBeanStore(null));
        assertEquals("Wrong number of store objects", STORE_NAMES.length, res
                .getBeanStoreNames().size());
        assertSame("Wrong parent of store", myStore, res.getBeanStore(
                STORE_NAMES[1]).getParent());
        assertTrue("root bean not found", myStore.providerNames().contains(
                "rootBean"));
    }

    /**
     * Tests whether the builder result contains helper objects, even if none
     * have been passed to the builder.
     */
    @Test
    public void testBuilderResultDefaultHelperObjects() throws BuilderException
    {
        BeanBuilderResult result = build();
        assertNotNull("No class loader provider",
                result.getClassLoaderProvider());
        assertNotNull("No invocation helper", result.getInvocationHelper());
    }

    /**
     * Tests whether the builder result contains the expected class loader
     * provider.
     */
    @Test
    public void testBuilderResultGetClassLoaderProvider()
            throws BuilderException
    {
        ClassLoaderProvider clp = new DefaultClassLoaderProvider();
        BeanBuilderResult result =
                setUpBuilder().build(SCRIPT_LOCATOR, null, clp);
        assertSame("Wrong class loader provider", clp,
                result.getClassLoaderProvider());
    }

    /**
     * Tests whether the builder result contains the expected invocation helper.
     */
    @Test
    public void testBuilderResultGetInvocationHelper() throws BuilderException
    {
        InvocationHelper invHlp = new InvocationHelper();
        BeanBuilderResult result =
                setUpBuilder().build(SCRIPT_LOCATOR, null, null, invHlp);
        assertSame("Wrong invocation helper", invHlp,
                result.getInvocationHelper());
    }

    /**
     * Tests calling build() with no locator. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuildNullLocator() throws BuilderException
    {
        builder = setUpBuilder();
        builder.build(null, null, null);
    }

    /**
     * Tries to invoke a script that causes an error. The exception must be
     * redirected as a builder exception.
     */
    @Test
    public void testBuildError() throws BuilderException
    {
        builder = setUpBuilder();
        try
        {
            builder.build(ERROR_SCRIPT_LOCATOR, null, null);
            fail("Error not detected!");
        }
        catch (BuilderException bex)
        {
            assertEquals("Wrong script URL", ERROR_SCRIPT_LOCATOR.getURL(), bex
                    .getScriptURL());
        }
    }

    /**
     * Tests a builder invocation that throws an IO error. The exception must be
     * redirected as a builder exception.
     */
    @Test
    public void testBuildIOError() throws BuilderException
    {
        builder = setUpBuilder();
        Locator locator = FileLocator.getInstance("A non existing file!");
        try
        {
            builder.build(locator, null, null);
            fail("IO exception not detected!");
        }
        catch(BuilderException bex)
        {
            assertEquals("Wrong script URL", locator.getURL(), bex.getScriptURL());
        }
    }

    /**
     * Tests whether an exception thrown by the locator is wrapped by a builder
     * exception.
     */
    @Test
    public void testBuildLocatorException() throws IOException,
            BuilderException
    {
        Locator locator = EasyMock.createMock(Locator.class);
        LocatorException locex = new LocatorException("Test exception!");
        URL url = new URL("http://jguiraffe.sf.net");
        EasyMock.expect(locator.getInputStream()).andThrow(locex);
        EasyMock.expect(locator.getURL()).andReturn(url);
        EasyMock.replay(locator);
        builder = setUpBuilder();
        try
        {
            builder.build(locator, null, null);
            fail("Locator exception not detected!");
        }
        catch (BuilderException bex)
        {
            assertEquals("Wrong cause", locex, bex.getCause());
            assertSame("Wrong URL", url, bex.getScriptURL());
        }
        EasyMock.verify(locator);
    }

    /**
     * Tests whether a locator exception is handled by the build() method if
     * querying the script URL causes an exception, too.
     */
    @Test
    public void testBuildLocatorExceptionURLEx() throws IOException,
            BuilderException
    {
        Locator locator = EasyMock.createMock(Locator.class);
        LocatorException locex = new LocatorException("Test exception!");
        EasyMock.expect(locator.getInputStream()).andThrow(locex);
        EasyMock.expect(locator.getURL()).andThrow(new LocatorException());
        EasyMock.replay(locator);
        builder = setUpBuilder();
        try
        {
            builder.build(locator, null, null);
            fail("Locator exception not detected!");
        }
        catch (BuilderException bex)
        {
            assertEquals("Wrong cause", locex, bex.getCause());
            assertNull("Got a URL", bex.getScriptURL());
        }
        EasyMock.verify(locator);

    }

    /**
     * Creates an in-memory locator pointing to the test Jelly script.
     *
     * @return the in-memory locator
     * @throws IOException if an error occurs
     */
    private ByteArrayLocator createMemoryLocator() throws IOException
    {
        ByteArrayOutputStream bais = JGuiraffeTestHelper.readStream(
                LocatorUtils.openStream(SCRIPT_LOCATOR), true);
        ByteArrayLocator locator = ByteArrayLocator.getInstance(bais
                .toByteArray());
        return locator;
    }

    /**
     * Tests preparing an input source when a regular URL is used.
     */
    @Test
    public void testPrepareInputSourceValidURL() throws BuilderException,
            IOException
    {
        builder = setUpBuilder();
        InputSource is = builder.prepareInputSource(SCRIPT_LOCATOR);
        assertEquals("Wrong system ID", SCRIPT_LOCATOR.getURL().toString(), is
                .getSystemId());
    }

    /**
     * Tests preparing an input source for an in-memory locator. The URL
     * returned by such a locator cannot be processed by Jelly. So the input
     * source is initialized only with the stream, not with a system ID.
     */
    @Test
    public void testPrepareInputSourceInMemoryLocator()
            throws BuilderException, IOException
    {
        builder = setUpBuilder();
        InputSource is = builder.prepareInputSource(createMemoryLocator());
        assertNull("Got a system ID", is.getSystemId());
    }

    /**
     * Tests whether a script from an in-memory locator can be executed.
     */
    @Test
    public void testBuildMemoryLocator() throws BuilderException, IOException
    {
        ByteArrayLocator locator = createMemoryLocator();
        builder = setUpBuilder();
        BeanBuilderResult result = builder.build(locator, null, null);
        BeanContext ctx = new DefaultBeanContext(result
                .getBeanStore(STORE_NAMES[0]));
        assertEquals("Wrong string constant", STR_PROP, ctx.getBean("strConst"));
    }

    /**
     * Tests accessing a constant bean.
     */
    @Test
    public void testBuildConstBean() throws BuilderException
    {
        BeanContext ctx = setUpContext();
        assertEquals("Wrong int constant", Integer.valueOf(INT_PROP), ctx
                .getBean("intConst"));
        assertEquals("Wrong string constant", STR_PROP, ctx.getBean("strConst"));
    }

    /**
     * Tests accessing a bean defined by its class that is a singleton.
     */
    @Test
    public void testBuildClassSingleton() throws BuilderException
    {
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean("rootBean");
        assertNull("String property already set", bean.getStringProp());
        assertSame("Multiple instances returned", bean, context
                .getBean("rootBean"));
    }

    /**
     * Tests accessing a bean defined by a constructor that is a singleton.
     */
    @Test
    public void testBuildConstructorSingleton() throws BuilderException
    {
        checkSingleton("singletonBean");
    }

    /**
     * Tests whether literal values can be used in a constructor invocation
     */
    @Test
    public void testBuildConstructorSingletonValues() throws BuilderException
    {
        checkSingleton("singletonBeanValues");
    }

    /**
     * Tests accessing a bean defined by a constructor that is a factory.
     */
    @Test
    public void testBuildConstructorFactory() throws BuilderException
    {
        checkFactory("factoryBean");
    }

    /**
     * Tests accessing a singleton bean defined by a factory method.
     */
    @Test
    public void testBuildFactoryMethodSingleton() throws BuilderException
    {
        checkSingleton("factoryMethodSingleton");
    }

    /**
     * Tests accessing a factory bean defined by a factory method.
     */
    @Test
    public void testBuildFactoryMethodFactory() throws BuilderException
    {
        checkFactory("factoryMethodFactory");
    }

    /**
     * Tests whether literal parameter values can be used when invoking a factory
     * method.
     */
    @Test
    public void testBuildFactoryMethodFactoryValues() throws BuilderException
    {
        checkFactory("factoryMethodFactoryValues");
    }

    /**
     * Tests a bean definition with property set operations that are passed
     * literal values.
     */
    @Test
    public void testBuildSetPropertiesValues() throws BuilderException
    {
        checkSingleton("propertyBeanValues");
    }

    /**
     * Tests a bean with a complex initialization script.
     */
    @Test
    public void testBuildInitScript() throws BuilderException
    {
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean("initializer");
        assertEquals("Wrong string property", STR_PROP + STR_PROP, bean
                .getStringProp());
        assertEquals("Wrong int property", 2 * INT_PROP, bean.getIntProp());
        assertNotSame("Same instance returned", bean, context
                .getBean("initializer"));
    }

    /**
     * Tests beans with a (valid) cyclic reference.
     */
    @Test
    public void testBuildCyclicReference() throws BuilderException
    {
        BeanContext context = setUpContext(STORE_NAMES[STORE_NAMES.length - 1]);
        TestBeanProviders.BeanA beanA = context
                .getBean(TestBeanProviders.BeanA.class);
        TestBeanProviders.BeanB beanB = beanA.getRefB();
        assertEquals("Wrong reference from B to A", beanA, beanB.getRefA());
        TestBeanProviders.BeanB beanB2 = context
                .getBean(TestBeanProviders.BeanB.class);
        assertNotSame("Same B instance returned", beanB, beanB2);
        TestBeanProviders.BeanA beanA2 = beanB2.getRefA();
        assertNotSame("Same A instance returned", beanA, beanA2);
        assertEquals("Wrong reference from A to B", beanB2, beanA2.getRefB());
    }

    /**
     * Tests whether the hierarchy of stores is correct.
     */
    @Test
    public void testBuildStores() throws BuilderException
    {
        BeanContext context = setUpContext();
        try
        {
            context.getBean(TestBeanProviders.BeanA.class);
            fail("Could access bean from child store!");
        }
        catch (InjectionException iex)
        {
            // ok
        }
    }

    /**
     * Tests an anonymous bean declaration.
     */
    @Test
    public void testBuildAnonymous() throws BuilderException
    {
        BeanContext context = setUpContext();
        TestBeanProviders.BeanB bean = (TestBeanProviders.BeanB) context
                .getBean("anonymous");
        TestBeanProviders.BeanA beanA = bean.getRefA();
        ReflectionTestClass refTest = beanA.getTestData();
        assertEquals("Wrong int property", INT_PROP, refTest.getIntProp());
        assertEquals("Wrong string property", STR_PROP, refTest.getStringProp());
    }

    /**
     * Helper method for testing the content of an ordered collection created by
     * the collection tags.
     *
     * @param col the collection to test
     */
    private static void checkOrderedCollection(Collection<?> col)
    {
        assertEquals("Wrong number of elements", COL_ELEMENTS.length, col
                .size());
        int idx = 0;
        for (Object o : col)
        {
            assertEquals("Wrong element at " + idx, COL_ELEMENTS[idx++], o);
        }
    }

    /**
     * Tests creating a list bean.
     */
    @Test
    public void testBuildList() throws BuilderException
    {
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean("list");
        List<?> lst = (List<?>) bean.getData();
        checkOrderedCollection(lst);
    }

    /**
     * Tests whether a list can be created as a top-level bean.
     */
    @Test
    public void testBuildListTopLevel() throws BuilderException
    {
        BeanContext context = setUpContext();
        checkOrderedCollection((List<?>) context.getBean("topLevelList"));
    }

    /**
     * Tests creating a list definition with a null element.
     */
    @Test
    public void testBuildListWithNullElem() throws BuilderException
    {
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean("listNull");
        List<?> lst = (List<?>) bean.getData();
        assertEquals("Wrong element 1", "1", lst.get(0));
        assertEquals("Wrong element 3", "3", lst.get(2));
        assertNull("Wrong element 2", lst.get(1));
    }

    /**
     * Tests creating a list with elements that are references to other beans.
     */
    @Test
    public void testBuildListWithDependencies() throws BuilderException
    {
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean("listDependency");
        List<?> lst = (List<?>) bean.getData();
        assertEquals("Wrong element 1", "1", lst.get(0));
        assertEquals("Wrong element 2", Integer.valueOf(INT_PROP), lst.get(1));
        assertEquals("Wrong element 3", STR_PROP, lst.get(2));
    }

    /**
     * Helper method for checking a set bean.
     *
     * @param set the set to be tested
     */
    private static void checkSet(Set<?> set)
    {
        assertEquals("Wrong number of elements", COL_ELEMENTS.length, set
                .size());
        for (Object o : COL_ELEMENTS)
        {
            assertTrue("Element not found: " + o, set.contains(o));
        }
    }

    /**
     * Tests creating a set bean.
     */
    @Test
    public void testBuildSet() throws BuilderException
    {
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context.getBean("set");
        assertTrue("Wrong set", bean.getData() instanceof HashSet<?>);
        Set<?> set = (Set<?>) bean.getData();
        checkSet(set);
    }

    /**
     * Tests whether a set can be created as a top-level bean.
     */
    @Test
    public void testBuildSetTopLevel() throws BuilderException
    {
        BeanContext context = setUpContext();
        checkSet((Set<?>) context.getBean("topLevelSet"));
    }

    /**
     * Tests creating an ordered set bean.
     */
    @Test
    public void testBuildOrderedSet() throws BuilderException
    {
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean("orderedset");
        Set<?> set = (Set<?>) bean.getData();
        checkOrderedCollection(set);
    }

    /**
     * Helper method for testing the content of a map.
     *
     * @param map the map
     */
    private void checkMap(Map<?, ?> map)
    {
        assertEquals("Wrong size of map", 3, map.size());
        assertEquals("Wrong key 1", Integer.valueOf(1), map.get("key1"));
        assertEquals("Wrong key 2", Integer.valueOf(2), map.get("key2"));
        assertEquals("Wrong key 3", Long.valueOf(1000), map.get("key3"));
    }

    /**
     * Tests creating a map bean.
     */
    @Test
    public void testBuildMap() throws BuilderException
    {
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context.getBean("map");
        assertTrue("Wrong map", bean.getData() instanceof HashMap<?, ?>);
        checkMap((Map<?, ?>) bean.getData());
    }

    /**
     * Tests whether a map can be created as top-level bean.
     */
    @Test
    public void testBuildMapTopLevel() throws BuilderException
    {
        BeanContext context = setUpContext();
        checkMap((Map<?, ?>) context.getBean("topLevelMap"));
    }

    /**
     * Tests creating an ordered map bean.
     */
    @Test
    public void testBuildOrderedMap() throws BuilderException
    {
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean("orderedmap");
        assertTrue("Wrong map: " + bean.getData(),
                bean.getData() instanceof LinkedHashMap<?, ?>);
        Map<?, ?> map = (Map<?, ?>) bean.getData();
        checkMap(map);
        int idx = 1;
        for (Object key : map.keySet())
        {
            assertEquals("Wrong key", "key" + idx, key);
            idx++;
        }
    }

    /**
     * Tests creating a map with complex key and value objects.
     */
    @Test
    public void testBuildComplexMap() throws BuilderException
    {
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean("mapcomplex");
        Map<?, ?> map = (Map<?, ?>) bean.getData();
        assertEquals("Wrong number of elements", 1, map.size());
        Collection<?> keyCol = (Collection<?>) map.keySet().iterator().next();
        checkOrderedCollection(keyCol);
        LinkedHashSet<?> valCol = (LinkedHashSet<?>) map.get(keyCol);
        checkOrderedCollection(valCol);
    }

    /**
     * Tests creating a map with dependencies to other beans.
     */
    @Test
    public void testBuildMapWithDepenencies() throws BuilderException
    {
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean("dependencymap");
        Map<?, ?> map = (Map<?, ?>) bean.getData();
        assertEquals("Wrong number of elements", 3, map.size());
        assertEquals("Dependencies not resolved", INT_PROP, map.get(STR_PROP));
    }

    /**
     * Helper method for checking the content of a properties object produced by
     * the tags under test.
     *
     * @param props the properties to test
     */
    private void checkProperties(Properties props)
    {
        assertEquals("Wrong number of properties", 3, props.size());
        assertEquals("Wrong usr", "scott", props.get("db.usr"));
        assertEquals("Wrong pwd", "tiger", props.get("db.pwd"));
        assertEquals("Wrong src", "defaultDS", props.get("db.src"));
    }

    /**
     * Tests creating a properties object.
     */
    @Test
    public void testBuildProperties() throws BuilderException
    {
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean("properties");
        Properties props = (Properties) bean.getData();
        checkProperties(props);
    }

    /**
     * Tests whether properties can be created as top-level beans.
     */
    @Test
    public void testBuildPropertiesTopLevel() throws BuilderException
    {
        BeanContext context = setUpContext();
        checkProperties((Properties) context.getBean("topLevelProps"));
    }

    /**
     * Tests creating an object that requires complex initialization, e.g. a
     * bean context client.
     */
    @Test
    public void testBuildContextClient() throws BuilderException
    {
        BeanContext context = setUpContext();
        BeanCreationListenerTestImpl l = new BeanCreationListenerTestImpl();
        context.addBeanCreationListener(l);
        TestBean bean = (TestBean) context.getBean("contextClient");
        assertEquals("Wrong context", context, bean.context);
        assertTrue("Test bean not found by listener", l.foundTestBean);
        assertEquals("Wrong number of events", 2, l.eventCount);
    }

    /**
     * Tests a bean with a shutdown handler.
     */
    @Test
    public void testBuildShutdown() throws BuilderException
    {
        final String beanName = "shutdown";
        BeanContext context = setUpContext();
        ReflectionTestClass bean =
                (ReflectionTestClass) context.getBean(beanName);
        checkTestObject(bean);
        BeanStore store = result.getBeanStore(null);
        assertTrue("Bean not found", store.providerNames().contains(beanName));
        store.getBeanProvider(beanName).shutdown(
                new RestrictedDependencyProvider(EasyMock
                        .createNiceMock(ClassLoaderProvider.class),
                        new InvocationHelper()));
        assertEquals("Int property not reset", 0, bean.getIntProp());
        assertEquals("String property not reset", "", bean.getStringProp());
    }

    /**
     * Tests a bean that defines a shutdown method in its shutdown handler.
     */
    @Test
    public void testBuildShutdownMethod() throws BuilderException
    {
        final String beanName = "shutdownMeth";
        BeanContext context = setUpContext();
        ReflectionTestClass bean =
                (ReflectionTestClass) context.getBean(beanName);
        assertFalse("Already shutdown", bean.isShutdown());
        BeanStore store = result.getBeanStore(null);
        assertTrue("Bean not found", store.providerNames().contains(beanName));
        store.getBeanProvider(beanName).shutdown(
                new RestrictedDependencyProvider(EasyMock
                        .createNiceMock(ClassLoaderProvider.class),
                        new InvocationHelper()));
        assertTrue("Shutdown method not called", bean.isShutdown());
    }

    /**
     * Tries to call release() without a builder result.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReleaseNull() throws BuilderException
    {
        setUpBuilder().release(null);
    }

    /**
     * Tests whether a dependency provider for releasing a builder result object
     * can be created.
     */
    @Test
    public void testCreateReleaseDependencyProvider() throws BuilderException
    {
        BeanBuilderResult res = EasyMock.createMock(BeanBuilderResult.class);
        ClassLoaderProvider clp =
                EasyMock.createMock(ClassLoaderProvider.class);
        InvocationHelper invHlp = new InvocationHelper();
        EasyMock.expect(res.getClassLoaderProvider()).andReturn(clp);
        EasyMock.expect(res.getInvocationHelper()).andReturn(invHlp);
        EasyMock.replay(res, clp);
        builder = setUpBuilder();
        RestrictedDependencyProvider depProvider =
                (RestrictedDependencyProvider) builder
                        .createReleaseDependencyProvider(res);
        assertSame("Wrong CLP", clp, depProvider.getClassLoaderProvider());
        assertSame("Wrong invocation helper", invHlp,
                depProvider.getInvocationHelper());
        EasyMock.verify(res, clp);
    }

    /**
     * Tests createReleaseDependencyProvider() if the builder data object does
     * not contain a class loader provider. This should not happen if the result
     * object returned by the builder is used.
     */
    @Test
    public void testCreateReleaseDependencyProviderNoCLP()
            throws BuilderException
    {
        BeanBuilderResult res = EasyMock.createMock(BeanBuilderResult.class);
        EasyMock.expect(res.getClassLoaderProvider()).andReturn(null);
        EasyMock.expect(res.getInvocationHelper())
                .andReturn(new InvocationHelper()).anyTimes();
        EasyMock.replay(res);
        builder = setUpBuilder();
        try
        {
            builder.createReleaseDependencyProvider(res);
            fail("Missing class loader provider not detected!");
        }
        catch (IllegalArgumentException iex)
        {
            EasyMock.verify(res);
        }
    }

    /**
     * Tests createReleaseDependencyProvider() if the builder data object does
     * not contain an invocation helper. This should not happen if the result
     * object returned by the builder is used.
     */
    @Test
    public void testCreateReleaseDependencyProviderNoInvHlp()
            throws BuilderException
    {
        BeanBuilderResult res = EasyMock.createMock(BeanBuilderResult.class);
        ClassLoaderProvider clp =
                EasyMock.createNiceMock(ClassLoaderProvider.class);
        EasyMock.expect(res.getClassLoaderProvider()).andReturn(clp).anyTimes();
        EasyMock.expect(res.getInvocationHelper()).andReturn(null);
        EasyMock.replay(res);
        builder = setUpBuilder();
        try
        {
            builder.createReleaseDependencyProvider(res);
            fail("Missing invocation helper not detected!");
        }
        catch (IllegalArgumentException iex)
        {
            EasyMock.verify(res);
        }
    }

    /**
     * Tests the value tag.
     */
    @Test
    public void testBuildValueTag() throws BuilderException
    {
        final String beanName = "valueTest";
        BeanContext context = setUpContext();
        ReflectionTestClass bean =
                (ReflectionTestClass) context.getBean(beanName);
        assertEquals("Wrong value", "Line 1" + LF + "Line 2" + LF + "Line 3",
                fixCR(bean.getStringProp()));
    }

    /**
     * Normalizes the line endings in the specified string. This is needed on
     * operating systems with different conventions for line endings. The
     * resulting string contains only the LF character for line endings.
     *
     * @param s the string to be processed
     * @return the string with normalized line endings
     */
    private static String fixCR(String s)
    {
        String result = s.replace(WND_LF, String.valueOf(LF));
        result = result.replace(CR, LF);
        return result;
    }

    /**
     * Tests whether the value tag does not escape its body.
     */
    @Test
    public void testBuildValueTagEscape() throws BuilderException
    {
        final String beanName = "valueTestEscape";
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean(beanName);
        assertEquals("Wrong value", "<test value=\"test\"></test>", bean
                .getStringProp());
    }

    /**
     * Tests whether a parameter can be set to null.
     */
    @Test
    public void testBuildNullParameter() throws BuilderException
    {
        final String beanName = "nullParam";
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean(beanName);
        assertNull("Got a string property", bean.getStringProp());
        assertEquals("Wrong int property", INT_PROP, bean.getIntProp());
    }

    /**
     * Tests whether a constant value can be retrieved.
     */
    @Test
    public void testBuildConstValue() throws BuilderException
    {
        final String beanName = "constValueParam";
        BeanContext context = setUpContext();
        ReflectionTestClass bean = (ReflectionTestClass) context
                .getBean(beanName);
        assertEquals("Wrong int property", ReflectionTestClass.ANSWER
                .intValue(), bean.getIntProp());
    }

    /**
     * Tests type conversion to an enum property.
     */
    @Test
    public void testBuildEnumProperty() throws BuilderException
    {
        final String beanName = "enumTest";
        BeanContext context = setUpContext();
        ReflectionTestClass bean =
                (ReflectionTestClass) context.getBean(beanName);
        assertEquals("Wrong mode property",
                ReflectionTestClass.Mode.PRODUCTION, bean.getMode());
    }

    /**
     * Tests whether from an initializer script a factory bean can be invoked.
     */
    @Test
    public void testBuildFactoryBeanInitializer() throws BuilderException
    {
        final String beanName = "factoryBeanScript";
        BeanContext context = setUpContext();
        String res = (String) context.getBean(beanName);
        assertEquals("Wrong result", "TestResult:"+STR_PROP+'_'+INT_PROP, res);
    }

    /**
     * A bean class used for testing enhanced initialization.
     */
    public static class TestBean extends ReflectionTestClass implements
            BeanContextClient
    {
        /** A reference to the current bean context. */
        BeanContext context;

        public TestBean()
        {
            super();
        }

        public TestBean(String s)
        {
            super(s);
        }

        public void setBeanContext(BeanContext context)
        {
            assertEquals("Wrong value of string property", STR_PROP,
                    getStringProp());
            assertEquals("Wrong value of int property", INT_PROP, getIntProp());
            this.context = context;
        }
    }

    /**
     * A test listener implementation for testing the invocation of creation
     * listeners.
     */
    private static class BeanCreationListenerTestImpl implements
            BeanCreationListener
    {
        /** The number of invocations of the listener. */
        int eventCount;

        /** A flag whether the test bean was found. */
        boolean foundTestBean;

        /**
         * Records this invocation. If the test bean was created, it is further
         * checked.
         */
        public void beanCreated(BeanCreationEvent event)
        {
            eventCount++;
            if (event.getBean() instanceof TestBean)
            {
                TestBean bean = (TestBean) event.getBean();
                assertEquals("Wrong value of string property", STR_PROP, bean
                        .getStringProp());
                assertEquals("Wrong value of int property", INT_PROP, bean
                        .getIntProp());
                assertNotNull("No data set", bean.getData());
                foundTestBean = true;
            }
        }
    }
}

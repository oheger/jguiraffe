/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.di.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.ChainedInvocation;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.ConstructorInvocation;
import net.sf.jguiraffe.di.impl.HelperInvocations;
import net.sf.jguiraffe.di.impl.Invokable;
import net.sf.jguiraffe.di.impl.MethodInvocation;
import net.sf.jguiraffe.di.impl.SetPropertyInvocation;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.di.impl.providers.ConstructorBeanProvider;
import net.sf.jguiraffe.di.impl.providers.FactoryBeanProvider;
import net.sf.jguiraffe.di.impl.providers.SingletonBeanProvider;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for BeanTag and its ancestor, AbstractBeanTag.
 *
 * @author Oliver Heger
 * @version $Id: TestBeanTag.java 208 2012-02-11 20:57:33Z oheger $
 */
public class TestBeanTag
{
    /** Constant for the name of the bean tag. */
    private static final String NAME = "myBean";

    /** Constant for the test bean class. */
    private static final Class<?> BEAN_CLASS = ReflectionTestClass.class;

    /** Constant for the name of a bean store. */
    private static final String STORE_NAME = "myBeanStore";

    /** Constant for the value of the bean. */
    private static final Object VALUE = 42;

    /** Stores the builder data object. */
    private DIBuilderData builderData;

    /** Stores the current context. */
    private JellyContext context;

    /** Stores the tag to be tested. */
    private BeanTag tag;

    @Before
    public void setUp() throws Exception
    {
        context = new JellyContext();
        builderData = new DIBuilderData();
        builderData.put(context);
        tag = new BeanTag();
        tag.setContext(context);
        tag.setName(NAME);
    }

    /**
     * Returns the bean store with the given name.
     *
     * @param name the name of the store (<b>null</b> for the root store)
     * @return the desired bean store
     */
    private BeanStore getStore(String name)
    {
        return builderData.getBeanStore(name);
    }

    /**
     * Convenience method for obtaining a bean provider from a bean store.
     *
     * @param storeName the name of the bean store (<b>null</b> for the root
     * store)
     * @return the found provider
     */
    private BeanProvider getProvider(String storeName)
    {
        return getStore(storeName).getBeanProvider(NAME);
    }

    /**
     * Tests setting the name of the value class.
     */
    @Test
    public void testSetValueClassName()
    {
        tag.setValueClassName(BEAN_CLASS.getName());
        assertEquals("Value class name not set", BEAN_CLASS.getName(), tag
                .getValueData().getValueClassData().getTargetClassName());
    }

    /**
     * Tests setting the class loader for the value class.
     */
    @Test
    public void testSetValueClassLoader()
    {
        tag.setValueClassLoader(NAME);
        assertEquals("Loader name not set", NAME, tag.getValueData()
                .getValueClassData().getClassLoaderName());
    }

    /**
     * Tests processing a bean tag when the bean is defined by its value.
     */
    @Test
    public void testProcessValue() throws JellyTagException
    {
        tag.setValue(VALUE);
        tag.processBeforeBody();
        tag.process();
        ConstantBeanProvider provider = (ConstantBeanProvider) getProvider(null);
        assertEquals("Wrong value set", VALUE, provider.getBean());
    }

    /**
     * Tests processing a bean tag when the class description is specified and
     * the singleton flag is set.
     */
    @Test
    public void testProcessBeanClassSingleton() throws JellyTagException
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        tag.setBeanClass(BEAN_CLASS);
        tag.processBeforeBody();
        tag.process();
        SingletonBeanProvider provider = (SingletonBeanProvider) getProvider(null);
        assertEquals("Wrong bean class", BEAN_CLASS, provider
                .getBeanClass(depProvider));
        assertTrue("Wrong creation provider",
                provider.getBeanCreator() instanceof ConstructorBeanProvider);
        EasyMock.verify(depProvider);
    }

    /**
     * Tests processing a bean tag when the class description is specified and
     * the singleton flag is set to false.
     */
    @Test
    public void testProcessBeanClassFactory() throws JellyTagException
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        final String loaderName = "myClassLoader";
        depProvider.loadClass(BEAN_CLASS.getName(), loaderName);
        EasyMock.expectLastCall().andReturn(BEAN_CLASS);
        EasyMock.replay(depProvider);
        tag.setBeanClassName(BEAN_CLASS.getName());
        tag.setBeanClassLoader(loaderName);
        tag.setSingleton(false);
        tag.processBeforeBody();
        tag.process();
        FactoryBeanProvider provider = (FactoryBeanProvider) getProvider(null);
        assertEquals("Wrong bean class", BEAN_CLASS, provider
                .getBeanClass(depProvider));
        assertTrue("Wrong creation provider",
                provider.getBeanCreator() instanceof ConstructorBeanProvider);
        EasyMock.verify(depProvider);
    }

    /**
     * Tests processing a bean tag when the creator is set in the tag's body.
     */
    @Test
    public void testProcessCreator() throws JellyTagException
    {
        ConstructorBeanProvider creator = new ConstructorBeanProvider(
                new ConstructorInvocation(ClassDescription
                        .getInstance(BEAN_CLASS), null));
        tag.processBeforeBody();
        tag.setBeanCreator(creator);
        tag.process();
        SingletonBeanProvider provider = (SingletonBeanProvider) getProvider(null);
        assertSame("Wrong creator set", creator, provider.getBeanCreator());
    }

    /**
     * Tests processing a bean tag with an in-line definition of an initializer.
     */
    @Test
    public void testProcessInitializer() throws JellyTagException
    {
        tag.setBeanClass(BEAN_CLASS);
        tag.processBeforeBody();
        InvocationData.get(context).addInvokable(
                new SetPropertyInvocation("intProp", ConstantBeanProvider
                        .getInstance(VALUE)), null, null);
        InvocationData.get(context).addInvokable(
                new SetPropertyInvocation("stringProp", ConstantBeanProvider
                        .getInstance("A string")), null, null);
        tag.process();
        SingletonBeanProvider provider = (SingletonBeanProvider) getProvider(null);
        ChainedInvocation inv = (ChainedInvocation) provider
                .getBeanInitializer();
        assertEquals("Wrong number of invocations", 2, inv.size());
    }

    /**
     * Tests the behavior when the bean is defined as a direct value and an
     * initializer is provided.
     */
    @Test
    public void testProcessValueAndInitializer() throws JellyTagException
    {
        tag.setValue(VALUE);
        tag.processBeforeBody();
        InvocationData.get(context).addInvokable(
                new MethodInvocation("initialize", null), null, null);
        tag.process();
        SingletonBeanProvider provider = (SingletonBeanProvider) getProvider(null);
        ConstantBeanProvider creator = (ConstantBeanProvider) provider
                .getBeanCreator();
        assertEquals("Wrong value of creator", VALUE, creator.getBean());
        ChainedInvocation inv = (ChainedInvocation) provider
                .getBeanInitializer();
        assertEquals("Wrong number of invocations", 1, inv.size());
    }

    /**
     * Tests whether the resultVar is evaluated correctly.
     */
    @Test
    public void testProcessResultVar() throws JellyTagException
    {
        DependencyProvider depProvider =
                EasyMock.createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        String varName = "result";
        tag.setResultVar(varName);
        tag.setBeanClass(BEAN_CLASS);
        tag.processBeforeBody();
        tag.getInitializerScript().addInvokable(
                HelperInvocations.IDENTITY_INVOCATION);
        tag.process();
        SingletonBeanProvider provider =
                (SingletonBeanProvider) getProvider(null);
        BeanProvider beanCreator = provider.getBeanCreator();
        assertNull("Got a bean", beanCreator.getBean(depProvider));
        assertEquals("Wrong bean class", BEAN_CLASS,
                beanCreator.getBeanClass(depProvider));
        String s = beanCreator.toString();
        assertTrue("Class description not found: " + s,
                s.indexOf(BEAN_CLASS.getName()) >= 0);
        ChainedInvocation inv =
                (ChainedInvocation) provider.getBeanInitializer();
        assertEquals("Wrong result variable name", varName,
                inv.getResultVariableName());
    }

    /**
     * Tests processing of a bean tag when the bean store name is provided.
     */
    @Test
    public void testProcessWithStore() throws JellyTagException
    {
        builderData.addBeanStore(STORE_NAME, null);
        tag.setValue(VALUE);
        tag.setStore(STORE_NAME);
        tag.processBeforeBody();
        tag.process();
        assertTrue("Bean not found in store",
                getProvider(STORE_NAME) instanceof ConstantBeanProvider);
    }

    /**
     * Tests processing of a bean tag when it is nested inside a bean store tag.
     */
    @Test
    public void testProcessWithStoreTag() throws JellyTagException
    {
        builderData.addBeanStore(STORE_NAME, null);
        BeanStoreTag storeTag = new BeanStoreTag();
        storeTag.setContext(context);
        storeTag.setName(STORE_NAME);
        tag.setValue(VALUE);
        tag.setBeanStoreTag(storeTag);
        tag.processBeforeBody();
        tag.process();
        assertTrue("Bean not found in store",
                getProvider(STORE_NAME) instanceof ConstantBeanProvider);
    }

    /**
     * Tests processing of a bean tag that is nested inside a bean store tag and
     * has then store attribute set. In this case the attribute should take
     * precedence.
     */
    @Test
    public void testProcessWithStoreAndTag() throws JellyTagException
    {
        builderData.addBeanStore(STORE_NAME, null);
        BeanStoreTag storeTag = new BeanStoreTag();
        storeTag.setContext(context);
        storeTag.setName("a different store name");
        tag.setValue(VALUE);
        tag.setBeanStoreTag(storeTag);
        tag.setStore(STORE_NAME);
        tag.processBeforeBody();
        tag.process();
        assertTrue("Bean not found in store",
                getProvider(STORE_NAME) instanceof ConstantBeanProvider);
    }

    /**
     * Tests processing of a tag with a missing name attribute. This should
     * cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testProcessNoName() throws JellyTagException
    {
        tag.setName(null);
        tag.processBeforeBody();
    }

    /**
     * Tests a bean tag, for which no creator is specified. This should cause an
     * exception.
     */
    @Test(expected = JellyTagException.class)
    public void testProcessNoCreator() throws JellyTagException
    {
        tag.processBeforeBody();
        tag.process();
    }

    /**
     * Tries setting multiple creators. This should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testSetBeanCreatorMultiple() throws JellyTagException
    {
        tag.setBeanCreator(ConstantBeanProvider.getInstance(VALUE));
        tag.setBeanCreator(ConstantBeanProvider.getInstance("another bean"));
    }

    /**
     * Tests an anonymous bean declaration.
     */
    @Test
    public void testIsAnonymous() throws JellyTagException
    {
        tag.setParent(new SetPropertyTag());
        tag.setName(null);
        tag.processBeforeBody();
        assertTrue("Tag not anonymous", tag.isAnonymous());
        assertEquals("Wrong target dependency", tag.getParent(), tag
                .getTargetDependency());
    }

    /**
     * Tests that the anonymous attribute is set to false if the tag has a name.
     */
    @Test
    public void testIsAnonymousWithName() throws JellyTagException
    {
        tag.setParent(new SetPropertyTag());
        tag.processBeforeBody();
        assertFalse("Tag not anonymous", tag.isAnonymous());
    }

    /**
     * Tests a tag without name that is nested inside a dependency tag, which
     * already has a reference name set. This is not allowed because the bean
     * tag cannot define the reference for the dependency tag.
     */
    @Test
    public void testIsAnonymousRefNameSet()
    {
        SetPropertyTag parent = new SetPropertyTag();
        parent.setRefName("AReference");
        tag.setParent(parent);
        tag.setName(null);
        try
        {
            tag.processBeforeBody();
            fail("Could create anonymous bean for defined parent tag!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tests processing an anonymous bean declaration.
     */
    @Test
    public void testProcessAnonymous() throws JellyTagException
    {
        SetPropertyTag parent = new SetPropertyTag();
        tag.setParent(parent);
        tag.setName(null);
        tag.setValue(VALUE);
        tag.processBeforeBody();
        tag.process();
        assertNotNull("RefName not set on parent", parent.getRefName());
        BeanProvider provider = getStore(null).getBeanProvider(
                parent.getRefName());
        assertEquals("Wrong value in provider", VALUE, provider.getBean(null));
    }

    /**
     * Tests whether the shutdownMethod attribute is evaluated.
     */
    @Test
    public void testSetShutdownMethod() throws JellyTagException
    {
        tag.setBeanClass(BEAN_CLASS);
        final String shutdownMethod = "shutdown";
        tag.setShutdownMethod(shutdownMethod);
        tag.processBeforeBody();
        tag.process();
        SingletonBeanProvider provider = (SingletonBeanProvider) getProvider(null);
        MethodInvocation inv = (MethodInvocation) provider.getShutdownHandler();
        assertEquals("Wrong method name", shutdownMethod, inv.getMethodName());
        assertEquals("Got parameter types", 0, inv.getParameterTypes().length);
        assertTrue("Got parameters", inv.getParameterDependencies().isEmpty());
    }

    /**
     * Tests whether a shutdown handler is reset after it is queried.
     */
    @Test
    public void testConsumeShutdownHandler() throws JellyTagException
    {
        tag.setBeanClass(BEAN_CLASS);
        tag.setShutdownMethod(STORE_NAME);
        tag.processBeforeBody();
        assertTrue("Wrong shutdown handler",
                tag.consumeShutdownHandler() instanceof MethodInvocation);
        for (int i = 0; i < 10; i++)
        {
            assertNull("Shutdown handler not reset", tag
                    .consumeShutdownHandler());
        }
    }

    /**
     * Tests setting a shutdown method when the bean provider does not support
     * one. This should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testSetShutdownMethodUnsupported() throws JellyTagException
    {
        tag.setBeanClass(BEAN_CLASS);
        tag.setShutdownMethod(STORE_NAME);
        tag.setSingleton(false);
        tag.processBeforeBody();
        tag.process();
    }

    /**
     * Tests setting different shutdown handlers. This should cause an
     * exception.
     */
    @Test(expected = JellyTagException.class)
    public void testSetShutdownHandlerMultipleTimes() throws JellyTagException
    {
        tag.setShutdownHandler(HelperInvocations.NULL_INVOCATION);
        tag.setShutdownHandler(EasyMock.createNiceMock(Invokable.class));
    }

    /**
     * Tests whether a shutdown handler can be reset.
     */
    @Test
    public void testSetShutdownHandlerReset() throws JellyTagException
    {
        tag.setShutdownHandler(HelperInvocations.NULL_INVOCATION);
        assertEquals("Wrong shutdown handler set",
                HelperInvocations.NULL_INVOCATION, tag.getShutdownHandler());
        tag.setShutdownHandler(null);
        assertNull("Handler not reset (1)", tag.consumeShutdownHandler());
        tag.setShutdownHandler(null);
        assertNull("Handler not reset (2)", tag.getShutdownHandler());
        Invokable inv = EasyMock.createMock(Invokable.class);
        EasyMock.replay(inv);
        tag.setShutdownHandler(inv);
        assertEquals("Wrong shutdown handler (2)", inv, tag
                .getShutdownHandler());
        EasyMock.verify(inv);
    }
}

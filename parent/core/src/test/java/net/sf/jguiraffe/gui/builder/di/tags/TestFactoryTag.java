/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.ConstructorInvocation;
import net.sf.jguiraffe.di.impl.MethodInvocation;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.di.impl.providers.MethodInvocationBeanProvider;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;

import junit.framework.TestCase;

/**
 * Test class for FactoryTag.
 *
 * @author Oliver Heger
 * @version $Id: TestFactoryTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFactoryTag extends TestCase
{
    /** Constant for the test class to be used. */
    private static final Class<?> TEST_CLASS = ReflectionTestClass.class;

    /** Constant for the class description of the test class. */
    private static final ClassDescription TEST_CLASS_DESC = ClassDescription
            .getInstance(TEST_CLASS);

    /** Constant for the method invocation to be used. */
    private static final MethodInvocation INVOCATION = new MethodInvocation(
            "initialize", null, ConstantBeanProvider.getInstance("test"),
            ConstantBeanProvider.getInstance(42));

    /** Stores the current Jelly context. */
    private JellyContext context;

    /** Stores the tag to be tested. */
    private FactoryTag tag;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        context = new JellyContext();
        tag = new FactoryTag();
        tag.setContext(context);
    }

    /**
     * Creates a bean tag and partly initializes it. The new bean tag is also
     * set as enclosing bean tag of the test tag.
     *
     * @return the new bean tag
     */
    private BeanTag setUpBeanTag()
    {
        BeanTag beanTag = new BeanTag();
        try
        {
            beanTag.setContext(context);
        }
        catch (JellyTagException e)
        {
            // should normally not happen
            fail("Could not init bean tag: " + e);
        }
        tag.setBeanTag(beanTag);
        return beanTag;
    }

    /**
     * Convenience method for processing the tag before its body gets evaluated.
     * This method initializes a bean tag and sets it as the enclosing tag. Then
     * it calls the test tag's processBeforeBody() method.
     *
     * @return the bean tag
     * @throws JellyTagException if an error occurs
     */
    private BeanTag preProcess() throws JellyTagException
    {
        BeanTag beanTag = setUpBeanTag();
        beanTag.setBeanClassDesc(TEST_CLASS_DESC);
        tag.processBeforeBody();
        return beanTag;
    }

    /**
     * Tests the processBeforeBody() method when there is no enclosing bean tag.
     * This should cause an exception.
     */
    public void testProcessBeforeBodyNoBeanTag()
    {
        try
        {
            tag.processBeforeBody();
            fail("Could process tag without a bean tag!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tests a successful processing of the FactoryTag when a dependency is
     * provided.
     */
    public void testProcessWithDependency() throws JellyTagException
    {
        BeanTag beanTag = setUpBeanTag();
        beanTag.setBeanClassDesc(TEST_CLASS_DESC);
        ReflectionTestClass obj = new ReflectionTestClass();
        tag.setValue(obj);
        tag.processBeforeBody();
        InvocationData.get(context).addInvokable(INVOCATION, null, null);
        tag.process();
        MethodInvocationBeanProvider provider = (MethodInvocationBeanProvider) beanTag
                .getBeanCreator();
        assertEquals("Wrong target bean class", TEST_CLASS_DESC, provider
                .getBeanClassDescription());
        assertEquals("Wrong method invocation", INVOCATION, provider
                .getInvocation());
        ConstantBeanProvider dep = (ConstantBeanProvider) provider
                .getTargetDependency();
        assertSame("Wrong target bean", obj, dep.getBean());
    }

    /**
     * Tests a successful processing of the FactoryTag when no dependency is
     * provided.
     */
    public void testProcessWithoutDependency() throws JellyTagException
    {
        BeanTag beanTag = preProcess();
        InvocationData.get(context).addInvokable(INVOCATION, null, null);
        tag.process();
        MethodInvocationBeanProvider provider = (MethodInvocationBeanProvider) beanTag
                .getBeanCreator();
        assertEquals("Wrong method invocation", INVOCATION, provider
                .getInvocation());
        assertNull("A target bean was set", provider.getTargetDependency());
    }

    /**
     * Tests processing when no bean class was specified at the enclosing bean
     * tag. The bean class is required for fully initialization of the method
     * invocation.
     */
    public void testProcessNoBeanClass() throws JellyTagException
    {
        setUpBeanTag();
        tag.processBeforeBody();
        try
        {
            InvocationData.get(context).addInvokable(INVOCATION, null, null);
            fail("Could process tag without bean class!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tests processing when no invocation is set in the tag's body. This should
     * cause an exception.
     */
    public void testProcessNoInvocation() throws JellyTagException
    {
        preProcess();
        try
        {
            tag.process();
            fail("Could process tag with empty body!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tests processing when an invalid invocation is set.
     */
    public void testProcessWrongInvocation() throws JellyTagException
    {
        preProcess();
        try
        {
            InvocationData.get(context).addInvokable(
                    new ConstructorInvocation(TEST_CLASS_DESC, null), null,
                    null);
            fail("Could add Invokable of wrong type!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tests processing when multiple Invokable objects are set. Only a single
     * one is allowed.
     */
    public void testProcessMultipleInvocations() throws JellyTagException
    {
        preProcess();
        InvocationData.get(context).addInvokable(INVOCATION, null, null);
        try
        {
            InvocationData.get(context).addInvokable(INVOCATION, null, null);
            fail("Could add multiple invocations!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }
}

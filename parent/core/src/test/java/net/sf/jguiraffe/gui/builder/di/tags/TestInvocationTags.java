/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.ChainedInvocation;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.ConstructorInvocation;
import net.sf.jguiraffe.di.impl.Invocation;
import net.sf.jguiraffe.di.impl.Invokable;
import net.sf.jguiraffe.di.impl.MethodInvocation;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * A test class for the tags creating invocation objects and related helper
 * tags.
 *
 * @author Oliver Heger
 * @version $Id: TestInvocationTags.java 207 2012-02-09 07:30:13Z oheger $
 */
public class TestInvocationTags
{
    /** An array with the parameter classes. */
    private static final Class<?>[] PARAM_TYPES =
    { String.class, Integer.TYPE };

    /** An array with the parameter values. */
    private static final Object[] PARAM_VALUES =
    { "my test string", 42 };

    /** Constant for the target class to be used. */
    private static Class<?> TARGET_CLASS = ReflectionTestClass.class;

    /** Constant for the name of the test method. */
    private static final String METHOD = "initialize";

    /** Constant for the name of the class loader. */
    private static final String LOADER = "myClassLoader";

    /** The current Jelly context. */
    private JellyContext context;

    @Before
    public void setUp() throws Exception
    {
        context = new JellyContext();
    }

    /**
     * A helper method for creating a Jelly tag. This method creates a new
     * instance of the specified tag handler class and initializes it with the
     * current Jelly context.
     *
     * @param tagClass the tag handler class
     * @return the newly created instance
     */
    private <T extends TagSupport> T createTag(Class<T> tagClass)
    {
        T result =
                new InvocationHelper().invokeConstructor(tagClass, null, null);
        try
        {
            result.setContext(context);
        }
        catch (JellyTagException jtex)
        {
            fail("Could not set context: " + jtex);
        }
        return result;
    }

    /**
     * Initializes an InvocationData object with the test InvokableSupport.
     *
     * @return the InvokableSupport
     */
    private InvocationTestInvokableSupport setUpInvocationData()
    {
        InvocationData idata = InvocationData.get(context);
        InvocationTestInvokableSupport support = new InvocationTestInvokableSupport();
        idata.registerInvokableSupport(support);
        return support;
    }

    /**
     * Sets the parameters of an invocation.
     *
     * @param tag the tag to be initialized
     * @param withTypes a flag whether the parameter type should also be set
     * @throws JellyTagException if an error occurs
     */
    private void initParameters(InvocationTag tag, boolean withTypes)
            throws JellyTagException
    {
        for (int i = 0; i < PARAM_TYPES.length; i++)
        {
            ParameterTag paramTag = createTag(ParameterTag.class);
            paramTag.setValue(PARAM_VALUES[i]);
            if (withTypes)
            {
                if (i % 2 == 0)
                {
                    paramTag.setParameterClass(PARAM_TYPES[i]);
                }
                else
                {
                    paramTag.setParameterClassName(PARAM_TYPES[i].getName());
                    paramTag.setParameterClassLoader(LOADER);
                }
            }
            paramTag.initInvocationTag(tag);
            paramTag.process();
        }
    }

    /**
     * Checks some properties of the passed in invocation object.
     *
     * @param inv the object to check
     * @param withTypes a flag whether the types of the parameters are to be
     * checked
     */
    private void checkInvocation(Invocation inv, boolean withTypes)
    {
        ClassDescription[] cds = inv.getParameterTypes();
        assertEquals("Wrong number of parameter types", PARAM_TYPES.length,
                cds.length);
        for (int i = 0; i < cds.length; i++)
        {
            if (withTypes)
            {
                assertEquals("Wrong parameter type " + i, PARAM_TYPES[i]
                        .getName(), cds[i].getTargetClassName());
            }
            else
            {
                assertNull("Parameter type is set " + i, cds[i]);
            }
        }

        List<Dependency> deps = inv.getParameterDependencies();
        assertEquals("Wrong number of dependencies", PARAM_VALUES.length, deps
                .size());
        for (int i = 0; i < PARAM_VALUES.length; i++)
        {
            ConstantBeanProvider bp = (ConstantBeanProvider) deps.get(i);
            assertEquals("Wrong parameter value " + i, PARAM_VALUES[i], bp
                    .getBean());
        }
    }

    /**
     * Tries to execute a parameter tag that is not nested inside an invocation
     * tag. This is not allowed.
     */
    @Test(expected = JellyTagException.class)
    public void testParameterTagWithoutInvocation() throws JellyTagException
    {
        ParameterTag paramTag = createTag(ParameterTag.class);
        paramTag.setValue(PARAM_VALUES[0]);
        paramTag.setValueClassName(PARAM_TYPES[0].getName());
        paramTag.process();
    }

    /**
     * Tests a valid MethodInvocationTag.
     */
    @Test
    public void testMethodInvocation() throws JellyTagException
    {
        checkMethodInvocation(true, false);
    }

    /**
     * Tests an invocation when no parameter types are specified.
     */
    @Test
    public void testMethodInvocationNoParamTypes() throws JellyTagException
    {
        checkMethodInvocation(false, false);
    }

    /**
     * Tests a static method invocation.
     */
    @Test
    public void testMethodInvocationStatic() throws JellyTagException
    {
        checkMethodInvocation(true, true);
    }

    /**
     * Tests a static method invocation when no parameter types are specified.
     */
    @Test
    public void testMethodInvocationStaticNoParamTypes()
            throws JellyTagException
    {
        checkMethodInvocation(false, true);
    }

    /**
     * Helper method for testing a valid method invocation tag.
     *
     * @param withTypes a flag whether parameter types should be used
     * @param isStatic the static invocation flag
     */
    private void checkMethodInvocation(boolean withTypes, boolean isStatic)
            throws JellyTagException
    {
        InvocationTestInvokableSupport support = setUpInvocationData();
        MethodInvocationTag tag = createTag(MethodInvocationTag.class);
        tag.setMethod(METHOD);
        initParameters(tag, withTypes);
        if (isStatic)
        {
            tag.setStatic(true);
            tag.setTargetClass(ReflectionTestClass.class);
        }
        tag.process();
        MethodInvocation inv = (MethodInvocation) support.getInvocation();
        assertEquals("Wrong method name", METHOD, inv.getMethodName());
        if (!isStatic)
        {
            assertNull("Found a target class", inv.getTargetClass());
        }
        assertEquals("Wrong static flag", isStatic, inv.isStaticInvocation());
        checkInvocation(inv, withTypes);
        assertNull("Got a target dependency", inv.getTargetDependency());
    }

    /**
     * Tests a MethodInvocationTag when no method name is specified. The method
     * name is mandatory.
     */
    @Test(expected = JellyTagException.class)
    public void testMethodInvocationNoMethod() throws JellyTagException
    {
        MethodInvocationTag tag = createTag(MethodInvocationTag.class);
        initParameters(tag, true);
        tag.process();
    }

    /**
     * Tests an invocation tag without an enclosing InvokableSupport tag. This
     * should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testInvocationNoSupport() throws JellyTagException
    {
        MethodInvocationTag tag = createTag(MethodInvocationTag.class);
        tag.setMethod(METHOD);
        tag.process();
    }

    /**
     * Tests an invocation tag that does not have any parameters.
     */
    @Test
    public void testInvocationNoParams() throws JellyTagException
    {
        MethodInvocationTag tag = createTag(MethodInvocationTag.class);
        tag.setMethod(METHOD);
        InvocationTestInvokableSupport support = setUpInvocationData();
        tag.process();
        MethodInvocation inv = (MethodInvocation) support.getInvocation();
        assertEquals("Found parameter types", 0, inv.getParameterTypes().length);
        assertTrue("Found parameter values", inv.getParameterDependencies()
                .isEmpty());
    }

    /**
     * Tests a method invocation tag with specified source and result attributes
     * if no chain is in the context. This should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testMethodInvocationSourceNoChain() throws JellyTagException
    {
        MethodInvocationTag tag = createTag(MethodInvocationTag.class);
        tag.setMethod(METHOD);
        initParameters(tag, true);
        setUpInvocationData();
        tag.setSource("mySource");
        tag.setResult("myResult");
        tag.process();
    }

    /**
     * Tests an invocation with specified source and result attributes.
     */
    @Test
    public void testMethodInvocationSourceWithChain() throws JellyTagException
    {
        MethodInvocationTag tag = createTag(MethodInvocationTag.class);
        tag.setMethod(METHOD);
        initParameters(tag, true);
        InvocationData idata = InvocationData.get(context);
        ChainedInvocation chain = new ChainedInvocation();
        idata.registerInvokableSupport(chain);
        tag.setSource("mySource");
        tag.setResult("myResult");
        tag.process();
        assertEquals("Not added to chain", 1, chain.size());
    }

    /**
     * Tests whether a target dependency is correctly handled.
     */
    @Test
    public void testMethodInvocationTargetDependency() throws JellyTagException
    {
        Dependency target = EasyMock.createMock(Dependency.class);
        EasyMock.replay(target);
        InvocationTestInvokableSupport support = setUpInvocationData();
        MethodInvocationTag tag = createTag(MethodInvocationTag.class);
        tag.setMethod(METHOD);
        initParameters(tag, true);
        tag.setTargetDependency(target);
        tag.process();
        MethodInvocation inv = (MethodInvocation) support.getInvocation();
        assertSame("Wrong target dependency", target, inv.getTargetDependency());
    }

    /**
     * Tests a constructor invocation when the target is directly specified.
     */
    @Test
    public void testConstructorInvocationClass() throws JellyTagException
    {
        ConstructorInvocation cinv = checkConstructorInvocation(true, false);
        ClassDescription target = cinv.getTargetClass();
        ClassLoaderProvider clp = EasyMock
                .createMock(ClassLoaderProvider.class);
        EasyMock.replay(clp);
        assertEquals("Wrong target class", TARGET_CLASS, target
                .getTargetClass(clp));
        EasyMock.verify(clp);
    }

    /**
     * Tests a constructor invocation when the target class is specified by
     * name.
     */
    @Test
    public void testConstructorInvocationClassName() throws JellyTagException
    {
        ConstructorInvocation cinv = checkConstructorInvocation(false, true);
        ClassDescription target = cinv.getTargetClass();
        ClassLoaderProvider clp = EasyMock
                .createMock(ClassLoaderProvider.class);
        clp.loadClass(TARGET_CLASS.getName(), LOADER);
        EasyMock.expectLastCall().andReturn(TARGET_CLASS);
        EasyMock.replay(clp);
        assertEquals("Wrong target class", TARGET_CLASS, target
                .getTargetClass(clp));
        EasyMock.verify(clp);
    }

    /**
     * Checks a valid ConstructorInvocationTag.
     *
     * @param withTypes a flag whether parameter types should be defined
     * @param byName a flag whether the name of the target class is to be set by
     * name
     * @return the created invocation
     * @throws JellyTagException if an error occurs
     */
    private ConstructorInvocation checkConstructorInvocation(boolean withTypes,
            boolean byName) throws JellyTagException
    {
        ConstructorInvocationTag tag = createTag(ConstructorInvocationTag.class);
        if (byName)
        {
            tag.setTargetClassName(TARGET_CLASS.getName());
            tag.setTargetClassLoader(LOADER);
        }
        else
        {
            tag.setTargetClass(TARGET_CLASS);
        }
        initParameters(tag, withTypes);
        InvocationTestInvokableSupport support = setUpInvocationData();
        tag.process();
        ConstructorInvocation inv = (ConstructorInvocation) support
                .getInvocation();
        assertEquals("Wrong target class name", TARGET_CLASS.getName(), inv
                .getTargetClass().getTargetClassName());
        checkInvocation(inv, withTypes);
        return inv;
    }

    /**
     * Tests a ConstructorInvocationTag when no target class is specified. This
     * should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testConstructorInvocationNoTargetClass()
            throws JellyTagException
    {
        ConstructorInvocationTag tag = createTag(ConstructorInvocationTag.class);
        initParameters(tag, true);
        setUpInvocationData();
        tag.process();
    }

    /**
     * A test implementation of the InvokableSupport interface used for
     * obtaining the newly created invocation object.
     */
    private static class InvocationTestInvokableSupport implements
            InvokableSupport
    {
        /** Stores the invocation object. */
        Invocation invocation;

        /**
         * Stores the passed in invokable object in an internal member.
         */
        public void addInvokable(Invokable inv) throws JellyTagException
        {
            assertNull("Too often called", invocation);
            invocation = (Invocation) inv;
        }

        /**
         * Returns the invocation object. Fails if none has been set.
         *
         * @return the invocation
         */
        public Invocation getInvocation()
        {
            assertNotNull("No invocation was set", invocation);
            return invocation;
        }
    }
}

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
package net.sf.jguiraffe.di.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * An abstract base class for testing {@code Invocation} objects. This
 * class defines some tests for the basic functionality provided by the
 * {@code Invocation} base class. These tests are run on concrete
 * implementations. Some useful constants are also provided.
 *
 * @author Oliver Heger
 * @version $Id: AbstractInvocationTest.java 207 2012-02-09 07:30:13Z oheger $
 */
public abstract class AbstractInvocationTest
{
    /** An array with the parameter types for the invocation. */
    protected static final ClassDescription[] PARAM_TYPES =
    { ClassDescription.getInstance(String.class),
            ClassDescription.getInstance(Integer.TYPE) };

    /** An array with the current parameter values. */
    protected static final Object[] PARAM_VALUES =
    { "ATestString", 42 };

    /** Constant for the class description of the test class. */
    protected static final ClassDescription TARGET_CLASS = ClassDescription
            .getInstance(ReflectionTestClass.class);

    /** An array with the parameter dependencies. */
    protected Dependency[] paramDeps;

    /**
     * Sets up the test environment. This implementation creates the array with
     * the dependencies.
     */
    @Before
    public void setUp() throws Exception
    {
        paramDeps = new Dependency[PARAM_VALUES.length];
        for (int i = 0; i < PARAM_VALUES.length; i++)
        {
            paramDeps[i] = ConstantBeanProvider.getInstance(PARAM_VALUES[i]);
        }
    }

    /**
     * Creates a mock object for the dependency provider and initializes it for
     * the given parameter dependencies. The mock object is initialized to
     * expect calls for querying the values of the dependencies and to return
     * the invocation helper.
     *
     * @param parameters an array with the parameter dependencies
     * @param replay a flag whether the provider mock should be replayed
     * @return the initialized mock object
     */
    protected DependencyProvider setUpDependencyProvider(
            Dependency[] parameters, boolean replay)
    {
        DependencyProvider mock = EasyMock.createMock(DependencyProvider.class);
        for (int i = 0; i < paramDeps.length; i++)
        {
            EasyMock.expect(mock.getDependentBean(parameters[i])).andReturn(
                    PARAM_VALUES[i]);
        }
        EasyMock.expect(mock.getInvocationHelper()).andReturn(
                new InvocationHelper());
        if (replay)
        {
            EasyMock.replay(mock);
        }
        return mock;
    }

    /**
     * Creates a mock object for the dependency provider, initializes it for the
     * given parameter dependencies, and replays it.
     *
     * @param parameters an array with the parameter dependencies
     * @return the initialized mock object
     */
    protected DependencyProvider setUpDependencyProvider(Dependency[] parameters)
    {
        return setUpDependencyProvider(parameters, true);
    }

    /**
     * Creates a mock object for the dependency provider and initializes it with
     * default parameters. Delegates to the method with the same name and passes
     * the default parameter dependencies.
     *
     * @return the initialized mock object
     */
    protected DependencyProvider setUpDependencyProvider()
    {
        return setUpDependencyProvider(paramDeps);
    }

    /**
     * Returns an array with parameter types that contains some null elements.
     * This can be used for testing whether a partly known method signature can
     * be resolved.
     *
     * @return the array with parameter types
     */
    protected static ClassDescription[] getPartlyKnowParameterTypes()
    {
        ClassDescription[] types = new ClassDescription[PARAM_TYPES.length];
        System.arraycopy(PARAM_TYPES, 0, types, 0, PARAM_TYPES.length);
        types[1] = null;
        return types;
    }

    /**
     * Creates a concrete {@code Invocation} object that is initialized
     * with the passed in parameter data.
     *
     * @param targetClass the target class of the invocation
     * @param types an array with the parameter types
     * @param values the dependencies for the parameter values
     * @return the object to be tested
     */
    protected abstract Invocation createInvocation(
            ClassDescription targetClass, ClassDescription[] types,
            Dependency... values);

    /**
     * Tests querying the parameter types when all type information is
     * available.
     */
    @Test
    public void testGetParameterTypesKnown()
    {
        Invocation inv = createInvocation(TARGET_CLASS, PARAM_TYPES, paramDeps);
        assertTrue("Wrong parameter types returned",
                Arrays.equals(PARAM_TYPES, inv.getParameterTypes()));
    }

    /**
     * Tests querying the parameter types when no type information is available
     * and null was passed to the constructor.
     */
    @Test
    public void testGetParameterTypesUnknown()
    {
        Invocation inv = createInvocation(TARGET_CLASS, null, paramDeps);
        ClassDescription[] types = inv.getParameterTypes();
        assertNotNull("Type info is null", types);
        assertEquals("Wrong length of type info", PARAM_TYPES.length,
                types.length);
        for (int i = 0; i < types.length; i++)
        {
            assertNull("Type info is not null " + i, types[i]);
        }
    }

    /**
     * Tests querying the parameter types if the invocation has no parameters.
     */
    @Test
    public void testGetParameterTypesNoArgs()
    {
        Invocation inv = createInvocation(TARGET_CLASS, null);
        assertNotNull("Type info is null", inv.getParameterTypes());
        assertEquals("Wrong length of type info", 0,
                inv.getParameterTypes().length);
    }

    /**
     * Tests that the parameter types cannot be manipulated.
     */
    @Test
    public void testGetParameterTypesModify()
    {
        Invocation inv = createInvocation(TARGET_CLASS, PARAM_TYPES, paramDeps);
        ClassDescription[] types = inv.getParameterTypes();
        ClassDescription cd = types[0];
        types[0] = null;
        ClassDescription[] types2 = inv.getParameterTypes();
        assertEquals("Description was changed", cd, types2[0]);
    }

    /**
     * Tests the constructor when an invalid number of parameter types is
     * passed. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitInvalidTypes()
    {
        createInvocation(TARGET_CLASS, new ClassDescription[1], paramDeps);
    }

    /**
     * Tests the constructor when invalid dependencies are provided. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitInvalidDeps()
    {
        createInvocation(TARGET_CLASS, null, new Dependency[1]);
    }

    /**
     * Tests querying the target class.
     */
    @Test
    public void testGetTargetClass()
    {
        Invocation inv = createInvocation(TARGET_CLASS, PARAM_TYPES, paramDeps);
        assertEquals("Wrong target class", TARGET_CLASS, inv.getTargetClass());
    }

    /**
     * Tests querying the parameter dependencies.
     */
    @Test
    public void testGetParameterDependencies()
    {
        Invocation inv = createInvocation(TARGET_CLASS, PARAM_TYPES, paramDeps);
        Iterator<Dependency> it = inv.getParameterDependencies().iterator();
        for (int i = 0; i < paramDeps.length; i++)
        {
            assertTrue("Too few elements in iteration " + i, it.hasNext());
            assertSame("Wrong dependency at " + i, paramDeps[i], it.next());
        }
        assertFalse("Too many elements in iteration", it.hasNext());
    }

    /**
     * Tests querying the parameter dependencies if there are no parameters.
     */
    @Test
    public void testGetParameterDependenciesNoArgs()
    {
        Invocation inv = createInvocation(TARGET_CLASS, null);
        assertEquals("Wrong number of dependencies", 0, inv
                .getParameterDependencies().size());
    }

    /**
     * Tests that the list of parameter dependencies cannot be modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetParameterDependenciesModify()
    {
        Invocation inv = createInvocation(TARGET_CLASS, PARAM_TYPES, paramDeps);
        Iterator<Dependency> it = inv.getParameterDependencies().iterator();
        it.next();
        it.remove();
    }

    /**
     * Tests the isTypeInfoComplete() method if the type info is complete.
     */
    @Test
    public void testIsTypeInfoCompleteFullyKnown()
    {
        Invocation inv = createInvocation(TARGET_CLASS, PARAM_TYPES, paramDeps);
        assertTrue("Type info not complete", inv.isTypeInfoComplete());
    }

    /**
     * Tests the isTypeInfoComplete() method if the type info is only partly
     * known.
     */
    @Test
    public void testIsTypeInfoCompletePartlyKnown()
    {
        ClassDescription[] types =
        { null, PARAM_TYPES[1] };
        Invocation inv = createInvocation(TARGET_CLASS, types, paramDeps);
        assertFalse("Type info complete", inv.isTypeInfoComplete());
    }

    /**
     * Tests the isTypeInfoComplete() method for a completely missing type info.
     */
    @Test
    public void testIsTypeInfoCompleteUnknown()
    {
        Invocation inv = createInvocation(TARGET_CLASS, null, paramDeps);
        assertFalse("Type info complete", inv.isTypeInfoComplete());
    }

    /**
     * Tests the isTypeInfoComplete() method if there are no parameters.
     */
    @Test
    public void testIsTypeInfoCompleteNoArgs()
    {
        Invocation inv = createInvocation(TARGET_CLASS, null);
        assertTrue("Type info incomplete for no args", inv.isTypeInfoComplete());
    }

    /**
     * Tests obtaining the resolved parameters.
     */
    @Test
    public void testGetResolvedParameters()
    {
        DependencyProvider mockDepProvider = EasyMock
                .createMock(DependencyProvider.class);
        Dependency[] deps = new Dependency[PARAM_VALUES.length];
        for (int i = 0; i < PARAM_VALUES.length; i++)
        {
            deps[i] = EasyMock.createMock(Dependency.class);
            EasyMock.expect(mockDepProvider.getDependentBean(deps[i]))
                    .andReturn(PARAM_VALUES[i]);
            EasyMock.replay(deps[i]);
        }
        EasyMock.replay(mockDepProvider);
        Invocation inv = createInvocation(TARGET_CLASS, PARAM_TYPES, deps);
        Object[] values = inv.getResolvedParameters(mockDepProvider);
        assertEquals("Wrong length of values array", deps.length, values.length);
        for (int i = 0; i < values.length; i++)
        {
            assertEquals("Wrong value of parameter " + i, PARAM_VALUES[i],
                    values[i]);
            EasyMock.verify(deps[i]);
        }
        EasyMock.verify(mockDepProvider);
    }

    /**
     * Tests obtaining the resolved parameters when there are no parameters.
     * Result should be null.
     */
    @Test
    public void testGetResolvedParametersNoArgs()
    {
        DependencyProvider mockDepProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(mockDepProvider);
        Invocation inv = createInvocation(TARGET_CLASS, null);
        assertNull("Wrong resolved parameters for no args", inv
                .getResolvedParameters(mockDepProvider));
        EasyMock.verify(mockDepProvider);
    }

    /**
     * Tests querying the parameter classes.
     */
    @Test
    public void testGetParameterClasses()
    {
        DependencyProvider mockDepProvider = EasyMock
                .createMock(DependencyProvider.class);
        mockDepProvider.loadClass(getClass().getName(), "testLoader");
        EasyMock.expectLastCall().andReturn(getClass());
        EasyMock.replay(mockDepProvider);
        ClassDescription[] descs = new ClassDescription[PARAM_TYPES.length + 1];
        System.arraycopy(PARAM_TYPES, 0, descs, 0, PARAM_TYPES.length);
        descs[descs.length - 1] = ClassDescription.getInstance(getClass()
                .getName(), "testLoader");
        Dependency[] deps = new Dependency[descs.length];
        System.arraycopy(paramDeps, 0, deps, 0, paramDeps.length);
        deps[deps.length - 1] = ConstantBeanProvider.getInstance(this);
        Invocation inv = createInvocation(TARGET_CLASS, descs, deps);
        Class<?>[] classes = inv.getParameterClasses(mockDepProvider);
        assertEquals("Wrong length of classes", descs.length, classes.length);
        for (int i = 0; i < PARAM_TYPES.length; i++)
        {
            assertEquals("Wrong parameter class at " + i, PARAM_TYPES[i]
                    .getTargetClassName(), classes[i].getName());
        }
        assertEquals("Wrong test parameter class", getClass(),
                classes[classes.length - 1]);
        EasyMock.verify(mockDepProvider);
    }

    /**
     * Tests querying the parameter classes if there are no arguments.
     */
    @Test
    public void testGetParameterClassesNoArgs()
    {
        DependencyProvider mockDepProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(mockDepProvider);
        Invocation inv = createInvocation(TARGET_CLASS, null);
        Class<?>[] classes = inv.getParameterClasses(mockDepProvider);
        assertEquals("Wrong length of parameter classes", 0, classes.length);
        EasyMock.verify(mockDepProvider);
    }

    /**
     * Tests whether a defensive copy of the array with parameter types is made.
     */
    @Test
    public void testGetParameterClassesModifyAfterCreation()
    {
        DependencyProvider mockDepProvider = EasyMock
        .createMock(DependencyProvider.class);
EasyMock.replay(mockDepProvider);
        ClassDescription[] types = new ClassDescription[PARAM_TYPES.length];
        System.arraycopy(PARAM_TYPES, 0, types, 0, types.length);
        Invocation inv = createInvocation(TARGET_CLASS, types, paramDeps);
        types[0] = ClassDescription.getInstance(getClass());
        Class<?>[] classes = inv.getParameterClasses(mockDepProvider);
        assertEquals("Wrong class", PARAM_TYPES[0].getTargetClass(mockDepProvider), classes[0]);
        EasyMock.verify(mockDepProvider);
    }

    /**
     * Tests converting the parameters to a string.
     */
    @Test
    public void testParametersToString()
    {
        Invocation inv = createInvocation(TARGET_CLASS, PARAM_TYPES, paramDeps);
        StringBuilder buf = new StringBuilder();
        inv.parametersToString(buf);
        for (Dependency d : paramDeps)
        {
            assertTrue("Dependency not found in string " + d, buf.toString()
                    .indexOf(d.toString()) >= 0);
        }
    }

    /**
     * Tests the parametersToString() method when no parameters are defined.
     */
    @Test
    public void testParametersToStringNoArgs()
    {
        Invocation inv = createInvocation(TARGET_CLASS, null);
        StringBuilder buf = new StringBuilder();
        inv.parametersToString(buf);
        assertEquals("Wrong parameter string", "()", buf.toString());
    }
}

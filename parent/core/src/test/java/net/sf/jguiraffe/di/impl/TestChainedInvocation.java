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
package net.sf.jguiraffe.di.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanInitializer;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ChainedInvocation.
 *
 * @author Oliver Heger
 * @version $Id: TestChainedInvocation.java 207 2012-02-09 07:30:13Z oheger $
 */
public class TestChainedInvocation
{
    /** Stores the invocation to be tested. */
    private ChainedInvocation invocation;

    @Before
    public void setUp() throws Exception
    {
        invocation = new ChainedInvocation();
    }

    /**
     * Tests the properties of a newly created invocation.
     */
    @Test
    public void testInit()
    {
        assertTrue("Wrong default for clear variables flag", invocation
                .isClearVariables());
        assertTrue("Variables are not empty", invocation.getVariableNames()
                .isEmpty());
        assertTrue("Dependencies are not empty", invocation
                .getParameterDependencies().isEmpty());
        assertTrue("Chain has already Invokables", invocation.getInvokables()
                .isEmpty());
        assertEquals("Wrong size of chain", 0, invocation.size());
        assertNull("Got a result variable", invocation.getResultVariableName());
    }

    /**
     * Tests setting a variable.
     */
    @Test
    public void testSetVariable()
    {
        final int count = 10;
        final String varName = "test";
        for (int i = 0; i < count; i++)
        {
            invocation.setVariable(varName + i, i);
        }
        assertEquals("Wrong number of variables", count, invocation
                .getVariableNames().size());
        for (int i = 0; i < count; i++)
        {
            assertTrue("Variable not found " + i, invocation.getVariableNames()
                    .contains(varName + i));
            assertEquals("Wrong value of variable " + i, Integer.valueOf(i),
                    invocation.getVariable(varName + i));
        }
    }

    /**
     * Tries accessing an unknown variable. This should cause an exception.
     */
    @Test(expected = InjectionException.class)
    public void testGetVariableUnknown()
    {
        invocation.getVariable("unknownVariable");
    }

    /**
     * Tests whether variables are cleared before an invocation.
     */
    @Test
    public void testSetClearVariablesTrue()
    {
        final String varName = "testVar";
        for (int i = 0; i < 10; i++)
        {
            invocation.setVariable(varName + i, i);
        }
        invocation.invoke(null, null);
        assertTrue("Variables not cleared", invocation.getVariableNames()
                .isEmpty());
    }

    /**
     * Tests whether disabling the clear variables flag has any effect.
     */
    @Test
    public void testSetClearVariablesFalse()
    {
        final String varName = "test";
        final Object varValue = 42;
        invocation.setVariable(varName, varValue);
        invocation.setClearVariables(false);
        invocation.invoke(null, null);
        assertEquals("Variable was modified", varValue, invocation
                .getVariable(varName));
    }

    /**
     * Tests querying the parameter dependencies of a chained invocation.
     */
    @Test
    public void testGetParameterDependencies()
    {
        ConstantBeanProvider[] dependencies =
        { ConstantBeanProvider.getInstance("test"),
                ConstantBeanProvider.getInstance(42),
                ConstantBeanProvider.getInstance(Boolean.TRUE),
                ConstantBeanProvider.getInstance("anotherTest") };
        final int count = 27;
        for (int i = 0, idx = 0; i < count; i++)
        {
            idx = appendInvokableDependencyMock(dependencies, idx, i);
        }
        List<Dependency> lstDeps = invocation.getParameterDependencies();
        assertEquals("Wrong number of dependencies", dependencies.length,
                lstDeps.size());
        for (Dependency d : dependencies)
        {
            assertTrue("Dependency not returned: " + d, lstDeps.contains(d));
        }
        EasyMock.verify(invocation.getInvokables().toArray());
    }

    /**
     * Creates a mock object for an Invokable that expects to be queried for its
     * dependencies. A number of dependencies from the passed in array will be
     * returned.
     *
     * @param deps the array with existing dependencies
     * @param idx the current start index in the array
     * @param no the sequence number of the Invokable
     * @return the new start index
     */
    private int appendInvokableDependencyMock(Dependency[] deps, int idx, int no)
    {
        int depCount = no % deps.length;
        List<Dependency> lst = new ArrayList<Dependency>(depCount);
        int startIdx = idx;
        for (int i = 0; i < depCount; i++)
        {
            lst.add(deps[startIdx++]);
            if (startIdx >= deps.length)
            {
                startIdx = 0;
            }
        }
        Invokable mock = EasyMock.createMock(Invokable.class);
        EasyMock.expect(mock.getParameterDependencies()).andReturn(lst);
        EasyMock.replay(mock);
        invocation.addInvokable(mock);
        return startIdx;
    }

    /**
     * Tests querying the parameter dependencies for an empty chain.
     */
    @Test
    public void testGetParameterDependenciesEmpty()
    {
        List<Dependency> lstDeps = invocation.getParameterDependencies();
        assertTrue("Dependencies not empty", lstDeps.isEmpty());
    }

    /**
     * Tests adding an invocation.
     */
    @Test
    public void testAddInvokable()
    {
        final int count = 11;
        Invokable[] invs = new Invokable[count];
        for (int i = 0; i < count; i++)
        {
            invs[i] = EasyMock.createMock(Invokable.class);
            invocation.addInvokable(invs[i]);
        }
        EasyMock.replay((Object[]) invs);
        assertEquals("Wrong size of chain", count, invocation.size());
        List<Invokable> lstInvs = invocation.getInvokables();
        assertEquals("Wrong number of invokables", count, lstInvs.size());
        for (int i = 0; i < count; i++)
        {
            assertEquals("Wrong Invokable at " + i, invs[i], lstInvs.get(i));
        }
        EasyMock.verify((Object[]) invs);
    }

    /**
     * Tries adding a null Invokable. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInvokableNull()
    {
        invocation.addInvokable(null);
    }

    /**
     * Tests requesting a chain dependency for an existing variable.
     */
    @Test
    public void testGetChainDependency()
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        BeanStore store = EasyMock.createMock(BeanStore.class);
        EasyMock.replay(depProvider, store);
        final String varName = "testVar";
        final Object varValue = "testValue";
        Dependency dep = invocation.getChainDependency(varName);
        assertNotNull("No chain dependency returned", dep);
        BeanProvider provider = dep.resolve(store, depProvider);
        assertNotNull("No bean provider returned", provider);
        invocation.setVariable(varName, varValue);
        assertEquals("Wrong bean returned by provider", varValue, provider
                .getBean(depProvider));
        EasyMock.verify(depProvider, store);
    }

    /**
     * Tests the BeanProvider methods of the provider returned by a chain
     * dependency.
     */
    @Test
    public void testGetChainDependencyBeanProvider()
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        final String varName = "testVar";
        final Object varValue = "testValue";
        invocation.setVariable(varName, varValue);
        Dependency dep = invocation.getChainDependency(varName);
        BeanProvider provider = dep.resolve(null, null);
        assertSame("Wrong bean provider", provider,
                invocation.getVariableBean(varName));
        assertEquals("Wrong bean class", varValue.getClass(), provider
                .getBeanClass(depProvider));
        assertNull("Wrong lock ID", provider.getLockID());
        provider.setLockID(42L);
        assertNull("Wrong lock ID after setLockID", provider.getLockID());
        assertTrue("Got dependencies", provider.getDependencies().isEmpty());
        assertTrue("Bean is not available", provider.isBeanAvailable());
        provider.shutdown(depProvider);
        EasyMock.verify(depProvider);
    }

    /**
     * Tries to request a chain dependency for a variable that does not exist
     * (at the time when it is tried to be resolved).
     */
    @Test(expected = InjectionException.class)
    public void testGetChainDependencyNonExistingVar()
    {
        Dependency dep = invocation.getChainDependency("nonExistingVar");
        BeanProvider provider = dep.resolve(null, null);
        provider.getBean(null);
    }

    /**
     * Tests whether chain dependencies are cached.
     */
    @Test
    public void testGetChainDependencyCached()
    {
        final String varName = "ATestVar";
        Dependency dep = invocation.getChainDependency(varName);
        assertSame("Chain dependency not cached", dep, invocation
                .getChainDependency(varName));
    }

    /**
     * Tries to obtain a chain dependency for a null variable. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetChainDependencyNull()
    {
        invocation.getChainDependency(null);
    }

    /**
     * Tests invoking an empty chain.
     */
    @Test
    public void testInvokeEmpty()
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        ReflectionTestClass target = new ReflectionTestClass("test", 1);
        assertSame("Wrong result of invocation", target, invocation.invoke(
                depProvider, target));
        assertEquals("String property was changed", "test", target
                .getStringProp());
        assertEquals("Int property was changed", 1, target.getIntProp());
        EasyMock.verify(depProvider);
    }

    /**
     * Tests executing a complex script.
     */
    @Test
    public void testInvoke()
    {
        DependencyProvider depProvider =
                EasyMock.createMock(DependencyProvider.class);
        final int oldValue = 42;
        final int newValue = 24;
        final String txt = "Modified. Old value was: ";
        ConstantBeanProvider depValue =
                ConstantBeanProvider.getInstance(newValue);
        ConstantBeanProvider depTxt = ConstantBeanProvider.getInstance(txt);
        EasyMock.expect(depProvider.getDependentBean(depValue)).andReturn(
                depValue.getBean());
        EasyMock.expect(depProvider.getDependentBean(depTxt)).andReturn(
                depTxt.getBean());
        EasyMock.expect(depProvider.getInvocationHelper())
                .andReturn(new InvocationHelper()).anyTimes();
        EasyMock.replay(depProvider);
        ReflectionTestClass target = new ReflectionTestClass();
        target.setIntProp(oldValue);

        // int oldval = target.getIntProp();
        invocation.addInvokable(new MethodInvocation("getIntProp", null),
                "oldval");
        // StringBuilder buf = new StringBuilder();
        invocation.addInvokable(new ConstructorInvocation(ClassDescription
                .getInstance(StringBuilder.class), null), "buf");
        // buf.append(txt);
        invocation.addInvokable(new MethodInvocation("append",
                new ClassDescription[]
                { ClassDescription.getInstance(String.class) }, depTxt), null,
                "buf");
        // buf.append(oldval);
        invocation.addInvokable(new MethodInvocation("append",
                new ClassDescription[]
                { ClassDescription.getInstance(Integer.TYPE) }, invocation
                        .getChainDependency("oldval")), null, "buf");
        // target.setStringProp(buf);
        invocation.addInvokable(new SetPropertyInvocation("stringProp",
                invocation.getChainDependency("buf")));
        // target.setIntProp(newValue);
        invocation.addInvokable(new SetPropertyInvocation("intProp", depValue));

        assertSame("Wrong result of invoke", target, invocation.invoke(
                new DependencyProviderWrapper(depProvider), target));
        assertEquals("Wrong value of int property", newValue, target
                .getIntProp());
        assertEquals("Wrong value of string property", txt + oldValue, target
                .getStringProp());
        EasyMock.verify(depProvider);
    }

    /**
     * Tests whether a result variable is taken into account by invoke().
     */
    @Test
    public void testInvokeWithResultVariable()
    {
        DependencyProvider depProvider =
                EasyMock.createMock(DependencyProvider.class);
        Invokable inv = EasyMock.createMock(Invokable.class);
        Object target = new Object();
        EasyMock.expect(inv.invoke(depProvider, target)).andReturn("someValue");
        EasyMock.replay(inv, depProvider);
        String varName = "myResultVariable";
        invocation.addInvokable(inv);
        invocation.setResultVariableName(varName);
        Object value = 20120204174326L;
        invocation.setVariable(varName, value);
        invocation.setClearVariables(false);
        assertEquals("Wrong invocation result", value,
                invocation.invoke(depProvider, target));
        EasyMock.verify(depProvider, inv);
    }

    /**
     * Tests the toString() implementation. We check whether the contained
     * invocations are found in the string (separated by newlines).
     */
    @Test
    public void testToString()
    {
        final int count = 8;
        MethodInvocation[] invs = new MethodInvocation[count];
        for (int i = 0; i < count; i++)
        {
            invs[i] = new MethodInvocation("method" + i, null);
            invocation.addInvokable(invs[i]);
        }
        invocation.addInvokable(new MethodInvocation("test", null),
                "testResult", "testTarget");
        String s = invocation.toString();
        for (int i = 0; i < count; i++)
        {
            assertTrue("Invocation " + i + " not found in string " + s, s
                    .indexOf(invs[i].toString() + '\n') >= 0);
        }
        assertTrue("Result variable not found in string " + s, s
                .indexOf("(result=testResult)") >= 0);
        assertTrue("Source variable not found in string " + s, s
                .indexOf("(source=testTarget)") >= 0);
    }

    /**
     * A simple wrapper for a DependencyProvider that can be passed to a
     * ChainedInvocation. Because chain dependencies are resolved during
     * execution of the chain a simple mock object cannot be used. So this class
     * treats them in a special way and otherwise delegates to the decorated
     * instance.
     */
    static class DependencyProviderWrapper implements DependencyProvider
    {
        /** Stores the wrapped provider. */
        private final DependencyProvider depProvider;

        /**
         * Creates a new instance of <code>DependencyProviderWrapper</code>
         * and sets the provider to wrap.
         *
         * @param wrappedProvider the dependency provider to wrap
         */
        public DependencyProviderWrapper(DependencyProvider wrappedProvider)
        {
            depProvider = wrappedProvider;
        }

        /**
         * Returns the dependent bean. If the passed in dependency is a chained
         * dependency, it is resolved now. Otherwise the call is delegated to
         * the wrapped provider.
         */
        public Object getDependentBean(Dependency dependency)
        {
            if (!(dependency instanceof ConstantBeanProvider))
            {
                return dependency.resolve(null, null).getBean(null);
            }
            return depProvider.getDependentBean(dependency);
        }

        /**
         * Delegates to the wrapped provider.
         */
        public Class<?> loadClass(String name, String loaderRef)
        {
            return depProvider.loadClass(name, loaderRef);
        }

        /**
         * Delegates to the wrapped provider.
         */
        public void addInitializer(BeanInitializer initializer)
        {
            depProvider.addInitializer(initializer);
        }

        /**
         * Delegates to the wrapped provider.
         */
        public boolean isBeanAvailable(Dependency dependency)
        {
            return depProvider.isBeanAvailable(dependency);
        }

        /**
         * Delegates to the wrapped provider.
         */
        public Set<String> classLoaderNames()
        {
            return depProvider.classLoaderNames();
        }

        /**
         * Delegates to the wrapped provider.
         */
        public ClassLoader getClassLoader(String name)
        {
            return depProvider.getClassLoader(name);
        }

        /**
         * Delegates to the wrapped provider.
         */
        public String getDefaultClassLoaderName()
        {
            return depProvider.getDefaultClassLoaderName();
        }

        /**
         * Delegates to the wrapped provider.
         */
        public void registerClassLoader(String name, ClassLoader loader)
        {
            depProvider.registerClassLoader(name, loader);
        }

        /**
         * Delegates to the wrapped provider.
         */
        public void setDefaultClassLoaderName(String loader)
        {
            depProvider.setDefaultClassLoaderName(loader);
        }

        /**
         * Delegates to the wrapped provider.
         */
        public void beanCreated(Object bean, BeanProvider provider)
        {
            depProvider.beanCreated(bean, provider);
        }

        /**
         * Delegates to the wrapped provider.
         */
        public void setCreationBeanContext(BeanContext context)
        {
            depProvider.setCreationBeanContext(context);
        }

        /**
         * Delegates to the wrapped provider.
         */
        public InvocationHelper getInvocationHelper()
        {
            return depProvider.getInvocationHelper();
        }
    }
}

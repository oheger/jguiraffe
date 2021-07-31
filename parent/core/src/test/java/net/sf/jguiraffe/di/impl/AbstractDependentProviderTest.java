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
package net.sf.jguiraffe.di.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.Dependency;

import org.easymock.EasyMock;
import org.junit.After;

/**
 * An abstract base class for unit tests that test dependency resolving between
 * bean providers. This class provides some basic functionality for creating
 * dependency chains using mock objects.
 *
 * @author Oliver Heger
 * @version $Id: AbstractDependentProviderTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractDependentProviderTest
{
    /** Constant with the prefix of the provider names. */
    protected static final String NAME_PREFIX = "TestProvider";

    /** Stores a list with the mock providers created during a test. */
    protected Collection<BeanProvider> mockProviders;

    /** The bean store, on which the test operates. */
    protected DefaultBeanStore store;

    /** Stores the running index of the provider names. */
    protected int index;

    public void setUp() throws Exception
    {
        store = new DefaultBeanStore();
        mockProviders = new ArrayList<BeanProvider>();
    }

    /**
     * Clears the test environment. If mock bean providers have been created,
     * they are now verified.
     */
    @After
    public void tearDown() throws Exception
    {
        EasyMock.verify(mockProviders.toArray());
    }

    /**
     * Returns the name of the test provider with the specified index.
     *
     * @param idx the index of the provider
     * @return the name for this provider
     */
    protected String getProviderName(int idx)
    {
        return NAME_PREFIX + idx;
    }

    /**
     * Creates a bean provider mock object. The mock is stored in the internal
     * list. It is not yet initialized.
     *
     * @return the mock provider object
     */
    protected BeanProvider createProviderMock()
    {
        BeanProvider mock = EasyMock.createMock(BeanProvider.class);
        mockProviders.add(mock);
        return mock;
    }

    /**
     * Creates a bean provider mock object and performs initialization. This is
     * a short cut for <code>createProviderMock(noLock, 1, dependencies)</code>.
     *
     * @param noLock flag whether the getLock() method should be initialized
     * @param dependencies the names of the dependent providers
     * @return the mock provider object
     */
    protected BeanProvider createProviderMock(boolean noLock,
            String... dependencies)
    {
        return createProviderMock(noLock, 1, dependencies);
    }

    /**
     * Creates a bean provider mock object and performs initialization for the
     * given times of expected invocations. The specified dependencies are set.
     * If the <code>noLock</code> argument is <b>true</b>, the getLock()
     * method will be initialized to return <b>null</b>.
     *
     * @param noLock flag whether the getLock() method should be initialized
     * @param invocationCount the expected number of invocations
     * @param dependencies the names of the dependent providers
     * @return the mock provider object
     */
    protected BeanProvider createProviderMock(boolean noLock,
            int invocationCount, String... dependencies)
    {
        BeanProvider mock = createProviderMock();
        Set<Dependency> deps;
        if (dependencies.length < 1)
        {
            deps = null;
        }
        else
        {
            deps = new HashSet<Dependency>();
            for (String depName : dependencies)
            {
                deps.add(NameDependency.getInstance(depName));
            }
        }
        EasyMock.expect(mock.getDependencies()).andReturn(deps).times(0,
                invocationCount);

        if (noLock)
        {
            EasyMock.expect(mock.getLockID()).andReturn(null).times(0,
                    invocationCount);
        }

        return mock;
    }

    /**
     * Calls replay() on all created provider mock objects.
     */
    protected void replayProviders()
    {
        EasyMock.replay(mockProviders.toArray());
    }

    /**
     * Increments the index for the name generation of mock providers and
     * returns the current index.
     *
     * @return the current index
     */
    protected int nextIndex()
    {
        return ++index;
    }

    /**
     * Adds the provider to the bean store under a default name. The index of
     * this name is returned.
     *
     * @param provider the provider to add
     * @return the name of this provider
     */
    protected int addProvider(BeanProvider provider)
    {
        int idx = nextIndex();
        String name = getProviderName(idx);
        store.addBeanProvider(name, provider);
        return idx;
    }

    /**
     * Creates a chain of provider objects where the provider i depends on
     * provider i + 1. This is a short form of
     * <code>createProviderChain(count, cycle, 1)</code>.
     *
     * @param count the number of providers in the chain
     * @param cycle a flag if a cycle is to be created
     * @return the index of the first provider in the chain
     */
    protected int createProviderChain(int count, boolean cycle)
    {
        return createProviderChain(count, cycle, 1);
    }

    /**
     * Creates a chain of provider objects where the provider i depends on
     * provider i + 1. If desired, the chain can be cyclic, i.e. provider n
     * depends on provider 1. The number of expected invocations of the mock
     * objects can also be specified.
     *
     * @param count the number of providers in the chain
     * @param cycle a flag if a cycle is to be created
     * @param invocationCount the number of expected invocations
     * @return the index of the first provider in the chain
     */
    protected int createProviderChain(int count, boolean cycle,
            int invocationCount)
    {
        int firstIndex = index + 1;

        for (int i = 0; i < count - 1; i++)
        {
            String depName = getProviderName(index + 2);
            addProvider(createProviderMock(true, invocationCount, depName));
        }

        if (cycle)
        {
            addProvider(createProviderMock(true, invocationCount,
                    getProviderName(firstIndex)));
        }
        else
        {
            addProvider(createProviderMock(true, invocationCount));
        }

        return firstIndex;
    }

    /**
     * Returns a collection with the mock providers in the specified index
     * range.
     *
     * @param fromIndex the from index
     * @param toIndex the to index (inclusive)
     * @return the collection with the specified providers
     */
    protected Collection<BeanProvider> getProviders(int fromIndex, int toIndex)
    {
        Collection<BeanProvider> result = new ArrayList<BeanProvider>(toIndex
                - fromIndex + 1);
        for (int idx = fromIndex; idx <= toIndex; idx++)
        {
            result.add(store.getBeanProvider(getProviderName(idx)));
        }
        return result;
    }
}

/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import net.sf.jguiraffe.di.impl.ChainedInvocation;
import net.sf.jguiraffe.di.impl.Invokable;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;

import junit.framework.TestCase;

/**
 * Test class for InvocationData.
 *
 * @author Oliver Heger
 * @version $Id: TestInvocationData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestInvocationData extends TestCase
{
    /** Stores the Jelly context used for testing. */
    private JellyContext context;

    /** Stores the data object to be tested. */
    private InvocationData data;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        context = new JellyContext();
        data = InvocationData.get(context);
    }

    /**
     * Tests whether get() returns a singleton per context.
     */
    public void testGetContextSingleton()
    {
        assertSame("Multiple instances per context", data, InvocationData
                .get(context));
        assertNotSame("Same instance in different context", data,
                InvocationData.get(new JellyContext()));
    }

    /**
     * Tries to register a null support object. This should cause an exception.
     */
    public void testRegisterInvokableSupportNull()
    {
        try
        {
            data.registerInvokableSupport((InvokableSupport) null);
            fail("Could register null support object!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tries to register a null chain object. This should cause an exception.
     */
    public void testRegisterInvokableSupportChainNull()
    {
        try
        {
            data.registerInvokableSupport((ChainedInvocation) null);
            fail("Could register null support object!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tries to add an Invokable when no support object has been registered.
     * This should cause an exception.
     */
    public void testAddInvokableSupportUnregistered()
    {
        Invokable inv = EasyMock.createMock(Invokable.class);
        try
        {
            data.addInvokable(inv, null, null);
            fail("Could add Invokable to non existing support object!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tries to add a null Invokable. This should cause an exception.
     */
    public void testAddInvokableSupportNull() throws JellyTagException
    {
        data.registerInvokableSupport(new ChainedInvocation());
        try
        {
            data.addInvokable(null, null, null);
            fail("Could add null Invokable!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests adding an Invokable object to a chain.
     */
    public void testAddInvokableToChain() throws JellyTagException
    {
        Invokable inv = EasyMock.createMock(Invokable.class);
        EasyMock.replay(inv);
        ChainedInvocation chain = new ChainedInvocation();
        data.registerInvokableSupport(chain);
        data.addInvokable(inv, "myResult", "mySource");
        assertEquals("Not added to chain", 1, chain.size());
        EasyMock.verify(inv);
    }

    /**
     * Tests adding an Invokable object to a support object.
     */
    public void testAddInvokableToSupport() throws JellyTagException
    {
        Invokable inv = EasyMock.createMock(Invokable.class);
        InvokableSupport support = EasyMock.createMock(InvokableSupport.class);
        support.addInvokable(inv);
        EasyMock.replay(inv, support);
        data.registerInvokableSupport(support);
        data.addInvokable(inv, null, null);
        EasyMock.verify(inv, support);
    }

    /**
     * Tries to add an Invokable to a support object using a result name. This
     * is not allowed in this context.
     */
    public void testAddInvokableToSupportWithResultName()
            throws JellyTagException
    {
        checkAddToSupportWithNames("myResult", null);
    }

    /**
     * Tries to add an Invokable to a support object using a source name. This
     * is not allowed in this context.
     */
    public void testAddInvokableToSupportWithSourceName()
            throws JellyTagException
    {
        checkAddToSupportWithNames(null, "myTarget");
    }

    /**
     * Helper method for testing addInvokable() when variable names are used and
     * no chain is in the current context.
     *
     * @param resultName the name of the result variable
     * @param sourceName the name of the source variable
     */
    private void checkAddToSupportWithNames(String resultName, String sourceName)
    {
        Invokable inv = EasyMock.createMock(Invokable.class);
        InvokableSupport support = EasyMock.createMock(InvokableSupport.class);
        EasyMock.replay(inv, support);
        data.registerInvokableSupport(support);
        try
        {
            data.addInvokable(inv, resultName, sourceName);
            fail("Could use a variable!");
        }
        catch (JellyTagException jtex)
        {
            EasyMock.verify(inv, support);
        }
    }

    /**
     * Tests obtaining a variable dependency.
     */
    public void testGetVariableDependency() throws JellyTagException
    {
        data.registerInvokableSupport(new ChainedInvocation());
        assertNotNull("Could not obtain variable dependency", data
                .getVariableDependency("myVar"));
    }

    /**
     * Tries to obtain a variable dependency when no chain is in the context.
     * This is not supported.
     */
    public void testGetVariableDependencyNoChain()
    {
        InvokableSupport support = EasyMock.createMock(InvokableSupport.class);
        EasyMock.replay(support);
        data.registerInvokableSupport(support);
        try
        {
            data.getVariableDependency("myVar");
            fail("Could obtain variable dependency when no chain is registered!");
        }
        catch (JellyTagException jtex)
        {
            EasyMock.verify(support);
        }
    }

    /**
     * Tries to obtain a variable dependency before anything is registered.
     */
    public void testGetVariableDependencyUnregistered()
    {
        try
        {
            data.getVariableDependency("myVar");
            fail("Could obtain variable dependency when nothing is registered!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tests registration and unregistration of support objects.
     */
    public void testUnregisterInvokableSupport() throws JellyTagException
    {
        InvokableSupport support = EasyMock.createMock(InvokableSupport.class);
        Invokable inv1 = EasyMock.createMock(Invokable.class);
        Invokable inv2 = EasyMock.createMock(Invokable.class);
        ChainedInvocation chain = new ChainedInvocation();
        support.addInvokable(inv1);
        EasyMock.replay(inv1, inv2, support);
        data.registerInvokableSupport(chain);
        data.registerInvokableSupport(support);
        data.addInvokable(inv1, null, null);
        data.unregisterInvokableSupport();
        data.addInvokable(inv2, "myResult", "mySource");
        data.unregisterInvokableSupport();
        assertEquals("Nothing added to chain", 1, chain.size());
        EasyMock.verify(inv1, inv2, support);
    }

    /**
     * Tries to unregister a non existing object. This should cause an
     * exception.
     */
    public void testUnregisterSupportEmpty()
    {
        try
        {
            data.unregisterInvokableSupport();
            fail("Could unregister non existing object!");
        }
        catch (IllegalStateException istex)
        {
            // ok
        }
    }
}

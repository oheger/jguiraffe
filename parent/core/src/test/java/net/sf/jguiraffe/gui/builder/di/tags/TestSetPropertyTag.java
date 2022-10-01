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
package net.sf.jguiraffe.gui.builder.di.tags;

import net.sf.jguiraffe.di.impl.ChainedInvocation;
import net.sf.jguiraffe.di.impl.Invokable;
import net.sf.jguiraffe.di.impl.SetPropertyInvocation;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;

import junit.framework.TestCase;

/**
 * Test class for SetPropertyTag.
 *
 * @author Oliver Heger
 * @version $Id: TestSetPropertyTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSetPropertyTag extends TestCase
{
    /** Constant for the name of the property. */
    private static final String PROP_NAME = "myProperty";

    /** Constant of the test value. */
    private static final Integer VALUE = 42;

    /** Stores the current Jelly context. */
    private JellyContext context;

    /** Stores the tag to be tested. */
    private SetPropertyTag tag;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        context = new JellyContext();
        tag = new SetPropertyTag();
        tag.setContext(context);
    }

    /**
     * Tests a tag that does not have a property name set. This should cause an
     * exception.
     */
    public void testMissingPropertyName()
    {
        tag.setValue(VALUE);
        try
        {
            tag.process();
            fail("Could process tag without a property name!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tests a valid execution of the tag.
     */
    public void testProcess() throws JellyTagException
    {
        tag.setValue(VALUE);
        tag.setProperty(PROP_NAME);
        SetPropertyInvokableSupport spis = new SetPropertyInvokableSupport();
        InvocationData.get(context).registerInvokableSupport(spis);
        tag.process();
        assertNotNull("No invocation was set", spis.propertyInvocation);
    }

    /**
     * Tests executing the tag when there is no InvokableSupport in the context.
     * This should cause an exception.
     */
    public void testProcessNoInvokableSupport()
    {
        tag.setProperty(PROP_NAME);
        tag.setValue(VALUE);
        try
        {
            tag.process();
            fail("Could process tag without an InvokableSupport");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tries setting a source attribute when no chain is in the context. This
     * should cause an exception.
     */
    public void testProcessWithSourceNoChain()
    {
        tag.setProperty(PROP_NAME);
        tag.setValue(VALUE);
        tag.setSource("mySource");
        InvocationData.get(context).registerInvokableSupport(
                new SetPropertyInvokableSupport());
        try
        {
            tag.process();
            fail("Could set source attribute without a chain!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tests processing the tag when a chain is in the context.
     */
    public void testProcessInChain() throws JellyTagException
    {
        tag.setProperty(PROP_NAME);
        tag.setValue(VALUE);
        tag.setSource("mySource");
        ChainedInvocation chain = new ChainedInvocation();
        InvocationData.get(context).registerInvokableSupport(chain);
        tag.process();
        assertEquals("Not added to chain", 1, chain.size());
    }

    /**
     * A simple InvokableSupport implementation that expects a
     * SetPropertyInvocation.
     */
    private static class SetPropertyInvokableSupport implements
            InvokableSupport
    {
        /** Stores the invocation that was passed in. */
        SetPropertyInvocation propertyInvocation;

        /**
         * Accepts the Invokable. Checks whether a SetPropertyInvocation is
         * passed in with the expected property name.
         */
        public void addInvokable(Invokable inv) throws JellyTagException
        {
            SetPropertyInvocation propInv = (SetPropertyInvocation) inv;
            assertEquals("Wrong property name", PROP_NAME, propInv
                    .getPropertyName());
            propertyInvocation = propInv;
        }
    }
}

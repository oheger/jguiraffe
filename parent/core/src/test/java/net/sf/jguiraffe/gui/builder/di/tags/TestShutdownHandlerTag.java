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
import static org.junit.Assert.fail;
import net.sf.jguiraffe.di.impl.ChainedInvocation;
import net.sf.jguiraffe.di.impl.HelperInvocations;
import net.sf.jguiraffe.di.impl.SetPropertyInvocation;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ShutdownHandlerTag}.
 *
 * @author Oliver Heger
 * @version $Id: TestShutdownHandlerTag.java 208 2012-02-11 20:57:33Z oheger $
 */
public class TestShutdownHandlerTag
{
    /** The parent tag of the test tag. */
    private BeanTag parent;

    /** The tag to be tested. */
    private ShutdownHandlerTag tag;

    @Before
    public void setUp() throws Exception
    {
        JellyContext context = new JellyContext();
        parent = new BeanTag();
        parent.setContext(context);
        tag = new ShutdownHandlerTag();
        tag.setContext(context);
        tag.setParent(parent);
    }

    /**
     * Tests a tag with a wrong parent. This should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testProcessBeforeBodyWrongParent() throws JellyTagException
    {
        tag.setParent(null);
        tag.processBeforeBody();
    }

    /**
     * Tests processing the tag and registering invokables.
     */
    @Test
    public void testProcess() throws JellyTagException
    {
        tag.processBeforeBody();
        InvocationData.get(tag.getContext()).addInvokable(
                new SetPropertyInvocation("intProp", ConstantBeanProvider
                        .getInstance(0)), null, null);
        InvocationData.get(tag.getContext()).addInvokable(
                new SetPropertyInvocation("stringProp",
                        ConstantBeanProvider.NULL), null, null);
        tag.process();
        ChainedInvocation inv = (ChainedInvocation) parent.getShutdownHandler();
        assertEquals("Wrong number of invocations", 2, inv.size());
    }

    /**
     * Tests whether the invocation script is unregistered when the tag ends.
     */
    @Test
    public void testProcessUnregisterInvokableSupport()
            throws JellyTagException
    {
        tag.processBeforeBody();
        tag.process();
        try
        {
            InvocationData.get(tag.getContext()).addInvokable(
                    HelperInvocations.NULL_INVOCATION, null, null);
            fail("Could still add invocations!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }
}

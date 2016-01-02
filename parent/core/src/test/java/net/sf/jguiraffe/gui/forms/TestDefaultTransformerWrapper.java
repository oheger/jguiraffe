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
package net.sf.jguiraffe.gui.forms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import net.sf.jguiraffe.transform.Transformer;
import net.sf.jguiraffe.transform.TransformerContext;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code DefaultTransformerWrapper}.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class TestDefaultTransformerWrapper
{
    /** A mock for a transformer. */
    private Transformer transformer;

    /** A mock for a context. */
    private TransformerContext context;

    @Before
    public void setUp() throws Exception
    {
        transformer = EasyMock.createMock(Transformer.class);
        context = EasyMock.createMock(TransformerContext.class);
    }

    /**
     * Tries to create an instance without a transformer.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoTransformer()
    {
        new DefaultTransformerWrapper(null, context);
    }

    /**
     * Tries to create an instance without a context.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoContext()
    {
        new DefaultTransformerWrapper(transformer, null);
    }

    /**
     * Tests a successful transformation.
     */
    @Test
    public void testTransform() throws Exception
    {
        final Object input = "a test input";
        final Object output = 42;
        EasyMock.expect(transformer.transform(input, context))
                .andReturn(output);
        EasyMock.replay(transformer, context);

        DefaultTransformerWrapper wrapper =
                new DefaultTransformerWrapper(transformer, context);
        assertSame("Wrong result", output, wrapper.transform(input));
        EasyMock.verify(transformer);
    }

    /**
     * Tests whether exceptions thrown by a transformation are handled.
     */
    @Test
    public void testTransformEx() throws Exception
    {
        Exception ex = new Exception();
        EasyMock.expect(transformer.transform(this, context)).andThrow(ex);
        EasyMock.replay(transformer, context);

        DefaultTransformerWrapper wrapper =
                new DefaultTransformerWrapper(transformer, context);
        try
        {
            wrapper.transform(this);
            fail("Exception not thrown!");
        }
        catch (FormRuntimeException frex)
        {
            assertEquals("Wrong cause", ex, frex.getCause());
        }
    }
}

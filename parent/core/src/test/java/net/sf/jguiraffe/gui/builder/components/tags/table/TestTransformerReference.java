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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import net.sf.jguiraffe.gui.forms.DummyWrapper;
import net.sf.jguiraffe.gui.forms.TransformerWrapper;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@code TransformerReference}.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class TestTransformerReference
{
    /**
     * Creates a mock for a transformer wrapper to be referenced.
     *
     * @return the mock wrapper
     */
    private static TransformerWrapper createWrapper()
    {
        TransformerWrapper trans =
                EasyMock.createMock(TransformerWrapper.class);
        EasyMock.replay(trans);
        return trans;
    }

    /**
     * Tests whether a transformer can be passed to the constructor.
     */
    @Test
    public void testInitWithTransformer()
    {
        TransformerWrapper trans = createWrapper();
        TransformerReference reference = new TransformerReference(trans);
        assertSame("Wrong referenced transformer", trans,
                reference.getTransformer());
    }

    /**
     * Tests whether null can be passed to the constructor.
     */
    @Test
    public void testInitNoTransformer()
    {
        TransformerReference reference = new TransformerReference(null);
        assertEquals("Wrong referenced transformer", DummyWrapper.INSTANCE,
                reference.getTransformer());
    }

    /**
     * Tests whether null can be passed to setTransformer().
     */
    @Test
    public void testSetTransformerUndefined()
    {
        TransformerWrapper trans = createWrapper();
        TransformerReference reference = new TransformerReference(trans);
        reference.setTransformer(null);
        assertSame("Wrong referenced transformer", DummyWrapper.INSTANCE,
                reference.getTransformer());
    }

    /**
     * Tests a transformation.
     */
    @Test
    public void testTransform()
    {
        final Object input = "someInput";
        final Object output = "someTransformedValue";
        TransformerWrapper trans =
                EasyMock.createMock(TransformerWrapper.class);
        EasyMock.expect(trans.transform(input)).andReturn(output);
        EasyMock.replay(trans);
        TransformerReference reference = new TransformerReference(null);

        reference.setTransformer(trans);
        assertSame("Wrong transformed result", output,
                reference.transform(input));
        EasyMock.verify(trans);
    }
}

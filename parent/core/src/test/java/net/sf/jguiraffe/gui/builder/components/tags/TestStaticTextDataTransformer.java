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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.model.StaticTextData;
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment;
import net.sf.jguiraffe.transform.TransformerContext;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code StaticTextDataTransformer}.
 *
 * @author Oliver Heger
 * @version $Id: TestStaticTextDataTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestStaticTextDataTransformer
{
    /** A mock for the transformer context. */
    private TransformerContext ctx;

    /** The transformer to be tested. */
    private StaticTextDataTransformerTestImpl transformer;

    @Before
    public void setUp() throws Exception
    {
        ctx = EasyMock.createMock(TransformerContext.class);
        transformer = new StaticTextDataTransformerTestImpl();
    }

    /**
     * Tests a newly created instance.
     */
    @Test
    public void testInit()
    {
        assertNull("Got a default text", transformer.getText());
        assertNull("Got a default icon", transformer.getIcon());
        assertNull("Got a default alignment", transformer.getAlignment());
    }

    /**
     * Tests whether null objects are handled.
     */
    @Test
    public void testTransformNull() throws Exception
    {
        EasyMock.replay(ctx);
        assertNull("Wrong result", transformer.transform(null, ctx));
        EasyMock.verify(ctx);
    }

    /**
     * Tests transform() if a static text data object is passed in.
     */
    @Test
    public void testTransformStaticTextData() throws Exception
    {
        StaticTextData data = EasyMock.createMock(StaticTextData.class);
        EasyMock.replay(ctx, data);
        assertSame("Wrong result", data, transformer.transform(data, ctx));
        EasyMock.verify(ctx, data);
    }

    /**
     * Tests populate() if there are no default properties.
     */
    @Test
    public void testPopulateNoProperties()
    {
        EasyMock.expect(ctx.properties()).andReturn(
                new HashMap<String, Object>());
        EasyMock.replay(ctx);
        StaticTextDataImpl data = new StaticTextDataImpl();
        transformer.populate(data, ctx);
        assertNull("Got a text", data.getText());
        assertNull("Got an icon", data.getIcon());
        assertEquals("No default alginment",
                new StaticTextDataImpl().getAlignment(), data.getAlignment());
        EasyMock.verify(ctx);
    }

    /**
     * Tests whether properties of the context are used for populating the data
     * object.
     */
    @Test
    public void testPopulateFromContext()
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("text", "MyText");
        props.put("icon", this);
        props.put("alignment", TextIconAlignment.RIGHT);
        transformer.setText("other Text");
        transformer.setIcon(new Object());
        transformer.setAlignment(TextIconAlignment.CENTER);
        EasyMock.expect(ctx.properties()).andReturn(
                Collections.unmodifiableMap(props));
        EasyMock.replay(ctx);
        StaticTextDataImpl data = new StaticTextDataImpl();
        transformer.populate(data, ctx);
        assertEquals("Wrong text", props.get("text"), data.getText());
        assertEquals("Wrong icon", this, data.getIcon());
        assertEquals("Wrong alignment", TextIconAlignment.RIGHT,
                data.getAlignment());
        EasyMock.verify(ctx);
    }

    /**
     * Tests whether the alignment enumeration is converted when data from the
     * context is read.
     */
    @Test
    public void testPopulateFromContextConvertEnum()
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("alignment", "right");
        EasyMock.expect(ctx.properties()).andReturn(
                Collections.unmodifiableMap(props));
        EasyMock.replay(ctx);
        StaticTextDataImpl data = new StaticTextDataImpl();
        transformer.populate(data, ctx);
        assertEquals("Wrong alignment", TextIconAlignment.RIGHT,
                data.getAlignment());
        EasyMock.verify(ctx);
    }

    /**
     * Tests whether properties of the transformer are used to populate the data
     * object.
     */
    @Test
    public void testPopulateFromProperties()
    {
        Map<String, Object> props = Collections.emptyMap();
        EasyMock.expect(ctx.properties()).andReturn(props);
        EasyMock.replay(ctx);
        transformer.setText("text");
        transformer.setIcon(this);
        transformer.setAlignment(TextIconAlignment.CENTER);
        StaticTextDataImpl data = new StaticTextDataImpl();
        transformer.populate(data, ctx);
        assertEquals("Wrong text", "text", data.getText());
        assertEquals("Wrong icon", this, data.getIcon());
        assertEquals("Wrong alignment", TextIconAlignment.CENTER,
                data.getAlignment());
        EasyMock.verify(ctx);
    }

    /**
     * Tests whether a text object is correctly handled by transform().
     */
    @Test
    public void testTransformCharSequence() throws Exception
    {
        transformer.setMockPopulate(true);
        final String text = "This is a test text!";
        StaticTextData data =
                (StaticTextData) transformer.transform(new StringBuilder(text),
                        ctx);
        assertEquals("Wrong text", text, data.getText());
        transformer.verify(data);
    }

    /**
     * Tests whether an icon object is correctly handled by transform().
     */
    @Test
    public void testTransformIcon() throws Exception
    {
        transformer.setMockPopulate(true);
        final Object icon = new Object();
        StaticTextData data = (StaticTextData) transformer.transform(icon, ctx);
        assertEquals("Wrong icon", icon, data.getIcon());
        transformer.verify(data);
    }

    /**
     * Tests whether an alignment object is correctly handled by transform().
     */
    @Test
    public void testTransformAlignment() throws Exception
    {
        transformer.setMockPopulate(true);
        StaticTextData data =
                (StaticTextData) transformer.transform(TextIconAlignment.LEFT,
                        ctx);
        assertEquals("Wrong alignment", TextIconAlignment.LEFT,
                data.getAlignment());
        transformer.verify(data);
    }

    /**
     * A special transformer implementation which allows mocking the population
     * of the data object.
     */
    private class StaticTextDataTransformerTestImpl extends
            StaticTextDataTransformer
    {
        /** The number of invocations of populate(). */
        private int populateCount;

        /** The data object passed to populate(). */
        private StaticTextDataImpl populateData;

        /** A flag whether the populate() method is to be mocked. */
        private boolean mockPopulate;

        /**
         * Returns a flag whether populate() is to be mocked.
         *
         * @return the mock populate() flag
         */
        public boolean isMockPopulate()
        {
            return mockPopulate;
        }

        /**
         * Sets a flag whether populate() is to be mocked.
         *
         * @param mockPopulate the mock flag
         */
        public void setMockPopulate(boolean mockPopulate)
        {
            this.mockPopulate = mockPopulate;
        }

        /**
         * Verifies whether a transformation was done correctly.
         *
         * @param data the data object returned by transform()
         */
        public void verify(StaticTextData data)
        {
            assertEquals("Wrong number of populate() calls", 1, populateCount);
            assertSame("Wrong data object", populateData, data);
        }

        /**
         * Either mocks this method and records the invocation or calls the
         * super method.
         */
        @Override
        protected void populate(StaticTextDataImpl data, TransformerContext tctx)
        {
            if (isMockPopulate())
            {
                populateCount++;
                populateData = data;
                assertSame("Wrong context", ctx, tctx);
            }
            else
            {
                super.populate(data, tctx);
            }
        }
    }
}

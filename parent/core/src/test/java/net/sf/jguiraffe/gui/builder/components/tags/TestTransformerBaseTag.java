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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.transform.Transformer;
import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.TransformerContextPropertiesWrapper;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for TransformerBaseTag.
 *
 * @author Oliver Heger
 * @version $Id: TestTransformerBaseTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTransformerBaseTag
{
    /** The tag to be tested. */
    private TransformerBaseTagTestImpl tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new TransformerBaseTagTestImpl();
        JellyContext context = new JellyContext();
        tag.setContext(context);
        ComponentBuilderData builderData = new ComponentBuilderData();
        builderData.put(context);
        builderData.initializeForm(new TransformerContextImpl(), new BeanBindingStrategy());
    }

    /**
     * Tests obtaining the transformer context when no properties are set.
     */
    @Test
    public void testGetTransformerContextNoProperties()
    {
        TransformerContext ctx = tag.getTransformerContext();
        assertEquals("Wrong transformer context", ComponentBuilderData.get(
                tag.getContext()).getTransformerContext(), ctx);
    }

    /**
     * Tests the getTransformerContext() method when properties have been set.
     */
    @Test
    public void testGetTransformerContextProperties()
    {
        final int count = 7;
        Map<String, Object> props = new HashMap<String, Object>();
        for (int i = 0; i < count; i++)
        {
            props.put("prop" + i, "value" + i);
        }
        tag.setProperties(props);
        TransformerContext ctx = tag.getTransformerContext();
        assertTrue("No wrapped context returned",
                ctx instanceof TransformerContextPropertiesWrapper);
        TransformerContextPropertiesWrapper cw = (TransformerContextPropertiesWrapper) ctx;
        assertEquals("Wrong wrapped context", ComponentBuilderData.get(
                tag.getContext()).getTransformerContext(), cw
                .getWrappedContext());
        Map<String, Object> ctxProps = ctx.properties();
        for (int i = 0; i < count; i++)
        {
            String key = "prop" + i;
            assertEquals("Wrong property " + i, props.get(key), ctxProps
                    .get(key));
        }
    }

    /**
     * Tests the passResults() method when the parent is an input component tag.
     */
    @Test
    public void testPassResultsInputComponentTag() throws JellyTagException
    {
        Transformer bean = EasyMock.createMock(Transformer.class);
        EasyMock.replay(bean);
        InputComponentTag parent = new TextAreaTag();
        tag.setParent(parent);
        assertTrue("Wrong result of passResults()", tag.passResults(bean));
        assertEquals("Wrong parent tag", parent, tag.targetTag);
        assertEquals("Wrong transformer bean", bean, tag.transformer);
        EasyMock.verify(bean);
    }

    /**
     * Tests the passResults() method when no input component tag is used as
     * parent.
     */
    @Test
    public void testPassResultsOtherParent() throws JellyTagException
    {
        Transformer bean = EasyMock.createMock(Transformer.class);
        EasyMock.replay(bean);
        tag.setParent(new LabelTag());
        assertFalse("Wrong result of passResults()", tag.passResults(bean));
        EasyMock.verify(bean);
    }

    /**
     * Tests the passResults() method when an invalid bean is passed in. This
     * should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testPassResultsInvalidClass() throws JellyTagException
    {
        tag.setParent(new LabelTag());
        tag.passResults(this);
    }

    /**
     * A concrete test implementation of TransformerBaseTag.
     */
    private static class TransformerBaseTagTestImpl extends
            TransformerBaseTag<net.sf.jguiraffe.transform.Transformer>
    {
        /** Stores the target tag. */
        InputComponentTag targetTag;

        /** Stores the transformer bean. */
        Transformer transformer;

        @Override
        protected void handleInputComponentTag(InputComponentTag tag,
                Transformer bean) throws JellyTagException
        {
            targetTag = tag;
            transformer = bean;
        }
    }
}

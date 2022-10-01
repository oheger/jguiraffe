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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.DefaultTransformerWrapper;
import net.sf.jguiraffe.gui.forms.FormRuntimeException;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.TransformerWrapper;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.transform.Transformer;
import net.sf.jguiraffe.transform.TransformerContext;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for TransformerTag.
 *
 * @author Oliver Heger
 * @version $Id: TestTransformerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTransformerTag
{
    /** An input component tag used as parent. */
    private InputComponentTag input;

    /** A test transformer that will be set. */
    private Transformer transformer;

    /** The tag to be tested. */
    private TransformerTag tag;

    @Before
    public void setUp() throws Exception
    {
        JellyContext context = new JellyContext();
        tag = new TransformerTag();
        tag.setContext(context);
        input = new TextFieldTag();
        input.setContext(context);
        ComponentBuilderData cdata = new ComponentBuilderData();
        cdata.initializeForm(new TransformerContextImpl(),
                new BeanBindingStrategy());
        cdata.put(context);
        transformer = EasyMock.createMock(Transformer.class);
    }

    /**
     * Returns the default transformer context.
     *
     * @return the default (i.e. global) transformer context
     */
    private TransformerContext getDefaultContext()
    {
        return ComponentBuilderData.get(tag.getContext())
                .getTransformerContext();
    }

    /**
     * Tests whether a correct transformer wrapper was set.
     *
     * @param wrapper the wrapper to test
     * @param defCtx a flag whether the default transformer context is expected
     */
    private void checkTransformer(TransformerWrapper wrapper, boolean defCtx)
    {
        assertTrue("Wrong transformer wrapper type: " + wrapper,
                wrapper instanceof DefaultTransformerWrapper);
        DefaultTransformerWrapper tw = (DefaultTransformerWrapper) wrapper;
        assertEquals("Wrong wrapped transformer", transformer, tw
                .getTransformer());
        assertEquals("Wrong context", defCtx, getDefaultContext().equals(
                tw.getTransformerContext()));
    }

    /**
     * Tests creating a transformer for type read.
     */
    @Test
    public void testReadTransformer() throws JellyTagException
    {
        tag.setAttribute("type", "Read");
        tag.handleInputComponentTag(input, transformer);
        checkTransformer(input.getReadTransformer(), true);
        assertNull("Write transformer was set", input.getWriteTransformer());
    }

    /**
     * Tests creating a transformer for type write.
     */
    @Test
    public void testWriteTransformer() throws JellyTagException
    {
        tag.setAttribute("type", "write");
        tag.handleInputComponentTag(input, transformer);
        checkTransformer(input.getWriteTransformer(), true);
        assertNull("Read transformer was set", input.getReadTransformer());
    }

    /**
     * Tests creating a transformer of the default type.
     */
    @Test
    public void testDefaultTransformer() throws JellyTagException
    {
        tag.handleInputComponentTag(input, transformer);
        checkTransformer(input.getReadTransformer(), true);
        assertNull("Write transformer was set", input.getWriteTransformer());
    }

    /**
     * Tests the behavior of the the tag if an invalid transformer type was set.
     */
    @Test(expected = JellyTagException.class)
    public void testInvalidTransformer() throws JellyTagException
    {
        tag.setAttribute("type", "invalid transformer type");
        tag.handleInputComponentTag(input, transformer);
    }

    /**
     * Tests whether properties set for the tag are taken into account.
     */
    @Test
    public void testTransformerWithProperties() throws JellyTagException
    {
        tag.setProperties(new HashMap<String, Object>());
        tag.handleInputComponentTag(input, transformer);
        checkTransformer(input.getReadTransformer(), false);
    }

    /**
     * Tests whether a component type is taken into account.
     */
    @Test
    public void testComponentType() throws JellyTagException,
            FormBuilderException
    {
        tag.setAttribute("type", "WRITE");
        tag.setAttribute("componentType", Integer.class.getName());
        tag.handleInputComponentTag(input, transformer);
        assertEquals("Component type was not set", Integer.class, input
                .getComponentType());
    }

    /**
     * Tests the transformer wrapper implementation.
     */
    @Test
    public void testTransformerWrapper() throws Exception
    {
        DefaultTransformerWrapper wrapper = new DefaultTransformerWrapper(
                transformer, getDefaultContext());
        final Object dataObj = "DataObject";
        final Object transObj = "TransformedObject";
        EasyMock.expect(transformer.transform(dataObj, getDefaultContext()))
                .andReturn(transObj);
        EasyMock.replay(transformer);
        assertEquals("Wrong transformed object", transObj, wrapper
                .transform(dataObj));
        EasyMock.verify(transformer);
    }

    /**
     * Tests the transformer wrapper if the transformer throws an exception.
     */
    @Test
    public void testTransformerWrapperException() throws Exception
    {
        DefaultTransformerWrapper wrapper = new DefaultTransformerWrapper(
                transformer, getDefaultContext());
        final Object dataObj = "DataObject";
        EasyMock.expect(transformer.transform(dataObj, getDefaultContext()))
                .andThrow(new Exception("TestException"));
        EasyMock.replay(transformer);
        try
        {
            wrapper.transform(dataObj);
            fail("Exception was not re-thrown!");
        }
        catch (FormRuntimeException frex)
        {
            EasyMock.verify(transformer);
        }
    }
}

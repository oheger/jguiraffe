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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.Composite;
import net.sf.jguiraffe.gui.builder.components.ContainerSelector;
import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for FormBaseTag.
 *
 * @author Oliver Heger
 * @version $Id: TestFormBaseTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFormBaseTag
{
    /** Constant for the name of the test builder. */
    private static final String BUILDER_NAME = "TEST BUILDER";

    /** Constant for the name of the default resource group. */
    private static final String RES_GRP = "testformbuilderresources";

    /** The tag to be tested. */
    private TestTag tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new TestTag();
        JellyContext context = new JellyContext();
        ComponentBuilderData data = new ComponentBuilderData();
        data.initializeForm(new TransformerContextImpl(),
                new BeanBindingStrategy());
        data.setBuilderName(BUILDER_NAME);
        data.setDefaultResourceGroup(RES_GRP);
        data.put(context);
        tag.setContext(context);
    }

    /**
     * Tests if the builder data can be obtained.
     */
    @Test
    public void testGetBuilderData() throws Exception
    {
        ComponentBuilderData data = tag.getBuilderData();
        assertNotNull("Cannot obtain builder data", data);
        assertEquals("Wrong builder name", BUILDER_NAME, data.getBuilderName());
        assertSame("Wrong builder data", data, FormBaseTag.getBuilderData(tag
                .getContext()));
    }

    /**
     * Tests unconditional execution.
     */
    @Test
    public void testUnconditionalExecute() throws Exception
    {
        assertTrue("Cannot process", tag.canProcess());
    }

    /**
     * Tests execution with the ifName attribute.
     */
    @Test
    public void testIfExecute() throws Exception
    {
        tag.setIfName(BUILDER_NAME);
        assertTrue("Cannot process", tag.canProcess());
    }

    /**
     * Tests execution with the unlessName attribute.
     */
    @Test
    public void testUnlessExecute() throws Exception
    {
        tag.setUnlessName("any name, but " + BUILDER_NAME);
        assertTrue("Cannot process", tag.canProcess());
    }

    /**
     * Tests non execution with a wrong ifName attribute.
     */
    @Test
    public void testNotIfExecute() throws Exception
    {
        tag.setIfName("any name, but " + BUILDER_NAME);
        assertFalse("Can process", tag.canProcess());
    }

    /**
     * Tests non execution with a wrong unlessName attribute.
     */
    @Test
    public void testNotUnlessExecute() throws Exception
    {
        tag.setUnlessName(BUILDER_NAME);
        assertFalse("Can process", tag.canProcess());
    }

    /**
     * Tests to resolve resources.
     */
    @Test
    public void testGetResourceText()
    {
        assertEquals("Text of test resource", tag.getResourceText(
                "testformbuilderresources", "TEST_RESOURCE"));
        assertEquals("Text of test resource", tag.getResourceText(null,
                "TEST_RESOURCE"));
    }

    /**
     * Tests obtaining a resource that cannot be found.
     */
    @Test(expected = FormBuilderRuntimeException.class)
    public void testGetResourceTextNonExisting()
    {
        tag.getResourceText(null, "NONEXISTINGRESOURCE");
    }

    /**
     * Tests the convertToClass() method when a class is specified.
     */
    @Test
    public void testConvertToClass()
    {
        assertEquals("Wrong class", ComponentBuilderData.class, FormBaseTag
                .convertToClass(ComponentBuilderData.class));
    }

    /**
     * Tests converting to a class when a class name is specified.
     */
    @Test
    public void testConvertToClassByName()
    {
        assertEquals("Wrong class", ComponentBuilderData.class, FormBaseTag
                .convertToClass(ComponentBuilderData.class.getName()));
    }

    /**
     * Tests convertToClass() for a null input.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConvertToClassNull()
    {
        FormBaseTag.convertToClass(null);
    }

    /**
     * Tests convertToClass() for an invalid argument.
     */
    @Test(expected = FormBuilderRuntimeException.class)
    public void testConvertToClassInvalid()
    {
        FormBaseTag.convertToClass(new Integer(42));
    }

    /**
     * Tests whether an enclosing container tag can be found.
     */
    @Test
    public void testFindContainer()
    {
        ContainerSelector selector =
                EasyMock.createMock(ContainerSelector.class);
        Composite composite = EasyMock.createMock(Composite.class);
        ContainerTag container = new PanelTag();
        EasyMock.expect(selector.getComposite(container)).andReturn(composite);
        EasyMock.replay(selector, composite);
        tag.setParent(container);
        ComponentBuilderData.get(tag.getContext()).setContainerSelector(
                selector);

        assertSame("Wrong composite", composite, tag.findContainer());
    }

    /**
     * A concrete test tag implementation.
     */
    private static class TestTag extends FormBaseTag
    {
        @Override
        protected void process() throws JellyTagException
        {
            // just a dummy
        }
    }
}

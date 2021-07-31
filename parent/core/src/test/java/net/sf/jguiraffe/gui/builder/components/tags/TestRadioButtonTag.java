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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentGroup;
import net.sf.jguiraffe.gui.builder.components.ComponentManagerImpl;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code RadioButtonTag}. This test class tests {@code
 * RadioButtonTag} directly. There is also another test class which executes a
 * test script.
 *
 * @author Oliver Heger
 * @version $Id: TestRadioButtonTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestRadioButtonTag
{
    /** Constant for the name of the radio group. */
    private static final String GROUP = "testRadioGroup";

    /** Constant for the name of the radio button. */
    private static final String NAME = "radio";

    /** The Jelly context. */
    private JellyContext context;

    /** The tag to be tested. */
    private RadioButtonTag tag;

    @Before
    public void setUp() throws Exception
    {
        context = new JellyContext();
        tag = new RadioButtonTag();
        tag.setContext(context);
        tag.setName(NAME);
        ComponentBuilderData compData = new ComponentBuilderData();
        compData.initializeForm(new TransformerContextImpl(),
                new BeanBindingStrategy());
        compData.setComponentManager(new ComponentManagerImpl());
        compData.put(context);
    }

    /**
     * Creates a radio group tag and sets it as parent of the test tag.
     *
     * @return the parent tag
     */
    private RadioGroupTag setUpParent()
    {
        RadioGroupTag parent = new RadioGroupTag();
        try
        {
            parent.setContext(context);
            parent.setName(GROUP);
            ComponentGroup.createGroup(context, GROUP);
            tag.setParent(parent);
        }
        catch (Exception ex)
        {
            fail("Exception when preparing parent tag: " + ex);
        }
        return parent;
    }

    /**
     * Tests the default value of the noField attribute.
     */
    @Test
    public void testIsNoFieldDefault()
    {
        assertTrue("A field", tag.isNoField());
    }

    /**
     * Tests whether the radio button is added to the radio group if it is in
     * the body of a group tag.
     */
    @Test
    public void testProcessBeforeBodyAddGroup() throws FormBuilderException,
            JellyTagException
    {
        setUpParent();
        tag.processBeforeBody();
        ComponentGroup group = ComponentGroup.fromContext(context, GROUP);
        assertEquals("Wrong number of elements", 1, group.getComponentNames()
                .size());
        assertTrue("Component not found", group.getComponentNames().contains(
                NAME));
    }

    /**
     * Tests whether the name attribute is already checked before processing of
     * the body.
     */
    @Test(expected = MissingAttributeException.class)
    public void testProcessBeforeBodyNoName() throws FormBuilderException,
            JellyTagException
    {
        setUpParent();
        tag.setName(null);
        tag.processBeforeBody();
    }
}

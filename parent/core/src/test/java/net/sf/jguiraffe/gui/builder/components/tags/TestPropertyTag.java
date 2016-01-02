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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.fail;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Tag;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for PropertyTag.
 *
 * @author Oliver Heger
 * @version $Id: TestPropertyTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestPropertyTag
{
    /** Constant for the property key. */
    private static final String PROP_KEY = "myPropertyKey";

    /** Constant for the property value. */
    private static final Object PROP_VALUE = "A property value";

    /** The tag to be tested. */
    private PropertyTag tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new PropertyTag();
        tag.setContext(new JellyContext());
    }

    /**
     * Creates a mock object for a valid parent tag.
     *
     * @return the (uninitialized) mock object
     */
    private PropertySupport setUpParent()
    {
        PropertySupportTag parent = EasyMock
                .createMock(PropertySupportTag.class);
        tag.setParent(parent);
        return parent;
    }

    /**
     * Tests an execution of a valid tag.
     */
    @Test
    public void testProcess() throws JellyTagException, FormBuilderException
    {
        tag.setProperty(PROP_KEY);
        tag.setValue(PROP_VALUE);
        PropertySupport parent = setUpParent();
        parent.setProperty(PROP_KEY, PROP_VALUE);
        EasyMock.replay(parent);
        tag.process();
        EasyMock.verify(parent);
    }

    /**
     * Tests a tag with no property attribute. This should cause an exception.
     */
    @Test
    public void testProcessNoProperty() throws JellyTagException,
            FormBuilderException
    {
        PropertySupport parent = setUpParent();
        EasyMock.replay(parent);
        try
        {
            tag.process();
            fail("Could process tag with no key attribute!");
        }
        catch (MissingAttributeException maex)
        {
            EasyMock.verify(parent);
        }
    }

    /**
     * Tests a tag with an invalid parent tag. This should cause an exception
     */
    @Test(expected = JellyTagException.class)
    public void testProcessInvalidParent() throws JellyTagException,
            FormBuilderException
    {
        tag.setProperty(PROP_KEY);
        tag.process();
    }

    /**
     * Definition of an interface that is a union of the Tag and the
     * PropertySupport interfaces. Such a union is needed for constructing valid
     * mock objects.
     */
    private static interface PropertySupportTag extends Tag, PropertySupport
    {
    }
}

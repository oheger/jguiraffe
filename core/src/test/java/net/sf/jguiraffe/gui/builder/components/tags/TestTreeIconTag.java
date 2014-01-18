/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import java.util.Map;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.TagScript;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for TreeIconTag.
 *
 * @author Oliver Heger
 * @version $Id: TestTreeIconTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTreeIconTag
{
    /** Constant for the name of a test icon. */
    private static final String NAME = "iconName";

    /** Constant for a test icon. */
    private static final Object ICON = "MyTestIcon";

    /** The output object. */
    private XMLOutput output;

    /** Stores the parent tree tag. */
    private TreeTag parent;

    /** The tag to be tested. */
    private TreeIconTag tag;

    @Before
    public void setUp() throws Exception
    {
        JellyContext context = new JellyContext();
        tag = new TreeIconTag();
        tag.setContext(context);
        parent = new TreeTag();
        tag.setParent(parent);
        tag.setBody(new TagScript());
        output = new XMLOutput();
    }

    /**
     * Tests a successful tag execution.
     */
    @Test
    public void testProcessNormal() throws JellyTagException
    {
        tag.setName(NAME);
        tag.setIcon(ICON);
        tag.doTag(output);
        Map<String, Object> icons = parent.getIcons();
        assertEquals("Wrong number of icons", 1, icons.size());
        assertEquals("Wrong icon", ICON, icons.get(NAME));
    }

    /**
     * Tests executing the tag when no name attribute is provided.
     */
    @Test(expected = MissingAttributeException.class)
    public void testProcessNoName() throws JellyTagException
    {
        tag.setIcon(ICON);
        tag.doTag(output);
    }

    /**
     * Tests executing the tag when no icon is provided.
     */
    @Test(expected = MissingAttributeException.class)
    public void testProcessNoIcon() throws JellyTagException
    {
        tag.setName(NAME);
        tag.doTag(output);
    }

    /**
     * Tests executing the tag with a wrong parent.
     */
    @Test(expected = JellyTagException.class)
    public void testProcessInvalidParent() throws JellyTagException
    {
        tag.setName(NAME);
        tag.setIcon(ICON);
        tag.setParent(new LabelTag());
        tag.doTag(output);
    }
}

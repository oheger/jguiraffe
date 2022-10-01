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
package net.sf.jguiraffe.gui.builder.action.tags;

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code EventListenerTypeTag}. This class only tests some error
 * conditions. A successful execution of the tag is tested by other test classes
 * that execute a test script.
 *
 * @author Oliver Heger
 * @version $Id: TestEventListenerTypeTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestEventListenerTypeTag
{
    /** The tag to be tested. */
    private EventListenerTypeTag tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new EventListenerTypeTag();
        tag.setParent(new FormEventListenerTagTestImpl());
        JellyContext ctx = new JellyContext();
        tag.setContext(ctx);
    }

    /**
     * Tests whether an invalid parent tag is detected.
     */
    @Test(expected = JellyTagException.class)
    public void testProcessWrongParent() throws JellyTagException,
            FormBuilderException
    {
        tag.setType(FormListenerType.ACTION.name());
        tag.setParent(null);
        tag.process();
    }

    /**
     * Tries to execute a tag without a type attribute.
     */
    @Test(expected = MissingAttributeException.class)
    public void testProcessNoType() throws JellyTagException,
            FormBuilderException
    {
        tag.process();
    }

    /**
     * Tries to execute a tag for a non-standard type without a listener class.
     */
    @Test(expected = JellyTagException.class)
    public void testProcessNoListenerClass() throws JellyTagException,
            FormBuilderException
    {
        tag.setType("Expansion");
        tag.process();
    }

    /**
     * A test implementation of a form event listener tag.
     */
    private static class FormEventListenerTagTestImpl extends
            FormEventListenerTag
    {
        public FormEventListenerTagTestImpl()
        {
            super(FormListenerType.ACTION);
        }
    }
}

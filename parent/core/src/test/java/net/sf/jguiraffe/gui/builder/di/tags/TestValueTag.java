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
package net.sf.jguiraffe.gui.builder.di.tags;

import static org.junit.Assert.assertFalse;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ValueTag}. Note: The tag is also tested by the test
 * class for {@code JellyBeanBuilder}.
 *
 * @author Oliver Heger
 * @version $Id: TestValueTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestValueTag
{
    /** The tag to be tested. */
    private ValueTag tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new ValueTag();
        tag.setContext(new JellyContext());
    }

    /**
     * Tests a tag with an invalid parent. This should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagInvalidParent() throws JellyTagException
    {
        tag.doTag(new XMLOutput());
    }

    /**
     * Tests that escaping is turned off for the value tag.
     */
    @Test
    public void testIsEscapeDisabled()
    {
        assertFalse("Escaping enabled", tag.isEscapeText());
    }
}

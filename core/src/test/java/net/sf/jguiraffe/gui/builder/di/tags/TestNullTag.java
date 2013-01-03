/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link NullTag}.
 *
 * @author Oliver Heger
 * @version $Id: TestNullTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestNullTag
{
    /** The output object. */
    private XMLOutput output;

    /** The tag to be tested. */
    private NullTag tag;

    @Before
    public void setUp() throws Exception
    {
        output = new XMLOutput();
        tag = new NullTag();
        tag.setContext(new JellyContext());
    }

    /**
     * Creates a mock for the parent tag.
     *
     * @return the parent mock
     */
    private ValueTag setUpParent()
    {
        ValueTag parent = EasyMock.createMock(ValueTag.class);
        tag.setParent(parent);
        return parent;
    }

    /**
     * Tries to execute a tag with an invalid parent. This should cause an
     * exception.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagInvalidParent() throws JellyTagException
    {
        tag.doTag(output);
    }

    /**
     * Tests a successful execution of doTag().
     */
    @Test
    public void testDoTag() throws JellyTagException
    {
        ValueTag parent = setUpParent();
        parent.setValue(null);
        EasyMock.replay(parent);
        tag.doTag(output);
        EasyMock.verify(parent);
    }

    /**
     * An interface combining both the Tag and ValueSupport interface. This is
     * needed for creating mock objects.
     */
    private static interface ValueTag extends Tag, ValueSupport
    {
    }
}

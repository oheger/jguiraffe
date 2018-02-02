/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

import static org.junit.Assert.assertEquals;
import net.sf.jguiraffe.di.impl.NameDependency;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.TagScript;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code InvocationTargetTag}.
 *
 * @author Oliver Heger
 * @version $Id: TestInvocationTargetTag.java 207 2012-02-09 07:30:13Z oheger $
 */
public class TestInvocationTargetTag
{
    /** The tag to be tested. */
    private InvocationTargetTag tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new InvocationTargetTag();
        JellyContext context = new JellyContext();
        tag.setContext(context);
        tag.setBody(new TagScript());
    }

    /**
     * Helper method for executing the test tag.
     *
     * @throws JellyTagException if execution causes an error
     */
    private void invokeTag() throws JellyTagException
    {
        XMLOutput output = new XMLOutput();
        tag.doTag(output);
    }

    /**
     * Tests a simple execution.
     */
    @Test
    public void testExecuteTag() throws JellyTagException
    {
        MethodInvocationTag parent = new MethodInvocationTag();
        tag.setParent(parent);
        final String dependencyName = "otherBean";
        tag.setRefName(dependencyName);
        invokeTag();
        assertEquals("Wrong target dependency",
                NameDependency.getInstance(dependencyName),
                parent.getTargetDependency());
    }

    /**
     * Tests whether an invalid parent tag is recognized.
     */
    @Test(expected = JellyTagException.class)
    public void testExecuteTagWrongParent() throws JellyTagException
    {
        BeanTag parent = new BeanTag();
        tag.setParent(parent);
        invokeTag();
    }
}

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

import java.util.Iterator;

import net.sf.jguiraffe.gui.builder.components.ComponentGroup;

import org.apache.commons.jelly.JellyException;

/**
 * Test class for ComponentGroupTag.
 *
 * @author Oliver Heger
 * @version $Id: TestComponentGroupTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestComponentGroupTag extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "componentgroup";

    /**
     * Tests creating component groups.
     */
    public void testCreateGroups() throws Exception
    {
        executeScript(SCRIPT);
        assertTrue(ComponentGroup.groupExists(context, "group1"));
        assertTrue(ComponentGroup.groupExists(context, "group2"));
        assertTrue(ComponentGroup.groupExists(context, "group3"));

        checkGroupContent("group1", new String[]
        { "text1", "text2" });
        checkGroupContent("group2", new String[]
        { "text2", "text5" });
        checkGroupContent("group3", new String[]
        { "text3", "text4" });
    }

    /**
     * Tests creating a component group without a name.
     */
    public void testErrorMissingName() throws Exception
    {
        builderData.setBuilderName(ERROR_BUILDER);
        try
        {
            executeScript(SCRIPT);
            fail("Could execute group tag without name attribute!");
        }
        catch (JellyException jex)
        {
            //ok
        }
    }

    /**
     * Tests creating a component group whose name already exists.
     */
    public void testErrorDuplicateName() throws Exception
    {
        builderData.setBuilderName("ERROR_NAME_EXISTS");
        try
        {
            executeScript(SCRIPT);
            fail("Could create group with duplicate name!");
        }
        catch (JellyException jex)
        {
            //ok
        }
    }

    /**
     * Tests creating a component with an invalid groups attribute.
     */
    public void testErrorInvalidGroupName() throws Exception
    {
        builderData.setBuilderName("ERROR_INVALID_GROUP");
        try
        {
            executeScript(SCRIPT);
            fail("Could create component with invalid group name!");
        }
        catch (JellyException jex)
        {
            //ok
        }
    }

    /**
     * Helper method for checking the content of a component group.
     *
     * @param grpName the name of the group to check
     * @param expected an array with the names of the contained components
     */
    private void checkGroupContent(String grpName, String[] expected)
    {
        ComponentGroup group = ComponentGroup.fromContext(context, grpName);
        assertEquals("Wrong number of elements in group", expected.length,
                group.getComponentNames().size());
        Iterator<String> it = group.getComponentNames().iterator();
        for (int i = 0; i < expected.length; i++)
        {
            assertEquals("Wrong element at " + i, expected[i], it.next());
        }
    }
}

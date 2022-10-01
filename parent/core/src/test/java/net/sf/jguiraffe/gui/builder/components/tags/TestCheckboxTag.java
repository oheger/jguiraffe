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

import org.apache.commons.jelly.JellyException;

/**
 * Test class for CheckboxTag.
 *
 * @author Oliver Heger
 * @version $Id: TestCheckboxTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestCheckboxTag extends AbstractTagTest
{
    /** Constant for the test script. */
    private static final String SCRIPT = "checkbox";

    /**
     * Tests executing a valid Jelly script.
     */
    public void testCreateCheckboxes() throws Exception
    {
        checkScript(
                SCRIPT,
                "Container: ROOT {"
                        + " CHECKBOX [ FCOL = "
                        + colorString(0, 0, 255)
                        + " NAME = fine "
                        + "TEXT = Everything fine? ALIGN = LEFT MNEMO = f ],"
                        + " CHECKBOX [ BCOL = "
                        + colorString(0, 0, 0)
                        + " NAME = checkIcon ICON = ICON [ "
                        + iconLocatorString()
                        + " ] ALIGN = CENTER ],"
                        + " CHECKBOX [ NAME = cbx1 TEXT = mushrooms ALIGN = LEFT ],"
                        + " CHECKBOX [ NAME = cbx2 TEXT = ham ALIGN = LEFT MNEMO = h ],"
                        + " CHECKBOX [ NAME = cbx3 TEXT = peperoni ALIGN = LEFT ] }");
        assertNotNull("Field handler not created", builderData
                .getFieldHandler("cbx1"));
    }

    /**
     * Tests executing a tag with missing attributes.
     */
    public void testErrUndefinedTag() throws Exception
    {
        builderData.setBuilderName(ERROR_BUILDER);
        try
        {
            executeScript(SCRIPT);
            fail("Could execute tag with missing attributes!");
        }
        catch (JellyException jex)
        {
            // ok
        }
    }
}

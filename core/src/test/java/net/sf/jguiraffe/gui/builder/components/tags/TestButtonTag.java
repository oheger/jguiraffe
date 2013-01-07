/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

/**
 * Test class for ButtonTag and ToggleButtonTag.
 *
 * @author Oliver Heger
 * @version $Id: TestButtonTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestButtonTag extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "button";

    /** Constant for the expected result of the test script. */
    private static final String EXPECTED = "Container: ROOT { BUTTON [ "
            + "TEXT = Hello world! ALIGN = LEFT MNEMO = w CMD = Hello "
            + "FCOL = (0, 0, 255) NAME = Hello ],"
            + " BUTTON [ ICON = ICON [ "
            + iconLocatorString()
            + " ] ALIGN = CENTER CMD = Image BCOL = (0, 0, 0) NAME = image ],"
            + " BUTTON [ TEXT = OK ALIGN = LEFT CMD = OK NAME = ok DEFAULT ],"
            + " BUTTON [ TEXT = Cancel ALIGN = LEFT MNEMO = C CMD = CANCEL NAME = cancel ],"
            + " TOGGLE [ TEXT = Toggle ALIGN = LEFT CMD = tog NAME = toggle ] }";

    /**
     * Tests whether the properties of button tags are correctly evaluated.
     */
    public void testCreateButtons() throws Exception
    {
        checkScript(SCRIPT, EXPECTED);
    }

    /**
     * Tests whether the expected component handlers are created for the test
     * buttons.
     */
    public void testCreateComponentHandlers() throws Exception
    {
        executeScript(SCRIPT);
        assertNull("Got a field for a command button", builderData.getForm()
                .getField("Hello"));
        assertNotNull("No button component", builderData.getComponent("Hello"));
        assertNull("Got a field for image button", builderData.getForm()
                .getField("image"));
        assertNotNull("No field for ok button", builderData.getForm().getField(
                "ok"));
        assertNotNull("No field for toggle button", builderData.getForm()
                .getField("toggle"));
    }

    /**
     * Tests whether the name of the default button is recorded.
     */
    public void testDefaultButtonName() throws Exception
    {
        executeScript(SCRIPT);
        assertEquals("Wrong name of default button", "ok", builderData
                .getDefaultButtonName());
    }

    /**
     * Tests executing an invalid script.
     */
    public void testButtonError() throws Exception
    {
        errorScript(SCRIPT, ERROR_BUILDER,
                "Could execute tag with missing attributes!");
    }
}

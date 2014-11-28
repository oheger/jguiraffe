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

/**
 * Test class for TextFieldTag.
 *
 * @author Oliver Heger
 * @version $Id: TestTextFieldTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTextFieldTag extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "textfield";

    /**
     * Tests to create several text fields with different attributes.
     */
    public void testCreateTextFields() throws Exception
    {
        checkScript(SCRIPT, "Container: ROOT { TEXTFIELD [ NAME = name ],"
                + " TEXTFIELD [ NAME = firstName COLUMNS = 20 ],"
                + " TEXTFIELD [ NAME = street MAXLEN = 30 ],"
                + " TEXTFIELD [ NAME = city COLUMNS = 20 MAXLEN = 35 ],"
                + " PASSWORDFIELD [ NAME = pwd COLUMNS = 8 MAXLEN = 10 ] }");
    }

    /**
     * Tests whether the expected field handlers have been created by the tags.
     */
    public void testFieldHandlers() throws Exception
    {
        executeScript(SCRIPT);
        checkHandler("name");
        checkHandler("firstName");
        checkHandler("street");
        checkHandler("city");
        checkHandler("pwd");
    }

    /**
     * Tests whether a field handler with the specified name was created.
     *
     * @param name the name to check
     */
    private void checkHandler(String name)
    {
        assertNotNull("FieldHandler not found: " + name, builderData
                .getFieldHandler(name));
    }
}

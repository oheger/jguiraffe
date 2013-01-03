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
package net.sf.jguiraffe.gui.builder.window.tags;

/**
 * A test class that tests multiple window declarations in a single script.
 *
 * @author Oliver Heger
 * @version $Id: TestMultipleWindows.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestMultipleWindows extends AbstractWindowTagTest
{
    /** Constant for the name of the builder script. */
    private static final String SCRIPT = "multiwindows";

    /**
     * Tests whether the script with multiple windows can be processed.
     */
    public void testCreateWindows() throws Exception
    {
        executeScript(SCRIPT);
        assertNotNull("No result window", windowBuilderData.getResultWindow());
        assertNotNull("No named window", windowBuilderData
                .getWindow("msgDialog"));
    }

    /**
     * Tests whether components of all windows can be accessed.
     */
    public void testAccessComponents() throws Exception
    {
        executeScript(SCRIPT);
        assertNotNull("Cannot access button", builderData
                .getComponentHandler("dlgOkButton"));
        assertNotNull("Cannot access label", builderData
                .getWidgetHandler("label"));
    }
}

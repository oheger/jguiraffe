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
package net.sf.jguiraffe.gui.builder.window.tags;

import net.sf.jguiraffe.gui.builder.action.tags.AbstractActionTagTest;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowManagerImpl;

/**
 * <p>
 * A base class for testing tags of the window builder library.
 * </p>
 * <p>
 * Like the corresponding test base classes of the form builder and action
 * builder tag libraries this class performs the necessary initialization for
 * executing Jelly scripts with window tags.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AbstractWindowTagTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractWindowTagTest extends AbstractActionTagTest
{
    /** Constant for the line delimiter. */
    private static final String CR = System.getProperty("line.separator");

    /** Stores the window builder data object. */
    protected WindowBuilderData windowBuilderData;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        setUpWindowBuilder();
    }

    /**
     * Initializes Jelly. This implementation takes care of the window builder
     * tag library.
     */
    @Override
    protected void setUpJelly()
    {
        super.setUpJelly();
        context.registerTagLibrary("windowBuilder",
                new WindowBuilderTagLibrary());
    }

    /**
     * Initializes the window builder objects.
     */
    protected void setUpWindowBuilder()
    {
        windowBuilderData = new WindowBuilderData();
        windowBuilderData.setWindowManager(new WindowManagerImpl());
        windowBuilderData.put(context);
    }

    /**
     * Executes a Jelly window builder script and returns the resulting window.
     *
     * @param script the name of the script
     * @param builderName the name of the builder to use
     * @return the newly created window (the test fails if no window was
     * created)
     * @throws Exception if an error occurs
     */
    protected Window fetchWindow(String script, String builderName)
            throws Exception
    {
        builderData.setBuilderName(builderName);
        executeScript(script);
        builderData.invokeCallBacks();
        assertNotNull("No window was created!", windowBuilderData
                .getResultWindow());
        return windowBuilderData.getResultWindow();
    }

    /**
     * Executes a Jelly script that creates a window and checks the results.
     *
     * @param script the name of the script
     * @param builderName the name of the builder to use
     * @param expected the expected results (this will be compared with the
     * results of the new window's <code>toString()</code> method)
     * @throws Exception if an error occurs
     */
    protected void checkWindowScript(String script, String builderName,
            String expected) throws Exception
    {
        builderData.setBuilderName(builderName);
        executeScript(script);
        assertNotNull("No window was created!", windowBuilderData
                .getResultWindow());
        String current = fetchWindow(script, builderName).toString();
        assertEquals("Resulting window's properties do not match expectations!"
                + CR + "exp: " + expected + CR + "was: " + current, expected,
                current);
    }
}

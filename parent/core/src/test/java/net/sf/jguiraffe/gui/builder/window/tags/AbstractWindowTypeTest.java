/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowImpl;

/**
 * <p>
 * A base class for testing the different window types supported by the window
 * builder tag library.
 * </p>
 * <p>
 * This base class already defines concrete test cases that depend on a specific
 * window type. Derived classes only have to set a specific type name, and then
 * all test cases will be executed for that type.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AbstractWindowTypeTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractWindowTypeTest extends AbstractWindowTagTest
{
    /** Constant for the Jelly script to execute. */
    protected static final String SCRIPT = "window";

    /** Constant for the builder generated window prologue. */
    protected static final String WINDOW_PROLOGUE = "WINDOW ";

    /** Constant for the window's root container prefix. */
    protected static final String CONTAINER = " { Container: WindowRootContainer { ";

    /** Constant for an empty window root container. */
    protected static final String CONTAINER_EMPTY = CONTAINER + " } }";

    /** Constant for the menu fragment. */
    protected static final String MENU = "MENU = Container: MENUBAR { "
            + "Container: MENU [ TEXT = File MNEMO = F ] { "
            + "MENUITEM [ TEXT = Exit ] } }";

    /** Constant for the simple window builder. */
    private static final String BUILDER_SIMPLE = "SIMPLE";

    /** Constant for the coordinates window builder. */
    private static final String BUILDER_COORDS = "COORDS";

    /** Constant for the centered window builder. */
    private static final String BUILDER_CENTERED = "CENTERED";

    /** Constant for the title window builder. */
    private static final String BUILDER_TITLE = "TITLE";

    /** Constant for the title res window builder. */
    private static final String BUILDER_TITLERES = "TITLERES";

    /** Constant for the icon window builder. */
    private static final String BUILDER_ICON = "ICON";

    /** Constant for the flags window builder. */
    private static final String BUILDER_FLAGS = "FLAGS";

    /** Constant for the autoClose window builder. */
    private static final String BUILDER_AUTOCLOSE = "AUTOCLOSE";

    /** Constant for the menu window builder. */
    private static final String BUILDER_MENU = "MENU";

    /** Constant for the menu error window builder. */
    private static final String BUILDER_MENUERR = "MENU_ERR";

    /** Constant for the content window builder. */
    private static final String BUILDER_CONTENT = "CONTENT";

    /** Constant for the default button window builder. */
    private static final String BUILDER_DEFBTN = "DEFBTN";

    /** Stores the name of the window type. */
    private String windowType;

    /**
     * Returns the window type.
     *
     * @return the window type
     */
    public String getWindowType()
    {
        return windowType;
    }

    /**
     * Sets the window type.
     *
     * @param windowType the window type
     */
    public void setWindowType(String windowType)
    {
        this.windowType = windowType;
    }

    /**
     * Tests a very simple window definition without special attributes.
     */
    public void testSimpleWindow() throws Exception
    {
        executeWindowTest(BUILDER_SIMPLE, "]" + CONTAINER_EMPTY);
    }

    /**
     * Tests a window definition with coordinates.
     */
    public void testWindowWithCoords() throws Exception
    {
        executeWindowTest(BUILDER_COORDS, "X = 10 Y = 20 W = 200 H = 100 ]"
                + CONTAINER_EMPTY);
    }

    /**
     * Tests a window with the centered flag set to true.
     */
    public void testWindowCentered() throws Exception
    {
        executeWindowTest(BUILDER_CENTERED, "W = 200 H = 100 CENTER ]"
                + CONTAINER_EMPTY);
    }

    /**
     * Tests a window with a directly set title.
     */
    public void testWindowWithTitle() throws Exception
    {
        executeWindowTest(BUILDER_TITLE, "TITLE = Window title ]"
                + CONTAINER_EMPTY);
    }

    /**
     * Tests a window with a title obtained from resources.
     */
    public void testWindowWithTitleFromRes() throws Exception
    {
        executeWindowTest(BUILDER_TITLERES, "TITLE = Window caption ]"
                + CONTAINER_EMPTY);
    }

    /**
     * Tests a window with an icon.
     */
    public void testWindowWithIcon() throws Exception
    {
        executeWindowTest(BUILDER_ICON,
                "ICON = ICON [ " + iconLocatorString() + " ] ]" + CONTAINER_EMPTY);
    }

    /**
     * Tests a window with flags.
     */
    public void testWindowWithFlags() throws Exception
    {
        executeWindowTest(BUILDER_FLAGS, "FLAGS = CIMR ]" + CONTAINER_EMPTY);
    }

    /**
     * Tests a window whose autoClose flag is disabled.
     */
    public void testWindowAutoClose() throws Exception
    {
        executeWindowTest(BUILDER_AUTOCLOSE, "NOAUTOCLOSE ]" + CONTAINER_EMPTY);
    }

    /**
     * Tests a window with a menu.
     */
    public void testWindowWithMenu() throws Exception
    {
        executeWindowTest(BUILDER_MENU, MENU + " ]" + CONTAINER_EMPTY);
    }

    /**
     * Tests a window that refers to a non existing menu.
     */
    public void testWindowWithInvalidMenu() throws Exception
    {
        errorScript(SCRIPT, builderName(BUILDER_MENUERR),
                "Non existing menu was not detected!");
    }

    /**
     * Tests a window with content in its root container.
     */
    public void testWindowWithContent() throws Exception
    {
        executeWindowTest(BUILDER_CONTENT, "]" + CONTAINER
                + "LABEL [ TEXT = A label ALIGN = LEFT ] } }");
    }

    /**
     * Tests a window with a default button definition.
     */
    public void testWindowWithDefBtn() throws Exception
    {
        executeWindowTest(BUILDER_DEFBTN, "]" + CONTAINER
                + "BUTTON [ TEXT = Press me ALIGN = LEFT "
                + "NAME = defaultButton DEFAULT ] } }");
        assertNull("Still got default button name", builderData
                .getDefaultButtonName());
    }

    /**
     * Tests whether the window's parent is correctly set.
     */
    public void testWindowWithParent() throws Exception
    {
        WindowImpl parent = new WindowImpl();
        parent.setTitle("Parent window");
        windowBuilderData.setParentWindow(parent);
        testSimpleWindow();
        assertSame("Parent window was not set", parent, windowBuilderData
                .getResultWindow().getParentWindow());
    }

    /**
     * Tests the tags when no root container is set. This should be no problem.
     * The window must not be inserted into a root container.
     */
    public void testWindowNoRootContainer() throws Exception
    {
        ComponentBuilderData compData = ComponentBuilderData.get(context);
        compData.setRootContainer(null);
        testSimpleWindow();
    }

    /**
     * Helper method for executing a test with a window.
     *
     * @param builderName the (base) name of the builder
     * @param expected the expected results (without the prologue)
     * @throws Exception if an error occurs
     */
    protected void executeWindowTest(String builderName, String expected)
            throws Exception
    {
        checkWindowScript(SCRIPT, builderName(builderName), WINDOW_PROLOGUE
                + getWindowType() + " [ " + expected);
    }

    /**
     * Creates the name of a builder based on the window type.
     *
     * @param name the builder's base name
     * @return the final builder name
     */
    private String builderName(String name)
    {
        return getWindowType() + "_" + name;
    }
}

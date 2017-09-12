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
package net.sf.jguiraffe.gui.builder.action.tags;

/**
 * Test class for the menu tags.
 *
 * @author Oliver Heger
 * @version $Id: TestMenuTags.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestMenuTags extends AbstractActionTagTest
{
    /** Constant for the name of the script file.*/
    private static final String SCRIPT = "menu";

    /**
     * Tests creating menu bars.
     */
    public void testMenuBar() throws Exception
    {
        checkMenuBar("emptyMenu", "Container: MENUBAR {  }");
        checkMenuBar("barWithEmptyMenus", "Container: MENUBAR { "
                + "Container: MENU [ TEXT = File MNEMO = F ] { "
                + "Container: MENU [ TEXT = Print ICON = ICON [ "
                + iconLocatorString()
                + " ] MNEMO = P ] {  } }, "
                + "Container: MENU [ TEXT = Edit MNEMO = E ] {  } }");
        checkMenuBar(
                "testMenu",
                "Container: MENUBAR { "
                        + "Container: MENU [ TEXT = File MNEMO = F ] { "
                        + "MENUITEM [ ACTION = Action FileOpen {  "
                        + "TEXT = File open TOOLTIP = Opens a file MNEMO = o "
                        + "TASK = class net.sf.jguiraffe.gui.builder.action.tags.TestActionTag$MyActionTask } ], "
                        + "MENUITEM [ ACTION = Action FileSave {  "
                        + "TEXT = File save TOOLTIP = Saves the current file MNEMO = s "
                        + "TASK = class net.sf.jguiraffe.gui.builder.action.tags.TestActionTag$MyActionTask } ], "
                        + "MENUITEM [ TEXT = Save as TOOLTIP = Saves the file under "
                        + "a new name MNEMO = a ], <SEPARATOR>, MENUITEM [ TEXT = Print "
                        + "TOOLTIP = Prints the document MNEMO = P ], "
                        + "<SEPARATOR>, "
                        + "MENUITEM [ TEXT = Exit TOOLTIP = Exits this application "
                        + "MNEMO = x ] }, "
                        + "Container: MENU [ TEXT = Edit MNEMO = E ] { "
                        + "MENUITEM [ TEXT = Cut ICON = ICON [ "
                        + iconLocatorString()
                        + " ] ], MENUITEM [ TEXT = Paste ], MENUITEM [ TEXT = Copy ], "
                        + "<SEPARATOR>, "
                        + "MENUITEM [ TEXT = Hex mode CHECKED ] } }");
        assertNotNull(builderData.getComponentHandler("EDIT_CUT"));
        assertNotNull(builderData.getComponentHandler("EDIT_PASTE"));
        assertNotNull(builderData.getComponentHandler("EDIT_COPY"));
    }

    /**
     * Tests creating a menu bar without a name.
     */
    public void testErrMenuBarNoName() throws Exception
    {
        errorScript(SCRIPT, "ERR_BAR_NO_NAME",
                "Could create menu bar without a name!");
    }

    /**
     * Tests creating a menu that is not fully defined.
     */
    public void testErrMenuUndef() throws Exception
    {
        errorScript(SCRIPT, "ERR_UNDEF_MENU", "Could create undefined menu!");
    }

    /**
     * Tests a not correctly nested menu tag.
     */
    public void testErrMenuNested() throws Exception
    {
        errorScript(SCRIPT, "ERR_MENU_NESTED",
                "Could process incorrectly nested menu tag!");
    }

    /**
     * Tests an undefined menu item tag.
     */
    public void testErrMenuItemUndef() throws Exception
    {
        errorScript(SCRIPT, "ERR_ITEM_UNDEF",
                "Could create undefined menu item tag!");
    }

    /**
     * Tests a not correctly nested menu item tag.
     */
    public void testErrMenuItemNested() throws Exception
    {
        errorScript(SCRIPT, "ERR_ITEM_NESTED",
                "Could process incorrectly nested menu item tag!");
    }

    /**
     * Tests a menu item tag that refers to a non existing action.
     */
    public void testErrMenuItemAction() throws Exception
    {
        errorScript(SCRIPT, "ERR_ITEM_ACTION",
                "Could refer to non existing action!");
    }

    /**
     * Tests a separator tag that is not correctly nested.
     */
    public void testErrSeparatorNested() throws Exception
    {
        errorScript(SCRIPT, "ERR_SEPARATOR_NESTED",
                "Could process incorrectly nested separator tag!");
    }

    /**
     * Tests to add a separator to a menu bar (which should fail).
     */
    public void testAddMenuBarSeparator()
    {
        MenuBarTag bar = new MenuBarTag();
        try
        {
            bar.addSeparator();
            fail("Could add separator to menu bar!");
        }
        catch (UnsupportedOperationException uoex)
        {
            // ok
        }
    }

    /**
     * Helper method for testing a complete menu bar. Executes the script with
     * the given builder name and compares the results with the expected text.
     *
     * @param builderName the builder name
     * @param menuName the name of the menu bar
     * @param expected the expected results
     * @throws Exception if an error occurs
     */
    protected void checkMenuBar(String menuName, String expected)
            throws Exception
    {
        executeScript(SCRIPT);
        Object menuBar = context.getVariable(menuName);
        assertNotNull("Could not find menu bar " + menuName, menuBar);
        assertEquals("Menu bar " + menuName + ": Expected " + expected
                + " but was: " + menuBar.toString(), expected, menuBar
                .toString());
    }
}

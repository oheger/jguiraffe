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
package net.sf.jguiraffe.gui.builder.action.tags;

/**
 * Test class for ToolbarTag and related handler classes.
 *
 * @author Oliver Heger
 * @version $Id: TestToolbarTags.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestToolbarTags extends AbstractActionTagTest
{
    /** Constant for the name of the script file. */
    private static final String SCRIPT = "toolbar";

    /**
     * Tests creating a toolbar.
     */
    public void testToolbar() throws Exception
    {
        checkScript(
                SCRIPT,
                "Container: ROOT { "
                        + "Container: TOOLBAR { "
                        + "TOOLBUTTON [ ACTION = Action FileOpen {  "
                        + "TEXT = File open TOOLTIP = Opens a file MNEMO = o "
                        + "TASK = class net.sf.jguiraffe.gui.builder.action.tags.TestActionTag$MyActionTask } ], "
                        + "TOOLBUTTON [ TEXT = Save as ICON = ICON [ "
                        + iconLocatorString()
                        + " ] TOOLTIP = Saves the file under a new name MNEMO = a ], "
                        + "<SEPARATOR>, "
                        + "TOOLBUTTON [ TEXT = Print TOOLTIP = Prints the document "
                        + "MNEMO = P CHECKED ] } }");
        assertNotNull("Component handler not created", builderData
                .getComponentHandler("FileSaveAs"));
    }

    /**
     * Tests an incorrectly nested tag.
     */
    public void testErrNested() throws Exception
    {
        errorScript(SCRIPT, "ERR_NESTED",
                "Could process incorrectly nested toolbutton tag!");
    }
}

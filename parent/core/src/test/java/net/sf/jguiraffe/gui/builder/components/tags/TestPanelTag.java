/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
 * Test class for PanelTag.
 *
 * @author Oliver Heger
 * @version $Id: TestPanelTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestPanelTag extends AbstractTagTest
{
    /**
     * Tests creating panels with content.
     */
    public void testPanel() throws Exception
    {
        context.setVariable("font", "TestFont");
        checkScript(
                "panel",
                "Container: ROOT { "
                        + "LABEL [ TEXT = Hello world! ALIGN = LEFT ], "
                        + "Container: PANEL [ BCOL = "
                        + colorString(0, 0, 0)
                        + " TEXT = TestPanel "
                        + "TEXTCOL = "
                        + colorString(255, 255, 0)
                        + " TEXTFONT = TestFont BORDER = TRUE BORDERREF = TESTBORDER ] { "
                        + "LABEL [ TEXT = My Input: ALIGN = LEFT ], "
                        + "TEXTFIELD [ NAME = input MAXLEN = 20 ] }, "
                        + "LABEL [ TEXT = Label after container ALIGN = LEFT ], "
                        + "Container: PANEL [ TEXT = Second Panel ] {  } }");
        assertNotNull(builderData.getFieldHandler("input"));
    }

    /**
     * Tests creating a panel with an undefined font reference.
     */
    public void testInvalidFontRef() throws Exception
    {
        builderData.setBuilderName("ERR_FONTREF");
        try
        {
            executeScript("panel");
            fail("Could assign invalid font reference!");
        }
        catch (JellyException jex)
        {
            // ok
        }
    }
}

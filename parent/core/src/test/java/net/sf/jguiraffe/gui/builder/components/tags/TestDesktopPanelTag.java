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

/**
 * Test class for DesktopPanelTag.
 *
 * @author Oliver Heger
 * @version $Id: TestDesktopPanelTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDesktopPanelTag extends AbstractTagTest
{
    /** Constant for the test script. */
    private static final String SCRIPT = "desktoppanel";

    /** Constant for the default builder. */
    private static final String BUILDER_DEFAULT = "DP_DEFDRAG";

    /** Constant for the drag mode builder. */
    private static final String BUILDER_DRAGMODE = "DP_DRAG";

    /** Constant for the invalid drag mode builder. */
    private static final String BUILDER_DRAGINV = "DP_DRAGINV";

    /** Constant for the start of the results. */
    private static final String RES_PREFIX = "Container: ROOT { "
            + "Container: DESKTOPPANEL [ ";

    /** Constant for the end of the results. */
    private static final String RES_SUFFIX = " ] {  } }";

    /**
     * Tests creating a default desktop panel.
     */
    public void testCreateDefaultDesktopPanel() throws Exception
    {
        builderData.setBuilderName(BUILDER_DEFAULT);
        checkScript(SCRIPT, RES_PREFIX + "BCOL = " + colorString(255, 255, 255)
                + " DRAGMODE = LIVE" + RES_SUFFIX);
    }

    /**
     * Tests creating a desktop panel with drag mode declaration.
     */
    public void testCreateDesktopPanelWithDragMode() throws Exception
    {
        builderData.setBuilderName(BUILDER_DRAGMODE);
        checkScript(SCRIPT, RES_PREFIX + "DRAGMODE = OUTLINE" + RES_SUFFIX);
    }

    /**
     * Tests an invalid drag mode. This should cause an exception.
     */
    public void testCreateDesktopPanelWithInvalidDragMode() throws Exception
    {
        errorScript(SCRIPT, BUILDER_DRAGINV, "Could process invalid drag mode!");
    }
}

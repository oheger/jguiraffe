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
package net.sf.jguiraffe.gui.builder.components.tags;

import org.apache.commons.jelly.JellyException;

/**
 * Test class for BorderLayoutTag and related classes.
 *
 * @author Oliver Heger
 * @version $Id: TestBorderLayoutTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestBorderLayoutTag extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "borderlayout";

    /**
     * Tests creating layout objects.
     */
    public void testLayouts() throws Exception
    {
        checkScript(
                SCRIPT,
                "Container: ROOT [ LAYOUT = "
                        + "BorderLayout [ ] ] { "
                        + "Container: PANEL [ LAYOUT = "
                        + "BorderLayout [ LEFT = NumberWithUnit [ 10px ] "
                        + "TOP = NumberWithUnit [ 0.5cm ] "
                        + "RIGHT = NumberWithUnit [ 4px ] "
                        + "BOTTOM = NumberWithUnit [ 8px ] "
                        + "NORTHGAP = NumberWithUnit [ 1.0cm ] "
                        + "WESTGAP = NumberWithUnit [ 1px ] "
                        + "SOUTHGAP = NumberWithUnit [ 1.0dlu ] "
                        + "EASTGAP = NumberWithUnit [ 2px ] "
                        + "SHRINK = true ] ] {  } "
                        + "(CENTER) }");
    }

    /**
     * Tests a tag with an invalid number declaration.
     */
    public void testInvalidNumber() throws Exception
    {
        builderData.setBuilderName("ERR_NUMBER");
        try
        {
            executeScript(SCRIPT);
            fail("Could process invalid number!");
        }
        catch (JellyException jex)
        {
            // ok
        }
    }

    /**
     * Tests a constraints tag with missing attributes.
     */
    public void testMissingConstraints() throws Exception
    {
        builderData.setBuilderName("ERR_MISSING");
        try
        {
            executeScript(SCRIPT);
            fail("Could process tag with missing attributes!");
        }
        catch (JellyException jex)
        {
            // ok
        }
    }
}

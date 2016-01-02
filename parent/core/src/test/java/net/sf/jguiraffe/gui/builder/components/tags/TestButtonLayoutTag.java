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
 * Test class for ButtonLayoutTag.
 *
 * @author Oliver Heger
 * @version $Id: TestButtonLayoutTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestButtonLayoutTag extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "buttonlayout";

    /**
     * Tests creating layout objects.
     */
    public void testLayouts() throws Exception
    {
        checkScript(SCRIPT, "Container: ROOT [ LAYOUT = " +
                "ButtonLayout [ LEFT = NumberWithUnit [ 2.0dlu ] " +
                "TOP = NumberWithUnit [ 2.0dlu ] " +
                "RIGHT = NumberWithUnit [ 2.0dlu ] " +
                "BOTTOM = NumberWithUnit [ 2.0dlu ] " +
                "GAP = NumberWithUnit [ 1.0dlu ] ALIGN = RIGHT ] ] { " +
                "Container: PANEL [ LAYOUT = " +
                "ButtonLayout [ LEFT = NumberWithUnit [ 10px ] " +
                "TOP = NumberWithUnit [ 0.5cm ] " +
                "RIGHT = NumberWithUnit [ 4px ] " +
                "BOTTOM = NumberWithUnit [ 8px ] " +
                "GAP = NumberWithUnit [ 1.0cm ] ALIGN = CENTER ] ] {  } }");
    }

    /**
     * Tests a tag with an invalid alignment.
     */
    public void testInvalidAlign() throws Exception
    {
        builderData.setBuilderName("ERR_ALIGN");
        try
        {
            executeScript(SCRIPT);
            fail("Could process invalid alignment!");
        }
        catch(JellyException jex)
        {
            //ok
        }
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
        catch(JellyException jex)
        {
            //ok
        }
    }
}

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

import net.sf.jguiraffe.gui.layout.CellConstraints;

import org.apache.commons.jelly.JellyException;

/**
 * Test class for PercentLayoutTag and related classes.
 *
 * @author Oliver Heger
 * @version $Id: TestPercentLayoutTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestPercentLayoutTag extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "percentlayout";

    /**
     * Tests creating layout objects.
     */
    public void testLayouts() throws Exception
    {
        checkScript(
                SCRIPT,
                "Container: ROOT [ LAYOUT = "
                        + "PercentLayout [ COLS = [ end/minimum preferred 4dlu preferred ] "
                        + "ROWS = [ 3dlu preferred 4dlu preferred 3dlu ] ] ] { "
                        + "Container: PANEL [ LAYOUT = PercentLayout [ "
                        + "COLS = [ CellConstraints [ END/MINIMUM(0px)/0 ] "
                        + "CellConstraints [ FULL/PREFERRED(0px)/0 ] ] "
                        + "ROWS = [ CellConstraints [ CENTER/PREFERRED(0px)/0 ] "
                        + "CellConstraints [ CENTER/NONE(3.0dlu)/0 ] "
                        + "CellConstraints [ CENTER/PREFERRED(0px)/0 ] ] "
                        + "COLGRPS = [ CellGroup [ indices = [0, 1] ] ] "
                        + "ROWGRPS = [ CellGroup [ indices = [0, 2] ] ] "
                        + "SHRINK = true ] ] { "
                        + "LABEL [ TEXT = Testlabel ALIGN = LEFT ] "
                        + "(net.sf.jguiraffe.gui.layout.PercentData [ COL = 0 "
                        + "ROW = 0 SPANX = 2 SPANY = 1 TARGETCOL = 1 TARGETROW = 0 "
                        + "COLCONSTR = FULL/PREFERRED(0px)/0 "
                        + "ROWCONSTR = CENTER/PREFERRED(0px)/0 ]) } }");
    }

    /**
     * Tests whether a builder for constraints objects is created and stored in
     * the context.
     */
    public void testSharedConstraintsBuilder() throws Exception
    {
        executeScript(SCRIPT);
        CellConstraints.Builder builder = (CellConstraints.Builder) context
                .getVariable(PercentCellConstraintsTag.class.getName()
                        + ".columnConstraintsBuilder");
        assertNotNull("Column builder not found", builder);
        builder = (CellConstraints.Builder) context
                .getVariable(PercentCellConstraintsTag.class.getName()
                        + ".rowConstraintsBuilder");
        assertNotNull("Row builder not found", builder);
    }

    /**
     * Tests if an undefined constraints tag throws an exception.
     */
    public void testErrorUndefConstraints() throws Exception
    {
        builderData.setBuilderName("CONSTR_ERROR_BUILDER1");
        try
        {
            executeScript(SCRIPT);
            fail("Could execute undefined constraints tag!");
        }
        catch (JellyException jex)
        {
            //ok
        }
    }

    /**
     * Tests if an undefined group tag throws an exception.
     */
    public void testErrorUndefGroup() throws Exception
    {
        builderData.setBuilderName("GROUP_ERROR_BUILDER1");
        try
        {
            executeScript(SCRIPT);
            fail("Could execute undefined group tag!");
        }
        catch (JellyException jex)
        {
            //ok
        }
    }

    /**
     * Tests if a constraints tag throws an exception when defined outside a
     * percent layout tag.
     */
    public void testErrorConstrOutside() throws Exception
    {
        builderData.setBuilderName("CONSTR_ERROR_BUILDER2");
        try
        {
            executeScript(SCRIPT);
            fail("Could execute not nested constraints tag!");
        }
        catch (JellyException jex)
        {
            //ok
        }
    }

    /**
     * Tests if a group tag throws an exception when defined outside a percent
     * layout tag.
     */
    public void testErrorGroupOutside() throws Exception
    {
        builderData.setBuilderName("GROUP_ERROR_BUILDER2");
        try
        {
            executeScript(SCRIPT);
            fail("Could execute not nested group tag!");
        }
        catch (JellyException jex)
        {
            //ok
        }
    }

    /**
     * Tests if a percent layout constraints tag with undefined attributes
     * throws an exception.
     */
    public void testErrorLayoutConstrUndef1() throws Exception
    {
        builderData.setBuilderName("LAYOUT_ERROR_BUILDER1");
        try
        {
            executeScript(SCRIPT);
            fail("Could execute percent layout constraints tag with missing col attribute!");
        }
        catch (JellyException jex)
        {
            //ok
        }
    }

    /**
     * Tests if a percent layout constraints tag with undefined attributes
     * throws an exception.
     */
    public void testErrorLayoutConstrUndef2() throws Exception
    {
        builderData.setBuilderName("LAYOUT_ERROR_BUILDER2");
        try
        {
            executeScript(SCRIPT);
            fail("Could execute percent layout constraints tag with missing row attribute!");
        }
        catch (JellyException jex)
        {
            //ok
        }
    }

    /**
     * Tests if a not nested percent layout constraints tag throws an exception.
     */
    public void testErrorLayoutConstrOutside() throws Exception
    {
        builderData.setBuilderName("LAYOUT_ERROR_BUILDER3");
        try
        {
            executeScript(SCRIPT);
            fail("Could execute not nested percent layout constraints tag!");
        }
        catch (JellyException jex)
        {
            //ok
        }
    }
}

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
package net.sf.jguiraffe.gui.builder.components.tags;

import org.apache.commons.jelly.JellyException;

import net.sf.jguiraffe.gui.forms.FieldHandler;

/**
 * Test class for ComboBoxTag.
 *
 * @author Oliver Heger
 * @version $Id: TestComboBoxTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestComboBoxTag extends AbstractTagTest
{
    static final String SCRIPT = "combo";

    /**
     * Tests creating combo boxes.
     */
    public void testCreateCombos() throws Exception
    {
        checkScript(SCRIPT,
                "Container: ROOT { COMBO [ NAME = combo1 EDIT = false "
                        + "MODEL = { Spaghetti, Tortelini, Penne } ], "
                        + "COMBO [ NAME = combo2 EDIT = true MODEL = "
                        + "{ Spaghetti, Tortelini, Penne } ] }");
        FieldHandler fh = builderData.getFieldHandler("combo1");
        assertNotNull(fh);
        assertEquals(Object.class, fh.getType());
    }

    /**
     * Tests creating a combo box with an undefined model.
     */
    public void testErrNoModel() throws Exception
    {
        builderData.setBuilderName(ERROR_BUILDER);
        try
        {
            executeScript(SCRIPT);
            fail("Could execute combo box tag without a model!");
        }
        catch (JellyException jex)
        {
            //ok
        }
    }

    /**
     * Tests creating a combo box with a non existing model.
     */
    public void testErrNonExistingModel() throws Exception
    {
        builderData.setBuilderName("ERR_UNKMODEL");
        try
        {
            executeScript(SCRIPT);
            fail("Could execute combo box tag with non existing model!");
        }
        catch (JellyException jex)
        {
            //ok
        }
    }

    /**
     * Tests creating a combo box with an invalid model.
     */
    public void testErrInvalidModel() throws Exception
    {
        builderData.setBuilderName("ERR_INVMODEL");
        context.setVariable("invalidModel", this);
        try
        {
            executeScript(SCRIPT);
            fail("Could execute combo box tag with an invalid model!");
        }
        catch (JellyException jex)
        {
            //ok
        }
    }
}

/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.forms.FieldHandler;

/**
 * Test class for ListBoxTag.
 *
 * @author Oliver Heger
 * @version $Id: TestListBoxTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestListBoxTag extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "list";

    /** Constant for the text representation of the list model. */
    private static final String MODEL = "MODEL = { Spaghetti, Tortelini, Penne }";

    /**
     * Tests creating list boxes.
     */
    public void testCreateLists() throws Exception
    {
        builderData.setBuilderName("BUILDER_LISTS");
        checkScript(SCRIPT,
                "Container: ROOT { LIST [ NAME = list1 MULTI = false " + MODEL
                        + " ], " + "LIST [ NAME = list2 MULTI = true " + MODEL
                        + " ] }");
        FieldHandler fh = builderData.getFieldHandler("list1");
        assertNotNull("No field handler (1)", fh);
        assertEquals("Wrong data type of field handler (1)", Object.class, fh
                .getType());
        fh = builderData.getFieldHandler("list2");
        assertNotNull("No field handler (2)", fh);
        assertEquals("Wrong data type of field handler (2)", Object[].class, fh
                .getType());
    }

    /**
     * Tests whether the scroll size can be specified when declaring a list.
     */
    public void testCreateListScrollSize() throws Exception
    {
        builderData.setBuilderName("BUILDER_SCROLL");
        checkScript(SCRIPT,
                "Container: ROOT { LIST [ NAME = list3 MULTI = false "
                        + MODEL + " SCROLLWIDTH = NumberWithUnit [ 2.0cm ] "
                        + "SCROLLHEIGHT = NumberWithUnit [ 2.5in ]"
                        + " ] }");
    }

    /**
     * Tests creating a combo box with an undefined model.
     */
    public void testErrNoModel() throws Exception
    {
        errorScript(SCRIPT, ERROR_BUILDER,
                "Could execute combo box tag without a model!");
    }

    /**
     * Tests creating a combo box with a non existing model.
     */
    public void testErrNonExistingModel() throws Exception
    {
        errorScript(SCRIPT, "ERR_UNKMODEL",
                "Could execute combo box tag with non existing model!");
    }

    /**
     * Tests creating a combo box with an invalid model.
     */
    public void testErrInvalidModel() throws Exception
    {
        context.setVariable("invalidModel", this);
        errorScript(SCRIPT, "ERR_INVMODEL",
                "Could execute combo box tag with an invalid model!");
    }
}

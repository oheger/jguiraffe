/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.layout.NumberWithUnit;

/**
 * Test class for TextAreaTag.
 *
 * @author Oliver Heger
 * @version $Id: TestTextAreaTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTextAreaTag extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "textarea";

    /** Constant for the simple text area builder. */
    private static final String BUILDER_SIMPLE = "BUILDER_SIMPLE";

    /** Constant for the text area columns and rows builder. */
    private static final String BUILDER_COLS = "BUILDER_COLS";

    /** Constant for the text area maximum length builder. */
    private static final String BUILDER_LEN = "BUILDER_LEN";

    /** Constant for the text area scroll size builder. */
    private static final String BUILDER_SCROLL = "BUILDER_SCROLLSIZE";

    /** Constant for the name prefix of text area components. */
    private static final String COMP_PREFIX = "desc";

    /** Constant for the format for the expected script. */
    private static final String EXPECTED_FMT = "Container: ROOT { TEXTAREA [ "
            + "NAME = " + COMP_PREFIX + "%d%s ] }";

    /** Constant for the default scroll sizes. */
    private static final String SCROLL_SIZES = " SCROLLWIDTH = "
            + NumberWithUnit.ZERO + " SCROLLHEIGHT = " + NumberWithUnit.ZERO;

    /**
     * Helper method for executing a test script.
     *
     * @param builder the name of the builder
     * @param idx the index of the component
     * @param txtAreaContent the expected content of the text area declaration
     * @throws Exception if an error occurs
     */
    private void checkScript(String builder, int idx, String txtAreaContent)
            throws Exception
    {
        builderData.setBuilderName(builder);
        checkScript(SCRIPT, String.format(EXPECTED_FMT, idx, txtAreaContent));
        assertNotNull("Component handler not found", builderData
                .getFieldHandler(COMP_PREFIX + idx));
    }

    /**
     * Tests the creation of a simple text area.
     */
    public void testCreateTextAreaSimple() throws Exception
    {
        checkScript(BUILDER_SIMPLE, 1, SCROLL_SIZES);
    }

    /**
     * Tests whether columns and rows attributes are taken into account.
     */
    public void testCreateTextAreaColRows() throws Exception
    {
        checkScript(BUILDER_COLS, 2, " COLUMNS = 40 ROWS = 5 WRAP = YES"
                + SCROLL_SIZES);
    }

    /**
     * Tests whether maximum length and wrap attributes are taken into account.
     */
    public void testCreateTextAreaLen() throws Exception
    {
        checkScript(BUILDER_LEN, 3, " MAXLEN = 300" + SCROLL_SIZES);
    }

    /**
     * Tests whether the scroll size attributes are taken into account.
     */
    public void testCreateTextAreaScrollSize() throws Exception
    {
        checkScript(BUILDER_SCROLL, 4,
                " SCROLLWIDTH = NumberWithUnit [ 2.0in ]"
                        + " SCROLLHEIGHT = NumberWithUnit [ 3.0cm ]");
    }
}

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

import net.sf.jguiraffe.gui.layout.NumberWithUnit;

/**
 * Test class for SplitterTag.
 *
 * @author Oliver Heger
 * @version $Id: TestSplitterTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSplitterTag extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "splitter";

    /** Constant for the prefix of the expected result string. */
    private static final String PREFIX = "Container: ROOT { SPLITTER [ ORIENTATION = ";

    /** Constant for the text representation of a component. */
    private static final String COMP = "TEXTAREA [ NAME = comp%d SCROLLWIDTH = "
            + NumberWithUnit.ZERO
            + " SCROLLHEIGHT = "
            + NumberWithUnit.ZERO
            + " ]";

    /** Constant for the simple builder. */
    private static final String BUILDER_SIMPLE = "TEST_SIMPLE";

    /** Constant for the nested builder. */
    private static final String BUILDER_NESTED = "TEST_NESTED";

    /** Constant for the error too few builder. */
    private static final String BUILDER_ERR_TOO_FEW = "ERR_TOO_FEW";

    /** Constant for the error too many builder. */
    private static final String BUILDER_ERR_TOO_MANY = "ERR_TOO_MANY";

    /** Constant for the error invalid size builder. */
    private static final String BUILDER_ERR_SIZE = "ERR_SIZE";

    /** Constant for the error invalid resize weight builder. */
    private static final String BUILDER_ERR_RESIZEWEIGHT = "ERR_RESIZEWEIGHT";

    /** Constant for the error invalid orientation builder. */
    private static final String BUILDER_ERR_ORIENTATION = "ERR_ORIENTATION";

    /**
     * Tests creating a simple splitter.
     */
    public void testSimpleSplitter() throws Exception
    {
        builderData.setBuilderName(BUILDER_SIMPLE);
        checkScript(SCRIPT, PREFIX
                + "VERTICAL POS = 50 SIZE = 10 RESIZEWEIGHT = 0 ] { " + comp(1)
                + ", " + comp(2) + " } }");
    }

    /**
     * Tests a splitter nested within another splitter.
     */
    public void testNestedSplitter() throws Exception
    {
        builderData.setBuilderName(BUILDER_NESTED);
        checkScript(SCRIPT, PREFIX
                + "VERTICAL POS = 50 SIZE = 10 RESIZEWEIGHT = 50 ] { "
                + comp(1) + ", "
                + "SPLITTER [ ORIENTATION = HORIZONTAL POS = 25 "
                + "RESIZEWEIGHT = 100 ] { " + comp(2) + ", " + comp(3)
                + " } } }");
    }

    /**
     * Tests a splitter with too few child components.
     */
    public void testTooFewComponents() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_TOO_FEW,
                "Could create splitter with too few components!");
    }

    /**
     * Tests a splitter with too many child components.
     */
    public void testTooManyComponents() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_TOO_MANY,
                "Could create splitter with too many components!");
    }

    /**
     * Tests a splitter with an invalid size attribute.
     */
    public void testInvalidSize() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_SIZE, "Invalid size was not detected!");
    }

    /**
     * Tests a splitter with an invalid resizeWeight attribute.
     */
    public void testInvalidResizeWeight() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_RESIZEWEIGHT,
                "Invalid resizeWeight was not detected!");
    }

    /**
     * Tests a splitter with an invalid orientation attribute.
     */
    public void testInvalidOrientation() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_ORIENTATION,
                "Invalid orientation not detected!");
    }

    /**
     * Creates the text representation of a component with the given index.
     *
     * @param nr the index
     * @return the text representation for this component
     */
    private static String comp(int nr)
    {
        return String.format(COMP, nr);
    }
}

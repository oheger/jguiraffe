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

import net.sf.jguiraffe.gui.builder.components.Orientation;

/**
 * Test class of {@code SliderTag}.
 *
 * @author hacker
 * @version $Id: TestSliderTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSliderTag extends AbstractTagTest
{
    /** Constant for the test script. */
    private static final String SCRIPT = "slider";

    /** Constant for the builder for testing a default slider. */
    private static final String BUILDER_TEST_DEFAULTS = "TEST_DEFAULT";

    /** Constant for the builder for testing the slider properties. */
    private static final String BUILDER_TEST_PROPS = "TEST_PROPS";

    /** Constant for the builder that tests a missing min attribute. */
    private static final String BUILDER_ERR_MIN = "ERR_NOMIN";

    /** Constant for the builder that tests a missing max attribute. */
    private static final String BUILDER_ERR_MAX = "ERR_NOMAX";

    /** Constant for the builder that tests an invalid range. */
    private static final String BUILDER_ERR_RANGE = "ERR_RANGE";

    /** Constant for the builder that tests an invalid orientation. */
    private static final String BUILDER_ERR_ORIENT = "ERR_ORIENTATION";

    /** Constant for the name of the slider component. */
    private static final String COMP_NAME = "slider";

    /** Constant for the prefix of the expected result. */
    private static final String RESULT_PREFIX = "Container: ROOT { "
            + "SLIDER [ NAME = " + COMP_NAME;

    /** Constant for the results of the default builder. */
    private static final String RESULT_FORMAT = RESULT_PREFIX + " MIN = %d"
            + " MAX = %d ORIENTATION = %s MAJORTICKS = %d MINORTICKS = %d "
            + "SHOWTICKS = %s SHOWLABELS = %s ] }";

    /**
     * Helper method for testing the result of a builder operation that creates
     * a slider.
     *
     * @param builder the name of the builder
     * @param min the minimum
     * @param max the maximum
     * @param or the orientation
     * @param minTicks the minor ticks
     * @param maxTicks the major ticks
     * @param showTicks the show ticks flag
     * @param showLabs the show labels flag
     * @throws Exception if an error occurs
     */
    private void check(String builder, int min, int max,
            Orientation or, int minTicks, int maxTicks,
            boolean showTicks, boolean showLabs) throws Exception
    {
        builderData.setBuilderName(builder);
        String expected = String.format(RESULT_FORMAT, min, max, or.name(),
                maxTicks, minTicks, String.valueOf(showTicks), String
                        .valueOf(showLabs));
        checkScript(SCRIPT, expected);
        assertNotNull("No component handler created", builderData
                .getComponentHandler(COMP_NAME));
    }

    /**
     * Tests whether a slider with default values can be created.
     */
    public void testCreateSliderDefaults() throws Exception
    {
        check(BUILDER_TEST_DEFAULTS, 0, 99, Orientation.HORIZONTAL,
                0, 0, false, false);
    }

    /**
     * Tests whether all properties of a slider can be defined.
     */
    public void testCreateSliderAllProperties() throws Exception
    {
        check(BUILDER_TEST_PROPS, 1, 200, Orientation.VERTICAL, 5,
                10, true, true);
    }

    /**
     * Tries to define a slider without a minimum value.
     */
    public void testErrNoMin() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_MIN,
                "Missing min attribute not detected!");
    }

    /**
     * Tries to define a slider without a maximum value.
     */
    public void testErrNoMax() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_MAX,
                "Missing max attribute not detected!");
    }

    /**
     * Tests a slider with an invalid range definition.
     */
    public void testErrRange() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_RANGE, "Invalid range not detected!");
    }

    /**
     * Tests a slider with an invalid orientation.
     */
    public void testErrOrientation() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERR_ORIENT,
                "Invalid orientation not detected!");
    }
}

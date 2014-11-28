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

import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment;

/**
 * Test class for StaticTextTag.
 *
 * @author Oliver Heger
 * @version $Id: TestStaticTextTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestStaticTextTag extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "statictext";

    /** Constant for the name of the created element. */
    private static final String ELEM_NAME = "t1";

    /** Constant for the prefix of the expected result. */
    private static final String RESULT_PREFIX = "Container: ROOT { "
            + "STATICTEXT [ NAME = " + ELEM_NAME;

    /** Constant for the suffix of the expected result. */
    private static final String RESULT_SUFFIX = " ] }";

    /** Constant for the text part of the result. */
    private static final String RESULT_TEXT = " TEXT = A static text";

    /** Constant for the alignment attribute. */
    private static final String ATTR_ALIGN = " ALIGN = ";

    /** Constant for an icon definition. */
    private static final String RESULT_ICON = " ICON = ICON [ "
            + iconLocatorString() + " ]";

    /** Constant for the expected default result. */
    private static final String DEFAULT_RESULT;

    /** Constant for the test text builder. */
    private static final String BUILDER_TEXT = "TEST_TEXT";

    /** Constant for the test text resource builder. */
    private static final String BUILDER_TEXTRES = "TEST_TEXTRES";

    /** Constant for the test text resource group builder. */
    private static final String BUILDER_TEXTRESGRP = "TEST_TEXTRESGRP";

    /** Constant for the test icon builder. */
    private static final String BUILDER_ICON = "TEST_ICON";

    /** Constant for the test text and icon builder. */
    private static final String BUILDER_TEXTICON = "TEST_TEXTICON";

    /** Constant for the test empty builder. */
    private static final String BUILDER_EMPTY = "TEST_STATIC_NULL";

    /** Constant for the test add to form builder. */
    private static final String BUILDER_FORM = "TEST_FORM";

    /**
     * Creates the part of the expected result text that deals with the
     * alignment.
     *
     * @param al the alignment
     * @return the alignment text
     */
    private static String align(TextIconAlignment al)
    {
        return ATTR_ALIGN + al.name();
    }

    // static initializer
    static
    {
        DEFAULT_RESULT = RESULT_PREFIX + RESULT_TEXT
                + align(TextIconAlignment.LEFT) + RESULT_SUFFIX;
    }

    /**
     * Helper method for performing a test. This method executes the test script
     * with the specified builder and checks the expected result. It will also
     * check if a component handler was created and, optionally, whether the
     * static text element was not added to the form.
     *
     * @param builderName the name of the builder
     * @param expected the expected result
     * @param checkForm a flag whether the form is to be checked
     * @throws Exception if an error occurs
     */
    private void check(String builderName, String expected, boolean checkForm)
            throws Exception
    {
        builderData.setBuilderName(builderName);
        checkScript(SCRIPT, expected);
        assertNotNull("No component handler was created", builderData
                .getComponentHandler(ELEM_NAME));
        if (checkForm)
        {
            assertNull("Element was added to form", builderData.getForm()
                    .getField(ELEM_NAME));
        }
    }

    /**
     * Tests creating a simple static text element.
     */
    public void testCreateStaticText() throws Exception
    {
        check(BUILDER_TEXT, DEFAULT_RESULT, true);
    }

    /**
     * Tests creating a static text element whose text is fetched from a
     * resource.
     */
    public void testCreateStaticTextResource() throws Exception
    {
        check(BUILDER_TEXTRES, DEFAULT_RESULT, true);
    }

    /**
     * Tests creating a static text element with text fetched from a resource
     * and a resource group.
     */
    public void testCreateStaticTextResGrp() throws Exception
    {
        check(BUILDER_TEXTRESGRP, DEFAULT_RESULT, true);
    }

    /**
     * Tests creating a static text element that only has an icon.
     */
    public void testCreateStaticTextIcon() throws Exception
    {
        check(BUILDER_ICON, RESULT_PREFIX + RESULT_ICON
                + align(TextIconAlignment.LEFT) + RESULT_SUFFIX, true);
    }

    /**
     * Tests creating a static text element that has both a text and an icon.
     */
    public void testCreateStaticTextWithTextAndIcon() throws Exception
    {
        check(BUILDER_TEXTICON, RESULT_PREFIX + RESULT_TEXT + RESULT_ICON
                + align(TextIconAlignment.RIGHT) + RESULT_SUFFIX, true);
    }

    /**
     * Tests creating an empty static text element. In contrast to other button
     * tags this is also allowed.
     */
    public void testCreateStaticTextEmpty() throws Exception
    {
        check(BUILDER_EMPTY, RESULT_PREFIX
                + align(TextIconAlignment.LEFT) + RESULT_SUFFIX, true);
    }

    /**
     * Tests whether a static text element can be added to a form object.
     */
    public void testAddToForm() throws Exception
    {
        check(BUILDER_FORM, DEFAULT_RESULT, false);
        assertNotNull("Element was not added to form", builderData.getForm()
                .getField(ELEM_NAME));
    }
}

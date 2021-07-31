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

import java.net.URL;

import net.sf.jguiraffe.locators.ByteArrayLocator;
import net.sf.jguiraffe.locators.ClassPathLocator;
import net.sf.jguiraffe.locators.Locator;
import net.sf.jguiraffe.locators.LocatorUtils;
import net.sf.jguiraffe.locators.URLLocator;

/**
 * Test class for IconTag.
 *
 * @author Oliver Heger
 * @version $Id: TestIconTag.java 211 2012-07-10 19:49:13Z oheger $
 */
public class TestIconTag extends AbstractTagTest
{
    /** Constant for the name of the script. */
    private static final String SCRIPT = "icon";

    /** Constant for the name of the icon resource. */
    private static final String ICON_NAME = "/icon.gif";

    /** Constant for the test create icon builder. */
    private static final String TEST_ICON_BUILDER = "TEST_ICON";

    /** Constant for the variable builder. */
    private static final String TEST_VAR_BUILDER = "TEST_VAR";

    /** Constant for the test locator builder. */
    private static final String TEST_LOCATOR_BUILDER = "TEST_LOCATOR";

    /** Constant for the error stand alone builder. */
    private static final String ERR_STDALONE_BUILDER = "ERR_STAND_ALONE";

    /** Constant for the error non existing reference builder. */
    private static final String ERR_UNKNREF_BUILDER = "ERR_UNKN_REF";

    /** Constant for the error over defined builder. */
    private static final String ERR_OVERDEF_BUILDER = "ERR_OVERDEF";

    /** Constant for the error unknown locator builder. */
    private static final String ERR_UNKLOCATOR_BUIlDER = "ERR_UNKN_LOC";

    /** Constant for the error unknown class loader builder. */
    private static final String ERR_UNKCLASSLOADER_BUIlDER = "ERR_UNKN_CL";

    /** Constant for the prefix of expected results. */
    private static final String EXPECTED_PREFIX = "Container: ROOT { LABEL [ "
            + "ICON = ICON [ LOCATOR = ";

    /** Constant for the suffix of expected results. */
    private static final String EXPECTED_SUFFIX = " ] ALIGN = LEFT ] }";

    /** Constant for the expected script result. */
    private static final String EXPECTED = EXPECTED_PREFIX + "%s"
            + EXPECTED_SUFFIX;

    /** Constant for the name of the locator variable. */
    private static final String VAR_LOCATOR = "iconLocator";

    /**
     * Tests the results of a script execution assuming the specified locator
     * part.
     *
     * @param locatorPart the part of the locator
     */
    private void checkLocatorResults(String locatorPart)
    {
        checkResult(String.format(EXPECTED, locatorPart));
    }

    /**
     * Executes the test script and checks the results. This method expects as
     * result an icon definition embedded in a label. The given parameter is the
     * variable part defining the icon locator.
     *
     * @param locatorPart the part with the locator
     * @throws Exception if an error occurs
     */
    private void checkScript(String locatorPart) throws Exception
    {
        executeScript(SCRIPT);
        checkLocatorResults(locatorPart);
    }

    /**
     * Tests loading an icon from an URL.
     */
    public void testLoadFromURL() throws Exception
    {
        final URL iconURL = getClass().getResource(ICON_NAME);
        assertNotNull("Could not resolve icon URL!", iconURL);
        StringBuilder buf = new StringBuilder();
        buf.append("  <f:label>\n");
        buf.append("    <f:icon url=\"").append(iconURL).append("\"/>\n");
        buf.append("  </f:label>\n");
        executeScript(createTestScript(buf.toString()));
        Locator locator = URLLocator.getInstance(iconURL);
        checkLocatorResults(LocatorUtils.locatorToDataString(locator));
    }

    /**
     * Tests loading an icon from a resource name.
     */
    public void testLoadFromResource() throws Exception
    {
        builderData.setBuilderName(TEST_ICON_BUILDER);
        Locator loc = ClassPathLocator.getInstance("myicon.gif");
        checkScript(LocatorUtils.locatorToDataString(loc));
    }

    /**
     * Tests if an icon can be saved in a variable and later be referred to.
     */
    public void testReference() throws Exception
    {
        builderData.setBuilderName(TEST_VAR_BUILDER);
        Locator loc = ClassPathLocator.getInstance("myicon.gif");
        checkScript(LocatorUtils.locatorToDataString(loc));
        assertNotNull("Icon was not found in context", context
                .getVariable("varIcon"));
    }

    /**
     * Tests loading an icon from an arbitrary locator.
     */
    public void testLoadFromLocator() throws Exception
    {
        builderData.setBuilderName(TEST_LOCATOR_BUILDER);
        Locator loc = ByteArrayLocator.getInstance(VAR_LOCATOR);
        context.setVariable(VAR_LOCATOR, loc);
        checkScript(LocatorUtils.locatorToDataString(loc));
    }

    /**
     * Tests an invalid icon declaration.
     */
    public void testInvalidIcon() throws Exception
    {
        errorScript(SCRIPT, ERROR_BUILDER, "Could execute undefined tag!");
    }

    /**
     * Tests a stand alone icon tag.
     */
    public void testStandAloneIcon() throws Exception
    {
        errorScript(SCRIPT, ERR_STDALONE_BUILDER,
                "Could execute stand alone icon tag!");
    }

    /**
     * Tests an icon tag that refers to an unknown reference. This should cause
     * an exception.
     */
    public void testUnknownReference() throws Exception
    {
        errorScript(SCRIPT, ERR_UNKNREF_BUILDER,
                "Could reference unknown variable!");
    }

    /**
     * Tests an icon tag with too many definition attributes. This should cause
     * an exception.
     */
    public void testOverdefined() throws Exception
    {
        errorScript(SCRIPT, ERR_OVERDEF_BUILDER,
                "Too many definition attributes were not detected!");
    }

    /**
     * Tests an icon tag that refers to an unknown locator. This should cause an
     * exception.
     */
    public void testUnknownLocator() throws Exception
    {
        errorScript(SCRIPT, ERR_UNKLOCATOR_BUIlDER,
                "Invalid locator reference not detected!");
    }

    /**
     * Tests an icon tag which references an unknown class loader.
     */
    public void testUnknownClassLoader() throws Exception
    {
        errorScript(SCRIPT, ERR_UNKCLASSLOADER_BUIlDER,
                "Unknown class loader not detected!");
    }
}

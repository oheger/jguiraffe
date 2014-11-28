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

import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.WidgetHandler;

import org.apache.commons.jelly.JellyException;

/**
 * Test class for the label tag.
 *
 * @author Oliver Heger
 * @version $Id: TestLabelTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestLabelTag extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "label";

    /** Constant for the error unknown reference builder. */
    private static final String BUILDER_ERRREF = "ERR_REF";

    /** Constant for the test get text builder. */
    private static final String BUILDER_GETTEXT = "TEST_GETTEXT";

    /** Constant for the test get no text builder. */
    private static final String BUILDER_GETNOTEXT = "TEST_GETNOTEXT";

    /** Constant for test named label builder. */
    private static final String BUILDER_NAMEDLABEL = "TEST_NAMEDLABEL";

    /** Constant for the test font builder. */
    private static final String BUILDER_FONT = "TEST_FONT";

    /** Constant for the prefix of expected results. */
    private static final String RESULT_PREFIX = "Container: ROOT { ";

    /** Constant for the expected result of the builder operation. */
    private static final String EXPECTED = RESULT_PREFIX + "LABEL [ "
            + "TEXT = Hello world! ALIGN = LEFT MNEMO = w FCOL = "
            + colorString(0, 0, 255) + " TOOLTIP = Label test tool tip ],"
            + " LABEL [ ICON = ICON [ " + iconLocatorString()
            + " ] ALIGN = CENTER BCOL = " + colorString(0, 0, 0) + " ],"
            + " LABEL [ TEXT = Label1 ALIGN = LEFT "
            + "TOOLTIP = Tool tip for label 1 ],"
            + " LABEL [ TEXT = Label2 ALIGN = LEFT MNEMO = L "
            + "COMP = TestComponent "
            + "TOOLTIP = Tool tip for the second label ]<linked>,"
            + " TEXTFIELD [ NAME = TestComponent ] }";

    /**
     * Tests a jelly script that executes the label tag.
     */
    public void testLabelTag() throws Exception
    {
        executeScript(SCRIPT);
        builderData.invokeCallBacks();
        checkResult(EXPECTED);
    }

    /**
     * Tests a label tag with missing attributes.
     */
    public void testLabelError() throws Exception
    {
        try
        {
            builderData.setBuilderName(ERROR_BUILDER);
            executeScript(SCRIPT);
            fail("Could execute invalid script!");
        }
        catch (JellyException jex)
        {
            // ok
        }
    }

    /**
     * Tests a component reference to an non existing component.
     */
    public void testInvalidRef() throws Exception
    {
        builderData.setBuilderName(BUILDER_ERRREF);
        executeScript(SCRIPT);
        try
        {
            builderData.invokeCallBacks();
            fail("Could link to unexisting component!");
        }
        catch (FormBuilderException fex)
        {
            // ok
        }
    }

    /**
     * Tests whether the text of the label can be obtained from a linked
     * component.
     */
    public void testGetTextFromComponent() throws Exception
    {
        builderData.setBuilderName(BUILDER_GETTEXT);
        executeScript(SCRIPT);
        builderData.invokeCallBacks();
        checkResult(RESULT_PREFIX + "LABEL [ ALIGN = LEFT COMP = testComponent1 "
                + "]<TEXT -> MyDisplay><linked>, "
                + "TEXTFIELD [ NAME = testComponent1 DISP = MyDisplay ] }");
    }

    /**
     * Tests a label with no text, but an icon, that is linked to a component.
     * In this case no text should be assigned from the component.
     */
    public void testGetTextFromComponentIcon() throws Exception
    {
        builderData.setBuilderName(BUILDER_GETNOTEXT);
        executeScript(SCRIPT);
        builderData.invokeCallBacks();
        checkResult(RESULT_PREFIX + "LABEL [ ICON = ICON [ "
                + iconLocatorString()
                + " ] ALIGN = LEFT COMP = testComponent2 ]"
                + "<linked>, TEXTFIELD [ NAME = testComponent2 ] }");
    }

    /**
     * Tests whether for a named label a widget handler can be obtained.
     */
    public void testNamedLabel() throws Exception
    {
        builderData.setBuilderName(BUILDER_NAMEDLABEL);
        executeScript(SCRIPT);
        WidgetHandler wh = builderData.getWidgetHandler("testLabel");
        assertNotNull("No widget handler found", wh);
        assertTrue("Wrong label text", wh.getWidget().toString().indexOf(
                "Test label") >= 0);
    }

    /**
     * Tests whether a font can be assigned to a label.
     */
    public void testFont() throws Exception
    {
        builderData.setBuilderName(BUILDER_FONT);
        Map<Object, Object> attrs = new HashMap<Object, Object>();
        attrs.put("fontAttr", "value");
        context.setVariable("fontAttrs", attrs);
        checkScript(SCRIPT, RESULT_PREFIX
                + "LABEL [ TEXT = Label with a special font!"
                + " ALIGN = LEFT FONT = FONT [ NAME = MyFont SIZE = 20 "
                + "BOLD = false ITALIC = true ATTRS = " + attrs + " ] ] }");
    }
}

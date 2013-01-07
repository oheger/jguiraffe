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

import java.util.Iterator;

import net.sf.jguiraffe.gui.builder.components.ComponentGroup;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.model.AbstractRadioButtonHandler;
import net.sf.jguiraffe.gui.builder.components.model.DefaultRadioButtonHandler;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.FieldHandler;

/**
 * Test class for {@code RadioButtonTag} that executes a test script.
 * {@code RadioGroupTag} is tested as well.
 *
 * @author Oliver Heger
 * @version $Id: TestRadioButtonTagScript.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestRadioButtonTagScript extends AbstractTagTest
{
    /** Constant for the expected result of the script execution. */
    private static final String EXPECTED = "Container: ROOT {"
            + " RADIO [ BCOL = (255, 255, 255) NAME = rad1 TEXT = Magherita "
            + "ICON = ICON [ " + iconLocatorString() + " ] ALIGN = CENTER ],"
            + " RADIO [ NAME = rad2 TEXT = Calzone ALIGN = LEFT ],"
            + " RADIO [ NAME = rad3 TEXT = Hawaii ALIGN = LEFT MNEMO = H ],"
            + " RADIO [ NAME = rad4 TEXT = Toscana ALIGN = LEFT ] }";

    /** Constant for the test script. */
    private static final String SCRIPT = "radio";

    /** Constant for the name of the test radio group. */
    private static final String GROUP_NAME = "pizze";

    /** Constant for the in-group builder. */
    private static final String BUILDER_INGROUP = "BUILDER_INGROUP";

    /** Constant for the external group builder. */
    private static final String BUILDER_EXGROUP = "BUILDER_EXGROUP";

    /** Constant for the mixed group builder. */
    private static final String BUILDER_MIXED = "BUILDER_MIXED";

    /** Constant for the handler builder. */
    private static final String BUILDER_HANDLER = "BUILDER_HANDLER";

    /** Constant for the error handler builder. */
    private static final String BUILDER_ERRHANDLER = "BUILDER_ERRHANDLER";

    /**
     * Helper method for testing whether the radio group was created correctly.
     *
     * @throws FormBuilderException if an error occurs
     */
    private void checkGroup() throws FormBuilderException
    {
        builderData.invokeCallBacks();
        ComponentGroup group = ComponentGroup.fromContext(context, GROUP_NAME);
        int idx = 1;
        for (Iterator<String> it = group.getComponentNames().iterator(); it
                .hasNext(); idx++)
        {
            assertEquals("Wrong element name at " + idx, "rad" + idx, it.next());
        }

        Object radioGroup = builderData.getComponent(GROUP_NAME);
        assertNotNull("Radio group not created", radioGroup);
        assertEquals("Wrong content of readio group",
                "RADIOGROUP { rad1, rad2, rad3, rad4 }", radioGroup);

        FieldHandler radioHandler = builderData.getFieldHandler(GROUP_NAME);
        assertNotNull("No field handler created for radio group", radioHandler);
        assertTrue("Wrong component handler: "
                + radioHandler.getComponentHandler(), radioHandler
                .getComponentHandler() instanceof DefaultRadioButtonHandler);
        DefaultRadioButtonHandler ch = (DefaultRadioButtonHandler) radioHandler
                .getComponentHandler();
        assertEquals("Wrong number of child handlers", 4, ch
                .getChildHandlerCount());
    }

    /**
     * Tests executing a valid Jelly script with radio buttons defined in the
     * body of a group tag.
     */
    public void testCreateRadioButtonsInGroup() throws Exception
    {
        builderData.setBuilderName(BUILDER_INGROUP);
        checkScript(SCRIPT, EXPECTED);
        assertNotNull("No field handler created", builderData
                .getFieldHandler("rad2"));
        checkGroup();
    }

    /**
     * Tests a Jelly script with a radio group and external button declarations.
     */
    public void testCreateRadioButtonsExternalGroup() throws Exception
    {
        builderData.setBuilderName(BUILDER_EXGROUP);
        checkScript(SCRIPT, EXPECTED);
        assertNull("Got a field handler", builderData.getFieldHandler("rad1"));
        assertNotNull("No field handler for rad2", builderData
                .getFieldHandler("rad2"));
        checkGroup();
    }

    /**
     * Tests a Jelly script with a radio group with both internal and external
     * button declarations.
     */
    public void testCreateRadioButtonsMixedGroup() throws Exception
    {
        builderData.setBuilderName(BUILDER_MIXED);
        checkScript(SCRIPT, EXPECTED);
        checkGroup();
    }

    /**
     * Tests whether a different component handler for the button group can be
     * specified.
     */
    public void testCreateRadioButtonsHandler() throws Exception
    {
        builderData.setBuilderName(BUILDER_HANDLER);
        checkScript(SCRIPT, EXPECTED);
        ComponentHandler<?> ch = builderData.getFieldHandler(GROUP_NAME)
                .getComponentHandler();
        assertTrue("Wrong component handler: " + ch,
                ch instanceof RadioButtonHandlerTestImpl);
    }

    /**
     * Tests the behavior if a group contains an invalid button reference.
     */
    public void testInvalidButtonReference() throws Exception
    {
        builderData.setBuilderName(BUILDER_INGROUP);
        executeScript(SCRIPT);
        ComponentGroup group = ComponentGroup.fromContext(context, GROUP_NAME);
        group.addComponent("Invalid Radio Button!");

        try
        {
            builderData.invokeCallBacks();
            fail("Could refer to invalid radio button!");
        }
        catch (FormBuilderException fex)
        {
            // ok
        }
    }

    /**
     * Tests whether an invalid component handler is detected.
     */
    public void testInvalidComponentHandler() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERRHANDLER,
                "Invalid component handler not detected!", false);
    }

    /**
     * A specialized radio button handler for testing whether an alternate
     * handler can be specified.
     */
    public static class RadioButtonHandlerTestImpl extends
            AbstractRadioButtonHandler<String>
    {
        public RadioButtonHandlerTestImpl()
        {
            super(String.class);
        }

        @Override
        protected int getButtonIndex(String value)
        {
            return 0;
        }

        @Override
        protected String getDataForButton(int idx)
        {
            return String.valueOf(idx);
        }
    }
}

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
package net.sf.jguiraffe.gui.forms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationMessageLevel;
import net.sf.jguiraffe.transform.ValidationResult;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link FormValidationMessageFormat}.
 *
 * @author Oliver Heger
 * @version $Id: TestFormValidationMessageFormat.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFormValidationMessageFormat
{
    /** Constant for the number of error fields. */
    private static final int ERR_FIELDS = 3;

    /** Constant for the field name prefix. */
    private static final String FIELD = "inputField";

    /** Constant for the field display name prefix. */
    private static final String DISPLAY = "displayField";

    /** Constant for the prefix for validation error messages. */
    private static final String MSG = "A validation error message";

    /** Constant for the prefix for validation warning messages. */
    private static final String MSG_WARN = "Just a warning message";

    /** Constant for the template used for warnings in the tests. */
    private static final String WARNING_TEMPLATE = "${field}: (${msg})\n";

    /** The format object to be tested. */
    private FormValidationMessageFormat format;

    @Before
    public void setUp() throws Exception
    {
        format = new FormValidationMessageFormat();
    }

    /**
     * Creates a mock for a validation message.
     *
     * @param msg the message
     * @param level the validation message level
     * @return the mock for the validation message
     */
    private static ValidationMessage setUpValidationMessage(String msg,
            ValidationMessageLevel level)
    {
        ValidationMessage vm = EasyMock.createMock(ValidationMessage.class);
        EasyMock.expect(vm.getMessage()).andReturn(msg);
        EasyMock.expect(vm.getLevel()).andReturn(level).anyTimes();
        EasyMock.replay(vm);
        return vm;
    }

    /**
     * Creates an object with validation results. This object contains error
     * messages for <code>ERR_FIELDS</code> fields. The first field has 1 error
     * message, the 2nd has 2 and so on. Optionally, the same number of warning
     * messages can be added for each field.
     *
     * @param warnings a flag whether warning messages should be generated, too
     * @return the initialized validation results objects
     */
    private static FormValidatorResults setUpValidationResults(boolean warnings)
    {
        Map<String, ValidationResult> map = new LinkedHashMap<String, ValidationResult>();
        for (int i = 1; i <= ERR_FIELDS; i++)
        {
            DefaultValidationResult.Builder builder = new DefaultValidationResult.Builder();
            for (int j = 1; j <= i; j++)
            {
                builder.addValidationMessage(setUpValidationMessage(
                        errMsg(j, i), ValidationMessageLevel.ERROR));
                if (warnings)
                {
                    builder.addValidationMessage(setUpValidationMessage(
                            warnMsg(j, i), ValidationMessageLevel.WARNING));
                }
            }
            map.put(FIELD + i, builder.build());
        }

        return new DefaultFormValidatorResults(map);
    }

    /**
     * Helper method for generating the text for a validation message.
     *
     * @param prefix the message prefix
     * @param msgIdx the message index
     * @param fldIdx the field index
     * @return the message
     */
    private static String validationMessage(String prefix, int msgIdx,
            int fldIdx)
    {
        StringBuilder buf = new StringBuilder();
        buf.append(prefix);
        buf.append('[').append(msgIdx).append(',').append(fldIdx).append(']');
        return buf.toString();
    }

    /**
     * Helper method for creating an error message for the specified message and
     * field index.
     *
     * @param msgIdx the message index
     * @param fldIdx the field index
     * @return the message
     */
    private static String errMsg(int msgIdx, int fldIdx)
    {
        return validationMessage(MSG, msgIdx, fldIdx);
    }

    /**
     * Helper method for creating a warning message for the specified message
     * and field index.
     *
     * @param msgIdx the message index
     * @param fldIdx the field index
     * @return the message
     */
    private static String warnMsg(int msgIdx, int fldIdx)
    {
        return validationMessage(MSG_WARN, msgIdx, fldIdx);
    }

    /**
     * Creates the text that is expected for the default formatting templates.
     *
     * @param warnings a flag whether warnings should be produced
     * @param prefix the prefix for warning messages
     * @param suffix the suffix for warning messages
     * @return the expected default text
     */
    private static String expectedDefaultText(boolean warnings, String prefix,
            String suffix)
    {
        StringBuilder buf = new StringBuilder();
        for (int i = 1; i <= ERR_FIELDS; i++)
        {
            buf
                    .append(expectedDefaultTextForField(i, warnings, prefix,
                            suffix));
        }
        return buf.toString();
    }

    /**
     * Creates the text that is expected for the default formatting templates
     * for the field with the specified index.
     *
     * @param fieldIdx the index of the field
     * @param warnings a flag whether warnings should be produced
     * @param prefix the prefix for warning messages
     * @param suffix the suffix for warning messages
     * @return the expected default text
     */
    private static String expectedDefaultTextForField(int fieldIdx,
            boolean warnings, String prefix, String suffix)
    {
        StringBuilder buf = new StringBuilder();
        for (int j = 1; j <= fieldIdx; j++)
        {
            buf.append(DISPLAY).append(fieldIdx).append(": ");
            buf.append(errMsg(j, fieldIdx));
            buf.append('\n');
        }

        if (warnings)
        {
            for (int j = 1; j <= fieldIdx; j++)
            {
                buf.append(DISPLAY).append(fieldIdx).append(": ");
                if (prefix != null)
                {
                    buf.append(prefix);
                }
                buf.append(warnMsg(j, fieldIdx));
                if (suffix != null)
                {
                    buf.append(suffix);
                }
                buf.append('\n');
            }
        }

        return buf.toString();
    }

    /**
     * Tests a newly created object.
     */
    @Test
    public void testInit()
    {
        assertEquals("No standard error template",
                FormValidationMessageFormat.DEF_ERRORS_TEMPLATE, format
                        .getFieldErrorTemplate());
        assertNull("Header template set", format.getFieldHeaderTemplate());
        assertNull("Footer template set", format.getFieldHeaderTemplate());
        assertNull("Warning template set", format.getFieldWarningTemplate());
        assertFalse("Warnings are suppressed", format.isSuppressWarnings());
    }

    /**
     * Tests formatting with the default templates.
     */
    @Test
    public void testFormatDefault()
    {
        assertEquals("Wrong text", expectedDefaultText(false, null, null),
                format
                        .format(setUpValidationResults(false),
                                new FormTestImpl()));
    }

    /**
     * Tests the default format if warning messages are available.
     */
    @Test
    public void testFormatDefaultWithWarnings()
    {
        assertEquals("Wrong text", expectedDefaultText(true, null, null),
                format.format(setUpValidationResults(true), new FormTestImpl()));
    }

    /**
     * Tests whether warning messages can be suppressed.
     */
    @Test
    public void testFormatSuppressWarnings()
    {
        format.setSuppressWarnings(true);
        assertEquals("Wrong text", expectedDefaultText(false, null, null),
                format.format(setUpValidationResults(true), new FormTestImpl()));
    }

    /**
     * Tests whether a specific template for warning messages can be set.
     */
    @Test
    public void testFormatWarningsTemplate()
    {
        format.setFieldWarningTemplate(WARNING_TEMPLATE);
        assertEquals("Wrong text", expectedDefaultText(true, "(", ")"), format
                .format(setUpValidationResults(true), new FormTestImpl()));
    }

    /**
     * Tests using complex formatting templates.
     */
    @Test
    public void testFormatComplex()
    {
        StringBuilder buf = new StringBuilder();
        for (int i = 1; i <= ERR_FIELDS; i++)
        {
            buf.append("Field ").append(DISPLAY).append(i).append(" (");
            buf.append(i).append(" message(s)):\n");
            for (int j = 1; j <= i; j++)
            {
                buf.append("Message ").append(j).append(" of ").append(i);
                buf.append(" for field ").append(DISPLAY).append(i);
                buf.append(":\n  \"").append(errMsg(j, i)).append("\"\n");
            }
            buf.append("End of field\n");
        }
        format
                .setFieldHeaderTemplate("Field ${field} (${msgCount} message(s)):\n");
        format
                .setFieldErrorTemplate("Message ${msgIndex} of ${msgCount} for field ${field}:\n  \"${msg}\"\n");
        format.setFieldFooterTemplate("End of field\n");
        assertEquals("Wrong text", buf.toString(), format.format(
                setUpValidationResults(false), new FormTestImpl()));
    }

    /**
     * Tests the format() method when no Form object is passed in. Then no
     * display names can be resolved.
     */
    @Test
    public void testFormatDefaultNoForm()
    {
        String expected = StringUtils.replace(expectedDefaultText(false, null,
                null), DISPLAY, FIELD);
        assertEquals("Wrong text", expected, format.format(
                setUpValidationResults(false), null));
    }

    /**
     * Tests the format() method when no templates are set.
     */
    @Test
    public void testFormatNoTemplates()
    {
        format.setFieldErrorTemplate(null);
        assertEquals("Wrong text", "", format.format(
                setUpValidationResults(true), new FormTestImpl()));
    }

    /**
     * Tests formatting a null results object. Result is again null.
     */
    @Test
    public void testFormatNull()
    {
        assertNull("Wrong result for null object", format.format(null,
                new FormTestImpl()));
    }

    /**
     * Tests formatField() if no results object is passed in.
     */
    @Test
    public void testFormatFieldNull()
    {
        assertNull("Wrong result for null object", format.formatField(null,
                new FormTestImpl(), FIELD));
    }

    /**
     * Tests formatField() if the field name cannot be resolved.
     */
    @Test
    public void testFormatFieldUnknownField()
    {
        assertEquals("Wrong result for unknown field", "", format.formatField(
                setUpValidationResults(true), new FormTestImpl(), FIELD));
    }

    /**
     * Tests formatField() if there are no warnings.
     */
    @Test
    public void testFormatFieldNoWarnings()
    {
        final int fieldIdx = 1;
        String expected = expectedDefaultTextForField(fieldIdx, false, null,
                null);
        format.setSuppressWarnings(true);
        assertEquals("Wrong result", expected, format.formatField(
                setUpValidationResults(true), new FormTestImpl(), FIELD
                        + fieldIdx));
    }

    /**
     * Tests formatField() if warnings should be taken into account.
     */
    @Test
    public void testFormatFieldWithWarnings()
    {
        final int fieldIdx = ERR_FIELDS;
        String expected = expectedDefaultTextForField(fieldIdx, true, "(", ")");
        format.setFieldWarningTemplate(WARNING_TEMPLATE);
        assertEquals("Wrong result", expected, format.formatField(
                setUpValidationResults(true), new FormTestImpl(), FIELD
                        + fieldIdx));
    }

    /**
     * Tests whether header and footer are taken into account by formatField().
     */
    @Test
    public void testFormatFieldHeaderFooter()
    {
        final int fieldIdx = 2;
        final String header = "Header!\n";
        final String footer = "That's it!";
        String expected = header
                + expectedDefaultTextForField(fieldIdx, true, "(", ")")
                + footer;
        format.setFieldWarningTemplate(WARNING_TEMPLATE);
        format.setFieldFooterTemplate(footer);
        format.setFieldHeaderTemplate(header);
        assertEquals("Wrong result", expected, format.formatField(
                setUpValidationResults(true), new FormTestImpl(), FIELD
                        + fieldIdx));
    }

    /**
     * A specialized form implementation allowing us to return controlled
     * display names for the input fields.
     */
    private static class FormTestImpl extends Form
    {
        public FormTestImpl()
        {
            super(new TransformerContextImpl(), new BeanBindingStrategy());
        }

        @Override
        public String getDisplayName(String fldName)
        {
            assertTrue("Wrong field name", fldName.startsWith(FIELD));
            String suffix = fldName.substring(FIELD.length());
            return DISPLAY + suffix;
        }
    }
}

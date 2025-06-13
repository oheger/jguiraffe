/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationMessageLevel;
import net.sf.jguiraffe.transform.ValidationResult;

import org.apache.commons.lang.text.StrSubstitutor;

/**
 * <p>
 * A class for converting a {@link FormValidatorResults} object to text.
 * </p>
 * <p>
 * If validation of a form fails, usually the corresponding error messages have
 * to be displayed somehow to the user. This class takes a
 * <code>FormValidationResults</code> object as input, iterates over the
 * contained validation error messages and converts them to text. The result is
 * a string that can be displayed in a message box for instance.
 * </p>
 * <p>
 * The class can be configured with a number of template strings for specifying
 * the desired output. There are four template strings that are evaluated:
 * <ol>
 * <li>The class iterates over all fields in the passed in {@code
 * FormValidatorResults} object, for which error messages are found. Whenever a
 * new field starts the {@code fieldHeaderTemplate} is issued.</li>
 * <li>Then the single validation messages available for this field are
 * processed. To each error message the {@code fieldErrorTemplate} template is
 * applied.</li>
 * <li>After the error messages the warning messages (if available) are
 * processed. To each warning message the {@code fieldWarningTemplate} template
 * is applied. If the {@code fieldWarningTemplate} is not defined, the {@code
 * fieldErrorTemplate} is used instead. The output of warning messages can be
 * suppressed at all by setting the {@code suppressWarnings} property to
 * <b>true</b>.</li>
 * <li>Finally, the {@code fieldFooterTemplate} template is output.</li>
 * </ol>
 * </p>
 * <p>
 * Templates are strings that can contain variables for the name of the current
 * field and the current error message. When processing the templates the
 * variables are replaced with their current values resulting in the text to be
 * displayed. For variables the syntax <code>${...}</code> is used, which should
 * be familiar from Ant. The following table lists the supported variables:
 * <table border="1">
 * <tr>
 * <th>Variable</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td valign="top">field</td>
 * <td>Will be replaced by the name of the current field. This variable is
 * allowed in all templates.</td>
 * </tr>
 * <tr>
 * <td valign="top">msgCount</td>
 * <td>Will be replaced by the number of error messages for the current field.
 * This could for instance be used in the header template to give an overview
 * over the number of errors detected for the current input field.</td>
 * </tr>
 * <tr>
 * <td valign="top">msg</td>
 * <td>Will be replaced by the current error message. This variable is only
 * supported by the <code>fieldErrorTemplate</code> template.</td>
 * </tr>
 * <tr>
 * <td valign="top">msgIndex</td>
 * <td>Will be replaced by the index of the current error message. The messages
 * of a field are numbered from 1 up to <code>${msgCount}</code>. With this
 * variable the index can be added to the resulting text.</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * The default initialization leaves the header and footer template empty and
 * sets the following error template: <code>${field}: ${msg}\n</code>. This
 * results in output like
 *
 * <pre>
 * Field1: Message1 for Field1
 * Field2: Message1 for Field2
 * Field2: Message2 for Field2
 * Field3: Message1 for Field3
 * </pre>
 *
 * By carefully designing the templates, more complex output can be generated as
 * in the following example:
 * <ul>
 * <li>fieldHeaderTemplate = <code>${field} ${msgCount} error(s):\n</code></li>
 * <li>fieldErrorTemplate = <code>- ${msg}</code></li>
 * <li>fieldFooterTemplate = <code>\n</code></li>
 * </ul>
 * In this example the output will look like the following:
 *
 * <pre>
 * Field1 1 error(s):
 * - Message1 for Field1
 * Field2 2 error(s):
 * - Message1 for Field2
 * - Message2 for Field2
 * </pre>
 *
 * Templates that are <b>null</b> will be ignored.
 * </p>
 * <p>
 * Implementation note: This class has a mutable state and thus is not
 * thread-safe. However if it is initialized once and the templates are not
 * changed later, it can be shared between multiple threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormValidationMessageFormat.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormValidationMessageFormat
{
    /** Constant for the default field errors template. */
    public static final String DEF_ERRORS_TEMPLATE = "${field}: ${msg}\n";

    /** Constant for the field variable. */
    public static final String VAR_FIELD = "field";

    /** Constant for the msg variable. */
    public static final String VAR_MSG = "msg";

    /** Constant for the msgCount variable. */
    public static final String VAR_MSG_COUNT = "msgCount";

    /** Constant for the msgIndex variable. */
    public static final String VAR_MSG_INDEX = "msgIndex";

    /** Constant for the initial buffer size. */
    private static final int BUF_SIZE = 256;

    /** Stores the template for the header of a field. */
    private String fieldHeaderTemplate;

    /** Stores the template for the footer of a field. */
    private String fieldFooterTemplate;

    /** Stores the template for the error messages of a field. */
    private String fieldErrorTemplate;

    /** Stores the template for the warning messages of a field. */
    private String fieldWarningTemplate;

    /** A flag whether warning messages should be suppressed. */
    private boolean suppressWarnings;

    /**
     * Creates a new instance of <code>FormValidationMessageFormat</code>.
     */
    public FormValidationMessageFormat()
    {
        setFieldErrorTemplate(DEF_ERRORS_TEMPLATE);
    }

    /**
     * Returns the template for the header of a field.
     *
     * @return the field header template
     */
    public String getFieldHeaderTemplate()
    {
        return fieldHeaderTemplate;
    }

    /**
     * Sets the template for the header of a field. This template will be
     * processed at the beginning of a new input field with validation error
     * messages.
     *
     * @param fieldHeaderTemplate the template for the header of a field
     */
    public void setFieldHeaderTemplate(String fieldHeaderTemplate)
    {
        this.fieldHeaderTemplate = fieldHeaderTemplate;
    }

    /**
     * Returns the template for the footer of a field.
     *
     * @return the field footer template
     */
    public String getFieldFooterTemplate()
    {
        return fieldFooterTemplate;
    }

    /**
     * Sets the template for the footer of a field. This template will be
     * processed after the error messages of an input field have been output.
     *
     * @param fieldFooterTemplate the template for the footer of a field
     */
    public void setFieldFooterTemplate(String fieldFooterTemplate)
    {
        this.fieldFooterTemplate = fieldFooterTemplate;
    }

    /**
     * Returns the template for the error messages of an input field.
     *
     * @return the error messages template
     */
    public String getFieldErrorTemplate()
    {
        return fieldErrorTemplate;
    }

    /**
     * Sets the template for the error messages of an input field. For each
     * validation error message associated with an input field this template
     * will be processed.
     *
     * @param fieldErrorTemplate the field error template
     */
    public void setFieldErrorTemplate(String fieldErrorTemplate)
    {
        this.fieldErrorTemplate = fieldErrorTemplate;
    }

    /**
     * Returns the template for the warning messages of an input field.
     *
     * @return the warning messages template
     */
    public String getFieldWarningTemplate()
    {
        return fieldWarningTemplate;
    }

    /**
     * Sets the template for the warning messages of an input field. For each
     * validation warning message associated with an input field this template
     * will be processed. Warning messages are only processed if the {@code
     * suppressWarnings} property is not set. If {@code suppressWarnings} is
     * <b>false</b> and no specific template for warning messages is set, the
     * error template is used.
     *
     * @param fieldWarningTemplate the field warnings template
     */
    public void setFieldWarningTemplate(String fieldWarningTemplate)
    {
        this.fieldWarningTemplate = fieldWarningTemplate;
    }

    /**
     * Returns a flag whether warning messages should be suppressed.
     *
     * @return the suppress warnings flag
     */
    public boolean isSuppressWarnings()
    {
        return suppressWarnings;
    }

    /**
     * Sets a flag whether warning messages should be suppressed. If this
     * message is called with the parameter <b>true</b>, the output generated by
     * this object contains only error messages.
     *
     * @param suppressWarnings the suppress warnings flag
     */
    public void setSuppressWarnings(boolean suppressWarnings)
    {
        this.suppressWarnings = suppressWarnings;
    }

    /**
     * The main formatting method. Transforms the passed in validation result
     * object into a text according to the values of the current templates.
     *
     * @param res the object with the validation results (can be <b>null</b>,
     *        then the result of this method is <b>null</b>)
     * @param form the current <code>Form</code> object; this object is used for
     *        obtaining the display names of the error fields; it can be
     *        <b>null</b>, then the field names are used
     * @return the corresponding text
     */
    public String format(FormValidatorResults res, Form form)
    {
        if (res == null)
        {
            return null;
        }

        StringBuilder buf = new StringBuilder(BUF_SIZE);
        for (String field : res.getErrorFieldNames())
        {
            processField(buf, res, form, field);
        }

        return buf.toString();
    }

    /**
     * Transforms all validation messages found in the passed {@code
     * FormValidatorResults} object for the given field name into a text
     * according to the values of the current templates. This method can be
     * called to process the messages of a single field only.
     *
     * @param res the object with the validation results (can be <b>null</b>,
     *        then the result of this method is <b>null</b>)
     * @param form the current <code>Form</code> object; this object is used for
     *        obtaining the display names of the error fields; it can be
     *        <b>null</b>, then the field names are used
     * @param field the name of the field in question (if this field cannot be
     *        found, result is an empty string)
     * @return the corresponding text
     */
    public String formatField(FormValidatorResults res, Form form, String field)
    {
        if (res == null)
        {
            return null;
        }

        StringBuilder buf = new StringBuilder(BUF_SIZE);
        processField(buf, res, form, field);
        return buf.toString();
    }

    /**
     * Initializes the variables for the specified field of the validation
     * results object. This method is invoked at the beginning of the processing
     * of a new field.
     *
     * @param vars the map with the variables
     * @param res the results object
     * @param form the form object (may be <b>null</b>)
     * @param field the name of the current field
     */
    protected void setUpVariablesForField(Map<String, String> vars,
            FormValidatorResults res, Form form, String field)
    {
        vars.put(VAR_FIELD, fetchDisplayName(form, field));
        vars.put(VAR_MSG_COUNT, String.valueOf(res.getResultsFor(field)
                .getValidationMessages().size()));
    }

    /**
     * Initializes the variables for an error message. This method is invoked
     * for each error message of a field. The passed in parameters represent the
     * information available for the current error message. The variables for
     * the field have already been initialized (
     * <code>setUpVariablesForField()</code> has already been called).
     *
     * @param vars the map with the variables
     * @param res the results object
     * @param form the form object (may be <b>null</b>)
     * @param field the name of the current field
     * @param msg the current validation error message
     * @param index the index of this message
     */
    protected void setUpVariablesForMessage(Map<String, String> vars,
            FormValidatorResults res, Form form, String field, String msg,
            int index)
    {
        vars.put(VAR_MSG, msg);
        vars.put(VAR_MSG_INDEX, String.valueOf(index));
    }

    /**
     * Applies the template for the error messages to all messages available for
     * the current field.
     *
     * @param buf the target buffer
     * @param subst the substitutor
     * @param res the validation results
     * @param form the form
     * @param field the current field
     * @param variables the map with the variables
     */
    protected void processMessages(StringBuilder buf, StrSubstitutor subst,
            FormValidatorResults res, Form form, String field,
            Map<String, String> variables)
    {
        int index = 1;
        int count = processMessagesOfLevel(buf, subst, res, form, field,
                variables, ValidationMessageLevel.ERROR,
                getFieldErrorTemplate(), index);

        if (!isSuppressWarnings())
        {
            index += count;
            String template = (getFieldWarningTemplate() != null) ? getFieldWarningTemplate()
                    : getFieldErrorTemplate();
            processMessagesOfLevel(buf, subst, res, form, field, variables,
                    ValidationMessageLevel.WARNING, template, index);
        }
    }

    /**
     * Determines the display name of the given field. If a <code>Form</code>
     * object is provided, it is used for resolving the display name. Otherwise
     * the field name is returned.
     *
     * @param form the form
     * @param field the field name
     * @return the display name
     */
    protected String fetchDisplayName(Form form, String field)
    {
        return (form != null) ? form.getDisplayName(field) : field;
    }

    /**
     * Helper method for generating output for all messages for the given field
     * with the specified validation level. This method is called for each field
     * to process for the validation levels to take into account.
     *
     * @param buf the target buffer
     * @param subst the substitutor
     * @param res the validation results
     * @param form the form
     * @param field the current field
     * @param variables the map with the variables
     * @param level the validation message level
     * @param template the template to use
     * @param index the index of the first field
     * @return the number of fields processed
     */
    private int processMessagesOfLevel(StringBuilder buf, StrSubstitutor subst,
            FormValidatorResults res, Form form, String field,
            Map<String, String> variables, ValidationMessageLevel level,
            String template, int index)
    {
        ValidationResult vres = res.getResultsFor(field);
        int count = 0;

        if (vres != null)
        {
            for (ValidationMessage msg : vres.getValidationMessages(level))
            {
                setUpVariablesForMessage(variables, res, form, field, msg
                        .getMessage(), count + index);
                buf.append(subst.replace(template));
                count++;
            }
        }

        return count;
    }

    /**
     * Produces output for the specified field. This helper method applies all
     * templates to the messages of the specified field.
     *
     * @param buf the target buffer
     * @param res the results object
     * @param form the form
     * @param field the field to be processed
     */
    private void processField(StringBuilder buf, FormValidatorResults res,
            Form form, String field)
    {
        if (!res.getFieldNames().contains(field))
        {
            return;
        }

        Map<String, String> variables = new HashMap<String, String>();
        setUpVariablesForField(variables, res, form, field);
        StrSubstitutor subst = new StrSubstitutor(variables);

        if (getFieldHeaderTemplate() != null)
        {
            buf.append(subst.replace(getFieldHeaderTemplate()));
        }

        if (getFieldErrorTemplate() != null)
        {
            processMessages(buf, subst, res, form, field, variables);
        }

        if (getFieldFooterTemplate() != null)
        {
            buf.append(subst.replace(getFieldFooterTemplate()));
        }
    }
}

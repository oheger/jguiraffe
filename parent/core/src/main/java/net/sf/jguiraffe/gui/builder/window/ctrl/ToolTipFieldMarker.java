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
package net.sf.jguiraffe.gui.builder.window.ctrl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.components.ToolTipManager;
import net.sf.jguiraffe.gui.forms.FormValidationMessageFormat;
import net.sf.jguiraffe.transform.ValidationResult;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A specialized {@code FormControllerValidationListener} implementation that is
 * able to adapt the tool tips of input elements for which validation messages
 * are available.
 * </p>
 * <p>
 * An instance of this class can be registered as validation listener at a
 * {@link FormController}. Whenever a validation event is received the instance
 * iterates over the validation results for all input fields. The texts for
 * error and warning messages are extracted and combined. Then they are set as
 * additional tool tip for the corresponding input element. This is an easy but
 * powerful means to give feedback about validation results to the user: the
 * user just have to move the mouse over a control, and all related validation
 * messages are displayed.
 * </p>
 * <p>
 * The tool tip texts produced by this class can be configured by associating an
 * instance with a {@link FormValidationMessageFormat} object. This object knows
 * how to translate validation messages to text. It is invoked for each field
 * with validation messages, and the text produced by it is set as additional
 * tool tip text.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe. An instance can be
 * associated with a single {@code FormController} only.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ToolTipFieldMarker.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ToolTipFieldMarker implements FormControllerValidationListener
{
    /** The object for formatting validation messages. */
    private final FormValidationMessageFormat format;

    /** Stores the names of the fields for which tool tips have been created. */
    private Set<String> changedFields;

    /**
     * Creates a new instance of {@code ToolTipFieldMarker} and initializes it
     * with the given {@code FormValidationMessageFormat} object.
     *
     * @param messageFormat the {@code FormValidationMessageFormat} object (must
     *        not be <b>null</b>)
     * @throws IllegalArgumentException if the format object is <b>null</b>
     */
    public ToolTipFieldMarker(FormValidationMessageFormat messageFormat)
    {
        if (messageFormat == null)
        {
            throw new IllegalArgumentException(
                    "Format object must not be null!");
        }

        format = messageFormat;
        changedFields = Collections.emptySet();
    }

    /**
     * A validation operation has been performed. This implementation determines
     * the fields for which validation messages exist. It creates corresponding
     * tool tips for the messages and sets them.
     *
     * @param event the validation event
     */
    public void validationPerformed(FormControllerValidationEvent event)
    {
        // stores the names of the fields affected by this operation
        Set<String> fields = new HashSet<String>();
        ToolTipManager manager = event.getFormController()
                .getComponentBuilderData().getToolTipManager();

        // process all fields with validation messages
        for (String field : event.getValidationResults().getFieldNames())
        {
            ValidationResult vres = event.getValidationResults().getResultsFor(
                    field);
            if (!vres.getValidationMessages().isEmpty())
            {
                String tip = format.formatField(event.getValidationResults(),
                        event.getFormController().getForm(), field);
                if (StringUtils.isNotEmpty(tip))
                {
                    manager.setAdditionalToolTip(field, tip);
                    fields.add(field);
                }
            }
        }

        // reset all fields that are now valid
        for (String field : changedFields)
        {
            if (!fields.contains(field))
            {
                manager.setAdditionalToolTip(field, null);
            }
        }

        changedFields = fields;
    }
}

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
package net.sf.jguiraffe.gui.builder.window.ctrl;

import net.sf.jguiraffe.gui.forms.FormValidatorResults;

/**
 * <p>
 * A specialized {@code FormControllerEvent} class that is generated when the
 * controller performs a validation.
 * </p>
 * <p>
 * Events of this type are sent to {@link FormControllerValidationListener}
 * objects registered at a {@link FormController} whenever a validation
 * operation was performed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormControllerValidationEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormControllerValidationEvent extends FormControllerEvent
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20091209L;

    /** The validation results. */
    private final transient FormValidatorResults validationResults;

    /**
     * Creates a new instance of {@code FormControllerValidationEvent} and
     * initializes it with the {@code FormController} that is the source of this
     * event and the results of the validation operation.
     *
     * @param source the source {@code FormController} (must not be <b>null</b>)
     * @param results the results of the validation operation (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is missing
     */
    public FormControllerValidationEvent(FormController source,
            FormValidatorResults results)
    {
        super(source);
        if (results == null)
        {
            throw new IllegalArgumentException(
                    "Validation results must not be null!");
        }

        validationResults = results;
    }

    /**
     * Returns the {@code FormValidatorResults} object with the results of the
     * validation operation.
     *
     * @return the {@code FormValidatorResults} object
     */
    public FormValidatorResults getValidationResults()
    {
        return validationResults;
    }
}

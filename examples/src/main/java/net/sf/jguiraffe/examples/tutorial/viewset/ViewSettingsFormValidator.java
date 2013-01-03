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
package net.sf.jguiraffe.examples.tutorial.viewset;

import java.util.Map;

import net.sf.jguiraffe.gui.forms.DefaultFormValidatorResults;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.forms.FormValidator;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;
import net.sf.jguiraffe.transform.ValidationResult;

/**
 * <p>
 * A {@code FormValidator} implementation for validating the dialog with view
 * settings.
 * </p>
 * <p>
 * This class performs specific validations that involve multiple fields and
 * thus cannot be achieved by simple {@code Validator} implementations. The
 * following validation rules are implemented:
 * <ul>
 * <li>If the check box for filtering file types is checked, at least one file
 * type must have been selected.</li>
 * <li>If the check box for filtering by file size is checked, a valid file
 * minimum size must have been entered.</li>
 * <li>If the check box for filtering by file date is checked, the from date
 * and/or the to date must have been provided.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ViewSettingsFormValidator.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ViewSettingsFormValidator implements FormValidator
{
    /** Constant for the field for the file types. */
    private static final String FLD_FILETYPES = "fileTypes";

    /** Constant for the field for the minimum file size. */
    private static final String FLD_MINFILESIZE = "minFileSize";

    /** Constant for the field with the from date. */
    private static final String FLD_FROMDATE = "fileDateFrom";

    /** Constant for the error message "no file types". */
    private static final String ERR_NOFILETYPES = "ERR_NOFILETYPES";

    /** Constant for the error message "no minimum file size". */
    private static final String ERR_NOFILESIZE = "ERR_NOFILESIZE";

    /** Constant for the error message "no date range". */
    private static final String ERR_NOFILEDATE = "ERR_NOFILEDATE";

    /** Constant for the error message "invalid date range". */
    private static final String ERR_FILEDATEINV = "ERR_FILEDATEINV";

    /**
     * Validates the specified {@code Form} instance.
     *
     * @param form the {@code Form}
     * @return an object with the results of the validation
     */
    @Override
    public FormValidatorResults isValid(Form form)
    {
        // Create a map with valid result objects as basis
        Map<String, ValidationResult> resultMap = DefaultFormValidatorResults
                .validResultMapForForm(form);

        // Obtain the data entered into the form
        ViewSettings settings = new ViewSettings();
        form.readFields(settings);

        // Now perform the specific checks
        if (settings.isFilterTypes())
        {
            if (settings.getFileTypes().length < 1)
            {
                resultMap.put(FLD_FILETYPES, DefaultFormValidatorResults
                        .createValidationErrorResult(form, ERR_NOFILETYPES));
            }
        }

        if (settings.isFilterSize())
        {
            if (settings.getMinFileSize() == null)
            {
                resultMap.put(FLD_MINFILESIZE, DefaultFormValidatorResults
                        .createValidationErrorResult(form, ERR_NOFILESIZE));
            }
        }

        if (settings.isFilterDate())
        {
            if (settings.getFileDateFrom() == null
                    && settings.getFileDateTo() == null)
            {
                resultMap.put(FLD_FROMDATE, DefaultFormValidatorResults
                        .createValidationErrorResult(form, ERR_NOFILEDATE));
            }
            else
            {
                if (settings.getFileDateFrom() != null
                        && settings.getFileDateTo() != null
                        && settings.getFileDateTo().before(
                                settings.getFileDateFrom()))
                {
                    resultMap
                            .put(FLD_FROMDATE, DefaultFormValidatorResults
                                    .createValidationErrorResult(form,
                                            ERR_FILEDATEINV));
                }
            }
        }

        // Return the validation results
        return new DefaultFormValidatorResults(resultMap);
    }
}

/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import net.sf.jguiraffe.gui.app.Application;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;
import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationResult;

/**
 * <p>
 * A default implementation of the {@code TableEditorValidationHandler}
 * interface.
 * </p>
 * <p>
 * This implementation simply concatenates all error messages contained in the
 * passed in {@link FormValidatorResults} objects and displays them in a message
 * box. (For this purpose the application's {@link MessageOutput} object is
 * used.) If validation was successful, no action is performed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.3
 */
public class DefaultTableEditorValidationHandler implements
        TableEditorValidationHandler
{
    /** Constant for the field name separator. */
    private static final String FLD_NAMESEP = ": ";

    /** Constant for the validation message separator. */
    private static final String VAL_MSGSEP = "\n";

    /** Constant for the initial buffer size. */
    private static final int BUF_SIZE = 128;

    /** Stores a reference to the central application object. */
    private Application application;

    /** Stores a reference to the message output object to be used. */
    private MessageOutput output;

    /**
     * Returns the message output object used for displaying message boxes.
     *
     * @return the output object
     */
    public MessageOutput getOutput()
    {
        return output;
    }

    /**
     * Sets the message output object to be used for displaying message boxes.
     * This object can be <b>null</b>, then no messages will be displayed.
     *
     * @param output the output object to be used
     */
    public void setOutput(MessageOutput output)
    {
        this.output = output;
    }

    /**
     * Returns a reference to the {@code Application} object. This object is
     * used internally for accessing some global information.
     *
     * @return a reference to the used {@code Application} object (can be
     *         <b>null</b>)
     */
    public Application getApplication()
    {
        return application;
    }

    /**
     * Initializes this object with a reference to the central
     * {@code Application} object. If no {@code MessageOutput} object has been
     * set so far, this implementation obtains the {@code MessageOutput} from
     * the application. It will be used in {@code validationPerformed()} for
     * displaying error messages. The {@code Application} object itself is
     * stored, too because it is needed for accessing some global information.
     * If you want to use a different {@code MessageOutput} object than the one
     * associated with the application, you have to call {@code setOutput()}
     * explicitly.
     *
     * @param app the central {@code Application} object
     */
    public void setApplication(Application app)
    {
        application = app;
        if (output == null)
        {
            setOutput(app.getApplicationContext().getMessageOutput());
        }
    }

    /**
     * Reacts on a validation. If some fields are invalid, and the current
     * message output object is not <b>null</b>, and the reference to the
     * central {@code Application} object has been initialized, a message box
     * will be displayed.
     *
     * @param table the table component (this is expected to be of class
     *        {@code javax.swing.JTable})
     * @param editForm the editor form
     * @param tableTag the tag with the table definition
     * @param results the object with the validation results
     * @param row the current row index
     * @param col the current column index
     * @return a flag whether field values have been modified (this
     *         implementation always returns <b>false</b>)
     */
    public boolean validationPerformed(Object table, Form editForm,
            TableTag tableTag, FormValidatorResults results, int row, int col)
    {
        if (!results.isValid() && getOutput() != null
                && getApplication() != null)
        {
            StringBuilder buf = new StringBuilder(BUF_SIZE);
            boolean first = true;
            for (String fld : results.getErrorFieldNames())
            {
                String fldName = editForm.getDisplayName(fld);
                ValidationResult vr = results.getResultsFor(fld);
                for (ValidationMessage msg : vr.getValidationMessages())
                {
                    if (first)
                    {
                        first = false;
                    }
                    else
                    {
                        buf.append(VAL_MSGSEP);
                    }

                    buf.append(fldName);
                    buf.append(FLD_NAMESEP);
                    buf.append(msg.getMessage());
                }
            }

            getOutput().show(
                    getApplication().getApplicationContext().getMainWindow(),
                    buf.toString(), tableTag.getValidationErrorTitle(),
                    MessageOutput.MESSAGE_ERROR, MessageOutput.BTN_OK);
        }

        return false;
    }
}

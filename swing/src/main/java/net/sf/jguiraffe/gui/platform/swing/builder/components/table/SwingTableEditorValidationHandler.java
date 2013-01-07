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
package net.sf.jguiraffe.gui.platform.swing.builder.components.table;

import net.sf.jguiraffe.gui.app.Application;
import net.sf.jguiraffe.gui.app.ApplicationClient;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableEditorValidationHandler;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;
import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationResult;

/**
 * <p>
 * A Swing-specific default implementation of the
 * <code>{@link TableEditorValidationHandler}</code> interface.
 * </p>
 * <p>
 * This implementation simply concatenates all error messages contained in the
 * passed in <code>{@link FormValidatorResults}</code> objects and displays
 * them in a message box. (For this purpose the application's
 * <code>{@link MessageOutput}</code> object is used.) If validation was
 * successful, no action is performed.
 * </p>
 * <p>
 * The class also implements the <code>{@link ApplicationClient}</code>
 * interface so that instances are automatically initialized with a reference to
 * the current application object when they are created.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingTableEditorValidationHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingTableEditorValidationHandler implements
        TableEditorValidationHandler, ApplicationClient
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
     * Returns a reference to the <code>Application</code> object. This object
     * will be used for resolving the resource identifiers of the validation
     * error messages.
     *
     * @return a reference to the used <code>Application</code> object (can be
     * <b>null</b>)
     */
    public Application getApplication()
    {
        return application;
    }

    /**
     * Initializes this object with a reference to the central
     * <code>Application</code> object. This implementation obtains the
     * <code>MessageOutput</code> from the application. It will be used in
     * <code>validationPerformed()</code> for displaying error messages. The
     * <code>Application</code> object itself will also be stored; it is
     * needed for resolving resource keys. If you want to use a different
     * <code>MessageOutput</code> object than the one associated with the
     * application, you have to call this method first and after that
     * <code>setOutput()</code>.
     *
     * @param app the central <code>Application</code> object
     * @see Application#getMessageOutput()
     */
    public void setApplication(Application app)
    {
        application = app;
        setOutput(app.getApplicationContext().getMessageOutput());
    }

    /**
     * Reacts on a validation. If some fields are invalid, and the current
     * message output object is not <b>null</b>, and the reference to the
     * central <code>Application</code> object has been initialized, a message
     * box will be displayed.
     *
     * @param table the table component (this is expected to be of class
     * <code>javax.swing.JTable</code>)
     * @param editForm the editor form
     * @param tableTag the tag with the table definition
     * @param results the object with the validation results
     * @param row the current row index
     * @param col the current column index
     * @return a flag whether field values have been modified (this
     * implementation always returns <b>false</b>)
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

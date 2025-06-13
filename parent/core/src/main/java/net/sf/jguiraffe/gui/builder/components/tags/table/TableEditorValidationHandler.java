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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;

/**
 * <p>
 * Definition of an interface to be implemented by objects interested in
 * validation events triggered by a column editor.
 * </p>
 * <p>
 * For the single columns of a table special editors or validators can be
 * defined. If validation fails, the user should somehow be notified to get a
 * feedback what he or she has done wrong. This is the purpose of this
 * interface: Its single method is invoked whenever user input entered into a
 * table gets validated (also if validation is successful). An implementation
 * can react on validation errors in a suitable manner.
 * </p>
 * <p>
 * Platform-specific default implementations of this interface are available and
 * are constructed for table components automatically. These implementations
 * display validation errors in a message box if validation fails. Only if a
 * different behavior is desired, custom implementations need to be created.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TableEditorValidationHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface TableEditorValidationHandler
{
    /**
     * Validation has been performed for a column editor of a table. An
     * implementation can now react on the results of this validation. The
     * passed in parameters provide all available information about the affected
     * column and the <code>Form</code> object used for editing. An
     * appropriate default behavior would be to display the validation errors
     * (if any). But an implementation is not limited to this action: It can
     * even manipulate the entered values. To achieve this the form can be
     * directly manipulated. Then <b>true</b> must be returned (which means
     * that the form's fields should again be queried). A return value of
     * <b>false</b> means that no further steps need to be performed.
     *
     * @param table the table component
     * @param editForm the editor form used for this table
     * @param tableTag the tag describing the affected table
     * @param results the object with the validation results
     * @param row the index of the affected row
     * @param col the index of the affected column
     * @return a flag whether the entered values should be read again from the
     * form's fields; an implementation should return <b>true</b> if it has
     * manipulated the content of the fields
     */
    boolean validationPerformed(Object table, Form editForm,
            TableTag tableTag, FormValidatorResults results, int row, int col);
}

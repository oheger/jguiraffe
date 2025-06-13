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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.DummyWrapper;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;
import net.sf.jguiraffe.gui.forms.TransformerWrapper;
import net.sf.jguiraffe.gui.forms.ValidatorWrapper;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;
import net.sf.jguiraffe.gui.layout.UnitSizeHandler;

/**
 * <p>
 * A helper class for dealing with {@code Form} objects related to table
 * components.
 * </p>
 * <p>
 * The implementation of tables for different UI platforms in <em>JGUIraffe</em>
 * requires some common functionality for dealing with the single properties of
 * model beans. When defining a table {@code Form} instances are created
 * allowing the manipulation of properties via the API provided by this class.
 * This also includes support for data type transformation and validation.
 * Nevertheless, there is still some boiler-plate code necessary to interact
 * with these forms, e.g. to retrieve the values to be displayed in a table
 * column or to write back data the user has changed.
 * </p>
 * <p>
 * This class aims at providing this functionality in a central place so that
 * platform-specific implementations of table components can be simplified.
 * Before version 1.3 of this library this code was mainly contained in the
 * Swing-specific table implementation. It is now refactored so that it can be
 * reused by other implementations, too.
 * </p>
 * <p>
 * This class uses a form-based approach for accessing the table model data of a
 * specific row. This means that if a new row is selected for being rendered or
 * edited, the data of the model is loaded into the row render and editor forms.
 * From there it can be read (and even updated) using the typical API offered by
 * the {@code Form} class.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.3
 */
public class TableFormController
{
    /**
     * Constant for an index of a non-existing row. This constant is used to
     * indicate that no row is selected.
     */
    private static final int INVALID_ROW = -1;

    /** The table tag with the definition of the whole table. */
    private final TableTag tableTag;

    /** The data model of the table as a list of beans. */
    private final List<Object> dataModel;

    /** The factory for creating transformers. */
    private final TransformerFactory transformerFactory;

    /** The index of the current row. */
    private int currentRow = INVALID_ROW;

    /**
     * Creates a new instance of {@code TableFormController} and initializes it
     * from the passed in {@code TableTag}.
     *
     * @param tabTag the {@code TableTag} (must not be <b>null</b>)
     * @throws IllegalArgumentException if the passed in tag is <b>null</b>
     */
    public TableFormController(TableTag tabTag)
    {
        this(tabTag, new TransformerFactory());
    }

    /**
     * Creates a new instance of {@code TableFormController} and allows setting
     * the dependencies to helper objects. This constructor is mainly used for
     * testing purposes.
     *
     * @param tabTag the {@code TableTag} (must not be <b>null</b>)
     * @param factory the {@code TransformerFactory}
     */
    TableFormController(TableTag tabTag, TransformerFactory factory)
    {
        if (tabTag == null)
        {
            throw new IllegalArgumentException("Tag must not be null!");
        }

        tableTag = tabTag;
        dataModel = createModel(tabTag);
        transformerFactory = factory;
    }

    /**
     * Returns the list serving as data model for the managed table. Note that
     * the list is directly returned, no defensive copy is created. Callers are
     * responsible for performing only valid modifications if any!
     *
     * @return the data model list
     */
    public List<Object> getDataModel()
    {
        return dataModel;
    }

    /**
     * Returns the number of rows in the data model of the managed table.
     *
     * @return the number of rows in the table
     */
    public int getRowCount()
    {
        return getDataModel().size();
    }

    /**
     * Returns the number of columns of the managed table.
     *
     * @return the number of columns
     */
    public int getColumnCount()
    {
        return getTableTag().getColumnCount();
    }

    /**
     * Returns the title of the column with the given index. This string can be
     * placed in the column header.
     *
     * @param col the index of the column in question
     * @return the title of this column
     */
    public String getColumnName(int col)
    {
        return getColumn(col).getHeaderText().getCaption();
    }

    /**
     * Returns the field name of the specified column in the render or edit
     * form. In the forms created for a table for each column per default a
     * field is created. This method can be used to determine the field name of
     * a column.
     *
     * @param col the index of the column in question
     * @return the name of the field associated with this column in the table
     *         forms
     */
    public String getColumnFieldName(int col)
    {
        return getColumn(col).getName();
    }

    /**
     * A convenience method which returns the bean from the data model with the
     * specified row index. Indices are 0-based.
     *
     * @param row the row index
     * @return the data object at this row index in the table model
     */
    public Object getModelBean(int row)
    {
        return getDataModel().get(row);
    }

    /**
     * Tells this controller that the specified row becomes the current row.
     * This method causes some initializations to be made. Namely, the forms
     * representing the content of the row are initialized.
     *
     * @param row the index of the new current row
     */
    public void selectCurrentRow(int row)
    {
        if (row != currentRow)
        {
            currentRow = row;
            Object bean = getModelBean(row);
            getTableTag().getRowRenderForm().initFields(bean);
            getTableTag().getRowEditForm().initFields(bean);
        }
    }

    /**
     * Resets the current row index. This is the opposite of the
     * {@code selectCurrentRow()} method. It invalidates the index of the
     * current row. Calling this method makes sense for instance if there have
     * been changes on the underlying data model.
     */
    public void resetCurrentRow()
    {
        currentRow = INVALID_ROW;
    }

    /**
     * Notifies this controller that a range of rows has changed in the
     * underlying table model. If the current row is affected, it is reset.
     *
     * @param fromIdx the start row index of the affected range
     * @param toIdx the end row index of the affected change (including)
     */
    public void invalidateRange(int fromIdx, int toIdx)
    {
        if (fromIdx <= currentRow && toIdx >= currentRow)
        {
            resetCurrentRow();
        }
    }

    /**
     * Returns the value from the given column in the current row. The value is
     * obtained from the field associated with this column from the row
     * rendering form. Note: Before this method can be used,
     * {@code selectCurrentRow()} must have been called first.
     *
     * @param col the index of the column in question
     * @return the value of this column for the current row
     */
    public Object getColumnValue(int col)
    {
        return getRenderField(col).getComponentHandler()
                .getData();
    }

    /**
     * Sets the value for the given column in the current row. This method can
     * be used for modifying the value of a cell if no special edit form is
     * specified for this column. (If there is an edit form, this method
     * performs no action.) The value is written into the edit form, and
     * validation is performed. The table's validation handler is invoked with
     * the results of the validation. Depending on this, the table is updated or
     * the changes are discarded. Note: Before this method can be used,
     * {@code selectCurrentRow()} must have been called first.
     *
     * @param table the current table component
     * @param col the index of the column in question
     * @param value the value to be set for this cell
     */
    public void setColumnValue(Object table, int col, Object value)
    {
        if (!hasEditor(col))
        {
            ComponentHandler ch =
                    getTableTag().getRowEditForm()
                            .getField(getColumn(col).getName())
                            .getComponentHandler();
            /* This causes an unchecked warning, but we cannot do much
             * about it. If the data type is wrong, a CCE is thrown.
             */
            ch.setData(value);
            validateColumn(table, col);
        }
    }

    /**
     * Validates a column in the current row. This method can be called after
     * the user has edited a cell in the table. It performs a validation on all
     * fields displayed in this table. With the results of this validation the
     * {@code TableEditorValidationHandler} is invoked. This object also
     * determines how to handle validation errors. If everything goes well, the
     * data from the edit form is copied into the model bean for the current
     * row.
     *
     * @param table the current table component
     * @param col the index of the column in question
     * @return a flag whether validation was successful
     */
    public boolean validateColumn(Object table, int col)
    {
        Set<String> fields = getEditFields(col);
        FormValidatorResults vres =
                getTableTag().getRowEditForm().validateFields(fields);

        boolean forceValues;
        if (getTableTag().getEditorValidationHandler().validationPerformed(
                table, getTableTag().getRowEditForm(), getTableTag(), vres,
                currentRow, col))
        {
            // The validation handler has manipulated the field values;
            // fetch them again
            getTableTag().getRowEditForm().validateFields(fields);
            forceValues = true;
        }
        else
        {
            forceValues = false;
        }

        if (forceValues || vres.isValid())
        {
            getTableTag().getRowEditForm().readFields(getModelBean(currentRow),
                    fields);
            return true;
        }
        return false;
    }

    /**
     * Returns the renderer component installed for the specified column. Result
     * is <b>null</b> if no renderer was set for this column.
     *
     * @param col the column index
     * @return the renderer component for this column or <b>null</b>
     */
    public Object getColumnRenderer(int col)
    {
        return getColumn(col).getRendererComponent();
    }

    /**
     * Returns the editor component installed for the specified column. Result
     * is <b>null</b> if no editor was set for this column.
     *
     * @param col the column index
     * @return the editor component for this column or <b>null</b>
     */
    public Object getColumnEditor(int col)
    {
        return getColumn(col).getEditorComponent();
    }

    /**
     * Checks whether for the specified column a custom editor is specified.
     *
     * @param col the column index
     * @return a flag if this column has its own editor
     */
    public boolean hasEditor(int col)
    {
        return getColumnEditor(col) != null;
    }

    /**
     * Checks whether for the specified column a custom renderer is specified.
     *
     * @param col the column index
     * @return a flag if this column has its own renderer component
     */
    public boolean hasRenderer(int col)
    {
        return getColumnRenderer(col) != null;
    }

    /**
     * Returns a flag whether the specified column is declared to be editable.
     *
     * @param col the column index
     * @return <b>true</b> if this column should be editable, <b>false</b>
     *         otherwise
     */
    public boolean isColumnEditable(int col)
    {
        return getTableTag().isColumnEditable(getColumn(col));
    }

    /**
     * Returns a flag whether the table is editable.
     *
     * @return the editable flag for the whole table
     */
    public boolean isTableEditable()
    {
        return getTableTag().isTableEditable();
    }

    /**
     * Returns the logic data class of the specified column. Result may be
     * <b>null</b> if no logic column class was assigned. In this case, the
     * {@code Class} object returned by {@code getDataClass()} may be used to
     * determine the column type.
     *
     * @param col the column index
     * @return the logic column class (may be <b>null</b>)
     */
    public ColumnClass getLogicDataClass(int col)
    {
        return getColumn(col).getLogicDataClass();
    }

    /**
     * Returns the data class for the specified column. Normally, it is
     * preferred to use a logic column class. Applications can also specify a
     * &quot;real&quot; class, but the platform in use must then support this
     * class. This method never returns <b>null</b>; if no explicit data class
     * was set, the generic {@code Object} class is returned.
     *
     * @param col the column index
     * @return the data class of this column
     */
    public Class<?> getDataClass(int col)
    {
        return getColumn(col).getDataClass();
    }

    /**
     * Returns the {@code TableColumnWidthController} for the represented table.
     * This implementation obtains the controller from the associated
     * {@code TableTag}.
     *
     * @return the {@code TableColumnWidthController}
     * @throws FormBuilderException if the width controller cannot be obtained
     * @see TableTag#getColumnWidthController()
     */
    public TableColumnWidthController getColumnWidthController()
            throws FormBuilderException
    {
        return getTableTag().getColumnWidthController();
    }

    /**
     * Convenience method for querying the {@code TableColumnRecalibrator}. This
     * method returns the {@code TableColumnWidthController} which also
     * implements this interface.
     *
     * @return the {@code TableColumnRecalibrator}
     * @throws FormBuilderException if this object cannot be obtained
     */
    public TableColumnRecalibrator getColumnRecalibrator()
            throws FormBuilderException
    {
        return getColumnWidthController();
    }

    /**
     * Convenience method for querying the {@code TableColumnWidthCalculator}.
     * This method returns the {@code TableColumnWidthController} which also
     * implements this interface.
     *
     * @return the {@code TableColumnWidthCalculator}
     * @throws FormBuilderException if this object cannot be obtained
     */
    public TableColumnWidthCalculator getColumnWidthCalculator()
            throws FormBuilderException
    {
        return getColumnWidthController();
    }

    /**
     * Installs special transformers and validators for the field associated
     * with the specified column based on its {@code ColumnClass}. This method
     * checks whether a logic type is defined for this column, if it does not
     * have a renderer, and if no transformers and validators are defined. If
     * all these conditions are met, corresponding transformers and validators
     * are created for the column's logic type and installed at the field
     * handlers of the row forms.
     *
     * @param col the column index
     * @return a flag whether transformers were installed
     */
    public boolean installTransformersForColumnType(int col)
    {
        ColumnClass columnClass = getLogicDataClass(col);
        if (columnClass != null && !hasRenderer(col))
        {
            FieldHandler field = getRenderField(col);
            if (hasNoTransformer(field))
            {
                TransformerWrapper readTransformer =
                        getTransformerFactory().getReadTransformer(
                                getTableTag(), columnClass);
                TransformerWrapper writeTransformer =
                        getTransformerFactory().getWriteTransformer(
                                getTableTag(), columnClass);
                ValidatorWrapper validator =
                        getTransformerFactory().getValidator(getTableTag(),
                                columnClass);
                installTransformer(field, readTransformer, writeTransformer,
                        validator);

                field =
                        getTableTag().getRowEditForm().getField(
                                getColumnFieldName(col));
                installTransformer(field, readTransformer, writeTransformer,
                        validator);
                return true;
            }
        }

        return false;
    }

    /**
     * Determines the fixed size columns of the represented table and
     * initializes their widths in the {@code TableColumnWidthController} of the
     * current table.
     *
     * @param sizeHandler the {@code UnitSizeHandler}
     * @param container the enclosing container object
     * @return the total width of all columns with a fixed column width
     * @throws FormBuilderException if an error occurs
     */
    public int calculateFixedColumnWidths(UnitSizeHandler sizeHandler,
            Object container) throws FormBuilderException
    {
        TableColumnWidthController widthController =
                getTableTag().getColumnWidthController();
        int totalWidth = 0;

        for (int i = 0; i < getColumnCount(); i++)
        {
            NumberWithUnit columnWidth = getColumn(i).getColumnWidth();
            if (columnWidth != null)
            {
                int width = columnWidth.toPixel(sizeHandler, container, false);
                widthController.setFixedWidth(i, width);
                totalWidth += width;
            }
        }

        return totalWidth;
    }

    /**
     * Returns a flag whether multi-selection mode is enabled for the associated
     * table.
     *
     * @return a flag whether multi-selection mode is enabled
     * @since 1.3.1
     */
    public boolean isMultiSelection()
    {
        return getTableTag().isMultiSelection();
    }

    /**
     * Returns the {@code TableTag} wrapped by this controller.
     *
     * @return the underlying {@code TableTag}
     */
    TableTag getTableTag()
    {
        return tableTag;
    }

    /**
     * Returns the {@code TransformerFactory} used by this controller.
     *
     * @return the {@code TransformerFactory}
     */
    TransformerFactory getTransformerFactory()
    {
        return transformerFactory;
    }

    /**
     * Returns a set with the names of all fields that are part of the edit form
     * for the specified column. If there is no special edit form for this
     * column, it only contains the single field it is associated with.
     *
     * @param col the index of the column in question
     * @return a set with the names of the fields in this column
     */
    private Set<String> getEditFields(int col)
    {
        Set<String> editFields = getColumn(col).getEditFields();
        return editFields.isEmpty() ? Collections
                .singleton(getColumnFieldName(col)) : editFields;
    }

    /**
     * Returns the {@code TableColumnTag} for the column with the given index.
     *
     * @param col the index of the column in question
     * @return the tag representing this column
     */
    private TableColumnTag getColumn(int col)
    {
        return getTableTag().getColumn(col);
    }

    /**
     * Returns the {@code FieldHandler} from the render form for the specified
     * column.
     *
     * @param col the column index
     * @return the {@code FieldHandler} for this column in the render form
     */
    private FieldHandler getRenderField(int col)
    {
        return getTableTag().getRowRenderForm().getField(
                getColumnFieldName(col));
    }

    /**
     * Obtains the {@code TableFieldHandlerFactory} from the table tag.
     *
     * @return the {@code TableFieldHandlerFactory}
     */
    private TableFieldHandlerFactory getFieldHandlerFactory()
    {
        return getTableTag().getFieldHandlerFactory();
    }

    /**
     * Checks that for the specified field handler no transformer or validator
     * is set.
     *
     * @param field the {@code FieldHandler}
     * @return <b>true</b> if not transformer or validator is defined,
     *         <b>false</b> otherwise
     */
    private boolean hasNoTransformer(FieldHandler field)
    {
        return getFieldHandlerFactory().getReadTransformerReference(field)
                .getTransformer() == DummyWrapper.INSTANCE
                && getFieldHandlerFactory().getWriteTransformerReference(field)
                        .getTransformer() == DummyWrapper.INSTANCE
                && getFieldHandlerFactory().getValidatorReference(field)
                        .getValidator() == DummyWrapper.INSTANCE;
    }

    /**
     * Helper method for installing the specified transformers and validators in
     * the references of the given field handler.
     *
     * @param field the {@code FieldHandler}
     * @param readTransformer the read transformer
     * @param writeTransformer the write transformer
     * @param validator the validator
     */
    private void installTransformer(FieldHandler field,
            TransformerWrapper readTransformer,
            TransformerWrapper writeTransformer, ValidatorWrapper validator)
    {
        getFieldHandlerFactory().getReadTransformerReference(field)
                .setTransformer(readTransformer);
        getFieldHandlerFactory().getWriteTransformerReference(field)
                .setTransformer(writeTransformer);
        getFieldHandlerFactory().getValidatorReference(field).setValidator(
                validator);
    }

    /**
     * Creates the data model for the managed table. This implementation tries
     * to directly reuse the passed in model collection, so that it can be
     * updated conveniently by the application. However, we have to ensure that
     * the collection serving as data model can be directly accessed by index.
     * So if this is not the case, a copy from the data collection is created.
     *
     * @param tabTag the {@code TableTag}
     * @return the data model for the table
     */
    private static List<Object> createModel(TableTag tabTag)
    {
        if (tabTag.getTableModel() instanceof List
                && tabTag.getTableModel() instanceof RandomAccess)
        {
            // This is not a safe cast, however, as we do not add new objects,
            // no class cast exceptions are produced.
            @SuppressWarnings("unchecked")
            List<Object> model = (List<Object>) tabTag.getTableModel();
            return model;
        }
        return new ArrayList<Object>(tabTag.getTableModel());
    }
}

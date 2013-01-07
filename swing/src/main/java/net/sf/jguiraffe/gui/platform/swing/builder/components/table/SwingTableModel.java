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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sf.jguiraffe.gui.builder.components.tags.table.ColumnClass;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;

/**
 * <p>
 * A table model implementation for tables defined by the {@link TableTag} tag
 * handler class.
 * </p>
 * <p>
 * This class implements the typical table model functionality based on the data
 * provided by a <code>TableTag</code> object. Especially the model collection
 * and the constructed renderer and editor forms are used for extracting the
 * required data. The basic idea is as follows: the index of the last accessed
 * row is cached. If a different row is to be accessed, the model will fetch the
 * data bean with this index from the model collection specified for the table
 * tag. Then the forms will be initialized with this bean.
 * </p>
 * <p>
 * For editable columns special handling is required: If the column defines a
 * special editor component, default form operations can be performed for
 * validating user input and transfering it into the corresponding model bean.
 * Otherwise the data passed to the <code>setValueAt()</code> method must be
 * manually passed to the corresponding <code>ComponentHandler</code> objects;
 * after that typical form operations can be used, too.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingTableModel.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingTableModel extends AbstractTableModel
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 5747770889625181193L;

    /** A mapping from logic column classes to Java classes. */
    private static final Map<ColumnClass, Class<?>> LOGIC_CLASSES;

    /** Stores a reference to the tag defining the table. */
    private final TableTag tableTag;

    /** A list with the model data. */
    private List<Object> modelData;

    /** Holds a reference to the associated table. */
    private final JTable table;

    /** Holds a reference to the associated custom renderer implementation. */
    private transient TableCellRenderer customRenderer;

    /** Holds a reference to the associated custom editor implementation. */
    private transient TableCellEditor customEditor;

    /** Stores the index of the currently rendered row. */
    private int renderedRowIndex;

    /** Stores the index of the currently edited row. */
    private int editedRowIndex;

    /**
     * Creates a new instance of <code>SwingTableModel</code> and initializes
     * it.
     *
     * @param tt the tag defining the underlying table
     * @param tab the associated table
     */
    public SwingTableModel(TableTag tt, JTable tab)
    {
        tableTag = tt;
        table = tab;
        renderedRowIndex = -1;
        editedRowIndex = -1;
    }

    /**
     * Returns the list with the data of this model. The list contains beans
     * that define the values of the single columns.
     *
     * @return the data list of this model
     */
    public List<Object> getModelData()
    {
        if (modelData == null)
        {
            modelData = fetchModelData(getTableTag());
        }
        return modelData;
    }

    /**
     * Returns the table tag this model is based onto.
     *
     * @return the table tag
     */
    public TableTag getTableTag()
    {
        return tableTag;
    }

    /**
     * Returns a reference to the associated table.
     *
     * @return the table
     */
    public JTable getTable()
    {
        return table;
    }

    /**
     * Returns the number of columns of the represented table.
     *
     * @return the number of columns of this table
     */
    public int getColumnCount()
    {
        return getTableTag().getColumnCount();
    }

    /**
     * Returns the number of rows of the represented table.
     *
     * @return the number of rows of this table
     */
    public int getRowCount()
    {
        return getModelData().size();
    }

    /**
     * Returns the value at the specified cell.
     *
     * @param row the row index
     * @param col the column index
     * @return the value of this cell
     */
    public Object getValueAt(int row, int col)
    {
        if (row != renderedRowIndex)
        {
            Object bean = getModelBean(row);
            initFormFields(getTableTag().getRowRenderForm(), bean);
            initFormFields(getTableTag().getRowEditForm(), bean);
            renderedRowIndex = row;
        }

        return getTableTag().getRowRenderForm().getField(
                getColumn(col).getName()).getComponentHandler().getData();
    }

    /**
     * Returns the data class of the specified column. This implementation
     * checks whether a logic column class was specified. If this is the case,
     * it is mapped to the corresponding Java class. Otherwise, the Java class
     * is directly obtained from the column definition.
     *
     * @param col the column index
     * @return the data class for the specified column
     */
    @Override
    public Class<?> getColumnClass(int col)
    {
        Class<?> result = LOGIC_CLASSES.get(getColumn(col).getLogicDataClass());
        return (result != null) ? result : getColumn(col).getDataClass();
    }

    /**
     * Returns the name for the specified column.
     *
     * @param col the column index
     * @return the title for this column
     */
    @Override
    public String getColumnName(int col)
    {
        return getColumn(col).getHeaderText().getCaption();
    }

    /**
     * Returns a flag whether the specified cell can be modified.
     *
     * @param row the row index
     * @param col the column index
     * @return a flag whether this cell can be edited
     */
    @Override
    public boolean isCellEditable(int row, int col)
    {
        return getTableTag().isColumnEditable(getColumn(col));
    }

    /**
     * Sets the value for the specified cell.
     *
     * @param value the value to set
     * @param row the row index
     * @param col the column index
     */
    @Override
    public void setValueAt(Object value, int row, int col)
    {
        editedRowIndex = row;
        if (!hasEditor(col))
        {
            ComponentHandler ch = getTableTag().getRowEditForm().getField(
                    getColumn(col).getName()).getComponentHandler();
            ch.setData(value);
            validateColumn(col);
        }
    }

    /**
     * Notifies listeners about a change in the data of this model. This
     * implementation checks whether the change affects the cached data about
     * the last read or edited rows.
     *
     * @param event the event
     */
    @Override
    public void fireTableChanged(TableModelEvent event)
    {
        super.fireTableChanged(event);

        if (event.getFirstRow() <= renderedRowIndex
                && event.getLastRow() >= renderedRowIndex)
        {
            renderedRowIndex = -1;
        }
        if (event.getFirstRow() <= editedRowIndex
                && event.getLastRow() >= editedRowIndex)
        {
            editedRowIndex = -1;
        }
    }

    /**
     * Checks whether for the specified column a custom editor is specified.
     *
     * @param col the column index
     * @return a flag if this column has its own editor
     */
    public boolean hasEditor(int col)
    {
        return getColumn(col).getEditorComponent() != null;
    }

    /**
     * Returns the cell editor associated with this model. There is exactly one
     * editor that is capable to serve all columns of this table (that define a
     * custom editor).
     *
     * @return the cell editor used for the represented table
     */
    public TableCellEditor getEditor()
    {
        if (customEditor == null)
        {
            customEditor = new SwingTableCellEditor(this);
        }
        return customEditor;
    }

    /**
     * Tests whether for the specified column a custom renderer is specified.
     *
     * @param col the column index
     * @return a flag whether this column has a custom renderer
     */
    public boolean hasRenderer(int col)
    {
        return getColumn(col).getRendererComponent() != null;
    }

    /**
     * Returns the cell renderer associated with this model. There is exactly
     * one renderer that is capable of rendering all columns of this table that
     * define a custom renderer.
     *
     * @return the cell renderer used for the represented table
     */
    public TableCellRenderer getRenderer()
    {
        if (customRenderer == null)
        {
            customRenderer = new SwingTableCellRenderer(this);
        }
        return customRenderer;
    }

    /**
     * Prepares the list with the model data. This method is called once for
     * initializing the data collection. It tries to extract the data the passed
     * in table tag refers to as a list which can be fast accessed.
     *
     * @param tt the table tag defining the underlying table
     * @return the data collection as a list
     */
    protected List<Object> fetchModelData(TableTag tt)
    {
        if (tt.getTableModel() instanceof List<?>
                && tt.getTableModel() instanceof RandomAccess)
        {
            @SuppressWarnings("unchecked")
            List<Object> result = (List<Object>) tt.getTableModel();
            return result;
        }
        else
        {
            return new ArrayList<Object>(tt.getTableModel());
        }
    }

    /**
     * Validates the column with the specified index. This method is always
     * called when the user has entered data into a cell of the table. It
     * delegates to the editor form to validate the input fields used in this
     * column. It will also notify the <code>TableEditorValidationHandler</code>
     * set for this table. If validation is successful, the value(s) will be
     * written into the model.
     *
     * @param col the column to be validated
     * @return a flag whether the data is valid
     */
    protected boolean validateColumn(int col)
    {
        Set<String> fields = getEditFields(col);
        FormValidatorResults vres = getTableTag().getRowEditForm()
                .validateFields(fields);
        assert getTableTag().getEditorValidationHandler() != null : "No validation handler set";
        boolean forceValues;
        if (getTableTag().getEditorValidationHandler().validationPerformed(
                getTable(), getTableTag().getRowEditForm(), getTableTag(),
                vres, editedRowIndex, col))
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

        if (!vres.isValid() && !forceValues)
        {
            return false;
        }
        readFormFields(getTableTag().getRowEditForm(), getModelBean(editedRowIndex),
                fields);
        renderedRowIndex = -1;
        return true;
    }

    /**
     * Returns the column tag for the specified column.
     *
     * @param col the column index
     * @return the column tag for this column
     */
    private TableColumnTag getColumn(int col)
    {
        return getTableTag().getColumn(col);
    }

    /**
     * Returns the data object that corresponds to the specified row index.
     *
     * @param row the row index
     * @return the data bean for this row
     */
    private Object getModelBean(int row)
    {
        return (Object) getModelData().get(row);
    }

    /**
     * Returns a set with the names of the fields that are affected by the
     * specified column. If the column defines a custom editor, the edit fields
     * are directly stored in the column tag. Otherwise a "faked" set is created
     * with the name of the property represented by this column.
     *
     * @param col the index of the column
     * @return a set with the names of the affected fields
     */
    private Set<String> getEditFields(int col)
    {
        TableColumnTag colTag = getColumn(col);
        Set<String> fields = colTag.getEditFields();
        if (fields.isEmpty())
        {
            fields = Collections.singleton(colTag.getName());
        }
        return fields;
    }

    /**
     * Helper method for initializing the fields of a form. Note that this is
     * not really type-safe - we cannot ensure that the bean is of the correct
     * type.
     *
     * @param form the form
     * @param bean the form bean
     */
    private void initFormFields(Form form, Object bean)
    {
        form.initFields(bean);
    }

    /**
     * Helper method for reading the user input from a form. Note that this is
     * not really type-safe - we cannot ensure that the bean is of the correct
     * type.
     *
     * @param form the form
     * @param bean the form bean
     * @param fields a set of fields which should be read
     */
    private void readFormFields(Form form, Object bean, Set<String> fields)
    {
        form.readFields(bean, fields);
    }

    static
    {
        LOGIC_CLASSES = new EnumMap<ColumnClass, Class<?>>(ColumnClass.class);
        LOGIC_CLASSES.put(ColumnClass.BOOLEAN, Boolean.class);
        LOGIC_CLASSES.put(ColumnClass.DATE, Date.class);
        LOGIC_CLASSES.put(ColumnClass.FLOAT, Double.class);
        LOGIC_CLASSES.put(ColumnClass.ICON, Icon.class);
        LOGIC_CLASSES.put(ColumnClass.NUMBER, Number.class);
        LOGIC_CLASSES.put(ColumnClass.STRING, String.class);
    }
}

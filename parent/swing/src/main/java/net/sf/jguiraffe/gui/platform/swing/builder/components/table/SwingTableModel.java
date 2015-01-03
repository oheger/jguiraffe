/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.tags.table.ColumnClass;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;

/**
 * <p>
 * A table model implementation for tables defined by the {@link TableTag} tag
 * handler class.
 * </p>
 * <p>
 * This class implements the typical table model functionality based on an
 * {@link TableFormController} object
 * provided by a {@code TableTag} instance. Many methods can directly delegate
 * to the controller object.
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

    /** The table form controller. */
    private final TableFormController controller;

    /** Holds a reference to the associated table. */
    private final JTable table;

    /** Holds a reference to the associated custom renderer implementation. */
    private transient TableCellRenderer customRenderer;

    /** Holds a reference to the associated custom editor implementation. */
    private transient TableCellEditor customEditor;

    /**
     * Creates a new instance of {@code SwingTableModel} and initializes it.
     *
     * @param tt the tag defining the underlying table
     * @param tab the associated table
     */
    public SwingTableModel(TableTag tt, JTable tab)
    {
        tableTag = tt;
        table = tab;
        controller = tt.getTableFormController();
    }

    /**
     * Returns the list with the data of this model. The list contains beans
     * that define the values of the single columns.
     *
     * @return the data list of this model
     */
    public List<Object> getModelData()
    {
        return getController().getDataModel();
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
        return getController().getColumnCount();
    }

    /**
     * Returns the number of rows of the represented table.
     *
     * @return the number of rows of this table
     */
    public int getRowCount()
    {
        return getController().getRowCount();
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
        getController().selectCurrentRow(row);
        return getController().getColumnValue(col);
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
        Class<?> result = LOGIC_CLASSES.get(getController().getLogicDataClass(col));
        return (result != null) ? result : getController().getDataClass(col);
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
        return getController().getColumnName(col);
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
        return getController().isColumnEditable(col);
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
        getController().selectCurrentRow(row);
        getController().setColumnValue(getTable(), col, value);
    }

    /**
     * Notifies listeners about a change in the data of this model. This
     * implementation also notifies the {@code TableFormController} about this
     * change.
     *
     * @param event the event
     */
    @Override
    public void fireTableChanged(TableModelEvent event)
    {
        super.fireTableChanged(event);
        getController()
                .invalidateRange(event.getFirstRow(), event.getLastRow());
    }

    /**
     * Checks whether for the specified column a custom editor is specified.
     *
     * @param col the column index
     * @return a flag if this column has its own editor
     */
    public boolean hasEditor(int col)
    {
        return getController().hasEditor(col);
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
        return getController().hasRenderer(col);
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
        //TODO to be removed when dependend objects have been adapted
        return true;
    }

    /**
     * Returns the {@code TableFormController} used by this model.
     *
     * @return the {@code TableFormController}
     */
    TableFormController getController()
    {
        return controller;
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

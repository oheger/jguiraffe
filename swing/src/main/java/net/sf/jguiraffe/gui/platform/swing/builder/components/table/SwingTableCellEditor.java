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

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * <p>
 * A specialized cell editor class for Swing tables.
 * </p>
 * <p>
 * This class is used as editor class for table columns that define a custom
 * editor. The component that is used as editor is obtained from the
 * <code>TableTag</code> defining the table. Together with the special table
 * model and the <code>Form</code> instance constructed for the table the
 * current values are transfered into the editor component and input validation
 * can be performed.
 * </p>
 * <p>
 * An instance of this class is responsible for a complete table. The editor
 * component to be used is obtained from the table definition based on the given
 * column index. Setting and retrieving the editor's values is of less
 * importance because this is handled by the table's editor form object.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingTableCellEditor.java 205 2012-01-29 18:29:57Z oheger $
 * @see SwingTableModel
 */
class SwingTableCellEditor extends AbstractCellEditor implements
        TableCellEditor
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -5256155233784882938L;

    /** Stores a reference to the model of the associated table. */
    private SwingTableModel model;

    /** Stores the current row index. */
    private int currentRow;

    /** Stores the current column index. */
    private int currentCol;

    /**
     * Creates a new instance of <code>SwingTableCellEditor</code> and sets
     * the associated table model.
     *
     * @param tabModel the table model
     */
    public SwingTableCellEditor(SwingTableModel tabModel)
    {
        model = tabModel;
    }

    /**
     * Returns a reference to the associated table model.
     *
     * @return the table model
     */
    public SwingTableModel getModel()
    {
        return model;
    }

    /**
     * Returns the editor component for the specified cell. This implementation
     * will return the editor component that was specified in the table
     * definition.
     *
     * @param table the affected table
     * @param value the current value of this cell
     * @param selected a flag if the cell is highlighted
     * @param row the row index
     * @param col the column index
     * @return the editor component to be used
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean selected, int row, int col)
    {
        currentCol = col;
        currentRow = row;
        Component result = (Component) getModel().getTableTag().getColumn(col)
                .getEditorComponent();
        getModel().getTableTag().getEditorSelectionHandler().prepareComponent(
                getModel().getTable(), getModel().getTableTag(), result, selected,
                false, row, col);
        return result;
    }

    /**
     * Returns the current value of this editor. This implementation just
     * returns <b>null</b>. The value will be processed by the table's editor
     * form and directly written into the table model.
     *
     * @return the editor's current value
     */
    public Object getCellEditorValue()
    {
        return null;
    }

    /**
     * Tests whether editing can be stopped. This implementation performs
     * validation of the user input.
     *
     * @return a flag if editing can be stopped (this is the case if the input
     * is valid)
     */
    @Override
    public boolean stopCellEditing()
    {
        getModel().setValueAt(null, currentRow, currentCol);
        return getModel().validateColumn(currentCol);
    }
}

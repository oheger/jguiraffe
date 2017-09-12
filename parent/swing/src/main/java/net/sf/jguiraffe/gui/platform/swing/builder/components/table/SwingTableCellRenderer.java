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
package net.sf.jguiraffe.gui.platform.swing.builder.components.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * <p>
 * A specialized renderer implementation that is used for table columns that
 * define their own custom renderer component.
 * </p>
 * <p>
 * This class is the counterpart of {@link SwingTableCellEditor}
 * for custom renderers. Its task is to extract the renderer component of a
 * column from the table definition and to initialize it.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingTableCellRenderer.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingTableCellRenderer implements TableCellRenderer
{
    /** Stores a reference to the associated table model. */
    private final SwingTableModel model;

    /**
     * Creates a new instance of <code>SwingTableCellRenderer</code>.
     *
     * @param tabModel the associated table model
     */
    public SwingTableCellRenderer(SwingTableModel tabModel)
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
     * Returns the renderer component for the specified cell. This
     * implementation fetches the <code>TableColumn</code> tag from the
     * associated table model and obtains the renderer component from it. The
     * {@link net.sf.jguiraffe.gui.builder.components.tags.table.TableSelectionHandler
     * TableSelectionHandler} for renderers will be invoked to initialize the
     * component. Note that there is no need to explicitly set the value
     * because this was already done through the renderer <code>Form</code> of
     * the table component.
     *
     * @param table the table object
     * @param value the current value
     * @param selected a flag whether the cell is selected
     * @param focused a flag whether the cell has the focus
     * @param row the current row
     * @param col the current column
     * @return the component to be used as cell renderer
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean selected, boolean focused, int row, int col)
    {
        Component renderer =
                (Component) getModel().getTableTag().getColumn(col)
                        .getRendererComponent();
        assert getModel().getTableTag().getRendererSelectionHandler() != null
            : "No renderer selection handler set";
        getModel()
                .getTableTag()
                .getRendererSelectionHandler()
                .prepareComponent(table, getModel().getTableTag(), renderer,
                        selected, focused, row, col);
        return renderer;
    }
}

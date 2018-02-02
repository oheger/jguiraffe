/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;

import net.sf.jguiraffe.gui.builder.components.tags.table.TableSelectionHandler;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;

/**
 * <p>
 * A Swing-specific default implementation of the
 * <code>TableSelectionHandler</code> interface.
 * </p>
 * <p>
 * This implementation expects that the component passed in to the
 * <code>prepareComponent()</code> method is a
 * <code>javax.swing.JComponent</code>. Depending on the selected or focused
 * flags the color of this component will be set, and eventually a border will
 * be drawn.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingTableSelectionHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingTableSelectionHandler implements TableSelectionHandler
{
    /**
     * Prepares the renderer or editor component. This implementation will set
     * the correct colors and a border for focused cells.
     *
     * @param table the table component
     * @param tableTag the table tag
     * @param component the component to be prepared
     * @param selected a flag whether this cell is selected
     * @param hasFocus a flag whether this cell has the focus
     * @param row the current row index
     * @param col the current column index
     */
    public void prepareComponent(Object table, TableTag tableTag,
            Object component, boolean selected, boolean hasFocus, int row,
            int col)
    {
        JComponent c = (JComponent) component;
        JTable tab = (JTable) table;
        if (selected)
        {
            c.setForeground(tab.getSelectionForeground());
            c.setBackground(tab.getSelectionBackground());
        }
        else
        {
            c.setForeground(tab.getForeground());
            c.setBackground(tab.getBackground());
        }

        if (hasFocus)
        {
            // TODO determine correct color
            c.setBorder(BorderFactory.createLineBorder(tab
                    .getSelectionBackground()));
        }
        else
        {
            c.setBorder(null);
        }
    }
}

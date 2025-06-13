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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import java.awt.Component;

import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;

/**
 * <p>
 * An internally used editor class for editing the nodes of a tree view.
 * </p>
 * <p>
 * This class is needed to ensure that the editor field used for editing a
 * node's label is initialized correctly. It has to be initialized with the name
 * of the {@code ConfigurationNode} representing the tree node. The actual
 * extraction of the node name is handled by the {@link SwingTreeNodeFormatter}
 * passed to the constructor.
 * </p>
 * <p>
 * Implementation note: This class is used internally only. Therefore, no
 * sophisticated parameter checks are performed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: $
 */
class SwingTreeCellEditor extends DefaultTreeCellEditor
{
    /**
     * Creates a new instance of {@code SwingTreeCellEditor} and initializes it.
     *
     * @param tree the associated tree component
     * @param renderer the renderer component
     */
    public SwingTreeCellEditor(JTree tree, SwingTreeCellRenderer renderer)
    {
        super(tree, renderer);
    }

    /**
     * Returns the {@code SwingTreeNodeFormatter} used by this editor.
     *
     * @return the {@code SwingTreeNodeFormatter}
     */
    public SwingTreeNodeFormatter getNodeFormatter()
    {
        return ((SwingTreeCellRenderer) renderer).getNodeFormatter();
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value,
            boolean isSelected, boolean expanded, boolean leaf, int row)
    {
        Component comp =
                super.getTreeCellEditorComponent(tree, value, isSelected,
                        expanded, leaf, row);
        initEditorComponentText(value);
        return comp;
    }

    /**
     * Sets the correct text for the editor component.
     *
     * @param value the value to be displayed in the editor component
     */
    private void initEditorComponentText(Object value)
    {
        ((JTextField) editingComponent).setText(getNodeFormatter().textForNode(
                value));
    }
}

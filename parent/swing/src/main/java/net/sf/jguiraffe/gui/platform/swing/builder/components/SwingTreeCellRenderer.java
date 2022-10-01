/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.sf.jguiraffe.gui.builder.components.tags.TreeIconHandler;

import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * <p>
 * A specialized tree renderer implementation.
 * </p>
 * <p>
 * This class has the following tasks:
 * <ul>
 * <li>Obtaining the correct text for a tree node. Because a hierarchical
 * configuration serves as data model for a tree we have to deal with
 * <code>ConfigurationNode</code> objects. The name of such a node is used as
 * text for a tree node.</li>
 * <li>Determining the icon to be displayed for a tree node. This is done in
 * collaboration with a {@link TreeIconHandler} object and the actual icons
 * defined for a tree. For each node to display the renderer asks the
 * {@link TreeIconHandler} for the name of the icon to use for this node. Then
 * in looks up the corresponding icon in the map of icons associated with the
 * current tree. If an icon is found, it is displayed. Otherwise the default
 * icon for the current node type (leaf or branch node, expanded or not) is
 * used.</li>
 * </ul>
 * </p>
 * <p>
 * Implementation node: This class is used internally only. Therefore, it does
 * not do any sophisticated checks for parameters or other things.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingTreeCellRenderer.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingTreeCellRenderer extends DefaultTreeCellRenderer
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -6276822538915250422L;

    /** Stores the icon handler. */
    private final TreeIconHandler iconHandler;

    /** Stores the map with the known icons. */
    private final Map<String, Icon> iconMap;

    /** The tree node formatter. */
    private final SwingTreeNodeFormatter nodeFormatter;

    /**
     * Creates a new instance of {@code SwingTreeCellRenderer} and
     * initializes it.
     *
     * @param handler the icon handler (must not be <b>null</b>)
     * @param icons a map with icons (must not be <b>null</b>)
     * @param fmt the formatter for nodes (must not be <b>null</b>)
     */
    public SwingTreeCellRenderer(TreeIconHandler handler,
            Map<String, Object> icons, SwingTreeNodeFormatter fmt)
    {
        iconHandler = handler;
        iconMap = initIconMap(icons);
        nodeFormatter = fmt;
    }

    /**
     * Returns the {@code SwingTreeNodeFormatter} used by this renderer.
     *
     * @return the {@code SwingTreeNodeFormatter}
     */
    public SwingTreeNodeFormatter getNodeFormatter()
    {
        return nodeFormatter;
    }

    /**
     * Returns the <code>TreeIconHandler</code> used by this renderer.
     *
     * @return the tree icon handler
     */
    public TreeIconHandler getIconHandler()
    {
        return iconHandler;
    }

    /**
     * Returns the icon for the specified name. Result can be <b>null</b> if no
     * icon is associated with the given name.
     *
     * @param name the name of the icon
     * @return the corresponding icon or <b>null</b>
     */
    public Icon getTreeIcon(String name)
    {
        return iconMap.get(name);
    }

    /**
     * Returns the component to be used for displaying the current cell of the
     * tree. This implementation calls the inherited method for obtaining a
     * partially initialized renderer component. Then, with the help of the
     * <code>textForNode()</code> method and the
     * {@link TreeIconHandler, it takes care of the text and the
     * icon to be used for this cell.
     *
     * @param tree the tree
     * @param value the data object representing the current cell
     * @param selected the selected flag
     * @param expanded a flag whether this node is expanded
     * @param leaf a flag whether this node is a leaf node
     * @param row the row number
     * @param hasFocus a flag whether this cell has the focus
     * @return the renderer component
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus)
    {
        JLabel c = (JLabel) super.getTreeCellRendererComponent(tree, value,
                selected, expanded, leaf, row, hasFocus);
        c.setText(getNodeFormatter().textForNode(value));

        String iconName = getIconHandler().getIconName(
                (ConfigurationNode) value, expanded, leaf);
        Icon icon = getTreeIcon(iconName);
        if (icon != null)
        {
            c.setIcon(icon);
        }

        return c;
    }

    /**
     * Transforms the icons map passed to the constructor into a map with Swing
     * icons. In addition a copy of the map is created.
     *
     * @param icons the original map with the icons that was passed to the
     *        constructor
     * @return the transformed icon map
     */
    private Map<String, Icon> initIconMap(Map<String, Object> icons)
    {
        Map<String, Icon> result = new HashMap<String, Icon>();

        for (Map.Entry<String, Object> e : icons.entrySet())
        {
            result.put(e.getKey(), (Icon) e.getValue());
        }

        return result;
    }
}

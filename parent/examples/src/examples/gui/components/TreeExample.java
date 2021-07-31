/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
package net.sf.jguiraffe.examples.gui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.sf.jguiraffe.gui.platform.swing.builder.components.SwingConfigurationTreeModel;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * <p>
 * An example class for demonstrating a tree view.
 * </p>
 * <p>
 * This class is intended as a demonstration of how a {@code
 * HierarchicalConfiguration} object can be used as a model of a tree view. It
 * uses Swing-specific implementations of a tree model and a tree renderer to
 * display the content of such a configuration object. The model is created in
 * the {@code initConfiguration()} method.
 * </p>
 * <p>
 * In <em>JGUIraffe</em> applications it is not necessary to create tree views
 * manually. This is done through builder scripts. This example should only
 * demonstrate the relation between the content of a {@code
 * HierarchicalConfiguration} object and the display of a tree that uses this
 * object as its model.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TreeExample.java 205 2012-01-29 18:29:57Z oheger $
 */
@SuppressWarnings("serial")
public class TreeExample extends JFrame
{
    /**
     * Creates a new instance of {@code TreeExample}.
     */
    public TreeExample()
    {
        initUI();
    }

    /**
     * Initializes the UI. The UI consists only of a tree view.
     */
    private void initUI()
    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Tree Demo");
        JTree tree = new JTree();
        tree.setModel(new SwingConfigurationTreeModel(initConfiguration()));
        tree.setCellRenderer(new Renderer());
        JScrollPane scr = new JScrollPane(tree);
        scr.setPreferredSize(new Dimension(400, 250));
        getContentPane().add(scr, BorderLayout.CENTER);
        pack();
    }

    /**
     * Sets up a configuration object with some test entries.
     *
     * @return the configuration to be used as tree model
     */
    private HierarchicalConfiguration initConfiguration()
    {
        HierarchicalConfiguration config = new HierarchicalConfiguration();
        config
                .addProperty("net.sf.jguiraffe.gui.app.Application",
                        Boolean.TRUE);
        config.addProperty("net.sf.jguiraffe.gui.app.ApplicationContext",
                Boolean.TRUE);
        config
                .addProperty("net.sf.jguiraffe.gui.builder.Builder",
                        Boolean.TRUE);
        config.addProperty("net.sf.jguiraffe.gui.builder.BeanBuilder",
                Boolean.TRUE);
        config.addProperty("net.sf.jguiraffe.transform.Transformer",
                Boolean.TRUE);
        config
                .addProperty("net.sf.jguiraffe.transform.Validator",
                        Boolean.TRUE);
        return config;
    }

    /**
     * Starts this application. Opens the frame with the tree view.
     *
     * @param args command line arguments (currently not evaluated)
     */
    public static void main(String[] args)
    {
        final TreeExample frame = new TreeExample();
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                frame.setVisible(true);
            }
        });
    }

    /**
     * A simple renderer class used by the tree. This implementation operates on
     * the node objects of the hierarchical configuration acting as the tree's
     * model. It displays their name.
     */
    private static class Renderer extends DefaultTreeCellRenderer
    {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasFocus)
        {
            JLabel comp = (JLabel) super.getTreeCellRendererComponent(tree,
                    value, selected, expanded, leaf, row, hasFocus);
            comp.setText(((ConfigurationNode) value).getName());
            return comp;
        }
    }
}

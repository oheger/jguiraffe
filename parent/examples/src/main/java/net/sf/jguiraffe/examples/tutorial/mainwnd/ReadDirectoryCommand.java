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
package net.sf.jguiraffe.examples.tutorial.mainwnd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.jguiraffe.examples.tutorial.model.DirectoryData;
import net.sf.jguiraffe.examples.tutorial.model.FileData;
import net.sf.jguiraffe.examples.tutorial.viewset.ViewSettings;
import net.sf.jguiraffe.gui.builder.components.model.TreeNodePath;
import net.sf.jguiraffe.gui.cmd.CommandBase;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationKey;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.apache.commons.configuration.tree.DefaultExpressionEngine;

/**
 * <p>
 * A command class for reading the content of a directory.
 * </p>
 * <p>
 * This class scans a directory and creates a corresponding structure of {@code
 * ConfigurationNode} objects in a background thread. Then it updates the tree
 * view.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ReadDirectoryCommand.java 205 2012-01-29 18:29:57Z oheger $
 */
class ReadDirectoryCommand extends CommandBase
{
    /** The data object for the directory to be read. */
    private final DirectoryData directoryData;

    /** The main controller. */
    private final MainWndController controller;

    /** The current path within the tree model. */
    private final TreeNodePath currentPath;

    /** The nodes created by the scan operation. */
    private List<ConfigurationNode> nodes;

    /** The view settings for the current directory. */
    private ViewSettings directorySettings;

    /**
     * Creates a new instance of {@code ReadDirectoryCommand} and initializes
     * it.
     *
     * @param ctrl the controller
     * @param p the currently selected path of the tree
     */
    public ReadDirectoryCommand(MainWndController ctrl, TreeNodePath p)
    {
        super(true);
        directoryData = (DirectoryData) p.getTargetNode().getValue();
        controller = ctrl;
        currentPath = p;
    }

    /**
     * Executes this command. Reads the content of the current directory;
     * creates {@code ConfigurationNode} objects for the sub directories and
     * {@code FileData} objects for the content.
     */
    @Override
    public void execute() throws Exception
    {
        directorySettings = ViewSettings.forDirectory(directoryData
                .getDirectory());

        File[] content = directoryData.getDirectory().listFiles(
                directorySettings.createFileFilter());
        if (content != null)
        {
            nodes = new ArrayList<ConfigurationNode>(content.length);
            List<FileData> data = new ArrayList<FileData>(content.length);

            for (File f : content)
            {
                Object icon;
                if (f.isDirectory())
                {
                    DefaultConfigurationNode nd = new DefaultConfigurationNode(
                            f.getName(), new DirectoryData(f));
                    nodes.add(nd);
                    icon = controller.getIconDirectory();
                }
                else
                {
                    icon = controller.getIconFile();
                }
                data.add(new FileData(f, icon));
            }

            Collections.sort(data, directorySettings.createComparator());
            directoryData.setContent(data);
        }

        else
        {
            directoryData.setContent(new ArrayList<FileData>(0));
        }
    }

    /**
     * Updates the UI after the execution of the command. Here the newly created
     * nodes are added to the tree model, and the selection is updated.
     */
    @Override
    protected void performGUIUpdate()
    {
        if (nodes != null)
        {
            HierarchicalConfiguration model = controller.getTreeModel();

            // obtain the path to the node in the model
            DefaultConfigurationKey key = new DefaultConfigurationKey(
                    (DefaultExpressionEngine) model.getExpressionEngine());
            currentPath.pathToKey(key);

            // add the nodes for the sub directories of the directory
            model.addNodes(key.toString(), nodes);
        }

        // handle selection and update table
        controller.getTree().setSelectedPath(currentPath);
        controller.fillTable(directoryData, directorySettings);
    }
}

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
package net.sf.jguiraffe.examples.tutorial.mainwnd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jguiraffe.examples.tutorial.model.DirectoryData;
import net.sf.jguiraffe.examples.tutorial.model.FileData;
import net.sf.jguiraffe.examples.tutorial.viewset.ViewSettings;
import net.sf.jguiraffe.gui.app.Application;
import net.sf.jguiraffe.gui.builder.action.ActionStore;
import net.sf.jguiraffe.gui.builder.components.WidgetHandler;
import net.sf.jguiraffe.gui.builder.components.model.TableHandler;
import net.sf.jguiraffe.gui.builder.components.model.TreeHandler;
import net.sf.jguiraffe.gui.builder.components.model.TreeNodePath;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;

/**
 * <p>
 * The controller class for the main window.
 * </p>
 * <p>
 * This class implements logic related to the GUI of the main window of the
 * JGUIraffe tutorial application.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: MainWndController.java 205 2012-01-29 18:29:57Z oheger $
 */
public class MainWndController
{
    /** Constant for the action group to be enabled for a single file selection. */
    static final String ACTGRP_SINGLE_FILE = "SINGLE_FILE";

    /** Constant for the action group to be enabled for a single selection. */
    static final String ACTGRP_SINGLE = "SINGLE_SEL";

    /** Constant for the action group to be enabled for a non-null selection. */
    static final String ACTGRP_SELECTION = "SELECTION";

    /** A reference to the main application object. */
    private final Application application;

    /** The handler for the tree component. */
    private final TreeHandler tree;

    /** The handler for the table component. */
    private final TableHandler table;

    /** The widget handler for the table. */
    private final WidgetHandler widgetTable;

    /** Stores the model used by the tree component. */
    private final HierarchicalConfiguration treeModel;

    /** A map with the data of the file systems. */
    private final Map<File, ConfigurationNode> fileSystems;

    /** A map for storing view settings for directories. */
    private final Map<File, ViewSettings> viewSettings;

    /** The icon for directories. */
    private Object iconDirectory;

    /** The icon for files. */
    private Object iconFile;

    /**
     * Creates a new instance of {@code MainWndController} and initializes it
     * with references to its dependencies.
     *
     * @param app the main {@code Application} object
     * @param treeHandler the handler for the tree component
     * @param tabHandler the handler for the table component
     */
    public MainWndController(Application app, TreeHandler treeHandler,
            TableHandler tabHandler, WidgetHandler widgetTab)
    {
        application = app;
        tree = treeHandler;
        table = tabHandler;
        widgetTable = widgetTab;
        treeModel = tree.getModel();
        fileSystems = new HashMap<File, ConfigurationNode>();
        viewSettings = new HashMap<File, ViewSettings>();
    }

    /**
     * Returns the central {@code Application} object.
     *
     * @return the {@code Application}
     */
    public Application getApplication()
    {
        return application;
    }

    /**
     * Returns the handler for the tree component.
     *
     * @return the tree handler
     */
    public TreeHandler getTree()
    {
        return tree;
    }

    /**
     * Returns the handler for the table component.
     *
     * @return the table handler
     */
    public TableHandler getTable()
    {
        return table;
    }

    /**
     * Returns the configuration acting a model for the tree component.
     *
     * @return the tree model
     */
    public HierarchicalConfiguration getTreeModel()
    {
        return treeModel;
    }

    /**
     * Returns the icon for directories.
     *
     * @return the icon for directories
     */
    public Object getIconDirectory()
    {
        return iconDirectory;
    }

    /**
     * Sets the icon for directories.
     *
     * @param iconDirectory the icon for directories
     */
    public void setIconDirectory(Object iconDirectory)
    {
        this.iconDirectory = iconDirectory;
    }

    /**
     * Returns the icon for files.
     *
     * @return the icon for files
     */
    public Object getIconFile()
    {
        return iconFile;
    }

    /**
     * Sets the icon for files.
     *
     * @param iconFile the icon for files
     */
    public void setIconFile(Object iconFile)
    {
        this.iconFile = iconFile;
    }

    /**
     * Returns a list with the {@code File} objects that are selected in the
     * current directory. The list can contain plain files and directories as
     * well.
     *
     * @return the list with the selected {@code File} objects
     */
    public List<File> getSelectedFiles()
    {
        int[] indices = getTable().getSelectedIndices();
        List<File> files = new ArrayList<File>(indices.length);
        List<Object> model = getTable().getModel();

        for (int idx : indices)
        {
            FileData data = (FileData) model.get(idx);
            files.add(data.getFile());
        }

        return files;
    }

    /**
     * Selects the specified directory in the tree view. The passed in directory
     * must be a sub directory of the current directory.
     *
     * @param dir the directory to be selected
     */
    public void selectSubDirectory(File dir)
    {
        TreeNodePath currentPath = tree.getSelectedPath();
        TreeNodePath newPath = currentPath.append(dir.getName());
        tree.setSelectedPath(newPath);
    }

    /**
     * Reads the content of the specified directory and adds it to the model of
     * the tree view. This method is called whenever the selection of the tree
     * view changes to a directory that has not yet been scanned.
     *
     * @param path the path in the tree model
     */
    void readDirectory(TreeNodePath path)
    {
        widgetTable.setVisible(false);
        application.execute(new ReadDirectoryCommand(this, path));
    }

    /**
     * The user has selected another file system. This method is called when the
     * selection of the combobox with the file systems changes. If data for this
     * file system is already available, it is installed in the tree model.
     * Otherwise, a new node structure for the file system has to be created.
     *
     * @param root the root of the file system
     */
    void fileSystemChanged(File root)
    {
        boolean load = false;
        ConfigurationNode node = fileSystems.get(root);

        if (node == null)
        {
            // first access to this file system => create initial nodes
            node = new DefaultConfigurationNode(root.getPath(),
                    new DirectoryData(root));
            fileSystems.put(root, node);
            load = true;
        }

        // Update tree model
        treeModel.setRootNode(new DefaultConfigurationNode());
        treeModel.addNodes(null, Collections.singleton(node));

        if (load)
        {
            // load initial data
            readDirectory(new TreeNodePath(node));
        }
    }

    /**
     * The selection of the tree view has changed. This method is called by the
     * event listener for the tree control. It reads in the data of the newly
     * selected directory if this has not been done yet. It ensures that the
     * table view is up-to-date.
     *
     * @param path the path to the selected tree node
     */
    void treeSelectionChanged(TreeNodePath path)
    {
        if (path != null)
        {
            DirectoryData dirData = (DirectoryData) path.getTargetNode()
                    .getValue();
            if (!dirData.isInitialized())
            {
                readDirectory(path);
            }
            else
            {
                fillTable(dirData, viewSettings.get(dirData.getDirectory()));
            }
        }
    }

    /**
     * Fills the table component with the data for the current directory.
     *
     * @param dirData the data object for the current directory
     * @param settings the current view settings
     */
    void fillTable(DirectoryData dirData, ViewSettings settings)
    {
        getTable().clearSelection();
        List<Object> model = getTable().getModel();
        int size = model.size();
        if (size > 0)
        {
            model.clear();
            table.rowsDeleted(0, size - 1);
        }

        model.addAll(dirData.getContent());
        getTable().rowsInserted(0, dirData.getContent().size() - 1);
        if (!dirData.getContent().isEmpty())
        {
            getTable().setSelectedIndex(0);
        }

        // store current directory and settings in global properties
        getApplication().getApplicationContext().setTypedProperty(
                DirectoryData.class, dirData);
        if (settings != null)
        {
            // may be null when the file system was changed
            getApplication().getApplicationContext().setTypedProperty(
                    ViewSettings.class, settings);
            viewSettings.put(dirData.getDirectory(), settings);

            applyViewSettings(settings);
            widgetTable.setVisible(true);
        }

        tableSelectionChanged();
    }

    /**
     * Sets the graphical properties according to the specified {@code
     * ViewSettings} object. This method is called every time a new directory is
     * listed. The properties of the table component have to be adjusted.
     *
     * @param settings the {@code ViewSettings} object
     */
    void applyViewSettings(ViewSettings settings)
    {
        widgetTable.setBackgroundColor(settings.getBackgroundColor());
        widgetTable.setForegroundColor(settings.getForegroundColor());
        table.setSelectionBackground(settings.getSelectionBackground());
        table.setSelectionForeground(settings.getSelectionForeground());
    }

    /**
     * Notifies this controller about a change of the selection of the table.
     * This method updates the enabled state of the actions that depend on the
     * selection of files.
     */
    void tableSelectionChanged()
    {
        ActionStore as = getApplication().getApplicationContext()
                .getActionStore();
        int[] indices = getTable().getSelectedIndices();
        as.enableGroup(ACTGRP_SELECTION, indices.length > 0);
        as.enableGroup(ACTGRP_SINGLE, indices.length == 1);

        boolean singleFile = false;
        if (indices.length == 1)
        {
            List<File> files = getSelectedFiles();
            if (files.get(0).isFile())
            {
                singleFile = true;
            }
        }
        as.enableGroup(ACTGRP_SINGLE_FILE, singleFile);
    }

    /**
     * Performs a refresh. The content of the current directory is read again.
     */
    void refresh()
    {
        TreeNodePath path = tree.getSelectedPath();
        if (path != null)
        {
            DirectoryData dirData = (DirectoryData) path.getTargetNode()
                    .getValue();
            dirData.setContent(null); // remove content
            treeSelectionChanged(path);
        }
    }
}

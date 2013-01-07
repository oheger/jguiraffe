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
package net.sf.jguiraffe.gui.builder.components;

import java.util.ArrayList;
import java.util.List;

import net.sf.jguiraffe.gui.builder.components.model.TreeExpansionListener;
import net.sf.jguiraffe.gui.builder.components.model.TreeHandler;
import net.sf.jguiraffe.gui.builder.components.model.TreeNodePath;
import net.sf.jguiraffe.gui.builder.components.model.TreePreExpansionListener;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;

import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * A test implementation of the {@code TreeHandler} interface. This class
 * provides some basic implementations for some of the methods defined by the
 * {@code TreeHandler} interface. It can be used for tests with tags that do
 * something with trees or their handlers.
 *
 * @author Oliver Heger
 * @version $Id: TreeHandlerImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TreeHandlerImpl extends ComponentHandlerImpl implements
        TreeHandler
{
    /** Stores the expansion listeners. */
    private final List<TreeExpansionListener> expansionListeners;

    /** Stores the pre-expansion listeners. */
    private final List<TreePreExpansionListener> preexpansionListeners;

    /** A list with selected paths. */
    private final List<TreeNodePath> selectedPaths;

    /** The selected path. */
    private TreeNodePath selectedPath;

    /** The model. */
    private HierarchicalConfiguration model;

    /**
     * Creates a new instance of {@code TreeHandlerImpl}.
     */
    public TreeHandlerImpl()
    {
        expansionListeners = new ArrayList<TreeExpansionListener>();
        preexpansionListeners = new ArrayList<TreePreExpansionListener>();
        selectedPaths = new ArrayList<TreeNodePath>();
    }

    /**
     * {@inheritDoc} Adds the listener to an internal list.
     */
    public void addExpansionListener(TreeExpansionListener l)
    {
        expansionListeners.add(l);
    }

    /**
     * {@inheritDoc} Adds the listener to an internal list.
     */
    public void addPreExpansionListener(TreePreExpansionListener l)
    {
        preexpansionListeners.add(l);
    }

    /**
     * {@inheritDoc} Adds the given path to an internal list.
     */
    public void addSelectedPath(TreeNodePath path)
    {
        selectedPaths.add(path);
    }

    /**
     * {@inheritDoc} Clears all internal fields that store a selection.
     */
    public void clearSelection()
    {
        selectedPath = null;
        selectedPaths.clear();
    }

    /**
     * {@inheritDoc} Empty dummy implementation of this interface method.
     */
    public void collapse(TreeNodePath path)
    {
    }

    /**
     * {@inheritDoc} Empty dummy implementation of this interface method.
     */
    public void expand(TreeNodePath path)
    {
    }

    /**
     * {@inheritDoc} Returns the value of the model field.
     */
    public HierarchicalConfiguration getModel()
    {
        return model;
    }

    /**
     * {@inheritDoc} The selected path is simply stored as a property.
     */
    public TreeNodePath getSelectedPath()
    {
        return selectedPath;
    }

    /**
     * {@inheritDoc} The selected paths are stored internally as a list. This
     * method just returns the content of this list.
     */
    public TreeNodePath[] getSelectedPaths()
    {
        return selectedPaths.toArray(new TreeNodePath[0]);
    }

    /**
     * {@inheritDoc} Removes the listener from the internal list.
     */
    public void removeExpansionListener(TreeExpansionListener l)
    {
        expansionListeners.remove(l);
    }

    /**
     * {@inheritDoc} Removes the listener from the internal list.
     */
    public void removePreExpansionListener(TreePreExpansionListener l)
    {
        preexpansionListeners.remove(l);
    }

    /**
     * {@inheritDoc} Sets the selected path property.
     */
    public void setSelectedPath(TreeNodePath path)
    {
        selectedPath = path;
    }

    /**
     * Sets the model to be returned by the {@link #getModel()} method.
     *
     * @param c the model
     */
    public void setModel(HierarchicalConfiguration c)
    {
        model = c;
    }

    /**
     * Returns an array with all {@code TreeExpansionListener} objects
     * registered for this handler.
     *
     * @return an array with all expansion listeners
     */
    public TreeExpansionListener[] getExpansionListeners()
    {
        return expansionListeners.toArray(new TreeExpansionListener[0]);
    }

    /**
     * Returns an array with all {@code TreePreExpansionListener} objects
     * registered at this handler.
     *
     * @return an array with all {@code TreePreExpansionListener} objects
     */
    public TreePreExpansionListener[] getPreExpansionListeners()
    {
        return preexpansionListeners.toArray(new TreePreExpansionListener[0]);
    }
}

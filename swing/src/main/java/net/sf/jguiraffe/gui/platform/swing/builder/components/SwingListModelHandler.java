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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import javax.swing.JComponent;

import net.sf.jguiraffe.gui.builder.components.model.ListComponentHandler;
import net.sf.jguiraffe.gui.builder.components.model.ListModel;

/**
 * <p>
 * An abstract base class for Swing component handlers that deal with list
 * models.
 * </p>
 * <p>
 * This class provides basic functionality for managing a list model. It will
 * act as the base class for specific handler implementations that wrap Swing
 * list-like components like <code>JList</code> or <code>JComboBox</code>.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingListModelHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
abstract class SwingListModelHandler extends SwingComponentHandler<Object> implements
        ListComponentHandler
{
    /** Stores the special Swing list model. */
    private SwingListModel model;

    /**
     * Creates a new instance of <code>SwingListModelHandler</code> and sets
     * the managed component and the original list model.
     *
     * @param component the component to manage
     * @param listModel the original list model; this class will copy this list
     * model's data into a new list model that can also be used by Swing
     * components
     */
    protected SwingListModelHandler(JComponent component, ListModel listModel)
    {
        super(component);
        model = createSwingListModel(listModel);
        initComponentModel(model);
    }

    /**
     * Returns the list model for this component.
     *
     * @return the list model
     */
    public ListModel getListModel()
    {
        return model;
    }

    /**
     * Adds an item to the list model of this component.
     *
     * @param index the index of the new item
     * @param display the display object
     * @param value the value object
     */
    public void addItem(int index, Object display, Object value)
    {
        model.insertItem(index, display, value);
    }

    /**
     * Removes the item with the given index from the list model of this
     * component.
     *
     * @param index the index
     */
    public void removeItem(int index)
    {
        model.removeElementAt(index);
    }

    /**
     * Returns this component's data type. This type is directly derived from
     * the list model.
     *
     * @return the data type of this component
     */
    public Class<?> getType()
    {
        return getListModel().getType();
    }

    /**
     * Creates the Swing specific list model from the given source model.
     *
     * @param m the original list model
     * @return the new Swing specific list model
     */
    protected SwingListModel createSwingListModel(ListModel m)
    {
        return new SwingListModel(m);
    }

    /**
     * Initializes the list model for the managed component. This method is
     * called after the Swing specific list model has been created. Derived
     * classes should initialize the managed component with this model.
     *
     * @param model the Swing list model
     */
    protected abstract void initComponentModel(SwingListModel model);
}

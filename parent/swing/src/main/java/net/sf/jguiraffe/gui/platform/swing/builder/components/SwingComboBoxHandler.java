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

import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sf.jguiraffe.gui.builder.components.model.EditableComboBoxModel;
import net.sf.jguiraffe.gui.builder.components.model.ListModel;
import net.sf.jguiraffe.gui.builder.components.tags.ListModelUtils;

/**
 * <p>
 * A specific Swing component handler implementation that deals with combo
 * boxes.
 * </p>
 * <p>
 * This class manages a <code>JComboBox</code> component, which is not
 * editable. The specified <code>ListModel</code> is used to transform the
 * combo box's selected index into a value object.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingComboBoxHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingComboBoxHandler extends SwingListModelHandler implements
        ActionListener
{
    /**
     * A reference to an editable combo box model for dealing with values not
     * found in the data model.
     */
    private final EditableComboBoxModel editModel;

    /**
     * Creates a new instance of <code>SwingComboBoxHandler</code> and
     * initializes it.
     *
     * @param combo the combo box
     * @param m the list model for the combo box
     */
    public SwingComboBoxHandler(JComboBox combo, ListModel m)
    {
        super(combo, m);
        editModel = ListModelUtils.fetchEditableComboBoxModel(m);
    }

    /**
     * Returns the managed combo box.
     *
     * @return the managed combo box component
     */
    public JComboBox getComboBox()
    {
        return (JComboBox) getComponent();
    }

    /**
     * Registers this component handler as a change listener at the managed
     * component. This implementation registers an action listener.
     */
    @Override
    protected void registerChangeListener()
    {
        getComboBox().addActionListener(this);
    }

    /**
     * Unregisters this component as change listener from the managed component.
     */
    @Override
    protected void unregisterChangeListener()
    {
        getComboBox().removeActionListener(this);
    }

    /**
     * Returns this component's data. Based on the selected index an object from
     * the list model will be returned.
     *
     * @return the component's data
     */
    public Object getData()
    {
        Object selectedItem = getComboBox().getSelectedItem();
        if (selectedItem == null)
        {
            return null;
        }

        int index = ListModelUtils.getDisplayIndex(getListModel(), selectedItem);
        if (index != ListModelUtils.IDX_UNDEFINED)
        {
            return ListModelUtils.getValue(getListModel(), index);
        }

        return editModel.toValue(selectedItem);
    }

    /**
     * Sets the data of this component.
     *
     * @param data the new data (which should be an item of the list model)
     */
    public void setData(Object data)
    {
        Object display;
        if (data == null)
        {
            display = null;
        }
        else
        {
            int index = ListModelUtils.getIndex(getListModel(), data);
            if (index != ListModelUtils.IDX_UNDEFINED)
            {
                display = getListModel().getDisplayObject(index);
            }
            else
            {
                display = editModel.toDisplay(data);
                getComboBox().setSelectedIndex(-1);
            }
        }
        getComboBox().setSelectedItem(display);
    }

    /**
     * Callback for action events. An action event indicates a change in the
     * combobox's selection.
     *
     * @param event the event
     */
    public void actionPerformed(ActionEvent event)
    {
        fireChangeEvent(event);
    }

    /**
     * Initializes the combo box with the given list model.
     *
     * @param model the list model
     */
    @Override
    protected void initComponentModel(SwingListModel model)
    {
        getComboBox().setModel(model);
    }
}

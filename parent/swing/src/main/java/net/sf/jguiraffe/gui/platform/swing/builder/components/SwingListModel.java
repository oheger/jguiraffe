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

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import net.sf.jguiraffe.gui.builder.components.model.ListModel;

/**
 * <p>
 * A specialized list model used internally by list-like components.
 * </p>
 * <p>
 * This class is a hybrid: It is both a Swing list model and a form builder list
 * model.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingListModel.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingListModel extends DefaultComboBoxModel implements ListModel
{
    /**
     * A default serial version UID.
     */
    private static final long serialVersionUID = 20131003L;

    /** Stores the data type of this model. */
    private final Class<?> type;

    /** The collection with value objects. */
    private final List<Object> valueObjects;

    /**
     * Creates a new instance of {@code SwingListModel}.
     *
     * @param model the underlying model
     */
    public SwingListModel(ListModel model)
    {
        type = model.getType();
        valueObjects = new ArrayList<Object>(model.size());
        initFromModel(model);
    }

    /**
     * Returns the display object with the given index.
     *
     * @param index the index (0 based)
     * @return the display object at this index
     */
    public Object getDisplayObject(int index)
    {
        return getElementAt(index);
    }

    /**
     * Returns the data object of this list model.
     *
     * @return the data type of this model
     */
    public Class<?> getType()
    {
        return type;
    }

    /**
     * Returns the value object with the given index.
     *
     * @param index the index (0 based)
     * @return the value object for this index
     */
    public Object getValueObject(int index)
    {
        return valueObjects.get(index);
    }

    /**
     * Returns the number of entries in this model.
     *
     * @return the number of entries
     */
    public int size()
    {
        return getSize();
    }

    /**
     * Inserts the given data at the specified index.
     *
     * @param index the index
     * @param display the display object
     * @param value the value object
     */
    public void insertItem(int index, Object display, Object value)
    {
        insertElementAt(display, index);
        valueObjects.add(index, value);
    }

    /**
     * {@inheritDoc} This implementation also updates the specific state of
     * this model.
     */
    @Override
    public void removeElementAt(int index)
    {
        super.removeElementAt(index);
        valueObjects.remove(index);
    }

    /**
     * Fills this model with the data from the passed in original object.
     *
     * @param model the original model
     */
    private void initFromModel(ListModel model)
    {
        for (int idx = 0; idx < model.size(); idx++)
        {
            addElement(model.getDisplayObject(idx));
            valueObjects.add(model.getValueObject(idx));
        }
    }
}

/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
    private static final long serialVersionUID = 1L;

    /** Stores the data type of this model. */
    private Class<?> type;

    /**
     * Creates a new instance of <code>SwingListModel</code>.
     *
     * @param model the underlying model
     */
    public SwingListModel(ListModel model)
    {
        type = model.getType();
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
        return itemAt(index).getDisplay();
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
        return itemAt(index).getValue();
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
        insertElementAt(createModelObject(display, value), index);
    }

    /**
     * Fills this model with the data from the passed in original object.
     *
     * @param model the original model
     */
    protected void initFromModel(ListModel model)
    {
        for (int idx = 0; idx < model.size(); idx++)
        {
            addElement(createModelObject(model.getDisplayObject(idx), model
                    .getValueObject(idx)));
        }
    }

    /**
     * Creates an item object from the passed in arguments.
     *
     * @param display the display object
     * @param value the value object
     * @return the new item object
     */
    protected Item createModelObject(Object display, Object value)
    {
        return new Item(display, value);
    }

    /**
     * Returns the item object at the specified index.
     *
     * @param index the index
     * @return the item object at this index
     */
    protected Item itemAt(int index)
    {
        return (Item) getElementAt(index);
    }

    /**
     * This class represents a model item. It contains both a display and a
     * value object.
     */
    private static class Item
    {
        /** The display object. */
        private Object display;

        /** The value object. */
        private Object value;

        /**
         * Creates a new instance of <code>Item</code>.
         *
         * @param displ the display object
         * @param val the value object
         */
        public Item(Object displ, Object val)
        {
            display = displ;
            value = val;
        }

        /**
         * Returns the display object.
         *
         * @return the display object
         */
        public Object getDisplay()
        {
            return display;
        }

        /**
         * Returns the value object.
         *
         * @return the value object
         */
        public Object getValue()
        {
            return value;
        }

        /**
         * Returns a string representation of this object. This is simply the
         * display object's string value because this will be displayed in the
         * Swing list component.
         *
         * @return a string representation of this object
         */
        @Override
        public String toString()
        {
            return getDisplay().toString(); // display object must not be null
        }
    }
}

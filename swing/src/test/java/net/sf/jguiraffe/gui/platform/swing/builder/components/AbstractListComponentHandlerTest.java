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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.swing.DefaultComboBoxModel;

import net.sf.jguiraffe.gui.builder.components.model.ListModel;

import org.junit.Test;

/**
 * A base test class for component handlers dealing with list-like components.
 * This class already implements some tests related to manipulations of the
 * internally used list model. Concrete sub classes must provide a reference to
 * a concrete handler implementation.
 *
 * @author Oliver Heger
 * @version $Id: AbstractListComponentHandlerTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractListComponentHandlerTest
{
    /** Constant for the number of list elements. */
    protected static final int MODEL_SIZE = 12;

    /**
     * Returns the handler under test. This method must be implemented in
     * concrete sub classes.
     *
     * @return the handler to be tested
     */
    protected abstract SwingListModelHandler getListModelHandler();

    /**
     * Initializes a default list model that can be used for testing.
     *
     * @return the initialized list model
     */
    protected ListModel setUpListModel()
    {
        return new ListModelImpl(MODEL_SIZE);
    }

    /**
     * Tests whether a valid list model is returned.
     */
    @Test
    public void testGetListModel()
    {
        ListModel model = getListModelHandler().getListModel();
        assertTrue("Wrong class of list model: " + model,
                model instanceof SwingListModel);
        assertEquals("Wrong model size", MODEL_SIZE, model.size());
        assertEquals("Wrong model type", String.class, model.getType());
        for (int i = 0; i < MODEL_SIZE; i++)
        {
            assertEquals("Wrong display object at " + i,
                    ListModelImpl.DISPLAY_PREFIX + i, model.getDisplayObject(i));
            assertEquals("Wrong value object at " + i,
                    ListModelImpl.VALUE_PREFIX + i, model.getValueObject(i));
        }
    }

    /**
     * Tests whether the correct type is returned.
     */
    @Test
    public void testGetType()
    {
        assertEquals("Wrong data type", String.class, getListModelHandler()
                .getType());
    }

    /**
     * Tests adding an item to a list.
     */
    @Test
    public void testAddItem()
    {
        SwingListModelHandler handler = getListModelHandler();
        DefaultComboBoxModel model = (DefaultComboBoxModel) handler
                .getListModel();
        handler.addItem(1, "TestDisplay", "TestValue");
        assertEquals("Wrong model size", MODEL_SIZE + 1, handler.getListModel()
                .size());
        assertEquals("Wrong size of combo model", MODEL_SIZE + 1, model
                .getSize());
        assertEquals("Wrong new display object", "TestDisplay", handler
                .getListModel().getDisplayObject(1));
        assertEquals("Wrong new value object", "TestValue", handler
                .getListModel().getValueObject(1));
        assertEquals("Wrong element at i + 1", ListModelImpl.DISPLAY_PREFIX
                + "1", handler.getListModel().getDisplayObject(2));
    }

    /**
     * Tests removing an item from the list.
     */
    @Test
    public void testRemoveItem()
    {
        SwingListModelHandler handler = getListModelHandler();
        ListModel model = handler.getListModel();
        handler.removeItem(1);
        assertEquals("Wrong number of elements", MODEL_SIZE - 1, model.size());
        assertEquals("Wrong element at i", ListModelImpl.DISPLAY_PREFIX + "2",
                model.getDisplayObject(1));
    }
}

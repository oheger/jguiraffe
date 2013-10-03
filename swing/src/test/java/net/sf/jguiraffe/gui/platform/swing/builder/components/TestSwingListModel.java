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

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingListModel.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingListModel.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingListModel
{
    /** Constant for the number of items in the list model. */
    private static final int COUNT = 8;

    /** The model to be tested. */
    private SwingListModel model;

    @Before
    public void setUp() throws Exception
    {
        model = new SwingListModel(new ListModelImpl(COUNT));
    }

    /**
     * Tests the getSize() implementation.
     */
    @Test
    public void testGetSize()
    {
        assertEquals("Wrong size", COUNT, model.getSize());
    }

    /**
     * Tests whether the model returns the correct size.
     */
    @Test
    public void testSize()
    {
        assertEquals("Wrong model size", COUNT, model.size());
    }

    /**
     * Tests whether the model returns the correct type.
     */
    @Test
    public void testGetType()
    {
        assertEquals("Wrong type", String.class, model.getType());
    }

    /**
     * Tests whether the correct elements (to be displayed) are returned.
     */
    @Test
    public void testGetElementAt()
    {
        for (int i = 0; i < COUNT; i++)
        {
            assertEquals("Wrong element", ListModelImpl.DISPLAY_PREFIX + i,
                    model.getElementAt(i).toString());
        }
    }

    /**
     * Tests whether the correct display objects are returned.
     */
    @Test
    public void testGetDisplayObject()
    {
        for (int i = 0; i < COUNT; i++)
        {
            assertEquals("Wrong display object", ListModelImpl.DISPLAY_PREFIX
                    + i, model.getDisplayObject(i).toString());
        }
    }

    /**
     * Tests whether the correct value objects are returned.
     */
    @Test
    public void testGetValueObject()
    {
        for (int i = 0; i < COUNT; i++)
        {
            assertEquals("Wrong value object", ListModelImpl.VALUE_PREFIX + i,
                    model.getValueObject(i));
        }
    }

    /**
     * Tests to add a new item.
     */
    @Test
    public void testInsertItem()
    {
        model.insertItem(1, "Display0.5", "Value0.5");
        assertEquals("Wrong new model size", COUNT + 1, model.size());
        assertEquals("Wrong display object", "Display0.5", model
                .getDisplayObject(1));
        assertEquals("Wrong value", "Value0.5", model.getValueObject(1));
    }

    /**
     * Tests to remove elements from the list model.
     */
    @Test
    public void testRemoveItem()
    {
        model.removeElementAt(1);
        assertEquals("Wrong new model size", COUNT - 1, model.size());
        assertEquals("Wrong display object",
                ListModelImpl.DISPLAY_PREFIX + "2", model.getDisplayObject(1));
        assertEquals("Wrong value object", ListModelImpl.VALUE_PREFIX + "2",
                model.getValueObject(1));
    }
}

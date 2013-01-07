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

import junit.framework.TestCase;

/**
 * Test class for SwingListModel.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingListModel.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingListModel extends TestCase
{
    SwingListModel model;

    protected void setUp() throws Exception
    {
        super.setUp();
        model = new SwingListModel(new ListModelImpl(10));
    }

    /**
     * Tests if the list model was correctly initialized.
     */
    public void testInitialize()
    {
        assertEquals(10, model.getSize());
        assertEquals(10, model.size());
        for(int i = 0; i < model.size(); i++)
        {
            assertEquals("Display" + i, model.getDisplayObject(i));
            assertEquals("Display" + i, model.getElementAt(i).toString());
            assertEquals("Value" + i, model.getValueObject(i));
        }
        assertEquals(String.class, model.getType());
    }

    /**
     * Tests to add a new item.
     */
    public void testInsertItem()
    {
        model.insertItem(1, "Display0.5", "Value0.5");
        assertEquals(11, model.size());
        assertEquals("Display0.5", model.getDisplayObject(1));
        assertEquals("Value0.5", model.getValueObject(1));
    }

    /**
     * Tests to remove elements from the list model.
     */
    public void testRemoveItem()
    {
        model.removeElementAt(1);
        assertEquals(9, model.size());
        assertEquals("Display2", model.getDisplayObject(1));
        assertEquals("Value2", model.getValueObject(1));
    }
}

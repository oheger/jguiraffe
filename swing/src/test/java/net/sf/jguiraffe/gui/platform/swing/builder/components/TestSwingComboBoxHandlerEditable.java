/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
import static org.junit.Assert.assertNull;

import javax.swing.JComboBox;

import net.sf.jguiraffe.gui.builder.components.model.EditableComboBoxModel;

import org.junit.Before;
import org.junit.Test;

/**
 * A specialized test class for {@code SwingComboBoxHandler} which deals with
 * editable combo boxes.
 *
 * @author Oliver Heger
 * @version $Id: $
 */
public class TestSwingComboBoxHandlerEditable
{
    /**
     * Constant for a suffix to be appended by a transformation to a display
     * object.
     */
    private static final String TRANS_TODISPLAY = "_toDisplay";

    /**
     * Constant for a suffix to be appended by a transformation to a value
     * object.
     */
    private static final String TRANS_TOVALUE = "_toValue";

    /** Constant for a value that is not part of the list model. */
    private static final Object VALUE = "UserInput";

    /** The underlying combo box. */
    private JComboBox combo;

    /** The list model. */
    private EditableListModel model;

    /** The handler to be tested. */
    private SwingComboBoxHandler handler;

    @Before
    public void setUp() throws Exception
    {
        combo = new JComboBox();
        combo.setEditable(true);
        model = new EditableListModel(16);
        handler = new SwingComboBoxHandler(combo, model);
    }

    /**
     * Tests whether getData() correctly returns an object from the model.
     */
    @Test
    public void testGetDataFromSelection()
    {
        combo.setSelectedItem(model.getDisplayObject(1));
        assertEquals("Wrong result", model.getValueObject(1), handler.getData());
    }

    /**
     * Tests getData() if the component does not contain data.
     */
    @Test
    public void testGetDataNoContent()
    {
        combo.setSelectedItem(null);
        assertNull("Got data", handler.getData());
    }

    /**
     * Tests content not part of the model is correctly processed.
     */
    @Test
    public void testGetDataFromDirectInput()
    {
        combo.setSelectedItem(VALUE);
        assertEquals("Wrong result", VALUE + TRANS_TOVALUE, handler.getData());
    }

    /**
     * Tests a setData() operation if the model contains the passed in value
     * object.
     */
    @Test
    public void testSetDataFromModel()
    {
        handler.setData(model.getValueObject(2));
        assertEquals("Wrong result", model.getDisplayObject(2),
                combo.getSelectedItem());
    }

    /**
     * Tests setData() if the passed in object cannot be found in the model.
     */
    @Test
    public void testSetDataNotInModel()
    {
        handler.setData(VALUE);
        assertEquals("Value not set", VALUE + TRANS_TODISPLAY,
                combo.getSelectedItem());
    }

    /**
     * Tests setData() for null input.
     */
    @Test
    public void testSetDataNull()
    {
        handler.setData(null);
        assertNull("Got a selected item", combo.getSelectedItem());
    }

    /**
     * A specialized list model which also implements EditableComboBoxModel.
     */
    private static class EditableListModel extends ListModelImpl implements
            EditableComboBoxModel
    {
        public EditableListModel(int cnt)
        {
            super(cnt);
        }

        public Object toDisplay(Object value)
        {
            return String.valueOf(value) + TRANS_TODISPLAY;
        }

        public Object toValue(Object displ)
        {
            return String.valueOf(displ) + TRANS_TOVALUE;
        }
    }
}

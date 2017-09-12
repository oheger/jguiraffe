/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import static org.junit.Assert.assertSame;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;

import net.sf.jguiraffe.gui.platform.swing.builder.event.ChangeListener;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * An abstract base class for testing component handlers for comboboxes. This
 * class already implements some concrete tests common to all kinds of combo
 * boxes. Derived classes will have special tests for editable and non-editable
 * combo boxes.
 *
 * @author Oliver Heger
 * @version $Id: AbstractComboBoxComponentHandlerTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractComboBoxComponentHandlerTest extends
        AbstractListComponentHandlerTest
{
    /** Stores the underlying combobox. */
    protected JComboBox component;

    @Before
    public void setUp() throws Exception
    {
        component = new JComboBox();
    }

    /**
     * Returns the handler to be tested. This method performs a cast from the
     * list handler returned by the super class.
     *
     * @return the handler to be tested
     */
    private SwingComboBoxHandler getComboBoxHandler()
    {
        return (SwingComboBoxHandler) getListModelHandler();
    }

    /**
     * Tests accessing the combo box.
     */
    @Test
    public void testGetComboBox()
    {
        assertSame("Wrong combo box returned", component, getComboBoxHandler()
                .getComboBox());
    }

    /**
     * Tests adding a change listener.
     */
    @Test
    public void testAddChangeListener()
    {
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        ActionEvent event = new ActionEvent(component, 42, "MyAction");
        mockListener.componentChanged(event);
        EasyMock.replay(mockListener);
        getComboBoxHandler().addChangeListener(mockListener);
        getComboBoxHandler().actionPerformed(event);
        EasyMock.verify(mockListener);
    }

    /**
     * Tests removing a change listener.
     */
    @Test
    public void testRemoveChangeListener()
    {
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        ActionEvent event = new ActionEvent(component, 42, "MyAction");
        mockListener.componentChanged(event);
        EasyMock.replay(mockListener);
        getComboBoxHandler().addChangeListener(mockListener);
        getComboBoxHandler().actionPerformed(event);
        getComboBoxHandler().removeChangeListener(mockListener);
        getComboBoxHandler().actionPerformed(event);
        EasyMock.verify(mockListener);
    }
}

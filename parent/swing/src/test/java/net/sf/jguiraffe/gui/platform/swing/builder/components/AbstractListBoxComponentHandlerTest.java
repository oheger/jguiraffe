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
import static org.junit.Assert.assertTrue;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;

import net.sf.jguiraffe.gui.platform.swing.builder.event.ChangeListener;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * A base test class for Swing list box handler classes. This class contains
 * some tests for all kinds of list box handlers. Concrete sub classes will deal
 * with single and multi selection list boxes.
 *
 * @author Oliver Heger
 * @version $Id: AbstractListBoxComponentHandlerTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractListBoxComponentHandlerTest extends
        AbstractListComponentHandlerTest
{
    /** Stores the underlying list. */
    protected JList component;

    /**
     * Initializes the test. This implementation already creates the list
     * component.
     */
    @Before
    public void setUp() throws Exception
    {
        component = new JList();
    }

    /**
     * Returns the handler to be tested. This implementation calls the
     * <code>getListModelHandler()</code> method to be defined in concrete sub
     * classes and casts the result.
     *
     * @return the handler to be tested
     */
    private SwingListBoxHandler getListHandler()
    {
        return (SwingListBoxHandler) getListModelHandler();
    }

    /**
     * Tests accessing the list component.
     */
    @Test
    public void testGetList()
    {
        assertSame("Wrong wrapped list", component, getListHandler().getList());
    }

    /**
     * Tests accessing the outer component. This should be a scroll pane.
     */
    @Test
    public void testGetOuterComponent()
    {
        assertTrue("Wrong outer component", getListHandler()
                .getOuterComponent() instanceof JScrollPane);
        JScrollPane scrp = (JScrollPane) getListHandler().getOuterComponent();
        assertSame("Wrong viewport component", component, scrp.getViewport()
                .getView());
    }

    /**
     * Tests registering a change listener.
     */
    @Test
    public void testAddChangeListener()
    {
        final ListSelectionEvent event = new ListSelectionEvent(component, 0,
                1, false);
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        mockListener.componentChanged(event);
        EasyMock.replay(mockListener);
        getListHandler().addChangeListener(mockListener);
        getListHandler().valueChanged(event);
        EasyMock.verify(mockListener);
    }

    /**
     * Tests removing a change listener.
     */
    @Test
    public void testRemoveChangeListener()
    {
        final ListSelectionEvent event = new ListSelectionEvent(component, 0,
                1, false);
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        mockListener.componentChanged(event);
        EasyMock.replay(mockListener);
        getListHandler().addChangeListener(mockListener);
        getListHandler().valueChanged(event);
        getListHandler().removeChangeListener(mockListener);
        getListHandler().valueChanged(event);
        EasyMock.verify(mockListener);
    }

    /**
     * Tests firing of an adjusting event. Events of this type should be
     * ignored.
     */
    @Test
    public void testFireChangeEventAdjusting()
    {
        final ListSelectionEvent event = new ListSelectionEvent(component, 0,
                1, true);
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        EasyMock.replay(mockListener);
        getListHandler().addChangeListener(mockListener);
        getListHandler().valueChanged(event);
        EasyMock.verify(mockListener);
    }
}

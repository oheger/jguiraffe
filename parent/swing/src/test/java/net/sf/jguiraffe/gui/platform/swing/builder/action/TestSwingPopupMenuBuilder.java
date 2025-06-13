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
package net.sf.jguiraffe.gui.platform.swing.builder.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.event.MouseEvent;

import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionManager;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingPopupMenuBuilder.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingPopupMenuBuilder.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingPopupMenuBuilder
{
    /** Constant for the triggering mouse event. */
    private static final MouseEvent EVENT = new MouseEvent(new JTextField(),
            42, System.currentTimeMillis(), 0, 150, 100, 1, true);

    /** The builder to be tested. */
    private SwingPopupMenuBuilderTestImpl builder;

    @Before
    public void setUp() throws Exception
    {
        ActionManager actionManager = EasyMock.createMock(ActionManager.class);
        ActionBuilder actionBuilder = EasyMock.createMock(ActionBuilder.class);
        builder = new SwingPopupMenuBuilderTestImpl(actionManager, actionBuilder, EVENT);
        assertSame("Wrong action manager", actionManager, builder.getActionManager());
        assertSame("Wrong action builder", actionBuilder, builder.getActionBuilder());
    }

    /**
     * Tests a newly created instance.
     */
    @Test
    public void testInit()
    {
        assertNotNull("No popup menu", builder.getMenu());
        assertEquals("Wrong triggering event", EVENT, builder.getTriggeringEvent());
    }

    /**
     * Tests creating an instance without an event. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoEvent()
    {
        new SwingPopupMenuBuilder(null, null, null);
    }

    /**
     * Tests whether the correct menu object is returned.
     */
    @Test
    public void testGetMenuUnderConstruction()
    {
        assertEquals("Wrong menu under construction", builder.getMenu(),
                builder.getMenuUnderConstruction());
    }

    /**
     * Tests creating the popup menu. It should also be displayed.
     */
    @Test
    public void testCreate()
    {
        assertEquals("Wrong result", builder.getMenu(), builder.create());
        assertTrue("Show was not called", builder.showCalled);
    }

    /**
     * Tests displaying the menu constructed by this builder.
     */
    @Test
    public void testShowMenu()
    {
        JPopupMenuMock menu = new JPopupMenuMock();
        builder.mockShow = false;
        builder.showMenu(menu);
        assertEquals("Wrong component", EVENT.getComponent(),
                menu.showComponent);
        assertEquals("Wrong x", EVENT.getX(), menu.showX);
        assertEquals("Wrong y", EVENT.getY(), menu.showY);
    }

    /**
     * A specialized test implementation of SwingPopuMenuBuilder.
     */
    private static class SwingPopupMenuBuilderTestImpl extends
            SwingPopupMenuBuilder
    {
        /** A flag whether the showMenu() method should be mocked. */
        boolean mockShow = true;

        /** A flag whether the showMenu() method is invoked. */
        boolean showCalled;

        public SwingPopupMenuBuilderTestImpl(ActionManager actMan,
                ActionBuilder builder, MouseEvent event)
        {
            super(actMan, builder, event);
        }

        /**
         * Records this invocation and invokes the inherited method unless the
         * mockShow property is set.
         */
        @Override
        protected void showMenu(JPopupMenu m)
        {
            showCalled = true;
            if (!mockShow)
            {
                super.showMenu(m);
            }
        }
    }

    /**
     * A mock implementation of a popup menu for checking whether the show()
     * method is invoked correctly.
     */
    @SuppressWarnings("serial")
    private static class JPopupMenuMock extends JPopupMenu
    {
        /** The component passed to show(). */
        Component showComponent;

        /** The x position passed to show(). */
        int showX;

        /** The y position passed to show(). */
        int showY;

        /**
         * Just records this invocation and stores the parameters.
         */
        @Override
        public void show(Component invoker, int x, int y)
        {
            showComponent = invoker;
            showX = x;
            showY = y;
        }
    }
}

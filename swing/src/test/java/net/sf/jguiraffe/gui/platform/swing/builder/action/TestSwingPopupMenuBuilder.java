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
package net.sf.jguiraffe.gui.platform.swing.builder.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.MenuElement;

import net.sf.jguiraffe.gui.builder.action.PopupMenuBuilder;

import org.junit.Test;

/**
 * Test class for SwingPopupMenuBuilder.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingPopupMenuBuilder.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingPopupMenuBuilder extends AbstractSwingMenuBuilderTest
{
    /** Constant for the triggering mouse event. */
    private static final MouseEvent EVENT = new MouseEvent(new JTextField(),
            42, System.currentTimeMillis(), 0, 150, 100, 1, true);

    /**
     * Creates the builder to test. This implementation returns a
     * SwingPopupMenuBuilder that is initialized with the test event.
     */
    @Override
    protected PopupMenuBuilder createBuilder()
    {
        return new SwingPopupMenuBuilderTestImpl(EVENT);
    }

    /**
     * Obtains the components of the specified menu. The menu is expected to be
     * a popup menu.
     *
     * @param menu the menu
     * @return the components of this menu
     */
    @Override
    protected Object[] getComponents(MenuElement menu)
    {
        return ((Container) menu).getComponents();
    }

    /**
     * Convenience method for obtaining the test builder.
     *
     * @return the test builder
     */
    private SwingPopupMenuBuilderTestImpl getTestBuilder()
    {
        return (SwingPopupMenuBuilderTestImpl) getBuilder();
    }

    /**
     * Tests a newly created instance.
     */
    @Test
    public void testInit()
    {
        SwingPopupMenuBuilder pb = getTestBuilder();
        assertNotNull("No popup menu", pb.getMenu());
        assertEquals("Wrong triggering event", EVENT, pb.getTriggeringEvent());
    }

    /**
     * Tests creating an instance without an event. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoEvent()
    {
        new SwingPopupMenuBuilder(null);
    }

    /**
     * Tests creating the popup menu. It should also be displayed.
     */
    @Test
    public void testCreate()
    {
        testAddAction();
        assertTrue("Show was not called", getTestBuilder().showCalled);
    }

    /**
     * Tests displaying the menu constructed by this builder.
     */
    @Test
    public void testShowMenu()
    {
        JPopupMenuMock menu = new JPopupMenuMock();
        getTestBuilder().mockShow = false;
        getTestBuilder().showMenu(menu);
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

        public SwingPopupMenuBuilderTestImpl(MouseEvent event)
        {
            super(event);
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

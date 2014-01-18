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

import javax.swing.JMenu;
import javax.swing.MenuElement;

import net.sf.jguiraffe.gui.builder.action.PopupMenuBuilder;

import org.junit.Test;

/**
 * Test class for SwingSubMenuBuilder.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingSubMenuBuilder.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingSubMenuBuilder extends AbstractSwingMenuBuilderTest
{
    /**
     * Creates the builder to be tested. This is a SwingSubMenuBuilder object.
     *
     * @return the builder to be tested
     */
    @Override
    protected PopupMenuBuilder createBuilder()
    {
        return new SwingSubMenuBuilder(menuData);
    }

    /**
     * Returns an array with the sub components of the specified menu. This
     * implementation expects that a menu is passed in and returns its content.
     *
     * @param menu the menu (must be a JMenu)
     * @return the menu components of this menu
     */
    @Override
    protected Object[] getComponents(MenuElement menu)
    {
        return ((JMenu) menu).getMenuComponents();
    }

    /**
     * Tests creating an instance without a menu description. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoDesc()
    {
        new SwingSubMenuBuilder(null);
    }

    /**
     * Tests creating a menu. We check whether the menu's properties match the
     * content of the action data object passed to the constructor.
     */
    @Test
    public void testCreate()
    {
        checkItem((JMenu) createMenu());
    }
}

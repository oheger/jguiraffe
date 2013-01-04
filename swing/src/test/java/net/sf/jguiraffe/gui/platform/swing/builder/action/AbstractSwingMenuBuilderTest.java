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
package net.sf.jguiraffe.gui.platform.swing.builder.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.MenuElement;

import net.sf.jguiraffe.gui.builder.action.ActionData;
import net.sf.jguiraffe.gui.builder.action.ActionDataImpl;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.action.PopupMenuBuilder;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * An abstract test class for different Swing-specific PopupMenuBuilder
 * implementations. This class defines a set of tests that can be executed for
 * all menu builder implementations. The concrete implementation to be tested
 * must be provided by a concrete subclass. Access to the menu components is
 * also specific for a concrete implementation.
 *
 * @author Oliver Heger
 * @version $Id: AbstractSwingMenuBuilderTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractSwingMenuBuilderTest
{
    /** Constant for the name of the icon. */
    private static final String ICON_NAME = "/icon.gif";

    /** Constant for the text of the menu. */
    private static final String TEXT = "TestMenu";

    /** Constant for the tool tip of the menu. */
    private static final String TIP = "Tool tip of the test menu";

    /** Constant for the mnemonic key of the menu. */
    private static final int MNEMO = 'T';

    /** Stores the icon for the menu. */
    private static Icon icon;

    /** The action data object with the description of the menu. */
    protected static ActionData menuData;

    /** The builder to be tested. */
    private PopupMenuBuilder builder;

    /**
     * Performs one-time initialization before the test class is executed. Loads
     * the test icon and initializes the default menu description.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        URL iconUrl = TestSwingSubMenuBuilder.class.getResource(ICON_NAME);
        assertNotNull("Icon cannot be loaded", iconUrl);
        icon = new ImageIcon(iconUrl);
        ActionDataImpl actData = new ActionDataImpl();
        actData.setIcon(icon);
        actData.setText(TEXT);
        actData.setToolTip(TIP);
        actData.setMnemonicKey(MNEMO);
        menuData = actData;
    }

    /**
     * Creates the builder object to be tested. This method is called by
     * <code>setUp()</code> to obtain the test fixture.
     *
     * @return the builder object to be tested
     */
    protected abstract PopupMenuBuilder createBuilder();

    /**
     * Returns the components of the specified menu. Concrete subclasses must
     * here return the correct sub items of the menu type they support.
     *
     * @param menu the menu
     * @return an array with the components of this menu
     */
    protected abstract Object[] getComponents(MenuElement menu);

    @Before
    public void setUp() throws Exception
    {
        builder = createBuilder();
    }

    /**
     * Returns the builder that is to be tested.
     *
     * @return the test builder
     */
    public PopupMenuBuilder getBuilder()
    {
        return builder;
    }

    /**
     * Helper method for obtaining the menu created by the builder.
     *
     * @return the menu
     */
    protected MenuElement createMenu()
    {
        return (MenuElement) builder.create();
    }

    /**
     * Obtains the single item of the test menu. This method is used for testing
     * whether the correct element was added by one of the add methods.
     *
     * @param menu the menu
     * @return the single sub item
     */
    private Object getItem(MenuElement menu)
    {
        Object[] items = getComponents(menu);
        assertEquals("Wrong number of menu items", 1, items.length);
        return items[0];
    }

    /**
     * Tests whether the specified menu item was constructed correctly. We test
     * whether the properties are all initialized with the test values.
     *
     * @param item the menu to test
     */
    protected void checkItem(JMenuItem item)
    {
        assertEquals("Wrong text", TEXT, item.getText());
        assertEquals("Wrong icon", icon, item.getIcon());
        assertEquals("Wrong tool tip", TIP, item.getToolTipText());
        assertEquals("Wrong mnemonic", MNEMO, item.getMnemonic());
    }

    /**
     * Tests adding an action.
     */
    @Test
    public void testAddAction()
    {
        Runnable task = EasyMock.createNiceMock(Runnable.class);
        SwingFormAction action = new SwingFormAction(TEXT, task);
        action.putValue(Action.NAME, TEXT);
        action.putValue(Action.SMALL_ICON, icon);
        action.putValue(Action.MNEMONIC_KEY, MNEMO);
        action.putValue(Action.SHORT_DESCRIPTION, TIP);
        assertEquals("Wrong builder reference", builder, builder
                .addAction(action));
        MenuElement menu = createMenu();
        JMenuItem item = (JMenuItem) getItem(menu);
        checkItem(item);
    }

    /**
     * Tests adding a null action. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddActionNull()
    {
        builder.addAction(null);
    }

    /**
     * Tests adding an action to this builder that is not a swing action. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddActionNoSwingAction()
    {
        FormAction action = EasyMock.createNiceMock(FormAction.class);
        builder.addAction(action);
    }

    /**
     * Tests adding a separator to the menu.
     */
    @Test
    public void testAddSeparator()
    {
        assertEquals("Wrong builder reference", builder, builder.addSeparator());
        MenuElement menu = createMenu();
        Object item = getItem(menu);
        assertTrue("Wrong separator component: " + item,
                item instanceof JPopupMenu.Separator);
    }

    /**
     * Helper method for testing adding a component to the builder.
     *
     * @param comp the component to add
     */
    private void checkAddComponent(Object comp)
    {
        builder.addSubMenu(comp);
        MenuElement menu = createMenu();
        assertEquals("Wrong menu component", comp, getItem(menu));
    }

    /**
     * Tests adding a sub menu.
     */
    @Test
    public void testAddSubMenu()
    {
        checkAddComponent(new JMenu(TEXT));
    }

    /**
     * Tests whether a different component can be added to the menu.
     */
    @Test
    public void testAddSubMenuOtherComponent()
    {
        checkAddComponent(new JTextField());
    }

    /**
     * Tests adding a null sub menu. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddSubMenuNull()
    {
        builder.addSubMenu(null);
    }

    /**
     * Tests adding an object as a sub menu that is not a Swing menu. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddSubMenuNoMenu()
    {
        builder.addSubMenu(this);
    }

    /**
     * Tests obtaining a sub menu builder.
     */
    @Test
    public void testSubMenuBuilder()
    {
        PopupMenuBuilder subBuilder = builder.subMenuBuilder(menuData);
        assertTrue("Wrong sub builder: " + subBuilder,
                subBuilder instanceof SwingSubMenuBuilder);
        JMenu menu = (JMenu) subBuilder.create();
        checkItem(menu);
    }

    /**
     * Tests requesting a sub menu builder without a menu description. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSubMenuBuilderNull()
    {
        builder.subMenuBuilder(null);
    }
}

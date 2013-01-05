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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.EnumSet;
import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import net.sf.jguiraffe.gui.builder.action.Accelerator;
import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionDataImpl;
import net.sf.jguiraffe.gui.builder.action.ActionTask;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.builder.action.PopupMenuHandler;
import net.sf.jguiraffe.gui.builder.action.tags.ActionTag;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData;
import net.sf.jguiraffe.gui.builder.event.Keys;
import net.sf.jguiraffe.gui.builder.event.Modifiers;
import net.sf.jguiraffe.gui.forms.BindingStrategy;
import net.sf.jguiraffe.gui.platform.swing.builder.components.SwingButtonHandler;
import net.sf.jguiraffe.resources.ResourceManager;
import net.sf.jguiraffe.transform.TransformerContext;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingActionManager.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingActionManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingActionManager
{
    /** Constant for the name of the test action. */
    private static final String ACTION_NAME = "TESTACTION";

    /** Constant for the action's text. */
    private static final String ACTION_TEXT = "File open";

    /** Constant for the action's tool tip. */
    private static final String ACTION_TOOLTIP = "Opens a file";

    /** Constant for the action's mnemonic. */
    private static final Integer ACTION_MNEMO = new Integer('o');

    /** Constant for the action's accelerator. */
    private static final Accelerator ACTION_ACC = Accelerator.getInstance(
            Keys.F3, null);

    /** The key stroke corresponding to the accelerator. */
    private static final KeyStroke ACTION_KEYSTROKE = KeyStroke.getKeyStroke(
            KeyEvent.VK_F3, 0);

    /** Constant for the action's icon. */
    private static final ImageIcon ACTION_ICON;

    /** The action manager to be tested. */
    private SwingActionManager actionManager;

    /** The action builder used for testing. */
    private ActionBuilder actionBuilder;

    /** Stores the task object for the action. */
    private Object actionTask;

    @Before
    public void setUp() throws Exception
    {
        actionBuilder = new ActionBuilder();
        actionManager = new SwingActionManager();
    }

    /**
     * Tests creating action objects.
     */
    @Test
    public void testCreateAction() throws FormActionException
    {
        ActionDataImpl actData = setUpActionData();
        FormAction action = actionManager.createAction(actionBuilder, actData);
        assertNotNull("No action returned", action);
        assertTrue("Wrong action class", action instanceof SwingFormAction);
        SwingFormAction act = (SwingFormAction) action;
        assertEquals("Wrong action name", ACTION_NAME, act.getName());
        assertEquals("Wrong action text", ACTION_TEXT, act
                .getValue(Action.NAME));
        assertEquals("Wrong tool tip", ACTION_TOOLTIP, act
                .getValue(Action.SHORT_DESCRIPTION));
        assertEquals("Wrong mnemonic", ACTION_MNEMO, act
                .getValue(Action.MNEMONIC_KEY));
        assertEquals("Wrong key stroke", ACTION_KEYSTROKE, act
                .getValue(Action.ACCELERATOR_KEY));
        assertEquals("Wrong icon", ACTION_ICON, act.getValue(Action.SMALL_ICON));
        assertEquals("Wrong action task", actionTask, act.getTask());
    }

    /**
     * Tests creating an action with most properties left empty.
     */
    @Test
    public void testCreateActionWithFewProperties() throws FormActionException
    {
        ActionDataImpl actData = new ActionDataImpl();
        actData.setName(ACTION_NAME);
        actData.setTask(EasyMock.createMock(ActionTask.class));
        SwingFormAction action = (SwingFormAction) actionManager.createAction(
                actionBuilder, actData);
        assertNull("Name was set", action.getValue(Action.NAME));
        assertNull("Mnemonic was set", action.getValue(Action.MNEMONIC_KEY));
        assertNull("Tool tip was set", action
                .getValue(Action.SHORT_DESCRIPTION));
        assertNull("Icon was set", action.getValue(Action.SMALL_ICON));
        assertNull("Accelerator was set", action.getValue(Action.ACCELERATOR_KEY));
    }

    /**
     * Tests creating a menu item based on an action.
     */
    @Test
    public void testCreateMenuItemFromAction() throws FormActionException
    {
        checkCreateActionMenuItem(false, false);
    }

    /**
     * Tests creating a checked menu item based on an action.
     */
    @Test
    public void testCreateCheckedMenuItemFromAction()
            throws FormActionException
    {
        checkCreateActionMenuItem(false, true);
    }

    /**
     * Tests creating a menu item based on an action which has an icon.
     */
    @Test
    public void testCreateMenuItemFromActionWithIcon()
            throws FormActionException
    {
        checkCreateActionMenuItem(true, false);
    }

    /**
     * Tests creating a checked menu item based on an action which has an icon.
     */
    @Test
    public void testCreateCheckedMenuItemFromActionWithIcon()
            throws FormActionException
    {
        checkCreateActionMenuItem(true, true);
    }

    /**
     * Tests creating a menu item from an action data object.
     */
    @Test
    public void testCreateMenuItemFromActionData() throws FormActionException
    {
        checkCreateActionDataMenuItem(false);
    }

    /**
     * Tests creating a checked menu item from an action data object.
     */
    @Test
    public void testCreateCheckedMenuItemFromActionData()
            throws FormActionException
    {
        checkCreateActionDataMenuItem(true);
    }

    /**
     * Tests creating a simple menu that is a sub menu of the menu bar.
     */
    @Test
    public void testCreateMenu() throws FormActionException
    {
        TextIconData data = new TextIconData(setUpActionTag());
        data.setText("File");
        data.setMnemonicKey("F");
        data.setIcon(ACTION_ICON);
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = createMenu(data, menuBar);
        assertEquals("Text was not set", "File", menu.getText());
        assertEquals("Icon was not set", ACTION_ICON, menu.getIcon());
        assertEquals("Mnemonic was not set", 'F', menu.getMnemonic());
        assertEquals("Menu was not added to bar", 1, menuBar.getMenuCount());
    }

    /**
     * Tests whether the properties of a menu can be resolved from resources.
     */
    @Test
    public void testCreateMenuResources() throws FormActionException
    {
        TextIconData data = new TextIconData(setUpActionTag());
        data.setTextres("MEN_FILE_TXT");
        data.setMnemonicResID("MEN_FILE_MNEMO");
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = createMenu(data, menuBar);
        assertEquals("Text was not set", "RES_null_MEN_FILE_TXT", menu.getText());
        assertEquals("Mnemonic was not set", 'R', menu.getMnemonic());
        assertEquals("Menu was not added to bar", 1, menuBar.getMenuCount());
    }

    /**
     * Tests creating a sub menu (i.e. a menu that is a child of another menu).
     */
    @Test
    public void testCreateSubMenu() throws FormActionException
    {
        TextIconData data = new TextIconData(setUpActionTag());
        data.setText("Tests");
        JMenu parentMenu = new JMenu("File");

        JMenu menu = createMenu(data, parentMenu);
        assertEquals("Text was not set", "Tests", menu.getText());
        assertNull("Menu has an item", menu.getIcon());
        assertEquals("Menu was not added to parent menu", 1, parentMenu
                .getItemCount());
    }

    /**
     * Tests creating a menu without a valid parent reference.
     */
    @Test(expected = FormActionException.class)
    public void testCreateMenuWithoutParent() throws FormActionException
    {
        TextIconData data = new TextIconData(new ActionTag());
        data.setText("Invalid");
        createMenu(data, null);
    }

    /**
     * Tests creating a menu bar.
     */
    @Test
    public void testCreateMenuBar() throws FormActionException
    {
        Object result = actionManager.createMenuBar(actionBuilder);
        assertNotNull("Undefined return value", result);
        assertTrue("Wrong class returned", result instanceof JMenuBar);
        assertEquals("Menu bar is not empty", 0, ((JMenuBar) result)
                .getMenuCount());
    }

    /**
     * Tests creating a toolbar.
     */
    @Test
    public void testCreateToolbar() throws FormActionException
    {
        Object result = actionManager.createToolbar(actionBuilder);
        assertNotNull("Undefined return value", result);
        assertTrue("Wrong class returned", result instanceof JToolBar);
        assertEquals("Toolbar not empty", 0, ((JToolBar) result)
                .getComponentCount());
    }

    /**
     * Tests creating a tool button based on an action.
     */
    @Test
    public void testCreateToolButtonFromAction() throws FormActionException
    {
        checkCreateActionButton(false, false);
    }

    /**
     * Tests creating a tool button based on an action with text.
     */
    @Test
    public void testCreateToolButtonFromActionWithText()
            throws FormActionException
    {
        checkCreateActionButton(true, false);
    }

    /**
     * Tests creating a checked tool button based on an action.
     */
    @Test
    public void testCreateCheckedToolButtonFromAction()
            throws FormActionException
    {
        checkCreateActionButton(false, true);
    }

    /**
     * Tests creating a checked tool button based on an action with text.
     */
    @Test
    public void testCreateCheckedToolButtonFromActionWithText()
            throws FormActionException
    {
        checkCreateActionButton(true, true);
    }

    /**
     * Tests creating a tool button based on an action data object.
     */
    @Test
    public void testCreateToolButtonFromActionData() throws FormActionException
    {
        checkCreateActionDataButton(false);
    }

    /**
     * Tests creating a checked tool button based on an action data object.
     */
    @Test
    public void testCreateCheckedToolButtonFromActionData()
            throws FormActionException
    {
        checkCreateActionDataButton(true);
    }

    /**
     * Tests adding a separator to a menu.
     */
    @Test
    public void testAddMenuSeparator() throws FormActionException
    {
        JMenu menu = new JMenu();
        actionManager.addMenuSeparator(actionBuilder, menu);
        assertEquals("Separator was not added", 1, menu.getItemCount());
        assertNull("Wrong component added", menu.getItem(0));
    }

    /**
     * Tests adding a separator to a toolbar.
     */
    @Test
    public void testAddToolbarSeparator() throws FormActionException
    {
        JToolBar bar = new JToolBar();
        actionManager.addToolBarSeparator(actionBuilder, bar);
        assertEquals("Separator was not added", 1, bar.getComponentCount());
        assertTrue("Wrong component added",
                bar.getComponent(0) instanceof JSeparator);
    }

    /**
     * Tests registering a menu handler for an object that is no Component. This
     * should cause an exception.
     */
    @Test(expected = FormActionException.class)
    public void testRegisterPopupMenuHandlerNoComponent()
            throws FormActionException
    {
        actionManager.registerPopupMenuHandler(this, EasyMock
                .createNiceMock(PopupMenuHandler.class),
                new ComponentBuilderData());
    }

    /**
     * Tests registering a menu handler at a component.
     */
    @Test
    public void testRegisterPopupMenuHandler() throws FormActionException
    {
        JComponent comp = new JTextArea();
        PopupMenuHandler handler = EasyMock
                .createNiceMock(PopupMenuHandler.class);
        ComponentBuilderData compData = new ComponentBuilderData();
        actionManager.registerPopupMenuHandler(comp, handler, compData);
        int count = 0;
        for (MouseListener ml : comp.getMouseListeners())
        {
            if (ml instanceof SwingPopupListener)
            {
                count++;
                SwingPopupListener spl = (SwingPopupListener) ml;
                assertEquals("Wrong handler", handler, spl.getMenuHandler());
                assertEquals("Wrong builder data", compData, spl
                        .getComponentBuilderData());
            }
        }
        assertEquals("Wrong number of listeners", 1, count);
    }

    /**
     * Tests handling of accelerators that contain simple characters.
     */
    @Test
    public void testKeyStrokeFromAcceleratorChar()
    {
        for (char c = 'a'; c <= 'z'; c++)
        {
            Accelerator acc = Accelerator.getInstance(c, null);
            KeyStroke ks = SwingActionManager.keyStrokeFromAccelerator(acc);
            assertEquals("Wrong character", c, ks.getKeyChar());
            assertEquals("Modifiers are set", 0, ks.getModifiers());
        }
    }

    /**
     * Tests handling of accelerators that contain a key code.
     */
    @Test
    public void testKeyStrokeFromAcceleratorKeyCode()
    {
        Accelerator acc = Accelerator.getInstance(KeyEvent.VK_F1, null);
        KeyStroke ks = SwingActionManager.keyStrokeFromAccelerator(acc);
        assertEquals("Wrong key code", KeyEvent.VK_F1, ks.getKeyCode());
        assertEquals("Modifiers are set", 0, ks.getModifiers());
    }

    /**
     * Helper method for testing the conversion of an accelerator with a special
     * key code into a Swing key stroke.
     *
     * @param key the key to be used by the accelerator
     * @param expected the expected Swing key code
     */
    private static void checkAccSpecialKey(Keys key, int expected)
    {
        Accelerator acc = Accelerator.getInstance(key, null);
        KeyStroke ks = SwingActionManager.keyStrokeFromAccelerator(acc);
        assertEquals("Wrong key code", expected, ks.getKeyCode());
        assertEquals("Modifiers are set", 0, ks.getModifiers());
    }

    /**
     * Tests whether special keys in accelerators are correctly converted.
     */
    @Test
    public void testKeyStokeFromAcceleratorSpecialKey()
    {
        checkAccSpecialKey(Keys.BACKSPACE, KeyEvent.VK_BACK_SPACE);
        checkAccSpecialKey(Keys.DELETE, KeyEvent.VK_DELETE);
        checkAccSpecialKey(Keys.DOWN, KeyEvent.VK_DOWN);
        checkAccSpecialKey(Keys.END, KeyEvent.VK_END);
        checkAccSpecialKey(Keys.ENTER, KeyEvent.VK_ENTER);
        checkAccSpecialKey(Keys.ESCAPE, KeyEvent.VK_ESCAPE);
        checkAccSpecialKey(Keys.F1, KeyEvent.VK_F1);
        checkAccSpecialKey(Keys.F2, KeyEvent.VK_F2);
        checkAccSpecialKey(Keys.F3, KeyEvent.VK_F3);
        checkAccSpecialKey(Keys.F4, KeyEvent.VK_F4);
        checkAccSpecialKey(Keys.F5, KeyEvent.VK_F5);
        checkAccSpecialKey(Keys.F6, KeyEvent.VK_F6);
        checkAccSpecialKey(Keys.F7, KeyEvent.VK_F7);
        checkAccSpecialKey(Keys.F8, KeyEvent.VK_F8);
        checkAccSpecialKey(Keys.F9, KeyEvent.VK_F9);
        checkAccSpecialKey(Keys.F10, KeyEvent.VK_F10);
        checkAccSpecialKey(Keys.F11, KeyEvent.VK_F11);
        checkAccSpecialKey(Keys.F12, KeyEvent.VK_F12);
        checkAccSpecialKey(Keys.F13, KeyEvent.VK_F13);
        checkAccSpecialKey(Keys.F14, KeyEvent.VK_F14);
        checkAccSpecialKey(Keys.F15, KeyEvent.VK_F15);
        checkAccSpecialKey(Keys.F16, KeyEvent.VK_F16);
        checkAccSpecialKey(Keys.HOME, KeyEvent.VK_HOME);
        checkAccSpecialKey(Keys.INSERT, KeyEvent.VK_INSERT);
        checkAccSpecialKey(Keys.LEFT, KeyEvent.VK_LEFT);
        checkAccSpecialKey(Keys.PAGE_DOWN, KeyEvent.VK_PAGE_DOWN);
        checkAccSpecialKey(Keys.PAGE_UP, KeyEvent.VK_PAGE_UP);
        checkAccSpecialKey(Keys.PRINT_SCREEN, KeyEvent.VK_PRINTSCREEN);
        checkAccSpecialKey(Keys.RIGHT, KeyEvent.VK_RIGHT);
        checkAccSpecialKey(Keys.SPACE, KeyEvent.VK_SPACE);
        checkAccSpecialKey(Keys.TAB, KeyEvent.VK_TAB);
        checkAccSpecialKey(Keys.UP, KeyEvent.VK_UP);
    }

    /**
     * Tests whether modifiers in accelerators are correctly converted.
     */
    @Test
    public void testKeyStrokeFromAcceleratorModifiers()
    {
        Accelerator acc = Accelerator.getInstance('j', EnumSet
                .allOf(Modifiers.class));
        KeyStroke ks = SwingActionManager.keyStrokeFromAccelerator(acc);
        assertTrue("No ALT modifier",
                (ks.getModifiers() & InputEvent.ALT_DOWN_MASK) != 0);
        assertTrue("No ALTGR modifier",
                (ks.getModifiers() & InputEvent.ALT_GRAPH_DOWN_MASK) != 0);
        assertTrue("No CONTROL modifier",
                (ks.getModifiers() & InputEvent.CTRL_DOWN_MASK) != 0);
        assertTrue("No META modifier",
                (ks.getModifiers() & InputEvent.META_DOWN_MASK) != 0);
        assertTrue("No SHIFT modifier",
                (ks.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0);
    }

    /**
     * Creates an action data object with typical values.
     *
     * @return the initialized action data object
     */
    protected ActionDataImpl setUpActionData()
    {
        actionTask = EasyMock.createMock(Runnable.class);
        ActionDataImpl data = new ActionDataImpl();
        data.setName(ACTION_NAME);
        data.setText(ACTION_TEXT);
        data.setToolTip(ACTION_TOOLTIP);
        data.setIcon(ACTION_ICON);
        data.setMnemonicKey(ACTION_MNEMO.intValue());
        data.setAccelerator(ACTION_ACC);
        data.setTask(actionTask);
        return data;
    }

    /**
     * Creates a Swing action object with typical values.
     *
     * @return the initialized action object
     */
    protected SwingFormAction setUpAction() throws FormActionException
    {
        return (SwingFormAction) actionManager.createAction(actionBuilder,
                setUpActionData());
    }

    /**
     * Helper method for creating a menu item based on an action.
     *
     * @param withIcon a flag if menu items should have icons
     * @param checked a flag if a checked item should be created
     * @throws FormActionException if an error occurs
     */
    private void checkCreateActionMenuItem(boolean withIcon, boolean checked)
            throws FormActionException
    {
        FormAction action = setUpAction();
        JMenu menu = new JMenu();
        actionBuilder.setMenuIcon(withIcon);
        assertEquals("Icon flag not correctly set!", withIcon, actionBuilder
                .isMenuIcon());
        checkMenuItem(actionManager.createMenuItem(actionBuilder, action,
                checked, menu), checked, menu, true);
    }

    /**
     * Helper method for testing to create a menu item based on an action data
     * object.
     *
     * @param checked the checked flag
     * @throws FormActionException if an error occurs
     */
    private void checkCreateActionDataMenuItem(boolean checked)
            throws FormActionException
    {
        JMenu menu = new JMenu();
        SwingButtonHandler handler = (SwingButtonHandler) actionManager
                .createMenuItem(actionBuilder, setUpActionData(), checked, menu);
        checkMenuItem(handler.getComponent(), checked, menu, false);
    }

    /**
     * Helper method for checking an action control.
     *
     * @param ctrl the control to check
     * @param actionMenuItem a flag if this is a menu item created from an
     * action
     * @param actionButton a flag if this is a toolbar button created from an
     * action
     */
    private void checkActionControl(AbstractButton ctrl,
            boolean actionMenuItem, boolean actionButton)
    {
        if (!actionButton || actionBuilder.isToolbarText())
        {
            assertEquals("Incorrect text", ACTION_TEXT, ctrl.getText());
        }
        else
        {
            assertNull("Text was set though toolbar text property is false!",
                    ctrl.getText());
        }
        assertEquals("Incorrect tooltip", ACTION_TOOLTIP, ctrl.getToolTipText());
        assertEquals("Incorrect mnemonic", ACTION_MNEMO.intValue(), ctrl
                .getMnemonic());
        if (!actionMenuItem || actionBuilder.isMenuIcon())
        {
            assertEquals("Incorrect icon", ACTION_ICON, ctrl.getIcon());
        }
        else
        {
            assertNull("Icon was set though menu icon property is false!", ctrl
                    .getIcon());
        }
    }

    /**
     * Helper method for checking if a menu item was correctly created and added
     * to the parent menu.
     *
     * @param item the item
     * @param checked the checked flag
     * @param menu the parent menu
     * @param fromAction a flag if this item was created from an action
     */
    private void checkMenuItem(Object item, boolean checked, JMenu menu,
            boolean fromAction)
    {
        assertEquals("Checked flag not evaluated! Item is of class "
                + item.getClass().getName(), checked,
                item instanceof JCheckBoxMenuItem);
        JMenuItem menItem = (JMenuItem) item;
        checkActionControl(menItem, fromAction, false);
        assertEquals("Wrong accelerator", ACTION_KEYSTROKE, menItem
                .getAccelerator());
        assertEquals("Item was not added to menu!", 1, menu.getItemCount());
    }

    /**
     * Helper method for creating a menu.
     *
     * @param data the data of the menu
     * @param parent the parent component
     * @return the new menu
     * @throws FormActionException if an error occurs
     */
    private JMenu createMenu(TextIconData data, Object parent)
            throws FormActionException
    {
        // createMenu must be called twice to correctly process all
        // initialization data available at different stages
        JMenu menu = (JMenu) actionManager.createMenu(actionBuilder, null,
                data, parent);
        return (JMenu) actionManager.createMenu(actionBuilder, menu, data,
                parent);
    }

    /**
     * Helper method for creating a toolbar button based on an action.
     *
     * @param withText a flag if tool buttons should have texts
     * @param checked a flag if a checked item should be created
     * @throws FormActionException if an error occurs
     */
    private void checkCreateActionButton(boolean withText, boolean checked)
            throws FormActionException
    {
        FormAction action = setUpAction();
        JToolBar bar = new JToolBar();
        actionBuilder.setToolbarText(withText);
        assertEquals("Text flag not correctly set!", withText, actionBuilder
                .isToolbarText());
        checkToolButton(actionManager.createToolbarButton(actionBuilder,
                action, checked, bar), checked, bar, true);
    }

    /**
     * Helper method for testing to create a toolbar button based on an action
     * data object.
     *
     * @param checked the checked flag
     * @throws FormActionException if an error occurs
     */
    private void checkCreateActionDataButton(boolean checked)
            throws FormActionException
    {
        JToolBar bar = new JToolBar();
        SwingButtonHandler handler = (SwingButtonHandler) actionManager
                .createToolbarButton(actionBuilder, setUpActionData(), checked,
                        bar);
        checkToolButton(handler.getComponent(), checked, bar, false);
    }

    /**
     * Helper method for checking if a button was correctly created and added to
     * the toolbar.
     *
     * @param button the button
     * @param checked the checked flag
     * @param bar the parent toolbar
     * @param fromAction a flag if this button was created from an action
     */
    private void checkToolButton(Object button, boolean checked, JToolBar bar,
            boolean fromAction)
    {
        assertEquals("Checked flag not evaluated! Item is of class "
                + button.getClass().getName(), checked,
                button instanceof JToggleButton);
        checkActionControl((AbstractButton) button, false, fromAction);
        assertEquals("Button was not added to toolbar!", 1, bar
                .getComponentCount());
    }

    /**
     * Creates an initialized action tag.
     *
     * @return the tag
     */
    private ActionTag setUpActionTag()
    {
        ActionTag tag = new ActionTag();
        try
        {
            JellyContext context = new JellyContext();
            ComponentBuilderData data = new ComponentBuilderData();
            data.put(context);
            TransformerContext tctx = setUpTransformerContext();
            BindingStrategy strat =
                    EasyMock.createNiceMock(BindingStrategy.class);
            EasyMock.replay(strat);
            data.initializeForm(tctx, strat);
            tag.setContext(context);
        }
        catch (JellyTagException e)
        {
            fail("Error when initializing action tag!");
        }
        return tag;
    }

    /**
     * Creates a mock for the transformer context. This method also mocks the
     * resource manager.
     *
     * @return the mock transformer context
     */
    public TransformerContext setUpTransformerContext()
    {
        TransformerContext tctx =
                EasyMock.createNiceMock(TransformerContext.class);
        ResourceManager rm = EasyMock.createMock(ResourceManager.class);
        final Locale testLocale = Locale.ENGLISH;
        EasyMock.expect(tctx.getLocale()).andReturn(testLocale).anyTimes();
        EasyMock.expect(
                rm.getText(EasyMock.eq(testLocale), EasyMock.anyObject(),
                        EasyMock.anyObject())).andAnswer(new IAnswer<String>()
        {
            public String answer() throws Throwable
            {
                return "RES_" + EasyMock.getCurrentArguments()[1] + "_"
                        + EasyMock.getCurrentArguments()[2];
            }
        }).anyTimes();
        EasyMock.expect(tctx.getResourceManager()).andReturn(rm).anyTimes();
        EasyMock.replay(rm, tctx);
        return tctx;
    }

    static
    {
        ACTION_ICON = new ImageIcon();
        ACTION_ICON.setDescription("A test icon");
    }
}

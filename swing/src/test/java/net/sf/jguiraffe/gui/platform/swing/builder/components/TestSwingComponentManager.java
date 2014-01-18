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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.font.TextAttribute;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeSelectionModel;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.DefaultFieldHandlerFactory;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;
import net.sf.jguiraffe.gui.builder.components.Orientation;
import net.sf.jguiraffe.gui.builder.components.WidgetHandler;
import net.sf.jguiraffe.gui.builder.components.model.ListModel;
import net.sf.jguiraffe.gui.builder.components.model.ProgressBarHandler;
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment;
import net.sf.jguiraffe.gui.builder.components.tags.ButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.CheckboxTag;
import net.sf.jguiraffe.gui.builder.components.tags.ComboBoxTag;
import net.sf.jguiraffe.gui.builder.components.tags.DesktopPanelTag;
import net.sf.jguiraffe.gui.builder.components.tags.FontTag;
import net.sf.jguiraffe.gui.builder.components.tags.FormBaseTag;
import net.sf.jguiraffe.gui.builder.components.tags.LabelTag;
import net.sf.jguiraffe.gui.builder.components.tags.ListBoxTag;
import net.sf.jguiraffe.gui.builder.components.tags.PanelTag;
import net.sf.jguiraffe.gui.builder.components.tags.PasswordFieldTag;
import net.sf.jguiraffe.gui.builder.components.tags.ProgressBarTag;
import net.sf.jguiraffe.gui.builder.components.tags.PushButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.RadioButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.SliderTag;
import net.sf.jguiraffe.gui.builder.components.tags.SplitterTag;
import net.sf.jguiraffe.gui.builder.components.tags.StaticTextTag;
import net.sf.jguiraffe.gui.builder.components.tags.TabbedPaneTag;
import net.sf.jguiraffe.gui.builder.components.tags.TextAreaTag;
import net.sf.jguiraffe.gui.builder.components.tags.TextFieldTag;
import net.sf.jguiraffe.gui.builder.components.tags.ToggleButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.TreeIconHandler;
import net.sf.jguiraffe.gui.builder.components.tags.TreeTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableEditorValidationHandler;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableSelectionHandler;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;
import net.sf.jguiraffe.gui.layout.Unit;
import net.sf.jguiraffe.gui.platform.swing.builder.components.table.AbstractTableModelTest;
import net.sf.jguiraffe.gui.platform.swing.builder.components.table.SwingTableColumnWidthListener;
import net.sf.jguiraffe.gui.platform.swing.builder.components.table.SwingTableModel;
import net.sf.jguiraffe.gui.platform.swing.builder.event.ChangeListener;
import net.sf.jguiraffe.gui.platform.swing.builder.event.SwingEventManager;
import net.sf.jguiraffe.gui.platform.swing.layout.SwingSizeHandler;
import net.sf.jguiraffe.locators.ClassPathLocator;
import net.sf.jguiraffe.locators.Locator;
import net.sf.jguiraffe.transform.TransformerContext;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.impl.TagScript;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingComponentManager.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingComponentManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingComponentManager
{
    /** An array with the existing Text Icon Alignment constants. */
    private TextIconAlignment[] ALIGNMENTS =
    { TextIconAlignment.LEFT, TextIconAlignment.RIGHT,
            TextIconAlignment.CENTER };

    /** An array with the swing alignment constants. */
    private int[] SWING_ALIGNMENTS =
    { SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.CENTER };

    /** Constant for a test icon resource.*/
    private static final String ICON_RES = "icon.gif";

    /** The instance to be tested. */
    private SwingComponentManager manager;

    /** A dummy jelly context if needed by some tags.*/
    private JellyContext context;

    @Before
    public void setUp() throws Exception
    {
        manager = new SwingComponentManager();
        context = new JellyContext();
    }

    /**
     * Helper method for initializing a Jelly tag. The tag is assigned a
     * context, and a {@link ComponentBuilderData} instance is created and
     * stored in the context. A tag initialized by this method can be executed.
     *
     * @param tag the tag to be initialized
     * @throws JellyTagException if an error occurs
     */
    private void initTag(FormBaseTag tag) throws JellyTagException
    {
        TransformerContext tctx = EasyMock.createNiceMock(TransformerContext.class);
        EasyMock.replay(tctx);
        tag.setContext(context);
        ComponentBuilderData builderData = new ComponentBuilderData();
        builderData.put(context);
        builderData.setRootContainer(new JPanel());
        builderData.setComponentManager(manager);
        builderData.initializeForm(tctx, new BeanBindingStrategy());
        builderData.setFieldHandlerFactory(new DefaultFieldHandlerFactory());
        tag.setBody(new TagScript());
    }

    /**
     * Tests transforming the <code>TextIconAlignment</code> constants into
     * corresponding Swing constants.
     */
    @Test
    public void testTransformAlignToSwing()
    {
        for (int i = 0; i < ALIGNMENTS.length; i++)
        {
            assertEquals("Wrong Swing alignment", SWING_ALIGNMENTS[i],
                    SwingComponentManager.transformAlign(ALIGNMENTS[i]));
        }
    }

    /**
     * Tests transforming Swing alignments to independent alignments.
     */
    @Test
    public void testTransformSwingAlign()
    {
        for (int i = 0; i < SWING_ALIGNMENTS.length; i++)
        {
            assertEquals("Wrong alignment", ALIGNMENTS[i],
                    SwingComponentManager
                            .transformSwingAlign(SWING_ALIGNMENTS[i]));
        }
    }

    /**
     * Tests transforming an invalid alignment. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTransformSwingAlignInvalid()
    {
        SwingComponentManager.transformSwingAlign(-12345);
    }

    /**
     * Tests whether a size handler can be obtained.
     */
    @Test
    public void testFetchSizeHandler()
    {
        FormBaseTag tag = createTreeTag(false);
        assertNotNull("No size handler", manager.fetchSizeHandler(tag));
    }

    /**
     * Tests whether the size handler instance is cached and reused.
     */
    @Test
    public void testFetchSizeHandlerCached()
    {
        FormBaseTag tag = createTreeTag(false);
        SwingSizeHandler handler = manager.fetchSizeHandler(tag);
        assertSame("Multiple instances", handler, manager.fetchSizeHandler(tag));
    }

    /**
     * Tests adding components to a container and using a layout.
     */
    @Test
    public void testAddContainerComponent()
    {
        JPanel pnlContainer = new JPanel();
        BorderLayout layout = new BorderLayout();
        manager.setContainerLayout(pnlContainer, layout);
        assertSame(layout, pnlContainer.getLayout());

        manager.addContainerComponent(pnlContainer, new JTextArea(),
                BorderLayout.CENTER);
        JPanel pnlButtons = new JPanel();
        manager.addContainerComponent(pnlContainer, pnlButtons,
                BorderLayout.SOUTH);
        manager.addContainerComponent(pnlButtons, new JButton("OK"), null);
        manager.addContainerComponent(pnlButtons, new JButton("Cancel"), null);
        assertEquals(2, pnlContainer.getComponentCount());
        assertEquals(2, pnlButtons.getComponentCount());
        assertTrue(pnlContainer.getComponent(0) instanceof JTextArea);
        assertTrue(pnlContainer.getComponent(1) instanceof JPanel);
    }

    /**
     * Tests creating the platform specific event manager.
     */
    @Test
    public void testCreateEventManager()
    {
        assertTrue(manager.createEventManager() instanceof SwingEventManager);
    }

    /**
     * Tests creating an icon.
     */
    @Test
    public void testCreateIcon() throws FormBuilderException
    {
        URL iconURL = getClass().getResource("/" + ICON_RES);
        assertNotNull("Cannot resolve icon URL", iconURL);
        Locator locator = EasyMock.createMock(Locator.class);
        EasyMock.expect(locator.getURL()).andReturn(iconURL);
        EasyMock.replay(locator);
        assertTrue("Wrong icon created",
                manager.createIcon(locator) instanceof ImageIcon);
        EasyMock.verify(locator);
    }

    /**
     * Tests creating an icon when the locator is undefined.
     */
    @Test(expected = FormBuilderException.class)
    public void testCreateIconUndefined() throws FormBuilderException
    {
        manager.createIcon(null);
    }

    /**
     * Tests creating a label.
     */
    @Test
    public void testCreateLabel() throws Exception
    {
        LabelTag tag = new LabelTag();
        tag.setAlignment("center");
        tag.setText("text");
        tag.setMnemonic("t");
        tag.setIcon(createIcon());
        assertNull(manager.createLabel(tag, true));

        JLabel lab = (JLabel) manager.createLabel(tag, false);
        assertNotNull(lab);
        assertEquals("text", lab.getText());
        assertEquals("Wrong mnemonic", 'T', lab.getDisplayedMnemonic());
        assertEquals(SwingConstants.CENTER, lab.getHorizontalAlignment());
        assertNotNull(lab.getIcon());
    }

    /**
     * Tests linking a label with a component. No label text is specified.
     */
    @Test
    public void testLinkLabelNoText() throws FormBuilderException
    {
        JLabel label = new JLabel();
        label.setText("labelText");
        JTextField text = new JTextField();
        manager.linkLabel(label, text, null);
        assertSame("Not linked to component", text, label.getLabelFor());
        assertEquals("Label text was changed", "labelText", label.getText());
    }

    /**
     * Tests linking a label with a component and setting its text.
     */
    @Test
    public void testLinkLabelWithText() throws FormBuilderException
    {
        JLabel label = new JLabel();
        label.setText("oldText");
        JTextField text = new JTextField();
        manager.linkLabel(label, text, "newText");
        assertSame("Not linked to component", text, label.getLabelFor());
        assertEquals("Label text was not changed", "newText", label.getText());
    }

    /**
     * Tests creating a panel.
     */
    @Test
    public void testCreatePanel() throws Exception
    {
        PanelTag tag = new PanelTag();
        assertNull(manager.createPanel(tag, true));
        JPanel panel = (JPanel) manager.createPanel(tag, false);
        assertNotNull(panel);
        assertNull(panel.getBorder());

        tag.setBorder(true);
        panel = (JPanel) manager.createPanel(tag, false);
        assertNotNull(panel.getBorder());

        Font font = new Font("Monospaced", Font.ITALIC, 12);
        tag.setTitleFont(font);
        context.setVariable("fontref", font);
        tag.setContext(context);
        tag.setTextFontRef("fontref");
        tag.setText("Panel");
        panel = (JPanel) manager.createPanel(tag, false);
        assertNotNull(panel);
        assertNotNull(panel.getBorder());
        assertTrue(panel.getBorder() instanceof TitledBorder);
        TitledBorder border = (TitledBorder) panel.getBorder();
        assertEquals("Panel", border.getTitle());
        assertEquals(font.getName(), border.getTitleFont().getName());
        assertEquals(font.getSize(), border.getTitleFont().getSize());

        tag.setBorderref("unexisting border ref");
        try
        {
            manager.createPanel(tag, false);
            fail("Could resolve invalid border ref!");
        }
        catch (FormBuilderException fex)
        {
            // ok
        }
    }

    /**
     * Tests creating a desktop panel.
     */
    @Test
    public void testCreateDesktopPanel() throws FormBuilderException
    {
        DesktopPanelTag tag = new DesktopPanelTag();
        assertNull("Create flag not evaluated", manager.createDesktopPanel(tag,
                true));

        tag.setDragMode(DesktopPanelTag.DragMode.LIVE);
        JDesktopPane pane = (JDesktopPane) manager.createDesktopPanel(tag,
                false);
        assertEquals("Wrong drag mode", JDesktopPane.LIVE_DRAG_MODE, pane
                .getDragMode());

        tag.setDragMode(DesktopPanelTag.DragMode.OUTLINE);
        Font font = new Font("Monospaced", Font.ITALIC, 12);
        tag.setFont(font);
        pane = (JDesktopPane) manager.createDesktopPanel(tag, false);
        assertEquals("Wrong drag mode", JDesktopPane.OUTLINE_DRAG_MODE, pane
                .getDragMode());
        assertEquals("Wrong font", font.getName(), pane.getFont().getName());
    }

    /**
     * Tests creating a radio group.
     */
    @Test
    public void testCreateRadioGroup() throws FormBuilderException
    {
        Map<String, Object> radios = new LinkedHashMap<String, Object>();
        radios.put("radio1", new JRadioButton("Radio 1"));
        radios.put("radio2", new JRadioButton("Radio 2"));
        radios.put("radio3", new JRadioButton("Radio 3"));
        radios.put("radio4", new JRadioButton("Radio 4"));

        ButtonGroup group = manager.createRadioGroup(radios);
        assertNotNull("No group returned", group);
        assertEquals("Wrong number of buttons in group", radios.size(), group
                .getButtonCount());
        Enumeration<?> en = group.getElements();
        Iterator<?> it = radios.values().iterator();
        while (en.hasMoreElements())
        {
            assertEquals("Wrong button in group", it.next(), en.nextElement());
        }
    }

    /**
     * Tries to create an empty radio group. This should cause an exception.
     */
    @Test(expected = FormBuilderException.class)
    public void testCreateRadioGroupEmpty() throws FormBuilderException
    {
        Map<String, Object> radios = new HashMap<String, Object>();
        manager.createRadioGroup(radios);
    }

    /**
     * Tests creating a toggle button.
     */
    @Test
    public void testCreateToggleButton() throws FormBuilderException
    {
        ToggleButtonTag tag = new ToggleButtonTag();
        initButtonTag(tag);
        assertNull(manager.createToggleButton(tag, true));
        checkButton(manager.createToggleButton(tag, false), JToggleButton.class);
    }

    /**
     * Tests creating a command button.
     */
    @Test
    public void testCreateButton() throws FormBuilderException
    {
        ButtonTag tag = new ButtonTag();
        initButtonTag(tag);
        assertNull(manager.createButton(tag, true));
        checkButton(manager.createButton(tag, false), JButton.class);
    }

    /**
     * Tests creating a checkbox.
     */
    @Test
    public void testCreateCheckbox() throws FormBuilderException
    {
        CheckboxTag tag = new CheckboxTag();
        initButtonTag(tag);
        assertNull(manager.createCheckbox(tag, true));
        checkButton(manager.createCheckbox(tag, false), JCheckBox.class);
    }

    /**
     * Tests creating a radio button.
     */
    @Test
    public void testCreateRadioButton() throws FormBuilderException
    {
        RadioButtonTag tag = new RadioButtonTag();
        initButtonTag(tag);
        assertNull(manager.createRadioButton(tag, true));
        checkButton(manager.createRadioButton(tag, false), JRadioButton.class);
    }

    /**
     * Helper method for initializing a button tag for future tests.
     *
     * @param tag the tag to initialize
     */
    private void initButtonTag(PushButtonTag tag) throws FormBuilderException
    {
        tag.setText("Button");
        tag.setMnemonic("b");
        tag.setAlignment("Right");
        tag.setIcon(createIcon());
    }

    /**
     * Helper method for testing a button object created by the Swing manager.
     *
     * @param handler the component handler for the button
     * @param clazz the expected component class
     */
    private void checkButton(ComponentHandler<?> handler, Class<?> clazz)
    {
        assertNotNull("Handler is null", handler);
        assertTrue("Wrong handler class", handler instanceof SwingButtonHandler);
        SwingButtonHandler bh = (SwingButtonHandler) handler;
        AbstractButton button = bh.getButton();
        assertNotNull("Wrapped button is null", button);
        assertEquals("Wrong button class", clazz, button.getClass());
        assertEquals("Wrong button text", "Button", button.getText());
        assertEquals("Wrong mnemonic", 'B', button.getMnemonic());
        assertEquals("Wrong alignment", SwingConstants.RIGHT, button
                .getHorizontalAlignment());
        assertNotNull("No icon set", button.getIcon());
    }

    /**
     * Tests creating a text field.
     */
    @Test
    public void testCreateTextField() throws Exception
    {
        TextFieldTag tag = new TextFieldTag();
        tag.setColumns(10);
        assertNull("Got a handler", manager.createTextField(tag, true));
        ComponentHandler<?> handler = manager.createTextField(tag, false);
        checkText(handler, JTextField.class, 0);
        tag.setMaxlength(10);
        handler = manager.createTextField(tag, false);
        checkText(handler, JTextField.class, tag.getMaxlength());
        JTextField text = (JTextField) handler.getComponent();
        assertEquals("Wrong number of columns", 10, text.getColumns());
        assertEquals("Wrong outer component", text, handler.getOuterComponent());
        assertEquals("Wrong data type", String.class, handler.getType());
    }

    /**
     * Tests whether a password field can be created.
     */
    @Test
    public void testCreatePasswordField() throws FormBuilderException,
            BadLocationException
    {
        PasswordFieldTag tag = new PasswordFieldTag();
        tag.setColumns(10);
        assertNull("Got a handler", manager.createPasswordField(tag, true));
        ComponentHandler<String> handler = manager.createPasswordField(tag,
                false);
        checkText(handler, JPasswordField.class, 0);
        tag.setMaxlength(12);
        handler = manager.createPasswordField(tag, false);
        checkText(handler, JPasswordField.class, tag.getMaxlength());
        JPasswordField text = (JPasswordField) handler.getComponent();
        assertEquals("Wrong number of columns", 10, text.getColumns());
        assertEquals("Wrong outer component", text, handler.getOuterComponent());
        assertEquals("Wrong data type", String.class, handler.getType());
    }

    /**
     * Tests creating a text area.
     */
    @Test
    public void testCreateTextArea() throws Exception
    {
        TextAreaTag tag = new TextAreaTag()
        {
            @Override
            public NumberWithUnit getPreferredScrollHeight()
            {
                return NumberWithUnit.ZERO;
            }

            @Override
            public NumberWithUnit getPreferredScrollWidth()
            {
                return NumberWithUnit.ZERO;
            }
        };
        initTag(tag);
        tag.setColumns(40);
        tag.setRows(8);
        tag.setWrap(true);
        assertNull("Got a handler", manager.createTextArea(tag, true));
        ComponentHandler<?> handler = manager.createTextArea(tag, false);
        checkText(handler, JTextArea.class, 0);
        tag.setMaxlength(1024);
        handler = manager.createTextArea(tag, false);
        checkText(handler, JTextArea.class, tag.getMaxlength());
        JTextArea text = (JTextArea) handler.getComponent();
        assertEquals("Wrong columns", 40, text.getColumns());
        assertEquals("Wrong rows", 8, text.getRows());
        assertTrue("Wrong line wrap", text.getLineWrap());
        assertTrue("Wrong wrap style", text.getWrapStyleWord());
        assertTrue("Wrong outer compoment",
                handler.getOuterComponent() instanceof JScrollPane);
    }

    /**
     * Tests whether the preferred scroll size is taken into account when a text
     * area is created.
     */
    @Test
    public void testCreateTextAreaPreferredScrollSize()
            throws JellyTagException, FormBuilderException
    {
        final NumberWithUnit scrollWidth = new NumberWithUnit(320);
        final NumberWithUnit scrollHeight = new NumberWithUnit(100);
        TextAreaTag tag = new TextAreaTag()
        {
            @Override
            public NumberWithUnit getPreferredScrollHeight()
            {
                return scrollHeight;
            }

            @Override
            public NumberWithUnit getPreferredScrollWidth()
            {
                return scrollWidth;
            }
        };
        initTag(tag);
        ComponentHandler<?> handler = manager.createTextArea(tag, false);
        Dimension psize = ((JScrollPane) handler.getOuterComponent())
                .getPreferredSize();
        assertEquals("Wrong preferred width", (int) scrollWidth.getValue(),
                psize.width);
        assertEquals("Wrong preferred height", (int) scrollHeight.getValue(),
                psize.height);
    }

    /**
     * Helper method for testing a text component handler.
     *
     * @param handler the handler to test
     * @param clazz the expected component class
     * @param maxlen the maximum text length
     * @throws BadLocationException if an error occurs
     */
    private void checkText(ComponentHandler<?> handler, Class<?> clazz,
            int maxlen) throws BadLocationException
    {
        assertTrue("Wrong handler class", handler instanceof SwingTextHandler);
        SwingTextHandler th = (SwingTextHandler) handler;

        JTextComponent text = th.getTextComponent();
        assertEquals("Wrong class of wrapped component", clazz, text.getClass());
        if (maxlen > 0)
        {
            assertTrue("Maxlen not taken into account",
                    text.getDocument() instanceof SwingLimitedTextModel);
            for (int i = 0; i < maxlen + 5; i++)
            {
                text.getDocument().insertString(0, "x", null);
            }
            assertEquals("Too many characters", maxlen, text.getDocument()
                    .getLength());
        }
        else
        {
            assertFalse("Wrong text model",
                    text.getDocument() instanceof SwingLimitedTextModel);
        }
    }

    /**
     * Helper method for testing the creation of a combo box.
     *
     * @param editable a flag whether the combo should be editable
     * @throws FormBuilderException if an error occurs
     */
    private void checkCreateComboBox(boolean editable)
            throws FormBuilderException
    {
        ComboBoxTag tag = new ComboBoxTag();
        tag.setListModel(new ListModelImpl(10));
        tag.setEditable(editable);
        ComponentHandler<?> ch = manager.createComboBox(tag, false);
        assertTrue("Wrong handler class", ch instanceof SwingComboBoxHandler);
        assertEquals("Wrong editable flag", editable,
                ((JComboBox) ch.getComponent()).isEditable());
    }

    /**
     * Tests the creation of a combo box if the create flag is set.
     */
    @Test
    public void testCreateComboBoxCreate() throws FormBuilderException
    {
        ComboBoxTag tag = new ComboBoxTag();
        tag.setListModel(new ListModelImpl(10));
        assertNull("Non-null result for create == true",
                manager.createComboBox(tag, true));
    }

    /**
     * Tests creating a non editable combo boxes.
     */
    @Test
    public void testCreateComboBoxNotEditable() throws FormBuilderException
    {
        checkCreateComboBox(false);
    }

    /**
     * Tests whether an editable combo box can be created.
     */
    @Test
    public void testCreateComboBoxEditable() throws FormBuilderException
    {
        checkCreateComboBox(true);
    }

    /**
     * Tests createListBox() if the create flag is set.
     */
    @Test
    public void testCreateListBoxCreateFlag() throws FormBuilderException
    {
        ListBoxTag tag = new ListBoxTag();
        assertNull("Non null result for create == true", manager.createListBox(
                tag, true));
    }

    /**
     * Helper method for creating and initializing a list box tag. All
     * parameters can be <b>null</b>, then they are not set at the tag.
     *
     * @param multi the multiple selection flag
     * @param scrollWidth the scroll width
     * @param scrollHeight the scroll height
     * @return the list box tag
     */
    private ListBoxTag createListBoxTag(Boolean multi, String scrollWidth,
            String scrollHeight)
    {
        ListBoxTag tag = new ListBoxTag();
        tag.setListModel(new ListModelImpl(10));
        tag.setName("testList");
        try
        {
            initTag(tag);
            if (multi != null)
            {
                tag.setMulti(multi.booleanValue());
            }
            if (scrollWidth != null)
            {
                tag.setScrollWidth(scrollWidth);
            }
            if (scrollHeight != null)
            {
                tag.setScrollHeight(scrollHeight);
            }
            tag.doTag(null);
        }
        catch (JellyTagException jtex)
        {
            fail("Unexpected exception: " + jtex);
        }
        return tag;
    }

    /**
     * Tests whether a list box with single selection can be created.
     */
    @Test
    public void testCreateListBoxSingleSel() throws FormBuilderException
    {
        ListBoxTag tag = createListBoxTag(null, null, null);
        ComponentHandler<?> ch = manager.createListBox(tag, false);
        assertTrue("Wrong list box handler", ch instanceof SwingListBoxHandler);
        SwingListBoxHandler handler = (SwingListBoxHandler) ch;
        JList list = handler.getList();
        assertEquals("Wrong selection mode",
                ListSelectionModel.SINGLE_SELECTION, list.getSelectionMode());
    }

    /**
     * Tests whether a list box with multiple selection can be created.
     */
    @Test
    public void testCreateListBoxMultiSel() throws FormBuilderException
    {
        ListBoxTag tag = createListBoxTag(Boolean.TRUE, null, null);
        ComponentHandler<?> ch = manager.createListBox(tag, false);
        assertTrue("Wrong multi handler class",
                ch instanceof SwingMultiListBoxHandler);
        SwingListBoxHandler handler = (SwingListBoxHandler) ch;
        JList list = handler.getList();
        assertEquals("Wrong multi selection mode",
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, list
                        .getSelectionMode());
    }

    /**
     * Tests whether the scroll size is taken into account when creating a list.
     */
    @Test
    public void testCreateListBoxScrollSize() throws FormBuilderException
    {
        final int scrollWidth = 150;
        final int scrollHeight = 90;
        ListBoxTag tag = createListBoxTag(null, String.valueOf(scrollWidth),
                String.valueOf(scrollHeight));
        SwingListBoxHandler handler = (SwingListBoxHandler) manager
                .createListBox(tag, false);
        Dimension d = ((Component) handler.getOuterComponent())
                .getPreferredSize();
        assertEquals("Wrong scroll width", scrollWidth, d.width);
        assertEquals("Wrong scroll height", scrollHeight, d.height);
    }

    /**
     * Creates a {@code FontTag} that is initialized to return the given map
     * with font attributes.
     *
     * @param attrs the map with attributes
     * @return the font tag
     * @throws JellyTagException if initialization fails
     */
    private FontTag createFontTag(final Map<?, ?> attrs)
            throws JellyTagException
    {
        FontTag tag = new FontTag()
        {
            @Override
            public Map<?, ?> getAttributesMap()
            {
                return attrs;
            }
        };
        initTag(tag);
        return tag;
    }

    /**
     * Helper method for testing the value of an optional font attribute. Either
     * the provided value or <b>null</b> are accepted.
     *
     * @param attrs the map with attributes
     * @param key the key to the attribute in question
     * @param expected the expected value
     * @param msg the error message
     */
    private static void checkOptionalFontAttribute(Map<TextAttribute, ?> attrs,
            TextAttribute key, Object expected, String msg)
    {
        Object value = attrs.get(key);
        if (value != null)
        {
            assertEquals("Wrong " + msg, expected, value);
        }
    }

    /**
     * Tests whether a font can be created that uses only standard attributes.
     */
    @Test
    public void testCreateFontStdAttrs() throws JellyTagException,
            FormBuilderException
    {
        FontTag tag = createFontTag(new HashMap<Object, Object>());
        tag.setName("Monospaced");
        tag.setSize(20);
        tag.setItalic(false);
        tag.setBold(false);
        Font ft = (Font) manager.createFont(tag);
        Map<TextAttribute, ?> attrs = ft.getAttributes();
        assertEquals("Wrong font name", tag.getName(), attrs
                .get(TextAttribute.FAMILY));
        assertEquals("Wrong size", Float.valueOf(tag.getSize()), attrs
                .get(TextAttribute.SIZE));
        checkOptionalFontAttribute(attrs, TextAttribute.WEIGHT,
                TextAttribute.WEIGHT_REGULAR, "weight");
        checkOptionalFontAttribute(attrs, TextAttribute.POSTURE,
                TextAttribute.POSTURE_REGULAR, "posture");
    }

    /**
     * Tests whether the bold attribute is taken into account when creating a
     * font.
     */
    @Test
    public void testCreateFontBold() throws JellyTagException,
            FormBuilderException
    {
        FontTag tag = createFontTag(new HashMap<Object, Object>());
        tag.setBold(true);
        Font ft = (Font) manager.createFont(tag);
        Map<TextAttribute, ?> attrs = ft.getAttributes();
        assertEquals("Wrong weight", TextAttribute.WEIGHT_BOLD, attrs
                .get(TextAttribute.WEIGHT));
    }

    /**
     * Tests whether the italic attribute is taken into account when creating a
     * font.
     */
    @Test
    public void testCreateFontItalic() throws JellyTagException,
            FormBuilderException
    {
        FontTag tag = createFontTag(new HashMap<Object, Object>());
        tag.setItalic(true);
        Font ft = (Font) manager.createFont(tag);
        Map<TextAttribute, ?> attrs = ft.getAttributes();
        assertEquals("Wrong posture", TextAttribute.POSTURE_OBLIQUE, attrs
                .get(TextAttribute.POSTURE));
    }

    /**
     * Tests whether a font with extended attributes can be created.
     */
    @Test
    public void testCreateFontExtAttrs() throws JellyTagException,
            FormBuilderException
    {
        Map<Object, Object> attrs = new HashMap<Object, Object>();
        attrs.put(TextAttribute.UNDERLINE,
                TextAttribute.UNDERLINE_LOW_TWO_PIXEL);
        attrs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        attrs.put("SUPERSCRIPT", TextAttribute.SUPERSCRIPT_SUPER);
        attrs.put(42, "not a valid attribute");
        FontTag tag = createFontTag(attrs);
        Font ft = (Font) manager.createFont(tag);
        Map<TextAttribute, ?> ftAttrs = ft.getAttributes();
        assertEquals("Wrong underline attribute",
                TextAttribute.UNDERLINE_LOW_TWO_PIXEL, ftAttrs
                        .get(TextAttribute.UNDERLINE));
        assertEquals("Wrong strikethrough attribute",
                TextAttribute.STRIKETHROUGH_ON, ftAttrs
                        .get(TextAttribute.STRIKETHROUGH));
        assertEquals("Wrong superscript attribute",
                TextAttribute.SUPERSCRIPT_SUPER, ftAttrs
                        .get(TextAttribute.SUPERSCRIPT));
    }

    /**
     * Tests whether extended font attributes override standard ones.
     */
    @Test
    public void testCreateFontExtAttrsOverride() throws JellyTagException,
            FormBuilderException
    {
        Map<Object, Object> attrs = new HashMap<Object, Object>();
        Float size = 15.5f;
        attrs.put(TextAttribute.SIZE, size);
        FontTag tag = createFontTag(attrs);
        tag.setSize(5);
        Font ft = (Font) manager.createFont(tag);
        Map<TextAttribute, ?> ftAttrs = ft.getAttributes();
        assertEquals("Wrong size", size, ftAttrs.get(TextAttribute.SIZE));
    }

    /**
     * A specialized splitter tag that is easier to test.
     */
    static class MySplitterTag extends SplitterTag
    {
        @Override
        public Object createContainer(ComponentManager manager, boolean create,
                Collection<Object[]> components) throws FormBuilderException,
                JellyTagException
        {
            return super.createContainer(manager, create, components);
        }
    }

    /**
     * Tests creating a splitter component.
     */
    @Test
    public void testCreateSplitter() throws FormBuilderException,
            JellyTagException
    {
        MySplitterTag tag = new MySplitterTag();
        checkSplitter(tag);

        tag.setPos(25);
        checkSplitter(tag);

        tag.setSize(15);
        checkSplitter(tag);

        tag.setOrientation(Orientation.HORIZONTAL.name());
        checkSplitter(tag);
    }

    /**
     * Tests if a splitter has correctly been created.
     *
     * @param tag the splitter tag
     */
    private void checkSplitter(MySplitterTag tag) throws FormBuilderException,
            JellyTagException
    {
        Collection<Object[]> comps = new ArrayList<Object[]>(2);
        Object[] data1 = new Object[2];
        data1[0] = new JPanel();
        comps.add(data1);
        Object[] data2 = new Object[2];
        data2[0] = new JTextArea();
        comps.add(data2);

        assertNull("Create flag not evaluated", tag.createContainer(manager,
                true, comps));

        JSplitPane split = (JSplitPane) tag.createContainer(manager, false,
                comps);
        assertEquals("Wrong resizeWeight", tag.getResizeWeight(), split
                .getResizeWeight(), 0.001f);
        if (tag.getPos() > 0)
        {
            assertEquals("Wrong position", tag.getPos(), split
                    .getDividerLocation());
        }
        if (tag.getSize() > 0)
        {
            assertEquals("Wrong size", tag.getSize(), split.getDividerSize());
        }
        int orient = tag.getSplitterOrientation() == Orientation.HORIZONTAL ? JSplitPane.HORIZONTAL_SPLIT
                : JSplitPane.VERTICAL_SPLIT;
        assertEquals("Wrong orientation", orient, split.getOrientation());
        assertSame("Wrong first component", data1[0], split.getLeftComponent());
        assertSame("Wrong second component", data2[0], split
                .getRightComponent());
    }

    /**
     * Tests creating a tabbed pane component.
     */
    @Test
    public void testCreateTabbedPane() throws FormBuilderException
    {
        Icon icon = createIcon();

        TabbedPaneTag tag = new TabbedPaneTag();
        TabbedPaneTag.TabData tabData = new TabbedPaneTag.TabData();
        tabData.setTitle("Tab1");
        tabData.setToolTip("Tip1");
        tabData.setIcon(icon);
        tabData.setComponent(new JLabel("Label1"));
        tag.addComponent(tabData, null);
        tabData = new TabbedPaneTag.TabData();
        tabData.setTitle("Tab2");
        tabData.setMnemonic('T');
        tabData.setComponent(new JLabel("Label2"));
        tag.addComponent(tabData, null);

        tag.setPlacementValue(TabbedPaneTag.Placement.BOTTOM);
        checkTabbedPane(tag, JTabbedPane.BOTTOM);
        tag.setPlacementValue(TabbedPaneTag.Placement.LEFT);
        checkTabbedPane(tag, JTabbedPane.LEFT);
        tag.setPlacementValue(TabbedPaneTag.Placement.RIGHT);
        checkTabbedPane(tag, JTabbedPane.RIGHT);
        tag.setPlacementValue(TabbedPaneTag.Placement.TOP);
        checkTabbedPane(tag, JTabbedPane.TOP);
    }

    /**
     * Checks the creation of a tabbed pane and the results.
     *
     * @param tag the initialized tag
     * @param placement the expected placement
     * @throws FormBuilderException if an error occurs
     */
    private void checkTabbedPane(TabbedPaneTag tag, int placement)
            throws FormBuilderException
    {
        assertNull("Create flag not evaluated", manager.createTabbedPane(tag,
                true));

        SwingTabbedPaneHandler handler = (SwingTabbedPaneHandler) manager
                .createTabbedPane(tag, false);
        JTabbedPane pane = handler.getTabbedPane();
        assertEquals("Wrong placement", placement, pane.getTabPlacement());
        assertEquals("Wrong number of tabs", 2, pane.getTabCount());
        assertEquals("Wrong title 1", "Tab1", pane.getTitleAt(0));
        assertEquals("Wrong tip 1", "Tip1", pane.getToolTipTextAt(0));
        assertNotNull("No icon for tab 1", pane.getIconAt(0));
        assertEquals("Wrong mnemonic for tab 1", 0, pane.getMnemonicAt(0));
        assertEquals("Wrong component for tab 1", "Label1", ((JLabel) pane
                .getComponentAt(0)).getText());
        assertEquals("Wrong title 2", "Tab2", pane.getTitleAt(1));
        assertNull("Wrong tip 2", pane.getToolTipTextAt(1));
        assertNull("Found an icon for tab 2", pane.getIconAt(1));
        assertEquals("Wrong mnemonic for tab 2", 'T', pane.getMnemonicAt(1));
        assertEquals("Wrong component for tab 2", "Label2", ((JLabel) pane
                .getComponentAt(1)).getText());
    }

    /**
     * Tests creating a static text element.
     */
    @Test
    public void testCreateStaticText() throws FormBuilderException
    {
        StaticTextTag tag = new StaticTextTag();
        tag.setText("MyStaticText");
        tag.setIcon(createIcon());
        SwingComponentHandler<?> handler = (SwingComponentHandler<?>) manager
                .createStaticText(tag, true);
        assertNull("Handler created with create == true", handler);

        handler = (SwingComponentHandler<?>) manager.createStaticText(tag, false);
        assertTrue("Wrong component", handler.getJComponent() instanceof JLabel);
        JLabel label = (JLabel) handler.getJComponent();
        assertEquals("Wrong label text", "MyStaticText", label.getText());
        assertNotNull("No icon is set for label", label.getIcon());
        assertEquals("Wrong alignment for label", SwingConstants.LEFT, label
                .getHorizontalAlignment());
    }

	/**
     * Tests creating progress bar elements.
     */
    @Test
	public void testCreateProgressBar() throws FormBuilderException
	{
		final Integer minimum = new Integer(5);
		final Integer maximum = new Integer(222);
		final String text = "A text";
		ProgressBarTag tag = new ProgressBarTag();
		tag.setMin(minimum);
		tag.setMax(maximum);
		tag.setText(text);
		assertNull("Handler created with create == true", manager
				.createProgressBar(tag, true));

		ProgressBarHandler handler = (ProgressBarHandler) manager
				.createProgressBar(tag, false);
		JProgressBar bar = (JProgressBar) ((SwingComponentHandler<?>) handler)
				.getJComponent();
		assertEquals("Wrong minimum", minimum.intValue(), bar.getMinimum());
		assertEquals("Wrong maximum", maximum.intValue(), bar.getMaximum());
		assertFalse("Text is supported", bar.isStringPainted());

		final Integer value = new Integer(100);
		tag.setValue(value);
		tag.setAllowText(true);
		handler = (ProgressBarHandler) manager.createProgressBar(tag, false);
		bar = (JProgressBar) ((SwingComponentHandler<?>) handler).getJComponent();
		assertTrue("Text not supported", bar.isStringPainted());
		assertEquals("Wrong progress string", text, bar.getString());
		assertEquals("Wrong value", value.intValue(), bar.getValue());
	}

    /**
     * Tests createSlider() if the create flag is set.
     */
    @Test
    public void testCreateSliderCreateFlag() throws FormBuilderException
    {
        assertNull("Got a result", manager.createSlider(new SliderTag(), true));
    }

    /**
     * Tests whether a slider can be created if most properties have default
     * values.
     */
    @Test
    public void testCreateSliderDefaults() throws FormBuilderException
    {
        SliderTag tag = new SliderTag()
        {
            @Override
            public Orientation getSliderOrientation()
            {
                return Orientation.HORIZONTAL;
            }
        };
        final int minimum = -50;
        final int maximum = 100;
        tag.setMin(minimum);
        tag.setMax(maximum);
        SwingSliderHandler handler = (SwingSliderHandler) manager.createSlider(
                tag, false);
        JSlider slider = handler.getSlider();
        assertEquals("Wrong minimum", minimum, slider.getMinimum());
        assertEquals("Wrong maximum", maximum, slider.getMaximum());
        assertFalse("Wrong paint ticks", slider.getPaintTicks());
        assertFalse("Wrong paint labels", slider.getPaintLabels());
        assertEquals("Wrong minor tick spacing", 0, slider
                .getMinorTickSpacing());
        assertEquals("Wrong major tick spacing", 0, slider
                .getMajorTickSpacing());
        assertEquals("Wrong orientation", JSlider.HORIZONTAL, slider
                .getOrientation());
    }

    /**
     * Tests whether all properties of a slider can be specified.
     */
    @Test
    public void testCreateSliderProperties() throws FormBuilderException
    {
        SliderTag tag = new SliderTag()
        {
            @Override
            public Orientation getSliderOrientation()
            {
                return Orientation.VERTICAL;
            }
        };
        tag.setMin(0);
        tag.setMax(500);
        tag.setMajorTicks(50);
        tag.setMinorTicks(10);
        tag.setShowLabels(true);
        tag.setShowTicks(true);
        SwingSliderHandler handler = (SwingSliderHandler) manager.createSlider(
                tag, false);
        JSlider slider = handler.getSlider();
        assertTrue("Wrong paint ticks", slider.getPaintTicks());
        assertTrue("Wrong paint labels", slider.getPaintLabels());
        assertEquals("Wrong minor tick spacing", 10, slider
                .getMinorTickSpacing());
        assertEquals("Wrong major tick spacing", 50, slider
                .getMajorTickSpacing());
        assertEquals("Wrong orientation", JSlider.VERTICAL, slider
                .getOrientation());
    }

    /**
     * Tests creating a table when the create flag is set. Result should be
     * null.
     */
    @Test
    public void testCreateTableCreateFlag() throws FormBuilderException
    {
        assertNull("Table created in create mode", manager.createTable(
                createTableTag(false), true));
    }

    /**
     * Tests creating a basic table.
     */
    @Test
    public void testCreateTable() throws FormBuilderException
    {
        SwingTableComponentHandler handler = checkTableHandler(createTableTag(false));
        assertFalse("No single selection mode", handler.isMultiSelection());
        assertEquals("Wrong auto resize",
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS, handler.getTable()
                        .getAutoResizeMode());
    }

    /**
     * Tests creating a table with some specific properties set.
     */
    @Test
    public void testCreateTableProperties() throws Exception
    {
        TableTag tag = createTableTag(false);
        tag.setMultiSelection(true);
        tag.setSelectionBackground("YELLOW");
        tag.setSelectionForeground("WHITE");
        AbstractTableModelTest.processTableTag(tag);
        SwingTableComponentHandler handler = checkTableHandler(tag);
        assertTrue("No multi selection mode", handler.isMultiSelection());
        assertEquals("Wrong selection background", Color.YELLOW, handler
                .getTable().getSelectionBackground());
        assertEquals("Wrong selection foreground", Color.WHITE, handler
                .getTable().getSelectionForeground());
    }

    /**
     * Tests creating a table when validation and selection handlers are already
     * defined.
     */
    @Test
    public void testCreateTableWithHandlers() throws FormBuilderException
    {
        TableTag tag = createTableTag(false);
        TableEditorValidationHandler valHandler = EasyMock
                .createMock(TableEditorValidationHandler.class);
        TableSelectionHandler selHandler = EasyMock
                .createMock(TableSelectionHandler.class);
        tag.setEditorValidationHandler(valHandler);
        tag.setEditorSelectionHandler(selHandler);
        checkTableHandler(tag);
        assertSame("Wrong validation handler", valHandler, tag
                .getEditorValidationHandler());
        assertSame("Wrong selection handler", selHandler, tag
                .getEditorSelectionHandler());
    }

    /**
     * Tests whether the column widths are taken into account when a table is
     * created.
     */
    @Test
    public void testCreateTableColumnWidth() throws FormBuilderException
    {
        TableTag tag = createTableTag(true);
        ComponentBuilderData data = new ComponentBuilderData();
        JPanel container = new JPanel();
        container.setFont(new Font("SansSerif", 0, 20));
        data.setRootContainer(container);
        data.put(tag.getContext());
        SwingTableComponentHandler handler = checkTableHandler(tag);
        int totalWidth = checkFixedColumnWidths(container, handler, tag);
        assertEquals("Wrong resize mode", JTable.AUTO_RESIZE_OFF, handler
                .getTable().getAutoResizeMode());
        assertEquals("Wrong number of listeners", 0, listenerCount(handler));
        // there is a delta due to sizes of scroll bars and insets
        int delta = Math.abs(handler.getTable()
                .getPreferredScrollableViewportSize().width
                - totalWidth);
        assertTrue("Wrong viewport width! Delta = " + delta, delta < 100);
    }

    /**
     * Tests whether columns with a percent width are handled correctly.
     */
    @Test
    public void testCreateTableColumnPercentWidth() throws FormBuilderException
    {
        TableTag tag = createTableTag(true);
        TableColumnTag colTag = new TableColumnTag();
        colTag.setPercentWidth(60);
        tag.addColumn(colTag);
        colTag = new TableColumnTag();
        colTag.setPercentWidth(40);
        tag.addColumn(colTag);
        ComponentBuilderData data = new ComponentBuilderData();
        JPanel container = new JPanel();
        container.setFont(new Font("SansSerif", 0, 20));
        data.setRootContainer(container);
        data.put(tag.getContext());
        SwingTableComponentHandler handler = checkTableHandler(tag);
        checkFixedColumnWidths(container, handler, tag);
        assertEquals("Wrong resize mode", JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS,
                handler.getTable().getAutoResizeMode());
        assertEquals("Wrong number of listeners", 1, listenerCount(handler));
    }

    /**
     * Tests whether the scroll size of the table can be specified.
     */
    @Test
    public void testCreateTableScrollSize() throws FormBuilderException
    {
        final int scrollWidth = 400;
        final int scrollHeight = 350;
        TableTag tag = createTableTag(false, String.valueOf(scrollWidth),
                String.valueOf(scrollHeight));
        SwingTableComponentHandler handler = checkTableHandler(tag);
        JScrollPane scr = (JScrollPane) handler.getOuterComponent();
        Dimension d = scr.getPreferredSize();
        assertEquals("Wrong preferred width", scrollWidth, d.width);
        assertEquals("Wrong preferred height", scrollHeight, d.height);
    }

    /**
     * Creates an initialized table tag.
     *
     * @param width a flag whether the widths of the columns should be
     *        initialized
     * @param scrollWidth the scroll width specification
     * @param scrollHeight the scroll height specification
     * @return the table tag
     */
    private TableTag createTableTag(boolean width, String scrollWidth,
            String scrollHeight)
    {
        try
        {
            return AbstractTableModelTest.setUpTableTag(null, width,
                    scrollWidth, scrollHeight);
        }
        catch (Exception ex)
        {
            // should not happen
            throw new FormBuilderRuntimeException(ex);
        }
    }

    /**
     * Creates a table tag and initializes it (partly with default values).
     *
     * @param width a flag whether the widths of the columns should be
     *        initialized
     * @return the table tag
     */
    private TableTag createTableTag(boolean width)
    {
        return createTableTag(width, null, null);
    }

    /**
     * Checks a component handler created for a table. We only need to check
     * whether a correct table with a correct model is created. The tests for
     * <code>SwingTableModel</code> ensure that the table will be properly
     * initialized.
     *
     * @param tag the table tag
     * @throws FormBuilderException if an exception occurs
     */
    private SwingTableComponentHandler checkTableHandler(TableTag tag)
            throws FormBuilderException
    {
        ComponentHandler<?> ch = manager.createTable(tag, false);
        assertTrue("No table handler: " + ch,
                ch instanceof SwingTableComponentHandler);
        SwingTableComponentHandler tabHandler = (SwingTableComponentHandler) ch;
        assertNotNull("No table created", tabHandler.getTable());
        assertTrue("Incorrect model: " + tabHandler.getTableModel(), tabHandler
                .getTableModel() instanceof SwingTableModel);
        SwingTableModel model = tabHandler.getTableModel();
        assertSame("Wrong underlying tag", tag, model.getTableTag());
        assertNotNull("No validation handler set", model.getTableTag()
                .getEditorValidationHandler());
        assertNotNull("No selection handler set", model.getTableTag()
                .getEditorSelectionHandler());
        return tabHandler;
    }

    /**
     * Checks whether the columns with a fixed width are correctly initialized.
     *
     * @param container the root container
     * @param handler the table handler
     * @param tag the table tag
     * @return the sum of the widths of all columns
     * @throws FormBuilderException if an error occurs
     */
    private int checkFixedColumnWidths(JPanel container,
            SwingTableComponentHandler handler, TableTag tag)
            throws FormBuilderException
    {
        SwingSizeHandler sizeHandler = new SwingSizeHandler();
        int totalWidth = 0;
        for (int i = 0; i < AbstractTableModelTest.COLUMN_WIDTHS.length; i++)
        {
            TableColumn column = handler.getTable().getColumnModel().getColumn(
                    i);
            int width = new NumberWithUnit(
                    AbstractTableModelTest.COLUMN_WIDTHS[i], Unit.DLU).toPixel(
                    sizeHandler, container, false);
            assertEquals("Wrong preferred width of column " + i, width, column
                    .getPreferredWidth());
            assertEquals("Wrong width in controller for column " + i, width,
                    tag.getColumnWidthController().getFixedWidth(i));
            totalWidth+= width;
        }
        return totalWidth;
    }

    /**
     * Returns the number of specific listeners registered at the table
     * components. This method is used to check whether listeners are correctly
     * registered.
     *
     * @param tabHandler the table component handler
     * @return the number of registered listeners
     */
    private int listenerCount(SwingTableComponentHandler tabHandler)
    {
        int compListeners = 0;
        for (Object l : ((JComponent) tabHandler.getOuterComponent())
                .getComponentListeners())
        {
            if (l instanceof SwingTableColumnWidthListener)
            {
                compListeners++;
            }
        }

        int modelListeners = 0;
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) tabHandler
                .getTable().getColumnModel();
        for (Object l : colModel.getListeners(TableColumnModelListener.class))
        {
            if (l instanceof SwingTableColumnWidthListener)
            {
                modelListeners++;
            }
        }
        assertEquals("Different listener counts", compListeners, modelListeners);
        return compListeners;
    }

    /**
     * Tests obtaining a widget handler for a component.
     */
    @Test
    public void testGetWidgetHandlerFor()
    {
        Component comp = new JLabel();
        WidgetHandler wh = manager.getWidgetHandlerFor(comp);
        assertTrue("Wrong widget handler implementation: " + wh,
                wh instanceof SwingWidgetHandler);
        assertSame("Wrong wrapped widget", comp, wh.getWidget());
    }

    /**
     * Tests the getWidgetHandler() method if the passed in component is a
     * scroll pane.
     */
    @Test
    public void testGetWidgetHandlerForJScrollPane()
    {
        JTextArea comp = new JTextArea();
        JScrollPane pane = new JScrollPane(comp);
        WidgetHandler wh = manager.getWidgetHandlerFor(pane);
        assertTrue("Wrong widget handler implementation: " + wh,
                wh instanceof SwingWidgetHandler);
        assertSame("Wrong wrapped widget", comp, wh.getWidget());
    }

    /**
     * Tests whether a widget handler for a radio button group can be created.
     */
    @Test
    public void testGetWidgetHandlerForButtonGroup()
    {
        ButtonGroup grp = new ButtonGroup();
        grp.add(new JRadioButton());
        SwingRadioGroupWidgetHandler handler = (SwingRadioGroupWidgetHandler) manager
                .getWidgetHandlerFor(grp);
        assertSame("Wrong underlying button group", grp, handler
                .getButtonGroup());
    }

    /**
     * Tries to obtain a widget handler for an empty button group. This should
     * cause an exception.
     */
    @Test(expected = FormBuilderRuntimeException.class)
    public void testGetWidgetHandlerForButtonGroupEmpty()
    {
        manager.getWidgetHandlerFor(new ButtonGroup());
    }

    /**
     * Tests creating a tree when the create flag is true.
     */
    @Test
    public void testCreateTreeCreateFlag() throws FormBuilderException
    {
        assertNull("Tree was created for create = true", manager.createTree(
                createTreeTag(false), true));
    }

    /**
     * Tests creating a tree with default parameters.
     */
    @Test
    public void testCreateTreeDefaults() throws FormBuilderException
    {
        JTree tree = checkTreeHandler(createTreeTag(true));
        assertTrue("Root not visible", tree.isRootVisible());
        assertFalse("Editable", tree.isEditable());
        assertEquals("No single selection",
                TreeSelectionModel.SINGLE_TREE_SELECTION, tree
                        .getSelectionModel().getSelectionMode());
    }

    /**
     * Tests creating a tree with multiple selection enabled.
     */
    @Test
    public void testCreateTreeMultiSelect() throws FormBuilderException
    {
        TreeTag tag = createTreeTag(true);
        tag.setMultiSelection(true);
        JTree tree = checkTreeHandler(tag);
        assertEquals("No multi selection",
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION, tree
                        .getSelectionModel().getSelectionMode());
    }

    /**
     * Tests creating a tree with an invisible root.
     */
    @Test
    public void testCreateTreeRootInvisible() throws FormBuilderException
    {
        TreeTag tag = createTreeTag(true);
        tag.setRootVisible(false);
        JTree tree = checkTreeHandler(tag);
        assertFalse("Root is visible", tree.isRootVisible());
    }

    /**
     * Tests creating a tree that can be edited.
     */
    @Test
    public void testCreateTreeEditable() throws FormBuilderException
    {
        TreeTag tag = createTreeTag(true);
        tag.setEditable(true);
        JTree tree = checkTreeHandler(tag);
        assertTrue("Not editable", tree.isEditable());
    }

    /**
     * Tests whether the scroll size is taken into account when a tree is
     * created.
     */
    @Test
    public void testCreateTreeScrollSize() throws FormBuilderException,
            JellyTagException
    {
        TreeTag tag = createTreeTag(false);
        final int scrollWidth = 320;
        final int scrollHeight = 480;
        tag.setScrollWidth(String.valueOf(scrollWidth));
        tag.setScrollHeight(String.valueOf(scrollHeight));
        tag.doTag(null);
        ComponentHandler<?> handler = tag.getComponentHandler();
        JScrollPane scr = (JScrollPane) handler.getOuterComponent();
        Dimension prefSz = scr.getPreferredSize();
        assertEquals("Wrong preferred width", scrollWidth, prefSz.width);
        assertEquals("Wrong preferred height", scrollHeight, prefSz.height);
    }

    /**
     * Creates a tag describing a tree component.
     *
     * @param execute a flag whether the tag should be executed
     * @return the tree tag
     */
    private TreeTag createTreeTag(boolean execute)
    {
        final TreeIconHandler handler = EasyMock
                .createMock(TreeIconHandler.class);
        EasyMock.expect(
                handler.getIconName((ConfigurationNode) EasyMock.anyObject(),
                        EasyMock.anyBoolean(), EasyMock.anyBoolean()))
                .andStubReturn(ICON_RES);
        EasyMock.replay(handler);
        TreeTag tag = new TreeTag()
        {
            @Override
            public TreeIconHandler getResolvedIconHandler()
            {
                return handler;
            }
        };
        try
        {
            initTag(tag);
            tag.setTreeModel(new HierarchicalConfiguration());
            tag.addIcon(ICON_RES, createIcon());
            tag.setName("tree");
            if (execute)
            {
                tag.doTag(null);
            }
        }
        catch (Exception ex)
        {
            fail("Exception when creating tree tag: " + ex);
        }

        return tag;
    }

    /**
     * Checks basic properties of the tree handler created for the specified
     * tag.
     *
     * @param tag the tree tag
     * @return the tree component
     */
    private JTree checkTreeHandler(TreeTag tag) throws FormBuilderException
    {
        ComponentHandler<Object> ch = manager.createTree(tag, false);
        assertTrue("Wrong type of handler",
                ch instanceof SwingTreeComponentHandler);

        SwingTreeComponentHandler treeHandler = (SwingTreeComponentHandler) ch;
        JTree tree = treeHandler.getTree();
        SwingTreeCellRenderer r = (SwingTreeCellRenderer) tree
                .getCellRenderer();
        assertEquals("Icon handler not set", tag.getResolvedIconHandler(), r
                .getIconHandler());
        assertNotNull("Icons not initialized", r.getTreeIcon(ICON_RES));
        assertNotNull("No tree node formatter", r.getNodeFormatter());
        assertTrue("Wrong cell editor",
                tree.getCellEditor() instanceof SwingTreeCellEditor);

        return tree;
    }

    /**
     * Creates an icon that can be used for testing tags that support icons. The
     * test icon is loaded and returned.
     *
     * @return the test icon
     * @throws FormBuilderException if an error occurs
     */
    private Icon createIcon() throws FormBuilderException
    {
        return (Icon) manager.createIcon(ClassPathLocator.getInstance(ICON_RES));
    }

    /**
     * A test listener that receives the generated events.
     */
    static class TestEventListener implements ActionListener, FocusListener,
            ChangeListener
    {
        public Object event;

        public int called;

        public void actionPerformed(ActionEvent e)
        {
            setEvent(e);
        }

        public void focusGained(FocusEvent e)
        {
            setEvent(e);
        }

        public void focusLost(FocusEvent e)
        {
            setEvent(e);
        }

        public void componentChanged(Object e)
        {
            setEvent(e);
        }

        private synchronized void setEvent(Object e)
        {
            event = e;
            called++;
        }
    }

    /**
     * Test implementation of the ListModel interface.
     */
    static class ListModelImpl implements ListModel
    {
        private int count;

        public ListModelImpl(int cnt)
        {
            count = cnt;
        }

        public int size()
        {
            return count;
        }

        public Object getDisplayObject(int index)
        {
            return "Display" + index;
        }

        public Object getValueObject(int index)
        {
            return "Value" + index;
        }

        public Class<?> getType()
        {
            return String.class;
        }
    }
}

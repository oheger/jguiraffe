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

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
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
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.font.TextAttribute;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;
import net.sf.jguiraffe.gui.builder.components.Orientation;
import net.sf.jguiraffe.gui.builder.components.WidgetHandler;
import net.sf.jguiraffe.gui.builder.components.model.StaticTextData;
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment;
import net.sf.jguiraffe.gui.builder.components.tags.BorderLayoutTag;
import net.sf.jguiraffe.gui.builder.components.tags.ButtonLayoutTag;
import net.sf.jguiraffe.gui.builder.components.tags.ButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.CheckboxTag;
import net.sf.jguiraffe.gui.builder.components.tags.ComboBoxTag;
import net.sf.jguiraffe.gui.builder.components.tags.ComponentBaseTag;
import net.sf.jguiraffe.gui.builder.components.tags.DesktopPanelTag;
import net.sf.jguiraffe.gui.builder.components.tags.FontTag;
import net.sf.jguiraffe.gui.builder.components.tags.FormBaseTag;
import net.sf.jguiraffe.gui.builder.components.tags.InputComponentTag;
import net.sf.jguiraffe.gui.builder.components.tags.LabelTag;
import net.sf.jguiraffe.gui.builder.components.tags.ListBoxTag;
import net.sf.jguiraffe.gui.builder.components.tags.PanelTag;
import net.sf.jguiraffe.gui.builder.components.tags.PasswordFieldTag;
import net.sf.jguiraffe.gui.builder.components.tags.PercentLayoutTag;
import net.sf.jguiraffe.gui.builder.components.tags.ProgressBarTag;
import net.sf.jguiraffe.gui.builder.components.tags.RadioButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.SliderTag;
import net.sf.jguiraffe.gui.builder.components.tags.SplitterTag;
import net.sf.jguiraffe.gui.builder.components.tags.StaticTextTag;
import net.sf.jguiraffe.gui.builder.components.tags.TabbedPaneTag;
import net.sf.jguiraffe.gui.builder.components.tags.TextAreaTag;
import net.sf.jguiraffe.gui.builder.components.tags.TextFieldTag;
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData;
import net.sf.jguiraffe.gui.builder.components.tags.ToggleButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.TreeTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnWidthController;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManager;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.platform.swing.builder.components.table.SwingTableColumnWidthListener;
import net.sf.jguiraffe.gui.platform.swing.builder.components.table.SwingTableModel;
import net.sf.jguiraffe.gui.platform.swing.builder.components.table.SwingTableSelectionHandler;
import net.sf.jguiraffe.gui.platform.swing.builder.event.SwingEventManager;
import net.sf.jguiraffe.gui.platform.swing.layout.SwingPercentLayoutAdapter;
import net.sf.jguiraffe.gui.platform.swing.layout.SwingSizeHandler;
import net.sf.jguiraffe.locators.Locator;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * The Swing specific implementation of the <code>ComponentManager</code>
 * interface.
 * </p>
 * <p>
 * This class implements the methods of the <code>ComponentManager</code>
 * interface in a way that standard Swing components are created.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingComponentManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingComponentManager implements ComponentManager
{
    /** Constant for the Swing form builder name. */
    public static final String BUILDER_NAME = "SWING_FORM_BUILDER";

    /** An array with the generic tabbed pane placement constants. */
    private static final TabbedPaneTag.Placement[] TAB_PLACEMENTS = {
            TabbedPaneTag.Placement.BOTTOM, TabbedPaneTag.Placement.LEFT,
            TabbedPaneTag.Placement.RIGHT, TabbedPaneTag.Placement.TOP
    };

    /** An Array with the Swing tabbed pane placement constants. */
    private static final int[] SWING_TAB_PLACEMENTS = {
            JTabbedPane.BOTTOM, JTabbedPane.LEFT, JTabbedPane.RIGHT,
            JTabbedPane.TOP
    };

    /**
     * The name under which the size handler is stored in the current Jelly
     * context.
     */
    private static final String VAR_SIZE_HANDLER = SwingSizeHandler.class
            .getName();

    /** Stores a reference to a logger instance. */
    private final Log log = LogFactory.getLog(SwingComponentManager.class);

    /** The mapping between string and TextAttribute constants. */
    private final Map<String, TextAttribute> textAttributeMapping;

    /**
     * Creates a new instance of {@code SwingComponentManager}.
     */
    public SwingComponentManager()
    {
        textAttributeMapping = initTextAttributeMapping();
    }

    /**
     * Adds the specified component to a container using the given constraints.
     * This implementation expects the container object to be of type
     * <code>java.awt.Container</code> and the component of type
     * <code>java.awt.Component</code>. The constraints may be undefined.
     *
     * @param container the container
     * @param component the component
     * @param constraints the layout constrains
     */
    public void addContainerComponent(Object container, Object component,
            Object constraints)
    {
        Container c = (Container) container;
        Component comp = (Component) component;
        if (constraints == null)
        {
            c.add(comp);
        }
        else
        {
            c.add(comp, constraints);
        }
    }

    /**
     * Sets the layout manager for the specified container. The passed in
     * container object must be derived from <code>java.awt.Container</code>,
     * the layout manager object must implement the
     * <code>java.awt.LayoutManager</code> interface.
     *
     * @param container the container
     * @param layout the layout manager
     */
    public void setContainerLayout(Object container, Object layout)
    {
        ((Container) container).setLayout((LayoutManager) layout);
    }

    /**
     * Creates the Swing specific platform event manager. This is an instance of
     * the
     * {@link net.sf.jguiraffe.gui.platform.swing.builder.event.SwingEventManager
     * SwingEventManager} class.
     *
     * @return the platform event manager
     */
    public PlatformEventManager createEventManager()
    {
        return new SwingEventManager();
    }

    /**
     * Returns a {@code WidgetHandler} for the specified component. This
     * implementation expects that the passed in component is either derived
     * from {@code javax.swing.JComponent} or is a {@code
     * javax.swing.ButtonGroup}. It will then return a Swing-specific handler
     * implementation, which allows manipulation of this component. Some
     * components created by {@code SwingComponentManager} are wrapped inside a
     * {@code JScrollPane}. If the component passed in happens to be a scroll
     * pane, the {@code WidgetHandler} is therefore created for the viewport
     * component.
     *
     * @param component the affected component
     * @return a {@code WidgetHandler} for this component
     * @throws FormBuilderRuntimeException if the component is a {@code
     *         ButtonGroup} and does not contain any elements
     */
    public WidgetHandler getWidgetHandlerFor(Object component)
    {
        JComponent wrappedComp;

        if (component instanceof JScrollPane)
        {
            wrappedComp = (JComponent) ((JScrollPane) component).getViewport()
                    .getView();
        }

        else if (component instanceof ButtonGroup)
        {
            ButtonGroup group = (ButtonGroup) component;
            if (group.getButtonCount() < 1)
            {
                throw new FormBuilderRuntimeException(
                        "ButtonGroup must not be empty!");
            }
            return new SwingRadioGroupWidgetHandler(group);
        }
        else
        {
            wrappedComp = (JComponent) component;
        }

        return new SwingWidgetHandler(wrappedComp);
    }

    /**
     * Creates a label component with the information obtained from the given
     * tag. This method returns a <code>JLabel</code> object if the
     * <code>create</code> parameter is <b>false</b>.
     *
     * @param tag the label tag
     * @param create the create flag
     * @return the label
     */
    public Object createLabel(LabelTag tag, boolean create)
    {
        if (create)
        {
            return null;
        }
        else
        {
            JLabel label = new JLabel();
            initLabel(label, tag, tag.getTextIconData());
            return label;
        }
    }

    /**
     * Associates a label with another component. The label must be of type
     * <code>javax.swing.JLabel</code>, the component must be of type
     * <code>java.awt.Component</code>.
     *
     * @param label the label
     * @param component the component
     * @param text the text for the label
     * @throws FormBuilderException if an error occurs
     */
    public void linkLabel(Object label, Object component, String text)
            throws FormBuilderException
    {
        JLabel lab = (JLabel) label;
        lab.setLabelFor((Component) component);
        if (text != null)
        {
            lab.setText(text);
        }
    }

    /**
     * Creates an icon with the information obtained from the given locator. An
     * <code>ImageIcon</code> object will be returned.
     *
     * @param locator the <code>Locator</code> pointing to the icon's data
     * @return the icon
     * @throws FormBuilderException if the icon cannot be loaded
     */
    public Object createIcon(Locator locator) throws FormBuilderException
    {
        if (locator == null)
        {
            throw new FormBuilderException("Locator for icon must not be null!");
        }

        if (log.isInfoEnabled())
        {
            log.info("Loading icon from " + locator);
        }
        return new ImageIcon(locator.getURL());
    }

    /**
     * Creates a font based on the data provided by the given {@code FontTag}.
     * This implementation creates a {@code java.awt.Font} object using the
     * constructor that takes a map with attributes. The default font attributes
     * specified by the tag are mapped to corresponding constants of the {@code
     * TextAttribute} class. For the map with extended attributes two kinds of
     * keys are supported:
     * <ul>
     * <li>Objects of type {@code TextAttribute} are used directly.</li>
     * <li>If a key is of type {@code String}, it is checked whether it is the
     * name of a constant in the {@code TextAttribute} class. In this case the
     * key is accepted.</li>
     * </ul>
     * All other objects appearing as keys in the map are ignored.
     *
     * @param tag the {@code FontTag}
     * @return the newly created font
     * @throws FormBuilderException if an error occurs
     */
    public Object createFont(FontTag tag) throws FormBuilderException
    {
        Map<TextAttribute, Object> attrs = new HashMap<TextAttribute, Object>();

        handleStandardFontAttributes(tag, attrs);
        handleExtendedFontAttributes(tag, attrs);

        return new Font(attrs);
    }

    /**
     * Creates a percent layout with the information from the passed in tag. A
     * Swing specific adapter is created for the layout object maintained by the
     * tag.
     *
     * @param tag the percent layout tag
     * @return the new layout object
     * @throws FormBuilderException if an error occurs
     */
    public Object createPercentLayout(PercentLayoutTag tag)
            throws FormBuilderException
    {
        return new SwingPercentLayoutAdapter(tag.getPercentLayout());
    }

    /**
     * Creates a button layout with the information from the passed in tag. A
     * Swing specific adapter is created for the button layout maintained by the
     * tag.
     *
     * @param tag the button layout tag
     * @return the new layout object
     * @throws FormBuilderException if an error occurs
     */
    public Object createButtonLayout(ButtonLayoutTag tag)
            throws FormBuilderException
    {
        return new SwingPercentLayoutAdapter(tag.getButtonLayout());
    }

    /**
     * Creates a border layout with the information from the passed in tag. A
     * Swing specific adapter is created for the border layout maintained by the
     * tag.
     *
     * @param tag the border layout tag
     * @return the new layout object
     * @throws FormBuilderException if an error occurs
     */
    public Object createBorderLayout(BorderLayoutTag tag)
            throws FormBuilderException
    {
        return new SwingPercentLayoutAdapter(tag.getBorderLayout());
    }

    /**
     * Creates a panel with the information obtained from the passed in tag. The
     * returned object is of type <code>javax.swing.JPanel</code>. If a text is
     * defined or the <code>border</code> attribute is <b>true</b>, a border
     * will be added. This implementation also supports border objects that have
     * been placed into the Jelly context and that are referenced by the
     * <code>borderref</code> attribute.
     *
     * @param tag the panel tag
     * @param create the create flag
     * @return the new panel object
     * @throws FormBuilderException if an error occurs
     */
    public Object createPanel(PanelTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        JPanel panel = new JPanel();
        initComponent(panel, tag);

        if (tag.isBorder() || tag.getTextData().isDefined())
        {
            Border border;
            if (StringUtils.isNotEmpty(tag.getBorderref()))
            {
                border = (Border) tag.getContext().getVariable(
                        tag.getBorderref());
                if (border == null)
                {
                    throw new FormBuilderException("Cannot find border "
                            + tag.getBorderref());
                }
            }
            else
            {
                border = createDefaultBorder();
            }

            if (tag.getTextData().isDefined())
            {
                border = BorderFactory.createTitledBorder(border, tag
                        .getTextData().getCaption(),
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        (tag.getTitleFont() != null) ? (Font) tag
                                .getTitleFont() : panel.getFont(), (tag
                                .getColor() != null) ? SwingComponentUtils
                                .logic2SwingColor(tag.getColor()) : panel
                                .getForeground());
            }
            panel.setBorder(border);
        }

        return panel;
    }

    /**
     * Creates a desktop panel with the information obtained from the passed in
     * tag. This implementation will return an instance of the
     * <code>JDesktopPane</code> class.
     *
     * @param tag the desktop panel tag
     * @param create the create flag
     * @return the new desktop panel
     * @throws FormBuilderException if an error occurs
     */
    public Object createDesktopPanel(DesktopPanelTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        else
        {
            JDesktopPane pane = new JDesktopPane();
            initComponent(pane, tag);
            if (DesktopPanelTag.DragMode.OUTLINE == tag.getDragMode())
            {
                pane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
            }
            else
            {
                pane.setDragMode(JDesktopPane.LIVE_DRAG_MODE);
            }
            return pane;
        }
    }

    /**
     * Creates a splitter component from the information contained in the passed
     * in tag. This implementation returns an instance of the
     * <code>JSplitPane</code> class.
     *
     * @param tag the splitter tag
     * @param create the create flag
     * @return the splitter component
     * @throws FormBuilderException if an error occurs
     */
    public Object createSplitter(SplitterTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            JSplitPane split = new JSplitPane(
                    tag.getSplitterOrientation() == Orientation.HORIZONTAL
                    ? JSplitPane.HORIZONTAL_SPLIT
                            : JSplitPane.VERTICAL_SPLIT);
            initComponent(split, tag);
            split.setResizeWeight(tag.getResizeWeight());
            if (tag.getPos() > 0)
            {
                split.setDividerLocation(tag.getPos());
            }
            if (tag.getSize() > 0)
            {
                split.setDividerSize(tag.getSize());
            }
            split.setLeftComponent((Component) tag.getFirstComponent());
            split.setRightComponent((Component) tag.getSecondComponent());

            return split;
        }
    }

    /**
     * Creates a radio group, which contains the specified radio buttons. The
     * passed in elements must be of type {@code javax.swing.AbstractButton}.
     * The button group must not be empty.
     *
     * @param radios a collection with the radio buttons to add
     * @return the radio group
     * @throws FormBuilderException if an error occurs
     */
    public ButtonGroup createRadioGroup(Map<String, Object> radios)
            throws FormBuilderException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Creating radio group for " + radios.size() + " radios.");
        }
        if (radios.isEmpty())
        {
            throw new FormBuilderException("Radio group must not be empty!");
        }

        ButtonGroup group = new ButtonGroup();
        for (Object comp : radios.values())
        {
            group.add((AbstractButton) comp);
        }
        return group;
    }

    /**
     * Creates a component handler for a command button, which is specified by
     * the given tag.
     *
     * @param tag the button tag
     * @param create the create flag
     * @return the handler for the button
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<Boolean> createButton(ButtonTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        return createButtonHandler(new JButton(), tag, tag.getTextIconData(),
                tag.getCommand());
    }

    /**
     * Creates a component handler for a toggle button, which is specified by
     * the given tag.
     *
     * @param tag the toggle button tag
     * @param create the create flag
     * @return the handler for the toggle button
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<Boolean> createToggleButton(ToggleButtonTag tag,
            boolean create) throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        return createButtonHandler(new JToggleButton(), tag, tag
                .getTextIconData(), tag.getCommand());
    }

    /**
     * Creates a component handler for a text field which is specified by the
     * given tag.
     *
     * @param tag the text field tag
     * @param create the create flag
     * @return the new component handler
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<String> createTextField(TextFieldTag tag,
            boolean create) throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        JTextField text = new JTextField();
        return initTextField(tag, text);
    }

    /**
     * Creates a component handler for a text area which is specified by the
     * given tag.
     *
     * @param tag the text area tag
     * @param create the create flag
     * @return the new component handler
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<String> createTextArea(TextAreaTag tag,
            boolean create) throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        JTextArea text = new JTextArea();
        if (tag.getColumns() > 0)
        {
            text.setColumns(tag.getColumns());
        }
        if (tag.getRows() > 0)
        {
            text.setRows(tag.getRows());
        }
        if (tag.isWrap())
        {
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
        }

        initText(text, tag, tag.getMaxlength());
        SwingSizeHandler sizeHandler = fetchSizeHandler(tag);
        Object container = tag.findContainer().getContainer();
        return new SwingTextAreaHandler(text, tag.getPreferredScrollWidth()
                .toPixel(sizeHandler, container, false), tag
                .getPreferredScrollHeight().toPixel(sizeHandler, container,
                        true));
    }

    /**
     * Creates a component handler for a password text field which is specified
     * by the given tag.
     *
     * @param tag the password tag
     * @param create the create flag
     * @return the new component handler
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<String> createPasswordField(PasswordFieldTag tag,
            boolean create) throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        JTextField text = new JPasswordField();
        return initTextField(tag, text);
    }

    /**
     * Creates a component handler for a checkbox, which is specified by the
     * given tag.
     *
     * @param tag the checkbox tag
     * @param create the create flag
     * @return the new component handler
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<Boolean> createCheckbox(CheckboxTag tag,
            boolean create) throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        return createButtonHandler(new JCheckBox(), tag, tag.getTextIconData(),
                null);
    }

    /**
     * Creates a component handler for a radio button, which is specified by the
     * given tag.
     *
     * @param tag the radio button tag
     * @param create the create flag
     * @return the new component handler
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<Boolean> createRadioButton(RadioButtonTag tag,
            boolean create) throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        return createButtonHandler(new JRadioButton(), tag, tag
                .getTextIconData(), null);
    }

    /**
     * Creates a component handler for a combo box, which is specified by the
     * given tag.
     *
     * @param tag the combo box tag
     * @param create the create flag
     * @return the new component handler
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<Object> createComboBox(ComboBoxTag tag,
            boolean create) throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        JComboBox combo = new JComboBox();
        initComponent(combo, tag);
        if (tag.isEditable())
        {
            combo.setEditable(true);
        }
        return new SwingComboBoxHandler(combo, tag.getListModel());
    }

    /**
     * Creates a component handler for a list, which is specified by the given
     * tag.
     *
     * @param tag the list box tag
     * @param create the create flag
     * @return the new component handler
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<Object> createListBox(ListBoxTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        JList list = new JList();
        initComponent(list, tag);

        SwingSizeHandler sizeHandler = fetchSizeHandler(tag);
        Object container = tag.findContainer().getContainer();
        int scrollWidth = tag.getPreferredScrollWidth().toPixel(sizeHandler,
                container, false);
        int scrollHeight = tag.getPreferredScrollHeight().toPixel(sizeHandler,
                container, true);

        if (tag.isMulti())
        {
            list
                    .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            return new SwingMultiListBoxHandler(list, tag.getListModel(),
                    scrollWidth, scrollHeight);
        }
        else
        {
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            return new SwingListBoxHandler(list, tag.getListModel(),
                    scrollWidth, scrollHeight);
        }
    }

    /**
     * Creates a component handler for a tabbed pane, which is specified by the
     * given tag.
     *
     * @param tag the tabbed pane tag
     * @param create the create flag
     * @return the new component handler
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<Integer> createTabbedPane(TabbedPaneTag tag,
            boolean create) throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            JTabbedPane pane = new JTabbedPane();
            initComponent(pane, tag);
            for (int i = 0; i < TAB_PLACEMENTS.length; i++)
            {
                if (TAB_PLACEMENTS[i].equals(tag.getPlacementValue()))
                {
                    pane.setTabPlacement(SWING_TAB_PLACEMENTS[i]);
                    break;
                }
            }

            int index = 0;
            for (TabbedPaneTag.TabData tabData : tag.getTabs())
            {
                pane.addTab(tabData.getTitle(), (Icon) tabData.getIcon(),
                        (Component) tabData.getComponent(), tabData
                                .getToolTip());
                pane.setMnemonicAt(index, tabData.getMnemonic());
                index++;
            }
            return new SwingTabbedPaneHandler(pane);
        }
    }

    /**
     * Creates a component handler for a static text, which is specified by the
     * given tag.
     *
     * @param tag the static text tag
     * @param create the create flag
     * @return the component handler for the new element
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<StaticTextData> createStaticText(StaticTextTag tag,
            boolean create) throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            JLabel label = new JLabel();
            initLabel(label, tag, tag.getTextIconData());
            return new SwingStaticTextComponentHandler(label);
        }
    }

    /**
     * Creates a component handler for a progress bar, which is specified by the
     * given tag.
     *
     * @param tag the progress bar tag
     * @param create the create flag
     * @return the component handler for the progress bar element
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<Integer> createProgressBar(ProgressBarTag tag,
            boolean create) throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        else
        {
            JProgressBar bar = new JProgressBar();
            initComponent(bar, tag);
            bar.setMinimum(tag.getMin().intValue());
            bar.setMaximum(tag.getMax().intValue());
            if (tag.getValue() != null)
            {
                bar.setValue(tag.getValue().intValue());
            }
            if (tag.isAllowText())
            {
                bar.setStringPainted(true);
                bar.setString(tag.getProgressTextData().getCaption());
            }

            return new SwingProgressBarHandler(bar);
        }
    }

    /**
     * Creates a component handler for a slider, which is specified by the given
     * tag.
     *
     * @param tag the slider tag
     * @param create the create flag
     * @return the component handler for the slider element
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<Integer> createSlider(SliderTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        else
        {
            JSlider slider = new JSlider();
            initComponent(slider, tag);
            slider.setMinimum(tag.getMin());
            slider.setMaximum(tag.getMax());

            if (tag.getMinorTicks() > 0)
            {
                slider.setMinorTickSpacing(tag.getMinorTicks());
            }
            if (tag.getMajorTicks() > 0)
            {
                slider.setMajorTickSpacing(tag.getMajorTicks());
            }

            slider.setPaintLabels(tag.isShowLabels());
            slider.setPaintTicks(tag.isShowTicks());
            slider.setOrientation((tag.getSliderOrientation() == Orientation.VERTICAL)
                    ? JSlider.VERTICAL : JSlider.HORIZONTAL);

            return new SwingSliderHandler(slider);
        }
    }

    /**
     * Creates a component handler for a table specified by the given tag.
     *
     * @param tag the tag
     * @param create the create flag
     * @return the component handler
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<Object> createTable(TableTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        else
        {
            JTable table = new JTable();
            SwingTableModel model = new SwingTableModel(tag, table);
            table.setModel(model);
            initTableColumnWidths(tag, table);
            initColumnRenderers(tag.getTableFormController(), model, table);
            initComponent(table, tag);

            if (tag.getEditorSelectionHandler() == null)
            {
                tag.setEditorSelectionHandler(new SwingTableSelectionHandler());
            }

            if (tag.isMultiSelection())
            {
                table
                        .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            }
            else
            {
                table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            }

            if (tag.getSelectionBackgroundColor() != null)
            {
                table.setSelectionBackground(SwingComponentUtils
                        .logic2SwingColor(tag.getSelectionBackgroundColor()));
            }
            if (tag.getSelectionForegroundColor() != null)
            {
                table.setSelectionForeground(SwingComponentUtils
                        .logic2SwingColor(tag.getSelectionForegroundColor()));
            }

            SwingSizeHandler sizeHandler = fetchSizeHandler(tag);
            Object container = tag.findContainer().getContainer();
            SwingTableComponentHandler handler =
                    new SwingTableComponentHandler(table, tag
                            .getPreferredScrollWidth().toPixel(sizeHandler,
                                    container, false), tag
                            .getPreferredScrollHeight().toPixel(sizeHandler,
                                    container, true));
            registerTableListener(tag, handler);
            return handler;
        }
    }

    /**
     * Creates a component handler for a tree specified by the given tag.
     *
     * @param tag the tag
     * @param create the create flag
     * @return the component handler
     * @throws FormBuilderException if an error occurs
     */
    public ComponentHandler<Object> createTree(TreeTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }

        else
        {
            SwingConfigurationTreeModel model = new SwingConfigurationTreeModel(
                    tag.getTreeModel());
            JTree tree = new JTree(model);
            initComponent(tree, tag);

            tree.setRootVisible(tag.isRootVisible());
            tree.setEditable(tag.isEditable());
            tree
                    .getSelectionModel()
                    .setSelectionMode(
                            tag.isMultiSelection() ? TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
                                    : TreeSelectionModel.SINGLE_TREE_SELECTION);

            SwingTreeNodeFormatter formatter = new SwingTreeNodeFormatter();
            SwingTreeCellRenderer renderer = new SwingTreeCellRenderer(tag
                    .getResolvedIconHandler(), tag.getIcons(), formatter);
            tree.setCellRenderer(renderer);
            tree.setCellEditor(new SwingTreeCellEditor(tree, renderer));

            SwingSizeHandler sizeHandler = fetchSizeHandler(tag);
            Object container = tag.findContainer().getContainer();
            return new SwingTreeComponentHandler(tree, model, tag.getName(),
                    tag.getPreferredScrollWidth().toPixel(sizeHandler,
                            container, false), tag.getPreferredScrollHeight()
                            .toPixel(sizeHandler, container, true));
        }
    }

    /**
     * Initializes the given component from the specified tag.
     *
     * @param component the component to initialize
     * @param tag the tag
     */
    protected void initComponent(JComponent component, ComponentBaseTag tag)
    {
        if (tag.getFont() != null)
        {
            component.setFont((Font) tag.getFont());
        }

        if (tag.getBackgroundColor() != null)
        {
            SwingComponentUtils.setBackgroundColor(component, tag
                    .getBackgroundColor());
        }
        if (tag.getForegroundColor() != null)
        {
            SwingComponentUtils.setForegroundColor(component, tag
                    .getForegroundColor());
        }

        if (tag.getToolTipData().isDefined())
        {
            component.setToolTipText(tag.getToolTipData().getCaption());
        }

        component.setName(tag.getName());
    }

    /**
     * Initializes a label from a <code>TextIconData</code> object.
     *
     * @param label the label to be initialized
     * @param tag the tag with the label definition
     * @param data the text icon data object
     */
    protected void initLabel(JLabel label, ComponentBaseTag tag,
            TextIconData data)
    {
        label.setText(data.getCaption());
        if (data.getIcon() != null)
        {
            label.setIcon((Icon) data.getIcon());
        }
        if (data.getMnemonic() > 0)
        {
            label.setDisplayedMnemonic(SwingComponentUtils.toMnemonic(data
                    .getMnemonic()));
        }
        label.setHorizontalAlignment(transformAlign(data.getAlignment()));

        initComponent(label, tag);
    }

    /**
     * Initializes a button component like a toggle button or a checkbox. These
     * components can all be handled pretty the same.
     *
     * @param button the button to initialize
     * @param tag the tag for the button
     * @param data the text icon data object
     * @param command the button's command (can be <b>null</b>)
     */
    protected void initButton(AbstractButton button, InputComponentTag tag,
            TextIconData data, String command)
    {
        initComponent(button, tag);

        button.setText(data.getCaption());
        if (data.getIcon() != null)
        {
            button.setIcon((Icon) data.getIcon());
        }
        if (data.getMnemonic() > 0)
        {
            button.setMnemonic(SwingComponentUtils.toMnemonic(data
                    .getMnemonic()));
        }
        button.setHorizontalAlignment(transformAlign(data.getAlignment()));
        if (command != null)
        {
            button.setActionCommand(command);
        }
    }

    /**
     * Initializes a button component and creates a component handler for it.
     * This method calls <code>initButton()</code> and then creates a
     * <code>SwingButtonHandler</code> that wraps the button.
     *
     * @param button the button to initialize
     * @param tag the tag for the button
     * @param data the text icon data object
     * @param command the button's command (can be <b>null</b>)
     * @return the component handler for the button
     */
    protected ComponentHandler<Boolean> createButtonHandler(
            AbstractButton button, InputComponentTag tag, TextIconData data,
            String command)
    {
        initButton(button, tag, data, command);
        return new SwingButtonHandler(button);
    }

    /**
     * Initializes a text component. Default initialization will be performed
     * and a limited text document will be set if necessary.
     *
     * @param text the text component
     * @param tag the input component tag
     * @param maxlen the maximum text length
     */
    protected void initText(JTextComponent text, InputComponentTag tag,
            int maxlen)
    {
        initComponent(text, tag);
        if (maxlen > 0)
        {
            text.setDocument(new SwingLimitedTextModel(maxlen));
        }
    }

    /**
     * Creates a default border. This method is used for panels that should have
     * a border. If no specific border is specified, the default border returned
     * by this method is used.
     *
     * @return the default border
     */
    protected Border createDefaultBorder()
    {
        return BorderFactory.createEtchedBorder();
    }

    /**
     * Initializes the widths of the columns of the specified table. This
     * implementation checks whether there is at least one column tag with the
     * width attribute defined. In this case it turns off auto resizing of the
     * table's columns and sets the preferred and minimum widths of the columns
     * affected.
     *
     * @param tag the tag defining the table
     * @param table the table
     * @throws FormBuilderException if an error occurs
     */
    protected void initTableColumnWidths(TableTag tag, JTable table)
            throws FormBuilderException
    {
        TableColumnWidthController widthCtrl = tag.getColumnWidthController();
        SwingSizeHandler sizeHandler = null;
        Object container = null;
        int totalWidth = 0;

        for (int i = 0; i < tag.getColumnCount(); i++)
        {
            if (!widthCtrl.isPercentWidth(i))
            {
                if (sizeHandler == null)
                {
                    sizeHandler = fetchSizeHandler(tag);
                    container = tag.findContainer().getContainer();
                }

                int width = widthCtrl.getOriginalFixedWidth(i).toPixel(
                        sizeHandler, container, false);
                table.getColumnModel().getColumn(i).setPreferredWidth(width);
                widthCtrl.setFixedWidth(i, width);
                totalWidth += width;
            }
        }

        if (widthCtrl.getNumberOfColumnWithPercentWidth() == 0)
        {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            Dimension sz = table.getPreferredScrollableViewportSize();
            sz.setSize(totalWidth, sz.getHeight());
            table.setPreferredScrollableViewportSize(sz);
        }
    }

    /**
     * Registers a specialized listener for resizing table columns if required.
     * This method checks whether the table has columns with a relative width.
     * If this is the case, a listener is registered that adjusts the columns'
     * widths when the table is resized.
     *
     * @param tag the table tag
     * @param handler the handler for the table
     * @throws FormBuilderException if an error occurs
     */
    protected void registerTableListener(TableTag tag,
            SwingTableComponentHandler handler) throws FormBuilderException
    {
        if (tag.getColumnWidthController().getNumberOfColumnWithPercentWidth() > 0)
        {
            SwingTableColumnWidthListener l = new SwingTableColumnWidthListener(
                    handler.getTable(), tag.getColumnWidthController());
            ((JComponent) handler.getOuterComponent()).addComponentListener(l);
            handler.getTable().getColumnModel().addColumnModelListener(l);
        }
    }

    /**
     * Helper method for transforming a platform independent alignment into a
     * Swing constant.
     *
     * @param al the alignment
     * @return the transformed alignment constant
     */
    static int transformAlign(TextIconAlignment al)
    {
        int align;
        if (TextIconAlignment.RIGHT.equals(al))
        {
            align = SwingConstants.RIGHT;
        }
        else if (TextIconAlignment.CENTER.equals(al))
        {
            align = SwingConstants.CENTER;
        }
        else
        {
            align = SwingConstants.LEFT;
        }

        return align;
    }

    /**
     * Transforms a Swing specific alignment constant into a platform
     * independent alignment constant.
     *
     * @param al the Swing alignment constant
     * @return the independent alignment constant
     */
    static TextIconAlignment transformSwingAlign(int al)
    {
        switch (al)
        {
        case SwingConstants.RIGHT:
            return TextIconAlignment.RIGHT;
        case SwingConstants.LEFT:
            return TextIconAlignment.LEFT;
        case SwingConstants.CENTER:
            return TextIconAlignment.CENTER;
        default:
            throw new IllegalArgumentException("Unknown alignment value: " + al);
        }
    }

    /**
     * Obtains the current {@code SwingSizeHandler}. An instance of {@code
     * SwingSizeHandler} is created on demand and stored in the current Jelly
     * context. Thus the caching facilities provided by this class can be used.
     * Threading issues do not apply because the Jelly context is confined to a
     * single thread.
     *
     * @param tag the tag which is currently processed
     * @return the {@code SwingSizeHandler}
     */
    SwingSizeHandler fetchSizeHandler(FormBaseTag tag)
    {
        JellyContext ctx = tag.getContext();
        SwingSizeHandler sizeHandler = (SwingSizeHandler) ctx
                .getVariable(VAR_SIZE_HANDLER);

        if (sizeHandler == null)
        {
            sizeHandler = new SwingSizeHandler();
            ctx.setVariable(VAR_SIZE_HANDLER, sizeHandler);
        }

        return sizeHandler;
    }

    /**
     * Helper method for initializing a text field.
     *
     * @param tag the tag describing the field
     * @param text the text field to be initialized
     * @return the component handler for the text field
     */
    private ComponentHandler<String> initTextField(TextFieldTag tag,
            JTextField text)
    {
        if (tag.getColumns() > 0)
        {
            text.setColumns(tag.getColumns());
        }
        initText(text, tag, tag.getMaxlength());
        return new SwingTextHandler(text);
    }

    /**
     * Initializes a map which associates strings with {@code TextAttribute}
     * constants. All available keys are obtaining using reflection.
     *
     * @return the map
     */
    private Map<String, TextAttribute> initTextAttributeMapping()
    {
        log.info("Initializing TextAttribute mapping.");
        Map<String, TextAttribute> map = new HashMap<String, TextAttribute>();

        for (Field field : TextAttribute.class.getFields())
        {
            if (field.getType().equals(TextAttribute.class))
            {
                try
                {
                    map.put(field.getName(), (TextAttribute) field.get(null));
                }
                catch (IllegalArgumentException e)
                {
                    log.warn("Error when reading field " + field.getName(), e);
                }
                catch (IllegalAccessException e)
                {
                    log.warn("Error when reading field " + field.getName(), e);
                }
            }
        }

        return map;
    }

    /**
     * Deals with standard font attributes. Populates the specified map with the
     * standard attributes defined for the given font tag.
     *
     * @param tag the font tag
     * @param attrs the map to be filled
     */
    private static void handleStandardFontAttributes(FontTag tag,
            Map<TextAttribute, Object> attrs)
    {
        if (tag.getName() != null)
        {
            attrs.put(TextAttribute.FAMILY, tag.getName());
        }
        if (tag.getSize() > 0)
        {
            attrs.put(TextAttribute.SIZE, Float.valueOf(tag.getSize()));
        }
        attrs.put(TextAttribute.WEIGHT,
                tag.isBold() ? TextAttribute.WEIGHT_BOLD
                        : TextAttribute.WEIGHT_REGULAR);
        attrs.put(TextAttribute.POSTURE,
                tag.isItalic() ? TextAttribute.POSTURE_OBLIQUE
                        : TextAttribute.POSTURE_REGULAR);
    }

    /**
     * Deals with extended font attributes. Attributes of type {@code
     * TextAttribute} are directly accepted. For other attributes a conversion
     * to {@code TextAttribute} is tried.
     *
     * @param tag the {@code FontTag}
     * @param attrs the map to be filled
     */
    private void handleExtendedFontAttributes(FontTag tag,
            Map<TextAttribute, Object> attrs)
    {
        for (Map.Entry<?, ?> e : tag.getAttributesMap().entrySet())
        {
            if (e.getKey() instanceof TextAttribute)
            {
                attrs.put((TextAttribute) e.getKey(), e.getValue());
            }
            else
            {
                TextAttribute ta = textAttributeMapping.get(e.getKey());
                if (ta != null)
                {
                    attrs.put(ta, e.getValue());
                }
                else
                {
                    log.warn("Ignoring font attribute " + e.getKey());
                }
            }
        }
    }

    /**
     * Initializes custom renderer components for the columns of the specified
     * table.
     *
     * @param controller the {@code TableFormController}
     * @param tableModel the table model
     * @param table the table
     */
    private static void initColumnRenderers(TableFormController controller,
                                            SwingTableModel tableModel, JTable table)
    {
        for (int i = 0; i < controller.getColumnCount(); i++)
        {
            if (controller.hasRenderer(i))
            {
                table.getColumnModel().getColumn(i)
                        .setCellRenderer(tableModel.getRenderer());
            }
        }
    }
}

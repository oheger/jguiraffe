/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.sf.jguiraffe.gui.builder.components.model.StaticTextData;
import net.sf.jguiraffe.gui.builder.components.model.TreeNodePath;
import net.sf.jguiraffe.gui.builder.components.tags.BorderLayoutTag;
import net.sf.jguiraffe.gui.builder.components.tags.ButtonLayoutTag;
import net.sf.jguiraffe.gui.builder.components.tags.ButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.CheckboxTag;
import net.sf.jguiraffe.gui.builder.components.tags.ComboBoxTag;
import net.sf.jguiraffe.gui.builder.components.tags.ComponentBaseTag;
import net.sf.jguiraffe.gui.builder.components.tags.DesktopPanelTag;
import net.sf.jguiraffe.gui.builder.components.tags.FontTag;
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
import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManager;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManagerImpl;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;
import net.sf.jguiraffe.gui.layout.BorderLayout;
import net.sf.jguiraffe.gui.layout.ButtonLayout;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;
import net.sf.jguiraffe.locators.Locator;
import net.sf.jguiraffe.locators.LocatorUtils;

/**
 * This is a test implementation of the ComponentManager interface. Instead of
 * real components only strings are created whose names represent the
 * corresponding components. So the builder's results should be easy to test.
 *
 * @author Oliver Heger
 * @version $Id: ComponentManagerImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ComponentManagerImpl implements ComponentManager
{
    public void addContainerComponent(Object container, Object component,
            Object constraints)
    {
        ((Container) container).addComponent(component, constraints);
    }

    public void setContainerLayout(Object container, Object layout)
    {
        ((Container) container).setLayout(layout);
    }

    public PlatformEventManager createEventManager()
    {
        return new PlatformEventManagerImpl();
    }

    /**
     * Returns a widget handler for the specified component. This implementation
     * returns a <code>{@link WidgetHandlerImpl}</code> object.
     *
     * @param component the component to wrap
     * @return the widget handler
     */
    public WidgetHandler getWidgetHandlerFor(Object component)
    {
        return new WidgetHandlerImpl(component);
    }

    public Object createLabel(LabelTag tag, boolean create)
            throws FormBuilderException
    {
        if (!create)
        {
            StringBuilder buf = new StringBuilder();
            dumpTextIconData(buf, tag.getTextIconData());
            appendAttr(buf, "COMP", tag.getComponentref());
            dumpComponent(buf, tag);
            return toPropString("LABEL", buf);
        }
        else
        {
            return null;
        }
    }

    public void linkLabel(Object label, Object component, String text)
            throws FormBuilderException
    {
        StringBuilder buf = (StringBuilder) label;
        if (text != null)
        {
            buf.append("<TEXT -> ").append(text).append(">");
        }
        buf.append("<linked>");
    }

    public Object createIcon(Locator locator) throws FormBuilderException
    {
        StringBuilder buf = new StringBuilder();
        appendAttr(buf, "LOCATOR", LocatorUtils.locatorToDataString(locator));
        return toPropString("ICON", buf);
    }

    public Object createFont(FontTag tag) throws FormBuilderException
    {
        StringBuilder buf = new StringBuilder();
        appendAttr(buf, "NAME", tag.getName());
        appendAttr(buf, "SIZE", tag.getSize());
        appendAttr(buf, "BOLD", tag.isBold());
        appendAttr(buf, "ITALIC", tag.isItalic());
        Map<?, ?> attrs = tag.getAttributesMap();
        if (!attrs.isEmpty())
        {
            appendAttr(buf, "ATTRS", attrs);
        }
        return toPropString("FONT", buf);
    }

    public Object createPercentLayout(PercentLayoutTag tag)
            throws FormBuilderException
    {
        StringBuilder buf = new StringBuilder("PercentLayout [ COLS = [");
        if (tag.getColumns() != null)
        {
            buf.append(' ').append(tag.getColumns());
        }
        else
        {
            for (Iterator it = tag.getColConstraints().iterator(); it.hasNext();)
            {
                buf.append(' ').append(it.next());
            }
        }
        buf.append(" ] ROWS = [");
        if (tag.getRows() != null)
        {
            buf.append(' ').append(tag.getRows());
        }
        else
        {
            for (Iterator it = tag.getRowConstraints().iterator(); it.hasNext();)
            {
                buf.append(' ').append(it.next());
            }
        }
        buf.append(" ]");
        if (!tag.getColGroups().isEmpty())
        {
            buf.append(" COLGRPS = [ ");
            for (Iterator it = tag.getColGroups().iterator(); it.hasNext();)
            {
                buf.append(it.next()).append(' ');
            }
            buf.append(']');
        }
        if (!tag.getRowGroups().isEmpty())
        {
            buf.append(" ROWGRPS = [ ");
            for (Iterator it = tag.getRowGroups().iterator(); it.hasNext();)
            {
                buf.append(it.next()).append(' ');
            }
            buf.append(']');
        }
        if (tag.getPercentLayout().isCanShrink())
        {
            appendAttr(buf, "SHRINK", Boolean.TRUE);
        }
        buf.append(" ]");
        return buf.toString();
    }

    public Object createButtonLayout(ButtonLayoutTag tag)
            throws FormBuilderException
    {
        StringBuilder buf = new StringBuilder("ButtonLayout [");
        ButtonLayout layout = tag.getButtonLayout();
        appendAttr(buf, "LEFT", layout.getLeftMargin());
        appendAttr(buf, "TOP", layout.getTopMargin());
        appendAttr(buf, "RIGHT", layout.getRightMargin());
        appendAttr(buf, "BOTTOM", layout.getBottomMargin());
        appendAttr(buf, "GAP", layout.getGap());
        appendAttr(buf, "ALIGN", layout.getAlignment().name());
        buf.append(" ]");
        return buf.toString();
    }

    public Object createBorderLayout(BorderLayoutTag tag)
            throws FormBuilderException
    {
        StringBuilder buf = new StringBuilder("BorderLayout [");
        BorderLayout layout = tag.getBorderLayout();
        appendNoUnitAttr(buf, "LEFT", layout.getLeftMargin());
        appendNoUnitAttr(buf, "TOP", layout.getTopMargin());
        appendNoUnitAttr(buf, "RIGHT", layout.getRightMargin());
        appendNoUnitAttr(buf, "BOTTOM", layout.getBottomMargin());
        appendNoUnitAttr(buf, "NORTHGAP", layout.getNorthGap());
        appendNoUnitAttr(buf, "WESTGAP", layout.getWestGap());
        appendNoUnitAttr(buf, "SOUTHGAP", layout.getSouthGap());
        appendNoUnitAttr(buf, "EASTGAP", layout.getEastGap());
        if (layout.isCanShrink())
        {
            appendAttr(buf, "SHRINK", layout.isCanShrink());
        }
        buf.append(" ]");
        return buf.toString();
    }

    public Object createPanel(PanelTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            dumpComponent(buf, tag);
            appendAttr(buf, "TEXT", tag.getTextData().getCaption());
            appendAttr(buf, "TEXTCOL", tag.getColor());
            appendAttr(buf, "TEXTFONT", tag.getTitleFont());
            appendAttr(buf, "BORDER", (tag.isBorder()) ? "TRUE" : null);
            appendAttr(buf, "BORDERREF", tag.getBorderref());
            Container c = new Container("PANEL");
            c.setAttributes(buf.toString());
            return c;
        }
    }

    public Object createDesktopPanel(DesktopPanelTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            dumpComponent(buf, tag);
            appendAttr(buf, "DRAGMODE", tag.getDragMode().name());
            Container c = new Container("DESKTOPPANEL");
            c.setAttributes(buf.toString());
            return c;
        }
    }

    public Object createSplitter(SplitterTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            dumpComponent(buf, tag);
            appendAttr(buf, "ORIENTATION", tag.getSplitterOrientation().name());
            if (tag.getPos() > 0)
            {
                appendAttr(buf, "POS", new Integer(tag.getPos()));
            }
            if (tag.getSize() > 0)
            {
                appendAttr(buf, "SIZE", new Integer(tag.getSize()));
            }
            // convert resize weight to integer so that it can be better
            // compared; it will be in the range 0 to 100
            Float resizeWeight = new Float(tag.getResizeWeight() * 100);
            appendAttr(buf, "RESIZEWEIGHT",
                    new Integer(resizeWeight.intValue()));

            StringBuilder b2 = new StringBuilder(toPropString("SPLITTER", buf).toString());
            b2.append(" { ").append(tag.getFirstComponent()).append(", ");
            b2.append(tag.getSecondComponent()).append(" }");
            return b2.toString();
        }
    }

    public Object createRadioGroup(Map<String, Object> radios) throws FormBuilderException
    {
        StringBuilder buf = new StringBuilder();
        buf.append("RADIOGROUP { ");
        boolean first = true;
        for (String name : radios.keySet())
        {
            if (first)
            {
                first = false;
            }
            else
            {
                buf.append(", ");
            }
            buf.append(name);
        }
        buf.append(" }");
        return buf.toString();
    }

    public ComponentHandler createButton(ButtonTag tag, boolean create)
            throws FormBuilderException
    {
        return createPushButton(tag, create, "BUTTON",
                tag.isDefault() ? "DEFAULT" : null);
    }

    public ComponentHandler createToggleButton(ToggleButtonTag tag,
            boolean create) throws FormBuilderException
    {
        return createPushButton(tag, create, "TOGGLE", null);
    }

    /**
     * Helper method for creating a component handler for a button tag.
     *
     * @param tag the tag
     * @param create the create flag
     * @param name the name of the component (i.e. the button type)
     * @param furtherAttrs optional additional attributes to be added
     * @return the component handler
     * @throws FormBuilderException if an error occurs
     */
    private ComponentHandler createPushButton(ToggleButtonTag tag,
            boolean create, String name, String furtherAttrs)
            throws FormBuilderException
    {
        if (!create)
        {
            StringBuilder buf = new StringBuilder();
            dumpTextIconData(buf, tag.getTextIconData());
            appendAttr(buf, "CMD", tag.getCommand());
            dumpInputComponent(buf, tag);
            if (furtherAttrs != null)
            {
                buf.append(' ').append(furtherAttrs);
            }
            return createHandler(name, buf, tag, Boolean.TYPE);
        }
        else
        {
            return null;
        }
    }

    public ComponentHandler createTextField(TextFieldTag tag, boolean create)
            throws FormBuilderException
    {
        return createTextComponent("TEXTFIELD", tag, create);
    }

    public ComponentHandler createPasswordField(PasswordFieldTag tag,
            boolean create) throws FormBuilderException
    {
        return createTextComponent("PASSWORDFIELD", tag, create);
    }

    /**
     * Helper method for creating a text field component which can either be a
     * normal text field or a password field.
     *
     * @param type the type name of the text field
     * @param tag the tag
     * @param create the create flag
     * @return the component handler
     * @throws FormBuilderException if an error occurs
     */
    private ComponentHandler createTextComponent(String type, TextFieldTag tag,
            boolean create) throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            dumpInputComponent(buf, tag);
            if (tag.getColumns() > 0)
            {
                appendAttr(buf, "COLUMNS", new Integer(tag.getColumns()));
            }
            if (tag.getMaxlength() > 0)
            {
                appendAttr(buf, "MAXLEN", new Integer(tag.getMaxlength()));
            }
            return createHandler(type, buf, tag, String.class);
        }
    }

    public ComponentHandler createTextArea(TextAreaTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            dumpInputComponent(buf, tag);
            if (tag.getColumns() > 0)
            {
                appendAttr(buf, "COLUMNS", new Integer(tag.getColumns()));
            }
            if (tag.getRows() > 0)
            {
                appendAttr(buf, "ROWS", new Integer(tag.getRows()));
            }
            if (tag.isWrap())
            {
                appendAttr(buf, "WRAP", "YES");
            }
            if (tag.getMaxlength() > 0)
            {
                appendAttr(buf, "MAXLEN", new Integer(tag.getMaxlength()));
            }
            appendAttr(buf, "SCROLLWIDTH", tag.getPreferredScrollWidth());
            appendAttr(buf, "SCROLLHEIGHT", tag.getPreferredScrollHeight());
            return createHandler("TEXTAREA", buf, tag, String.class);
        }
    }

    public ComponentHandler createCheckbox(CheckboxTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            dumpInputComponent(buf, tag);
            dumpTextIconData(buf, tag.getTextIconData());
            return createHandler("CHECKBOX", buf, tag, Boolean.TYPE);
        }
    }

    public ComponentHandler createRadioButton(RadioButtonTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            dumpInputComponent(buf, tag);
            dumpTextIconData(buf, tag.getTextIconData());
            return createHandler("RADIO", buf, tag, Boolean.TYPE);
        }
    }

    public ComponentHandler createComboBox(ComboBoxTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            dumpInputComponent(buf, tag);
            appendAttr(buf, "EDIT", new Boolean(tag.isEditable()));
            StringBuilder model = new StringBuilder("{ ");
            for (int i = 0; i < tag.getListModel().size(); i++)
            {
                if (i > 0)
                {
                    model.append(", ");
                }
                model.append(tag.getListModel().getDisplayObject(i));
            }
            model.append(" }");
            appendAttr(buf, "MODEL", model.toString());
            return createHandler("COMBO", buf, tag, tag.getListModel()
                    .getType());
        }
    }

    public ComponentHandler createListBox(ListBoxTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            dumpInputComponent(buf, tag);
            appendAttr(buf, "MULTI", new Boolean(tag.isMulti()));
            StringBuilder model = new StringBuilder("{ ");
            for (int i = 0; i < tag.getListModel().size(); i++)
            {
                if (i > 0)
                {
                    model.append(", ");
                }
                model.append(tag.getListModel().getDisplayObject(i));
            }
            model.append(" }");
            appendAttr(buf, "MODEL", model.toString());
            appendNoUnitAttr(buf, "SCROLLWIDTH", tag.getPreferredScrollWidth());
            appendNoUnitAttr(buf, "SCROLLHEIGHT", tag.getPreferredScrollHeight());
            return createHandler("LIST", buf, tag,
                    (tag.isMulti()) ? Object[].class : tag.getListModel()
                            .getType());
        }
    }

    public ComponentHandler createTabbedPane(TabbedPaneTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            dumpInputComponent(buf, tag);
            appendAttr(buf, "PLACEMENT", tag.getPlacementValue().name());
            buf.append(" TABS { ");
            for (Iterator it = tag.getTabs().iterator(); it.hasNext();)
            {
                TabbedPaneTag.TabData tabData = (TabbedPaneTag.TabData) it
                        .next();
                buf.append("TAB [");
                appendAttr(buf, "TITLE", tabData.getTitle());
                appendAttr(buf, "ICON", tabData.getIcon());
                appendAttr(buf, "TOOLTIP", tabData.getToolTip());
                if (tabData.getMnemonic() != 0)
                {
                    appendAttr(buf, "MNEMO", new Character((char) tabData
                            .getMnemonic()));
                }
                appendAttr(buf, "COMP", tabData.getComponent());
                buf.append(" ] ");
            }
            buf.append("} ");
            return createHandler("TABBEDPANE", buf, tag, Integer.class);
        }
    }

    public ComponentHandler createStaticText(StaticTextTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            dumpInputComponent(buf, tag);
            dumpTextIconData(buf, tag.getTextIconData());
            return createHandler("STATICTEXT", buf, tag, StaticTextData.class);
        }
    }

	public ComponentHandler createProgressBar(ProgressBarTag tag, boolean create)
			throws FormBuilderException
	{
		if (create)
		{
			return null;
		}
		else
		{
			StringBuilder buf = new StringBuilder();
			dumpInputComponent(buf, tag);
			appendAttr(buf, "MIN", tag.getMin());
			appendAttr(buf, "MAX", tag.getMax());
			appendAttr(buf, "VALUE", tag.getValue());
			appendAttr(buf, "ALLOWTEXT", tag.isAllowText() ? Boolean.TRUE
					: Boolean.FALSE);
			if (tag.getProgressTextData().isDefined())
			{
				appendAttr(buf, "TEXT", tag.getProgressTextData().getCaption());
			}
			return createHandler("PROGRESSBAR", buf, tag, Integer.class);
		}
	}

    public ComponentHandler<Integer> createSlider(SliderTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            dumpInputComponent(buf, tag);
            appendAttr(buf, "MIN", tag.getMin());
            appendAttr(buf, "MAX", tag.getMax());
            appendAttr(buf, "ORIENTATION", tag.getSliderOrientation().name());
            appendAttr(buf, "MAJORTICKS", tag.getMajorTicks());
            appendAttr(buf, "MINORTICKS", tag.getMinorTicks());
            appendAttr(buf, "SHOWTICKS", tag.isShowTicks());
            appendAttr(buf, "SHOWLABELS", tag.isShowLabels());
            return createHandler("SLIDER", buf, tag, Integer.class);
        }
    }

    public ComponentHandler createTable(TableTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        dumpInputComponent(buf, tag);
        appendAttr(buf, "SELECTIONFG", tag.getSelectionForegroundColor());
        appendAttr(buf, "SELECTIONBG", tag.getSelectionBackgroundColor());
        appendAttr(buf, "EDITABLE", tag.isTableEditable());
        appendAttr(buf, "VALIDATIONERRTITLE", tag.getValidationErrorTitle());
        if (tag.isMultiSelection())
        {
            buf.append(" MULTISELECT");
        }
        appendNoUnitAttr(buf, "SCROLLWIDTH", tag.getPreferredScrollWidth());
        appendNoUnitAttr(buf, "SCROLLHEIGHT", tag.getPreferredScrollHeight());

        buf.append(" COLUMNS {");
        for (TableColumnTag col : tag.getColumns())
        {
            buf.append(" COLUMN [");
            dumpInputComponent(buf, col);
            if (col.getHeaderText().isDefined())
            {
                appendAttr(buf, "HEADER", col.getHeaderText().getCaption());
            }
            if (col.getLogicDataClass() != null)
            {
                appendAttr(buf, "LOGICCLASS", col.getLogicDataClass().name());
            }
            else if (col.getDataClass() != null)
            {
                appendAttr(buf, "CLASS", col.getDataClass().getName());
            }
            appendAttr(buf, "WIDTH", col.getColumnWidth());
            if (col.getPercentWidth() != 0)
            {
                appendAttr(buf, "PERCENTWIDTH", col.getPercentWidth());
            }
            appendAttr(buf, "EDITABLE", tag.isColumnEditable(col));
            if (col.getRendererComponent() != null)
            {
                buf.append(" RENDERER { ").append(col.getRendererComponent());
                buf.append(" }");
            }
            if (col.getEditorComponent() != null)
            {
                buf.append(" EDITOR { ").append(col.getEditorComponent());
                buf.append(" }");
            }
            buf.append(" ]");
        }
        buf.append(" }");
        return createHandler("TABLE", buf, tag, Integer.TYPE);
    }

    public ComponentHandler<Object> createTree(TreeTag tag, boolean create)
            throws FormBuilderException
    {
        if (create)
        {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        dumpInputComponent(buf, tag);
        appendAttr(buf, "MODEL", tag.getModel());
        appendAttr(buf, "ICONHANDLER", tag.getIconHandler());
        appendAttr(buf, "EDITABLE", tag.isEditable());
        appendAttr(buf, "ROOTVISIBLE", tag.isRootVisible());
        if (tag.isMultiSelection())
        {
            buf.append(" MULTISELECT");
        }
        Map<String, Object> icons = new TreeMap<String, Object>(tag.getIcons());
        if (!icons.isEmpty())
        {
            buf.append(" ICONS {");
            for (Map.Entry<String, Object> entry : icons.entrySet())
            {
                appendAttr(buf, "NAME", entry.getKey());
                buf.append(" [ ").append(entry.getValue()).append("]");
            }
            buf.append(" }");
        }
        appendNoUnitAttr(buf, "SCROLLWIDTH", tag.getPreferredScrollWidth());
        appendNoUnitAttr(buf, "SCROLLHEIGHT", tag.getPreferredScrollHeight());
        TreeHandlerImpl handler = new TreeHandlerImpl();
        initHandler(handler, "TREE", buf, TreeNodePath.class);
        handler.setModel(tag.getTreeModel());
        return handler;
    }

    /**
     * Creates a string representation for the properties of the passed in
     * component tag.
     *
     * @param buf the target buffer
     * @param tag the tag
     */
    protected void dumpComponent(StringBuilder buf, ComponentBaseTag tag)
    {
        appendAttr(buf, "BCOL", tag.getBackgroundColor());
        appendAttr(buf, "FCOL", tag.getForegroundColor());
        appendAttr(buf, "FONT", tag.getFont());
        appendAttr(buf, "FTREF", tag.getFontRef());
        appendAttr(buf, "TOOLTIP", tag.getToolTipData().getCaption());
    }

    /**
     * Creates a string representation for the properties of the passed in input
     * component tag.
     *
     * @param buf the target buffer
     * @param tag the tag
     */
    protected void dumpInputComponent(StringBuilder buf, InputComponentTag tag)
    {
        dumpComponent(buf, tag);
        appendAttr(buf, "NAME", tag.getName());
        appendAttr(buf, "PROP", tag.getPropertyName());
        appendAttr(buf, "DISP", tag.getDisplayName());
        appendClassAttr(buf, "FLDVAL", tag.getFieldValidator());
        appendClassAttr(buf, "FRMVAL", tag.getFormValidator());
        appendClassAttr(buf, "READTR", tag.getReadTransformer());
        appendClassAttr(buf, "WRITETR", tag.getWriteTransformer());
    }

    /**
     * Creates a string representation for the properties of the passed in text
     * icon data object.
     *
     * @param buf the target buffer
     * @param data the data object
     */
    protected void dumpTextIconData(StringBuilder buf, TextIconData data)
    {
        appendAttr(buf, "TEXT", data.getCaption());
        appendAttr(buf, "ICON", data.getIcon());
        appendAttr(buf, "ALIGN", data.getAlignmentString());
        if (data.getMnemonic() != 0)
        {
            appendAttr(buf, "MNEMO", new Character((char) data.getMnemonic()));
        }
    }

    /**
     * Creates a string with the component name and its properties.
     *
     * @param compName the name of the component
     * @param buf the buffer with the properties
     * @return the string representation
     */
    protected Object toPropString(String compName, StringBuilder buf)
    {
        StringBuilder buf2 = new StringBuilder(compName);
        if (buf.length() > 0)
        {
            buf2.append(" [").append(buf.toString()).append(" ]");
        }
        return buf2;
    }

    /**
     * Creates a dummy component handler object.
     *
     * @param compName the name of the component
     * @param buf the target buffer
     * @param tag the tag
     * @param defType the default component type
     * @return the component handler
     * @throws FormBuilderException if an error occurs
     */
    protected ComponentHandler createHandler(String compName, StringBuilder buf,
            InputComponentTag tag, Class defType) throws FormBuilderException
    {
        ComponentHandlerImpl ch = new ComponentHandlerImpl();
        initHandler(ch, compName, buf, defType);
        return ch;
    }

    /**
     * Initializes the standard properties of the given handler object.
     *
     * @param ch the handler
     * @param compName the name of the component
     * @param buf the target buffer
     * @param defType the default component type
     */
    private void initHandler(ComponentHandlerImpl ch, String compName,
            StringBuilder buf, Class<?> defType)
    {
        ch.setType(defType);
        ch.setComponent(toPropString(compName, buf));
    }

    /**
     * Helper method for generating string representations.
     *
     * @param buf the buffer
     * @param name the attribute's name
     * @param val the attribute's value
     */
    private static void appendAttr(StringBuilder buf, String name, Object val)
    {
        if (val != null)
        {
            buf.append(' ').append(name).append(" = ").append(val);
        }
    }

    /**
     * Helper method for generating a string representation for a class
     * attribute.
     *
     * @param buf the buffer
     * @param name the attribute's name
     * @param val the attribute's value
     */
    private static void appendClassAttr(StringBuilder buf, String name,
            Object val)
    {
        if (val != null)
        {
            appendAttr(buf, name, val.getClass().getName());
        }
    }

    /**
     * Helper method for generating a string representation for a number with
     * unit attribute. If the number is 0, no text will be appended.
     *
     * @param buf the buffer
     * @param name the attribute's name
     * @param val the attribute's value
     */
    private static void appendNoUnitAttr(StringBuilder buf, String name,
            NumberWithUnit val)
    {
        if (val != null && !NumberWithUnit.ZERO.equals(val))
        {
            appendAttr(buf, name, val);
        }
    }
}

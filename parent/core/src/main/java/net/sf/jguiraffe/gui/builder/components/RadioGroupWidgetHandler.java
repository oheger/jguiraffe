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
package net.sf.jguiraffe.gui.builder.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;

/**
 * <p>
 * A specialized {@link WidgetHandler} implementation for radio groups.
 * </p>
 * <p>
 * Radio groups in UI libraries are typically somewhat special. They are not
 * graphical components on their own, but merely refer to the actual radio
 * button components. However, in the <em>JGUIraffe</em> library radio groups
 * are more or less treated like regular components, and their properties can be
 * accessed through the {@link WidgetHandler} interface.
 * </p>
 * <p>
 * Because of this a special implementation is needed for radio buttons. This
 * implementations acts like a typical composite. Set operations are delegated
 * to all components in the group. For get operations there are some
 * limitations: Unless noted otherwise for a specific operation, the group
 * widget handler expects that all radio buttons in the group share the same
 * properties (e.g. colors or visible state). Therefore it simply returns the
 * corresponding property of the first button in the group.
 * </p>
 * <p>
 * Implementation notes: This implementation is fully functional and can be used
 * independent on the underlying UI library. This is possible because radio
 * button controls are accessed only via the {@code WidgetHandler} interface.
 * This class is not thread-safe. It should be accessed from the <em>event
 * dispatch thread</em> only.
 * </p>
 *
 * @since 1.4
 */
public class RadioGroupWidgetHandler implements WidgetHandler
{
    /** Stores the button group widget. */
    private final Object buttonGroup;

    /** List with the handlers of the managed radio buttons. */
    private final List<WidgetHandler> radioButtons;

    /** The separator between two combined tool tips. */
    private final String tipSeparator;

    /** An array with the original tool tips of the radio buttons. */
    private final String[] buttonTips;

    /** The tool tip of the whole radio group. */
    private String groupTip;

    /**
     * Creates a new instance of {@code RadioGroupWidgetHandler} that handles
     * the given collection of radio buttons.
     *
     * @param group the object representing the button group
     * @param radios the collection with radio buttons to be managed
     * @param toolTipSeparator the separator for tool tips
     */
    public RadioGroupWidgetHandler(Object group,
            Collection<? extends WidgetHandler> radios, String toolTipSeparator)
    {
        buttonGroup = group;
        radioButtons = Collections
                .unmodifiableList(new ArrayList<WidgetHandler>(radios));
        tipSeparator = toolTipSeparator;
        buttonTips = new String[radios.size()];
    }

    /**
     * Returns a (unmodifiable) list with the widget handlers of the radio
     * buttons managed by this object.
     *
     * @return the handlers of the managed radio buttons
     */
    public List<WidgetHandler> getRadioButtons()
    {
        return radioButtons;
    }

    /**
     * Returns the separator between two combined tool tips.
     *
     * @return the tool tip separator
     */
    public String getTipSeparator()
    {
        return tipSeparator;
    }

    public Object getWidget()
    {
        return buttonGroup;
    }

    /**
     * {@inheritDoc} This implementation expects that all buttons in the group
     * have the same visible state. So only the first button is checked.
     */
    public boolean isVisible()
    {
        return firstChild().isVisible();
    }

    /**
     * {@inheritDoc} This implementation changes the visibility state of all
     * radio buttons in this group.
     */
    public void setVisible(boolean f)
    {
        for (WidgetHandler h : getRadioButtons())
        {
            h.setVisible(f);
        }
    }

    /**
     * {@inheritDoc} This implementation assumes that all buttons in the group
     * have the same background color. So the property of the first button is
     * returned.
     */
    public Color getBackgroundColor()
    {
        return firstChild().getBackgroundColor();
    }

    /**
     * {@inheritDoc} This implementation sets the new background color for all
     * radio buttons in this group.
     */
    public void setBackgroundColor(Color c)
    {
        for (WidgetHandler h : getRadioButtons())
        {
            h.setBackgroundColor(c);
        }
    }

    /**
     * {@inheritDoc} This implementation assumes that all buttons in the group
     * have the same foreground color. So the property of the first button is
     * returned.
     */
    public Color getForegroundColor()
    {
        return firstChild().getForegroundColor();
    }

    /**
     * {@inheritDoc} This implementation sets the new foreground color for all
     * radio buttons in this group.
     */
    public void setForegroundColor(Color c)
    {
        for (WidgetHandler h : getRadioButtons())
        {
            h.setForegroundColor(c);
        }
    }

    /**
     * {@inheritDoc} This implementation returns the tool tip of the whole radio
     * group. This tool tip is independent of the tips of single radio buttons.
     * So this implementation just returns the string that was set using
     * {@link #setToolTip(String)}.
     */
    public String getToolTip()
    {
        return groupTip;
    }

    /**
     * {@inheritDoc} This implementation sets the overall tool tip, and it
     * changes the tool tips of the radio buttons that belong to this group. The
     * tool tips of the radio buttons are generated by their original tool tip,
     * a separator, plus the group's tool tip.
     */
    public void setToolTip(String tip)
    {
        int i = 0;
        for (WidgetHandler h : getRadioButtons())
        {
            String expectedTip =
                    combineToolTips(buttonTips[i], groupTip, getTipSeparator());
            String currentTip = h.getToolTip();
            if (!ObjectUtils.equals(expectedTip, currentTip))
            {
                // the tip of the button has changed in the mean time
                buttonTips[i] = currentTip;
            }

            h.setToolTip(
                    combineToolTips(buttonTips[i], tip, getTipSeparator()));
            i++;
        }

        groupTip = tip;
    }

    /**
     * {@inheritDoc} This implementation assumes that all buttons in the group
     * have the same font. So the property of the first button is returned.
     */
    public Object getFont()
    {
        return firstChild().getFont();
    }

    /**
     * {@inheritDoc} This implementation sets the new font for all radio buttons
     * in this group.
     */
    public void setFont(Object font)
    {
        for (WidgetHandler h : getRadioButtons())
        {
            h.setFont(font);
        }
    }

    /**
     * Combines the given tool tips (which both can be <strong>null</strong> to
     * a single one. This method is useful when an already existing tool tip is
     * to be extended by some additional information.
     *
     * @param tip1 the first tool tip
     * @param tip2 the second tool tip
     * @param separator the separator between two defined tool tips
     * @return the resulting combined tool tip; can be <strong>null</strong> if
     *         both tips are <strong>null</strong>
     */
    static String combineToolTips(String tip1, String tip2, String separator)
    {
        if (tip1 == null && tip2 == null)
        {
            return null;
        }
        else if (tip1 == null)
        {
            return tip2;
        }
        else if (tip2 == null)
        {
            return tip1;
        }
        return tip1 + separator + tip2;
    }

    /**
     * Returns the widget handler for the first child of the button group. This
     * element plays a special role because it is accessed by all the methods
     * that query a state property of the group.
     *
     * @return the handler for the first child element
     */
    private WidgetHandler firstChild()
    {
        return getRadioButtons().get(0);
    }
}

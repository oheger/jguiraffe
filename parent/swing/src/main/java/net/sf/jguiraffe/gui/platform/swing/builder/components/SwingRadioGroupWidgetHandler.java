/*
 * Copyright 2006-2016 The JGUIraffe Team.
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

import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.builder.components.WidgetHandler;

import org.apache.commons.lang.ObjectUtils;

/**
 * <p>
 * A specialized {@link WidgetHandler} implementation for Swing radio groups.
 * </p>
 * <p>
 * A radio group in Swing is somewhat special. It is not a graphical component
 * on its own, but it merely refers to the actual radio button components.
 * However, in the <em>JGUIraffe</em> library radio groups are more or less
 * treated like regular components, and their properties can be accessed through
 * the {@link WidgetHandler} interface.
 * </p>
 * <p>
 * Because of this a special implementation is needed for radio buttons. This
 * implementations acts like a typical composite. Set operations are delegated
 * to all components in the group. For get operations there are some
 * limitations: Unless noted otherwise for a specific operation, the group widget
 * handler expects that all radio buttons in the group share the same properties
 * (e.g. colors or visible state). Therefore it simply returns the corresponding
 * property of the first button in the group.
 * </p>
 * <p>
 * Implementation notes: This class is not thread-safe. It should be accessed
 * from the <em>event dispatch thread</em> only. The {@code ButtonGroup} must
 * not be changed after it was passed to the constructor.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingRadioGroupWidgetHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingRadioGroupWidgetHandler implements WidgetHandler
{
    /** Constant for the tool tip separator. */
    private static final String TIP_SEPARATOR = "\n";

    /** Stores the underlying button group. */
    private final ButtonGroup buttonGroup;

    /** An array for easy access to the buttons in the group. */
    private final AbstractButton[] buttons;

    /** An array with the original tool tips of the radio buttons. */
    private final String[] buttonTips;

    /** The tool tip of the whole radio group. */
    private String groupTip;

    /**
     * Creates a new instance of {@code SwingRadioGroupWidgetHandler} and
     * initializes it with the underlying {@code ButtonGroup}.
     *
     * @param group the {@code ButtonGroup}
     */
    public SwingRadioGroupWidgetHandler(ButtonGroup group)
    {
        assert group != null : "Null ButtonGroup passed in!";
        buttonGroup = group;

        buttons = new AbstractButton[group.getButtonCount()];
        int idx = 0;
        for (Enumeration<AbstractButton> en = group.getElements(); en
                .hasMoreElements();)
        {
            buttons[idx++] = en.nextElement();
        }
        assert buttons.length > 0 : "Group is empty!";

        buttonTips = new String[buttons.length];
    }

    /**
     * Returns a reference to the underlying {@code ButtonGroup} object.
     *
     * @return the {@code ButtonGroup}
     */
    public ButtonGroup getButtonGroup()
    {
        return buttonGroup;
    }

    /**
     * Returns the background color of this radio button group. This
     * implementation assumes that all radio buttons in the group have the same
     * color. So it returns the color of the first radio button.
     *
     * @return the background color of this radio button group
     */
    public Color getBackgroundColor()
    {
        return SwingComponentUtils.getBackgroundColor(buttons[0]);
    }

    /**
     * Returns the foreground color of this radio button group. This
     * implementation assumes that all radio buttons in the group have the same
     * color. So it returns the color of the first radio button.
     *
     * @return the foreground color of this radio button group
     */
    public Color getForegroundColor()
    {
        return SwingComponentUtils.getForegroundColor(buttons[0]);
    }

    /**
     * Returns the tool tip of this radio group. The tool tip for the whole
     * group is independent of the tips of single radio buttons. So this
     * implementation just returns the string that was set using
     * {@link #setToolTip(String)}.
     *
     * @return the tool tip of this radio group
     */
    public String getToolTip()
    {
        return groupTip;
    }

    /**
     * Returns the widget wrapped by this handler. This implementation returns
     * the underlying {@code ButtonGroup} object, which is not a real graphical
     * widget.
     *
     * @return the wrapped widget
     */
    public Object getWidget()
    {
        return getButtonGroup();
    }

    /**
     * Returns the visible state of this radio group. This implementation
     * expects that all buttons in the group have the same visible state. So
     * only the first button is checked.
     *
     * @return the visible state of this group
     */
    public boolean isVisible()
    {
        return buttons[0].isVisible();
    }

    /**
     * Sets the background color of this radio button group. This implementation
     * passes the color to all radio buttons in the group. If the color is
     * <b>null</b>, this method has no effect.
     *
     * @param c the new background color
     */
    public void setBackgroundColor(Color c)
    {
        if (c != null)
        {
            for (AbstractButton b : buttons)
            {
                SwingComponentUtils.setBackgroundColor(b, c);
            }
        }
    }

    /**
     * Sets the foreground color of this radio button group. This implementation
     * passes the color to all radio buttons in the group. If the color is
     * <b>null</b>, this method has no effect.
     *
     * @param c the new foreground color
     */
    public void setForegroundColor(Color c)
    {
        if (c != null)
        {
            for (AbstractButton b : buttons)
            {
                SwingComponentUtils.setForegroundColor(b, c);
            }
        }
    }

    /**
     * Sets the tool tip of this radio group. This implementation sets the
     * overall tool tip, and it changes the tool tips of the radio buttons that
     * belong to this group. The tool tips of the radio buttons are generated by
     * their original tool tip, a separator, plus the group's tool tip.
     *
     * @param tip the new tool tip for the whole group
     */
    public void setToolTip(String tip)
    {
        for (int i = 0; i < buttons.length; i++)
        {
            String expectedTip = SwingComponentUtils.combineToolTips(
                    buttonTips[i], groupTip, TIP_SEPARATOR);
            String currentTip = SwingComponentUtils.getToolTip(buttons[i]);
            if (!ObjectUtils.equals(expectedTip, currentTip))
            {
                // the tip of the button has changed in the mean time
                buttonTips[i] = currentTip;
            }

            SwingComponentUtils.setToolTip(buttons[i], buttonTips[i], tip,
                    TIP_SEPARATOR);
        }

        groupTip = tip;
    }

    /**
     * Sets the visible state of this radio group. This implementation sets the
     * visible state for all radio buttons in the group.
     *
     * @param f the new visible state
     */
    public void setVisible(boolean f)
    {
        for (AbstractButton b : buttons)
        {
            b.setVisible(f);
        }
    }

    /**
     * Returns the font of this radio group. This implementation returns the
     * font of the first button.
     *
     * @return the font of this widget
     */
    public Object getFont()
    {
        return SwingComponentUtils.getFont(buttons[0]);
    }

    /**
     * Sets the font for this radio group. This implementation sets the font for
     * all buttons in the group.
     *
     * @param font the font to be set
     */
    public void setFont(Object font)
    {
        for (AbstractButton b : buttons)
        {
            SwingComponentUtils.setFont(b, font);
        }
    }
}

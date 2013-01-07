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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;

import net.sf.jguiraffe.gui.builder.components.ColorHelper;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code SwingRadioGroupWidgetHandler}.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingRadioGroupWidgetHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingRadioGroupWidgetHandler
{
    /** Constant for the tool tip for the radio buttons. */
    private static final String RADIO_TIP = "Tool tip for radio %d";

    /** Constant for the tool tip of the group. */
    private static final String GROUP_TIP = "Tool tip of the group.";

    /** Constant for the combined tool tip. */
    private static final String COMBINED_TIP = SwingComponentUtils
            .toHtml(RADIO_TIP + "\n" + GROUP_TIP);

    /** Constant for the number of test radio buttons. */
    private static final int RADIO_COUNT = 4;

    /** Stores the test radio buttons. */
    private JRadioButton[] radios;

    /** A test font object. */
    private Font font;

    /** The handler to be tested. */
    private SwingRadioGroupWidgetHandler handler;

    @Before
    public void setUp() throws Exception
    {
        radios = new JRadioButton[RADIO_COUNT];
        ButtonGroup grp = new ButtonGroup();
        for (int i = 0; i < RADIO_COUNT; i++)
        {
            radios[i] = new JRadioButton();
            grp.add(radios[i]);
        }
        handler = new SwingRadioGroupWidgetHandler(grp);
        assertEquals("Wrong button group", grp, handler.getButtonGroup());
    }

    /**
     * Returns the test font. It is created on demand.
     *
     * @return the test font
     */
    private Font getTestFont()
    {
        if (font == null)
        {
            font = new Font("Monospace", Font.BOLD, 11);
        }
        return font;
    }

    /**
     * Tests whether the correct background color is returned.
     */
    @Test
    public void testGetBackgroundColor()
    {
        radios[0].setBackground(Color.GREEN);
        assertEquals("Wrong background color", ColorHelper.NamedColor.GREEN
                .getColor(), handler.getBackgroundColor());
    }

    /**
     * Tests whether the correct foreground color is returned.
     */
    @Test
    public void testGetForegroundColor()
    {
        radios[0].setForeground(Color.PINK);
        assertEquals("Wrong foreground color", ColorHelper.NamedColor.PINK
                .getColor(), handler.getForegroundColor());
    }

    /**
     * Tests whether the background color can be changed.
     */
    @Test
    public void testSetBackgroundColor()
    {
        handler.setBackgroundColor(ColorHelper.NamedColor.BLUE.getColor());
        int idx = 0;
        for (JRadioButton r : radios)
        {
            assertEquals("Wrong background color at " + idx, Color.BLUE, r
                    .getBackground());
            idx++;
        }
    }

    /**
     * Tests setBackgroundColor() if null is passed in.
     */
    @Test
    public void testSetBackgroundColorNull()
    {
        for (JRadioButton r : radios)
        {
            r.setBackground(Color.YELLOW);
        }
        handler.setBackgroundColor(null);
        for (JRadioButton r : radios)
        {
            assertEquals("Color was changed", Color.YELLOW, r.getBackground());
        }
    }

    /**
     * Tests whether the foreground color can be changed.
     */
    @Test
    public void testSetForegroundColor()
    {
        handler.setForegroundColor(ColorHelper.NamedColor.BLUE.getColor());
        int idx = 0;
        for (JRadioButton r : radios)
        {
            assertEquals("Wrong foreground color at " + idx, Color.BLUE, r
                    .getForeground());
            idx++;
        }
    }

    /**
     * Tests setBackgroundColor() if null is passed in.
     */
    @Test
    public void testSetForegroundColorNull()
    {
        for (JRadioButton r : radios)
        {
            r.setForeground(Color.YELLOW);
        }
        handler.setForegroundColor(null);
        for (JRadioButton r : radios)
        {
            assertEquals("Color was changed", Color.YELLOW, r.getForeground());
        }
    }

    /**
     * Tests whether the visible state of the radio group can be queried if the
     * group is visible.
     */
    @Test
    public void testIsVisibleTrue()
    {
        radios[0].setVisible(true);
        radios[1].setVisible(false);
        assertTrue("Not visible", handler.isVisible());
    }

    /**
     * Tests whether the visible state of the radio group can be queried if it
     * is not visible.
     */
    @Test
    public void testIsVisibleFalse()
    {
        radios[0].setVisible(false);
        radios[1].setVisible(true);
        assertFalse("Visible", handler.isVisible());
    }

    /**
     * Helper method for testing the visible status of the buttons in the group.
     *
     * @param expected the expected status
     */
    private void checkVisibleState(boolean expected)
    {
        int idx = 0;
        for (JRadioButton r : radios)
        {
            assertEquals("Wrong visible status at " + idx, expected, r
                    .isVisible());
            idx++;
        }
    }

    /**
     * Tests whether the visible state of the group can be changed.
     */
    @Test
    public void testSetVisible()
    {
        handler.setVisible(true);
        checkVisibleState(true);
        handler.setVisible(false);
        checkVisibleState(false);
    }

    /**
     * Prepares a tool tip for a test radio button. The index is added to the
     * tip.
     *
     * @param tip the tip
     * @param index the index
     * @return the formatted tool tip
     */
    private static String formatTip(String tip, int index)
    {
        return String.format(tip, index);
    }

    /**
     * Helper method for initializing the tool tips for the test radio buttons.
     */
    private void initToolTips()
    {
        int idx = 0;
        for (JRadioButton r : radios)
        {
            r.setToolTipText(formatTip(RADIO_TIP, idx++));
        }
    }

    /**
     * Helper method for testing the tool tips of the buttons in the group.
     *
     * @param expected the expected tool tip; can contain a place holder which
     *        will be replaced by the button index
     */
    private void checkToolTips(String expected)
    {
        int idx = 0;
        for (JRadioButton r : radios)
        {
            String tip = formatTip(expected, idx);
            assertEquals("Wrong tip at " + idx,
                    SwingComponentUtils.toHtml(tip), r.getToolTipText());
            idx++;
        }
    }

    /**
     * Tests whether the group's tool tip can be queried if it is undefined.
     */
    @Test
    public void testGetToolTipUndefined()
    {
        assertNull("Got a tool tip", handler.getToolTip());
    }

    /**
     * Tests whether the group's tool tip can be set if there are no tips for
     * the radio buttons.
     */
    @Test
    public void testSetToolTipGroupOnly()
    {
        handler.setToolTip(GROUP_TIP);
        checkToolTips(GROUP_TIP);
        assertEquals("Wrong group tip", GROUP_TIP, handler.getToolTip());
    }

    /**
     * Tests whether the tool tip of the group is combined with the tips of the
     * radio buttons.
     */
    @Test
    public void testSetToolTipCombined()
    {
        initToolTips();
        handler.setToolTip(GROUP_TIP);
        checkToolTips(COMBINED_TIP);
        assertEquals("Wrong group tip", GROUP_TIP, handler.getToolTip());
    }

    /**
     * Tests whether the radio button's tool tips are correctly handled if the
     * group's tip is reset to null.
     */
    @Test
    public void testSetToolTipGroupNull()
    {
        initToolTips();
        handler.setToolTip(GROUP_TIP);
        handler.setToolTip(null);
        checkToolTips(RADIO_TIP);
    }

    /**
     * Tests whether the radio button's tool tips are correctly handled if the
     * group's tip is reset to an empty string.
     */
    @Test
    public void testSetToolTipGroupEmpty()
    {
        initToolTips();
        handler.setToolTip(GROUP_TIP);
        handler.setToolTip("");
        checkToolTips(RADIO_TIP);
    }

    /**
     * Tests setToolTip() if the tip of a radio button has changed in the mean
     * time.
     */
    @Test
    public void testSetToolTipRadioChanged()
    {
        initToolTips();
        handler.setToolTip(GROUP_TIP);
        final String changedTip = "New tool tip for radio button";
        radios[1].setToolTipText(changedTip);
        handler.setToolTip(null);
        assertEquals("Changed tip not detected", changedTip, radios[1]
                .getToolTipText());
    }

    /**
     * Tests whether the correct widget is returned.
     */
    @Test
    public void testGetWidget()
    {
        assertEquals("Wrong widget", handler.getButtonGroup(), handler
                .getWidget());
    }

    /**
     * Tests whether the correct font is returned.
     */
    @Test
    public void testGetFont()
    {
        radios[0].setFont(getTestFont());
        assertEquals("Wrong font", getTestFont(), handler.getFont());
    }

    /**
     * Tests whether the font can be set.
     */
    @Test
    public void testSetFont()
    {
        handler.setFont(getTestFont());
        for (JComponent c : radios)
        {
            assertEquals("Wrong font", getTestFont(), c.getFont());
        }
    }
}

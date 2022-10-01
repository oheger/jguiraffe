/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.sf.jguiraffe.gui.builder.components.Color;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingWidgetHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingWidgetHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingWidgetHandler
{
    /** Constant for a logic test color. */
    private static final Color LOGIC_COLOR = Color.newInstance(64, 128, 255);

    /** Constant for the corresponding Swing color. */
    private static final java.awt.Color SWING_COLOR = new java.awt.Color(64,
            128, 255);

    /** Stores the underlying component. */
    private JComponent component;

    /** A test font. */
    private Font font;

    /** Stores the handler to be tested. */
    private SwingWidgetHandler handler;

    @Before
    public void setUp() throws Exception
    {
        component = new JLabel();
        handler = new SwingWidgetHandler(component);
    }

    /**
     * Returns a font that can be used for testing. It is created on demand.
     *
     * @return the test font
     */
    private Font getTestFont()
    {
        if (font == null)
        {
            font = new Font("Serif", Font.ITALIC, 18);
        }
        return font;
    }

    /**
     * Tests whether the underlying widget can be accessed.
     */
    @Test
    public void testGetWidget()
    {
        assertEquals("Wrong widget returned", component, handler.getWidget());
    }

    /**
     * Tests whether the widget's background color can be queried.
     */
    @Test
    public void testGetBackgroundColor()
    {
        component.setBackground(SWING_COLOR);
        assertEquals("Wrong background color", LOGIC_COLOR, handler
                .getBackgroundColor());
    }

    /**
     * Tests whether the widget's background color can be set.
     */
    @Test
    public void testSetBackgroundColor()
    {
        handler.setBackgroundColor(LOGIC_COLOR);
        assertEquals("Background color was not set", SWING_COLOR, component
                .getBackground());
    }

    /**
     * Tests setting the background color to null. This should be a noop.
     */
    @Test
    public void testSetBackgroundColorNull()
    {
        component.setBackground(SWING_COLOR);
        handler.setBackgroundColor(null);
        assertEquals("Color was changed", SWING_COLOR, component
                .getBackground());
    }

    /**
     * Tests whether the widget's foreground color can be queried.
     */
    @Test
    public void testGetForegroundColor()
    {
        component.setForeground(SWING_COLOR);
        assertEquals("Wrong foreground color", LOGIC_COLOR, handler
                .getForegroundColor());
    }

    /**
     * Tests whether the widget's foreground color can be set.
     */
    @Test
    public void testSetForegroundColor()
    {
        handler.setForegroundColor(LOGIC_COLOR);
        assertEquals("Foreground color was not set", SWING_COLOR, component
                .getForeground());
    }

    /**
     * Tests setting the foreground color to null. This should not have any
     * effect.
     */
    @Test
    public void testSetForegroundColorNull()
    {
        component.setForeground(SWING_COLOR);
        handler.setForegroundColor(null);
        assertEquals("Foreground color was changed", SWING_COLOR, component
                .getForeground());
    }

    /**
     * Tests querying the widget's visible flag.
     */
    @Test
    public void testIsVisible()
    {
        component.setVisible(false);
        assertFalse("Wrong visible flag false", handler.isVisible());
        component.setVisible(true);
        assertTrue("Wrong visible flag true", handler.isVisible());
    }

    /**
     * Tests setting the widget's visible flag.
     */
    @Test
    public void testSetVisible()
    {
        handler.setVisible(false);
        assertFalse("Visible flag not set to false", component.isVisible());
        handler.setVisible(true);
        assertTrue("Visible flag not set to true", component.isVisible());
    }

    /**
     * Tests whether the tool tip of a component can be queried.
     */
    @Test
    public void testGetToolTip()
    {
        assertNull("Got a tool tip", handler.getToolTip());
        String tip = "My test tool tip!";
        component.setToolTipText(tip);
        assertEquals("Wrong tool tip", tip, handler.getToolTip());
    }

    /**
     * Tests whether the tool tip can be set through the handler.
     */
    @Test
    public void testSetToolTip()
    {
        String tip = "another test tool tip...";
        handler.setToolTip(tip);
        assertEquals("Tip not set", tip, component.getToolTipText());
        handler.setToolTip(null);
        assertNull("Got a tool tip", component.getToolTipText());
    }

    /**
     * Tests whether a tool tip can be set which requires HTML code.
     */
    @Test
    public void testSetToolTipHtml()
    {
        handler.setToolTip("A\nmultiline\ntool tip.");
        assertEquals("Wrong tip", "<html>A<br>multiline<br>tool tip.</html>",
                component.getToolTipText());
    }

    /**
     * Tests whether the font can be queried.
     */
    @Test
    public void testGetFont()
    {
        component.setFont(getTestFont());
        assertEquals("Wrong font", getTestFont(), handler.getFont());
    }

    /**
     * Tests whether the font can be set.
     */
    @Test
    public void testSetFont()
    {
        handler.setFont(getTestFont());
        assertEquals("Wrong font", getTestFont(), component.getFont());
    }
}

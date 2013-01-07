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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.builder.components.ColorHelper;
import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for {@code SwingComponentUtils}. This test class only tests a
 * subset of the functionality provided by the utility class; other methods are
 * tested implicitly by other test classes.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingComponentUtils.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingComponentUtils
{
    /** Constant for a logic test color. */
    private static final Color LOGIC_COLOR = Color.newInstance(128, 64, 192);

    /** Constant for a Swing test color. */
    private static final java.awt.Color SWING_COLOR = new java.awt.Color(128,
            64, 192);

    /** An array with AWT colors corresponding to named JGUIraffe colors. */
    private static java.awt.Color[] awtColors;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        awtColors = new java.awt.Color[ColorHelper.NamedColor.values().length];
        Class<java.awt.Color> colClass = java.awt.Color.class;
        int idx = 0;
        for (ColorHelper.NamedColor nc : ColorHelper.NamedColor.values())
        {
            Field fld = colClass.getField(nc.name());
            awtColors[idx++] = (java.awt.Color) fld.get(null);
        }
    }

    /**
     * Tests converting a Swing color into a logic color.
     */
    @Test
    public void testSwing2LogicColor()
    {
        assertEquals("Wrong logic color", LOGIC_COLOR, SwingComponentUtils
                .swing2LogicColor(SWING_COLOR));
    }

    /**
     * Tests whether null Swing colors are correctly handled.
     */
    @Test
    public void testSwing2LogicColorNull()
    {
        assertNull("Non null result for null Swing color", SwingComponentUtils
                .swing2LogicColor(null));
    }

    /**
     * Tests converting a logic color into a Swing color.
     */
    @Test
    public void testLogic2SwingColor()
    {
        assertEquals("Wrong Swing color", SWING_COLOR, SwingComponentUtils
                .logic2SwingColor(LOGIC_COLOR));
    }

    /**
     * Tests whether null logic colors are correctly handled.
     */
    @Test
    public void testLogic2SwingColorNull()
    {
        assertNull("Non null result for null logic color", SwingComponentUtils
                .logic2SwingColor(null));
    }

    /**
     * Tests whether all named colors can be converted to logic colors.
     */
    @Test
    public void testSwing2LogicColorNamed()
    {
        for (int i = 0; i < awtColors.length; i++)
        {
            assertEquals("Wrong color at " + i,
                    ColorHelper.NamedColor.values()[i].getColor(),
                    SwingComponentUtils.swing2LogicColor(awtColors[i]));
        }
    }

    /**
     * Tests whether all named logic colors can be converted to AWT colors.
     */
    @Test
    public void testLogic2SwingColorNamed()
    {
        for (int i = 0; i < awtColors.length; i++)
        {
            assertEquals("Wrong color at " + i, awtColors[i],
                    SwingComponentUtils.logic2SwingColor(ColorHelper.NamedColor
                            .values()[i].getColor()));
        }
    }

    /**
     * Tests whether a character can be converted to a mnemonic code.
     */
    @Test
    public void testToMnemonic()
    {
        assertEquals("Wrong code", KeyEvent.VK_O, SwingComponentUtils
                .toMnemonic('O'));
        assertEquals("Wrong code for lower case character", KeyEvent.VK_O,
                SwingComponentUtils.toMnemonic('o'));
    }

    /**
     * Tests whether a scroll pane can be created if no preferred size is
     * specified. As we do not want to duplicate the algorithm in the test we
     * just check whether the calculated size is in the expected direction.
     */
    @Test
    public void testScrollPaneForNoPreferredSize()
    {
        JTextArea comp = new JTextArea(40, 8);
        Dimension d = comp.getPreferredScrollableViewportSize();
        JScrollPane scr = SwingComponentUtils.scrollPaneFor(comp, 0, -1);
        Dimension dscr = scr.getPreferredSize();
        int deltaX = dscr.width - d.width
                - scr.getVerticalScrollBar().getPreferredSize().width;
        int deltaY = dscr.height - d.height
                - scr.getHorizontalScrollBar().getPreferredSize().height;
        assertTrue("Wrong deltaX: " + deltaX, deltaX < 10);
        assertTrue("Wrong deltaY: " + deltaY, deltaY < 10);
        dscr = scr.getMinimumSize();
    }

    /**
     * Tests whether the preferred size is taken into account when creating the
     * scroll pane.
     */
    @Test
    public void testScrollPaneForWithPreferredSize()
    {
        JTextArea comp = new JTextArea(40, 8);
        Dimension d = comp.getPreferredScrollableViewportSize();
        Dimension d2 = new Dimension(d.width * 2, d.height * 2);
        JScrollPane scr = SwingComponentUtils.scrollPaneFor(comp, d2.width,
                d2.height);
        assertEquals("Wrong preferred size", d2, scr.getPreferredSize());
    }

    /**
     * Creates a font that can be used for tests.
     *
     * @return the test font
     */
    private Font createFont()
    {
        return new Font("Serif", Font.ITALIC, 20);
    }

    /**
     * Tests whether a font can be queried.
     */
    @Test
    public void testGetFont()
    {
        JLabel comp = new JLabel();
        Font ft = createFont();
        comp.setFont(ft);
        assertEquals("Wrong font", ft, SwingComponentUtils.getFont(comp));
    }

    /**
     * Tests whether a font can be set.
     */
    @Test
    public void testSetFont()
    {
        JLabel comp = new JLabel();
        Font ft = createFont();
        SwingComponentUtils.setFont(comp, ft);
        assertEquals("Wrong font", ft, comp.getFont());
    }

    /**
     * Tests whether the font can be set to null.
     */
    @Test
    public void testSetFontNull()
    {
        JLabel comp = new JLabel();
        comp.setFont(createFont());
        SwingComponentUtils.setFont(comp, null);
        assertFalse("Got a font", comp.isFontSet());
    }

    /**
     * Tries to set an invalid font object.
     */
    @Test(expected = FormBuilderRuntimeException.class)
    public void testSetFontInvalid()
    {
        JLabel comp = new JLabel();
        SwingComponentUtils.setFont(comp, this);
    }
}

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
package net.sf.jguiraffe.gui.platform.swing.layout;

import static org.junit.Assert.assertEquals;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for {@link SwingSizeHandler}.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingSizeHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingSizeHandler
{
    /** Constant for the test font sizes. */
    private static final double[] FONT_SIZES = {
            20, 15
    };

    /** Constant for the delta for floating point comparisons. */
    private static final double DELTA = 0.0005;

    /** The test font. */
    private static Font font;

    /** A component with this font. */
    private static JComponent component;

    /** The handler to be tested. */
    private SwingSizeHandlerTestImpl handler;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        font = new Font("Monospaced", 0, 12);
    }

    @Before
    public void setUp() throws Exception
    {
        handler = new SwingSizeHandlerTestImpl();
        component = new JLabel();
        component.setFont(font);
    }

    /**
     * Tests whether the font sizes are correctly calculated.
     */
    @Test
    public void testCalculateFontSizes()
    {
        double[] sizes = handler.calculateFontSizes(font, component);
        FontMetrics fm = component.getFontMetrics(font);
        assertEquals("Wrong width", fm.charWidth('a'), sizes[0], 0.005);
        assertEquals("Wrong height", fm.getHeight(), sizes[1], 0.005);
    }

    /**
     * Tests whether the X size of a font can be queried.
     */
    @Test
    public void testGetFontSizeX()
    {
        handler.sizes = FONT_SIZES;
        assertEquals("Wrong X font size", FONT_SIZES[0], handler.getFontSize(
                component, false), DELTA);
    }

    /**
     * Tests whether the Y size of a font can be queried.
     */
    @Test
    public void testGetFontSizeY()
    {
        handler.sizes = FONT_SIZES;
        assertEquals("Wrong Y font size", FONT_SIZES[1], handler.getFontSize(
                component, true), DELTA);
    }

    /**
     * Tests whether the font sizes are cached.
     */
    @Test
    public void testGetFontSizeCached()
    {
        handler.sizes = FONT_SIZES;
        assertEquals("Wrong X font size", FONT_SIZES[0], handler.getFontSize(
                component, false), DELTA);
        handler.sizes = new double[] {
                99, 88
        };
        assertEquals("X font size not cached", FONT_SIZES[0], handler
                .getFontSize(component, false), DELTA);
        assertEquals("Y font size not cached", FONT_SIZES[1], handler
                .getFontSize(component, true), DELTA);
    }

    /**
     * Tests getFontSize() for a null component. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetFontSizeNull()
    {
        handler.getFontSize(null, true);
    }

    /**
     * Tests getFontSize() if no Component is passed in. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetFontSizeOtherObject()
    {
        handler.getFontSize(this, false);
    }

    /**
     * Tests whether the correct screen resolution is returned. Unfortunately,
     * we can here only duplicate the logic for querying the screen resolution
     * using the Toolkit class.
     */
    @Test
    public void testGetScreenResolution()
    {
        int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
        assertEquals("Wrong screen resolution", screenRes, handler
                .getScreenResolution());
    }

    /**
     * A test implementation of SwingSizeHandler.
     */
    @SuppressWarnings("serial")
    private static class SwingSizeHandlerTestImpl extends SwingSizeHandler
    {
        /** A mock result for calculateFontSizes(). */
        double[] sizes;

        /**
         * Returns the mock result or calls the super method.
         */
        @Override
        double[] calculateFontSizes(Font font, Component comp)
        {
            assertEquals("Wrong component", component, comp);
            assertEquals("Wrong font", TestSwingSizeHandler.font, font);
            return (sizes != null) ? sizes : super.calculateFontSizes(font,
                    comp);
        }
    }
}

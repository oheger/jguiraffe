/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A class with utility methods for dealing with Swing components.
 * </p>
 * <p>
 * This class is used internally by implementations for Swing widgets. It
 * provides functionality for accessing certain properties of Swing components
 * and converting between Swing-specific data types and platform-independent
 * types.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingComponentUtils.java 205 2012-01-29 18:29:57Z oheger $
 */
final class SwingComponentUtils
{
    /** Constant for the HTML start tag. */
    private static final String HTML_START = "<html>";

    /** Constant for the HTML end tag. */
    private static final String HTML_END = "</html>";

    /** Constant for the "br" tag. */
    private static final String TAG_BR = "<br>";

    /** Constant for the line break string. */
    private static final String LF = "\n";

    /** The logger. */
    private static final Log LOG = LogFactory.getLog(SwingComponentUtils.class);

    /**
     * Private constructor so that no instances can be created.
     */
    private SwingComponentUtils()
    {
    }

    /**
     * Converts the passed in Swing (or AWT) color into a platform-independent
     * logic color. <b>null</b> values are allowed as input values, the result
     * will then be <b>null</b>, too.
     *
     * @param color the Swing color object
     * @return the corresponding logic color object
     */
    public static Color swing2LogicColor(java.awt.Color color)
    {
        return (color != null) ? Color.newRGBInstance(color.getRed(), color
                .getGreen(), color.getBlue()) : null;
    }

    /**
     * Converts the passed in logic color description into a Swing (or AWT)
     * specific color object. If the color is not supported (if it is based on a
     * logic color definition), this method returns <b>null</b>. <b>null</b>
     * values are allowed as input values, the result will then be <b>null</b>,
     * too.
     *
     * @param color the logic color description
     * @return the corresponding Swing color object
     */
    public static java.awt.Color logic2SwingColor(Color color)
    {
        if (color != null)
        {
            if (!color.isLogicColor())
            {
                return new java.awt.Color(color.getRed(), color.getGreen(),
                        color.getBlue());
            }
            else
            {
                LOG.warn("Cannot convert color: " + color
                        + "! Logic colors are not supported by Swing.");
            }
        }

        return null;
    }

    /**
     * Returns the background color of the specified component.
     *
     * @param component the component
     * @return the background color of this component
     */
    public static Color getBackgroundColor(JComponent component)
    {
        return swing2LogicColor(component.getBackground());
    }

    /**
     * Returns the foreground color of the specified component.
     *
     * @param component the component
     * @return the foreground color of this component
     */
    public static Color getForegroundColor(JComponent component)
    {
        return swing2LogicColor(component.getForeground());
    }

    /**
     * Sets the background color of the specified component. The passed in
     * platform independent {@code Color} object will be transformed into an AWT
     * color object (if possible). If the color is <b>null</b>, this method has
     * no effect.
     *
     * @param component the component
     * @param c the new background color
     */
    public static void setBackgroundColor(JComponent component, Color c)
    {
        java.awt.Color convertedColor = logic2SwingColor(c);
        if (convertedColor != null)
        {
            component.setBackground(convertedColor);
        }
    }

    /**
     * Sets the foreground color of the specified component. The passed in
     * platform independent {@code Color} object will be transformed into an AWT
     * color object (if possible). If the color is <b>null</b>, this method has
     * no effect.
     *
     * @param component the component
     * @param c the new foreground color
     */
    public static void setForegroundColor(JComponent component, Color c)
    {
        java.awt.Color convertedColor = logic2SwingColor(c);
        if (convertedColor != null)
        {
            component.setForeground(convertedColor);
        }
    }

    /**
     * Returns the font object of the specified component.
     *
     * @param component the component
     * @return the font object of this component
     */
    public static Object getFont(JComponent component)
    {
        return component.getFont();
    }

    /**
     * Sets the font object for the specified component. This implementation
     * checks whether the specified font object is of type {@code java.awt.Font}
     * . If not, a runtime exception is thrown.
     *
     * @param component the component
     * @param font the font to be set
     * @throws FormBuilderRuntimeException if the font object is invalid
     */
    public static void setFont(JComponent component, Object font)
    {
        try
        {
            component.setFont((Font) font);
        }
        catch (ClassCastException ccex)
        {
            throw new FormBuilderRuntimeException(
                    "Cannot cast object to Font: " + font, ccex);
        }
    }

    /**
     * Returns the tool tip of the associated component.
     *
     * @param component the component
     * @return the tool tip
     */
    public static String getToolTip(JComponent component)
    {
        return component.getToolTipText();
    }

    /**
     * Sets the tool tip of the specified component. If required, the text of
     * the tip is transformed to HTML first.
     *
     * @param component the component
     * @param tip the new tool tip text
     */
    public static void setToolTip(JComponent component, String tip)
    {
        component.setToolTipText(toHtml(tip));
    }

    /**
     * Sets a tool tip of the specified component that is a concatenation of two
     * strings. Some components use tool tips combined of two components. This
     * method combines the components and sets the tool tip text accordingly.
     * One or both components of the tip can be <b>null</b> or empty strings. In
     * these case no separator is placed in the resulting string.
     *
     * @param component the component
     * @param tip1 component 1 of the tool tip
     * @param tip2 component 2 of the tool tip
     * @param separator the separator between the tip components
     */
    public static void setToolTip(JComponent component, String tip1,
            String tip2, String separator)
    {
        component.setToolTipText(combineToolTips(tip1, tip2, separator));
    }

    /**
     * Combines two tool tips. Some components use tool tips combined of two
     * components. This method generates such a combined tool tip by creating
     * the concatenation of the passed in strings. The strings can be
     * <b>null</b> or empty; then no separator is placed in the resulting
     * string. The resulting combined tip is also converted to HTML if necessary
     * by calling {@link #toHtml(String)}.
     *
     * @param tip1 component 1 of the tool tip
     * @param tip2 component 2 of the tool tip
     * @param separator the separator between the tip components
     * @return the combined tool tip
     */
    public static String combineToolTips(String tip1, String tip2,
            String separator)
    {
        String tip;
        if (StringUtils.isEmpty(tip1))
        {
            tip = tip2;
        }
        else if (StringUtils.isEmpty(tip2))
        {
            tip = tip1;
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            buf.append(tip1);
            buf.append(separator);
            buf.append(tip2);
            tip = buf.toString();
        }

        return toHtml(tip);
    }

    /**
     * Transforms the specified string to an HTML string. Swing components can
     * deal with HTML code. This method is called if HTML may be required to
     * display a text correctly, e.g. because it contains line breaks. This
     * implementation checks whether a transformation to HTML is necessary. If
     * this is the case, HTML code is returned. Otherwise, result is the same
     * text as passed in. We check here for the existence of line breaks. If
     * some are found, HTML code is returned which contains {@code <br>} tags for the
     * line breaks.
     *
     * @param s the string to be processed (can be <b>null</b>)
     * @return the processed string
     */
    public static String toHtml(String s)
    {
        if (s != null)
        {
            if (s.indexOf(LF) >= 0)
            {
                StringBuilder buf = new StringBuilder();
                buf.append(HTML_START);
                buf.append(s.replace(LF, TAG_BR));
                buf.append(HTML_END);
                return buf.toString();
            }
        }

        return s;
    }

    /**
     * Converts the specified character to a mnemonic code. The Swing key codes
     * {@code VK_A} to {@code VK_Z} are only defined on upper case letters. So
     * this implementation converts the given character to upper case. Note that
     * this might not work out for all languages and codes.
     *
     * @param c the mnemonic character
     * @return the key code to use
     */
    public static int toMnemonic(char c)
    {
        return Character.toUpperCase(c);
    }

    /**
     * Creates a scroll pane for the specified component. The preferred with of
     * the scroll pane is properly initialized. If values greater than 0 are
     * provided for the scroll width and scroll height, these values are set for
     * the scroll pane's preferred width. Otherwise, the the preferred width is
     * determined based on the preferred viewport size of the component.
     *
     * @param comp the component to be added to the scroll pane
     * @param scrWidth the preferred scroll width (&lt;= 0 for undefined)
     * @param scrHeight the preferred scroll height (&lt;= 0 for undefined)
     * @return the scroll pane
     */
    public static JScrollPane scrollPaneFor(Scrollable comp, int scrWidth,
            int scrHeight)
    {
        JScrollPane scr = new JScrollPane((Component) comp);
        Dimension prefSize;
        Insets is;

        if (scrWidth <= 0 || scrHeight <= 0)
        {
            is = scr.getInsets();
            prefSize = comp.getPreferredScrollableViewportSize();
        }
        else
        {
            is = null;
            prefSize = new Dimension();
        }

        if (scrWidth <= 0)
        {
            prefSize.width += scr.getVerticalScrollBar().getPreferredSize()
                    .getWidth()
                    + is.left + is.right;
        }
        else
        {
            prefSize.width = scrWidth;
        }

        if (scrHeight <= 0)
        {
            prefSize.height += scr.getHorizontalScrollBar().getPreferredSize()
                    .getHeight()
                    + is.top + is.bottom;
        }
        else
        {
            prefSize.height = scrHeight;
        }

        scr.setPreferredSize(prefSize);
        return scr;
    }
}

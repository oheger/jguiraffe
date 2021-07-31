/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.util.Collection;

import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.builder.components.ColorHelper;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A specific container tag implementation that creates a panel.
 * </p>
 * <p>
 * This container tag will create the most simple container. The following table
 * lists the attributes that are provided in addition to the ones supported for
 * simple components.
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">text</td>
 * <td>Defines a text for the panel. This text will be displayed as a caption
 * over the elements that are contained in the panel.</td>
 * </td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valing="top">textres</td>
 * <td>Allows to specify the panel's text as a resource.</td>
 * <td>yes</td>
 * </tr>
 * <tr>
 * <td valign="top">resgrp</td>
 * <td>Allows to define a special resource group for resolving the text
 * resource. If undefined, the default resource group for the form builder will
 * be used.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">textColor</td>
 * <td>With this attribute a color for the text can be set. It allows color
 * definitions in a format supported by the
 * {@link net.sf.jguiraffe.gui.builder.components.ColorHelper ColorHelper}
 * class. Note that not all builder implementations may support a colored title
 * text.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">textFontRef</td>
 * <td>This attribute can be used to define the font for the panel's text. A
 * font with this name must exist in the Jelly context. Note that not all
 * builder implementations will support a font for the panel's text.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">border</td>
 * <td>This is a boolean attribute that allows to draw a border around the
 * panel. Note that if a text is specified, a border will always be drawn.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">borderref</td>
 * <td>If the <code>border</code> attribute is set to <b>true</b> or a text
 * is defined, a default border will be painted for this panel. There are some
 * GUI libraries (especially Swing), which support a rich set of different
 * border types. To allow an application to make use of this specific borders
 * the <code>borderref</code> attribute can be set to the name of a border
 * object that has been placed in the Jelly context. A builder implementation
 * that supports this mechanism will then retrieve this specified border and use
 * it.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PanelTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PanelTag extends ContainerTag
{
    /** Holds information about the panel's text. */
    private TextData textData;

    /** Stores the resolved font object. */
    private Object textFont;

    /** Stores the resolved text color object. */
    private Color color;

    /** Stores the text color. */
    private String textColor;

    /** Stores the text font reference. */
    private String textFontRef;

    /** Stores the border reference. */
    private String borderref;

    /** Stores the border attribute. */
    private boolean border;

    /**
     * Creates a new instance of <code>PanelTag</code>.
     */
    public PanelTag()
    {
        textData = new TextData(this);
    }

    /**
     * Returns the text data object for this tag.
     *
     * @return the text data for the panel's text
     */
    public TextData getTextData()
    {
        return textData;
    }

    /**
     * Setter method for the text attribute.
     *
     * @param s the attribute value
     */
    public void setText(String s)
    {
        getTextData().setText(s);
    }

    /**
     * Setter method for the textres attribute.
     *
     * @param s the attribute value
     */
    public void setTextres(String s)
    {
        getTextData().setTextres(s);
    }

    /**
     * Setter method for the resgrp attribute.
     *
     * @param s the attribute value
     */
    public void setResgrp(String s)
    {
        getTextData().setResgrp(s);
    }

    /**
     * Returns a flag whether this panel should have a border.
     *
     * @return the border flag
     */
    public boolean isBorder()
    {
        return border;
    }

    /**
     * Setter method of the border attribute.
     *
     * @param border the attribute value
     */
    public void setBorder(boolean border)
    {
        this.border = border;
    }

    /**
     * Returns the name of a predefined border to use.
     *
     * @return a border reference in the Jelly context
     */
    public String getBorderref()
    {
        return borderref;
    }

    /**
     * Setter method of the borderref attribute.
     *
     * @param borderref the attribute value
     */
    public void setBorderref(String borderref)
    {
        this.borderref = borderref;
    }

    /**
     * Returns the font for the panel's text (the title).
     *
     * @return the font for the title
     */
    public Object getTitleFont()
    {
        return textFont;
    }

    /**
     * Allows to specify the font for the panel's text (the title).
     *
     * @param font the title font
     */
    public void setTitleFont(Object font)
    {
        textFont = font;
    }

    /**
     * Returns the color for the title text.
     *
     * @return the text color
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * Setter method of the textColor attribute.
     *
     * @param textColor the attribute value
     */
    public void setTextColor(String textColor)
    {
        this.textColor = textColor;
    }

    /**
     * Setter method of the textFontRef attribute.
     *
     * @param textFontRef the attribute value
     */
    public void setTextFontRef(String textFontRef)
    {
        this.textFontRef = textFontRef;
    }

    /**
     * Creates the concrete container widget. This implementation creates a
     * panel using the component manager object.
     *
     * @param manager the manager
     * @param create the create flag
     * @param components a collection with the container's children
     * @return the newly created container
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is used incorrectly
     */
    @Override
    protected Object createContainer(ComponentManager manager, boolean create,
            Collection<Object[]> components) throws FormBuilderException,
            JellyTagException
    {
        if (create)
        {
            // Initialize some internal fields
            if (StringUtils.isNotEmpty(textFontRef))
            {
                Object font = getContext().getVariable(textFontRef);
                if (font == null)
                {
                    throw new FormBuilderException(
                            "Cannot find font with name " + textFontRef);
                }
                setTitleFont(font);
            }

            color = ColorHelper.resolveColor(textColor);
        }

        return manager.createPanel(this, create);
    }
}

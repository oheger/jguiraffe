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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.layout.ButtonLayout;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A specialized layout tag that creates a
 * <code>{@link net.sf.jguiraffe.gui.layout.ButtonLayout ButtonLayout}</code>.
 * </p>
 * <p>
 * All properties defining a button layout can be specified using attributes of
 * this tag. The following table lists all supported attributes:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">topMargin</td>
 * <td>Allows to define the top margin (a number with an optional unit).</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">leftMargin</td>
 * <td>Allows to define the left margin (a number with an optional unit).</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">rightMargin</td>
 * <td>Allows to define the right margin (a number with an optional unit).</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">bottomMargin</td>
 * <td>Allows to define the bottom margin (a number with an optional unit).</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">gap</td>
 * <td>Defines the gap between the buttons (a number with an optional unit).</td>
 * <td>yes</td>. </tr>
 * <tr>
 * <td valign="top">align</td>
 * <td>Defines the alignment of the button bar. This is a string with the
 * allowed values <em>Left</em>, <em>right</em>, or <em>center</em>
 * (case does not matter).</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ButtonLayoutTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ButtonLayoutTag extends LayoutTag
{
    /** A map for mapping alignment names to the corresponding constants. */
    private static final Map<String, ButtonLayout.Alignment> ALIGNMENT_MAPPING;

    /** Stores the topMargin. */
    private String topMargin;

    /** Stores the left margin. */
    private String leftMargin;

    /** Stores the right margin. */
    private String rightMargin;

    /** Stores the bottom margin. */
    private String bottomMargin;

    /** Stores the gap. */
    private String gap;

    /** Stores the alignment. */
    private String align;

    /** Stores the newly created button layout object. */
    private ButtonLayout buttonLayout;

    /**
     * Returns the alignment.
     *
     * @return the alignment
     */
    public String getAlign()
    {
        return align;
    }

    /**
     * Sets the alignment.
     *
     * @param align the alignment as string
     */
    public void setAlign(String align)
    {
        this.align = align;
    }

    /**
     * Returns the bottom margin.
     *
     * @return the bottom margin
     */
    public String getBottomMargin()
    {
        return bottomMargin;
    }

    /**
     * Sets the bottom margin.
     *
     * @param bottomMargin the bottom margin as string
     */
    public void setBottomMargin(String bottomMargin)
    {
        this.bottomMargin = bottomMargin;
    }

    /**
     * Returns the gap.
     *
     * @return the gap between the buttons
     */
    public String getGap()
    {
        return gap;
    }

    /**
     * Sets the gap.
     *
     * @param gap the gap between the buttons as string
     */
    public void setGap(String gap)
    {
        this.gap = gap;
    }

    /**
     * Returns the left margin.
     *
     * @return the left margin
     */
    public String getLeftMargin()
    {
        return leftMargin;
    }

    /**
     * Sets the left margin.
     *
     * @param leftMargin the left margin as string
     */
    public void setLeftMargin(String leftMargin)
    {
        this.leftMargin = leftMargin;
    }

    /**
     * Returns the right margin.
     *
     * @return the right margin
     */
    public String getRightMargin()
    {
        return rightMargin;
    }

    /**
     * Sets the right margin.
     *
     * @param rightMargin the right margin as string
     */
    public void setRightMargin(String rightMargin)
    {
        this.rightMargin = rightMargin;
    }

    /**
     * Returns the top margin.
     *
     * @return the top margin
     */
    public String getTopMargin()
    {
        return topMargin;
    }

    /**
     * Sets the top margin.
     *
     * @param topMargin the top margin as string
     */
    public void setTopMargin(String topMargin)
    {
        this.topMargin = topMargin;
    }

    /**
     * Returns the newly created button layout object.
     *
     * @return the button layout
     */
    public ButtonLayout getButtonLayout()
    {
        return buttonLayout;
    }

    /**
     * Creates the layout object.
     *
     * @param manager the component manager
     * @return the new layout object
     * @throws FormBuilderException if creation fails or invalid attribute
     * values are provided
     * @throws MissingAttributeException if required attributes are missing
     */
    @Override
    protected Object createLayout(ComponentManager manager)
            throws FormBuilderException, MissingAttributeException
    {
        buttonLayout = createButtonLayout();
        return manager.createButtonLayout(this);
    }

    /**
     * Creates the button layout object. This method is called by the
     * <code>createLayout()</code> method.
     *
     * @return the button layout object
     * @throws FormBuilderException if an error occurs
     */
    protected ButtonLayout createButtonLayout() throws FormBuilderException
    {
        ButtonLayout result = new ButtonLayout();
        if (StringUtils.isNotEmpty(getAlign()))
        {
            result.setAlignment(checkAlign(getAlign()));
        }

        NumberWithUnit n;
        n = convertToNumberWithUnit(getTopMargin());
        if (n != null)
        {
            result.setTopMargin(n);
        }
        n = convertToNumberWithUnit(getLeftMargin());
        if (n != null)
        {
            result.setLeftMargin(n);
        }
        n = convertToNumberWithUnit(getRightMargin());
        if (n != null)
        {
            result.setRightMargin(n);
        }
        n = convertToNumberWithUnit(getBottomMargin());
        if (n != null)
        {
            result.setBottomMargin(n);
        }
        n = convertToNumberWithUnit(getGap());
        if (n != null)
        {
            result.setGap(n);
        }

        return result;
    }

    /**
     * Checks the specified alignment string and returns the corresponding code.
     * If the alignment string is invalid, an exception will be thrown.
     *
     * @param al the alignment string
     * @return the alignment code
     * @throws FormBuilderException if the alignment string is invalid
     */
    private static ButtonLayout.Alignment checkAlign(String al)
            throws FormBuilderException
    {
        ButtonLayout.Alignment align = ALIGNMENT_MAPPING.get(al
                .toUpperCase(Locale.ENGLISH));
        if (align == null)
        {
            throw new FormBuilderException("Invalid alignment string: " + al);
        }

        return align;
    }

    static
    {
        ALIGNMENT_MAPPING = new HashMap<String, ButtonLayout.Alignment>();
        ALIGNMENT_MAPPING.put("LEFT", ButtonLayout.Alignment.LEFT);
        ALIGNMENT_MAPPING.put("RIGHT", ButtonLayout.Alignment.RIGHT);
        ALIGNMENT_MAPPING.put("CENTER", ButtonLayout.Alignment.CENTER);
    }
}

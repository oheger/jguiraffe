/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.layout.BorderLayout;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;

import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A special layout tag that creates a {@link BorderLayout} object.
 * </p>
 * <p>
 * All properties provided by the extended <code>BorderLayout</code> class can
 * be set using attributes of this tag. The following table lists all supported
 * attributes:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">leftMargin</td>
 * <td>Allows to define the left margin. This is a number with an optional unit.
 * </td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">topMargin</td>
 * <td>Allows to define the top margin. This is a number with an optional unit.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">rightMargin</td>
 * <td>Allows to define the right margin. This is a number with an optional
 * unit.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">bottomMargin</td>
 * <td>Allows to define the bottom margin. This is a number with an optional
 * unit.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">northGap</td>
 * <td>Allows to define the north gap, i.e. the space between the north and the
 * center cell.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">westGap</td>
 * <td>Allows to define the west gap, i.e. the space between the west and the
 * center cell.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">southGap</td>
 * <td>Allows to define the south gap, i.e. the space between the south and the
 * center cell.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">eastGap</td>
 * <td>Allows to define the east gap, i.e. the space between the east and the
 * center cell.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">canShrink</td>
 * <td>Sets the {@code canShrink} flag of the layout, i.e. the flag whether the
 * layout can shrink below its preferred size. Default value is <b>false</b>.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BorderLayoutTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class BorderLayoutTag extends LayoutTag
{
    /** Stores the left margin. */
    private String leftMargin;

    /** Stores the top margin. */
    private String topMargin;

    /** Stores the right margin. */
    private String rightMargin;

    /** Stores the bottom margin. */
    private String bottomMargin;

    /** Stores the north gap. */
    private String northGap;

    /** Stores the west gap. */
    private String westGap;

    /** Stores the south gap. */
    private String southGap;

    /** Stores the east gap. */
    private String eastGap;

    /** The shrink flag. */
    private boolean canShrink;

    /** Stores the new border layout object. */
    private BorderLayout borderLayout;

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
     * Returns the east gap.
     *
     * @return the east gap
     */
    public String getEastGap()
    {
        return eastGap;
    }

    /**
     * Sets the east gap.
     *
     * @param eastGap the east gap as string
     */
    public void setEastGap(String eastGap)
    {
        this.eastGap = eastGap;
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
     * Returns the north gap.
     *
     * @return the north gap
     */
    public String getNorthGap()
    {
        return northGap;
    }

    /**
     * Sets the north gap.
     *
     * @param northGap the north gap as string
     */
    public void setNorthGap(String northGap)
    {
        this.northGap = northGap;
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
     * Returns the south gap.
     *
     * @return the south gap
     */
    public String getSouthGap()
    {
        return southGap;
    }

    /**
     * Sets the south gap.
     *
     * @param southGap the south gap as string
     */
    public void setSouthGap(String southGap)
    {
        this.southGap = southGap;
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
     * Returns the west gap.
     *
     * @return the west gap
     */
    public String getWestGap()
    {
        return westGap;
    }

    /**
     * Sets the west gap.
     *
     * @param westGap the west gap as string
     */
    public void setWestGap(String westGap)
    {
        this.westGap = westGap;
    }

    /**
     * Returns the {@code canShrink} flag of the layout.
     *
     * @return the shrink flag
     */
    public boolean isCanShrink()
    {
        return canShrink;
    }

    /**
     * Set method of the {@code canShrink} attribute.
     *
     * @param canShrink the attribute's value
     */
    public void setCanShrink(boolean canShrink)
    {
        this.canShrink = canShrink;
    }

    /**
     * Returns the newly created border layout object.
     *
     * @return the layout object
     */
    public BorderLayout getBorderLayout()
    {
        return borderLayout;
    }

    /**
     * Creates the border layout object.
     *
     * @param manager the component manager
     * @return the new layout object
     * @throws FormBuilderException if the layout cannot be created or
     * attributes contain invalid values
     * @throws MissingAttributeException if required attributes are missing
     */
    @Override
    protected Object createLayout(ComponentManager manager)
            throws FormBuilderException, MissingAttributeException
    {
        borderLayout = createBorderLayout();
        return manager.createBorderLayout(this);
    }

    /**
     * Creates the border layout object based on this tag's attributes.
     *
     * @return the new border layout object
     * @throws FormBuilderException if an error occurs caused by invalid
     * attributes
     */
    protected BorderLayout createBorderLayout() throws FormBuilderException
    {
        BorderLayout layout = new BorderLayout();

        NumberWithUnit n;
        n = convertToNumberWithUnit(getLeftMargin());
        if (n != null)
        {
            layout.setLeftMargin(n);
        }
        n = convertToNumberWithUnit(getTopMargin());
        if (n != null)
        {
            layout.setTopMargin(n);
        }
        n = convertToNumberWithUnit(getRightMargin());
        if (n != null)
        {
            layout.setRightMargin(n);
        }
        n = convertToNumberWithUnit(getBottomMargin());
        if (n != null)
        {
            layout.setBottomMargin(n);
        }

        n = convertToNumberWithUnit(getNorthGap());
        if (n != null)
        {
            layout.setNorthGap(n);
        }
        n = convertToNumberWithUnit(getWestGap());
        if (n != null)
        {
            layout.setWestGap(n);
        }
        n = convertToNumberWithUnit(getSouthGap());
        if (n != null)
        {
            layout.setSouthGap(n);
        }
        n = convertToNumberWithUnit(getEastGap());
        if (n != null)
        {
            layout.setEastGap(n);
        }

        layout.setCanShrink(isCanShrink());
        return layout;
    }
}

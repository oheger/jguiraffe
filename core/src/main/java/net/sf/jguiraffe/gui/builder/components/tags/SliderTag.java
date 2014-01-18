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
import net.sf.jguiraffe.gui.builder.components.Orientation;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A specialized input component tag that defines a slider component.
 * </p>
 * <p>
 * A slider is an input component that allows the user to enter a numeric value
 * in a specified range by simply dragging a thumb to the desired position. The
 * slider can have horizontal or vertical orientation. Allowed values are
 * defined by the {@link Orientation} enumeration class. In addition to
 * the slider's range the space of minor and major ticks can be specified.
 * Further it can be configured whether labels and ticks should be painted. The
 * following table lists all attributes supported by this tag (of course, all of
 * the standard attributes are also allowed):
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">min</td>
 * <td>Defines the minimum value of this slider.</td>
 * <td valign="top">No</td>
 * </tr>
 * <tr>
 * <td valign="top">max</td>
 * <td>Defines the maximum value of this slider.</td>
 * <td valign="top">No</td>
 * </tr>
 * <tr>
 * <td valign="top">orientation</td>
 * <td>Using this attribute the slider's orientation can be specified. Possible
 * values are defined by the {@link Orientation} enumeration class (case
 * does not matter). The default is <em>HORIZONTAL</em>.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">majorTicks</td>
 * <td>This attribute specifies the spacing of major ticks (in values). This is
 * a big step when moving the slider.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">minorTicks</td>
 * <td>This attribute specifies the spacing of minor ticks (in values). It is
 * analogous to {@code majorTicks}, but defines a small step for the slider.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">showTicks</td>
 * <td>A boolean value which controls whether ticks should be painted. If set to
 * <b>true</b>, the slider draws a chart that corresponds to the spacing defined
 * by the {@code majorTicks} and {@code minorTicks} attributes.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">showLabels</td>
 * <td>A boolean value which controls whether the slider should draw labels. The
 * labels are shown at representative positions that correspond to the spacing
 * of the ticks. (Note that labels may not be supported by all UI platforms.)</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SliderTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SliderTag extends InputComponentTag
{
    /** The converted orientation of the slider. */
    private Orientation sliderOrientation;

    /** The orientation of the slider as string value. */
    private String orientation;

    /** The minimum value of the slider. */
    private int min;

    /** The maximum value of the slider. */
    private int max;

    /** The spacing for major ticks. */
    private int majorTicks;

    /** The spacing for minor ticks. */
    private int minorTicks;

    /** A flag whether ticks should be drawn. */
    private boolean showTicks;

    /** A flag whether labels should be drawn. */
    private boolean showLabels;

    /** A flag whether the minimum value was set. */
    private boolean minimumSet;

    /** A flag whether the maximum value was set. */
    private boolean maximumSet;

    /**
     * Returns the slider's orientation as string value. This is the string that
     * was set for the {@code orientation} attribute (it may not be a valid
     * orientation value).
     *
     * @return the orientation as string
     */
    public String getOrientation()
    {
        return orientation;
    }

    /**
     * Set method of the {@code orientation} attribute.
     *
     * @param orientation the attribute's value
     */
    public void setOrientation(String orientation)
    {
        this.orientation = orientation;
    }

    /**
     * Returns the minimum value of the slider.
     *
     * @return the minimum value
     */
    public int getMin()
    {
        return min;
    }

    /**
     * Set method of the {@code min} attribute.
     *
     * @param min the attribute's value
     */
    public void setMin(int min)
    {
        this.min = min;
        minimumSet = true;
    }

    /**
     * Returns the maximum value of the slider.
     *
     * @return the maximum value
     */
    public int getMax()
    {
        return max;
    }

    /**
     * Set method of the {@code max} attribute.
     *
     * @param max the attribute's value
     */
    public void setMax(int max)
    {
        this.max = max;
        maximumSet = true;
    }

    /**
     * Returns the spacing for major ticks.
     *
     * @return the major ticks spacing
     */
    public int getMajorTicks()
    {
        return majorTicks;
    }

    /**
     * Set method of the {@code majorTicks} attribute.
     *
     * @param majorTicks the attribute's value
     */
    public void setMajorTicks(int majorTicks)
    {
        this.majorTicks = majorTicks;
    }

    /**
     * Returns the spacing for minor ticks.
     *
     * @return the minor ticks spacing
     */
    public int getMinorTicks()
    {
        return minorTicks;
    }

    /**
     * Set method of the {@code minorTicks} attribute.
     *
     * @param minorTicks the attribute's value
     */
    public void setMinorTicks(int minorTicks)
    {
        this.minorTicks = minorTicks;
    }

    /**
     * Returns a flag whether ticks should be painted by the slider.
     *
     * @return a flag whether ticks should be painted
     */
    public boolean isShowTicks()
    {
        return showTicks;
    }

    /**
     * Set method of the {@code showTicks} attribute.
     *
     * @param showTicks the attribute's value
     */
    public void setShowTicks(boolean showTicks)
    {
        this.showTicks = showTicks;
    }

    /**
     * Returns a flag whether labels for values should be painted by the slider.
     *
     * @return a flag whether labels should be painted
     */
    public boolean isShowLabels()
    {
        return showLabels;
    }

    /**
     * Set method of the {@code showLabels} attribute.
     *
     * @param showLabels the attribute's value
     */
    public void setShowLabels(boolean showLabels)
    {
        this.showLabels = showLabels;
    }

    /**
     * Returns the {@code Orientation} value of the slider. The value of the
     * {@code orientation} attribute is transformed into an {@code Orientation}
     * instance by the {@link #processBeforeBody()} method if possible.
     * Otherwise, an exception is thrown.
     *
     * @return the {@code Orientation} value of the slider
     */
    public Orientation getSliderOrientation()
    {
        return sliderOrientation;
    }

    /**
     * Performs processing before evaluation of the tag body. This
     * implementation checks whether the required attributes are set and does
     * some additional validity checks.
     *
     * @throws JellyTagException if the tag is incorrectly used
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        super.processBeforeBody();

        if (!minimumSet)
        {
            throw new MissingAttributeException("min");
        }
        if (!maximumSet)
        {
            throw new MissingAttributeException("max");
        }
        if (getMin() >= getMax())
        {
            throw new JellyTagException("min must be less max!");
        }

        convertOrientation();
    }

    /**
     * Creates a {@code ComponentHandler} for the managed slider component.
     *
     * @param manager the {@code ComponentManager}
     * @param create the create flag
     * @return the handler for the newly created component
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is used incorrectly
     */
    @Override
    protected ComponentHandler<?> createComponentHandler(
            ComponentManager manager, boolean create)
            throws FormBuilderException, JellyTagException
    {
        return manager.createSlider(this, create);
    }

    /**
     * Converts the string value passed to the {@code orientation} attribute to
     * an instance of the {@code Orientation} class. If this fails, an exception
     * is thrown. This method is called at the beginning of tag processing.
     *
     * @throws FormBuilderException if the orientation attribute is invalid
     */
    private void convertOrientation() throws FormBuilderException
    {
        sliderOrientation = Orientation.getOrientation(getOrientation(),
                Orientation.HORIZONTAL);
    }
}

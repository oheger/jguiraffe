/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A special input component tag that defines a progress bar.
 * </p>
 * <p>
 * A progress bar can be used to give the user a visual feedback about a longer
 * lasting operation. This tag supports the typical properties of such a GUI
 * element. The following tables displays the allowed attributes:
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
 * <td>Defines the minimum value of the progress bar. The default is set to 0.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">max</td>
 * <td>Defines the maximum value of the progress bar. The value can be set
 * between the specified minimum and maximum values. If no maximum value is set,
 * the default value 100 is used.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">value</td>
 * <td>With this attribute the current value of the progress bar can be set.
 * Usually this property will be set during run time to display the progress of
 * the associated operation.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">allowText</td>
 * <td>With this boolean property it can be determined whether the component
 * should support a display text. If this is supported by the underlying GUI
 * library (which may not be the case), the text will be displayed over the
 * progress bar. The content of the text can be set using the <code>text</code>
 * or <code>textres</code> attributes, or at runtime through the component
 * handler.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">text</td>
 * <td>Defines the text of this component. This attribute has only effect if the
 * <code>allowText</code> attribute is set to <b>true</b>. In this case the
 * initial text can be set. Typically a text that corresponds to the current
 * value (e.g. a percent label) will be set during runtime.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">textres</td>
 * <td>Allows to define the component's text as a resource identifier.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">resgrp</td>
 * <td>If the text of the component is specified using a resource identifier,
 * with this attribute the corresponding resource group can be specified. If
 * undefined, the application's default resource group will be used.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">noField</td>
 * <td>Though a progress bar is an input component, it is typically not used for
 * gathering user input. Because of that it will no be added to a
 * <code>Form</code> object per default. If this default behavior is not
 * desired, this attribute must be manually set to <b>false</b>.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * The component handler that will be created for this component is of type
 * {@link net.sf.jguiraffe.gui.builder.components.model.ProgressBarHandler}.
 * Through the methods defined in this interface the most important properties
 * can be set at runtime.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ProgressBarTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ProgressBarTag extends InputComponentTag
{
    /** Constant for the default minimum value. */
    private static final Integer DEF_MINIMUM = 0;

    /** Constant for the default maximum value. */
    private static final Integer DEF_MAXIMUM = 100;

    /** A helper object for defining the progress text. */
    private TextData textData;

    /** Stores the minimum value of the progress bar. */
    private Integer min;

    /** Stores the maximum value of the progress bar. */
    private Integer max;

    /** Stores the current value. */
    private Integer value;

    /** Stores the text allowed flag. */
    private boolean allowText;

    /**
     * Creates a new instance of <code>ProgressBarTag</code>.
     */
    public ProgressBarTag()
    {
        textData = new TextData(this);
        setMin(DEF_MINIMUM);
        setMax(DEF_MAXIMUM);
        setNoField(true);
    }

    /**
     * Returns the maximum value of the progress bar.
     *
     * @return the maximum value
     */
    public Integer getMax()
    {
        return max;
    }

    /**
     * Set method for the max attribute.
     *
     * @param max the attribute's value
     */
    public void setMax(Integer max)
    {
        this.max = max;
    }

    /**
     * Returns the minimum value of the progress bar.
     *
     * @return the minimum value
     */
    public Integer getMin()
    {
        return min;
    }

    /**
     * Set method for the min attribute.
     *
     * @param min the attribute's value
     */
    public void setMin(Integer min)
    {
        this.min = min;
    }

    /**
     * Returns the current value of the progress bar.
     *
     * @return the current value
     */
    public Integer getValue()
    {
        return value;
    }

    /**
     * Set method for the value attribute.
     *
     * @param value the attribute's value
     */
    public void setValue(Integer value)
    {
        this.value = value;
    }

    /**
     * Returns the data object that defines the progress text of this progress
     * bar.
     *
     * @return data about the progress text
     */
    public TextData getProgressTextData()
    {
        return textData;
    }

    /**
     * Set method for the text attribute.
     *
     * @param s the attribute's value
     */
    public void setText(String s)
    {
        textData.setText(s);
    }

    /**
     * Set method for the textres attribute.
     *
     * @param s the attribute's value
     */
    public void setTextres(String s)
    {
        textData.setTextres(s);
    }

    /**
     * Set method for the resgrp attribute.
     *
     * @param s the attribute's value
     */
    public void setResgrp(String s)
    {
        textData.setResgrp(s);
    }

    /**
     * Returns a flag whether text is allowed for this progress bar.
     *
     * @return the text allowed flag
     */
    public boolean isAllowText()
    {
        return allowText;
    }

    /**
     * Set method for the allowText attribute.
     *
     * @param allowText the attribute's value
     */
    public void setAllowText(boolean allowText)
    {
        this.allowText = allowText;
    }

    /**
     * Creates a component handler for the represented component. This
     * implementation will ask the specified component manager to create a
     * handler for a progress bar.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the component handler for the newly created component
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected ComponentHandler<?> createComponentHandler(
            ComponentManager manager, boolean create)
            throws FormBuilderException, JellyTagException
    {
        return manager.createProgressBar(this, create);
    }
}

/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.builder.components.ColorHelper;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * An abstract base class for tags that create GUI components.
 * </p>
 * <p>
 * This class provides a framework for creating a Java (GUI) component,
 * initializing it with a set of standard attributes (like font or color) and
 * adding it to an enclosing container. Derived classes will mainly have to deal
 * with the creation of a concrete component and eventually perform additional
 * tasks.
 * </p>
 * <p>
 * The sub classes of this class can be divided into two groups: Tag classes for
 * simple GUI components (which only display static content) and tag classes for
 * input components. The latter gather user input and can be added to a
 * {@link net.sf.jguiraffe.gui.forms.Form Form} object for easy
 * validation and evaluation of the entered data.
 * </p>
 * <p>
 * The following basic properties are supported by this tag handler base class:
 * <ul>
 * <li>The component's foreground color using the <code>foreColor</code>
 * attribute.</li>
 * <li>The component's background color using the <code>backColor</code>
 * attribute.</li>
 * <li>A font for the component, which can be either directly set through the
 * <code>setFont()</code> method or by specifying the name of a variable that
 * exists in the context in the <code>fontRef</code> attribute.</li>
 * <li>An object representing layout constraints can be attached to this
 * component using the <code>setConstraints()</code> method.</li>
 * <li>With the <code>name</code> attribute, the component can be given an
 * optional name. If a name is set, it can be used for accessing this component
 * later.</li>
 * <li>A component can be assigned a tool tip. This can be done either directly
 * using the {@code tooltip} attribute or by specifying a resource ID and an
 * optional resource group using the {@code tooltipres} and {@code tooltipresgrp}
 * attributes.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComponentBaseTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class ComponentBaseTag extends FormBaseTag
{
    /** Stores information about the tool tip. */
    private final TextData toolTipData;

    /** Stores the font of this component. */
    private Object font;

    /** Stores the name of the font for this component. */
    private String fontRef;

    /** Stores the background color for this component. */
    private String backColor;

    /** Stores the foreground color for this component. */
    private String foreColor;

    /** Stores a constraint object for this component. */
    private Object constraints;

    /** Stores the resolved foreground color. */
    private Color foregroundColor;

    /** Stores the resolved background color. */
    private Color backgroundColor;

    /** Stores the value of the name attribute. */
    private String name;

    /**
     * Creates a new instance of {@code ComponentBaseTag}.
     */
    protected ComponentBaseTag()
    {
        toolTipData = new TextData(this);
    }

    /**
     * Returns the background color for this component as a string.
     *
     * @return the background color for this component
     */
    public String getBackColor()
    {
        return backColor;
    }

    /**
     * Sets the background color for this component as a string.
     *
     * @param backColor the background color for this component
     */
    public void setBackColor(String backColor)
    {
        this.backColor = backColor;
    }

    /**
     * Returns the constraints object for this component.
     *
     * @return the constraints object for this component
     */
    public Object getConstraints()
    {
        return constraints;
    }

    /**
     * Sets the constraints object for this component. This object is used when
     * the component is added to its container.
     *
     * @param constraints the constraints object
     */
    public void setConstraints(Object constraints)
    {
        this.constraints = constraints;
    }

    /**
     * Returns the font object for this component.
     *
     * @return the font for this component
     */
    public Object getFont()
    {
        return font;
    }

    /**
     * Sets the font object for this component.
     *
     * @param font the font object for this component
     */
    public void setFont(Object font)
    {
        this.font = font;
    }

    /**
     * Returns the name of the font to be used by this component.
     *
     * @return the font reference
     */
    public String getFontRef()
    {
        return fontRef;
    }

    /**
     * Sets the name of the font to be used by this component. This font must
     * have been created earlier by a <code>&lt;font&gt;</code> tag and stored
     * under this name in the jelly context.
     *
     * @param fontRef the name of the font to set
     */
    public void setFontRef(String fontRef)
    {
        this.fontRef = fontRef;
    }

    /**
     * Returns the foreground color of this component as string.
     *
     * @return the foreground color of this component
     */
    public String getForeColor()
    {
        return foreColor;
    }

    /**
     * Sets the foreground color of this component as string.
     *
     * @param foreColor the foreground color of this component
     */
    public void setForeColor(String foreColor)
    {
        this.foreColor = foreColor;
    }

    /**
     * Returns the background color of this component as a color object.
     *
     * @return the component's background color
     */
    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    /**
     * Returns the foreground color of this component as a color object.
     *
     * @return the component's foreground color
     */
    public Color getForegroundColor()
    {
        return foregroundColor;
    }

    /**
     * Returns the name of this input component.
     *
     * @return the component's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Setter method for the name attribute.
     *
     * @param name the attribute value
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Set method of the {@code tooltip} attribute.
     *
     * @param s the tool tip text
     */
    public void setTooltip(String s)
    {
        toolTipData.setText(s);
    }

    /**
     * Set method of the {@code tooltipres} attribute.
     *
     * @param s the resource ID for the tool tip
     */
    public void setTooltipres(String s)
    {
        toolTipData.setTextres(s);
    }

    /**
     * Set method of the {@code tooltipresgrp} attribute.
     *
     * @param g the resource group for the tool tip
     */
    public void setTooltipresgrp(String g)
    {
        toolTipData.setResgrp(g);
    }

    /**
     * Returns a {@code TextData} object with information about the tool tip for
     * this component.
     *
     * @return the {@code TextData} object for this component's tool tip
     */
    public TextData getToolTipData()
    {
        return toolTipData;
    }

    /**
     * Returns the component managed by this tag. This is the component that was
     * created by the tag and that will be placed in the UI. Note that this
     * object may only be available (and fully initialized) after this tag was
     * completely executed.
     *
     * @return the component managed by this tag
     */
    public abstract Object getComponent();

    /**
     * Inserts the specified component to the generated GUI. This method takes
     * care that the given component is added to the appropriate container
     * (which can be either defined by an enclosing container tag or can be the
     * root container). If the name is defined, the component will also be added
     * to an internal table maintained by the <code>ComponentBuilderData</code>
     * instance so that it can be accessed again later.
     *
     * @param name the name under which the component is to be stored
     * @param comp the component itself
     */
    protected void insertComponent(String name, Object comp)
    {
        findContainer().addComponent(comp, getConstraints());
        if (name != null)
        {
            getBuilderData().storeComponent(name, comp);
        }
    }

    /**
     * Performs processing of some default attributes. Should be called by
     * derived classes.
     *
     * @throws FormBuilderException if attributes have invalid names
     * @throws JellyTagException if a tag related error occurs
     */
    @Override
    protected void process() throws FormBuilderException, JellyTagException
    {
        if (getFont() == null && StringUtils.isNotEmpty(getFontRef()))
        {
            Object f = getContext().getVariable(getFontRef());
            if (f == null)
            {
                throw new FormBuilderException("Could not find font with name "
                        + getFontRef());
            }
            setFont(f);
        }

        backgroundColor = ColorHelper.resolveColor(getBackColor());
        foregroundColor = ColorHelper.resolveColor(getForeColor());
    }
}

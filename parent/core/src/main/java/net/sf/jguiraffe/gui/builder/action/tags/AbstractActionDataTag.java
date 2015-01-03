/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action.tags;

import net.sf.jguiraffe.gui.builder.action.Accelerator;
import net.sf.jguiraffe.gui.builder.action.ActionData;
import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.IconSupport;
import net.sf.jguiraffe.gui.builder.components.tags.TextData;
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * An abstract base class for tag handler classes that need to deal with action
 * objects and their properties.
 * </p>
 * <p>
 * This base class provides all functionality for managing an action's
 * properties. These properties can be set using various setter methods. They
 * are accessed through the implemented {@link ActionData} interface.
 * </p>
 * <p>
 * The following table lists all supported attributes:
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
 * <td>Directly defines the text of this action.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">textres</td>
 * <td>Defines the action's text from a text resource.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">resgrp</td>
 * <td>Allows specifying a resource group. This group will be used when
 * resolving the resources for the text, the mnemonic, and the tool tip (unless
 * for the tool tip a separate group is defined).</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">mnemonic</td>
 * <td>Directly sets the mnemonic key.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">mnemonicres</td>
 * <td>Allows to specify a resource ID from which the mnemonic will be obtained.
 * </td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">tooltip</td>
 * <td>Directly sets the tooltip for this action.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">tooltipres</td>
 * <td>Allows specifying a resource ID for this action's tool tip.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">acceleratorDef</td>
 * <td>Sets the accelerator for this action. This attribute expects a string
 * representation of an accelerator that can be parsed by the
 * {@link Accelerator#parse(String)} method.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">acceleratorRef</code>
 * <td>With this attribute a reference to an {@link Accelerator} object can be
 * specified. This object is searched in the current bean context. The
 * attributes <code>acceleratorDef</code> and <code>acceleratorRef</code> are
 * mutual exclusive.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * Because this tag handler class also implements the {@link IconSupport}
 * interface an <code>&lt;icon&gt;</code> tag can occur in the body.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AbstractActionDataTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractActionDataTag extends ActionBaseTag implements
        ActionData, IconSupport
{
    /** Stores a text icon data instance for defining text and icon. */
    private TextIconData tiData;

    /** Stores a text data instance for defining the tool tip. */
    private TextData toolTipData;

    /** Stores the accelerator associated with the represented action.*/
    private Accelerator accelerator;

    /** Stores the name of this component. */
    private String name;

    /** Stores the accelerator definition.*/
    private String acceleratorDef;

    /** Stores the reference to an accelerator.*/
    private String acceleratorRef;

    /**
     * Creates a new instance of <code>ActionDataTag</code> and initializes
     * it.
     */
    public AbstractActionDataTag()
    {
        super();
        tiData = new TextIconData(this);
        toolTipData = new TextData(this);
    }

    /**
     * Setter method of the text attribute.
     *
     * @param t the attribute value
     */
    public void setText(String t)
    {
        tiData.setText(t);
    }

    /**
     * Setter method for the textres attribute.
     *
     * @param r the attribute value
     */
    public void setTextres(String r)
    {
        tiData.setTextres(r);
    }

    /**
     * Setter method for the resgrp attribute.
     *
     * @param s the attribute value
     */
    public void setResgrp(String s)
    {
        tiData.setResgrp(s);
        toolTipData.setResgrp(s);
    }

    /**
     * Setter method for the mnemonic attribute.
     *
     * @param s the attribute value
     */
    public void setMnemonic(String s)
    {
        tiData.setMnemonicKey(s);
    }

    /**
     * Setter method for the mnemonicres attribute.
     *
     * @param s the attribute value
     */
    public void setMnemonicres(String s)
    {
        tiData.setMnemonicResID(s);
    }

    /**
     * Setter method for the tooltip attribute.
     *
     * @param s the attribute value
     */
    public void setTooltip(String s)
    {
        toolTipData.setText(s);
    }

    /**
     * Setter method for the tooltipres attribute.
     *
     * @param s the attribute value
     */
    public void setTooltipres(String s)
    {
        toolTipData.setTextres(s);
    }

    /**
     * Sets the icon for this action.
     *
     * @param icon the icon
     */
    public void setIcon(Object icon)
    {
        tiData.setIcon(icon);
    }

    /**
     * Returns the icon for this action.
     *
     * @return the icon
     */
    public Object getIcon()
    {
        return tiData.getIcon();
    }

    /**
     * Returns the mnemonic for this action.
     *
     * @return the mnemonic
     */
    public int getMnemonicKey()
    {
        return tiData.getMnemonic();
    }

    /**
     * Returns this action's text.
     *
     * @return the text
     */
    public String getText()
    {
        return tiData.getCaption();
    }

    /**
     * Returns the text for this action's tool tip.
     *
     * @return the tool tip
     */
    public String getToolTip()
    {
        return toolTipData.getCaption();
    }

    /**
     * Returns the name of this component.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of this component. The concrete meaning of this name is
     * determined by derived classes.
     *
     * @param name the name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the accelerator definition.
     *
     * @return the accelerator definition
     */
    public String getAcceleratorDef()
    {
        return acceleratorDef;
    }

    /**
     * Set method of the acceleratorDef attribute. Here the string
     * representation of an accelerator can be set. It will be used for
     * obtaining a corresponding <code>{@link Accelerator}</code> instance.
     *
     * @param acceleratorDef the accelerator definition as string
     * @see Accelerator#parse(String)
     */
    public void setAcceleratorDef(String acceleratorDef)
    {
        this.acceleratorDef = acceleratorDef;
    }

    /**
     * Returns the name of a variable to be used as accelerator.
     *
     * @return the name of an accelerator variable
     */
    public String getAcceleratorRef()
    {
        return acceleratorRef;
    }

    /**
     * Set method of the acceleratorRef attribute. Here the name of a variable
     * can be specified that should be used as accelerator. The tag will look up
     * this variable in the current bean context.
     *
     * @param acceleratorRef the accelerator reference
     */
    public void setAcceleratorRef(String acceleratorRef)
    {
        this.acceleratorRef = acceleratorRef;
    }

    /**
     * Returns the <code>Accelerator</code> associated with the represented
     * action.
     *
     * @return the <code>Accelerator</code<
     */
    public Accelerator getAccelerator()
    {
        return accelerator;
    }

    /**
     * Allows setting an <code>Accelerator</code> for the represented action.
     * This method may be called by tags in the body of this tag.
     *
     * @param accelerator the <code>Accelerator</code> for this action
     */
    public void setAccelerator(Accelerator accelerator)
    {
        this.accelerator = accelerator;
    }

    /**
     * A helper method for checking whether all attributes are correctly
     * defined. This implementation tests whether at least a text or an icon was
     * provided for the action. Note that this method is not automatically
     * called; sub classes must invoke it if necessary.
     *
     * @throws JellyTagException if attributes are invalid or required
     *         attributes are missing
     */
    protected void checkAttributes() throws JellyTagException
    {
        if (!tiData.isDefined())
        {
            throw new JellyTagException(
                    "A text or an icon must be defined for an action!");
        }
    }

    /**
     * Performs processing before the body of this tag gets executed. This
     * implementation checks the accelerator specification. If one is defined,
     * it is fetched and passed to the <code>setAccelerator()</code> method.
     *
     * @throws JellyTagException if the tag is incorrectly used
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        super.processBeforeBody();

        if (getAcceleratorDef() != null && getAcceleratorRef() != null)
        {
            throw new JellyTagException(
                    "Set either acceleratorRef or acceleratorDef attribute, "
                            + "but not both!");
        }

        if (getAcceleratorDef() != null)
        {
            try
            {
                setAccelerator(Accelerator.parse(getAcceleratorDef()));
            }
            catch (IllegalArgumentException iex)
            {
                throw new FormActionException(iex);
            }
        }

        if (getAcceleratorRef() != null)
        {
            setAccelerator((Accelerator) getBuilderData().getBeanContext()
                    .getBean(getAcceleratorRef()));
        }
    }
}

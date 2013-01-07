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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specific tag handler class for creating command button components.
 * </p>
 * <p>
 * A button can have a text and an icon. In addition a command string can be
 * defined, which can be used to identify the button when an action event
 * occurs. Though command buttons do not gather any input data, they are treated
 * as input components, mainly due to a requirement of the event mechanism.
 * Because the typical use case of a simple command button does not require any
 * data to be stored for this component, this tag handler class will set the
 * <code>noField</code> attribute to a default value of <b>true</b>. So if this
 * attribute is not explicitly set in the Jelly script, no field will be created
 * for the button in the generated <code>Form</code> object.
 * </p>
 * <p>
 * The following table lists all attributes supported by this tag handler class:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top"><code>text</code></td>
 * <td>With this attribute the label's text can directly be set.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>textres</code></td>
 * <td>Defines the resource ID for the label's text. The real text is resolved
 * using the current resource manager and the current locale.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>resgrp</code></td>
 * <td>Specifies the resource group of the label's text. If set, this resource
 * group is used when resolving the label's text as defined by the
 * <code>textres</code> attribute. If undefined, the form builder's default
 * resource group will be used.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>alignment</code></td>
 * <td>Defines the relative position of the label's icon to its text. This can
 * be one of the literal names of the
 * {@link net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment
 * TextIconAlignment} class.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>mnemonic</code></td>
 * <td>Here a mnemonic for this label can be specified. If the user enters this
 * key, the associated component will be focused (if supported by the platform).
 * </td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>mnemonicres</code></td>
 * <td>This attribute defines the mnemonic as a resource, which makes sense for
 * i18n applications.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>command</code></td>
 * <td>Allows defining a command string. Action events that are caused by this
 * button will contain this string.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>default</code></td>
 * <td>A boolean flag that indicates whether this button is the default button.
 * The default button is automatically triggered by a UI-specific user action
 * (typically pressing enter), even if it does not have the keyboard focus. Only
 * one button in a form should be marked as default button.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ButtonTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ButtonTag extends ToggleButtonTag
{
    /** A flag whether this is the default button. */
    private boolean defaultButton;

    /**
     * Creates a new instance of <code>ButtonTag</code>.
     */
    public ButtonTag()
    {
        super();
        setNoField(true);
    }

    /**
     * Returns a flag whether this tag represents the default button of the
     * current form.
     *
     * @return the default button flag
     */
    public boolean isDefault()
    {
        return defaultButton;
    }

    /**
     * Set method of the {@code default} attribute.
     *
     * @param f the attribute's value
     */
    public void setDefault(boolean f)
    {
        defaultButton = f;
    }

    /**
     * Creates the new command button component.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the component handler for the newly created button
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected ComponentHandler<Boolean> createPushButton(ComponentManager manager,
            boolean create) throws FormBuilderException
    {
        return manager.createButton(this, create);
    }

    /**
     * Performs processing before this tag's body is evaluated. This
     * implementation checks whether this button is marked as default button. If
     * so, the button's name is stored in the current
     * {@link net.sf.jguiraffe.gui.builder.components.ComponentBuilderData
     * ComponentBuilderData} object.
     *
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        super.processBeforeBody();

        if (isDefault())
        {
            getBuilderData().setDefaultButtonName(getName());
        }
    }
}

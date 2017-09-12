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
 * A specific input component tag for creating a radio button.
 * </p>
 * <p>
 * Radio buttons are simple input components that allow to enter a boolean
 * value: They can either be selected or deselected. Typically radio buttons are
 * grouped together in a radio group, and then only one button of the group can
 * be selected. To achieve this behavior, the radio buttons must be added to a
 * radio group (by either nesting the radio button tags inside a
 * {@link RadioGroupTag} or by specifying the {@code groups} attribute. Per
 * default it is assumed that a radio button belongs to a group, so the
 * {@code noField} attribute is set to <b>true</b>. If it is desired that a
 * field handler is created for the radio button, {@code noField} must be set to
 * <b>false</b> explicitly.
 * </p>
 * <p>
 * Radio buttons can have a label text and be associated with an icon. The
 * following attributes are supported by this tag handler class (in addition to
 * the default attributes allowed for all input component tags):
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
 * </table>
 * </p>
 * <p>
 * This tag supports an icon, which can be defined by an {@link IconTag} in the
 * tag's body. If neither a text nor an icon are defined, an exception will be
 * thrown.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: RadioButtonTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class RadioButtonTag extends PushButtonTag
{
    /**
     * Creates a new instance of {@code RadioButtonTag}.
     */
    public RadioButtonTag()
    {
        setNoField(true);
    }

    /**
     * Performs processing before the execution of the tag body. This
     * implementation checks whether this radio button is in the body of a
     * {@link RadioGroupTag}. If this is the case, it is added to the group.
     *
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        RadioGroupTag groupTag = (RadioGroupTag) findAncestorWithClass(RadioGroupTag.class);
        if (groupTag != null)
        {
            checkName();
            addToGroups(groupTag.getName());
        }

        super.processBeforeBody();
    }

    /**
     * Creates the radio button.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the component handler for the checkbox
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected ComponentHandler<?> createPushButton(ComponentManager manager,
            boolean create) throws FormBuilderException
    {
        return manager.createRadioButton(this, create);
    }
}

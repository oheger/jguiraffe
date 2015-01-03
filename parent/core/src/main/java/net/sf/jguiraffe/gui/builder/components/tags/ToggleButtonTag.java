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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A specific tag handler class for creating toggle buttons.
 * </p>
 * <p>
 * Toggle buttons are very similar to command buttons except for the fact that
 * they have an associated data value that is also stored in the generated
 * <code>Form</code>'s data bean: the information whether or not the button is
 * selected (pressed). So components of this type can be used to implement a
 * switch.
 * </p>
 * <p>
 * Analogously to other tags derived from the <code>PushButtonTag</code> class
 * tags of this type can have an icon and a display text. The following table
 * lists all supported attributes (in addition to the default attributes allowed
 * for all input components):
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
 * <td>Allows to define a command string. Action events that are caused by this
 * button will contain this string.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ToggleButtonTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ToggleButtonTag extends PushButtonTag
{
    /** Stores the command string of this button. */
    private String command;

    /**
     * Returns the command string of this button.
     *
     * @return the command of this button
     */
    public String getCommand()
    {
        return command;
    }

    /**
     * Setter method of the command attribute.
     *
     * @param command the attribute value
     */
    public void setCommand(String command)
    {
        this.command = command;
    }

    /**
     * Creates the new toggle button component.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the handler for the newly created component
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected ComponentHandler<?> createPushButton(ComponentManager manager,
            boolean create) throws FormBuilderException
    {
        return manager.createToggleButton(this, create);
    }
}

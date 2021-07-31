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

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderCallBack;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A tag class for creating labels.
 * </p>
 * <p>
 * This tag will create a simple label and add it to the enclosing container
 * object. Because a label does not gather any user input, it is not added to
 * the current form object. The following attributes are supported by label
 * tags:
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
 * <td valign="top"><code>componentref</code></td>
 * <td>With this attribute the name of an associated component can be specified.
 * If set and if supported by the current platform, the label can be used to
 * focus this component. There must be a component with the name given here.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * The label tag supports an icon, which can be defined by an {@link IconTag} in
 * the tag's body. If a label is associated with another component (by using the
 * <code>componentref</code> attribute), it is possible to obtain the text from
 * the display name of this component. This is automatically done if neither a
 * text nor an icon is defined. This is a very convenient way of creating a
 * label. If the <code>componentref</code> attribute is undefined, a text or an
 * icon must be defined; otherwise an exception will be thrown.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: LabelTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class LabelTag extends TextIconTag
{
    /** Stores the component attribute. */
    private String componentref;

    /**
     * Returns the name of the component this label is associated with.
     *
     * @return the associated component
     */
    public String getComponentref()
    {
        return componentref;
    }

    /**
     * Setter method of the componentref attribute.
     *
     * @param component the attribute value
     */
    public void setComponentref(String component)
    {
        this.componentref = component;
    }

    /**
     * Creates the label component.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the new label component
     * @throws MissingAttributeException if required attributes are missing
     * @throws FormBuilderException if an error occurs creating the component
     */
    @Override
    protected Object createComponent(ComponentManager manager, boolean create)
            throws MissingAttributeException, FormBuilderException
    {
        if (!create && getComponentref() == null)
        {
            checkAttributes();
        }

        Object result = manager.createLabel(this, create);
        if (!create && StringUtils.isNotEmpty(getComponentref()))
        {
            getBuilderData().addCallBack(new LabelLinker(), this);
        }
        return result;
    }

    /**
     * Ensures that a label is associated with a component if the componentref
     * attribute is set.
     */
    private static class LabelLinker implements ComponentBuilderCallBack
    {
        /**
         * Associates the label with a component.
         *
         * @param builderData the builder data
         * @param params the parameter object (the label tag)
         * @throws FormBuilderException if the component the label should be
         * linked with cannot be found
         */
        public void callBack(ComponentBuilderData builderData, Object params)
                throws FormBuilderException
        {
            LabelTag tag = (LabelTag) params;
            Object component = builderData.getComponent(tag.getComponentref());
            if (component == null)
            {
                throw new FormBuilderException(
                        "Cannot link label with component "
                                + tag.getComponentref()
                                + ", for it does not exist!");
            }

            String labelText = tag.getTextIconData().isDefined() ? null
                    : builderData.getForm().getDisplayName(
                            tag.getComponentref());
            builderData.getComponentManager().linkLabel(tag.getComponent(),
                    component, labelText);
        }
    }
}

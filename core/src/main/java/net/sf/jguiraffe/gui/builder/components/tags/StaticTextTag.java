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

import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A tag handler class for creating a static text component.
 * </p>
 * <p>
 * A static text is very similar to a label. The main difference is that it is
 * treated as an input component and can thus be addressed by name and
 * manipulated. Per default a static text component won't become part of the
 * form that is constructed during the build operation. But for it a
 * {@link net.sf.jguiraffe.gui.builder.components.ComponentHandler
 * ComponentHandler} object is created that can be obtained from the
 * <code>ComponentBuilderData</code> object. This handler can be used to
 * manipulate the data of the static text.
 * </p>
 * <p>
 * The properties of a static text are its text, its icon, and the alignment
 * between the two. They can be read and written using objects of type
 * {@link net.sf.jguiraffe.gui.builder.components.model.StaticTextData
 * StaticTextData}. This can be done in the following way (assuming that
 * <code>myStatic</code> is the name of a static text element and
 * <code>builderData</code> is an instance of
 * {@link net.sf.jguiraffe.gui.builder.components.ComponentBuilderData
 * ComponentBuilderData}):
 * </p>
 *
 * <pre>
 * ComponentHandler handler = builderData.getComponentHandler(&quot;myStatic&quot;);
 * StaticTextData data = (StaticTextData) handler.getData();
 * // set a new text
 * data.setText(&quot;Some new text&quot;);
 * handler.setData(data);
 * </pre>
 * <p>
 * Note that it is not sufficient to simply manipulate the
 * <code>StaticTextData</code> object. Instead the altered object must be passed
 * again to the <code>setData()</code> method of the component handler. The
 * ability of being altered after the creation makes static text elements
 * suitable for implementing dynamic labels. So simple labels, which won't be
 * changed during the life time of a form, can be realized using the
 * {@link LabelTag}, while this tag allows for instance labels whose text or
 * icon is based on some calculations or change dynamically. The following
 * attributes are supported by this tag handler class (in addition to the
 * default attributes allowed for all input component tags):
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
 * <td valign="top"><code>noField</code></td>
 * <td>This boolean standard attribute determines whether this element is to be
 * added to the form object. In contrast to most of the other input components
 * it has a default value of <b>true</b>, meaning that static text elements are
 * not part of the constructed form. If desired, this can be manually set to
 * <b>false</b>.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * This tag supports an icon, which can be defined by an {@link IconTag} in the
 * tag's body. It is supported to define neither a text nor an icon. The static
 * text's content may then be defined later.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: StaticTextTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class StaticTextTag extends PushButtonTag
{
    /**
     * Creates a new instance of <code>StaticTextTag</code>.
     */
    public StaticTextTag()
    {
        super();
        setNoField(true);
    }

    /**
     * Performs a validation for this tag. For a static text element no
     * validation is needed because completely undefined tags are allowed, too.
     *
     * @throws FormBuilderException if the tag is incorrectly used
     * @throws MissingAttributeException if a required attribute is missing
     */
    @Override
    protected void validateTag() throws FormBuilderException,
            MissingAttributeException
    {
    }

    /**
     * Creates the element. This implementation will create a static text
     * element using the specified component manager.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the component handler for the newly created element
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected ComponentHandler<?> createPushButton(ComponentManager manager,
            boolean create) throws FormBuilderException
    {
        return manager.createStaticText(this, create);
    }
}

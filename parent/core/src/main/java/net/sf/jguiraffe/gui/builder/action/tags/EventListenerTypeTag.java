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
package net.sf.jguiraffe.gui.builder.action.tags;

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.FormBaseTag;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A specialized tag handler implementation that allows adding another event
 * listener type to a {@link FormEventListenerTag}.
 * </p>
 * <p>
 * Per default, tags derived from {@link FormEventListenerTag} support only a
 * single event listener interface. The tag implemented by this handler class
 * can be placed in the body of a {@link FormEventListenerTag} to add another
 * listener interface. The event listener produced by the outer tag will then
 * support this new listener interface, too. This is useful if multiple event
 * types fired by a component should be mapped to a single action.
 * </p>
 * <p>
 * This tag handler class can deal with both standard and non-standard events.
 * For non-standard events the class of the event listener interface must be
 * specified. The following attributes are supported:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">type</td>
 * <td>Defines the name of the event listener type. This can be the name of a
 * standard type (e.g. <em>change</em> or <em>mouse</em> - case is irrelevant)
 * or a non-standard type. In the latter case the naming conventions used by the
 * {@link net.sf.jguiraffe.gui.builder.event.FormEventManager} class apply.</td>
 * <td valign="top">No</td>
 * </tr>
 * <tr>
 * <td valign="top">listenerClass</td>
 * <td>Here the class of the event listener interface can be provided. This is
 * only needed for non-standard listener types. If the {@code type} attribute
 * contains the name of a standard event type, this attribute is ignored.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * The following example shows how this tag can be used to add an additional
 * tree expansion listener to a change listener for a tree:
 *
 * <pre>
 * &lt;f:tree name="myTree" model="myTreeModel"&gt;
 *   &lt;a:changeListener actionName="myAction"&gt;
 *     &lt;a:listenerType type="Expansion"
 *       listenerClass="net.sf.jguiraffe.gui.builder.components.model.TreeExpansionLitener"/&gt;
 *   &lt;/a:changeListener&gt;
 * &lt;/f:tree&gt;
 * </pre>
 *
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: EventListenerTypeTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class EventListenerTypeTag extends FormBaseTag
{
    /** The name of the listener type. */
    private String type;

    /** The listener class. */
    private Object listenerClass;

    /**
     * Returns the event listener type as a string.
     *
     * @return the event listener type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Set method of the {@code type} attribute.
     *
     * @param type the attribute's value
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Returns the event listener class as an object. This attribute accepts
     * either a class object or the fully-qualified name of a class.
     *
     * @return the event listener class
     */
    public Object getListenerClass()
    {
        return listenerClass;
    }

    /**
     * Set method of the {@code listenerClass} attribute.
     *
     * @param listenerClass the attribute's value
     */
    public void setListenerClass(Object listenerClass)
    {
        this.listenerClass = listenerClass;
    }

    /**
     * Executes this tag. This implementation checks whether the tag is
     * contained in the body of a {@link FormEventListenerTag}. If this is the
     * case, an additional event listener type is added to the parent tag.
     * Otherwise, an exception is thrown.
     *
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        if (!(getParent() instanceof FormEventListenerTag))
        {
            throw new JellyTagException(
                    "This tag must be nested inside a FormEventListenerTag!");
        }
        if (getType() == null)
        {
            throw new MissingAttributeException("type");
        }

        FormEventListenerTag parent = (FormEventListenerTag) getParent();
        FormListenerType listenerType = FormListenerType.fromString(getType());
        if (listenerType == null)
        {
            if (getListenerClass() == null)
            {
                throw new JellyTagException("An event listener class must be "
                        + "provided for non-standard event type " + getType());
            }
            parent.addListenerType(getType(),
                    convertToClass(getListenerClass()));
        }
        else
        {
            parent.addListenerType(listenerType);
        }
    }
}

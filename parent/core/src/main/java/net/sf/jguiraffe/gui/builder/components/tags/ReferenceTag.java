/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A tag handler class that can be used to define references to other components
 * or groups in the body of a {@link ComponentHandlerTag}.
 * </p>
 * <p>
 * A <code>ComponentHandlerTag</code> can define a composite component
 * handler, which itself contains other component handlers. This tag provides a
 * means to specify the component handlers that should be added to the composite
 * handler. This can be done by either of the following possibilities:
 * </p>
 * <p>
 * <ul>
 * <li>With the <code>component</code> attribute the name of another
 * component can be defined that is constructed during the builder process. The
 * associated <code>ComponentHandler</code> of this component will be fetched
 * and added to the composite handler.</li>
 * <li>The <code>group</code> attribute allows to specify the name of a
 * component group. If this attribute is defined, the specified group is
 * retrieved and the component handlers of all containing components are added
 * to the composite handler.</li>
 * </ul>
 * </p>
 * <p>
 * The references will be resolved and added to the composite handler in order
 * of appearance. It does not matter if the components refered to have not yet
 * been created; the resolving takes place at the very end of the building
 * process.
 * </p>
 *
 * @see net.sf.jguiraffe.gui.builder.components.ComponentGroup
 * @see net.sf.jguiraffe.gui.builder.components.tags.ComponentGroupTag
 * @see net.sf.jguiraffe.gui.builder.components.tags.ComponentHandlerTag
 *
 * @author Oliver Heger
 * @version $Id: ReferenceTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ReferenceTag extends FormBaseTag
{
    /** Stores the component reference. */
    private String component;

    /** Stores the group reference. */
    private String group;

    /**
     * Returns the name of the referenced component.
     *
     * @return the component reference
     */
    public String getComponent()
    {
        return component;
    }

    /**
     * Setter method of the component attribute.
     *
     * @param component the attribute value
     */
    public void setComponent(String component)
    {
        this.component = component;
    }

    /**
     * Returns the name of the referenced group.
     *
     * @return the group reference
     */
    public String getGroup()
    {
        return group;
    }

    /**
     * Setter method of the group attribute.
     *
     * @param group the attribute value
     */
    public void setGroup(String group)
    {
        this.group = group;
    }

    /**
     * Executes this tag. Finds the enclosing <code>ComponentHandlerTag</code>
     * and passes the reference to it.
     *
     * @throws JellyTagException if a Jelly related error occurs
     */
    @Override
    protected void process() throws JellyTagException
    {
        ComponentHandlerTag parent =
                (ComponentHandlerTag) findAncestorWithClass(ComponentHandlerTag.class);
        if (parent == null)
        {
            throw new JellyTagException(
                    "This tag must be nested inside a ComponentHandlerTag!");
        }
        if (StringUtils.isEmpty(getGroup())
                && StringUtils.isEmpty(getComponent()))
        {
            throw new MissingAttributeException("component");
        }
        else if (!StringUtils.isEmpty(getGroup())
                && !StringUtils.isEmpty(getComponent()))
        {
            throw new JellyTagException(
                    "Either one group or component may be defined!");
        }
        else if (!StringUtils.isEmpty(getGroup()))
        {
            parent.addGroupReference(getGroup());
        }
        else
        {
            parent.addComponentReference(getComponent());
        }
    }
}

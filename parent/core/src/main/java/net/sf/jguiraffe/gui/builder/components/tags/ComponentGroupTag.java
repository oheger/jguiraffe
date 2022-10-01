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

import net.sf.jguiraffe.gui.builder.components.ComponentGroup;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A tag for constructing (logic) groups of components.
 * </p>
 * <p>
 * With this tag a {@link ComponentGroup ComponentGroup} object can be
 * constructed. The group will be created and stored in the current Jelly
 * context. It will be filled during the builder process. It can later be
 * referenced to access all component that belong to this group.
 * </p>
 * <p>
 * Groups can be filled with components in two different ways:
 * <ul>
 * <li>Each tag derived from {@link InputComponentTag} supports the
 * <code>groups</code> attribute that can obtain a comma separated list of the
 * names of the group the component should be added to. The tag will then find
 * the specified group objects and add the new component's name to all of them
 * other.</li>
 * <li>It is also possible to nest tags defining input components inside a
 * <code>ComponentGroupTag</code>. They are then automatically added to the
 * outer group (but only if the <code>groups</code> attribute is undefined).</li>
 * </ul>
 * </p>
 * <p>
 * Tags of this type support only one required attribute <code>name</code>,
 * which defines the group's name. Names for groups must be unique.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComponentGroupTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ComponentGroupTag extends FormBaseTag
{
    /** Stores the associated component group object. */
    private ComponentGroup group;

    /** Stores the group's name. */
    private String name;

    /**
     * Returns the group's name.
     *
     * @return the name of the group
     */
    public String getName()
    {
        return name;
    }

    /**
     * Setter method of the name attribute.
     *
     * @param name the attribute value
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the group that was created by this tag.
     *
     * @return the group
     */
    public ComponentGroup getGroup()
    {
        return group;
    }

    /**
     * Executes this tag before its body is processed. The corresponding group
     * object will be created immediately.
     *
     * @throws FormBuilderException if there is another group with the specified
     *         name
     * @throws JellyTagException if a jelly related error occurs
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        super.processBeforeBody();
        if (StringUtils.isEmpty(getName()))
        {
            throw new MissingAttributeException("name");
        }

        group = ComponentGroup.createGroup(getContext(), getName());
    }

    /**
     * Executes this tag after its body was processed. This is a dummy
     * implementation. All actions have been performed before body processing.
     */
    @Override
    protected void process()
    {
    }
}

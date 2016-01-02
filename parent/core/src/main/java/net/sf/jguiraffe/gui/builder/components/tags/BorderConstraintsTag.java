/*
 * Copyright 2006-2016 The JGUIraffe Team.
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

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

/**
 * <p>
 * A special tag handler implementation for creating constraints object for a
 * <code>{@link net.sf.jguiraffe.gui.layout.BorderLayout BorderLayout}</code>.
 * </p>
 * <p>
 * This is a quite simple tag that only supports the <code>name</code>
 * attribute, which must be set to the desired border constraints name. The
 * passed in value is not checked by this tag, this will be done later by the
 * layout object. The tag can be placed inside the body of a component tag whose
 * owning container uses a border layout.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BorderConstraintsTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class BorderConstraintsTag extends ConstraintsTag
{
    /** Stores the constraints name. */
    private String name;

    /**
     * Returns the name of the border constraints.
     *
     * @return the constraints name
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
     * Creates the constraints object. This implementation returns the string
     * that was passed in through the <code>name</code> attribute.
     *
     * @param manager the component manager
     * @return the constraints object
     * @throws FormBuilderException if an error occurs
     * @throws MissingAttributeException if a required attribute is missing
     */
    protected Object createConstraints(ComponentManager manager)
            throws FormBuilderException, MissingAttributeException
    {
        if (StringUtils.isEmpty(getName()))
        {
            throw new MissingAttributeException("name");
        } /* if */
        return getName();
    }
}

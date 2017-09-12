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

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A simple tag handler class for creating a separator for menus, toolbars, and
 * related components.
 * </p>
 * <p>
 * The tag implemented by this handler class can appear in the body of a tag
 * implementing the {@link ActionContainer} interface. It will invoke the
 * <code>addSeparator()</code> method of this interface to cause a separator to
 * be added to the corresponding container component.
 * </p>
 * <p>
 * This tag does not support any attributes nor body content.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SeparatorTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SeparatorTag extends ActionBaseTag
{
    /**
     * Executes this tag. Adds a separator to the nesting container.
     *
     * @throws JellyTagException if the tag is not correctly nested
     * @throws FormBuilderException if an error occurs creating the separator
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        ActionContainer container =
                (ActionContainer) findAncestorWithClass(ActionContainer.class);
        if (container == null)
        {
            throw new JellyTagException(
                    "Separator tag must be nested inside an action container!");
        }

        container.addSeparator();
    }
}

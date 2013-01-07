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
package net.sf.jguiraffe.gui.builder.action.tags;

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A tag handler class for creating menu bars.
 * </p>
 * <p>
 * This tag handler class uses the current
 * {@link net.sf.jguiraffe.gui.builder.action.ActionManager ActionManager}
 * object to create a menu bar. This menu bar will be stored in the Jelly
 * context under the specified name. It can be accessed by other tags creating
 * components that support menu bars (e.g. frames or dialogs). In the tag's body
 * an arbitrary number of <code>MenuTag</code> tags can occur defining the
 * menu bar's content.
 * </p>
 * <p>
 * The following table lists all supported attributes:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">name</td>
 * <td>The name under which the menu bar is stored in the Jelly context.</td>
 * <td valign="top">no</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: MenuBarTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class MenuBarTag extends ActionBaseTag implements ActionContainer
{
    /** Stores the new menu bar. */
    private Object menuBar;

    /** Stores the name of this menu bar. */
    private String name;

    /**
     * Returns the name of this menu bar.
     *
     * @return the name
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
     * Executes this tag before its body was processed. Here the menu bar will
     * be created and stored in an internal field.
     *
     * @throws JellyTagException if the tag is not correctly used
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        if (StringUtils.isEmpty(getName()))
        {
            throw new MissingAttributeException("name");
        }
        menuBar = getActionManager().createMenuBar(getActionBuilder());
    }

    /**
     * Executes this tag. This implementation stores the fully initialized menu
     * bar in the Jelly context.
     *
     * @throws JellyTagException if the tag is not correctly used
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws  JellyTagException, FormBuilderException
    {
        getContext().setVariable(getName(), getContainer());
    }

    /**
     * Returns the menu bar container.
     *
     * @return the menu bar
     */
    public Object getContainer()
    {
        return menuBar;
    }

    /**
     * Adds a separator to this container. This will throw an exception because
     * menu bars do not support separators.
     */
    public void addSeparator()
    {
        throw new UnsupportedOperationException(
                "Cannot add a separator to a menu bar!");
    }
}

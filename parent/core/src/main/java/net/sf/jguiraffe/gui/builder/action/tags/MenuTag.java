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
package net.sf.jguiraffe.gui.builder.action.tags;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.IconSupport;
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A tag handler class for defining menus.
 * </p>
 * <p>
 * With this tag handler a menu object can be defined that belongs to a menu
 * bar. This menu is defined by a text, a mnemonic, and an optional icon (not
 * all platforms may support icons for menus). Text and/or mnemonic can be
 * specified either directly or as resource IDs.
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
 * <td valign="top"><code>text</code></td>
 * <td>With this attribute the menu's text can directly be set.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>textres</code></td>
 * <td>Defines the resource ID for the menu's text. The real text is resolved
 * using the current resource manager and the current locale.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>resgrp</code></td>
 * <td>Specifies the resource group of the menu's text. If set, this resource
 * group is used when resolving the menu's text as defined by the
 * <code>textres</code> attribute. If undefined, the form builder's default
 * resource group will be used.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>mnemonic</code></td>
 * <td>Here a mnemonic for this menu can be specified. </td>
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
 * In this tag's body an icon tag can be placed to define the icon for the menu.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: MenuTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class MenuTag extends ActionBaseTag implements ActionContainer,
        IconSupport
{
    /** A list with the tag classes this tag can be placed inside. */
    private static Collection<Class<?>> parentClasses;

    /** Stores the text icon data for this menu. */
    private TextIconData data;

    /** Stores the menu created by this tag. */
    private Object menu;

    /**
     * Creates a new instance of <code>MenuTag</code>.
     */
    public MenuTag()
    {
        data = new TextIconData(this);
    }

    /**
     * Returns the definition data for this menu.
     *
     * @return the data for this menu
     */
    public TextIconData getData()
    {
        return data;
    }

    /**
     * Returns the container maintained by this tag. This implementation returns
     * the menu object.
     *
     * @return the container object
     */
    public Object getContainer()
    {
        return menu;
    }

    /**
     * Sets the icon for this menu.
     *
     * @param icon the icon
     */
    public void setIcon(Object icon)
    {
        getData().setIcon(icon);
    }

    /**
     * Setter method for the text attribute.
     *
     * @param s the attribute value
     */
    public void setText(String s)
    {
        getData().setText(s);
    }

    /**
     * Setter method for the textres attribute.
     *
     * @param s the attribute value
     */
    public void setTextres(String s)
    {
        getData().setTextres(s);
    }

    /**
     * Setter method for the resgrp attribute.
     *
     * @param s the attribute value
     */
    public void setResgrp(String s)
    {
        getData().setResgrp(s);
    }

    /**
     * Setter method for the mnemonic attribute.
     *
     * @param s the attribute value
     */
    public void setMnemonic(String s)
    {
        getData().setMnemonicKey(s);
    }

    /**
     * Setter method for the mnemonicres attribute.
     *
     * @param s the attribute value
     */
    public void setMnemonicres(String s)
    {
        getData().setMnemonicResID(s);
    }

    /**
     * Adds a separator to this menu. This task is delegated to the current
     * action manager.
     *
     * @throws FormActionException if an error occurs
     */
    public void addSeparator() throws FormActionException
    {
        getActionManager().addMenuSeparator(getActionBuilder(), getContainer());
    }

    /**
     * Executes this tag before its body is processed. This implementation
     * creates the menu object and stores it in an internal member field.
     *
     * @throws JellyTagException if the tag is not used correctly
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void processBeforeBody() throws JellyTagException, FormBuilderException
    {
        if (!getData().isDefined())
        {
            throw new JellyTagException("Menu must be completely defined!");
        }

        menu = getActionManager().createMenu(getActionBuilder(), null,
                getData(), findParentMenu());
    }

    /**
     * Executes this tag. Performs further initialization of the menu.
     *
     * @throws JellyTagException if the tag is not used correctly
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        menu = getActionManager().createMenu(getActionBuilder(),
                getContainer(), getData(), findParentMenu());
    }

    /**
     * Tries to find the parent menu to which this menu has to be added. This
     * can be either a menu bar or another menu this menu is a sub menu from. If
     * neither a nesting menu bar tag nor a menu tag can be found, an exception
     * will be thrown.
     *
     * @return the parent menu
     * @throws JellyTagException if no parent can be found
     */
    protected Object findParentMenu() throws JellyTagException
    {
        ActionContainer container = findParentContainer();
        if (container == null)
        {
            throw new JellyTagException(
                    "Menu tag must be nested inside a menu or menubar tag!");
        }
        return container.getContainer();
    }

    /**
     * Tries to find the action container for the parent menu. This tag can
     * occur inside a menu bar tag or a menu tag. If none of these is found,
     * <b>null</b> is returned.
     *
     * @return the parent menu container
     */
    protected ActionContainer findParentContainer()
    {
        return (ActionContainer) findAncestorWithClass(parentClasses);
    }

    // static initializer
    static
    {
        parentClasses = new ArrayList<Class<?>>(2);
        parentClasses.add(MenuTag.class);
        parentClasses.add(MenuBarTag.class);
    }
}

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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A tag for defining icons for tree components.
 * </p>
 * <p>
 * Tags of this type can be placed in the body of a <code>{@link TreeTag}</code>
 * . A <code>TreeIconTag</code> must be passed an icon name and the
 * corresponding icon (which is usually specified by a nested
 * <code>{@link IconTag}</code>). It collects this data and invokes the
 * <code>addIcon()</code> method of the <code>{@link TreeTag}</code>.
 * </p>
 * <p>
 * The following fragment demonstrates how this tag can be used for changing the
 * default icons of a tree component:
 *
 * <pre>
 * &lt;f:tree model=&quot;myTreeModel&quot; name=&quot;myTree&quot;&gt;
 *   &lt;f:treeIcon name=&quot;LEAF&quot;&gt;
 *     &lt;f:icon resource=&quot;myLeafIcon.gif&quot;/&gt;
 *   &lt;/f:treeIcon&gt;
 *   &lt;f:treeIcon name=&quot;BRANCH_EXPANDED&quot;&gt;
 *     &lt;f:icon resource=&quot;myExpandedIcon.gif&quot;/&gt;
 *   &lt;/f:treeIcon&gt;
 *   &lt;f:treeIcon name=&quot;BRANCH_COLLAPSED&quot;&gt;
 *     &lt;f:icon resource=&quot;myCollapsedIcon.gif&quot;/&gt;
 *   &lt;/f:treeIcon&gt;
 * &lt;/f:tree&gt;
 * </pre>
 *
 * </p>
 * <p>
 * The names of the icons must correspond with the
 * <code>{@link TreeIconHandler}</code> used by the tree component. The icon
 * handler returns icon names for the nodes of the tree. These names must
 * exactly match the icon names passed to the <code>name</code> attribute of the
 * <code>TreeIconTag</code>. In the example above the names used by the default
 * icon handler are used. If a custom icon handler is used the names may have to
 * be adapted.
 * </p>
 * <p>
 * The following table displays the attributes supported by this tag:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">name</td>
 * <td>Defines the name of the icon. Each icon is associated with a name.</td>
 * <td valign="top">No</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * Both an icon and an icon name must be provided otherwise an exception will be
 * thrown.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TreeIconTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TreeIconTag extends FormBaseTag implements IconSupport
{
    /** Stores the icon name. */
    private String name;

    /** Stores the icon itself. */
    private Object icon;

    /**
     * Returns the name of the icon.
     *
     * @return the icon name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the icon. This is the set method of the name attribute.
     *
     * @param name the icon name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the icon.
     *
     * @return the icon
     */
    public Object getIcon()
    {
        return icon;
    }

    /**
     * Sets the icon. This method is intended to be called by tags in the body
     * of this tag.
     *
     * @param icon the icon
     */
    public void setIcon(Object icon)
    {
        this.icon = icon;
    }

    /**
     * Executes this tag. This implementation tests whether all required
     * properties are set. Then it adds the icon to the parent tree tag.
     *
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        if (!(getParent() instanceof TreeTag))
        {
            throw new JellyTagException(
                    "This tag must be nested inside a TreeTag!");
        }
        if (getName() == null)
        {
            throw new MissingAttributeException("name");
        }
        if (getIcon() == null)
        {
            throw new MissingAttributeException("icon");
        }

        ((TreeTag) getParent()).addIcon(getName(), getIcon());
    }
}

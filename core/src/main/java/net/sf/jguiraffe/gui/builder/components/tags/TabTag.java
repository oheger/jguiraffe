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

import java.util.Collection;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A tag handler class for creating register tab pages of a tabbed pane.
 * </p>
 * <p>
 * Instances of this class can be placed in the body of a
 * <code>{@link TabbedPaneTag}</code>. Each tag defines exactly one tab page.
 * For this purpose a bunch of attributes can be used:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">title</td>
 * <td>Defines the title of this register as a string constant.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">titleres</td>
 * <td>Defines the title of this register as a resource ID.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">resgrp</td>
 * <td>Defines the resource group for resolving resource IDs. This attribute is
 * only evaluated if one of the other text attributes is specified using a
 * resource ID. In this case the corresponding resource group is obtained from
 * this value. If no resource group is specified, the default resource group
 * will be used.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">tooltip</td>
 * <td>Allows to specify a tool tip for this register.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">tooltipres</td>
 * <td>Allows to specify a tool tip for this register as a resource ID.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">mnemonic</td>
 * <td>With this attribute a mnemonic key can be defined for this register.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">mnemonicres</td>
 * <td>Allows to specifiy a mnemonic key that is obtained from a resource.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * In addition to these attributes in the body of this tag an
 * <code>{@link IconTag}</code> can be placed to define an icon for the
 * register to create.
 * </p>
 * <p>
 * The content of the register is defined by a component that must also be
 * defined in the tag's body. This is done by placing one of the component tags
 * in this tag's body. Note that only a single component is supported. If
 * multiple components are necessary, they must be placed inside a container
 * tag, e.g. a <code>{@link PanelTag}</code>.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TabTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TabTag extends ContainerTag implements IconSupport
{
    /** Stores a text icon data object for the tab's text and icon. */
    private TextIconData tid;

    /** Stores a text data object for the tab's tool tip. */
    private TextData tdToolTip;

    /**
     * Creates a new instance of <code>TabTag</code>.
     */
    public TabTag()
    {
        tid = new TextIconData(this);
        tdToolTip = new TextData(this);
    }

    /**
     * Set method for the title attribute.
     *
     * @param v the attribute's value
     */
    public void setTitle(String v)
    {
        tid.setText(v);
    }

    /**
     * Set method for the titleres attribute.
     *
     * @param v the attribute's value
     */
    public void setTitleres(String v)
    {
        tid.setTextres(v);
    }

    /**
     * Set method for the titlegrp attribute.
     *
     * @param v the attribute's value
     */
    public void setResgrp(String v)
    {
        tid.setResgrp(v);
        tdToolTip.setResgrp(v);
    }

    /**
     * Set method for the tooltip attribute.
     *
     * @param v the attribute's value
     */
    public void setTooltip(String v)
    {
        tdToolTip.setText(v);
    }

    /**
     * Set method for the tooltipres attribute.
     *
     * @param v the attribute's value
     */
    public void setTooltipres(String v)
    {
        tdToolTip.setTextres(v);
    }

    /**
     * Set method for the mnemonic attribute.
     *
     * @param v the attribute's value
     */
    public void setMnemonic(String v)
    {
        tid.setMnemonicKey(v);
    }

    /**
     * Set method for the mnemonicres attribute.
     *
     * @param v the attribute's value
     */
    public void setMnemonicres(String v)
    {
        tid.setMnemonicResID(v);
    }

    /**
     * Allows to set an icon for this tab. This method will be called by nested
     * <code>{@link IconTag}</code> tags.
     *
     * @param icon the icon for this tab
     */
    public void setIcon(Object icon)
    {
        tid.setIcon(icon);
    }

    /**
     * Creates the container object. This implementation is a bit different: It
     * will create an instance of the <code>TabData</code> inner class of
     * <code>TabbedPaneTag</code>. This <code>TabData</code> object will be
     * initialized with the component object that was added to this container
     * tag (only one or zero child elements are allowed). This causes the data
     * object to be added to the enclosing <code>TabbedPaneTag</code> tag.
     *
     * @param manager the component manager
     * @param create the create flag
     * @param components a collection with the child elements of this container
     * @return the container object
     * @throws FormBuilderException if the tag is not correctly used
     * @throws MissingAttributeException if a required attribute is missing
     */
    @Override
    protected Object createContainer(ComponentManager manager, boolean create,
            Collection<Object[]> components) throws FormBuilderException,
            MissingAttributeException
    {
        if (create)
        {
            if (!(findContainer() instanceof TabbedPaneTag))
            {
                throw new FormBuilderException(
                        "Tab tag must be directly nested inside a tabbed pane tag!");
            }
            return null;
        }
        else
        {
            if (!tid.isDefined())
            {
                throw new FormBuilderException(
                        "A tab must at least have either a text or an icon!");
            }
            if (components.size() > 1)
            {
                throw new FormBuilderException(
                        "Not more than one component can be placed in a tab!");
            }
            TabbedPaneTag.TabData tabData = new TabbedPaneTag.TabData();
            tabData.setTitle(tid.getCaption());
            tabData.setIcon(tid.getIcon());
            tabData.setMnemonic(tid.getMnemonic());
            tabData.setToolTip(tdToolTip.getCaption());
            if (components.size() > 0)
            {
                Object[] comp = components.iterator().next();
                tabData.setComponent(comp[0]);
            }
            return tabData;
        }
    }

    /**
     * Adds the child elements to the represented container. For this specific
     * tag this is not needed because the children are already processed in the
     * <code>createContainer()</code> method. So this is just a dummy.
     *
     * @param manager the component manager
     * @param container the newly created container object
     * @param comps a collection with the child elements
     */
    @Override
    protected void addComponents(ComponentManager manager, Object container,
            Collection<Object[]> comps)
    {
        // Empty implementation, not needed here
    }
}

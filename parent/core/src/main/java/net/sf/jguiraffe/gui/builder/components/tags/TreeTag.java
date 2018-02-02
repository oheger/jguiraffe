/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A tag that creates a tree component.
 * </p>
 * <p>
 * With this tag a typical tree view for displaying data organized in a
 * hierarchical manner can be created. Per default a tree view is not directly
 * used for entering data into a form. Often only the selection of a tree is
 * relevant, e.g. for identifying an object to be edited. Therefore the
 * component handler created by this tag (a
 * {@link net.sf.jguiraffe.gui.builder.components.model.TreeHandler TreeHandler}
 * by the way) is not added to the form per default. This can be changed by
 * setting the <code>noField</code> attribute to <b>false</b>. In this respect
 * this tag works analogously to the tag for creating tables.
 * </p>
 * <p>
 * The content of the tree is provided by a model which must be specified to
 * this tag. The <em>JGUIraffe</em> framework uses
 * <code>HierarchicalConfiguration</code> objects as tree models. With the
 * <code>model</code> attribute the name of the model variable can be set. The
 * tag will lookup this variable in the current <code>BeanContext</code>, i.e.
 * the whole power of the dependency injection framework is available for
 * defining the model data.
 * </p>
 * <p>
 * The following table lists the attributes supported by the
 * <code>TreeTag</code> tag handler class:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">model</td>
 * <td>With this attribute the model of the tree is specified. Here the name of
 * a variable that can be found in the current <code>BeanContext</code> must be
 * provided. This variable must be a <code>HierarchicalConfiguration</code>
 * object.</td>
 * <td valign="top">No</td>
 * </tr>
 * <tr>
 * <td valign="top">editable</td>
 * <td>This boolean property determines whether the tree is read-only or can be
 * edited. The default value is <b>false</b>. If set to <b>true</b>, the nodes
 * of the tree can be edited, which will also update the tree's model.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">multiSelection</td>
 * <td>This boolean property indicates whether the tree should support
 * multi-selection, i.e. multiple nodes can be selected at the same time. If the
 * attribute is missing, <b>false</b> is the default value.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">rootVisible</td>
 * <td>With this boolean attribute the presentation of the tree's root node can
 * be determined. If set to <b>true</b> (which is the default), the root node is
 * visible. Setting this attribute to <b>false</b> the root node won't be drawn.
 * </td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">iconHandler</td>
 * <td>Using this attribute a custom {@link TreeIconHandler} can be provided. A
 * bean with the name specified here will be looked up in the current bean
 * context. It must implement the {@link TreeIconHandler} interface. This bean
 * will then be used for determining the icons to be displayed for the single
 * tree nodes. If this attribute is undefined, a default icon handler will be
 * used.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valing="top">scrollWidth</td>
 * <td>Here the preferred width of the scroll pane enclosing the tree can be
 * specified as a number with unit (e.g. &quot;1.5cm&quot;). If specified, the
 * scroll pane will have exactly this preferred width. Otherwise, the width is
 * determined by the preferred width of the tree.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valing="top">scrollHeight</td>
 * <td>Here the preferred height of the scroll pane enclosing the tree can be
 * specified as a number with unit (e.g. &quot;10dlu&quot;). If specified, the
 * scroll pane will have exactly this preferred height. Otherwise, the height is
 * determined by the preferred height of the tree.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * With {@link TreeIconTag} tags placed in the body of this tag the icons
 * displayed by the tree for its nodes can be specified. You can either override
 * the default icons for leaf and (expanded or collapsed) branch nodes or
 * specify a custom {@link TreeIconHandler} object using the
 * <code>iconHandler</code> attribute that is responsible for drawing the icons.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TreeTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TreeTag extends InputComponentTag implements ScrollSizeSupport
{
    /**
     * Constant for the name of the default icon used for expanded branch nodes.
     */
    public static final String ICON_BRANCH_EXPANDED = "BRANCH_EXPANDED";

    /**
     * Constant for the name of the default icon used for collapsed branch
     * nodes.
     */
    public static final String ICON_BRANCH_COLLAPSED = "BRANCH_COLLAPSED";

    /** Constant for the name of the default icon used for leaf nodes. */
    public static final String ICON_LEAF = "LEAF";

    /**
     * Constant for the default icon handler. This object is used when no custom
     * handler is set.
     */
    private static final TreeIconHandler DEF_ICON_HANDLER = new TreeIconHandler()
    {
        /*
         * Returns the name of the icon to use for the specified node. This
         * implementation only evaluates the expanded and leaf flags and
         * returns one of three default icon names (one of the <code>ICON_XXX</code> constants).
         */
        public String getIconName(ConfigurationNode node, boolean expanded,
                boolean leaf)
        {
            if (leaf)
            {
                return ICON_LEAF;
            }
            else
            {
                return expanded ? ICON_BRANCH_EXPANDED : ICON_BRANCH_COLLAPSED;
            }
        }
    };

    /** Stores the configuration that serves as the tree model. */
    private HierarchicalConfiguration treeModel;

    /** Stores the icon handler to be used with this tree. */
    private TreeIconHandler iconHandlerImplementation;

    /** A map for storing the icons. */
    private Map<String, Object> icons;

    /** Stores the name of the model. */
    private String model;

    /** Stores the name of the icon handler. */
    private String iconHandler;

    /** The scroll width as a string. */
    private String scrollWidth;

    /** The scroll height as a string. */
    private String scrollHeight;

    /** The preferred scroll width as number with unit. */
    private NumberWithUnit preferredScrollWidth;

    /** The preferred scroll height as number with unit. */
    private NumberWithUnit preferredScrollHeight;

    /** Stores the editable flag. */
    private boolean editable;

    /** Stores the multi selection flag. */
    private boolean multiSelection;

    /** Stores the root visible flag. */
    private boolean rootVisible;

    /**
     * Creates a new instance of <code>TreeTag</code>.
     */
    public TreeTag()
    {
        setNoField(true);
        setRootVisible(true);
        icons = new HashMap<String, Object>();
    }

    /**
     * Returns the configuration used as tree model.
     *
     * @return the model of the tree
     */
    public HierarchicalConfiguration getTreeModel()
    {
        return treeModel;
    }

    /**
     * Sets the configuration used as tree model. Per default the model will be
     * obtained from the current bean context; its name is determined using the
     * <code>model</code> attribute.
     *
     * @param treeModel the configuration to be used as tree model
     */
    public void setTreeModel(HierarchicalConfiguration treeModel)
    {
        this.treeModel = treeModel;
    }

    /**
     * Returns the name of the variable that serves as tree model.
     *
     * @return the name of the tree model variable
     */
    public String getModel()
    {
        return model;
    }

    /**
     * Sets the name of the variable that serves as tree model.
     *
     * @param model the name of the tree model variable
     */
    public void setModel(String model)
    {
        this.model = model;
    }

    /**
     * Returns the editable flag. This flag determines whether the tree supports
     * updating the model.
     *
     * @return the editable flag
     */
    public boolean isEditable()
    {
        return editable;
    }

    /**
     * Set method of the editable attribute.
     *
     * @param editable the value of the attribute
     */
    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    /**
     * Returns the multiple selection flag. If set to <b>true</b>, multiple
     * nodes can be selected concurrently.
     *
     * @return the multiple selection flag
     */
    public boolean isMultiSelection()
    {
        return multiSelection;
    }

    /**
     * Set method for the multiSelection attribute.
     *
     * @param multiSelection the attribute value
     */
    public void setMultiSelection(boolean multiSelection)
    {
        this.multiSelection = multiSelection;
    }

    /**
     * Returns the value of the rootVisible flag. This flag determines whether
     * the root node of the tree is displayed.
     *
     * @return a flag whether the root node is visible
     */
    public boolean isRootVisible()
    {
        return rootVisible;
    }

    /**
     * Set method of the rootVisible attribute.
     *
     * @param rootVisible the value of the attribute
     */
    public void setRootVisible(boolean rootVisible)
    {
        this.rootVisible = rootVisible;
    }

    /**
     * Returns the name of the bean to be used as icon handler for this tree.
     *
     * @return the name of the icon handler bean
     */
    public String getIconHandler()
    {
        return iconHandler;
    }

    /**
     * Set method of the iconHandler attribute.
     *
     * @param iconHandler the value of the attribute
     */
    public void setIconHandler(String iconHandler)
    {
        this.iconHandler = iconHandler;
    }

    /**
     * Returns the preferred scroll width. This value is calculated based on the
     * {@code scrollWidth} attribute during tag processing. If no preferred
     * scroll width is specified, an object with the value 0 is returned.
     *
     * @return the preferred scroll width
     */
    public NumberWithUnit getPreferredScrollWidth()
    {
        return preferredScrollWidth;
    }

    /**
     * Returns the preferred scroll height. This value is calculated based on
     * the {@code scrollHeight} attribute during tag processing. If no preferred
     * scroll height is specified, an object with the value 0 is returned.
     *
     * @return the preferred scroll height
     */
    public NumberWithUnit getPreferredScrollHeight()
    {
        return preferredScrollHeight;
    }

    /**
     * Set method of the {@code scrollWidth} attribute.
     *
     * @param scrollWidth the attribute's value
     */
    public void setScrollWidth(String scrollWidth)
    {
        this.scrollWidth = scrollWidth;
    }

    /**
     * Set method of the {@code scrollHeight} attribute.
     *
     * @param scrollHeight the attribute's value
     */
    public void setScrollHeight(String scrollHeight)
    {
        this.scrollHeight = scrollHeight;
    }

    /**
     * Adds an icon to this tree. The icon is given a unique name, under which
     * it can be accessed by the tree's <code>{@link TreeIconHandler}</code>.
     * The default icon handler (which will be used when no custom handler is
     * specified using the <code>iconHandler</code> attribute) uses the
     * following names for icons:
     * <ul>
     * <li>{@value #ICON_BRANCH_EXPANDED} for expanded branch nodes</li>
     * <li>{@value #ICON_BRANCH_COLLAPSED} for collapsed branch nodes and</li>
     * <li>{@value #ICON_LEAF} for leaf nodes</li>
     * </ul>
     * By adding icons with these names the default icons of the tree can be
     * changed. However, if a custom icon handler is used, it is up to this
     * object which icons it supports.
     *
     * @param name the name of the icon (must not be <b>null</b>)
     * @param icon the icon (must not be <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is missing
     */
    public void addIcon(String name, Object icon)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Icon name must not be null");
        }
        if (icon == null)
        {
            throw new IllegalArgumentException("Icon must not be null!");
        }

        icons.put(name, icon);
    }

    /**
     * Returns a map with the icons defined for this tree. This map will be used
     * for actually rendering the icons for the single nodes. The
     * <code>{@link TreeIconHandler}</code> is queried for the name of the icon
     * for the current node. Then this map is consulted for retrieving the
     * corresponding icon. If no icon is found for the name returned by the icon
     * handler, the default icon for this node will be displayed.
     *
     * @return a map with the icons defined for this tree
     */
    public Map<String, Object> getIcons()
    {
        return Collections.unmodifiableMap(icons);
    }

    /**
     * Returns the <code>TeeIconHandler</code> to be used with this tree. If an
     * icon handler was specified using the <code>iconHandler</code> attribute,
     * it is resolved from the current bean context. Otherwise a default icon
     * handler will be returned that uses the standard icons of the current
     * platform. So the return value is never <b>null</b>.
     *
     * @return the <code>TreeIconHandler</code> for this tree
     * @see #addIcon(String, Object)
     */
    public TreeIconHandler getResolvedIconHandler()
    {
        return iconHandlerImplementation;
    }

    /**
     * Performs pre-processing before the body of this tag is executed. This
     * implementation tries to resolve the icon handler if one is specified.
     *
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is incorrectly used
     * @throws net.sf.jguiraffe.di.InjectionException if a bean cannot be resolved
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        super.processBeforeBody();

        iconHandlerImplementation =
                (getIconHandler() != null) ? resolveIconHandler(getIconHandler())
                        : createDefaultIconHandler();
    }

    /**
     * Creates the component handler for the tree component. This implementation
     * will check whether a tree model is defined and obtain it from the bean
     * context if necessary. Then it will delegate to the
     * <code>ComponentManager</code> for actually creating the component.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the component handler for the newly created component
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is used incorrectly
     * @throws net.sf.jguiraffe.di.InjectionException if the model bean cannot
     *         be resolved
     */
    @Override
    protected ComponentHandler<?> createComponentHandler(
            ComponentManager manager, boolean create)
            throws FormBuilderException, JellyTagException
    {
        if (create)
        {
            preferredScrollWidth = convertToNumberWithUnit(scrollWidth,
                    NumberWithUnit.ZERO);
            preferredScrollHeight = convertToNumberWithUnit(scrollHeight,
                    NumberWithUnit.ZERO);
        }
        else
        {
            initTreeModel();
        }

        return manager.createTree(this, create);
    }

    /**
     * Resolves the <code>TreeIconHandler</code> bean. This method is called if
     * the <code>iconHandler</code> attribute is specified. It obtains the bean
     * with the given name from the current bean context.
     *
     * @param name the name of the icon handler bean
     * @return the icon handler bean
     */
    protected TreeIconHandler resolveIconHandler(String name)
    {
        return (TreeIconHandler) getBuilderData().getBeanContext()
                .getBean(name);
    }

    /**
     * Returns a default <code>TreeIconHandler</code> object. This method is
     * called by code>processBeforeBody()</code> if no custom icon handler is
     * specified. It returns a default implementation that uses the default
     * icons of the current platform.
     *
     * @return a default icon handler
     */
    protected TreeIconHandler createDefaultIconHandler()
    {
        return DEF_ICON_HANDLER;
    }

    /**
     * Initializes the tree model. If a model was set explicitly, it is used.
     * Otherwise the current bean context is queried for the bean acting as the
     * model. If no model bean is defined, an exception is thrown.
     *
     * @throws JellyTagException if no model is defined
     */
    private void initTreeModel() throws JellyTagException
    {
        if (getTreeModel() == null)
        {
            if (getModel() == null)
            {
                throw new MissingAttributeException("model");
            }

            setTreeModel((HierarchicalConfiguration) getBuilderData()
                    .getBeanContext().getBean(getModel()));
        }
    }
}

/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.Composite;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specialized tag handler implementation for creating a tabbed pane.
 * </p>
 * <p>
 * A tabbed pane provides an arbitrary number of registers, from which the user
 * can select one. The content of this register is then displayed while all
 * others are invisible.
 * </p>
 * <p>
 * To fully define a tabbed pane in a builder script this tag is used together
 * with the {@link TabTag} tag: For each register a
 * <code>&lt;register&gt;</code> tag is to be placed in the body of this tag, in
 * which the properties of the register are set. When the tag is processed the
 * current {@link net.sf.jguiraffe.gui.builder.components.ComponentManager
 * ComponentManager} implementation will construct proper tab components from
 * this data.
 * </p>
 * <p>
 * Though a tabbed pane itself usually does not contribute to the input data a
 * user can enter in a form, these components are here treated as input
 * components. The reason is that they have a state (the index of the selected
 * register) that can be queried and altered by application code. This can be
 * done through the {@link net.sf.jguiraffe.gui.forms.ComponentHandler
 * ComponentHandler} object that is created for the tabbed pane. This handler
 * can also be used to register a change listener that will be notified when
 * ever the selected tab register changes. However per default for a tabbed pane
 * no form element will be created (i.e. the <code>noField</code> property is
 * set to <b>false</b>).
 * </p>
 * <p>
 * The following table lists the attributes that are supported by this tag. Note
 * that some of the properties defined here may not be available in all
 * underlying GUI libraries:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">placement</td>
 * <td>With this property the placement of the registers can be specified. It
 * can have one of the values <em>left, right,
 * top</em>, or <em>bottom</em>. The default value is <em>top</em>.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">name</td>
 * <td>Because tabbed panes are regular input components they can be given a
 * name, under which they can be accessed. However the name is optional. If none
 * is defined, a name of the following form is automatically generated:
 * <code>tabbedPane&lt;n&gt;</code> with <em>&lt;n&gt;</em> being a serial
 * number starting at 1.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TabbedPaneTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TabbedPaneTag extends InputComponentTag implements Composite
{
    /** Constant for the prefix for automatically generated tab names. */
    public static final String AUTO_NAME_PREFIX = "tabbedPane";

    /** Stores a collection with the tabs of this tabbed pane. */
    private final Collection<TabData> tabs;

    /** Stores the placement of this tabbed pane. */
    private Placement placementValue;

    /** Stores the value of the placement attribute. */
    private String placement;

    /**
     * Creates a new instance of <code>TabbedPaneTag</code>.
     */
    public TabbedPaneTag()
    {
        tabs = new ArrayList<TabData>();
        setNoField(true);
    }

    /**
     * Returns the string value of the placement attribute.
     *
     * @return the placement as string
     */
    public String getPlacement()
    {
        return placement;
    }

    /**
     * Set method of the placement attribute.
     *
     * @param v the attribute's value
     */
    public void setPlacement(String v)
    {
        placement = v;
    }

    /**
     * Returns the placement of this tabbed pane's registers.
     *
     * @return the placement of the registers
     */
    public Placement getPlacementValue()
    {
        return (placementValue != null) ? placementValue : Placement.TOP;
    }

    /**
     * Allows to directly specify a placement value. (This is used for testing
     * mainly.)
     *
     * @param p the new placement value
     */
    public void setPlacementValue(Placement p)
    {
        placementValue = p;
    }

    /**
     * Adds a child component to this composite object. This implementation
     * expects that the child component is a <code>TabData</code> object.
     * Otherwise an exception is thrown. The constraints will be ignored.
     *
     * @param comp the child component to be added
     * @param constraints the layout constraints object
     * @throws FormBuilderRuntimeException if the passed in component is not a
     *         {@code TabData} object
     */
    public void addComponent(Object comp, Object constraints)
    {
        if (!(comp instanceof TabData))
        {
            throw new FormBuilderRuntimeException(
                    "Only TabData objects can be added to TabbedPaneTag!");
        }

        tabs.add((TabData) comp);
    }

    /**
     * Returns the container object. This is not needed for this tag.
     *
     * @return the container object
     */
    public Object getContainer()
    {
        // just a dummy implementation
        return null;
    }

    /**
     * Sets a layout for this container. For a tabbed pane layout objects are
     * not supported, so an exception will be thrown.
     *
     * @param layout the layout object
     */
    public void setLayout(Object layout)
    {
        throw new UnsupportedOperationException(
                "Layouts are not supported for tabbed panes!");
    }

    /**
     * Returns a collection with the tabs of this tabbed pane. This information
     * will be evaluated by the component manager to create the tab pages.
     *
     * @return a collection with all defined tabs
     */
    public Collection<TabData> getTabs()
    {
        return tabs;
    }

    /**
     * Processes this tag. This implementation ensures that always a name is
     * specified for this component - either in the script a name was defined or
     * one is generated automatically.
     *
     * @throws FormBuilderException if the tag is incorrectly used
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected void process() throws  FormBuilderException, JellyTagException
    {
        if (getName() == null)
        {
            setName(generateAutoName());
        }
        super.process();
    }

    /**
     * Creates the component handler object for the represented component. This
     * implementation will ask the component manager to create a tabbed pane
     * object and its corresponding component handler.
     *
     * @param manager the component handler
     * @param create the create flag
     * @return the component handler for the new component
     * @throws FormBuilderException if the tag is incorrectly used
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected ComponentHandler<?> createComponentHandler(ComponentManager manager,
            boolean create) throws FormBuilderException, JellyTagException
    {
        if (!create)
        {
            if (getPlacement() != null)
            {
                try
                {
                    setPlacementValue(Placement.valueOf(getPlacement()
                            .toUpperCase(Locale.ENGLISH)));
                }
                catch (IllegalArgumentException iex)
                {
                    throw new FormBuilderException("Invalid placement: "
                            + getPlacement());
                }
            }
        }

        return manager.createTabbedPane(this, create);
    }

    /**
     * Generates a name for this tabbed pane. This method is called when no name
     * was specified. It must return a unique default name. This implementation
     * uses the prefix defined by the <code>AUTO_NAME_PREFIX</code> constant
     * and adds a serial number to it.
     *
     * @return an auto generated name for this component
     */
    protected String generateAutoName()
    {
        int index = 1;
        String name;

        do
        {
            name = AUTO_NAME_PREFIX + index;
            index++;
        } while (getBuilderData().getComponentHandler(name) != null);

        return name;
    }

    /**
     * An enumeration class that defines the valid values for the
     * <code>placement</code> attribute.
     */
    public static enum Placement
    {
        /** Tabs are placed at the top. */
        TOP,

        /** Tabs are placed at the bottom. */
        BOTTOM,

        /** Tabs are placed at the left. */
        LEFT,

        /** Tabs are placed at the right. */
        RIGHT
    }

    /**
     * A data class that holds all properties of a register that can be added to
     * a tabbed pane.
     */
    public static class TabData
    {
        /** Stores the title of this register. */
        private String title;

        /** Stores the icon of this register. */
        private Object icon;

        /** Stores a tooltip for this register. */
        private String toolTip;

        /** Stores a mnemonic for this register. */
        private int mnemonic;

        /** Stores the component that represents the content of this register. */
        private Object component;

        /**
         * Returns the component of this register.
         *
         * @return the component
         */
        public Object getComponent()
        {
            return component;
        }

        /**
         * Sets the component of this register. This component will be displayed
         * when this register is selected. It is responsible for the graphical
         * representation of the register.
         *
         * @param component the component for this register
         */
        public void setComponent(Object component)
        {
            this.component = component;
        }

        /**
         * Returns the icon of this register.
         *
         * @return the icon (can be <b>null</b>)
         */
        public Object getIcon()
        {
            return icon;
        }

        /**
         * Sets the icon for this register.
         *
         * @param icon the icon
         */
        public void setIcon(Object icon)
        {
            this.icon = icon;
        }

        /**
         * Returns a mnemonic for this register.
         *
         * @return the mnemonic
         */
        public int getMnemonic()
        {
            return mnemonic;
        }

        /**
         * Sets a mnemonic for this register. If defined, the user can press ALT
         * plus this key to navigate to this register.
         *
         * @param mnemonic the mnemonic key
         */
        public void setMnemonic(int mnemonic)
        {
            this.mnemonic = mnemonic;
        }

        /**
         * Returns the title of this register.
         *
         * @return the title
         */
        public String getTitle()
        {
            return title;
        }

        /**
         * Sets the title for this register.
         *
         * @param title the title
         */
        public void setTitle(String title)
        {
            this.title = title;
        }

        /**
         * Returns a tool tip for this register.
         *
         * @return the tool tip
         */
        public String getToolTip()
        {
            return toolTip;
        }

        /**
         * Sets a tool tip for this register.
         *
         * @param toolTip the new tool tip
         */
        public void setToolTip(String toolTip)
        {
            this.toolTip = toolTip;
        }
    }
}

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
package net.sf.jguiraffe.gui.layout;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A test implementation of {@link PercentLayoutPlatformAdapter}. This class
 * uses simple objects as components that simulate UI controls. They only store
 * coordinates.
 *
 * @author Oliver Heger
 * @version $Id: PercentLayoutPlatformAdapterImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PercentLayoutPlatformAdapterImpl implements
        PercentLayoutPlatformAdapter, UnitSizeHandler, Serializable
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20090730L;

    /** Constant for the default font size. */
    public static final int FONT_SIZE = 20;

    /** Constant for the screen resolution in DPI. */
    public static final int SCREEN_RES = 96;

    /** A list with the components added to this layout. */
    private final List<Component> components = new ArrayList<Component>();

    private final List<Object> constraints = new ArrayList<Object>();

    /** A map for storing named components. */
    private final Map<String, Component> namedComponents = new HashMap<String, Component>();

    /**
     * Adds the given component.
     *
     * @param name the optional component name
     * @param comp the component to add
     * @param constr the constraints
     */
    public void addComponent(String name, Component comp, Object constr)
    {
        components.add(comp);
        constraints.add(constr);
        if (name != null)
        {
            namedComponents.put(name, comp);
        }
    }

    /**
     * Creates a component with the given sizes and adds it to this layout.
     *
     * @param name the optional name
     * @param wpref the preferred width
     * @param hpref the preferred height
     * @param wmin the minimum width
     * @param hmin the minimum height
     * @param constr the constraints
     * @return the component
     */
    public Component createComponent(String name, int wpref, int hpref,
            int wmin, int hmin, Object constr)
    {
        Component comp = new Component();
        comp.preferredHeight = hpref;
        comp.preferredWidth = wpref;
        comp.minHeight = hmin;
        comp.minWidth = wmin;
        addComponent(name, comp, constr);
        return comp;
    }

    /**
     * Creates a component with the given preferred size and adds it this
     * layout. As minimum size the preferred size divided by 2 is used.
     *
     * @param name the optional name
     * @param wpref the preferred with
     * @param hpref the preferred height
     * @param constr the constraints
     * @return the component
     */
    public Component createComponent(String name, int wpref, int hpref,
            Object constr)
    {
        return createComponent(name, wpref, hpref, wpref / 2, hpref / 2, constr);
    }

    /**
     * Returns the component with the given name.
     *
     * @param name the name
     * @return the component with this name or <b>null</b>
     */
    public Component getComponentByName(String name)
    {
        return namedComponents.get(name);
    }

    public Component getComponent(int index)
    {
        return components.get(index);
    }

    public int getComponentCount()
    {
        return components.size();
    }

    public Object getConstraints(int index)
    {
        return constraints.get(index);
    }

    public int getMinimumComponentSize(Object component, boolean vert)
    {
        Component comp = (Component) component;
        return vert ? comp.minHeight : comp.minWidth;
    }

    public int getPreferredComponentSize(Object component, boolean vert)
    {
        Component comp = (Component) component;
        return vert ? comp.preferredHeight : comp.preferredWidth;
    }

    public UnitSizeHandler getSizeHandler()
    {
        return this;
    }

    public void setComponentBounds(Object component, Rectangle bounds)
    {
        Component comp = (Component) component;
        comp.x = bounds.x;
        comp.y = bounds.y;
        comp.width = bounds.width;
        comp.height = bounds.height;
    }

    /**
     * Removes a component from this adapter.
     *
     * @param component the component to remove
     */
    public void removeComponent(Object component)
    {
        int index = components.indexOf(component);
        if (index >= 0)
        {
            components.remove(index);
            constraints.remove(index);
        }
    }

    public double getFontSize(Object component, boolean y)
    {
        return FONT_SIZE;
    }

    public int getScreenResolution()
    {
        return SCREEN_RES;
    }

    /**
     * A class for the "components" managed by this layout adapter. These
     * components only store bounds and preferred sizes to verify the layouting
     * algorithms.
     */
    public static class Component implements Serializable
    {
        /**
         * The serial version UID.
         */
        private static final long serialVersionUID = 1L;

        /** The x position. */
        public int x;

        /** The y position. */
        public int y;

        /** The width. */
        public int width;

        /** The height. */
        public int height;

        /** The minimum width. */
        public int minWidth;

        /** The minimum height. */
        public int minHeight;

        /** The preferred width. */
        public int preferredWidth;

        /** The preferred height. */
        public int preferredHeight;
    }
}

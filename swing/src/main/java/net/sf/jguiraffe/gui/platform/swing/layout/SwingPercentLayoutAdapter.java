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
package net.sf.jguiraffe.gui.platform.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.jguiraffe.gui.layout.PercentLayoutBase;
import net.sf.jguiraffe.gui.layout.PercentLayoutPlatformAdapter;
import net.sf.jguiraffe.gui.layout.UnitSizeHandler;

/**
 * <p>
 * Implementation of a Swing-specific adapter class for the percent layout
 * manager.
 * </p>
 * <p>
 * This class implements the percent layout manager for swing. It implements the
 * <code>LayoutManager2</code> interface and can be used as a standard Swing
 * layout.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingPercentLayoutAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingPercentLayoutAdapter implements LayoutManager2,
        PercentLayoutPlatformAdapter, Serializable
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20090730L;

    /** Stores a reference to the associated percent layout object. */
    private final PercentLayoutBase percentLayout;

    /** Stores the contained components and their constraints. */
    private final List<ConstraintsData> components;

    /** Stores the size handler used by this layout. */
    private final UnitSizeHandler sizeHandler = new SwingSizeHandler();

    /**
     * Creates a new instance of {@code SwingPercentLayouAdaptert} and sets the
     * associated percent layout.
     *
     * @param percentLayout the percent layout object (must not be <b>null</b>)
     * @throws IllegalArgumentException if the {@code PercentLayoutBase} object
     *         is <b>null</b>
     */
    public SwingPercentLayoutAdapter(PercentLayoutBase percentLayout)
    {
        if (percentLayout == null)
        {
            throw new IllegalArgumentException(
                    "Percent layout must not be null!");
        }

        this.percentLayout = percentLayout;
        percentLayout.setPlatformAdapter(this);
        components = new ArrayList<ConstraintsData>();
    }

    /**
     * Returns a reference to the associated percent layout object.
     *
     * @return the percent layout object
     */
    public PercentLayoutBase getPercentLayout()
    {
        return percentLayout;
    }

    /**
     * Returns the layout alignment in X direction.
     *
     * @param container the associated container
     * @return the layout alignment in X direction
     */
    public float getLayoutAlignmentX(Container container)
    {
        return 0;
    }

    /**
     * Returns the layout alignment in Y direction.
     *
     * @param container the associated container
     * @return the layout alignment in Y direction
     */
    public float getLayoutAlignmentY(Container container)
    {
        return 0;
    }

    /**
     * Invalidates this layout. Clears all cached values.
     *
     * @param container the associated container
     */
    public void invalidateLayout(Container container)
    {
        invalidate();
    }

    /**
     * Returns the maximum layout size. For this layout type there is no upper
     * limit.
     *
     * @param container the container
     * @return the maximum layout size
     */
    public Dimension maximumLayoutSize(Container container)
    {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Adds a component to this layout.
     *
     * @param comp the component to add
     * @param constraints the constraints
     */
    public void addLayoutComponent(Component comp, Object constraints)
    {
        components.add(new ConstraintsData(comp, constraints));
        invalidate();
    }

    /**
     * Adds a component to this layout manager using the given name as
     * constraints object.
     *
     * @param name the name
     * @param comp the component
     */
    public void addLayoutComponent(String name, Component comp)
    {
        addLayoutComponent(comp, name);
    }

    /**
     * Removes the specified component from the layout manager.
     *
     * @param c the component to remove
     */
    public void removeLayoutComponent(Component c)
    {
        int index = -1;
        for (int i = 0; i < components.size() && index < 0; i++)
        {
            ConstraintsData cd = components.get(i);
            if (cd.getComponent().equals(c))
            {
                index = i;
            }
        }

        if (index >= 0)
        {
            components.remove(index);
            getPercentLayout().removeComponent(c);
        }
    }

    /**
     * Determines the coordinates and sizes of all components that belong to
     * this layout.
     *
     * @param container the associated container
     */
    public void layoutContainer(Container container)
    {
        synchronized (container.getTreeLock())
        {
            Insets insets = container.getInsets();
            Dimension size = container.getSize();
            getPercentLayout().performLayout(
                    container,
                    new Rectangle(insets.left, insets.top, insets.right,
                            insets.bottom), size);
        }
    }

    /**
     * Returns the minimum size of this layout. For this layout type this equals
     * the preferred layout size.
     *
     * @param container the associated container
     * @return the minimum layout size
     */
    public Dimension minimumLayoutSize(Container container)
    {
        return addInsets(getPercentLayout().calcMinimumLayoutSize(container),
                container);
    }

    /**
     * Returns the preferred size of this layout.
     *
     * @param container the associated container
     * @return the preferred layout size
     */
    public Dimension preferredLayoutSize(Container container)
    {
        return addInsets(getPercentLayout().calcPreferredLayoutSize(container),
                container);
    }

    /**
     * Returns the size handler used by this layout. This happens to be a swing
     * size handler.
     *
     * @return the size handler
     */
    public UnitSizeHandler getSizeHandler()
    {
        return sizeHandler;
    }

    /**
     * Returns the number of components in this layout.
     *
     * @return the number of components
     */
    public int getComponentCount()
    {
        return components.size();
    }

    /**
     * Returns the component with the given index.
     *
     * @param index the index
     * @return the component with this index
     */
    public Object getComponent(int index)
    {
        return components.get(index).getComponent();
    }

    /**
     * Returns the constraints for the component with the given index.
     *
     * @param index the index
     * @return the constraints for the specified component
     */
    public Object getConstraints(int index)
    {
        return components.get(index).getConstraints();
    }

    /**
     * Returns the minimum size of the specified component in the given axis.
     *
     * @param component the component
     * @param vert the flag for the axis
     * @return the minimum component size
     */
    public int getMinimumComponentSize(Object component, boolean vert)
    {
        Dimension d = ((Component) component).getMinimumSize();
        return PercentLayoutBase.getOrientationValue(d.width, d.height, vert);
    }

    /**
     * Returns the preferred size of the specified component in the given axis.
     *
     * @param component the component
     * @param vert the flag for the axis
     * @return the preferred component size
     */
    public int getPreferredComponentSize(Object component, boolean vert)
    {
        Dimension d = ((Component) component).getPreferredSize();
        return PercentLayoutBase.getOrientationValue(d.width, d.height, vert);
    }

    /**
     * Initializes the bounds for the specified component.
     *
     * @param component the component
     * @param bounds the new bounds
     */
    public void setComponentBounds(Object component, Rectangle bounds)
    {
        ((Component) component).setBounds(bounds);
    }

    /**
     * Invalidates the associated percent layout.
     */
    private void invalidate()
    {
        getPercentLayout().flushCache();
    }

    /**
     * Adds the container's insets to the specified dimension object.
     *
     * @param size the dimension object
     * @param container the container
     * @return the modified dimensions
     */
    private static Dimension addInsets(Dimension size, Container container)
    {
        Insets insets = container.getInsets();
        size.width += insets.left + insets.right;
        size.height += insets.top + insets.bottom;
        return size;
    }

    /**
     * A helper class for storing information about a component and its
     * constraints.
     */
    static class ConstraintsData implements Serializable
    {
        /**
         * The serial version UID.
         */
        private static final long serialVersionUID = 20090730L;

        /** Stores the component. */
        private final Component component;

        /** Stores the constraints object for this component. */
        private final Object constraints;

        /**
         * Creates a new instance of <code>ConstraintsData</code> and
         * initializes it.
         *
         * @param comp the component
         * @param constr the constraints
         */
        public ConstraintsData(Component comp, Object constr)
        {
            component = comp;
            constraints = constr;
        }

        /**
         * Returns the component.
         *
         * @return the component
         */
        public Component getComponent()
        {
            return component;
        }

        /**
         * Returns the constraints.
         *
         * @return the constraints
         */
        public Object getConstraints()
        {
            return constraints;
        }
    }
}

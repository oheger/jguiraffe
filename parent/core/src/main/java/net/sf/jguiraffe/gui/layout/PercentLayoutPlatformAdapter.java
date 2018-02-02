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

/**
 * <p>
 * Definition of an interface that encapsulates platform (library) specific
 * access to GUI components that are managed by a percent layout manager.
 * </p>
 * <p>
 * The family of percent layout managers is intended to work together with
 * different GUI libraries. To achieve this the classes cannot directly access
 * the managed components. They rather implement only the layouting algorithms
 * and delegate to a platform specific adapter when it comes to manipulating
 * components.
 * </p>
 * <p>
 * This interface defines how such an adapter looks like. It contains methods
 * that can roughly be devided into two different groups: One group allows
 * access to the components currently associated with this layout manager and
 * their layout constraints. The other group supports manipulating of components
 * and accessing their properties.
 * </p>
 * <p>
 * For each specific GUI library to be supported by percent layouts an adapter
 * class has to be created. This single adapter class will then play together
 * with all different percent layout implementations.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PercentLayoutPlatformAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface PercentLayoutPlatformAdapter
{
    /**
     * Returns the number of components that belong to this layout manager.
     *
     * @return the number of managed components
     */
    int getComponentCount();

    /**
     * Returns the component with the given index.
     *
     * @param index the index of a component (0-based)
     * @return the component with this index
     */
    Object getComponent(int index);

    /**
     * Returns the constraints object for the component with the given index.
     *
     * @param index the index of a component (0-based)
     * @return the constraints object for this component
     */
    Object getConstraints(int index);

    /**
     * Returns the platform specific <code>{@link UnitSizeHandler}</code>
     * implementation.
     *
     * @return the <code>SizeHandler</code> for this platform
     */
    UnitSizeHandler getSizeHandler();

    /**
     * Returns the minimum component size of the specified component for the
     * given axis.
     *
     * @param component the component
     * @param vert the direction flag (<code>true</code> for the y axis,
     * <b>false</b> for the x axis)
     * @return the minimum component size
     */
    int getMinimumComponentSize(Object component, boolean vert);

    /**
     * Returns the preferred component size of the specified component for the
     * given axis.
     *
     * @param component the component
     * @param vert the direction flag (<code>true</code> for the y axis,
     * <b>false</b> for the x axis)
     * @return the preferred component size
     */
    int getPreferredComponentSize(Object component, boolean vert);

    /**
     * Sets the bounds of a component. This method will be invoked after the
     * layout manager has calculated the final bounds of a component.
     *
     * @param component the affected component
     * @param bounds the bounds for this component
     */
    void setComponentBounds(Object component, Rectangle bounds);
}

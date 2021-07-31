/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

/**
 * <p>
 * An enumeration class that defines possible sizes for the cells of a
 * <em>percent layout</em>.
 * </p>
 * <p>
 * A constant defined by this enumeration class is used to specify the sizing
 * behavior of cells in the layout. It is evaluated to calculate the optimum
 * size of the container to layout. The constants represent different strategies
 * for querying the sizes of contained components. Based on these sizes the
 * required space is calculated.
 * </p>
 * <p>
 * The {@link CellConstraints} class has a property of this type.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CellSize.java 205 2012-01-29 18:29:57Z oheger $
 */
public enum CellSize
{
    /**
     * The size constant <em>preferred</em>. To determine the optimum size of a
     * cell the preferred size of the contained component is evaluated.
     */
    PREFERRED,

    /**
     * The size constant <em>minimum</em>. To determine the optimum size of a
     * cell the minimum size of the contained component is evaluated.
     */
    MINIMUM,

    /**
     * The size constant <em>none</em>. This means that the size of a component
     * contained in this cell is ignored. This is appropriate for cells that do
     * not contain components, but act as delimiters between other cells (e.g. a
     * column that separates labels from their associated input fields).
     * Typically these cells are assigned a fix size, so that no size
     * calculation is required.
     */
    NONE
}

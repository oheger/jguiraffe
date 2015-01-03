/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.tags.table;

/**
 * <p>
 * Definition of an interface for a component which can recalibrate itself based
 * on the column widths of a table.
 * </p>
 * <p>
 * This interface is used to access functionality provided by
 * {@link TableColumnWidthController} without having to reference the full
 * object. So if a client just needs this recalibration, it can use this
 * interface.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.3
 */
public interface TableColumnRecalibrator
{
    /**
     * Recalibrates the internally stored column sizes. This method is intended
     * to be called if there is an external change in the sizes of the columns
     * managed by this object, for instance if the user manually changed a
     * column width. In this case all current column widths have to be passed to
     * this method. The method then adjusts the sizes of the columns with fixed
     * widths and re-calculates the percent values of the other columns.
     *
     * @param columnSizes an array with the new sizes of the managed columns
     * @throws IllegalArgumentException if the array passed to this method is
     *         <b>null</b> or does not have the expected number of elements
     */
    void recalibrate(int[] columnSizes);
}

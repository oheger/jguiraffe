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
package net.sf.jguiraffe.gui.builder.components.tags.table;

/**
 * <p>
 * Definition of an interface for a component which can calculate the widths of
 * a table's columns given the total width of the table.
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
public interface TableColumnWidthCalculator
{
    /**
     * Calculates the current widths of all columns managed by this controller.
     * The currently available size of the table is specified. This method
     * mainly calculates the widths of columns with a relative width. It sums up
     * the fixed widths and subtracts them from the given total size. The
     * remaining space is used to calculate the absolute widths for columns
     * whose width is specified as a percent value.
     *
     * @param totalSize the total size available
     * @return an array with the widths of all columns
     */
    int[] calculateWidths(int totalSize);
}

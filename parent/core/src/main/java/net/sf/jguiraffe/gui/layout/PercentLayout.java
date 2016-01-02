/*
 * Copyright 2006-2016 The JGUIraffe Team.
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

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 * The concrete percent layout implementation.
 * </p>
 * <p>
 * This class implements a basic percent layout as it is described in the
 * documentation of the base class: a table-like layout with a set of properties
 * for each column and row. Please refer to this documentation for a more
 * complete description of all supported features.
 * </p>
 * <p>
 * Heart of this class is the implementation of the <code>initCells()</code>
 * method. This implementation expects that all components added to the layout
 * have been associated with valid constraints of type {@link PercentData}. It
 * will then initialize all cells of the layout correctly.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PercentLayout.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PercentLayout extends PercentLayoutBase implements Serializable
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20090730L;

    /**
     * Creates a new instance of <code>PercentLayout</code> and sets the
     * layout's dimensions. The constraints for the cells are set to default
     * values.
     *
     * @param cols the number of columns
     * @param rows the number of rows
     */
    public PercentLayout(int cols, int rows)
    {
        super(cols, rows);
    }

    /**
     * Creates a new instance of <code>PercentLayout</code> and initializes it
     * from the specified collections with {@link CellConstraints} objects.
     *
     * @param colConstr a collection with column constraints
     * @param rowConstr a collection with row constraints
     */
    public PercentLayout(Collection<? extends CellConstraints> colConstr,
            Collection<? extends CellConstraints> rowConstr)
    {
        super(colConstr, rowConstr);
    }

    /**
     * Creates a new instance of <code>PercentLayout</code> and initializes it
     * from the given string with specifications for the column and row
     * constraints. These strings must contain valid specifications of cell
     * constraints as defined in the documentation of {@link CellConstraints}.
     * As separators between two cell definitions the following characters can
     * be used: &quot; ,;&quot;.
     *
     * @param colConstr a string defining column constraints
     * @param rowConstr a string defining row constraints
     */
    public PercentLayout(String colConstr, String rowConstr)
    {
        super(colConstr, rowConstr);
    }

    /**
     * Performs initialization of this layout. Information about the dimensions
     * and the column and row constraints has already been set directly through
     * the constructors and the appropriate setter methods. The task of this
     * method is to initialize the cell array with information about the
     * contained components. Those are fetched from the passed in platform
     * adapter.
     *
     * @param adapter the platform adapter
     */
    @Override
    protected void initCells(PercentLayoutPlatformAdapter adapter)
    {
        clearCells();
        for (int i = 0; i < adapter.getComponentCount(); i++)
        {
            initCell(adapter.getComponent(i), adapter.getConstraints(i));
        }
    }
}

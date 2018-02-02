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
package net.sf.jguiraffe.gui.builder.components.tags.table;

/**
 * <p>
 * Definition of an interface that allows changing the way a selected or focused
 * cell in the table is displayed.
 * </p>
 * <p>
 * If custom editors or renderers are installed for columns of a table the
 * components used for this purpose must reflect the selected and/or focused
 * state of the represented cell. The platform specific table implementations
 * will provide defaults for doing this. If an application needs a very specific
 * way of marking cells as selected or focused, it can create a specialized
 * implementation of this interface.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TableSelectionHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface TableSelectionHandler
{
    /**
     * Prepares the renderer or editor component before it is displayed. This
     * method is called whenever a custom renderer or editor component is
     * requested. An implementation can initialize this component in an
     * arbitrary way based on the passed in parameters.
     *
     * @param table the table component
     * @param tableTag the tag representing the affected table
     * @param component the component that is used as custom renderer or editor
     * @param selected a flag whether the cell is selected
     * @param hasFocus a flag whether the cell has the focus
     * @param row the index of the affected row
     * @param col the index of the affected column
     */
    void prepareComponent(Object table, TableTag tableTag, Object component,
            boolean selected, boolean hasFocus, int row, int col);
}

/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import java.util.Set;

import net.sf.jguiraffe.gui.forms.Form;

/**
 * <p>
 * A tag for defining the editor of a column within a table.
 * </p>
 * <p>
 * In the body of this tag a single component (which can also be a container
 * with an arbitrary number of children) can be specified that will be used for
 * editing the column this tag belongs to. No other specific attributes are
 * supported.
 * </p>
 * <p>
 * A more detailed description can be found in the class comments of the super
 * class and the <code>TableTag</code> and the <code>ColumnTag</code>.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ColumnEditorTag.java 205 2012-01-29 18:29:57Z oheger $
 * @see TableTag
 * @see TableColumnTag
 */
public class ColumnEditorTag extends ColumnComponentTag
{
    /**
     * Returns the form affected by this tag from the specified table tag. This
     * implementation will return the edit form.
     *
     * @param tabTag the table tag
     * @return the form used by this tag
     */
    @Override
    protected Form getTableForm(TableTag tabTag)
    {
        return tabTag.getRowEditForm();
    }

    /**
     * Initializes the passed in column tag with the results produced by this
     * tag. This implementation sets the editor component and the set with the
     * names of the components used by the editor form for this column.
     *
     * @param colTag the parent column tag
     * @param bodyComponent the component defined in the body of this tag
     * @param componentNames a set with the names of the involved components
     */
    @Override
    protected void initializeColumn(TableColumnTag colTag,
            Object bodyComponent, Set<String> componentNames)
    {
        colTag.initEditFields(componentNames);
        colTag.initEditorComponent(bodyComponent);
    }
}

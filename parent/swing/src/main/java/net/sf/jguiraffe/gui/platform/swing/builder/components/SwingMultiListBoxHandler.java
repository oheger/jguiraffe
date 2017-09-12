/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import javax.swing.JList;

import net.sf.jguiraffe.gui.builder.components.model.ListModel;
import net.sf.jguiraffe.gui.builder.components.tags.ListModelUtils;

/**
 * <p>
 * A Swing specific component handler implementation that deals with multi
 * selection lists.
 * </p>
 * <p>
 * A multi selection list can have multiple items selected at the same time. So
 * the component's data is an array of selected objects.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingMultiListBoxHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingMultiListBoxHandler extends SwingListBoxHandler
{
    /**
     * Creates a new instance of {@code SwingMultiListBoxHandler}.
     *
     * @param list the list component
     * @param listModel the model
     * @param scrollWidth the preferred scroll width
     * @param scrollHeight the preferred scroll height
     */
    public SwingMultiListBoxHandler(JList list, ListModel listModel,
            int scrollWidth, int scrollHeight)
    {
        super(list, listModel, scrollWidth, scrollHeight);
    }

    /**
     * Returns this component's data. Based on the selection objects from the
     * list model will be returned.
     *
     * @return the component's data
     */
    @Override
    public Object getData()
    {
        return ListModelUtils.getValues(getListModel(), getList()
                .getSelectedIndices());
    }

    /**
     * Sets the data of this component.
     *
     * @param data the new data (which should be an array with items of the list
     * model)
     */
    @Override
    public void setData(Object data)
    {
        getList().setSelectedIndices(
                ListModelUtils.getIndices(getListModel(), (Object[]) data));
    }

    /**
     * Returns this component's data type. This is a generic array type.
     *
     * @return the data type of this component
     */
    @Override
    public Class<?> getType()
    {
        return Object[].class;
    }
}

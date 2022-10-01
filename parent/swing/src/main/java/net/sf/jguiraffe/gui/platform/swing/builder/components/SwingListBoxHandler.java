/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.jguiraffe.gui.builder.components.model.ListModel;
import net.sf.jguiraffe.gui.builder.components.tags.ListModelUtils;

/**
 * <p>
 * A specific Swing component handler implementation that deals with single
 * selection lists.
 * </p>
 * <p>
 * Single selection lists are quite similar to non editable combo boxes. Their
 * data is based on the provided list model.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingListBoxHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingListBoxHandler extends SwingListModelHandler implements
        ListSelectionListener
{
    /** Stores the list's scroll pane. */
    private final JScrollPane scrollPane;

    /**
     * Creates a new instance of {@code SwingListBoxHandler}.
     *
     * @param list the list component
     * @param listModel the list model
     * @param scrollWidth the preferred scroll width
     * @param scrollHeight the preferred scroll height
     */
    public SwingListBoxHandler(JList list, ListModel listModel,
            int scrollWidth, int scrollHeight)
    {
        this(list, listModel, SwingComponentUtils.scrollPaneFor(list,
                scrollWidth, scrollHeight));
    }

    /**
     * Creates a new instance of {@code SwingListBoxHandler} and passes in the
     * scroll pane to be used.
     *
     * @param list the list component
     * @param listModel the list model
     * @param scr the scroll pane
     * @since 1.4
     */
    public SwingListBoxHandler(JList list, ListModel listModel, JScrollPane scr)
    {
        super(list, listModel);
        scrollPane = scr;
    }

    /**
     * Returns the managed list.
     *
     * @return the managed list component
     */
    public JList getList()
    {
        return (JList) getComponent();
    }

    /**
     * Callback for list selection changes. These events are fired as change
     * events. Adjusting events are ignored.
     *
     * @param event the change event
     */
    public void valueChanged(ListSelectionEvent event)
    {
        if (!event.getValueIsAdjusting())
        {
            fireChangeEvent(event);
        }
    }

    /**
     * Registers this component handler as a change listener at the managed
     * component. This implementation registers a list selection listener.
     */
    @Override
    protected void registerChangeListener()
    {
        getList().addListSelectionListener(this);
    }

    /**
     * Removes this component handler as change listener from the managed
     * component.
     */
    @Override
    protected void unregisterChangeListener()
    {
        getList().removeListSelectionListener(this);
    }

    /**
     * Initializes the list box with the given list model.
     *
     * @param model the list model
     */
    @Override
    protected void initComponentModel(SwingListModel model)
    {
        getList().setModel(model);
    }

    /**
     * Returns this component's data. Based on the selected index an object from
     * the list model will be returned.
     *
     * @return the component's data
     */
    public Object getData()
    {
        return ListModelUtils
                .getValue(getListModel(), getList().getSelectedIndex());
    }

    /**
     * Sets the data of this component.
     *
     * @param data the new data (which should be an item of the list model)
     */
    public void setData(Object data)
    {
        if (data == null)
        {
            getList().clearSelection();
        }
        else
        {
            int index = ListModelUtils.getIndex(getListModel(), data);
            if (index >= 0)
            {
                getList().setSelectedIndex(index);
            }
            else
            {
                getList().clearSelection();
            }
        }
    }

    /**
     * Returns the outer most component. For lists this is a scroll pane.
     *
     * @return the outer most component
     */
    @Override
    public Object getOuterComponent()
    {
        return scrollPane;
    }
}

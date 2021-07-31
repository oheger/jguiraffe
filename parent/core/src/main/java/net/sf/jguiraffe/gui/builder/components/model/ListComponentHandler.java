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
package net.sf.jguiraffe.gui.builder.components.model;

import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A specialized component handler interface for components with list-like
 * structures.
 * </p>
 * <p>
 * This component handler interface extends the base interface by some methods
 * that allow access to the component's <code>{@link ListModel}</code> and
 * support its manipulation. This can be useful in cases where an application
 * needs to update displayed lists at runtime, e.g. in reaction of user input.
 * </p>
 * <p>
 * When dealing with list-like components like combo boxes or list boxes, the
 * <code>ComponentHandler</code> reference returned by the
 * <code>ComponentBuilderData</code> instance for these components can be casted into a
 * <code>ListComponentHandler</code>. Then the additional methods are
 * available.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ListComponentHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ListComponentHandler extends ComponentHandler<Object>
{
    /**
     * Returns the <code>ListModel</code> for this component. From this object
     * the list's current content can be obtained. Note that the object returned
     * here need not be identical to or even of the same class than the original
     * specified list model for the component. A component manager
     * implementation can provide a different <code>ListModel</code>
     * implementation.
     *
     * @return the component's list model
     */
    ListModel getListModel();

    /**
     * Adds an item to the component's list model. This new item will imediately
     * be displayed in the component's list.
     *
     * @param index the index where the item is to be inserted (0 based)
     * @param display the display object (to be displayed in the list)
     * @param value the corresponding value object (to be stored in the form's
     * data; can be <b>null</b>)
     */
    void addItem(int index, Object display, Object value);

    /**
     * Removes the list item at the given index.
     *
     * @param index the index of the item to remove
     */
    void removeItem(int index);
}

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
package net.sf.jguiraffe.gui.builder.components.model;

import net.sf.jguiraffe.gui.builder.components.AbstractCompositeComponentHandler;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A base class for {@code ComponentHandler} implementations for radio buttons.
 * </p>
 * <p>
 * Radio buttons typically are not used in isolation, but work together in a
 * group. This base class provides functionality to manage a number of child
 * {@code ComponentHandler} objects representing the radio buttons in the
 * associated group. It supports the conversion of the data of the child
 * handlers to a combined data object.
 * </p>
 * <p>
 * Because a number of radio buttons form a group an application is typically
 * not interested in the data of the single radio buttons (i.e. the selected
 * state). The relevant information is which radio button in the group is
 * selected. This information has to be encoded somehow and is stored in the
 * data of the hosting form. This base class provides two abstract methods for
 * dealing with this encoding:
 * <ul>
 * <li>{@code getDataForButton(int)} is called by {@link #getData()}. {@code
 * getData()} determines the index of the selected button in the group. Then
 * {@code getDataForButton()} has to return a corresponding data object.</li>
 * <li>{@code getButtonIndex()} is called by {@link #setData(Object)}. {@code
 * setData()} passes the value object to be set to {@code getButtonIndex()}
 * method in order to find out which radio button in the group must be selected.
 * </li>
 * </ul>
 * So these two methods basically implement a mapping between the index of the
 * selected radio button and the data of this composite component handler.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AbstractRadioButtonHandler.java 205 2012-01-29 18:29:57Z oheger $
 * @param <T> the type of the data supported by this {@code ComponentHandler}
 */
public abstract class AbstractRadioButtonHandler<T> extends
        AbstractCompositeComponentHandler<T, Boolean>
{
    /** Constant of an invalid index. */
    private static final int INVALID_INDEX = -1;

    /**
     * Creates a new instance of {@code AbstractRadioButtonHandler}.
     *
     * @param dataType the data type of this handler
     */
    protected AbstractRadioButtonHandler(Class<T> dataType)
    {
        super(dataType);
    }

    /**
     * Returns the data of this {@code ComponentHandler}. This implementation
     * determines which radio button in the associated group is selected. If
     * none is selected, the result of {@link #getUnselectedData()} is returned.
     * Otherwise, the object returned by {@link #getDataForButton(int)} is
     * returned.
     *
     * @return the data of this handler
     */
    public T getData()
    {
        int idx = 0;
        for (ComponentHandler<Boolean> radio : getChildHandlers())
        {
            Boolean value = radio.getData();
            if (value != null && value.booleanValue())
            {
                return getDataForButton(idx);
            }
            idx++;
        }

        return getUnselectedData();
    }

    /**
     * Sets the data of this handler. If the data passed in is <b>null</b>, the
     * index of the child handler to be selected is determined by calling
     * {@link #getUnselectedIndex()}. (This method may return an invalid index
     * causing all radio buttons to be unselected.) Otherwise,
     * {@link #getButtonIndex(Object)} is called with the passed in data object.
     * Then only the child handler with the returned index is set to
     * <b>true</b>, all others are set to <b>false</b>.
     *
     * @param data the data object to be set
     */
    public void setData(T data)
    {
        int selectedIndex = (data != null) ? getButtonIndex(data)
                : getUnselectedIndex();
        int idx = 0;

        for (ComponentHandler<Boolean> radio : getChildHandlers())
        {
            radio.setData(idx == selectedIndex);
            idx++;
        }
    }

    /**
     * Returns the data to be returned by this handler if none of the radio
     * buttons in this group is selected. This base implementation returns
     * <b>null</b>. Derived classes that support a default value can override
     * this method.
     *
     * @return the data to be returned if no radio button is selected
     */
    protected T getUnselectedData()
    {
        return null;
    }

    /**
     * Returns the index of the radio button that should be selected if this
     * handler does not contain any data. This method is called by {@code
     * setData()} if <b>null</b> is passed in. This base implementation returns
     * an invalid index (-1), which has the effect that no radio button is
     * selected. Derived classes can override this method if one of the radio
     * buttons should be selected per default.
     *
     * @return the index of the radio button to be selected if no data is
     *         available
     */
    protected int getUnselectedIndex()
    {
        return INVALID_INDEX;
    }

    /**
     * Returns the data value that corresponds to the radio button with the
     * given index. This method is called by {@link #getData()}. A concrete
     * implementation must return the data value that corresponds to the radio
     * button with the given index.
     *
     * @param idx the index of the radio button
     * @return the corresponding data value
     */
    protected abstract T getDataForButton(int idx);

    /**
     * Returns the index of the radio button that corresponds to the specified
     * data value. This method is called by {@link #setData(Object)}. {@code
     * setData()} then sets the data of this radio button to <b>true</b> and all
     * other to <b>false</b>.
     *
     * @param value the data value
     * @return the index of the corresponding radio button
     */
    protected abstract int getButtonIndex(T value);
}

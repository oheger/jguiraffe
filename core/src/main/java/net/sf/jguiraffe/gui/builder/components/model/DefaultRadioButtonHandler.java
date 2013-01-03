/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

/**
 * <p>
 * A concrete default implementation of a {@code CompositeComponentHandler} for
 * a group of radio buttons.
 * </p>
 * <p>
 * An instance of this class is used as {@code ComponentHandler} for a radio
 * button group if no specific handler class is specified. This implementation
 * just uses the index of the selected button in the group as data. So a Java
 * bean acting as model for a form that contains a radio button group should
 * have a corresponding property of type {@code Integer} for this group. Note
 * that really {@code Integer} should be used instead of {@code int} because the
 * value can be set to <b>null</b> if no radio button in the group is selected.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DefaultRadioButtonHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DefaultRadioButtonHandler extends
        AbstractRadioButtonHandler<Integer>
{
    /**
     * Creates a new instance of {@code DefaultRadioButtonHandler}.
     */
    public DefaultRadioButtonHandler()
    {
        super(Integer.class);
    }

    /**
     * Returns the index of the radio button in the associated group that
     * represents the passed in data value. Because the data value is
     * interpreted as the selected index it is simply returned.
     *
     * @param value the data value
     * @return the index of the corresponding radio button
     */
    @Override
    protected int getButtonIndex(Integer value)
    {
        assert value != null : "No index passed in!";
        return value.intValue();
    }

    /**
     * Returns the data value that corresponds to the radio button with the
     * given index. Again because there is a 1:1 mapping between radio button
     * index and data value the index can be directly returned as value.
     *
     * @param idx the index of the selected radio button
     * @return the corresponding data value
     */
    @Override
    protected Integer getDataForButton(int idx)
    {
        return idx;
    }
}

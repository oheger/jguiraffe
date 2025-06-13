/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
package net.sf.jguiraffe.examples.tutorial.viewset;

import net.sf.jguiraffe.gui.builder.components.model.AbstractRadioButtonHandler;

/**
 * <p>
 * A specialized {@code ComponentHandler} implementation for a radio button
 * group with only two elements.
 * </p>
 * <p>
 * This handler implementation is used for the radio button group which defines
 * the sort direction. It can be either <em>ascending</em> or
 * <em>descending</em>. For demonstration purposes we map these values to a
 * boolean flag (<b>true</b> means descending, <b>false</b> means ascending). It
 * would also be possible to use an enumeration type instead. This class mainly
 * demonstrates how a custom component handler for radio buttons can be created.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BooleanRadioButtonHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class BooleanRadioButtonHandler extends
        AbstractRadioButtonHandler<Boolean>
{
    /** Constant for the index of the DESCENDING button. */
    private static final int IDX_DESC = 1;

    /** Constant for the index of the ASCENDING button. */
    private static final int IDX_ASC = 0;

    /**
     * Creates a new instance of {@code BooleanRadioButtonHandler}
     */
    public BooleanRadioButtonHandler()
    {
        super(Boolean.TYPE);
    }

    /**
     * Returns the index of the button with the specified value. This method
     * maps data values of the radio group to specific radio buttons. Because in
     * our example <b>true</b> represents the <em>descending</em> button we
     * return the corresponding index.
     *
     * @param value the data value of the radio group
     * @return the index of the selected radio button
     */
    @Override
    protected int getButtonIndex(Boolean value)
    {
        assert value != null : "Null value passed in!";
        return value.booleanValue() ? IDX_DESC : IDX_ASC;
    }

    /**
     * Returns the value of the radio button with the specified index. This
     * method determines the data value for the whole radio group based on the
     * selected radio button. Here we return <b>true</b> if and only if the
     * <em>descending</em> radio button is selected.
     *
     * @param idx the index of the selected radio button
     * @return the corresponding value for the whole radio group
     */
    @Override
    protected Boolean getDataForButton(int idx)
    {
        return idx == IDX_DESC;
    }
}

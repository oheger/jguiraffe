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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import javax.swing.JComboBox;

import net.sf.jguiraffe.gui.builder.components.model.ListModel;

/**
 * <p>
 * A specific Swing component handler implementation that deals with editable
 * combo boxes.
 * </p>
 * <p>
 * An editable combo box is different from a default one in that it allows to
 * enter data that is not part of its list model. The <code>getData()</code>
 * and <code>setData()</code> must take this into account. The component's
 * data type is still derived from the list model, but in most cases this should
 * be String.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingEditableComboBoxHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingEditableComboBoxHandler extends SwingComboBoxHandler
{
    /**
     * Creates a new instance of <code>SwingEditableComboBoxHandler</code>.
     *
     * @param combo the managed combo box
     * @param model the list model
     */
    public SwingEditableComboBoxHandler(JComboBox combo, ListModel model)
    {
        super(combo, model);
    }

    /**
     * Returns this component's data. This is the data in the combo box's text
     * field.
     *
     * @return the component's data
     */
    public Object getData()
    {
        return getComboBox().getSelectedItem();
    }

    /**
     * Sets the component's data.
     *
     * @param data the new data
     */
    public void setData(Object data)
    {
        getComboBox().setSelectedItem(data);
    }
}

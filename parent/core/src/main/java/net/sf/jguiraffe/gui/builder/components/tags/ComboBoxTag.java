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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.model.ListModel;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specific input component tag that creates a combo box component.
 * </p>
 * <p>
 * A combo box displays a list of options from which the user can select one.
 * The options to be displayed are obtained from a
 * {@link net.sf.jguiraffe.gui.builder.components.model.ListModel ListModel}
 * object, which is set either by a tag in this tag's body or by specifying the
 * model's name in the <code>modelRef</code> attribute. A component handler that
 * wraps a combo box must maintain a single data object of the same type as the
 * list model (i.e. the return value of the model's <code>getType()</code>
 * method).
 * </p>
 * <p>
 * The following table lists all attributes that are supported by this tag (in
 * addition to the default attributes that are allowed for all input component
 * tags):
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">modelRef</td>
 * <td>With this attribute the name of the list model can be supplied. It is
 * then fetched from the current {@code BeanContext}. So a list model with this
 * name must have been created before, either directly by the calling client or
 * by another tag that has already been executed. If this attribute is not
 * defined, a list model must be set by a nested tag.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">editable</td>
 * <td>This boolean attribute determines whether the user can enter arbitrary
 * text in the combo box's text field. If set to <b>false </b>, the user can
 * only select one of the displayed options. For combo boxes that are editable
 * the data value is not obtained from the list model, but will be the data
 * entered by the user.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComboBoxTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ComboBoxTag extends InputComponentTag implements ListModelSupport
{
    /** Stores a reference to the list model. */
    private ListModel listModel;

    /** Stores the value of the modelRef attribute. */
    private String modelRef;

    /** Stores the editable flag. */
    private boolean editable;

    /**
     * Returns a flag whether this combo box is editable.
     *
     * @return the editable flag
     */
    public boolean isEditable()
    {
        return editable;
    }

    /**
     * Setter method of the editable attribute.
     *
     * @param editable the attribute value
     */
    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    /**
     * Returns the name of the model to use.
     *
     * @return the list model's name in the jelly context
     */
    public String getModelRef()
    {
        return modelRef;
    }

    /**
     * Setter method of the modelRef attribute.
     *
     * @param modelRef the attribute value
     */
    public void setModelRef(String modelRef)
    {
        this.modelRef = modelRef;
    }

    /**
     * Returns the list model to be used by this combo box.
     *
     * @return the list model
     */
    public ListModel getListModel()
    {
        return listModel;
    }

    /**
     * Sets the list model for this combo box. This method is intended to be
     * called by nested tags.
     *
     * @param model the list model
     */
    public void setListModel(ListModel model)
    {
        listModel = model;
    }

    /**
     * Creates the combo box component.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the handler for the newly created component
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected ComponentHandler<Object> createComponentHandler(
            ComponentManager manager, boolean create)
            throws FormBuilderException, JellyTagException
    {
        if (!create)
        {
            fetchListModel();
        }
        return manager.createComboBox(this, create);
    }

    /**
     * Helper method for obtaining the list model. If the model has not yet been
     * set by a nested tag, it is fetched from the context.
     *
     * @throws FormBuilderException if the model cannot be fetched
     * @throws JellyTagException if no model has been provided
     */
    private void fetchListModel() throws FormBuilderException,
            JellyTagException
    {
        ListModelUtils.initializeListModel(this);
    }
}

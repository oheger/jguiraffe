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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.model.ListModel;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A specific input component tag that creates a list box component.
 * </p>
 * <p>
 * A list box displays a list of options from which the user can select one or
 * many, depending on the style of the list. The options to be displayed are
 * obtained from a {@link ListModel} object, which is set either by a tag in
 * this tag's body or by specifying the model's name in the
 * <code>modelRef</code> attribute. The data type of a component handler that
 * wraps a list box must be based on the data type of the list model (i.e. the
 * return value of the model's <code>getType()</code> method): For single
 * selection lists it is a single object of this type, for multi selection lists
 * it is an array of this type.
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
 * then fetched from the current bean context. So a list model with this name
 * must have been created before, either directly by the calling client or by
 * another tag that has already been executed. If this attribute is not defined,
 * a list model must be set by a nested tag.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">multi</td>
 * <td>This boolean attribute determines whether the user can select only one or
 * multiple of the options displayed by the list. Note that this attribute has
 * impact of the data managed by the list's component handler (and thus on the
 * corresponding form bean property).</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valing="top">scrollWidth</td>
 * <td>Here the preferred width of the scroll pane enclosing the list can be
 * specified as a number with unit (e.g. &quot;1.5cm&quot;). If specified, the
 * scroll pane will have exactly this preferred width. Otherwise, the width is
 * determined by the preferred width of the list.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valing="top">scrollHeight</td>
 * <td>Here the preferred height of the scroll pane enclosing the list can be
 * specified as a number with unit (e.g. &quot;10dlu&quot;). If specified, the
 * scroll pane will have exactly this preferred height. Otherwise, the height is
 * determined by the preferred height of the list.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ListBoxTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ListBoxTag extends InputComponentTag implements ListModelSupport,
        ScrollSizeSupport
{
    /** Stores a reference to the list model. */
    private ListModel listModel;

    /** Stores the value of the modelRef attribute. */
    private String modelRef;

    /** The preferred scroll width converted to a number with unit. */
    private NumberWithUnit preferredScrollWidth;

    /** The preferred scroll height converted to a number with unit. */
    private NumberWithUnit preferredScrollHeight;

    /** The scroll width specification as string. */
    private String scrollWidth;

    /** The scroll height specification as string. */
    private String scrollHeight;

    /** Stores the multi flag. */
    private boolean multi;

    /**
     * Returns a flag whether the list box allows multi-selection.
     *
     * @return the multiple selection flag
     */
    public boolean isMulti()
    {
        return multi;
    }

    /**
     * Setter method of the {@code multi} attribute.
     *
     * @param multi the attribute value
     */
    public void setMulti(boolean multi)
    {
        this.multi = multi;
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
     * Returns the scroll width specification as a string.
     *
     * @return the scroll width specification
     */
    public String getScrollWidth()
    {
        return scrollWidth;
    }

    /**
     * Set method of the {@code scrollWidth} attribute.
     *
     * @param scrollWidth the attribute's value
     */
    public void setScrollWidth(String scrollWidth)
    {
        this.scrollWidth = scrollWidth;
    }

    /**
     * Returns the scroll height specification as a string.
     *
     * @return the scroll height specification
     */
    public String getScrollHeight()
    {
        return scrollHeight;
    }

    /**
     * Set method of the {@code scrollHeight} attribute.
     *
     * @param scrollHeight the attribute's value
     */
    public void setScrollHeight(String scrollHeight)
    {
        this.scrollHeight = scrollHeight;
    }

    /**
     * Returns the preferred scroll width. This value is calculated from the
     * string-based scroll width specification during tag processing. If no
     * scroll width has been specified, this method returns
     * {@link NumberWithUnit#ZERO}.
     *
     * @return the preferred scroll width
     */
    public NumberWithUnit getPreferredScrollWidth()
    {
        return preferredScrollWidth;
    }

    /**
     * Returns the preferred scroll height. This value is calculated from the
     * string-based scroll height specification during tag processing. If no
     * scroll height has been specified, this method returns
     * {@link NumberWithUnit#ZERO}.
     *
     * @return the preferred scroll height
     */
    public NumberWithUnit getPreferredScrollHeight()
    {
        return preferredScrollHeight;
    }

    /**
     * Creates the list box component.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the component handler for the newly created list box
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if this tag is incorrectly used
     */
    @Override
    protected ComponentHandler<?> createComponentHandler(ComponentManager manager,
            boolean create) throws FormBuilderException, JellyTagException
    {
        if (create)
        {
            preferredScrollWidth = convertToNumberWithUnit(getScrollWidth(),
                    NumberWithUnit.ZERO);
            preferredScrollHeight = convertToNumberWithUnit(getScrollHeight(),
                    NumberWithUnit.ZERO);
        }
        else
        {
            fetchListModel();
        }

        return manager.createListBox(this, create);
    }

    /**
     * Helper method for obtaining the list model. If the model has not yet been
     * set by a nested tag, it is fetched from the current bean context.
     *
     * @throws FormBuilderException if the model cannot be fetched
     * @throws MissingAttributeException if no model has been provided
     */
    private void fetchListModel() throws FormBuilderException,
            MissingAttributeException
    {
        ListModelUtils.initializeListModel(this);
    }
}

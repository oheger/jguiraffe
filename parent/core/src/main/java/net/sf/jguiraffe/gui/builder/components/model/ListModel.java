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
package net.sf.jguiraffe.gui.builder.components.model;

/**
 * <p>
 * Definition of an interface that is used to obtain the content of a list box
 * or combo box component.
 * </p>
 * <p>
 * When lists or combo boxes or similar components are to be displayed their
 * content must be specified. This can be a list of static texts in the most
 * simple cases, but also the results of a data base query. To support all of
 * these possibilities the form builder library introduces this interface which
 * serves as an abstraction of a real data source.
 * </p>
 * <p>
 * This interface defines methods for obtaining the data to be displayed. The
 * displayed data need not be the same that will be stored in the form bean for
 * this element, so it is also possible to define a specific value for each
 * element to be displayed. The <code>getType()</code> method will determine
 * the data type of the corresponding property in the form bean.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ListModel.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ListModel
{
    /**
     * Returns the number of entries in this model.
     *
     * @return the number of entries
     */
    int size();

    /**
     * Returns the display object with the given index. This object will be
     * displayed in the list box (or its string representation respective). A
     * concrete implementation must never return <b>null </b>.
     *
     * @param index the index (0 based)
     * @return the display object at this index
     */
    Object getDisplayObject(int index);

    /**
     * <p>
     * Returns the value object with the given index.
     * </p>
     * <p>
     * This method makes sense when the display objects are different from the
     * values that should be stored when corresponding display objects are
     * selected. E.g. the display objects might be simple strings that represent
     * complex data objects. When a string is selected the component's data
     * really is the data object behind this string. In this case the
     * <code>getValueObject()</code> method would return the complex data
     * objects, while <code>getDisplayObject()</code> would return
     * corresponding string representations.
     * </p>
     * <p>
     * When working with a fix set of options from which the user can select one
     * or some, it is often useful to store the indices of the selected display
     * objects. Imagine a combo box that offer the choices &quot;yes&quot;,
     * &quot;no&quot;, and &quot;maybe&quot;. In the bean of the form that
     * contains this combo box it makes sense to store the values 0, 1, or 2
     * depending on the user's selection rather than the (language dependend)
     * string values. To achieve this, the <code>getValueObject()</code>
     * method can always return <b>null </b>. Then as values always the
     * corresponding indices will be used as values. Note: an implementation
     * must not mix these variants; either it must return <b>null </b> for all
     * of its values or none.
     * </p>
     *
     * @param index the index (0 based)
     * @return the value object for this index
     */
    Object getValueObject(int index);

    /**
     * Returns the data object of this list model. This is the type of the value
     * objects stored in the data property for the corresponding component.
     *
     * @return the data type of this model
     */
    Class<?> getType();
}

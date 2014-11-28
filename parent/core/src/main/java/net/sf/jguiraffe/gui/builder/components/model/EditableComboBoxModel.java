/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
 * Definition of a specialized {@code ListModel} extension interface to be used
 * by editable combo boxes.
 * </p>
 * <p>
 * A {@code ListModel} per default contains a mapping from display objects (to
 * be presented on the UI) to value objects (the application uses internally)
 * and vice versa. This is fine as long the user can only select an element from
 * a given set of available options.
 * </p>
 * <p>
 * For an editable combo box situation is different. Here the user can enter
 * arbitrary values, and therefore it is possible that there is no mapping from
 * the display object to a value object in the model. This extended interface
 * addresses this problem. It defines explicit methods for mapping from display
 * objects to value objects or vice versa. These methods are called if no match
 * in the current {@code ListModel} is found. A concrete implementation can then
 * perform a transformation as it pleases.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: $
 */
public interface EditableComboBoxModel
{
    /**
     * Transforms a value object to a display object. This method is called at
     * initialization of a component if the object obtained from the data model
     * cannot be matched in the list model. An implementation is then
     * responsible for mapping it to a correct object that can be displayed by
     * the combo box.
     *
     * @param value the value object to be transformed
     * @return the corresponding display object for this value
     */
    Object toDisplay(Object value);

    /**
     * Transforms a display object to a value object. This method is called when
     * data entered by the user is to be written back into the data model and
     * the selected object of the combo box cannot be matched in the list model.
     * An implementation then has to create a value object to be passed to the
     * data model.
     *
     * @param displ the display object to be transformed
     * @return the corresponding value object
     */
    Object toValue(Object displ);
}

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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.table

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.scene.control.Cell

/**
 * A trait for extracting the ''selected'' property from a cell.
 *
 * This trait is mainly used for testing purposes. A cell's selected state cannot
 * be set manually. Therefore, some tricks are needed to manipulate this flag in
 * a unit test.
 */
trait CellSelectionExtractor {
  def selectedProperty(cell: Cell[_]): ReadOnlyBooleanProperty =
    cell.selectedProperty
}

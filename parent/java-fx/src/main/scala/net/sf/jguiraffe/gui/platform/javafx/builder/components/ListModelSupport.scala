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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import javafx.collections.ObservableList
import net.sf.jguiraffe.gui.builder.components.model.ListComponentHandler
import net.sf.jguiraffe.gui.builder.components.model.ListModel
import net.sf.jguiraffe.gui.builder.components.tags.ListModelUtils

/**
 * Definition of a trait supporting component handlers which manage controls
 * with a list model.
 *
 * This trait can be mixed in handlers for combo boxes and lists. It implements
 * functionality for managing a list model, including adding or removing model
 * elements.
 *
 * This implementation expects that the ''initListModel()'' is called before
 * the object is actually used. Because it is used internally only, no
 * sophisticated checks are in place.
 */
private trait ListModelSupport extends ListComponentHandler {
  /** The ''EditableComboBoxModel'' provided by the associated model. */
  lazy val editableModel = initEditableComboBoxModel()

  /**
   * The list to be used for backing the ''JavaFxListModel'' object's display
   * list. This list is associated with a JavaFX control.
   */
  protected val displayList: ObservableList[Object]

  /** Stores the original data model from which this object is initialized. */
  private var originalListModel: ListModel = _

  /** Stores the FX list model used by this object. */
  private var fxListModel: JavaFxListModel = _

  /**
   * Initializes the ''JavaFxListModel'' managed by this object from the passed
   * in generic list model.
   * @param dataModel the underlying list model
   */
  def initListModel(dataModel: ListModel) {
    originalListModel = dataModel
    fxListModel = new JavaFxListModel(displayList, dataModel.getType)
    fxListModel.initFromModel(dataModel)
  }

  def getListModel: ListModel = fxListModel

  /**
   * @inheritdoc This implementation adds the new item to the FX list model.
   */
  def addItem(index: Int, display: Object, value: Object) {
    fxListModel.insertItem(index, display, value)
  }

  /**
   * @inheritdoc This implementation removes the specified element from the FX
   * list model.
   */
  def removeItem(index: Int) {
    fxListModel.removeItem(index)
  }

  /**
   * Initializes an ''EditableComboBoxModel'' from the original list model.
   * @return the ''EditableComboBoxModel''
   */
  private def initEditableComboBoxModel() =
    ListModelUtils.fetchEditableComboBoxModel(originalListModel)
}

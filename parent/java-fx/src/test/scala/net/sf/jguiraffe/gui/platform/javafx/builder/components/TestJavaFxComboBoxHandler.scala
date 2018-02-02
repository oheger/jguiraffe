/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.{BeforeClass, Before, Test}

import javafx.scene.control.ComboBox
import net.sf.jguiraffe.gui.builder.components.model.EditableComboBoxModel

object TestJavaFxComboBoxHandler {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''JavaFxComboBoxHandler''. This class also tests functionality
 * provided by the ''ListModelSupport'' trait.
 */
class TestJavaFxComboBoxHandler {
  /** A suffix to be added to a transformed display object. */
  private val TransformedDisplay = "_dispTransformed"

  /** A suffix to be added to a transformed value object. */
  private val TransformedValue = "_valueTransformed"

  /** The combo box component passed to the handler. */
  private var combo: ComboBox[Object] = _

  /** The handler to be tested. */
  private var handler: JavaFxComboBoxHandler = _

  @Before def setUp() {
    combo = new ComboBox
    handler = new JavaFxComboBoxHandler(combo)
  }

  /**
   * Initializes the handler's list model and returns it.
   * @return the test list model
   */
  private def initListModel(): ListModelTestImpl = {
    val model = new ListModelTestImpl
    handler initListModel model
    model
  }

  /**
   * Tests whether the handler returns the correct type.
   */
  @Test def testType() {
    val model = initListModel()
    assertEquals("Wrong type", model.getType, handler.getType)
  }

  /**
   * Tests whether the handler correctly initializes its list model.
   */
  @Test def testInitializedListModel() {
    val model = initListModel()
    val handlerModel = handler.getListModel
    assertEquals("Wrong model size", model.size, handlerModel.size)
    var idx = 0
    while (idx < model.size) {
      assertEquals("Wrong display object at " + idx, model.getDisplayObject(idx),
        handlerModel.getDisplayObject(idx))
      assertEquals("Wrong value object at " + idx, model.getValueObject(idx),
        handlerModel.getValueObject(idx))
      idx = idx + 1
    }
  }

  /**
   * Tests whether the combo box's item collection is used as display collection.
   */
  @Test def testDisplayCollection() {
    initListModel()
    val fxModel = handler.getListModel.asInstanceOf[JavaFxListModel]
    assertSame("Wrong display collection", combo.getItems, fxModel.displayList)
  }

  /**
   * Tests a getData() operation for a non editable combo box.
   */
  @Test def testGetDataNotEditableFromModel() {
    val model = initListModel()
    val selIdx = 1
    combo.getSelectionModel.select(selIdx)
    assertEquals("Wrong data", model.getValueObject(selIdx), handler.getData)
  }

  /**
   * Tests whether data from an empty combo box is correctly returned.
   */
  @Test def testGetDataNonEditableNoContent() {
    initListModel()
    combo.getSelectionModel.clearSelection()
    assertNull("Got data", handler.getData)
  }

  /**
   * Tests a getData() operation for an editable combo box if the data object
   * is part of the model.
   */
  @Test def testGetDataEditableFromModel() {
    val model = initListModel()
    val selIdx = 2
    combo setEditable true
    combo setValue model.getDisplayObject(selIdx)
    assertEquals("Wrong data", model.getValueObject(selIdx), handler.getData)
  }

  /**
   * Tests getData() if the control contains a value not defined in the model.
   */
  @Test def testGetDataEditableNotFromModel() {
    val value = "CurrentValue"
    handler initListModel (new EditableListModel)
    combo setEditable false
    combo setValue value
    assertEquals("Wrong data", value + TransformedValue, handler.getData)
  }

  /**
   * Tests whether a non editable combo box can be set to null.
   */
  @Test def testSetDataNullNonEditable() {
    initListModel()
    handler setData null
    assertNull("Got data", combo.getValue)
  }

  /**
   * Tests whether an editable combo box can be set to null.
   */
  @Test def testSetDataNullEditable() {
    initListModel()
    combo setEditable true
    handler setData null
    assertNull("Got data", combo.getValue)
  }

  /**
   * Tests whether for a non editable combo box data can be set.
   */
  @Test def testSetDataNonEditable() {
    val model = initListModel()
    val selIdx = 3
    handler setData (model.getValueObject(selIdx))
    assertEquals("Wrong data", model.getDisplayObject(selIdx), combo.getValue)
  }

  /**
   * Tests a setData() operation on an editable combo box if the data is
   * contained in the model.
   */
  @Test def testSetDataEditableFromModel() {
    val model = initListModel()
    val selIdx = 4
    handler setData (model.getValueObject(selIdx))
    assertEquals("Wrong data", model.getDisplayObject(selIdx), combo.getValue)
  }

  /**
   * Tests a setData() operation on an editable combo box if the data is not
   * contained in the model.
   */
  @Test def testSetDataEditableNotFromModel() {
    val value = "SomeNewTestValue"
    handler.initListModel(new EditableListModel)
    combo setEditable true
    handler setData value
    assertEquals("Wrong data", value + TransformedDisplay, combo.getValue)
  }

  /**
   * Tests whether an item can be added to the list model.
   */
  @Test def testAddItem() {
    val model = initListModel()
    val newValue = "NewValueObject"
    val newDisplay = "NewDisplayObject"
    val idx = 4
    handler.addItem(idx, newDisplay, newValue)
    assertEquals("Item not added", model.size + 1, handler.getListModel.size)
    assertEquals("Wrong new display object", newDisplay,
      handler.getListModel.getDisplayObject(idx))
    assertEquals("Wrong new value object", newValue,
      handler.getListModel.getValueObject(idx))
  }

  /**
   * Tests whether an item can be removed from the list model.
   */
  @Test def testRemoveItem() {
    val model = initListModel()
    val idx = 4
    handler.removeItem(idx)
    assertEquals("Item not removed", model.size - 1, handler.getListModel.size)
    assertEquals("Wrong removed display object", model.getDisplayObject(idx + 1),
      handler.getListModel.getDisplayObject(idx))
    assertEquals("Wrong removed value object", model.getValueObject(idx + 1),
      handler.getListModel.getValueObject(idx))
  }

  /**
   * Tests whether the correct property is returned for the change event
   * source.
   */
  @Test def testChangeEventSource() {
    assertEquals("Wrong property", combo.valueProperty, handler.observableValue)
  }

  /**
   * A specialized list model implementation which also implements the
   * ''EditableComboBoxModel'' interface.
   */
  class EditableListModel extends ListModelTestImpl with EditableComboBoxModel {
    def toDisplay(obj: Object) = obj + TransformedDisplay

    def toValue(obj: Object) = obj + TransformedValue
  }
}

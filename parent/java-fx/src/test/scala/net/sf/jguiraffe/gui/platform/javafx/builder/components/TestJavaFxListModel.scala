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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.scalatest.junit.JUnitSuite

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import net.sf.jguiraffe.gui.builder.components.model.ListModel

/**
 * Test class for ''JavaFxListModel''.
 */
class TestJavaFxListModel extends JUnitSuite {
  /**
   * Returns a new observable array list.
   * @return the new list
   */
  private def displayList(): ObservableList[Object] =
    FXCollections.observableArrayList()

  /**
   * Creates a test list model and initializes it.
   * @return the initialized test list model
   */
  private def prepareModel(): JavaFxListModel = {
    val orgModel = new ListModelTestImpl
    val model = new JavaFxListModel(displayList(), classOf[String])
    model initFromModel orgModel
    model
  }

  /**
   * Helper method for testing whether the given model contains the expected
   * display object at the given index.
   * @param model the model to check
   * @param idx the index to be checked
   * @param expIdx the index of the expected display object
   */
  private def checkDisplayAt(model: ListModel, idx: Int, expIdx: Int) {
    assertEquals("Wrong display object at " + idx,
      ListModelTestImpl.DisplayPrefix + expIdx, model.getDisplayObject(idx))
  }

  /**
   * Helper method for testing whether the standard display object is found at
   * the specified index.
   * @param model the model to check
   * @param idx the index to be checked
   */
  private def checkDisplayAt(model: ListModel, idx: Int) {
    checkDisplayAt(model, idx, idx)
  }

  /**
   * Helper method for testing whether the given model contains the expected
   * value object at the given index.
   * @param model the model to check
   * @param idx the index to be checked
   * @param expIdx the index of the expected value object
   */
  private def checkValueAt(model: ListModel, idx: Int, expIdx: Int) {
    assertEquals("Wrong value object at " + idx,
      ListModelTestImpl.ValuePrefix + expIdx, model.getValueObject(idx))
  }

  /**
   * Helper method for testing whether the standard value object is found at
   * the specified index.
   * @param model the model to check
   * @param idx the index to be checked
   */
  private def checkValueAt(model: ListModel, idx: Int) {
    checkValueAt(model, idx, idx)
  }

  /**
   * Tests whether the expected type is returned.
   */
  @Test def testType() {
    val model = new JavaFxListModel(displayList(), classOf[String])
    assertEquals("Wrong type", classOf[String], model.`type`)
    assertEquals("Wrong getType", classOf[String], model.getType)
  }

  /**
   * Tests whether the expected size is returned.
   */
  @Test def testSize() {
    val model = prepareModel()
    assert(ListModelTestImpl.DefaultSize === model.size)
  }

  /**
   * Tests whether the correct display objects are returned.
   */
  @Test def testGetDisplayObject() {
    val model = prepareModel()
    checkDisplayAt(model, 0)
    checkDisplayAt(model, ListModelTestImpl.DefaultSize - 1)
  }

  /**
   * Tests whether the correct value objects are returned.
   */
  @Test def testGetValueObject() {
    val model = prepareModel()
    checkValueAt(model, 0)
    checkValueAt(model, ListModelTestImpl.DefaultSize - 1)
  }

  /**
   * Tests whether the passed in value collection is correctly filled.
   */
  @Test def testPopulateValueCollection() {
    val list = displayList()
    val model = new JavaFxListModel(list, classOf[String])
    model initFromModel (new ListModelTestImpl)
    assertEquals("Wrong list size", ListModelTestImpl.DefaultSize, list.size)
    var idx = 0
    while (idx < ListModelTestImpl.DefaultSize) {
      assertEquals("Wrong display object at " + idx,
        ListModelTestImpl.DisplayPrefix + idx, list.get(idx))
      idx = idx + 1
    }
  }

  /**
   * Tests an instance which has not yet been initialized from a model.
   */
  @Test def testUninitialized() {
    val list = displayList()
    val model = new JavaFxListModel(list, classOf[String])
    assert(0 === model.size)
    assertTrue("Data in display list", list.isEmpty)
  }

  /**
   * Tests whether a new item can be inserted.
   */
  @Test def testInsertItem() {
    val idx = 4
    val model = prepareModel()
    model.insertItem(idx, ListModelTestImpl.DisplayPrefix,
      ListModelTestImpl.ValuePrefix)
    assertEquals("Wrong new size", ListModelTestImpl.DefaultSize + 1, model.size)
    assertEquals("Wrong display object", ListModelTestImpl.DisplayPrefix,
      model.getDisplayObject(idx))
    assertEquals("Wrong value object", ListModelTestImpl.ValuePrefix,
      model.getValueObject(idx))
    checkDisplayAt(model, idx - 1)
    checkValueAt(model, idx - 1)
    checkDisplayAt(model, idx + 1, idx)
    checkValueAt(model, idx + 1, idx)
  }

  /**
   * Tests whether an item can be removed.
   */
  @Test def testRemoveItem() {
    val idx = 6
    val model = prepareModel()
    model.removeItem(idx)
    assertEquals("Wrong new size", ListModelTestImpl.DefaultSize - 1, model.size)
    checkDisplayAt(model, idx - 1)
    checkValueAt(model, idx - 1)
    checkDisplayAt(model, idx, idx + 1)
    checkValueAt(model, idx, idx + 1)
  }
}

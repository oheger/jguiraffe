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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import java.util.Arrays

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.{BeforeClass, Before, Test}

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import net.sf.jguiraffe.gui.builder.components.model.ListModel

object TestJavaFxMultiSelectionListHandler {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''JavaFxMultiSelectionListHandler''.
 */
class TestJavaFxMultiSelectionListHandler {
  /** The list view control to be managed by the test instance. */
  private var listView: ListView[Object] = _

  /** The list model used by the handler. */
  private var listModel: ListModel = _

  /** The handler to be tested. */
  private var handler: JavaFxMultiSelectionListHandler = _

  @Before def setUp() {
    listView = new ListView
    listModel = new ListModelTestImpl
    handler = new JavaFxMultiSelectionListHandler(listView)
    handler initListModel listModel
  }

  /**
   * Tests whether the correct display list is returned.
   */
  @Test def testDisplayList() {
    val model = handler.getListModel.asInstanceOf[JavaFxListModel]
    assertSame("Wrong display list", listView.getItems, model.displayList)
  }

  /**
   * Tests whether the correct type is returned.
   */
  @Test def testGetType() {
    assertEquals("Wrong type", listModel.getType, handler.getType)
  }

  /**
   * Tests whether the list's selection model has been correctly initialized.
   */
  @Test def testSelectionModel() {
    assertEquals("Wrong selection mode", SelectionMode.MULTIPLE,
      listView.getSelectionModel.getSelectionMode())
  }

  /**
   * Tests whether data can be queried if there is no selection.
   */
  @Test def testGetDataNoSelection() {
    listView.getSelectionModel.clearSelection()
    assertNull("Got data", handler.getData)
  }

  /**
   * Tests whether getData() returns the selected indices.
   */
  @Test def testGetDataSelectedIndices() {
    val idx1 = 1
    val idx2 = 4
    val expected = Array(listModel.getValueObject(idx1),
      listModel.getValueObject(idx2))
    listView.getSelectionModel().selectIndices(idx1, idx2)
    assertTrue("Wrong result",
      Arrays.equals(expected, handler.getData.asInstanceOf[Array[Object]]))
  }

  /**
   * Helper method for testing setData() if there is no selection.
   * @param data the data object to be passed to the handler
   */
  private def checkSetDataNoSelection(data: Object) {
    listView.getSelectionModel.selectAll()
    handler setData data
    assertTrue("Got a selection",
      listView.getSelectionModel.getSelectedIndices.isEmpty)
  }

  /**
   * Tests setData() for null input.
   */
  @Test def testSetDataNull() {
    checkSetDataNoSelection(null)
  }

  /**
   * Tests setData() if an empty array is passed in.
   */
  @Test def testSetDataEmptyArray() {
    checkSetDataNoSelection(Array.empty[Object])
  }

  /**
   * Tests setData() if value objects are provided.
   */
  @Test def testSetDataValues() {
    val indices = Array(0, 1, 4, 7)
    val values = indices map { listModel.getValueObject(_) }
    listView.getSelectionModel.selectIndices(2, 3, 5, 6)
    handler setData values
    val selection = listView.getSelectionModel.getSelectedIndices
    assertEquals("Wrong number of selected indices", indices.length,
      selection.size)
    val it = selection.iterator
    while (it.hasNext) {
      val elem = it.next()
      assertTrue("Unexpected value: " + elem, indices.contains(elem.intValue))
    }
  }

  /**
   * Tests whether setData() can handle a single selection.
   */
  @Test def testSetDataSingleValue() {
    val idx = 3
    handler setData Array(listModel.getValueObject(idx))
    val selection = listView.getSelectionModel.getSelectedIndices
    assertEquals("Wrong number of selected indices", 1, selection.size)
    assertEquals("Wrong selection", idx, selection.get(0).intValue)
  }

  /**
   * Tests whether the correct property is used for the change event source.
   */
  @Test def testChangeEventSource() {
    var triggeredChanges: Int = 0
    handler.observableValue.addListener(new ChangeListener[Number] {
      def changed(obs: ObservableValue[_ <: Number], oldVal: Number, newVal: Number) {
        triggeredChanges += 1
      }
    })
    listView.getSelectionModel.select(1)
    assertTrue("No change event", triggeredChanges > 0)
  }
}

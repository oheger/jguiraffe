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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.scalatest.junit.JUnitSuite

import javafx.scene.control.ListView
import net.sf.jguiraffe.gui.builder.components.model.ListModel

/**
 * Test class for ''JavaFxListViewHandler''.
 */
class TestJavaFxListViewHandler extends JUnitSuite {
  /** The list view control to be managed by the test instance. */
  private var listView: ListView[Object] = _

  /** The list model used by the handler. */
  private var listModel: ListModel = _

  /** The handler to be tested. */
  private var handler: JavaFxListViewHandler = _

  @Before def setUp() {
    listView = new ListView
    listModel = new ListModelTestImpl
    handler = new JavaFxListViewHandler(listView)
    handler initListModel listModel
  }

  /**
   * Checks that the test list has no selection.
   */
  private def checkNoSelection() {
    assertTrue("Got a selection",
      listView.getSelectionModel.getSelectedIndices.isEmpty);
    assertNull("Got a selected item", listView.getSelectionModel.getSelectedItem)
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
   * Tests setData() for null input.
   */
  @Test def testSetDataNull() {
    listView.getSelectionModel.selectAll()
    handler setData null
    checkNoSelection()
  }

  /**
   * Tests setData() if a model object is passed in.
   */
  @Test def testSetDataModelObject() {
    val idx = 4
    handler setData listModel.getValueObject(idx)
    assertEquals("Wrong selected index", idx,
      listView.getSelectionModel.getSelectedIndex)
  }

  /**
   * Tests setData() if an unknown object is passed in.
   */
  @Test def testSetDataModelOtherObject() {
    listView.getSelectionModel.selectAll()
    handler setData "someUnknownObject"
    checkNoSelection()
  }

  /**
   * Tests getData() if there is no selection.
   */
  @Test def testGetDataNoSelection() {
    listView.getSelectionModel.clearSelection()
    assertNull("Got data", handler.getData)
  }

  /**
   * Tests whether the selected item can be obtained.
   */
  @Test def testGetDataSelection() {
    val idx = 2
    listView.getSelectionModel.select(idx)
    assertEquals("Wrong data", listModel.getValueObject(idx), handler.getData)
  }

  /**
   * Tests whether the expected property for change events is returned.
   */
  @Test def testChangeEventSource() {
    assertSame("Wrong observable value",
      listView.getSelectionModel.selectedIndexProperty, handler.observableValue)
  }
}

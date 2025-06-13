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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.table

import org.loadui.testfx.GuiTest
import scala.beans.BeanProperty
import javafx.scene.control.{TableColumn, TableView}
import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController
import org.easymock.{IAnswer, EasyMock}
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.cell.PropertyValueFactory
import javafx.collections.FXCollections
import javafx.scene.{Node, Parent}
import org.junit.Test
import org.junit.Assert._
import javafx.scene.input.KeyCode
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper

/**
 * TestFX test class for ''EditableTableCell''.
 *
 * @author Oliver Heger
 * @version $Id$
 */
class UITestEditableTableCell extends GuiTest {
  import JavaFxTestHelper.functionToCallback
  import GuiTest._

  /** A mock for the form controller. */
  private var formController: TableFormController = _

  /** An array with the content of the table. */
  private var tableData: Array[Author] = _

  /** The number of the current row in the table. */
  private var currentRow = 0

  /**
   * Sets up this test case and returns the UI control to be tested.
   * This is a table containing cells of the test class.
   * @return the test table
   */
  override def getRootNode: Parent = {
    tableData = Array(new Author("Edgar", "Alan", "Poe"), new Author("Peter", "F", "Hamilton"),
      new Author("Philip", "K", "Dick"))
    formController = createFormController()
    createTable()
  }

  private def createFormController(): TableFormController = {
    val controller: TableFormController = EasyMock.createMock(classOf[TableFormController])
    controller selectCurrentRow EasyMock.anyInt()
    EasyMock.expectLastCall().andAnswer(new IAnswer[Object] {
      override def answer(): Object = {
        currentRow = EasyMock.getCurrentArguments()(0).asInstanceOf[Int]
        null
      }
    }).anyTimes()
    EasyMock.expect(controller.getColumnValue(EasyMock.anyInt())).andAnswer(new IAnswer[Object] {
      override def answer(): Object = {
        tableData(currentRow) getProperty EasyMock.getCurrentArguments()(0).asInstanceOf[Int]
      }
    }).anyTimes()
    controller.setColumnValue(EasyMock.anyObject(), EasyMock.anyInt(), EasyMock.anyObject())
    EasyMock.expectLastCall().andAnswer(new IAnswer[Object] {
      override def answer(): Object = {
        val index = EasyMock.getCurrentArguments()(1).asInstanceOf[Int]
        val value = String.valueOf(EasyMock.getCurrentArguments()(2))
        tableData(currentRow).setProperty(index, value)
        null
      }
    }).anyTimes()

    EasyMock.replay(controller)
    controller
  }

  /**
   * Creates the table view used by this test class. The table has three columns
   * corresponding to the properties of the test bean. The first and the last
   * column are editable.
   * @return the test table view
   */
  private def createTable(): TableView[AnyRef] = {
    def createEditableColumn(name: String, index: Int): TableColumn[AnyRef, AnyRef] = {
      val column = new TableColumn[AnyRef, AnyRef](name)
      column setCellValueFactory functionToCallback(f => new SimpleObjectProperty[AnyRef](f
        .getValue))
      column setCellFactory functionToCallback(f => new EditableTableCell(formController, index))
      column
    }

    val colMiddle = new TableColumn[AnyRef, String]("Middle Name")
    colMiddle setCellValueFactory new PropertyValueFactory("middleName")
    colMiddle setEditable false
    val table = new TableView[AnyRef]
    table setEditable true
    table.getColumns.addAll(createEditableColumn("First Name", 0),
      colMiddle, createEditableColumn("Last Name", 2))

    val data = FXCollections.observableArrayList[AnyRef]
    tableData foreach (a => data.add(a))
    table setItems data
    table
  }

  /**
   * Helper method for finding a cell by its label. Causes the test to fail
   * if this label cannot be found.
   * @param label the label to be searched for
   * @return the node for the cell with this label
   */
  private def findCell(label: String): Node = find[Node](label)

  /**
   * Starts edit mode of the cell defined by the given label. If such a cell
   * cannot be found, the test fails.
   * @param label the label of the desired cell
   * @return the node on which edit mode was enabled
   */
  private def startEdit(label: String): Node = {
    val node: Node = findCell(label)
    click(node).click(node)
    node
  }

  /**
   * Starts edit mode in the first cell of the specified row.
   * @param row the row index
   * @return the node on which edit mode was enabled
   */
  private def startEdit(row: Int): Node =
    startEdit(tableData(row).firstName)

  /**
   * Tests whether the table contains the expected content.
   */
  @Test def testTableContent() {
    def findAuthor(author: Author) {
      find(author.firstName)
      find(author.lastName)
      find(author.middleName)
    }

    tableData foreach findAuthor
  }

  /**
   * Tests whether a cell can be edited.
   */
  @Test def testEditCell() {
    val Row = 0
    val NewName = "New"
    startEdit(Row)
    `type`(NewName)
    push(KeyCode.ENTER)
    find(NewName)
    assertEquals("Name not changed", NewName, tableData(Row).firstName)
  }

  /**
   * Tests whether an edit operation can be canceled.
   */
  @Test def testCancelEdit() {
    val Row = 1
    val oldName = tableData(Row).firstName
    startEdit(Row)
    `type`("xx")
    push(KeyCode.ESCAPE, KeyCode.ENTER)
    find(oldName)
    assertEquals("Name was changed", oldName, tableData(Row).firstName)
  }

  /**
   * Tests whether a focus lost event causes a commit, too.
   */
  @Test def testCommitOnFocusLost() {
    val Row = 0
    val NewName = "New"
    startEdit(Row)
    `type`(NewName)
    click(findCell(tableData(Row).middleName))
    find(NewName)
    assertEquals("Name not changed", NewName, tableData(Row).firstName)
  }

  /**
   * Helper method for testing whether the tab key works as expected when a cell
   * is edited.
   * @param row the row to be used
   * @param propertyIdx1 the index of the first property to be edited
   * @param propertyIdx2 the index of the 2nd property to be edited
   * @param shift flag whether the SHIFT key should be pressed
   */
  private def checkTabHandling(row: Int, propertyIdx1: Int, propertyIdx2: Int, shift: Boolean) {
    val NewName1 = "New1"
    val NewName2 = "New2"
    startEdit(tableData(row).getProperty(propertyIdx1))
    `type`(NewName1)
    if (shift) {
      press(KeyCode.SHIFT).push(KeyCode.TAB).release(KeyCode.SHIFT)
    } else {
      push(KeyCode.TAB)
    }
    `type`(NewName2)
    push(KeyCode.ENTER)

    find(NewName1)
    find(NewName2)
    assertEquals("First property not changed", NewName1, tableData(row).getProperty(propertyIdx1))
    assertEquals("2nd property not changed", NewName2, tableData(row).getProperty(propertyIdx2))
  }

  /**
   * Tests whether the tab key can be used to jump to the next editable cell.
   */
  @Test def testTabToNextEditableCell() {
    checkTabHandling(2, Author.IndexFirstName, Author.IndexLastName, shift = false)
  }

  /**
   * Tests whether a tab at the last editable column moves to the first one.
   */
  @Test def testTabToNextEditableCellCircular() {
    checkTabHandling(1, Author.IndexLastName, Author.IndexFirstName, shift = false)
  }

  /**
   * Tests whether shift+tab can be used to jump to the previous editable cell.
   */
  @Test def testShiftTabToNextEditableCell() {
    checkTabHandling(0, Author.IndexLastName, Author.IndexFirstName, shift = true)
  }

  /**
   * Tests whether shift+tab at the first editable column moves to the last one.
   */
  @Test def testShiftTabToNextEditableCellCircular() {
    checkTabHandling(0, Author.IndexFirstName, Author.IndexLastName, shift = true)
  }
}

object Author {
  /** Index of the first name property. */
  val IndexFirstName = 0

  /** Index of the middle name property. */
  val IndexMiddleName = 1

  /** Index of the last name property. */
  val IndexLastName = 2

  /** A list with the indices of all defined properties. */
  val PropertyIndices = List(IndexFirstName, IndexMiddleName, IndexLastName)
}

/**
 * A test bean class representing an author used as content of the test table produced by this test.
 * @param firstName the first name
 * @param middleName the middle name
 * @param lastName the last name
 */
private class Author(@BeanProperty var firstName: String, @BeanProperty var middleName: String,
                     @BeanProperty var lastName: String) {

  import Author._

  /**
   * Returns the property with the given index.
   * @param index the index of the property
   * @return the value of this property
   */
  def getProperty(index: Int): String = {
    index match {
      case IndexFirstName => firstName
      case IndexMiddleName => middleName
      case IndexLastName => lastName
    }
  }

  /**
   * Sets the value of the property with the given index.
   * @param index the index of the property
   * @param value the new value for this property
   */
  def setProperty(index: Int, value: String) {
    index match {
      case IndexFirstName => firstName = value
      case IndexMiddleName => middleName = value
      case IndexLastName => lastName = value
    }
  }
}

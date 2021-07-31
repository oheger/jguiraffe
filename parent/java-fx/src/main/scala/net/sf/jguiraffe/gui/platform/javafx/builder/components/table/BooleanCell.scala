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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.table

import java.lang
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.control.cell.CheckBoxTableCell

import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController
import net.sf.jguiraffe.gui.platform.javafx.common.FunctionCallback
import org.apache.commons.logging.LogFactory

/**
 * A specialized ''TableCell'' implementation for dealing with boolean values.
 *
 * This cell displays a checkbox. The status of the checkbox is kept in sync
 * with the value of the associated property. The checkbox is also editable;
 * the user can click it and update the value of the associated property.
 * The binding to properties is managed by a
 * [[net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController]]
 * object.
 *
 * @param formController the form controller
 * @param columnIndex the column index
 * @tparam S the type of elements contained in the current column
 * @tparam T the element type of the table view
 */
private class BooleanCell[S, T](override val formController: TableFormController,
                             override val columnIndex: Int) extends CheckBoxTableCell[S, T]
with FormControllerCell[S, T] {
  /** The logger. */
  private val log = LogFactory.getLog(getClass)

  /** The change listener on the value property. */
  private val changeListener = new PropertyChangeListener

  /** The boolean property storing this cell's current value. */
  private val property = createProperty(changeListener)

  setSelectedStateCallback(FunctionCallback(i => property))

  /**
   * @inheritdoc
   * This method handles external updates of the cell's data. It reads the
   * current value from the table cell controller and writes it into the
   * managed boolean property.
   */
  override def updateItem(item: T, empty: Boolean) {
    super.updateItem(item, empty)
    val value = if (empty) false else readBooleanCellValue
    if (value != property.get()) {
      changeListener.ignoreChange()
      property set value
    }
  }

  /**
   * Reads the value of this cell from the form controller and converts it to a
   * boolean. If the controller is not setup correctly, it may return non-boolean
   * values. In this case, '''false''' is assumed as value for the cell.
   * @return the boolean value of this cell
   */
  private def readBooleanCellValue: Boolean = {
    readCellValue match {
      case b: java.lang.Boolean => b
      case other =>
        log.warn(s"Cell value is not a boolean, but '$other'!")
        false
    }
  }

  /**
   * Creates the property for storing the boolean cell value. The property value
   * needs to be monitored so that edit operations are detected.
   * @param listener the change listener for the property
   * @return the property storing the cell value
   */
  private def createProperty(listener: ChangeListener[lang.Boolean]): SimpleBooleanProperty = {
    val prop = new SimpleBooleanProperty
    prop addListener listener
    prop
  }

  /**
   * A specialized change listener implementation for the current value of the
   * cell. The listener is triggered when the property value is updated. This
   * can happen externally (when the user clicks the checkbox), but also via
   * direct modifications of the value. In the latter case, the listener should
   * not react. Therefore, it contains a mechanism to disable it temporarily.
   */
  private class PropertyChangeListener extends ChangeListener[lang.Boolean] {
    /** The number of events to be ignored. */
    private var changesToBeIgnored = 0

    /**
     * Tells this listener to ignore the following change event. This method is
     * called when the cell value is updated internally. Then the next change
     * event has to be ignored.
     */
    def ignoreChange() {
      changesToBeIgnored += 1
    }

    override def changed(obs: ObservableValue[_ <: lang.Boolean], oldValue: lang.Boolean,
                         newValue: lang.Boolean) {
      if (changesToBeIgnored > 0) changesToBeIgnored -= 1
      else writeCellValue(newValue)
    }
  }

}

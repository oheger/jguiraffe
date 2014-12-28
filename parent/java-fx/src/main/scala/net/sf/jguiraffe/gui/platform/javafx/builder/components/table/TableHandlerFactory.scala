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

import javafx.scene.control.TableView

import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController
import net.sf.jguiraffe.gui.forms.ComponentHandler
import net.sf.jguiraffe.gui.layout.UnitSizeHandler

/**
 * A factory for table handlers.
 *
 * This is the public entry point into the package for table components. This
 * class deals with the multiple helper objects required for the implementation
 * of JavaFX table views. From the outside, there is only a single method for
 * creating a ''TableHandler'' object. Internally, a lot of stuff happens to
 * ensure that a ''TableView'' is created and initialized with all of its
 * columns and its content according to the properties defined by a passed in
 * [[net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController]]
 * object.
 *
 * @param componentFactory the factory for creating table components
 */
class TableHandlerFactory private[table](private[table] val componentFactory:
                                         TableComponentFactory) {
  /**
   * Creates a new instance of ''TableHandlerFactory'' with default settings.
   */
  def this() = this(new TableComponentFactory)

  /**
   * Creates a ''ComponentHandler'' for a table view based on the data provided
   * by the given ''TableFormController''.
   * @param controller the ''TableFormController''
   * @param sizeHandler the ''UnitSizeHandler''
   * @param container the enclosing container
   * @return the handler for the table view
   */
  def createTableHandler(controller: TableFormController, sizeHandler: UnitSizeHandler,
                         container: AnyRef): ComponentHandler[Object] = {
    val tableView = new TableView[AnyRef]
    val resizePolicy = componentFactory createColumnResizePolicy controller.getColumnRecalibrator
    val rowFactory = componentFactory.createRowFactory()

    createColumns(controller, tableView, resizePolicy)
    tableView setColumnResizePolicy resizePolicy
    tableView setRowFactory rowFactory
    tableView setEditable controller.isTableEditable
    installTableWidthListener(controller, tableView)
    controller.calculateFixedColumnWidths(sizeHandler, container)

    val handler = new JavaFxTableHandler(tableView, controller.getDataModel,
      rowFactory.styleProperty)
    handler.tableDataChanged()
    handler
  }

  /**
   * Creates and initializes the columns for the table view.
   * @param controller the ''TableFormController''
   * @param tableView the table view
   * @param resizePolicy the resize policy
   */
  private def createColumns(controller: TableFormController, tableView: TableView[AnyRef],
                            resizePolicy: TableColumnRecalibrationResizePolicy) {
    for (i <- 0 until controller.getColumnCount) {
      val column = componentFactory.columnFactory.createColumn(controller, i)
      tableView.getColumns add column
      resizePolicy installWidthChangeListener column
    }
  }

  /**
   * Creates and installs the listener reacting on table width changes.
   * @param controller the ''TableFormController''
   * @param tableView the table view
   */
  def installTableWidthListener(controller: TableFormController, tableView: TableView[AnyRef]) {
    val widthListener = componentFactory.createTableWidthListener(controller
      .getColumnWidthCalculator, tableView)
    componentFactory.tableWidthProperty(tableView) addListener widthListener
  }
}

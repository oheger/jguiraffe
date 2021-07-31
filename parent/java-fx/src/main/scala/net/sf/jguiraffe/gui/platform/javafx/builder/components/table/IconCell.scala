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

import javafx.scene.control.{ContentDisplay, TableCell}
import javafx.scene.image.{Image, ImageView}

import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController
import net.sf.jguiraffe.gui.platform.javafx.common.ImageWrapper
import org.apache.commons.logging.LogFactory

/**
 * A specialized ''TableCell'' implementation for displaying an icon.
 *
 * This cell expects that the form property it is associated with is of type
 * [[net.sf.jguiraffe.gui.platform.javafx.common.ImageWrapper]]. It sets up a
 * graphical node which displays the image wrapped by this object.
 * The binding to properties is managed by a
 * [[net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController]]
 * object.
 *
 * @tparam T the type of the objects stored in the table
 * @param formController the form controller
 * @param columnIndex the column index
 */
class IconCell[T](override val formController: TableFormController,
                  override val columnIndex: Int) extends TableCell[AnyRef, T]
with FormControllerCell[AnyRef, T] {
  /** The logger. */
  private val log = LogFactory.getLog(getClass)

  /** The image view used as graphic component. */
  private val imageView = new ImageView

  setGraphic(imageView)
  setContentDisplay(ContentDisplay.GRAPHIC_ONLY)

  /**
   * @inheritdoc
   * This implementation obtains the current value for this cell from the
   * ''TableFormController''. If it is an ''ImageWrapper'' object, the current
   * image is replaced by the new one.
   */
  override def updateItem(item: T, empty: Boolean) {
    super.updateItem(item, empty)
    val image = if (empty) null else readImage
    imageView setImage image
  }

  /**
   * Reads the value of this cell and converts it to an image. This implementation
   * expects that the cell value is of type ''ImageWrapper''. If this is not the
   * case, result is '''null'''.
   * @return the value of this cell as image
   */
  private def readImage: Image = {
    readCellValue match {
      case ImageWrapper(image) =>
        image
      case other =>
        log.warn(s"Cell value is not an IconWrapper, but $other.")
        null
    }
  }
}

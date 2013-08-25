/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.builder

import javafx.scene.control.ContentDisplay
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment

package object components {
  /**
   * Converts a ''TextIconAlignment'' enumeration value to the corresponding
   * Java FX ''ContentDisplay'' value.
   * @param al the alignment to be converted
   * @return the corresponding ''ContentDisplay'' value
   */
  def convertAlignment(al: TextIconAlignment): ContentDisplay = {
    al match {
      case TextIconAlignment.CENTER => ContentDisplay.CENTER
      case TextIconAlignment.RIGHT => ContentDisplay.RIGHT
      case _ => ContentDisplay.LEFT
    }
  }

  /**
   * Converts a ''ContentDisplay'' enumeration literal to the corresponding
   * ''JGUIraffe TextIconAlignment'' value.
   * @param cd the ''ContentDisplay'' to be converted
   * @return the corresponding ''TextIconAlignment'' value
   */
  def convertContentDisplay(cd: ContentDisplay): TextIconAlignment = {
    cd match {
      case ContentDisplay.RIGHT => TextIconAlignment.RIGHT
      case ContentDisplay.CENTER => TextIconAlignment.CENTER
      case _ => TextIconAlignment.LEFT
    }
  }
}

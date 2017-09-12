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
package net.sf.jguiraffe.gui.platform.javafx.builder.action

import javafx.scene.input.KeyCombination

import net.sf.jguiraffe.gui.builder.action.Accelerator
import net.sf.jguiraffe.gui.builder.event.{Keys, Modifiers}
import org.apache.commons.lang.StringUtils

/**
 * A converter for transforming a
 * [[net.sf.jguiraffe.gui.builder.action.Accelerator]] into a
 * ''KeyCombination'' used by JavaFx.
 */
private object AcceleratorConverter {
  /**
   * A mapping for the JavaFx key code names to the logic ''Keys'' constants.
   */
  private val KeysMapping = Map(Keys.BACKSPACE -> "BACK_SPACE",
    Keys.DELETE -> "DELETE", Keys.DOWN -> "DOWN", Keys.END -> "END",
    Keys.ENTER -> "ENTER", Keys.ESCAPE -> "ESCAPE", Keys.F1 -> "F1",
    Keys.F2 -> "F2", Keys.F3 -> "F3", Keys.F4 -> "F4", Keys.F5 -> "F5",
    Keys.F6 -> "F6", Keys.F7 -> "F7", Keys.F8 -> "F8", Keys.F9 -> "F9",
    Keys.F10 -> "F10", Keys.F11 -> "F11", Keys.F12 -> "F12", Keys.F13 -> "F13",
    Keys.F14 -> "F14", Keys.F15 -> "F15", Keys.F16 -> "F16",
    Keys.HOME -> "HOME", Keys.INSERT -> "INSERT", Keys.LEFT -> "LEFT",
    Keys.PAGE_DOWN -> "PAGE_DOWN", Keys.PAGE_UP -> "PAGE_UP",
    Keys.PRINT_SCREEN -> "PRINTSCREEN", Keys.RIGHT -> "RIGHT",
    Keys.SPACE -> "SPACE", Keys.TAB -> "TAB", Keys.UP -> "UP")

  /**
   * A mapping for the JavaFx modifier names to logic ''Modifier'' constants.
   */
  private val ModifierMapping = Map(Modifiers.ALT -> "ALT",
    Modifiers.ALT_GRAPH -> "ALT", Modifiers.CONTROL -> "CTRL",
    Modifiers.META -> "META", Modifiers.SHIFT -> "SHIFT")

  /**
   * Converts the specified ''Accelerator'' into a ''KeyCombination''.
   * This implementation supports only single characters or logic keys;
   * platform-specific key codes are ignored. Input can be '''null''';
   * in this case, result is '''null''' as well.
   * @param acc the ''Accelerator'' to be converted
   * @return the corresponding ''KeyCombination''
   */
  def convertAccelerator(acc: Accelerator): KeyCombination = {
    val code = acc match {
      case AcceleratorData(c, _) if c != null =>
        s"'$c'"
      case AcceleratorData(_, key) if key != null =>
        KeysMapping(key)
      case _ => StringUtils.EMPTY
    }

    if (!code.isEmpty) {
      val modifiers = convertModifiers(acc)
      val codeStr = if (modifiers.isDefined) modifiers.get + '+' + code
      else code
      KeyCombination valueOf codeStr
    }
    else null
  }

  /**
   * Generates a string for the modifiers of the specified ''Accelerator''.
   * If there are no modifiers, result is ''None''.
   * @param acc the ''Accelerator''
   * @return a string for the defined modifiers
   */
  private def convertModifiers(acc: Accelerator): Option[String] = {
    import scala.collection.JavaConversions._
    if (acc.getModifiers.isEmpty) None
    else Some((acc.getModifiers map (ModifierMapping(_))).mkString("+"))
  }
}

/**
 * An extractor for the possible components of an ''Accelerator''.
 */
private object AcceleratorData {
  def unapply(acc: Accelerator): Option[(Character, Keys)] =
    if (acc == null) None
    else Some(acc.getKey, acc.getSpecialKey)
}

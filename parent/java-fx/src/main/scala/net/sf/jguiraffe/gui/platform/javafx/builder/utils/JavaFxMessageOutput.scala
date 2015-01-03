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
package net.sf.jguiraffe.gui.platform.javafx.builder.utils

/**
 * The ''MessageOutput'' implementation for JavaFX.
 *
 * Almost all functionality is implemented by the base class. This class mainly
 * mixes in the default providers for dynamically obtaining the required
 * components.
 *
 * @param maximumTextWidth the maximum width of the label for displaying the
 *                         message text; if the text is wider, it is wrapped
 */
class JavaFxMessageOutput(maximumTextWidth: Double) extends JavaFxMessageOutputBase(maximumTextWidth)
with MessageOutputStageProvider with MessageOutputButtonProvider with MessageOutputIconProvider {
  /**
   * Creates a new instance of ''JavaFxMessageOutput'' and initializes it with
   * a default maximum text length.
   */
  def this() = this(JavaFxMessageOutput.DefaultMaximumTextWidth)
}

/**
 * Companion object for ''JavaFxMessageOutput''.
 */
object JavaFxMessageOutput {
  /**
   * Constant for a default maximum text width. This width is used by the
   * default constructor, if no specific value is passed in.
   */
  val DefaultMaximumTextWidth = 640.0
}

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
package net.sf.jguiraffe.gui.platform.javafx.builder.utils

import javafx.stage.{StageStyle, Modality, Stage, Window}

/**
 * Definition of a trait which creates the ''Stage'' object to be used by the
 * JavaFX-specific ''MessageOutput'' implementation.
 *
 * This trait is used by
 * [[net.sf.jguiraffe.gui.platform.javafx.builder.utils.JavaFxMessageOutput]]
 * to obtain the stage for displaying the message box. A base implementation is
 * provided which creates a default modal ''Stage'' with the passed in parent
 * window as its owner and no decorations.
 */
trait MessageOutputStageProvider {
  /**
   * Creates a new ''Stage'' as a child window of the specified parent.
   * @param parent the parent window
   * @return the new ''Stage''
   */
  def createStage(parent: Window): Stage = {
    val stage = new Stage(StageStyle.UTILITY)
    stage initOwner parent
    stage initModality Modality.APPLICATION_MODAL
    stage
  }
}

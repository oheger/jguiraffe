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
package net.sf.jguiraffe.gui.platform.javafx.builder.window

import javafx.stage.Stage

/**
  * A trait for creating new JavaFX ''Stage'' objects.
  *
  * This trait defines a method for creating a new ''Stage''. It is used by
  * [[JavaFxWindowManager]] in order to create the underlying stages for
  * JGUIraffe window objects.
  *
  * It is possible to replace the standard implementation of this trait, so
  * that the creation of stages can be fully customized by an application.
  *
  * @since 1.3.1
  */
trait StageFactory {
  /**
    * Creates a new ''Stage'' object. This method is called for each window to
    * be created by [[JavaFxWindowManager]].
    * @return the new ''Stage''
    */
  def createStage(): Stage
}

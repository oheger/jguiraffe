/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.tree

import org.apache.commons.configuration.tree.ConfigurationNode

import javafx.application.Platform
import net.sf.jguiraffe.gui.builder.components.model.TreeModelChangeListener

/**
 * A specialized implementation of ''TreeModelChangeListener'' that propagates
 * incoming events to another listener object in the JavaFX thread.
 *
 * Events reporting changes of a tree's model can occur in any thread. The
 * processing of these events leads to manipulations on the UI and thus can
 * only take place in the JavaFX thread. When setting up a JavaFX tree view
 * and a corresponding component handler an instance of this class is created
 * which bridges between the different threads.
 *
 * @param listener the wrapped listener
 */
private class FxThreadModelChangeListener(val listener: TreeModelChangeListener)
  extends TreeModelChangeListener {
  /**
   * @inheritdoc This implementation propagates the current change event to the
   * wrapped listener, but in the JavaFX thread.
   */
  override def treeModelChanged(node: ConfigurationNode) {
    Platform.runLater(new Runnable {
      def run() {
        listener treeModelChanged node
      }
    })
  }
}

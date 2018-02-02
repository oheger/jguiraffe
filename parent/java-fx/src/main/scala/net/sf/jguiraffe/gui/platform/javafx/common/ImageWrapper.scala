/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.common

import javafx.scene.image.{Image, ImageView}

/**
 * A helper class that wraps a JavaFX image.
 *
 * There is some functionality frequently required when dealing with icons.
 * First, an image for the icon has to be managed. For many use cases, this
 * is not sufficient: in order to display the image in the UI, it has to be
 * added to a ''Node'' object, typically an ''ImageView''. This simple
 * helper class provides this functionality. It holds an image object and
 * provides a factory method for creating a new ''ImageView''.
 *
 * Note, that it is intended to create always a new ''ImageView''. It is not
 * possible to add a single ''Node'' multiple times to the scene graph.
 * Therefore, every time the icon is to be displayed, another ''ImageView''
 * has to be created.
 *
 * @param image the wrapped image
 */
case class ImageWrapper(image: Image) {
  /**
   * Creates a new ''ImageView'' component that is initialized with the wrapped
   * image.
   * @return the newly created image view
   */
  def newImageView(): ImageView = new ImageView(image)
}

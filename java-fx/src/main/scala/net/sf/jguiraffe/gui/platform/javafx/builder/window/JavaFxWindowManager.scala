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
package net.sf.jguiraffe.gui.platform.javafx.builder.window

import net.sf.jguiraffe.gui.builder.window.Window
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData
import net.sf.jguiraffe.gui.builder.window.WindowData
import net.sf.jguiraffe.gui.builder.window.WindowManager

/**
 * The Java FX-based implementation of the ''WindowManager'' interface.
 */
class JavaFxWindowManager extends WindowManager {
  def createFrame(builderData: WindowBuilderData, data: WindowData,
    wnd: Window): Window = {
    null
  }

  def createDialog(builderData: WindowBuilderData, data: WindowData,
    modal: Boolean, wnd: Window): Window = {
    null
  }

  def createInternalFrame(builderData: WindowBuilderData, data: WindowData,
    wnd: Window): Window = {
    null
  }
}

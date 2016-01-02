/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.widget

import javafx.scene.control.Control

import net.sf.jguiraffe.gui.platform.javafx.common.ToolTipFactory

/**
 * A concrete ''WidgetHandler'' implementation for JavaFX controls.
 *
 * Controls provide some more functionality than plain nodes. Therefore, this
 * handler mixes in traits offering extended features.
 *
 * @param control the control wrapped by this handler
 * @param toolTipFactory the factory for creating tool tips
 */
private[components] class ControlWidgetHandler(control: Control, override val toolTipFactory:
ToolTipFactory)
  extends NodeWidgetHandler(control) with ToolTipWidgetHandler {
  override val toolTipProperty = control.tooltipProperty
}

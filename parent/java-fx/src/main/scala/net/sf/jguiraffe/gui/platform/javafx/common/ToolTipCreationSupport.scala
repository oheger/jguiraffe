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
package net.sf.jguiraffe.gui.platform.javafx.common

import javafx.beans.property.ObjectProperty
import javafx.scene.control.{Control, Tooltip}

import org.apache.commons.jelly.{JellyContext, Tag}

/**
 * A trait providing functionality for the creation of tooltips.
 *
 * During a builder operation multiple manager objects need to create UI
 * components which may have tooltips. In JavaFx the creation of tooltips is a
 * bit more complex because this has to be done in the UI thread. Therefore,
 * tooltips cannot be created directly in a builder operation; rather, the
 * requests for the creation of tooltips are stored and then executed as
 * callbacks. Corresponding functionality is offered by this trait.
 *
 * Manager classes can simply mix in this trait. They have to provide the
 * [[ToolTipFactory]] to be used. Then they can call one of the
 * ''addCreateToolTipRequest()'' methods whenever the tooltip for a control
 * needs to be created.
 */
trait ToolTipCreationSupport {
  /**
   * The ''ToolTipFactory'' for creating tool tips. This field has to be
   * defined by the classes mixing in this trait.
   */
  val toolTipFactory: ToolTipFactory

  /**
   * Adds a request for the creation of a tool tip for the specified property.
   * The passed in ''JellyContext'' is used to obtain the objects required for
   * tooltip creation.
   * @param context the ''JellyContext''
   * @param property the property for which the tooltip is for
   * @param tip the text of the tooltip
   */
  def addCreateToolTipRequest(context: JellyContext, property: ObjectProperty[Tooltip],
                              tip: String): Unit = {
    val callBack = ToolTipCreationCallBack.getInstance(context, toolTipFactory)
    callBack.addCreateToolTipRequest(property, tip)
  }

  /**
   * Adds a request for the creation of a tool tip for the specified control.
   * The passed in ''JellyContext'' is used to obtain the objects required for
   * tooltip creation.
   * @param context the ''JellyContext''
   * @param control the control for which the tooltip is for
   * @param tip the text of the tooltip
   */
  def addCreateToolTipRequest(context: JellyContext, control: Control, tip: String): Unit = {
    addCreateToolTipRequest(context, control.tooltipProperty, tip)
  }

  /**
   * Obtains a helper object for tooltip creation from the ''JellyContext'' of
   * the specified tag and adds a request for the given property to it.
   * @param tag the tag
   * @param property the property for which the tooltip is for
   * @param tip the text of the tooltip
   */
  def addCreateToolTipRequest(tag: Tag, property: ObjectProperty[Tooltip], tip: String): Unit = {
    addCreateToolTipRequest(tag.getContext, property, tip)
  }

  /**
   * Obtains a helper object for tooltip creation from the ''JellyContext'' of
   * the specified tag and adds a request for the given control to it.
   * @param tag the tag
   * @param control the control for which the tooltip is for
   * @param tip the text of the tooltip
   */
  def addCreateToolTipRequest(tag: Tag, control: Control, tip: String): Unit = {
    addCreateToolTipRequest(tag, control.tooltipProperty, tip)
  }
}

/**
 * Copyright 2006-2014 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import net.sf.jguiraffe.gui.platform.javafx.common.ToolTipFactory
import org.apache.commons.jelly.TagSupport
import org.apache.commons.logging.LogFactory

import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.scene.control.Control
import javafx.scene.control.Tooltip
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderCallBack
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData

/**
 * An internally used helper class for creating the tool tips of controls
 * constructed during a builder operation.
 *
 * A builder typically runs in a background thread so that the UI is not
 * blocked. This causes problems with Java FX tool tips which can only be
 * created in the Java FX thread. Therefore, the Java FX-specific
 * implementation of the ''ComponentManager'' interface does not create and
 * assign tool tips directly. Rather, a task object is created which collects
 * all requests for creating tool tips. After the build operation this task
 * is run as a ''ComponentBuilderCallBack'' and handles the initialization of
 * tool tips in the Java FX thread.
 *
 * If tool tips are involved, an instance of this class is created on demand
 * and stored in the current Jelly context. (This is safe from a threading
 * aspect because the Jelly context is assigned to a single build operation
 * only.) The companion object provides a method for easily obtaining the
 * instance for the current Jelly context. It performs all necessary
 * one-time initialization. Requests for new tool tips can be added to an
 * instance obtained this way.
 *
 * @param toolTipFactory the factory for tool tips
 */
private class ToolTipCreationCallBack(val toolTipFactory: ToolTipFactory)
  extends ComponentBuilderCallBack {
  /** The logger. */
  private val log = LogFactory.getLog(classOf[ToolTipCreationCallBack])

  /** A list storing the requests for creating tool tops. */
  private var toolTipRequests = List.empty[ToolTipCreationRequest]

  /**
   * Adds a request for creating a tool tip for the specified control.
   * @param ctrl the control
   * @param text the text of the tool tip
   */
  def addCreateToolTipRequest(ctrl: Control, text: String) {
    addCreateToolTipRequest(ctrl.tooltipProperty, text)
  }

  /**
   * Adds a request for creating a tool tip for the specified tool tip
   * property.
   * @param prop the property in which to store the tool tip
   * @param text the text of the tool tip
   */
  def addCreateToolTipRequest(prop: ObjectProperty[Tooltip], text: String) {
    toolTipRequests = ToolTipCreationRequest(prop, text) :: toolTipRequests
  }

  /**
   * Returns the current list with requests for tool tip creations.
   * @return the current list with tool tip creation requests
   */
  def requests: List[ToolTipCreationRequest] = toolTipRequests

  /**
   * @inheritdoc This implementation starts the initialization of tool tips
   * asynchronously in the Java FX thread.
   */
  override def callBack(data: ComponentBuilderData, param: Any) {
    log.info("Creating tool tips.")
    Platform.runLater(new Runnable {
      def run() {
        createAndAssignToolTips()
      }
    })
  }

  /**
   * Creates all requested tool tips and assigns them to the corresponding
   * controls. This method is called by ''callBack()'' in the Java FX thread.
   */
  private[components] def createAndAssignToolTips() {
    requests foreach (handleCreateToolTipRequest(_))
  }

  /**
   * Processes the given request for creating a new tool tip. This method
   * creates a new tool tip (using the associated tool tip factory) and assigns
   * it to the correct control.
   * @param request the request to be handled
   */
  private def handleCreateToolTipRequest(request: ToolTipCreationRequest) {
    request.prop.set(toolTipFactory.createToolTip(request.tip))
  }
}

/**
 * The companion object for ''ToolTipCreationCallBack''.
 */
private object ToolTipCreationCallBack {
  /**
   * Constant for the key in the Jelly context under which an instance is
   * stored.
   */
  private val ContextKey = classOf[ToolTipCreationCallBack].getName

  /**
   * Obtains an instance of ''ToolTipCreationCallBack'' from the passed in
   * tag. From the tag the Jelly context can be retrieved in which the instance
   * lives. If this is the first request, a new instance is created and is also
   * registered as call back at the component builder data object.
   * @param tag the current tag
   * @param factory the tool tip factory
   */
  def getInstance(tag: TagSupport, factory: ToolTipFactory): ToolTipCreationCallBack = {
    val context = tag.getContext
    context.getVariable(ContextKey) match {
      case cb: ToolTipCreationCallBack => cb
      case _ =>
        val cb = new ToolTipCreationCallBack(factory)
        cb.log.info("Created instance of ToolTipCreationCallBack for Jelly context.")
        context.setVariable(ContextKey, cb)
        val builderData = ComponentBuilderData.get(context)
        builderData.addCallBack(cb, null)
        cb
    }
  }
}

/**
 * A simple data class for storing the data of a tool tip request.
 * @param prop the property in which to store the newly created tool tip
 * @param tip the text of the tool tip
 */
private case class ToolTipCreationRequest(prop: ObjectProperty[Tooltip], tip: String)

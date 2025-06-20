/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.scene.control.{Control, Tooltip}

import net.sf.jguiraffe.gui.builder.components.{ComponentBuilderCallBack, ComponentBuilderData}
import org.apache.commons.jelly.{JellyContext, TagSupport}
import org.apache.commons.logging.LogFactory

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
class ToolTipCreationCallBack(val toolTipFactory: ToolTipFactory)
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
  def addCreateToolTipRequest(ctrl: Control, text: String): Unit = {
    addCreateToolTipRequest(ctrl.tooltipProperty, text)
  }

  /**
   * Adds a request for creating a tool tip for the specified tool tip
   * property.
   * @param prop the property in which to store the tool tip
   * @param text the text of the tool tip
   */
  def addCreateToolTipRequest(prop: ObjectProperty[Tooltip], text: String): Unit = {
    toolTipRequests = ToolTipCreationRequest(prop, text) :: toolTipRequests
  }

  /**
   * Returns the current list with requests for tool tip creations.
   * @return the current list with tool tip creation requests
   */
  private[common] def requests: List[ToolTipCreationRequest] = toolTipRequests

  /**
   * @inheritdoc This implementation starts the initialization of tool tips
   * asynchronously in the Java FX thread.
   */
  override def callBack(data: ComponentBuilderData, param: Any): Unit = {
    log.info("Creating tool tips.")
    Platform.runLater(new Runnable {
      def run(): Unit = {
        createAndAssignToolTips()
      }
    })
  }

  /**
   * Creates all requested tool tips and assigns them to the corresponding
   * controls. This method is called by ''callBack()'' in the Java FX thread.
   */
  private[common] def createAndAssignToolTips(): Unit = {
    requests foreach (handleCreateToolTipRequest(_))
  }

  /**
   * Processes the given request for creating a new tool tip. This method
   * creates a new tool tip (using the associated tool tip factory) and assigns
   * it to the correct control.
   * @param request the request to be handled
   */
  private def handleCreateToolTipRequest(request: ToolTipCreationRequest): Unit = {
    request.prop.set(toolTipFactory.createToolTip(request.tip))
  }
}

/**
 * The companion object for ''ToolTipCreationCallBack''.
 */
object ToolTipCreationCallBack {
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
   * @return the instance living in the context of the provided tag
   */
  def getInstance(tag: TagSupport, factory: ToolTipFactory): ToolTipCreationCallBack =
    getInstance(tag.getContext, factory)

  /**
   * Obtains an instance of ''ToolTipCreationCallBack'' from the passed in
   * ''JellyContext''. If this is the first request, a new instance is created
   * and is also registered as call back at the component builder data object.
   * @param context the ''JellyContext''
   * @param factory the tool tip factory
   * @return the instance living in the specified context
   */
  def getInstance(context: JellyContext, factory: ToolTipFactory): ToolTipCreationCallBack = {
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

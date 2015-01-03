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

import org.apache.commons.jelly.JellyContext
import org.junit.Assert._

/**
 * A special mock implementation of ''ToolTipCreationSupport''.
 *
 * This trait can be used to test whether manager implementations create the
 * expected tooltips. Usage is as follows: Each tooltip creation request is
 * recorded. With the ''verifyToolTipCreationRequest()'' methods it can be
 * checked whether a specific request was processed.
 */
trait MockToolTipCreationSupport extends ToolTipCreationSupport {
  /** Stores the processed tool tip creation requests. */
  private val processedRequests = collection.mutable.Set.empty[ProcessedRequest]

  /**
   * Verifies that a tooltip creation request for a property was processed. The
   * return value indicates whether there are more requests that have been
   * processed; this can be used, for instance, to check that only a specific
   * set of requests has been passed to this instance.
   * @param context the ''JellyContext''
   * @param property the property
   * @param tip the text of the tip
   * @return '''true''' if there are more requests, '''false''' otherwise
   */
  def verifyToolTipCreationRequest(context: JellyContext, property: ObjectProperty[Tooltip],
                                   tip: String): Boolean = {
    val request = ProcessedRequest(context, property, tip)
    assertTrue("Unexpected request: " + request, processedRequests contains request)
    processedRequests -= request
    processedRequests.nonEmpty
  }

  /**
   * Verifies that a tooltip creation request for a control was processed.
   * Works like the method with the same name, but checks for the tooltip
   * property of the given control.
   */
  def verifyToolTipCreationRequest(context: JellyContext, control: Control, tip: String): Boolean =
    verifyToolTipCreationRequest(context, control.tooltipProperty, tip)

  /**
   * Verifies that no tool tip request was added.
   */
  def verifyNoInteraction(): Unit = {
    assertTrue("Got requests", processedRequests.isEmpty)
  }

  /**
   * @inheritdoc
   * This implementation just records this request.
   */
  override def addCreateToolTipRequest(context: JellyContext, property: ObjectProperty[Tooltip],
                                       tip: String): Unit = {
    processedRequests += ProcessedRequest(context, property, tip)
  }

  /**
   * A simple data class for recording the requests processed by an instance.
   * @param context the ''JellyContext''
   * @param property the property
   * @param tip the text of the tip
   */
  private case class ProcessedRequest(context: JellyContext, property: ObjectProperty[Tooltip],
                                      tip: String)
}

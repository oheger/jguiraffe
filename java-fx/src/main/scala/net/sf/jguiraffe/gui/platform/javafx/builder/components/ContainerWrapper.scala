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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import scala.collection.mutable.ArrayBuffer

import javafx.scene.Node
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import net.sf.jguiraffe.gui.builder.components.FormBuilderException
import net.sf.jguiraffe.gui.layout.PercentLayoutBase

/**
 * An internally used helper class which manages panels as containers for
 * other components.
 *
 * The Java FX approach to layouts is a bit different from the model used by
 * JGUIraffe: no layout objects are used, but there are special panel
 * implementations providing specific layouting facilities. Therefore, based
 * on the desired layout object, a specific panel class has to be created.
 * This makes the creation of panels a bit more complex.
 *
 * This class is used throughout the builder process to represent all panels
 * (including root containers in windows). It is responsible for keeping
 * track of the child components added to a panel and for eventually creating
 * the correct panel implementation. It is used by the component manager
 * in the implementation of its ''addContainerComponent()'' method.
 */
class ContainerWrapper {
  /** A buffer for storing the managed components. */
  private val components = ArrayBuffer.empty[ComponentData]

  /** The layout object of the wrapped container. */
  private var layout: Option[PercentLayoutBase] = None

  /**
   * Adds the specified component to the container represented by this class.
   * An optional constraints object can be provided which is evaluated by
   * the container's layout object. This method expects the component to be
   * either a ''Node'' or another ''ContainerWrapper''. In the latter case,
   * the wrapper's container is requested and added to the children of this
   * container.
   * @param component the component to be added
   * @param constraints optional constraints
   * @throws FormBuilderException if an unsupported component is added
   */
  @throws(classOf[FormBuilderException])
  def addComponent(component: Object, constraints: Object) {
    component match {
      case nd: Node =>
        components += ComponentData(nd, constraints)
      case wrap: ContainerWrapper =>
        components += ComponentData(wrap.createContainer(), constraints)
      case _ =>
        throw new FormBuilderException("Unsupported component: " + component)
    }
  }

  /**
   * Initializes the layout for the represented container.
   * @param percLayout the layout
   */
  def initLayout(percLayout: PercentLayoutBase) {
    layout = Some(percLayout)
  }

  /**
   * Creates the Java FX container described by this wrapper object. This
   * method is called after the container has been initialized with its
   * children and an optional layout object. Based on the layout, an
   * appropriate sub class of ''Pane'' is returned.
   * @return a newly created container
   */
  def createContainer(): Pane = {
    val compData = components.toArray
    val pane = createLayoutPane(compData)
    appendChildren(pane, compData)
    pane
  }

  /**
   * Creates the correct ''Pane'' implementation for the current layout. If no
   * layout is set, a default Java FX ''FlowPane'' is created.
   * @param compData an array with data about the container's components
   * @return the layout pane
   */
  private def createLayoutPane(compData: Array[ComponentData]): Pane = {
    (layout map { createPercentLayoutPane(_, compData) }).getOrElse(new FlowPane)
  }

  /**
   * Creates a specialized layout pane which uses the provided percent layout.
   * @param percLayout the percent layout
   * @param compData an array with data about the container's components
   * @return the corresponding layout pane
   */
  private def createPercentLayoutPane(percLayout: PercentLayoutBase,
    compData: Array[ComponentData]): Pane = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  /**
   * Adds all children to the specified pane.
   * @param pane the target pane
   * @param compData an array with data about the container's components
   */
  private def appendChildren(pane: Pane, compData: Array[ComponentData]) {
    compData foreach { cd => pane.getChildren.add(cd.component) }
  }

  /**
   * A simple data class storing information about a child component to be
   * added to the resulting container object.
   * @param component the actual component
   * @param constraints an arbitrary constraints object
   */
  private case class ComponentData(component: Node, constraints: Object)
}

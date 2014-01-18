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
package net.sf.jguiraffe.gui.platform.javafx.layout

import scala.collection.mutable.ArrayBuffer
import javafx.scene.Node
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import net.sf.jguiraffe.gui.builder.components.FormBuilderException
import net.sf.jguiraffe.gui.layout.PercentLayoutBase
import net.sf.jguiraffe.gui.layout.UnitSizeHandler

/**
 * A helper class which manages panels as containers for other components.
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
 * the correct panel implementation. It also stores some properties of a
 * container which are not directly supported by Java FX, e.g. a font. (The
 * semantic meaning of these properties is that they are set for all children
 * of the container unless they are overridden there.)
 *
 * The component manager makes use of this class in the implementation of its
 * ''addContainerComponent()'' method and for creating panels and layout
 * objects.
 *
 * When creating panels with percent layout a ''UnitSizeHandler'' is required.
 * Such an object can be passed to the constructor. (This is useful if the
 * caller has access to a shared ''UnitSizeHandler'' instance.) If no size
 * handler is provided, a new default instance is created if necessary.
 *
 * Note: This class is not thread-safe. However, for the default usage
 * scenario - creating and initializing an instance by a builder in a
 * background thread and then using it in the Java FX thread - this should
 * not be a problem.
 *
 * @param sizeHandler an optional ''UnitSizeHandler''
 */
class ContainerWrapper(val sizeHandler: Option[UnitSizeHandler] = None) {
  /**
   * The font to be used for the components of this container. If here a
   * value is set during the building process, all child components created
   * during the build are also assigned this font.
   */
  var font: Option[Font] = None

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
   * Returns the ''Font'' for this container. If a font has been set, it is
   * returned. Otherwise, result is the default system font.
   * @return the font of this container
   */
  def getContainerFont: Font = font.getOrElse(Font.getDefault())

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
    val components = compData map (_.component)
    val constraints = compData map (_.constraints)
    percLayout.setPlatformAdapter(
      new JavaFxPercentLayoutAdapter(components, constraints, sizeHandler))
    new PercentLayoutPane(percLayout, this)
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

/**
 * The companion object for ''ContainerWrapper''.
 */
object ContainerWrapper {
  /**
   * Convenience method for converting a plain object to an instance of
   * ''ContainerWrapper''. Because of the generic nature of the JGUIraffe
   * library containers are often passed around as objects. When dealing with
   * them a conversion has to be performed. This is done by this method. If
   * the passed in object is a ''ContainerWrapper'' instance, it is cast and
   * returned. Otherwise an exception is thrown.
   * @param obj the object to be converted
   * @return the object cast as ''ContainerWrapper''
   * @throws IllegalArgumentException if the object cannot be cast
   */
  def fromObject(obj: Object): ContainerWrapper = {
    if (obj == null || !obj.isInstanceOf[ContainerWrapper]) {
      throw new IllegalArgumentException("Not a ContainerWrapper: " + obj)
    }
    obj.asInstanceOf[ContainerWrapper]
  }
}

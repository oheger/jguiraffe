/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.components.Composite
import net.sf.jguiraffe.gui.builder.components.tags.FormBaseTag
import net.sf.jguiraffe.gui.platform.javafx.layout.ContainerWrapper
import org.apache.commons.jelly.JellyContext

private object ContainerMapping {
  /** Constant for the name of the Jelly context variable. */
  private val VariableName = "jguiraffe.ContainerMapping"

  /**
    * Obtains a ''ContainerMapping'' instance from the specified Jelly context.
    * If the context does not contain an instance, a new one is created and
    * stored in the context.
    * @param context the Jelly context
    * @return the instance from this context
    */
  def fromContext(context: JellyContext): ContainerMapping = {
    val instance = context.getVariable(VariableName)
    instance match {
      case mapping: ContainerMapping => mapping
      case _ => storeContainerMapping(context, new ContainerMapping)
    }
  }

  /**
    * Stores the specified ''ContainerMapping'' instance in the Jelly context
    * under the reserved key.
    * @param context the Jelly context
    * @param mapping the ''ContainerMapping''
    * @return the same mapping instance
    */
  def storeContainerMapping(context: JellyContext, mapping: ContainerMapping): ContainerMapping = {
    context.setVariable(VariableName, mapping)
    mapping
  }
}

/**
  * A helper class which stores information about container tags encountered
  * while constructing a UI.
  *
  * The creation of containers is a bit complex. For instance, typically the
  * creation is deferred until all contained components are available.
  * Attributes of the container - like its colors or font - have to be stored
  * so that they can be applied when the container is created.
  *
  * Containers also stand in a hierarchical relation with each other. For doing
  * size calculations, it is sometimes necessary to obtain the font of a
  * container. If this font is not defined, the parent has to be consulted.
  * Therefore, hierarchical information needs to be present.
  *
  * This class allows storing information about ''Composite'' objects (the
  * JGUIraffe abstraction for containers) and the actual container objects
  * associated with them. This information can be used to implement the use
  * cases described: storing attributes of a container, and allowing access to
  * the parent.
  */
private class ContainerMapping {
  /** A mapping from tags to containers. */
  private val tagMapping = collection.mutable.Map.empty[FormBaseTag, ContainerWrapper]

  /**
    * Adds a mapping for the specified objects.
    * @param tag the tag defining the container
    * @param container the associated container
    */
  def add(tag: FormBaseTag, container: ContainerWrapper): Unit = {
    tagMapping += tag -> container
  }

  /**
    * Returns the container associated with the specified tag if any.
    * @param tag the tag
    * @return an option with the container mapped to this tag
    */
  def getContainer(tag: FormBaseTag): Option[ContainerWrapper] = tagMapping get tag

  /**
    * Returns the container associated with the specified ''Composite'' if any.
    * The mapping is consulted only if the ''Composite'' is actually a
    * component tag. Otherwise, result is ''None''.
    * @param composite the ''Composite'' to be looked up
    * @return an option with the container mapped to this ''Composite''
    */
  def getContainerFromComposite(composite: Composite): Option[ContainerWrapper] =
    composite match {
      case tag: FormBaseTag =>
        getContainer(tag)

      case _ => None
    }
}

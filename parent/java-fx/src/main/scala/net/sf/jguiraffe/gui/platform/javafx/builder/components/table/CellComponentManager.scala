/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.table

import java.lang.reflect.{InvocationHandler, Method}
import javafx.scene.Node

import net.sf.jguiraffe.gui.builder.components.tags.table.ColumnComponentTag
import net.sf.jguiraffe.gui.builder.components.tags.{ComponentBaseTag, ContainerTag}
import net.sf.jguiraffe.gui.builder.components._
import net.sf.jguiraffe.gui.forms.{ComponentHandler, Form}
import net.sf.jguiraffe.gui.platform.javafx.layout.ContainerWrapper
import org.apache.commons.jelly.{Tag, XMLOutput}

/**
 * Companion object for ''CellComponentManager''.
 */
object CellComponentManager {
  /**
   * Helper method for creating a proxy object of the specified type using the
   * provided invocation handler.
   * @param ifc the interface to be implemented by the proxy
   * @param handler the invocation handler
   * @tparam T the type of the proxy
   * @return the new proxy object
   */
  private[table] def createProxy[T](ifc: Class[T], handler: InvocationHandler): T =
    ifc.cast(java.lang.reflect.Proxy.newProxyInstance(getClass.getClassLoader, Array(ifc), handler))
}

/**
 * A class for managing a set of components to be used as renderers for a table
 * cell.
 *
 * The API of the ''JGUIraffe'' library allows that the display of a column in a
 * table is defined using a renderer form. In contrast to the Swing-specific
 * implementation (where the content of this form can simply be used as
 * renderer component), in JavaFX this is not trivial to achieve. Here the
 * main problem is that components (''Nodes'') may appear only once in the
 * scene graph. Therefore, affected components have to be created multiple
 * times - once for each existing table cell.
 *
 * This class implements a solution for this problem. It works as follows:
 * $ - A special proxy implementation for the ''ComponentManager'' interface is
 * provided. Such a proxy is installed when a new form context for a column
 * form is opened.
 * $ - The special ''ComponentManager'' creates special ''ComponentHandler''
 * objects which are not associated with a specific control, but dynamically
 * delegate to another handler based on the currently active table cell.
 * $ - When a new cell is created, this object is asked to replay the body of
 * the tag which has created the form context for a column. This time, the
 * original ''ComponentManager'' is used; so the actual UI for this specific
 * cell is created.
 * $ - When the cell needs access to its data it ensures that it is selected
 * as the active cell. Thus, the special ''ComponentHandler'' objects know
 * from where to access the actual data.
 *
 * @param tag the tag that created the form context controlled by this object
 * @param form the form for the current column
 */
class CellComponentManager(val tag: ContainerTag, val form: Form) {
  /** A map for the registered cells. */
  private val cells = collection.mutable.Map.empty[AnyRef, Form]

  /** A map for the data of the current row render form. */
  private val currentData = collection.mutable.Map.empty[String, AnyRef]

  /**
   * Installs a special ''ComponentManager'' proxy implementation for the
   * current builder. This manager is used for executing the tags in a special
   * sub form context.
   * @param tag the current tag opening the sub form context
   * @return the newly installed component manager
   */
  def installComponentManagerProxy(tag: Tag): ComponentManager = {
    val manager = createComponentManagerProxy()
    val builderData = ComponentBuilderData.get(tag.getContext)
    builderData setComponentManager manager
    manager
  }

  /**
   * Registers a new cell at this manager. This causes the body of the test
   * tag to be executed again which creates a new form and a new UI. The
   * form is associated with the passed in cell object. Thus it is accessed
   * from the form representing the column whenever this cell is selected.
   * The root component of the UI is returned.
   * @param cell the cell to be registered
   * @return the UI created during the tag execution
   */
  def registerCell(cell: AnyRef): Node = {
    val builderData = ComponentBuilderData.get(tag.getContext)
    val composite: CompositeImpl = installComposite(builderData)
    val newForm = new Form(form.getTransformerContext, form.getBindingStrategy)
    cells += cell -> newForm

    builderData.pushFormContext(newForm, getClass.getName)
    executeTagForCellRegistration()
    builderData.popFormContext()

    extractUIComponent(composite)
  }

  /**
   * Selects the specified cell. This determines the form associated with this
   * cell. It is filled with the current data values provided by the component
   * handler proxies via the ''initFieldData()'' method.
   * @param cell the cell to be selected
   */
  def selectCell(cell: AnyRef): Unit = {
    val formOfCurrentCell = cells(cell)
    currentData.iterator foreach { kv =>
      val handler = formOfCurrentCell.getField(kv._1).getComponentHandler
        .asInstanceOf[ComponentHandler[AnyRef]]
      handler setData kv._2
    }
  }

  /**
   * Initializes the current data of the given field. This method is called by a
   * proxy for a component handler when it is passed new data. (This happens when
   * the form for the current row is initialized.) It stores the passed in value
   * in an internal data structure. When the cell for this row gets selected its
   * form is initialized with the data collected here.
   * @param name the name of the field in question
   * @param data the data for this field
   */
  private[table] def initFieldData(name: String, data: AnyRef): Unit =
    currentData += name -> data

  /**
   * Creates a proxy for a component manager.
   * @return the component manager proxy
   */
  private def createComponentManagerProxy(): ComponentManager =
    CellComponentManager.createProxy(classOf[ComponentManager],
      new ComponentManagerInvocationHandler(this))

  /**
   * Executes the tag and constructs a new form and UI for a newly registered cell.
   */
  private def executeTagForCellRegistration(): Unit = {
    tag.getBody.run(tag.getContext, XMLOutput.createDummyXMLOutput())
  }

  /**
   * Install an alternative ''Composite'' in which the UI for the column is
   * stored. Note that we cannot simply access the tag passed to this
   * object for this purpose. Jelly creates a new tag instance when executing
   * the tag body because this is now done in a separate thread.
   * @param builderData the ''ComponentBuilderData'' object
   * @return the alternative ''Composite'' object
   */
  private def installComposite(builderData: ComponentBuilderData): CompositeImpl = {
    val composite = new CompositeImpl
    builderData setContainerSelector new ContainerSelector {
      override def getComposite(tag: Composite): Composite =
        tag match {
          case t: ColumnComponentTag =>
            composite
          case comp => comp
        }
    }
    composite
  }

  /**
   * Extracts the UI component for a newly registered cell from the given
   * composite object.
   * @param composite the composite
   * @return the root UI component
   */
  private def extractUIComponent(composite: CompositeImpl): Node = {
    val component = composite.getComponents.iterator().next()(0)
    ContainerWrapper.obtainPossiblyWrappedNode(component)
  }
}

/**
 * An ''InvocationHandler'' implementation for a proxy for a component manager.
 *
 * This handler handles the following different kinds of methods:
 * $ - Methods returning a component return a ''CellComponentManager'' object.
 * This is used to pass the cell manager to the outside.
 * $ - Methods returning a ''ComponentHandler'' return a special handler proxy
 * which is able to delegate to a real ''ComponentHandler'' belonging to the
 * form of the currently selected table cell.
 * $ - All other methods are ignored.
 *
 * @param cellManager the ''CellComponentManager''
 */
private class ComponentManagerInvocationHandler(cellManager: CellComponentManager) extends
InvocationHandler {
  val TypeComponentHandler = classOf[ComponentHandler[_]]

  override def invoke(proxy: scala.Any, method: Method, args: Array[AnyRef]): AnyRef = {
    method.getReturnType match {
      case Void.TYPE =>
        null
      case TypeComponentHandler =>
        val tag = args(0).asInstanceOf[ComponentBaseTag]
        CellComponentManager.createProxy(classOf[ComponentHandler[_]],
          new ComponentHandlerInvocationHandler(cellManager, tag.getName))
      case _ =>
        cellManager
    }
  }
}

/**
 * An ''InvocationHandler'' implementation for the proxy of a component handler.
 *
 * The purpose of this proxy is to intercept calls to the ''setData()'' method
 * which are triggered when the form of the current row of the table is filled
 * with data. This data is passed to the associated ''CellComponentManager''
 * object; from there it is propagated to the cell responsible for displaying
 * the current row.
 * @param cellManager the ''CellComponentManager''
 * @param name the name of the associated property
 */
private class ComponentHandlerInvocationHandler(cellManager: CellComponentManager,
                                                name: String) extends InvocationHandler {
  /** The name of the setData() method. */
  private val MethodSetData = "setData"

  /**
   * @inheritdoc
   * Intercepts a ''setData()'' call and passes the data to the ''CellComponentManager''.
   */
  override def invoke(proxy: scala.Any, method: Method, args: Array[AnyRef]): AnyRef = {
    if(MethodSetData == method.getName) {
      cellManager.initFieldData(name, args(0))
    }
    null
  }
}

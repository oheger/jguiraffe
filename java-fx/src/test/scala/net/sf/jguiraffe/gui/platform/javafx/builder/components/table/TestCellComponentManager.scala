/*
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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.table

import javafx.scene.control.{Label, TextField}

import net.sf.jguiraffe.gui.builder.components.tags.{LabelTag, TextFieldTag}
import net.sf.jguiraffe.gui.builder.components.{ComponentBuilderData, ComponentManager}
import net.sf.jguiraffe.gui.forms._
import net.sf.jguiraffe.gui.platform.javafx.builder.components.JavaFxTextHandler
import net.sf.jguiraffe.gui.platform.javafx.layout.ContainerWrapper
import net.sf.jguiraffe.transform.TransformerContext
import org.apache.commons.jelly._
import org.easymock.{EasyMock, IAnswer}
import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

/**
 * Test class for ''CellComponentManager''.
 */
class TestCellComponentManager extends JUnitSuite with EasyMockSugar {
  /** Constant for the name of a component. */
  private val ComponentName = "TestComponent"

  /** The tag which triggers the cell component manager. */
  private var tag: Tag = _

  /** The form for the test manager. */
  private var form: Form = _

  /** The manager to be tested. */
  private var manager: CellComponentManager = _

  @Before def setUp(): Unit = {
    tag = createTag()
    builderData().initializeForm(niceMock[TransformerContext], niceMock[BindingStrategy])
    form = builderData().getForm
    manager = new CellComponentManager(tag, form)
  }

  /**
   * Creates the tag to be passed to the test manager.
   * @return the test tag
   */
  private def createTag(): Tag = {
    val tag = new TagSupport() {
      override def doTag(output: XMLOutput): Unit = {
        //no implementation
      }
    }
    val context = new JellyContext
    val data = new ComponentBuilderData()
    data put context
    data setComponentManager mock[ComponentManager]
    tag setContext context
    tag
  }

  /**
   * Convenience method for accessing the builder data from the test tag.
   * @return the builder data object
   */
  private def builderData() = ComponentBuilderData.get(tag.getContext)

  /**
   * Tests whether an instance can install a special component manager.
   */
  @Test def testInstallComponentManagerProxy(): Unit = {
    val data = builderData()
    val oldManager = data.getComponentManager

    val newManager = manager installComponentManagerProxy tag
    assertNotSame("No special manager returned", oldManager, newManager)
    assertSame("Manager not installed", newManager, data.getComponentManager)
  }

  /**
   * Helper method for obtaining the component manager proxy.
   * @return the proxy
   */
  private def prepareComponentManagerProxy(): ComponentManager =
    manager installComponentManagerProxy tag

  /**
   * Tests whether the proxy for the component manager handles void methods correctly.
   * We can only test that nothing specific happens and no exception is thrown.
   */
  @Test def testComponentManagerProxyVoidMethod(): Unit = {
    val proxy = prepareComponentManagerProxy()
    proxy.addContainerComponent(this, "A component", "some constraints")
  }

  /**
   * Tests a method of the component manager proxy which returns a component.
   */
  @Test def testComponentManagerProxyComponentMethod(): Unit = {
    val proxy = prepareComponentManagerProxy()
    assertSame("Wrong result", manager, proxy.createLabel(new LabelTag, false))
  }

  /**
   * Tests whether a component handler proxy ignores the method getOuterComponent().
   */
  @Test def testComponentHandlerProxyIgnoreGetOuterComponent(): Unit = {
    val proxy = prepareComponentManagerProxy()
    val handler = proxy.createTextField(new TextFieldTag, false)

    assertNull("Wrong result", handler.getOuterComponent)
  }

  /**
   * Creates a mock for a tag body and installs it at the test tag.
   * @return the mock tag body
   */
  private def createTagBody(): Script = {
    val body = mock[Script]
    tag setBody body
    body
  }

  /**
   * Creates a test field handler associated with the given component handler.
   * @param compHandler the component handler
   * @return the field handler
   */
  private def createFieldHandler(compHandler: ComponentHandler[_]): FieldHandler = {
    val field = new DefaultFieldHandler
    field setPropertyName ComponentName
    field setComponentHandler compHandler
    field
  }

  /**
   * Prepares the mock tag body to expect an execution. An answer is added
   * which allows obtaining the current context form.
   * @param body the mock for the tag body
   * @return the answer providing access to the context form
   */
  private def expectTagExecution(body: Script): TagExecAnswer = {
    val answer = new TagExecAnswer
    body.run(EasyMock.anyObject(classOf[JellyContext]), EasyMock.anyObject(classOf[XMLOutput]))
    EasyMock.expectLastCall().andAnswer(answer)
    answer
  }

  /**
   * Initializes the form for the column. A component handler for the test field
   * is created and added to the form.
   * @param proxy the proxy component manager for creating the handler
   * @return the field handler for the test field
   */
  def initColumnForm(proxy: ComponentManager): FieldHandler = {
    val tag = new TextFieldTag
    tag setName ComponentName
    val handler = proxy.createTextField(tag, false)
    val field = createFieldHandler(handler)
    form.addField(ComponentName, field)
    field
  }

  /**
   * Checks whether the test field in the given form contains the expected data.
   * @param frm the form
   * @param expected the expected data
   */
  private def checkFieldData(frm: Form, expected: AnyRef): Unit = {
    val data = frm.getField(ComponentName).getComponentHandler.getData
    assertEquals("Wrong data", expected, data)
  }

  /**
   * Tests whether a component handler proxy created by the component manager proxy
   * can be used to access data in the column form.
   */
  @Test def testDataAccessViaComponentHandlerProxy(): Unit = {
    val proxy = prepareComponentManagerProxy()
    val field = initColumnForm(proxy)
    val TestValue = "SomeTestData"

    val body = createTagBody()
    val answer = expectTagExecution(body)
    whenExecuting(body) {
      manager registerCell this
      manager selectCell this
      field setData TestValue
      checkFieldData(answer.contextForm, TestValue)
    }
  }

  /**
   * Tests whether the context form created during a tag execution is initialized
   * with the expected properties.
   */
  @Test def testContextFormProperties(): Unit = {
    prepareComponentManagerProxy()
    val body = createTagBody()
    val answer = expectTagExecution(body)
    whenExecuting(body) {
      manager registerCell this

      assertEquals("Wrong transformer context", form.getTransformerContext,
        answer.contextForm.getTransformerContext)
      assertEquals("Wrong binding strategy", form.getBindingStrategy,
        answer.contextForm.getBindingStrategy)
    }
  }

  /**
   * Tests whether multiple cells can be handled; selectCell() has to set the correct current
   * cell.
   */
  @Test def testSelectCurrentCell(): Unit = {
    val proxy = prepareComponentManagerProxy()
    initColumnForm(proxy)
    val TestValue1 = "SomeTestData"
    val TestValue2 = "OtherTestData"
    val Cell1 = "Cell1"
    val Cell2 = "Cell2"

    val body = createTagBody()
    val answer1 = expectTagExecution(body)
    val answer2 = expectTagExecution(body)
    whenExecuting(body) {
      manager registerCell Cell1
      manager registerCell Cell2
      answer1.contextForm.getField(ComponentName) setData TestValue1
      answer2.contextForm.getField(ComponentName) setData TestValue2
      manager selectCell Cell1
      checkFieldData(form, TestValue1)
      manager selectCell Cell2
      checkFieldData(form, TestValue2)
    }
  }

  /**
   * Tests that the UI for a new cell is created correctly.
   */
  @Test def testUICreation(): Unit = {
    prepareComponentManagerProxy()
    val body = createTagBody()
    val answer = expectTagExecution(body)
    whenExecuting(body) {
      assertEquals("Wrong UI", answer.UIComponent, manager registerCell this)
    }
  }

  /**
   * An implementation of ''IAnswer'' which simulates the execution of a column
   * component tag. It populates the context form with a
   * component handler and retrieves the current context form. In addition,
   * the creation of the UI is simulated. This is used by
   * tests that include the execution of the test tag.
   */
  private class TagExecAnswer extends IAnswer[Void] {
    /** The UI generated by the tag. */
    val UIComponent = new Label("TestUI")

    /** The current context form when this answer was executed. */
    var contextForm: Form = _

    override def answer(): Void = {
      assertEquals("Wrong context", tag.getContext, EasyMock.getCurrentArguments()(0))
      val form = builderData().getContextForm
      val field = createFieldHandler(new JavaFxTextHandler(new TextField))
      form.addField(ComponentName, field)

      val container = builderData().getRootContainer.asInstanceOf[ContainerWrapper]
      container.addComponent(UIComponent, null)

      contextForm = form
      null
    }
  }

}

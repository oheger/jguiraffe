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

import org.apache.commons.jelly.JellyContext
import org.apache.commons.lang.StringUtils
import org.easymock.EasyMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar
import javafx.scene.Node
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.text.Text
import net.sf.jguiraffe.gui.builder.components.Color
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData
import net.sf.jguiraffe.gui.builder.components.FormBuilderException
import net.sf.jguiraffe.gui.builder.components.tags.BorderLayoutTag
import net.sf.jguiraffe.gui.builder.components.tags.ButtonLayoutTag
import net.sf.jguiraffe.gui.builder.components.tags.FontTag
import net.sf.jguiraffe.gui.builder.components.tags.LabelTag
import net.sf.jguiraffe.gui.builder.components.tags.PercentLayoutTag
import net.sf.jguiraffe.gui.forms.ComponentStoreImpl
import net.sf.jguiraffe.gui.layout.BorderLayout
import net.sf.jguiraffe.gui.layout.ButtonLayout
import net.sf.jguiraffe.gui.layout.PercentLayoutBase
import net.sf.jguiraffe.gui.platform.javafx.builder.event.JavaFxEventManager
import net.sf.jguiraffe.gui.platform.javafx.layout.ContainerWrapper
import net.sf.jguiraffe.locators.ClassPathLocator
import net.sf.jguiraffe.gui.builder.components.tags.TextFieldTag
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import net.sf.jguiraffe.gui.builder.components.tags.TextAreaTag
import javafx.scene.control.TextArea

/**
 * Test class for ''JavaFxComponentManager''.
 */
class TestJavaFxComponentManager extends JUnitSuite with EasyMockSugar {
  /** Constant for a component name. */
  private val ComponentName = "TestComponent"

  /** The manager to be tested. */
  private var manager: JavaFxComponentManager = _

  @Before def setUp() {
    manager = new JavaFxComponentManager
  }

  /**
   * Tests whether a component can be added to a container.
   */
  @Test def testAddComponent() {
    val wrapper = mock[ContainerWrapper]
    val component = mock[Node]
    val constraints = "TestConstraints"
    wrapper.addComponent(component, constraints)
    whenExecuting(wrapper, component) {
      manager.addContainerComponent(wrapper, component, constraints)
    }
  }

  /**
   * Tests addComponent() if the container is not a wrapper object.
   */
  @Test(expected = classOf[FormBuilderException])
  def testAddComponentNoWrapper() {
    manager.addContainerComponent(this, "someComponent", "someConstraints")
  }

  /**
   * Tests whether an icon can be created.
   */
  @Test def testCreateIcon() {
    val locator = ClassPathLocator.getInstance("icon.jpg")
    val icon = manager.createIcon(locator).asInstanceOf[ImageView]
    assertNotNull("No image", icon.getImage())
  }

  /**
   * Tests whether IO exceptions when loading images are handled correctly by
   * createIcon().
   */
  @Test(expected = classOf[FormBuilderException])
  def testCreateIconIOEx() {
    val locator = ClassPathLocator.getInstance("nonExistingIcon.jpg")
    manager.createIcon(locator)
  }

  /**
   * Tests whether mnemonicText() can handle null input.
   */
  @Test def testMnemonicTextNull() {
    assertNull("Wrong result", JavaFxComponentManager.mnemonicText(null, 'x'))
  }

  /**
   * Tests whether a text with a mnemonic is correctly manipulated.
   */
  @Test def testMnemonicTextFound() {
    assertEquals("Wrong result (1)", "A _Test", JavaFxComponentManager.mnemonicText("A Test", 'T'))
    assertEquals("Wrong result (2)", "_Test", JavaFxComponentManager.mnemonicText("Test", 'T'))
    assertEquals("Wrong result (3)", "ab_c", JavaFxComponentManager.mnemonicText("abc", 'c'))
    assertEquals("Wrong result (4)", "_a", JavaFxComponentManager.mnemonicText("a", 'a'))
  }

  /**
   * Tests whether whether case is ignored when searching for mnemonics.
   */
  @Test def testMnemonicTextCase() {
    assertEquals("Wrong result (1)", "a_bc", JavaFxComponentManager.mnemonicText("abc", 'B'))
    assertEquals("Wrong result (2)", "A_BC", JavaFxComponentManager.mnemonicText("ABC", 'b'))
  }

  /**
   * Tests mnemonicText() if the mnemonic cannot be found.
   */
  @Test def testMnemonicTextNotFound() {
    assertEquals("Wrong result (1)", "check", JavaFxComponentManager.mnemonicText("check", 'z'))
    assertEquals("Wrong result (2)", "", JavaFxComponentManager.mnemonicText("", 'a'))
  }

  /**
   * Tests createLabel() if the create flag is true.
   */
  @Test def testCreateLabelCreate() {
    assertNull("Wrong result", manager.createLabel(new LabelTag, true))
  }

  /**
   * Tests whether a label with an icon can be created.
   */
  @Test def testCreateLabelIcon() {
    val tag = new LabelTag
    val icon = new ImageView(new Image("icon.jpg"))
    tag.setIcon(icon)
    val label = manager.createLabel(tag, false).asInstanceOf[Label]
    assertTrue("Got a text", StringUtils.isEmpty(label.getText))
    assertEquals("Wrong icon", icon, label.getGraphic)
    assertEquals("Wrong alignment", ContentDisplay.LEFT, label.getContentDisplay)
  }

  /**
   * Tests whether a label with properties can be created.
   */
  @Test def testCreateLabelWithProperties() {
    val tag = new LabelTag
    tag.setText("Test Label")
    tag.setMnemonic("L")
    tag.setAlignment("right")
    tag.setName("componentName")
    val label = manager.createLabel(tag, false).asInstanceOf[Label]
    assertNull("Got a graphics", label.getGraphic)
    assertEquals("Wrong text", "Test _Label", label.getText)
    assertEquals("Wrong alignment", ContentDisplay.RIGHT, label.getContentDisplay)
    assertEquals("Name not set", "componentName", label.getId)
  }

  /**
   * Tests the remaining values of the alignment property.
   */
  @Test def testCreateLabelContentDisplay() {
    val tag = new LabelTag
    tag.setText("Hallo")
    tag.setAlignment("CENTER")
    val label = manager.createLabel(tag, false).asInstanceOf[Label]
    assertEquals("Wrong alignment", ContentDisplay.CENTER, label.getContentDisplay)
  }

  /**
   * Tests whether a label can be assigned to a control.
   */
  @Test def testLinkLabel() {
    val label = new Label
    val ctrl = new Text("Test")
    val labTxt = "Text for label"
    manager.linkLabel(label, ctrl, labTxt)
    assertEquals("Not linked", ctrl, label.getLabelFor)
    assertEquals("Text not set", labTxt, label.getText)
  }

  /**
   * Tests whether a container can be assigned a layout.
   */
  @Test def testSetContainerLayout() {
    val wrapper = mock[ContainerWrapper]
    val layout = mock[PercentLayoutBase]
    wrapper.initLayout(layout)
    whenExecuting(wrapper, layout) {
      manager.setContainerLayout(wrapper, layout)
    }
  }

  /**
   * Tests whether a percent layout can be created.
   */
  @Test def testCreatePercentLayout() {
    val tag = mock[PercentLayoutTag]
    val layout = mock[PercentLayoutBase]
    EasyMock.expect(tag.getPercentLayout()).andReturn(layout)
    whenExecuting(tag, layout) {
      assertSame("Wrong layout", layout, manager.createPercentLayout(tag))
    }
  }

  /**
   * Tests whether a button layout can be created.
   */
  @Test def testCreateButtonLayout() {
    val tag = mock[ButtonLayoutTag]
    val layout = mock[ButtonLayout]
    EasyMock.expect(tag.getButtonLayout()).andReturn(layout)
    whenExecuting(tag, layout) {
      assertSame("Wrong layout", layout, manager.createButtonLayout(tag))
    }
  }

  /**
   * Tests whether a border layout can be created.
   */
  @Test def testCreateBorderLayout() {
    val tag = mock[BorderLayoutTag]
    val layout = mock[BorderLayout]
    EasyMock.expect(tag.getBorderLayout()).andReturn(layout)
    whenExecuting(tag, layout) {
      assertSame("Wrong layout", layout, manager.createBorderLayout(tag))
    }
  }

  /**
   * Tests whether a font can be created if all properties are undefined.
   */
  @Test def testCreateFontUndefined() {
    val tag = new FontTag
    val font = manager.createFont(tag).asInstanceOf[JavaFxFont]
    assertFalse("Got a family", font.family.isDefined)
    assertFalse("Got a size", font.size.isDefined)
    assertEquals("Wrong weight", "normal", font.weight.get)
    assertEquals("Wrong style", "normal", font.style.get)
    assertFalse("Got a font def", font.fontDef.isDefined)
  }

  /**
   * Tests whether font properties are correctly set.
   */
  @Test def testCreateFontWithProperties() {
    val tag = new FontTag
    tag.setName("MyFontName")
    tag.setSize(42)
    tag.setBold(true)
    tag.setItalic(true)
    val font = manager.createFont(tag).asInstanceOf[JavaFxFont]
    assertEquals("Wrong family", '"' + tag.getName() + '"', font.family.get)
    assertEquals("Wrong size", "42px", font.size.get)
    assertEquals("Wrong weight", "bold", font.weight.get)
    assertEquals("Wrong style", "italic", font.style.get)
    assertFalse("Got a font def", font.fontDef.isDefined)
  }

  /**
   * Tests whether the background color property is set correctly when creating
   * a control.
   */
  @Test def testInitControlBackgroundColor() {
    val tag = new LabelTag {
      override def getBackgroundColor: Color = Color.newRGBInstance(0x80, 0xff, 0x80)
    }
    tag.setText("Test Label")
    val ctrl = manager.createLabel(tag, false).asInstanceOf[Label]
    val style = ctrl.getStyle()
    assertTrue("Background color not set: " + style,
      style.contains("-fx-background-color: #80ff80"))
  }

  /**
   * Tests whether the foreground color property is set correctly when creating
   * a control.
   */
  @Test def testInitControlForegroundColor() {
    val tag = new LabelTag {
      override def getForegroundColor: Color = Color.newRGBInstance(0x40, 0xff, 0x80)
    }
    tag.setText("Test Label")
    val ctrl = manager.createLabel(tag, false).asInstanceOf[Label]
    val style = ctrl.getStyle()
    assertTrue("Foreground color not set: " + style,
      style.contains("-fx-text-fill: #40ff80"))
  }

  /**
   * Tests whether the font is correctly evaluated when creating a control.
   */
  @Test def testInitControlFont() {
    val tag = new LabelTag
    val font = JavaFxFont(size = Some("20"))
    tag setFont font
    val ctrl = manager.createLabel(tag, false).asInstanceOf[Label]
    val style = ctrl.getStyle()
    assertTrue("Font style not set: " + style, style.contains("-fx-font-size"))
  }

  /**
   * Tests whether an appropriate default tool tip factory is created.
   */
  @Test def testDefaultToolTipFactory() {
    assertTrue("Wrong default tool tip factory",
      manager.toolTipFactory.isInstanceOf[DefaultToolTipFactory])
  }

  /**
   * Tests whether the tool tip is evaluated when creating a control.
   */
  @Test def testInitControlToolTip() {
    val tag = new LabelTag
    val tip = "MyToolTip"
    tag setTooltip tip
    tag setContext (new JellyContext)
    val builderData = new ComponentBuilderData
    builderData.put(tag.getContext())
    builderData.pushComponentStore(new ComponentStoreImpl)
    val label = manager.createLabel(tag, false)
    val callBack = ToolTipCreationCallBack.getInstance(tag, null)

    assertSame("Wrong tool tip factory", manager.toolTipFactory,
      callBack.toolTipFactory)
    assertEquals("Wrong number of requests", 1, callBack.requests.size)
    val req = callBack.requests.head
    assertEquals("Wrong control", label, req.control)
    assertEquals("Wrong tip text", tip, req.tip)
  }

  /**
   * Tests whether a widget handler can be created.
   */
  @Test def testCreateWidgetHandler() {
    val widget = new Label
    val handler = manager.getWidgetHandlerFor(widget)
      .asInstanceOf[JavaFxWidgetHandler]
    assertSame("Wrong wrapped widget", widget, handler.widget)
    assertSame("Wrong tool tip factory", manager.toolTipFactory,
      handler.toolTipFactory)
  }

  /**
   * Tests whether an event manager can be created.
   */
  @Test def testCreateEventManager() {
    val evMan = manager.createEventManager()
    assertTrue("Wrong event manager: " + evMan,
      evMan.isInstanceOf[JavaFxEventManager])
  }

  /**
   * Tests createTextField() if the create flag is set.
   */
  @Test def testCreateTextFieldCreateFlag() {
    assertNull("Got a result", manager.createTextField(new TextFieldTag, true))
  }

  /**
   * Helper method for testing whether the given text control has the expected
   * maximum length initialized.
   * @param ctrl the control
   * @param expMaxLen the expected maximum length property
   */
  private def checkMaxTextLength(ctrl: TextInputControl, expMaxLen: Int) {
    val lenRestr = ctrl.asInstanceOf[TextLengthRestriction]
    assertEquals("Wrong max length", expMaxLen, lenRestr.getMaximumLength)
  }

  /**
   * Tests whether a text field can be created if no text-specific attributes
   * are provided, but only some basic attributes common to all controls.
   */
  @Test def testCreateTextFieldComponentAttributes() {
    val tag = new TextFieldTag
    tag setName ComponentName
    val handler = manager.createTextField(tag, false).asInstanceOf[JavaFxTextHandler]
    val txtCtrl = handler.component.asInstanceOf[TextField]
    assertEquals("Control not initialized", ComponentName, txtCtrl.getId)
    checkMaxTextLength(txtCtrl, 0)
  }

  /**
   * Tests whether specific attributes for text components are correctly
   * evaluated.
   */
  @Test def testCreateTextFieldTextAttributes() {
    val tag = new TextFieldTag
    tag setColumns 20
    tag setMaxlength 30
    val handler = manager.createTextField(tag, false)
    val txtCtrl = handler.getComponent.asInstanceOf[TextField]
    assertEquals("Wrong preferred column count", tag.getColumns,
      txtCtrl.getPrefColumnCount)
    checkMaxTextLength(txtCtrl, tag.getMaxlength)
  }

  /**
   * Tests createTextArea() if the create flag is set.
   */
  @Test def testCreateTextAreaCreateFlag() {
    assertNull("Got a result", manager.createTextArea(new TextAreaTag, true))
  }

  /**
   * Tests whether a text area can be created if no text-specific attributes
   * are provided, but only some basic attributes common to all controls.
   */
  @Test def testCreateTextAreaComponentAttributes() {
    val tag = new TextAreaTag
    tag setName ComponentName
    val handler = manager.createTextArea(tag, false).asInstanceOf[JavaFxTextHandler]
    val txtCtrl = handler.component.asInstanceOf[TextArea]
    assertEquals("Control not initialized", ComponentName, txtCtrl.getId)
    assertFalse("Wrap flag is set", txtCtrl.isWrapText)
    checkMaxTextLength(txtCtrl, 0)
  }

  /**
   * Tests whether specific attributes for text areas are correctly evaluated
   * when creating a text area.
   */
  @Test def testCreateTextAreaTextAttributes() {
    val tag = new TextAreaTag
    tag setColumns 50
    tag setRows 25
    tag setWrap true
    tag setMaxlength 1000
    val handler = manager.createTextArea(tag, false)
    val txtCtrl = handler.getComponent.asInstanceOf[TextArea]
    assertEquals("Preferred columns not set", tag.getColumns,
        txtCtrl.getPrefColumnCount)
    assertEquals("Preferred rows not set", tag.getRows, txtCtrl.getPrefRowCount)
    assertTrue("Wrap flag not set", txtCtrl.isWrapText)
    checkMaxTextLength(txtCtrl, tag.getMaxlength)
  }
}

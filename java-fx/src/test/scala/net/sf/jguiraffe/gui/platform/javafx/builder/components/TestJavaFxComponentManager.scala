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

import org.apache.commons.lang.StringUtils
import org.junit.Assert.assertEquals
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
import net.sf.jguiraffe.gui.builder.components.FormBuilderException
import net.sf.jguiraffe.gui.builder.components.tags.LabelTag
import net.sf.jguiraffe.gui.platform.javafx.layout.ContainerWrapper
import net.sf.jguiraffe.locators.ClassPathLocator
import net.sf.jguiraffe.gui.layout.PercentLayout
import net.sf.jguiraffe.gui.platform.javafx.layout.PercentLayoutPane
import net.sf.jguiraffe.gui.layout.PercentLayoutBase
import net.sf.jguiraffe.gui.builder.components.tags.PercentLayoutTag
import org.easymock.EasyMock
import net.sf.jguiraffe.gui.builder.components.tags.ButtonLayoutTag
import net.sf.jguiraffe.gui.layout.ButtonLayout
import net.sf.jguiraffe.gui.builder.components.tags.BorderLayoutTag
import net.sf.jguiraffe.gui.layout.BorderLayout

/**
 * Test class for ''JavaFxComponentManager''.
 */
class TestJavaFxComponentManager extends JUnitSuite with EasyMockSugar {
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
}

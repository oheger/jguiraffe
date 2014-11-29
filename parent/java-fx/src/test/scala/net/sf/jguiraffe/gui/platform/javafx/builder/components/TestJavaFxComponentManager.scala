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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.FlowPane
import javafx.scene.text.Text

import net.sf.jguiraffe.gui.builder.components.tags.table.{ColumnRendererTag, TableFormController, TableTag}
import net.sf.jguiraffe.gui.builder.components.tags.{BorderLayoutTag, ButtonLayoutTag, ButtonTag, CheckboxTag, ComboBoxTag, FontTag, LabelTag, ListBoxTag, PanelTag, PasswordFieldTag, PercentLayoutTag, ProgressBarTag, RadioButtonTag, SliderTag, SplitterTag, StaticTextTag, TabbedPaneTag, TextAreaTag, TextFieldTag, ToggleButtonTag, TreeTag}
import net.sf.jguiraffe.gui.builder.components._
import net.sf.jguiraffe.gui.forms.{ComponentHandler, Form}
import net.sf.jguiraffe.gui.layout.{BorderLayout, ButtonLayout, PercentLayoutBase, UnitSizeHandler}
import net.sf.jguiraffe.gui.platform.javafx.builder.components.table.{CellComponentManager, TableHandlerFactory}
import net.sf.jguiraffe.gui.platform.javafx.builder.components.tree.TreeHandlerFactory
import net.sf.jguiraffe.gui.platform.javafx.builder.components.widget.{MenuItemWidgetHandler,
ControlWidgetHandler, NodeWidgetHandler, JavaFxFont}
import net.sf.jguiraffe.gui.platform.javafx.builder.event.JavaFxEventManager
import net.sf.jguiraffe.gui.platform.javafx.common.{DefaultToolTipFactory, ImageWrapper, MockToolTipCreationSupport}
import net.sf.jguiraffe.gui.platform.javafx.layout.{ContainerWrapper, JavaFxUnitSizeHandler}
import net.sf.jguiraffe.locators.ClassPathLocator
import org.apache.commons.jelly.{JellyContext, Tag}
import org.apache.commons.lang.StringUtils
import org.easymock.EasyMock
import org.junit.Assert.{assertEquals, assertFalse, assertNotNull, assertNull, assertSame, assertTrue}
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

/**
 * Test class for ''JavaFxComponentManager''.
 */
class TestJavaFxComponentManager extends JUnitSuite with EasyMockSugar {
  /** Constant for a component name. */
  private val ComponentName = "TestComponent"

  /** The manager to be tested. */
  private var manager: JavaFxComponentManager = _

  @Before def setUp() {
    manager = new JavaFxComponentManager with MockToolTipCreationSupport
  }

  /**
   * Checks whether the specified control has been initialized with its
   * default size.
   * @param ctrl the control to be checked
   */
  private def checkDefaultSize(ctrl: Control) {
    assertEquals("Got a preferred width", Control.USE_COMPUTED_SIZE,
      ctrl.getPrefWidth, .001)
    assertEquals("Got a preferred height", Control.USE_COMPUTED_SIZE,
      ctrl.getPrefHeight, .001)
  }

  /**
   * Returns an object for testing tool tip creation.
   * @return the ''MockToolTipCreationSupport''
   */
  private def mockToolTipSupport(): MockToolTipCreationSupport = manager
    .asInstanceOf[MockToolTipCreationSupport]

  /**
   * Creates a ''JellyContext'' and installs it in the given tag.
   * @param tag the tag
   * @return the ''JellyContext''
   */
  private def createAndInstallContext(tag: Tag): JellyContext = {
    val context = new JellyContext
    tag setContext context
    context
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
    val icon = manager.createIcon(locator).asInstanceOf[ImageWrapper]
    assertNotNull("No image", icon.image)
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
    val context = createAndInstallContext(tag)
    val tip = "MyToolTip"
    tag setTooltip tip
    val label = manager.createLabel(tag, create = false).asInstanceOf[Label]

    val tipSupport = mockToolTipSupport()
    assertFalse("Too many tooltips", tipSupport.verifyToolTipCreationRequest(context, label, tip))
  }

  /**
   * Tests whether a widget handler for a plain node can be created.
   */
  @Test def testWidgetHandlerForNode(): Unit = {
    val node = new ImageView
    val handler = manager getWidgetHandlerFor node

    assertTrue("Wrong widget handler type", handler.isInstanceOf[NodeWidgetHandler])
    assertSame("Wrong widget", node, handler.getWidget)
  }

  /**
   * Tests whether a widget handler for a control can be created.
   */
  @Test def testWidgetHandlerForControl() {
    val widget = new Label
    val handler = manager.getWidgetHandlerFor(widget)
      .asInstanceOf[ControlWidgetHandler]
    assertSame("Wrong wrapped widget", widget, handler.widget)
    assertSame("Wrong tool tip factory", manager.toolTipFactory,
      handler.toolTipFactory)
  }

  /**
   * Tests whether a widget handler for a menu item can be created.
   */
  @Test def testWidgetHandlerForMenuItem(): Unit = {
    val item = new MenuItem
    val handler = manager getWidgetHandlerFor item

    assertTrue("Wrong widget handler type", handler.isInstanceOf[MenuItemWidgetHandler])
    assertSame("Wrong widget", item, handler.getWidget)
  }

  /**
   * Tests whether an exception is thrown if an unsupported object is passed to
   * widgetHandlerFor().
   */
  @Test(expected = classOf[FormBuilderRuntimeException]) def testWidgetHandlerForUnknownObject():
  Unit = {
    manager getWidgetHandlerFor this
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
    val sizeHandler = mock[UnitSizeHandler]
    val tag = new TextAreaTag with ScrollSizeSupportUndefined
    val context = new JellyContext
    tag setContext context
    JavaFxComponentManager.installSizeHandler(tag, sizeHandler)
    tag setName ComponentName
    val handler = manager.createTextArea(tag, false).asInstanceOf[JavaFxTextHandler]
    val txtCtrl = handler.component.asInstanceOf[TextArea]
    assertEquals("Control not initialized", ComponentName, txtCtrl.getId)
    assertFalse("Wrap flag is set", txtCtrl.isWrapText)
    checkMaxTextLength(txtCtrl, 0)
    checkDefaultSize(txtCtrl)
    tag.verify(sizeHandler)
  }

  /**
   * Tests whether specific attributes for text areas are correctly evaluated
   * when creating a text area.
   */
  @Test def testCreateTextAreaTextAttributes() {
    val sizeHandler = mock[UnitSizeHandler]
    val tag = new TextAreaTag with ScrollSizeSupportSpecific
    val context = new JellyContext
    tag setContext context
    JavaFxComponentManager.installSizeHandler(tag, sizeHandler)
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
    assertEquals("Wrong preferred width", tag.xScrollSize, txtCtrl.getPrefWidth.toInt)
    assertEquals("Wrong preferred height", tag.yScrollSize, txtCtrl.getPrefHeight.toInt)
    tag.verify(sizeHandler)
  }

  /**
   * Tests whether a password field can be created.
   */
  @Test def testCreatePasswordField() {
    val tag = new PasswordFieldTag
    tag setColumns 10
    tag setMaxlength 20
    val handler = manager.createPasswordField(tag, false)
    val txtCtrl = handler.getComponent.asInstanceOf[PasswordField]
    assertEquals("Preferred columns not set", tag.getColumns,
      txtCtrl.getPrefColumnCount)
    checkMaxTextLength(txtCtrl, tag.getMaxlength)
  }

  /**
   * Tests the creation of a static text component if the create flag is true.
   */
  @Test def testCreateStaticTextCreate() {
    assertNull("Got a static text", manager.createStaticText(new StaticTextTag,
      true))
  }

  /**
   * Tests whether a static text component can be correctly created.
   */
  @Test def testCreateStaticText() {
    val Text = "TestStaticText"
    val tag = new StaticTextTag
    tag setText Text
    val handler = manager.createStaticText(tag, false)
      .asInstanceOf[JavaFxStaticTextHandler]
    assertEquals("Label not initialized", Text, handler.getText)
  }

  /**
   * Tests the creation of a button component if the create flag is set.
   */
  @Test def testCreateButtonCreate() {
    assertNull("Got a button", manager.createButton(new ButtonTag, true))
  }

  /**
   * Tests whether a button can be created if no properties are provided.
   */
  @Test def testCreateButtonNoProperties() {
    val btnHandler = manager.createButton(new ButtonTag, false)
      .asInstanceOf[JavaFxButtonHandler]
    val button = btnHandler.component.asInstanceOf[Button]
    assertTrue("Got text", StringUtils.isEmpty(button.getText))
    assertFalse("A default button", button.isDefaultButton)
    assertFalse("A cancel button", button.isCancelButton)
    assertNull("Got an action command", btnHandler.actionCommand)
  }

  /**
   * Tests whether properties are evaluated when creating a button component.
   */
  @Test def testCreateButtonWithProperties() {
    val Text = "TestButton"
    val Command = "TestButtonActionCommand"
    val tag = new ButtonTag
    tag setText Text
    tag setDefault true
    tag setCommand Command
    val btnHandler = manager.createButton(tag, false)
      .asInstanceOf[JavaFxButtonHandler]
    val button = btnHandler.component.asInstanceOf[Button]
    assertEquals("Wrong text", Text, button.getText)
    assertTrue("Not the default button", button.isDefaultButton)
    assertFalse("A cancel button", button.isCancelButton)
    assertEquals("Wrong action command", Command, btnHandler.actionCommand)
  }

  /**
   * Tests whether a handler for a regular button can be created.
   */
  @Test def testCreateButtonHandler(): Unit = {
    val button = new Button
    val Command = "MyButtonCommand"
    val handler = manager.createButtonHandler(button, Command).asInstanceOf[JavaFxButtonHandler]
    assertSame("Wrong wrapped button", button, handler.getComponent)
    assertEquals("Wrong command", Command, handler.actionCommand)
  }

  /**
   * Tests whether a handler for a toggle button can be created.
   */
  @Test def testCreateButtonHandlerForToggleButton(): Unit = {
    val button = new ToggleButton
    val Command = "MyButtonCommand"
    val handler = manager.createButtonHandler(button,
      Command).asInstanceOf[JavaFxToggleButtonHandler]
    assertSame("Wrong wrapped button", button, handler.getComponent)
    assertEquals("Wrong command", Command, handler.actionCommand)
  }

  /**
   * Tests the creation of a check box if the create flag is set.
   */
  @Test def testCreateCheckboxCreate() {
    assertNull("Got a check box", manager.createCheckbox(new CheckboxTag, true))
  }

  /**
   * Tests whether a check box can be created and initialized.
   */
  @Test def testCreateCheckbox() {
    val Text = "TestCheckBox"
    val tag = new CheckboxTag
    tag setText Text
    val handler = manager.createCheckbox(tag, false).asInstanceOf[JavaFxCheckBoxHandler]
    val checkBox = handler.component.asInstanceOf[CheckBox]
    assertEquals("Text not set", Text, checkBox.getText)
    assertFalse("Already selected", handler.getData)
  }

  /**
   * Tests the creation of a toggle button if the create flag is set.
   */
  @Test def testCreateToggleButtonCreate() {
    assertNull("Got a toggle button",
      manager.createToggleButton(new ToggleButtonTag, true))
  }

  /**
   * Tests whether a toggle button can be created and initialized.
   */
  @Test def testCreateToggleButton() {
    val Text = "TestToggleButton"
    val Command = "MyToggleButtonActionCommand"
    val tag = new ToggleButtonTag
    tag setText Text
    tag setCommand Command
    val handler = manager.createToggleButton(tag, false)
      .asInstanceOf[JavaFxToggleButtonHandler]
    val button = handler.component.asInstanceOf[ToggleButton]
    assertEquals("Text not set", Text, button.getText)
    assertNull("Got a toggle button group", button.getToggleGroup)
    assertFalse("Button already selected", button.isSelected)
    assertEquals("Wrong action command", Command, handler.actionCommand)
  }

  /**
   * Tests the creation of a radio button if the create flag is set.
   */
  @Test def testCreateRadioButtonCreate() {
    assertNull("Got a radio button",
      manager.createRadioButton(new RadioButtonTag, true))
  }

  /**
   * Tests whether a radio button can be created and initialized.
   */
  @Test def testCreateRadioButton() {
    val Text = "TestRadioButton"
    val tag = new RadioButtonTag
    tag setText Text
    val handler = manager.createRadioButton(tag, false)
      .asInstanceOf[JavaFxToggleButtonHandler]
    val radio = handler.component.asInstanceOf[RadioButton]
    assertEquals("Text not set", Text, radio.getText)
    assertNull("Got a toggle group", radio.getToggleGroup)
    assertFalse("Already selected", radio.isSelected)
  }

  /**
   * Tests whether a radio group can be created.
   */
  @Test def testCreateRadioGroup() {
    import scala.collection.JavaConversions._
    val radio1 = new RadioButton
    val radio2 = new RadioButton
    val map = Map("radio1" -> radio1.asInstanceOf[Object], "radio2" -> radio2)
    val group = manager.createRadioGroup(map).asInstanceOf[ToggleGroup]
    assertEquals("Group not set for R1", group, radio1.getToggleGroup)
    assertEquals("Group not set for R2", group, radio2.getToggleGroup)
  }

  /**
   * Tests the creation of a tab pane if the create flag is set.
   */
  @Test def testCreateTabbedPaneCreate() {
    assertNull("Got a tab pane", manager.createTabbedPane(new TabbedPaneTag, true))
  }

  /**
   * Tests whether a tab pane can be created and initialized.
   */
  @Test def testCreateTabbedPane() {
    val tag = new TabbedPaneTag
    val context = createAndInstallContext(tag)
    tag setPlacementValue TabbedPaneTag.Placement.RIGHT
    val tabData1 = new TabbedPaneTag.TabData
    val icon = manager.createIcon(
      ClassPathLocator.getInstance("icon.jpg")).asInstanceOf[ImageWrapper]
    tabData1 setIcon icon
    tabData1 setComponent new Label("Test")
    val tabData2 = new TabbedPaneTag.TabData
    tabData2 setTitle "TestTitle"
    tabData2 setToolTip "TestToolTip"
    tabData2 setComponent new ContainerWrapper
    tag.getTabs add tabData1
    tag.getTabs add tabData2
    val handler = manager.createTabbedPane(tag, create = false).asInstanceOf[JavaFxTabPaneHandler]
    assertEquals("Wrong selected index", 0, handler.getData.intValue)
    val tabPane = handler.component.asInstanceOf[TabPane]
    assertEquals("Wrong side of tabs", Side.RIGHT, tabPane.getSide)
    assertEquals("Wrong number of tabs", 2, tabPane.getTabs.size)
    val tab1 = tabPane.getTabs.get(0)
    assertEquals("Wrong icon", icon.image, tab1.getGraphic.asInstanceOf[ImageView].getImage)
    assertFalse("Closeable", tab1.isClosable)
    assertTrue("Got a title", StringUtils.isEmpty(tab1.getText))
    assertNull("Got a tool tip", tab1.getTooltip)
    assertEquals("Wrong content", tabData1.getComponent, tab1.getContent)
    val tab2 = tabPane.getTabs.get(1)
    assertNull("Got an icon", tab2.getGraphic)
    assertEquals("Wrong text", tabData2.getTitle, tab2.getText)
    assertTrue("ContainerWrapper not resolved",
      tab2.getContent.isInstanceOf[FlowPane])
    val tipSupport = mockToolTipSupport()
    assertFalse("Too many tooltips", tipSupport.verifyToolTipCreationRequest(context,
      tab2.tooltipProperty(), tabData2.getToolTip))
  }

  /**
   * Tests whether invalid content is detected when creating a tab pane.
   */
  @Test(expected = classOf[FormBuilderException])
  def testCreateTabbedPaneInvalidContent() {
    val tag = new TabbedPaneTag
    val tabData = new TabbedPaneTag.TabData
    tabData setComponent this
    tag.getTabs add tabData
    manager.createTabbedPane(tag, false)
  }

  /**
   * Tests the creation of a panel if the create flag is true.
   */
  @Test def testCreatePanelCreate() {
    assertNull("Got a panel", manager.createPanel(new PanelTag, true))
  }

  /**
   * Tests whether a panel can be created.
   */
  @Test def testCreatePanelCreateFalse() {
    val sizeHandler = mock[UnitSizeHandler]
    val tag = new PanelTag
    val context = new JellyContext
    tag setContext context
    JavaFxComponentManager.installSizeHandler(tag, sizeHandler)

    val wrapper = manager.createPanel(tag, false).asInstanceOf[ContainerWrapper]
    assertSame("Size handler not initialized", sizeHandler,
      wrapper.sizeHandler.get)
  }

  /**
   * Tests the creation of a combo box if the create flag is true.
   */
  @Test def testCreateComboBoxCreate() {
    assertNull("Got a control", manager.createComboBox(new ComboBoxTag, true))
  }

  /**
   * Helper method for testing whether a combo box can be created.
   * @param editable flag whether the combo box should be editable
   */
  private def checkCreateComboBox(editable: Boolean) {
    val model = new ListModelTestImpl
    val tag = new ComboBoxTag
    tag setEditable editable
    tag setListModel model
    val handler = manager.createComboBox(tag, false).asInstanceOf[JavaFxComboBoxHandler]
    val combo = handler.component.asInstanceOf[ComboBox[Object]]
    assertEquals("Wrong editable flag", editable, combo.isEditable)
    val listModel = handler.getListModel
    assertTrue("Wrong model: " + listModel, listModel.isInstanceOf[JavaFxListModel])
    assertEquals("List model not initialized", model.size, listModel.size)
  }

  /**
   * Tests whether a non editable combo box can be created.
   */
  @Test def testCreateComboBoxNonEditable() {
    checkCreateComboBox(false)
  }

  /**
   * Tests whether an editable combo box can be created.
   */
  @Test def testCreateComboBoxEditable() {
    checkCreateComboBox(true)
  }

  /**
   * Tests the creation of a list box if the create flag is true.
   */
  @Test def testCreateListBoxCreate() {
    assertNull("Got a control", manager.createListBox(new ListBoxTag, true))
  }

  /**
   * Helper method for testing the creation of a list box.
   * @param multi flag for multiple selection
   * @param expHandlerClass the expected handler class
   * @param expSelMode the expected selection mode
   */
  private def checkCreateListBox(multi: Boolean, expHandlerClass: Class[_],
    expSelMode: SelectionMode) {
    val sizeHandler = mock[UnitSizeHandler]
    val model = new ListModelTestImpl
    val tag = new ListBoxTag with ScrollSizeSupportUndefined
    val context = new JellyContext
    tag setContext context
    tag setListModel model
    tag setMulti multi
    JavaFxComponentManager.installSizeHandler(tag, sizeHandler)
    val handler = manager.createListBox(tag, false)
    val modelSupport = handler.asInstanceOf[ListModelSupport]
    assertEquals("Wrong handler", expHandlerClass, handler.getClass)
    assertTrue("Wrong model: " + modelSupport.getListModel,
      modelSupport.getListModel.isInstanceOf[JavaFxListModel])
    assertEquals("List model not initialized", model.size,
      modelSupport.getListModel.size)
    val list = handler.getComponent.asInstanceOf[ListView[Object]]
    assertEquals("Wrong selection mode", expSelMode,
      list.getSelectionModel.getSelectionMode)
    checkDefaultSize(list)
    tag.verify(sizeHandler)
  }

  /**
   * Tests whether a list box with single selection can be created.
   */
  @Test def testCreateListBoxSingleSelection() {
    checkCreateListBox(false, classOf[JavaFxListViewHandler],
      SelectionMode.SINGLE)
  }

  /**
   * Tests whether a list box with multiple selection can be created.
   */
  @Test def testCreateListBoxMultiSelection() {
    checkCreateListBox(true, classOf[JavaFxMultiSelectionListHandler],
      SelectionMode.MULTIPLE)
  }

  /**
   * Tests whether the preferred scroll size of a list box is taken into
   * account.
   */
  @Test def testCreateListBoxScrollSize() {
    val sizeHandler = mock[UnitSizeHandler]
    val model = new ListModelTestImpl
    val tag = new ListBoxTag with ScrollSizeSupportSpecific
    val context = new JellyContext
    tag setContext context
    tag setListModel model
    JavaFxComponentManager.installSizeHandler(tag, sizeHandler)
    val handler = manager.createListBox(tag, false)
    val list = handler.getComponent.asInstanceOf[ListView[Object]]
    assertEquals("Wrong scroll width", tag.xScrollSize, list.getPrefWidth.toInt)
    assertEquals("Wrong scroll height", tag.yScrollSize, list.getPrefHeight.toInt)
    tag.verify(sizeHandler)
  }

  /**
   * Tests whether the size handler can be queried if a new instance has to
   * be created.
   */
  @Test def testCurrentSizeHandlerNewInstance() {
    val context = new JellyContext
    val tag = new LabelTag
    tag setContext context
    assertTrue("Wrong size handler",
      JavaFxComponentManager.fetchSizeHandler(tag).isInstanceOf[JavaFxUnitSizeHandler])
  }

  /**
   * Tests whether the size handler is cached in the current Jelly context.
   */
  @Test def testCurrentSizeHandlerCached() {
    val handler = mock[UnitSizeHandler]
    val context = new JellyContext
    val tag = new LabelTag
    tag setContext context
    JavaFxComponentManager.installSizeHandler(tag, handler)
    assertSame("Wrong handler", handler,
      JavaFxComponentManager.fetchSizeHandler(tag))
  }

  /**
   * Tests whether a correct default factory for tree handlers is used.
   */
  @Test def testDefaultTreeHandlerFactory() {
    assertNotNull("No tree handler factory", manager.treeHandlerFactory)
  }

  /**
   * Tests the creation of a tree view if the create flag is set.
   */
  @Test def testCreateTreeCreate() {
    assertNull("Got a component", manager.createTree(new TreeTag, true))
  }

  /**
   * Tests whether a tree view component can be created.
   */
  @Test def testCreateTree() {
    val sizeHandler = mock[UnitSizeHandler]
    val handler = mock[ComponentHandler[Object]]
    val factory = mock[TreeHandlerFactory]
    val tag = new TreeTag with ScrollSizeSupportUndefined
    val treeView = new TreeView[String]
    val context = new JellyContext
    tag setContext context
    EasyMock.expect(factory.createTreeHandler(tag)).andReturn(handler)
    EasyMock.expect(handler.getComponent).andReturn(treeView).anyTimes()
    JavaFxComponentManager.installSizeHandler(tag, sizeHandler)

    whenExecuting(handler, factory) {
      manager = new JavaFxComponentManager(toolTipFactory = new DefaultToolTipFactory,
        treeHandlerFactory = factory, tableHandlerFactory = null, splitPaneFactory = null)
      assertSame("Wrong handler", handler, manager.createTree(tag, false))
    }
    checkDefaultSize(treeView)
  }

  /**
   * Tests whether the scroll size is taken into account when creating a tree
   * view component.
   */
  @Test def testCreateTreeScrollSize() {
    val sizeHandler = mock[UnitSizeHandler]
    val handler = mock[ComponentHandler[Object]]
    val factory = mock[TreeHandlerFactory]
    val tag = new TreeTag with ScrollSizeSupportSpecific
    val treeView = new TreeView[String]
    val context = new JellyContext
    tag setContext context
    EasyMock.expect(factory.createTreeHandler(tag)).andReturn(handler)
    EasyMock.expect(handler.getComponent).andReturn(treeView).anyTimes()
    JavaFxComponentManager.installSizeHandler(tag, sizeHandler)

    whenExecuting(handler, factory) {
      manager = new JavaFxComponentManager(toolTipFactory = new DefaultToolTipFactory,
        treeHandlerFactory = factory, tableHandlerFactory = null, splitPaneFactory = null)
      assertSame("Wrong handler", handler, manager.createTree(tag, false))
    }
    assertEquals("Wrong scroll width", tag.xScrollSize, treeView.getPrefWidth.toInt)
    assertEquals("Wrong scroll height", tag.yScrollSize, treeView.getPrefHeight.toInt)
    tag.verify(sizeHandler)
  }

  /**
   * Tests whether a default factory for table handlers is created.
   */
  @Test def testDefaultTableHandlerFactory() {
    assertNotNull("No table handler factory", manager.tableHandlerFactory)
  }

  /**
   * Tests the creation of a table view if the create flag is set.
   */
  @Test def testCreateTableCreate() {
    assertNull("Got a handler", manager.createTable(new TableTag, create = true))
  }

  /**
   * Tests whether a table can be created.
   */
  @Test def testCreateTable() {
    val sizeHandler = mock[UnitSizeHandler]
    val handler = mock[ComponentHandler[Object]]
    val factory = mock[TableHandlerFactory]
    val controller = mock[TableFormController]
    val tag = new TableTagTestImpl(controller) with ScrollSizeSupportUndefined
    val tableView = new TableView[AnyRef]
    val context = new JellyContext
    tag setContext context
    EasyMock.expect(factory.createTableHandler(controller)).andReturn(handler)
    EasyMock.expect(handler.getComponent).andReturn(tableView).anyTimes()
    JavaFxComponentManager.installSizeHandler(tag, sizeHandler)

    whenExecuting(handler, factory) {
      manager = new JavaFxComponentManager(toolTipFactory = new DefaultToolTipFactory,
        tableHandlerFactory = factory, treeHandlerFactory = null, splitPaneFactory = null)
      assertSame("Wrong handler", handler, manager.createTable(tag, create = false))
    }
    checkDefaultSize(tableView)
  }

  /**
   * Tests that a scroll size definition is taken into account when creating a table.
   */
  @Test def testCreateTableScrollSize() {
    val sizeHandler = mock[UnitSizeHandler]
    val handler = mock[ComponentHandler[Object]]
    val factory = mock[TableHandlerFactory]
    val controller = mock[TableFormController]
    val tag = new TableTagTestImpl(controller) with ScrollSizeSupportSpecific
    val tableView = new TableView[AnyRef]
    val context = new JellyContext
    tag setContext context
    EasyMock.expect(factory.createTableHandler(controller)).andReturn(handler)
    EasyMock.expect(handler.getComponent).andReturn(tableView).anyTimes()
    JavaFxComponentManager.installSizeHandler(tag, sizeHandler)

    whenExecuting(handler, factory) {
      manager = new JavaFxComponentManager(toolTipFactory = new DefaultToolTipFactory,
        tableHandlerFactory = factory, treeHandlerFactory = null, splitPaneFactory = null)
      assertSame("Wrong handler", handler, manager.createTable(tag, create = false))
    }
    assertEquals("Wrong scroll width", tag.xScrollSize, tableView.getPrefWidth.toInt)
    assertEquals("Wrong scroll height", tag.yScrollSize, tableView.getPrefHeight.toInt)
    tag.verify(sizeHandler)
  }

  /**
   * Tests the creation of a progress bar if the create flag is set.
   */
  @Test def testCreateProgressBarCreate() {
    assertNull("Got a handler", manager.createProgressBar(new ProgressBarTag, true))
  }

  /**
   * Tests whether a progress bar component can be created.
   */
  @Test def testCreateProgressBar() {
    val tag = new ProgressBarTag
    tag setMin 5
    tag setMax 80
    tag setValue 20
    val handler = manager.createProgressBar(tag, false).asInstanceOf[JavaFxProgressBarHandler]
    assertEquals("Wrong minimum", tag.getMin, handler.min)
    assertEquals("Wrong maximum", tag.getMax, handler.max)
    assertEquals("Wrong value", tag.getValue, handler.getValue)
  }

  /**
   * Tests the creation of a progress bar if no initial value was specified.
   */
  @Test def testCreateProgressBarNoValue() {
    val handler = manager.createProgressBar(new ProgressBarTag, false)
    val bar = handler.getComponent.asInstanceOf[ProgressBar]
    assertEquals("Got a progress value", 0, bar.getProgress, .001)
  }

  /**
   * Tests the creation of a slider component if the create flag is set.
   */
  @Test def testCreateSliderCreate() {
    assertNull("Got a handler", manager.createSlider(new SliderTag, true))
  }

  /**
   * Creates a ''SliderTag'' with the given orientation.
   * @param or the orientation
   * @return the slider tag
   */
  private def sliderTag(or: Orientation): SliderTag = {
    new SliderTag {
      override def getSliderOrientation = or
    }
  }

  /**
   * Helper method for checking the orientation of a slider component.
   * @param or the orientation of the tag
   * @return the expected slider orientation
   */
  private def checkCreateSliderOrientation(or: Orientation,
    expSliderOr: javafx.geometry.Orientation) {
    val tag = sliderTag(or)
    tag setMax 100
    val handler = manager.createSlider(tag, false)
    val slider = handler.getComponent.asInstanceOf[Slider]
    assertEquals("Wrong orientation", expSliderOr, slider.getOrientation)
  }

  /**
   * Tests whether a slider with vertical orientation can be created.
   */
  @Test def testCreateSliderOrientationVertical() {
    checkCreateSliderOrientation(Orientation.VERTICAL,
      javafx.geometry.Orientation.VERTICAL)
  }

  /**
   * Tests whether a slider with horizontal orientation can be created.
   */
  @Test def testCreateSliderOrientationHorizontal() {
    checkCreateSliderOrientation(Orientation.HORIZONTAL,
      javafx.geometry.Orientation.HORIZONTAL)
  }

  /**
   * Tests whether settings about a slider's ticks are correctly evaluated.
   */
  @Test def testCreateSliderWithTickSettings() {
    val tag = sliderTag(Orientation.HORIZONTAL)
    tag setMin 10
    tag setMax 50
    tag setMinorTicks 5
    tag setMajorTicks 10
    tag setShowLabels true
    tag setShowTicks true
    val handler = manager.createSlider(tag, false)
    val slider = handler.getComponent.asInstanceOf[Slider]
    assertEquals("Wrong minimum", tag.getMin, slider.getMin.toInt)
    assertEquals("Wrong maximum", tag.getMax, slider.getMax.toInt)
    assertEquals("Wrong major ticks", tag.getMajorTicks, slider.getMajorTickUnit.toInt)
    assertEquals("Wrong minor ticks", 2, slider.getMinorTickCount)
    assertTrue("Labels not shown", slider.isShowTickLabels)
    assertTrue("Marks not shown", slider.isShowTickMarks)
  }

  /**
   * Tests whether a slider can be created if there are no settings for ticks.
   */
  @Test def testCreateSliderNoTickSettings() {
    val tag = sliderTag(Orientation.HORIZONTAL)
    tag setMin 10
    tag setMax 50
    val handler = manager.createSlider(tag, false)
    val slider = handler.getComponent.asInstanceOf[Slider]
    assertFalse("Labels shown", slider.isShowTickLabels)
    assertFalse("Marks shown", slider.isShowTickMarks)
    assertEquals("Wrong major ticks", tag.getMax, slider.getMajorTickUnit.toInt)
    assertEquals("Wrong minor ticks", 0, slider.getMinorTickCount)
  }

  /**
   * Tests whether the correct default split pane factory is created.
   */
  @Test def testDefaultSplitPaneFactory() {
    assertEquals("Wrong split pane factory", classOf[SplitPaneFactoryImpl],
        manager.splitPaneFactory.getClass)
  }

  /**
   * Tests the creation of a splitter component if the create flag is true.
   */
  @Test def testCreateSplitterNull() {
    assertNull("Got a component", manager.createSplitter(new SplitterTag, true))
  }

  /**
   * Tests whether a splitter component can be created.
   */
  @Test def testCreateSplitter() {
    val factory = mock[SplitPaneFactory]
    val manager = new JavaFxComponentManager(toolTipFactory = null, treeHandlerFactory = null,
      tableHandlerFactory = null, splitPaneFactory = factory)
    val tag = new SplitterTag
    tag setName "MySplitter"
    val split = new SplitPane
    EasyMock.expect(factory.createSplitPane(tag)).andReturn(split)

    whenExecuting(factory) {
      assertSame("Wrong split pane", split, manager.createSplitter(tag, false))
      assertEquals("Not initialized", tag.getName, split.getId)
    }
  }

  /**
   * Tests the notification of a form context creation if the source is of no interest.
   */
  @Test def testFormContextCreatedIrrelevant() {
    val form = mock[Form]
    whenExecuting(form) {
      manager.formContextCreated(form, this)
    }
  }

  /**
   * Tests the notification of a form context creation if the source is a column component
   * tag.
   */
  @Test def testFormContextCreatedForColumnComponent() {
    val form = mock[Form]
    val tag = new ColumnRendererTag
    val context = new JellyContext
    val builderData = new ComponentBuilderData
    tag setContext context
    builderData put context

    manager.formContextCreated(form, tag)
    val proxyManager = builderData.getComponentManager
    val cellManager = proxyManager.createBorderLayout(null).asInstanceOf[CellComponentManager]
    assertSame("Wrong form", form, cellManager.form)
    assertSame("Wrong tag", tag, cellManager.tag)
  }

  /**
   * Tests a form context closed notification that is of no interest.
   */
  @Test def testFormContextClosedIrrelevant() {
    val form = mock[Form]
    whenExecuting(form) {
      manager.formContextClosed(form, this)
    }
  }

  /**
   * Tests the reaction on a form context closed notification if the source is a column
   * component.
   */
  @Test def testFormContextClosedForColumnComponent() {
    val form = mock[Form]
    val tag = new ColumnRendererTag
    val context = new JellyContext
    val builderData = new ComponentBuilderData
    tag setContext context
    builderData put context

    manager.formContextClosed(form, tag)
    assertSame("Component manager not reset", manager, builderData.getComponentManager)
  }

  /**
   * A test implementation of a table tag which allows injecting a mock form controller.
   * @param getTableFormController the mock form controller
   */
  private class TableTagTestImpl(override val getTableFormController: TableFormController)
    extends TableTag

}
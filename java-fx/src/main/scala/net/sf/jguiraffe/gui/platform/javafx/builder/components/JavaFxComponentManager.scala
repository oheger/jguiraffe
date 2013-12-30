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

import scala.reflect.Manifest
import org.apache.commons.lang.StringUtils
import javafx.scene.Node
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import net.sf.jguiraffe.gui.builder.components.ComponentManager
import net.sf.jguiraffe.gui.builder.components.FormBuilderException
import net.sf.jguiraffe.gui.builder.components.WidgetHandler
import net.sf.jguiraffe.gui.builder.components.model.StaticTextData
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment
import net.sf.jguiraffe.gui.builder.components.tags.BorderLayoutTag
import net.sf.jguiraffe.gui.builder.components.tags.ButtonLayoutTag
import net.sf.jguiraffe.gui.builder.components.tags.ButtonTag
import net.sf.jguiraffe.gui.builder.components.tags.CheckboxTag
import net.sf.jguiraffe.gui.builder.components.tags.ComboBoxTag
import net.sf.jguiraffe.gui.builder.components.tags.ComponentBaseTag
import net.sf.jguiraffe.gui.builder.components.tags.DesktopPanelTag
import net.sf.jguiraffe.gui.builder.components.tags.FontTag
import net.sf.jguiraffe.gui.builder.components.tags.LabelTag
import net.sf.jguiraffe.gui.builder.components.tags.ListBoxTag
import net.sf.jguiraffe.gui.builder.components.tags.PanelTag
import net.sf.jguiraffe.gui.builder.components.tags.PasswordFieldTag
import net.sf.jguiraffe.gui.builder.components.tags.PercentLayoutTag
import net.sf.jguiraffe.gui.builder.components.tags.ProgressBarTag
import net.sf.jguiraffe.gui.builder.components.tags.RadioButtonTag
import net.sf.jguiraffe.gui.builder.components.tags.SliderTag
import net.sf.jguiraffe.gui.builder.components.tags.SplitterTag
import net.sf.jguiraffe.gui.builder.components.tags.StaticTextTag
import net.sf.jguiraffe.gui.builder.components.tags.TabbedPaneTag
import net.sf.jguiraffe.gui.builder.components.tags.TextAreaTag
import net.sf.jguiraffe.gui.builder.components.tags.TextFieldTag
import net.sf.jguiraffe.gui.builder.components.tags.ToggleButtonTag
import net.sf.jguiraffe.gui.builder.components.tags.TreeTag
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag
import net.sf.jguiraffe.gui.builder.event.PlatformEventManager
import net.sf.jguiraffe.gui.forms.ComponentHandler
import net.sf.jguiraffe.gui.platform.javafx.layout.ContainerWrapper
import net.sf.jguiraffe.locators.Locator
import net.sf.jguiraffe.locators.LocatorException
import JavaFxComponentManager.as
import net.sf.jguiraffe.gui.layout.PercentLayoutBase
import javafx.scene.control.Control
import net.sf.jguiraffe.gui.platform.javafx.builder.event.JavaFxEventManager
import javafx.scene.control.TextField
import javafx.scene.control.TextArea
import javafx.scene.control.PasswordField
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData
import javafx.scene.control.Labeled
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ToggleButton
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.beans.property.ObjectProperty
import javafx.scene.control.Tooltip
import javafx.scene.control.TabPane
import javafx.scene.control.Tab
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import net.sf.jguiraffe.gui.layout.UnitSizeHandler
import org.apache.commons.jelly.TagSupport
import net.sf.jguiraffe.gui.platform.javafx.layout.JavaFxUnitSizeHandler
import net.sf.jguiraffe.gui.builder.components.tags.ScrollSizeSupport
import net.sf.jguiraffe.gui.builder.components.tags.FormBaseTag
import net.sf.jguiraffe.gui.platform.javafx.builder.components.tree.TreeHandlerFactory

/**
 * The Java FX-based implementation of the ''ComponentManager'' interface.
 *
 * @param toolTipFactory the factory object for creating tool tips
 * @param treeHandlerFactory the factory for creating ''TreeHandler''s
 */
class JavaFxComponentManager(val toolTipFactory: ToolTipFactory,
  val treeHandlerFactory: TreeHandlerFactory)
  extends ComponentManager {
  /**
   * Creates a new instance of ''JavaFxComponentManager'' and initializes it
   * with a default tool tip factory.
   */
  def this() = this(new DefaultToolTipFactory, new TreeHandlerFactory)

  /**
   * @inheritdoc This implementation expects that the container is a
   * ''ContainerWrapper'' object. In this case, the component is added to the
   * wrapper. Otherwise, a ''FormBuilderException'' exception is thrown.
   */
  @throws(classOf[FormBuilderException])
  def addContainerComponent(container: Object, component: Object,
    constraints: Object) {
    as[ContainerWrapper](container).addComponent(component, constraints)
  }

  /**
   * @inheritdoc This implementation is able to set a ''PercentLayoutBase''
   * object into a ''ContainerWrapper''.
   */
  def setContainerLayout(container: Object, layout: Object) {
    as[ContainerWrapper](container).initLayout(as[PercentLayoutBase](layout))
  }

  /**
   * @inheritdoc This implementation creates a Java FX-specific event manager
   * object.
   */
  def createEventManager(): PlatformEventManager = new JavaFxEventManager

  /**
   * @inheritdoc This implementation expects the passed in object to be of type
   * ''Node''. It returns a corresponding widget handler implementation.
   */
  def getWidgetHandlerFor(component: Object): WidgetHandler = {
    assert(component != null, "No component provided!")
    new JavaFxWidgetHandler(component.asInstanceOf[Node], toolTipFactory)
  }

  /**
   * @inheritdoc This implementation creates a Java FX Label component.
   */
  def createLabel(tag: LabelTag, create: Boolean): Object = {
    if (create) null
    else createLabelControl(tag, tag.getTextIconData)
  }

  /**
   * @inheritdoc This implementation expects that the label argument is of type
   * ''Label'' and the component is an arbitrary ''Node''.
   */
  def linkLabel(label: Object, component: Object, text: String) {
    val fxlab = label.asInstanceOf[Label]
    fxlab.setLabelFor(component.asInstanceOf[Node])
    if (StringUtils.isNotEmpty(text)) {
      fxlab.setText(text)
    }
  }

  /**
   * @inheritdoc This implementation returns an ''ImageView'' object initialized
   * with the image defined by the ''Locator''.
   */
  @throws(classOf[FormBuilderException])
  def createIcon(locator: Locator): Object = {
    try {
      val image = new Image(locator.getURL().toExternalForm)
      new ImageView(image)
    } catch {
      case lex: LocatorException =>
        throw new FormBuilderException(lex)
    }
  }

  /**
   * @inheritdoc This implementation returns a ''JavaFxFont'' object initialized
   * with the properties set for the given tag.
   */
  def createFont(tag: FontTag): Object =
    JavaFxFont(family = JavaFxComponentManager.convertFontFamily(tag),
      size = JavaFxComponentManager.convertFontSize(tag),
      weight = JavaFxComponentManager.convertFontWeight(tag),
      style = JavaFxComponentManager.convertFontStyle(tag))

  /**
   * @inheritdoc This implementation directly returns the layout stored in the
   * tag. The corresponding Java FX layout implementation is created when the
   * owning panel is instantiated.
   */
  def createPercentLayout(tag: PercentLayoutTag): Object = tag.getPercentLayout

  /**
   * @inheritdoc This implementation directly returns the layout stored in the
   * tag. The corresponding Java FX layout implementation is created when the
   * owning panel is instantiated.
   */
  def createButtonLayout(tag: ButtonLayoutTag): Object = tag.getButtonLayout

  /**
   * @inheritdoc This implementation directly returns the layout stored in the
   * tag. The corresponding Java FX layout implementation is created when the
   * owning panel is instantiated.
   */
  def createBorderLayout(tag: BorderLayoutTag): Object = tag.getBorderLayout

  /**
   * @inheritdoc This implementation creates a ''ContainerWrapper'' object.
   * The actual panel is created by the wrapper when it is added to its
   * parent.
   */
  def createPanel(tag: PanelTag, create: Boolean): Object = {
    if (create) null
    else {
      //TODO deal with the tag's attributes
      new ContainerWrapper(Some(JavaFxComponentManager.fetchSizeHandler(tag)))
    }
  }

  def createDesktopPanel(tag: DesktopPanelTag, create: Boolean): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createSplitter(tag: SplitterTag, create: Boolean): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  /**
   * @inheritdoc This implementation creates a JavaFX ''ToggleGroup'' object.
   * Then it iterates over all values in the given map, casts them to
   * ''RadioButton'' objects, and sets the newly created group as the buttons'
   * toggle group.
   */
  def createRadioGroup(radioButtons: java.util.Map[String, Object]): Object = {
    val group = new ToggleGroup
    val it = radioButtons.values().iterator()
    while (it.hasNext) {
      val radio = as[RadioButton](it.next())
      radio setToggleGroup group
    }
    group
  }

  /**
   * @inheritdoc This implementation creates a JavaFX ''Button'' control wrapped
   * by a ''JavaFxButtonHandler''.
   */
  def createButton(tag: ButtonTag, create: Boolean): ComponentHandler[java.lang.Boolean] = {
    if (create) null
    else {
      val button = new Button
      initLabeled(button, tag, tag.getTextIconData)
      button setDefaultButton (tag.isDefault)
      new JavaFxButtonHandler(button, tag.getCommand)
    }
  }

  /**
   * @inheritdoc This implementation creates a JavaFX ''ToggleButton''
   * control wrapped by a ''JavaFxToggleButtonHandler''.
   */
  def createToggleButton(tag: ToggleButtonTag, create: Boolean): ComponentHandler[java.lang.Boolean] = {
    if (create) null
    else {
      val button = new ToggleButton
      initLabeled(button, tag, tag.getTextIconData)
      new JavaFxToggleButtonHandler(button, tag.getCommand)
    }
  }

  /**
   * @inheritdoc This implementation creates a Java FX ''TextField'' control
   * wrapped by a ''JavaFxTextComponentHandler''. The text field also mixes in
   * the [[net.sf.jguiraffe.gui.platform.javafx.builder.components.TextLengthRestriction]]
   * trait to limit the maximum text length.
   */
  def createTextField(tag: TextFieldTag, create: Boolean): ComponentHandler[String] =
    createAndInitializeTextField(tag, create,
      new TextField with TextLengthRestriction)

  /**
   * @inheritdoc This implementation creates a Java FX ''TextArea'' control
   * wrapped by a ''JavaFxTextComponentHandler''. The text area also mixes in
   * the [[net.sf.jguiraffe.gui.platform.javafx.builder.components.TextLengthRestriction]]
   * trait to limit the maximum text length.
   */
  def createTextArea(tag: TextAreaTag, create: Boolean): ComponentHandler[String] = {
    if (create) null
    else {
      val ctrl = new TextArea with TextLengthRestriction
      initControl(tag, ctrl)

      if (tag.getColumns > 0) {
        ctrl setPrefColumnCount tag.getColumns
      }
      if (tag.getRows > 0) {
        ctrl setPrefRowCount tag.getRows
      }
      ctrl setWrapText tag.isWrap
      ctrl setMaximumLength tag.getMaxlength
      JavaFxComponentManager.initScrollSize(tag, ctrl)

      new JavaFxTextHandler(ctrl)
    }
  }

  /**
   * @inheritdoc This implementation works similar to ''createTextField()'',
   * except that a Java FX ''PasswordField'' control is created.
   */
  def createPasswordField(tag: PasswordFieldTag,
    create: Boolean): ComponentHandler[String] =
    createAndInitializeTextField(tag, create,
      new PasswordField with TextLengthRestriction)

  /**
   * @inheritdoc This implementation creates a JavaFX ''CheckBox'' control
   * wrapped within a ''JavaFxCheckBoxHandler''.
   */
  def createCheckbox(tag: CheckboxTag, create: Boolean): ComponentHandler[java.lang.Boolean] = {
    if (create) null
    else {
      val checkBox = new CheckBox
      initLabeled(checkBox, tag, tag.getTextIconData)
      new JavaFxCheckBoxHandler(checkBox)
    }
  }

  /**
   * @inheritdoc This implementation creates a JavaFX ''RadioButton'' control
   * wrapped by a ''JavaFxToggleButtonHandler''.
   */
  def createRadioButton(tag: RadioButtonTag, create: Boolean): ComponentHandler[java.lang.Boolean] = {
    if (create) null
    else {
      val radio = new RadioButton
      initLabeled(radio, tag, tag.getTextIconData)
      new JavaFxToggleButtonHandler(radio)
    }
  }

  /**
   * @inheritdoc This implementation creates a JavaFX ''ComboBox'' control
   * wrapped by a ''JavaFxComboBoxHandler''.
   */
  def createComboBox(tag: ComboBoxTag, create: Boolean): ComponentHandler[Object] = {
    if (create) null
    else {
      val combo = new ComboBox[Object]
      initControl(tag, combo)
      combo setEditable tag.isEditable
      val handler = new JavaFxComboBoxHandler(combo)
      handler.initListModel(tag.getListModel)
      handler
    }
  }

  /**
   * @inheritdoc This implementation creates a JavaFX ''ListView'' control
   * wrapped by either a ''JavaFxListViewHandler'' or a
   * ''JavaFxMultiSelectionListHandler''.
   */
  def createListBox(tag: ListBoxTag, create: Boolean): ComponentHandler[Object] = {
    if (create) null
    else {
      val list = new ListView[Object]
      initControl(tag, list)
      JavaFxComponentManager.initScrollSize(tag, list)
      val handler = if (tag.isMulti) new JavaFxMultiSelectionListHandler(list)
      else new JavaFxListViewHandler(list)
      handler.initListModel(tag.getListModel)
      handler
    }
  }

  /**
   * @inheritdoc This implementation creates a JavaFX ''TabPane'' control
   * wrapped by a ''JavaFxTabPaneHandler''.
   */
  def createTabbedPane(tag: TabbedPaneTag, create: Boolean): ComponentHandler[Integer] = {
    def createTab(tabData: TabbedPaneTag.TabData): Tab = {
      val tab = new Tab
      tab setClosable false
      tab setText tabData.getTitle
      if (tabData.getIcon != null) {
        tab setGraphic as[Node](tabData.getIcon)
      }
      if (StringUtils.isNotEmpty(tabData.getToolTip)) {
        initToolTip(tag, tab.tooltipProperty, tabData.getToolTip)
      }

      tab setContent (tabData.getComponent match {
        case cw: ContainerWrapper =>
          cw.createContainer()
        case nd: Node =>
          nd
        case other =>
          throw new FormBuilderException("Invalid content of a tab pane: " + other)
      })
      tab
    }

    if (create) null
    else {
      val tabPane = new TabPane
      initControl(tag, tabPane)
      tabPane setSide convertPlacementToSide(tag.getPlacementValue)
      val itTabs = tag.getTabs.iterator
      while (itTabs.hasNext) {
        tabPane.getTabs.add(createTab(itTabs.next()))
      }
      new JavaFxTabPaneHandler(tabPane)
    }
  }

  /**
   * @inheritdoc This implementation creates a JavaFX ''Label'' control
   * wrapped by a ''JavaFxStaticTextHandler''.
   */
  def createStaticText(tag: StaticTextTag, create: Boolean): ComponentHandler[StaticTextData] = {
    if (create) null
    else new JavaFxStaticTextHandler(createLabelControl(tag, tag.getTextIconData))
  }

  def createProgressBar(tag: ProgressBarTag,
    create: Boolean): ComponentHandler[Integer] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createSlider(tag: SliderTag, create: Boolean): ComponentHandler[Integer] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createTable(tag: TableTag, create: Boolean): ComponentHandler[Object] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  /**
   * @inheritdoc This implementation delegates to the associated
   * ''TreeHandlerFactory'' to create a component handler for a JavaFX
   * ''TreeView'' component.
   */
  def createTree(tag: TreeTag, create: Boolean): ComponentHandler[Object] = {
    if (create) null
    else {
      val handler = treeHandlerFactory.createTreeHandler(tag)
      val ctrl = JavaFxComponentManager.as[Control](handler.getComponent)
      initControl(tag, ctrl)
      JavaFxComponentManager.initScrollSize(tag, ctrl)
      handler
    }
  }

  /**
   * Helper method for actually creating text field controls. This method is
   * used by the methods for creating text fields and password fields (which
   * are actually pretty similar). The control to be returned is passed as a
   * by name-parameter. Thus the creation can be controlled by the caller.
   * @param tag the text field tag
   * @param create the create flag
   * @param comp the text field component
   * @return the component handler with the initialized text field control
   */
  private def createAndInitializeTextField(tag: TextFieldTag, create: Boolean,
    comp: => TextField with TextLengthRestriction): ComponentHandler[String] = {
    if (create) null
    else {
      val ctrl = comp
      initControl(tag, ctrl)

      if (tag.getColumns > 0) {
        ctrl setPrefColumnCount tag.getColumns
      }
      ctrl setMaximumLength tag.getMaxlength
      new JavaFxTextHandler(ctrl)
    }
  }

  /**
   * Creates a JavaFX ''Label'' and initializes it based on the given data
   * object.
   * @param tag the component tag
   * @param data the data object for the label
   * @return the newly created label
   */
  private def createLabelControl(tag: ComponentBaseTag, data: TextIconData): Label = {
    val label = new Label
    initLabeled(label, tag, data)
    label
  }

  /**
   * Helper method for initializing the properties of the specified ''Labeled''
   * object from the given ''TextIconData''.
   * @param label the label to be initialized
   * @param tag the component tag
   * @param data the ''TextIconData'' object with special properties for the label
   */
  private def initLabeled(label: Labeled, tag: ComponentBaseTag,
    data: TextIconData) {
    label.setText(JavaFxComponentManager.mnemonicText(data.getCaption,
      data.getMnemonic))
    if (data.getIcon != null) {
      label.setGraphic(data.getIcon.asInstanceOf[ImageView])
    }
    label.setContentDisplay(convertAlignment(data.getAlignment))

    initControl(tag, label)
  }

  /**
   * Initializes standard properties of the given ''Control'' from the
   * specified tag. This method delegates to ''initNode()'' for handling
   * base properties and then deals with additional properties for controls.
   * @param tag the component tag defining fundamental properties
   * @param control the control to be initialized
   */
  private def initControl(tag: ComponentBaseTag, control: Control) {
    JavaFxComponentManager.initNode(tag, control)

    if (tag.getToolTipData.isDefined) {
      initToolTip(tag, control.tooltipProperty, tag.getToolTipData.getCaption())
    }
  }

  /**
   * Creates a tool tip with the specified text and initializes the given
   * property with it. The tool tip creation itself is done by the specialized
   * callback object.
   * @param tag the current component tag to be processed
   * @param property the property for the tool tip
   * @param tip the text of the tool tip
   */
  private def initToolTip(tag: ComponentBaseTag,
    property: ObjectProperty[Tooltip], tip: String) {
    val callBack = ToolTipCreationCallBack.getInstance(tag, toolTipFactory)
    callBack.addCreateToolTipRequest(property, tip)
  }
}

/**
 * The companion object for ''JavaFxComponentManager''.
 */
object JavaFxComponentManager {
  /** Constant for the normal font weight and style. */
  private val FontStyleNormal = "normal"

  /** Constant for the font style italic. */
  private val FontStyleItalic = "italic"

  /** Constant for the font weight bold. */
  private val FontWeightBold = "bold"

  /** Constant for the mnemonic marker. */
  private val MnemonicMarker = '_'

  /**
   * Initializes standard properties of the given node from the specified tag.
   * @param tag the component tag
   * @param node the node to be initialized
   */
  private def initNode(tag: ComponentBaseTag, node: Node) {
    if (StringUtils.isNotEmpty(tag.getName)) {
      node.setId(tag.getName)
    }

    val styleDef = createStylesForTag(tag).toExternalForm()
    if (!styleDef.isEmpty) {
      node.setStyle(styleDef)
    }
  }

  /**
   * Creates a ''Styles'' object which is initialized from the attributes of
   * the given component tag.
   * @param tag the component tag
   * @return the initialized ''Styles'' object
   */
  private def createStylesForTag(tag: ComponentBaseTag): Styles = {
    val stylesHandler = new JavaFxStylesHandler
    if (tag.getBackgroundColor() != null) {
      stylesHandler setBackgroundColor (tag.getBackgroundColor())
    }
    if (tag.getForegroundColor() != null) {
      stylesHandler setForegroundColor (tag.getForegroundColor())
    }

    tag.getFont match {
      case f: JavaFxFont =>
        stylesHandler setFont f
      case _ => // ignore
    }

    stylesHandler.styles
  }

  /**
   * Adds a mnemonic marker to the specified text if possible. In Java FX, a
   * mnemonic character is specified by putting an underscore in front of it.
   * This method tries to find the given mnemonic character in the text. If
   * it is found (ignoring case), the text is modified to contain the
   * underscore.
   * @param txt the text to be modified
   * @param mnemonic the mnemonic character
   * @return the manipulated string
   */
  private[components] def mnemonicText(txt: String, mnemonic: Char): String = {
    if (txt == null) null
    else {
      var pos = txt.indexOf(mnemonic)
      if (pos < 0) {
        var mnemonicCase = if (mnemonic.isUpper) mnemonic.toLower
        else mnemonic.toUpper
        pos = txt.indexOf(mnemonicCase)
      }
      if (pos < 0) txt
      else {
        val buf = new java.lang.StringBuilder(txt.length + 1)
        if (pos > 0) {
          buf.append(txt.substring(0, pos))
        }
        buf append MnemonicMarker
        buf.append(txt.substring(pos))
        buf.toString()
      }
    }
  }

  /**
   * Returns the ''UnitSizeHandler'' for the current builder operation. A
   * size handler instance is created on demand and stored in the current
   * Jelly context. This implementation checks whether there is already an
   * instance in the current context. If not, it is created now.
   * @param tag the current tag
   * @return the ''UnitSizeHandler'' for the current builder operation
   */
  private[components] def fetchSizeHandler(tag: TagSupport): UnitSizeHandler =
    JavaFxUnitSizeHandler.fromContext(tag.getContext)

  /**
   * Installs the specified ''UnitSizeHandler'' for the current builder
   * operation. The handler is stored in the tag's Jelly context. From there
   * it can be obtained if needed by another operation.
   * @param tag the current tag
   * @param handler the ''UnitSizeHandler'' to be installed
   * @return the newly installed size handler
   */
  private[components] def installSizeHandler(tag: TagSupport,
    handler: UnitSizeHandler): UnitSizeHandler =
    JavaFxUnitSizeHandler.storeSizeHandler(tag.getContext, handler)

  /**
   * Converts the font name as specified in the given tag to a corresponding
   * style definition. If the name is undefined, result is ''None''.
   * @param tag the font tag
   * @return an ''Option'' for the font family style definition
   */
  private def convertFontFamily(tag: FontTag): Option[String] = {
    if (tag.getName != null) {
      Some(s"${'"'}${tag.getName}${'"'}")
    } else None
  }

  /**
   * Converts the font size as specified in the given tag to a corresponding
   * style definition. Font sizes less or equal 0 are considered as undefined.
   * @param tag the font tag
   * @return an ''Option'' for the font size style definition
   */
  private def convertFontSize(tag: FontTag): Option[String] = {
    val size = tag.getSize
    if (size > 0) Some(s"${size}px")
    else None
  }

  /**
   * Converts the font style as specified in the given tag to a corresponding
   * style definition. This method never returns ''None''; the style is
   * derived from the tag's ''italic'' property.
   * @param tag the font tag
   * @return an ''Option'' for the font style definition
   */
  private def convertFontStyle(tag: FontTag): Option[String] =
    Some(if (tag.isItalic) FontStyleItalic else FontStyleNormal)

  /**
   * Converts the font weight as specified in the given tag to a corresponding
   * style definition. This method never returns ''None''; the weight style is
   * derived from the tag's ''bold'' property.
   * @param tag the font tag
   * @return an ''Option'' for the font weight style definition
   */
  private def convertFontWeight(tag: FontTag): Option[String] =
    Some(if (tag.isBold) FontWeightBold else FontStyleNormal)

  /**
   * Initializes the given control's preferred size based on the scroll size
   * defined by the passed in ''ScrollSizeSupport'' tag. This method determines
   * the preferred scroll size using the current ''UnitSizeHandler''. If it is
   * defined, the control's preferred width or height are set.
   * @param tag the current tag
   * @param ctrl the control to be initialized
   */
  private def initScrollSize(tag: FormBaseTag with ScrollSizeSupport, ctrl: Control) {
    val sizeHandler = fetchSizeHandler(tag)
    val container = tag.findContainer.getContainer
    val scrollWidth = tag.getPreferredScrollWidth
    val xSize = scrollWidth.toPixel(sizeHandler, container, false)
    val ySize = tag.getPreferredScrollHeight.toPixel(sizeHandler, container, true)

    if (xSize > 0) {
      ctrl setPrefWidth xSize
    }
    if (ySize > 0) {
      ctrl setPrefHeight ySize
    }
  }

  /**
   * Helper method for converting an object to the specified type. Because the
   * JGUIraffe library operates on abstract and generic objects type casts have
   * to be performed frequently. This helper method tries to cast the specified
   * object to the desired result type. If this fails, an exception is thrown.
   * @param [T] the result type
   * @param obj the object to be converted
   * @return the converted object
   * @throws FormBuilderException if conversion fails
   */
  private def as[T](obj: Any)(implicit m: Manifest[T]): T = {
    obj match {
      case t: T => t
      case _ =>
        throw new FormBuilderException("Wrong object! Expected " + m.toString +
          ", was: " + obj)
    }
  }
}

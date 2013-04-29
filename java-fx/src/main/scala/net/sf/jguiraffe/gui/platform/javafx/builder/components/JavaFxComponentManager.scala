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

/**
 * The Java FX-based implementation of the ''ComponentManager'' interface.
 */
class JavaFxComponentManager extends ComponentManager {
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

  def createEventManager(): PlatformEventManager = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def getWidgetHandlerFor(component: Object): WidgetHandler = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  /**
   * @inheritdoc This implementation creates a Java FX Label component.
   */
  def createLabel(tag: LabelTag, create: Boolean): Object = {
    if (create) null
    else {
      val label = new Label
      val data = tag.getTextIconData
      label.setText(JavaFxComponentManager.mnemonicText(data.getCaption,
        data.getMnemonic))
      if (data.getIcon != null) {
        label.setGraphic(data.getIcon.asInstanceOf[ImageView])
      }
      label.setContentDisplay(JavaFxComponentManager.convertAlignment(data.getAlignment))

      JavaFxComponentManager.initNode(tag, label)
      label
    }
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

  def createFont(tag: FontTag): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

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

  def createPanel(tag: PanelTag, create: Boolean): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createDesktopPanel(tag: DesktopPanelTag, create: Boolean): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createSplitter(tag: SplitterTag, create: Boolean): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createRadioGroup(radioButtons: java.util.Map[String, Object]): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createButton(tag: ButtonTag, create: Boolean): ComponentHandler[java.lang.Boolean] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createToggleButton(tag: ToggleButtonTag, create: Boolean): ComponentHandler[java.lang.Boolean] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createTextField(tag: TextFieldTag, create: Boolean): ComponentHandler[String] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createTextArea(tag: TextAreaTag, create: Boolean): ComponentHandler[String] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createPasswordField(tag: PasswordFieldTag,
    create: Boolean): ComponentHandler[String] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createCheckbox(tag: CheckboxTag, create: Boolean): ComponentHandler[java.lang.Boolean] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createRadioButton(tag: RadioButtonTag, create: Boolean): ComponentHandler[java.lang.Boolean] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createComboBox(tag: ComboBoxTag, create: Boolean): ComponentHandler[Object] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createListBox(tag: ListBoxTag, create: Boolean): ComponentHandler[Object] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createTabbedPane(tag: TabbedPaneTag, create: Boolean): ComponentHandler[Integer] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createStaticText(tag: StaticTextTag, create: Boolean): ComponentHandler[StaticTextData] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
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

  def createTree(tag: TreeTag, create: Boolean): ComponentHandler[Object] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }
}

/**
 * The companion object for ''JavaFxComponentManager''.
 */
object JavaFxComponentManager {
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
    //TODO handle further attributes
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
   * Converts a ''TextIconAlignment'' enumeration value to the corresponding
   * Java FX ''ContentDisplay'' value.
   * @param al the alignment to be converted
   * @return the corresponding ''ContentDisplay'' value
   */
  private def convertAlignment(al: TextIconAlignment): ContentDisplay = {
    al match {
      case TextIconAlignment.CENTER => ContentDisplay.CENTER
      case TextIconAlignment.RIGHT => ContentDisplay.RIGHT
      case _ => ContentDisplay.LEFT
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

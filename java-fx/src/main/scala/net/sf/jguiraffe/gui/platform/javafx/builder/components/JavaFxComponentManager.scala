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

import net.sf.jguiraffe.gui.builder.components.ComponentManager
import net.sf.jguiraffe.gui.builder.components.WidgetHandler
import net.sf.jguiraffe.gui.builder.components.model.StaticTextData
import net.sf.jguiraffe.gui.builder.components.tags.BorderLayoutTag
import net.sf.jguiraffe.gui.builder.components.tags.ButtonLayoutTag
import net.sf.jguiraffe.gui.builder.components.tags.ButtonTag
import net.sf.jguiraffe.gui.builder.components.tags.CheckboxTag
import net.sf.jguiraffe.gui.builder.components.tags.ComboBoxTag
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
import net.sf.jguiraffe.locators.Locator

class JavaFxComponentManager extends ComponentManager {
  def addContainerComponent(container: Object, component: Object,
    constraints: Object) {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def setContainerLayout(container: Object, layout: Object) {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createEventManager(): PlatformEventManager = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def getWidgetHandlerFor(component: Object): WidgetHandler = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createLabel(tag: LabelTag, create: Boolean): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def linkLabel(label: Object, component: Object, text: String) {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createIcon(locator: Locator): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createFont(tag: FontTag): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createPercentLayout(tag: PercentLayoutTag): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createButtonLayout(tag: ButtonLayoutTag): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createBorderLayout(tag: BorderLayoutTag) = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

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

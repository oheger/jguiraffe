/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.widget

/**
 * A simple data class representing a font in Java FX.
 *
 * This is a simple case class holding the various attributes supported by a
 * Java FX font as ''Option'' values. All of these properties are optional, in
 * fact the whole object can be fully undefined. The data stored in an instance
 * is used to update the styles definition of a Java FX node. Therefore, all
 * properties are of type String - they directly correspond to style sheet
 * definitions.
 *
 * @param family the font family
 * @param size the font size
 * @param style the font style
 * @param weight the font weight
 * @param fontDef a kind of meta attribute for a font definition in a single
 * style (this corresponds to the Java FX `fx-font` attribute)
 */
case class JavaFxFont(family: Option[String] = None,
  size: Option[String] = None,
  style: Option[String] = None,
  weight: Option[String] = None,
  fontDef: Option[String] = None)

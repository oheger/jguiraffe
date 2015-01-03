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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.widget

import org.apache.commons.logging.LogFactory

import scala.util.parsing.combinator.JavaTokenParsers

/**
 * An internally used helper class for managing style sheet definitions for
 * a Java FX component.
 *
 * A scene node can be assigned an arbitrary number of styles in form of key
 * value pairs. This class is able to parse such a styles definition and
 * provide access to the single keys and their values. It is possible to
 * remove keys or add new ones. The modified styles definition can then be
 * transformed back to a string and passed to a ''Node'' object.
 *
 * Implementation note: This class is not thread-safe.
 *
 * @param map the map with the original styles definitions
 */
private[components] class Styles private (map: Map[String, String]) {
  /**
   * Holds the current map with style definitions.
   */
  private var stylesMap = map

  /**
   * Adds the given styles definition to this object. An already existing
   * definition for this key is overridden.
   * @param styleDef a key value pair with the style key and its value
   */
  def +=(styleDef: (String, String)) {
    stylesMap = stylesMap + styleDef
  }

  /**
   * Removes the styles definition for the given key from this object.
   * @param key the key of the styles definition to be removed
   */
  def -=(key: String) {
    stylesMap = stylesMap - key
  }

  /**
   * Adds or removes a style definition. This method evaluates the ''Option''
   * object with the style's value. If it is defined, the key is added with
   * this value (replacing an already existing value). Otherwise, the key is
   * removed from this object. This method is convenient for clients that
   * already store properties in ''Option'' objects; they do not have to do
   * any checks or conversions.
   * @param key the key of the style to be updated
   * @param value the value of this style as an ''Option''
   */
  def updateStyle(key: String, value: Option[String]) {
    value match {
      case Some(x) => +=(key, x)
      case None => -=(key)
    }
  }

  /**
   * Returns a set with all keys of style definitions contained in this object.
   * @return a set with the keys of all known style definitions
   */
  def styleKeys: Set[String] = stylesMap.keySet

  /**
   * Returns an ''Option'' object with the value of the style with the given
   * key.
   * @param key the key of the style in question
   * @return an ''Option'' with the value of this style
   */
  def apply(key: String): Option[String] = stylesMap get key

  /**
   * Transforms the contained style definitions to a string which can be passed
   * to a Java FX ''Node'' object. The order of the style definitions in the
   * returned string is random.
   * @return a string representation for all contained style definitions
   */
  def toExternalForm(): String = {
    val buf = new StringBuilder(Styles.BufSize)
    stylesMap foreach { styleDef =>
      buf ++= styleDef._1
      buf ++= Styles.KeySeparator
      buf ++= styleDef._2
      buf ++= Styles.DefinitionEnd
    }
    buf.toString()
  }

  /**
   * Creates a string representation for this object. This string contains
   * the currently defined style sheets in textual form.
   */
  override def toString() = s"Styles {\n${toExternalForm}}"
}

/**
 * The companion object for ''Styles''.
 *
 * This object provides factory methods for creating instances of ''Styles''.
 * An instance can be created based on an existing styles definition. It is
 * also possible to create an empty instance.
 */
private[components] object Styles {
  /** The logger. */
  private val Log = LogFactory.getLog(classOf[Styles])

  /** Constant for the key value separator in style definitions. */
  private val KeySeparator = ": "

  /** Constant for the end token of a style definition. */
  private val DefinitionEnd = ";\n"

  /** Constant for the default string buffer size. */
  private val BufSize = 256

  /**
   * Creates a new, empty instance of ''Styles''.
   * @return the newly created instance
   */
  def apply(): Styles = new Styles(Map.empty)

  /**
   * Creates a new instance of ''Styles'' based on the given textual styles
   * definition. The passed in string is parsed to extract all style
   * definitions. If parsing fails, a warning is logged, and an empty object
   * is created.
   * @param styleDef a textual styles definition
   * @return a newly created instance initialized with these styles definitions
   */
  def apply(styleDef: String): Styles = {
    if (Log.isDebugEnabled()) {
      Log.debug("Parsing styles definition: " + styleDef)
    }

    val parser = new StylesParser
    val result = parser.parseAll(parser.styleSheet, styleDef)
    if (result.successful) {
      new Styles(result.get)
    } else {
      Log.warn("Error parsing styles definition '" + styleDef + "': " + result)
      apply()
    }
  }
}

/**
 * A parser class for extracting single styles (with their keys and values)
 * from a style sheet definition.
 *
 * This class is used to process the styles set for a Java FX ''Node''. It
 * returns a map with the names of the defined styles and their values.
 */
private class StylesParser extends JavaTokenParsers {
  /** Parses a complete style sheet. */
  def styleSheet: Parser[Map[String, String]] = rep(styleDefinition) ^^
    (Map() ++ _)

  /** Parses a single style definition. */
  def styleDefinition: Parser[(String, String)] = styleKey ~ ":" ~ styleValue <~ ";" ^^ {
    case key ~ ":" ~ value => (key, value)
  }

  /** Parses all tokens of the value of a single style definition. */
  def styleValue: Parser[String] = rep(styleToken) ^^
    (_.addString(StringBuilder.newBuilder, " ").toString)

  /** A token in the value of a style definition. */
  def styleToken: Parser[String] = styleValueToken | stringLiteral

  /** A key for a style definition. */
  def styleKey: Parser[String] = """([\w-"])+""".r

  /**
   * A text fragment in a style definition. We are very open here and allow
   * most characters except for some specific delimiters.
   */
  def styleValueToken: Parser[String] = """([^\s;"])+""".r
}

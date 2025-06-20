/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import scala.annotation.tailrec

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
    * An internally used class to keep track of the state while parsing a
    * stylesheet.
    *
    * @param styleDef      the style definition to be parsed
    * @param stylesMap     the map with already extracted styles
    * @param key           the key if it has been parsed already
    * @param text          stores the currently processed text
    * @param stringLiteral contains the quote character when parsing a string
    *                      literal
    * @param escape        flag whether the next character is escaped
    */
  private case class ParserState(styleDef: String,
                                 stylesMap: Map[String, String],
                                 key: Option[String],
                                 text: StringBuilder,
                                 stringLiteral: Char,
                                 escape: Boolean) {
    /**
      * Returns a flag whether the parse process is complete. This is the case
      * if the index exceeds the length of the definition to parse.
      *
      * @param index the current parser index
      * @return '''true''' if the whole content has been parsed; '''false'''
      *         otherwise
      */
    def isDone(index: Int): Boolean = index >= styleDef.length

    /**
      * Returns the character of the style definition to parse at the given
      * index.
      *
      * @param index the index
      * @return the character of the style definition at the given index
      */
    def apply(index: Int): Char = styleDef(index)

    /**
      * Returns a flag whether for the current style the key has already been
      * found.
      *
      * @return '''true''' if the current key is known, '''false''' otherwise
      */
    def keyFound: Boolean = key.isDefined

    /**
      * Returns a flag whether currently a string literal with double quotes is
      * parsed.
      *
      * @return a flag whether a double quite string literal is parsed
      */
    def inDoubleQuoteStringLiteral: Boolean = stringLiteral == '"'

    /**
      * Returns a flag whether currently a string literal with single quotes is
      * parsed.
      *
      * @return a flag whether a single quite string literal is parsed
      */
    def inSingleQuoteStringLiteral: Boolean = stringLiteral == '\''

    /**
      * Returns a flag whether currently a string literal is parsed.
      *
      * @return a flag whether a string literal is parsed
      */
    def inStringLiteral: Boolean = inDoubleQuoteStringLiteral || inSingleQuoteStringLiteral

    /**
      * Returns a flag whether the last added character is a whitespace. This
      * is used to strip multiple whitespace in a sequence in values.
      *
      * @return a flag whether the last added character is a whitespace
      */
    def inWhitespace: Boolean =
      text.lastOption.contains(' ')

    /**
      * Returns an updated state object with a current style key that is
      * defined by the aggregated text.
      *
      * @return the updated state
      */
    def withKey(): ParserState =
      copy(
        key = Some(text.toString()),
        text = new StringBuilder()
      )

    /**
      * Returns an updated state object with an added style derived from the
      * key and the current aggregated text.
      *
      * @return the updated state
      */
    def withValue(): ParserState =
      copy(
        stylesMap = stylesMap + (key.getOrElse("") -> text.toString().trim),
        text = new StringBuilder(),
        key = None
      )

    /**
      * Returns an updated state object with the given character added to the
      * current text. Some characters cause updates of some state properties.
      *
      * @param ch the character to be added
      * @return the updated state
      */
    def withCharacter(ch: Char): ParserState = {
      text.append(ch)
      ch match {
        case '\\' if !escape => copy(escape = true)
        case _ if escape => copy(escape = false)
        case _ => this
      }
    }

    /**
      * Returns an updated state object reporting that currently a string with
      * double quotes is parsed.
      *
      * @return the updated state
      */
    def withDoubleQuoteStringLiteral(): ParserState =
      startStringLiteral('"')

    /**
      * Returns an updated state object reporting that currently a string with
      * single quotes is parsed.
      *
      * @return the updated state
      */
    def withSingleQuoteStringLiteral(): ParserState =
      startStringLiteral('\'')

    /**
      * Returns an updated state object that is no longer in the state of
      * parsing a string literal.
      *
      * @return the updated state
      */
    def withStringLiteralCompleted(): ParserState =
      withCharacter(stringLiteral).copy(stringLiteral = '\u0000')

    /**
      * Returns the map with parsed styles at the end of a parse operation.
      * If the last definition is not completed with a ';', it is added based
      * on the current aggregated text.
      *
      * @return the map with the parsed styles and their values
      */
    def toStylesMap: Map[String, String] =
      key.fold(stylesMap)(_ => withValue().toStylesMap)

    /**
      * Returns an updated state object with the property for string literal
      * parsing set to the given quote character. This character is also added
      * to the current text.
      *
      * @param quote the quote character used for the string literal
      * @return the updated state
      */
    private def startStringLiteral(quote: Char): ParserState = {
      text.append(quote)
      copy(stringLiteral = quote)
    }
  }

  /**
    * Creates a new, empty instance of ''Styles''.
    *
    * @return the newly created instance
    */
  def apply(): Styles = new Styles(Map.empty)

  /**
    * Creates a new instance of ''Styles'' based on the given textual styles
    * definition. The passed in string is parsed to extract all style
    * definitions. Note that parsing is quite limited, since for the purpose of
    * this library, it is sufficient to extract only a limited number of
    * styles, e.g. for colors or font attributes. For other styles, it is
    * sufficient that their values can be reproduced again, but they do not
    * have to be interpreted.
    *
    * @param styleDef a textual styles definition
    * @return a newly created instance initialized with these styles definitions
    */
  def apply(styleDef: String): Styles = {
    if (Log.isDebugEnabled) {
      Log.debug("Parsing styles definition: " + styleDef)
    }

    val stylesMap = parseStyleDefinition(initialParserState(styleDef), 0)
    new Styles(stylesMap)
  }

  /**
    * Returns an initial [[ParserState]] object for parsing the given style
    * definition.
    *
    * @param styleDef the style definition to parse
    * @return the initial state for the parse operation
    */
  private def initialParserState(styleDef: String): ParserState =
    ParserState(
      styleDef = styleDef,
      stylesMap = Map.empty,
      key = None,
      text = new StringBuilder(),
      stringLiteral = '\u0000',
      escape = false
    )

  /**
    * Parses a style definition stored in a [[ParserState]] and returns the
    * extracted styles and their values.
    *
    * @param state the object with state information
    * @param index the index of the current character to process
    * @return the extracted styles and their values
    */
  @tailrec private def parseStyleDefinition(state: ParserState, index: Int): Map[String, String] =
    if (state.isDone(index)) {
      state.toStylesMap
    } else {
      val updatedState = nextState(state, state(index))
      parseStyleDefinition(updatedState, index + 1)
    }

  /**
    * Updates the state according to the current next character.
    *
    * @param state the current state
    * @param ch    the current character to process
    * @return the updated state
    */
  private def nextState(state: ParserState, ch: Char): ParserState =
    if (state.keyFound)
      ch match {
        case ';' if !state.inStringLiteral => state.withValue()
        case '"' if state.inDoubleQuoteStringLiteral && !state.escape => state.withStringLiteralCompleted()
        case '"' if !state.inSingleQuoteStringLiteral => state.withDoubleQuoteStringLiteral()
        case '\'' if state.inSingleQuoteStringLiteral && !state.escape => state.withStringLiteralCompleted()
        case '\'' if !state.inDoubleQuoteStringLiteral => state.withSingleQuoteStringLiteral()
        case c if c.isWhitespace && !state.inStringLiteral =>
          if (state.inWhitespace) state
          else state.withCharacter(' ')
        case c => state.withCharacter(c)
      }
    else
      ch match {
        case ':' => state.withKey()
        case c if c.isWhitespace => state
        case c => state.withCharacter(c)
      }
}

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
private[components] class Styles private(map: Map[String, String]) {
  /**
    * Holds the current map with style definitions.
    */
  private var stylesMap = map

  import Styles._

  /**
    * Adds the given styles definition to this object. An already existing
    * definition for this key is overridden.
    *
    * @param styleDef a key value pair with the style key and its value
    */
  def +=(styleDef: (String, String)): Unit = {
    stylesMap = stylesMap + styleDef
  }

  /**
    * Removes the styles definition for the given key from this object.
    *
    * @param key the key of the styles definition to be removed
    */
  def -=(key: String): Unit = {
    stylesMap = stylesMap - key
  }

  /**
    * Adds or removes a style definition. This method evaluates the ''Option''
    * object with the style's value. If it is defined, the key is added with
    * this value (replacing an already existing value). Otherwise, the key is
    * removed from this object. This method is convenient for clients that
    * already store properties in ''Option'' objects; they do not have to do
    * any checks or conversions.
    *
    * @param key   the key of the style to be updated
    * @param value the value of this style as an ''Option''
    */
  def updateStyle(key: String, value: Option[String]): Unit = {
    value match {
      case Some(x) => +=(key, x)
      case None => -=(key)
    }
  }

  /**
    * Returns a set with all keys of style definitions contained in this object.
    *
    * @return a set with the keys of all known style definitions
    */
  def styleKeys: Set[String] = stylesMap.keySet

  /**
    * Returns an ''Option'' object with the value of the style with the given
    * key.
    *
    * @param key the key of the style in question
    * @return an ''Option'' with the value of this style
    */
  def apply(key: String): Option[String] = stylesMap get key

  /**
    * Transforms the contained style definitions to a string which can be passed
    * to a Java FX ''Node'' object. The order of the style definitions in the
    * returned string is random.
    *
    * @return a string representation for all contained style definitions
    */
  def toExternalForm: String = {
    val buf = new StringBuilder(BufSize)
    stylesMap foreach { styleDef =>
      buf ++= styleDef._1
      buf ++= KeySeparator
      buf ++= styleDef._2
      buf ++= DefinitionEnd
    }
    buf.toString()
  }

  /**
    * Creates a string representation for this object. This string contains
    * the currently defined style sheets in textual form.
    */
  override def toString = s"Styles {\n$toExternalForm}"
}

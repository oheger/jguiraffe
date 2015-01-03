/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.builder.window

import java.net.URL

/**
 * Companion object.
 */
object StyleSheetProvider {
  /** Constant for the name of the standard style sheet. */
  val StandardStyleSheet = "jguiraffe-fx.css"

  /** The separator for the URL list string. */
  private val Separator = ","
}

/**
 * A class for providing CSS style sheets to be applied to newly created
 * ''Scene'' objects.
 *
 * An instance of this class is expected by the constructor of
 * [[net.sf.jguiraffe.gui.platform.javafx.builder.window.JavaFxWindowManager]].
 * The style sheet URLs managed by this instance are applied to each ''Scene''
 * object created by the window manager.
 *
 * The ''JGUIraffe JavaFX integration'' comes with one standard style sheet.
 * In addition, applications can add an arbitrary number of style sheet files.
 * Style sheets provided by the application can be passed as a comma-separated
 * list to the constructor. The standard style sheet is defined via a separate
 * constructor argument containing the name of the corresponding class path
 * resource. If the constructor is used that only takes the string list of
 * application-defined style sheets, the name of the standard style sheet is
 * set automatically. The purpose of this design is to allow an easy
 * customization in JGUIraffe applications:
 *
 * In order to add application-specific style sheets, applications typically
 * override the standard declaration of this bean with their own one. They
 * can then configure the additional style sheets directly in their own builder
 * script by collecting invocations of the ''<di:resource>'' tag in a variable.
 * This variable can be referenced in the declaration for the custom
 * ''StyleSheetProvider'' bean. The single-argument constructor should be used
 * to keep the standard style sheet.
 *
 * If the standard style sheet is to be replaced, the constructor accepting two
 * arguments is to be called. Typically, the second argument is set to
 * '''null''', meaning that this style sheet is to be ignored. The
 * corresponding classes to be adapted can then be defined by one of the
 * application-specific style sheets.
 *
 * @param styleSheetURLList a comma-separated string with the URLs of style sheets
 *                          to be added on behalf of the application
 * @param standardStyleSheet the resource name of the standard style sheet
 *                           shipped with this library
 */
class StyleSheetProvider(styleSheetURLList: String, val standardStyleSheet: String) {
  lazy val styleSheetURLs = createStyleSheetURLSet()

  /**
   * Creates a new instance of ''StyleSheetProvider'' with the specified string list
   * of style sheet URLs using the default name for the standard style sheet.
   * @param styleSheetURLList a comma-separated string with the URLs of style sheets
   *                          to be added on behalf of the application
   */
  def this(styleSheetURLList: String) = this(styleSheetURLList, StyleSheetProvider
    .StandardStyleSheet)

  /**
   * Creates the set with style sheet URLs based on the constructor parameters.
   * @return the set of style sheet URLs
   */
  private def createStyleSheetURLSet(): Set[String] = {
    val urls = styleSheetURLList.split(StyleSheetProvider.Separator).map(_.trim).filter(_.length
      > 0).toSet
    if (standardStyleSheet != null)
      urls + resolveStandardStyleSheet().toExternalForm
    else urls
  }

  /**
   * Resolve the standard style sheet name. This is a resource name. It is resolved
   * using the class loader of this class. If this fails, an exception is thrown.
   * @return the URL pointing to the standard style sheet
   * @throws IllegalStateException if the style sheet name cannot be resolved
   */
  private def resolveStandardStyleSheet(): URL = {
    val standardURL = getClass.getClassLoader.getResource(standardStyleSheet)
    if (standardURL == null) {
      throw new IllegalStateException(s"Standard style sheet cannot be resolved: $standardStyleSheet!")
    }
    standardURL
  }
}

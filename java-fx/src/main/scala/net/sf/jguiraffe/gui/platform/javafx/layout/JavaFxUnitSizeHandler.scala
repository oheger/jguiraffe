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
package net.sf.jguiraffe.gui.platform.javafx.layout

import java.util.concurrent.atomic.AtomicReference

import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Screen
import net.sf.jguiraffe.gui.layout.UnitSizeHandler
import net.sf.jguiraffe.gui.platform.javafx.builder.utils.JavaFxGUISynchronizer

/**
 * The Java FX-based implementation of the ''UnitSizeHandler'' interface.
 *
 * This class implements functionality for detecting screen properties and
 * calculating font sizes based on the Java FX library. Unfortunately, some
 * of these calculations are not trivial; especially, there is no easy way
 * to determine the width and height of a text string. The workaround applied
 * here makes use of Java FX components which are configured and layouted to
 * obtain their size. This has to happen in the Java FX thread, so threading
 * issues have to be taken into account.
 *
 * Because the calculation of font sizes can be expensive, a cache is
 * maintained which is shared by all instances. Access to the cache is
 * protected so this class can be called from different threads. (This is
 * required because size calculations will also have to take place in
 * background threads executing builder scripts.)
 */
class JavaFxUnitSizeHandler extends UnitSizeHandler {
  /**
   * @inheritdoc This implementation expects the passed in component to be an
   * instance of ''ContainerWrapper''. It makes use of a static cache for font
   * sizes. If the cache does not contain the font in question, a ''Text''
   * component is created for determining the font size. This has to happen in
   * the Java FX thread. Atomic variables are used to deal with concurrent
   * access.
   */
  def getFontSize(component: Object, y: Boolean): Double = {
    val container = ContainerWrapper.fromObject(component)
    val font = container.getContainerFont
    val cache = JavaFxUnitSizeHandler.fontSizeCache.get

    if (cache.contains(font)) {
      JavaFxUnitSizeHandler.extractSize(cache(font), y)
    } else {
      val fontSize = JavaFxUnitSizeHandler.calculateFontSize(font)
      JavaFxUnitSizeHandler.updateCache(cache, font, fontSize)
      JavaFxUnitSizeHandler.extractSize(fontSize, y)
    }
  }

  /**
   * @inheritdoc This implementation obtains the resolution from the primary
   * screen.
   */
  def getScreenResolution(): Int = Screen.getPrimary().getDpi().toInt
}

/**
 * The companion object of ''JavaFxUnitSizeHandler''. This object maintains a
 * shared cache with font sizes.
 */
object JavaFxUnitSizeHandler {
  /**
   * Constant for the string used for determining the average character width.
   */
  private val WidthString =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

  /** Constant for the length of the width string. */
  private val WidthStringLength = WidthString.length

  /** The shared cache with font sizes already queried. */
  private final val fontSizeCache =
    new AtomicReference(Map.empty[Font, Tuple2[Double, Double]])

  /**
   * Determines the width and height of the passed in font.
   * @param ft the font in question
   * @return a tuple with the average width and height of this font
   */
  private def calculateFontSize(ft: Font): Tuple2[Double, Double] = {
    var fontSize: Tuple2[Double, Double] = null
    JavaFxGUISynchronizer.syncJavaFxInvocation { () =>
      val txt = new Text(WidthString)
      txt setFont ft
      txt.snapshot(null, null)
      val bounds = txt.getLayoutBounds()
      fontSize = (bounds.getWidth / WidthStringLength, bounds.getHeight)
    }
    fontSize
  }

  /**
   * Updates the cache with the specified font size data.
   * @param the current cache map
   * @param font the font
   * @param value the font size
   */
  private def updateCache(cacheMap: Map[Font, Tuple2[Double, Double]],
    font: Font, value: Tuple2[Double, Double]) {
    var currentCache = cacheMap
    var done = false
    do {
      val newMap = cacheMap + (font -> value)
      done = fontSizeCache.compareAndSet(currentCache, newMap)
      if (!done) {
        currentCache = fontSizeCache.get
        done = currentCache.contains(font)
      }
    } while (!done)
  }

  /**
   * Extracts the desired field from the tuple with the font size.
   * @param fontSize the font size tuple
   * @param y flag for height or width
   * @return the desired size component
   */
  private def extractSize(fontSize: Tuple2[Double, Double], y: Boolean): Double =
    if (y) fontSize._2
    else fontSize._1
}

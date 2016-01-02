/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
import javafx.scene.text.Text
import javafx.stage.Screen

import net.sf.jguiraffe.gui.layout.UnitSizeHandler
import net.sf.jguiraffe.gui.platform.javafx.builder.utils.JavaFxGUISynchronizer
import org.apache.commons.jelly.JellyContext

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
    val init = container.getFontInitializer
    val cache = JavaFxUnitSizeHandler.fontSizeCache.get

    if (cache.contains(init)) {
      JavaFxUnitSizeHandler.extractSize(cache(init), y)
    } else {
      val fontSize = JavaFxUnitSizeHandler.calculateFontSize(init)
      JavaFxUnitSizeHandler.updateCache(cache, init, fontSize)
      JavaFxUnitSizeHandler.extractSize(fontSize, y)
    }
  }

  /**
   * @inheritdoc This implementation obtains the resolution from the primary
   * screen.
   */
  def getScreenResolution(): Int = Screen.getPrimary.getDpi.toInt
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

  /** Constant for the name of the size handler instance in the Jelly context. */
  private val SizeHandlerVariable = "jguiraffe.SizeHandler"

  /** The shared cache with font initializer objects already handled. */
  private final val fontSizeCache =
    new AtomicReference(Map.empty[ContainerWrapper.TextFontInitializer, (Double, Double)])

  /**
   * Obtains a size handler instance from the passed in Jelly context. If
   * the context does not contain an instance yet, a new one is created.
   * @param context the current Jelly context
   * @return the instance of this class from this context
   */
  def fromContext(context: JellyContext): UnitSizeHandler = {
    context.getVariable(SizeHandlerVariable) match {
      case handler: UnitSizeHandler => handler
      case _ => storeSizeHandler(context, new JavaFxUnitSizeHandler)
    }
  }

  /**
   * Stores the specified size handler instance under a well-known name in the
   * current Jelly context.
   * @param context the current Jelly context
   * @param handler the instance to be stored in the context
   * @return the stored size handler instance
   */
  def storeSizeHandler(context: JellyContext, handler: UnitSizeHandler): UnitSizeHandler = {
    context.setVariable(SizeHandlerVariable, handler)
    handler
  }

  /**
   * Determines the width and height of the font represented by the given
    * initializer.
   * @param init the font initializer
   * @return a tuple with the average width and height of this font
   */
  private def calculateFontSize(init: ContainerWrapper.TextFontInitializer): (Double, Double) = {
    var fontSize: (Double, Double) = null
    JavaFxGUISynchronizer.syncJavaFxInvocation { () =>
      val txt = init(new Text(WidthString))
      txt.snapshot(null, null)
      val bounds = txt.getLayoutBounds
      fontSize = (bounds.getWidth / WidthStringLength, bounds.getHeight)
    }
    fontSize
  }

  /**
   * Updates the cache with the specified font size data.
   * @param cacheMap the current cache map
   * @param init the font initializer
   * @param value the font size
   */
  private def updateCache(cacheMap: Map[ContainerWrapper.TextFontInitializer, (Double, Double)],
                          init: ContainerWrapper.TextFontInitializer, value: (Double, Double)) {
    var currentCache = cacheMap
    var done = false
    do {
      val newMap = cacheMap + (init -> value)
      done = fontSizeCache.compareAndSet(currentCache, newMap)
      if (!done) {
        currentCache = fontSizeCache.get
        done = currentCache.contains(init)
      }
    } while (!done)
  }

  /**
   * Extracts the desired field from the tuple with the font size.
   * @param fontSize the font size tuple
   * @param y flag for height or width
   * @return the desired size component
   */
  private def extractSize(fontSize: (Double, Double), y: Boolean): Double =
    if (y) fontSize._2
    else fontSize._1
}

/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.builder.utils

import java.util.Locale
import javafx.scene.control.Button

import net.sf.jguiraffe.gui.app.ApplicationContext
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.resources.ResourceManager
import net.sf.jguiraffe.resources.impl.ResourceManagerImpl
import net.sf.jguiraffe.resources.impl.bundle.BundleResourceLoader
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

/**
 * Companion object for ''TestMessageOutputButtonProvider''.
 */
object TestMessageOutputButtonProvider {
  /** A collection with the resource keys used by the message output implementation. */
  private val Resources = List("BTN_YES", "BTN_NO", "BTN_OK", "BTN_CANCEL")

  /** Constant for a test button title. */
  private val BTN_TITLE = "TestButtonTitle"

  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''MessageOutputButtonProvider''.
 */
class TestMessageOutputButtonProvider extends JUnitSuite with EasyMockSugar {

  import net.sf.jguiraffe.gui.platform.javafx.builder.utils.TestMessageOutputButtonProvider._

  /** The button provider to be tested. */
  private var buttonProvider: MessageOutputButtonProvider = _

  @Before def setUp(): Unit = {
    buttonProvider = new MessageOutputButtonProvider {}
  }

  /**
   * Creates a resource manager.
   * @return the resource manager
   */
  private def createResourceManager(): ResourceManager =
    new ResourceManagerImpl(new BundleResourceLoader())

  /**
   * Tests whether the expected resource keys can be resolved.
   */
  @Test def testResources(): Unit = {
    val resourceManager = createResourceManager()
    val TestLocale = Locale.ENGLISH

    for (res <- Resources) {
      assertNotNull(s"Cannot resolve $res", resourceManager.getResource(TestLocale,
        buttonProvider.MessageResourceGroup, res))
    }
  }

  /**
   * Tests whether a German translation of all message resources is available.
   */
  @Test def testResourcesGermanTranslation(): Unit = {
    val TestLocale = Locale.ENGLISH
    val TranslationLocale = Locale.GERMAN
    val resourceManager = createResourceManager()
    val defaultLocale = Locale.getDefault
    Locale setDefault TestLocale

    try {
      for (res <- Resources if res != "BTN_OK") {
        val resDefault = resourceManager.getResource(TestLocale,
          buttonProvider.MessageResourceGroup,
          res)
        val resTranslated = resourceManager.getResource(TranslationLocale,
          buttonProvider.MessageResourceGroup, res)
        assertNotEquals(s"No translation available for $res", resDefault, resTranslated)
      }
    } finally {
      Locale setDefault defaultLocale
    }
  }

  /**
   * Checks whether a correctly initialized button is returned by the provider.
   * @param resKey the expected resource key
   * @param f a function for obtaining the button from the provider
   * @return the newly created button
   */
  private def checkButton(resKey: String)(f: (MessageOutputButtonProvider,
    ApplicationContext) => Button): Button = {
    val appCtx = mock[ApplicationContext]
    EasyMock.expect(appCtx.getResourceText(buttonProvider.MessageResourceGroup,
      resKey)).andReturn(BTN_TITLE)
    var button: Button = null
    whenExecuting(appCtx) {
      button = f(buttonProvider, appCtx)
      assertEquals("Wrong button text", BTN_TITLE, button.getText)
    }
    button
  }

  /**
   * Tests whether the OK button can be obtained.
   */
  @Test def testOkButton(): Unit = {
    checkButton("BTN_OK")(_.okButton(_))
  }

  /**
   * Tests whether the Cancel button can be obtained.
   */
  @Test def testCancelButton(): Unit = {
    val button = checkButton("BTN_CANCEL")(_.cancelButton(_))
    assertTrue("Not the cancel button", button.isCancelButton)
  }

  /**
   * Tests whether the Yes button can be obtained.
   */
  @Test def testYesButton(): Unit = {
    checkButton("BTN_YES")(_.yesButton(_))
  }

  /**
   * Tests whether the No button can be obtained.
   */
  @Test def testNotButton(): Unit = {
    checkButton("BTN_NO")(_.noButton(_))
  }
}

/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.JUnitSuite

/**
 * Test class for ''StyleSheetProvider''.
 */
class TestStyleSheetProvider extends JUnitSuite {
  /**
   * Tests whether the correct name of the standard style sheet is set.
   */
  @Test def testStandardStyleSheetName(): Unit = {
    val provider = new StyleSheetProvider("someURLs")
    assertEquals("Wrong standard style sheet name", StyleSheetProvider.StandardStyleSheet,
      provider.standardStyleSheet)
  }

  /**
   * Creates a sequence with test style sheet URLs.
   * @param count the number of URLs to generate
   * @return the sequence with test URLs
   */
  private def createURLs(count: Int): IndexedSeq[String] = {
    (1 to count) map ("Css_URL" + _)
  }

  /**
   * Tests whether the standard style sheet can be removed.
   */
  @Test def testSuppressStandardStyleSheet(): Unit = {
    val URLCount = 8
    val urls = createURLs(URLCount)
    val provider = new StyleSheetProvider(urls mkString ",", null)

    assertEquals("Wrong number of style sheet URLs", URLCount, provider.styleSheetURLs.size)
    assertTrue("Wrong URLs", urls forall provider.styleSheetURLs.contains)
  }

  /**
   * Tests that the standard style sheet is added correctly to the set of URLs
   * after it has been resolved.
   */
  @Test def testStandardStyleSheetAddedToSet(): Unit = {
    val URLCount = 4
    val urls = createURLs(URLCount)
    val provider = new StyleSheetProvider(urls mkString ",")

    assertEquals("Wrong number of style sheet URLs", URLCount + 1, provider.styleSheetURLs.size)
    assertTrue("Test URLs not found", urls forall provider.styleSheetURLs.contains)
    val standardURL = provider.styleSheetURLs.find(_.endsWith(StyleSheetProvider
      .StandardStyleSheet))
    assertTrue("Standard style sheet URL not found", standardURL.isDefined)
    //test that a valid URL has been created
    new URL(standardURL.get)
  }

  /**
   * Tests the case that the provided standard style sheet cannot be resolved.
   */
  @Test(expected = classOf[IllegalStateException]) def testStandardStyleSheetCannotBeResolved():
  Unit = {
    val provider = new StyleSheetProvider("someURIs", "unresolvable style sheet")
    provider.styleSheetURLs
  }

  /**
   * Tests that an empty list of style sheets can be provided.
   */
  @Test def testEmptyStyleSheets(): Unit = {
    val provider = new StyleSheetProvider("", null)
    assertTrue("Got style sheets: " + provider.styleSheetURLs, provider.styleSheetURLs.isEmpty)
  }

  /**
   * Tests that white space before or after the separator is removed.
   */
  @Test def testWhiteSpaceAroundSeparatorRemoved(): Unit = {
    val Style1 = "TestStyleSheet1.css"
    val Style2 = "OtherStyleSheet.css"
    val provider = new StyleSheetProvider(Style1 + "  ,    " + Style2, null)

    assertTrue("Style1 not found: " + provider.styleSheetURLs, provider.styleSheetURLs contains
      Style1)
    assertTrue("Style2 not found", provider.styleSheetURLs contains Style2)
  }
}

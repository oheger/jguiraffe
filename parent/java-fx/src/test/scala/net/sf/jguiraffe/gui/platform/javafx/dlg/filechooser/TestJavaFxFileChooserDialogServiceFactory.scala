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
package net.sf.jguiraffe.gui.platform.javafx.dlg.filechooser

import net.sf.jguiraffe.gui.app.ApplicationContext
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.Test
import org.scalatest.easymock.EasyMockSugar
import org.scalatest.junit.JUnitSuite

/**
  * Test class for ''JavaFxFileChooserDialogServiceFactory''.
  */
class TestJavaFxFileChooserDialogServiceFactory extends JUnitSuite with EasyMockSugar {
  /**
    * Tests whether a correct service instance is created.
    */
  @Test def testServiceCanBeCreated(): Unit = {
    val appCtx = mock[ApplicationContext]
    EasyMock.replay(appCtx)
    val factory = new JavaFxFileChooserDialogServiceFactory

    factory.createService(appCtx) match {
      case fxService: JavaFxFileChooserDialogService =>
        assertEquals("Wrong application context", appCtx, fxService.applicationContext)
      case r => fail("Unexpected result: " + r)
    }
  }
}

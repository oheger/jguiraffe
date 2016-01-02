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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import net.sf.jguiraffe.gui.builder.components.Composite
import net.sf.jguiraffe.gui.builder.components.tags.{ContainerTag, FormBaseTag}
import net.sf.jguiraffe.gui.platform.javafx.layout.ContainerWrapper
import org.apache.commons.jelly.JellyContext
import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

/**
  * Test class for ''ContainerMapping''.
  */
class TestContainerMapping extends JUnitSuite with EasyMockSugar {
  /** The mapping to be tested. */
  private var mapping: ContainerMapping = _

  @Before def setUp(): Unit = {
    mapping = new ContainerMapping
  }

  /**
    * Tests whether a mapping can be queried by a tag object.
    */
  @Test def testGetMappingByTag(): Unit = {
    val tag = mock[FormBaseTag]
    val container = mock[ContainerWrapper]

    whenExecuting(tag, container) {
      mapping.add(tag, container)
      assertEquals("Wrong container", container, mapping.getContainer(tag).get)
    }
  }

  /**
    * Tests whether the mapping can be queried by a composite which is a tag.
    */
  @Test def testGetMappingByCompositeTag(): Unit = {
    val tag = mock[ContainerTag]
    val composite: Composite = tag
    val container = mock[ContainerWrapper]

    whenExecuting(tag, container) {
      mapping.add(tag, container)
      assertEquals("Wrong container", container, mapping.getContainerFromComposite(composite).get)
    }
  }

  /**
    * Tries to query the mapping with a Composite which is not a tag.
    */
  @Test def testGetMappingByCompositeNoTag(): Unit = {
    val composite = mock[Composite]

    whenExecuting(composite) {
      assertTrue("Got a container", mapping.getContainerFromComposite(composite).isEmpty)
    }
  }

  /**
    * Tests whether an existing instance can be queried from a Jelly context.
    */
  @Test def testFromContextExisting(): Unit = {
    val context = new JellyContext

    assertSame("Wrong result", mapping, ContainerMapping.storeContainerMapping(context, mapping))
    assertSame("Mapping not retrieved", mapping, ContainerMapping fromContext context)
  }

  /**
    * Tests whether a new mapping instance is created by fromContext() if the
    * context does not contain a mapping.
    */
  @Test def testFromContextNonExisting(): Unit = {
    val context = new JellyContext

    val mapping = ContainerMapping fromContext context
    assertNotNull("No mapping", mapping)
    assertSame("Multiple instances", mapping, ContainerMapping fromContext context)
  }
}

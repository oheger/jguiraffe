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
package net.sf.jguiraffe.gui.builder.components;

import static org.junit.Assert.assertSame;

import net.sf.jguiraffe.gui.builder.components.tags.ContainerTag;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code DefaultContainerSelector}.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class TestDefaultContainerSelector
{
    /** The selector to be tested. */
    private DefaultContainerSelector selector;

    @Before
    public void setUp() throws Exception
    {
        selector = new DefaultContainerSelector();
    }

    /**
     * Tests whether the expected composite is returned.
     */
    @Test
    public void testGetComposite()
    {
        ContainerTag tag = EasyMock.createMock(ContainerTag.class);
        EasyMock.replay(tag);

        assertSame("Wrong composite", tag, selector.getComposite(tag));
    }
}

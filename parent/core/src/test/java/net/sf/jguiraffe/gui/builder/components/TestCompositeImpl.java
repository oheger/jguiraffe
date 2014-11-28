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
package net.sf.jguiraffe.gui.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code CompositeImpl}.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class TestCompositeImpl
{
    /** The composite to be tested. */
    private CompositeImpl composite;

    @Before
    public void setUp() throws Exception
    {
        composite = new CompositeImpl();
    }

    /**
     * Tests a newly created instance.
     */
    @Test
    public void testInit()
    {
        assertNull("Got a container", composite.getContainer());
        assertNull("Got a layout", composite.getLayout());
        assertTrue("Got components", composite.getComponents().isEmpty());
    }

    /**
     * Tests that the collection with components cannot be modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetComponentsModify()
    {
        composite.getComponents().add(null);
    }

    /**
     * Tests whether a layout object can be set.
     */
    @Test
    public void testSetLayout()
    {
        final Object layout = "Layout";
        composite.setLayout(layout);
        assertSame("Wrong layout", layout, composite.getLayout());
    }

    /**
     * Tests whether the container can be accessed.
     */
    @Test
    public void testGetContainer()
    {
        final Object container = "Container";
        composite.setContainer(container);
        assertSame("Wrong container", container, composite.getContainer());
    }

    /**
     * Tests whether components can be added.
     */
    @Test
    public void testAddComponent()
    {
        final int count = 8;
        final String componentPrefix = "Component_";
        final String constraintPrefix = "Constraint_";
        for (int i = 0; i < count; i++)
        {
            composite.addComponent(componentPrefix + i, constraintPrefix + i);
        }

        Collection<Object[]> components = composite.getComponents();
        assertEquals("Wrong number of components", count, components.size());
        int idx = 0;
        for (Object[] compData : components)
        {
            assertEquals("Wrong component at " + idx, componentPrefix + idx,
                    compData[0]);
            assertEquals("Wrong constraint at " + idx, constraintPrefix + idx,
                    compData[1]);
            idx++;
        }
    }
}

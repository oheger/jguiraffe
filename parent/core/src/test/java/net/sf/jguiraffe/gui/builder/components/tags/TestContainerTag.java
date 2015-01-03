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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.Composite;
import net.sf.jguiraffe.gui.builder.components.CompositeImpl;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ContainerTag}.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class TestContainerTag
{
    /** The container tag to be tested. */
    private ContainerTag tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new ContainerTag()
        {
            @Override
            protected Object createContainer(ComponentManager manager,
                    boolean create, Collection<Object[]> components)
                    throws FormBuilderException, JellyTagException
            {
                return null;
            }
        };

        JellyContext context = new JellyContext();
        ComponentBuilderData builderData = new ComponentBuilderData();
        builderData.put(context);
        tag.setContext(context);
        tag.processBeforeBody();
    }

    /**
     * Tests whether a default Composite object has been set.
     */
    @Test
    public void testDefaultComposite()
    {
        assertTrue("Wrong composite",
                tag.getComposite() instanceof CompositeImpl);
    }

    /**
     * Tests whether a new composite object is created for each execution.
     */
    @Test
    public void testNewCompositeWhenExecutedAgain()
            throws FormBuilderException, JellyTagException
    {
        Composite composite = tag.getComposite();
        tag.processBeforeBody();
        assertNotSame("No new composite", composite, tag.getComposite());
    }

    /**
     * Tests whether an alternative Composite can be set and components added to
     * it.
     */
    @Test
    public void testAddComponentToReplacedComposite()
    {
        CompositeImpl composite = new CompositeImpl();
        tag.setComposite(composite);
        tag.addComponent(this, "constraints");

        Collection<Object[]> components = composite.getComponents();
        assertEquals("Wrong number of components", 1, components.size());
        Object[] compData = components.iterator().next();
        assertEquals("Wrong component", this, compData[0]);
        assertEquals("Wrong constraint", "constraints", compData[1]);
    }

    /**
     * Tests whether an alternative Composite can be set and is used for storing
     * a layout object.
     */
    @Test
    public void testSetLayoutToReplacedComposite()
    {
        CompositeImpl composite = new CompositeImpl();
        tag.setComposite(composite);
        tag.setLayout(this);

        assertEquals("Wrong layout", this, composite.getLayout());
    }
}

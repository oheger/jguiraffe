/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.model;

import java.util.Collections;

import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for {@code TreeExpansionEvent}.
 *
 * @author Oliver Heger
 * @version $Id: $
 */
public class TestTreeExpansionEvent
{
    /** Constant for a component name. */
    private static final String NAME = "testTreeExpansionComponent";

    /** Constant for a tree path associated with the event. */
    private static final TreeNodePath PATH = createPath();

    /** A tree handler to be associated with test events. */
    private static TreeHandler handler;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        handler = EasyMock.createMock(TreeHandler.class);
        EasyMock.replay(handler);
    }

    /**
     * Creates a new path object.
     *
     * @return the new path
     */
    private static TreeNodePath createPath()
    {
        ConfigurationNode node =
                new DefaultConfigurationNode("testNode" + System.nanoTime());
        return new TreeNodePath(Collections.singleton(node));
    }

    /**
     * Tests equals() if the expected result is true.
     */
    @Test
    public void testEqualsTrue()
    {
        TreeExpansionEvent event =
                new TreeExpansionEvent(this, handler, NAME,
                        TreeExpansionEvent.Type.NODE_EXPAND, PATH);
        JGuiraffeTestHelper.checkEquals(event, event, true);
        TreeExpansionEvent e2 =
                new TreeExpansionEvent(this, handler, NAME,
                        TreeExpansionEvent.Type.NODE_EXPAND, PATH);
        JGuiraffeTestHelper.checkEquals(event, e2, true);
    }

    /**
     * Tests equals() if the expected result is false.
     */
    @Test
    public void testEqualsFalse()
    {
        TreeExpansionEvent event =
                new TreeExpansionEvent(this, handler, NAME,
                        TreeExpansionEvent.Type.NODE_EXPAND, PATH);
        TreeExpansionEvent e2 =
                new TreeExpansionEvent(this, handler, NAME + "_other",
                        TreeExpansionEvent.Type.NODE_EXPAND, PATH);
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 = new TreeExpansionEvent(this, handler, NAME, null, PATH);
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new TreeExpansionEvent(this, handler, NAME,
                        TreeExpansionEvent.Type.NODE_COLLAPSE, PATH);
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new TreeExpansionEvent(this, handler, NAME,
                        TreeExpansionEvent.Type.NODE_EXPAND, null);
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new TreeExpansionEvent(this, handler, NAME,
                        TreeExpansionEvent.Type.NODE_EXPAND, createPath());
        JGuiraffeTestHelper.checkEquals(event, e2, false);
    }
}

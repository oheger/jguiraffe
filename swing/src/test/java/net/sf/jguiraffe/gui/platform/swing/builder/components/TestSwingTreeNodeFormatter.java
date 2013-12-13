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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import static org.junit.Assert.assertEquals;

import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code SwingTreeNodeFormatter}.
 *
 * @author Oliver Heger
 * @version $Id: $
 */
public class TestSwingTreeNodeFormatter
{
    /** Constant for a test node name. */
    private static final String NODE_NAME = "MyTestNode";

    /** The formatter to be tested. */
    private SwingTreeNodeFormatter formatter;

    @Before
    public void setUp() throws Exception
    {
        formatter = new SwingTreeNodeFormatter();
    }

    /**
     * Tests whether the correct text is returned for the given node.
     */
    @Test
    public void testNodeText()
    {
        ConfigurationNode node = new DefaultConfigurationNode(NODE_NAME);
        assertEquals("Wrong text", NODE_NAME, formatter.textForNode(node));
    }
}

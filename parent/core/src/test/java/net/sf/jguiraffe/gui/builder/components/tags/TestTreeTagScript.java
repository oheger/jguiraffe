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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * A test class for tag handler classes related to tree components. This test
 * executes a Jelly script with a tree definition. It tests whether trees can be
 * created and initialized from Jelly scripts.
 *
 * @author Oliver Heger
 * @version $Id: TestTreeTagScript.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTreeTagScript extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "tree";

    /** Constant for the name of the tree model variable. */
    private static final String VAR_MODEL = "treeModel";

    /**
     * Constant for the name of the configuration file to be loaded as tree
     * model.
     */
    private static final String MODEL_CONFIG_FILE = "testappconfigmax.xml";

    /**
     * Initializes the Jelly context. This implementation loads a configuration
     * file as tree model and stores it in the context.
     */
    @Override
    protected void setUpJelly()
    {
        super.setUpJelly();

        try
        {
            XMLConfiguration conf = new XMLConfiguration();
            conf.load(MODEL_CONFIG_FILE);
            context.setVariable(VAR_MODEL, conf);
        }
        catch (ConfigurationException cex)
        {
            cex.printStackTrace();
            fail("Could not load configuration file!");
        }
    }

    /**
     * Tests processing a script with a tree definition.
     */
    public void testCreateTree() throws Exception
    {
        checkScript(SCRIPT, "Container: ROOT { TREE [ NAME = myTree "
                + "MODEL = treeModel EDITABLE = false ROOTVISIBLE = true "
                + "ICONS { NAME = LEAF [ ICON [ " + iconLocatorString()
                + " ]] } ] }");
    }
}

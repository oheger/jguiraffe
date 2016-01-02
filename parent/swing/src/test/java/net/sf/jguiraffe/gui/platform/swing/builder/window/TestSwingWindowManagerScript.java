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
package net.sf.jguiraffe.gui.platform.swing.builder.window;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JLabel;

import net.sf.jguiraffe.gui.app.Application;
import net.sf.jguiraffe.gui.app.ApplicationBuilderData;
import net.sf.jguiraffe.gui.app.ApplicationContext;
import net.sf.jguiraffe.gui.app.ApplicationException;
import net.sf.jguiraffe.gui.builder.BuilderException;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowUtils;
import net.sf.jguiraffe.locators.ClassPathLocator;
import net.sf.jguiraffe.locators.Locator;

import org.junit.Test;

/**
 * Test class for {@link SwingWindowManager} that executes a test script for
 * creating a window. The main purpose of this test is to verify that the
 * content of the window is correctly created and added to the window's content
 * pane.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingWindowManagerScript.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingWindowManagerScript
{
    /** The locator for the window script. */
    private static final Locator SCRIPT = ClassPathLocator
            .getInstance("jelly_scripts/window.jelly");

    /** Constant for the name of the builder. */
    private static final String BUIlDER_NAME = "FRAME_CONTENT";

    /**
     * Tests creating a frame window based on a script.
     */
    @Test
    public void testCreateFrame() throws ApplicationException, BuilderException
    {
        Application app = new Application();
        app.setConfigResourceName("testappconfigfactorymin.xml");
        Application.startup(app, new String[0]);
        ApplicationContext appCtx = app.getApplicationContext();
        ApplicationBuilderData builderData = appCtx.initBuilderData();
        builderData.setBuilderName(BUIlDER_NAME);
        Window wnd = appCtx.newBuilder().buildWindow(SCRIPT, builderData);
        assertNotNull("No window was created", wnd);
        JFrame frame = (JFrame) WindowUtils.getPlatformWindow(wnd);
        boolean found = false;
        for (Component comp : frame.getContentPane().getComponents())
        {
            if (comp instanceof JLabel)
            {
                JLabel lab = (JLabel) comp;
                if ("A label".equals(lab.getText()))
                {
                    found = true;
                }
            }
        }
        assertTrue("Content label not found!", found);
    }
}

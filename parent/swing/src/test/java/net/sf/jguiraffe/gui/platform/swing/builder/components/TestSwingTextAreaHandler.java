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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code SwingTextAreaHandler}.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingTextAreaHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingTextAreaHandler
{
    /** Constant for the scroll width.*/
    private static final int SCROLL_WIDTH = 320;

    /** Constant for the scroll height.*/
    private static final int SCROLL_HEIGHT = 200;

    /** The test text area.*/
    private JTextArea textArea;

    /** The handler to be tested.*/
    private SwingTextAreaHandler handler;

    @Before
    public void setUp() throws Exception
    {
        textArea = new JTextArea();
        handler = new SwingTextAreaHandler(textArea, SCROLL_WIDTH, SCROLL_HEIGHT);
    }

    /**
     * Tests whether the outer scroll pane is correctly initialized.
     */
    @Test
    public void testGetOuterComponent()
    {
        JScrollPane scr = (JScrollPane) handler.getOuterComponent();
        assertSame("Wrong view port component", textArea, scr.getViewport().getView());
        Dimension d = scr.getPreferredSize();
        assertEquals("Wrong preferred width", SCROLL_WIDTH, d.width);
        assertEquals("Wrong preferred height", SCROLL_HEIGHT, d.height);
    }
}

/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.window;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * <p>
 * An abstract base class for tests for concrete implementations of the
 * <code>{@link Window}</code> interface.
 * </p>
 * <p>
 * This test base class defines some test cases that check the methods of the
 * <code>Window</code> interface. The abstract method <code>getWindow()</code>
 * must be defined in concrete sub classes to provide access to the window to
 * test.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowBaseTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class WindowBaseTest
{
    /**
     * Tests setting and getting the window's coordinates.
     */
    @Test
    public void testBounds()
    {
        getWindow().setBounds(10, 20, 200, 100);
        assertEquals("Wrong xpos", 10, getWindow().getXPos());
        assertEquals("Wrong ypos", 20, getWindow().getYPos());
        assertEquals("Wrong width", 200, getWindow().getWidth());
        assertEquals("Wrong height", 100, getWindow().getHeight());
    }

    /**
     * Tests if a closing strategy can be set and accessed.
     */
    @Test
    public void testClosingStrategy()
    {
        WindowClosingStrategy strategy = EasyMock.createMock(WindowClosingStrategy.class);
        EasyMock.replay(strategy);
        assertNotNull("Closing strategy is null", getWindow()
                .getWindowClosingStrategy());
        getWindow().setWindowClosingStrategy(strategy);
        assertSame("Closing strategy was not stored", strategy, getWindow()
                .getWindowClosingStrategy());
        getWindow().setWindowClosingStrategy(null);
        assertNotNull("Closing strategy is null", getWindow()
                .getWindowClosingStrategy());
    }

    /**
     * Tests opening and closing the window.
     */
    @Test
    public void testOpenAndClose()
    {
        getWindow().open();
        assertTrue("Window not visible", getWindow().isVisible());
        getWindow().close(true);
        assertFalse("Window is not hidden", getWindow().isVisible());
    }

    /**
     * Tests showing and hiding the window.
     */
    @Test
    public void testVisible()
    {
        getWindow().setVisible(true);
        assertTrue("Window not visible", getWindow().isVisible());
        getWindow().setVisible(false);
        assertFalse("Window is not hidden", getWindow().isVisible());
    }

    /**
     * Tests accessing the window's title.
     */
    @Test
    public void testWindowTitle()
    {
        getWindow().setTitle("My window");
        assertEquals("Title not set", "My window", getWindow().getTitle());
    }

    /**
     * Returns the window to be tested.
     *
     * @return the test window
     */
    protected abstract Window getWindow();
}

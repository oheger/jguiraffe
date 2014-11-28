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
package net.sf.jguiraffe.gui.builder;

import static org.junit.Assert.fail;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@link AutoReleaseListener}.
 *
 * @author Oliver Heger
 * @version $Id: TestAutoReleaseListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestAutoReleaseListener
{
    /** A mock object for a builder. */
    private Builder builder;

    /** A mock object for a BuilderData. */
    private BuilderData builderData;

    /** The mock window that is part of the window event. */
    private Window window;

    /**
     * Creates a window event that can be used for tests.
     *
     * @param type the event type
     * @return the event
     */
    private WindowEvent setUpEvent(WindowEvent.Type type)
    {
        window = EasyMock.createMock(Window.class);
        return new WindowEvent(this, window, type);
    }

    /**
     * Creates a builder data mock. This mock is prepared to expect an
     * invocation of the getBuilder() method and to return the mock builder.
     *
     * @return the mock builder data
     */
    private BuilderData setUpBuilderData()
    {
        builder = EasyMock.createMock(Builder.class);
        builderData = EasyMock.createMock(BuilderData.class);
        EasyMock.expect(builderData.getBuilder()).andReturn(builder);
        return builderData;
    }

    /**
     * Helper method for replaying mock objects that can be null.
     *
     * @param mocks the mock objects
     */
    private static void replay(Object... mocks)
    {
        for (Object mock : mocks)
        {
            if (mock != null)
            {
                EasyMock.replay(mock);
            }
        }
    }

    /**
     * Helper method for verifying mock objects that can be null. Only the
     * non-null objects are passed to EasyMock.
     *
     * @param mocks the mock objects
     */
    private static void verify(Object... mocks)
    {
        for (Object mock : mocks)
        {
            if (mock != null)
            {
                EasyMock.verify(mock);
            }
        }
    }

    /**
     * Replays all defined mock objects.
     */
    private void replay()
    {
        replay(builderData, builder, window);
    }

    /**
     * Verifies all defined mock objects.
     */
    private void verify()
    {
        verify(builderData, builder, window);
    }

    /**
     * Tests creating an instance without a BuilderData. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoBuilderData()
    {
        new AutoReleaseListener(null);
    }

    /**
     * Tests creating an instance when the BuilderData does not contain a
     * builder. This should cause an exception.
     */
    @Test
    public void testInitNoBuilder()
    {
        builderData = EasyMock.createMock(BuilderData.class);
        EasyMock.expect(builderData.getBuilder()).andReturn(null);
        replay();
        try
        {
            new AutoReleaseListener(builderData);
            fail("Missing builder not detected!");
        }
        catch (IllegalArgumentException iex)
        {
            verify();
        }
    }

    /**
     * Tests the window activated event. We can only test that no exception is
     * thrown and that the window object is not touched.
     */
    @Test
    public void testWindowActivated()
    {
        setUpBuilderData();
        WindowEvent event = setUpEvent(WindowEvent.Type.WINDOW_ACTIVATED);
        replay();
        AutoReleaseListener l = new AutoReleaseListener(builderData);
        l.windowActivated(event);
        verify();
    }

    /**
     * Tests the window deactivated event. We can only test that no exception is
     * thrown and that the window object is not touched.
     */
    @Test
    public void testWindowDeactivated()
    {
        setUpBuilderData();
        WindowEvent event = setUpEvent(WindowEvent.Type.WINDOW_DEACTIVATED);
        replay();
        AutoReleaseListener l = new AutoReleaseListener(builderData);
        l.windowDeactivated(event);
        verify();
    }

    /**
     * Tests the window deiconified event. We can only test that no exception is
     * thrown and that the window object is not touched.
     */
    @Test
    public void testWindowDeiconified()
    {
        setUpBuilderData();
        WindowEvent event = setUpEvent(WindowEvent.Type.WINDOW_DEICONIFIED);
        replay();
        AutoReleaseListener l = new AutoReleaseListener(builderData);
        l.windowDeiconified(event);
        verify();
    }

    /**
     * Tests the window iconified event. We can only test that no exception is
     * thrown and that the window object is not touched.
     */
    @Test
    public void testWindowIconified()
    {
        setUpBuilderData();
        WindowEvent event = setUpEvent(WindowEvent.Type.WINDOW_ICONIFIED);
        replay();
        AutoReleaseListener l = new AutoReleaseListener(builderData);
        l.windowIconified(event);
        verify();
    }

    /**
     * Tests the window opened event. We can only test that no exception is
     * thrown and that the window object is not touched.
     */
    @Test
    public void testWindowOpened()
    {
        setUpBuilderData();
        WindowEvent event = setUpEvent(WindowEvent.Type.WINDOW_OPENED);
        replay();
        AutoReleaseListener l = new AutoReleaseListener(builderData);
        l.windowOpened(event);
        verify();
    }

    /**
     * Tests the window closing event. We can only test that no exception is
     * thrown and that the window object is not touched.
     */
    @Test
    public void testWindowClosing()
    {
        setUpBuilderData();
        WindowEvent event = setUpEvent(WindowEvent.Type.WINDOW_CLOSING);
        replay();
        AutoReleaseListener l = new AutoReleaseListener(builderData);
        l.windowClosing(event);
        verify();
    }

    /**
     * Tests the reaction on the window closed event. Here the release operation
     * has to be performed.
     */
    @Test
    public void testWindowClosed()
    {
        setUpBuilderData();
        WindowEvent event = setUpEvent(WindowEvent.Type.WINDOW_CLOSED);
        builder.release(builderData);
        replay();
        AutoReleaseListener l = new AutoReleaseListener(builderData);
        l.windowClosed(event);
        verify();
    }
}

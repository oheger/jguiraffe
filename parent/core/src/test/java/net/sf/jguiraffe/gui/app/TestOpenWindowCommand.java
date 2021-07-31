/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.gui.builder.Builder;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.locators.Locator;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code OpenWindowCommand}.
 *
 * @author Oliver Heger
 * @version $Id: TestOpenWindowCommand.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestOpenWindowCommand
{
    /** The locator for the builder script. */
    private Locator locator;

    @Before
    public void setUp() throws Exception
    {
        locator = EasyMock.createMock(Locator.class);
        EasyMock.replay(locator);
    }

    /**
     * Tries to create an instance without a locator. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoLocator()
    {
        new OpenWindowCommand(null);
    }

    /**
     * Tests a newly created instance.
     */
    @Test
    public void testInit()
    {
        OpenWindowCommand cmd = new OpenWindowCommand(locator);
        assertEquals("Wrong locator", locator, cmd.getLocator());
        assertNull("Got an application", cmd.getApplication());
    }

    /**
     * Tests whether the command performs UI updates.
     */
    @Test
    public void testIsUpdateGUI()
    {
        OpenWindowCommand cmd = new OpenWindowCommand(locator);
        assertTrue("Command does not update GUI", cmd.isUpdateGUI());
    }

    /**
     * Tries to execute the command without the application being initialized.
     * This should cause an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testExecuteNoApp() throws Exception
    {
        OpenWindowCommand cmd = new OpenWindowCommand(locator);
        cmd.execute();
    }

    /**
     * Tests a successful execution of the command.
     */
    @Test
    public void testExecute() throws Exception
    {
        ApplicationContext appCtx = EasyMock
                .createMock(ApplicationContext.class);
        Builder builder = EasyMock.createMock(Builder.class);
        Window wnd = EasyMock.createMock(Window.class);
        Application app = new Application();
        ApplicationBuilderData builderData = new ApplicationBuilderData();
        EasyMock.expect(appCtx.newBuilder()).andReturn(builder);
        EasyMock.expect(appCtx.initBuilderData()).andReturn(builderData);
        EasyMock.expect(builder.buildWindow(locator, builderData)).andReturn(
                wnd);
        EasyMock.replay(appCtx, builder, wnd);
        app.setApplicationContext(appCtx);
        OpenWindowCommandTestImpl cmd = new OpenWindowCommandTestImpl(locator);
        cmd.setApplication(app);
        cmd.execute();
        assertEquals("Wrong window", wnd, cmd.getWindow());
        EasyMock.verify(locator, appCtx, builder, wnd);
    }

    /**
     * Tests the UI update. Here the window should be displayed.
     */
    @Test
    public void performGUIUpdate()
    {
        Window wnd = EasyMock.createMock(Window.class);
        wnd.open();
        EasyMock.replay(wnd);
        OpenWindowCommandTestImpl cmd = new OpenWindowCommandTestImpl(locator);
        cmd.mockWindow = wnd;
        cmd.performGUIUpdate();
        EasyMock.verify(wnd);
    }

    /**
     * Tests the UI update if an exception was thrown. In this case the window
     * is not opened.
     */
    @Test
    public void performGUIUpdateEx()
    {
        Window wnd = EasyMock.createMock(Window.class);
        EasyMock.replay(wnd);
        OpenWindowCommandTestImpl cmd = new OpenWindowCommandTestImpl(locator);
        cmd.mockWindow = wnd;
        cmd.setException(new Exception());
        cmd.performGUIUpdate();
        EasyMock.verify(wnd);
    }

    /**
     * A test open window command implementation for testing whether the command
     * is correctly executed.
     */
    private static class OpenWindowCommandTestImpl extends OpenWindowCommand
    {
        /** The builder data passed to prepareBuilderData(). */
        ApplicationBuilderData prepareBuilderData;

        /** The mock window to be returned by getWindow(). */
        Window mockWindow;

        public OpenWindowCommandTestImpl(Locator loc)
        {
            super(loc);
        }

        /**
         * Records this invocation.
         */
        @Override
        protected void prepareBuilderData(ApplicationBuilderData builderData)
        {
            assertNull("Too many invocations", prepareBuilderData);
            prepareBuilderData = builderData;
            super.prepareBuilderData(builderData);
        }

        /**
         * Either returns the mock window or calls the super method.
         */
        @Override
        Window getWindow()
        {
            return (mockWindow != null) ? mockWindow : super.getWindow();
        }
    }
}

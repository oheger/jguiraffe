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
package net.sf.jguiraffe.gui.builder.window;

import static org.junit.Assert.assertSame;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code WindowManagerWrapper}.
 *
 * @author Oliver Heger
 * @version $Id: TestWindowManagerWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestWindowManagerWrapper
{
    /** The mock for the wrapped window manager. */
    private WindowManager wrappedManager;

    /** The wrapper to be tested. */
    private WindowManagerWrapper wrapper;

    @Before
    public void setUp() throws Exception
    {
        wrappedManager = EasyMock.createMock(WindowManager.class);
        wrapper = new WindowManagerWrapper(wrappedManager)
        {
        };
    }

    /**
     * Replays the specified mock objects including the mock for the wrapped
     * manager.
     *
     * @param mocks the mocks to be replayed
     */
    private void replay(Object... mocks)
    {
        EasyMock.replay(mocks);
        EasyMock.replay(wrappedManager);
    }

    /**
     * Verifies the specified mock objects including the mock for the wrapped
     * manager.
     *
     * @param mocks the mocks to be verified
     */
    private void verify(Object... mocks)
    {
        EasyMock.verify(mocks);
        EasyMock.verify(wrappedManager);
    }

    /**
     * Tests whether the correct wrapped action manager is returned.
     */
    @Test
    public void testGetWrappedActionManager()
    {
        assertSame("Wrong manager", wrappedManager,
                wrapper.getWrappedWindowManager());
    }

    /**
     * Tests the createFrame() implementation.
     */
    @Test
    public void testCreateFrame() throws WindowBuilderException
    {
        Window window = EasyMock.createMock(Window.class);
        Window resWnd = EasyMock.createMock(Window.class);
        WindowData data = EasyMock.createMock(WindowData.class);
        WindowBuilderData wbd = new WindowBuilderData();
        EasyMock.expect(wrappedManager.createFrame(wbd, data, window))
                .andReturn(resWnd);
        replay(window, resWnd, data);
        assertSame("Wrong result", resWnd,
                wrapper.createFrame(wbd, data, window));
        verify(window, resWnd, data);
    }

    /**
     * Tests the createInternalFrame() implementation.
     */
    @Test
    public void testCreateInternalFrame() throws WindowBuilderException
    {
        Window window = EasyMock.createMock(Window.class);
        Window resWnd = EasyMock.createMock(Window.class);
        WindowData data = EasyMock.createMock(WindowData.class);
        WindowBuilderData wbd = new WindowBuilderData();
        EasyMock.expect(wrappedManager.createInternalFrame(wbd, data, window))
                .andReturn(resWnd);
        replay(window, resWnd, data);
        assertSame("Wrong result", resWnd,
                wrapper.createInternalFrame(wbd, data, window));
        verify(window, resWnd, data);
    }

    /**
     * Helper method for testing the createDialog() implementation.
     *
     * @param modal the modal flag
     * @throws WindowBuilderException if an error occurs
     */
    private void checkCreateDialog(boolean modal) throws WindowBuilderException
    {
        Window window = EasyMock.createMock(Window.class);
        Window resWnd = EasyMock.createMock(Window.class);
        WindowData data = EasyMock.createMock(WindowData.class);
        WindowBuilderData wbd = new WindowBuilderData();
        EasyMock.expect(wrappedManager.createDialog(wbd, data, modal, window))
                .andReturn(resWnd);
        replay(window, resWnd, data);
        assertSame("Wrong result", resWnd,
                wrapper.createDialog(wbd, data, modal, window));
        verify(window, resWnd, data);
    }

    /**
     * Tests createDialog() for non-modal dialogs.
     */
    @Test
    public void testCreateDialogNonModal() throws WindowBuilderException
    {
        checkCreateDialog(false);
    }

    /**
     * Tests createDialog() for modal dialogs.
     */
    @Test
    public void testCreateDialogModal() throws WindowBuilderException
    {
        checkCreateDialog(true);
    }
}

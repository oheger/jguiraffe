/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action;

import static org.junit.Assert.assertSame;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.tags.LabelTag;
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ActionManagerWrapper}.
 *
 * @author Oliver Heger
 * @version $Id: TestActionManagerWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestActionManagerWrapper
{
    /** The mock for the wrapped action manager. */
    private ActionManager wrappedManager;

    /** The wrapper to be tested. */
    private ActionManagerWrapper wrapper;

    @Before
    public void setUp() throws Exception
    {
        wrappedManager = EasyMock.createMock(ActionManager.class);
        wrapper = new ActionManagerWrapper(wrappedManager)
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
     * Tests whether the correct manager is returned.
     */
    @Test
    public void testGetWrappedActionManager()
    {
        assertSame("Wrong wrapped manager", wrappedManager,
                wrapper.getWrappedActionManager());
    }

    /**
     * Tests the createAction() implementation.
     */
    @Test
    public void testCreateAction() throws FormActionException
    {
        FormAction action = EasyMock.createMock(FormAction.class);
        ActionData data = EasyMock.createMock(ActionData.class);
        ActionBuilder builder = new ActionBuilder();
        EasyMock.expect(wrappedManager.createAction(builder, data)).andReturn(
                action);
        replay(action, data);
        assertSame("Wrong action", action, wrapper.createAction(builder, data));
        verify(action, data);
    }

    /**
     * Tests the createMenuItem() implementation which expects an action.
     */
    @Test
    public void testCreateMenuItemFromAction() throws FormActionException
    {
        FormAction action = EasyMock.createMock(FormAction.class);
        final Object parent = new Object();
        final Object item = new Object();
        ActionBuilder builder = new ActionBuilder();
        EasyMock.expect(
                wrappedManager.createMenuItem(builder, action, true, parent))
                .andReturn(item);
        replay(action);
        assertSame("Wrong item", item,
                wrapper.createMenuItem(builder, action, true, parent));
        verify(action);
    }

    /**
     * Tests the createMenuItem() implementation which expects a data object.
     */
    @Test
    public void testCreateMenuItemFromData() throws FormActionException
    {
        ActionData data = EasyMock.createMock(ActionData.class);
        ComponentHandler<?> ch = EasyMock.createMock(ComponentHandler.class);
        ActionBuilder builder = new ActionBuilder();
        final Object parent = new Object();
        wrappedManager.createMenuItem(builder, data, false, parent);
        EasyMock.expectLastCall().andReturn(ch);
        replay(data, ch);
        assertSame("Wrong component handler", ch,
                wrapper.createMenuItem(builder, data, false, parent));
        verify(data, ch);
    }

    /**
     * Tests the createMenuBar() implementation.
     */
    @Test
    public void testCreateMenuBar() throws FormActionException
    {
        ActionBuilder builder = new ActionBuilder();
        final Object bar = new Object();
        EasyMock.expect(wrappedManager.createMenuBar(builder)).andReturn(bar);
        replay();
        assertSame("Wrong menu bar", bar, wrapper.createMenuBar(builder));
        verify();
    }

    /**
     * Tests the createMenu() implementation.
     */
    @Test
    public void testCreateMenu() throws FormActionException
    {
        ActionBuilder builder = new ActionBuilder();
        final Object menu = new Object();
        final Object newMenu = new Object();
        final Object parent = new Object();
        final TextIconData tiData = new TextIconData(new LabelTag());
        EasyMock.expect(
                wrappedManager.createMenu(builder, menu, tiData, parent))
                .andReturn(newMenu);
        replay();
        assertSame("Wrong menu", newMenu,
                wrapper.createMenu(builder, menu, tiData, parent));
        verify();
    }

    /**
     * Tests the createToolbar() implementation.
     */
    @Test
    public void testCreateToolbar() throws FormActionException
    {
        ActionBuilder builder = new ActionBuilder();
        final Object toolbar = new Object();
        EasyMock.expect(wrappedManager.createToolbar(builder)).andReturn(
                toolbar);
        replay();
        assertSame("Wrong tool bar", toolbar, wrapper.createToolbar(builder));
        verify();
    }

    /**
     * Tests the createToolbarButton() implementation which expects an action.
     */
    @Test
    public void testCreateToolbarButtonFromAction() throws FormActionException
    {
        FormAction action = EasyMock.createMock(FormAction.class);
        ActionBuilder builder = new ActionBuilder();
        final Object parent = new Object();
        final Object button = new Object();
        EasyMock.expect(
                wrappedManager.createToolbarButton(builder, action, true,
                        parent)).andReturn(button);
        replay(action);
        assertSame("Wrong button", button,
                wrapper.createToolbarButton(builder, action, true, parent));
        verify(action);
    }

    /**
     * Tests the createToolbarButton() implementation which expects a data
     * object.
     */
    @Test
    public void testCreateToolbarButtonFromData() throws FormActionException
    {
        ActionData data = EasyMock.createMock(ActionData.class);
        ComponentHandler<?> ch = EasyMock.createMock(ComponentHandler.class);
        ActionBuilder builder = new ActionBuilder();
        final Object parent = new Object();
        wrappedManager.createToolbarButton(builder, data, false, parent);
        EasyMock.expectLastCall().andReturn(ch);
        replay(data, ch);
        assertSame("Wrong component handler", ch,
                wrapper.createToolbarButton(builder, data, false, parent));
        verify(data, ch);
    }

    /**
     * Tests the addMenuSeparator() implementation.
     */
    @Test
    public void testAddMenuSeparator() throws FormActionException
    {
        ActionBuilder builder = new ActionBuilder();
        final Object menu = new Object();
        wrappedManager.addMenuSeparator(builder, menu);
        replay();
        wrapper.addMenuSeparator(builder, menu);
        verify();
    }

    /**
     * Tests the addToolbarSeparator() implementation.
     */
    @Test
    public void testAddToolbarSeparator() throws FormActionException
    {
        ActionBuilder builder = new ActionBuilder();
        final Object toolbar = new Object();
        wrappedManager.addToolBarSeparator(builder, toolbar);
        replay();
        wrapper.addToolBarSeparator(builder, toolbar);
        verify();
    }

    /**
     * Tests the registerPopupMenuHandler() implementation.
     */
    @Test
    public void testRegisterPopupMenuHandler() throws FormActionException
    {
        PopupMenuHandler ph = EasyMock.createMock(PopupMenuHandler.class);
        ComponentBuilderData compData = new ComponentBuilderData();
        final Object comp = new Object();
        wrappedManager.registerPopupMenuHandler(comp, ph, compData);
        replay(ph);
        wrapper.registerPopupMenuHandler(comp, ph, compData);
        verify(ph);
    }
}

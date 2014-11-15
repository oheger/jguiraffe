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
package net.sf.jguiraffe.gui.builder.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code AbstractPopupMenuBuilder}.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class TestAbstractPopupMenuBuilder
{
    /** The builder to be tested. */
    private PopupMenuBuilderTestImpl builder;

    @Before
    public void setUp() throws Exception
    {
        ActionBuilder actBuilder = EasyMock.createMock(ActionBuilder.class);
        ActionManager manager = EasyMock.createMock(ActionManager.class);
        EasyMock.replay(actBuilder);
        builder = new PopupMenuBuilderTestImpl(manager, actBuilder);
    }

    /**
     * Tests whether an action can be added to the menu.
     */
    @Test
    public void testAddAction() throws FormActionException
    {
        FormAction action = EasyMock.createMock(FormAction.class);
        EasyMock.expect(
                builder.getActionManager().createMenuItem(
                        builder.getActionBuilder(), action, false,
                        builder.getMenuUnderConstruction())).andReturn(null);
        EasyMock.replay(builder.getActionManager(), action);

        assertSame("Wrong builder", builder, builder.addAction(action));
        EasyMock.verify(builder.getActionManager());
    }

    /**
     * Tests whether exceptions are handled when creating an action menu item.
     */
    @Test
    public void testAddActionException() throws FormActionException
    {
        FormAction action = EasyMock.createMock(FormAction.class);
        FormActionException ex = new FormActionException("Test exception");
        EasyMock.expect(
                builder.getActionManager().createMenuItem(
                        builder.getActionBuilder(), action, false,
                        builder.getMenuUnderConstruction())).andThrow(ex);
        EasyMock.replay(builder.getActionManager(), action);

        try
        {
            builder.addAction(action);
            fail("Exception not thrown!");
        }
        catch (FormBuilderRuntimeException fbrex)
        {
            assertEquals("Wrong exception", ex, fbrex.getCause());
        }
    }

    /**
     * Tests whether a sub menu can be added. We can only test here that none of
     * the objects involved are manipulated.
     */
    @Test
    public void testAddSubMenu()
    {
        EasyMock.replay(builder.getActionManager());
        assertSame("Wrong builder", builder, builder.addSubMenu("some menu"));
    }

    /**
     * Tests whether a separator can be added to the popup menu.
     */
    @Test
    public void testAddSeparator() throws FormActionException
    {
        builder.getActionManager().addMenuSeparator(builder.getActionBuilder(),
                builder.getMenuUnderConstruction());
        EasyMock.replay(builder.getActionManager());

        assertSame("Wrong builder", builder, builder.addSeparator());
        EasyMock.verify(builder.getActionManager());
    }

    /**
     * Tests that exceptions are handled when adding a separator.
     */
    @Test
    public void testAddSeparatorException() throws FormActionException
    {
        FormActionException ex = new FormActionException("Test exception");
        builder.getActionManager().addMenuSeparator(builder.getActionBuilder(),
                builder.getMenuUnderConstruction());
        EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(builder.getActionManager());

        try
        {
            builder.addSeparator();
            fail("Exception not thrown!");
        }
        catch (FormBuilderRuntimeException fbrex)
        {
            assertEquals("Wrong exception", ex, fbrex.getCause());
        }
    }

    /**
     * A test implementation defining the required methods.
     */
    private static class PopupMenuBuilderTestImpl extends
            AbstractPopupMenuBuilder
    {
        /** Simulates the parent menu to be constructed. */
        private static final Object PARENT_MENU = new Object();

        public PopupMenuBuilderTestImpl(ActionManager manager,
                ActionBuilder builder)
        {
            super(manager, builder);
        }

        @Override
        protected Object getMenuUnderConstruction()
        {
            return PARENT_MENU;
        }

        public PopupMenuBuilder subMenuBuilder(ActionData menuDesc)
        {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        public Object create()
        {
            throw new UnsupportedOperationException("Unexpected method call!");
        }
    }
}

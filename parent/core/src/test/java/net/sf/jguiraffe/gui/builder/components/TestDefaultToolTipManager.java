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
package net.sf.jguiraffe.gui.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;

import net.sf.jguiraffe.gui.builder.utils.GUIRuntimeException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code DefaultToolTipManager}.
 *
 * @author Oliver Heger
 * @version $Id: TestDefaultToolTipManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDefaultToolTipManager
{
    /** Constant for a standard tool tip. */
    private static final String TIP = "A default tool tip";

    /** Constant for an additional tool tip. */
    private static final String ADD_TIP = "An additional tool tip ;-)";

    /** Constant for the combined tool tip. */
    private static final String COMBINED_TIP = TIP + "\n" + ADD_TIP;

    /** Constant for the name of a test component. */
    private static final String COMP_NAME = "testComponent";

    /** Constant for a test component. */
    private static final Object COMP = new Object();

    /** The component builder data. */
    private ComponentBuilderDataTestImpl compData;

    /** The tool tip manager to be tested. */
    private DefaultToolTipManager manager;

    @Before
    public void setUp() throws Exception
    {
        compData = new ComponentBuilderDataTestImpl();
        manager = new DefaultToolTipManager(compData);
    }

    /**
     * Tests whether the correct component builder data object is returned.
     */
    @Test
    public void testGetComponentBuilderData()
    {
        assertEquals("Wrong data", compData, manager.getComponentBuilderData());
    }

    /**
     * Tries to create an instance without a data object. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoData()
    {
        new DefaultToolTipManager(null);
    }

    /**
     * Tests whether the correct default separator is returned.
     */
    @Test
    public void testGetAdditionalTipSeparatorDefault()
    {
        assertEquals("Wrong default tip separator", "\n", manager
                .getAdditionalTipSeparator());
    }

    /**
     * Tests whether the tip separator can be changed.
     */
    @Test
    public void testSetAdditionalTipSeparator()
    {
        final String sep = "-";
        manager.setAdditionalTipSeparator(sep);
        assertEquals("Wrong tip separator", sep, manager
                .getAdditionalTipSeparator());
    }

    /**
     * Tries to query the tool tip by name if the name cannot be resolved. This
     * should cause an exception.
     */
    @Test
    public void testGetToolTipNameUnknown()
    {
        compData.addComponentExpectation(COMP_NAME, null);
        try
        {
            manager.getToolTip(COMP_NAME);
            fail("Unknown name not detected!");
        }
        catch (GUIRuntimeException gex)
        {
            compData.verify();
        }
    }

    /**
     * Tests whether the standard tool tip can be queried by name.
     */
    @Test
    public void testGetToolTipName()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(TIP);
        EasyMock.replay(handler);
        compData.addComponentExpectation(COMP_NAME, COMP);
        compData.addExpectation(COMP, handler);
        assertEquals("Wrong tool tip", TIP, manager.getToolTip(COMP_NAME));
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tests whether tool tips queried by name are cached.
     */
    @Test
    public void testGetToolTipNameCached()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(TIP);
        EasyMock.replay(handler);
        compData.addComponentExpectation(COMP_NAME, COMP);
        compData.addExpectation(COMP, handler);
        compData.addComponentExpectation(COMP_NAME, COMP);
        assertEquals("Wrong tool tip", TIP, manager.getToolTip(COMP_NAME));
        assertEquals("Wrong cached tip", TIP, manager.getToolTip(COMP_NAME));
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tests whether the standard tool tip for a component can be queried.
     */
    @Test
    public void testGetToolTipComp()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(TIP);
        EasyMock.replay(handler);
        compData.addExpectation(COMP, handler);
        assertEquals("Wrong tool tip", TIP, manager.getToolTip(COMP));
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tests whether the tool tip information is cached after it was fetched for
     * a component.
     */
    @Test
    public void testGetToolTipCompCached()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(TIP);
        EasyMock.replay(handler);
        compData.addExpectation(COMP, handler);
        compData.addComponentExpectation(COMP_NAME, COMP);
        assertEquals("Wrong tool tip", TIP, manager.getToolTip(COMP));
        assertEquals("Wrong tool tip (2)", TIP, manager.getToolTip(COMP));
        assertEquals("Wrong tool tip for name", TIP, manager
                .getToolTip(COMP_NAME));
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tries to query the additional tool tip for an unknown name. This should
     * cause an exception.
     */
    @Test
    public void testGetAdditionalToolTipNameUnknown()
    {
        compData.addComponentExpectation(COMP_NAME, null);
        try
        {
            manager.getAdditionalToolTip(COMP_NAME);
            fail("Unknown name not detected!");
        }
        catch (GUIRuntimeException gex)
        {
            compData.verify();
        }
    }

    /**
     * Tests whether the additional tool tip can be queried by name if it is
     * undefined.
     */
    @Test
    public void testGetAdditionalToolTipNameUndefined()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(TIP);
        EasyMock.replay(handler);
        compData.addComponentExpectation(COMP_NAME, COMP);
        compData.addExpectation(COMP, handler);
        assertNull("Got an additional tip", manager
                .getAdditionalToolTip(COMP_NAME));
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tests whether the additional tool tip can be queried by component if it
     * is undefined.
     */
    @Test
    public void testGetAdditionalToolTipCompUndefined()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(TIP);
        EasyMock.replay(handler);
        compData.addExpectation(COMP, handler);
        assertNull("Got an additional tip", manager.getAdditionalToolTip(COMP));
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tries to set the tool tip for an unknown component name. This should
     * cause an exception.
     */
    @Test
    public void testSetToolTipNameUnknown()
    {
        compData.addComponentExpectation(COMP_NAME, null);
        try
        {
            manager.setToolTip(COMP_NAME, TIP);
            fail("Unknown name not detected!");
        }
        catch (GUIRuntimeException gex)
        {
            compData.verify();
        }
    }

    /**
     * Tests whether the tool tip can be set for a component specified by name.
     */
    @Test
    public void testSetToolTipName()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(null);
        handler.setToolTip(TIP);
        EasyMock.replay(handler);
        compData.addComponentExpectation(COMP_NAME, COMP);
        compData.addExpectation(COMP, handler);
        compData.addExpectation(COMP, handler);
        manager.setToolTip(COMP_NAME, TIP);
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tests whether the tool tip can be set for a component.
     */
    @Test
    public void testSetToolTipComp()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(null);
        handler.setToolTip(TIP);
        EasyMock.replay(handler);
        compData.addExpectation(COMP, handler);
        compData.addExpectation(COMP, handler);
        manager.setToolTip(COMP, TIP);
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tests whether the correct tool tip is returned after it has been set.
     */
    @Test
    public void testGetToolTipAfterSet()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(null);
        handler.setToolTip(TIP);
        EasyMock.replay(handler);
        compData.addExpectation(COMP, handler);
        compData.addExpectation(COMP, handler);
        manager.setToolTip(COMP, TIP);
        assertEquals("Tool tip not set", TIP, manager.getToolTip(COMP));
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tries to set the additional tool tip for an unknown component name. This
     * should cause an exception.
     */
    @Test
    public void testSetAdditionalToolTipNameUnknown()
    {
        compData.addComponentExpectation(COMP_NAME, null);
        try
        {
            manager.setAdditionalToolTip(COMP_NAME, ADD_TIP);
            fail("Unknown name not detected!");
        }
        catch (GUIRuntimeException gex)
        {
            compData.verify();
        }
    }

    /**
     * Tests whether the additional tool tip can be set for a component
     * specified by its name if no standard tool tip is set.
     */
    @Test
    public void testSetAdditionalToolTipNameNoStdTip()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(null);
        handler.setToolTip(ADD_TIP);
        EasyMock.replay(handler);
        compData.addComponentExpectation(COMP_NAME, COMP);
        compData.addExpectation(COMP, handler);
        compData.addExpectation(COMP, handler);
        manager.setAdditionalToolTip(COMP_NAME, ADD_TIP);
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tests whether the additional tool tip can be set for a component if no
     * standard tool tip is set.
     */
    @Test
    public void testSetAdditionalToolTipCompNoStdTip()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(null);
        handler.setToolTip(ADD_TIP);
        EasyMock.replay(handler);
        compData.addExpectation(COMP, handler);
        compData.addExpectation(COMP, handler);
        manager.setAdditionalToolTip(COMP, ADD_TIP);
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tests whether the additional tool tip can be queried after it has been
     * set.
     */
    @Test
    public void testGetAdditionalToolTipAfterSet()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(null);
        handler.setToolTip(ADD_TIP);
        EasyMock.replay(handler);
        compData.addExpectation(COMP, handler);
        compData.addExpectation(COMP, handler);
        manager.setAdditionalToolTip(COMP, ADD_TIP);
        assertEquals("Additional tip not set", ADD_TIP, manager
                .getAdditionalToolTip(COMP));
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tests whether the standard and the additional tool tip are combined to a
     * common tip.
     */
    @Test
    public void testSetAdditionalToolTipCompWithStdTip()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(TIP);
        handler.setToolTip(COMBINED_TIP);
        EasyMock.replay(handler);
        compData.addExpectation(COMP, handler);
        compData.addExpectation(COMP, handler);
        manager.setAdditionalToolTip(COMP, ADD_TIP);
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tests whether both the additional and the standard tip can be set and
     * later reset.
     */
    @Test
    public void testSetAndResetTip()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(null);
        handler.setToolTip(ADD_TIP);
        handler.setToolTip(COMBINED_TIP);
        handler.setToolTip(TIP);
        EasyMock.replay(handler);
        for (int i = 0; i < 4; i++)
        {
            compData.addExpectation(COMP, handler);
        }
        manager.setAdditionalToolTip(COMP, ADD_TIP);
        manager.setToolTip(COMP, TIP);
        manager.setAdditionalToolTip(COMP, null);
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * Tests whether both components of the tool tip can be set to null.
     */
    @Test
    public void testSetToolTipNull()
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(handler.getToolTip()).andReturn(TIP);
        handler.setToolTip(COMBINED_TIP);
        handler.setToolTip(TIP);
        handler.setToolTip(null);
        EasyMock.replay(handler);
        for (int i = 0; i < 4; i++)
        {
            compData.addExpectation(COMP, handler);
        }
        manager.setAdditionalToolTip(COMP, ADD_TIP);
        manager.setAdditionalToolTip(COMP, null);
        manager.setToolTip(COMP, null);
        compData.verify();
        EasyMock.verify(handler);
    }

    /**
     * A helper class for storing an expectation about a widget. This class
     * stores either the name or the component for which a widget handler is
     * expected to be queried. The widget handler to be returned is also stored.
     */
    private static class WidgetExpectation
    {
        /** The component. */
        private final Object component;

        /** The name of the component. */
        private final String componentName;

        /** The widget handler to return. */
        private final WidgetHandler handler;

        /**
         * Creates a new instance of {@code WidgetExpectation} and initializes
         * it.
         *
         * @param comp the component
         * @param name the name of the component
         * @param h the widget handler
         */
        public WidgetExpectation(Object comp, String name, WidgetHandler h)
        {
            component = comp;
            componentName = name;
            handler = h;
        }

        /**
         * Processes a query for a widget handler for the specified component.
         * Throws an exception if a different component is passed.
         *
         * @param comp the component
         * @return the widget handler
         */
        public WidgetHandler queryHandler(Object comp)
        {
            assertEquals("Wrong component", component, comp);
            return handler;
        }

        /**
         * Processes a query for a component for the specified component name.
         * Throws an exception if a different name is passed.
         *
         * @param name the component name
         * @return the component
         */
        public Object queryComponent(String name)
        {
            assertEquals("Wrong component name", componentName, name);
            return component;
        }
    }

    /**
     * A test implementation of ComponentBuilderData that allows specifying
     * expectations about the widget handlers to be queried.
     */
    private static class ComponentBuilderDataTestImpl extends
            ComponentBuilderData
    {
        /** The list with the expectations. */
        private final LinkedList<WidgetExpectation> expectations = new LinkedList<WidgetExpectation>();

        /**
         * Adds an expectation for the specified component that should return
         * the given handler.
         *
         * @param comp the expected component
         * @param handler the widget handler
         */
        public void addExpectation(Object comp, WidgetHandler handler)
        {
            expectations.add(new WidgetExpectation(comp, null, handler));
        }

        /**
         * Adds an expectation for the specified component name that should
         * return the given component.
         *
         * @param name the expected component name
         * @param comp the corresponding component
         */
        public void addComponentExpectation(String name, Object comp)
        {
            expectations.add(new WidgetExpectation(comp, name, null));
        }

        /**
         * Returns the widget handler for the given component. Checks whether a
         * corresponding expectation exists.
         */
        @Override
        public WidgetHandler getWidgetHandlerForComponent(Object component)
        {
            return nextExpectation().queryHandler(component);
        }

        /**
         * Returns the component for the given name. Checks whether a
         * corresponding expectation exists.
         */
        @Override
        public Object getComponent(String name)
        {
            return nextExpectation().queryComponent(name);
        }

        /**
         * Returns the next expectation.
         *
         * @return the next expectation
         */
        private WidgetExpectation nextExpectation()
        {
            assertFalse("Too many queries for widget handlers", expectations
                    .isEmpty());
            return expectations.removeFirst();
        }

        /**
         * Verifies whether all expectations have been processed.
         */
        public void verify()
        {
            assertTrue("Remaining expectations: " + expectations, expectations
                    .isEmpty());
        }
    }
}

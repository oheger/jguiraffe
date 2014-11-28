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
package net.sf.jguiraffe.gui.platform.swing.layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.gui.layout.ButtonLayout;
import net.sf.jguiraffe.gui.layout.CellConstraints;
import net.sf.jguiraffe.gui.layout.CellGroup;
import net.sf.jguiraffe.gui.layout.PercentLayoutBase;
import net.sf.jguiraffe.gui.layout.PercentLayoutPlatformAdapter;
import net.sf.jguiraffe.gui.layout.UnitSizeHandler;

import org.junit.Test;

/**
 * Test class for {@code SwingPercentLayoutAdapter}.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingPercentLayoutAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingPercentLayoutAdapter
{
    /** Constant for the number of columns in the test layout. */
    private static final int COL_COUNT = 4;

    /** Constant for the number of rows in the test layout. */
    private static final int ROW_COUNT = 3;

    /** Constant for the container width. */
    private static final int WIDTH = 320;

    /** Constant for the container height. */
    private static final int HEIGHT = 200;

    /** Constant for the preferred width. */
    private static final int PREF_WIDTH = 400;

    /** Constant for the preferred height. */
    private static final int PREF_HEIGHT = 222;

    /** Constant for the insets in X direction. */
    private static final int INSETS_X = 20;

    /** Constant for the insets in Y direction. */
    private static final int INSETS_Y = 10;

    /** Constant for the test insets. */
    private static final Insets INSETS = new Insets(INSETS_Y / 2 - 1,
            INSETS_X / 2 + 1, INSETS_Y / 2 + 1, INSETS_X / 2 - 1);

    /** Constant for the number of test components. */
    private static final int COMPONENT_COUNT = 12;

    /** Stores the percent layout base object. */
    private PercentLayoutBaseTestImpl percentLayout;

    /**
     * Creates a test layout adapter. The corresponding percent layout base
     * object is created, too.
     *
     * @return the adapter
     */
    private SwingPercentLayoutAdapter setUpAdapter()
    {
        percentLayout = new PercentLayoutBaseTestImpl();
        return new SwingPercentLayoutAdapter(percentLayout);
    }

    /**
     * Creates a test container. It is assigned to the percent layout base
     * object (which must have been created before).
     */
    @SuppressWarnings("serial")
    private void setUpContainer()
    {
        percentLayout.container = new JPanel()
        {
            @Override
            public Insets getInsets()
            {
                return INSETS;
            }
        };
        percentLayout.container.setSize(WIDTH, HEIGHT);
    }

    /**
     * Tries to create an instance without a layout. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoLayout()
    {
        new SwingPercentLayoutAdapter(null);
    }

    /**
     * Tests whether the correct layout object is returned.
     */
    @Test
    public void testGetPercentLayout()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        assertEquals("Wrong layout", percentLayout, adapter.getPercentLayout());
    }

    /**
     * Tests whether the layout is correctly calculated.
     */
    @Test
    public void testLayoutContainer()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        setUpContainer();
        adapter.layoutContainer(percentLayout.container);
        assertEquals("Wrong performLayout() calls", 1,
                percentLayout.performLayoutCalls);
    }

    /**
     * Tests whether the correct minimum layout size is calculated.
     */
    @Test
    public void testMinimumLayoutSize()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        setUpContainer();
        Dimension d = adapter.minimumLayoutSize(percentLayout.container);
        assertEquals("Wrong width", WIDTH + INSETS_X, d.width);
        assertEquals("Wrong height", HEIGHT + INSETS_Y, d.height);
    }

    /**
     * Tests whether the correct preferred layout size is calculated.
     */
    @Test
    public void testPreferredLayoutSize()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        setUpContainer();
        Dimension d = adapter.preferredLayoutSize(percentLayout.container);
        assertEquals("Wrong width", PREF_WIDTH + INSETS_X, d.width);
        assertEquals("Wrong height", PREF_HEIGHT + INSETS_Y, d.height);
    }

    /**
     * Tests the maximum layout size. There is no limitation for the maximum
     * size.
     */
    @Test
    public void testMaximumLayoutSize()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        setUpContainer();
        Dimension d = adapter.maximumLayoutSize(percentLayout.container);
        assertEquals("Wrong width", Integer.MAX_VALUE, d.width);
        assertEquals("Wrong height", Integer.MAX_VALUE, d.height);
    }

    /**
     * Tests the layout alignment in X direction.
     */
    @Test
    public void testGetLayoutAlignmentX()
    {
        assertEquals("Wrong alignment", 0.0, setUpAdapter()
                .getLayoutAlignmentX(null), 0.001);
    }

    /**
     * Tests the layout alignment in Y direction.
     */
    @Test
    public void testGetLayoutAlignmentY()
    {
        assertEquals("Wrong alignment", 0.0, setUpAdapter()
                .getLayoutAlignmentY(null), 0.001);
    }

    /**
     * Tests whether the layout is correctly invalidated.
     */
    @Test
    public void testInvalidateLayout()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        adapter.invalidateLayout(null);
        assertEquals("Wrong flushCache() calls", 1,
                percentLayout.flushCacheCalls);
    }

    /**
     * Helper method for checking whether components can be added to the layout.
     * Both variants of the addLayoutComponent() method can be tested.
     *
     * @param withName flag whether the component name should be used
     */
    private void checkAddLayoutComponent(boolean withName)
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        final Component comp = new JLabel();
        final Object constr = "TestConstraints";
        if (withName)
        {
            adapter.addLayoutComponent(constr.toString(), comp);
        }
        else
        {
            adapter.addLayoutComponent(comp, constr);
        }
        assertEquals("Wrong number of components", 1, adapter
                .getComponentCount());
        assertEquals("Wrong component", comp, adapter.getComponent(0));
        assertEquals("Wrong constraints", constr, adapter.getConstraints(0));
        assertEquals("Wrong flushCache() calls", 1,
                percentLayout.flushCacheCalls);
    }

    /**
     * Tests whether components can be added to the layout.
     */
    @Test
    public void testAddLayoutComponent()
    {
        checkAddLayoutComponent(false);
    }

    /**
     * Tests whether components can be added to the layout if the name is
     * specified.
     */
    @Test
    public void testAddLayoutComponentName()
    {
        checkAddLayoutComponent(true);
    }

    /**
     * Tests whether components can be removed from the layout.
     */
    @Test
    public void testRemoveLayoutComponent()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        final Component comp = new JLabel();
        adapter.addLayoutComponent(comp, "TestConstraints");
        adapter.removeLayoutComponent(comp);
        assertEquals("Wrong component to remove", comp,
                percentLayout.compToRemove);
        assertEquals("Wrong number of components", 0, adapter
                .getComponentCount());
    }

    /**
     * Tests the behavior of removeLayoutComponent() if the component to remove
     * cannot be found.
     */
    @Test
    public void testRemoveLayoutComponentNonExisting()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        adapter.addLayoutComponent(new JLabel(), null);
        adapter.removeLayoutComponent(new JLabel("Another component"));
        assertNull("Component was removed", percentLayout.compToRemove);
        assertEquals("Wrong number of components", 1, adapter
                .getComponentCount());
    }

    /**
     * Tests whether a correct size handler is returned.
     */
    @Test
    public void testGetSizeHandler()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        UnitSizeHandler ush = adapter.getSizeHandler();
        assertTrue("Wrong size handler: " + ush,
                ush instanceof SwingSizeHandler);
        assertSame("Multiple size handlers", ush, adapter.getSizeHandler());
    }

    /**
     * Adds a number of test components to a test layout.
     *
     * @param comps the collection to be filled with the components
     * @param constr the collection to be filled with the constraints
     * @return the layout adapter
     */
    private SwingPercentLayoutAdapter addTestComponents(
            Collection<Component> comps, Collection<Object> constr)
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        Collection<Component> colComp = (comps != null) ? comps
                : new ArrayList<Component>(COMPONENT_COUNT);
        Collection<Object> colConstr = (constr != null) ? constr
                : new ArrayList<Object>(COMPONENT_COUNT);
        for (int i = 0; i < COMPONENT_COUNT; i++)
        {
            Component c = new JLabel("Component" + i);
            Object constraints = (i % 2 == 0) ? "Constraints" + i : null;
            colComp.add(c);
            colConstr.add(constraints);
            adapter.addLayoutComponent(c, constraints);
        }
        assertEquals("Wrong number of components", COMPONENT_COUNT, adapter
                .getComponentCount());
        return adapter;
    }

    /**
     * Tests whether the components can be queried.
     */
    @Test
    public void testGetCompponent()
    {
        List<Component> comps = new ArrayList<Component>(COMPONENT_COUNT);
        SwingPercentLayoutAdapter adapter = addTestComponents(comps, null);
        for (int i = 0; i < COMPONENT_COUNT; i++)
        {
            assertEquals("Wrong component at " + i, comps.get(i), adapter
                    .getComponent(i));
        }
    }

    /**
     * Tests whether the constraints can be queried. This also tests support for
     * null constraints.
     */
    @Test
    public void testGetConstraints()
    {
        List<Object> constr = new ArrayList<Object>(COMPONENT_COUNT);
        SwingPercentLayoutAdapter adapter = addTestComponents(null, constr);
        for (int i = 0; i < COMPONENT_COUNT; i++)
        {
            assertEquals("Wrong constraints at " + i, constr.get(i), adapter
                    .getConstraints(i));
        }
    }

    /**
     * Creates a component with the minimum and preferred sizes initialized. The
     * minimum is determined by the INSETS_ constants. The preferred size is the
     * same multiplied by 2.
     *
     * @return the component
     */
    private Component setUpSizeComponent()
    {
        JLabel lab = new JLabel("Test");
        lab.setMinimumSize(new Dimension(INSETS_X, INSETS_Y));
        lab.setPreferredSize(new Dimension(2 * INSETS_X, 2 * INSETS_Y));
        return lab;
    }

    /**
     * Tests whether the horizontal minimum size of a component can be queried.
     */
    @Test
    public void testGetMinimumComponentSizeHor()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        assertEquals("Wrong size", INSETS_X, adapter.getMinimumComponentSize(
                setUpSizeComponent(), false));
    }

    /**
     * Tests whether the vertical minimum size of a component can be queried.
     */
    @Test
    public void testGetMinimumComponentSizeVert()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        assertEquals("Wrong size", INSETS_Y, adapter.getMinimumComponentSize(
                setUpSizeComponent(), true));
    }

    /**
     * Tests whether the horizontal preferred size of a component can be
     * queried.
     */
    @Test
    public void testGetPreferredComponentSizeHor()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        assertEquals("Wrong size", 2 * INSETS_X, adapter
                .getPreferredComponentSize(setUpSizeComponent(), false));
    }

    /**
     * Tests whether the vertical preferred size of a component can be queried.
     */
    @Test
    public void testGetPreferredComponentSizeVert()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        assertEquals("Wrong size", 2 * INSETS_Y, adapter
                .getPreferredComponentSize(setUpSizeComponent(), true));
    }

    /**
     * Tests whether the bounds of a component can be set.
     */
    @Test
    public void testSetComponentBounds()
    {
        SwingPercentLayoutAdapter adapter = setUpAdapter();
        JLabel lab = new JLabel();
        Rectangle rect = new Rectangle(INSETS.left, INSETS.top, WIDTH, HEIGHT);
        adapter.setComponentBounds(lab, rect);
        assertEquals("Wrong bounds", rect, lab.getBounds());
    }

    /**
     * Tests whether an adapter can be serialized.
     */
    @Test
    public void testSerialize() throws IOException
    {
        SwingPercentLayoutAdapter adapter = new SwingPercentLayoutAdapter(
                new ButtonLayout());
        SwingPercentLayoutAdapter adapter2 = JGuiraffeTestHelper
                .serialize(adapter);
        assertTrue("Wrong layout",
                adapter2.getPercentLayout() instanceof ButtonLayout);
    }

    /**
     * A mock implementation of {@code PercentLayoutBase} for testing the
     * interaction between the layout adapter and the layout object.
     *
     * @author Oliver Heger
     * @version $Id: TestSwingPercentLayoutAdapter.java 205 2012-01-29 18:29:57Z oheger $
     */
    @SuppressWarnings("serial")
    private static class PercentLayoutBaseTestImpl extends PercentLayoutBase
    {
        /** The test container. */
        Container container;

        /** The number of invocations of performLayout(). */
        int performLayoutCalls;

        /** The number of flushCache() invocations. */
        int flushCacheCalls;

        /** The component passed to removeComponent(). */
        Object compToRemove;

        public PercentLayoutBaseTestImpl()
        {
            super(COL_COUNT, ROW_COUNT);
            addColumnGroup(new CellGroup(1, 3));
            addRowGroup(new CellGroup(0, 2));
        }

        @Override
        protected void initCells(PercentLayoutPlatformAdapter adapter)
        {
            clearCells();
        }

        /**
         * Checks whether the expected values are passed in and returns an array
         * with test sizes.
         */
        @Override
        public int[] calcSizes(CellConstraints[] constraints, int count,
                Collection<CellGroup> cellGroups, Object container,
                int containerSize, boolean vert)
        {
            if (vert)
            {
                assertTrue("Wrong row constraints", Arrays.equals(
                        getAllRowConstraints(), constraints));
                assertEquals("Wrong col count", getColumnCount(), count);
                assertTrue("Wrong row groups", JGuiraffeTestHelper
                        .collectionEquals(getRowGroups(), cellGroups));
            }
            else
            {
                assertTrue("Wrong col constraints", Arrays.equals(
                        getAllColumnConstraints(), constraints));
                assertEquals("Wrong row count", getRowCount(), count);
                assertTrue("Wrong col groups", JGuiraffeTestHelper
                        .collectionEquals(getColumnGroups(), cellGroups));
            }
            assertEquals("Wrong container", this.container, container);
            return createSizes(1, vert);
        }

        /**
         * Checks the given parameters and returns test positions.
         */
        @Override
        public int[] calcCellPositions(int[] sizes, int startPos)
        {
            boolean vert = sizes.length == getRowCount();
            checkSizes(sizes, 1, vert);
            int expectedStart = vert ? INSETS.top : INSETS.left;
            assertEquals("Wrong start position", expectedStart, startPos);
            return createSizes(3, vert);
        }

        /**
         * Checks the parameters and records this invocation.
         */
        @Override
        public void performLayout(Object container, Rectangle insets,
                Dimension size)
        {
            assertEquals("Wrong container", this.container, container);
            assertEquals("Wrong left insets", INSETS.left, insets.x);
            assertEquals("Wrong right insets", INSETS.right, insets.width);
            assertEquals("Wrong top insets", INSETS.top, insets.y);
            assertEquals("Wrong bottom insets", INSETS.bottom, insets.height);
            assertEquals("Wrong width", WIDTH, size.width);
            assertEquals("Wrong height", HEIGHT, size.height);
            performLayoutCalls++;
        }

        /**
         * Checks the parameters and returns test values.
         */
        @Override
        public Dimension calcMinimumLayoutSize(Object container)
        {
            assertEquals("Wrong container", this.container, container);
            return new Dimension(WIDTH, HEIGHT);
        }

        /**
         * Checks the parameters and returns test values.
         */
        @Override
        public Dimension calcPreferredLayoutSize(Object container)
        {
            assertEquals("Wrong container", this.container, container);
            return new Dimension(PREF_WIDTH, PREF_HEIGHT);
        }

        /**
         * Records this invocation.
         */
        @Override
        public void flushCache()
        {
            flushCacheCalls++;
        }

        /**
         * Records this invocation.
         */
        @Override
        public boolean removeComponent(Object comp)
        {
            compToRemove = comp;
            return true;
        }

        /**
         * Populates the given array with test values. Starting with the given
         * number the array is filled with increasing numbers.
         *
         * @param sizes the array with the test sizes
         * @param startValue the start number
         */
        private static void fillSizes(int[] sizes, int startValue)
        {
            for (int i = 0; i < sizes.length; i++)
            {
                sizes[i] = startValue + i;
            }
        }

        /**
         * Creates an array with test size values. The array starts with the
         * given number and simply uses increasing numbers. Its length depends
         * on the dimensions of the given direction - vertical or horizontal.
         *
         * @param startValue the start number
         * @param vert the vertical flag
         * @return the array with the test sizes
         */
        private int[] createSizes(int startValue, boolean vert)
        {
            int[] res = new int[vert ? getRowCount() : getColumnCount()];
            fillSizes(res, startValue);
            return res;
        }

        /**
         * Tests whether the array contains the expected values.
         *
         * @param sizes the array to test
         * @param startValue the expected start value
         * @param vert the vertical flag
         */
        private void checkSizes(int[] sizes, int startValue, boolean vert)
        {
            int[] expected = createSizes(startValue, vert);
            assertTrue("Wrong values: " + Arrays.toString(sizes) + " is not " + Arrays.toString(expected), Arrays.equals(expected, sizes));
        }
    }
}

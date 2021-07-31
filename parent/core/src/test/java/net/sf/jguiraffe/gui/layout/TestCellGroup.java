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
package net.sf.jguiraffe.gui.layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.junit.Test;

/**
 * Test class for CellGroup.
 *
 * @author Oliver Heger
 * @version $Id: TestCellGroup.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestCellGroup
{
    /**
     * Tests a cell group with only 2 elements.
     */
    @Test
    public void testApply2()
    {
        CellGroup group = new CellGroup(1, 3);
        checkGroup(group, 2);
    }

    /**
     * Tests a group with 3 elements.
     */
    @Test
    public void testApply3()
    {
        CellGroup group = new CellGroup(1, 3, 5);
        checkGroup(group, 3);
    }

    /**
     * Tests a group with 4 elements.
     */
    @Test
    public void testApply4()
    {
        CellGroup group = new CellGroup(1, 3, 5, 7);
        checkGroup(group, 4);
    }

    /**
     * Tests the constructor, which takes an array.
     */
    public void testApplyArray()
    {
        int[] indices = new int[] {
                1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21
        };
        CellGroup group = CellGroup.fromArray(indices);
        checkGroup(group, indices.length);
    }

    /**
     * Tests the fromArray() method if null is passed in. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromArrayNull()
    {
        CellGroup.fromArray(null);
    }

    /**
     * Tests fromArray() if the array contains too few elements. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromArrayTooFewElements()
    {
        CellGroup.fromArray(1);
    }

    /**
     * Tests whether fromArray() makes a defensive copy.
     */
    @Test
    public void testFromArrayModify()
    {
        int[] indices = new int[] {
                0, 1
        };
        CellGroup cg = CellGroup.fromArray(indices);
        indices[0] = -1;
        int[] values = new int[] {
                1, 2
        };
        cg.apply(values);
        assertEquals("Wrong size 1", 2, values[0]);
        assertEquals("Wrong size 2", 2, values[1]);
    }

    /**
     * Tests initialization of a cell group if an index is less than 0. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromArrayInvalidIndex()
    {
        CellGroup.fromArray(1, 2, 3, -1, 4);
    }

    /**
     * Tests parsing strings.
     */
    public void testInitString()
    {
        CellGroup group = CellGroup.fromString("1 3; 5,7/9 11 13,15,17");
        checkGroup(group, 9);
    }

    /**
     * Tries to parse a string that contains an invalid number. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStringInvalidNumber()
    {
        CellGroup.fromString("1 3 5 8 34.5");
    }

    /**
     * Tries to parse a null string. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStringNull()
    {
        CellGroup.fromString((String) null);
    }

    /**
     * Tries to parse an empty string. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStringEmpty()
    {
        CellGroup.fromString("");
    }

    /**
     * Tries to parse a string that contains only a single index. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStringSingleIndex()
    {
        CellGroup.fromString("1");
    }

    /**
     * Helper method for checking whether the apply() method really calculates
     * the maximum.
     *
     * @param size1 size 1
     * @param size2 size 2
     */
    private void checkApplyMax(int size1, int size2)
    {
        CellGroup cg = new CellGroup(0, 1);
        int[] sizes = new int[] {
                size1, size2
        };
        int max = Math.max(size1, size2);
        cg.apply(sizes);
        for (int i = 0; i < sizes.length; i++)
        {
            assertEquals("Wrong size at " + i, max, sizes[i]);
        }
    }

    /**
     * Tests the apply() method if index 1 is the maximum.
     */
    @Test
    public void testApplyIndex1Max()
    {
        checkApplyMax(10, 5);
    }

    /**
     * Tests the apply() method if index 2 is the maximum.
     */
    @Test
    public void testApplyIndex2Max()
    {
        checkApplyMax(20, 100);
    }

    /**
     * Tests apply() if a null array is provided. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testApplyNull()
    {
        CellGroup.fromArray(1, 2, 3).apply(null);
    }

    /**
     * Tests apply() if indices of the group are larger than the array size.
     * This should cause an exception.
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testApplyInvalidIndex()
    {
        CellGroup cg = new CellGroup(0, 10);
        cg.apply(new int[] {
                1, 2, 3
        });
    }

    /**
     * Tests buildString() if a null buffer is passed in. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuildStringNull()
    {
        new CellGroup(0, 1).buildString(null);
    }

    /**
     * Tests building a string representation.
     */
    @Test
    public void testToString()
    {
        int[] indices = new int[] {
                1, 4, 7, 12
        };
        CellGroup group = CellGroup.fromArray(indices);
        assertEquals("Wrong string", "CellGroup [ indices = "
                + Arrays.toString(indices) + " ]", group.toString());
    }

    /**
     * Tests equals() if the expected result is true.
     */
    @Test
    public void testEqualsTrue()
    {
        CellGroup cg = new CellGroup(1, 2, 3);
        JGuiraffeTestHelper.checkEquals(cg, cg, true);
        CellGroup cg2 = CellGroup.fromString("1,2,3");
        JGuiraffeTestHelper.checkEquals(cg, cg2, true);
        cg2 = CellGroup.fromArray(new int[] {
                3, 1, 2
        });
        JGuiraffeTestHelper.checkEquals(cg, cg2, true);
    }

    /**
     * Tests equals() if the expected result is false.
     */
    @Test
    public void testEqualsFalse()
    {
        CellGroup cg = new CellGroup(0, 1);
        CellGroup cg2 = new CellGroup(1, 2);
        JGuiraffeTestHelper.checkEquals(cg, cg2, false);
        cg2 = new CellGroup(0, 1, 2);
        JGuiraffeTestHelper.checkEquals(cg, cg2, false);
    }

    /**
     * Tests equals() with other objects.
     */
    @Test
    public void testEqualsTrivial()
    {
        CellGroup cg = new CellGroup(1, 2);
        JGuiraffeTestHelper.testTrivialEquals(cg);
    }

    /**
     * Helper method for checking a cell group object. This method creates an
     * array with cell sizes and checks if the cell group's <code>apply()</code>
     * method works properly. The cell group must have been initialized with the
     * given number of indices. The indices must be the first <code>count</code>
     * odd numbers.
     *
     * @param count the number of indices
     */
    private void checkGroup(CellGroup group, int count)
    {
        assertEquals("Wrong number of elements", count, group.groupSize());
        int[] sizes = new int[2 * count];
        for (int i = 0; i < sizes.length; i++)
        {
            int idx = (count % 2 == 0) ? i : sizes.length - 1 - i;
            sizes[idx] = i * 10;
        }
        group.apply(sizes);
        int expected = (2 * count - count % 2 - 1) * 10;
        for (int i = 1; i < sizes.length; i += 2)
        {
            assertEquals("Sizes not equal at " + i, expected, sizes[i]);
            assertFalse("Sizes were changed: " + i, expected == sizes[i - 1]);
        }
    }
}

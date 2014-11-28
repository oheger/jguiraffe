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
package net.sf.jguiraffe.gui.layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link PercentData} and its builder class.
 *
 * @author Oliver Heger
 * @version $Id: TestPercentData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestPercentData
{
    /** Constant for the x position. */
    private static final int XPOS = 2;

    /** Constant for the y position. */
    private static final int YPOS = 4;

    /** The builder for PercentData objects. */
    private PercentData.Builder builder;

    @Before
    public void setUp() throws Exception
    {
        builder = new PercentData.Builder();
    }

    /**
     * Helper method for testing default values.
     *
     * @param pd the data object to be checked
     */
    private void checkDefaults(PercentData pd)
    {
        assertEquals("Wrong x", XPOS, pd.getColumn());
        assertEquals("Wrong y", YPOS, pd.getRow());
        assertEquals("Wrong span X", 1, pd.getSpanX());
        assertEquals("Wrong span Y", 1, pd.getSpanY());
        assertEquals("Got a target column", PercentData.POS_UNDEF, pd
                .getTargetColumn());
        assertEquals("Got a target row", PercentData.POS_UNDEF, pd
                .getTargetRow());
        assertNull("Got column constraints", pd.getColumnConstraints());
        assertNull("Got row constraints", pd.getRowConstraints());
    }

    /**
     * Tries to create an instance without setting a position. This should cause
     * an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testCreateNoPos()
    {
        builder.span(1, 2).withTargetColumn(5).withTargetRow(4).create();
    }

    /**
     * Tests the default values for uninitialized properties.
     */
    @Test
    public void testCreateDefaults()
    {
        PercentData pd = builder.xy(XPOS, YPOS).create();
        checkDefaults(pd);
    }

    /**
     * Tries to pass an invalid X position. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testXYInvalidX()
    {
        builder.xy(-1, YPOS);
    }

    /**
     * Tries to pass an invalid Y position. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testXYInvalidY()
    {
        builder.xy(XPOS, -44);
    }

    /**
     * Tests whether the span is correctly set.
     */
    @Test
    public void testCreateSpan()
    {
        PercentData pd = builder.xy(XPOS, YPOS).spanX(3).spanY(2).create();
        assertEquals("Wrong span X", 3, pd.getSpanX());
        assertEquals("Wrong span Y", 2, pd.getSpanY());
    }

    /**
     * Tests the convenience span() method that allows setting both values.
     */
    @Test
    public void testCreateSpanConvenience()
    {
        PercentData pd = builder.xy(XPOS, YPOS).span(3, 2).create();
        assertEquals("Wrong span X", 3, pd.getSpanX());
        assertEquals("Wrong span Y", 2, pd.getSpanY());
    }

    /**
     * Tries to set an invalid value for the x span. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSpanXInvalid()
    {
        builder.spanX(0);
    }

    /**
     * Tries to set an invalid value for the y span. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSpanYInvalid()
    {
        builder.spanY(-1);
    }

    /**
     * Tests whether a target column can be set.
     */
    @Test
    public void testCreateWithTargetColumn()
    {
        PercentData pd = builder.xy(XPOS, YPOS).withTargetColumn(XPOS).create();
        assertEquals("Wrong target column", XPOS, pd.getTargetColumn());
    }

    /**
     * Tests whether a target row can be set.
     */
    @Test
    public void testCreateWithTargetRow()
    {
        PercentData pd = builder.xy(XPOS, YPOS).withTargetRow(YPOS).create();
        assertEquals("Wrong target row", YPOS, pd.getTargetRow());
    }

    /**
     * Tries to set an invalid target column. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWithTargetColumnInvalid()
    {
        builder.withTargetColumn(-1);
    }

    /**
     * Tries to set an invalid target row. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWithTargetRowInvalid()
    {
        builder.withTargetRow(-1);
    }

    /**
     * Tests whether column constraints can be set.
     */
    @Test
    public void testCreateWithColumnConstraints()
    {
        CellConstraints cc = new CellConstraints.Builder().defaultColumn()
                .create();
        PercentData pd = builder.xy(XPOS, YPOS).withColumnConstraints(cc)
                .create();
        assertEquals("Wrong column constraints", cc, pd.getColumnConstraints());
    }

    /**
     * Tests whether row constraints can be set.
     */
    @Test
    public void testCreateWithRowConstraints()
    {
        CellConstraints cc = new CellConstraints.Builder().defaultRow()
                .create();
        PercentData pd = builder.xy(XPOS, YPOS).withRowConstraints(cc).create();
        assertEquals("Wrong row constraints", cc, pd.getRowConstraints());
    }

    /**
     * Tests whether a create() is automatically followed by a reset().
     */
    @Test
    public void testCreateWithReset()
    {
        CellConstraints.Builder cb = new CellConstraints.Builder();
        builder.xy(12, 10).span(2, 3).withTargetColumn(13).withTargetRow(11)
                .withColumnConstraints(cb.defaultColumn().create())
                .withRowConstraints(cb.defaultRow().create()).create();
        PercentData pd = builder.xy(XPOS, YPOS).create();
        checkDefaults(pd);
    }

    /**
     * Tests whether reset() also clears the position.
     */
    @Test(expected = IllegalStateException.class)
    public void testResetPos()
    {
        builder.xy(XPOS, YPOS);
        builder.reset();
        builder.create();
    }

    /**
     * Tests the pos() convenience method.
     */
    @Test
    public void testPos()
    {
        PercentData pd = builder.pos(XPOS, YPOS);
        checkDefaults(pd);
    }

    /**
     * Tests the pos() method if properties have been set before.
     */
    @Test
    public void testPosWithProperties()
    {
        PercentData pd = builder.spanX(2).pos(XPOS, YPOS);
        assertEquals("Wrong spanX", 2, pd.getSpanX());
    }

    /**
     * Tests the equals() method if the expected result is true.
     */
    @Test
    public void testEqualsTrue()
    {
        CellConstraints.Builder cb = new CellConstraints.Builder();
        PercentData pd1 = builder.pos(XPOS, YPOS);
        JGuiraffeTestHelper.checkEquals(pd1, pd1, true);
        PercentData pd2 = builder.xy(XPOS, YPOS).create();
        JGuiraffeTestHelper.checkEquals(pd1, pd2, true);
        pd1 = builder.xy(XPOS, YPOS).span(2, 3).create();
        pd2 = builder.span(2, 3).xy(XPOS, YPOS).create();
        JGuiraffeTestHelper.checkEquals(pd1, pd2, true);
        pd1 = builder.xy(XPOS, YPOS).withTargetColumn(2).withTargetRow(3)
                .create();
        pd2 = builder.withTargetRow(3).xy(XPOS, YPOS).withTargetColumn(2)
                .create();
        JGuiraffeTestHelper.checkEquals(pd1, pd2, true);
        pd1 = builder.xy(XPOS, YPOS).span(2, 3).withColumnConstraints(
                cb.defaultColumn().create()).withRowConstraints(
                cb.defaultRow().create()).withTargetColumn(2).withTargetRow(3)
                .create();
        pd2 = builder.xy(XPOS, YPOS).span(2, 3).withColumnConstraints(
                cb.defaultColumn().create()).withRowConstraints(
                cb.defaultRow().create()).withTargetColumn(2).withTargetRow(3)
                .create();
        JGuiraffeTestHelper.checkEquals(pd1, pd2, true);
    }

    /**
     * Tests the equals() method if the expected result is false.
     */
    @Test
    public void testEqualsFalse()
    {
        CellConstraints.Builder cb = new CellConstraints.Builder();
        PercentData pd1 = builder.pos(XPOS, YPOS);
        PercentData pd2 = builder.xy(XPOS + 1, YPOS).create();
        PercentData pos = builder.pos(XPOS, YPOS);
        JGuiraffeTestHelper.checkEquals(pd1, pd2, false);
        pd2 = builder.xy(XPOS, YPOS + 1).create();
        JGuiraffeTestHelper.checkEquals(pd1, pd2, false);
        pd1 = builder.xy(XPOS, YPOS).spanX(2).create();
        JGuiraffeTestHelper.checkEquals(pd1, pd2, false);
        pd2 = builder.xy(XPOS, YPOS).spanX(3).create();
        JGuiraffeTestHelper.checkEquals(pd1, pd2, false);
        pd1 = builder.xy(XPOS, YPOS).spanY(2).create();
        JGuiraffeTestHelper.checkEquals(pd1, pos, false);
        pd2 = builder.xy(XPOS, YPOS).spanY(3).create();
        JGuiraffeTestHelper.checkEquals(pd1, pd2, false);
        pd1 = builder.xy(XPOS, YPOS).withTargetColumn(1).create();
        JGuiraffeTestHelper.checkEquals(pd1, pos, false);
        pd2 = builder.xy(XPOS, YPOS).withTargetColumn(2).create();
        JGuiraffeTestHelper.checkEquals(pd1, pd2, false);
        pd1 = builder.xy(XPOS, YPOS).withTargetRow(1).create();
        JGuiraffeTestHelper.checkEquals(pd1, pos, false);
        pd2 = builder.xy(XPOS, YPOS).withTargetRow(2).create();
        JGuiraffeTestHelper.checkEquals(pd1, pd2, false);
        pd1 = builder.xy(XPOS, YPOS).withColumnConstraints(
                cb.defaultColumn().create()).create();
        JGuiraffeTestHelper.checkEquals(pd1, pos, false);
        pd2 = builder.xy(XPOS, YPOS).withColumnConstraints(
                cb.defaultRow().create()).create();
        JGuiraffeTestHelper.checkEquals(pd1, pd2, false);
        pd1 = builder.xy(XPOS, YPOS).withRowConstraints(
                cb.defaultRow().create()).create();
        JGuiraffeTestHelper.checkEquals(pd1, pos, false);
        pd2 = builder.xy(XPOS, YPOS).withRowConstraints(
                cb.defaultColumn().create()).create();
        JGuiraffeTestHelper.checkEquals(pd1, pd2, false);
    }

    /**
     * Tests equals() with other objects.
     */
    @Test
    public void testEqualsTrivial()
    {
        PercentData pd = builder.pos(XPOS, YPOS);
        JGuiraffeTestHelper.testTrivialEquals(pd);
    }

    /**
     * Tests the string representation if all properties are defined.
     */
    @Test
    public void testToString()
    {
        CellConstraints.Builder cb = new CellConstraints.Builder();
        CellConstraints cccol = cb.defaultColumn().create();
        CellConstraints ccrow = cb.defaultRow().create();
        PercentData pd = builder.xy(XPOS, YPOS).span(2, 1).withTargetColumn(2)
                .withTargetRow(3).withColumnConstraints(cccol)
                .withRowConstraints(ccrow).create();
        String s = pd.toString();
        assertTrue("No column: " + s, s.indexOf("COL = " + XPOS) > 0);
        assertTrue("No row: " + s, s.indexOf("ROW = " + YPOS) > 0);
        assertTrue("No span x: " + s, s.indexOf("SPANX = " + 2) > 0);
        assertTrue("No span y: " + s, s.indexOf("SPANY = " + 1) > 0);
        assertTrue("No target col: " + s, s.indexOf("TARGETCOL = " + 2) > 0);
        assertTrue("No target row: " + s, s.indexOf("TARGETROW = " + 3) > 0);
        assertTrue("No colconstr: " + s, s.indexOf("COLCONSTR = "
                + cccol.toSpecificationString()) > 0);
        assertTrue("No rowconstr: " + s, s.indexOf("ROWCONSTR = "
                + ccrow.toSpecificationString()) > 0);
    }

    /**
     * Tests the string representation if the optional properties are missing.
     */
    @Test
    public void testBuildString()
    {
        PercentData pd = builder.pos(XPOS, YPOS);
        StringBuilder buf = new StringBuilder();
        pd.buildString(buf);
        assertEquals("Wrong string", "COL = " + XPOS + " ROW = " + YPOS
                + " SPANX = 1 SPANY = 1", buf.toString());
    }

    /**
     * Tests buildString() for null input. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuildStringNull()
    {
        PercentData pd = builder.pos(XPOS, YPOS);
        pd.buildString(null);
    }
}

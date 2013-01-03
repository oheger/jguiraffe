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
package net.sf.jguiraffe.gui.layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link CellConstraints} and its nested builder class.
 *
 * @author Oliver Heger
 * @version $Id: TestCellConstraints.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestCellConstraints
{
    /** Constant for a test minimum size. */
    private static final NumberWithUnit MINSIZE = new NumberWithUnit(10,
            Unit.DLU);

    /** Constant for a test weight factor. */
    private static final int WEIGHT = 35;

    /** Constant for a test specification string. */
    private static final String SPEC = "CENTER/PREFERRED("
            + MINSIZE.toUnitString() + ")/" + WEIGHT;

    /** The builder instance. */
    private CellConstraints.Builder builder;

    @Before
    public void setUp() throws Exception
    {
        builder = new CellConstraints.Builder();
    }

    /**
     * Helper method for checking a constraints instance.
     *
     * @param cc the instance to check
     * @param expSz the expected size
     * @param expAl the expected alignment
     * @param expMinSz the expected minimum size
     * @param expWeight the expected weight
     */
    private void checkConstraints(CellConstraints cc, CellSize expSz,
            CellAlignment expAl, NumberWithUnit expMinSz, int expWeight)
    {
        assertEquals("Wrong cell size", expSz, cc.getCellSize());
        assertEquals("Wrong alignment", expAl, cc.getAlignment());
        assertEquals("Wrong minimum size", NumberWithUnit.nonNull(expMinSz), cc
                .getMinSize());
        assertEquals("Wrong weight factor", expWeight, cc.getWeight());
    }

    /**
     * Tests the builder if only the cell size is specified.
     */
    @Test
    public void testCreateSize()
    {
        CellConstraints cc = builder.withCellSize(CellSize.MINIMUM).create();
        checkConstraints(cc, CellSize.MINIMUM, CellAlignment.FULL, null, 0);
    }

    /**
     * Tests the builder if only a minimum size is specified.
     */
    @Test
    public void testCreateMinimumSize()
    {
        CellConstraints cc = builder.withMinimumSize(MINSIZE).create();
        checkConstraints(cc, CellSize.NONE, CellAlignment.FULL, MINSIZE, 0);
    }

    /**
     * Tests whether null can be passed to withMinimumSize().
     */
    @Test
    public void testCreateMinimumSizeNull()
    {
        CellConstraints cc = builder.withMinimumSize(null).create();
        checkConstraints(cc, CellSize.NONE, CellAlignment.FULL,
                NumberWithUnit.ZERO, 0);
    }

    /**
     * Tries to pass in a negative minimum size. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateMinimumSizeNegative()
    {
        builder.withMinimumSize(new NumberWithUnit(-10));
    }

    /**
     * Tries to pass null for the cell size. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateSizeNull()
    {
        builder.withCellSize(null);
    }

    /**
     * Tries to create an instance without a size. This should cause an
     * exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testCreateNoSize()
    {
        builder.create();
    }

    /**
     * Tests whether the alignment can be specified.
     */
    @Test
    public void testCreateAlignment()
    {
        CellConstraints cc = builder.withCellSize(CellSize.PREFERRED)
                .withCellAlignment(CellAlignment.END).create();
        checkConstraints(cc, CellSize.PREFERRED, CellAlignment.END, null, 0);
    }

    /**
     * Tries to pass a null alignment. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateAlignmentNull()
    {
        builder.withCellAlignment(null);
    }

    /**
     * Tests whether the weight factor can be specified.
     */
    @Test
    public void testCreateWeight()
    {
        CellConstraints cc = builder.withCellSize(CellSize.PREFERRED)
                .withWeight(WEIGHT).create();
        checkConstraints(cc, CellSize.PREFERRED, CellAlignment.FULL, null,
                WEIGHT);
    }

    /**
     * Tries to pass a negative number as weight factor. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateWeightNegative()
    {
        builder.withWeight(-1);
    }

    /**
     * Tests the creation of a default column.
     */
    @Test
    public void testCreateDefaultColumn()
    {
        CellConstraints cc = builder.defaultColumn().create();
        checkConstraints(cc, CellSize.PREFERRED, CellAlignment.FULL, null, 0);
    }

    /**
     * Tests the creation of a default row.
     */
    @Test
    public void testCreateDefaultRow()
    {
        CellConstraints cc = builder.defaultRow().create();
        checkConstraints(cc, CellSize.PREFERRED, CellAlignment.CENTER, null, 0);
    }

    /**
     * Tries to set the default alignment to null. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetDefaultAlignmentNull()
    {
        builder.setDefaultAlignment(null);
    }

    /**
     * Tests whether the default alignment can be changed.
     */
    @Test
    public void testSetDefaultAlignment()
    {
        builder.setDefaultAlignment(CellAlignment.END);
        builder.reset();
        CellConstraints cc = builder.withCellSize(CellSize.MINIMUM).create();
        checkConstraints(cc, CellSize.MINIMUM, CellAlignment.END, null, 0);
    }

    /**
     * Tests the reset() method.
     */
    @Test
    public void testReset()
    {
        builder.withCellAlignment(CellAlignment.START).withMinimumSize(MINSIZE)
                .withWeight(WEIGHT);
        builder.reset();
        CellConstraints cc = builder.withCellSize(CellSize.NONE).create();
        checkConstraints(cc, CellSize.NONE, CellAlignment.FULL, null, 0);
    }

    /**
     * Tests whether the cell size is reset, too.
     */
    @Test(expected = IllegalStateException.class)
    public void testResetCellSize()
    {
        builder.withCellSize(CellSize.PREFERRED);
        builder.reset();
        builder.create();
    }

    /**
     * Tests that a reset is performed after create().
     */
    @Test
    public void testCreateReset()
    {
        CellConstraints cc = builder.withCellAlignment(CellAlignment.END)
                .withCellSize(CellSize.PREFERRED).withMinimumSize(MINSIZE)
                .withWeight(WEIGHT).create();
        checkConstraints(cc, CellSize.PREFERRED, CellAlignment.END, MINSIZE,
                WEIGHT);
        cc = builder.withCellSize(CellSize.MINIMUM).create();
        checkConstraints(cc, CellSize.MINIMUM, CellAlignment.FULL, null, 0);
    }

    /**
     * Tests that instances are cached by the builder.
     */
    @Test
    public void testCreateCached()
    {
        CellConstraints cc = builder.withCellAlignment(CellAlignment.END)
                .withCellSize(CellSize.PREFERRED).withMinimumSize(MINSIZE)
                .withWeight(WEIGHT).create();
        assertSame("Multiple instances", cc, builder.withCellAlignment(
                CellAlignment.END).withCellSize(CellSize.PREFERRED)
                .withMinimumSize(MINSIZE).withWeight(WEIGHT).create());
    }

    /**
     * Tests a string that only contains the cell size.
     */
    @Test
    public void testFromStringSize()
    {
        CellConstraints cc = builder.fromString("minimum");
        checkConstraints(cc, CellSize.MINIMUM, CellAlignment.FULL, null, 0);
    }

    /**
     * Tests a string that contains the cell size and the minimum size.
     */
    @Test
    public void testFromStringSizeAndMinSize()
    {
        CellConstraints cc = builder.fromString("PreFerreD(10)");
        checkConstraints(cc, CellSize.PREFERRED, CellAlignment.FULL,
                new NumberWithUnit(10), 0);
    }

    /**
     * Tests a string that contains only the minimum size.
     */
    @Test
    public void testFromStringMinSize()
    {
        CellConstraints cc = builder.fromString(MINSIZE.toUnitString());
        checkConstraints(cc, CellSize.NONE, CellAlignment.FULL, MINSIZE, 0);
    }

    /**
     * Tests a string that contains the size and the alignment.
     */
    @Test
    public void testFromStringAlignSize()
    {
        CellConstraints cc = builder.fromString(" Start / minimum ");
        checkConstraints(cc, CellSize.MINIMUM, CellAlignment.START, null, 0);
    }

    /**
     * Tests a string that contains the minimum size and the alignment.
     */
    @Test
    public void testFromStringAlignMinSize()
    {
        NumberWithUnit n = new NumberWithUnit(20, Unit.DLU);
        CellConstraints cc = builder.fromString("FULL/" + n.toUnitString());
        checkConstraints(cc, CellSize.NONE, CellAlignment.FULL, n, 0);
    }

    /**
     * Tests a string that contains the size, the minimum size, and the
     * alignment.
     */
    @Test
    public void testFromStringAlignSizeMinSize()
    {
        NumberWithUnit n = new NumberWithUnit(15, Unit.CM);
        CellConstraints cc = builder.fromString("end/preferred(15cm)");
        checkConstraints(cc, CellSize.PREFERRED, CellAlignment.END, n, 0);
    }

    /**
     * Tests a string that contains the size, the minimum size, and the weight.
     */
    @Test
    public void testFromStringSizeMinSizeWeight()
    {
        NumberWithUnit n = new NumberWithUnit(11, Unit.INCH);
        CellConstraints cc = builder.fromString("minimum(" + n.toUnitString()
                + ")/10");
        checkConstraints(cc, CellSize.MINIMUM, CellAlignment.FULL, n, 10);
    }

    /**
     * Tests a string that contains the size and the weight factor.
     */
    @Test
    public void testFromStringSizeWeight()
    {
        CellConstraints cc = builder.fromString("PREFERRED/5");
        checkConstraints(cc, CellSize.PREFERRED, CellAlignment.FULL, null, 5);
    }

    /**
     * Tests a string that contains the size, the alignment, and the weight.
     */
    @Test
    public void testFromStringSizeAlignmentWeight()
    {
        CellConstraints cc = builder.fromString("end/preferred/25");
        checkConstraints(cc, CellSize.PREFERRED, CellAlignment.END, null, 25);
    }

    /**
     * Tests a string that contains the minimum size, the alignment, and the
     * weight.
     */
    @Test
    public void testFromStringMinSizeAlignmentWeight()
    {
        NumberWithUnit n = new NumberWithUnit(10, Unit.DLU);
        CellConstraints cc = builder.fromString("END/" + n.toUnitString()
                + "/33");
        checkConstraints(cc, CellSize.NONE, CellAlignment.END, n, 33);
    }

    /**
     * Tests a string that contains the cell size, the minimum size, the
     * alignment, and the weight.
     */
    @Test
    public void testFromStringSizeMinSizeAlignmentWeight()
    {
        CellConstraints cc = builder.fromString("StarT / minimum("
                + MINSIZE.toUnitString() + ") / " + WEIGHT);
        checkConstraints(cc, CellSize.MINIMUM, CellAlignment.START, MINSIZE,
                WEIGHT);
    }

    /**
     * Tests whether the default alignment is taken into account by the
     * fromString() method.
     */
    @Test
    public void testFromStringDefaultAlignment()
    {
        builder.setDefaultAlignment(CellAlignment.END);
        CellConstraints cc = builder.fromString("preferred");
        checkConstraints(cc, CellSize.PREFERRED, CellAlignment.END, null, 0);
    }

    /**
     * Tests whether cell constraints instances are obtained from cache if
     * possible.
     */
    @Test
    public void testFromStringCached()
    {
        CellConstraints cc = builder.withCellAlignment(CellAlignment.CENTER)
                .withCellSize(CellSize.PREFERRED).withMinimumSize(MINSIZE)
                .withWeight(WEIGHT).create();
        assertSame("Instance not cached", cc, builder.fromString(SPEC));
    }

    /**
     * Tests whether fromString() also finds instances using the canonical
     * string.
     */
    @Test
    public void testFromStringCachedCanonical()
    {
        final String testSpec = " CenteR /  PREFerred ( "
                + MINSIZE.toUnitString() + " ) / " + WEIGHT;
        CellConstraints cc = builder.fromString(testSpec);
        assertSame("Instance not cached", cc, builder.fromString(SPEC));
    }

    /**
     * Tries to parse a null string. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringNull()
    {
        builder.fromString(null);
    }

    /**
     * Tries to parse an empty string. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringEmpty()
    {
        builder.fromString("");
    }

    /**
     * Tries to parse a string containing of a slash only. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringSlash()
    {
        builder.fromString("/");
    }

    /**
     * Tries to parse a string with an invalid size definition. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringInvalidSize()
    {
        builder.fromString("undef");
    }

    /**
     * Tries to parse a string with an invalid minimum size definition. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringInvalidMinSize()
    {
        builder.fromString("minimum(42");
    }

    /**
     * Tries to parse a string with an invalid alignment. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringInvalidAlign()
    {
        builder.fromString("42/minimum/10");
    }

    /**
     * Tries to parse a string with an invalid weight factor. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringInvalidWeight()
    {
        builder.fromString("preferred/weight");
    }

    /**
     * Tries to parse a string with too many tokens. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringTooManyTokens()
    {
        builder.fromString("start/minimum/10/4");
    }

    /**
     * Tries to parse a string with a negative minimum size. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringNegativeMinSize()
    {
        builder.fromString("END/PREFERRED(-1cm)");
    }

    /**
     * Tries to parse a string with a negative weight factor. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringNegativeWeight()
    {
        builder.fromString("PREFERRED/-10");
    }

    /**
     * Tests whether an instance can be transformed into a specification string.
     */
    @Test
    public void testToSpecificationString()
    {
        CellConstraints cc = builder.withCellAlignment(CellAlignment.CENTER)
                .withCellSize(CellSize.PREFERRED).withMinimumSize(MINSIZE)
                .withWeight(WEIGHT).create();
        assertEquals("Wrong specification string", SPEC, cc
                .toSpecificationString());
    }

    /**
     * Tests the specification string if some values have not been initialized.
     */
    @Test
    public void testToSpecificationStringDefaults()
    {
        CellConstraints cc = builder.withCellSize(CellSize.PREFERRED).create();
        assertEquals("Wrong specification string", "FULL/PREFERRED("
                + NumberWithUnit.ZERO.toUnitString() + ")/0", cc
                .toSpecificationString());
    }

    /**
     * Tests the string representation.
     */
    @Test
    public void testToString()
    {
        CellConstraints cc = builder.fromString(SPEC);
        String s = cc.toString();
        assertEquals("Wrong string", "CellConstraints [ " + SPEC + " ]", s);
    }

    /**
     * Tests the equals() method if the expected result is true.
     */
    @Test
    public void testEqualsTrue()
    {
        CellConstraints.Builder b2 = new CellConstraints.Builder();
        CellConstraints cc1 = builder.withCellSize(CellSize.PREFERRED).create();
        JGuiraffeTestHelper.checkEquals(cc1, cc1, true);
        CellConstraints cc2 = b2.withCellSize(CellSize.PREFERRED).create();
        JGuiraffeTestHelper.checkEquals(cc1, cc2, true);
        cc1 = builder.withCellAlignment(CellAlignment.CENTER).withCellSize(
                CellSize.MINIMUM).create();
        cc2 = b2.withCellAlignment(CellAlignment.CENTER).withCellSize(
                CellSize.MINIMUM).create();
        JGuiraffeTestHelper.checkEquals(cc1, cc2, true);
        cc1 = builder.withCellAlignment(CellAlignment.END).withMinimumSize(
                MINSIZE).withWeight(WEIGHT).create();
        cc2 = b2.withCellAlignment(CellAlignment.END).withMinimumSize(MINSIZE)
                .withWeight(WEIGHT).create();
        JGuiraffeTestHelper.checkEquals(cc1, cc2, true);
    }

    /**
     * Tests equals() if the expected result is false.
     */
    @Test
    public void testEqualsFalse()
    {
        CellConstraints c1 = builder.fromString(SPEC);
        CellConstraints c2 = builder.withCellAlignment(CellAlignment.END)
                .withCellSize(CellSize.PREFERRED).withMinimumSize(MINSIZE)
                .withWeight(WEIGHT).create();
        JGuiraffeTestHelper.checkEquals(c1, c2, false);
        c2 = builder.withCellAlignment(CellAlignment.CENTER).withCellSize(
                CellSize.MINIMUM).withMinimumSize(MINSIZE).withWeight(WEIGHT)
                .create();
        JGuiraffeTestHelper.checkEquals(c1, c2, false);
        c2 = builder.withCellAlignment(CellAlignment.CENTER).withCellSize(
                CellSize.MINIMUM).withMinimumSize(NumberWithUnit.ZERO)
                .withWeight(WEIGHT).create();
        JGuiraffeTestHelper.checkEquals(c1, c2, false);
        c2 = builder.withCellAlignment(CellAlignment.CENTER).withCellSize(
                CellSize.MINIMUM).withMinimumSize(MINSIZE).withWeight(0)
                .create();
        JGuiraffeTestHelper.checkEquals(c1, c2, false);
    }

    /**
     * Tests equals() for other objects.
     */
    @Test
    public void testEqualsTrivial()
    {
        CellConstraints cc = builder.fromString(SPEC);
        JGuiraffeTestHelper.testTrivialEquals(cc);
    }
}

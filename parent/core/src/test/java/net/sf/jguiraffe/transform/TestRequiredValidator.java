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
package net.sf.jguiraffe.transform;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for RequiredValidator.
 *
 * @author Oliver Heger
 * @version $Id: TestRequiredValidator.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestRequiredValidator extends AbstractTransformerTest
{
    /** Stores the validator to be tested. */
    private RequiredValidator validator;

    @Before
    public void setUp() throws Exception
    {
        validator = new RequiredValidator();
    }

    /**
     * Tests validating an object, which should be rejected.
     *
     * @param o the affected object
     */
    private void rejectTest(Object o)
    {
        setUpContextMock(new HashMap<String, Object>());
        expectError(ValidationMessageConstants.ERR_FIELD_REQUIRED);
        replayMocks();
        checkError(ValidationMessageConstants.ERR_FIELD_REQUIRED, validator
                .isValid(o, context));
        verifyMocks();
    }

    /**
     * Tests validating an object, which should be accepted.
     *
     * @param o the affected object
     * @param msg a message for the assert statement
     */
    private void acceptTest(Object o, String msg)
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        assertTrue(msg + " not accepted", validator.isValid(o, context)
                .isValid());
        verifyMocks();
    }

    /**
     * Tests validating a null object. This should be rejected.
     */
    @Test
    public void testIsValidNull()
    {
        rejectTest(null);
    }

    /**
     * Tests validating a non empty string. This should be accepted.
     */
    @Test
    public void testIsValidString()
    {
        acceptTest("A test", "Non empty string");
    }

    /**
     * Tests validating an empty string. This should be rejected.
     */
    @Test
    public void testIsValidEmptyString()
    {
        rejectTest("");
    }

    /**
     * Tests validating a string that contains only whitespace. This should be
     * rejected.
     */
    @Test
    public void testIsValidBlancString()
    {
        rejectTest("         ");
    }

    /**
     * Tests validating a non empty collection. This should be accepted.
     */
    @Test
    public void testIsValidCollection()
    {
        Collection<Object> col = new ArrayList<Object>(1);
        col.add("Test");
        acceptTest(col, "Non empty collection");
    }

    /**
     * Tests validating an empty collection. This should be rejected.
     */
    @Test
    public void testIsValidEmptyCollection()
    {
        rejectTest(new ArrayList<Object>());
    }

    /**
     * Tests validating an array with some elements. This should be accepted,
     * even if the elements are null.
     */
    @Test
    public void testIsValidArray()
    {
        acceptTest(new Object[5], "Non empty array");
    }

    /**
     * Tests validating an array with no elements. This should be rejected.
     */
    @Test
    public void testIsValidEmptyArray()
    {
        rejectTest(new String[0]);
    }

    /**
     * Tests validating an arbitrary object. All non-null objects that are no
     * instances of specially treated classes should be accepted.
     */
    @Test
    public void testIsValidObject()
    {
        acceptTest(this, "Arbitrary object");
    }
}

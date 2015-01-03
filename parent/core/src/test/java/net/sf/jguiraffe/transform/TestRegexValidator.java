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
package net.sf.jguiraffe.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Map;

import net.sf.jguiraffe.gui.forms.TransformerContextImpl;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code RegexValidator}.
 *
 * @author Oliver Heger
 * @version $Id: TestRegexValidator.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestRegexValidator
{
    /** Constant for the default regular expression. */
    private static final String REGEX = "a.*z";

    /** The transformer context. */
    private TransformerContextImpl tctx;

    /** The validator to be tested. */
    private RegexValidator validator;

    @Before
    public void setUp() throws Exception
    {
        tctx = new TransformerContextImpl();
        validator = new RegexValidator();
    }

    /**
     * Creates a context with the specified overridden property.
     *
     * @param property the property key
     * @param value the property value
     * @return the context with this property
     */
    private TransformerContext override(String property, Object value)
    {
        Map<String, Object> props = Collections.singletonMap(property, value);
        return new TransformerContextPropertiesWrapper(tctx, props);
    }

    /**
     * Tests the default values of the properties.
     */
    @Test
    public void testDefaultSettings()
    {
        assertEquals("Wrong regex", ".*", validator.getRegex());
        assertFalse("Dotall", validator.isDotAll());
        assertFalse("Case insensitive", validator.isCaseInsensitive());
    }

    /**
     * Tries to set the regular expression to null. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetRegexNull()
    {
        validator.setRegex(null);
    }

    /**
     * Tests validation with default settings.
     */
    @Test
    public void testIsValidDefaultSettings()
    {
        validator.setRegex(REGEX);
        assertTrue("Not valid", validator.isValid("atoz", tctx).isValid());
        assertFalse("Valid (1)", validator.isValid("AtoZ", tctx).isValid());
        assertFalse("Valid (2)", validator.isValid("a\nto\nz", tctx).isValid());
        assertFalse("Valid (3)", validator.isValid("invalid input", tctx)
                .isValid());
    }

    /**
     * Tests whether the case insensitive flag is evaluated.
     */
    @Test
    public void testIsValidCaseInsensitive()
    {
        validator.setRegex(REGEX);
        validator.setCaseInsensitive(true);
        assertTrue("Not valid (1)", validator.isValid("atoz", tctx).isValid());
        assertTrue("Not valid (2)", validator.isValid("AtoZ", tctx).isValid());
    }

    /**
     * Tests whether the dot all flag is evaluated.
     */
    @Test
    public void testIsValidDotAll()
    {
        validator.setRegex(REGEX);
        validator.setDotAll(true);
        assertTrue("Not valid (1)", validator.isValid("atoz", tctx).isValid());
        assertTrue("Not valid (2)", validator.isValid("a\nto\nz", tctx)
                .isValid());
    }

    /**
     * Tests whether the regex property can be overridden.
     */
    @Test
    public void testIsValidOverrideRegex()
    {
        validator.setRegex(REGEX);
        TransformerContext ctx = override("regex", "z.*a");
        assertTrue("Not valid", validator.isValid("ztoa", ctx).isValid());
        assertFalse("Valid", validator.isValid("atoz", ctx).isValid());
    }

    /**
     * Tests whether the case insensitive property can be overridden.
     */
    @Test
    public void testIsValidOverrideCaseInsensitive()
    {
        validator.setRegex(REGEX);
        TransformerContext ctx = override("caseInsensitive", Boolean.TRUE);
        assertTrue("Not valid", validator.isValid("AtoZ", ctx).isValid());
    }

    /**
     * Tests whether the multiple lines property can be overridden.
     */
    @Test
    public void testIsValidOverrideMultiLine()
    {
        validator.setRegex(REGEX);
        TransformerContext ctx = override("dotAll", Boolean.TRUE);
        assertTrue("Not valid", validator.isValid("a\nto\nz", ctx).isValid());
    }

    /**
     * Tests whether the regular expression can be changed.
     */
    @Test
    public void testIsValidChangeRegex()
    {
        validator.setRegex(REGEX);
        assertTrue("Not valid (1)", validator.isValid("atoz", tctx).isValid());
        validator.setRegex("z.*a");
        assertTrue("Not valid (2)", validator.isValid("ztoa", tctx).isValid());
    }

    /**
     * Tests the validation result produced for an invalid input.
     */
    @Test
    public void testIsValidError()
    {
        validator.setRegex(REGEX);
        ValidationResult vres = validator.isValid("invalid", tctx);
        assertEquals("Wrong number of messages", 1, vres.getValidationMessages()
                .size());
        ValidationMessage msg = vres.getValidationMessages().iterator().next();
        assertEquals("Wrong key", ValidationMessageConstants.ERR_PATTERN, msg
                .getKey());
        assertTrue("Regex not found: " + msg.getMessage(), msg.getMessage()
                .indexOf(REGEX) >= 0);
    }

    /**
     * Tests whether an overridden regular expression string appears in the
     * validation error message.
     */
    @Test
    public void testIsValidErrorOverrideRegex()
    {
        validator.setRegex(REGEX);
        final String regexOverride = "z.*a";
        TransformerContext ctx = override("regex", regexOverride);
        ValidationResult vres = validator.isValid("invalid", ctx);
        assertEquals("Wrong number of messages", 1, vres.getValidationMessages()
                .size());
        ValidationMessage msg = vres.getValidationMessages().iterator().next();
        assertEquals("Wrong key", ValidationMessageConstants.ERR_PATTERN, msg
                .getKey());
        assertTrue("Overridden regex not found: " + msg.getMessage(), msg
                .getMessage().indexOf(regexOverride) >= 0);
    }

    /**
     * Tests validation of a null object. This should be accepted.
     */
    @Test
    public void testIsValidNull()
    {
        assertTrue("Null input not valid", validator.isValid(null, tctx)
                .isValid());
    }
}

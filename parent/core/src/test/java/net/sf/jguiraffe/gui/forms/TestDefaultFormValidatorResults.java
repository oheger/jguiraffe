/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.forms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationMessageHandler;
import net.sf.jguiraffe.transform.ValidationMessageLevel;
import net.sf.jguiraffe.transform.ValidationResult;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for DefaultFormValidatorResults.
 *
 * @author Oliver Heger
 * @version $Id: TestDefaultFormValidatorResults.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDefaultFormValidatorResults
{
    /** Constant for the field prefix. */
    private static final String FIELD = "field";

    /** Constant for the error field prefix. */
    private static final String ERRFIELD = "errorField";

    /**
     * Creates a {@code ValidationMessage} object with the specified level.
     *
     * @param level the level
     * @return the mock validation message object
     */
    private static ValidationMessage createValidationMessage(
            ValidationMessageLevel level)
    {
        ValidationMessage vm = EasyMock.createMock(ValidationMessage.class);
        EasyMock.expect(vm.getLevel()).andReturn(level).anyTimes();
        EasyMock.replay(vm);
        return vm;
    }

    /**
     * Returns a validation result object that is invalid.
     *
     * @return the invalid result
     */
    private ValidationResult createInvalidResult()
    {
        DefaultValidationResult vr = new DefaultValidationResult.Builder()
                .addValidationMessage(
                        createValidationMessage(ValidationMessageLevel.ERROR))
                .build();
        return vr;
    }

    /**
     * Creates a map with a number of valid and invalid fields. The names of the
     * valid fields starts with the {@code FIELD} prefix, the names for the
     * invalid fields with the {@code ERRFIELD} prefix. They have running
     * indices.
     *
     * @param validCnt the number of valid fields
     * @param errCnt the number of invalid fields
     * @return the resulting map
     */
    private Map<String, ValidationResult> setUpFieldMap(int validCnt, int errCnt)
    {
        Map<String, ValidationResult> res = new HashMap<String, ValidationResult>();
        for (int i = 0; i < validCnt; i++)
        {
            res.put(FIELD + i, DefaultValidationResult.VALID);
        }
        for (int i = 0; i < errCnt; i++)
        {
            res.put(ERRFIELD + i, createInvalidResult());
        }
        return res;
    }

    /**
     * Tests whether the specified collection contains (at least) the given
     * number of fields starting with the specified prefix.
     *
     * @param fields the collection to check
     * @param prefix the prefix for the fields
     * @param count the number of fields
     */
    private void checkFields(Collection<String> fields, String prefix, int count)
    {
        for (int i = 0; i < count; i++)
        {
            assertTrue("Field not found: " + i, fields.contains(prefix + i));
        }
    }

    /**
     * Helper method for creating a results instance with the given number of
     * valid and invalid dummy fields.
     *
     * @param validCnt the number of valid fields
     * @param errCnt the number of invalid fields
     * @return the instance
     */
    private DefaultFormValidatorResults setUpResults(int validCnt, int errCnt)
    {
        return new DefaultFormValidatorResults(setUpFieldMap(validCnt, errCnt));
    }

    /**
     * Tests an instance created from an empty map.
     */
    @Test
    public void testInitEmptyMap()
    {
        DefaultFormValidatorResults results = new DefaultFormValidatorResults(
                new HashMap<String, ValidationResult>());
        assertTrue("New instance is not valid", results.isValid());
        assertTrue("New instance has error fields", results
                .getErrorFieldNames().isEmpty());
        assertTrue("New instance has field names", results.getFieldNames()
                .isEmpty());
    }

    /**
     * Tests creating an instance from a null map. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullMap()
    {
        new DefaultFormValidatorResults(null);
    }

    /**
     * Tests creating an instance when the map contains null values. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullValues()
    {
        Map<String, ValidationResult> fields = setUpFieldMap(5, 1);
        fields.put("anotherField", null);
        new DefaultFormValidatorResults(fields);
    }

    /**
     * Tests isValid() when there are no error fields.
     */
    @Test
    public void testIsValidNoErrorFields()
    {
        DefaultFormValidatorResults results = setUpResults(10, 0);
        assertTrue("Not valid", results.isValid());
    }

    /**
     * Tests isValid() if error fields exist.
     */
    @Test
    public void testIsValidErrorFields()
    {
        DefaultFormValidatorResults results = setUpResults(10, 1);
        assertFalse("Valid", results.isValid());
    }

    /**
     * Tests querying the field names.
     */
    @Test
    public void testGetFieldNames()
    {
        final int validCnt = 4;
        final int errCnt = 2;
        DefaultFormValidatorResults results = setUpResults(validCnt, errCnt);
        Set<String> fields = results.getFieldNames();
        assertEquals("Wrong number of fields", validCnt + errCnt, fields.size());
        checkFields(fields, FIELD, validCnt);
        checkFields(fields, ERRFIELD, errCnt);
    }

    /**
     * Tests querying the error fields.
     */
    @Test
    public void testGetErrorFields()
    {
        final int errCnt = 3;
        DefaultFormValidatorResults results = setUpResults(4, errCnt);
        Set<String> errFields = results.getErrorFieldNames();
        assertEquals("Wrong number of fields", errCnt, errFields.size());
        checkFields(errFields, ERRFIELD, errCnt);
    }

    /**
     * Tests the error fields for a valid object.
     */
    @Test
    public void testGetErrorFieldsValid()
    {
        DefaultFormValidatorResults results = setUpResults(5, 0);
        assertTrue("Got error fields", results.getErrorFieldNames().isEmpty());
    }

    /**
     * Tests obtaining results for specific fields.
     */
    @Test
    public void testGetResultsFor()
    {
        final int validCnt = 4;
        final int errCnt = 2;
        DefaultFormValidatorResults results = setUpResults(validCnt, errCnt);
        for (int i = 0; i < validCnt; i++)
        {
            ValidationResult vres = results.getResultsFor(FIELD + i);
            assertTrue("Invalid result for " + i, vres.isValid());
        }
        for (int i = 0; i < errCnt; i++)
        {
            ValidationResult vres = results.getResultsFor(ERRFIELD + i);
            assertFalse("Valid result for " + i, vres.isValid());
        }
    }

    /**
     * Tests querying results for a non existing field.
     */
    @Test
    public void testGetResultsForNonExisting()
    {
        DefaultFormValidatorResults results = setUpResults(5, 2);
        assertNull("Got results", results.getResultsFor("unknown field"));
    }

    /**
     * Tests the equals method.
     */
    @Test
    public void testEquals()
    {
        Map<String, ValidationResult> map1 = new HashMap<String, ValidationResult>();
        Map<String, ValidationResult> map2 = new HashMap<String, ValidationResult>();
        DefaultFormValidatorResults r1 = new DefaultFormValidatorResults(map1);
        DefaultFormValidatorResults r2 = new DefaultFormValidatorResults(map2);
        JGuiraffeTestHelper.checkEquals(r1, r1, true);
        JGuiraffeTestHelper.checkEquals(r1, r2, true);
        ValidationResult vr = new DefaultValidationResult.Builder().build();
        map1.put("field1", vr);
        vr = createInvalidResult();
        map2.put("field2", vr);
        r1 = new DefaultFormValidatorResults(map1);
        r2 = new DefaultFormValidatorResults(map2);
        JGuiraffeTestHelper.checkEquals(r1, r2, false);
        map1.put("field2", vr);
        JGuiraffeTestHelper.checkEquals(r1, r2, false);
        map2.put("field1", DefaultValidationResult.VALID);
        r1 = new DefaultFormValidatorResults(map1);
        r2 = new DefaultFormValidatorResults(map2);
        JGuiraffeTestHelper.checkEquals(r1, r2, true);
    }

    /**
     * Tests comparing with invalid objects.
     */
    @Test
    public void testTrivialEquals()
    {
        JGuiraffeTestHelper.testTrivialEquals(setUpFieldMap(1, 1));
    }

    /**
     * Tests that the map passed to the constructor is copied so that later
     * manipulations have no effect.
     */
    @Test
    public void testInitModifyMap()
    {
        Map<String, ValidationResult> map = setUpFieldMap(5, 2);
        DefaultFormValidatorResults results = new DefaultFormValidatorResults(
                map);
        map.clear();
        assertFalse("Data was changed", results.getFieldNames().isEmpty());
    }

    /**
     * Tests that the field names cannot be modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetFieldNamesModify()
    {
        DefaultFormValidatorResults results = setUpResults(6, 1);
        results.getFieldNames().clear();
    }

    /**
     * Tests that the error field names cannot be modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetErrorFieldNamesModify()
    {
        DefaultFormValidatorResults results = setUpResults(6, 1);
        results.getErrorFieldNames().clear();
    }

    /**
     * Creates a test form with some fields.
     *
     * @param count the number of fields
     * @return the test form
     */
    private Form setUpTestForm(int count)
    {
        Form form = new Form(new TransformerContextImpl(),
                new BeanBindingStrategy());
        for (int i = 0; i < count; i++)
        {
            form.addField(FIELD + i, EasyMock
                    .createNiceMock(FieldHandler.class));
        }
        return form;
    }

    /**
     * Tests creating a validation map for a form.
     */
    @Test
    public void testValidResultMapForForm()
    {
        final int count = 4;
        Form form = setUpTestForm(count);
        Map<String, ValidationResult> map = DefaultFormValidatorResults
                .validResultMapForForm(form);
        assertEquals("Wrong size of the map", count, map.size());
        for (int i = 0; i < count; i++)
        {
            String field = FIELD + i;
            ValidationResult vres = map.get(field);
            assertTrue("Result not valid for " + field, vres.isValid());
        }
    }

    /**
     * Tests creating a results object for a form.
     */
    @Test
    public void testValidResultsForForm()
    {
        final int count = 8;
        Form form = setUpTestForm(count);
        DefaultFormValidatorResults results = DefaultFormValidatorResults
                .validResultsForForm(form);
        assertTrue("Results not valid", results.isValid());
        assertTrue("Got error fields", results.getErrorFieldNames().isEmpty());
        assertEquals("Wrong number of fields", count, results.getFieldNames()
                .size());
        for (int i = 0; i < count; i++)
        {
            String field = FIELD + i;
            assertTrue("Wrong result for " + field, results
                    .getResultsFor(field).isValid());
        }
    }

    /**
     * Tries to obtain a result object for a null form. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidResultsForFormNull()
    {
        DefaultFormValidatorResults.validResultsForForm(null);
    }

    /**
     * Tests the string representation. We test whether the string contains
     * information about all fields contained in the results object.
     */
    @Test
    public void testToString()
    {
        DefaultFormValidatorResults results = setUpResults(5, 2);
        String s = results.toString();
        for (String field : results.getFieldNames())
        {
            assertTrue("No information about field " + field + " in " + s, s
                    .indexOf(field) > 0);
        }
    }

    /**
     * Tests obtaining a validation message.
     */
    @Test
    public void testCreateValidationMessage()
    {
        TransformerContext ctx = EasyMock.createMock(TransformerContext.class);
        ValidationMessageHandler handler = EasyMock
                .createMock(ValidationMessageHandler.class);
        ValidationMessage msg = EasyMock.createMock(ValidationMessage.class);
        EasyMock.expect(ctx.getValidationMessageHandler()).andReturn(handler);
        final Object param = "Error parameter";
        EasyMock.expect(handler.getValidationMessage(ctx, ERRFIELD, param))
                .andReturn(msg);
        EasyMock.replay(ctx, handler, msg);
        Form form = new Form(ctx, new BeanBindingStrategy());
        assertEquals("Wrong validation message", msg,
                DefaultFormValidatorResults.createValidationMessage(form,
                        ERRFIELD, param));
        EasyMock.verify(ctx, handler, msg);
    }

    /**
     * Tests createValidationMessage() if a null form is passed in. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateValidationMessageNullForm()
    {
        DefaultFormValidatorResults.createValidationMessage(null, ERRFIELD);
    }

    /**
     * Tests the convenience method for creating a validation result with an
     * error message.
     */
    @Test
    public void testCreateValidationErrorResult()
    {
        TransformerContext ctx = EasyMock.createMock(TransformerContext.class);
        ValidationMessageHandler handler = EasyMock
                .createMock(ValidationMessageHandler.class);
        ValidationMessage msg = EasyMock.createMock(ValidationMessage.class);
        EasyMock.expect(msg.getLevel()).andReturn(ValidationMessageLevel.ERROR)
                .anyTimes();
        EasyMock.expect(ctx.getValidationMessageHandler()).andReturn(handler);
        final Object param = "Error parameter";
        EasyMock.expect(handler.getValidationMessage(ctx, ERRFIELD, param))
                .andReturn(msg);
        EasyMock.replay(ctx, handler, msg);
        Form form = new Form(ctx, new BeanBindingStrategy());
        ValidationResult vres = DefaultFormValidatorResults
                .createValidationErrorResult(form, ERRFIELD, param);
        assertFalse("Result is valid", vres.isValid());
        Collection<ValidationMessage> msgs = vres.getValidationMessages();
        assertEquals("Wrong number of messages", 1, msgs.size());
        assertEquals("Wrong message", msg, msgs.iterator().next());
        EasyMock.verify(ctx, handler, msg);
    }

    /**
     * Tries to create a validation result with an error message if a null for
     * is passed in. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateValidationErrorResultNullForm()
    {
        DefaultFormValidatorResults.createValidationErrorResult(null, ERRFIELD);
    }

    /**
     * Tests merge() if the first argument is null.
     */
    @Test
    public void testMergeNull1()
    {
        DefaultFormValidatorResults res = setUpResults(10, 2);
        assertSame("Wrong merge result", res, DefaultFormValidatorResults
                .merge(null, res));
    }

    /**
     * Tests merge() if the 2nd argument is null.
     */
    @Test
    public void testMergeNull2()
    {
        DefaultFormValidatorResults res = setUpResults(10, 2);
        assertSame("Wrong merge result", res, DefaultFormValidatorResults
                .merge(res, null));
    }

    /**
     * Tests merge() if all arguments are null.
     */
    @Test
    public void testMergeNullBoth()
    {
        assertNull("Non-null result", DefaultFormValidatorResults.merge(null,
                null));
    }

    /**
     * Tests merge() with non-null arguments.
     */
    @Test
    public void testMergeNonNull()
    {
        DefaultValidationResult.Builder builder = new DefaultValidationResult.Builder();
        Map<String, ValidationResult> map1 = new HashMap<String, ValidationResult>();
        Map<String, ValidationResult> map2 = new HashMap<String, ValidationResult>();
        map1.put(FIELD, builder.addValidationMessage(
                createValidationMessage(ValidationMessageLevel.WARNING))
                .build());
        map2.put(FIELD, builder.addValidationMessage(
                createValidationMessage(ValidationMessageLevel.WARNING))
                .build());
        map1.put(ERRFIELD, createInvalidResult());
        String field2 = "anotherField";
        map2.put(field2, builder.addValidationMessage(
                createValidationMessage(ValidationMessageLevel.WARNING))
                .build());
        DefaultFormValidatorResults res1 = new DefaultFormValidatorResults(map1);
        DefaultFormValidatorResults res2 = new DefaultFormValidatorResults(map2);
        FormValidatorResults merge = DefaultFormValidatorResults.merge(res1,
                res2);
        assertNotSame("Got res1", merge, res1);
        assertNotSame("Got res2", merge, res2);
        assertEquals("Wrong number of fields", 3, merge.getFieldNames().size());
        ValidationResult vr = merge.getResultsFor(FIELD);
        assertEquals("Wrong number of warnings", 2, vr.getValidationMessages(
                ValidationMessageLevel.WARNING).size());
        assertTrue("Not valid", vr.isValid());
        vr = merge.getResultsFor(ERRFIELD);
        assertFalse("ERRFIELD is valid", vr.isValid());
        vr = merge.getResultsFor(field2);
        assertEquals("Wrong number of warnings 2", 1, vr.getValidationMessages(
                ValidationMessageLevel.WARNING).size());
    }
}

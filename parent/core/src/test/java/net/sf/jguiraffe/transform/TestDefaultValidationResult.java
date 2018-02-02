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
package net.sf.jguiraffe.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for DefaultValidationResult.
 *
 * @author Oliver Heger
 * @version $Id: TestDefaultValidationResult.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDefaultValidationResult
{
    /** Stores the validation messages added to the test object. */
    private List<ValidationMessage> messages;

    /**
     * Verifies the message mock objects that have been created.
     */
    private void verify()
    {
        if (messages != null)
        {
            EasyMock.verify((Object[]) messages.toArray());
        }
    }

    /**
     * Creates a new mock object for a validation message. The object is added
     * to an internal list and will be automatically verified at the end of the
     * current test case.
     *
     * @param level the validation message level
     * @return the mock object
     */
    private ValidationMessage createValidationMessage(
            ValidationMessageLevel level)
    {
        ValidationMessage msg = EasyMock.createMock(ValidationMessage.class);
        EasyMock.expect(msg.getLevel()).andReturn(level).anyTimes();
        EasyMock.replay(msg);
        if (messages == null)
        {
            messages = new ArrayList<ValidationMessage>();
        }
        messages.add(msg);

        return msg;
    }

    /**
     * Creates a mock object for a validation message with level error.
     *
     * @return the mock object
     */
    private ValidationMessage createValidationMessage()
    {
        return createValidationMessage(ValidationMessageLevel.ERROR);
    }

    /**
     * Creates a number of mock messages with the specified level.
     *
     * @param count the number of messages
     * @param level the level
     * @return the collection with the messages
     */
    private Collection<ValidationMessage> createMessages(int count,
            ValidationMessageLevel level)
    {
        Collection<ValidationMessage> result = new ArrayList<ValidationMessage>(
                count);
        for (int i = 0; i < count; i++)
        {
            result.add(createValidationMessage(level));
        }
        return result;
    }

    /**
     * Tests the VALID constant.
     */
    @Test
    public void testValidResult()
    {
        assertTrue("Invalid", DefaultValidationResult.VALID.isValid());
        assertTrue("Got error messages", DefaultValidationResult.VALID
                .getValidationMessages().isEmpty());
    }

    /**
     * Tests isValid() if the instance contains an error message.
     */
    @Test
    public void testIsValidWithErrorMessage()
    {
        DefaultValidationResult result = new DefaultValidationResult.Builder()
                .addValidationMessage(createValidationMessage()).build();
        assertFalse("Result is valid", result.isValid());
        verify();
    }

    /**
     * Tests isValid() if the instance contains only a warning message.
     */
    @Test
    public void testIsValidWithWarningMessage()
    {
        DefaultValidationResult result = new DefaultValidationResult.Builder()
                .addValidationMessage(
                        createValidationMessage(ValidationMessageLevel.WARNING))
                .build();
        assertTrue("Result not valid", result.isValid());
        verify();
    }

    /**
     * Tests to add error messages.
     */
    @Test
    public void testBuilderAddMessage()
    {
        final int count = 10;
        DefaultValidationResult.Builder builder = new DefaultValidationResult.Builder();
        for (int i = 0; i < count; i++)
        {
            builder.addValidationMessage(createValidationMessage());
        }
        DefaultValidationResult result = builder.build();
        assertEquals("Wrong number of messages", count, result
                .getValidationMessages().size());
        int idx = 0;
        for (ValidationMessage m : result.getValidationMessages())
        {
            assertEquals("Wrong message at" + idx, messages.get(idx++), m);
        }
        verify();
    }

    /**
     * Tests whether the builder performs a reset automatically after creating
     * an object.
     */
    @Test
    public void testBuilderResetAfterBuild()
    {
        DefaultValidationResult.Builder builder = new DefaultValidationResult.Builder();
        builder.addValidationMessage(createValidationMessage()).build();
        builder
                .addValidationMessage(createValidationMessage(ValidationMessageLevel.WARNING));
        DefaultValidationResult vr = builder.build();
        assertEquals("Wrong number of messages", 1, vr.getValidationMessages()
                .size());
        assertEquals("Wrong number of warnings", 1, vr.getValidationMessages(
                ValidationMessageLevel.WARNING).size());
        assertTrue("Not valid", vr.isValid());
    }

    /**
     * Tests whether the builder can be reset.
     */
    @Test
    public void testBuilderReset()
    {
        DefaultValidationResult.Builder builder = new DefaultValidationResult.Builder();
        builder.addValidationMessage(createValidationMessage());
        builder.reset();
        builder
                .addValidationMessage(createValidationMessage(ValidationMessageLevel.WARNING));
        DefaultValidationResult vr = builder.build();
        assertEquals("Wrong number of messages", 1, vr.getValidationMessages()
                .size());
        assertEquals("Wrong number of warnings", 1, vr.getValidationMessages(
                ValidationMessageLevel.WARNING).size());
        assertTrue("Not valid", vr.isValid());
    }

    /**
     * Tries to manipulate the list of messages directly. This should
     * cause an exception.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetValidationMessagesModify()
    {
        DefaultValidationResult result = new DefaultValidationResult.Builder()
                .addValidationMessage(createValidationMessage()).build();
        Collection<ValidationMessage> msgs = result.getValidationMessages();
        msgs.clear();
    }

    /**
     * Tries to add a null message. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddValidationMessageNull()
    {
        new DefaultValidationResult.Builder().addValidationMessage(null);
    }

    /**
     * Tests adding a collection of messages.
     */
    @Test
    public void testAddValidationMessages()
    {
        final int count = 5;
        Collection<ValidationMessage> msgs = createMessages(count, ValidationMessageLevel.ERROR);
        DefaultValidationResult result = new DefaultValidationResult.Builder()
                .addValidationMessages(msgs).build();
        Collection<ValidationMessage> msgs2 = result.getValidationMessages();
        assertNotSame("Collection is same", msgs, msgs2);
        assertEquals("Different numbers of elements", msgs.size(), msgs2.size());
        Iterator<ValidationMessage> it = msgs2.iterator();
        for (ValidationMessage vm : msgs)
        {
            assertEquals("Different element", vm, it.next());
        }
        verify();
    }

    /**
     * Tests chained invocations of addErrorMessage().
     */
    @Test
    public void testAddValidationMessagesChained()
    {
        Collection<ValidationMessage> msgs = new ArrayList<ValidationMessage>(2);
        final int count = 4;
        ValidationMessage[] allMsgs = new ValidationMessage[count];
        for (int i = 0; i < count; i++)
        {
            allMsgs[i] = createValidationMessage();
        }
        msgs.add(allMsgs[1]);
        msgs.add(allMsgs[2]);
        DefaultValidationResult result = new DefaultValidationResult.Builder()
                .addValidationMessage(allMsgs[0]).addValidationMessages(msgs)
                .addValidationMessage(allMsgs[3]).build();
        Collection<ValidationMessage> errMsgs = result.getValidationMessages();
        assertEquals("Wrong number of error messages", allMsgs.length, errMsgs
                .size());
        int i = 0;
        for (ValidationMessage m : errMsgs)
        {
            assertEquals("Wrong message at " + i, allMsgs[i], m);
            i++;
        }
        verify();
    }

    /**
     * Tries adding a null collection of messages. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddValidationMessagesNull()
    {
        new DefaultValidationResult.Builder().addValidationMessages(null);
    }

    /**
     * Tests adding a collection of error messages when an element is null. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddValidationMessagesNullElement()
    {
        Collection<ValidationMessage> msgs = createMessages(10, ValidationMessageLevel.ERROR);
        msgs.add(null);
        msgs.add(createValidationMessage());
        new DefaultValidationResult.Builder().addValidationMessages(null).build();
    }

    /**
     * Tests building a valid result.
     */
    @Test
    public void testBuildValidResult()
    {
        DefaultValidationResult res = new DefaultValidationResult.Builder()
                .build();
        assertTrue("Invalid", res.isValid());
    }

    /**
     * Tests equals() for valid results.
     */
    @Test
    public void testEqualsValid()
    {
        DefaultValidationResult res = new DefaultValidationResult.Builder()
                .build();
        JGuiraffeTestHelper.checkEquals(res, res, true);
        JGuiraffeTestHelper.checkEquals(res, DefaultValidationResult.VALID,
                true);
    }

    /**
     * Tests the equals() method for objects with error messages.
     */
    @Test
    public void testEquals()
    {
        ValidationMessage msg = createValidationMessage();
        DefaultValidationResult res1 = new DefaultValidationResult.Builder()
                .addValidationMessage(msg).build();
        JGuiraffeTestHelper.checkEquals(res1, DefaultValidationResult.VALID,
                false);
        DefaultValidationResult res2 = new DefaultValidationResult.Builder()
                .addValidationMessage(msg).build();
        JGuiraffeTestHelper.checkEquals(res1, res2, true);
        res2 = new DefaultValidationResult.Builder().addValidationMessage(msg)
                .addValidationMessage(createValidationMessage()).build();
        JGuiraffeTestHelper.checkEquals(res1, res2, false);
    }

    /**
     * Tests comparing a result to invalid objects.
     */
    @Test
    public void testTrivialEquals()
    {
        DefaultValidationResult res = new DefaultValidationResult.Builder()
                .addValidationMessage(createValidationMessage()).build();
        JGuiraffeTestHelper.testTrivialEquals(res);
    }

    /**
     * Tests the merge() method if the 1st parameter is null.
     */
    @Test
    public void testMergeNull1()
    {
        DefaultValidationResult res = new DefaultValidationResult.Builder()
                .build();
        assertSame("Wrong result", res, DefaultValidationResult
                .merge(null, res));
    }

    /**
     * Tests the merge() method if the 2nd parameter is null.
     */
    @Test
    public void testMergeNull2()
    {
        DefaultValidationResult res = new DefaultValidationResult.Builder()
                .addValidationMessage(
                        createValidationMessage(ValidationMessageLevel.WARNING))
                .build();
        assertSame("Wrong result", res, DefaultValidationResult
                .merge(res, null));
    }

    /**
     * Tests merge() if both parameters are null.
     */
    @Test
    public void testMergeNull()
    {
        assertNull("Non-null result", DefaultValidationResult.merge(null, null));
    }

    /**
     * Tests the merge() method if one of the arguments is valid.
     */
    @Test
    public void testMergeWithValidArgument()
    {
        DefaultValidationResult vr = new DefaultValidationResult.Builder()
                .addValidationMessage(createValidationMessage()).build();
        assertSame("Wrong merge result (1)", vr, DefaultValidationResult.merge(
                DefaultValidationResult.VALID, vr));
        assertSame("Wrong merge result (2)", vr, DefaultValidationResult.merge(
                vr, DefaultValidationResult.VALID));
    }

    /**
     * Tests the merge() method if both arguments are not valid.
     */
    @Test
    public void testMergeNonValid()
    {
        DefaultValidationResult result = new DefaultValidationResult.Builder()
                .addValidationMessage(createValidationMessage()).build();
        DefaultValidationResult vr = new DefaultValidationResult.Builder()
                .addValidationMessage(createValidationMessage()).addValidationMessage(
                        createValidationMessage(ValidationMessageLevel.WARNING)).build();
        ValidationResult vr2 = DefaultValidationResult.merge(result, vr);
        assertNotSame("Returned argument 1", result, vr2);
        assertNotSame("Returned argument 2", vr, vr2);
        assertFalse("Result is valid", vr2.isValid());
        Collection<ValidationMessage> msgs = vr2.getValidationMessages();
        assertEquals("Wrong number of messages", 3, msgs.size());
        int idx = 0;
        for (ValidationMessage m : msgs)
        {
            assertEquals("Wrong message at " + idx, messages.get(idx), m);
            idx++;
        }
        verify();
    }

    /**
     * Tests whether merge() handles warning messages correctly.
     */
    @Test
    public void testMergeWithWarningMessage()
    {
        DefaultValidationResult.Builder builder = new DefaultValidationResult.Builder();
        DefaultValidationResult vr1 = builder.addValidationMessage(
                createValidationMessage()).build();
        DefaultValidationResult vr2 = builder.addValidationMessage(
                createValidationMessage(ValidationMessageLevel.WARNING))
                .build();
        ValidationResult vrMerge = DefaultValidationResult.merge(vr1, vr2);
        assertFalse("Result is valid", vrMerge.isValid());
        assertEquals("Wrong number of warnings", 1, vrMerge
                .getValidationMessages(ValidationMessageLevel.WARNING).size());
        assertEquals("Wrong number of errors", 1, vrMerge
                .getValidationMessages(ValidationMessageLevel.ERROR).size());
    }

    /**
     * Tests the merge() method when both arguments are valid.
     */
    @Test
    public void testMergeValid()
    {
        ValidationResult vr = DefaultValidationResult.merge(
                DefaultValidationResult.VALID, DefaultValidationResult.VALID);
        assertTrue("Wrong result", vr.isValid());
    }

    /**
     * Tests the string representation of a validation result object.
     */
    @Test
    public void testToString()
    {
        final int count = 10;
        Collection<ValidationMessage> msgs = new ArrayList<ValidationMessage>(
                count);
        for (int i = 0; i < count; i++)
        {
            final String errMsg = String.format("Error message %02d", i);
            msgs.add(new ValidationMessage()
            {
                public String getKey()
                {
                    return errMsg;
                }

                public String getMessage()
                {
                    return errMsg;
                }

                @Override
                public String toString()
                {
                    return errMsg;
                }

                public ValidationMessageLevel getLevel()
                {
                    return ValidationMessageLevel.ERROR;
                }
            });
        }
        DefaultValidationResult result = new DefaultValidationResult.Builder()
                .addValidationMessages(msgs).build();
        String s = result.toString();
        for (ValidationMessage vm : msgs)
        {
            String sub = vm.toString();
            assertTrue("VM string " + sub + " not found: " + s,
                    s.indexOf(sub) > 0);
        }
    }

    /**
     * Tests the string representation for a valid object.
     */
    @Test
    public void testToStringValid()
    {
        String s = DefaultValidationResult.VALID.toString();
        assertTrue("Valid not found", s.indexOf("[ VALID ]") > 0);
    }

    /**
     * Tests the static convenience method for creating an error result.
     */
    @Test
    public void testCreateValidationErrorResult()
    {
        TransformerContext context = EasyMock
                .createMock(TransformerContext.class);
        ValidationMessageHandler msgHandler = EasyMock
                .createMock(ValidationMessageHandler.class);
        ValidationMessage msg = EasyMock.createMock(ValidationMessage.class);
        EasyMock.expect(msg.getLevel()).andReturn(ValidationMessageLevel.ERROR)
                .anyTimes();
        EasyMock.expect(context.getValidationMessageHandler()).andReturn(
                msgHandler);
        final String key = "errorKey";
        final Object param = "testParam";
        EasyMock.expect(msgHandler.getValidationMessage(context, key, param))
                .andReturn(msg);
        EasyMock.replay(context, msgHandler, msg);
        ValidationResult vres = DefaultValidationResult
                .createValidationErrorResult(context, key, param);
        assertFalse("Result is valid", vres.isValid());
        assertEquals("Wrong number of messages", 1, vres
                .getValidationMessages().size());
        assertEquals("Wrong error message", msg, vres.getValidationMessages()
                .iterator().next());
        EasyMock.verify(context, msgHandler, msg);
    }

    /**
     * Tries to create an error validation result with a null context. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateValidationErrorResultNullCtx()
    {
        DefaultValidationResult
                .createValidationErrorResult(null, "errorMsgKey");
    }

    /**
     * Tests whether the existence of certain messages can be tested.
     */
    @Test
    public void testHasMessages()
    {
        DefaultValidationResult result = new DefaultValidationResult.Builder()
                .addValidationMessage(
                        createValidationMessage(ValidationMessageLevel.ERROR))
                .addValidationMessage(
                        createValidationMessage(ValidationMessageLevel.WARNING))
                .build();
        assertTrue("No error messages", result
                .hasMessages(ValidationMessageLevel.ERROR));
        assertTrue("No warning messages", result
                .hasMessages(ValidationMessageLevel.WARNING));
    }

    /**
     * Tests the behavior of hasMessages() for a null level.
     */
    @Test
    public void testHasMessagesNull()
    {
        DefaultValidationResult result = new DefaultValidationResult.Builder()
                .addValidationMessage(
                        createValidationMessage(ValidationMessageLevel.WARNING))
                .build();
        assertFalse("Got messages with null level", result.hasMessages(null));
    }

    /**
     * Tests hasMessages() for the VALID instance.
     */
    @Test
    public void testHasMessagesValid()
    {
        assertFalse("Has error messages", DefaultValidationResult.VALID
                .hasMessages(ValidationMessageLevel.ERROR));
        assertFalse("Has warning messages", DefaultValidationResult.VALID
                .hasMessages(ValidationMessageLevel.WARNING));
    }

    /**
     * Tests whether messages of a given level can be queried.
     */
    @Test
    public void testGetValidationMessagesLevel()
    {
        final int errCount = 3;
        final int warnCount = 8;
        Collection<ValidationMessage> msgErr = createMessages(errCount,
                ValidationMessageLevel.ERROR);
        Collection<ValidationMessage> msgWarn = createMessages(warnCount,
                ValidationMessageLevel.WARNING);
        DefaultValidationResult.Builder builder = new DefaultValidationResult.Builder();
        DefaultValidationResult result = builder.addValidationMessages(msgErr)
                .addValidationMessages(msgWarn).build();
        assertTrue("Wrong error messages", JGuiraffeTestHelper
                .collectionEquals(msgErr, result
                        .getValidationMessages(ValidationMessageLevel.ERROR)));
        assertTrue("Wrong warning messages", JGuiraffeTestHelper
                .collectionEquals(msgWarn, result
                        .getValidationMessages(ValidationMessageLevel.WARNING)));
        verify();
    }

    /**
     * Tries to modify a list with messages returned by the result object. This
     * should not be allowed.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetValidationMessagesLevelModify()
    {
        DefaultValidationResult result = new DefaultValidationResult.Builder()
                .addValidationMessages(
                        createMessages(12, ValidationMessageLevel.WARNING))
                .build();
        Iterator<ValidationMessage> it = result.getValidationMessages(
                ValidationMessageLevel.WARNING).iterator();
        it.next();
        it.remove();
    }

    /**
     * Tests the behavior of getValidationMessages() for a null level.
     */
    @Test
    public void testGetValidationMessageLevelNull()
    {
        DefaultValidationResult result = new DefaultValidationResult.Builder()
                .addValidationMessage(createValidationMessage())
                .addValidationMessage(
                        createValidationMessage(ValidationMessageLevel.WARNING))
                .build();
        assertTrue("Got null-level messages", result
                .getValidationMessages(null).isEmpty());
    }
}

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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ChainValidator.
 *
 * @author Oliver Heger
 * @version $Id: TestChainValidator.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestChainValidator
{
    /** Constant for a test object. */
    private static final Object TEST_OBJ = new Object();

    /** Constant for the prefix of an error message. */
    private static final String ERR_MSG = "error";

    /** An array with the child validators used for testing. */
    private Validator[] children;

    /** The validator to be tested. */
    private ChainValidator validator;

    /** A counter for the created error messages. */
    private int errCounter;

    @Before
    public void setUp() throws Exception
    {
        validator = new ChainValidator();
    }

    /**
     * Initializes the test validator with a number of child validators. Each
     * child is a mock object.
     *
     * @param count the number of child validators to create
     */
    private void initChildValidators(int count)
    {
        children = new Validator[count];
        for (int i = 0; i < count; i++)
        {
            children[i] = EasyMock.createMock(Validator.class);
            validator.addChildValidator(children[i]);
        }
    }

    /**
     * Replays the mock objects for the child validators.
     */
    private void replay()
    {
        EasyMock.replay((Object[]) children);
    }

    /**
     * Verifies the mock objects for the child validators.
     */
    private void verify()
    {
        EasyMock.verify((Object[]) children);
    }

    /**
     * Creates a validation result indicating an error.
     *
     * @return the result object
     */
    private ValidationResult errorResult()
    {
        ValidationMessage vmsg = EasyMock.createMock(ValidationMessage.class);
        EasyMock.expect(vmsg.getMessage()).andStubReturn(ERR_MSG + errCounter);
        EasyMock.expect(vmsg.getLevel())
                .andReturn(ValidationMessageLevel.ERROR).anyTimes();
        errCounter++;
        EasyMock.replay(vmsg);
        return new DefaultValidationResult.Builder().addValidationMessage(vmsg).build();
    }

    /**
     * Creates a context that will be later used in a wrapped context.
     *
     * @return the context mock
     */
    private TransformerContext setUpContext()
    {
        TransformerContext ctx = EasyMock.createMock(TransformerContext.class);
        EasyMock.expect(ctx.properties()).andReturn(
                new HashMap<String, Object>()).times(1, 2);
        EasyMock.replay(ctx);
        return ctx;
    }

    /**
     * Creates a map with test properties.
     *
     * @return the map with the properties
     */
    private Map<String, Object> createTestProperties()
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("test", this);
        return props;
    }

    /**
     * Tests a wrapped context.
     *
     * @param c the context to check
     * @param ctx the expected parent context
     */
    private void checkWrappedContext(TransformerContext c,
            TransformerContext ctx)
    {
        assertTrue("Wrong context returned: " + c,
                c instanceof TransformerContextPropertiesWrapper);
        TransformerContextPropertiesWrapper cw = (TransformerContextPropertiesWrapper) c;
        assertEquals("Wrong wrapped context", ctx, cw.getWrappedContext());
        Map<String, Object> props = createTestProperties();
        Map<String, Object> props2 = cw.properties();
        for (String key : props.keySet())
        {
            assertEquals("Wrong property " + key, props.get(key), props2
                    .get(key));
        }
        EasyMock.verify(ctx);
    }

    /**
     * Tests a newly created object.
     */
    @Test
    public void testInit()
    {
        assertEquals("Wrong number of child validators", 0, validator.size());
        assertTrue("No short evaluation set", validator.isShortEvaluation());
    }

    /**
     * Tests adding child validators.
     */
    @Test
    public void testAddChildValidator()
    {
        final int count = 10;
        initChildValidators(count);
        replay();
        assertEquals("Wrong number of child validators", count, validator
                .size());
        for (int i = 0; i < count; i++)
        {
            assertEquals("Wrong child at index " + i, children[i], validator
                    .getChildValidator(i));
        }
        verify();
    }

    /**
     * Tries adding a null child validator. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddChildValidatorNull()
    {
        validator.addChildValidator(null);
    }

    /**
     * Tests obtaining a child at an invalid index. This should cause an
     * exception.
     */
    @Test
    public void testGetChildValidatorIllegalIndex()
    {
        final int count = 5;
        initChildValidators(count);
        replay();
        try
        {
            validator.getChildValidator(count);
            fail("Could obtain child at invalid index!");
        }
        catch (IndexOutOfBoundsException iex)
        {
            verify();
        }
    }

    /**
     * Tests querying the context for a child when no custom properties were
     * set.
     */
    @Test
    public void testGetContextForChildValidatorNoProperties()
    {
        TransformerContext ctx = EasyMock.createMock(TransformerContext.class);
        EasyMock.replay(ctx);
        initChildValidators(1);
        replay();
        assertEquals("Wrong context", ctx, validator
                .getContextForChildValidator(0, ctx));
        verify();
        EasyMock.verify(ctx);
    }

    /**
     * Tests querying the context for a child when custom properties were set.
     */
    @Test
    public void testGetContextForChildValidatorWithProperties()
    {
        TransformerContext ctx = setUpContext();
        Validator child = EasyMock.createMock(Validator.class);
        EasyMock.replay(child);
        validator.addChildValidator(child, createTestProperties());
        TransformerContext c = validator.getContextForChildValidator(0, ctx);
        checkWrappedContext(c, ctx);
        EasyMock.verify(child);
    }

    /**
     * Tests querying the context for a child at an invalid index.
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetContextForChildValidatorInvalidIndex()
    {
        validator.getContextForChildValidator(0, null);
    }

    /**
     * Tests the isValid() method for a valid object.
     */
    @Test
    public void testIsValidShortEvaluationValid()
    {
        TransformerContext ctx = setUpContext();
        final int count = 8;
        initChildValidators(count);
        for (Validator v : children)
        {
            EasyMock.expect(v.isValid(TEST_OBJ, ctx)).andReturn(
                    DefaultValidationResult.VALID);
        }
        replay();
        assertTrue("Wrong validation result", validator.isValid(TEST_OBJ, ctx)
                .isValid());
        verify();
        EasyMock.verify(ctx);
    }

    /**
     * Tests the isValid() method in short evaluation mode.
     */
    @Test
    public void testIsValidShortEvaluation()
    {
        TransformerContext ctx = setUpContext();
        final int count = 10;
        final int errIdx = 4;
        initChildValidators(count);
        for (int i = 0; i < errIdx; i++)
        {
            EasyMock.expect(children[i].isValid(TEST_OBJ, ctx)).andReturn(
                    DefaultValidationResult.VALID);
        }
        EasyMock.expect(children[errIdx].isValid(TEST_OBJ, ctx)).andReturn(
                errorResult());
        replay();
        ValidationResult vr = validator.isValid(TEST_OBJ, ctx);
        assertFalse("Wrong validation result", vr.isValid());
        Collection<ValidationMessage> msgs = vr.getValidationMessages();
        assertEquals("Wrong number of messages", 1, msgs.size());
        ValidationMessage msg = msgs.iterator().next();
        assertEquals("Wrong message", ERR_MSG + "0", msg.getMessage());
        verify();
        EasyMock.verify(ctx);
    }

    /**
     * Tests the isValid() method when short evaluation is turned off.
     */
    @Test
    public void testIsValidNoShortEvaluation()
    {
        TransformerContext ctx = setUpContext();
        final Integer[] errIdx = {
                1, 5, 6, 10
        };
        final int count = 16;
        initChildValidators(count);
        validator.setShortEvaluation(false);
        Set<Integer> errIndices = new HashSet<Integer>(Arrays.asList(errIdx));
        for (int i = 0; i < count; i++)
        {
            ValidationResult vr = errIndices.contains(i) ? errorResult()
                    : DefaultValidationResult.VALID;
            EasyMock.expect(children[i].isValid(TEST_OBJ, ctx)).andReturn(vr);
        }
        replay();
        ValidationResult vr = validator.isValid(TEST_OBJ, ctx);
        assertFalse("Wrong validation result", vr.isValid());
        Collection<ValidationMessage> msgs = vr.getValidationMessages();
        assertEquals("Wrong number of messages", errIdx.length, msgs.size());
        Iterator<ValidationMessage> it = msgs.iterator();
        for (int i = 0; i < errIdx.length; i++)
        {
            ValidationMessage msg = it.next();
            assertEquals("Wrong message at " + i, ERR_MSG + i, msg.getMessage());
        }
        verify();
        EasyMock.verify(ctx);
    }

    /**
     * Tests the isValid() method for a validator without children.
     */
    @Test
    public void testIsValidNoChildren()
    {
        TransformerContext ctx = EasyMock.createMock(TransformerContext.class);
        EasyMock.replay(ctx);
        assertTrue("Wrong validation result for empty validator", validator
                .isValid(TEST_OBJ, ctx).isValid());
        EasyMock.verify(ctx);
    }

    /**
     * Tests whether a correct context is passed to the child validators in the
     * isValid() method.
     */
    @Test
    public void testIsValidProperties()
    {
        final TransformerContext parentContext = setUpContext();
        validator.addChildValidator(new Validator()
        {
            public ValidationResult isValid(Object o, TransformerContext ctx)
            {
                checkWrappedContext(ctx, parentContext);
                return DefaultValidationResult.VALID;
            }
        }, createTestProperties());
        assertTrue("Wrong validation result", validator.isValid(TEST_OBJ,
                parentContext).isValid());
    }

    /**
     * Tests whether properties can override the short evaluation setting for
     * the isValid() method.
     */
    @Test
    public void testIsValidNoShortEvaluationProperties()
    {
        TransformerContext ctx = EasyMock.createMock(TransformerContext.class);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ChainValidator.PROP_SHORT_EVAL, "no");
        EasyMock.expect(ctx.properties()).andReturn(props);
        EasyMock.replay(ctx);
        initChildValidators(2);
        EasyMock.expect(children[0].isValid(TEST_OBJ, ctx)).andReturn(
                errorResult());
        EasyMock.expect(children[1].isValid(TEST_OBJ, ctx)).andReturn(
                DefaultValidationResult.VALID);
        replay();
        assertFalse("Wrong validation result", validator.isValid(TEST_OBJ, ctx)
                .isValid());
        verify();
        EasyMock.verify(ctx);
    }
}

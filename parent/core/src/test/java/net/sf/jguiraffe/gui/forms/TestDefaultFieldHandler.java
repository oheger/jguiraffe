/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationMessageLevel;
import net.sf.jguiraffe.transform.ValidationResult;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link DefaultFieldHandler}.
 *
 * @author Oliver Heger
 * @version $Id: TestDefaultFieldHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDefaultFieldHandler
{
    /** Constant for a test data object. */
    private static final Object TEST_DATA = "My test data";

    /** Constant for a transformed test data object. */
    private static final Object TRANSFORMED_DATA = "Transformed Test data";

    /** The handler to be tested. */
    private DefaultFieldHandler handler;

    @Before
    public void setUp() throws Exception
    {
        handler = new DefaultFieldHandler();
    }

    /**
     * Creates mock objects for the transformers and validators of a field
     * handler.
     */
    private void setUpTransformerMocks()
    {
        handler.setSyntaxValidator(EasyMock.createMock(ValidatorWrapper.class));
        handler.setLogicValidator(EasyMock.createMock(ValidatorWrapper.class));
        handler.setReadTransformer(EasyMock
                .createMock(TransformerWrapper.class));
        handler.setWriteTransformer(EasyMock
                .createMock(TransformerWrapper.class));
        handler.setComponentHandler(new ComponentHandlerImpl());
    }

    /**
     * Replays the mocks for the transformers and validators.
     */
    private void replayMocks()
    {
        EasyMock.replay(handler.getSyntaxValidator(),
                handler.getLogicValidator(), handler.getReadTransformer(),
                handler.getWriteTransformer());
    }

    /**
     * Verifies the mocks for the transformers and validators.
     */
    private void verifyMocks()
    {
        EasyMock.verify(handler.getSyntaxValidator(),
                handler.getLogicValidator(), handler.getReadTransformer(),
                handler.getWriteTransformer());
    }

    /**
     * Tests if a newly created handler is correctly initialized.
     */
    @Test
    public void testNewHandler()
    {
        assertEquals("Wrong field validator", DummyWrapper.INSTANCE,
                handler.getSyntaxValidator());
        assertEquals("Wrong form validator", DummyWrapper.INSTANCE,
                handler.getLogicValidator());
        assertEquals("Wrong read transformer", DummyWrapper.INSTANCE,
                handler.getReadTransformer());
        assertEquals("Wrong write transformer", DummyWrapper.INSTANCE,
                handler.getWriteTransformer());
        assertNull("Component handler is set", handler.getComponentHandler());
        assertNull("A type is set", handler.getType());
        assertNull("A property name is set", handler.getPropertyName());
    }

    /**
     * Tests initializing the field's data.
     */
    @Test
    public void testSetData()
    {
        setUpTransformerMocks();
        EasyMock.expect(handler.getWriteTransformer().transform(TEST_DATA))
                .andReturn(TRANSFORMED_DATA);
        replayMocks();
        handler.setData(TEST_DATA);
        verifyMocks();
        assertEquals("Wrong data in component handler", TRANSFORMED_DATA,
                handler.getComponentHandler().getData());
    }

    /**
     * Tests whether ClassCastExceptions are correctly handled.
     */
    @Test
    public void testSetDataClassCastException()
    {
        @SuppressWarnings("unchecked")
        ComponentHandler<Object> ch = EasyMock
                .createMock(ComponentHandler.class);
        ch.setData(TEST_DATA);
        EasyMock.expectLastCall().andThrow(new ClassCastException());
        EasyMock.replay(ch);
        handler.setComponentHandler(ch);
        try
        {
            handler.setData(TEST_DATA);
            fail("ClassCastException not detected!");
        }
        catch (FormRuntimeException frex)
        {
            EasyMock.verify(ch);
        }
    }

    /**
     * Helper method for preparing a validation test.
     *
     * @return the component handler of the field handler
     */
    private ComponentHandlerImpl prepareValidationTest()
    {
        setUpTransformerMocks();
        ComponentHandlerImpl ch = (ComponentHandlerImpl) handler
                .getComponentHandler();
        ch.setType(String.class);
        ch.setData(TEST_DATA);
        return ch;
    }

    /**
     * Tests validation at the field level.
     */
    @Test
    public void testValidate()
    {
        prepareValidationTest();
        EasyMock.expect(handler.getSyntaxValidator().isValid(TEST_DATA))
                .andReturn(DefaultValidationResult.VALID);
        replayMocks();
        ValidationResult result = handler.validate(ValidationPhase.SYNTAX);
        assertTrue("Wrong validation result", result.isValid());
        verifyMocks();
    }

    /**
     * Tests validation at the form level.
     */
    @Test
    public void testValidateForm()
    {
        prepareValidationTest();
        EasyMock.expect(handler.getReadTransformer().transform(TEST_DATA))
                .andReturn(TRANSFORMED_DATA);
        EasyMock.expect(handler.getLogicValidator().isValid(TRANSFORMED_DATA))
                .andReturn(DefaultValidationResult.VALID);
        replayMocks();
        ValidationResult result = handler.validate(ValidationPhase.LOGIC);
        assertTrue("Wrong validation result", result.isValid());
        verifyMocks();
        assertEquals("Wrong data in handler", TRANSFORMED_DATA, handler
                .getData());
    }

    /**
     * Tests a validation if no component handler is set. This should cause an
     * exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testValidateNoCompHandler()
    {
        handler.validate(ValidationPhase.SYNTAX);
    }

    /**
     * Tests a failing validation at the form level.
     */
    @Test
    public void testFailedValidation()
    {
        ValidationMessage vm = EasyMock.createMock(ValidationMessage.class);
        EasyMock.expect(vm.getLevel()).andReturn(ValidationMessageLevel.ERROR)
                .anyTimes();
        EasyMock.replay(vm);
        prepareValidationTest();
        DefaultValidationResult vr = new DefaultValidationResult.Builder()
                .addValidationMessage(vm).build();
        EasyMock.expect(handler.getSyntaxValidator().isValid(TEST_DATA))
                .andReturn(DefaultValidationResult.VALID);
        EasyMock.expect(handler.getReadTransformer().transform(TEST_DATA))
                .andReturn(TRANSFORMED_DATA);
        EasyMock.expect(handler.getLogicValidator().isValid(TRANSFORMED_DATA))
                .andReturn(vr);
        replayMocks();
        assertTrue("Wrong result of field validation", handler.validate(
                ValidationPhase.SYNTAX).isValid());
        assertSame("Wrong result of form validation", vr, handler
                .validate(ValidationPhase.LOGIC));
        verifyMocks();
        assertNull("Data of handler was not reset", handler.getData());
        EasyMock.verify(vm);
    }
}

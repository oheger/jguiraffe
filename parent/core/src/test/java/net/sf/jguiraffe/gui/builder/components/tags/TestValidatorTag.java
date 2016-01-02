/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.forms.DefaultValidatorWrapper;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.ValidationPhase;
import net.sf.jguiraffe.gui.forms.ValidatorWrapper;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.Validator;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ValidatorTag.
 *
 * @author Oliver Heger
 * @version $Id: TestValidatorTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestValidatorTag
{
    /** An input component tag used as parent. */
    private InputComponentTag input;

    /** A validator that will be set at the input component. */
    private Validator validator;

    /** The tag to be tested. */
    private ValidatorTag tag;

    @Before
    public void setUp() throws Exception
    {
        JellyContext context = new JellyContext();
        tag = new ValidatorTag();
        tag.setContext(context);
        input = new TextFieldTag();
        input.setContext(context);
        ComponentBuilderData cdata = new ComponentBuilderData();
        cdata.initializeForm(new TransformerContextImpl(),
                new BeanBindingStrategy());
        cdata.put(context);
        validator = EasyMock.createMock(Validator.class);
    }

    /**
     * Returns the default transformer context.
     *
     * @return the default (i.e. global) transformer context
     */
    private TransformerContext getDefaultContext()
    {
        return ComponentBuilderData.get(tag.getContext())
                .getTransformerContext();
    }

    /**
     * Tests whether a correct validator wrapper was set.
     *
     * @param wrapper the wrapper to test
     * @param defCtx a flag whether the default transformer context is expected
     */
    private void checkValidator(ValidatorWrapper wrapper, boolean defCtx)
    {
        assertTrue("Wrong validator wrapper type: " + wrapper,
                wrapper instanceof DefaultValidatorWrapper);
        DefaultValidatorWrapper vw = (DefaultValidatorWrapper) wrapper;
        assertEquals("Wrong wrapped validator", validator, vw.getValidator());
        assertEquals("Wrong context", defCtx, getDefaultContext().equals(
                vw.getTransformerContext()));
    }

    /**
     * Tests creating a field validator.
     */
    @Test
    public void testFieldValidator() throws JellyTagException
    {
        tag.setAttribute(ValidatorTag.ATTR_PHASE, "Syntax");
        tag.handleInputComponentTag(input, validator);
        checkValidator(input.getFieldValidator(), true);
        assertNull("A form validator was set", input.getFormValidator());
    }

    /**
     * Tests creating a form validator.
     */
    @Test
    public void testFormValidator() throws JellyTagException
    {
        tag.setAttribute(ValidatorTag.ATTR_PHASE, "logic");
        tag.handleInputComponentTag(input, validator);
        checkValidator(input.getFormValidator(), true);
        assertNull("A field validator was set", input.getFieldValidator());
    }

    /**
     * Tests whether the standard validation phase is used if no phase is
     * provided.
     */
    @Test
    public void testValidatorNoPhase() throws JellyTagException
    {
        tag.handleInputComponentTag(input, validator);
        assertEquals("Wrong standard validation phase", ValidationPhase.SYNTAX,
                tag.getValidationPhase());
    }

    /**
     * Tests creating a validator when an invalid phase is specified. This
     * should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testValidatorInvalidPhase() throws JellyTagException
    {
        tag.setAttribute(ValidatorTag.ATTR_PHASE, "an invalid phase");
        tag.handleInputComponentTag(input, validator);
    }

    /**
     * Tests whether the validation phase can be obtained from a parent tag.
     */
    @Test
    public void testGetValidationPhaseFromParent() throws JellyTagException
    {
        ValidatorsTag parent = new ValidatorsTag();
        parent.setAttribute(ValidatorTag.ATTR_PHASE, "logic");
        tag.setParent(parent);
        assertEquals("Wrong validation phase", ValidationPhase.LOGIC, tag
                .getValidationPhase());
    }

    /**
     * Tests whether an own phase attributes overrides the value from the
     * parent.
     */
    @Test
    public void testGetValidationPhaseFromParentOverload()
            throws JellyTagException
    {
        ValidatorsTag parent = new ValidatorsTag();
        parent.setAttribute(ValidatorTag.ATTR_PHASE, "logic");
        tag.setParent(parent);
        tag.setAttribute(ValidatorTag.ATTR_PHASE, "Syntax");
        assertEquals("Wrong validation phase", ValidationPhase.SYNTAX, tag
                .getValidationPhase());
    }

    /**
     * Tests whether properties set for the validator are taken into account.
     */
    @Test
    public void testValidatorWithProperties() throws JellyTagException
    {
        tag.setAttribute(ValidatorTag.ATTR_PHASE, "SYNTAX");
        tag.setProperties(new HashMap<String, Object>());
        tag.handleInputComponentTag(input, validator);
        checkValidator(input.getFieldValidator(), false);
    }

    /**
     * Tests the ValidatorWrapper implementation provided by the tag.
     */
    @Test
    public void testValidatorWrapper()
    {
        DefaultValidatorWrapper wrapper = new DefaultValidatorWrapper(
                validator, getDefaultContext());
        final Object dataObj = "MyDataToValidate";
        EasyMock.expect(validator.isValid(dataObj, getDefaultContext()))
                .andReturn(DefaultValidationResult.VALID);
        EasyMock.replay(validator);
        assertEquals("Wrong validation result", DefaultValidationResult.VALID,
                wrapper.isValid(dataObj));
        EasyMock.verify(validator);
    }

    /**
     * Tests whether a ValidatorsTag as parent is specially treated.
     */
    @Test
    public void testHandleOtherParentValidatorsTag() throws JellyTagException
    {
        Validator validator = EasyMock.createMock(Validator.class);
        EasyMock.replay(validator);
        Map<String, Object> properties = new HashMap<String, Object>();
        tag.setProperties(properties);
        ValidatorsTagTestImpl parent = new ValidatorsTagTestImpl(validator,
                properties);
        assertTrue("Parent tag not processed", tag.handleOtherParent(parent,
                validator));
        assertTrue("Parent tag not invoked", parent.called);
        EasyMock.verify(validator);
    }

    /**
     * Tests the handleOtherParent() method for an unsupported parent tag.
     */
    @Test
    public void testHandleOtherParentUnknownTag() throws JellyTagException
    {
        Validator validator = EasyMock.createMock(Validator.class);
        EasyMock.replay(validator);
        assertFalse("Wrong result for not supported tag", tag
                .handleOtherParent(new LabelTag(), validator));
        EasyMock.verify(validator);
    }

    /**
     * A test implementation of the ValidatorsTag allowing us to check whether
     * the expected methods have been called.
     */
    private static class ValidatorsTagTestImpl extends ValidatorsTag
    {
        /** Stores the expected child validator. */
        private Validator validator;

        /** Stores the expected properties. */
        private Map<String, Object> properties;

        /** A flag whether the tag was called. */
        boolean called;

        public ValidatorsTagTestImpl(Validator v, Map<String, Object> props)
        {
            validator = v;
            properties = props;
        }

        @Override
        public void addChildValidator(Validator child, Map<String, Object> props)
        {
            called = true;
            assertEquals("Wrong validator passed", validator, child);
            assertSame("Wrong properties passed", properties, props);
        }
    }
}

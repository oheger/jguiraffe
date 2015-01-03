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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.assertEquals;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.forms.DefaultValidatorWrapper;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.ValidationPhase;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.transform.ChainValidator;
import net.sf.jguiraffe.transform.Validator;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.TagScript;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ValidatorsTag.
 *
 * @author Oliver Heger
 * @version $Id: TestValidatorsTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestValidatorsTag
{
    /** An input component tag used as parent. */
    private InputComponentTag input;

    /** An output object for executing the test tag. */
    private XMLOutput output;

    /** The tag to be tested. */
    private ValidatorsTag tag;

    @Before
    public void setUp() throws Exception
    {
        JellyContext context = new JellyContext();
        tag = new ValidatorsTag();
        tag.setContext(context);
        input = new TextFieldTag();
        input.setContext(context);
        tag.setParent(input);
        tag.setBody(new TagScript());
        tag.setAttribute(ValidatorTag.ATTR_PHASE, ValidationPhase.SYNTAX.name());
        output = new XMLOutput();
        ComponentBuilderData cdata = new ComponentBuilderData();
        cdata.initializeForm(new TransformerContextImpl(),
                new BeanBindingStrategy());
        cdata.put(context);
    }

    /**
     * Tests adding a child validator.
     */
    @Test
    public void testAddChildValidator() throws JellyTagException
    {
        Validator v = EasyMock.createMock(Validator.class);
        EasyMock.replay(v);
        tag.addChildValidator(v, null);
        tag.doTag(output);
        DefaultValidatorWrapper wrapper = (DefaultValidatorWrapper) input
                .getFieldValidator();
        ChainValidator cv = (ChainValidator) wrapper.getValidator();
        assertEquals("Wrong number of child validators", 1, cv.size());
        assertEquals("Wrong child validator", v, cv.getChildValidator(0));
        EasyMock.verify(v);
    }

    /**
     * Tests whether a chain validator is correctly initialized when stored as a
     * variable.
     */
    @Test
    public void testStoreVariable() throws JellyTagException
    {
        Validator v = EasyMock.createMock(Validator.class);
        EasyMock.replay(v);
        tag.addChildValidator(v, null);
        final String varName = "myVariable";
        tag.setAttribute(ValidatorTag.ATTR_VAR, varName);
        tag.setParent(null);
        tag.doTag(output);
        ChainValidator cv = (ChainValidator) tag.getContext().getVariable(
                varName);
        assertEquals("Wrong number of child validators", 1, cv.size());
        assertEquals("Wrong child validator", v, cv.getChildValidator(0));
        EasyMock.verify(v);
    }

    /**
     * Tests a tag with a class attribute setting an invalid base class.
     */
    @Test(expected = JellyTagException.class)
    public void testInvalidClass() throws JellyTagException
    {
        tag.setAttribute(ValidatorTag.ATTR_CLASS, getClass().getName());
        tag.doTag(output);
    }
}

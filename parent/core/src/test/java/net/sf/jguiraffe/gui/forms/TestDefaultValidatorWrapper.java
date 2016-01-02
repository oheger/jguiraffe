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
package net.sf.jguiraffe.gui.forms;

import static org.junit.Assert.assertSame;

import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.ValidationResult;
import net.sf.jguiraffe.transform.Validator;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code DefaultValidatorWrapper}.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class TestDefaultValidatorWrapper
{
    /** A mock validator. */
    private Validator validator;

    /** A mock transformer context. */
    private TransformerContext context;

    @Before
    public void setUp() throws Exception
    {
        validator = EasyMock.createMock(Validator.class);
        context = EasyMock.createMock(TransformerContext.class);
    }

    /**
     * Tries to create an instance without a validator.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoValidator()
    {
        new DefaultValidatorWrapper(null, context);
    }

    /**
     * Tries to create an instance without a transformer context.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoContext()
    {
        new DefaultValidatorWrapper(validator, null);
    }

    /**
     * Tests a validation operation.
     */
    @Test
    public void testIsValid()
    {
        final Object input = "an input";
        ValidationResult vres = EasyMock.createMock(ValidationResult.class);
        EasyMock.expect(validator.isValid(input, context)).andReturn(vres);
        EasyMock.replay(validator, context, vres);

        DefaultValidatorWrapper wrapper =
                new DefaultValidatorWrapper(validator, context);
        assertSame("Wrong result", vres, wrapper.isValid(input));
        EasyMock.verify(validator);
    }
}

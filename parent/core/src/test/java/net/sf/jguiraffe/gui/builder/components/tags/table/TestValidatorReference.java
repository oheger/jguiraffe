/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import net.sf.jguiraffe.gui.forms.DummyWrapper;
import net.sf.jguiraffe.gui.forms.ValidatorWrapper;
import net.sf.jguiraffe.transform.ValidationResult;

import org.easymock.EasyMock;
import org.junit.Test;

public class TestValidatorReference
{
    /**
     * Creates a mock for a validator wrapper to be referenced.
     *
     * @return the mock wrapper
     */
    private static ValidatorWrapper createWrapper()
    {
        ValidatorWrapper validator =
                EasyMock.createMock(ValidatorWrapper.class);
        EasyMock.replay(validator);
        return validator;
    }

    /**
     * Tests whether a validator can be passed to the constructor.
     */
    @Test
    public void testInitWithValidator()
    {
        ValidatorWrapper validator = createWrapper();
        ValidatorReference reference = new ValidatorReference(validator);
        assertSame("Wrong referenced validator", validator,
                reference.getValidator());
    }

    /**
     * Tests whether null can be passed to the constructor.
     */
    @Test
    public void testInitNoValidator()
    {
        ValidatorReference reference = new ValidatorReference(null);
        assertEquals("Wrong referenced validator", DummyWrapper.INSTANCE,
                reference.getValidator());
    }

    /**
     * Tests whether null can be passed to setTransformer().
     */
    @Test
    public void testSetValidatorUndefined()
    {
        ValidatorWrapper trans = createWrapper();
        ValidatorReference reference = new ValidatorReference(trans);
        reference.setValidator(null);
        assertSame("Wrong referenced validator", DummyWrapper.INSTANCE,
                reference.getValidator());
    }

    /**
     * Tests a validation.
     */
    @Test
    public void testIsValid()
    {
        ValidationResult result = EasyMock.createMock(ValidationResult.class);
        final Object input = "someInput";
        ValidatorWrapper validator =
                EasyMock.createMock(ValidatorWrapper.class);
        EasyMock.expect(validator.isValid(input)).andReturn(result);
        EasyMock.replay(validator, result);
        ValidatorReference reference = new ValidatorReference(null);

        reference.setValidator(validator);
        assertSame("Wrong validation result", result, reference.isValid(input));
        EasyMock.verify(validator);
    }
}

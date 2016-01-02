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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import net.sf.jguiraffe.transform.DefaultValidationResult;

import org.junit.Test;

/**
 * Test class for {@code DummyWrapper}.
 */
public class TestDummyWrapper
{
    /**
     * Tests a transformation.
     */
    @Test
    public void testTransform()
    {
        final Object obj = 42;
        assertEquals("Wrong result", obj, DummyWrapper.INSTANCE.transform(obj));
    }

    /**
     * Tests a validation.
     */
    @Test
    public void testValidate()
    {
        assertSame("Wrong result", DefaultValidationResult.VALID,
                DummyWrapper.INSTANCE.isValid(this));
    }
}

/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.window.ctrl;

import static org.junit.Assert.assertEquals;
import net.sf.jguiraffe.transform.ValidationMessageLevel;
import net.sf.jguiraffe.transform.ValidationResult;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@code FieldValidationStatus}.
 *
 * @author Oliver Heger
 * @version $Id: TestFieldValidationStatus.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFieldValidationStatus
{
    /**
     * Tests getStatus() for a null result with visited = true.
     */
    @Test
    public void testGetStatusNullVisited()
    {
        assertEquals("Wrong status", FieldValidationStatus.VALID,
                FieldValidationStatus.getStatus(null, true));
    }

    /**
     * Tests getStatus() for a null result with visited = false.
     */
    @Test
    public void testGetStatusNullNotVisited()
    {
        assertEquals("Wrong status", FieldValidationStatus.NOT_VISITED_VALID,
                FieldValidationStatus.getStatus(null, false));
    }

    /**
     * Helper method for testing getStatus() for a validation result with
     * warnings.
     *
     * @param expected the expected outcome
     * @param visited the visited flag
     */
    private void checkGetStatusWarning(FieldValidationStatus expected,
            boolean visited)
    {
        ValidationResult vres = EasyMock.createMock(ValidationResult.class);
        EasyMock.expect(vres.isValid()).andReturn(Boolean.TRUE);
        EasyMock.expect(vres.hasMessages(ValidationMessageLevel.WARNING))
                .andReturn(Boolean.TRUE);
        EasyMock.replay(vres);
        assertEquals("Wrong status", expected, FieldValidationStatus.getStatus(
                vres, visited));
        EasyMock.verify(vres);
    }

    /**
     * Tests getStatus() for a result with warnings and visited = true.
     */
    @Test
    public void testGetStatusWarningVisited()
    {
        checkGetStatusWarning(FieldValidationStatus.WARNING, true);
    }

    /**
     * Tests getStatus() for a result with warnings and visited = false.
     */
    @Test
    public void testGetStatusWarningNotVisited()
    {
        checkGetStatusWarning(FieldValidationStatus.NOT_VISITED_WARNING, false);
    }

    /**
     * Helper method for testing getStatus() for an invalid validation result.
     *
     * @param expected the expected outcome
     * @param visited the visited flag
     */
    private void checkGetStatusInvalid(FieldValidationStatus expected,
            boolean visited)
    {
        ValidationResult vres = EasyMock.createMock(ValidationResult.class);
        EasyMock.expect(vres.isValid()).andReturn(Boolean.FALSE);
        EasyMock.replay(vres);
        assertEquals("Wrong status", expected, FieldValidationStatus.getStatus(
                vres, visited));
        EasyMock.verify(vres);
    }

    /**
     * Tests getStatus() for an invalid result and visited = true.
     */
    @Test
    public void testGetStatusInvalidVisited()
    {
        checkGetStatusInvalid(FieldValidationStatus.INVALID, true);
    }

    /**
     * Tests getStatus() for an invalid result and visited = false.
     */
    @Test
    public void testGetStatusInvalidNotVisited()
    {
        checkGetStatusInvalid(FieldValidationStatus.NOT_VISITED_INVALID, false);
    }
}

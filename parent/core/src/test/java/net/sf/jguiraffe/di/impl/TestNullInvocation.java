/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.di.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.di.DependencyProvider;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@link NullInvocation}.
 *
 * @author Oliver Heger
 * @version $Id: TestNullInvocation.java 207 2012-02-09 07:30:13Z oheger $
 */
@SuppressWarnings("deprecation")
public class TestNullInvocation
{
    /**
     * Tests querying the dependencies. There should be none.
     */
    @Test
    public void testGetParameterDependencies()
    {
        assertTrue("Got dependencies", NullInvocation.INSTANCE
                .getParameterDependencies().isEmpty());
    }

    /**
     * Tests the invoke() implementation. We can only test that the objects
     * involved are not touched.
     */
    @Test
    public void testInvoke()
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        assertNull("Wrong result of invoke (1)", NullInvocation.INSTANCE
                .invoke(depProvider, this));
        assertNull("Wrong result of invoke (2)", NullInvocation.INSTANCE
                .invoke(depProvider, null));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests the string representation of the null invocation.
     */
    @Test
    public void testToString()
    {
        String s = NullInvocation.INSTANCE.toString();
        assertEquals("Wrong string: " + s,
                NullInvocation.STRING_REPRESENTATION, s);
    }
}

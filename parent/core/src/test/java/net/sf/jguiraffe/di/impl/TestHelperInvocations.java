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
package net.sf.jguiraffe.di.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.impl.HelperInvocations;
import net.sf.jguiraffe.di.impl.Invokable;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@code HelperInvocations}.
 *
 * @author Oliver Heger
 * @version $Id: TestHelperInvocations.java 208 2012-02-11 20:57:33Z oheger $
 */
public class TestHelperInvocations
{
    /**
     * Tests whether no dependencies are required by the dummy implementations.
     */
    @Test
    public void testGetParameterDependencies()
    {
        for (Invokable inv : HelperInvocations.values())
        {
            assertTrue("Got dependencies for " + inv, inv
                    .getParameterDependencies().isEmpty());
        }
    }

    /**
     * Creates a mock for the dependency provider which does not expect any
     * method invocations.
     *
     * @return the mock dependency provider
     */
    private static DependencyProvider setUpDependencyProvider()
    {
        DependencyProvider depProvider =
                EasyMock.createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        return depProvider;
    }

    /**
     * Tests the invoke() implementation of NullInvocation.
     */
    @Test
    public void testInvokeNullInvocation()
    {
        DependencyProvider depProvider = setUpDependencyProvider();
        assertNull("Wrong result",
                HelperInvocations.NULL_INVOCATION.invoke(depProvider, this));
    }

    /**
     * Tests the invoke() implementation of IdentityInvocation.
     */
    @Test
    public void testInvokeIdentityInvocation()
    {
        DependencyProvider depProvider = setUpDependencyProvider();
        Object target = new Object();
        assertSame("Wrong result", target,
                HelperInvocations.IDENTITY_INVOCATION.invoke(depProvider,
                        target));
    }
}

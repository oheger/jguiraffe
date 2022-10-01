/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.ReflectionTestClass;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for ConstructorInvocation.
 *
 * @author Oliver Heger
 * @version $Id: TestConstructorInvocation.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestConstructorInvocation extends AbstractInvocationTest
{
    @Override
    protected Invocation createInvocation(ClassDescription targetClass,
            ClassDescription[] types, Dependency... values)
    {
        return new ConstructorInvocation(targetClass, types, values);
    }

    /**
     * Tries to create an instance without specifying a target class. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoTargetClass()
    {
        createInvocation(null, PARAM_TYPES, paramDeps);
    }

    /**
     * Tests the invoke() method. This works when all parameter types are known
     * and for unknown types as well. By specifying a target parameter it can be
     * decided, which of the two invoke() methods is to be called.
     *
     * @param paramTypes the array with the parameter types
     * @param target if <b>null</b>, the invoke() method without a target
     * object is called; otherwise the variant with a target parameter is used
     */
    private void checkInvoke(ClassDescription[] paramTypes, Object target)
    {
        DependencyProvider depProvider = setUpDependencyProvider();
        ConstructorInvocation inv = new ConstructorInvocation(TARGET_CLASS,
                paramTypes, paramDeps);
        ReflectionTestClass obj = (ReflectionTestClass) ((target == null) ? inv
                .invoke(depProvider) : inv.invoke(depProvider, target));
        assertEquals("Wrong string property", PARAM_VALUES[0], obj
                .getStringProp());
        assertEquals("Wrong int property", PARAM_VALUES[1], obj.getIntProp());
        EasyMock.verify(depProvider);
    }

    /**
     * Tests the invoke() method when all parameter types are known.
     */
    @Test
    public void testInvokeParamTypes()
    {
        checkInvoke(PARAM_TYPES, null);
    }

    /**
     * Tests the invoke() method when not all parameter types are known.
     */
    @Test
    public void testInvokeParamValues()
    {
        checkInvoke(getPartlyKnowParameterTypes(), null);
    }

    /**
     * Tests the invoke() method that takes a target object when all parameter
     * types are known.
     */
    @Test
    public void testInvokeParamTypesTarget()
    {
        checkInvoke(PARAM_TYPES, this);
    }

    /**
     * Tests the invoke() method that takes a target object when not all
     * parameter types are known.
     */
    @Test
    public void testInvokeParamValuesTarget()
    {
        checkInvoke(getPartlyKnowParameterTypes(), this);
    }

    /**
     * Tries to call invoke() without specifying a dependency provider. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvokeNullDependencyProvider()
    {
        new ConstructorInvocation(TARGET_CLASS, PARAM_TYPES, paramDeps)
                .invoke(null);
    }

    /**
     * Tests the toString() method.
     */
    @Test
    public void testToString()
    {
        String s = createInvocation(TARGET_CLASS, PARAM_TYPES,
                paramDeps).toString();
        assertTrue("Constructor name not found: " + s, s.indexOf("[ "
                + TARGET_CLASS
                + ConstructorInvocation.CONSTR_NAME + '(') >= 0);
    }
}

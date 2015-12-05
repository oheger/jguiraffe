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
package net.sf.jguiraffe.di.impl.providers;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.ClassDescription;

import org.easymock.EasyMock;
import org.junit.Assert;

/**
 * A helper class that provides some default functionality for testing simple
 * bean providers, especially providers that use {@code Invocation}
 * objects for creating bean instances. This class defines some constants and
 * static utility methods that can be used by corresponding test classes.
 *
 * @author Oliver Heger
 * @version $Id: SimpleBeanProviderTestHelper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SimpleBeanProviderTestHelper
{
    /** Constant for the string property. */
    public static final String STR_VALUE = "A test string";

    /** Constant for the int property. */
    public static final int INT_VALUE = 42;

    /** An array with the parameter types. */
    public static final Class<?>[] PARAM_TYPES = {
            String.class, Integer.TYPE
    };

    /** An array with the values of the parameters for the invocation. */
    public static final Object[] PARAM_VALUES =
    { STR_VALUE, INT_VALUE };

    /**
     * Returns an array with the dependencies for the typical invocation
     * parameters. The values are defined by the {@code PARAM_VALUES}
     * array.
     *
     * @return an array with the dependencies for the invocation parameters
     */
    public static Dependency[] getParameterDependencies()
    {
        Dependency[] result = new Dependency[PARAM_VALUES.length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = ConstantBeanProvider.getInstance(PARAM_VALUES[i]);
        }
        return result;
    }

    /**
     * Returns class description objects for the parameter types of the
     * invocation.
     *
     * @return an array with class descriptions for the parameter types
     */
    public static ClassDescription[] getParameterClassDescriptions()
    {
        ClassDescription[] descs = new ClassDescription[PARAM_TYPES.length];
        for (int i = 0; i < PARAM_TYPES.length; i++)
        {
            descs[i] = ClassDescription.getInstance(PARAM_TYPES[i]);
        }
        return descs;
    }

    /**
     * Creates a mock for a DependencyProvider, which is able to resolve the
     * test class. The call for the loadClass() method is optional, i.e. if a
     * ClassDedescription caches its value, this won't be a problem.
     *
     * @param replay a flag whether the replay() method is to be called
     * @return the initialized mock object
     */
    public static DependencyProvider setUpDependencyProvider(boolean replay)
    {
        DependencyProvider mock = EasyMock.createMock(DependencyProvider.class);
        mock.loadClass(ReflectionTestClass.class.getName(), null);
        EasyMock.expectLastCall().andReturn(ReflectionTestClass.class)
                .times(0, 1);
        EasyMock.expect(mock.getInvocationHelper())
                .andReturn(new InvocationHelper()).anyTimes();
        if (replay)
        {
            EasyMock.replay(mock);
        }
        return mock;
    }

    /**
     * Creates a dependency provider mock object, which can be used for testing
     * a getBean() operation. This mock expects calls to its getDependentBean()
     * method. The return values for these calls are obtained from the given
     * bean provider's dependencies.
     *
     * @param provider the bean provider
     * @return the initialized mock object
     */
    public static DependencyProvider setUpDepProviderForGetBean(
            BeanProvider provider)
    {
        DependencyProvider mock = setUpDependencyProvider(false);
        for (Dependency d : provider.getDependencies())
        {
            ConstantBeanProvider p = (ConstantBeanProvider) d;
            EasyMock.expect(mock.getDependentBean(d)).andReturn(p.getBean());
        }
        EasyMock.replay(mock);
        return mock;
    }

    /**
     * Tests a newly created bean instance. This method checks whether the
     * passed in object is an instance of {@code ReflectionTestClass} and
     * whether its properties are correctly set.
     *
     * @param o the object to check
     */
    public static void checkTestInstance(Object o)
    {
        Assert.assertTrue("Object is of incorrect type: " + o,
                o instanceof ReflectionTestClass);
        ReflectionTestClass obj = (ReflectionTestClass) o;
        Assert.assertEquals("Wrong value of string property",
                SimpleBeanProviderTestHelper.STR_VALUE, obj.getStringProp());
        Assert.assertEquals("Wrong value of int property",
                SimpleBeanProviderTestHelper.INT_VALUE, obj.getIntProp());
    }
}

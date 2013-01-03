/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import junit.framework.Assert;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.impl.ClassDescription;

/**
 * <p>
 * A specialized mock implementation of <code>ClassDescription</code>.
 * </p>
 * <p>
 * This class is useful for testing components that have to deal with class
 * descriptions that have to be dynamically resolved. Especially it can be
 * tested, whether <code>getTargetClass()</code> is called with an expected
 * <code>ClassLoaderProvider</code>.
 * </p>
 * <p>
 * Usage of this class is as follows: Create a new instance and pass the target
 * class as constructor parameter. Call <code>expectClassLoaderProvider()</code>
 * and <code>expectResolveCount()</code> for setting the expected class loader
 * provider and the expected number of <code>getTargetClass()</code>
 * invocations (default for the latter is 1). Then the object can be passed to
 * the test class. Finally call <code>verify()</code> to check whether all
 * expectations are met.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ClassDescriptionMock.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ClassDescriptionMock extends ClassDescription
{
    /** The expected class loader provider. */
    private ClassLoaderProvider expectedLoaderProvider;

    /** The expected number of invocations. */
    private int expectedResolveCount;

    /** A counter for the actual invocations. */
    private int invocationCount;

    /**
     * Creates a new instance of <code>ClassDescriptionMock</code> and sets
     * the class.
     *
     * @param targetCls the class for this description (not <b>null</b>)
     */
    public ClassDescriptionMock(Class<?> targetCls)
    {
        super(targetCls, targetCls.getName(), null);
        expectedResolveCount = 1;
    }

    /**
     * Prepares this object to expect the specified class loader provider.
     * <code>getTargetClass()</code> will check if this provider is passed in.
     *
     * @param clp the expected provider
     * @return a reference to this object for method chaining
     */
    public ClassDescriptionMock expectClassLoaderProvider(
            ClassLoaderProvider clp)
    {
        expectedLoaderProvider = clp;
        return this;
    }

    /**
     * Prepares this object to expect the given number of
     * <code>getTargetClass()</code> invocations.
     *
     * @param cnt the expected number
     * @return a reference to this object for method chaining
     */
    public ClassDescriptionMock expectResolveCount(int cnt)
    {
        expectedResolveCount = cnt;
        return this;
    }

    /**
     * Verifies this object. Checks via assert whether the expectations were
     * met.
     */
    public void verify()
    {
        Assert.assertEquals("Wrong number of getTargetClass() invocations",
                expectedResolveCount, invocationCount);
    }

    /**
     * Returns the target class. Records this invocation and checks the
     * parameters.
     *
     * @param depProvider the class loader provider
     * @return the resolved class
     */
    @Override
    public Class<?> getTargetClass(ClassLoaderProvider depProvider)
    {
        Assert.assertEquals("Wrong class loader provider",
                expectedLoaderProvider, depProvider);
        invocationCount++;
        return super.getTargetClass(depProvider);
    }
}

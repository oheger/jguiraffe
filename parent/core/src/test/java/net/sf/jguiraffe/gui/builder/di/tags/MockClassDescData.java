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
package net.sf.jguiraffe.gui.builder.di.tags;

import static org.junit.Assert.assertSame;
import net.sf.jguiraffe.di.ClassLoaderProvider;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specialized class description data implementation with a mock
 * resolveClass() implementation.
 * </p>
 * <p>
 * This mock implementation of {@code ClassDescData} can be used for testing tag
 * handler classes that need to deal with the dynamic creation of classes.
 * Especially, it can be tested whether the {@code resolveClass()} method is
 * called with the expected class loader provider.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: MockClassDescData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class MockClassDescData extends ClassDescData
{
    /** The expected class loader provider. */
    private final ClassLoaderProvider clProvider;

    /** The class to be returned by {@code resolveClass()}. */
    private final Class<?> targetClass;

    /**
     * Creates a new instance of {@code MockClassDescData}.
     *
     * @param clp the expected class loader provider
     * @param targetcls the class to be returned by
     *        {@link #resolveClass(ClassLoaderProvider)}
     */
    public MockClassDescData(ClassLoaderProvider clp, Class<?> targetcls)
    {
        clProvider = clp;
        targetClass = targetcls;
    }

    /**
     * Mock implementation of this method. Tests whether the specified provider
     * is the expected one. Always returns the test class.
     */
    @Override
    public Class<?> resolveClass(ClassLoaderProvider clProvider)
            throws JellyTagException
    {
        assertSame("Wrong provider", this.clProvider, clProvider);
        return targetClass;
    }
}

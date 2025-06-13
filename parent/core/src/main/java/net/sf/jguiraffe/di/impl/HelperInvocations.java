/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import java.util.Collections;
import java.util.List;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;

/**
 * <p>
 * An enumeration class defining some simple helper {@code Invokable}
 * implementations.
 * </p>
 * <p>
 * There are cases where an {@code Invokable} is needed, but no concrete value
 * object is available. For instance, when creating a bean an initializer script
 * may be supported, but it is optional. This enumeration class provides helper
 * objects which can be used for optional invocations rather than doing
 * <b>null</b> checks all the time. So this is an application of the <em>null
 * object pattern</em>.
 * </p>
 * <p>
 * The constants defined by this class are dummy implementations which do not
 * actually perform any meaningful action. They differ in the values returned by
 * their implementation of the {@code invoke()} method. Because these
 * implementations are state-less, they can be defined as enumeration constants
 * and shared between all interested parties.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: HelperInvocations.java 208 2012-02-11 20:57:33Z oheger $
 * @since 1.1
 */
public enum HelperInvocations implements Invokable
{
    /**
     * A specialized {@code Invokable} implementation which always returns
     * <b>null</b> in its {@code invoke()} implementation.
     */
    NULL_INVOCATION
    {
        /**
         * {@inheritDoc} This implementation always returns <b>null</b>.
         */
        public Object invoke(DependencyProvider depProvider, Object target)
        {
            return null;
        }
    },

    /**
     * An implementation of {@code Invokable} which realizes an identity
     * invocation. It always returns the object passed in as invocation target
     * without any further modifications.
     */
    IDENTITY_INVOCATION
    {
        /**
         * {@inheritDoc} This implementation always returns the target object.
         */
        public Object invoke(DependencyProvider depProvider, Object target)
        {
            return target;
        }
    };

    /**
     * Returns the dependencies for this invocation. This implementation always
     * returns an empty list.
     *
     * @return the dependencies of this {@code Invokable}
     */
    public List<Dependency> getParameterDependencies()
    {
        return Collections.emptyList();
    }
}

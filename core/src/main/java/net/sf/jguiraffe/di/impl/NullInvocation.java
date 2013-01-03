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

import java.util.Collections;
import java.util.List;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;

/**
 * <p>
 * A dummy implementation of the {@code Invokable} interface that does not
 * perform any action when it is invoked.
 * </p>
 * <p>
 * This class (or the default instance provided through the {@code INSTANCE}
 * constant) can be used as a default place holder for an {@code Invokable}
 * object. All methods are dummy implementations that have no side effect. So
 * instead of checking for a <b>null</b> {@code Invokable} reference, this dummy
 * invocation can be used.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: NullInvocation.java 208 2012-02-11 20:57:33Z oheger $
 * @deprecated Use {@link HelperInvocations} instead; it provides some dummy
 * implementations of the {@code Invokable} interface which are useful in some
 * situations where a full-blown implementation is not required.
 */
@Deprecated
public final class NullInvocation implements Invokable
{
    /** The default instance of this class. */
    public static final NullInvocation INSTANCE = new NullInvocation();

    /** Constant for the string to be returned by toString(). */
    static final String STRING_REPRESENTATION = "<null invocation>";

    /**
     * Returns the dependencies of this {@code Invokable}. This is always an
     * empty list.
     *
     * @return the dependencies of this object
     */
    public List<Dependency> getParameterDependencies()
    {
        return Collections.emptyList();
    }

    /**
     * Invokes this {@code Invokable}. This is just an empty dummy.
     *
     * @param depProvider the dependency provider
     * @param target the target object
     * @return the result of the invocation, which is always <b>null</b> in this
     *         case
     */
    public Object invoke(DependencyProvider depProvider, Object target)
    {
        return null;
    }

    /**
     * Returns a string representation of this object. This implementation just
     * returns the text {@literal <no initializer>}.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        return STRING_REPRESENTATION;
    }
}

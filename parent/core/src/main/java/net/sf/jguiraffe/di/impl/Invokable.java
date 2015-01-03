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

import java.util.List;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;

/**
 * <p>
 * Definition of an interface for objects that perform some kind of method
 * invocation.
 * </p>
 * <p>
 * This interface allows handling of different invocations (e.g. constructor
 * invocation, method invocation, etc.) in a generic way. It defines an
 * <code>invoke()</code> method with a generic signature. It also demands that
 * invokable objects must be able to return a list of dependencies they require.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Invokable.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface Invokable
{
    /**
     * Returns a list with all dependencies required for this invocation.
     * Typically these dependencies define the parameters for the method call to
     * be performed.
     *
     * @return a list with the dependencies required by this invocation
     */
    List<Dependency> getParameterDependencies();

    /**
     * Performs the invocation. This is the main method of an
     * <code>Invokable</code> object, which actually executes a method. The
     * passed in parameters should satisfy all requirements of an arbitrary
     * invocation. Some of them may not be needed for a concrete invocation
     * (e.g. a constructor invocation does not require a target object).
     *
     * @param depProvider the dependency provider, which can be used for
     * resolving the parameter dependencies
     * @param target the target object, on which the invocation should be
     * performed
     * @return the result of the invocation
     */
    Object invoke(DependencyProvider depProvider, Object target);
}

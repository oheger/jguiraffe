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

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;

/**
 * <p>
 * A special <code>Invocation</code> implementation for setting properties.
 * </p>
 * <p>
 * This <code>Invocation</code> implementation is initialized with a property
 * name and value (the latter one is provided as a {@link Dependency} object. In
 * its <code>invoke()</code> method it will set this property on the target
 * object.
 * </p>
 * <p>
 * This is more or less a convenience class because similar results could be
 * achieved by using {@link MethodInvocation} and specifying the name of the set
 * method corresponding to the property and the property value as parameter
 * dependency. Setting the property is done using the
 * {@link net.sf.jguiraffe.di.InvocationHelper InvocationHelper} class. This
 * ensures that required type conversions are done automatically.
 * </p>
 * <p>
 * Once initialized, an instance is immutable. So it can easily be shared
 * between multiple components and threads without having to care about
 * synchronization issues.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SetPropertyInvocation.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SetPropertyInvocation extends Invocation implements Invokable
{
    /** Stores the name of the property to set. */
    private String propertyName;

    /**
     * Creates a new instance of <code>SetPropertyInvocation</code> and
     * initializes it.
     *
     * @param propName the name of the property to set (must not be <b>null</b>)
     * @param propValue a dependency that defines the property's value (must not
     * be <b>null</b>)
     * @throws IllegalArgumentException if the property name or its value is
     * undefined
     */
    public SetPropertyInvocation(String propName, Dependency propValue)
    {
        super(null, null, propValue);
        if (propName == null)
        {
            throw new IllegalArgumentException(
                    "Property name must not be null!");
        }
        propertyName = propName;
    }

    /**
     * Returns the name of the property that will be set.
     *
     * @return the name of the property
     */
    public String getPropertyName()
    {
        return propertyName;
    }

    /**
     * Performs the invocation and sets the property on the specified target
     * object.
     *
     * @param depProvider the dependency provider (must not be <b>null</b>)
     * @param target the target instance (must not be <b>null</b>)
     * @return the result of the invocation, which is always <b>null</b> in this
     *         case
     * @throws InjectionException if an error occurs
     * @throws IllegalArgumentException if one of the parameters is <b>null</b>
     */
    public Object invoke(DependencyProvider depProvider, Object target)
    {
        if (target == null)
        {
            throw new InjectionException("Target object must not be null!");
        }

        Object[] values = getResolvedParameters(depProvider);
        assert values != null && values.length == 1 : "Wrong number of values!";
        depProvider.getInvocationHelper().setProperty(target,
                getPropertyName(), values[0]);
        return null;
    }

    /**
     * Outputs further information about this invocation to the specified
     * buffer. This implementation will print the name of the affected property.
     *
     * @param buf the target buffer
     */
    @Override
    protected void invocationInfoToString(StringBuilder buf)
    {
        buf.append(getPropertyName());
    }
}

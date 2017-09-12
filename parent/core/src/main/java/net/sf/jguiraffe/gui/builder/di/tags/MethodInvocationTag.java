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
package net.sf.jguiraffe.gui.builder.di.tags;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.impl.Invokable;
import net.sf.jguiraffe.di.impl.MethodInvocation;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A specialized {@code InvocationTag} implementation that deals with
 * {@link MethodInvocation} objects.
 * </p>
 * <p>
 * The following attributes are supported by this tag handler class:
 * </p>
 * <p>
 * <table border="1">
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">method</td>
 * <td>The name of the method that is to be invoked.</td>
 * <td valign="top">no</td>
 * </tr>
 * <tr>
 * <td valign="top">static</td>
 * <td>This boolean attribute determines whether a static method is to be
 * invoked. The default value is <b>false</b>.</td>
 * <td valign="top">no</td>
 * </tr>
 * <tr>
 * <td valign="top">targetClass</td>
 * <td>Defines the target class (i.e. the class, to which the method belongs).
 * This information is required only if a static method is to be invoked.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">targetClassName</td>
 * <td>Like {@code targetClass}, but defines the target class by name.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">targetClassLoader</td>
 * <td>If the target class is defined by name, this attribute can be used for
 * determining the class loader to be used for resolving the class name.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">source</td>
 * <td>If this invocation belongs to a {@code ChainedInvocation}, with
 * this attribute the name of the source variable (i.e. the object, on which to
 * invoke the method) can be defined.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">result</td>
 * <td>If this invocation belongs to a {@code ChainedInvocation}, with
 * this attribute the name of the source variable (i.e. the variable, in which
 * the result of this invocation is stored) can be defined.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * In the body of this tag an arbitrary number of {@link ParameterTag} tags can
 * be placed for defining the parameters of this invocation. Also a
 * {@link InvocationTargetTag} can be used to define a specific target
 * dependency.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: MethodInvocationTag.java 207 2012-02-09 07:30:13Z oheger $
 */
public class MethodInvocationTag extends InvocationTag
{
    /** The target dependency of this method invocation.*/
    private Dependency targetDependency;

    /** Stores the name of the method to be invoked. */
    private String method;

    /** Stores the source of the method invocation. */
    private String source;

    /** Stores the static flag. */
    private boolean staticFlag;

    /**
     * Returns the name of the method to be invoked.
     *
     * @return then method name
     */
    public String getMethod()
    {
        return method;
    }

    /**
     * Set method of the method attribute.
     *
     * @param method the attribute's value
     */
    public void setMethod(String method)
    {
        this.method = method;
    }

    /**
     * Returns the name of the source variable. This variable can be set if this
     * invocation tag belongs to a {@code ChainedInvocation}.
     *
     * @return the name of the source variable
     */
    @Override
    public String getSource()
    {
        return source;
    }

    /**
     * Set method of the source attribute.
     *
     * @param source the attribute's value
     */
    public void setSource(String source)
    {
        this.source = source;
    }

    /**
     * Returns a flag whether a static method is to be invoked.
     *
     * @return the static invocation flag
     */
    public boolean isStatic()
    {
        return staticFlag;
    }

    /**
     * Set method of the static attribute.
     *
     * @param f the attribute's value
     */
    public void setStatic(boolean f)
    {
        staticFlag = f;
    }

    /**
     * Returns the target dependency of this method invocation.
     *
     * @return the target dependency (may be <b>null</b>)
     * @since 1.1
     */
    public Dependency getTargetDependency()
    {
        return targetDependency;
    }

    /**
     * Sets the target dependency of this method invocation. This dependency is
     * typically set by tags in the body which select an alternative target
     * object.
     *
     * @param targetDependency the target dependency
     * @since 1.1
     */
    public void setTargetDependency(Dependency targetDependency)
    {
        this.targetDependency = targetDependency;
    }

    /**
     * Creates the invocation object. This implementation creates a
     * {@link MethodInvocation} object.
     *
     * @return the new invocation object
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected Invokable createInvocation() throws JellyTagException
    {
        if (getMethod() == null)
        {
            throw new MissingAttributeException("method");
        }
        return new MethodInvocation(getTargetClassDescription(),
                getTargetDependency(), getMethod(), isStatic(),
                getParameterTypes(), getParameterDependencies());
    }
}

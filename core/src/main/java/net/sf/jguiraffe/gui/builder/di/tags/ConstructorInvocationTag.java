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
package net.sf.jguiraffe.gui.builder.di.tags;

import net.sf.jguiraffe.di.impl.ConstructorInvocation;
import net.sf.jguiraffe.di.impl.Invokable;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specialized <code>InvocationTag</code> implementation that deals with
 * <code>{@link ConstructorInvocation}</code> objects.
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
 * <td valign="top">targetClass</td>
 * <td>Defines the target class (i.e. the class, to which the method belongs).
 * This information is required for constructor invocations, however there are
 * several ways of setting it.</td>
 * <td valign="top">no</td>
 * </tr>
 * <tr>
 * <td valign="top">targetClassName</td>
 * <td>Like <code>targetClass</code>, but defines the target class by name.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">targetClassLoader</td>
 * <td>If the target class is defined by name, this attribute can be used for
 * determining the class loader to be used for resolving the class name.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">result</td>
 * <td>If this invocation belongs to a <code>ChainedInvocation</code>, with
 * this attribute the name of the source variable (i.e. the variable, in which
 * the result of this invocation is stored) can be defined.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * In the body of this tag an arbitrary number of
 * <code>{@link ParameterTag}</code> tags can be placed for defining the
 * parameters of this invocation.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ConstructorInvocationTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ConstructorInvocationTag extends InvocationTag
{
    /**
     * Creates the invocation object. This implementation creates a
     * <code>{@link ConstructorInvocation}</code> object.
     *
     * @return the new invocation object
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected Invokable createInvocation() throws JellyTagException
    {
        if (getTargetClassDescription() == null)
        {
            throw new JellyTagException("No target class specified!");
        }
        return new ConstructorInvocation(getTargetClassDescription(), getParameterTypes(),
                getParameterDependencies());
    }
}

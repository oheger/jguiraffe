/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>
 * A specialized tag handler implementation for defining the target object of a
 * method invocation.
 * </p>
 * <p>
 * When a {@link MethodInvocationTag} is used in the initializer script of a
 * bean declaration the method is typically invoked on the managed object which
 * is the result of the bean declaration. Using {@code InvocationTargetTag} it
 * is possible to define a different target, e.g. another bean defined in the
 * current bean context. A use case would be a factory bean which is called to
 * create objects of a specific type. The following example fragment shows how
 * the tag can be used in an initializer script of a bean declaration:
 * </p>
 *
 * <pre>
 * &lt;di:bean name="initializer" singleton="false"&gt;
 *   &lt;di:methodInvocation method="create" result="obj"&gt;
 *     &lt;di:param parameterClass="java.lang.String" refName="strConst"/&gt;
 *     &lt;di:invocationTarget refName="factory"/&gt;
 *   &lt;/di:methodInvocation&gt;
 *   ...
 * &lt;/di:bean&gt;
 * </pre>
 * <p>
 * The tag defines no attributes in addition to the ones inherited from its base
 * class. So basically the various options for defining the dependency are
 * available. Refer to the documentation of {@code DependencyTag} or
 * {@link ParameterTag}. The tag must occur in the body of a
 * {@link MethodInvocationTag}.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: InvocationTargetTag.java 207 2012-02-09 07:30:13Z oheger $
 * @since 1.1
 */
public class InvocationTargetTag extends DependencyTag
{
    /**
     * {@inheritDoc} This implementation determines the parent tag which must be
     * of type {@link MethodInvocationTag}. This tag is passed the dependency
     * defined here.
     */
    public void doTag(XMLOutput output) throws JellyTagException
    {
        if (!(getParent() instanceof MethodInvocationTag))
        {
            throw new JellyTagException("InvocationTargetTag must be nested in"
                    + " a MethodInvocationTag!");
        }

        invokeBody(output);
        ((MethodInvocationTag) getParent())
                .setTargetDependency(getDependency());
    }
}

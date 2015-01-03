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

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.impl.providers.PropertiesBeanProvider;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specialized <code>MapTag</code> implementation for creating a
 * <code>java.util.Properties</code> object.
 * </p>
 * <p>
 * This tag behaves analogously to {@link MapTag}, but the bean created by this
 * tag is of type <code>java.util.Properties</code>. Following is an example of
 * how this tag can be used:
 *
 * <pre>
 * &lt;constructor&gt;
 *   &lt;param&gt;
 *     &lt;properties&gt;
 *       &lt;entry key=&quot;db.user&quot; value=&quot;scott&quot;/&gt;
 *       &lt;entry key=&quot;db.pwd&quot; value=&quot;tiger&quot;/&gt;
 *       &lt;entry key=&quot;db.url&quot; value=&quot;jdbc:thin:localhost:test&quot;/&gt;
 *     &lt;/properties&gt;
 *   &lt;/param&gt;
 * &lt;/constructor&gt;
 * </pre>
 *
 * </p>
 * <p>
 * This tag does not specify any attributes. It inherits the attributes from its
 * base class, <code>MapTag</code>, however in most cases their use makes hardly
 * sense. The classes for keys and values are initialized to
 * <code>String.class</code>, which should not be changed for properties.
 * </p>
 * <p>
 * When adding values to the <code>Properties</code> object using the
 * {@link EntryTag} tag in the body of this tag, no type check will be
 * performed. So it is possible to add values other than strings to the
 * <code>Properties</code> object (as is possible using the inherited
 * <code>put()</code> method of <code>java.util.Properties</code>. It lies in
 * the responsibility of a client application to ensure that only valid data is
 * added to this tag.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PropertiesTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PropertiesTag extends MapTag
{
    /**
     * Creates a new instance of <code>PropertiesTag</code>.
     */
    public PropertiesTag()
    {
        // Properties operate on strings
        setKeyClass(String.class);
        setValueClass(String.class);
    }

    /**
     * Creates the <code>BeanProvider</code> managed by this tag. This
     * implementation will create a {@link PropertiesBeanProvider} object.
     *
     * @return the bean provider
     * @throws JellyTagException in case of an error
     */
    @Override
    protected BeanProvider createBeanProvider() throws JellyTagException
    {
        return new PropertiesBeanProvider(getKeyDependencies(),
                getValueDependencies());
    }
}

/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>
 * A tag handler class for defining a value.
 * </p>
 * <p>
 * This tag handler class can appear in the body of a tag implementing the
 * {@link ValueSupport} interface. It evaluates its body, transforms it into a
 * string, and passes the result to the {@code ValueSupport} tag.
 * </p>
 * <p>
 * Typically tags producing a value (e.g. {@link ParameterTag} or
 * {@link SetPropertyTag}) allow setting the value through attributes. XML
 * attributes however have some constraints. For instance, it is not possible to
 * specify a string value with newline characters. Because {@code ValueTag}
 * obtains the value from its body these restrictions do not apply here. So this
 * tag can be used to define more complex values. The values are then passed to
 * the parent tag.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ValueTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ValueTag extends TagSupport
{
    /**
     * Creates a new instance of {@code ValueTag}.
     */
    public ValueTag()
    {
        setEscapeText(false);
    }

    /**
     * Executes this tag. Obtains the parent tag (which must implement the
     * {@code ValueSupport} interface) and passes the body of this tag to it as
     * value.
     *
     * @param out the output object
     * @throws JellyTagException if the tag is used incorrectly
     */
    public void doTag(XMLOutput out) throws JellyTagException
    {
        if (!(getParent() instanceof ValueSupport))
        {
            throw new JellyTagException(
                    "Value tag must be in the body of a ValueSupport tag!");
        }

        ((ValueSupport) getParent()).setValue(getBodyText());
    }
}

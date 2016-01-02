/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
 * A tag handler class for specifying <b>null</b> values.
 * </p>
 * <p>
 * This tag handler class can appear in the body of a tag implementing the
 * {@link ValueSupport} interface. It sets the value of its parent tag to
 * <b>null</b>.
 * </p>
 * <p>
 * The main use case for this tag is to be placed in the body of a
 * {@link ParameterTag} or {@link SetPropertyTag}. These tags require a value to
 * set; otherwise they throw an exception. Using this tag means "there is a
 * value, but it is <b>null</b>".
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: NullTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class NullTag extends TagSupport
{
    /**
     * Executes this tag. This implementation checks whether the parent tag
     * implements the {@link ValueSupport} interface. If this is the case, its
     * value is set to <b>null</b>. Otherwise an exception is thrown.
     *
     * @param out the output object
     * @throws JellyTagException if the tag is used incorrectly
     */
    public void doTag(XMLOutput out) throws JellyTagException
    {
        if (!(getParent() instanceof ValueSupport))
        {
            throw new JellyTagException(
                    "Null tag must be in the body of a ValueSupport tag!");
        }

        ((ValueSupport) getParent()).setValue(null);
    }
}

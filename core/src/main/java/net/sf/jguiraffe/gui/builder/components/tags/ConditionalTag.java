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
package net.sf.jguiraffe.gui.builder.components.tags;

import org.apache.commons.jelly.Tag;

/**
 * <p>
 * Definition of an interface for tags that are only executed for certain
 * builders.
 * </p>
 * <p>
 * In theory the XML GUI definition should be portable, i.e. compatible with
 * different builders. In practice there may be cases where this is not
 * possible. Then it comes in handy if there is an easy possibility to specify
 * that certain tags are only executed under certain conditions. This is exactly
 * the purpose of this interface. Note that with standard Jelly means it is
 * quite easy to achieve such a conditional execution of tags; the form builder
 * library however tries to provide an even more convenient mechanism by simply
 * specifying an attribute with the name of a builder. The tag is then executed
 * if the current builder either matches or does not match this name.
 * </p>
 * <p>
 * So the major part of tags in this Jelly tag library will support the
 * following attributes for conditional execution:
 * <ul>
 * <li><em>ifName</em>: This attribute can be assigned a comma separated
 * list of builder names. The tag gets executed only if the name of the current
 * builder matches one of the specified names.</li>
 * <li><em>unlessName</em>: This attribute is very similar to the one
 * explained before. The difference is that the tag gets executed only if the
 * current builder's name does <strong>not </strong> match a name specified in
 * the attribute.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ConditionalTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ConditionalTag extends Tag
{
    /**
     * Returns the value of the <code>ifName</code> attribute. Here a list of
     * builder names can be returned, for which the tag should be executed.
     *
     * @return a list of supported builders
     */
    String getIfName();

    /**
     * Returns the value of the <code>unlessName</code> attribute. Here a list
     * of builder names can be returned, for which the tag should bot be
     * executed.
     *
     * @return a list of not supported builders
     */
    String getUnlessName();
}

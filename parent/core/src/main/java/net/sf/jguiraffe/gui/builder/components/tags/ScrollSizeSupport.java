/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.layout.NumberWithUnit;

/**
 * <p>
 * Definition of an interface to be implemented by tags that allow the
 * specification of a preferred scroll width and height.
 * </p>
 * <p>
 * This interface is to be implemented by tags responsible for the creation of
 * UI controls with scrolling support (e.g. text areas, lists, etc.). For
 * elements like this it is typically not obvious which default size they should
 * be given. Some UI platforms use their own specific default sizes which may
 * not be appropriate for a given application. Therefore, JGUIraffe tags allow
 * the developer to explicitly define the size. From the tags, when the
 * represented UI control is created, the width and height to be used can be
 * queried as a {@link NumberWithUnit} element. It is then possible for a
 * concrete component manager implementation to setup the control's size
 * accordingly.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: $
 * @since 1.3
 */
public interface ScrollSizeSupport
{
    /**
     * Returns the preferred scroll width of the represented component as a
     * {@code NumberWithUnit}. Typically, this value is determined during
     * processing of this tag. An implementation should never return
     * <b>null</b>. If no scroll width has been specified, a value of 0 or
     * negative should be returned.
     *
     * @return the preferred scroll width
     */
    NumberWithUnit getPreferredScrollWidth();

    /**
     * Returns the preferred scroll height as a {@code NumberWithUnit}.
     * Typically, this value is determined during processing of this tag. An
     * implementation should never return <b>null</b>. If no scroll height has
     * been specified, a value of 0 or negative should be returned.
     *
     * @return the preferred scroll height
     */
    NumberWithUnit getPreferredScrollHeight();
}

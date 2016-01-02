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
package net.sf.jguiraffe.gui.builder.enablers;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

/**
 * <p>
 * A trivial implementation of the {@code ElementEnabler} interface that does
 * nothing.
 * </p>
 * <p>
 * This class provides an empty dummy implementation of the {@code
 * setEnabledState()} method. It can be used as an application of the
 * <em>null object pattern</em> where an {@code ElementEnabler} is optional.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: NullEnabler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class NullEnabler implements ElementEnabler
{
    /** A constant for a default instance of this class. */
    public static final NullEnabler INSTANCE = new NullEnabler();

    /**
     * {@inheritDoc} An empty dummy implementation of this interface method. It
     * does literally nothing.
     */
    public void setEnabledState(ComponentBuilderData compData, boolean state)
            throws FormBuilderException
    {
    }
}

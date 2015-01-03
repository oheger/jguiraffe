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
package net.sf.jguiraffe.gui.builder.action.tags;

import net.sf.jguiraffe.gui.builder.event.filter.OrEventFilter;

/**
 * <p>
 * A specialized <code>ChainedEventFilter</code> implementation that creates
 * an
 * <code>{@link net.sf.jguiraffe.gui.builder.event.filter.OrEventFilter OrEventFilter}</code>.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: OrEventFilterTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class OrEventFilterTag extends ChainedEventFilterTag
{
    /**
     * Creates a new instance of <code>OrEventFilterTag</code>.
     */
    public OrEventFilterTag()
    {
        super(OrEventFilter.class);
    }
}

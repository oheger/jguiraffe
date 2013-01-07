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
package net.sf.jguiraffe.gui.builder.action.tags;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.jelly.JellyTagException;

import net.sf.jguiraffe.gui.builder.event.filter.ChainedEventFilter;
import net.sf.jguiraffe.gui.builder.event.filter.EventFilter;

/**
 * <p>
 * A specialized event filter class that deals with chained filters, i.e. event
 * filters that can child event filters.
 * </p>
 * <p>
 * This tag handler class adds an additional <code>addChildFilter</code>
 * method to the methods inherited by its super class. This method will be
 * called by <code>EventFilterTag</code> if it detects that it is nested
 * inside a chained filter definition.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ChainedEventFilterTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ChainedEventFilterTag extends EventFilterTag
{
    /** Stores a list of child filters. */
    private Collection<EventFilter> childFilters;

    /**
     * Creates a new instance of <code>ChainedEventFilterTag</code>.
     */
    public ChainedEventFilterTag()
    {
        super();
    }

    /**
     * Creates a new instance of <code>ChainedEventFilterTag</code> and sets
     * the default class.
     *
     * @param defaultClass the default class
     */
    public ChainedEventFilterTag(Class<?> defaultClass)
    {
        super(defaultClass, defaultClass);
    }

    /**
     * Adds the specified event filter to the list of child filters of the
     * current chained filter tag.
     *
     * @param child the child filter
     */
    public void addChildFilter(EventFilter child)
    {
        if (childFilters == null)
        {
            childFilters = new ArrayList<EventFilter>();
        }
        childFilters.add(child);
    }

    /**
     * Performs post processing of the new bean. This implementation adds the
     * child filters to the new chained filter if there are any.
     *
     * @param bean the new bean
     * @return a flag whether the bean could be passed to a target
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected boolean passResults(Object bean) throws JellyTagException
    {
        if (childFilters != null)
        {
            ChainedEventFilter filter = (ChainedEventFilter) bean;
            filter.addFilters(childFilters);
        }

        return super.passResults(bean);
    }
}

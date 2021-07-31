/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.components.tags.UseBeanBaseTag;
import net.sf.jguiraffe.gui.builder.event.filter.EventFilter;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specialized tag handler class for creating event filters.
 * </p>
 * <p>
 * With this tag handler class simple (i.e. non chained) event filters can be
 * constructed. By specifying the <code>class</code> attribute the concrete
 * class of the filter to be created is defined. Then all the properties
 * supported by this specific filter class can be set in further attributes.
 * </p>
 * <p>
 * The tag also checks if it is nested inside a {@link ChainedEventFilterTag}
 * tag. If this is the case, the current filter is added as a child filter to
 * this chained filter tag. Otherwise the variable <code>CURRENT_FILTER</code>
 * is set to the active event filter, which can be queried by other tags.
 * </p>
 * <p>
 * By inheriting from <code>UseBeanBaseTag</code> all of the features provided
 * by this base class are available for this tag handler class, too.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: EventFilterTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class EventFilterTag extends UseBeanBaseTag
{
    /**
     * Constant for the current filter variable. This variable will be set to
     * the currently created event filter and can be evaluated by other tags.
     */
    public static final String CURRENT_FILTER = "CURRENT_FILTER";

    /**
     * Creates a new instance of <code>EventFilterTag</code>.
     */
    public EventFilterTag()
    {
        super();
        setBaseClass(EventFilter.class);
    }

    /**
     * Creates a new instance of <code>EventFilterTag</code> and sets the
     * default class and the base class.
     *
     * @param defaultClass the default class
     * @param baseClass the base class
     */
    public EventFilterTag(Class<?> defaultClass, Class<?> baseClass)
    {
        super(defaultClass, baseClass);
    }

    /**
     * Performs post processing on the bean. This implementation checks if this
     * tag is nested inside a <code>ChainedEventFilter</code> tag. If this is
     * the case, the filter is added as a child filter. Otherwise the filter is
     * set in the Jelly context as the current filter.
     *
     * @param bean the new bean
     * @return a flag whether the bean could be passed to a target
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected boolean passResults(Object bean) throws JellyTagException
    {
        ChainedEventFilterTag parent =
                (ChainedEventFilterTag) findAncestorWithClass(ChainedEventFilterTag.class);
        if (parent != null)
        {
            parent.addChildFilter((EventFilter) bean);
        }
        else
        {
            getContext().setVariable(CURRENT_FILTER, bean);
        }

        return true;
    }
}

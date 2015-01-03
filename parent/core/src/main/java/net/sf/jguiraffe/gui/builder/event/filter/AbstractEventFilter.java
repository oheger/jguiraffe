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
package net.sf.jguiraffe.gui.builder.event.filter;

import net.sf.jguiraffe.gui.builder.event.BuilderEvent;

/**
 * <p>
 * An abstract base class for event filters.
 * </p>
 * <p>
 * This class can be used as base class by simple event filters that do not need
 * to bother with specialties like <b>null</b> values or non event objects.
 * The class can be configured whether it should accept <b>null</b> values or
 * not. It can be initialized with a base class that must be derived from
 * <code>BuilderEvent</code>. All objects accepted by this filter must then
 * be of this class or one of its subclasses
 * </p>
 * <p>
 * In this class a base implementation of the <code>accept()</code> method is
 * provided, which casts the passed in object to an event object and then
 * delegates to the abstract <code>acceptEvent()</code> method.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AbstractEventFilter.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractEventFilter implements EventFilter
{
    /** Stores the base class. */
    private Class<?> baseClass;

    /** Stores a flag whether <b>null</b> values are accepted. */
    private boolean acceptNull;

    /**
     * Creates a new instance of <code>AbstractEventFilter</code>. The base
     * class is set to <code>BuilderEvent</code>; <b>null</b> values won't
     * be accepted.
     */
    protected AbstractEventFilter()
    {
        this(BuilderEvent.class, false);
    }

    /**
     * Creates a new instance of <code>AbstractEventFilter</code> and
     * initializes it with the base class. <b>null</b> values won't be
     * accepted.
     *
     * @param baseClass the base class (must no be <b>null</b>)
     */
    protected AbstractEventFilter(Class<?> baseClass)
    {
        this(baseClass, false);
    }

    /**
     * Creates a new instance of <code>AbstractEventFilter</code> and
     * initializes it with the base class and the <code>acceptNull</code>
     * flag.
     *
     * @param baseClass the base class (must no be <b>null</b>)
     * @param acceptNull a flag if <b>null</b> values are accepted
     */
    protected AbstractEventFilter(Class<?> baseClass, boolean acceptNull)
    {
        setBaseClass(baseClass);
        setAcceptNull(acceptNull);
    }

    /**
     * Tests whether the passed in object is accepted by this filter. This
     * implementation will perform a type cast an delegate to the
     * <code>{@link #acceptEvent(BuilderEvent)}</code> method.
     *
     * @param obj the object to test
     * @return a flag if this object is accepted
     */
    public boolean accept(Object obj)
    {
        if (obj == null)
        {
            return isAcceptNull();
        }
        else if (!getBaseClass().isAssignableFrom(obj.getClass()))
        {
            return false;
        }
        else
        {
            return acceptEvent((BuilderEvent) obj);
        }
    }

    /**
     * Returns the <code>acceptNull</code> flag.
     *
     * @return a flag if <b>null</b> values are accepted
     */
    public boolean isAcceptNull()
    {
        return acceptNull;
    }

    /**
     * Sets the <code>acceptNull</code> flag. If a <b>null</b> value is
     * passed to the <code>accept()</code> method, the value of this flag is
     * returned.
     *
     * @param acceptNull a flag if <b>null</b> values are accepted
     */
    public void setAcceptNull(boolean acceptNull)
    {
        this.acceptNull = acceptNull;
    }

    /**
     * Returns the base class.
     *
     * @return the filter's base class
     */
    public Class<?> getBaseClass()
    {
        return baseClass;
    }

    /**
     * Sets the base class. The passed in class object must not be <b>null</b>
     * and must be derived from <code>BuilderEvent</code>.
     *
     * @param baseClass the base class
     */
    public void setBaseClass(Class<?> baseClass)
    {
        if (baseClass == null)
        {
            throw new IllegalArgumentException("Base class must not be null!");
        }
        if (!BuilderEvent.class.isAssignableFrom(baseClass))
        {
            throw new IllegalArgumentException(
                    "Base class must be derived from BuilderEvent!");
        }

        this.baseClass = baseClass;
    }

    /**
     * Tests if the passed in event object is accepted by this filter. This
     * method is called by the base implementation of <code>accept()</code>.
     *
     * @param event the event to be tested
     * @return a flag whether the event object is accepted
     */
    protected abstract boolean acceptEvent(BuilderEvent event);
}

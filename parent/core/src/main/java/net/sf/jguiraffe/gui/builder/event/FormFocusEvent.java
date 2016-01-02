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
package net.sf.jguiraffe.gui.builder.event;

import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * <p>
 * A specific event class dealing with focus events.
 * </p>
 * <p>
 * A focus event is fired when a component gains or loses focus.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormFocusEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormFocusEvent extends FormEvent
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 2244179360483037190L;

    /** The type of this event. */
    private final Type type;

    /**
     * Creates a new instance of {@code FormFocusEvent} and initializes it.
     *
     * @param source the source event
     * @param handler the component handler
     * @param name the component's name
     * @param type the focus type of the event (gained or lost)
     */
    public FormFocusEvent(Object source, ComponentHandler<?> handler,
            String name, Type type)
    {
        super(source, handler, name);
        this.type = type;
    }

    /**
     * Returns the type of this event.
     *
     * @return the type of this event
     */
    public Type getType()
    {
        return type;
    }

    /**
     * {@inheritDoc} This implementation takes the additional fields into
     * account declared by this class.
     *
     * @since 1.3
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(getType()).toHashCode();
    }

    /**
     * {@inheritDoc} This implementation also checks the additional fields
     * declared by this class.
     *
     * @since 1.3
     */
    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj)
                && ObjectUtils.equals(getType(),
                        ((FormFocusEvent) obj).getType());
    }

    /**
     * An enumeration class defining the different types of focus events. Every
     * {@code FormFocusEvent} has such a type determining what exactly happened.
     */
    public static enum Type
    {
        /** A component gained focus. */
        FOCUS_GAINED,

        /** A component lost focus. */
        FOCUS_LOST
    }
}

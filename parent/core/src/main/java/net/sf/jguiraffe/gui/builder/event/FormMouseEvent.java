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

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * <p>
 * A specialized event class for reporting events related to mouse actions.
 * </p>
 * <p>
 * Events of this type are passed to components that have registered themselves
 * as mouse listeners at input components. This way listeners get notified, for
 * instance, if the mouse is clicked, released, or moved within an input
 * components. The {@code type} property can be queried to find out which mouse
 * action was performed. The coordinates of the mouse cursor (relative to the
 * origin of the source component) are also available. Further, the affected
 * mouse button and the status of special modifier keys (like <em>SHIFT</em> or
 * <em>ALT</em>) are available.
 * </p>
 * <p>
 * Mouse events are more low-level. In typical controller classes for input
 * forms it is rarely necessary to react on specific mouse actions. In most
 * cases action or change events are more appropriate. An exception can be
 * double-click events, which are only available through a mouse listener.
 * </p>
 * <p>
 * {@code FormMouseEvent} is derived from {@code FormEvent} and thus provides
 * access to the {@link ComponentHandler} and the name of the component that
 * triggered this event. Nevertheless, it is possible to register a mouse
 * listener at non-input components, e.g. windows. In this case, the {@code
 * handler} and {@code name} properties are undefined.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormMouseEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormMouseEvent extends FormEvent
{
    /**
     * Constant for the mouse button 1. This is the left button. The return
     * value of {@link #getButton()} can be checked with this constant.
     */
    public static final int BUTTON1 = 1;

    /**
     * Constant for the mouse button 2. This is the middle mouse button. The
     * return value of {@link #getButton()} can be checked with this constant.
     */
    public static final int BUTTON2 = 2;

    /**
     * Constant for the mouse button 3. This is the right mouse button. The
     * return value of {@link #getButton()} can be checked with this constant.
     */
    public static final int BUTTON3 = 3;

    /**
     * Constant for an undefined mouse button. This value is returned by
     * {@link #getButton()} if the event is not related to a button.
     */
    public static final int NO_BUTTON = 0;

    /** Constant for the pattern for creating the string representation. */
    private static final String TO_STRING_PATTERN = "FormMouseEvent [ componentName = %s, "
            + "TYPE = %s, X = %d, Y = %d, BUTTON = %d, MODIFIERS = %s ]";

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20090915L;

    /** The type of this event. */
    private final Type type;

    /** The X position of the mouse cursor. */
    private final int x;

    /** The Y position of the mouse cursor. */
    private final int y;

    /** The button that was pressed. */
    private final int button;

    /** A set with modifier keys. */
    private final Set<Modifiers> modifiers;

    /**
     * Creates a new instance of {@code FormMouseEvent} and initializes all its
     * properties.
     *
     * @param source the source of this event (this is typically the original,
     *        platform-specific event this object was created from)
     * @param handler the {@code ComponentHandler} of the input component that
     *        caused this event
     * @param name the name of the input component that caused this event
     * @param t the type of this event (must not be <b>null</b>)
     * @param xp the x position of the mouse cursor
     * @param yp the y position of the mouse cursor
     * @param btn the index of the affected button
     * @param mods a set with modifier keys (may be empty or <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is missing
     */
    public FormMouseEvent(Object source, ComponentHandler<?> handler,
            String name, Type t, int xp, int yp, int btn,
            Collection<Modifiers> mods)
    {
        super(source, handler, name);
        if (t == null)
        {
            throw new IllegalArgumentException("Event type must not be null!");
        }

        type = t;
        x = xp;
        y = yp;
        button = btn;

        if (mods == null || mods.isEmpty())
        {
            modifiers = Collections.emptySet();
        }
        else
        {
            modifiers = Collections.unmodifiableSet(EnumSet.copyOf(mods));
        }
    }

    /**
     * Returns the type of this event. The type provides information about which
     * mouse action has actually happened.
     *
     * @return the type of this event
     */
    public Type getType()
    {
        return type;
    }

    /**
     * Returns the X position of the mouse relative to the origin of the
     * component that caused this event.
     *
     * @return the X position of the mouse cursor
     */
    public int getX()
    {
        return x;
    }

    /**
     * Returns the Y position of the mouse relative to the origin of the
     * component that caused this event.
     *
     * @return the Y position of the mouse cursor
     */
    public int getY()
    {
        return y;
    }

    /**
     * Returns the index of the mouse button affected by this event. The return
     * value is one of the {@code BUTTONx} constants. It indicates which button
     * was pressed or released. If the event is not related to a mouse button,
     * result is {@link #NO_BUTTON}.
     *
     * @return the index of the mouse button affected by this event
     */
    public int getButton()
    {
        return button;
    }

    /**
     * Returns a set with {@code Modifiers} representing the special modifier
     * keys that were pressed when the mouse event occurred. This information
     * can be used to distinguish enhanced mouse gestures, e.g. SHIFT+CLICK for
     * text selection. The set returned by this method cannot be modified.
     *
     * @return a set with the active modifier keys
     */
    public Set<Modifiers> getModifiers()
    {
        return modifiers;
    }

    /**
     * Returns a string representation of this object. This string contains the
     * most important properties that have a meaningful string representation.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        return String.format(TO_STRING_PATTERN, getName(), getType().name(),
                getX(), getY(), getButton(), getModifiers().toString());
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
                .append(getType()).append(getX()).append(getY())
                .append(getButton()).append(getModifiers()).toHashCode();
    }

    /**
     * {@inheritDoc} This implementation checks the additional fields declared
     * by this class.
     *
     * @since 1.3
     */
    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            FormMouseEvent c = (FormMouseEvent) obj;
            return new EqualsBuilder().append(getType(), c.getType())
                    .append(getX(), c.getX()).append(getY(), c.getY())
                    .append(getButton(), c.getButton())
                    .append(getModifiers(), c.getModifiers()).isEquals();
        }
        return false;
    }

    /**
     * An enumeration class defining constants for the possible mouse actions
     * that can trigger a {@code FormMouseEvent}. Each {@code FormMouseEvent} is
     * associated with such a type constant, so that it is possible to find out
     * what exactly has happened.
     *
     * @author Oliver Heger
     * @version $Id: FormMouseEvent.java 205 2012-01-29 18:29:57Z oheger $
     */
    public static enum Type
    {
        /**
         * A mouse button was pressed.
         */
        MOUSE_PRESSED,

        /**
         * A mouse button was released.
         */
        MOUSE_RELEASED,

        /**
         * A mouse button was clicked. This means the button was pressed and
         * released.
         */
        MOUSE_CLICKED,

        /**
         * A double click with a mouse button was performed. This means the
         * button was clicked twice during an OS-specific interval.
         */
        MOUSE_DOUBLE_CLICKED,

        /**
         * The mouse cursor entered the monitored component.
         */
        MOUSE_ENTERED,

        /**
         * The mouse cursor exited the monitored component.
         */
        MOUSE_EXITED
    }
}

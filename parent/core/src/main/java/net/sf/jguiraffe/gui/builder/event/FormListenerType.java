/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import java.util.EventListener;
import java.util.Locale;

import javax.swing.event.EventListenerList;

/**
 * <p>
 * An enumeration class describing the supported event listener types.
 * </p>
 * <p>
 * This class is used internally in the implementation of the generic event
 * handling layer provided by the form framework. Clients usually need not deal
 * with it directly.
 * </p>
 * <p>
 * The enumeration literals defined by this class represent the supported event
 * listener types. The class also provides functionality for firing events, i.e.
 * calling listeners.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormListenerType.java 205 2012-01-29 18:29:57Z oheger $
 */
public enum FormListenerType
{
    /** The listener type <em>Action</em>. */
    ACTION(FormActionListener.class)
    {
        /**
         * {@inheritDoc} This implementation calls the corresponding method of
         * the action listener interface.
         */
        @Override
        public void callListener(FormEventListener l, FormEvent event)
        {
            ((FormActionListener) l).actionPerformed((FormActionEvent) event);
        }
    },

    /** The listener type <em>Change</em>. */
    CHANGE(FormChangeListener.class)
    {
        /**
         * {@inheritDoc} This implementation calls the corresponding method of
         * the change listener interface.
         */
        @Override
        public void callListener(FormEventListener l, FormEvent event)
        {
            ((FormChangeListener) l).elementChanged((FormChangeEvent) event);
        }
    },

    /** The listener type <em>Focus</em>. */
    FOCUS(FormFocusListener.class)
    {
        /**
         * {@inheritDoc} This implementation calls the corresponding method of
         * the focus listener interface.
         */
        @Override
        public void callListener(FormEventListener l, FormEvent event)
        {
            FormFocusEvent fe = (FormFocusEvent) event;
            FormFocusListener fl = (FormFocusListener) l;
            if (fe.getType() == FormFocusEvent.Type.FOCUS_LOST)
            {
                fl.focusLost(fe);
            }
            else
            {
                fl.focusGained(fe);
            }
        }
    },

    /** The listener type <em>Mouse</em>. */
    MOUSE(FormMouseListener.class)
    {
        /**
         * {@inheritDoc} This implementation calls the corresponding method of
         * the mouse listener interface.
         */
        @Override
        public void callListener(FormEventListener l, FormEvent event)
        {
            FormMouseEvent me = (FormMouseEvent) event;
            FormMouseListener ml = (FormMouseListener) l;
            assert me.getType() != null : "No event type!";

            switch (me.getType())
            {
            case MOUSE_CLICKED:
                ml.mouseClicked(me);
                break;
            case MOUSE_PRESSED:
                ml.mousePressed(me);
                break;
            case MOUSE_RELEASED:
                ml.mouseReleased(me);
                break;
            case MOUSE_ENTERED:
                ml.mouseEntered(me);
                break;
            case MOUSE_EXITED:
                ml.mouseExited(me);
                break;
            case MOUSE_DOUBLE_CLICKED:
                ml.mouseDoubleClicked(me);
                break;
            default:
                // should not happen
                throw new AssertionError("Unknown mouse event type!");
            }
        }
    };

    /** Stores the event listener class handled by this instance. */
    private final Class<? extends EventListener> listenerClass;

    /**
     * Creates a new instance of <code>FormListenerType</code> and sets the
     * event listener class.
     *
     * @param lstnCls the event listener class
     */
    private FormListenerType(Class<? extends EventListener> lstnCls)
    {
        listenerClass = lstnCls;
    }

    /**
     * Returns the event listener class that is handled by this instance.
     *
     * @return the event listener class
     */
    public Class<? extends EventListener> getListenerClass()
    {
        return listenerClass;
    }

    /**
     * Invokes all listeners in the specified listener list that are of the
     * listener type represented by this instance. The specified event will be
     * passed to the listener, which is actually done by the
     * <code>callListener()</code> method.
     *
     * @param listenerList a list with event listeners
     * @param event the event object to pass to the listeners
     */
    public void callListeners(EventListenerList listenerList, FormEvent event)
    {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == getListenerClass())
            {
                callListener((FormEventListener) listeners[i + 1], event);
            }
        }
    }

    /**
     * Returns the listener type name. This is the name to be used for
     * registering listeners of the corresponding type via reflection, e.g.
     * <em>Action</em> or <em>Focus</em>. If the listener type name is
     * <em>Foo</em>, there must be a corresponding method on the target object
     * named <em>addFooListener()</em> which will be called to register the
     * listener.
     *
     * @return the listener type name for this instance
     */
    public String listenerTypeName()
    {
        return name().charAt(0)
                + name().substring(1).toLowerCase(Locale.ENGLISH);
    }

    /**
     * Tries to find a {@code FormListenerType} constant that matches the passed
     * in string. The string is compared - ignoring case - with the names of all
     * constants defined for this enumeration class. If a matching constant is
     * found, it is returned. Otherwise the return value is <b>null</b>.
     *
     * @param s the string to be tested
     * @return the corresponding type constant or <b>null</b>
     */
    public static FormListenerType fromString(String s)
    {
        for (FormListenerType t : values())
        {
            if (t.name().equalsIgnoreCase(s))
            {
                return t;
            }
        }

        return null;
    }

    /**
     * Invokes an event listener. This method must be defined in concrete sub
     * classes to perform the necessary type casts and call the appropriate
     * listener method.
     *
     * @param l the event listener
     * @param event the event
     * @throws ClassCastException if the listener object or the event are
     *         incompatible with the listener type
     */
    public abstract void callListener(FormEventListener l, FormEvent event);
}

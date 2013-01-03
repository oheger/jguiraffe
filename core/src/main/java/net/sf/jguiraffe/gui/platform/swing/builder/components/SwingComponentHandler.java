/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JComponent;

import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.platform.swing.builder.event.ChangeListener;
import net.sf.jguiraffe.gui.platform.swing.builder.event.SwingEventSource;

/**
 * <p>
 * A base class for all <code>ComponentHandler</code> classes of the Swing
 * implementation.
 * </p>
 * <p>
 * This base class maintains a <code>JComponent</code> reference and provides
 * some common functionality, especially related to event handling.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingComponentHandler.java 205 2012-01-29 18:29:57Z oheger $
 * @param <T> the data type of this component handler
 */
abstract class SwingComponentHandler<T> implements ComponentHandler<T>,
        SwingEventSource
{
    /** Holds a reference to the nested component. */
    private final JComponent component;

    /** Stores the single change listener supported by this handler. */
    private ChangeListener changeListener;

    /** A lock for synchronizing access to the change listener. */
    private final Lock lockChangeListener;

    /**
     * Creates a new instance of <code>SwingComponentHandler</code> and sets
     * the component to manage.
     *
     * @param comp the associated component
     */
    protected SwingComponentHandler(JComponent comp)
    {
        component = comp;
        lockChangeListener = new ReentrantLock();
    }

    /**
     * Returns a reference to the managed Swing component.
     *
     * @return the Swing component
     */
    public JComponent getJComponent()
    {
        return component;
    }

    /**
     * Returns a reference to the managed component.
     *
     * @return the component
     */
    public Object getComponent()
    {
        return getJComponent();
    }

    /**
     * Returns the outer most component. This implementation returns the same
     * component as {@link #getComponent()}.
     *
     * @return the outer most component
     */
    public Object getOuterComponent()
    {
        return getComponent();
    }

    /**
     * Checks whether the managed component is enabled.
     *
     * @return a flag whether this component is enabled
     */
    public boolean isEnabled()
    {
        return getJComponent().isEnabled();
    }

    /**
     * Sets the enabled flag of the managed component.
     *
     * @param f the value of the enabled flag
     */
    public void setEnabled(boolean f)
    {
        getJComponent().setEnabled(f);
    }

    /**
     * Adds an action listener at the managed component. This implementation
     * throws an <code>UnsupportedOperationException</code> exception. (Many
     * components do not support action listeners.)
     *
     * @param l the listener to register
     */
    public void addActionListener(ActionListener l)
    {
        throw new UnsupportedOperationException(
                "Action listeners are not supported for this component type!");
    }

    /**
     * Removes an action listener from the managed component. This
     * implementation throws an <code>UnsupportedOperationException</code>
     * exception. (Many components do not support action listeners.)
     *
     * @param l the listener to remove
     */
    public void removeActionListener(ActionListener l)
    {
        throw new UnsupportedOperationException(
                "Action listeners are not supported for this component type!");
    }

    /**
     * Adds a change listener at the managed component. This implementation
     * supports only a single change listener (which should not be a limitation
     * because <code>FormEventManager</code> will do the multiplexing). Because
     * there is no generic change listener in Swing
     * {@link #registerChangeListener()} will be invoked for doing the actual
     * registration.
     *
     * @param l the listener to register
     */
    public void addChangeListener(ChangeListener l)
    {
        lockChangeListener.lock();
        try
        {
            assert changeListener == null : "Already a listener registered!";
            registerChangeListener();
            changeListener = l;
        }
        finally
        {
            lockChangeListener.unlock();
        }
    }

    /**
     * Removes a change listener from this component. With this method the
     * change listener that has been registered using
     * <code>addChangeListener()</code> can be removed again. If the passed in
     * listener is different from the registered listener, this operation will
     * have no effect.
     *
     * @param l the listener to be removed
     * @see #addChangeListener(ChangeListener)
     */
    public void removeChangeListener(ChangeListener l)
    {
        lockChangeListener.lock();
        try
        {
            if (l == changeListener)
            {
                changeListener = null;
                unregisterChangeListener();
            }
        }
        finally
        {
            lockChangeListener.unlock();
        }
    }

    /**
     * Registers a focus listener at the managed component. This implementation
     * is fully functional.
     *
     * @param l the listener to register
     */
    public void addFocusListener(FocusListener l)
    {
        getJComponent().addFocusListener(l);
    }

    /**
     * Removes the specified focus listener from this component.
     *
     * @param l the listener to be removed
     */
    public void removeFocusListener(FocusListener l)
    {
        getJComponent().removeFocusListener(l);
    }

    /**
     * Adds the specified mouse listener to the managed component. This
     * implementation is fully functional.
     *
     * @param l the listener to be added
     */
    public void addMouseListener(MouseListener l)
    {
        getJComponent().addMouseListener(l);
    }

    /**
     * Removes the specified mouse listener from the managed component. This
     * implementation is fully functional.
     *
     * @param l the listener to remove
     */
    public void removeMouseListener(MouseListener l)
    {
        getJComponent().removeMouseListener(l);
    }

    /**
     * Registers this component handler as a change listener at the managed
     * component. This method is called when the first change listener is added.
     * A derived class which supports change listeners must override it by
     * registering the correct listener type. In the listener's event handling
     * method the <code>fireEvent()</code> method should be called. This
     * implementation throws an <code>UnsupportedOperationException</code>
     * exception.
     */
    protected void registerChangeListener()
    {
        throw new UnsupportedOperationException(
                "Change listeners are not supported for this component type!");
    }

    /**
     * Unregisters this component as change listener from the managed component.
     * This method is invoked by <code>removeChangeListener()</code>. This
     * base implementation throws an <code>UnsupportedOperationException</code>
     * exception.
     *
     * @see #registerChangeListener()
     */
    protected void unregisterChangeListener()
    {
        throw new UnsupportedOperationException(
                "Change listeners are not supported for this component type!");
    }

    /**
     * Fires a change event to the registered change listeners.
     *
     * @param event the event
     */
    protected void fireChangeEvent(Object event)
    {
        lockChangeListener.lock();
        try
        {
            if (changeListener != null)
            {
                changeListener.componentChanged(event);
            }
        }
        finally
        {
            lockChangeListener.unlock();
        }
    }
}

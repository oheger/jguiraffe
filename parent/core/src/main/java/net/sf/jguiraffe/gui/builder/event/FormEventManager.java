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
package net.sf.jguiraffe.gui.builder.event;

import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.event.EventListenerList;

import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.ComponentStore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * The main class for event handling in the form framework.
 * </p>
 * <p>
 * When programming against a concrete GUI framework like Swing or AWT, a
 * developer usually registers event listeners directly at the components of
 * interest. In this generic GUI framework the same task is done using this
 * class.
 * </p>
 * <p>
 * This class is used by clients of the form framework to register event
 * listeners at components of a form. It provides an
 * <code>addXXXListener()</code> method for each supported event listener type.
 * Each of these methods comes in two overloaded versions: One version allows to
 * specify the name of a concrete component. The listener will then only be
 * registered at that component. The other version registers the listeners for
 * all components; so it will receive all occurring events of the corresponding
 * type. Corresponding <code>removeXXXListener()</code> methods for
 * unregistering listeners are also available.
 * </p>
 * <p>
 * Registering and unregistering event listeners can be done from arbitrary
 * threads and will not conflict with the firing of events. This class
 * implements proper synchronization. It is also possible to add or remove
 * listeners in an invoked event handler.
 * </p>
 * <p>
 * This event manager class provides an additional level of abstraction over the
 * platform (i.e. GUI library) specific event manager class (the concrete
 * implementation of the {@link PlatformEventManager} interface).
 * It is possible to use this implementation directly to register event
 * listeners. The reason for this additional level is that it provides a simple
 * way for registering broadcast event listeners, i.e. listeners that are
 * capable of receiving all events of a specific type, caused by all involved
 * components. In addition it provides an easier interface for the listener
 * registration process.
 * </p>
 * <p>
 * The event manager class needs access to the component handlers of the
 * available components. For this purpose it maintains a reference to a
 * {@link ComponentStore} instance. This instance must have been
 * initialized before the manager can be used.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormEventManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormEventManager
{
    /** Constant for the name pattern for an add event listener method. */
    private static final String METH_ADD_LISTENER = "add%sListener";

    /** Constant for the name pattern for a remove event listener method. */
    private static final String METH_REMOVE_LISTENER = "remove%sListener";

    /**
     * Constant for the argument types of an event listener registration method.
     */
    private static final Class<?>[] LISTENER_REG_TYPES = {
        null
    };

    /** The logger used by this class. */
    private final Log log = LogFactory.getLog(getClass());

    /** Stores a reference to the platform event manager. */
    private final PlatformEventManager platformEventManager;

    /** Holds a reference to the component store storing the actual components. */
    private ComponentStore componentStore;

    /** A map with event listeners that are registered at specific components. */
    private final Map<String, EventListenerList> namedListeners;

    /** Stores then listeners that are registered for all components. */
    private final EventListenerList allListeners;

    /** A helper object for invoking a method using reflection. */
    private final InvocationHelper invocationHelper;

    /** A lock for protecting adding and removing event listeners. */
    private final Lock lockListeners;

    /**
     * Creates a new instance of <code>FormEventManager</code> and sets the
     * platform specific event manager.
     *
     * @param platformEventManager the platform event manager
     */
    public FormEventManager(PlatformEventManager platformEventManager)
    {
        this.platformEventManager = platformEventManager;
        namedListeners = new HashMap<String, EventListenerList>();
        allListeners = new EventListenerList();
        lockListeners = new ReentrantLock();
        invocationHelper = new InvocationHelper();
    }

    /**
     * Returns the platform specific event manager.
     *
     * @return the platform event manager
     */
    public PlatformEventManager getPlatformEventManager()
    {
        return platformEventManager;
    }

    /**
     * Returns the component store used by this event manager instance.
     *
     * @return the underlying component store
     */
    public ComponentStore getComponentStore()
    {
        return componentStore;
    }

    /**
     * Sets the component store to be used. From this store the event manager
     * will fetch the <code>ComponentHandler</code> objects of the components,
     * for which event listeners are to be registered.
     *
     * @param componentStore the component store to be used
     */
    public void setComponentStore(ComponentStore componentStore)
    {
        this.componentStore = componentStore;
    }

    /**
     * Returns the component handler with the given name. If this name is
     * undefined, <b>null </b> is returned.
     *
     * @param name the name of the desired component
     * @return the component handler for the component with this name
     * @throws IllegalStateException if no component store has been set
     */
    public ComponentHandler<?> getComponentHandler(String name)
    {
        if (getComponentStore() == null)
        {
            throw new IllegalStateException("ComponentStore must be set!");
        }
        return getComponentStore().findComponentHandler(name);
    }

    /**
     * Adds an action listener for the specified component.
     *
     * @param name the component's name
     * @param l the listener to add (can be <b>null</b>, then this operation
     *        will have no effect)
     * @throws NoSuchElementException if no component with this names exists
     */
    public void addActionListener(String name, FormActionListener l)
    {
        addListener(FormListenerType.ACTION, name, l);
    }

    /**
     * Adds an action listener that will be notified about all occurring action
     * events.
     *
     * @param l the listener to add (can be <b>null</b>, then this operation
     *        will have no effect)
     */
    public void addActionListener(FormActionListener l)
    {
        addActionListener(null, l);
    }

    /**
     * Removes the specified action listener for the specified component.
     *
     * @param name the component's name
     * @param l the listener to be removed (if this listener is not registered
     *        at this component, this operation will have no effect)
     */
    public void removeActionListener(String name, FormActionListener l)
    {
        removeListener(FormListenerType.ACTION, name, l);
    }

    /**
     * Removes an action listener that is registered for all components.
     *
     * @param l the action listener to be removed (if this listener is not
     *        registered for all components, this operation will have no effect)
     */
    public void removeActionListener(FormActionListener l)
    {
        removeActionListener(null, l);
    }

    /**
     * Adds a change listener for the specified component.
     *
     * @param name the component's name
     * @param l the listener to add
     * @throws NoSuchElementException if no component with this names exists
     */
    public void addChangeListener(String name, FormChangeListener l)
    {
        addListener(FormListenerType.CHANGE, name, l);
    }

    /**
     * Adds a change listener that will be notified about all occurring change
     * events.
     *
     * @param l the listener to add
     */
    public void addChangeListener(FormChangeListener l)
    {
        addChangeListener(null, l);
    }

    /**
     * Removes the specified change listener for the specified component.
     *
     * @param name the component's name
     * @param l the listener to be removed (if this listener is not registered
     *        at this component, this operation will have no effect)
     */
    public void removeChangeListener(String name, FormChangeListener l)
    {
        removeListener(FormListenerType.CHANGE, name, l);
    }

    /**
     * Removes a change listener that is registered for all components.
     *
     * @param l the change listener to be removed (if this listener is not
     *        registered for all components, this operation will have no effect)
     */
    public void removeChangeListener(FormChangeListener l)
    {
        removeChangeListener(null, l);
    }

    /**
     * Adds a focus listener for the specified component.
     *
     * @param name the component's name
     * @param l the listener to add
     * @throws NoSuchElementException if no component with this names exists
     */
    public void addFocusListener(String name, FormFocusListener l)
    {
        addListener(FormListenerType.FOCUS, name, l);
    }

    /**
     * Adds a focus listener that will be notified about all occurring focus
     * events.
     *
     * @param l the listener to add
     */
    public void addFocusListener(FormFocusListener l)
    {
        addFocusListener(null, l);
    }

    /**
     * Removes the specified focus listener for the specified component.
     *
     * @param name the component's name
     * @param l the listener to be removed (if this listener is not registered
     *        at this component, this operation will have no effect)
     */
    public void removeFocusListener(String name, FormFocusListener l)
    {
        removeListener(FormListenerType.FOCUS, name, l);
    }

    /**
     * Removes a focus listener that is registered for all components.
     *
     * @param l the focus listener to be removed (if this listener is not
     *        registered for all components, this operation will have no effect)
     */
    public void removeFocusListener(FormFocusListener l)
    {
        removeFocusListener(null, l);
    }

    /**
     * Adds a mouse listener to the specified component.
     *
     * @param name the name of the component
     * @param l the listener to add
     * @throws NoSuchElementException if no component with this name exists
     */
    public void addMouseListener(String name, FormMouseListener l)
    {
        addListener(FormListenerType.MOUSE, name, l);
    }

    /**
     * Adds a mouse listener that will be notified about all mouse events
     * generated for the components in the current form.
     *
     * @param l the listener to add
     */
    public void addMouseListener(FormMouseListener l)
    {
        addMouseListener(null, l);
    }

    /**
     * Removes the specified mouse listener from the component with the given
     * name. If this listener is not registered at this component, this
     * operation has no effect.
     *
     * @param name the name of the component
     * @param l the listener to be removed
     */
    public void removeMouseListener(String name, FormMouseListener l)
    {
        removeListener(FormListenerType.MOUSE, name, l);
    }

    /**
     * Removes a mouse listener that is registered for all components. If the
     * listener is unknown, this operation has no effect.
     *
     * @param l the listener to be removed
     */
    public void removeMouseListener(FormMouseListener l)
    {
        removeMouseListener(null, l);
    }

    /**
     * Sends the specified event to all registered listeners. This
     * implementation relies on the given event object to be properly
     * initialized, especially the component handler and name fields must be
     * correctly filled. From the event the name of the affected component is
     * extracted. All event listeners of the specified type that are registered
     * for this specific component are notified first. Then the unspecific event
     * listeners are invoked.
     *
     * @param event the event
     * @param type the event listener type
     */
    public void fireEvent(FormEvent event, FormListenerType type)
    {
        lock(type);
        try
        {
            if (event.getName() != null)
            {
                EventListenerList listeners = fetchListenersForComponent(event
                        .getName(), false);
                if (listeners != null)
                {
                    type.callListeners(listeners, event);
                }
            }

            type.callListeners(allListeners, event);
        }
        finally
        {
            unlock(type);
        }
    }

    /**
     * Adds a generic event listener to a component. This is the most generic
     * way of adding an event listener. While there are specific methods for
     * adding default event listeners (e.g. {@code addActionListener()} or
     * {@code addFocusListener()}), using this method arbitrary listeners can be
     * added to components - also for non-standard events. The type of the
     * listener is specified as a string. If this string refers to a standard
     * event type (i.e. the string contains the name of one of the constants
     * defined by the {@link FormListenerType} enumeration class ignoring case),
     * the behavior of this method is exactly the same as if the corresponding
     * specific {@code add()} method was called. Otherwise, the method uses
     * reflection to find a corresponding method for adding the listener to a
     * {@code ComponentHandler} based on naming conventions. The following
     * convention is used: If the string <em>Foo</em> is passed, a method with
     * the name <em>addFooListener()</em> is searched. This method is then
     * invoked passing in the specified event listener. The type of the listener
     * must be compatible with the listener type expected by the {@code
     * addXXXListener()} method. For instance, the {@code TreeHandler}
     * interface, an extension of {@code ComponentHandler} defines an {@code
     * addExpansionListener()} method for adding specialized listeners for tree
     * events. By passing in the string <em>Expansion</em> it is possible to
     * register listeners of this type. If a component name is passed in, the
     * listener is only registered at this component. Otherwise all {@code
     * ComponentHandler} objects currently known are searched for corresponding
     * methods for adding event listeners. The return value of this method
     * determines the number of components, for which the event listener was
     * added. A return value of 0 typically indicates that something went wrong:
     * maybe there was a typo in the string representing the event listener
     * type.
     *
     * @param componentName the name of the component, for which the listener
     *        should be added; can be <b>null</b>, then all fitting components
     *        are processed
     * @param listenerType a string determining the listener type
     * @param l the listener to be added (can be <b>null</b>, then this method
     *        has no effect)
     * @return the number of components the event listener was registered at
     */
    public int addEventListener(String componentName, String listenerType,
            EventListener l)
    {
        return processEventListener(componentName, listenerType, l, false);
    }

    /**
     * Removes an arbitrary event listener from a component. This method is the
     * counterpart of the
     * {@link #addEventListener(String, String, EventListener)} method. It works
     * analogously to remove event listeners. The return value indicates the
     * number of components for which the remove method for the event listener
     * was called. This does not necessarily mean that the listener was actually
     * registered at this components and was removed.
     *
     * @param componentName the name of the component, for which the listener
     *        should be added; can be <b>null</b>, then all fitting components
     *        are processed
     * @param listenerType a string determining the listener type
     * @param l the listener to be removed (can be <b>null</b>, then this method
     *        has no effect)
     * @return the number of components from which the event listener was
     *         removed
     */
    public int removeEventListener(String componentName, String listenerType,
            EventListener l)
    {
        return processEventListener(componentName, listenerType, l, true);
    }

    /**
     * Adds a generic event listener to the specified object. This method works
     * like {@link #addEventListener(String, String, EventListener)}, however,
     * the target object can be specified directly. The listener is registered
     * using reflection as described in the comment for {@code
     * addEventListener()}.
     *
     * @param target the target object to which the listener is to be registered
     * @param listenerType a string determining the listener type
     * @param l the listener to be added (can be <b>null</b>, then this method
     *        has no effect)
     * @return a flag whether the listener could be added successfully
     */
    public boolean addEventListenerToObject(Object target, String listenerType,
            EventListener l)
    {
        return processNonStdEventListenerTargetObject(target, listenerType, l,
                false);
    }

    /**
     * Removes an arbitrary event listener from the specified object. This is
     * the counterpart of
     * {@link #addEventListenerToObject(Object, String, EventListener)}. It
     * works analogously to
     * {@link #removeEventListener(String, String, EventListener)}, but the
     * object affected is directly passed.
     *
     * @param target the object from which the listener is to be removed
     * @param listenerType a string determining the listener type
     * @param l the listener to be removed (can be <b>null</b>, then this method
     *        has no effect)
     * @return a flag whether the listener could be removed without errors
     */
    public boolean removeEventListenerFromObject(Object target,
            String listenerType, EventListener l)
    {
        return processNonStdEventListenerTargetObject(target, listenerType, l,
                true);
    }

    /**
     * Performs the actual adding of an event listener. This method is called by
     * the various <code>addXXXListener()</code> methods.
     *
     * @param type the type of the listener to be added
     * @param name the name of the component (<b>null</b> for all listeners)
     * @param l the affected event listener
     */
    protected void addListener(FormListenerType type, String name,
            FormEventListener l)
    {
        if (l != null)
        {
            if (name == null)
            {
                addAllListener(type, l);
            }
            else
            {
                addNamedListener(type, name, l);
            }
        }
    }

    /**
     * Performs the actual removal of an event listener. This method is called
     * by the various {@code removeXXXListener()} methods.
     *
     * @param type the type of the listener to be removed
     * @param name the name of the component (<b>null</b> for all listeners)
     * @param l the affected event listener
     * @return a flag whether the listener could be removed
     */
    protected boolean removeListener(FormListenerType type, String name,
            FormEventListener l)
    {
        if (name == null)
        {
            return removeAllListener(type, l);
        }
        else
        {
            return removeNamedListener(type, name, l);
        }
    }

    /**
     * Helper method for registering an event listener at a specific component.
     *
     * @param type the listener type
     * @param name the component's name
     * @param l the listener to register
     * @throws NoSuchElementException if no component with this names exists
     */
    private void addNamedListener(FormListenerType type, String name,
            FormEventListener l) throws NoSuchElementException
    {
        ComponentHandler<?> ch = getComponentHandler(name);
        if (ch == null)
        {
            throw new NoSuchElementException("No component with the name "
                    + name);
        }

        lock(type);
        try
        {
            if (fetchAllListenerCount(type) == 0
                    && fetchNamedListenerCount(name, type) == 0)
            {
                if (log.isInfoEnabled())
                {
                    log.info("Registering listener of type " + type.name()
                            + " for component " + name);
                    getPlatformEventManager().registerListener(name, ch, this,
                            type);
                }
            }
            registerListener(type, l, fetchListenersForComponent(name, true));
        }
        finally
        {
            unlock(type);
        }
    }

    /**
     * Helper method for registering an event listener of a specific type for
     * all components. This method registers this event manager as listener for
     * the specified type for all components that have not yet been registered.
     *
     * @param type the listener type
     * @param l the listener to register
     */
    private void addAllListener(FormListenerType type, FormEventListener l)
    {
        lock(type);
        try
        {
            if (fetchAllListenerCount(type) < 1)
            {
                for (String compName : getComponentStore()
                        .getComponentHandlerNames())
                {
                    if (fetchNamedListenerCount(compName, type) < 1)
                    {
                        if (log.isInfoEnabled())
                        {
                            log.info("Registering listener of type "
                                    + type.name() + " for component "
                                    + compName);
                        }
                        getPlatformEventManager().registerListener(compName,
                                getComponentHandler(compName), this, type);
                    }
                }
            }

            registerListener(type, l, allListeners);
        }
        finally
        {
            unlock(type);
        }
    }

    /**
     * Removes a listener for a specific component.
     *
     * @param type the event listener type
     * @param name the name of the component
     * @param l the listener to be removed
     * @return a flag whether the listener could be removed
     */
    private boolean removeNamedListener(FormListenerType type, String name,
            FormEventListener l)
    {
        lock(type);
        try
        {
            int countBefore = fetchNamedListenerCount(name, type);
            if (countBefore > 0)
            {
                unregisterListener(type, l, fetchListenersForComponent(name,
                        false));

                int countAfter = fetchNamedListenerCount(name, type);
                if (countAfter == 0 && fetchAllListenerCount(type) == 0)
                {
                    getPlatformEventManager().unregisterListener(name,
                            getComponentHandler(name), this, type);
                }

                return countBefore > countAfter;
            }
        }
        finally
        {
            unlock(type);
        }

        return false;
    }

    /**
     * Removes the specified all listener from all components.
     *
     * @param type the event listener type
     * @param l the listener to be removed
     * @return a flag whether the listener could be removed
     */
    private boolean removeAllListener(FormListenerType type, FormEventListener l)
    {
        lock(type);
        try
        {
            int countBefore = fetchAllListenerCount(type);
            if (countBefore > 0)
            {
                unregisterListener(type, l, allListeners);

                int countAfter = fetchAllListenerCount(type);
                if (countAfter == 0)
                {
                    for (String name : getComponentStore()
                            .getComponentHandlerNames())
                    {
                        if (fetchNamedListenerCount(name, type) < 1)
                        {
                            getPlatformEventManager().unregisterListener(name,
                                    getComponentHandler(name), this, type);
                        }
                    }
                }

                return countBefore > countAfter;
            }
        }
        finally
        {
            unlock(type);
        }

        return false;
    }

    /**
     * Obtains the lock for the specified listener type. Adding or removing
     * event listeners of the same type is not possible concurrently, so locks
     * must be used. However in theory listeners of different types can be
     * processed simultaneously. However practice has shown that this is
     * problematic (especially on machines with a dual core processor). So we
     * perform a strict locking here.
     *
     * @param type the listener type to be locked
     */
    private void lock(FormListenerType type)
    {
        lockListeners.lock();
    }

    /**
     * Unlocks the specified listener type again.
     *
     * @param type the listener type to be unlocked
     * @see #lock(FormListenerType)
     */
    private void unlock(FormListenerType type)
    {
        lockListeners.unlock();
    }

    /**
     * Fetches the list with the event listeners registered for the specified
     * component. This list will be created lazily on the first write access.
     * The <code>create</code> parameter determines whether the list should be
     * created if it does not exist yet. If it is <b>false</b>, the return value
     * may be <b>null</b>.
     *
     * @param name the name of the component
     * @param create the create flag
     * @return the list with the event listeners registered at this component
     */
    private EventListenerList fetchListenersForComponent(String name,
            boolean create)
    {
        synchronized (namedListeners)
        {
            EventListenerList result = namedListeners.get(name);
            if (create && result == null)
            {
                result = new EventListenerList();
                namedListeners.put(name, result);
            }
            return result;
        }
    }

    /**
     * Determines the number of event listeners of the given type that are
     * registered at the component with the given name.
     *
     * @param name the name of the component
     * @param type the event listener type
     * @return the number of registered listeners of that type
     */
    private int fetchNamedListenerCount(String name, FormListenerType type)
    {
        EventListenerList lst = fetchListenersForComponent(name, false);
        return (lst != null) ? lst.getListenerCount(type.getListenerClass())
                : 0;
    }

    /**
     * Returns the number of event listeners of the specified type that are
     * registered at all components.
     *
     * @param type the event listener type
     * @return the number of all event listeners of that type
     */
    private int fetchAllListenerCount(FormListenerType type)
    {
        return allListeners.getListenerCount(type.getListenerClass());
    }

    /**
     * Adds an event listener of the specified type to the given event listener
     * list.
     *
     * @param type the type
     * @param l the listener
     * @param listeners the list with the listeners
     */
    @SuppressWarnings("unchecked")
    private void registerListener(FormListenerType type, FormEventListener l,
            EventListenerList listeners)
    {
        listeners.add((Class<FormEventListener>) type.getListenerClass(), l);
    }

    /**
     * Removes a listener of the specified type from the given event listener
     * list.
     *
     * @param type the type
     * @param l the listener
     * @param listeners the list with the listeners
     */
    @SuppressWarnings("unchecked")
    private void unregisterListener(FormListenerType type, FormEventListener l,
            EventListenerList listeners)
    {
        listeners.remove((Class<FormEventListener>) type.getListenerClass(), l);
    }

    /**
     * Helper method for adding or removing an event listener. This method is
     * called by both {@code addEventListener()}, and {@code
     * removeEventListener()} to actually handle the registration stuff.
     *
     * @param componentName the name of the component
     * @param listenerType the type of the listener
     * @param l the affected listener
     * @param remove flag for remove (<b>true</b>) or add (<b>false</b>) the
     *        listener
     * @return the number of components affected by this operation
     */
    private int processEventListener(String componentName, String listenerType,
            EventListener l, boolean remove)
    {
        if (l == null)
        {
            return 0;
        }

        FormListenerType type = FormListenerType.fromString(listenerType);
        if (type != null && type.getListenerClass().isInstance(l))
        {
            return processStdEventListener(componentName, type,
                    (FormEventListener) l, remove);
        }

        else
        {
            Set<String> compNames = (componentName != null) ? Collections
                    .singleton(componentName) : getComponentStore()
                    .getComponentHandlerNames();
            return processNonStdEventListener(compNames, listenerType, l,
                    remove);
        }
    }

    /**
     * Helper method for adding or removing a standard event listener. This
     * method is called by {@code processEventListener()} if the listener type
     * can be resolved to a standard type. It delegates either to {@code
     * addListener()} or {@code removeListener()}.
     *
     * @param componentName the name of the component
     * @param type the type of the event listener
     * @param l the affected listener
     * @param remove the remove flag
     * @return the number of components affected by this operation
     */
    private int processStdEventListener(String componentName,
            FormListenerType type, FormEventListener l, boolean remove)
    {
        boolean success;

        try
        {
            if (remove)
            {
                success = removeListener(type, componentName, l);
            }
            else
            {
                addListener(type, componentName, l);
                success = true;
            }
        }
        catch (NoSuchElementException nsex)
        {
            success = false;
        }

        if (!success)
        {
            return 0;
        }
        else
        {
            return (componentName != null) ? 1 : getComponentStore()
                    .getComponentHandlerNames().size();
        }
    }

    /**
     * Helper method for adding or removing a non-standard event listener. This
     * method is called by {@code processEventListener()} if the listener type
     * does not refer to a standard type. It tries to invoke listener
     * registration methods on the known {@code ComponentHandler} objects
     * through reflection.
     *
     * @param compNames a set with the names of the {@code ComponentHandler}
     *        objects to be processed
     * @param listenerType the type of the event listener
     * @param l the affected event listener
     * @param remove the remove flag
     * @return the number of {@code ComponentHandler} objects that could be
     *         processed
     */
    private int processNonStdEventListener(Set<String> compNames,
            String listenerType, EventListener l, boolean remove)
    {
        int successCount = 0;

        for (String compName : compNames)
        {
            ComponentHandler<?> handler = getComponentHandler(compName);
            if (handler != null
                    && doProcessNonStdEventListener(handler, listenerType, l,
                            remove))
            {
                successCount++;
            }
        }

        return successCount;
    }

    /**
     * A helper method that implements the major part of the functionality
     * required by {@code addEventListenerToObject()} and {@code
     * removeEventListenerFromObject()}. It performs some validity checks and
     * then delegates to {@code doProcessNonStdEventListener()}.
     *
     * @param target the target object
     * @param listenerType the type of the event listener
     * @param l the affected event listener
     * @param remove the remove flag
     * @return a flag whether the operation was successful
     */
    private boolean processNonStdEventListenerTargetObject(
            Object target, String listenerType, EventListener l, boolean remove)
    {
        return (target == null || l == null) ? false
                : doProcessNonStdEventListener(target, listenerType, l, remove);
    }

    /**
     * Helper method that uses reflection for adding or removing an event
     * handler from an arbitrary object. The return value indicates, whether the
     * operation was successful. Exceptions are caught.
     *
     * @param target the target object
     * @param listenerType the type of the event listener
     * @param l the affected event listener
     * @param remove the remove flag
     * @return a flag whether the operation was successful
     */
    private boolean doProcessNonStdEventListener(Object target,
            String listenerType, EventListener l, boolean remove)
    {
        String methodName =
                String.format(
                        remove ? METH_REMOVE_LISTENER : METH_ADD_LISTENER,
                        listenerType);

        try
        {
            invocationHelper.invokeInstanceMethod(target, methodName,
                    LISTENER_REG_TYPES, new Object[] {
                        l
                    });
            return true;
        }
        catch (InjectionException iex)
        {
            log.info("Could not register listener of type " + listenerType
                    + " at target object " + target, iex);
            return false;
        }
    }
}

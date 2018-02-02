/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.sf.jguiraffe.gui.builder.event.BuilderEvent;
import net.sf.jguiraffe.gui.builder.event.filter.AndEventFilter;
import net.sf.jguiraffe.gui.builder.event.filter.EventFilter;

/**
 * <p>
 * A class that allows to combine actions with event listeners.
 * </p>
 * <p>
 * This class can be used to create an event listener proxy for an arbitrary
 * event listener interface that will delegate to a specified {@link FormAction}
 * object. By defining an {@link EventFilter} it can be exactly specified, which
 * event should cause the action to be invoked.
 * </p>
 * <p>
 * The purpose of this class is to serve as a bridge between the event listener
 * API and the action API. This is especially useful when event listeners are to
 * be defined in builder scripts: Then it is easy to route to actions, which are
 * defined by action tags anyway.
 * </p>
 * <p>
 * Internally this class makes use of the Proxy mechanism supported by Java 1.3
 * and higher. For the passed in event listener interface(s) a proxy object is
 * created. Every invocation of one of the proxy's methods will cause the
 * specified filter to be called to check whether the current event object
 * matches the filter's criteria. If this is the case and if the action is
 * enabled, its <code>execute()</code> method will be invoked.
 * </p>
 * <p>
 * Instances of <code>ActionInvoker</code> should be created using the static
 * factory methods. These methods return an object, which can be passed to one
 * of the interfaces that was passed to the factory method. Then it can be
 * registered as the corresponding event listener at the desired form component.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionInvoker.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ActionInvoker implements InvocationHandler
{
    /**
     * Constant for a dummy filter that will be used when no filter is provided.
     * This filter will accept all passed in objects.
     */
    private static final EventFilter DUMMY_FILTER = new AndEventFilter();

    /** Stores the action to be invoked. */
    private final FormAction action;

    /** Stores the filter for events. */
    private final EventFilter filter;

    /**
     * Creates a new instance of <code>ActionInvoker</code> and initializes it
     * with the action to be invoked. No event filter is used.
     *
     * @param action the action (must not be <b>null</b>)
     */
    ActionInvoker(FormAction action)
    {
        this(action, null);
    }

    /**
     * Creates a new instance of <code>ActionInvoker</code> and initializes it
     * with the action to be invoked and the event filter.
     *
     * @param action the action (must not be <b>null</b>)
     * @param filter the event filter; if <b>null</b>, all events will be
     * accepted
     */
    ActionInvoker(FormAction action, EventFilter filter)
    {
        if (action == null)
        {
            throw new IllegalArgumentException("Action must not be null!");
        }
        this.action = action;
        this.filter = (filter != null) ? filter : DUMMY_FILTER;
    }

    /**
     * Callback method that is invoked whenever a method on an associated event
     * listener interface is called. This implementation will pass the first
     * method argument (or <b>null</b> if there is none) to the associated
     * event filter. If it is accepted by the filter, the associated action will
     * be executed if it is enabled. If the first method argument is of type
     * <code>{@link net.sf.jguiraffe.gui.builder.event.BuilderEvent}</code>,
     * it will be passed to the action's <code>execute()</code> method;
     * otherwise <b>null</b> will be passed.
     *
     * @param obj the current object instance
     * @param meth the method to be invoked
     * @param args the method arguments
     * @return the method's return value (<b>null</b> in this case)
     * @throws Throwable for all occurring exceptions
     */
    public Object invoke(Object obj, Method meth, Object[] args)
            throws Throwable
    {
        Object testObj = (args == null || args.length < 1) ? null : args[0];
        if (filter.accept(testObj))
        {
            if (action.isEnabled())
            {
                BuilderEvent event = (testObj instanceof BuilderEvent) ? (BuilderEvent) testObj
                        : null;
                action.execute(event);
            }
        }

        return null; // return value does not matter
    }

    /**
     * Creates an action invoker proxy for the specified listener interface that
     * will invoke the given action whenever a method of the listener interface
     * is called. The returned object can be casted to the specified listener
     * class and then registered at a component.
     *
     * @param listenerClass the class of the listener interface
     * @param action the action to be invoked (must not be <b>null</b>)
     * @return the event listener proxy
     */
    public static Object create(Class<?> listenerClass, FormAction action)
    {
        return create(listenerClass, action, null);
    }

    /**
     * Creates an action invoker proxy for the specified listener interface that
     * will invoke the given action when an event is triggered that is accepted
     * by the passed in filter. The returned object can be casted to the
     * specified listener class and then registered at a component.
     *
     * @param listenerClass the class of the listener interface
     * @param action the action to be invoked (must not be <b>null</b>)
     * @param filter the event filter (can be <b>null</b>, then all events will
     * be accepted)
     * @return the event listener proxy
     */
    public static Object create(Class<?> listenerClass, FormAction action,
            EventFilter filter)
    {
        return create(new Class<?>[] {
            listenerClass
        }, action, filter);
    }

    /**
     * Creates an action invoker proxy that implements all the specified
     * listener interfaces. It will invoke the given action when an event is
     * triggered that is accepted by the passed in filter. The returned object
     * can be casted to all the specified listener classes and then registered
     * at a component.
     *
     * @param listenerClasses an array of the classes of the listener interfaces
     * @param action the action to be invoked (must not be <b>null</b>)
     * @param filter the event filter (can be <b>null</b>, then all events will
     * be accepted)
     * @return the event listener proxy
     */
    public static Object create(Class<?>[] listenerClasses, FormAction action,
            EventFilter filter)
    {
        if (listenerClasses == null)
        {
            throw new IllegalArgumentException(
                    "Listener classes must not be null!");
        }
        return Proxy.newProxyInstance(listenerClasses[0].getClassLoader(),
                listenerClasses, new ActionInvoker(action, filter));
    }
}

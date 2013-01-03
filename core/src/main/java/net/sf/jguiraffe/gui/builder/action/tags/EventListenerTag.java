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
package net.sf.jguiraffe.gui.builder.action.tags;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.NoSuchElementException;

import net.sf.jguiraffe.gui.builder.action.ActionInvoker;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;
import net.sf.jguiraffe.gui.builder.event.filter.EventFilter;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A base class for event listener tags.
 * </p>
 * <p>
 * This tag handler class provides basic functionality for declarative event
 * listener support. Per default, event listeners are registered manually at the
 * corresponding components (e.g. by calling methods of the
 * {@link net.sf.jguiraffe.gui.builder.event.FormEventManager FormEventManager}
 * class. With the event listener tag family it is now possible to declare event
 * listeners in a builder script. This is done by delegating events to actions
 * (making use of the {@link ActionInvoker} class).
 * </p>
 * <p>
 * Actions can be defined in the builder script or are obtained from the action
 * store maintained by the current
 * {@link net.sf.jguiraffe.gui.builder.BuilderData BuilderData} object. This tag
 * handler class is passed an <code>actionName</code> attribute that identifies
 * the action to be invoked. It also supports the definition of an
 * {@link net.sf.jguiraffe.gui.builder.event.filter.EventFilter EventFilter}
 * object, which can be specified either through the <code>eventFilter</code>
 * attribute or by nesting {@link EventFilterTag} tags in the body of this tag.
 * </p>
 * <p>
 * The basic idea is that a concrete sub class of <code>EventListenerTag</code>
 * registers an event listener of a specific type at a component defined by a
 * tag this tag is nested inside. This event listener will use the specified
 * event filter (if any) to determine, for which event types the action is to be
 * invoked. If the filter matches an event, the associated action is invoked.
 * </p>
 * <p>
 * An advantage of this concept is that events can be treated as actions. So
 * typical functionality of an application can be completely defined in action
 * objects and is then also available for reacting on events. One example where
 * this is useful is an action that terminates an application (of course only
 * after asking the user whether unsaved changes should be stored). This action
 * is per default associated with a menu item and maybe with a tool bar icon.
 * With the mapping from event listeners to actions it is also possible to
 * invoke this action when the close icon of the application's main window is
 * clicked - without having to write any glue code.
 * </p>
 * <p>
 * This base class provides a simple framework for dealing with filters and
 * creating an {@link ActionInvoker} object. Concrete sub classes have to
 * implement the {@link #createAndRegisterListener()} method, which will be
 * invoked by the implementation of the {@link #process()} method. The following
 * attributes are supported:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">actionName</td>
 * <td>Here the name of the action that is to be called by the generated event
 * handler must be specified. An action with this name is looked up in the
 * current context.</td>
 * <td valign="top">No</td>
 * </tr>
 * <tr>
 * <td valign="top">eventFilter</td>
 * <td>With this attribute the tag can be associated with an already existing
 * filter. This attribute is only evaluated if no {@link EventFilter} is
 * specified by a nested tag.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">var</td>
 * <td>If this attribute is specified, the tag stores the event listener that it
 * has created under this name in the current context. This is useful if the
 * listener should be added to other components, too. In this case it can be
 * referenced by an {@code <eventListener>} tag for instance.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">targetBean</td>
 * <td>Typically the target object the event listener is to be registered at is
 * determined by the parent tag this tag is nested inside. Using the {@code
 * targetBean} attribute, an arbitrary object available in the current {@code
 * BeanContext} can be defined as target of the registration. The tag will
 * obtain a bean with the name specified here from the current {@code
 * BeanContext} and delegate to the event manager to register the listener at
 * this bean. It is possible to register a listener at both the object
 * determined by the parent tag and the target object specified by this
 * attribute. Only if the tag is not nested inside an appropriate tag and this
 * attribute is not provided, an exception is thrown.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: EventListenerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class EventListenerTag extends ActionBaseTag
{
    /** A list with information about the listeners to register. */
    private final List<ListenerRegistrationData> listenerTypes;

    /** Stores the name of the action to invoke. */
    private String actionName;

    /** Stores the name of the event filter. */
    private String eventFilter;

    /** The name of the target bean. */
    private String targetBean;

    /** A name under which the newly created event listener is to be stored. */
    private String var;

    /**
     * Creates a new instance of {@code EventListenerTag}.
     */
    protected EventListenerTag()
    {
        listenerTypes = new ArrayList<ListenerRegistrationData>();
    }

    /**
     * Returns the name of the action to be invoked.
     *
     * @return the action's name
     */
    public String getActionName()
    {
        return actionName;
    }

    /**
     * Setter method for the actionName attribute.
     *
     * @param actionName the attribute's value
     */
    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    /**
     * Returns the name of the filter to be used.
     *
     * @return the filter's name
     */
    public String getEventFilter()
    {
        return eventFilter;
    }

    /**
     * Setter method for the eventFilter attribute.
     *
     * @param filterName the attribute's value
     */
    public void setEventFilter(String filterName)
    {
        this.eventFilter = filterName;
    }

    /**
     * Returns the name of the target bean.
     *
     * @return the target bean
     */
    public String getTargetBean()
    {
        return targetBean;
    }

    /**
     * Set method of the {@code targetBean} attribute.
     *
     * @param targetBean the attribute's value
     */
    public void setTargetBean(String targetBean)
    {
        this.targetBean = targetBean;
    }

    /**
     * Returns a variable name for storing the event listener created by this
     * tag.
     *
     * @return the name of a variable for the event listener
     */
    public String getVar()
    {
        return var;
    }

    /**
     * Set method of the {@code var} attribute.
     *
     * @param var the attribute's value
     */
    public void setVar(String var)
    {
        this.var = var;
    }

    /**
     * Adds another listener type in form of a {@code FormListenerType} object.
     * The event listener produced by this tag will also support this listener
     * interface.
     *
     * @param lt the {@code FormListenerType}
     */
    public final void addListenerType(FormListenerType lt)
    {
        listenerTypes.add(new ListenerRegistrationData(lt));
    }

    /**
     * Adds another listener type specified by the type name and the listener
     * class. The event listener produced by this tag will also support this
     * listener interface. This method can be used for non-standard event types.
     *
     * @param typeName the name of the listener type
     * @param listenerClass the class of the listener interface
     * @see net.sf.jguiraffe.gui.builder.event.FormEventManager#addEventListener(String,
     *      String, java.util.EventListener)
     */
    public final void addListenerType(String typeName, Class<?> listenerClass)
    {
        listenerTypes
                .add(new ListenerRegistrationData(typeName, listenerClass));
    }

    /**
     * Performs processing before the tag's body is evaluated. This
     * implementation resets the current filter variable. If an event filter tag
     * is placed in this tag's body, the variable will be set again.
     *
     * @throws JellyTagException if an error occurs
     * @throws FormBuilderException if the tag is incorrectly used
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        getContext().removeVariable(EventFilterTag.CURRENT_FILTER);
    }

    /**
     * Executes this tag. This implementation creates the event listener and
     * handles its registration at a bean if necessary. Then it delegates to
     * {@link #registerListener(EventListener)}, which does tag-specific
     * listener registration.
     *
     * @throws JellyTagException if an error occurs
     * @throws FormBuilderException if the tag is incorrectly used
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        EventListener listener = createEventListener();

        if (getTargetBean() != null)
        {
            addBeanRegistrationCallbacks(listener, getTargetBean(), null);
        }
        if (!registerListener(listener))
        {
            if (getTargetBean() == null)
            {
                throw new JellyTagException("Could not register listener! "
                        + "No target found.");
            }
        }

        if (getVar() != null)
        {
            getContext().setVariable(getVar(), listener);
        }
    }

    /**
     * Creates an event listener proxy for all event listener classes in the
     * specified array. This method creates an {@link ActionInvoker} object and
     * initialize it with the specified action and filter. Note: This
     * implementation expects that the resulting listener can be cast to {@code
     * java.util.EventListener}.
     *
     * @param listenerClasses an array with the event listener classes
     * @return the event listener proxy
     * @throws JellyTagException if the tag is incorrectly used
     * @throws FormBuilderException if an error occurs
     */
    protected EventListener createEventListener(Class<?>[] listenerClasses)
            throws JellyTagException, FormBuilderException
    {
        return (EventListener) ActionInvoker.create(listenerClasses,
                fetchAction(), fetchFilter());
    }

    /**
     * Creates an event listener object for all listener types that have been
     * added to this tag. This method obtains the classes for the event listener
     * interfaces by calling {@link #fetchListenerClasses()}. Then it delegates
     * to {@link #createEventListener(Class[])} to create the listener object.
     * The listener object returned by this method is added to the target
     * object.
     *
     * @return the event listener object.
     * @throws JellyTagException if the tag is incorrectly used
     * @throws FormBuilderException if an error occurs
     */
    protected EventListener createEventListener() throws JellyTagException,
            FormBuilderException
    {
        return createEventListener(fetchListenerClasses());
    }

    /**
     * Tries to obtain the action that is to be invoked by the event listener.
     * This action is fetched from the current action store. If this fails, an
     * exception will be thrown.
     *
     * @return the action to be invoked
     * @throws JellyTagException if the tag is incorrectly used
     * @throws FormBuilderException if a required attribute is missing
     */
    protected FormAction fetchAction() throws JellyTagException,
            FormBuilderException
    {
        if (getActionName() == null)
        {
            throw new MissingAttributeException("actionName");
        }

        try
        {
            return getActionBuilder().getActionStore().getAction(
                    getActionName());
        }
        catch (NoSuchElementException nex)
        {
            throw new FormActionException("Cannot find action with name "
                    + getActionName());
        }
    }

    /**
     * Fetches the event filter if one is defined. Filters defined in the tag's
     * body take precedence. If a filter in the tag's body is defined, it is
     * used. Otherwise the value of the <code>eventFilter</code> attribute is
     * checked. If it is defined, a filter with this name is fetched from the
     * Jelly context.
     *
     * @return the filter to be used (can be <b>null</b>)
     * @throws JellyTagException if a filter name is specified, which cannot be
     *         resolved
     */
    protected EventFilter fetchFilter() throws JellyTagException
    {
        if (getContext().getVariables().containsKey(
                EventFilterTag.CURRENT_FILTER))
        {
            return (EventFilter) getContext().getVariable(
                    EventFilterTag.CURRENT_FILTER);
        }

        if (getEventFilter() != null)
        {
            EventFilter filter = (EventFilter) getContext().getVariable(
                    getEventFilter());
            if (filter == null)
            {
                throw new JellyTagException("Cannot find filter with name "
                        + getEventFilter());
            }
            return filter;
        }
        else
        {
            return null;
        }
    }

    /**
     * Creates callbacks for the registration of all event listener types at the
     * specified component.
     *
     * @param listener the listener object
     * @param compName the name of the component
     */
    protected void addComponentRegistrationCallbacks(EventListener listener,
            String compName)
    {
        for (ListenerRegistrationData lrd : listenerTypes)
        {
            getBuilderData().addCallBack(
                    new ComponentRegistrationCallBack(compName, lrd
                            .getTypeName(), listener, false), null);
        }
    }

    /**
     * Creates callbacks for the registration of all event listener types at the
     * specified bean.
     *
     * @param listener the listener object
     * @param beanName the name of the target bean
     * @param params the parameters for the callback
     */
    protected void addBeanRegistrationCallbacks(EventListener listener,
            String beanName, Object params)
    {
        for (ListenerRegistrationData lrd : listenerTypes)
        {
            getBuilderData().addCallBack(
                    new BeanRegistrationCallBack(beanName, lrd.getTypeName(),
                            listener, false), params);
        }
    }

    /**
     * Returns an array with the classes of the event listeners this tag has to
     * support.
     *
     * @return the specified event listener classes
     */
    protected Class<?>[] fetchListenerClasses()
    {
        Class<?>[] listenerClasses = new Class<?>[listenerTypes.size()];
        int idx = 0;

        for (ListenerRegistrationData lrd : listenerTypes)
        {
            listenerClasses[idx++] = lrd.getListenerClass();
        }

        return listenerClasses;
    }

    /**
     * Registers the event listener. This method is called during execution of
     * this tag with the listener object created by the
     * {@link #createEventListener()} method. Its task is to perform
     * tag-specific listener registration (this base class already takes about
     * registration of the event listener at a bean that may be specified using
     * the {@code targetBean} attribute). The return value indicates whether a
     * registration was possible. If it is <b>false</b> and no {@code
     * targetBean} attribute is defined, tag execution throws an exception
     * because no target could be determined.
     *
     * @param listener the listener to be registered
     * @return a flag whether registration was successful
     * @throws JellyTagException if an error occurs
     * @throws FormBuilderException if the tag is incorrectly used
     */
    protected abstract boolean registerListener(EventListener listener)
            throws JellyTagException, FormBuilderException;

    /**
     * A simple data class for storing the information required for registering
     * an event listener of a specific type.
     */
    private static class ListenerRegistrationData
    {
        /** The name of the listener type. */
        private final String typeName;

        /** The listener class. */
        private final Class<?> listenerClass;

        /**
         * Creates a new instance of {@code ListenerRegistrationData} and
         * initializes it with the given type name and listener class.
         *
         * @param type the type name
         * @param cls the listener class
         */
        public ListenerRegistrationData(String type, Class<?> cls)
        {
            typeName = type;
            listenerClass = cls;
        }

        /**
         * Creates a new instance of {@code ListenerRegistrationData} and
         * initializes it with the given listener type.
         *
         * @param flt the listener type
         */
        public ListenerRegistrationData(FormListenerType flt)
        {
            typeName = flt.listenerTypeName();
            listenerClass = flt.getListenerClass();
        }

        /**
         * Returns the name of the listener type.
         *
         * @return the type name
         */
        public String getTypeName()
        {
            return typeName;
        }

        /**
         * Returns the listener class.
         *
         * @return the listener class
         */
        public Class<?> getListenerClass()
        {
            return listenerClass;
        }
    }
}

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
package net.sf.jguiraffe.gui.builder.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.impl.DefaultBeanContext;
import net.sf.jguiraffe.di.impl.SimpleBeanStoreImpl;
import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManager;
import net.sf.jguiraffe.gui.forms.BindingStrategy;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.ComponentStore;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.transform.TransformerContext;

import org.apache.commons.jelly.JellyContext;

/**
 * <p>
 * A class for storing temporary data and the results of a form builder
 * operation.
 * </p>
 * <p>
 * For every call of a form builder an instance of this class is created. During
 * the build process a couple of information is created. Some of this belongs to
 * the final result, other parts need to be accessed later to resolve references
 * or for different purposes. This class is a home for all these kinds of data.
 * </p>
 * <p>
 * Especially the components created during a builder operation must be stored
 * somewhere so that they can be combined to a resulting {@code Form}
 * object. For this purpose this class provides an implementation of the
 * {@code ComponentStore} interface and registers it as the default
 * component store. To access the component store interested parties do not
 * directly invoke the methods provided by {@code ComponentStore}, but
 * access them through the {@code storeXXXX()} and {@code getXXXX()}
 * methods defined in this class. These methods obtain the current store and
 * delegate the call to it. During the builder operation a different
 * {@code ComponentStore} can be temporarily set using the
 * {@code pushComponentStore()} and {@code popComponentStore()}
 * methods. This makes it possible for complex components to catch all the
 * components created in their context; this way sub forms can be created that
 * for instance represent a row in a table. With the
 * {@code pushFormContext()} and {@code popFormContext()} methods a
 * complete new form context can be created, i.e. all components will be added
 * to the passed in form, and event registration can be performed on the
 * components that belong to that form.
 * </p>
 * <p>
 * To make the current form and all of its components available to the
 * <em>dependency injection</em> framework, this class implements the
 * {@link SimpleBeanStoreImpl.BeanContributor} interface and can therefore
 * collaborate with a {@link SimpleBeanStoreImpl}. There is also a
 * {@code getBeanContext()} method that returns a context for querying all
 * available beans. Through this context the global beans (as defined by the
 * application) can be queried and the objects created during the builder
 * operation as well. To access the components and their associated handler
 * classes the following naming scheme is used:
 * <ul>
 * <li>Components (i.e. the platform specific objects created by the
 * {@code ComponentManager}) can be accessed using the name that is
 * specified in the Jelly builder script. For instance (if
 * {@code builderData} is an instance of {@code ComponentBuilderData})
 * {@code builderData.getBeanContext().getBean("txtFirstName");} would
 * return the input component associated with the name <em>txtFirstName</em>
 * (probably a {@code JTextField} if Swing is used).</li>
 * <li>For accessing the {@code ComponentHandler}s for the managed
 * components the prefix <em>comp:</em>:
 * {@code builderData.getBeanContext().getBean("comp:txtFirstName");} would
 * return the {@code ComponentHandler} for the <em>txtFirstName</em>
 * component.</li>
 * <li>{@code FieldHandler}s are also accessed using a special prefix:
 * <em>field:</em>. So for obtaining the {@code FieldHandler} for the
 * <em>txtFirstName</em> field you would write
 * {@code builderData.getBeanContext().getBean("field:txtFirstName");}.</li>
 * <li>Each component is associated with a {@code WidgetHandler}. For
 * obtaining a component's {@code WidgetHandler} the prefix
 * <em>widget:</em> is used, as in
 * {@code builderData.getBeanContext().getBean("widget:txtFirstName");}.</li>
 * <li>The {@code Form} object constructed during the current builder
 * operation can also be accessed under a special reserved key:
 * {@code Form form = (Form) builderData.getBeanContext().getBean("CURRENT_FORM");}
 * .</li>
 * <li>The {@code BeanContext} maintained by this instance can also be
 * accessed (e.g. to be injected into a bean defined by the current builder
 * script). This can be done using the key <em>CURRENT_CONTEXT</em>.</li>
 * <li>The current {@link net.sf.jguiraffe.gui.builder.BuilderData BuilderData}
 * object is available under the key <em>BUILDER_DATA</em>.</li>
 * <li>Finally the current {@code ComponentBuilderData} instance itself is
 * exposed through the {@code BeanStore} implementation. The corresponding
 * reserved key is named <em>COMPONENT_BUILDER_DATA</em>.</li>
 * </ul>
 * To avoid naming conflicts the identifiers for components in the builder
 * scripts should be chosen in a way that they do not interfere with this
 * reserved keys. Note that this class also defines constants for these keys.
 * </p>
 * <p>
 * The current instance of this class for the running builder process is stored
 * in the Jelly Context, where it can be accessed from all tags. From here also
 * references to the factories needed for creating components can be obtained.
 * The root container object is maintained by this object, too.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe. If it is accessed
 * concurrently by multiple threads, proper synchronization must be ensured.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComponentBuilderData.java 208 2012-02-11 20:57:33Z oheger $
 */
public class ComponentBuilderData implements Composite,
        SimpleBeanStoreImpl.BeanContributor
{
    /**
     * Constant for the prefix for accessing component handlers. This prefix has
     * to be used for obtaining the {@code ComponentHandler} of a
     * component from the bean context managed by this class.
     */
    public static final String KEY_COMPHANDLER_PREFIX = "comp";

    /**
     * Constant for the prefix for accessing field handlers. This prefix has to
     * be used for obtaining the {@code FieldHandler} of a component from
     * the bean context managed by this class.
     */
    public static final String KEY_FIELDHANDLER_PREFIX = "field";

    /**
     * Constant for the prefix for accessing widget handlers. This prefix has to
     * be used for obtaining the {@code WidgetHandler} of a component
     * from the bean context managed by this class.
     */
    public static final String KEY_WIDGETHANDLER_PREFIX = "widget";

    /**
     * Constant for the key for accessing the current form from the bean context
     * managed by this class.
     */
    public static final String KEY_FORM = "CURRENT_FORM";

    /**
     * Constant for the key for accessing the current instance of this class
     * from the managed bean context.
     */
    public static final String KEY_COMPONENT_BUILDER_DATA = "COMPONENT_BUILDER_DATA";

    /**
     * Constant for the key for accessing the current bean context. This name
     * can be used to inject a context into beans defined in a Jelly builder
     * script.
     */
    public static final String KEY_CURRENT_CONTEXT = "CURRENT_CONTEXT";

    /**
     * Constant for the key for accessing the current {@code BuilderData}
     * object from the bean context managed by this class. The
     * {@code BuilderData} object allows access to some important,
     * application-global objects.
     */
    public static final String KEY_BUILDER_DATA = "BUILDER_DATA";

    /** Constant for the key prefix separator. */
    static final char PREFIX_SEPARATOR = ':';

    /**
     * Constant for the name under which an object is stored in the jelly
     * context.
     */
    private static final String CTX_NAME = ComponentBuilderData.class.getName();

    /** Stores a reference to the component manager. */
    private ComponentManager componentManager;

    /** Stores a reference to the field handler factory. */
    private FieldHandlerFactory fieldHandlerFactory;

    /** The container selector used during the builder operation. */
    private ContainerSelector containerSelector;

    /** A stack for managing the form-relevant information. */
    private final Stack<FormContextData> formContextStack;

    /** A stack for storing the forms of the open form contexts. */
    private final Stack<Form> formStack;

    /** Stores a map with the so far requested widget handlers. */
    private final Map<Object, WidgetHandler> widgetHandlers;

    /** A list for the context listeners registered at this object. */
    private final List<FormContextListener> contextListeners;

    /**
     * Stores a reference to the main form object that is set up during the
     * builder operation.
     */
    private Form form;

    /** Stores the bean context. */
    private BeanContext beanContext;

    /** Stores the default resource group. */
    private Object defaultResourceGroup;

    /** Stores the root container for this form building operation. */
    private Object rootContainer;

    /** A map with the known event managers. */
    private final Map<Form, FormEventManager> formEventManagers;

    /** Stores the current event manager object. */
    private FormEventManager eventManager;

    /** Stores a reference to the platform event manager. */
    private PlatformEventManager platformEventManager;

    /** Stores the tool tip manager. */
    private ToolTipManager toolTipManager;

    /** Stores the name of the current builder. */
    private String builderName;

    /** Stores the name of the current default button. */
    private String defaultButtonName;

    /** A counter that determines whether callbacks are disabled. */
    private int callBacksEnabledState;

    /**
     * Creates a new instance of {@code ComponentBuilderData}.
     */
    public ComponentBuilderData()
    {
        formContextStack = new Stack<FormContextData>();
        formStack = new Stack<Form>();
        formEventManagers = new HashMap<Form, FormEventManager>();
        widgetHandlers = new HashMap<Object, WidgetHandler>();
        contextListeners = new CopyOnWriteArrayList<FormContextListener>();
        containerSelector = new DefaultContainerSelector();
    }

    /**
     * Initializes the main form to be maintained by the {@code ComponentBuilderData}
     * object. This method must be called after the construction of this object
     * to explicitly initialize the {@code Form} object.
     * @param tctx the {@code TransformerContext}
     * @param strategy the {@code BindingStrategy} for the form
     * @throws IllegalArgumentException if a required parameter is <b>null</b>
     */
    public void initializeForm(TransformerContext tctx, BindingStrategy strategy)
    {
        form = new Form(tctx, strategy);
        // Open main form context
        pushFormContext(form);
    }

    /**
     * Returns the name of the current builder.
     *
     * @return the builder name
     */
    public String getBuilderName()
    {
        return builderName;
    }

    /**
     * Sets the name of the current builder.
     *
     * @param builderName the builder name
     */
    public void setBuilderName(String builderName)
    {
        this.builderName = builderName;
    }

    /**
     * Returns the root container.
     *
     * @return the root container
     */
    public Object getRootContainer()
    {
        return rootContainer;
    }

    /**
     * Sets the root container. All component tags that are not nested inside a
     * container tag will add their created objects to this container object.
     *
     * @param rootContainer the root container to use
     */
    public void setRootContainer(Object rootContainer)
    {
        this.rootContainer = rootContainer;
    }

    /**
     * Returns the component manager.
     *
     * @return the component manager
     */
    public ComponentManager getComponentManager()
    {
        return componentManager;
    }

    /**
     * Sets the component manager. This object will be used to create and
     * manipulate GUI components.
     *
     * @param componentManager the component manager to use
     */
    public void setComponentManager(ComponentManager componentManager)
    {
        this.componentManager = componentManager;
    }

    /**
     * Returns the field handler factory.
     *
     * @return the field handler factory
     */
    public FieldHandlerFactory getFieldHandlerFactory()
    {
        return fieldHandlerFactory;
    }

    /**
     * Sets the field handler factory. This object is used by input component
     * tags for creating the field handlers that are then passed to the internal
     * form object.
     *
     * @param fieldHandlerFactory the handler factory
     */
    public void setFieldHandlerFactory(FieldHandlerFactory fieldHandlerFactory)
    {
        this.fieldHandlerFactory = fieldHandlerFactory;
    }

    /**
     * Returns the {@code ContainerSelector} used by this object.
     *
     * @return the {@code ContainerSelector}
     * @since 1.3
     */
    public ContainerSelector getContainerSelector()
    {
        return containerSelector;
    }

    /**
     * Sets the {@code ContainerSelector} to be used by this object.
     *
     * @param containerSelector the {@code ContainerSelector}
     * @since 1.3
     */
    public void setContainerSelector(ContainerSelector containerSelector)
    {
        this.containerSelector = containerSelector;
    }

    /**
     * Returns the default resource group.
     *
     * @return the default resource group
     */
    public Object getDefaultResourceGroup()
    {
        return defaultResourceGroup;
    }

    /**
     * Sets the default resource group. This group will be used if no specific
     * group is specified in a resource request.
     *
     * @param defaultResourceGroup the default resource group
     */
    public void setDefaultResourceGroup(Object defaultResourceGroup)
    {
        this.defaultResourceGroup = defaultResourceGroup;
    }

    /**
     * Returns the {@code Form} object. This object is created during the builder
     * process. It contains all fields that have been created so far. <strong>Note:</strong>
     * Before calling this method {@code initializeForm()} must have been invoked;
     * otherwise an exception is thrown.
     *
     * @return the {@code Form} object
     * @throws IllegalStateException if the form has not yet been initialized
     */
    public Form getForm()
    {
        if (form == null)
        {
            throw new IllegalStateException("Form has not been initialized!"
                    + "Call initializeForm() first.");
        }
        return form;
    }

    /**
     * Returns the transformer context.
     *
     * @return the transformer context
     */
    public TransformerContext getTransformerContext()
    {
        return getForm().getTransformerContext();
    }

    /**
     * Returns the {@code FormEventManager} used by this builder
     * operation. This object can be used to register event handlers at
     * components created during the building process.
     *
     * @return the event manager object
     */
    public FormEventManager getEventManager()
    {
        if (eventManager == null)
        {
            assert getContextForm() != null : "No context form set!";
            eventManager = getEventManagerForForm(getContextForm());
        }
        return eventManager;
    }

    /**
     * Allows to set an event manager. All event handling logic, e.g.
     * registering event listeners, will be done by this object. Normally it is
     * not necessary to set a specific event manager; there is a default
     * instance. This method is intended for complex components that need to
     * hook into the event logic.
     *
     * @param evMan the new event manager to be set (can be <b>null</b>, then
     * the default event manager for the current form will be set)
     */
    public void setEventManager(FormEventManager evMan)
    {
        eventManager = evMan;
    }

    /**
     * Returns the {@code ToolTipManager} associated with this object. If no
     * specific {@code ToolTipManager} has been set, a default instance is
     * created and returned.
     *
     * @return the {@code ToolTipManager}
     */
    public ToolTipManager getToolTipManager()
    {
        if (toolTipManager == null)
        {
            toolTipManager = createToolTipManager();
        }

        return toolTipManager;
    }

    /**
     * Sets the {@code ToolTipManager} for this object. This {@code
     * ToolTipManager} is then used for manipulating tool tips for components.
     * Normally it is not necessary to set a specific tool tip manager. If none
     * is set, a default instance is created. This method can be used to inject
     * a custom tool tip manager.
     *
     * @param toolTipManager the {@code ToolTipManager} to be used
     */
    public void setToolTipManager(ToolTipManager toolTipManager)
    {
        this.toolTipManager = toolTipManager;
    }

    /**
     * Returns a reference to the current component store. This store will be
     * used for searching and storing components.
     *
     * @return the currently used {@code ComponentStore}
     */
    public ComponentStore getComponentStore()
    {
        return fetchFormContextData().componentStore;
    }

    /**
     * Adds a new component store to this object that will replace the current
     * store. All newly created components will be added to this store. It will
     * be active until {@code popComponentStore()} is called, then the
     * replaced component store will become the current store again. The purpose
     * of this method is to allow complex tags to install their own store so
     * that all components created in their context are put into this store.
     * Thus it is possible to create sub forms or things like that. Call backs
     * that are registered using the {@code addCallBack()} method will
     * also be affected: they are always created in the context of the current
     * component store and executed when {@code popComponentStore()} is
     * invoked.
     *
     * @param store the new store (must not be <b>null</b>)
     * @return the old active store; this store is replaced by the new one
     * @throws IllegalArgumentException if the passed in store is <b>null</b>
     * @see #popComponentStore()
     */
    public ComponentStore pushComponentStore(ComponentStore store)
    {
        if (store == null)
        {
            throw new IllegalArgumentException(
                    "Component store must not be null!");
        }
        ComponentStore result = formContextStack.isEmpty() ? null
                : getComponentStore();
        FormContextData fcd = new FormContextData(store);
        formContextStack.push(fcd);
        return result;
    }

    /**
     * Removes a component store from this object. This method is the counter
     * part of {@code pushComponentStore()}. It removes the last pushed
     * component store, making the store before to the current store again. If
     * any call backs have been registered for the popped component store, they
     * will now be invoked.
     *
     * @return the store that was removed
     * @throws FormBuilderException if an error occurs when invoking call backs
     * @throws java.util.EmptyStackException if there are no more stores to pop
     */
    public ComponentStore popComponentStore() throws FormBuilderException
    {
        invokeCallBacks();
        FormContextData fcd = formContextStack.pop();
        return fcd.getComponentStore();
    }

    /**
     * Returns the event manager for the specified form. An event manager is
     * always associated with a {@code Form} object; it uses the form's
     * {@link ComponentStore} for retrieving the components, for
     * which event listeners are to be registered. With this method an event
     * manager for a given form can be requested. If no such event manager
     * exists, it will be created now. Per default there will be a single event
     * manager for the main form constructed during the build process. However
     * if complex components are involved that construct sub forms (which need
     * their own event handling logic), it may be necessary to have a different
     * event manager.
     *
     * @param f the form the event manager is associated with
     * @return the event manager for this form
     */
    public FormEventManager getEventManagerForForm(Form f)
    {
        FormEventManager evMan = formEventManagers.get(f);
        if (evMan == null)
        {
            evMan = createEventManager();
            evMan.setComponentStore(f.getComponentStore());
            formEventManagers.put(f, evMan);
        }
        return evMan;
    }

    /**
     * Adds a {@code FormContextListener} object to this data object. The
     * listener receives notifications when a new form context is created or the
     * current context is closed. This method can be called from an arbitrary
     * thread
     *
     * @param listener the listener to be registered (must not be <b>null</b>)
     * @throws IllegalArgumentException if the listener is <b>null</b>
     * @since 1.3
     */
    public void addFormContextListener(FormContextListener listener)
    {
        if (listener == null)
        {
            throw new IllegalArgumentException("Listener must not be null!");
        }
        contextListeners.add(listener);
    }

    /**
     * Removes the specified {@code FormContextListener} from this object.
     *
     * @param listener the listener to be removed
     * @since 1.3
     */
    public void removeFormContextListener(FormContextListener listener)
    {
        contextListeners.remove(listener);
    }

    /**
     * Installs a new form context for the specified form. Works like the method
     * with the same name, but passes <b>null</b> for the source.
     *
     * @param f the sub form of the new form context (must not be <b>null</b>)
     * @throws IllegalArgumentException if the form instance is <b>null</b>
     * @see #pushComponentStore(ComponentStore)
     * @see #getEventManagerForForm(Form)
     */
    public void pushFormContext(Form f)
    {
        pushFormContext(f, null);
    }

    /**
     * Installs a new form context for the specified form and passes information
     * about the responsible source. This method can be called by complex
     * components that create their own (sub) form instances. It has the
     * following effect:
     * <ul>
     * <li>{@code pushComponentStore()} is called with the component store of
     * the specified form. So newly created components will be added to this
     * store.</li>
     * <li>The event manager for this form is obtained using
     * {@code getEventManagerForForm()} and made to the active event manager.
     * This ensures that event listener registration logic for the sub form is
     * handled by the appropriate event manager.</li>
     * <li>Registered {@code FormContextListener} objects are notified about the
     * newly created context.</li>
     * </ul>
     *
     * @param form the sub form of the new form context (must not be
     *        <b>null</b>)
     * @param source the source object responsible for the form context
     * @throws IllegalArgumentException if the form instance is <b>null</b>
     * @see #pushComponentStore(ComponentStore)
     * @see #getEventManagerForForm(Form)
     * @since 1.3
     */
    public void pushFormContext(Form form, Object source)
    {
        if (form == null)
        {
            throw new IllegalArgumentException(
                    "Form for context must not be null!");
        }
        formStack.push(form);
        pushComponentStore(form.getComponentStore());
        setEventManager(null);
        fireFormContextCreated(form, source);
    }

    /**
     * Removes the outer most form context. Works like the method with the same
     * name, but no information about a source is provided.
     *
     * @return the {@code Form} instance of the removed form context
     * @throws FormBuilderException if an error occurs when closing the current
     *         form context
     * @throws IllegalStateException if {@code pushFormContext()} has not been
     *         called before (and the context to be removed is the root context)
     */
    public Form popFormContext() throws FormBuilderException
    {
        return popFormContext(null);
    }

    /**
     * Removes the outer most form context passing in information about the
     * responsible source. This method is the counter part of
     * {@code pushFormContext()}. It makes the previous form to the active form
     * again (and ensures that the correct component store and event manager are
     * selected. This method must be called after processing of a sub form has
     * completed. <em>Note:</em> This method calls {@code popComponentStore()}
     * to make the component store of the previous form to the current one.
     * Clients must be aware that the calls to the push and pop methods must be
     * symmetric and correctly nested, otherwise the association between the
     * current forms and their component stores may get lost!
     *
     * @param source the source object responsible for the form context
     * @return the {@code Form} instance of the removed form context
     * @throws FormBuilderException if an error occurs when closing the current
     *         form context
     * @throws IllegalStateException if {@code pushFormContext()} has not been
     *         called before (and the context to be removed is the root context)
     * @see #pushFormContext(Form, Object)
     * @since 1.3
     */
    public Form popFormContext(Object source) throws FormBuilderException
    {
        if (formStack.size() <= 1)
        {
            // only root context present?
            throw new IllegalStateException("Root context must not be closed!");
        }

        Form result = formStack.pop();
        popComponentStore();
        setEventManager(null);
        fireFormContextClosed(result, source);
        return result;
    }

    /**
     * Returns the form of the current form context. While the
     * {@code getForm()} method always returns the main form of this
     * builder operation, this method takes the current form context into
     * account, i.e. if {@code pushFormContext()} has been called before,
     * the form passed to this method will be returned.
     *
     * @return the form of the current form context
     * @see #pushFormContext(Form)
     */
    public Form getContextForm()
    {
        assert !formStack.isEmpty() : "No form context open!";
        return formStack.peek();
    }

    /**
     * Stores the specified component in the current {@code ComponentStore}.
     * From there it can be accessed e.g. if another component defines a
     * reference to it.
     *
     * @param name the name of this component
     * @param component the component itself
     */
    public void storeComponent(String name, Object component)
    {
        getComponentStore().add(name, component);
    }

    /**
     * Returns the component with the given name from the currently active
     * {@code ComponentStore}. If no such component can be found, the
     * method tries to find a component handler with this name and extract the
     * component object from this handler. If this fails, too, <b>null</b> is
     * returned.
     *
     * @param name the name of the desired component
     * @return the component
     */
    public Object getComponent(String name)
    {
        Object component = getComponentStore().findComponent(name);
        if (component == null)
        {
            ComponentHandler<?> handler = getComponentHandler(name);
            return (handler != null) ? handler.getComponent() : null;
        }
        else
        {
            return component;
        }
    }

    /**
     * Stores the given component handler in the current
     * {@code ComponentStore}. From there it can later be accessed,
     * which is useful if it is referenced by other tags.
     *
     * @param name the name of this component handler
     * @param handler the handler itself
     */
    public void storeComponentHandler(String name, ComponentHandler<?> handler)
    {
        getComponentStore().addComponentHandler(name, handler);
    }

    /**
     * Returns the component handler with the specified name from the current
     * {@code ComponentStore}. If no such handler can be found, return
     * value is <b>null </b>.
     *
     * @param name the name of the desired handler
     * @return the handler
     */
    public ComponentHandler<?> getComponentHandler(String name)
    {
        return getComponentStore().findComponentHandler(name);
    }

    /**
     * Stores the specified field handler. This field will be added to the
     * internally maintained form object. The component that is associated with
     * the field handler will also be accessible by the
     * {@link #getComponent(String)} and
     * {@link #getComponentHandler(String)} methods.
     *
     * @param name the name of the field
     * @param fld the field handler
     */
    public void storeFieldHandler(String name, FieldHandler fld)
    {
        getComponentStore().addFieldHandler(name, fld);
        storeComponentHandler(name, fld.getComponentHandler());
    }

    /**
     * Returns the field handler with the specified name from the current
     * {@code ComponentStore} object. If no handler exists with this
     * name, <b>null</b> is returned.
     *
     * @param name the name of the desired field handler
     * @return the field handler with this name
     */
    public FieldHandler getFieldHandler(String name)
    {
        return getComponentStore().findFieldHandler(name);
    }

    /**
     * Returns a {@code WidgetHandler} for accessing the component with
     * the given name. A component with this name is searched in the current
     * {@code ComponentStore} object. If it cannot be found, <b>null</b>
     * will be returned. Otherwise the current {@code ComponentManager}
     * is asked to create a {@code WidgetHandler} object for this
     * component. A once created {@code WidgetHandler} object will be
     * cached, so that it can be directly returned if it is queried for the
     * second time.
     *
     * @param name the name of the component
     * @return a {@code WidgetHandler} object wrapping this component
     */
    public WidgetHandler getWidgetHandler(String name)
    {
        return getWidgetHandlerForComponent(getComponent(name));
    }

    /**
     * Returns a {@code WidgetHandler} object for the specified
     * component. This method checks whether already a
     * {@code WidgetHandler} for the passed in component has been created
     * (by looking it up in the internal cache). If this is the case, it can be
     * directly returned. Otherwise the current {@code ComponentManager}
     * is asked to create a new {@code WidgetHandler} instance now. If
     * the passed in component is <b>null</b>, <b>null</b> will be returned.
     *
     * @param component the component, for which a {@code WidgetHandler}
     * is to be obtained
     * @return the {@code WidgetHandler} for this component
     */
    public WidgetHandler getWidgetHandlerForComponent(Object component)
    {
        if (component == null)
        {
            return null;
        }

        WidgetHandler wh = widgetHandlers.get(component);
        if (wh == null)
        {
            wh = getComponentManager().getWidgetHandlerFor(component);
            widgetHandlers.put(component, wh);
        }
        return wh;
    }

    /**
     * Adds the specified component to the root container. This method is called
     * by component tags that are not nested inside container tags.
     *
     * @param comp the component to add
     * @param constraints the constraints for this component
     * @throws FormBuilderRuntimeException if no root container was set
     */
    public void addComponent(Object comp, Object constraints)
            throws FormBuilderRuntimeException
    {
        if (getRootContainer() == null)
        {
            throw new FormBuilderRuntimeException("No root container was set!");
        }
        fetchComponentHandler().addContainerComponent(getRootContainer(), comp,
                constraints);
    }

    /**
     * Sets the layout for the root container. This method is called by layout
     * tags that are not nested inside container tags.
     *
     * @param layout the layout object to set
     */
    public void setLayout(Object layout)
    {
        if (getRootContainer() == null)
        {
            throw new FormBuilderRuntimeException("No root container was set!");
        }
        fetchComponentHandler().setContainerLayout(getRootContainer(), layout);
    }

    /**
     * Returns the concrete container component. In this case this is the root
     * container.
     *
     * @return the container component
     */
    public Object getContainer()
    {
        return getRootContainer();
    }

    /**
     * Disables the call back mechanism. Newly added callbacks are ignored and
     * will not be executed by {@link #invokeCallBacks()}. Calls to this method
     * can be nested. A corresponding number of {@link #enableCallBacks()} is
     * necessary in order to enable callbacks again.
     *
     * @since 1.3
     */
    public void disableCallBacks()
    {
        callBacksEnabledState--;
    }

    /**
     * Enables the call back mechanism. This is the counter part of
     * {@link #disableCallBacks()}.
     *
     * @since 1.3
     */
    public void enableCallBacks()
    {
        callBacksEnabledState++;
    }

    /**
     * Returns a flag whether the call back mechanism is currently enabled. If
     * this method returns <b>false</b>, all callbacks added to this object are
     * ignored.
     *
     * @return <b>true</b> if callbacks are enabled, <b>false</b> otherwise
     * @since 1.3
     */
    public boolean isCallBacksEnabled()
    {
        return callBacksEnabledState >= 0;
    }

    /**
     * Registers the specified call back at this builder data object. It will be
     * invoked after the building operation is complete for the current form
     * context.
     *
     * @param callBack the call back object
     * @param param a parameter object; this object is passed to the call back
     *        when it is invoked
     */
    public void addCallBack(ComponentBuilderCallBack callBack, Object param)
    {
        if (isCallBacksEnabled())
        {
            fetchFormContextData().getCallBacks().add(
                    new CallBackData(callBack, param));
        }
    }

    /**
     * Invokes all call backs that are registered at this object for the current
     * form context.
     *
     * @throws FormBuilderException if an exception is thrown by one of the call
     * backs
     */
    public void invokeCallBacks() throws FormBuilderException
    {
        for (CallBackData cbd : fetchFormContextData().getCallBacks())
        {
            cbd.invokeCallBack(this);
        }
    }

    /**
     * Returns a set with the names of all contained bean. This implementation
     * returns the names of all stored components. If these components are
     * associated with handlers, the correspondingly prefixed names are also
     * contained in the set.
     *
     * @param names the set in which to store the names of the managed beans
     */
    public void beanNames(Set<String> names)
    {
        ComponentStore cStore = getComponentStore();
        appendNames(names, cStore.getComponentNames(), null);
        appendNames(names, cStore.getComponentNames(), KEY_WIDGETHANDLER_PREFIX);
        appendNames(names, cStore.getComponentHandlerNames(),
                KEY_COMPHANDLER_PREFIX);
        appendNames(names, cStore.getFieldHandlerNames(),
                KEY_FIELDHANDLER_PREFIX);
        names.add(KEY_FORM);
        names.add(KEY_COMPONENT_BUILDER_DATA);
    }

    /**
     * Returns the bean with the given name. This implementation supports the
     * names of the stored components. If for a component a
     * {@code ComponentHandler}, a {@code FieldHandler}, or a
     * {@code WidgetHandler} is available, the correspondingly prefixed
     * name is also supported. In addition the other reserved keys as described
     * in the header comment can be used.
     *
     * @param name the name of the desired bean provider
     * @return the bean with this name or <b>null</b>
     */
    public Object getBean(String name)
    {
        Object bean = null;

        if (KEY_COMPONENT_BUILDER_DATA.equals(name))
        {
            bean = this;
        }
        else if (KEY_FORM.equals(name))
        {
            bean = getForm();
        }
        else if (KEY_CURRENT_CONTEXT.equals(name))
        {
            bean = getBeanContext();
        }

        else
        {
            if (name != null)
            {
                int pos = name.indexOf(PREFIX_SEPARATOR);
                if (pos > 0)
                {
                    String prefix = name.substring(0, pos);
                    String cname = name.substring(pos + 1);
                    bean = getPrefixedComponent(prefix, cname);
                }
            }

            if (bean == null)
            {
                // always check for a component with this name
                bean = getComponent(name);
            }
        }

        return bean;
    }

    /**
     * Initializes the specified bean store object. This method is called by the
     * builder when the {@code BeanContext} used during the builder
     * operation is constructed. This implementation will add the static beans
     * to the given store and register this object as
     * {@code BeanContributor}.
     *
     * @param store the store to be initialized
     */
    public void initBeanStore(SimpleBeanStoreImpl store)
    {
        store.addBean(KEY_COMPONENT_BUILDER_DATA, this);
        store.addBeanContributor(this);
    }

    /**
     * Returns the {@code BeanContext} managed by this instance. If not
     * context has been set, a default context will be returned, which allows
     * access only to the beans defined in this object (i.e. the components
     * created during the builder operation and their handlers). Typically the
     * builder will create a context in the initialization phase of a builder
     * operation.
     *
     * @return the context maintained by this instance
     */
    public BeanContext getBeanContext()
    {
        if (beanContext == null)
        {
            SimpleBeanStoreImpl store = new SimpleBeanStoreImpl();
            initBeanStore(store);
            // create a default context
            beanContext = new DefaultBeanContext(store);
        }
        return beanContext;
    }

    /**
     * Allows to set a specific {@code BeanContext}. This is not necessary
     * normally, because the bean context is correctly set up automatically
     * taking account the appropriate hierarchy of contexts and bean stores.
     *
     * @param ctx the new bean context to be used
     */
    public void setBeanContext(BeanContext ctx)
    {
        beanContext = ctx;
    }

    /**
     * Returns the name of the default button. This can be <b>null</b> if no
     * default button has been set.
     *
     * @return the name of the default button
     */
    public String getDefaultButtonName()
    {
        return defaultButtonName;
    }

    /**
     * Sets the name of the default button. This method is called by a button
     * tag if the button is marked as default button of the current window.
     * Window tags can evaluate this property to decide whether some action is
     * necessary to actually make this button the window's default button.
     *
     * @param defaultButtonName the name of the default button; can be
     *        <b>null</b> to clear the default button
     */
    public void setDefaultButtonName(String defaultButtonName)
    {
        this.defaultButtonName = defaultButtonName;
    }

    /**
     * Stores this instance in the specified context. From there it can be
     * retrieved using the {@code get()} method.
     *
     * @param ctx the Jelly context (must not be <b>null</b>)
     * @throws IllegalArgumentException if the context is <b>null</b>
     */
    public void put(JellyContext ctx)
    {
        if (ctx == null)
        {
            throw new IllegalArgumentException("Context must not be null!");
        }
        ctx.setVariable(CTX_NAME, this);
    }

    /**
     * Returns the instance of this class stored in the specified Jelly context.
     * If no such instance can be found, <b>null</b> is returned.
     *
     * @param ctx the Jelly context
     * @return the instance of this class stored in this context
     */
    public static ComponentBuilderData get(JellyContext ctx)
    {
        return (ctx != null) ? (ComponentBuilderData) ctx
                .findVariable(CTX_NAME) : null;
    }

    /**
     * Returns the associated component manager. If this object is not set, a
     * runtime exception will be thrown.
     *
     * @return the component manager
     * @throws FormBuilderRuntimeException if no component manager was set
     */
    protected ComponentManager fetchComponentHandler()
            throws FormBuilderRuntimeException
    {
        if (getComponentManager() == null)
        {
            throw new FormBuilderRuntimeException("No component manager set!");
        }
        return getComponentManager();
    }

    /**
     * Creates the event manager object. This method is called when the event
     * manager is accessed for the first time. It creates a new instance of
     * {@code FormEventManager} and initializes it with the platform
     * specific event manager obtained from the component manager.
     *
     * @return the new event manager
     * @throws FormBuilderRuntimeException if no component manager was set
     */
    protected FormEventManager createEventManager()
    {
        if (platformEventManager == null)
        {
            platformEventManager = createPlatformEventManager();
        }

        FormEventManager evMan = new FormEventManager(platformEventManager);
        return evMan;
    }

    /**
     * Creates the platform specific event manager. This method is called once
     * on first access to the platform event manager. This implementation
     * obtains the event manager from the component handler.
     *
     * @return the platform specific event manager
     * @throws FormBuilderRuntimeException if no component manager was set
     */
    protected PlatformEventManager createPlatformEventManager()
    {
        return fetchComponentHandler().createEventManager();
    }

    /**
     * Creates the {@code ToolTipManager}. This method is called when the
     * {@code ToolTipManager} is accessed for the first time, but no specific
     * instance has been set. This implementation creates a default tool tip
     * manager object.
     *
     * @return the new {@code ToolTipManager} instance
     */
    protected ToolTipManager createToolTipManager()
    {
        return new DefaultToolTipManager(this);
    }

    /**
     * Notifies registered listeners about a newly created form context.
     *
     * @param form the sub form of the new form context
     * @param source the source object responsible for the form context
     */
    private void fireFormContextCreated(Form form, Object source)
    {
        for (FormContextListener listener : contextListeners)
        {
            listener.formContextCreated(form, source);
        }
    }

    /**
     * Notifies registered listeners about a form context that has been closed.
     *
     * @param form the sub form of the closed form context
     * @param source the source object responsible for the form context
     */
    private void fireFormContextClosed(Form form, Object source)
    {
        for (FormContextListener listener : contextListeners)
        {
            listener.formContextClosed(form, source);
        }
    }

    /**
     * Returns a reference to the current form component data object that holds
     * information about the currently constructed form.
     *
     * @return the current form component data object
     */
    private FormContextData fetchFormContextData()
    {
        return formContextStack.peek();
    }

    /**
     * Tries to resolve the specified prefixed name.
     *
     * @param prefix the prefix
     * @param name the name
     * @return the corresponding component
     */
    private Object getPrefixedComponent(String prefix, String name)
    {
        Object comp = null;

        if (KEY_COMPHANDLER_PREFIX.equals(prefix))
        {
            comp = getComponentHandler(name);
        }
        else if (KEY_FIELDHANDLER_PREFIX.equals(prefix))
        {
            comp = getFieldHandler(name);
        }
        else if (KEY_WIDGETHANDLER_PREFIX.equals(prefix))
        {
            comp = getWidgetHandler(name);
        }

        return comp;
    }

    /**
     * Processes the names for elements with a given prefix and adds them to the
     * target name set.
     *
     * @param target the target name set
     * @param source the source name set
     * @param prefix the prefix to be used
     */
    private static void appendNames(Set<String> target, Set<String> source,
            String prefix)
    {
        for (String s : source)
        {
            target.add(prefixedName(prefix, s));
        }
    }

    /**
     * Creates a prefixed name to be used for the bean store implementation.
     *
     * @param prefix the prefix (can be <b>null</b>)
     * @param name the name
     * @return the prefixed name
     */
    private static String prefixedName(String prefix, String name)
    {
        if (prefix == null)
        {
            return name;
        }

        StringBuilder buf = new StringBuilder(prefix.length() + name.length()
                + 1);
        buf.append(prefix).append(PREFIX_SEPARATOR).append(name);
        return buf.toString();
    }

    /**
     * A simple data class for storing information about registered call backs.
     */
    private static class CallBackData
    {
        /** Stores the call back. */
        private ComponentBuilderCallBack callBack;

        /** Stores the parameter for the call back. */
        private Object param;

        /**
         * Creates a new instance of {@code CallBackData} and initializes
         * it.
         *
         * @param cb the call back
         * @param p the parameter for the call back
         */
        public CallBackData(ComponentBuilderCallBack cb, Object p)
        {
            callBack = cb;
            param = p;
        }

        /**
         * Invokes the stored call back.
         *
         * @param data the builder data object
         * @throws FormBuilderException if the call back throws an exception
         */
        public void invokeCallBack(ComponentBuilderData data)
                throws FormBuilderException
        {
            callBack.callBack(data, param);
        }
    }

    /**
     * A simple data class that stores all information needed for the currently
     * defined form. Typically only a single form is constructed during a
     * builder process. But there may be complex components that create sub
     * forms. In this case the relevant information about the current form is
     * packed in an instance of this class and pushed on a stack. After the
     * complex component and its sub form are completely processed the old state
     * is restored by popping the information back from the stack.
     */
    private static class FormContextData
    {
        /** Stores a reference to the component store associated with the form. */
        private final ComponentStore componentStore;

        /** A list for the registered call backs. */
        private final Collection<CallBackData> callBacks;

        /**
         * Creates a new instance of {@code FormComponentData} and sets
         * the component store. An empty collection for the call backs will also
         * be created.
         *
         * @param store the associated component store
         */
        public FormContextData(ComponentStore store)
        {
            componentStore = store;
            callBacks = new LinkedList<CallBackData>();
        }

        /**
         * Returns the {@code ComponentStore}.
         *
         * @return the {@code ComponentStore}
         */
        public ComponentStore getComponentStore()
        {
            return componentStore;
        }

        /**
         * Returns the collection with callback objects.
         *
         * @return the callBacks the collection with callback objects
         */
        public Collection<CallBackData> getCallBacks()
        {
            return callBacks;
        }
    }
}

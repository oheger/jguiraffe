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
package net.sf.jguiraffe.gui.app;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanCreationEvent;
import net.sf.jguiraffe.di.BeanCreationListener;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.MutableBeanStore;
import net.sf.jguiraffe.gui.builder.Builder;
import net.sf.jguiraffe.gui.builder.action.ActionStore;
import net.sf.jguiraffe.gui.builder.utils.GUISynchronizer;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.forms.BindingStrategy;
import net.sf.jguiraffe.resources.Message;
import net.sf.jguiraffe.resources.ResourceManager;
import net.sf.jguiraffe.transform.ValidationMessageHandler;

import org.apache.commons.configuration.Configuration;

/**
 * <p>
 * A default implementation of the <code>ApplicationContext</code> interface.
 * </p>
 * <p>
 * This class is used by the <code>Application</code> class to store global
 * information that is needed by different components of an application. Because
 * an <code>ApplicationContext</code> implementation also implements the
 * <code>TransformerContext</code> interface instances can also be passed to
 * transformer objects.
 * </p>
 * <p>
 * Note that this implementation is not thread-safe. The intended use case is
 * that an instance is created at application startup and initialized with the
 * central helper objects managed by the instance. Later these objects should
 * not be changed any more. If used in this way, the instance can be used from
 * both the event dispatch thread and from commands executing in background
 * threads.
 * </p>
 *
 * @see net.sf.jguiraffe.transform.TransformerContext
 * @author Oliver Heger
 * @version $Id: ApplicationContextImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ApplicationContextImpl implements ApplicationContext
{
    /** Stores the actual locale. */
    private volatile Locale locale;

    /** Stores a reference to the resource manager to use. */
    private ResourceManager resourceManager;

    /** Stores a reference to the global configuration object. */
    private Configuration configuration;

    /** Stores the global bean context. */
    private BeanContext beanContext;

    /** Stores the validation message handler.*/
    private ValidationMessageHandler validationMessageHandler;

    /** Stores the object for displaying messages. */
    private MessageOutput msgOut;

    /** Stores the application's main window. */
    private Window mainWindow;

    /** Stores the application's main action store. */
    private ActionStore actionStore;

    /** A map for maintaining properties. */
    private final Map<String, Object> properties;

    /** The map for storing typed properties. */
    private final Map<Class<?>, Object> typedProperties;

    /**
     * Creates a new instance of <code>ApplicationContextImpl</code>. The
     * instance is not yet initialized.
     */
    public ApplicationContextImpl()
    {
        properties = new HashMap<String, Object>();
        typedProperties = new ConcurrentHashMap<Class<?>, Object>();
        actionStore = new ActionStore();
    }

    /**
     * Creates a new instance of <code>ApplicationContextImpl</code> and sets
     * the current locale.
     *
     * @param locale the <code>Locale</code>
     */
    public ApplicationContextImpl(Locale locale)
    {
        this();
        setLocale(locale);
    }

    /**
     * Creates a new instance of <code>ApplicationContextImpl</code> and sets
     * the current locale and the resource manager to use.
     *
     * @param locale the <code>Locale</code>
     * @param resMan the <code>ResourceManager</code> to use
     */
    public ApplicationContextImpl(Locale locale, ResourceManager resMan)
    {
        this(locale);
        setResourceManager(resMan);
    }

    /**
     * Returns the current locale. If no specific locale has been set yet, the
     * system's default locale will be used.
     *
     * @return the <code>Locale</code>
     */
    public Locale getLocale()
    {
        return (locale != null) ? locale : Locale.getDefault();
    }

    /**
     * Sets the current locale. Note: This implementation is thread-safe; the
     * newly set locale is visible for other threads, too.
     *
     * @param locale the new <code>Locale</code>
     */
    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    /**
     * Returns the actual resource manager.
     *
     * @return the <code>ResourceManager</code> for accessing resources
     */
    public ResourceManager getResourceManager()
    {
        return resourceManager;
    }

    /**
     * Sets the resource manager. Access to system resources will be handled by
     * this object.
     *
     * @param resourceManager the <code>ResourceManager</code>
     */
    public void setResourceManager(ResourceManager resourceManager)
    {
        this.resourceManager = resourceManager;
    }

    /**
     * Returns a map with properties maintained by this context.
     *
     * @return a map with properties
     */
    public Map<String, Object> properties()
    {
        return properties;
    }

    /**
     * Returns a reference to the global configuration.
     *
     * @return the configuration object
     */
    public Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * Sets the global configuration.
     *
     * @param configuration the configuration object
     */
    public void setConfiguration(Configuration configuration)
    {
        this.configuration = configuration;
    }

    /**
     * Returns the global bean context.
     *
     * @return the bean context
     */
    public BeanContext getBeanContext()
    {
        return beanContext;
    }

    /**
     * Sets the global bean context.
     *
     * @param beanContext the new bean context
     */
    public void setBeanContext(BeanContext beanContext)
    {
        this.beanContext = beanContext;
    }

    /**
     * Returns the {@code ClassLoaderProvider}. This implementation always returns
     * the {@code ClassLoaderProvider} managed by the current {@link BeanContext}.
     *
     * @return the {@code ClassLoaderProvider}
     */
    public ClassLoaderProvider getClassLoaderProvider()
    {
        BeanContext bctx = getBeanContext();
        return (bctx == null) ? null : bctx.getClassLoaderProvider();
    }

    /**
     * Sets the {@code ClassLoaderProvider}. This implementation does not store
     * the {@code ClassLoaderProvider} in a separate member field. Rather, it is
     * passed to the current {@link BeanContext}. To avoid inconsistencies when
     * loading classes there must be only a single {@code ClassLoaderProvider}
     * instance. If this method is called before a {@link BeanContext} was set,
     * an {@code IllegalStateException} exception is thrown.
     *
     * @param classLoaderProvider the new class loader provider
     * @throws IllegalStateException if no {@link BeanContext} has been set
     */
    public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider)
    {
        BeanContext bctx = getBeanContext();
        if (bctx == null)
        {
            throw new IllegalStateException(
                    "A BeanContext must be set before setting the CLP!");
        }
        bctx.setClassLoaderProvider(classLoaderProvider);
    }

    /**
     * Returns the <code>ValidationMessageHandler</code>.
     *
     * @return the <code>ValidationMessageHandler</code>
     */
    public ValidationMessageHandler getValidationMessageHandler()
    {
        return validationMessageHandler;
    }

    /**
     * Sets the <code>ValidationMessageHandler</code>. This object can be
     * queried by validators to obtain a specific validation message.
     *
     * @param validationMessageHandler the new
     *        <code>ValidationMessageHandler</code> (must not be <b>null</b>)
     * @throws IllegalArgumentException if the passed validation message handler
     *         is <b>null</b>
     */
    public void setValidationMessageHandler(
            ValidationMessageHandler validationMessageHandler)
    {
        if (validationMessageHandler == null)
        {
            throw new IllegalArgumentException(
                    "ValidationMessageHandler must not be null!");
        }
        this.validationMessageHandler = validationMessageHandler;
    }

    /**
     * Returns a reference to the object for displaying messages. This object
     * can be used to create message boxes.
     *
     * @return the object for displaying messages
     */
    public MessageOutput getMessageOutput()
    {
        return msgOut;
    }

    /**
     * Sets the message output object to be used by this application.
     *
     * @param msg the new <code>MessageOutput</code> object
     */
    public void setMessageOutput(MessageOutput msg)
    {
        msgOut = msg;
    }

    /**
     * Convenience method for looking up a resource specified as group and
     * resource ID.
     *
     * @param groupID the resource group ID
     * @param resID the resource ID
     * @return the found resource
     * @throws java.util.MissingResourceException if the resource cannot be found
     */
    public Object getResource(Object groupID, Object resID)
    {
        return getResourceManager().getResource(getLocale(), groupID, resID);
    }

    /**
     * Convenience method for looking up a resource that is specified as a
     * <code>Message</code> object.
     *
     * @param msg the resource definition (must not be <b>null</b>)
     * @return the found resource
     * @throws java.util.MissingResourceException if the resource cannot be found
     * @throws IllegalArgumentException if then message is undefined
     */
    public Object getResource(Message msg)
    {
        return getResourceText(msg);
    }

    /**
     * Convenience method for looking up a resource. The passed in object is
     * checked to be an instance of
     * <code>{@link net.sf.jguiraffe.resources.Message Message}</code>. If
     * this is the case, the resource group and the resource ID are extracted
     * from this object. Otherwise the passed in object is interpreted as
     * resource ID and the default resource group will be used.
     *
     * @param resID the resource ID
     * @return the found resource
     * @throws java.util.MissingResourceException if the resource cannot be found
     */
    public Object getResource(Object resID)
    {
        return (resID instanceof Message) ? getResource((Message) resID)
                : getResource(null, resID);
    }

    /**
     * Convenience method for looking up the text of a resource specified as
     * group and resource ID.
     *
     * @param groupID the resource group ID
     * @param resID the resource ID
     * @return the found resource text
     * @throws java.util.MissingResourceException if the resource cannot be found
     */
    public String getResourceText(Object groupID, Object resID)
    {
        return getResourceManager().getText(getLocale(), groupID, resID);
    }

    /**
     * Convenience method for looking up the text of a resource specified as a
     * <code>Message</code> object.
     *
     * @param msg defines the resource (must not be <b>null</b>)
     * @return the found resource
     * @throws java.util.MissingResourceException if the resource cannot be found
     * @throws IllegalArgumentException if the message is undefined
     */
    public String getResourceText(Message msg)
    {
        if (msg == null)
        {
            throw new IllegalArgumentException(
                    "Resource definition must not be null!");
        }
        return msg.resolve(getResourceManager(), getLocale());
    }

    /**
     * Convenience method for looking up the text of a specified resource. This
     * method works analogous to <code>getResourceText(Object)</code>,
     * especially the passed in object can be an instance of
     * {@link net.sf.jguiraffe.resources.Message Message}.
     *
     * @param resID defines the requested resource
     * @return the found resource
     * @throws java.util.MissingResourceException if the resource cannot be found
     */
    public String getResourceText(Object resID)
    {
        return (resID instanceof Message) ? getResourceText((Message) resID)
                : getResourceText(null, resID);
    }

    /**
     * A convenience method for displaying a message box. This method invokes
     * the application's associated <code>MessageOutput</code> object. Before
     * that the passed in resource IDs (which can be either resource IDs or
     * instances of the {@link net.sf.jguiraffe.resources.Message Message}
     * class) will be resolved.
     *
     * @param resMsg the resource defining the message to be displayed
     * @param resTitle the resource defining the message box's title (can be
     *        <b>null</b>)
     * @param msgType the message type (one of the <code>MESSAGE_XXX</code>
     *        constants of <code>MessageOutput</code>)
     * @param btnType the button type (one of the <code>BTN_XXX</code> constants
     *        of <code>MessageOutput</code>)
     * @return the message box's return value (one of the <code>RET_XXX</code>
     *         constants of <code>MessageOutput</code>)
     * @see net.sf.jguiraffe.gui.builder.utils.MessageOutput
     */
    public int messageBox(Object resMsg, Object resTitle, int msgType,
            int btnType)
    {
        String title = (resTitle != null) ? getResourceText(resTitle) : null;
        return getMessageOutput().show(getMainWindow(), getResource(resMsg),
                title, msgType, btnType);
    }

    /**
     * Returns the <code>GUISynchronizer</code>. This implementation obtains
     * the synchronizer from the bean context.
     *
     * @return the <code>GUISynchronizer</code>
     */
    public GUISynchronizer getGUISynchronizer()
    {
        return (GUISynchronizer) getBeanContext().getBean(
                Application.BEAN_GUI_SYNCHRONIZER);
    }

    /**
     * Returns the application's main window.
     *
     * @return the main window of this application
     */
    public Window getMainWindow()
    {
        return mainWindow;
    }

    /**
     * Allows to set the application's main window.
     *
     * @param mainWindow the new main window
     */
    public void setMainWindow(Window mainWindow)
    {
        this.mainWindow = mainWindow;
    }

    /**
     * Returns the application's <code>ActionStore</code>.
     *
     * @return the application's action store
     */
    public ActionStore getActionStore()
    {
        return actionStore;
    }

    /**
     * Sets the application's <code>ActionStore</code>. This object contains
     * the definitions for all top level actions known to the application.
     *
     * @param actionStore the new action store
     */
    public void setActionStore(ActionStore actionStore)
    {
        this.actionStore = actionStore;
    }

    /**
     * Returns a new <code>{@link Builder}</code> instance. This
     * implementation obtains the builder instance from the global bean context.
     *
     * @return the new <code>Builder</code> instance
     */
    public Builder newBuilder()
    {
        return (Builder) getBeanContext().getBean(Application.BEAN_BUILDER);
    }

    /**
     * Returns an initialized <code>ApplicationBuilderData</code> object that
     * can be used for calling the GUI builder. Most of the properties of the
     * returned object are already set to default values, so only specific
     * settings must be performed. The following properties have already been
     * initialized with information available directly in this object or from
     * the configuration:
     * <ul>
     * <li>the parent {@link BeanContext}</li>
     * <li>the default resource group</li>
     * <li>the menu icon flag</li>
     * <li>the tool bar text flag</li>
     * <li>the transformer context</li>
     * <li>the parent window</li>
     * <li>the action store: Here the following strategy is used: if the
     * <code>ActionStore</code> managed by this object is empty, it is
     * directly used. Otherwise a new <code>ActionStore</code> instance is
     * created with the managed <code>ActionStore</code> as parent. This has
     * the effect that the first builder script will populate the global action
     * store. Further scripts use their own store.</li>
     * <li>the <code>MessageOutput</code> object</li>
     * <li>the <code>CommandQueue</code></li>
     * <li>the {@code BindingStrategy}</li>
     * <li>a {@link BeanCreationListener} is set that can inject a reference to
     * the central {@code Application} object into beans implementing the
     * {@link ApplicationClient} interface</li>
     * </ul>
     *
     * @return an initialized GUI builder parameter object
     */
    public ApplicationBuilderData initBuilderData()
    {
        ApplicationBuilderData data = new ApplicationBuilderData();
        data.setDefaultResourceGroup(getResourceManager()
                .getDefaultResourceGroup());
        data.setMenuIcon(getConfiguration().getBoolean(
                Application.PROP_BUILDER_MENU_ICON, false));
        data.setToolbarText(getConfiguration().getBoolean(
                Application.PROP_BUILDER_TOOLBAR_TEXT, false));
        data.setTransformerContext(this);
        data.setParentWindow(getMainWindow());
        data.setParentContext(getBeanContext());
        data.setActionStore(actionStoreForBuilderData());
        data.setMessageOutput(getMessageOutput());
        Application app = Application.getInstance(getBeanContext());
        data.setCommandQueue(app.getCommandQueue());
        data.setBindingStrategy(fetchBindingStrategy());
        data.addBeanCreationListener(new ApplicationInjectBeanCreationListener(
                app));

        return data;
    }

    /**
     * Returns the value of the specified typed property or <b>null</b> if it
     * cannot be found.
     *
     * @param <T> the type of the property
     * @param propCls the property class
     * @return the value of this typed property
     */
    public <T> T getTypedProperty(Class<T> propCls)
    {
        @SuppressWarnings("unchecked")
        T result = (propCls == null) ? null : (T) typedProperties.get(propCls);
        return result;
    }

    /**
     * Sets the value of the given typed property. Note: This method is
     * thread-safe. It ensures proper synchronization so that the property is
     * visible to other threads, too.
     *
     * @param <T> the type of the property
     * @param propCls the property class (must not be <b>null</b>)
     * @param value the new value (<b>null</b> for clearing this property)
     * @throws IllegalArgumentException if the property class is <b>null</b>
     */
    public <T> void setTypedProperty(Class<T> propCls, T value)
    {
        if (propCls == null)
        {
            throw new IllegalArgumentException(
                    "Property class must not be null!");
        }

        if (value == null)
        {
            typedProperties.remove(propCls);
        }
        else
        {
            typedProperties.put(propCls, value);
        }
    }

    /**
     * Obtains the {@code BindingStrategy} for a builder operation. This method
     * is invoked by {@link #initBuilderData()} for populating the {@code
     * bindingStrategy} property of the {@code ApplicationBuilderData} object.
     * The default algorithm looks up the {@code BindingStrategy} from the
     * global {@code BeanContext}.
     *
     * @return the {@code BindingStrategy}
     */
    protected BindingStrategy fetchBindingStrategy()
    {
        return (BindingStrategy) getBeanContext().getBean(
                Application.BEAN_BINDING_STRATEGY);
    }

    /**
     * Makes the specified bean store to the given bean context's default store.
     * The current default store will become the new store's parent.
     *
     * @param context the bean context
     * @param store the new default bean store
     * @throws IllegalArgumentException if either the bean context or the bean
     * store is <b>null</b>
     */
    static void installBeanStore(BeanContext context, MutableBeanStore store)
    {
        if (context == null)
        {
            throw new IllegalArgumentException("BeanContext must not be null!");
        }
        if (store == null)
        {
            throw new IllegalArgumentException("BeanStore must not be null!");
        }

        store.setParent(context.getDefaultBeanStore());
        context.setDefaultBeanStore(store);
    }

    /**
     * Determines the action store to be used for the builder data. If
     * necessary, a new store will be created.
     *
     * @return the store to be used
     */
    private ActionStore actionStoreForBuilderData()
    {
        if (getActionStore() == null
                || !getActionStore().getActionNames().isEmpty())
        {
            return new ActionStore(getActionStore());
        }
        else
        {
            return getActionStore();
        }
    }

    /**
     * A specialized {@code BeanCreationListener} implementation that is able to
     * inject the central {@code Application} instance into bean implementing
     * the {@link ApplicationClient} interface. An instance of this class is
     * added to the {@code BuilderData} object created by {@code
     * initBuilderData()}. It ensures that the {@code Application} is
     * automatically injected into all beans that are interested in this
     * reference.
     */
    private static class ApplicationInjectBeanCreationListener implements
            BeanCreationListener
    {
        /** Stores a reference to the central {@code Application} object. */
        private final Application application;

        /**
         * Creates a new instance of {@code
         * ApplicationInjectBeanCreationListener} and initializes it with the
         * central {@code Application} reference.
         *
         * @param app the {@code Application}
         */
        public ApplicationInjectBeanCreationListener(Application app)
        {
            application = app;
        }

        /**
         * A bean was created. This implementation checks whether this bean
         * implements the {@code ApplicationClient} interface. If so, the
         * {@code Application} is injected.
         *
         * @param event the {@code BeanCreationEvent}
         */
        public void beanCreated(BeanCreationEvent event)
        {
            Application.setApplicationReference(event.getBean(), application);
        }
    }
}

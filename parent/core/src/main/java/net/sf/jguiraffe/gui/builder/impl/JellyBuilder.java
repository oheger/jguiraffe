/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanCreationListener;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.ConversionHelper;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.MutableBeanStore;
import net.sf.jguiraffe.di.impl.BeanContextWrapper;
import net.sf.jguiraffe.di.impl.CombinedBeanStore;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.di.impl.SimpleBeanStoreImpl;
import net.sf.jguiraffe.gui.builder.AutoReleaseListener;
import net.sf.jguiraffe.gui.builder.BeanBuilderResult;
import net.sf.jguiraffe.gui.builder.Builder;
import net.sf.jguiraffe.gui.builder.BuilderData;
import net.sf.jguiraffe.gui.builder.BuilderException;
import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionManager;
import net.sf.jguiraffe.gui.builder.action.tags.ActionBuilderTagLibrary;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.DefaultFieldHandlerFactory;
import net.sf.jguiraffe.gui.builder.components.FieldHandlerFactory;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.FormContextListener;
import net.sf.jguiraffe.gui.builder.components.tags.FormBuilderTagLibrary;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowManager;
import net.sf.jguiraffe.gui.builder.window.tags.WindowBuilderTagLibrary;
import net.sf.jguiraffe.locators.Locator;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.jelly.JellyContext;

/**
 * <p>
 * An implementation of the <code>Builder</code> interface that uses <a
 * href="http://commons.apache.org/jelly">Commons Jelly</a> for processing build
 * scripts and creating GUI components.
 * </p>
 * <p>
 * This builder implementation will interpret Jelly build scripts that contain
 * tags for creating bean definitions, form components, actions, and windows. To
 * fulfill its purpose the builder object must be initialized with a couple of
 * manager and factory interfaces, which will perform the real create operations
 * for elements that comprise the generated GUI. Usually a client won't have to
 * deal with these initialization stuff; instead this is handled by the
 * dependency injection framework.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe. The intended usage is
 * that a new instance is created for each new builder operator.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: JellyBuilder.java 211 2012-07-10 19:49:13Z oheger $
 */
public class JellyBuilder extends JellyBeanBuilder implements Builder
{
    /**
     * Constant for the default name space URI for the component builder tag
     * library.
     */
    public static final String NSURI_COMPONENT_BUILDER = "formBuilder";

    /**
     * Constant for the default name space URI for the action builder tag
     * library.
     */
    public static final String NSURI_ACTION_BUILDER = "actionBuilder";

    /**
     * Constant for the default namespace URI for the window builder tag
     * library.
     */
    public static final String NSURI_WINDOW_BUILDER = "windowBuilder";

    /** Constant for the prefix of internal beans. */
    private static final String BEAN_PREFIX = "jguiraffe.";

    /** Constant for the name of the component builder data bean. */
    private static final String BEAN_COMP_BUILDER_DATA = BEAN_PREFIX
            + "componentBuilderData";

    /** Constant for the name of the action builder data bean. */
    private static final String BEAN_ACTION_BUILDER_DATA = BEAN_PREFIX
            + "actionBuilder";

    /** Constant for the name of the window builder data bean. */
    private static final String BEAN_WINDOW_BUILDER_DATA = BEAN_PREFIX
            + "windowBuilderData";

    /** A map with the so far registered default converters. */
    private final Map<Class<?>, Converter> defaultConverters;

    /** A map with the so far registered base class default converters. */
    private final Map<Class<?>, Converter> defaultBaseClassConverters;

    /** Stores a reference to the component manager. */
    private ComponentManager componentManager;

    /** Stores a reference to the field handler factory. */
    private FieldHandlerFactory fieldHandlerFactory;

    /** Stores a reference to the action manager. */
    private ActionManager actionManager;

    /** Stores a reference to the window manager. */
    private WindowManager windowManager;

    /** Stores the name of this builder. */
    private String name;

    /** Stores the name space for the component builder library. */
    private String componentBuilderNamespace;

    /** Stores the name space for the action builder library. */
    private String actionBuilderNamespace;

    /** Stores the name space for the window builder library. */
    private String windowBuilderNamespace;

    /**
     * Creates a new instance of <code>JellyBuilder</code>.
     */
    public JellyBuilder()
    {
        defaultConverters = new LinkedHashMap<Class<?>, Converter>();
        defaultBaseClassConverters = new LinkedHashMap<Class<?>, Converter>();
        setDiBuilderNameSpaceURI(JellyBeanBuilderFactory.NSURI_DI_BUILDER);
    }

    /**
     * Returns the name of this builder.
     *
     * @return the builder's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of this builder. The name can be used in scripts to execute
     * conditional code.
     *
     * @param name the builder's name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name space used for the action builder tag library.
     *
     * @return the name space URI for the action builder tag library
     */
    public String getActionBuilderNamespace()
    {
        return (actionBuilderNamespace == null) ? NSURI_ACTION_BUILDER
                : actionBuilderNamespace;
    }

    /**
     * Sets the name space used for the action builder tag library.
     *
     * @param actionBuilderNamespace the new name space URI
     */
    public void setActionBuilderNamespace(String actionBuilderNamespace)
    {
        this.actionBuilderNamespace = actionBuilderNamespace;
    }

    /**
     * Returns the name space used for the component builder tag library.
     *
     * @return the name space URI for the component builder tag library
     */
    public String getComponentBuilderNamespace()
    {
        return (componentBuilderNamespace == null) ? NSURI_COMPONENT_BUILDER
                : componentBuilderNamespace;
    }

    /**
     * Sets the name space used for the component builder tag library.
     *
     * @param componentBuilderNamespace the new name space
     */
    public void setComponentBuilderNamespace(String componentBuilderNamespace)
    {
        this.componentBuilderNamespace = componentBuilderNamespace;
    }

    /**
     * Returns the name space used for the window builder tag library.
     *
     * @return the name space URI for the window builder tag library
     */
    public String getWindowBuilderNamespace()
    {
        return (windowBuilderNamespace == null) ? NSURI_WINDOW_BUILDER
                : windowBuilderNamespace;
    }

    /**
     * Sets the name space used for the window builder tag library.
     *
     * @param windowBuilderNamespace the new name space
     */
    public void setWindowBuilderNamespace(String windowBuilderNamespace)
    {
        this.windowBuilderNamespace = windowBuilderNamespace;
    }

    /**
     * Returns the action manager used by this builder.
     *
     * @return the action manager
     */
    public ActionManager getActionManager()
    {
        return actionManager;
    }

    /**
     * Sets the action manager to be used by this builder. The action manager is
     * responsible for the creation of actions, menus, toolbar items etc.
     *
     * @param actionManager the action manager
     */
    public void setActionManager(ActionManager actionManager)
    {
        this.actionManager = actionManager;
    }

    /**
     * Returns the component manager used by this builder.
     *
     * @return the component manager
     */
    public ComponentManager getComponentManager()
    {
        return componentManager;
    }

    /**
     * Sets the component manager to be used by this builder. The component
     * manager is responsible for creating all kinds of GUI components in forms.
     *
     * @param componentManager the component manager
     */
    public void setComponentManager(ComponentManager componentManager)
    {
        this.componentManager = componentManager;
    }

    /**
     * Returns the field handler factory used by this builder.
     *
     * @return the field handler factory
     */
    public FieldHandlerFactory getFieldHandlerFactory()
    {
        if (fieldHandlerFactory == null)
        {
            fieldHandlerFactory = new DefaultFieldHandlerFactory();
        }
        return fieldHandlerFactory;
    }

    /**
     * Sets the field handler factory to be used by this builder. The field
     * handler factory will create handler objects that deal with form
     * components.
     *
     * @param fieldHandlerFactory the field handler factory
     */
    public void setFieldHandlerFactory(FieldHandlerFactory fieldHandlerFactory)
    {
        this.fieldHandlerFactory = fieldHandlerFactory;
    }

    /**
     * Returns the window manager used by this builder.
     *
     * @return the window manager
     */
    public WindowManager getWindowManager()
    {
        return windowManager;
    }

    /**
     * Sets the window manager to be used by this builder. The window manager is
     * responsible for creating all kinds of windows.
     *
     * @param windowManager the window manager
     */
    public void setWindowManager(WindowManager windowManager)
    {
        this.windowManager = windowManager;
    }

    /**
     * Returns a map with the default converters that have been registered so
     * far.
     *
     * @return a map with the default converters; the map is unmodifiable
     * @see #addDefaultConverter(Converter, Class)
     */
    public Map<Class<?>, Converter> getDefaultConverters()
    {
        return Collections.unmodifiableMap(defaultConverters);
    }

    /**
     * Adds a new default converter to this builder. Default converters are
     * stored internally. When a build operation is executed and a default
     * {@link ConversionHelper} object has to be created, these default
     * converters are automatically registered at the helper object. Normally
     * client code does not have to call this method manually. This is done when
     * this builder instance is created (per default the dependency injection
     * framework is used to create new builder instances; when this happens the
     * default converters are automatically initialized).
     *
     * @param converter the converter to be added (must not be <b>null</b>)
     * @param targetClass the target class of the conversion (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is missing
     * @see ConversionHelper#registerConverter(Converter, Class)
     */
    public void addDefaultConverter(Converter converter, Class<?> targetClass)
    {
        checkRegisterConverterArgs(converter, targetClass);
        defaultConverters.put(targetClass, converter);
    }

    /**
     * Adds all converters in the specified map as default converters to this
     * object. Delegates to {@link #addDefaultConverter(Converter, Class)} for
     * each entry in the map.
     *
     * @param converters the map with the converters to be added (must not be
     *        <b>null</b>
     * @throws IllegalArgumentException if the map is <b>null</b> or contains
     *         <b>null</b> entries
     */
    public void addDefaultConverters(
            Map<Class<?>, ? extends Converter> converters)
    {
        checkConvertersMap(converters);

        for (Map.Entry<Class<?>, ? extends Converter> e : converters.entrySet())
        {
            addDefaultConverter(e.getValue(), e.getKey());
        }
    }

    /**
     * Removes all default converters that have been added to this object so
     * far.
     */
    public void clearDefaultConverters()
    {
        defaultConverters.clear();
    }

    /**
     * Returns a map with the default base class converters that have been
     * registered so far.
     *
     * @return a map with the default base class converters; the map is
     *         unmodifiable
     * @see #addDefaultConverter(Converter, Class)
     */
    public Map<Class<?>, Converter> getDefaultBaseClassConverters()
    {
        return Collections.unmodifiableMap(defaultBaseClassConverters);
    }

    /**
     * Adds a new default base class converter to this builder. This method
     * works exactly like {@link #addDefaultConverter(Converter, Class)}, but
     * the {@code Converter} passed to this method is registered as a base class
     * converter at the {@link ConversionHelper} object created for a new
     * builder operation.
     *
     * @param converter the converter to be added (must not be <b>null</b>)
     * @param targetClass the target class of the conversion (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is missing
     * @see ConversionHelper#registerBaseClassConverter(Converter, Class)
     */
    public void addDefaultBaseClassConverter(Converter converter,
            Class<?> targetClass)
    {
        checkRegisterConverterArgs(converter, targetClass);
        defaultBaseClassConverters.put(targetClass, converter);
    }

    /**
     * Adds all converters in the specified map as default base class converters
     * to this object. Delegates to
     * {@link #addDefaultBaseClassConverter(Converter, Class)} for each entry in
     * the map.
     *
     * @param converters the map with the converters to be added (must not be
     *        <b>null</b>
     * @throws IllegalArgumentException if the map is <b>null</b> or contains
     *         <b>null</b> entries
     */
    public void addDefaultBaseClassConverters(
            Map<Class<?>, ? extends Converter> converters)
    {
        checkConvertersMap(converters);

        for (Map.Entry<Class<?>, ? extends Converter> e : converters.entrySet())
        {
            addDefaultBaseClassConverter(e.getValue(), e.getKey());
        }
    }

    /**
     * Adds all converters in the specified map as default base class converters
     * to this object using the specified {@code ClassLoaderProvider} to resolve
     * class names. This method works like the method with the same name, but
     * converter class are specified by name and resolved dynamically.
     *
     * @param converters the map with the converters to be added (must not be
     *        <b>null</b>
     * @param clp the {@code ClassLoaderProvider} (must not be <b>null</b>)
     * @throws IllegalArgumentException if the map is <b>null</b> or contains
     *         <b>null</b> entries or the {@code ClassLoaderProvider} is
     *         <b>null</b>
     * @since 1.2
     */
    public void addDefaultBaseClassConverters(
            Map<String, ? extends Converter> converters, ClassLoaderProvider clp)
    {
        checkConvertersMap(converters);
        if (clp == null)
        {
            throw new IllegalArgumentException(
                    "ClassLoaderProvider must not be null!");
        }

        for (Map.Entry<String, ? extends Converter> e : converters.entrySet())
        {
            addDefaultBaseClassConverter(e.getValue(),
                    clp.loadClass(e.getKey(), null));
        }
    }

    /**
     * Removes all base class converters that have been added to this object
     * before.
     */
    public void clearDefaultBaseClassConverters()
    {
        defaultBaseClassConverters.clear();
    }

    /**
     * Registers default converters at the specified {@code ConversionHelper}
     * instance. These are the converters added using the
     * {@link #addDefaultConverter(Converter, Class)} or
     * {@link #addDefaultBaseClassConverter(Converter, Class)} methods. If the
     * {@link BuilderData} object passed to the {@code build()} methods does not
     * contain an {@link InvocationHelper} instance, a new instance is
     * automatically created, and the default converters are registered at the
     * associated {@link ConversionHelper} instance. However, if a custom
     * {@link InvocationHelper} is set, this registration is not done - the
     * helper object is used as is. Using this method the default converters can
     * be registered manually. Clients of the builder that need their own
     * {@link InvocationHelper} can decide whether they call this method so that
     * enhanced type conversion facilities are supported or not.
     *
     * @param conHlp the {@code ConversionHelper} to be initialized (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if the {@code ConversionHelper} is
     *         <b>null</b>
     */
    public void registerDefaultConverters(ConversionHelper conHlp)
    {
        if (conHlp == null)
        {
            throw new IllegalArgumentException(
                    "ConversionHelper must not be null!");
        }

        for (Map.Entry<Class<?>, Converter> e : defaultConverters.entrySet())
        {
            conHlp.registerConverter(e.getValue(), e.getKey());
        }
        for (Map.Entry<Class<?>, Converter> e : defaultBaseClassConverters
                .entrySet())
        {
            conHlp.registerBaseClassConverter(e.getValue(), e.getKey());
        }
    }

    /**
     * A generic build method for executing a builder script. The specified
     * Jelly script will be executed. Results are returned in properties of the
     * parameter object.
     *
     * @param script specifies the script to be executed
     * @param data the parameter object
     * @throws BuilderException if an error occurs
     */
    public void build(Locator script, BuilderData data) throws BuilderException
    {
        performBuild(script, data, null);
    }

    /**
     * A convenience method for building windows. Executes the specified Jelly
     * script and returns the top level window created by this script.
     *
     * @param script specifies the script to be executed
     * @param data the parameter object
     * @return the result window
     * @throws BuilderException if an error occurs
     */
    public Window buildWindow(Locator script, BuilderData data)
            throws BuilderException
    {
        performBuild(script, data, null);
        return (Window) data.getBuilderContext().getBean(
                BuilderData.KEY_RESULT_WINDOW);
    }

    /**
     * A convenience method for building the content of a container object.
     * Executes the specified Jelly script with the passed in container as root
     * container.
     *
     * @param script specifies the script to be executed
     * @param data the parameter object
     * @param container the root container
     * @throws BuilderException if an error occurs
     */
    public void buildContainer(Locator script, BuilderData data,
            Object container) throws BuilderException
    {
        performBuild(script, data, container);
    }

    /**
     * Frees all resources associated with the specified {@code BuilderData}
     * object. The {@code BuilderData} object passed to this method must have
     * been initialized by this builder. Especially the {@code BeanContext} and
     * the {@code BeanBuilderResult} properties must have been set. Otherwise an
     * exception is thrown.
     *
     * @param data the {@code BuilderData} object
     * @throws IllegalArgumentException if the data object is <b>null</b> or is
     *         not fully initialized
     */
    public void release(BuilderData data)
    {
        if (data == null)
        {
            throw new IllegalArgumentException(
                    "BuilderData object must not be null!");
        }
        if (data.getBeanBuilderResult() == null)
        {
            throw new IllegalArgumentException(
                    "Invalid BuilderData: No BeanBuilderResult found!");
        }
        if (data.getBuilderContext() == null)
        {
            throw new IllegalArgumentException(
                    "Invalid BuilderData: No BeanContext found!");
        }

        release(data.getBeanBuilderResult());
        data.getBuilderContext().close();
    }

    /**
     * The main method for executing a builder script. This method is called by
     * all public <code>build()</code> methods. It executes the specified script
     * with a newly initialized context and collects all results afterwards.
     *
     * @param script specifies the build script
     * @param data the builder parameter object
     * @param rootContainer the root container object
     * @throws BuilderException if an error occurs
     */
    protected void performBuild(Locator script, BuilderData data,
            Object rootContainer) throws BuilderException
    {
        checkBuilderData(data);
        checkState();
        data.setBuilder(this);

        JellyContext context = setUpExtendedJellyContext(data, rootContainer);
        InvocationHelper invHlp = initInvocationHelper(data);
        MutableBeanStore rootStore =
                initBuilderBeanContext(data, context, invHlp);

        fetchResults(
                context,
                data,
                executeScript(script, context, rootStore, data
                        .getParentContext().getClassLoaderProvider(), invHlp),
                script);
    }

    /**
     * Creates and initializes the Jelly context to be used for executing the
     * builder script. This method will perform all necessary initialization
     * steps so that the context can be directly used hereafter. Extended in
     * this context means that the context will be used for the more complex
     * build operations (forms, actions, and windows) and not only for bean
     * definitions.
     *
     * @param data the builder parameters
     * @param rootContainer the root container for the builder process
     * @return the fully initialized context
     */
    protected JellyContext setUpExtendedJellyContext(BuilderData data,
            Object rootContainer)
    {
        JellyContext result = createJellyContext();
        initProperties(data, result);

        createComponentBuilderData(data, rootContainer).put(result);
        createActionBuilderData(data).put(result);
        createWindowBuilderData(data).put(result);
        registerExtendedTagLibraries(result, data);

        return result;
    }

    /**
     * Creates the component builder data object for the current builder
     * process. This implementation first tries to obtain a {@code
     * ComponentBuilderData} bean from the parent bean context. If such a bean
     * cannot be found, a default bean is created.
     *
     * @param data the builder parameters
     * @param rootContainer the root container
     * @return the component builder data object
     */
    protected ComponentBuilderData createComponentBuilderData(BuilderData data,
            Object rootContainer)
    {
        ComponentBuilderData result;
        if (data.getParentContext().containsBean(BEAN_COMP_BUILDER_DATA))
        {
            result = (ComponentBuilderData) data.getParentContext().getBean(
                    BEAN_COMP_BUILDER_DATA);
        }
        else
        {
            result = new ComponentBuilderData();
        }

        if (data.getBuilderName() != null)
        {
            result.setBuilderName(data.getBuilderName());
        }
        else
        {
            result.setBuilderName(getName());
        }
        initComponentManagerOnBuilderData(result);
        result.setFieldHandlerFactory(getFieldHandlerFactory());
        result.setDefaultResourceGroup(data.getDefaultResourceGroup());
        result.setRootContainer(rootContainer);
        result.initializeForm(data.getTransformerContext(), data
                .getBindingStrategy());
        result.getForm().setFormValidator(data.getFormValidator());

        return result;
    }

    /**
     * Initializes the {@code InvocationHelper} to be used during the builder
     * operation. This method is called before the builder script gets executed.
     * If an {@code InvocationHelper} instance is provided in the specified
     * {@code BuilderData} object, it is directly returned. Otherwise, a new
     * instance is created with a specialized {@link ConversionHelper} whose
     * parent is obtained from the parent bean store. That way all converters
     * registered for the parent bean store are also available in the current
     * builder script.
     *
     * @param data the builder parameters
     * @return the {@code InvocationHelper} to use
     */
    protected InvocationHelper initInvocationHelper(BuilderData data)
    {
        InvocationHelper result = data.getInvocationHelper();

        if (result == null)
        {
            ConversionHelper parent =
                    DefaultBeanStore.fetchConversionHelper(data
                            .getParentContext().getDefaultBeanStore(), false);
            result = new InvocationHelper(new ConversionHelper(parent));
            registerDefaultConverters(result.getConversionHelper());
        }

        return result;
    }

    /**
     * Creates the root bean store for the current builder operation and
     * initializes the {@code BeanContext} to be used. In this bean store
     * the bean definitions defined by the builder script are stored (unless a
     * different store is explicitly selected). A new {@code BeanContext}
     * is created and initialized, so that it allows access to the following
     * bean definitions (in this order):
     * <ul>
     * <li>the beans defined in the root store</li>
     * <li>the beans stored in this {@code ComponentBuilderData}
     * instance</li>
     * <li>the beans defined in the parent context</li>
     * <li>the content of the Jelly context</li>
     * </ul>
     *
     * @param data the builder parameters
     * @param context the Jelly context
     * @param invHlp the current {@code InvocationHelper}
     * @return the new root store for the builder operation
     */
    protected MutableBeanStore initBuilderBeanContext(BuilderData data,
            JellyContext context, InvocationHelper invHlp)
    {
        SimpleBeanStoreImpl beanStore = new SimpleBeanStoreImpl();
        beanStore.setParent(data.getParentContext().getDefaultBeanStore());

        // Connect all builder data objects with the bean store
        ComponentBuilderData compData = ComponentBuilderData.get(context);
        assert compData != null : "No component builder data found!";
        compData.initBeanStore(beanStore);
        ActionBuilder actData = ActionBuilder.get(context);
        assert actData != null : "No action builder data found!";
        actData.initBeanStore(beanStore);
        WindowBuilderData wndData = WindowBuilderData.get(context);
        assert wndData != null : "No widow builder data found!";
        wndData.initBeanStore(beanStore);

        // Store the builder data itself
        context.setVariable(ComponentBuilderData.KEY_BUILDER_DATA, data);

        DefaultBeanStore store = new DefaultBeanStore();
        store.setParent(beanStore);
        store.setConversionHelper(invHlp.getConversionHelper());
        BeanContext builderContext =
                new BeanContextWrapper(data.getParentContext(),
                        new CombinedBeanStore(store, new JellyContextBeanStore(
                                context, null)));
        registerBeanCreationListeners(builderContext, data);
        compData.setBeanContext(builderContext);
        data.setBuilderContext(builderContext);
        return store;
    }

    /**
     * Creates the action builder data object for the current builder process.
     * This implementation first tries to obtain a {@code ActionBuilder} bean
     * from the parent bean context. If such a bean cannot be found, a new
     * default instance is created.
     *
     * @param data the builder parameters
     * @return the action builder data object
     */
    protected ActionBuilder createActionBuilderData(BuilderData data)
    {
        ActionBuilder result;
        if (data.getParentContext().containsBean(BEAN_ACTION_BUILDER_DATA))
        {
            result = (ActionBuilder) data.getParentContext().getBean(
                    BEAN_ACTION_BUILDER_DATA);
        }
        else
        {
            result = new ActionBuilder();
        }

        result.setActionManager(getActionManager());
        result.setActionStore(data.getActionStore());
        result.setMenuIcon(data.isMenuIcon());
        result.setToolbarText(data.isToolbarText());

        return result;
    }

    /**
     * Creates the window builder data object for the current builder process.
     * This implementation first checks whether a {@code WindowBuilderData} bean
     * can be obtained from the parent bean context. If such a bean cannot be
     * found, a new default instance is created.
     *
     * @param data the builder parameters
     * @return the window builder data object
     */
    protected WindowBuilderData createWindowBuilderData(BuilderData data)
    {
        WindowBuilderData result;
        if (data.getParentContext().containsBean(BEAN_WINDOW_BUILDER_DATA))
        {
            result = (WindowBuilderData) data.getParentContext().getBean(
                    BEAN_WINDOW_BUILDER_DATA);
        }
        else
        {
            result = new WindowBuilderData();
        }

        result.setWindowManager(getWindowManager());
        result.setFormBean(data.getFormBean());
        result.setParentWindow(data.getParentWindow());

        return result;
    }

    /**
     * Registers the builder tag libraries at the given context. This method is
     * called by <code>setUpExtendedJellyContext()</code> before the builder
     * script will be executed. "Extended" in this context means that all tag
     * libraries for the more complex builders are registered.
     *
     * @param context the context
     * @param data the builder parameters
     */
    protected void registerExtendedTagLibraries(JellyContext context,
            BuilderData data)
    {
        registerTagLibraries(context);
        context.registerTagLibrary(getComponentBuilderNamespace(),
                new FormBuilderTagLibrary());
        context.registerTagLibrary(getActionBuilderNamespace(),
                new ActionBuilderTagLibrary());
        context.registerTagLibrary(getWindowBuilderNamespace(),
                new WindowBuilderTagLibrary());
    }

    /**
     * Checks the state of this builder. This method is called before a build
     * operation starts. It checks whether all required properties have been
     * initialized. If this is not the case, an exception is thrown.
     *
     * @throws IllegalStateException if initialization of this instance is
     * incomplete
     */
    protected void checkState()
    {
        if (getComponentManager() == null)
        {
            throw new IllegalStateException("No component manager set!");
        }
        if (getActionManager() == null)
        {
            throw new IllegalStateException("No action manager set!");
        }
        if (getWindowManager() == null)
        {
            throw new IllegalStateException("No window manager set!");
        }
    }

    /**
     * Fetches all result variables from the context and stores them in the
     * builder parameter object. This method is called after successful script
     * execution. It also deals with invoking registered callback objects.
     *
     * @param context the context object
     * @param data the parameter object
     * @param result the result object from the bean builder
     * @param script the locator to the current build script
     * @throws BuilderException if an error occurs
     */
    protected void fetchResults(JellyContext context, BuilderData data,
            BeanBuilderResult result, Locator script) throws BuilderException
    {
        ComponentBuilderData compData = ComponentBuilderData.get(context);
        try
        {
            compData.invokeCallBacks();
        }
        catch (FormBuilderException e)
        {
            throw new BuilderException(script.getURL(), e);
        }

        WindowBuilderData wndData = WindowBuilderData.get(context);
        if (wndData.getResultWindow() != null)
        {
            context.setVariable(BuilderData.KEY_RESULT_WINDOW, wndData
                    .getResultWindow());
            // Check if auto release should be enabled
            if (data.isAutoRelease())
            {
                wndData.getResultWindow().addWindowListener(
                        new AutoReleaseListener(data));
            }
        }

        data.setBeanBuilderResult(result);
    }

    /**
     * Registers the {@code BeanCreationListener} objects defined by the
     * {@code BuilderData} object at the specified {@code BeanContext}. This
     * method is called by
     * {@link #initBuilderBeanContext(BuilderData, JellyContext, InvocationHelper)}
     * with the newly created {@code BeanContext} as parameter. If the
     * {@code BuilderData} object contains bean creation listeners, these
     * listeners have to be added to the context. (Note: this method is called
     * in any case, even if the collection with creation listeners is
     * <b>null</b> or empty.)
     *
     * @param context the {@code BeanContext} used by the builder
     * @param data the {@code BuilderData} object
     */
    protected void registerBeanCreationListeners(BeanContext context,
            BuilderData data)
    {
        if (data.getBeanCreationListeners() != null)
        {
            for (BeanCreationListener l : data.getBeanCreationListeners())
            {
                context.addBeanCreationListener(l);
            }
        }
    }

    /**
     * Checks whether the passed in {@code BuilderData} object is valid. If this
     * is not the case, an exception is thrown.
     *
     * @param data the object to be checked
     * @throws BuilderException if the object is invalid
     */
    private void checkBuilderData(BuilderData data) throws BuilderException
    {
        if (data == null)
        {
            throw new BuilderException("No parameter object specified!");
        }
        if (data.getParentContext() == null)
        {
            throw new BuilderException("No parent bean context set!");
        }
    }

    /**
     * Initializes the {@code ComponentBuilderData} object with the
     * {@code ComponentManager}. This method also checks whether the component
     * manager is a {@link FormContextListener}. In this case, it is registered
     * at the {@code ComponentBuilderData}.
     *
     * @param builderData the {@code ComponentBuilderData}
     */
    private void initComponentManagerOnBuilderData(
            ComponentBuilderData builderData)
    {
        builderData.setComponentManager(getComponentManager());
        if (getComponentManager() instanceof FormContextListener)
        {
            builderData
                    .addFormContextListener((FormContextListener) getComponentManager());
        }
    }

    /**
     * Sets additional properties in the Jelly context. This method is called
     * after the context has been created. It checks whether additional
     * properties are defined in the builder data object. if this is the case,
     * corresponding variables are created in the context.
     *
     * @param data the {@code BuilderData} object
     * @param context the Jelly context
     */
    private static void initProperties(BuilderData data, JellyContext context)
    {
        if (data.getProperties() != null)
        {
            for (Map.Entry<String, Object> e : data.getProperties().entrySet())
            {
                context.setVariable(e.getKey(), e.getValue());
            }
        }
    }

    /**
     * Helper method for testing the parameters of a register converter
     * operation. Throws an exception if a required parameter is missing.
     *
     * @param conv the converter
     * @param cls the target class
     * @throws IllegalArgumentException if a required parameter is missing
     */
    private static void checkRegisterConverterArgs(Converter conv, Class<?> cls)
    {
        if (conv == null)
        {
            throw new IllegalArgumentException("Converter mut not be null!");
        }
        if (cls == null)
        {
            throw new IllegalArgumentException("Target class must not be null!");
        }
    }

    /**
     * Helper method for checking the validity of a map with converters.
     *
     * @param converters the map to be checked
     * @throws IllegalArgumentException if the map is invalid
     */
    private static void checkConvertersMap(
            Map<?, ? extends Converter> converters)
    {
        if (converters == null)
        {
            throw new IllegalArgumentException(
                    "Map with converters must not be null!");
        }
    }
}

/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanCreationListener;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.gui.builder.BeanBuilderResult;
import net.sf.jguiraffe.gui.builder.Builder;
import net.sf.jguiraffe.gui.builder.BuilderData;
import net.sf.jguiraffe.gui.builder.action.ActionStore;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.cmd.CommandQueue;
import net.sf.jguiraffe.gui.forms.BindingStrategy;
import net.sf.jguiraffe.gui.forms.FormValidator;
import net.sf.jguiraffe.transform.TransformerContext;

/**
 * <p>
 * The application specific default implementation of the {@code BuilderData}
 * interface.
 * </p>
 * <p>
 * This class provides meaningful implementations of all methods required by the
 * {@code BuilderData} interface. An instance can be obtained from the
 * {@link ApplicationContext} class that is already initialized with predefined
 * values for many fields. So a client need not bother with all of the data
 * supported by this interface, but has only to set the values it is specially
 * interested in.
 * </p>
 * <p>
 * Implementation note: this class is not thread-safe. The typical usage
 * scenario is that an instance is requested from {@link ApplicationContext},
 * initialized with the properties required by the application and passed to a
 * builder.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ApplicationBuilderData.java 205 2012-01-29 18:29:57Z oheger $
 * @see ApplicationContext#initBuilderData()
 */
public class ApplicationBuilderData implements BuilderData
{
    /** Stores the default resource group. */
    private Object defaultResourceGroup;

    /** Stores the action store. */
    private ActionStore actionStore;

    /** Stores the parent window. */
    private Window parentWindow;

    /** Stores the transformer context. */
    private TransformerContext transformerContext;

    /** Stores the form bean. */
    private Object formBean;

    /** Stores the binding strategy. */
    private BindingStrategy bindingStrategy;

    /** Stores the form validator. */
    private FormValidator formValidator;

    /** Stores the parent bean context. */
    private BeanContext parentContext;

    /** Stores the bean context used by the builder. */
    private BeanContext builderContext;

    /** Stores the result object of the builder. */
    private BeanBuilderResult beanBuilderResult;

    /** Stores the invocation helper. */
    private InvocationHelper invocationHelper;

    /** Stores the message output object.*/
    private MessageOutput messageOutput;

    /** Stores the command queue.*/
    private CommandQueue commandQueue;

    /** A collection with bean creation listeners.*/
    private final Collection<BeanCreationListener> beanCreationListeners;

    /** A map for additional properties. */
    private Map<String, Object> properties;

    /** The builder object that processed this instance.*/
    private Builder builder;

    /** Stores the name of the builder.*/
    private String builderName;

    /** Stores the menu icon flag. */
    private boolean menuIcon;

    /** Stores the toolbar text flag. */
    private boolean toolbarText;

    /** The auto release flag.*/
    private boolean autoRelease = true;

    /**
     * Creates a new instance of {@code ApplicationBuilderData}.
     */
    public ApplicationBuilderData()
    {
        beanCreationListeners = new ArrayList<BeanCreationListener>();
    }

    /**
     * Returns the action store.
     *
     * @return the action store
     */
    public ActionStore getActionStore()
    {
        return actionStore;
    }

    /**
     * Sets the action store.
     *
     * @param actionStore the actionStore
     */
    public void setActionStore(ActionStore actionStore)
    {
        this.actionStore = actionStore;
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
     * Sets the default resource group.
     *
     * @param defaultResourceGroup the default resource group
     */
    public void setDefaultResourceGroup(Object defaultResourceGroup)
    {
        this.defaultResourceGroup = defaultResourceGroup;
    }

    /**
     * Returns the form bean.
     *
     * @return the form bean
     */
    public Object getFormBean()
    {
        return formBean;
    }

    /**
     * Sets the form bean.
     *
     * @param formBean the form bean
     */
    public void setFormBean(Object formBean)
    {
        this.formBean = formBean;
    }

    /**
     * Returns the {@code BindingStrategy} used by the current form.
     *
     * @return the {@code BindingStrategy}
     */
    public BindingStrategy getBindingStrategy()
    {
        return bindingStrategy;
    }

    /**
     * Sets the {@code BindingStrategy} to be used by the current form.
     *
     * @param strat the {@code BindingStrategy}
     */
    public void setBindingStrategy(BindingStrategy strat)
    {
        bindingStrategy = strat;
    }

    /**
     * Returns the {@code FormValidator} for validating the current form.
     *
     * @return the {@code FormValidator}
     */
    public FormValidator getFormValidator()
    {
        return formValidator;
    }

    /**
     * Sets the {@code FormValidator} for validating the current form. If no
     * {@code FormValidator} is set, no form-level validation is performed. The
     * form's fields may be validated though if corresponding validators have
     * been defined.
     *
     * @param validator the {@code FormValidator}
     */
    public void setFormValidator(FormValidator validator)
    {
        formValidator = validator;
    }

    /**
     * Returns the menu icon flag.
     *
     * @return the menu icon flag
     */
    public boolean isMenuIcon()
    {
        return menuIcon;
    }

    /**
     * Sets the menu icon flag.
     *
     * @param menuIcon the flag value
     */
    public void setMenuIcon(boolean menuIcon)
    {
        this.menuIcon = menuIcon;
    }

    /**
     * Returns the parent window.
     *
     * @return the parent window
     */
    public Window getParentWindow()
    {
        return parentWindow;
    }

    /**
     * Sets the parent window.
     *
     * @param parentWindow the parent window
     */
    public void setParentWindow(Window parentWindow)
    {
        this.parentWindow = parentWindow;
    }

    /**
     * Returns the toolbar text flag.
     *
     * @return the toolbar text flag
     */
    public boolean isToolbarText()
    {
        return toolbarText;
    }

    /**
     * Sets the toolbar text flag.
     *
     * @param toolbarText the toolbar text flag
     */
    public void setToolbarText(boolean toolbarText)
    {
        this.toolbarText = toolbarText;
    }

    /**
     * Returns the transformer context.
     *
     * @return the transformer context
     */
    public TransformerContext getTransformerContext()
    {
        return transformerContext;
    }

    /**
     * Sets the transformer context.
     *
     * @param transformerContext the transformer context
     */
    public void setTransformerContext(TransformerContext transformerContext)
    {
        this.transformerContext = transformerContext;
    }

    /**
     * Returns the result object from the bean builder.
     *
     * @return the results of the bean builder
     */
    public BeanBuilderResult getBeanBuilderResult()
    {
        return beanBuilderResult;
    }

    /**
     * Sets the result object for the bean builder.
     *
     * @param res the results of the bean builder
     */
    public void setBeanBuilderResult(BeanBuilderResult res)
    {
        beanBuilderResult = res;
    }

    /**
     * Returns the parent bean context.
     *
     * @return the parent bean context
     */
    public BeanContext getParentContext()
    {
        return parentContext;
    }

    /**
     * Sets the parent bean context.
     *
     * @param ctx the parent bean context
     */
    public void setParentContext(BeanContext ctx)
    {
        parentContext = ctx;
    }

    /**
     * Returns the root store populated by the builder. This method can only be
     * called after the builder operation.
     *
     * @return the root store returned from the builder
     * @throws IllegalStateException if no builder results are available yet
     */
    public BeanStore getRootStore()
    {
        if (getBeanBuilderResult() == null)
        {
            throw new IllegalStateException(
                    "Root store cannot be queried before builder "
                            + "results are available!");
        }

        return getBeanBuilderResult().getBeanStore(null);
    }

    /**
     * Returns the bean context used by the builder.
     *
     * @return the builder's bean context
     */
    public BeanContext getBuilderContext()
    {
        return builderContext;
    }

    /**
     * Sets the bean context used by the builder.
     *
     * @param ctx the builder's bean context
     */
    public void setBuilderContext(BeanContext ctx)
    {
        builderContext = ctx;
    }

    /**
     * Returns the {@code InvocationHelper}.
     *
     * @return the {@code InvocationHelper}
     */
    public InvocationHelper getInvocationHelper()
    {
        return invocationHelper;
    }

    /**
     * Sets the {@code InvocationHelper}.
     *
     * @param invocationHelper the {@code InvocationHelper}
     */
    public void setInvocationHelper(InvocationHelper invocationHelper)
    {
        this.invocationHelper = invocationHelper;
    }

    /**
     * Returns the <code>MessageOutput</code> object.
     *
     * @return the message output object
     */
    public MessageOutput getMessageOutput()
    {
        return messageOutput;
    }

    /**
     * Sets the <code>MessageOutput</code> object.
     *
     * @param messageOutput the message output object
     */
    public void setMessageOutput(MessageOutput messageOutput)
    {
        this.messageOutput = messageOutput;
    }

    /**
     * Returns the <code>CommandQueue</code>.
     *
     * @return the command queue
     */
    public CommandQueue getCommandQueue()
    {
        return commandQueue;
    }

    /**
     * Sets the <code>CommandQueue</code>.
     *
     * @param commandQueue the command queue
     */
    public void setCommandQueue(CommandQueue commandQueue)
    {
        this.commandQueue = commandQueue;
    }

    /**
     * Returns a collection with {@code BeanCreationListener} objects to be
     * registered at the {@code BeanContext} created by the builder. Note: this
     * collection cannot be modified.
     *
     * @return a collection with {@code BeanCreationListener} objects
     */
    public Collection<BeanCreationListener> getBeanCreationListeners()
    {
        return Collections.unmodifiableCollection(beanCreationListeners);
    }

    /**
     * Adds the specified {@code BeanCreationListener} to this object. It will
     * be registered at the {@code BeanContext} created by the builder and thus
     * notified for all bean created by the
     * <em>dependency injection framework</em>.
     *
     * @param l the {@code BeanCreationListener} to be added (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if the {@code BeanCreationListener} is
     *         <b>null</b>
     */
    public void addBeanCreationListener(BeanCreationListener l)
    {
        if (l == null)
        {
            throw new IllegalArgumentException(
                    "BeanCreationListener must not be null!");
        }
        beanCreationListeners.add(l);
    }

    /**
     * Adds all {@code BeanCreationListener} objects contained in the given
     * collection to this object. They will be registered at the {@code
     * BeanContext} created by the builder and thus notified for all bean
     * created by the <em>dependency injection framework</em>.
     *
     * @param listeners the collection with {@code BeanCreationListener} objects
     *        (must not be <b>null</b>)
     * @throws IllegalArgumentException if the collection is <b>null</b> or
     *         contains <b>null</b> elements
     */
    public void addBeanCreationListeners(
            Collection<? extends BeanCreationListener> listeners)
    {
        if (listeners == null)
        {
            throw new IllegalArgumentException(
                    "BeanCreationListener collection must not be null!");
        }

        for (BeanCreationListener l : listeners)
        {
            addBeanCreationListener(l);
        }
    }

    /**
     * Returns a reference to the {@code Builder} instance that processed this
     * object. This value is available only after the {@code Builder} was
     * called.
     *
     * @return the {@code Builder} that processed this {@code BuilderData}
     *         object
     */
    public Builder getBuilder()
    {
        return builder;
    }

    /**
     * Sets the {@code Builder} that processed this object. This method is
     * called by the {@code Builder} instance during the builder operation.
     *
     * @param builder the {@code Builder}
     */
    public void setBuilder(Builder builder)
    {
        this.builder = builder;
    }

    /**
     * Returns the name of the builder.
     *
     * @return the name of the builder
     */
    public String getBuilderName()
    {
        return builderName;
    }

    /**
     * Sets a name for the builder. This name is available during the build
     * process (through the {@code ComponentBuilderData} object). It can be used
     * for conditional execution of builder scripts.
     *
     * @param builderName the name of the builder
     */
    public void setBuilderName(String builderName)
    {
        this.builderName = builderName;
    }

    /**
     * Returns the <em>auto release</em> flag.
     *
     * @return the auto release flag
     */
    public boolean isAutoRelease()
    {
        return autoRelease;
    }

    /**
     * Sets the <em>auto release</em> flag. This flag is evaluated if a window
     * is generated during the builder operation. A value of <b>true</b> means
     * that this object and all resources referenced by it should be freed
     * automatically when the window is closed. This is done by invoking
     * {@link Builder#release(BuilderData)} on the {@link Builder} responsible.
     * Note that the default value of this flag is <b>true</b>, so auto release
     * is enabled per default.
     *
     * @param autoRelease the value of the auto release flag
     */
    public void setAutoRelease(boolean autoRelease)
    {
        this.autoRelease = autoRelease;
    }

    /**
     * Returns a map with additional properties for the builder operation. This
     * implementation either returns the map set by {@link #setProperties(Map)}
     * or the one that was created automatically when {@link #addProperty()} was
     * called. Result may also be <b>null</b> if no properties have been set.
     *
     * @return a map with additional properties for the builder operation
     * @see #setProperties(Map)
     * @see #addProperty(String, Object)
     */
    public Map<String, Object> getProperties()
    {
        return properties;
    }

    /**
     * Sets additional properties for the builder operation. The map passed to
     * this method is directly stored and passed to the builder.
     *
     * @param props the map with additional properties
     */
    public void setProperties(Map<String, Object> props)
    {
        properties = props;
    }

    /**
     * Adds an additional property for the builder operation. This method can be
     * used to populate the map with additional properties that is returned by
     * {@link #getProperties()}. If no map with properties has been set yet, a
     * new one is created ({@link #getProperties()} will return this new map).
     * Otherwise, the property is added to the existing map.
     *
     * @param key the key of the property
     * @param value the value of the property
     */
    public void addProperty(String key, Object value)
    {
        if (properties == null)
        {
            properties = new HashMap<String, Object>();
        }

        properties.put(key, value);
    }
}

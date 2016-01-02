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
package net.sf.jguiraffe.gui.builder;

import java.util.Collection;
import java.util.Map;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanCreationListener;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.gui.builder.action.ActionStore;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.cmd.CommandQueue;
import net.sf.jguiraffe.gui.forms.BindingStrategy;
import net.sf.jguiraffe.gui.forms.FormValidator;
import net.sf.jguiraffe.transform.TransformerContext;

/**
 * <p>
 * Definition of an interface that describes the parameters of a builder
 * operation.
 * </p>
 * <p>
 * An implementation of this interface is used in calls of builder methods to
 * pass all required parameters. There is a bunch of parameters supported by the
 * builder, all of which must be defined so that the builder can work properly.
 * </p>
 * <p>
 * In addition to the input parameters required by the builder, the builder will
 * also store its results in this object. During the builder operation a
 * {@link BeanStore} is created and populated, which can be
 * queried for obtaining objects created by the builder. Some constants define
 * reserved keys for typical objects involved in a builder operation. More of
 * these keys allowing access to typical builder results are defined by the
 * {@link net.sf.jguiraffe.gui.forms.components.ComponentBuilderData ComponentBuilderData}
 * class.
 * </p>
 * <p>
 * There will be implementations of this interface that provide default values
 * for many of the settings defined here. So an application won't have to bother
 * with all. If not marked otherwise in the description of a getter method, the
 * corresponding property is expected to be set by the application invoking the
 * builder.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BuilderData.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface BuilderData
{
    /** Constant for the key under which the result window is stored. */
    String KEY_RESULT_WINDOW = "RESULT_WINDOW";

    /**
     * Returns the default resource group. This property is used for i18n
     * support. If builder scripts contain only resource IDs without a
     * corresponding group definition, this default group will be used.
     *
     * @return the default resource group
     */
    Object getDefaultResourceGroup();

    /**
     * Returns the transformer context to be used by the builder. This context
     * contains some important data required for resolving resources and for
     * validators and transformers.
     *
     * @return the transformer context
     */
    TransformerContext getTransformerContext();

    /**
     * Returns a reference to the action store. Actions defined during the
     * builder process will be stored here.
     *
     * @return the action store
     */
    ActionStore getActionStore();

    /**
     * Returns a reference to the <code>MessageOutput</code> object to be used
     * by the builder and dependent objects. Components that need to output
     * messages to the user can make use of this object.
     *
     * @return the <code>MessageOutput</code> object
     */
    MessageOutput getMessageOutput();

    /**
     * Returns a reference to the global <code>CommandQueue</code> object.
     * This object can be used by the builder (or objects created by the
     * builder) if commands have to be issued.
     *
     * @return the <code>CommandQueue</code>
     */
    CommandQueue getCommandQueue();

    /**
     * Returns a name for the builder. The name returned by this method is
     * evaluated by most of the tags defined by this library. It can be tested
     * using the standard {@code ifName} and {@code unlessName} attributes. This
     * is a very easy means for supporting conditional execution of builder
     * scripts. Per default no builder name is set.
     *
     * @return a name for the builder
     */
    String getBuilderName();

    /**
     * Returns the value of the menu icon flag. This is a hint for the builder
     * that indicates whether menu items should have icons (if defined and
     * supported by the platform).
     *
     * @return the menu icon flag
     */
    boolean isMenuIcon();

    /**
     * Returns the value of the toolbar text flag. This is a hint for the
     * builder that indicates whether toolbar buttons should have texts.
     *
     * @return the toolbar text flag
     */
    boolean isToolbarText();

    /**
     * Returns the form bean. This is a data object that will be used to
     * initialize the input components defined in the GUI and for later storing
     * the current values.
     *
     * @return the form bean
     */
    Object getFormBean();

    /**
     * Returns the {@code BindingStrategy} to be used by the form. The {@code
     * BindingStrategy} must be compatible with the model object used as form
     * bean, i.e. it determines the type of model objects that can be used.
     *
     * @return the {@code BindingStrategy} for the {@code Form}
     * @see #getFormBean()
     */
    BindingStrategy getBindingStrategy();

    /**
     * Returns the {@code FormValidator} for validating the {@code Form} object.
     * This property is optional. If a {@code FormValidator} is defined, input
     * validation on the form-level is performed before the data is stored in
     * the model object.
     *
     * @return the {@code FormValidator}
     */
    FormValidator getFormValidator();

    /**
     * Returns the parent window. This information is needed if a (child) window
     * is to be created.
     *
     * @return the parent window
     */
    Window getParentWindow();

    /**
     * Returns a reference to the parent bean context. This object - and
     * especially the default {@link BeanStore} set for the context - provides
     * access to the global bean definitions. These can be accessed during the
     * builder operation.
     *
     * @return the parent bean context
     */
    BeanContext getParentContext();

    /**
     * Returns a collection with objects to be registered as {@code
     * BeanCreationListener} at the {@code BeanContext} created during the
     * builder operation. The objects contained in this collection are added
     * before the builder operation actually starts as listeners at the {@code
     * BeanContext} that can be queried through the {@link #getBuilderContext()}
     * method. These listeners are triggered immediately for newly created
     * beans, even if beans are created during the builder operation. This is an
     * optional property; an implementation can return <b>null</b> if there are
     * no listeners to register.
     *
     * @return a collection with the {@code BeanCreationListener} objects to be
     *         registered at the {@code BeanContext} created by the builder
     */
    Collection<BeanCreationListener> getBeanCreationListeners();

    /**
     * Returns a map with properties that should be available during the builder
     * operation. An application can use this map to pass in data objects that
     * should be accessible by tags in builder scripts. While executing a
     * builder script access to all beans in the parent {@code BeanStore} is
     * possible. However, sometimes additional objects need to be passed to the
     * builder, for instance model objects for lists, tables, or tree views.
     * These additional objects can be passed through the map returned by this
     * method. This is more convenient than adding objects to the parent {@code
     * BeanStore} only for this purpose. The {@code Builder} implementation
     * evaluates this map and ensures that its content is made available during
     * the execution of the builder script. An implementation is allowed to
     * return <b>null</b> if there are no additional properties.
     *
     * @return a map with additional properties for the builder operation
     */
    Map<String, Object> getProperties();

    /**
     * Returns the bean context that is used by the builder and the created
     * components. This property is initialized by the builder. From the context
     * returned by this method access to all objects created during the builder
     * operation is possible.
     *
     * @return the bean context of the current builder operation
     */
    BeanContext getBuilderContext();

    /**
     * Sets the bean context used during the builder operation. This method is
     * called by the builder.
     *
     * @param ctx the new bean context used during the builder operation
     */
    void setBuilderContext(BeanContext ctx);

    /**
     * Returns the result of the bean builder. This object contains the
     * <code>{@link BeanStore}</code> objects created during the build. From
     * here access to the components created during the builder operation is
     * possible. This property is initialized by the builder.
     *
     * @return the object with the results of the bean builder
     */
    BeanBuilderResult getBeanBuilderResult();

    /**
     * Sets the result of the bean builder. This method is called by the builder
     * for passing this result object to the caller.
     *
     * @param res the result object of the bean builder
     */
    void setBeanBuilderResult(BeanBuilderResult res);

    /**
     * Returns the root store created during the builder operation. This is a
     * convenience method for callers to directly access the populated bean
     * store. (Access to this object is also possible through the
     * {@link BeanBuilderResult} object.
     *
     * @return the root store of the builder operation
     * @see #getBeanBuilderResult()
     */
    BeanStore getRootStore();

    /**
     * Returns the {@code InvocationHelper} object to be used during this
     * builder operation. This object is used for reflection operation and data
     * type conversions. If no {@code InvocationHelper} is provided, a default
     * one is used by the builder. In most cases the default invocation helper
     * will be sufficient. It is initialized in a way that it can access
     * converters defined for the parent bean stores. If there are specific
     * requirements related to converters, a custom object can be returned here.
     *
     * @return the {@code InvocationHelper} to be used
     */
    InvocationHelper getInvocationHelper();

    /**
     * Returns the <em>auto release</em> flag. If this flag is set to
     * <b>true</b>, at the main window produced by the builder operation a
     * specialized window listener will be registered. When the window is closed
     * this listener ensures that the builder's
     * {@link Builder#release(BuilderData)} method is invoked. This is a very
     * convenient way to automatically free resources obtained during the
     * builder operation and used by the UI. Note that this flag is only
     * evaluated if a window is produced by the builder operation. If the flag
     * is set to <b>false</b> or if no window is generated by the builder
     * operation, the caller is responsible for releasing the results of the
     * builder. This can be done by passing this object to the builder's {@code
     * release()} method. (A reference to the builder is also stored in the
     * {@code builder} property.)
     *
     * @return the <em>auto release</em> flag
     */
    boolean isAutoRelease();

    /**
     * Returns a reference to the {@link Builder} object that generated the
     * builder results. This property is set by the builder.
     *
     * @return a reference to the the {@code Builder} used
     */
    Builder getBuilder();

    /**
     * Sets a reference to the {@code Builder} that performs this builder
     * operation. This method is called by the builder at the beginning of a
     * builder operation. Having a reference to the {@code Builder} in the
     * {@code BuilderData} object can be convenient, especially when the object
     * is to be released.
     *
     * @param bldr the {@code Builder} performing the builder operation
     */
    void setBuilder(Builder bldr);
}

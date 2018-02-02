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

import java.util.Locale;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.gui.builder.Builder;
import net.sf.jguiraffe.gui.builder.action.ActionStore;
import net.sf.jguiraffe.gui.builder.utils.GUISynchronizer;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.resources.Message;
import net.sf.jguiraffe.resources.ResourceManager;
import net.sf.jguiraffe.transform.TransformerContext;

import org.apache.commons.configuration.Configuration;

/**
 * <p>
 * Definition of an interface for accessing application global information.
 * </p>
 * <p>
 * This interface defines a context of the actual running application. This
 * context stores some important information and helper objects that are usually
 * needed by many components in the application. The main application class of
 * this framework will provide access to the global context object so the
 * information stored here can be obtained from everywhere.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ApplicationContext.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ApplicationContext extends TransformerContext
{
    /**
     * Allows to set the current locale. This locale can be used for
     * internationalization purposes, e.g. for resolving resources.
     *
     * @param locale the locale of the actual user
     */
    void setLocale(Locale locale);

    /**
     * Sets the resource manager. All accesses to system resources are performed
     * using this object.
     *
     * @param rm the resource manager to use
     */
    void setResourceManager(ResourceManager rm);

    /**
     * Returns a reference to the configuration data of the running application.
     * All configuration information can be obtained using this object.
     *
     * @return the global configuration object
     */
    Configuration getConfiguration();

    /**
     * Sets the global configuration object of this application.
     *
     * @param configuration the configuration object
     */
    void setConfiguration(Configuration configuration);

    /**
     * Returns the application global <code>BeanContext</code>. This is the
     * main entry point into the <em>dependency injection</em> framework. From
     * this object the globally defined beans can be obtained.
     *
     * @return the global bean context
     */
    BeanContext getBeanContext();

    /**
     * Returns the <code>ClassLoaderProvider</code> to be used.
     *
     * @return the <code>ClassLoaderProvider</code>
     */
    ClassLoaderProvider getClassLoaderProvider();

    /**
     * Sets the <code>ClassLoaderProvider</code> to be used. This object is
     * consulted when a class is to be resolved by name.
     *
     * @param provider the class loader provider to be used
     */
    void setClassLoaderProvider(ClassLoaderProvider provider);

    /**
     * Returns a reference to the object for displaying messages. This object
     * can be used to create message boxes.
     *
     * @return the object for displaying messages
     */
    MessageOutput getMessageOutput();

    /**
     * Sets the message output object to be used by this application.
     *
     * @param msg the new <code>MessageOutput</code> object
     */
    void setMessageOutput(MessageOutput msg);

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
    int messageBox(Object resMsg, Object resTitle, int msgType, int btnType);

    /**
     * Returns the GUI synchronizer. This object is needed for updating the GUI
     * in a different thread than the main event dispatch thread.
     *
     * @return the <code>GUISynchronizer</code> object
     */
    GUISynchronizer getGUISynchronizer();

    /**
     * Returns the application's main window.
     *
     * @return the main window of this application
     */
    Window getMainWindow();

    /**
     * Allows to set the application's main window.
     *
     * @param mainWindow the new main window
     */
    void setMainWindow(Window mainWindow);

    /**
     * Returns a new <code>{@link Builder}</code> instance. This instance can
     * be used for processing a builder definition file. Note that the returned
     * <code>Builder</code> object should only be used by a single thread.
     *
     * @return the new <code>Builder</code> instance
     */
    Builder newBuilder();

    /**
     * Returns an initialized <code>ApplicationBuilderData</code> object that
     * can be used for calling the GUI builder. Most of the properties of the
     * returned object are already set to default values, so only specific
     * settings must be performed.
     *
     * @return an initialized GUI builder parameter object
     */
    ApplicationBuilderData initBuilderData();

    /**
     * Convenience method for looking up a resource specified as group and
     * resource ID.
     *
     * @param groupID the resource group ID
     * @param resID the resource ID
     * @return the found resource
     * @throws java.util.MissingResourceException if the resource cannot be found
     */
    Object getResource(Object groupID, Object resID);

    /**
     * Convenience method for looking up a resource that is specified as a
     * <code>Message</code> object.
     *
     * @param msg the resource definition (must not be <b>null</b>)
     * @return the found resource
     * @throws java.util.MissingResourceException if the resource cannot be found
     * @throws IllegalArgumentException if then message is undefined
     */
    Object getResource(Message msg);

    /**
     * Convenience method for looking up a resource. The passed in object is
     * checked to be an instance of
     * {@link net.sf.jguiraffe.resources.Message Message}. If
     * this is the case, the resource group and the resource ID are extracted
     * from this object. Otherwise the passed in object is interpreted as
     * resource ID and the default resource group will be used.
     *
     * @param resID the resource ID
     * @return the found resource
     * @throws java.util.MissingResourceException if the resource cannot be found
     */
    Object getResource(Object resID);

    /**
     * Convenience method for looking up the text of a resource specified as
     * group and resource ID.
     *
     * @param groupID the resource group ID
     * @param resID the resource ID
     * @return the found resource text
     * @throws java.util.MissingResourceException if the resource cannot be found
     */
    String getResourceText(Object groupID, Object resID);

    /**
     * Convenience method for looking up the text of a resource specified as a
     * <code>Message</code> object.
     *
     * @param msg defines the resource (must not be <b>null</b>)
     * @return the found resource
     * @throws java.util.MissingResourceException if the resource cannot be found
     * @throws IllegalArgumentException if the message is undefined
     */
    String getResourceText(Message msg);

    /**
     * Convenience method for looking up the text of a specified resource. This
     * method works analogous to <code>getResourceText(Object)</code>,
     * especially the passed in object can be an instance of
     * <code>{@link net.sf.jguiraffe.resources.Message Message}</code>.
     *
     * @param resID defines the requested resource
     * @return the found resource
     * @throws java.util.MissingResourceException if the resource cannot be found
     */
    String getResourceText(Object resID);

    /**
     * Returns the application's <code>ActionStore</code>.
     *
     * @return the application's action store
     */
    ActionStore getActionStore();

    /**
     * Sets the application's <code>ActionStore</code>. This object contains
     * the definitions for all top level actions known to the application.
     *
     * @param actionStore the new action store
     */
    void setActionStore(ActionStore actionStore);

    /**
     * Sets the value of a typed property. With this method properties can be
     * set that can later be queried using the {@link #getTypedProperty(Class)}
     * method. This provides type-safe access to arbitrary properties. A use
     * case for this method is the storage of application-global data in the
     * {@code ApplicationContext}. All components that have access to an {@code
     * ApplicationContext} can also obtain or manipulate the data stored in
     * these properties. Because the {@code ApplicationContext} can be accessed
     * from multiple threads (e.g. the event dispatch thread and the worker
     * thread used by the application to execute background tasks) an
     * implementation should ensure a proper synchronization. A typed property
     * can be cleared by passing the value <b>null</b>. The property class must
     * not be <b>null</b>, otherwise an exception is thrown.
     *
     * @param <T> the type of the property
     * @param propCls the property class
     * @param value the new value for this property
     * @throws IllegalArgumentException if the property class is <b>null</b>
     */
    <T> void setTypedProperty(Class<T> propCls, T value);
}

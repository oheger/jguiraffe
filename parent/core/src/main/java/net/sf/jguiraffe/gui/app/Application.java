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
package net.sf.jguiraffe.gui.app;

import java.awt.Rectangle;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.event.EventListenerList;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.MutableBeanStore;
import net.sf.jguiraffe.di.impl.DefaultBeanContext;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.gui.builder.BeanBuilder;
import net.sf.jguiraffe.gui.builder.BeanBuilderFactory;
import net.sf.jguiraffe.gui.builder.BeanBuilderResult;
import net.sf.jguiraffe.gui.builder.Builder;
import net.sf.jguiraffe.gui.builder.BuilderException;
import net.sf.jguiraffe.gui.builder.impl.JellyBeanBuilderFactory;
import net.sf.jguiraffe.gui.builder.utils.GUISynchronizer;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.cmd.Command;
import net.sf.jguiraffe.gui.cmd.CommandQueue;
import net.sf.jguiraffe.locators.ClassPathLocator;
import net.sf.jguiraffe.locators.Locator;
import net.sf.jguiraffe.locators.LocatorConverter;
import net.sf.jguiraffe.locators.LocatorUtils;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.PropertyConverter;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.BeanHelper;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * The main startup class of the GUI application framework.
 * </p>
 * <p>
 * With this class a Java GUI application can be started. This works as follows:
 * </p>
 * <p>
 * <ol>
 * <li>The class uses the values of system properties to find out the name of
 * the application's configuration file.</li>
 * <li>This configuration file is loaded using commons-configuration.</li>
 * <li>From properties defined in the application's configuration the
 * {@code ApplicationContext} is created and initialized. This includes
 * setting up a resource manager.</li>
 * <li>The name of the application's main GUI builder script is also determined
 * by configuration properties. This script is executed, and the resulting main
 * window is made visible.</li>
 * </ol>
 * </p>
 * <p>
 * Per default the application's configuration file is expected to be located in
 * the class path and has the name <em>config.xml</em>. This can be changed
 * using system properties: The property
 * {@code net.sf.jguiraffe.configName} allows to change the name of the
 * configuration file. If defined, a file with this name will be searched in the
 * class path. If the property {@code net.sf.jguiraffe.configURL} is
 * provided, the class tries to load this file directly from this URL.
 * </p>
 * <p>
 * A bunch of configuration properties is evaluated by this class to perform the
 * correct setup. All of these must be placed in a section called
 * {@code framework}. The following table lists the available properties:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Property</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">appctx</td>
 * <td>In this section some properties of the application context are defined:
 * <dl>
 * <dt>{@code locale}</dt>
 * <dd>Here the locale to be set at startup can be specified. If the property is
 * missing, the system's default locale will be used.</dd>
 * <dt>{@code defaultResourceGroup}</dt>
 * <dd>Allows to define a default resource group that is used by the resource
 * manager when no specific resource group is specified.</dd>
 * </dl>
 * </td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">builder</td>
 * <td>This section contains some setting related to the builders used for
 * processing bean definitions and GUI scripts. All properties in this section
 * are optional - meaningful default values are applied if a value is not set.
 * The following sub elements are supported:
 * <dl>
 * <dt>{@code beanBuilderFactory}</dt>
 * <dd>Specifies the full qualified name of the {@link BeanBuilderFactory}
 * implementation that is used to obtain bean builder instances. Here the
 * implementation class and additional initialization properties can be
 * specified.</dd>
 * <dt>{@code beandefinitions}</dt>
 * <dd>In this subsection an arbitrary number of {@code beandefinition}
 * elements can be specified. Each {@code beandefinition} element points to
 * a script with bean definitions. These scripts will be processed by the
 * default bean builder.</dd>
 * <dt>{@code menuIcon}</dt>
 * <dd>An optional boolean flag that determines whether menu items should be
 * rendered with an icon if one is defined. Note that this may not work on all
 * platforms. The default value for this flag is <b>false</b>.</dd>
 * <dt>{@code toolbarText}</dt>
 * <dd>An optional boolean flag that determines whether toolbar buttons should
 * display their text. Note that this may not be supported by all platforms. The
 * default value of this flag is <b>false</b>.</dd>
 * <dt>{@code mainScript}</dt>
 * <dd>With this property the name of the main builder script can be specified.
 * If defined, the script will be executed using the application's builder. If
 * this results in a window, this window will be displayed.</dd></td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">frame</td>
 * <td>In this section some properties of the application's main window can be
 * defined, especially its location and size. The idea is that this information
 * will be stored in a user configuration so that the last settings can be set
 * again on next application start. The following properties can be defined in
 * this section:
 * <dl>
 * <dt>{@code xpos}</dt>
 * <dd>Defines the x position of the main window.</dd>
 * <dt>{@code ypos}</dt>
 * <dd>Defines the y position of the main window.</dd>
 * <dt>{@code width}</dt>
 * <dd>Defines the width position of the main window.</dd>
 * <dt>{@code height}</dt>
 * <dd>Defines the height position of the main window.</dd></td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">storeuserconfig</td>
 * <td>A boolean property that determines whether the user specific
 * configuration should be stored when the application terminates. Defaults to
 * <b>false</b>.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">userconfigname</td>
 * <td>Defines the name of the user configuration in the configuration
 * definition file. (The configuration definition file can include an arbitrary
 * number of configuration sources. To determine, which of these is the user
 * configuration, its name must be specified. If no name is specified, the
 * default <em>userConfig</em> will be used.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * A major part of the configuration of the application is defined in terms of
 * bean definitions. Here many helper classes used by the application are
 * defined (e.g. the resource manager, the message output object, the GUI
 * builder, and many more). At start up, the application creates a
 * {@link BeanContext} that provides access to these beans and creates the
 * required instances. There is a default bean definition file (
 * <em>defaultbeans.jelly</em>) with default bean definitions for all available
 * helper classes. It is loaded first. Concrete applications can override some
 * or all of these beans. This is a powerful means of customizing the
 * application.
 * </p>
 * <p>
 * To override bean definitions, use the
 * {@code framework.builder.beandefinitions} section in the application's
 * main configuration file (see above). In this section the names of an
 * arbitrary number of bean definition files can be specified (the files will be
 * loaded from the class path). Using the predefined names for the default beans
 * in these scripts causes the beans to be replaced by the custom ones. Have a
 * look at the <em>defaultbeans.jelly</em> script for more information; in this
 * file all available default beans are listed with a documentation for each.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Application.java 211 2012-07-10 19:49:13Z oheger $
 */
public class Application
{
    /** Constant for the system property with the URL to the configuration file. */
    public static final String PROP_CONFIG_URL = "net.sf.jguiraffe.configURL";

    /**
     * Constant for the system property with the resource name of the
     * configuration file.
     */
    public static final String PROP_CONFIG_NAME = "net.sf.jguiraffe.configName";

    /** Constant for the default name of the configuration file. */
    public static final String DEF_CONFIG_NAME = "config.xml";

    /** Constant for the prefix for bean definitions used by the framework. */
    public static final String BEAN_PREFIX = "jguiraffe.";

    /** Constant for the name of the bean with the global configuration. */
    public static final String BEAN_CONFIGURATION = BEAN_PREFIX
            + "configuration";

    /** Constant for the name of the bean with the current application instance. */
    public static final String BEAN_APPLICATION = BEAN_PREFIX + "application";

    /** Constant for the name of the bean with the application context. */
    public static final String BEAN_APPLICATION_CONTEXT = BEAN_PREFIX
            + "applicationContext";

    /** Constant for the name of the bean with the global bean context. */
    public static final String BEAN_GLOBAL_CONTEXT = BEAN_PREFIX
            + "globalContext";

    /** Constant for the name of the builder bean. */
    public static final String BEAN_BUILDER = BEAN_PREFIX + "builder";

    /** Constant for the name of the command queue bean. */
    public static final String BEAN_COMMAND_QUEUE = BEAN_PREFIX
            + "commandQueue";

    /** Constant for the name of the GUI synchronizer bean. */
    public static final String BEAN_GUI_SYNCHRONIZER = BEAN_PREFIX
            + "guiSynchronizer";

    /** Constant for the name of the binding strategy bean. */
    public static final String BEAN_BINDING_STRATEGY = BEAN_PREFIX
            + "bindingStrategy";

    /** Constant for the name of the bean with the locale. */
    public static final String BEAN_LOCALE = BEAN_PREFIX + "locale";

    /** Constant for the name of the bean with the default resource group. */
    public static final String BEAN_DEF_RES_GROUP = BEAN_PREFIX
            + "defaultResourceGroup";

    /** Constant for the name of the bean for the class loader provider. */
    public static final String BEAN_CLASS_LOADER_PROVIDER = BEAN_PREFIX
            + "classLoaderProvider";

    /** Constant for the configuration section for the framework. */
    public static final String CONFIG_SECTION = "framework.";

    /** Constant for the application context property in the config file. */
    public static final String PROP_APPCTX = CONFIG_SECTION + "appctx";

    /** Constant for the locale property in the config file. */
    public static final String PROP_LOCALE = PROP_APPCTX + ".locale";

    /** Constant for the default resource group property in the config file. */
    public static final String PROP_DEFRESGROUP = PROP_APPCTX
            + ".defaultResourceGroup";

    /** Constant for the section with the builder information. */
    public static final String BUILDER_SECTION = CONFIG_SECTION + "builder.";

    /** Constant for the builder factory property. */
    public static final String PROP_BUILDER_FACTORY = BUILDER_SECTION
            + "factory";

    /** Constant for the bean builder factory property. */
    public static final String PROP_BEAN_BUILDER_FACTORY = BUILDER_SECTION
            + "beanBuilderFactory";

    /** Constant for the bean definitions property. */
    public static final String PROP_BEAN_DEFS = BUILDER_SECTION
            + "beandefinitions.beandefinition";

    /** Constant for the builder menu icon property. */
    public static final String PROP_BUILDER_MENU_ICON = BUILDER_SECTION
            + "menuIcon";

    /** Constant for the builder toolbar text property. */
    public static final String PROP_BUILDER_TOOLBAR_TEXT = BUILDER_SECTION
            + "toolbarText";

    /** Constant for the builder main script property. */
    public static final String PROP_BUILDER_MAIN_SCRIPT = BUILDER_SECTION
            + "mainScript";

    /** Constant for the frame section in the configuration file. */
    public static final String FRAME_SECTION = CONFIG_SECTION + "frame.";

    /** Constant for the xpos property in the config file. */
    public static final String PROP_XPOS = FRAME_SECTION + "xpos";

    /** Constant for the ypos property in the config file. */
    public static final String PROP_YPOS = FRAME_SECTION + "ypos";

    /** Constant for the width property in the config file. */
    public static final String PROP_WIDTH = FRAME_SECTION + "width";

    /** Constant for the height property in the config file. */
    public static final String PROP_HEIGHT = FRAME_SECTION + "height";

    /** Constant for the storeuserconfig property in the config file. */
    public static final String PROP_USRCONF = CONFIG_SECTION
            + "storeuserconfig";

    /** Constant for the userconfigname property in the config file. */
    public static final String PROP_USRCONFNAME = CONFIG_SECTION
            + "userconfigname";

    /** Constant for the name of the user configuration. */
    public static final String USRCONF_NAME = "userConfig";

    /**
     * Constant for the name of the class loader which loaded the application
     * class. This class loader is set as the default class loader at the
     * {@code ClassLoaderProvider} created at startup.
     *
     * @since 1.2
     */
    public static final String CLASS_LOADER = BEAN_PREFIX
            + "Application.classLoader";

    /** Constant for the default bean builder factory class. */
    private static final Class<?> DEF_BEAN_BUILDER_FACTORY_CLS = JellyBeanBuilderFactory.class;

    /** Constant for the script with the default bean definitions. */
    private static final Locator DEFAULT_BEANS = ClassPathLocator.getInstance(
            "defaultbeans.jelly");

    /**
     * Constant for the locator to the script with platform-specific bean
     * definitions.
     */
    private static final Locator PLATFORM_BEANS = ClassPathLocator
            .getInstance("platformbeans.jelly");

    /** The logger to use. */
    protected final Log log = LogFactory.getLog(Application.class);

    /** Stores the URL for the configuration file. */
    private String configURL;

    /** Stores the resource name for the configuration file. */
    private String configResourceName;

    /** Stores a reference to the application context. */
    private ApplicationContext applicationContext;

    /** Stores the bean builder factory. */
    private BeanBuilderFactory beanBuilderFactory;

    /** Stores a reference to the command queue. */
    private CommandQueue cmdQueue;

    /** Stores the registered shutdown listeners. */
    private final EventListenerList shutdownListeners;

    /** A list with the bean builder results created during initialization.*/
    private final Collection<BeanBuilderResult> beanBuilderResults;

    /**
     * The default exit handler. This instance is returned if no specific exit
     * handler has been set. It calls {@code System.exit()} with the current exit
     * code.
     */
    private final Runnable defaultExitHandler = new Runnable()
    {
        public void run()
        {
            System.exit(getExitCode());
        }
    };

    /** The current exit handler of this application. */
    private final AtomicReference<Runnable> exitHandler;

    /** The exit code of this application. */
    private int exitCode;

    /**
     * Creates a new instance of {@code Application}.
     */
    public Application()
    {
        shutdownListeners = new EventListenerList();
        beanBuilderResults = new ArrayList<BeanBuilderResult>();
        exitHandler = new AtomicReference<Runnable>();
    }

    /**
     * Returns the URL from which the configuration file is to be loaded.
     *
     * @return the URL to the configuration file
     */
    public String getConfigURL()
    {
        return configURL;
    }

    /**
     * Sets the URL to the configuration file. The configuration file can either
     * be loaded directly from a URL or as a resource from the class path. If
     * this property is defined, it is loaded directly from the URL specified
     * here.
     *
     * @param configURL the URL to the configuration file
     */
    public void setConfigURL(String configURL)
    {
        this.configURL = configURL;
    }

    /**
     * Returns the resource name of the configuration file.
     *
     * @return the resource name of the configuration file
     */
    public String getConfigResourceName()
    {
        return configResourceName;
    }

    /**
     * Sets the resource name under which the configuration file can be loaded.
     * If no configuration URL is provided, this property is used to look up the
     * configuration file from the class path.
     *
     * @param configResourceName the resource name of the configuration file
     */
    public void setConfigResourceName(String configResourceName)
    {
        this.configResourceName = configResourceName;
    }

    /**
     * Returns a reference to the actual application context.
     *
     * @return the {@code ApplicationContext}
     */
    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    /**
     * Sets the application context.
     *
     * @param context the new context
     */
    public void setApplicationContext(ApplicationContext context)
    {
        applicationContext = context;
    }

    /**
     * Returns the {@code BeanBuilderFactory} for obtaining a bean
     * builder. This method can be used when a bean definition file is to be
     * processed.
     *
     * @return the {@code BeanBuilderFactory}
     */
    public BeanBuilderFactory getBeanBuilderFactory()
    {
        return beanBuilderFactory;
    }

    /**
     * Allows to set the {@code BeanBuilderFactory}. Normally it is not
     * necessary to set this property. When the application is initialized it
     * creates a default factory.
     *
     * @param beanBuilderFactory the new {@code BeanBuilderFactory}
     */
    public void setBeanBuilderFactory(BeanBuilderFactory beanBuilderFactory)
    {
        this.beanBuilderFactory = beanBuilderFactory;
    }

    /**
     * Registers the specified object as a shutdown listeners.
     *
     * @param l the listener to register
     */
    public void addShutdownListener(ApplicationShutdownListener l)
    {
        shutdownListeners.add(ApplicationShutdownListener.class, l);
    }

    /**
     * Removes the specified shutdown listener.
     *
     * @param l the listener to remove
     */
    public void removeShutdownListener(ApplicationShutdownListener l)
    {
        shutdownListeners.remove(ApplicationShutdownListener.class, l);
    }

    /**
     * Helper method for locating a resource. The resource can either be
     * specified by a full URL, in which case it is directly loaded, or by a
     * resource name. In the latter case the class loader is used to find the
     * resource.
     *
     * @param url a URL to the resource; this can be a full qualified URL or the
     * name of a file (either relative or absolute)
     * @param name the resource name
     * @return the URL to the resource or <b>null </b> if it cannot be found
     * @deprecated This method does not make sense in the public interface of
     * this class. It will be removed in later versions. Use corresponding
     * functionality from the {@code LocatorUtils} class instead.
     */
    @Deprecated
    public static URL resolveResourceURL(String url, String name)
    {
        return LocatorUtils.locate(url, name);
    }

    /**
     * Tries to the set a reference to the global {@code Application}
     * object in the target object. If the target object implements the
     * {@code ApplicationClient} interface, the reference can be set.
     *
     * @param target the target object
     * @param ref the application reference to set
     */
    public static void setApplicationReference(Object target, Application ref)
    {
        if (target instanceof ApplicationClient)
        {
            ((ApplicationClient) target).setApplication(ref);
        }
    }

    /**
     * <p>
     * Returns the configuration object with user specific settings. This
     * configuration can be used to store personalization settings that override
     * the default configuration. For this method to work the system's main
     * configuration definition file must include an entry for the user
     * configuration that is identified by its name. Per default the name is
     * &quot;userConfig&quot;, but can be altered with the
     * {@code userconfigname} property. The following example fragment
     * from the application's configuration definition file demonstrates how the
     * user configuration should be declared:
     * </p>
     * <p>
     *
     * <pre>
     * &lt;configuration&gt;
     *   ...
     *   &lt;xml fileName="${user.home}/myAppConfig.xml" config-name="userConfig"
     *     config-optional="true" config-forceCreate="true"/&gt;
     *   &lt;!-- Further configuration sources to load --&gt;
     *   ...
     * </pre>
     *
     * </p>
     * <p>
     * In this example the attributes starting with {@code config-} are
     * of special importance. With {@code config-name} the
     * configuration's name is specified, which is necessary for the framework
     * to retrieve the correct user configuration. {@code config-optional}
     * declares this configuration source as optional. This means that it won't
     * cause an error when this source cannot be loaded (which will probably be
     * the case when a user starts this application for the first time). The
     * {@code config-forceCreate} attribute finally tells the
     * configuration framework to create an empty configuration when loading of
     * the configuration file fails. This will cause the configuration to be
     * automatically created for the new user. If the user customizes the
     * application, these settings can be stored in this configuration. When the
     * application terminates it checks the value of the
     * {@code storeuserconfig} property. If this is set to <b>true</b>,
     * the user configuration will be stored. So the next time the application
     * starts it will be found there and override the values in all other
     * configuration sources.
     * </p>
     *
     * @return the configuration with user specific settings
     * @throws ApplicationRuntimeException if the user configuration cannot be
     * obtained
     */
    public Configuration getUserConfiguration()
            throws ApplicationRuntimeException
    {
        Configuration userConfig = null;

        if (getApplicationContext().getConfiguration() instanceof CombinedConfiguration)
        {
            CombinedConfiguration cconf = (CombinedConfiguration) getApplicationContext()
                    .getConfiguration();
            String userConfigName = cconf.getString(PROP_USRCONFNAME,
                    USRCONF_NAME);
            userConfig = cconf.getConfiguration(userConfigName);
        }

        if (userConfig == null)
        {
            throw new ApplicationRuntimeException(
                    "Cannot obtain user configuration!");
        }
        return userConfig;
    }

    /**
     * Stores the configuration with user specific settings. This method obtains
     * the user configuration by calling the
     * {@link #getUserConfiguration()}
     * method. It expects that the user configuration implements the
     * {@code FileConfiguration} interface. If a different configuration
     * type is used as user configuration, this method should also be adapted.
     *
     * @throws ApplicationException if an error occurs
     */
    public void saveUserConfiguration() throws ApplicationException
    {
        Configuration conf = getUserConfiguration();
        if (conf instanceof FileConfiguration)
        {
            FileConfiguration fconf = (FileConfiguration) conf;
            try
            {
                fconf.save();
                if (log.isInfoEnabled())
                {
                    log.info("Saved user configuration to " + fconf.getURL());
                }
            }
            catch (ConfigurationException cex)
            {
                throw new ApplicationException(
                        "Could not save user configuration!", cex);
            }
        }

        else
        {
            throw new ApplicationException(
                    "User configuration is no file-based configuration!");
        }
    }

    /**
     * Creates and initializes the application context. Loads the configuration,
     * too.
     *
     * @return the application context
     * @throws ApplicationRuntimeException if an error occurs during
     * initialization
     */
    protected ApplicationContext createApplicationContext()
    {
        HierarchicalConfiguration config = createConfiguration();
        setBeanBuilderFactory(createBeanBuilderFactory(config));
        BeanContext beanContext = initBeans(config);
        return (ApplicationContext) beanContext
                .getBean(BEAN_APPLICATION_CONTEXT);
    }

    /**
     * Helper method for extracting the startup locale from the application's
     * configuration data.
     *
     * @param config the configuration data
     * @return the locale to use; if not defined in the configuration, the
     * system's default locale will be returned
     * @throws ApplicationRuntimeException if the locale is invalid
     */
    Locale parseLocale(Configuration config)
    {
        if (config.containsKey(PROP_LOCALE))
        {
            try
            {
                return PropertyConverter.toLocale(config
                        .getProperty(PROP_LOCALE));
            }
            catch (ConversionException cex)
            {
                throw new ApplicationRuntimeException(
                        "Error when parsing locale", cex);
            }
        }

        return Locale.getDefault();
    }

    /**
     * Returns the URL to the configuration file. This URL is determined by the
     * properties {@code configURL} and {@code configResourceName}.
     *
     * @return the URL to the application's main configuration file
     * @throws ApplicationRuntimeException if the configuration file cannot be
     * located
     */
    protected URL fetchConfigURL()
    {
        URL url =
                LocatorUtils.locate(getConfigURL(), getConfigResourceName(),
                        getClass().getClassLoader());
        if (url == null)
        {
            throw new ApplicationRuntimeException(
                    "Cannot find configuration file!");
        }
        return url;
    }

    /**
     * Creates the configuration for this application. This method calls
     * {@code fetchConfigURL()} to determine the URL to the main
     * configuration file. Then this file is loaded with commons-configuration.
     *
     * @return the configuration to use
     * @throws ApplicationRuntimeException if the configuration file cannot be
     *         located
     */
    protected HierarchicalConfiguration createConfiguration()
    {
        return createConfiguration(fetchConfigURL());
    }

    /**
     * Reads the application's configuration from the specified URL. The
     * {@code DefaultConfigurationBuilder} of
     * <em>Commons Configuration</em> is used for reading the configuration.
     * Occurring exceptions are re-thrown as runtime exceptions.
     *
     * @param configURL the configuration URL
     * @return the application's main configuration
     * @throws ApplicationRuntimeException if the configuration cannot be loaded
     */
    protected HierarchicalConfiguration createConfiguration(URL configURL)
    {
        log.info("Loading configuration from " + configURL);
        try
        {
            DefaultConfigurationBuilder factory = new DefaultConfigurationBuilder();
            factory.setURL(configURL);
            return factory.getConfiguration(true);
        }
        catch (ConfigurationException cex)
        {
            throw new ApplicationRuntimeException(
                    "Error when loading configuration!", cex);
        }
    }

    /**
     * Creates the root bean store. This bean store will contain the fundamental
     * bean definitions required by the framework. Because it is at the top
     * level the contained bean definitions can be used or even overridden by
     * child stores. This implementation creates a default store and populates
     * it with the configuration and the application instance itself.
     *
     * @param config the configuration object
     * @return the initialized root bean store
     */
    protected MutableBeanStore createRootStore(Configuration config)
    {
        DefaultBeanStore store = new DefaultBeanStore();
        addBean(store, BEAN_CONFIGURATION, config);
        addBean(store, BEAN_APPLICATION, this);
        addBean(store, BEAN_LOCALE, parseLocale(config));
        addBean(store, BEAN_DEF_RES_GROUP, config.getString(PROP_DEFRESGROUP));
        return store;
    }

    /**
     * Creates the factory for the bean builder. This method is called during
     * initialization phase. It tries to obtain the factory implementation from
     * the main configuration file. If this fails, a default factory instance
     * will be returned.
     *
     * @param config the main configuration
     * @return the {@code BeanBuilderFactory} to be used
     */
    protected BeanBuilderFactory createBeanBuilderFactory(Configuration config)
    {
        BeanDeclaration decl = new XMLBeanDeclaration(
                (HierarchicalConfiguration) config, PROP_BEAN_BUILDER_FACTORY,
                true);
        return (BeanBuilderFactory) BeanHelper.createBean(decl,
                DEF_BEAN_BUILDER_FACTORY_CLS);
    }

    /**
     * Initializes the application's bean definitions. This implementation will
     * first process the framework-internal bean definition file, which defines
     * the standard beans. After that {@code findBeanDefinitions()} is
     * called for obtaining a list of additional definition files to be
     * evaluated. Finally a bean context is created allowing access to all beans
     * defined this way. This algorithm allows concrete applications to define
     * their own beans in an easy way and also to override standard beans used
     * by the framework.
     *
     * @param config the main configuration source
     * @return the global bean context
     */
    protected BeanContext initBeans(Configuration config)
    {
        // Initialize global bean context
        BeanContext context = new DefaultBeanContext();
        MutableBeanStore rootStore = createRootStore(config);
        addBean(rootStore, BEAN_GLOBAL_CONTEXT, context);
        context.setDefaultBeanStore(rootStore);

        // Read default bean definitions
        beanBuilderResults.add(readBeanDefinition(DEFAULT_BEANS, rootStore,
                null));

        // Initialize the class loader provider
        ClassLoaderProvider clp = (ClassLoaderProvider) context
                .getBean(BEAN_CLASS_LOADER_PROVIDER);
        ClassLoaderProvider clpInit = initClassLoaderProvider(clp);
        if (clp != clpInit)
        {
            // the init method replaces the class loader provider
            addBean(rootStore, BEAN_CLASS_LOADER_PROVIDER, clpInit);
        }

        // Now process custom bean definitions
        processBeanDefinitions(findBeanDefinitions(config, context), context,
                clpInit);

        return context;
    }

    /**
     * Initializes the global {@code ClassLoaderProvider}. This method is called
     * by {@link #initBeans(Configuration)} after the default beans have been
     * loaded. The {@code ClassLoaderProvider} passed to this method was
     * obtained from the default beans. A derived class can override this method
     * to perform specific initialization of the passed in {@code
     * ClassLoaderProvider}. It can even create a completely new object (the
     * {@code ClassLoaderProvider} returned by this method will become the
     * global {@code ClassLoaderProvider}; it need not be the same object as was
     * passed to this method). This base implementation registers the
     * class loader which has loaded the concrete {@code Application} sub
     * class and makes it the default class loader.
     *
     * @param clp the {@code ClassLoaderProvider} as obtained from the default
     *        beans
     * @return the new global {@code ClassLoaderProvider} (must never be
     *         <b>null</b>)
     */
    protected ClassLoaderProvider initClassLoaderProvider(
            ClassLoaderProvider clp)
    {
        clp.registerClassLoader(CLASS_LOADER, getClass().getClassLoader());
        clp.setDefaultClassLoaderName(CLASS_LOADER);
        return clp;
    }

    /**
     * A convenience method for processing a file with bean definitions. A new
     * bean builder will be created, which processes the passed in script. The
     * defined beans are stored in the specified root store. Occurring builder
     * exceptions are re-thrown as runtime exceptions.
     *
     * @param script defines the script with the bean definitions
     * @param rootStore the root store for storing the results
     * @param loaderProvider the optional class loader provider
     * @return the result object returned by the builder
     * @throws IllegalArgumentException if required parameters are missing
     * @throws ApplicationRuntimeException if an error occurs
     */
    protected BeanBuilderResult readBeanDefinition(Locator script,
            MutableBeanStore rootStore, ClassLoaderProvider loaderProvider)
    {
        try
        {
            return getBeanBuilderFactory().getBeanBuilder().build(script,
                    rootStore, loaderProvider);
        }
        catch (BuilderException bex)
        {
            throw new ApplicationRuntimeException(
                    "Error when processing script " + script, bex);
        }
    }

    /**
     * Returns a collection with additional bean definition files to process.
     *
     * @param config the main configuration source
     * @return a list with locators to bean definition files to be processed
     *         (can be <b>null</b>)
     * @deprecated This method is replaced by
     *             {@link #findBeanDefinitions(Configuration, BeanContext)}. It
     *             is still called during application initialization to keep
     *             backwards compatibility, but this base implementation simply
     *             returns an empty collection.
     */
    @Deprecated
    protected Collection<Locator> findBeanDefinitions(Configuration config)
    {
        return new ArrayList<Locator>(0);
    }

    /**
     * Returns a collection with additional bean definition files to process.
     * This method is called when the application context is created. All files
     * contained in the returned list will be processed by the bean builder.
     * This base implementation obtains the value(s) of the
     * {@code framework.builder.beandefinitions.beandefinition} configuration
     * property. The values are interpreted as textual representations of
     * {@link Locator} objects which can be converted using the
     * {@link LocatorConverter} class. Strings that do not contain a locator
     * type prefix (e.g. {@code classpath:} or {@code url:} are expected to be
     * names of bean definition files, which can be read from the class path. In
     * addition, the {@link #getPlatformBeansLocator()} method is called to
     * obtain a {@code Locator} for a file with platform-specific bean
     * declarations; if this method returns a non-<b>null</b> value, this
     * {@code Locator} is added to the list, too. If an application has
     * different requirements for specifying additional bean definition files,
     * this method can be overridden.
     *
     * @param config the main configuration source
     * @param beanCtx the current {@code BeanContext}
     * @return a list with locators to bean definition files to be processed
     *         (can be <b>null</b>)
     * @since 1.2
     */
    protected Collection<Locator> findBeanDefinitions(Configuration config,
            BeanContext beanCtx)
    {
        List<?> defLocators = config.getList(PROP_BEAN_DEFS);
        // for backwards compatibility reasons call old method
        Collection<Locator> locs = findBeanDefinitions(config);
        if (locs == null)
        {
            locs = Collections.emptyList();
        }

        Collection<Locator> result =
                new ArrayList<Locator>(defLocators.size() + locs.size() + 1);
        Locator platformLocator = getPlatformBeansLocator();
        if (platformLocator != null)
        {
            result.add(platformLocator);
        }
        result.addAll(locs);
        LocatorConverter converter = null;

        for (Iterator<?> it = defLocators.iterator(); it.hasNext();)
        {
            Object locatorRep = it.next();
            String strLocatorRep = String.valueOf(locatorRep);
            if (strLocatorRep.indexOf(LocatorConverter.PREFIX_SEPARATOR) >= 0)
            {
                if (converter == null)
                {
                    converter =
                            new LocatorConverter(
                                    beanCtx.getBean(ClassLoaderProvider.class));
                }
                result.add((Locator) converter.convert(Locator.class,
                        locatorRep));
            }
            else
            {
                result.add(cpLocator(strLocatorRep));
            }
        }
        return result;
    }

    /**
     * Returns a {@code Locator} object pointing to a file with bean
     * declarations related to the platform or UI toolkit. This method is called
     * when additional bean declaration files to be loaded during application
     * initialization are detected. The base implementation returns a locator
     * pointing to a class path resource with the name
     * {@code platformbeans.jelly}. This file contains declarations for beans
     * like the platform-specific component manager, window manager, etc. In a
     * standard JGUIraffe application a single file with this name exists which
     * contains definitions compatible to the supported platform. A derived
     * class may override this method and return a different {@code Locator}.
     * Result can be <b>null</b>, then no additional bean declaration file is
     * loaded.
     *
     * @return a {@code Locator} pointing to platform-specific bean declarations
     * @since 1.3
     */
    protected Locator getPlatformBeansLocator()
    {
        return PLATFORM_BEANS;
    }

    /**
     * Processes the given bean definition file. A new bean store is created as
     * a child of the bean context's default store. This store is passed to the
     * bean builder for being populated. Finally it is set as the new default
     * store in the bean context. Occurring exceptions will be re-directed as
     * runtime exceptions.
     *
     * @param script the script with the bean definitions
     * @param context the global context
     * @param clp the class loader provider
     * @throws ApplicationRuntimeException if an error occurs
     */
    void processBeanDefinition(Locator script, BeanContext context,
            ClassLoaderProvider clp)
    {
        DefaultBeanStore store = new DefaultBeanStore();
        beanBuilderResults.add(readBeanDefinition(script, store, clp));
        ApplicationContextImpl.installBeanStore(context, store);
    }

    /**
     * Processes a list of bean definitions.
     *
     * @param defs the list with the bean definitions (can be <b>null</b>)
     * @param context the global context
     * @param clp the class loader provider
     */
    void processBeanDefinitions(Collection<Locator> defs, BeanContext context,
            ClassLoaderProvider clp)
    {
        if (defs != null)
        {
            for (Locator l : defs)
            {
                processBeanDefinition(l, context, clp);
            }
        }
    }

    /**
     * Returns a collection with the {@code BeanBuilderResult} objects that were
     * created during initialization of the application. These objects must be
     * released on shutdown.
     *
     * @return a collection with the builder results to release on shutdown
     */
    Collection<BeanBuilderResult> getIninitializedBuilderResults()
    {
        return beanBuilderResults;
    }

    /**
     * Initializes the application's main GUI. This method checks whether a
     * script for the main GUI is defined in the application's configuration. If
     * this is the case, the script is executed, and the resulting main window
     * is stored.
     *
     * @param appCtx the application context
     * @throws ApplicationRuntimeException if an error occurs
     */
    protected void initGUI(ApplicationContext appCtx)
    {
        Configuration config = appCtx.getConfiguration();
        Locator scriptLocator = locatorForMainScript(config);
        if (scriptLocator != null)
        {
            try
            {
                Builder builder = appCtx.newBuilder();
                Window mainWindow = builder.buildWindow(scriptLocator, appCtx
                        .initBuilderData());
                if (mainWindow != null)
                {
                    appCtx.setMainWindow(mainWindow);
                    initMainWindow(mainWindow, config);
                }
            }
            catch (BuilderException bex)
            {
                throw new ApplicationRuntimeException(bex);
            }
        }
    }

    /**
     * Returns the locator object for the application's main build script. This
     * implementation checks if a script is specified in the configuration. If
     * this is the case, a class path locator will be returned. Derived classes
     * can use different mechanisms to determine the build script. A return
     * value of <b>null</b> means that no script should be executed.
     *
     * @param config the configuration
     * @return the locator for the main builder script
     */
    protected Locator locatorForMainScript(Configuration config)
    {
        if (config.containsKey(PROP_BUILDER_MAIN_SCRIPT))
        {
            return cpLocator(config.getString(PROP_BUILDER_MAIN_SCRIPT));
        }
        else
        {
            return null;
        }
    }

    /**
     * Initializes the application's main window object. This method is called
     * after the main window has been created by the main build script. Here the
     * passed in configuration object can be used to initialize properties on
     * this object. This implementation deals with the window's bounds, which
     * can be initialized from the configuration.
     *
     * @param window the new main window object
     * @param config the actual configuration
     */
    protected void initMainWindow(Window window, Configuration config)
    {
        if (config.containsKey(PROP_XPOS) || config.containsKey(PROP_YPOS)
                || config.containsKey(PROP_WIDTH)
                || config.containsKey(PROP_HEIGHT))
        {
            // only do something if necessary
            Rectangle bnds = new Rectangle(window.getXPos(), window.getYPos(),
                    window.getWidth(), window.getHeight());
            bnds.setLocation(config.getInt(PROP_XPOS, (int) bnds.getX()),
                    config.getInt(PROP_YPOS, (int) bnds.getY()));
            bnds.setSize(config.getInt(PROP_WIDTH, (int) bnds.getWidth()),
                    config.getInt(PROP_HEIGHT, (int) bnds.getHeight()));
            window.setBounds(bnds.x, bnds.y, bnds.width, bnds.height);
        }
    }

    /**
     * Shows the application's main window. This method is called after the main
     * window has been fully initialized. All other parts of the application have
     * also been initialized.
     *
     * @param window the main window
     */
    protected void showMainWindow(Window window)
    {
        window.open();
    }

    /**
     * Creates and initializes the application's command queue. This
     * implementation obtains the command queue from the global bean context
     * managed by the application context.
     *
     * @param appCtx the application context
     * @return the new command queue
     * @throws net.sf.jguiraffe.di.InjectionException if the command queue
     *         cannot be created
     */
    protected CommandQueue createCommandQueue(ApplicationContext appCtx)
    {
        CommandQueue q = (CommandQueue) appCtx.getBeanContext().getBean(
                BEAN_COMMAND_QUEUE);
        return q;
    }

    /**
     * Returns a reference to the command queue that is used for executing
     * commands in another thread.
     *
     * @return the command queue
     */
    public CommandQueue getCommandQueue()
    {
        return cmdQueue;
    }

    /**
     * Sets the command queue that is used for executing commands.
     *
     * @param q the new command queue (must not be <b>null</b>)
     * @throws IllegalArgumentException if the command queue is <b>null</b>
     */
    public void setCommandQueue(CommandQueue q)
    {
        if (q == null)
        {
            throw new IllegalArgumentException(
                    "Command queue must not be null!");
        }
        cmdQueue = q;
    }

    /**
     * Returns the {@code GUISynchronizer} object used by this
     * application. This object can be used to deal with the event dispatch
     * thread.
     *
     * @return the GUI synchronizer
     */
    public GUISynchronizer getGUISynchronizer()
    {
        return getCommandQueue().getGUISynchronizer();
    }

    /**
     * Sets the {@code GUISynchronizer} object to be used by this
     * application. This object can be used for safe GUI updates that need to
     * take place at the event dispatch thread. It will also set at the
     * application's command queue, so that always the same synchronizer is
     * used.
     *
     * @param sync the GUI synchronizer
     */
    public void setGUISynchronizer(GUISynchronizer sync)
    {
        getCommandQueue().setGUISynchronizer(sync);
    }

    /**
     * Executes the given command. The command is put into the internal command
     * queue. It is then executed in another thread. If the passed in command
     * implements the {@code ApplicationClient} interface, a reference to
     * this application will be automatically set before execution.
     *
     * @param cmd the command to be executed
     */
    public void execute(Command cmd)
    {
        setApplicationReference(cmd, this);
        getCommandQueue().execute(cmd);
    }

    /**
     * Shuts down this application. This method should always be called by the
     * main window class when the application is to be terminated. This
     * implementation checks if commands are still running. If this is the case,
     * a message is displayed (using the message output object) to the user
     * asking if the application should be ended anyway. The resource for this
     * message is defined by the passed in parameter, which can be a resource ID
     * or an {@code ApplicationResourceDef} object. Only if the user
     * confirms this, the application will be ended.
     *
     * @param msgres defines the resource of the message to be displayed
     * @param titleres defines the resource of the title of the message
     */
    public void shutdown(Object msgres, Object titleres)
    {
        log.info("shutdown() called.");
        if (titleres != null && getCommandQueue().isPending())
        {
            if (getApplicationContext().messageBox(msgres, titleres,
                    MessageOutput.MESSAGE_QUESTION, MessageOutput.BTN_YES_NO)
                    != MessageOutput.RET_YES)
            {
                return;
            }
        }

        log.debug("Calling shutdown listeners.");
        if (fireCanShutdown())
        {
            fireShutdown();
            onShutdown();
            getCommandQueue().shutdown(true);
            releaseBeanBuilderResults(getIninitializedBuilderResults());
            exitApplication(0);
        }
        else
        {
            log.debug("Veto of shutdown listener!");
        }
    }

    /**
     * Shuts down this application unconditionally. This version of the {@code
     * shutdown()} method does not check for tasks still running in the
     * background. It directly invokes the registered shutdown listeners and
     * exits the application if none vetos.
     */
    public void shutdown()
    {
        shutdown(null, null);
    }

    /**
     * Returns the current <em>exit handler</em> of this application. This is
     * the object called during a {@code shutdown()} operation. This method
     * never returns <b>null</b>. If no exit handler has been set, a default one
     * is returned. The default exit handler terminates this application by
     * calling {@code System.exit()} with the current exit code.
     *
     * @return the exit handler of this application
     * @since 1.2
     */
    public Runnable getExitHandler()
    {
        Runnable eh = exitHandler.get();
        return (eh != null) ? eh : defaultExitHandler;
    }

    /**
     * Sets the <em>exit handler</em> for this application. The exit handler is
     * called eventually by {@code shutdown()}. Its task is to ultimately
     * terminate this application, e.g. by calling {@code System.exit()}.
     *
     * @param handler the exit handler for this application (may be <b>null</b>)
     * @since 1.2
     */
    public void setExitHandler(Runnable handler)
    {
        exitHandler.set(handler);
    }

    /**
     * Returns the current exit code for this application. This value is only
     * defined during a {@code shutdown()} operation. This method is intended to
     * be called by an <em>exit handler</em> to find out the exit status of the
     * application.
     *
     * @return this application's exit status
     * @see #setExitHandler(Runnable)
     * @since 1.2
     */
    public int getExitCode()
    {
        return exitCode;
    }

    /**
     * A hook for shutdown. This method is called by the default implementation
     * of the {@code shutdown()} method. Here application specific
     * cleanup can be placed. Note: if this method is overloaded in a derived
     * class, the inherited method should be called. This implementation cares
     * for storing the user specific configuration if the
     * {@code storeuserconfig} property is <b>true</b>. Before that the
     * {@link #updateUserConfiguration()} method is called.
     */
    protected void onShutdown()
    {
        if (getApplicationContext().getConfiguration().getBoolean(PROP_USRCONF,
                false))
        {
            try
            {
                updateUserConfiguration();
                saveUserConfiguration();
            }
            catch (ApplicationException cex)
            {
                log.warn("Error when saving user configuration!", cex);
            }
        }
    }

    /**
     * Updates the user configuration. This method is called during shutdown if
     * the {@code storeuserconfig} configuration property is set. Here
     * actual settings can be written in the user configuration object so that
     * they can be restored the next time the application is started again. This
     * implementation stores the actual bounds of the main frame in the user
     * config.
     */
    protected void updateUserConfiguration()
    {
        Window wnd = getApplicationContext().getMainWindow();
        if (wnd != null)
        {
            Configuration uc = getUserConfiguration();
            uc.setProperty(PROP_XPOS, Integer.valueOf(wnd.getXPos()));
            uc.setProperty(PROP_YPOS, Integer.valueOf(wnd.getYPos()));
            uc.setProperty(PROP_WIDTH, Integer.valueOf(wnd.getWidth()));
            uc.setProperty(PROP_HEIGHT, Integer.valueOf(wnd.getHeight()));
        }
    }

    /**
     * Calls the {@code canShutdown()} method on all registered shutdown
     * listeners. If one of them returns <b>false </b>, the remaining listeners
     * are not invoked and shutdown process is canceled.
     *
     * @return a flag whether to proceed with the shutdown process
     */
    protected boolean fireCanShutdown()
    {
        Object[] listeners = shutdownListeners.getListenerList();
        boolean result = true;

        for (int i = listeners.length - 2; i >= 0 && result; i -= 2)
        {
            if (listeners[i] == ApplicationShutdownListener.class)
            {
                result = ((ApplicationShutdownListener) listeners[i + 1])
                        .canShutdown(this);
            }
        }

        return result;
    }

    /**
     * Notifies all registered shutdown listeners about the shutdown of this
     * application.
     */
    protected void fireShutdown()
    {
        Object[] listeners = shutdownListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ApplicationShutdownListener.class)
            {
                ((ApplicationShutdownListener) listeners[i + 1]).shutdown(this);
            }
        }
    }

    /**
     * Releases the results of bean builder operations that were created during
     * initialization of this application. The results of all bean builder
     * operations performed by {@link #initBeans(Configuration)} are stored so
     * that they can be released when the application shuts down. This is
     * exactly the task of this method. It is called by {@code shutdown()}.
     *
     * @param results the collection with the builder results to be released
     */
    protected void releaseBeanBuilderResults(
            Collection<BeanBuilderResult> results)
    {
        try
        {
            BeanBuilder builder = getBeanBuilderFactory().getBeanBuilder();

            for (BeanBuilderResult result : getIninitializedBuilderResults())
            {
                builder.release(result);
            }
        }
        catch (BuilderException bex)
        {
            log.warn("Error when releasing bean builder results", bex);
        }
    }

    /**
     * Terminates this application. This method is called by
     * {@link #shutdown(Object, Object)} at the very end. It calls the
     * <em>exit handler</em>. This object is responsible for actually
     * terminating this application.
     *
     * @param exitCode the exit code
     * @see #setExitHandler(Runnable)
     */
    protected void exitApplication(int exitCode)
    {
        if (log.isInfoEnabled())
        {
            log.info("Application ends with exit code " + exitCode);
            log.info("Calling exit handler.");
        }

        getExitHandler().run();
    }

    /**
     * The main execute method of this application. Performs all initialization
     * steps and displays the application's main window. This method starts it
     * all.
     *
     * @throws ApplicationException if an error occurs during initialization
     */
    public void run() throws ApplicationException
    {
        applicationContext = createApplicationContext();
        setCommandQueue(createCommandQueue(applicationContext));
        initGUI(applicationContext);
        if (applicationContext.getMainWindow() != null)
        {
            showMainWindow(applicationContext.getMainWindow());
        }
    }

    /**
     * Hook method for processing the command line arguments. Here a derived
     * class can place some logic to evaluate passed in parameters. This base
     * implementation is empty.
     *
     * @param args the command line arguments
     * @throws ApplicationException if a command line error occurs
     */
    public void processCommandLine(String[] args) throws ApplicationException
    {
        // empty method, may be defined in derived classes
    }

    /**
     * Starts an application. This method performs all steps to initialize and
     * startup an {@code Application} object. First the system properties
     * are checked if the configuration file is specified. Then the application
     * is given the opportunity of processing its command line. Finally its
     * {@code run()} method is invoked, which starts the application. A
     * typical use case for this method is to create an {@code Application}
     * instance (which also can be of a derived class) and pass it to this
     * method together with the command line array. The rest is done by this
     * method.
     *
     * @param application the application to start
     * @param args the command line arguments
     * @throws ApplicationException if an error occurs
     */
    public static void startup(Application application, String[] args)
            throws ApplicationException
    {
        if (application.getConfigResourceName() == null
                && application.getConfigURL() == null)
        {
            if (System.getProperties().containsKey(PROP_CONFIG_NAME))
            {
                application.setConfigResourceName(System
                        .getProperty(PROP_CONFIG_NAME));
            }
            else if (System.getProperties().containsKey(PROP_CONFIG_URL))
            {
                application.setConfigURL(System.getProperty(PROP_CONFIG_URL));
            }
            else
            {
                application.setConfigResourceName(DEF_CONFIG_NAME);
            }
        }

        application.processCommandLine(args);
        application.run();
    }

    /**
     * A main method for applications based on this framework. This method tries
     * to determine the name of the configuration file from system properties.
     * Then it creates an instance of this class, initializes it, and calls the
     * {@code startup()} method.
     *
     * @param args command line arguments
     * @throws ApplicationException if an error occurs
     */
    public static void main(String[] args) throws ApplicationException
    {
        startup(new Application(), args);
    }

    /**
     * Obtains the central {@code Application} instance from the specified
     * {@code BeanContext}. This method provides an easy way for obtaining
     * the {@code Application} when only the {@code ApplicationContext} is
     * known: just call
     * <pre>
     * Application myApp = Application.getInstance(appCtx.getBeanContext());
     * </pre>
     * If the application cannot be found in the given bean context, an
     * exception is thrown.
     *
     * @param context the bean context
     * @return the {@code Application} object defined in this bean context
     * @throws net.sf.jguiraffe.di.InjectionException if the application bean
     *         cannot be found
     * @throws IllegalArgumentException if the passed in context is <b>null</b>
     */
    public static Application getInstance(BeanContext context)
    {
        if (context == null)
        {
            throw new IllegalArgumentException("Bean context must not be null!");
        }
        return (Application) context.getBean(BEAN_APPLICATION);
    }

    /**
     * Helper method for creating a {@code ClassPathLocator} which is configured
     * with this application's class loader.
     *
     * @param resource the name of the resource to be loaded
     * @return the newly created {@code ClassPathLocator}
     */
    private ClassPathLocator cpLocator(String resource)
    {
        return ClassPathLocator.getInstance(resource, getClass()
                .getClassLoader());
    }

    /**
     * Adds a bean to a bean store. This implementation creates a constant bean
     * provider for the specified bean.
     *
     * @param store the bean store
     * @param name the name of the bean
     * @param bean the bean itself
     */
    private static void addBean(MutableBeanStore store, String name, Object bean)
    {
        store.addBeanProvider(name, ConstantBeanProvider.getInstance(bean));
    }
}

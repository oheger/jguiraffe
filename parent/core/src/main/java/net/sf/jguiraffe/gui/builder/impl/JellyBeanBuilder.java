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
package net.sf.jguiraffe.gui.builder.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.MutableBeanStore;
import net.sf.jguiraffe.di.impl.DefaultClassLoaderProvider;
import net.sf.jguiraffe.di.impl.RestrictedDependencyProvider;
import net.sf.jguiraffe.gui.builder.BeanBuilder;
import net.sf.jguiraffe.gui.builder.BeanBuilderResult;
import net.sf.jguiraffe.gui.builder.BuilderException;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;
import net.sf.jguiraffe.gui.builder.di.tags.DITagLibrary;
import net.sf.jguiraffe.locators.Locator;
import net.sf.jguiraffe.locators.LocatorException;
import net.sf.jguiraffe.locators.LocatorUtils;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.xml.sax.InputSource;

/**
 * <p>
 * An implementation of the <code>BeanBuilder</code> interface that is able to
 * process bean definitions defined in a <a
 * href="http://commons.apache.org/jelly/"> Apache Commons Jelly</a> script.
 * </p>
 * <p>
 * This class prepares a <code>JellyContext</code> object and registers the
 * <em>dependency injection tag library</em> ({@link DITagLibrary}).
 * Then it invokes <em>Jelly</em> for evaluating the specified script.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: JellyBeanBuilder.java 205 2012-01-29 18:29:57Z oheger $
 */
public class JellyBeanBuilder implements BeanBuilder
{
    /** Stores the name space URI to be used for the DI builder tag library. */
    private String diBuilderNameSpaceURI;

    /**
     * Creates a new instance of <code>JellyBeanBuilder</code>. Instances
     * should only be created using the factory.
     */
    protected JellyBeanBuilder()
    {
    }

    /**
     * Returns the name space URI, under which the DI tag library must be
     * registered.
     *
     * @return the name space URI for the DI tag library
     */
    public String getDiBuilderNameSpaceURI()
    {
        return diBuilderNameSpaceURI;
    }

    /**
     * Sets the name space URI for the DI tag library.
     *
     * @param diBuilderNameSpaceURI the new name space URI
     */
    public void setDiBuilderNameSpaceURI(String diBuilderNameSpaceURI)
    {
        this.diBuilderNameSpaceURI = diBuilderNameSpaceURI;
    }

    /**
     * {@inheritDoc} This implementation just calls the other {@code build()}
     * method passing in a <b>null</b> {@link InvocationHelper}.
     */
    public BeanBuilderResult build(Locator script, MutableBeanStore rootStore,
            ClassLoaderProvider loaderProvider) throws BuilderException
    {
        return build(script, rootStore, loaderProvider, null);
    }

    /**
     * {@inheritDoc} Delegates to {@code #executeScript()} which does the real
     * work.
     */
    public BeanBuilderResult build(Locator script, MutableBeanStore rootStore,
            ClassLoaderProvider loaderProvider, InvocationHelper invHlp)
            throws BuilderException
    {
        JellyContext context = setUpJellyContext();
        return executeScript(script, context, rootStore, loaderProvider, invHlp);
    }

    /**
     * Releases the specified {@code BeanBuilderResult} object. This will
     * especially invoke the {@code shutdown()} method on all bean providers
     * stored in one of the {@code BeanContext} objects contained in the result
     * object.
     *
     * @param builderResult the {@code BeanBuilderResult} object to be released
     * @throws IllegalArgumentException if the passed in result object is
     *         <b>null</b> or invalid
     */
    public void release(BeanBuilderResult builderResult)
    {
        if (builderResult == null)
        {
            throw new IllegalArgumentException(
                    "Builder result must not be null!");
        }

        DependencyProvider depProvider =
                createReleaseDependencyProvider(builderResult);
        boolean foundRootStore = false;
        for (String storeName : builderResult.getBeanStoreNames())
        {
            releaseBeanStore(builderResult.getBeanStore(storeName), depProvider);
            if (storeName == null)
            {
                foundRootStore = true;
            }
        }
        if (!foundRootStore)
        {
            // release the root bean store manually
            releaseBeanStore(builderResult.getBeanStore(null), depProvider);
        }
    }

    /**
     * Executes the specified script on the given Jelly context. Occurring Jelly
     * exceptions are caught and re-thrown as builder exceptions.
     *
     * @param script the script to be executed (must not be <b>null</b>)
     * @param context the Jelly context
     * @param rootStore the root bean store (can be <b>null</b>)
     * @param loaderProvider a data object with the registered class loaders
     *        (can be <b>null</b>)
     * @param invHlp a helper object for reflection operations (can be
     *        <b>null</b>)
     * @return an object with the results of the builder operation
     * @throws BuilderException if an error occurs while executing the script
     * @throws IllegalArgumentException if the script is <b>null</b>
     */
    protected BeanBuilderResult executeScript(Locator script,
            JellyContext context, MutableBeanStore rootStore,
            ClassLoaderProvider loaderProvider, InvocationHelper invHlp)
            throws BuilderException
    {
        if (script == null)
        {
            throw new IllegalArgumentException("Script must not be null!");
        }
        DIBuilderData builderData =
                createBuilderData(context, rootStore, loaderProvider, invHlp);

        try
        {
            InputSource source = prepareInputSource(script);
            context.runScript(source, XMLOutput.createDummyXMLOutput());
            return new BeanBuilderResultImpl(builderData);
        }
        catch (JellyException jex)
        {
            throw new BuilderException(extractScriptURL(script),
                    "Error when executing builder script", jex);
        }
        catch (IOException ioex)
        {
            throw new BuilderException(extractScriptURL(script),
                    "IO error when executing builder script", ioex);
        }
        catch (LocatorException locex)
        {
            throw new BuilderException(extractScriptURL(script),
                    "Locator threw an exception", locex);
        }
    }

    /**
     * Creates the {@code DIBuilderData} object used during the builder
     * operation. This object holds central data required by multiple components
     * involved in the builder operation.
     *
     * @param context the Jelly context
     * @param rootStore the root bean store
     * @param loaderProvider the class loader provider
     * @param invHlp the invocation helper
     * @return the newly created {@code DIBuilderData} object
     */
    protected DIBuilderData createBuilderData(JellyContext context,
            MutableBeanStore rootStore, ClassLoaderProvider loaderProvider,
            InvocationHelper invHlp)
    {
        DIBuilderData builderData = new DIBuilderData();
        builderData.initRootBeanStore(rootStore);
        builderData
                .setClassLoaderProvider(fetchClassLoaderProvider(loaderProvider));
        builderData.setInvocationHelper(invHlp);
        builderData.put(context);
        return builderData;
    }

    /**
     * Prepares an <code>InputSource</code> object for the specified
     * <code>Locator</code>. This method is called by
     * <code>executeScript()</code>. The resulting <code>InputSource</code> is
     * then passed to Jelly for processing the represented script. Note that the
     * way Jelly deals with URLs is not compatible with <code>Locator</code>
     * implementations derived from <code>ByteArrayLocator</code> (in-memory
     * locators). This is due to the fact that the URL is first transformed into
     * a string and later back into a URL. This implementation tries to work
     * around this problem by creating the <code>InputSource</code> from the
     * stream the <code>Locator</code> provides. If the URL can be transformed
     * to a string and back to a URL, it is also set as the system ID of the
     * input source (this makes it possible to resolve relative files).
     *
     * @param script the <code>Locator</code> pointing the the Jelly script
     * @return an <code>InputSource</code> for this script
     * @throws IOException if an IO error occurs
     */
    protected InputSource prepareInputSource(Locator script) throws IOException
    {
        InputSource source = new InputSource(LocatorUtils.openStream(script));
        String sysID = script.getURL().toString();
        try
        {
            new URL(sysID); // test whether a valid URL can be constructed
            source.setSystemId(sysID);
        }
        catch (MalformedURLException mex)
        {
            // This is a special URL => don't set a system ID
        }

        return source;
    }

    /**
     * Creates and initializes the Jelly context to be used for executing the
     * builder script. This method will also register the required tag libraries
     * and perform other mandatory initialization steps.
     *
     * @return the fully initialized context
     */
    protected JellyContext setUpJellyContext()
    {
        JellyContext result = createJellyContext();
        registerTagLibraries(result);
        return result;
    }

    /**
     * Creates the Jelly context for executing the builder script. This method
     * is called by <code>setUpJellyContext()</code>. Its task is only the
     * creation of the context, not its initialization.
     *
     * @return the newly created context
     */
    protected JellyContext createJellyContext()
    {
        return new JellyContext();
    }

    /**
     * Registers the required builder tag libraries at the given context. This
     * method is called by <code>setUpJellyContext()</code> before the builder
     * script will be executed.
     *
     * @param context the context
     */
    protected void registerTagLibraries(JellyContext context)
    {
        context.registerTagLibrary(getDiBuilderNameSpaceURI(),
                new DITagLibrary());
    }

    /**
     * Returns a {@code ClassLoaderProvider}. Some of the builder methods need a
     * {@code ClassLoaderProvider}, but the corresponding parameter is optional.
     * This method is called to obtain a valid {@code ClassLoaderProvider}
     * reference. If the passed in {@code ClassLoaderProvider} object is
     * defined, it is directly returned. Otherwise a default {@code
     * ClassLoaderProvider} is created, which does not has any registered class
     * loaders.
     *
     * @param clp the input {@code ClassLoaderProvider}
     * @return the {@code ClassLoaderProvider} to be used
     */
    protected ClassLoaderProvider fetchClassLoaderProvider(
            ClassLoaderProvider clp)
    {
        return (clp != null) ? clp : new DefaultClassLoaderProvider();
    }

    /**
     * Creates a {@code DependencyProvider} object that can be used during a
     * {@code release()} operation. This method creates a restricted dependency
     * provider that can be used for executing simple shutdown scripts, but does
     * not support access to external beans.
     *
     * @param result the {@code BeanBuilderResult} object that is to be released
     * @return the {@code DependencyProvider} for release operations
     * @throws IllegalArgumentException if helper objects required for the
     *         dependency provider are undefined
     */
    protected DependencyProvider createReleaseDependencyProvider(
            BeanBuilderResult result)
    {
        assert result != null : "Result object is null!";
        ClassLoaderProvider clp = result.getClassLoaderProvider();
        if (clp == null)
        {
            throw new IllegalArgumentException(
                    "No ClassLoaderProvider found in builder result!");
        }
        InvocationHelper ih = result.getInvocationHelper();
        if (ih == null)
        {
            throw new IllegalArgumentException(
                    "No InvocationHelper found in builder result!");
        }

        return new RestrictedDependencyProvider(clp, ih);
    }

    /**
     * Releases the specified {@code BeanStore}. This method will invoke the
     * {@code shutdown()} method on all bean providers found in the store.
     *
     * @param store the store to be released
     * @param depProvider the dependency provider
     */
    private void releaseBeanStore(BeanStore store,
            DependencyProvider depProvider)
    {
        for (String name : store.providerNames())
        {
            store.getBeanProvider(name).shutdown(depProvider);
        }
    }

    /**
     * Extracts the script URL from the given {@code Locator}. Occurring
     * exceptions are caught; in this case result is <b>null</b>. (This method
     * is just to provide details for exception messages; therefore it is not
     * necessary to handle exceptions more gracefully.)
     *
     * @param script the {@code Locator}
     * @return the URL of this {@code Locator} or <b>null</b>
     */
    private static URL extractScriptURL(Locator script)
    {
        try
        {
            return script.getURL();
        }
        catch (LocatorException locex)
        {
            return null;
        }
    }

    /**
     * An implementation of the {@code BeanBuilderResult} interface
     * specific for this builder implementation. This class delegates to a
     * {@link DIBuilderData} object, which holds the actual data.
     */
    private static class BeanBuilderResultImpl implements BeanBuilderResult
    {
        /** Stores the wrapped builder data object. */
        private final DIBuilderData builderData;

        /**
         * Creates a new instance of <code>BeanBuilderResultImpl</code> and
         * initializes it with the builder data object.
         *
         * @param data the wrapped builder data object
         */
        public BeanBuilderResultImpl(DIBuilderData data)
        {
            builderData = data;
        }

        /**
         * Returns the bean store with the given name.
         *
         * @param name the name of the bean store
         * @return the store with this name
         * @throws java.util.NoSuchElementException if the name is unknown
         */
        public BeanStore getBeanStore(String name)
        {
            return builderData.getBeanStore(name);
        }

        /**
         * Returns an set with the names of all defined bean stores.
         *
         * @return the names of the defined bean stores
         */
        public Set<String> getBeanStoreNames()
        {
            return builderData.getBeanStoreNames();
        }

        /**
         * Returns the {@code ClassLoaderProvider} used during the build.
         *
         * @return the {@code ClassLoaderProvider}
         */
        public ClassLoaderProvider getClassLoaderProvider()
        {
            return builderData.getClassLoaderProvider();
        }

        /**
         * Returns the {@code InvocationHelper} used during the build.
         *
         * @return the {@code InvocationHelper}
         */
        public InvocationHelper getInvocationHelper()
        {
            return builderData.getInvocationHelper();
        }
    }
}

/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;
import net.sf.jguiraffe.di.impl.DefaultBeanContext;
import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentManagerImpl;
import net.sf.jguiraffe.gui.builder.components.Container;
import net.sf.jguiraffe.gui.builder.components.DefaultFieldHandlerFactory;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;
import net.sf.jguiraffe.gui.builder.impl.JellyContextBeanStore;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.locators.ByteArrayLocator;
import net.sf.jguiraffe.locators.ClassPathLocator;
import net.sf.jguiraffe.locators.Locator;
import net.sf.jguiraffe.locators.LocatorUtils;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

/**
 * <p>
 * A base class for testing Jelly tag classes.
 * </p>
 * <p>
 * This class provides basic functionality for locating and executing Jelly
 * scripts with form builder tags. The results of these scripts (i.e. the data
 * generated by the builder) can also be compared with expected values. This
 * makes it quite convenient to write test cases for concrete tag classes.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AbstractTagTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractTagTest extends TestCase
{
    /** Constant for the name of the test builder. */
    protected static final String BUILDER_NAME = "TEST_BUILDER";

    /** Constant for the name of the builder that generates errors. */
    protected static final String ERROR_BUILDER = "ERROR_BUILDER";

    /** Constant for the name of the root container. */
    protected static final String ROOT_CONTAINER = "ROOT";

    /** Constant for the name of the default resource group. */
    protected static final String DEF_RES_GRP = "testformbuilderresources";

    /** Constant for the output directory for self written Jelly scripts. */
    protected static final String JELLY_OUT_DIR = "target";

    /** Constant for the default prefix for Jelly scripts.*/
    protected static final String JELLY_PREFIX = "/jelly_scripts/";

    /** Constant for the default extension for jelly scripts. */
    protected static final String JELLY_EXT = ".jelly";

    /** Constant for the name of the default icon.*/
    protected static final String ICON_NAME = "icon.gif";

    /** Constant for the header of a typical Jelly test script. */
    private static final String JELLY_HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
            + "<j:jelly xmlns:j=\"jelly:core\" xmlns:f=\"formBuilder\">\n";

    /** Constant for the footer of a typical Jelly test script. */
    private static final String JELLY_FOOTER = "</j:jelly>";

    /** Constant for the name of the locator attribute.*/
    private static final String ATTR_LOCATOR = "LOCATOR = ";

    /** A logger.*/
    protected final Log log = LogFactory.getLog(getClass());

    /** Stores the builder data object. */
    protected ComponentBuilderData builderData;

    /** Stores a reference to the Jelly context. */
    protected JellyContext context;

    /** Stores the XMLOutput object used by Jelly scripts. */
    protected XMLOutput output;

    /**
     * Sets up this this test class. Initializes the objects needed for script
     * execution.
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        setUpBuilderData();
        setUpJelly();
    }

    /**
     * Initializes the builder data object.
     */
    protected void setUpBuilderData()
    {
        builderData = new ComponentBuilderData();
        builderData.setBuilderName(BUILDER_NAME);
        builderData.setComponentManager(new ComponentManagerImpl());
        builderData.setFieldHandlerFactory(new DefaultFieldHandlerFactory());
        builderData.setRootContainer(new Container("ROOT"));
        builderData.initializeForm(new TransformerContextImpl(),
                new BeanBindingStrategy());
        builderData.setDefaultResourceGroup(DEF_RES_GRP);
    }

    /**
     * Sets up the jelly context and XML output. This method also creates a bean
     * context, which accesses the Jelly context (so that all variables stored
     * in the Jelly context can be queried through the bean context).
     */
    protected void setUpJelly()
    {
        context = new JellyContext();
        context.registerTagLibrary("formBuilder", new FormBuilderTagLibrary());
        builderData.put(context);
        output = XMLOutput.createDummyXMLOutput();
        builderData.setBeanContext(new DefaultBeanContext(
                new JellyContextBeanStore(context, null)));
        DIBuilderData diData = new DIBuilderData();
        diData.put(context);
    }

    /**
     * Executes a jelly script. The script is searched in the class path. If
     * necessary, a leading slash and the extension ".jelly" are added.
     *
     * @param name the name of the script
     * @throws Exception if an error occurs
     */
    protected void executeScript(String name) throws Exception
    {
        String scriptName = name;
        if (scriptName.indexOf('/') < 0
                && scriptName.indexOf(File.pathSeparator) < 0)
        {
            scriptName = JELLY_PREFIX + scriptName;
        }

        URL scriptURL = getClass().getResource(completeScriptName(scriptName));
        assertNotNull("Resolving script " + scriptName, scriptURL);
        executeScript(scriptURL);
    }

    /**
     * Generates the complete script name. Appends an extension if it is
     * missing.
     *
     * @param name the script name
     * @return the complete script name
     */
    protected String completeScriptName(String name)
    {
        return (name.indexOf('.') < 0) ? name + JELLY_EXT : name;
    }

    /**
     * Executes a jelly script specified by a {@code Locator}.
     *
     * @param locator the script locator
     * @throws Exception if an error occurs
     */
    protected void executeScript(Locator locator) throws Exception
    {
        log.info("Executing test script " + locator.getURL());
        context.runScript(new InputSource(LocatorUtils.openStream(locator)),
                output);
    }

    /**
     * Executes a jelly script. The script is specified using a full URL.
     *
     * @param url the script URL
     * @throws Exception if an error occurs
     */
    protected void executeScript(URL url) throws Exception
    {
    	log.info("Executing test script " + url);
        context.runScript(url, output);
    }

    /**
     * Compares the results of a builder operation with the expected string.
     *
     * @param expected the expected results
     */
    protected void checkResult(String expected)
    {
        String current = builderData.getRootContainer().toString();
        assertEquals(expected + "\n" + current, expected, current);
    }

    /**
     * Executes a Jelly form builder script and compares the results with the
     * expected values. This is a convenience method that combines calls of
     * {@link #executeScript(String)} and {@link #checkResult(String)}.
     *
     * @param scriptName the name of the script to be executed
     * @param expected the expected return value
     * @throws Exception if an error occurs
     */
    protected void checkScript(String scriptName, String expected)
            throws Exception
    {
        executeScript(scriptName);
        checkResult(expected);
    }

    /**
     * Executes a script, optionally executes the builder callbacks, and expects
     * an error.
     *
     * @param scriptName the name of the script to be executed
     * @param builderName the name of the builder to be used
     * @param msg an error message for the {@code fail()} statement
     * @param callBacks a flag whether the callbacks are to be executed
     * @throws Exception in case of an error
     */
    protected void errorScript(String scriptName, String builderName,
            String msg, boolean callBacks) throws Exception
    {
        builderData.setBuilderName(builderName);
        try
        {
            executeScript(scriptName);
            if (callBacks)
            {
                builderData.invokeCallBacks();
            }
            fail(msg);
        }
        catch (JellyException jex)
        {
            // ok
        }
        catch (FormBuilderException fex)
        {
            // ok, too
        }
    }

    /**
     * Executes a script and expects an error. This is a short form of
     * {@code errorScript(scriptName, builderName, msg, false);}.
     *
     * @param scriptName the name of the script to be executed
     * @param builderName the builder name to use
     * @param msg an error message for the {@code fail()} statement
     * @throws Exception if an error occurs
     */
    protected void errorScript(String scriptName, String builderName, String msg)
            throws Exception
    {
        errorScript(scriptName, builderName, msg, false);
    }

    /**
     * Creates a test script. This method adds a correct header and footer to
     * the passed in script content. Then the script is packaged into an in
     * memory locator. This locator can be passed to the
     * {@code executeScript()} method.
     *
     * @param content the content of the test script
     * @return the locator pointing to the test script
     */
    protected Locator createTestScript(String content)
    {
        StringBuilder buf = new StringBuilder();
        buf.append(JELLY_HEADER);
        buf.append(content);
        buf.append(JELLY_FOOTER);
        return ByteArrayLocator.getInstance(buf.toString());
    }

    /**
     * Returns a string representation of a locator pointing to the default
     * icon. This string can be used by tag tests that have to deal with icons.
     *
     * @return the text for a locator pointing to the default icon
     */
    protected static String iconLocatorString()
    {
        Locator locator = ClassPathLocator.getInstance(ICON_NAME);
        return ATTR_LOCATOR + LocatorUtils.locatorToDataString(locator);
    }

    /**
     * Returns a string representation of the color with the given RGB
     * components.
     *
     * @param r the value for red
     * @param g the value for green
     * @param b the value for blue
     * @return the string representation of this color
     */
    protected static String colorString(int r, int g, int b)
    {
        Color c = Color.newRGBInstance(r, g, b);
        return c.toString();
    }
}

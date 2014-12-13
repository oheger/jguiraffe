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
package net.sf.jguiraffe.gui.builder.di.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;

import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.impl.DefaultClassLoaderProvider;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ResourceTag}.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class TestResourceTag
{
    /** Constant for the test resource name. */
    private static final String RESOURCE = "testplatformbeans.jelly";

    /** Constant for the variable name. */
    private static final String VAR = "result";

    /** The Jelly context. */
    private JellyContext context;

    /** The output object for invoking the tag. */
    private XMLOutput output;

    /** The tag to be tested. */
    private ResourceTag tag;

    @Before
    public void setUp() throws Exception
    {
        output = new XMLOutput();
        context = new JellyContext();
        setUpDIData(context);
        tag = new ResourceTag();
        tag.setContext(context);
        tag.setResource(RESOURCE);
        tag.setVar(VAR);
    }

    /**
     * Generates a builder data object to be used in the test.
     *
     * @param context the Jelly context
     */
    private static void setUpDIData(JellyContext context)
    {
        DIBuilderData data = new DIBuilderData();
        DefaultClassLoaderProvider provider = new DefaultClassLoaderProvider();
        provider.registerClassLoader("default",
                TestResourceTag.class.getClassLoader());
        provider.setDefaultClassLoaderName("default");
        data.setClassLoaderProvider(provider);
        data.put(context);
    }

    /**
     * Tries to execute a tag without a resource name.
     */
    @Test
    public void testUndefinedResource() throws JellyTagException
    {
        tag.setResource(null);
        try
        {
            tag.doTag(output);
            fail("Missing attribute not detected!");
        }
        catch (MissingAttributeException mex)
        {
            assertEquals("Wrong attribute name", "resource",
                    mex.getMissingAttribute());
        }
    }

    /**
     * Tries to execute a tag without a variable name.
     */
    @Test
    public void testUndefinedVariable() throws JellyTagException
    {
        tag.setVar(null);
        try
        {
            tag.doTag(output);
            fail("Missing attribute not detected!");
        }
        catch (MissingAttributeException mex)
        {
            assertEquals("Wrong attribute name", "var",
                    mex.getMissingAttribute());
        }
    }

    /**
     * Checks whether the result variable has been set correctly.
     */
    private void verifyResultVariable()
    {
        String result = (String) context.getVariable(VAR);
        verifyResult(result);
    }

    /**
     * Verifies the specified result of a resolve operation. This method checks
     * whether the given result is a URL pointing to the test resource.
     *
     * @param result the result
     */
    private static void verifyResult(String result)
    {
        assertTrue("Wrong resolved URI: " + result, result.endsWith(RESOURCE));
        try
        {
            new URL(result);
        }
        catch (MalformedURLException e)
        {
            fail("Result is not a correct URL: " + result);
        }
    }

    /**
     * Tests whether a resource name can be resolved successfully.
     */
    @Test
    public void testResolveResource() throws JellyTagException
    {
        tag.doTag(output);
        verifyResultVariable();
    }

    /**
     * Tests whether a specific class loader can be used for resolving the
     * resource.
     */
    @Test
    public void testResolveResourceWithClassLoader() throws JellyTagException
    {
        ClassLoader loader = EasyMock.createMock(ClassLoader.class);
        EasyMock.replay(loader);
        ClassLoaderProvider provider =
                DIBuilderData.get(context).getClassLoaderProvider();
        final String loaderName = "specialClassLoader";
        provider.registerClassLoader(loaderName, getClass().getClassLoader());
        provider.registerClassLoader(provider.getDefaultClassLoaderName(),
                loader);
        tag.setClassLoader(loaderName);

        tag.doTag(output);
        verifyResultVariable();
    }

    /**
     * Tests a failed resolve operation.
     */
    @Test(expected = JellyTagException.class)
    public void testResolveFailed() throws JellyTagException
    {
        tag.setResource("non resolvable resource");
        tag.doTag(output);
    }

    /**
     * Checks a resolve operation with a delimiter specified if no old variable
     * value is set.
     *
     * @throws JellyTagException if an error occurs
     */
    private void checkResolveWithDelimiterUnspecifiedOldValue()
            throws JellyTagException
    {
        tag.setDelimiter("some delimiter");

        tag.doTag(output);
        verifyResultVariable();
    }

    /**
     * Tests a resolve operation with a delimiter specified if the target
     * variable does not exist.
     */
    @Test
    public void testResolveWithDelimiterNewValue() throws JellyTagException
    {
        checkResolveWithDelimiterUnspecifiedOldValue();
    }

    /**
     * Tests a resolve operation with a delimiter specified if the target
     * variable contains an empty string.
     */
    @Test
    public void testResolveWithDelimiterEmptyValue() throws JellyTagException
    {
        context.setVariable(VAR, "");
        checkResolveWithDelimiterUnspecifiedOldValue();
    }

    /**
     * Tests whether the result of a resolve operation can be concatenated to an
     * existing value.
     */
    @Test
    public void testResolveWithDelimiterConcatenation()
            throws JellyTagException
    {
        final String delimiter = ",";
        final String oldValue = "value1";
        tag.setDelimiter(delimiter);
        context.setVariable(VAR, oldValue);

        tag.doTag(output);
        String result = (String) context.getVariable(VAR);
        assertTrue("No first value: " + result,
                result.startsWith(oldValue + delimiter));
        result = result.substring(oldValue.length() + delimiter.length());
        verifyResult(result);
    }

    /**
     * Tests a resolve operation if a delimiter is specified and the target
     * variable exists, but is not a string.
     */
    @Test
    public void testResolveOldValueOtherType() throws JellyTagException
    {
        context.setVariable(VAR, this);
        checkResolveWithDelimiterUnspecifiedOldValue();
    }
}

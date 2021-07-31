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
package net.sf.jguiraffe.gui.builder.di.tags;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.junit.Before;
import org.junit.Test;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;

/**
 * Test class for {@code ContextBeanTag}.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class TestContextBeanTag
{
    /** The variable to be read from the context. */
    private static final String VAR_NAME = "myContextVariable";

    /** The Jelly context. */
    private JellyContext context;

    /** The tag to be tested. */
    private ContextBeanTag tag;

    @Before
    public void setUp() throws Exception
    {
        context = new JellyContext();
        tag = new ContextBeanTag();
        tag.setContext(context);
    }

    /**
     * Executes the test tag and returns the resulting bean provider.
     *
     * @throws JellyTagException if an exception occurs
     */
    private BeanProvider executeTag() throws JellyTagException
    {
        return tag.createBeanProvider();
    }

    /**
     * Tests that an exception is thrown for an undefined var attribute.
     */
    @Test
    public void testUndefinedVarAttribute() throws JellyTagException
    {
        try
        {
            executeTag();
            fail("Missing attribute not detected!");
        }
        catch (MissingAttributeException e)
        {
            assertEquals("Wrong attribute", "var", e.getMissingAttribute());
        }
    }

    /**
     * Tests that an exception is thrown for an undefined variable value in the
     * context.
     */
    @Test
    public void testUndefinedVariableInContext()
    {
        tag.setVar(VAR_NAME);
        try
        {
            executeTag();
            fail("Missing variable not detected!");
        }
        catch (JellyTagException e)
        {
            assertThat("Wrong exception message", e.getMessage(),
                    containsString(VAR_NAME));
        }
    }

    /**
     * Tests that the correct bean provider is created.
     */
    @Test
    public void testCreationOfBeanProvider() throws JellyTagException
    {
        Object bean = new Object();
        context.setVariable(VAR_NAME, bean);
        tag.setVar(VAR_NAME);

        ConstantBeanProvider provider = (ConstantBeanProvider) executeTag();
        assertEquals("Wrong bean", bean, provider.getBean());
    }

    /**
     * Tests whether a named bean can be created.
     */
    @Test
    public void testName()
    {
        final String name = "mySpecialBean";
        tag.setName(name);
        assertEquals("Wrong bean name", name, tag.getName());
    }
}

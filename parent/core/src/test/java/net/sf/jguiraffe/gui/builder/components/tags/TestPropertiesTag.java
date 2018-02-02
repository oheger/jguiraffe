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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * Test class for PropertiesTag.
 *
 * @author Oliver Heger
 * @version $Id: TestPropertiesTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestPropertiesTag extends TestCase
{
    /** An array with names of properties. */
    private static final String[] PROP_NAMES =
    { "test", "version", "db.url", "db.usr", "db.pwd" };

    /** An array with values of properties. */
    private static final Object[] PROP_VALUES =
    { Boolean.TRUE, 42, "jdbc:test.db", "scott", "tiger" };

    /** Constant for the name of a variable. */
    private static final String VAR_NAME = "myMapVar";

    /** Stores the Jelly context. */
    private JellyContext context;

    /** Stores the tag to be tested. */
    private PropertiesTag tag;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        context = new JellyContext();
        tag = new PropertiesTag();
        tag.setContext(context);
    }

    /**
     * Creates and initializes a parent tag.
     *
     * @return the parent tag
     */
    private PropertiesSupportTagTestImpl setUpParent()
    {
        PropertiesSupportTagTestImpl parent = new PropertiesSupportTagTestImpl();
        tag.setParent(parent);
        return parent;
    }

    /**
     * Adds the test properties to the test tag.
     */
    private void addProperties()
    {
        for (int i = 0; i < PROP_NAMES.length; i++)
        {
            tag.setProperty(PROP_NAMES[i], PROP_VALUES[i]);
        }
    }

    /**
     * Tests whether the passed in properties object contains all the test
     * properties.
     *
     * @param props the properties to test
     * @param size a flag whether the size of the map must also be checked
     */
    private void checkProperties(Map<?, ?> props, boolean size)
    {
        for (int i = 0; i < PROP_NAMES.length; i++)
        {
            assertEquals("Wrong property " + PROP_NAMES[i], PROP_VALUES[i],
                    props.get(PROP_NAMES[i]));
        }
        if (size)
        {
            assertEquals("Wrong number of properties", PROP_NAMES.length, props
                    .size());
        }
    }

    /**
     * Tests creating new properties and passing them to the parent tag.
     */
    public void testCreatePropertiesWithParent() throws JellyTagException,
            FormBuilderException
    {
        PropertiesSupportTagTestImpl parent = setUpParent();
        tag.processBeforeBody();
        addProperties();
        tag.process();
        checkProperties(parent.properties, true);
    }

    /**
     * Tests creating new properties and storing them as variable.
     */
    public void testCreatePropertiesWithVar() throws JellyTagException,
            FormBuilderException
    {
        tag.setVar(VAR_NAME);
        tag.processBeforeBody();
        addProperties();
        tag.process();
        checkProperties((Map<?, ?>) context.getVariable(VAR_NAME), true);
    }

    /**
     * Tests creating new properties when both a parent and a target variable
     * are specified.
     */
    public void testCreatePropertiesWithParentAndVar()
            throws JellyTagException, FormBuilderException
    {
        PropertiesSupportTagTestImpl parent = setUpParent();
        tag.setVar(VAR_NAME);
        tag.processBeforeBody();
        addProperties();
        tag.process();
        checkProperties(parent.properties, true);
        assertSame("Wrong value of variable", parent.properties, context
                .getVariable(VAR_NAME));
    }

    /**
     * Tests processing a tag when no target is specified. This should cause an
     * exception.
     */
    public void testProcessNoTarget() throws JellyTagException,
            FormBuilderException
    {
        tag.processBeforeBody();
        addProperties();
        try
        {
            tag.process();
            fail("Missing target was not detected!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tests using the ref attribute for specifying an existing map.
     */
    public void testRefProperties() throws JellyTagException,
            FormBuilderException
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("prop", "value");
        context.setVariable(VAR_NAME, props);
        PropertiesSupportTagTestImpl parent = setUpParent();
        tag.setRef(VAR_NAME);
        tag.processBeforeBody();
        addProperties();
        tag.process();
        checkProperties(props, false);
        assertSame("Wrong map passed to parent", props, parent.properties);
        assertEquals("Initial property not found", "value", props.get("prop"));
        assertEquals("Wrong number of properties", PROP_NAMES.length + 1, props
                .size());
    }

    /**
     * Tests using an unknown reference to a properties map. This should cause
     * an exception.
     */
    public void testProcessBeforeBodyUnknownRef() throws JellyTagException,
            FormBuilderException
    {
        tag.setRef(VAR_NAME);
        try
        {
            tag.processBeforeBody();
            fail("Unknown reference name not detected!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tests using an invalid reference (to a wrong class) to a properties map.
     * This should cause an exception.
     */
    public void testProcessBeforeBodyInvalidRef() throws JellyTagException,
            FormBuilderException
    {
        tag.setRef(VAR_NAME);
        context.setVariable(VAR_NAME, this);
        try
        {
            tag.processBeforeBody();
            fail("Invalid reference not detected!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * A dummy tag class implementing the PropertiesSupport interface that will
     * be used as parent tag in tests.
     */
    private static class PropertiesSupportTagTestImpl extends TagSupport
            implements PropertiesSupport
    {
        /** Stores the properties passed to this tag. */
        Map<String, Object> properties;

        /**
         * Sets the properties for this tag.
         *
         * @param props the properties
         */
        public void setProperties(Map<String, Object> props)
        {
            properties = props;
        }

        /**
         * Dummy implementation of this interface method.
         */
        public void doTag(XMLOutput arg0) throws JellyTagException
        {
        }
    }
}

/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.forms.bind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.sf.jguiraffe.gui.forms.FormRuntimeException;
import net.sf.jguiraffe.gui.forms.PersonBean;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link BeanBindingStrategy}.
 *
 * @author Oliver Heger
 * @version $Id: TestBeanBindingStrategy.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestBeanBindingStrategy
{
    /** Constant for a test first name. */
    private static final String FIRST_NAME = "Harry";

    /** Constant for a test last name. */
    private static final String NAME = "Hirsch";

    /** The test person bean. */
    private PersonBean person;

    /** The strategy to be tested. */
    private BeanBindingStrategy strategy;

    @Before
    public void setUp() throws Exception
    {
        person = new PersonBean();
        strategy = new BeanBindingStrategy();
    }

    /**
     * Tests reading a property from a bean.
     */
    @Test
    public void testReadProperty()
    {
        person.setFirstName(FIRST_NAME);
        person.setName(NAME);
        assertEquals("Wrong first name", FIRST_NAME, strategy.readProperty(
                person, "firstName"));
        assertEquals("Wrong name", NAME, strategy.readProperty(person, "name"));
    }

    /**
     * Tests reading a property that does not exist. This should cause an
     * exception.
     */
    @Test(expected = FormRuntimeException.class)
    public void testReadPropertyNonExisting()
    {
        strategy.readProperty(person, "nonExistingProperty");
    }

    /**
     * Tests reading a property from a null bean. This should cause an
     * exception.
     */
    @Test(expected = FormRuntimeException.class)
    public void testReadPropertyNullBean()
    {
        strategy.readProperty(null, "firstName");
    }

    /**
     * Tests writing a property.
     */
    @Test
    public void testWriteProperty()
    {
        strategy.writeProperty(person, "firstName", FIRST_NAME);
        strategy.writeProperty(person, "name", NAME);
        assertEquals("Wrong first name", FIRST_NAME, person.getFirstName());
        assertEquals("Wrong last name", NAME, person.getName());
    }

    /**
     * Tests writing a non existing property. This should cause an exception.
     */
    @Test(expected = FormRuntimeException.class)
    public void testWritePropertyNonExisting()
    {
        strategy.writeProperty(person, "nonExistingProperty", 42);
    }

    /**
     * Tests writing a property on a null bean. This should cause an exception.
     */
    @Test(expected = FormRuntimeException.class)
    public void testWritePropertyNullBean()
    {
        strategy.writeProperty(null, "name", NAME);
    }

    /**
     * Tests whether a null value can be written into a property.
     */
    @Test
    public void testWritePropertyNullValue()
    {
        person.setName(NAME);
        strategy.writeProperty(person, "name", null);
        assertNull("Got a name", person.getName());
    }
}

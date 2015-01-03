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
package net.sf.jguiraffe.gui.forms.bind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.gui.forms.PersonBean;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link DummyBindingStrategy}.
 *
 * @author Oliver Heger
 * @version $Id: TestDummyBindingStrategy.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDummyBindingStrategy
{
    /** A bean that can be used for testing. */
    private PersonBean bean;

    @Before
    public void setUp()
    {
        bean = new PersonBean();
        bean.setFirstName("Harry");
        bean.setName("Hirsch");
    }

    /**
     * Tests reading a property. This is just a dummy operation, which always
     * returns null.
     */
    @Test
    public void testReadProperty()
    {
        assertNull("Got a property value", DummyBindingStrategy.INSTANCE
                .readProperty(bean, "name"));
    }

    /**
     * Tests writing a property. This is just a dummy operation, so the bean
     * should not be changed.
     */
    @Test
    public void testWriteProperty()
    {
        DummyBindingStrategy.INSTANCE.writeProperty(bean, "name", "Sepp");
        DummyBindingStrategy.INSTANCE.writeProperty(bean, "birthDate",
                JGuiraffeTestHelper.createDate(1950, 3, 10));
        assertEquals("Property was changed", "Hirsch", bean.getName());
        assertNull("Property was written", bean.getBirthDate());
    }
}

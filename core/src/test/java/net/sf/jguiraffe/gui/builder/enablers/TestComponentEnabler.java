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
package net.sf.jguiraffe.gui.builder.enablers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;

import org.junit.Test;

/**
 * Test class for {@link ComponentEnabler}.
 *
 * @author Oliver Heger
 * @version $Id: TestComponentEnabler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestComponentEnabler
{
    /** Constant for the name of a component. */
    private static final String COMP_NAME = "TestComponent";

    /**
     * Creates and initializes a {@code ComponentBuilderData} object.
     *
     * @return the initialized object
     */
    private ComponentBuilderData setUpCompData()
    {
        ComponentBuilderData compData = new ComponentBuilderData();
        compData.initializeForm(new TransformerContextImpl(),
                new BeanBindingStrategy());
        return compData;
    }

    /**
     * Tests creating an instance without a component name. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoCompName()
    {
        new ComponentEnabler(null);
    }

    /**
     * Tests setting the enabled state of a component.
     */
    @Test
    public void testSetEnabledState() throws FormBuilderException
    {
        ComponentBuilderData compData = setUpCompData();
        ComponentHandlerImpl ch = new ComponentHandlerImpl();
        compData.storeComponentHandler(COMP_NAME, ch);
        ComponentEnabler enabler = new ComponentEnabler(COMP_NAME);
        enabler.setEnabledState(compData, true);
        assertTrue("Not enabled", ch.isEnabled());
        enabler.setEnabledState(compData, false);
        assertFalse("Not disabled", ch.isEnabled());
    }

    /**
     * Tests setting the enabled state when the component cannot be resolved.
     * This should cause an exception.
     */
    @Test(expected = FormBuilderException.class)
    public void testSetEnabledStateUnknownComponent()
            throws FormBuilderException
    {
        ComponentBuilderData compData = setUpCompData();
        ComponentEnabler enabler = new ComponentEnabler(COMP_NAME);
        enabler.setEnabledState(compData, true);
    }
}

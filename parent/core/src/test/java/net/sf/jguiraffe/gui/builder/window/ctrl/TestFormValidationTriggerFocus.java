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
package net.sf.jguiraffe.gui.builder.window.ctrl;

import static org.junit.Assert.assertEquals;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormFocusEvent;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManagerImpl;
import net.sf.jguiraffe.gui.forms.BindingStrategy;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;
import net.sf.jguiraffe.gui.forms.ComponentStore;
import net.sf.jguiraffe.gui.forms.ComponentStoreImpl;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code FormValidationTriggerFocus}.
 *
 * @author Oliver Heger
 * @version $Id: TestFormValidationTriggerFocus.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFormValidationTriggerFocus
{
    /** Constant for the number of test components. */
    private static final int COMP_COUNT = 8;

    /** Constant for the prefix of component names. */
    private static final String COMP_NAME = "testComponent";

    /** The form controller. */
    private FormControllerTestImpl controller;

    /** The platform event manager. */
    private PlatformEventManagerImpl eventManager;

    /** The validation trigger to test. */
    private FormValidationTriggerFocus trigger;

    @Before
    public void setUp() throws Exception
    {
        controller = new FormControllerTestImpl();
        ComponentBuilderData compData = new ComponentBuilderData();
        compData.initializeForm(new TransformerContextImpl(), EasyMock
                .createNiceMock(BindingStrategy.class));
        eventManager = new PlatformEventManagerImpl();
        compData.setEventManager(new FormEventManager(eventManager));
        compData.getEventManager().setComponentStore(setUpComponents());
        controller.setComponentBuilderData(compData);
        trigger = new FormValidationTriggerFocus();
        trigger.initTrigger(controller);
    }

    /**
     * Creates some test components.
     *
     * @return the component store
     */
    private ComponentStore setUpComponents()
    {
        ComponentStoreImpl store = new ComponentStoreImpl();
        for (int i = 0; i < COMP_COUNT; i++)
        {
            ComponentHandlerImpl ch = new ComponentHandlerImpl();
            store.addComponentHandler(COMP_NAME + i, ch);
        }
        return store;
    }

    /**
     * Helper method for creating a focus event.
     *
     * @param type the event type
     * @return the test event
     */
    private FormFocusEvent createFocusEvent(FormFocusEvent.Type type)
    {
        return new FormFocusEvent(this, new ComponentHandlerImpl(), COMP_NAME,
                type);
    }

    /**
     * Tests whether the trigger initializes itself correctly.
     */
    @Test
    public void testInitTrigger()
    {
        assertEquals("Wrong controller", controller, trigger
                .getFormController());
        for (int i = 0; i < COMP_COUNT; i++)
        {
            String compName = COMP_NAME + i;
            assertEquals("Wrong registrations for " + compName, 1, eventManager
                    .getNumberOf(compName, FormListenerType.FOCUS));
        }
    }

    /**
     * Tests the focusGained() implementation. Here nothing should happen.
     */
    @Test
    public void testFocusGained()
    {
        trigger.focusGained(createFocusEvent(FormFocusEvent.Type.FOCUS_GAINED));
        assertEquals("Validation performed", 0, controller.validateCounter);
    }

    /**
     * Tests the behavior if a focus lost event was received. This should
     * trigger a validation.
     */
    @Test
    public void testFocusLost()
    {
        trigger.focusLost(createFocusEvent(FormFocusEvent.Type.FOCUS_LOST));
        assertEquals("Wrong number of validations", 1,
                controller.validateCounter);
    }

    /**
     * A specialized form controller test implementation for testing whether
     * validation operations are invoked as expected.
     */
    private static class FormControllerTestImpl extends FormController
    {
        /** A counter for validate() calls. */
        int validateCounter;

        /**
         * Records this invocation.
         */
        @Override
        public FormValidatorResults validate()
        {
            validateCounter++;
            return null;
        }
    }
}

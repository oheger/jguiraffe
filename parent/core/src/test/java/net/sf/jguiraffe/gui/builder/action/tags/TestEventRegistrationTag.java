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
package net.sf.jguiraffe.gui.builder.action.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.EventListener;
import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.TextFieldTag;
import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManagerImpl;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for {@code EventRegistrationTag}.
 *
 * @author Oliver Heger
 * @version $Id: TestEventRegistrationTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestEventRegistrationTag
{
    /** Constant for the name of the test component. */
    private static final String COMPONENT = "testComponent";

    /** Constant for the name of the test bean. */
    private static final String TARGET_BEAN_NAME = "aTestBean";

    /** Constant for the target bean object. */
    private static final Object TARGET_BEAN = new Object();

    /** Constant for the event type attribute. */
    private static final String TYPE = "Expansion";

    /** An event listener bean. */
    private static Object bean;

    /** The component builder data. */
    private ComponentBuilderData builderData;

    /** The event manager. */
    private FormEventManagerTestImpl eventManager;

    /** The tag to be tested. */
    private EventRegistrationTagTestImpl tag;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        bean = EasyMock.createMock(EventListener.class);
        EasyMock.replay(bean);
    }

    @Before
    public void setUp() throws Exception
    {
        JellyContext context = new JellyContext();
        builderData = new ComponentBuilderData();
        builderData.initializeForm(new TransformerContextImpl(),
                new BeanBindingStrategy());
        builderData.put(context);
        eventManager = new FormEventManagerTestImpl();
        builderData.setEventManager(eventManager);
        eventManager.setComponentStore(builderData.getComponentStore());
        tag = new EventRegistrationTagTestImpl();
        tag.setContext(context);
        tag.setAttribute("eventType", TYPE);
    }

    /**
     * Tests whether the registration of the listener is performed correctly.
     *
     * @param expectedComponent the expected component name
     */
    private void checkRegistration(String expectedComponent)
            throws JellyTagException, FormBuilderException
    {
        assertTrue("Wrong result", tag.passResults(bean));
        assertNull("Event manager already invoked",
                eventManager.addListenerComponent);
        builderData.invokeCallBacks();
        assertEquals("Wrong component name", expectedComponent,
                eventManager.addListenerComponent);
        assertEquals("Wrong event type", TYPE, eventManager.addListenerType);
    }

    /**
     * Tests whether the correct properties to be ignored are set.
     */
    @Test
    public void testIgnoreProperties()
    {
        final String[] props = {
                "eventType", "component", "targetBean", "multiple",
                "ignoreFail"
        };
        Set<String> ignoreProps = tag.getIgnorePropertySet();
        for (String p : props)
        {
            assertTrue("No ignore property: " + p, ignoreProps.contains(p));
        }
    }

    /**
     * Tests whether the correct base class of the bean is set.
     */
    @Test
    public void testBaseClass()
    {
        assertEquals("Wrong base class", EventListener.class, tag
                .getBaseClass());
    }

    /**
     * Tests a tag without the event type attribute. This should cause an
     * exception.
     */
    @Test
    public void testPassResultsNoEventType() throws JellyTagException
    {
        tag.setAttribute("eventType", null);
        try
        {
            tag.passResults(bean);
        }
        catch (MissingAttributeException max)
        {
            assertTrue("Wrong attribute: " + max, max.getMessage().indexOf(
                    "eventType") >= 0);
        }
    }

    /**
     * Tests the behavior of the tag if no component is specified.
     */
    @Test
    public void testPassResultsNoComponent() throws JellyTagException
    {
        assertFalse("Wrong result", tag.passResults(bean));
    }

    /**
     * Tests the behavior of passResults() if the component attribute is
     * specified.
     */
    @Test
    public void testPassResultsComponentName() throws JellyTagException,
            FormBuilderException
    {
        tag.setAttribute("component", COMPONENT);
        checkRegistration(COMPONENT);
    }

    /**
     * Tests whether the component name can be obtained from a parent tag.
     */
    @Test
    public void testPassResultsParentComponent() throws JellyTagException,
            FormBuilderException
    {
        TextFieldTag parent = new TextFieldTag();
        parent.setContext(tag.getContext());
        parent.setName(COMPONENT);
        tag.setParent(parent);
        checkRegistration(COMPONENT);
    }

    /**
     * Tests whether an event listener can be registered at multiple components.
     */
    @Test
    public void testPassResultsMultiple() throws JellyTagException,
            FormBuilderException
    {
        tag.setAttribute("multiple", Boolean.TRUE);
        checkRegistration(null);
    }

    /**
     * Tests the behavior of the tag if both a component name and the multiple
     * attribute are specified. This should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testPassResultsMultipleAndName() throws JellyTagException
    {
        tag.setAttribute("multiple", Boolean.TRUE);
        tag.setAttribute("component", COMPONENT);
        tag.passResults(bean);
    }

    /**
     * Tests the behavior of the tag if both a bean name and the multiple
     * attribute are specified. This should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testPassResultsMultipleAndTargetBean() throws JellyTagException
    {
        tag.setAttribute("multiple", Boolean.TRUE);
        tag.setAttribute("targetBean", TARGET_BEAN_NAME);
        tag.passResults(bean);
    }

    /**
     * Tests the behavior if both a component and a bean name are specified.
     * This should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testPassResultsComponentAndBean() throws JellyTagException
    {
        tag.setAttribute("component", COMPONENT);
        tag.setAttribute("targetBean", TARGET_BEAN_NAME);
        tag.passResults(bean);
    }

    /**
     * Tests whether the callback throws an exception if registration is not
     * possible.
     */
    @Test(expected = FormBuilderException.class)
    public void testPassResultsFail() throws JellyTagException,
            FormBuilderException
    {
        tag.setAttribute("component", COMPONENT);
        eventManager.addListenerResult = 0;
        tag.passResults(bean);
        builderData.invokeCallBacks();
    }

    /**
     * Tests whether an exception caused by a failed registration can be
     * suppressed.
     */
    @Test
    public void testPassResultsFailIgnore() throws JellyTagException,
            FormBuilderException
    {
        tag.setAttribute("component", COMPONENT);
        tag.setAttribute("ignoreFail", Boolean.TRUE);
        eventManager.addListenerResult = 0;
        checkRegistration(COMPONENT);
    }

    /**
     * Tests whether a listener can be added to a bean in the context.
     */
    @Test
    public void testPassResultsTargetBean() throws JellyTagException,
            FormBuilderException
    {
        BeanContext bc = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(bc.getBean(TARGET_BEAN_NAME)).andReturn(TARGET_BEAN);
        EasyMock.replay(bc);
        builderData.setBeanContext(bc);
        tag.setAttribute("targetBean", TARGET_BEAN_NAME);
        assertTrue("Wrong result", tag.passResults(bean));
        assertNull("Listener type already set", eventManager.addListenerType);
        builderData.invokeCallBacks();
        assertEquals("Wrong target object", TARGET_BEAN,
                eventManager.addListenerTarget);
        assertEquals("Wrong event type", TYPE, eventManager.addListenerType);
        EasyMock.verify(bc);
    }

    /**
     * Tests whether an invalid bean name is detected and causes an exception.
     */
    @Test
    public void testPassResultsTargetBeanInvalid() throws JellyTagException,
            FormBuilderException
    {
        BeanContext bc = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(bc.getBean(TARGET_BEAN_NAME)).andThrow(
                new InjectionException());
        EasyMock.replay(bc);
        builderData.setBeanContext(bc);
        tag.setAttribute("targetBean", TARGET_BEAN_NAME);
        assertTrue("Wrong result", tag.passResults(bean));
        try
        {
            builderData.invokeCallBacks();
            fail("Invalid bean name not detected!");
        }
        catch (FormBuilderException fex)
        {
            assertNull("EventManager was called", eventManager.addListenerType);
            EasyMock.verify(bc);
        }
    }

    /**
     * Tests whether an exception caused by an invalid bean name is ignored if
     * the corresponding attribute is set.
     */
    @Test
    public void testPassResultsTargetBeanInvalidIgnore()
            throws JellyTagException, FormBuilderException
    {
        BeanContext bc = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(bc.getBean(TARGET_BEAN_NAME)).andThrow(
                new InjectionException());
        EasyMock.replay(bc);
        builderData.setBeanContext(bc);
        tag.setAttribute("targetBean", TARGET_BEAN_NAME);
        tag.setAttribute("ignoreFail", Boolean.TRUE);
        assertTrue("Wrong result", tag.passResults(bean));
        builderData.invokeCallBacks();
        assertNull("EventManager was called", eventManager.addListenerType);
        EasyMock.verify(bc);
    }

    /**
     * Tests whether the result of the listener registration is taken into
     * account.
     */
    @Test
    public void testPassResultsTargetBeanFail() throws JellyTagException,
            FormBuilderException
    {
        BeanContext bc = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(bc.getBean(TARGET_BEAN_NAME)).andReturn(TARGET_BEAN);
        EasyMock.replay(bc);
        builderData.setBeanContext(bc);
        tag.setAttribute("targetBean", TARGET_BEAN_NAME);
        eventManager.addListenerObjResult = false;
        assertTrue("Wrong result", tag.passResults(bean));
        try
        {
            builderData.invokeCallBacks();
            fail("Failed invocation not detected!");
        }
        catch (FormBuilderException fex)
        {
            assertEquals("Wrong target object", TARGET_BEAN,
                    eventManager.addListenerTarget);
            assertEquals("Wrong event type", TYPE, eventManager.addListenerType);
            EasyMock.verify(bc);
        }
    }

    /**
     * Tests whether a failed registration is ignored if the attribute is set.
     */
    @Test
    public void testPassResultsTargetBeanFailIgnore() throws JellyTagException,
            FormBuilderException
    {
        BeanContext bc = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(bc.getBean(TARGET_BEAN_NAME)).andReturn(TARGET_BEAN);
        EasyMock.replay(bc);
        builderData.setBeanContext(bc);
        tag.setAttribute("targetBean", TARGET_BEAN_NAME);
        tag.setAttribute("ignoreFail", Boolean.TRUE);
        eventManager.addListenerObjResult = false;
        assertTrue("Wrong result", tag.passResults(bean));
        assertNull("Listener type already set", eventManager.addListenerType);
        builderData.invokeCallBacks();
        assertEquals("Wrong target object", TARGET_BEAN,
                eventManager.addListenerTarget);
        assertEquals("Wrong event type", TYPE, eventManager.addListenerType);
        EasyMock.verify(bc);
    }

    /**
     * A specialized event manager implementation used for testing whether the
     * expected listener registration method is called.
     */
    private static class FormEventManagerTestImpl extends FormEventManager
    {
        /** The component name passed to addEventListener(). */
        String addListenerComponent;

        /** The bean passed to addEventListenerToObject(). */
        Object addListenerTarget;

        /** The listener type passed to addEventListener(). */
        String addListenerType;

        /** The result to be returned by addEventListener(). */
        int addListenerResult = 1;

        /** The result to be returned by addEventListenerToObject(). */
        boolean addListenerObjResult = true;

        public FormEventManagerTestImpl()
        {
            super(new PlatformEventManagerImpl());
        }

        /**
         * Records this invocation.
         */
        @Override
        public int addEventListener(String componentName, String listenerType,
                EventListener l)
        {
            assertEquals("Wrong listener bean", bean, l);
            addListenerComponent = componentName;
            addListenerType = listenerType;
            return addListenerResult;
        }

        /**
         * Records this invocation.
         */
        @Override
        public boolean addEventListenerToObject(Object target,
                String listenerType, EventListener l)
        {
            assertEquals("Wrong listener bean", bean, l);
            addListenerTarget = target;
            addListenerType = listenerType;
            return addListenerObjResult;
        }
    }

    /**
     * A test implementation of EventRegistrationTag.
     */
    private static class EventRegistrationTagTestImpl extends
            EventRegistrationTag
    {
        @Override
        @SuppressWarnings("unchecked")
        public Set<String> getIgnorePropertySet()
        {
            return super.getIgnorePropertySet();
        }
    }
}

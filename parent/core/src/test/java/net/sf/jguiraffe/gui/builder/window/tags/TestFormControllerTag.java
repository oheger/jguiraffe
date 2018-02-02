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
package net.sf.jguiraffe.gui.builder.window.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormController;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerFieldStatusListener;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerFormListener;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerValidationListener;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormValidationTrigger;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormValidationTriggerNone;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for FormControllerTag.
 *
 * @author Oliver Heger
 * @version $Id: TestFormControllerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFormControllerTag
{
    /** Stores the Jelly context. */
    private JellyContext context;

    /** The tag to be tested. */
    private FormControllerTag tag;

    @Before
    public void setUp() throws Exception
    {
        context = new JellyContext();
        ComponentBuilderData compData = new ComponentBuilderData();
        compData.put(context);
        WindowBuilderData wndData = new WindowBuilderData();
        wndData.put(context);
        wndData.setResultWindow(EasyMock.createNiceMock(Window.class));
        tag = new FormControllerTag();
        tag.setContext(context);
    }

    /**
     * Creates a controller object and initializes the helper objects.
     *
     * @return the controller
     */
    private FormController setUpController()
    {
        FormController ctrl = new FormController();
        ctrl.setValidationTrigger(new FormValidationTriggerNone());
        return ctrl;
    }

    /**
     * Creates a mock object for a bean context and initializes the component
     * builder data with it.
     *
     * @param beanCtx the bean context
     */
    private BeanContext prepareBeanContext()
    {
        BeanContext beanCtx = EasyMock.createMock(BeanContext.class);
        ComponentBuilderData cbd = ComponentBuilderData.get(context);
        cbd.setBeanContext(beanCtx);
        return beanCtx;
    }

    /**
     * Tests whether the base class is correctly set.
     */
    @Test
    public void testGetBaseClass()
    {
        assertEquals("Wrong base class", FormController.class, tag
                .getBaseClass());
    }

    /**
     * Tests whether the component builder data object is correctly initialized.
     */
    @Test
    public void testPassResultsCompBuilderData() throws JellyTagException
    {
        FormController ctrl = setUpController();
        tag.passResults(ctrl);
        assertEquals("Wrong comp builder data", ComponentBuilderData
                .get(context), ctrl.getComponentBuilderData());
    }

    /**
     * Tests whether the window builder data object is correctly initialized.
     */
    @Test
    public void testPassResultsWndBuilderData() throws JellyTagException
    {
        FormController ctrl = setUpController();
        tag.passResults(ctrl);
        assertEquals("Wrong wnd builder data", WindowBuilderData.get(context),
                ctrl.getWindowBuilderData());
    }

    /**
     * Tests passResults() when all helper objects are set. In this case they
     * must not be touched.
     */
    @Test
    public void testPassResultsHelperObjectsSet() throws JellyTagException
    {
        FormController ctrl = setUpController();
        FormValidationTrigger fvt = ctrl.getValidationTrigger();
        tag.passResults(ctrl);
        assertSame("Trigger was changed", fvt, ctrl.getValidationTrigger());
    }

    /**
     * Tests the passResults() method when the validation trigger has to be
     * initialized.
     */
    @Test
    public void testPassResultsInitValidationTrigger() throws JellyTagException
    {
        FormController ctrl = setUpController();
        ctrl.setValidationTrigger(null);
        FormValidationTrigger trigger = new FormValidationTriggerNone();
        BeanContext bc = prepareBeanContext();
        EasyMock.expect(bc.getBean(FormControllerTag.BEAN_VALIDATION_TRIGGER))
                .andReturn(trigger);
        EasyMock.replay(bc);
        tag.passResults(ctrl);
        assertEquals("Trigger was not set", trigger, ctrl
                .getValidationTrigger());
        // test whether the trigger does really nothing
        trigger.initTrigger(ctrl);
        EasyMock.verify(bc);
    }

    /**
     * Tests whether the form bean is initialized if the corresponding property
     * is set.
     */
    @Test
    public void testPassResultsFormBean() throws JellyTagException
    {
        BeanContext bc = prepareBeanContext();
        final String formBeanName = "myFormBean";
        final Object formBean = new Object();
        EasyMock.expect(bc.getBean(formBeanName)).andReturn(formBean);
        EasyMock.replay(bc);
        FormController ctrl = setUpController();
        tag.setAttribute("formBeanName", formBeanName);
        tag.passResults(ctrl);
        assertEquals("Form bean not set", formBean, WindowBuilderData.get(
                tag.getContext()).getFormBean());
        EasyMock.verify(bc);
    }

    /**
     * Tests whether validation listeners are added to the controller if
     * defined.
     */
    @Test
    public void testPassResultsValidationListeners() throws JellyTagException
    {
        final int listenerCount = 8;
        FormControllerValidationListener[] listeners = new FormControllerValidationListener[listenerCount];
        for (int i = 0; i < listenerCount; i++)
        {
            listeners[i] = EasyMock
                    .createMock(FormControllerValidationListener.class);
            EasyMock.replay(listeners[i]);
            tag.addValidationListener(listeners[i]);
        }
        FormController ctrl = setUpController();
        tag.passResults(ctrl);
        checkListeners(listeners, ctrl.getValidationListeners());
        EasyMock.verify((Object[]) listeners);
    }

    /**
     * Tests whether field status listeners are added to the controller if
     * defined.
     */
    @Test
    public void testPassResultsFieldStatusListeners() throws JellyTagException
    {
        final int listenerCount = 4;
        FormControllerFieldStatusListener[] listeners = new FormControllerFieldStatusListener[listenerCount];
        for (int i = 0; i < listenerCount; i++)
        {
            listeners[i] = EasyMock
                    .createMock(FormControllerFieldStatusListener.class);
            EasyMock.replay(listeners[i]);
            tag.addFieldStatusListener(listeners[i]);
        }
        FormController ctrl = setUpController();
        tag.passResults(ctrl);
        checkListeners(listeners, ctrl.getFieldStatusListeners());
        EasyMock.verify((Object[]) listeners);
    }

    /**
     * Tests whether form listeners are added to the controller if defined.
     */
    @Test
    public void testPassResultsFormListeners() throws JellyTagException
    {
        final int listenerCount = 5;
        FormControllerFormListener[] listeners = new FormControllerFormListener[listenerCount];
        for (int i = 0; i < listenerCount; i++)
        {
            listeners[i] = EasyMock
                    .createMock(FormControllerFormListener.class);
            EasyMock.replay(listeners[i]);
            tag.addFormListener(listeners[i]);
        }
        FormController ctrl = setUpController();
        tag.passResults(ctrl);
        checkListeners(listeners, ctrl.getFormListeners());
        EasyMock.verify((Object[]) listeners);
    }

    /**
     * Helper method for comparing arrays with event listeners. The problem here
     * is that the actual event listeners can be in a different order than the
     * expected listeners.
     *
     * @param expected an array with the expected event listeners
     * @param actual an array with the actual event listeners
     */
    private static void checkListeners(Object[] expected, Object[] actual)
    {
        assertEquals("Wrong number of listeners", expected.length,
                actual.length);
        Set<Object> set = new HashSet<Object>(Arrays.asList(expected));
        for (Object o : actual)
        {
            assertTrue("Invalid listener: " + o, set.remove(o));
        }
    }
}

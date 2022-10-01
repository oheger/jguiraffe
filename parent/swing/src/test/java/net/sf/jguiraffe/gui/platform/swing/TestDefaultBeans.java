/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.swing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.app.Application;
import net.sf.jguiraffe.gui.app.ApplicationContext;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.DefaultToolTipManager;
import net.sf.jguiraffe.gui.builder.enablers.ElementEnabler;
import net.sf.jguiraffe.gui.builder.impl.JellyBuilder;
import net.sf.jguiraffe.gui.builder.window.ctrl.ColorFieldMarker;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormValidationTriggerFocus;
import net.sf.jguiraffe.gui.forms.BindingStrategy;
import net.sf.jguiraffe.gui.forms.FormValidationMessageFormat;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.locators.Locator;
import net.sf.jguiraffe.locators.LocatorConverter;
import net.sf.jguiraffe.transform.DefaultValidationMessageHandler;

import org.apache.commons.beanutils.Converter;
import org.junit.Before;
import org.junit.Test;

/**
 * A test class for validating the default beans definition file shipped with
 * the framework. In this file a bunch of default beans are defined. These beans
 * can be used by client applications out of the box. Alternatively they can be
 * overridden with custom beans. This test class checks whether the expected
 * default beans can be obtained from the application's global bean context.
 *
 * @author Oliver Heger
 * @version $Id: TestDefaultBeans.java 211 2012-07-10 19:49:13Z oheger $
 */
public class TestDefaultBeans
{
    /** Constant for the name of the configuration file. */
    private static final String CONFIG_FILE = "testappconfigfactorymin.xml";

    /** Stores the global bean context. */
    private BeanContext context;

    @Before
    public void setUp() throws Exception
    {
        Application app = new Application();
        app.setConfigResourceName(CONFIG_FILE);
        Application.startup(app, new String[0]);
        ApplicationContext appCtx = app.getApplicationContext();
        context = appCtx.getBeanContext();
    }

    /**
     * Tests whether the bean with the given name can be found in the bean
     * context. It is also checked whether the class of the bean is correct.
     *
     * @param name the name of the bean
     * @param expClass the expected bean class
     * @return the bean obtained from the context
     */
    private <T> T checkBean(String name, Class<T> expClass)
    {
        Object bean = context.getBean(name);
        assertEquals("Wrong bean class", expClass, bean.getClass());
        return expClass.cast(bean);
    }

    /**
     * Tests whether the validation message handler can be obtained.
     */
    @Test
    public void testValidationMessageHandler()
    {
        checkBean("jguiraffe.validationMessageHandler",
                DefaultValidationMessageHandler.class);
    }

    /**
     * Tests whether the form validation message format object can be obtained.
     */
    @Test
    public void testFormValidationMessageFormat()
    {
        checkBean("jguiraffe.validationMessageFormat",
                FormValidationMessageFormat.class);
    }

    /**
     * Tests whether the form validation trigger can be obtained.
     */
    @Test
    public void testFormValidationTrigger()
    {
        Object trigger = checkBean("jguiraffe.formValidationTrigger",
                FormValidationTriggerFocus.class);
        assertNotSame("Bean is a singleton", trigger, context
                .getBean("jguiraffe.formValidationTrigger"));
    }

    /**
     * Tests whether the field marker can be obtained.
     */
    @Test
    public void testFieldMarker()
    {
        ColorFieldMarker marker = checkBean("jguiraffe.fieldMarker",
                ColorFieldMarker.class);
        assertNotNull("Invalid BG not set", marker.getInvalidBackground());
        assertNotNull("Invalid FG not set", marker.getInvalidForeground());
        assertNotNull("Not visited BG not set", marker
                .getNotVisitedInvalidBackground());
        assertNotNull("Not visited FG not set", marker
                .getNotVisitedInvalidForeground());
        assertNotSame("Bean is a singleton", marker, context
                .getBean("jguiraffe.fieldMarker"));
    }

    /**
     * Tests whether the binding strategy can be obtained.
     */
    @Test
    public void testBindingStrategy()
    {
        BindingStrategy strat = checkBean(Application.BEAN_BINDING_STRATEGY,
                BeanBindingStrategy.class);
        assertSame("Bean is not singleton", strat, checkBean(
                Application.BEAN_BINDING_STRATEGY, BeanBindingStrategy.class));
    }

    /**
     * Tests whether a component builder data object can be obtained.
     */
    @Test
    public void testComponentBuilderData()
    {
        ComponentBuilderData data = checkBean("jguiraffe.componentBuilderData",
                ComponentBuilderData.class);
        assertTrue("Wrong tool tip manager: " + data.getToolTipManager(), data
                .getToolTipManager() instanceof DefaultToolTipManager);
        DefaultToolTipManager ttm = (DefaultToolTipManager) data
                .getToolTipManager();
        assertEquals("Wrong data reference", data, ttm
                .getComponentBuilderData());
        assertNotSame("A singleton", data, checkBean(
                "jguiraffe.componentBuilderData", ComponentBuilderData.class));
    }

    /**
     * Tests whether the builder can be obtained.
     */
    @Test
    public void testBuilder()
    {
        JellyBuilder builder =
                checkBean("jguiraffe.builder", JellyBuilder.class);
        assertNotNull("No component manager set", builder.getComponentManager());
        assertNotNull("No action manager set", builder.getActionManager());
        assertNotNull("No window manager set", builder.getWindowManager());
        Map<Class<?>, Converter> baseClassConverters =
                builder.getDefaultBaseClassConverters();
        assertTrue("No locator converter",
                baseClassConverters.containsKey(Locator.class));
        assertTrue("No enabler converter",
                baseClassConverters.containsKey(ElementEnabler.class));
    }

    /**
     * Tests whether the builder bean is no singleton.
     */
    @Test
    public void testBuilderNoSingleton()
    {
        JellyBuilder builder =
                checkBean("jguiraffe.builder", JellyBuilder.class);
        assertNotSame("Singleton", builder,
                context.getBean("jguiraffe.builder"));
    }

    /**
     * Tests whether the default locator converter has been setup correctly.
     */
    @Test
    public void testLocatorConverter()
    {
        LocatorConverter conv = context.getBean(LocatorConverter.class);
        assertSame("Wrong CLP",
                context.getBean("jguiraffe.classLoaderProvider"),
                conv.getClassLoaderProvider());
    }
}

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
package net.sf.jguiraffe.gui.builder.window.tags;

import java.util.Iterator;

import net.sf.jguiraffe.di.impl.DefaultBeanContext;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.gui.app.ApplicationBuilderData;
import net.sf.jguiraffe.gui.builder.action.ActionManagerImpl;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentManagerImpl;
import net.sf.jguiraffe.gui.builder.impl.JellyBuilder;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowClosingStrategy;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;
import net.sf.jguiraffe.gui.builder.window.WindowImpl;
import net.sf.jguiraffe.gui.builder.window.WindowListener;
import net.sf.jguiraffe.gui.builder.window.WindowManagerImpl;
import net.sf.jguiraffe.gui.builder.window.ctrl.ColorFieldMarker;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormController;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerFieldStatusListener;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerValidationListener;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.locators.ClassPathLocator;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;

/**
 * Test class for WindowControllerTag.
 *
 * @author Oliver Heger
 * @version $Id: TestWindowControllerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestWindowControllerTag extends AbstractWindowTagTest
{
    /** Constant for the name of the test script. */
    protected static final String SCRIPT = "/jelly_scripts/windowctrl.jelly";

    /** Constant for the simple controller builder. */
    private static final String BUILDER_SIMPLE = "CTRL_SIMPLE";

    /** Constant for the window controller builder. */
    private static final String BUILDER_WINDOW = "CTRL_WINDOW";

    /** Constant for the window builder data controller builder. */
    private static final String BUILDER_WINDOWBUILDER = "CTRL_WINDOWDATA";

    /** Constant for the form controller builder. */
    private static final String BUILDER_FORM = "CTRL_FORM";

    /** Constant for the form bean controller builder. */
    private static final String BUILDER_FORMBEAN = "CTRL_FORMBEAN";

    /** Constant for the form builder data controller builder. */
    private static final String BUILDER_FORMBUILDER = "CTRL_FORMBUILDER";

    /** Constant for the source builder. */
    private static final String BUILDER_SOURCE = "CTRL_SOURCE";

    /** Constant for the no class builder. */
    private static final String BUILDER_NOCLASS = "ERR_NOCLASS";

    /** Constant for the nested error builder. */
    private static final String BUILDER_NESTED = "ERR_NESTED";

    /** Constant for the closing strategy builder. */
    private static final String BUILDER_CLOSINGSTR = "CTRL_CLOSINGSTRAT";

    /** Constant for the listener builder. */
    private static final String BUILDER_LISTENER = "CTRL_LISTENER";

    /** Constant for the not nested builder. */
    private static final String BUILDER_NOTNESTED = "CTRL_NOTNESTED";

    /** Constant for the inject controls builder. */
    private static final String BUILDER_INJECTCTRLS = "CTRL_INJECTCTRLS";

    /** Constant for the form controller builder.*/
    private static final String BUILDER_FORMCTRL = "CTRL_FORMCTRL";

    /** Constant for the form controller listener builder. */
    private static final String BUILDER_FORMCTRLLIST = "CTRL_FORMCTRLLISTENER";

    /** Constant for the name of the test window. */
    private static final String WINDOW_TITLE = "Testwindow";

    /** Constant for a test form bean. */
    private static final String FORM_BEAN = "A test form bean";

    /** Constant for the name of the controller source. */
    private static final String SOURCE = "windowCtrl";

    /** Stores the builder data object. */
    private ApplicationBuilderData data;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        data = new ApplicationBuilderData();
        data.setParentContext(new DefaultBeanContext(new DefaultBeanStore()));
        data.setTransformerContext(new TransformerContextImpl());
        data.setBindingStrategy(new BeanBindingStrategy());
    }

    /**
     * Tests creating a simple controller without further initialization.
     */
    public void testCreateController() throws Exception
    {
        WindowController ctrl = fetchController(BUILDER_SIMPLE);
        assertNull("A window was set", ctrl.getWindow());
        assertNull("A form was set", ctrl.getForm());
        assertNull("A form bean was set", ctrl.getFormBean());
        assertNull("A form builder data object was set", ctrl
                .getFormBuilderData());
        assertNull("A window builder data object was set", ctrl
                .getWindowBuilderData());
    }

    /**
     * Tests creating a controller that is initialized with the window.
     */
    public void testCreateControllerWithWindow() throws Exception
    {
        WindowController ctrl = fetchController(BUILDER_WINDOW);
        assertNotNull("Window was not initialized", ctrl.getWindow());
        assertNull("Window builder data set", ctrl.getWindowBuilderData());
        assertEquals("Wrong window was set", WINDOW_TITLE, ctrl.getWindow()
                .getTitle());
    }

    /**
     * Tests creating a controller that is initialized with the window builder
     * data object.
     */
    public void testCreateControllerWithWindowBuilderData() throws Exception
    {
        WindowController ctrl = fetchController(BUILDER_WINDOWBUILDER);
        assertNotNull("No window builder data", ctrl.getWindowBuilderData());
        assertSame("Window builder data was not correctly initialized", data
                .getBuilderContext().getBean(
                        WindowBuilderData.KEY_WINDOW_BUILDER_DATA), ctrl
                .getWindowBuilderData());
    }

    /**
     * Tests creating a controller that is initialized with the form.
     */
    public void testCreateControllerWithForm() throws Exception
    {
        WindowController ctrl = fetchController(BUILDER_FORM);
        assertNotNull("Form was not set", ctrl.getForm());
        assertSame("Wrong form was set", data.getBuilderContext().getBean(
                ComponentBuilderData.KEY_FORM), ctrl.getForm());
    }

    /**
     * Tests creating a controller that is initialized with the form bean.
     */
    public void testCreateControllerWithFormBean() throws Exception
    {
        data.setFormBean(FORM_BEAN);
        WindowController ctrl = fetchController(BUILDER_FORMBEAN);
        assertEquals("Incorrect form bean", FORM_BEAN, ctrl.getFormBean());
    }

    /**
     * Tests creating a controller that is initialized with the form builder
     * data object.
     */
    public void testCreateControllerWithFormBuilder() throws Exception
    {
        WindowController ctrl = fetchController(BUILDER_FORMBUILDER);
        assertNotNull("No component builder data set", ctrl
                .getFormBuilderData());
        assertSame("Incorrect form builder data", data.getBuilderContext()
                .getBean(ComponentBuilderData.KEY_COMPONENT_BUILDER_DATA), ctrl
                .getFormBuilderData());
    }

    /**
     * Tests whether a controller can be directly obtained from the Jelly
     * context.
     */
    public void testFetchControllerFromSource() throws Exception
    {
        WindowController srcCtrl = new WindowController();
        WindowImpl wnd = new WindowImpl();
        srcCtrl.setWindow(wnd);
        context.setVariable(SOURCE, srcCtrl);
        WindowController ctrl = fetchController(BUILDER_SOURCE);
        assertSame("Incorrect controller returned", srcCtrl, ctrl);
        assertSame("Window was not set", wnd, ctrl.getWindow());
    }

    /**
     * Tests creating a controller with a missing class attribute. This should
     * throw an exception.
     */
    public void testCreateControllerWithoutClass() throws Exception
    {
        errorScript(SCRIPT, BUILDER_NOCLASS,
                "Builder without class could be created!");
    }

    /**
     * Tests processing an incorrectly nested controller tag. This should throw
     * an exception because no reference to the controller's window can be
     * obtained.
     */
    public void testNotNestedControllerTag() throws Exception
    {
        errorScript(SCRIPT, BUILDER_NESTED, "Could process not nested tag!");
    }

    /**
     * Tests if a controller that implements the closing strategy interface will
     * be automatically set as the window's closing strategy.
     */
    public void testControllerWithClosingStrategy() throws Exception
    {
        WindowControllerWithClosingStrategy ctrl = (WindowControllerWithClosingStrategy) fetchController(
                BUILDER_CLOSINGSTR, WindowControllerWithClosingStrategy.class);
        WindowImpl wnd = (WindowImpl) ctrl.getWindow();
        assertSame("Controller not set as closing strategy", ctrl, wnd
                .getWindowClosingStrategy());
    }

    /**
     * Tests if a controller that implements the window listener interface will
     * be automatically added as a listener for this window.
     */
    public void testControllerWithListener() throws Exception
    {
        WindowListenerController ctrl = (WindowListenerController) fetchController(
                BUILDER_LISTENER, WindowListenerController.class);
        WindowImpl wnd = (WindowImpl) ctrl.getWindow();
        Iterator<WindowListener> it = wnd.getWindowListeners().iterator();
        assertTrue("No listener registered", it.hasNext());
        assertSame("Controller not added as window listener", ctrl, it.next());
    }

    /**
     * Tests a controller tag that is not nested inside a window tag, but can
     * obtain its window from the window builder data object.
     */
    public void testNotNestedController() throws Exception
    {
        runScript(BUILDER_NOTNESTED);
        Object ctrl = data.getBuilderContext().getBean(SOURCE);
        assertNotNull("Controller not bound to context!", ctrl);
        assertTrue("Controller is of wrong class",
                ctrl instanceof WindowController);
    }

    /**
     * Tests injecting some controls.
     */
    public void testInjectControls() throws Exception
    {
        WindowCtrlController ctrl = (WindowCtrlController) fetchController(
                BUILDER_INJECTCTRLS, WindowCtrlController.class);
        assertNotNull("Text control not set", ctrl.getTextControl());
        assertNotNull("Static text control not set", ctrl.getStatTxtControl());
    }

    /**
     * Tests the fetchWindow() method when the window cannot be found.
     */
    public void testFetchWindowNotFound() throws JellyTagException
    {
        WindowControllerTag tag = new WindowControllerTag();
        tag.setContext(context);
        try
        {
            tag.fetchWindow();
            fail("Non existing window not detected!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }

    /**
     * Tests a formController tag.
     */
    public void testFormController() throws Exception
    {
        FormController ctrl = (FormController) fetchController(
                BUILDER_FORMCTRL, FormController.class);
        assertNotNull("Comp data not set", ctrl.getComponentBuilderData());
        assertNotNull("Wnd data not set", ctrl.getWindowBuilderData());
        assertNotNull("No validation trigger set", ctrl.getValidationTrigger());
    }

    /**
     * Tests a formController tag with a listener declaration.
     */
    public void testFormControllerListener() throws Exception
    {
        FormController ctrl = (FormController) fetchController(
                BUILDER_FORMCTRLLIST, FormController.class);
        FormControllerValidationListener[] valLst = ctrl
                .getValidationListeners();
        assertEquals("Wrong number of validation listeners", 1, valLst.length);
        FormControllerFieldStatusListener[] fldLst = ctrl
                .getFieldStatusListeners();
        assertEquals("Wrong number of field listeners", 1, fldLst.length);
        assertTrue("Wrong listener type: " + valLst[0],
                valLst[0] instanceof ColorFieldMarker);
        assertEquals("Different listeners", valLst[0], fldLst[0]);
    }

    /**
     * Executes the test script with the specified builder name and returns the
     * main window.
     *
     * @param builderName the name of the builder
     * @return the main window
     * @throws Exception if an error occurs
     */
    protected Window runScript(String builderName) throws Exception
    {
        JellyBuilder builder = new JellyBuilder()
        {
            @Override
            protected JellyContext createJellyContext()
            {
                return context;
            }
        };
        builder.setComponentManager(new ComponentManagerImpl());
        builder.setActionManager(new ActionManagerImpl());
        builder.setWindowManager(new WindowManagerImpl());
        builder.setName(builderName);
        return builder.buildWindow(ClassPathLocator.getInstance(SCRIPT), data);
    }

    /**
     * Executes the test script with the specified builder name and obtains the
     * window controller. The controller's class is checked.
     *
     * @param builderName the name of the builder
     * @param expectedClass the expected class of the controller
     * @return the controller
     * @throws Exception if an error occurs
     */
    protected Object fetchController(String builderName, Class<?> expectedClass)
            throws Exception
    {
        Object ctrl = runScript(builderName).getWindowController();
        assertNotNull("No controller was created!", ctrl);
        assertTrue("Controller class is not as expected", expectedClass
                .isAssignableFrom(ctrl.getClass()));
        return ctrl;
    }

    /**
     * A convenience method for obtaining a window's controller if it is of type
     * <code>WindowController</code>. Calls the overloaded method with
     * corresponding arguments.
     *
     * @param builderName the name of the builder
     * @return the controller
     * @throws Exception if an error occurs
     */
    protected WindowController fetchController(String builderName)
            throws Exception
    {
        return (WindowController) fetchController(builderName,
                WindowController.class);
    }

    /**
     * A simple test bean acting as window controller. The bean supports the
     * properties that can be defined in the controller tag.
     */
    public static class WindowController
    {
        private Window window;

        private Form form;

        private ComponentBuilderData formBuilderData;

        private WindowBuilderData windowBuilderData;

        private String formBean;

        public Form getForm()
        {
            return form;
        }

        public void setForm(Form form)
        {
            this.form = form;
        }

        public String getFormBean()
        {
            return formBean;
        }

        public void setFormBean(String formBean)
        {
            this.formBean = formBean;
        }

        public ComponentBuilderData getFormBuilderData()
        {
            return formBuilderData;
        }

        public void setFormBuilderData(ComponentBuilderData formBuilderData)
        {
            this.formBuilderData = formBuilderData;
        }

        public Window getWindow()
        {
            return window;
        }

        public void setWindow(Window window)
        {
            this.window = window;
        }

        /**
         * An overloaded method that should not be called by method injection.
         */
        public void initWindow(Window window, String test)
        {
            throw new UnsupportedOperationException("Should not be called!");
        }

        /**
         * An overloaded method that should not be called by method injection.
         */
        public void initWindow(String test)
        {
            throw new UnsupportedOperationException("Should not be called!");
        }

        public void initWindow(Window window)
        {
            this.window = window;
        }

        /**
         * A method that will throw a runtime exception. Used for testing
         * exception handling.
         */
        public void initWindowEx(Window window)
        {
            throw new RuntimeException("Just an exception!");
        }

        public WindowBuilderData getWindowBuilderData()
        {
            return windowBuilderData;
        }

        public void setWindowBuilderData(WindowBuilderData windowBuilderData)
        {
            this.windowBuilderData = windowBuilderData;
        }
    }

    /**
     * A test window controller bean that also implements the closing strategy
     * interface.
     */
    public static class WindowControllerWithClosingStrategy extends
            WindowController implements WindowClosingStrategy
    {
        public boolean canClose(Window window)
        {
            // just a dummy implementation
            return true;
        }
    }

    /**
     * A test window controller bean that also implements the window listener
     * interface.
     */
    public static class WindowListenerController extends WindowController
            implements WindowListener
    {
        public void windowActivated(WindowEvent event)
        {
        }

        public void windowClosed(WindowEvent event)
        {
        }

        public void windowDeactivated(WindowEvent event)
        {
        }

        public void windowDeiconified(WindowEvent event)
        {
        }

        public void windowIconified(WindowEvent event)
        {
        }

        public void windowOpened(WindowEvent event)
        {
        }

        public void windowClosing(WindowEvent event)
        {
        }
    }

    /**
     * A controller class for testing injection of controls.
     */
    public static class WindowCtrlController
    {
        /** Stores the component handler for the text control. */
        private ComponentHandler<?> textControl;

        /** Stores the component handler for the static text control. */
        private ComponentHandler<?> statTxtControl;

        public ComponentHandler<?> getTextControl()
        {
            return textControl;
        }

        public void setTextControl(ComponentHandler<?> textControl)
        {
            this.textControl = textControl;
        }

        public ComponentHandler<?> getStatTxtControl()
        {
            return statTxtControl;
        }

        public void setStatTxtControl(ComponentHandler<?> statTxtControl)
        {
            this.statTxtControl = statTxtControl;
        }
    }
}

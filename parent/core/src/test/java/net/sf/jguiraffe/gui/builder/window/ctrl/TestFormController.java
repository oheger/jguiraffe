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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.builder.BuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentManagerImpl;
import net.sf.jguiraffe.gui.builder.event.FormActionEvent;
import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormFocusEvent;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManagerImpl;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;
import net.sf.jguiraffe.gui.cmd.Command;
import net.sf.jguiraffe.gui.cmd.CommandQueue;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;
import net.sf.jguiraffe.gui.forms.DefaultFieldHandler;
import net.sf.jguiraffe.gui.forms.FormValidationMessageFormat;
import net.sf.jguiraffe.gui.forms.FormValidator;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.ValidatorWrapper;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.resources.ResourceManager;
import net.sf.jguiraffe.transform.DefaultValidationMessageHandler;
import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationMessageConstants;
import net.sf.jguiraffe.transform.ValidationMessageLevel;
import net.sf.jguiraffe.transform.ValidationResult;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for FormController.
 *
 * @author Oliver Heger
 * @version $Id: TestFormController.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFormController
{
    /** Constant for the name of the test field. */
    private static final String FIELD = "testInputField";

    /** Constant for the name of the OK button. */
    private static final String BTN_OK = "btnOK";

    /** Constant for the name of the cancel button. */
    private static final String BTN_CANCEL = "btnCancel";

    /** Constant for a test validation message. */
    private static final String ERR_MSG = "This is a validation error message!";

    /** Constant for a validation error message template. */
    private static final String ERR_TEMPLATE = "${msg}";

    /** Constant for the caption of the validation message box. */
    private static final String MSGBOX_CAPTION = "Validation errors!";

    /** Stores the component builder data object used by the tests. */
    private ComponentBuilderData compBuilderData;

    /** Stores the window builder data object used by the tests. */
    private WindowBuilderData wndBuilderData;

    /** The form bean. */
    private FormBeanTestImpl formBean;

    /** Stores the mock for the validator. */
    private ValidatorMock validator;

    /** The component handler for the test input field. */
    private ComponentHandlerImpl compHandler;

    /** A mock validation listener. */
    private ValidationListenerTestImpl mockListener;

    /** The form controller to be tested. */
    private FormControllerTestImpl controller;

    @Before
    public void setUp() throws Exception
    {
        controller = new FormControllerTestImpl();
    }

    /**
     * Creates the component builder data object. Some test components will be
     * created to populate the test form. Other helper objects needed by the
     * controller will also be created.
     */
    private void setUpComponents()
    {
        compBuilderData = new ComponentBuilderData();
        compBuilderData.setComponentManager(new ComponentManagerImpl());
        compBuilderData.initializeForm(new TransformerContextImpl(),
                new BeanBindingStrategy());
        DefaultFieldHandler fh = new DefaultFieldHandler();
        fh.setPropertyName(FIELD);
        compHandler = new ComponentHandlerImpl();
        compHandler.setType(Integer.TYPE);
        compHandler.setComponent("testComponent");
        validator = new ValidatorMock();
        fh.setSyntaxValidator(validator);
        fh.setComponentHandler(compHandler);
        compBuilderData.getForm().addField(FIELD, fh);
        setUpButton(BTN_OK);
        setUpButton(BTN_CANCEL);
        controller.setComponentBuilderData(compBuilderData);
        wndBuilderData = new WindowBuilderData();
        formBean = new FormBeanTestImpl();
        wndBuilderData.setFormBean(formBean);
        wndBuilderData.setResultWindow(EasyMock.createMock(Window.class));
        controller.setWindowBuilderData(wndBuilderData);
    }

    /**
     * Adds a button component to the test form.
     *
     * @param name the name of the button component
     */
    private void setUpButton(String name)
    {
        ComponentHandlerImpl ch = new ComponentHandlerImpl();
        ch.setType(Boolean.TYPE);
        ch.setComponent("BTN_" + name);
        compBuilderData.getComponentStore().addComponentHandler(name, ch);
    }

    /**
     * Creates a test window event.
     *
     * @return the event
     */
    private WindowEvent event()
    {
        return new WindowEvent(this, null, WindowEvent.Type.WINDOW_OPENED);
    }

    /**
     * Creates a focus event for the test field of the specified type.
     *
     * @param type the event type
     * @return the event
     */
    private FormFocusEvent focusEvent(FormFocusEvent.Type type)
    {
        return new FormFocusEvent(this, compBuilderData
                .getComponentHandler(FIELD), FIELD, type);
    }

    /**
     * Creates an action event for the specified button.
     *
     * @param btnName the name of the button
     * @return the action event
     */
    private FormActionEvent actionEvent(String btnName)
    {
        return new FormActionEvent(this, compBuilderData
                .getComponentHandler(btnName), btnName, btnName);
    }

    /**
     * Helper method for retrieving the test platform event manager. This object
     * can be used for testing the event listener registration.
     *
     * @return the test event manager
     */
    private PlatformEventManagerImpl getEventManager()
    {
        return (PlatformEventManagerImpl) compBuilderData.getEventManager()
                .getPlatformEventManager();
    }

    /**
     * Tests a newly created controller.
     */
    @Test
    public void testInit()
    {
        assertNull("OK button is set", controller.getBtnOkName());
        assertNull("A cancel button is set", controller.getBtnCancelName());
        assertNull("Component builder data is set", controller
                .getComponentBuilderData());
        assertNull("Window builder data is set", controller
                .getWindowBuilderData());
        assertNull("A validation trigger is set", controller
                .getValidationTrigger());
        assertNull("A message output object is set", controller
                .getMessageOutput());
        assertNull("An OK command is set", controller.getOkCommand());
        assertNull("A cancel command is set", controller.getCancelCommand());
        assertEquals("Got validation listeners", 0, controller
                .getValidationListeners().length);
        assertEquals("Got field status listeners", 0, controller
                .getFieldStatusListeners().length);
        assertEquals("Got form listeners", 0,
                controller.getFormListeners().length);
    }

    /**
     * Tests querying the form from the controller.
     */
    @Test
    public void testGetForm()
    {
        setUpComponents();
        assertEquals("Wrong form", compBuilderData.getForm(), controller
                .getForm());
    }

    /**
     * Tests querying the form bean.
     */
    @Test
    public void testGetFormBean()
    {
        setUpComponents();
        assertEquals("Wrong form bean", formBean, controller.getFormBean());
    }

    /**
     * Tests the windowOpened() method when no component builder data is set.
     * This should cause an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testWindowOpenedNoCompBuilderData()
    {
        setUpComponents();
        controller.setComponentBuilderData(null);
        controller.windowOpened(event());
    }

    /**
     * Tests the windowOpened() method when no window builder data is set. This
     * should cause an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testWindowOpenedNoWindowBuilderData()
    {
        setUpComponents();
        controller.setWindowBuilderData(null);
        controller.windowOpened(event());
    }

    /**
     * Tests the windowOpened() method when no window object is specified. This
     * should cause an exception.
     */
    @Test
    public void testWindowOpenedNoWindow()
    {
        setUpComponents();
        controller.getWindowBuilderData().setResultWindow(null);
        try
        {
            controller.windowOpened(event());
            fail("Missing window was not detected!");
        }
        catch (IllegalStateException istex)
        {
            assertEquals("Wrong window returned", wndBuilderData
                    .getResultWindow(), controller.getWindow());
        }
    }

    /**
     * Tests whether an action listener for the OK button is registered.
     */
    @Test
    public void testWindowOpenedRegisterBtnOk()
    {
        setUpComponents();
        controller.setBtnOkName(BTN_OK);
        controller.windowOpened(event());
        assertEquals("No action listener for OK button registered", 1,
                getEventManager().getNumberOf(BTN_OK, FormListenerType.ACTION));
        assertEquals("Listener for cancel button registered", 0,
                getEventManager().getNumberOf(BTN_CANCEL,
                        FormListenerType.ACTION));
    }

    /**
     * Tests whether an action listener for the cancel button is registered.
     */
    @Test
    public void testWindowOpenedRegisterBtnCancel()
    {
        setUpComponents();
        controller.setBtnCancelName(BTN_CANCEL);
        controller.windowOpened(event());
        assertEquals("No action listener for cancel button registered", 1,
                getEventManager().getNumberOf(BTN_CANCEL,
                        FormListenerType.ACTION));
    }

    /**
     * Tests whether the controller registers itself as focus listener.
     */
    @Test
    public void testWindowOpenedFocusListener()
    {
        setUpComponents();
        controller.windowOpened(event());
        assertEquals("No focus listener registered", 1, getEventManager()
                .getNumberOf(FIELD, FormListenerType.FOCUS));
    }

    /**
     * Tests whether the validation trigger is called in the setup phase.
     */
    @Test
    public void testWindowOpenedValidationTrigger()
    {
        FormValidationTrigger trigger = EasyMock
                .createMock(FormValidationTrigger.class);
        trigger.initTrigger(controller);
        EasyMock.replay(trigger);
        controller.setValidationTrigger(trigger);
        setUpComponents();
        controller.windowOpened(event());
        EasyMock.verify(trigger);
    }

    /**
     * Tests whether the form's fields are initialized in the setup phase.
     */
    @Test
    public void testWindowOpenedInitFormFields()
    {
        setUpComponents();
        final String testText = "A test text";
        formBean.setTestInputField(testText);
        controller.windowOpened(event());
        ComponentHandler<?> ch = compBuilderData.getComponentHandler(FIELD);
        assertEquals("Field was not initialized", testText, ch.getData());
    }

    /**
     * Tests whether the {@code FormValidator} is set when the window is opened.
     */
    @Test
    public void testWindowOpenedFormValidator()
    {
        FormValidator val = EasyMock.createMock(FormValidator.class);
        EasyMock.replay(val);
        controller.setFormValidator(val);
        setUpComponents();
        assertNull("Got a form validator", controller.getForm()
                .getFormValidator());
        controller.windowOpened(event());
        assertEquals("FormValidator not set", val, controller.getForm()
                .getFormValidator());
        assertEquals("FormValidator not set internally", val, controller
                .getFormValidator());
        EasyMock.verify(val);
    }

    /**
     * Tests whether a {@code FormValidator} can be set if the form already
     * exists.
     */
    @Test
    public void testSetFormValidatorExistingForm()
    {
        FormValidator val = EasyMock.createMock(FormValidator.class);
        EasyMock.replay(val);
        setUpComponents();
        controller.setFormValidator(val);
        assertEquals("FormValidator not set", val, controller.getForm()
                .getFormValidator());
        assertEquals("FormValidator not set internally", val, controller
                .getFormValidator());
        EasyMock.verify(val);
    }

    /**
     * Tests the dummy implementations of the other window events. We can only
     * test here whether an exception is thrown.
     */
    @Test
    public void testWindowEvents()
    {
        WindowEvent event = event();
        controller.windowActivated(event);
        controller.windowClosed(event);
        controller.windowDeactivated(event);
        controller.windowDeiconified(event);
        controller.windowIconified(event);
        controller.windowClosing(event);
    }

    /**
     * Helper method for simulating that the test input field was entered and
     * left. This causes the field to be marked as visited.
     */
    private void visitField()
    {
        controller.focusGained(focusEvent(FormFocusEvent.Type.FOCUS_GAINED));
        controller.focusLost(focusEvent(FormFocusEvent.Type.FOCUS_LOST));
    }

    /**
     * Tests whether visited fields are correctly tracked.
     */
    @Test
    public void testIsFieldVisited()
    {
        setUpComponents();
        assertFalse("Field already visited", controller.isFieldVisited(FIELD));
        visitField();
        assertTrue("Field not visited", controller.isFieldVisited(FIELD));
    }

    /**
     * Prepares a test that involves an invocation of a validation listener.
     *
     * @param status the expected validation status
     */
    private void prepareValidationCheck(FieldValidationStatus status)
    {
        validator.validFlag = status == FieldValidationStatus.NOT_VISITED_VALID
                || status == FieldValidationStatus.VALID;
        mockListener = new ValidationListenerTestImpl(controller);
        controller.addValidationListener(mockListener);
    }

    /**
     * Tests the initial validation that happens when the form is opened.
     */
    @Test
    public void testInitialValidation()
    {
        setUpComponents();
        prepareValidationCheck(FieldValidationStatus.NOT_VISITED_VALID);
        controller.windowOpened(event());
        FormControllerValidationEvent event = mockListener.nextEvent();
        assertTrue("Wrong validation results", event.getValidationResults().isValid());
        mockListener.verifyEmpty();
    }

    /**
     * Tests a validation if the field is invalid, but has not yet been
     * visited.
     */
    @Test
    public void testValidationInvalidNotVisited()
    {
        setUpComponents();
        prepareValidationCheck(FieldValidationStatus.NOT_VISITED_INVALID);
        compHandler.setData("some input");
        assertFalse("Form is valid", controller.validate().isValid());
        assertNull("Form bean contains data", formBean.getTestInputField());
        FormControllerValidationEvent event = mockListener.nextEvent();
        assertFalse("Wrong results", event.getValidationResults().isValid());
        mockListener.verifyEmpty();
    }

    /**
     * Tests whether the last known validation status is stored.
     */
    @Test
    public void testValidationLastStatus()
    {
        setUpComponents();
        prepareValidationCheck(FieldValidationStatus.INVALID);
        visitField();
        assertFalse("Form is valid", controller.validate().isValid());
        FormValidatorResults vres1 = controller.getLastValidationResults();
        validator.validFlag = true;
        assertTrue("Form is invalid", controller.validate().isValid());
        FormValidatorResults vres2 = controller.getLastValidationResults();
        FormControllerValidationEvent event = mockListener.nextEvent();
        assertEquals("Wrong validation results 1", vres1, event
                .getValidationResults());
        event = mockListener.nextEvent();
        assertEquals("Wrong validation results 2", vres2, event
                .getValidationResults());
        mockListener.verifyEmpty();
    }

    /**
     * Tests a valid validation. Data should be copied into the model object.
     */
    @Test
    public void testValidationValid()
    {
        setUpComponents();
        prepareValidationCheck(FieldValidationStatus.NOT_VISITED_VALID);
        final String input = "testInput";
        compHandler.setData(input);
        assertTrue("Form not valid", controller.validate().isValid());
        assertEquals("Data not copied to model", input, formBean
                .getTestInputField());
        FormControllerValidationEvent event = mockListener.nextEvent();
        assertTrue("Not valid", event.getValidationResults().isValid());
        mockListener.verifyEmpty();
    }

    /**
     * Tests whether the results of the last validation are recorded and can be
     * queried later.
     */
    @Test
    public void testGetLastValidationResults()
    {
        setUpComponents();
        prepareValidationCheck(FieldValidationStatus.NOT_VISITED_INVALID);
        compHandler.setData("some input");
        FormValidatorResults vres = controller.validate();
        assertNotNull("No event received", mockListener.nextEvent());
        mockListener.verifyEmpty();
        assertSame("Wrong last validation results", vres, controller
                .getLastValidationResults());
    }

    /**
     * Tests the results returned by getLastValidationResults() if no validation
     * has been performed so far. In this case a valid results object should be
     * returned.
     */
    @Test
    public void testGetLastValidationResultsNoValidation()
    {
        setUpComponents();
        FormValidatorResults vres = controller.getLastValidationResults();
        assertTrue("Not valid", vres.isValid());
        assertTrue("Test field not found", vres.getFieldNames().contains(FIELD));
    }

    /**
     * Tests validation if no model object exists. This should work, but no
     * data is copied.
     */
    @Test
    public void testValidationNoModel()
    {
        setUpComponents();
        prepareValidationCheck(FieldValidationStatus.NOT_VISITED_VALID);
        final String input = "testInput";
        compHandler.setData(input);
        wndBuilderData.setFormBean(null);
        assertTrue("Form not valid", controller.validate().isValid());
        assertNull("Model manipulated", formBean.getTestInputField());
        assertNotNull("No event received", mockListener.nextEvent());
        mockListener.verifyEmpty();
    }

    /**
     * Prepares a test that accesses the current builder data. A mock for a bean
     * context will be initialized to expect a request for the builder data. The
     * passed in object will be returned.
     *
     * @param data the data object
     */
    private void prepareBuilderData(BuilderData data)
    {
        setUpComponents();
        BeanContext context = EasyMock.createMock(BeanContext.class);
        compBuilderData.setBeanContext(context);
        EasyMock.expect(context.getBean(ComponentBuilderData.KEY_BUILDER_DATA))
                .andReturn(data);
        EasyMock.replay(context);
    }

    /**
     * Tests whether the BuilderData object can be obtained.
     */
    @Test
    public void testGetBuilderData()
    {
        BuilderData data = EasyMock.createNiceMock(BuilderData.class);
        prepareBuilderData(data);
        assertEquals("Wrong builder data returned", data, controller
                .getBuilderData());
        EasyMock.verify(compBuilderData.getBeanContext());
    }

    /**
     * Tests obtaining the message output object when one was explicitly set.
     */
    @Test
    public void testFetchMessageOutputExplicit()
    {
        MessageOutput output = EasyMock.createNiceMock(MessageOutput.class);
        controller.setMessageOutput(output);
        assertEquals("Wrong output object returned", output, controller
                .fetchMessageOutput());
    }

    /**
     * Tests obtaining the message output object from the builder data.
     */
    @Test
    public void testFetchMessageOutputBuilderData()
    {
        MessageOutput output = EasyMock.createNiceMock(MessageOutput.class);
        BuilderData data = EasyMock.createMock(BuilderData.class);
        EasyMock.expect(data.getMessageOutput()).andReturn(output);
        EasyMock.replay(data);
        prepareBuilderData(data);
        assertEquals("Wrong message output", output, controller
                .fetchMessageOutput());
        EasyMock.verify(data);
    }

    /**
     * Tests obtaining the validation message format object when it was
     * explicitly set.
     */
    @Test
    public void testFetchValidationMessageFormatExplicit()
    {
        FormValidationMessageFormat fmt = new FormValidationMessageFormat();
        controller.setValidationMessageFormat(fmt);
        assertSame("Wrong format object returned", fmt, controller
                .fetchValidationMessageFormat());
    }

    /**
     * Tests obtaining the validation message format object when no specific
     * object was set. In this case the default format object from the bean
     * context must be fetched.
     */
    @Test
    public void testFetchValidationMessageFormatDefault()
    {
        BeanContext bc = EasyMock.createMock(BeanContext.class);
        setUpComponents();
        compBuilderData.setBeanContext(bc);
        FormValidationMessageFormat fmt = new FormValidationMessageFormat();
        EasyMock.expect(
                bc.getBean(FormController.BEAN_VALIDATION_MESSAGE_FORMAT))
                .andReturn(fmt);
        EasyMock.replay(bc);
        assertSame("Wrong format object returned", fmt, controller
                .fetchValidationMessageFormat());
        EasyMock.verify(bc);
    }

    /**
     * Tests obtaining the caption for the validation message box when no
     * caption was set explicitly. In this case it will be fetched from the
     * resources.
     */
    @Test
    public void testFetchValidationMessageBoxCaptionDefault()
    {
        BuilderData data = EasyMock.createMock(BuilderData.class);
        TransformerContext tc = EasyMock.createMock(TransformerContext.class);
        ResourceManager resMan = EasyMock.createMock(ResourceManager.class);
        EasyMock.expect(data.getTransformerContext()).andReturn(tc);
        EasyMock.expect(tc.getResourceManager()).andReturn(resMan);
        final Locale locale = Locale.ENGLISH;
        EasyMock.expect(tc.getLocale()).andStubReturn(locale);
        EasyMock
                .expect(
                        resMan
                                .getText(
                                        locale,
                                        DefaultValidationMessageHandler.DEFAULT_RESOURCE_GROUP_NAME,
                                        ValidationMessageConstants.ERR_MESSAGE_CAPTION))
                .andReturn(MSGBOX_CAPTION);
        prepareBuilderData(data);
        EasyMock.replay(data, tc, resMan);
        assertEquals("Wrong caption", MSGBOX_CAPTION, controller
                .fetchValidationMessageBoxCaption());
        EasyMock.verify(data, tc, resMan);
    }

    /**
     * Tests the handling of the action event for the cancel button. The
     * associated window should be closed immediately.
     */
    @Test
    public void testActionPerformedCancel()
    {
        setUpComponents();
        controller.setBtnCancelName(BTN_CANCEL);
        EasyMock.expect(wndBuilderData.getResultWindow().close(true))
                .andReturn(Boolean.TRUE);
        EasyMock.replay(wndBuilderData.getResultWindow());
        controller.actionPerformed(actionEvent(BTN_CANCEL));
        EasyMock.verify(wndBuilderData.getResultWindow());
    }

    /**
     * Creates a validation message format object, initializes it with default
     * templates, and passes it to the controller.
     */
    private void setUpValidationMessageFormat()
    {
        FormValidationMessageFormat fmt = new FormValidationMessageFormat();
        fmt.setFieldErrorTemplate(ERR_TEMPLATE);
        controller.setValidationMessageFormat(fmt);
    }

    /**
     * Tests the handling of the action event for the OK button when the form's
     * input fields contain valid data.
     */
    @Test
    public void testActionPerformedOkValid()
    {
        setUpComponents();
        controller.setBtnOkName(BTN_OK);
        MessageOutput output = EasyMock.createMock(MessageOutput.class);
        EasyMock.expect(wndBuilderData.getResultWindow().close(true))
                .andReturn(Boolean.TRUE);
        EasyMock.replay(output, wndBuilderData.getResultWindow());
        setUpValidationMessageFormat();
        validator.validFlag = true;
        controller.setMessageOutput(output);
        controller.actionPerformed(actionEvent(BTN_OK));
        EasyMock.verify(output, wndBuilderData.getResultWindow());
    }

    /**
     * Tests the handling of the action event for the OK button when the form's
     * input fields contain invalid data. In this case the window is not closed,
     * but an error message is displayed using the message output object. Also
     * all form fields should be marked as visited.
     */
    @Test
    public void testActionPerformedOkInvalid()
    {
        setUpComponents();
        controller.setBtnOkName(BTN_OK);
        MessageOutput output = EasyMock.createMock(MessageOutput.class);
        EasyMock.expect(
                output.show(wndBuilderData.getResultWindow(), ERR_MSG,
                        MSGBOX_CAPTION, MessageOutput.MESSAGE_ERROR,
                        MessageOutput.BTN_OK)).andReturn(MessageOutput.RET_OK);
        EasyMock.replay(output, wndBuilderData.getResultWindow());
        setUpValidationMessageFormat();
        validator.validFlag = false;
        controller.setMessageOutput(output);
        controller.setValidationMessageBoxCaption(MSGBOX_CAPTION);
        controller.actionPerformed(actionEvent(BTN_OK));
        EasyMock.verify(output, wndBuilderData.getResultWindow());
        assertTrue("Field not visited", controller.isFieldVisited(FIELD));
    }

    /**
     * Tests the validation method that displays error messages in case of
     * validation failures.
     */
    @Test
    public void testValidateWithErrorMessageDisplay()
    {
        setUpComponents();
        controller.setBtnOkName(BTN_OK);
        MessageOutput output = EasyMock.createMock(MessageOutput.class);
        EasyMock.expect(
                output.show(wndBuilderData.getResultWindow(), ERR_MSG,
                        MSGBOX_CAPTION, MessageOutput.MESSAGE_ERROR,
                        MessageOutput.BTN_OK)).andReturn(MessageOutput.RET_OK);
        EasyMock.replay(output, wndBuilderData.getResultWindow());
        setUpValidationMessageFormat();
        validator.validFlag = false;
        controller.setMessageOutput(output);
        controller.setValidationMessageBoxCaption(MSGBOX_CAPTION);
        assertFalse("Fields are valid", controller.validateAndDisplayMessages().isValid());
        EasyMock.verify(output, wndBuilderData.getResultWindow());
        assertTrue("Field not visited", controller.isFieldVisited(FIELD));
    }

    /**
     * Tests the actionPerformed() method when an unknown event is received.
     * This should be ignored.
     */
    @Test
    public void testActionPerformedUnknownButton()
    {
        setUpComponents();
        controller.setBtnCancelName(BTN_CANCEL);
        controller.setBtnOkName(BTN_OK);
        final String testButton = "testButton";
        setUpButton(testButton);
        EasyMock.replay(wndBuilderData.getResultWindow());
        controller.actionPerformed(actionEvent(testButton));
        EasyMock.verify(wndBuilderData.getResultWindow());
    }

    /**
     * Tests processing of an action event that does not have a component name.
     * This event should be ignored. (It's unlikely that such an event will ever
     * be received, but you never know...)
     */
    @Test
    public void testActionPerformedNoComponentName()
    {
        setUpComponents();
        EasyMock.replay(wndBuilderData.getResultWindow());
        FormActionEvent event = new FormActionEvent(this, null, null, "test");
        controller.actionPerformed(event);
        EasyMock.verify(wndBuilderData.getResultWindow());
    }

    /**
     * Tests whether the OK command is executed when the form is committed.
     */
    @Test
    public void testWindowClosedOKCommand()
    {
        Command cmd = EasyMock.createMock(Command.class);
        Command cmdCancel = EasyMock.createMock(Command.class);
        CommandQueue queue = EasyMock.createMock(CommandQueue.class);
        BuilderData data = EasyMock.createMock(BuilderData.class);
        EasyMock.expect(data.getCommandQueue()).andReturn(queue);
        queue.execute(cmd);
        prepareBuilderData(data);
        EasyMock.expect(wndBuilderData.getResultWindow().close(true))
                .andReturn(Boolean.TRUE);
        EasyMock.replay(cmd, cmdCancel, queue, data, wndBuilderData
                .getResultWindow());
        controller.setOkCommand(cmd);
        controller.setCancelCommand(cmdCancel);
        setUpValidationMessageFormat();
        validator.validFlag = true;
        controller.okButtonClicked(actionEvent(BTN_OK));
        controller.windowClosed(event());
        EasyMock.verify(cmd, cmdCancel, queue, data, wndBuilderData
                .getResultWindow());
    }

    /**
     * Tests whether the cancel command is executed when the form is not
     * committed.
     */
    @Test
    public void testWindowClosedCancelCommand()
    {
        Command cmd = EasyMock.createMock(Command.class);
        Command cmdOk = EasyMock.createMock(Command.class);
        CommandQueue queue = EasyMock.createMock(CommandQueue.class);
        BuilderData data = EasyMock.createMock(BuilderData.class);
        EasyMock.expect(data.getCommandQueue()).andReturn(queue);
        queue.execute(cmd);
        prepareBuilderData(data);
        EasyMock.replay(cmd, cmdOk, queue, data);
        controller.setOkCommand(cmdOk);
        controller.setCancelCommand(cmd);
        controller.windowClosed(event());
        EasyMock.verify(cmd, cmdOk, queue, data);
    }

    /**
     * Tries to create a form controller event without a controller reference. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateFormControllerEventNoController()
    {
        new FormControllerEvent(null);
    }

    /**
     * Tries to create a validation event without results. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateFormControllerValidationEventNoResults()
    {
        new FormControllerValidationEvent(controller, null);
    }

    /**
     * Tests whether validation listeners can be added and are called correctly.
     */
    @Test
    public void testAddValidationListener()
    {
        setUpComponents();
        ValidationListenerTestImpl l = new ValidationListenerTestImpl(
                controller);
        controller.addValidationListener(l);
        FormControllerValidationListener[] listeners = controller
                .getValidationListeners();
        assertEquals("Wrong number of listeners", 1, listeners.length);
        assertEquals("Wrong listener", l, listeners[0]);
        validator.validFlag = false;
        FormValidatorResults res1 = controller.validate();
        validator.validFlag = true;
        FormValidatorResults res2 = controller.validate();
        FormControllerValidationEvent event = l.nextEvent();
        assertSame("Wrong results in event 1", res1, event
                .getValidationResults());
        event = l.nextEvent();
        assertSame("Wrong results in event 2", res2, event
                .getValidationResults());
        l.verifyEmpty();
    }

    /**
     * Tries to add a null validation listener. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddValidationListenerNull()
    {
        controller.addValidationListener(null);
    }

    /**
     * Tests whether validation listeners can be removed.
     */
    @Test
    public void testRemoveValidationListener()
    {
        setUpComponents();
        ValidationListenerTestImpl l1 = new ValidationListenerTestImpl(controller);
        ValidationListenerTestImpl l2 = new ValidationListenerTestImpl(controller);
        controller.addValidationListener(l1);
        controller.addValidationListener(l2);
        controller.validate();
        controller.removeValidationListener(l2);
        controller.validate();
        controller.removeValidationListener(l1);
        controller.validate();
        l1.nextEvent();
        l2.nextEvent();
        l2.verifyEmpty();
        l1.nextEvent();
        l1.verifyEmpty();
    }

    /**
     * Tries to create a field status event without a field name. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateFormControllerFieldStatusEventNoField()
    {
        new FormControllerFieldStatusEvent(controller, null);
    }

    /**
     * Tests whether listeners for field status events can be added.
     */
    @Test
    public void testAddFieldStatusListener()
    {
        setUpComponents();
        FieldStatusListenerTestImpl l = new FieldStatusListenerTestImpl(
                controller);
        controller.addFieldStatusListener(l);
        FormControllerFieldStatusListener[] listeners = controller
                .getFieldStatusListeners();
        assertEquals("Wrong number of listeners", 1, listeners.length);
        assertEquals("Wrong listener", l, listeners[0]);
        controller.focusLost(focusEvent(FormFocusEvent.Type.FOCUS_LOST));
        FormControllerFieldStatusEvent event = l.nextEvent();
        assertEquals("Wrong controller", controller, event.getFormController());
        assertEquals("Wrong field name", FIELD, event.getFieldName());
        l.verifyEmpty();
    }

    /**
     * Tries to add a null field status listener. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddFieldStatusListenerNull()
    {
        controller.addFieldStatusListener(null);
    }

    /**
     * Tests whether field status listeners can be removed.
     */
    @Test
    public void testRemoveFieldStatusListener()
    {
        String testField = "anotherTestField";
        setUpComponents();
        FieldStatusListenerTestImpl l1 = new FieldStatusListenerTestImpl(
                controller);
        FieldStatusListenerTestImpl l2 = new FieldStatusListenerTestImpl(
                controller);
        controller.addFieldStatusListener(l1);
        controller.addFieldStatusListener(l2);
        controller.focusLost(focusEvent(FormFocusEvent.Type.FOCUS_LOST));
        controller.removeFieldStatusListener(l2);
        controller.focusLost(focusEvent(FormFocusEvent.Type.FOCUS_LOST));
        controller.focusLost(new FormFocusEvent(this,
                new ComponentHandlerImpl(), testField,
                FormFocusEvent.Type.FOCUS_LOST));
        FormControllerFieldStatusEvent event = l1.nextEvent();
        assertEquals("Wrong field name l1", FIELD, event.getFieldName());
        event = l2.nextEvent();
        assertEquals("Wrong field name l2", FIELD, event.getFieldName());
        l2.verifyEmpty();
        event = l1.nextEvent();
        assertEquals("Wrong field name for event 2", testField, event
                .getFieldName());
        l1.verifyEmpty();
    }

    /**
     * Tries to add a null form listener. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddFormListenerNull()
    {
        controller.addFormListener(null);
    }

    /**
     * Tests whether a form listener can be removed.
     */
    @Test
    public void testRemoveFormListener()
    {
        FormListenerTestImpl l1 = new FormListenerTestImpl(controller);
        FormListenerTestImpl l2 = new FormListenerTestImpl(controller);
        controller.addFormListener(l1);
        controller.addFormListener(l2);
        controller.fireFormEvent();
        controller.removeFormListener(l2);
        controller.fireFormEvent();
        FormControllerFormEvent e = l1.nextEvent();
        assertSame("Different event", e, l2.nextEvent());
        l2.verifyEmpty();
        l1.nextEvent();
        l1.verifyEmpty();
    }

    /**
     * Tests whether the registered form listeners can be queried.
     */
    @Test
    public void testGetFormListeners()
    {
        final int count = 12;
        Set<FormControllerFormListener> listeners = new HashSet<FormControllerFormListener>();
        for (int i = 0; i < count; i++)
        {
            FormListenerTestImpl l = new FormListenerTestImpl(controller);
            listeners.add(l);
            controller.addFormListener(l);
        }
        assertEquals("Wrong number of elements in set", count, listeners.size());
        FormControllerFormListener[] regListeners = controller
                .getFormListeners();
        assertEquals("Wrong number of listeners", count, regListeners.length);
        for (FormControllerFormListener l : regListeners)
        {
            assertTrue("Invalid listener: " + l, listeners.remove(l));
        }
    }

    /**
     * Helper method for testing a form event.
     *
     * @param e the event
     * @param expType the expected type
     */
    private void checkFormEvent(FormControllerFormEvent e,
            FormControllerFormEvent.Type expType)
    {
        assertSame("Wrong controller", controller, e.getFormController());
        assertEquals("Wrong type", expType, e.getType());
    }

    /**
     * Helper method for testing firing form events.
     *
     * @param committed the committed flag
     * @param expType the expected event type
     */
    private void checkFireFormEvent(boolean committed,
            FormControllerFormEvent.Type expType)
    {
        FormListenerTestImpl l = new FormListenerTestImpl(controller);
        controller.addFormListener(l);
        controller.mockCommit(committed);
        controller.fireFormEvent();
        checkFormEvent(l.nextEvent(), expType);
        l.verifyEmpty();
    }

    /**
     * Tests the form event fired for a non-committed form.
     */
    @Test
    public void testFireFormEventNotCommitted()
    {
        checkFireFormEvent(false, FormControllerFormEvent.Type.FORM_CANCELED);
    }

    /**
     * Tests the form event fired for a committed form.
     */
    @Test
    public void testFireFormEventCommitted()
    {
        checkFireFormEvent(true, FormControllerFormEvent.Type.FORM_COMMITTED);
    }

    /**
     * Helper method for testing the form events fired when the form is closed.
     *
     * @param committed the committed flag
     * @param expType the expected event type
     */
    private void checkWindowClosedFormEvent(boolean committed,
            FormControllerFormEvent.Type expType)
    {
        FormListenerTestImpl l = new FormListenerTestImpl(controller);
        controller.addFormListener(l);
        controller.mockCommit(committed);
        controller.windowClosed(event());
        checkFormEvent(l.nextEvent(), expType);
        l.verifyEmpty();
    }

    /**
     * Tests whether form listeners are called when the window is closed and the
     * form was not committed.
     */
    @Test
    public void testWindowClosedFormListenerNotCommitted()
    {
        checkWindowClosedFormEvent(false,
                FormControllerFormEvent.Type.FORM_CANCELED);
    }

    /**
     * Tests whether form listeners are called when the window is closed and the
     * form was committed.
     */
    @Test
    public void testWindowClosedFormListenerCommitted()
    {
        checkWindowClosedFormEvent(true,
                FormControllerFormEvent.Type.FORM_COMMITTED);
    }

    /**
     * Tests whether event listeners can be registered at a controller using the
     * reflection-based mechanism supported by FormEventManager. This is
     * important to demonstrate whether event registration tags work with these
     * kinds of listeners.
     */
    @Test
    public void testListenerRegistrationViaReflection()
    {
        FormEventManager evMan = new FormEventManager(
                new PlatformEventManagerImpl());
        ValidationListenerTestImpl lVal = new ValidationListenerTestImpl(
                controller);
        FieldStatusListenerTestImpl lField = new FieldStatusListenerTestImpl(
                controller);
        FormListenerTestImpl lForm = new FormListenerTestImpl(controller);
        assertTrue("Validation listener could not be registered", evMan
                .addEventListenerToObject(controller, "Validation", lVal));
        assertTrue("Field status listener could not be registered", evMan
                .addEventListenerToObject(controller, "FieldStatus", lField));
        assertTrue("Form listener could not be registered", evMan
                .addEventListenerToObject(controller, "Form", lForm));
        assertSame("Wrong validation listener", lVal, controller
                .getValidationListeners()[0]);
        assertSame("Wrong field status listener", lField, controller
                .getFieldStatusListeners()[0]);
        assertSame("Wrong form listener", lForm,
                controller.getFormListeners()[0]);
    }

    /**
     * A simple validator mock implementation that can easily be triggered to
     * return a valid or an invalid result.
     */
    private static class ValidatorMock implements ValidatorWrapper
    {
        /** A flag whether the object to test should be valid or invalid. */
        private boolean validFlag;

        public ValidationResult isValid(Object o)
        {
            if (validFlag)
            {
                return DefaultValidationResult.VALID;
            }
            else
            {
                DefaultValidationResult.Builder builder = new DefaultValidationResult.Builder();
                builder.addValidationMessage(new ValidationMessage()
                {
                    public String getKey()
                    {
                        return "ERR_TEST";
                    }

                    public String getMessage()
                    {
                        return ERR_MSG;
                    }

                    public ValidationMessageLevel getLevel()
                    {
                        return ValidationMessageLevel.ERROR;
                    }
                });
                return builder.build();
            }
        }
    }

    /**
     * A test form bean class.
     */
    public static class FormBeanTestImpl
    {
        private String testInputField;

        public String getTestInputField()
        {
            return testInputField;
        }

        public void setTestInputField(String testInputField)
        {
            this.testInputField = testInputField;
        }
    }

    /**
     * A base class for test event listeners for form controller events.
     *
     * @param <E> the event type
     */
    private static class FormControllerListener<E extends FormControllerEvent>
    {
        /** Stores the expected form controller. */
        private final FormController controller;

        /** A list with the events received by this listener. */
        private final LinkedList<E> events;

        protected FormControllerListener(FormController ctrl)
        {
            controller = ctrl;
            events = new LinkedList<E>();
        }

        /**
         * An event was received by this listener. Checks the expected
         * controller and adds the event to an internal list.
         *
         * @param event the event
         */
        public void eventReceived(E event)
        {
            assertEquals("Wrong controller", controller, event
                    .getFormController());
            events.add(event);
        }

        /**
         * Returns the next event received by this listener.
         *
         * @return the next event
         */
        public E nextEvent()
        {
            assertFalse("Too few events received", events.isEmpty());
            return events.removeFirst();
        }

        /**
         * Checks whether all events have been enumerated. This method can be
         * used to test whether too many events have been received.
         */
        public void verifyEmpty()
        {
            assertTrue("Too many events: " + events, events.isEmpty());
        }
    }

    /**
     * A test event listener class for validation events.
     */
    private static class ValidationListenerTestImpl extends
            FormControllerListener<FormControllerValidationEvent> implements
            FormControllerValidationListener
    {
        public ValidationListenerTestImpl(FormController ctrl)
        {
            super(ctrl);
        }

        public void validationPerformed(FormControllerValidationEvent event)
        {
            eventReceived(event);
        }
    }

    /**
     * A test event listener class for field status events.
     */
    private static class FieldStatusListenerTestImpl extends
            FormControllerListener<FormControllerFieldStatusEvent> implements
            FormControllerFieldStatusListener
    {
        public FieldStatusListenerTestImpl(FormController ctrl)
        {
            super(ctrl);
        }

        public void fieldStatusChanged(FormControllerFieldStatusEvent event)
        {
            eventReceived(event);
        }
    }

    /**
     * A test event listener class for form events.
     */
    private static class FormListenerTestImpl extends
            FormControllerListener<FormControllerFormEvent> implements
            FormControllerFormListener
    {
        public FormListenerTestImpl(FormController ctrl)
        {
            super(ctrl);
        }

        public void formClosed(FormControllerFormEvent event)
        {
            eventReceived(event);
        }
    }

    /**
     * A test implementation of FormController which is easier to mock.
     */
    private static class FormControllerTestImpl extends FormController
    {
        /** A flag whether isCommitted() is to be mocked. */
        private Boolean mockCommit;

        /**
         * Initializes mocking of the isCommitted() method.
         *
         * @param committed a flag whether the form was committed
         */
        public void mockCommit(boolean committed)
        {
            mockCommit = committed;
        }

        /**
         * {@inheritDoc} Either returns the mock committed flag or calls the
         * super method.
         */
        @Override
        protected boolean isCommitted()
        {
            return (mockCommit != null) ? mockCommit : super
                    .isCommitted();
        }
    }
}

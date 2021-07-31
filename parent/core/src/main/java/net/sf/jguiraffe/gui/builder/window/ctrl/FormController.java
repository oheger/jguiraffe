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

import javax.swing.event.EventListenerList;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.BuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.event.FormActionEvent;
import net.sf.jguiraffe.gui.builder.event.FormActionListener;
import net.sf.jguiraffe.gui.builder.event.FormFocusEvent;
import net.sf.jguiraffe.gui.builder.event.FormFocusListener;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;
import net.sf.jguiraffe.gui.builder.window.WindowListener;
import net.sf.jguiraffe.gui.cmd.Command;
import net.sf.jguiraffe.gui.forms.DefaultFormValidatorResults;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.forms.FormValidationMessageFormat;
import net.sf.jguiraffe.gui.forms.FormValidator;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;
import net.sf.jguiraffe.transform.DefaultValidationMessageHandler;
import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.ValidationMessageConstants;

/**
 * <p>
 * A base class for form controllers.
 * </p>
 * <p>
 * The form builder library follows the MVC paradigm when dealing with forms:
 * <ul>
 * <li>The dialog window displaying the input fields plays the role of the
 * <em>view</em>.</li>
 * <li>The <em>model</em> is represented by a data object, the so-called <em>form bean</em>
 * or <em>model object</em>. This object has
 * properties that are bound to the input fields of the form. It stores the data
 * entered by the user. In the initialization phase it provides the data for
 * filling the input fields. Access to the model object is controlled by a
 * {@link net.sf.jguiraffe.gui.forms.BindingStrategy BindingStrategy}.</li>
 * <li>The <em>controller</em> can be an instance of this class or one of its
 * subclasses. It controls the form's life-cycle and ensures that user input is
 * validated and correctly saved in the model.</li>
 * </ul>
 * </p>
 * <p>
 * This class provides a fully functional controller implementation, which
 * handles all phases of the form's life-cycle. It can be used out of the box.
 * In most cases adaptation to a specific application's needs is possible by
 * configuring some of the helper objects used by this class. This way for
 * instance the validation handling can be changed or the way fields containing
 * invalid data are displayed. Refer to the documentation of the corresponding
 * set methods for further details.
 * </p>
 * <p>
 * One of the main tasks of this class is to ensure that input validation is
 * performed when necessary. When clicking the <em>OK</em> button, a
 * validation has to be performed in any case. But it is also possible to
 * perform validation earlier, e.g. when the user left an input field. This can
 * be achieved by configuring a corresponding <code>FormValidationTrigger</code>.
 * A <code>FieldMarker</code> is used for defining the appearance of input
 * fields depending on their validation status.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe. It is intended to be
 * associated with a single form instance and not to be used concurrently with
 * multiple forms or threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormController.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormController implements WindowListener, FormFocusListener,
        FormActionListener
{
    /** Constant for the name of the bean with the validation message format. */
    static final String BEAN_VALIDATION_MESSAGE_FORMAT = "jguiraffe.validationMessageFormat";

    /** Stores the component builder data. */
    private ComponentBuilderData componentBuilderData;

    /** Stores the window builder data. */
    private WindowBuilderData windowBuilderData;

    /** Stores the form validation trigger. */
    private FormValidationTrigger validationTrigger;

    /** A set with the fields that have been visited so far. */
    private final Set<String> visitedFields;

    /** The list with all event listeners. */
    private final EventListenerList eventListeners;

    /** Stores a form validator. */
    private FormValidator formValidator;

    /** Stores the result of the last validation. */
    private FormValidatorResults lastValidationResults;

    /** Stores a message output object to be used. */
    private MessageOutput messageOutput;

    /** Stores the object for formatting validation messages. */
    private FormValidationMessageFormat validationMessageFormat;

    /** The command to be executed when the form is committed.*/
    private Command okCommand;

    /** The command to be executed when the form is canceled.*/
    private Command cancelCommand;

    /** Stores the name of the OK button. */
    private String btnOkName;

    /** Stores the name of the cancel button. */
    private String btnCancelName;

    /**
     * Stores the caption of the message box for displaying validation error
     * messages.
     */
    private String validationMessageBoxCaption;

    /** A flag whether the form was committed.*/
    private boolean committed;

    /**
     * Creates a new instance of {@code FormController}.
     */
    public FormController()
    {
        visitedFields = new HashSet<String>();
        eventListeners = new EventListenerList();
    }

    /**
     * Returns the <code>ComponentBuilderData</code> object.
     *
     * @return the object with information about all components that belong to
     *         the current form
     */
    public ComponentBuilderData getComponentBuilderData()
    {
        return componentBuilderData;
    }

    /**
     * Sets the <code>ComponentBuilderData</code> object. This object must
     * have been set before an instance of this class can be used. It allows
     * access to all components involved and the current form as well.
     *
     * @param componentBuilderData the component builder data object
     */
    public void setComponentBuilderData(
            ComponentBuilderData componentBuilderData)
    {
        this.componentBuilderData = componentBuilderData;
    }

    /**
     * Returns the <code>WindowBuilderData</code> object.
     *
     * @return the data object with information about the current window
     */
    public WindowBuilderData getWindowBuilderData()
    {
        return windowBuilderData;
    }

    /**
     * Sets the <code>WindowBuilderData</code> object. This object must have
     * been set before an instance of this class can be used. It allows access
     * to important information about the current window including its form
     * bean.
     *
     * @param windowBuilderData the window builder data object
     */
    public void setWindowBuilderData(WindowBuilderData windowBuilderData)
    {
        this.windowBuilderData = windowBuilderData;
    }

    /**
     * Returns a {@code FormValidator} for validating the associated form.
     * Result can be <b>null</b> if no specific {@code FormValidator} was set.
     * Note that the result of this method need not always be in sync with the
     * object returned by {@link Form#getFormValidator()}: this method always
     * returns the object that was set on a previous
     * {@link #setFormValidator(FormValidator)} call.
     *
     * @return the {@code FormValidator}
     * @see #setFormValidator(FormValidator)
     */
    public FormValidator getFormValidator()
    {
        return formValidator;
    }

    /**
     * Sets a {@code FormValidator} for the the associated form. This method is
     * intended to be called during a builder script. When the associated form
     * is opened, the {@code FormValidator} is automatically installed. If this
     * method is called later (if the window is already open and the form
     * exists), the specified in {@code FormValidator} is directly passed to the
     * {@code Form} object.
     *
     * @param formValidator the {@code FormValidator} for the associated {@code
     *        Form}
     */
    public void setFormValidator(FormValidator formValidator)
    {
        this.formValidator = formValidator;

        if (getComponentBuilderData() != null)
        {
            getForm().setFormValidator(formValidator);
        }
    }

    /**
     * Returns the current form bean. This is a convenience method which obtains
     * the form bean from the {@link WindowBuilderData} object.
     *
     * @return the current form bean
     */
    public Object getFormBean()
    {
        return getWindowBuilderData().getFormBean();
    }

    /**
     * Returns the current form. This is a convenience method which obtains the
     * form from the {@link ComponentBuilderData} object.
     *
     * @return the current form
     */
    public Form getForm()
    {
        return getComponentBuilderData().getForm();
    }

    /**
     * Returns the associated window. This is the window containing the form. It
     * is obtained from the {@link WindowBuilderData} object.
     *
     * @return the associated window
     */
    public Window getWindow()
    {
        return getWindowBuilderData().getResultWindow();
    }

    /**
     * Returns the <code>MessageOutput</code> object to be used by this
     * controller. This can be <b>null</b> if no specific object has been set.
     *
     * @return the <code>MessageOutput</code> object
     * @see #setMessageOutput(MessageOutput)
     */
    public MessageOutput getMessageOutput()
    {
        return messageOutput;
    }

    /**
     * Sets the <code>MessageOutput</code> object to be used by this controller.
     * The <code>MessageOutput</code> object is used for displaying validation
     * error messages to the user. Per default the <code>MessageOutput</code>
     * object defined in the {@link BuilderData} object will be used (which is
     * typically the application-global output object). With this method it is
     * possible to set a specific output object for this controller.
     *
     * @param messageOutput the <code>MessageOutput</code> object to use
     * @see #fetchMessageOutput()
     */
    public void setMessageOutput(MessageOutput messageOutput)
    {
        this.messageOutput = messageOutput;
    }

    /**
     * Returns the <code>FormValidationMessageFormat</code> object to be used
     * by this controller. This can be <b>null</b> if no specific object has
     * been set.
     *
     * @return the <code>FormValidationMessageFormat</code> object
     * @see #setValidationMessageFormat(FormValidationMessageFormat)
     */
    public FormValidationMessageFormat getValidationMessageFormat()
    {
        return validationMessageFormat;
    }

    /**
     * Sets the <code>FormValidationMessageFormat</code> object to be used by
     * this controller. This object is used for generating error messages for a
     * failed validation that are to be displayed to the user. With this method
     * it is possible to set a specific format object for this purpose. If no
     * specific object is set, the application-global default format object is
     * used (which is obtained from the current <code>BeanContext</code>).
     *
     * @param validationMessageFormat the
     *        <code>FormValidationMessageFormat</code> object to use
     * @see #fetchValidationMessageFormat()
     */
    public void setValidationMessageFormat(
            FormValidationMessageFormat validationMessageFormat)
    {
        this.validationMessageFormat = validationMessageFormat;
    }

    /**
     * Returns the name of the component representing the OK button.
     *
     * @return the name of the OK button
     */
    public String getBtnOkName()
    {
        return btnOkName;
    }

    /**
     * Sets the name of the component representing the OK button. This
     * controller will register an action listener at this component for
     * handling the commit operation accordingly.
     *
     * @param btnOkName the name of the OK button component
     */
    public void setBtnOkName(String btnOkName)
    {
        this.btnOkName = btnOkName;
    }

    /**
     * Returns the name of the component representing the cancel button.
     *
     * @return the name of the cancel button
     */
    public String getBtnCancelName()
    {
        return btnCancelName;
    }

    /**
     * Sets the name of the component representing the cancel button. This
     * controller will register an action listener at this component for
     * handling the cancel operation accordingly.
     *
     * @param btnCancelName the name of the cancel button component
     */
    public void setBtnCancelName(String btnCancelName)
    {
        this.btnCancelName = btnCancelName;
    }

    /**
     * Returns the caption of the message box for displaying validation error
     * messages.
     *
     * @return the caption of the validation error message box
     */
    public String getValidationMessageBoxCaption()
    {
        return validationMessageBoxCaption;
    }

    /**
     * Sets the caption of the message box for displaying validation error
     * messages. If the user hits the OK button, the data entered by the user is
     * validated. If this validation fails, a message box with the found
     * validation problems is displayed. This property allows defining the
     * caption of this message box. If no caption is set, a default caption is
     * used (which is defined as a resource ID and resolved using the current
     * resource manager).
     *
     * @param validationMessageBoxCaption the caption of the validation error
     *        message box
     */
    public void setValidationMessageBoxCaption(
            String validationMessageBoxCaption)
    {
        this.validationMessageBoxCaption = validationMessageBoxCaption;
    }

    /**
     * Returns the <code>FormValidationTrigger</code>.
     *
     * @return the validation trigger (can be <b>null</b> if none was set)
     */
    public FormValidationTrigger getValidationTrigger()
    {
        return validationTrigger;
    }

    /**
     * Sets the <code>FormValidationTrigger</code>. This object is called
     * once in the initialization phase to give it opportunity to register
     * itself as event listener.
     *
     * @param validationTrigger the new validation trigger (can be <b>null</b>)
     */
    public void setValidationTrigger(FormValidationTrigger validationTrigger)
    {
        this.validationTrigger = validationTrigger;
    }

    /**
     * Adds a {@code FormControllerValidationListener} to this controller. The
     * listener will be notified whenever a validation is performed.
     *
     * @param l the listener to be added (must not be <b>null</b>)
     * @throws IllegalArgumentException if the event listener is <b>null</b>
     */
    public void addValidationListener(FormControllerValidationListener l)
    {
        addEventListener(l, FormControllerValidationListener.class);
    }

    /**
     * Removes the specified {@code FormControllerValidationListener} from this
     * controller. If the listener is not registered, this method has no effect.
     *
     * @param l the listener to be removed
     */
    public void removeValidationListener(FormControllerValidationListener l)
    {
        eventListeners.remove(FormControllerValidationListener.class, l);
    }

    /**
     * Returns an array with all {@code FormControllerValidationListener}
     * objects registered at this {@code FormController}.
     *
     * @return an array with all registered {@code
     *         FormControllerValidationListener} objects
     */
    public FormControllerValidationListener[] getValidationListeners()
    {
        return eventListeners.getListeners(FormControllerValidationListener.class);
    }

    /**
     * Adds a {@code FormControllerFieldStatusListener} to this controller. The
     * listener will be notified whenever the visited status of a field in the
     * controller's form changes.
     *
     * @param l the listener to be added (must not be <b>null</b>)
     * @throws IllegalArgumentException if the event listener is <b>null</b>
     */
    public void addFieldStatusListener(FormControllerFieldStatusListener l)
    {
        addEventListener(l, FormControllerFieldStatusListener.class);
    }

    /**
     * Removes the specified {@code FormControllerFieldStatusListener} from this
     * controller. If the listener is not registered, this method has no effect.
     *
     * @param l the listener to be removed
     */
    public void removeFieldStatusListener(FormControllerFieldStatusListener l)
    {
        eventListeners.remove(FormControllerFieldStatusListener.class, l);
    }

    /**
     * Returns an array with all {@code FormControllerFieldStatusListener}
     * objects registered at this {@code FormController}.
     *
     * @return an array with all registered {@code
     *         FormControllerFieldStatusListener} objects
     */
    public FormControllerFieldStatusListener[] getFieldStatusListeners()
    {
        return eventListeners.getListeners(FormControllerFieldStatusListener.class);
    }

    /**
     * Adds a {@code FormControllerFormListener} to this controller. The
     * listener will be notified when the form associated with this controller
     * is closed.
     *
     * @param l the listener to be added (must not be <b>null</b>)
     * @throws IllegalArgumentException if the listener is <b>null</b>
     */
    public void addFormListener(FormControllerFormListener l)
    {
        addEventListener(l, FormControllerFormListener.class);
    }

    /**
     * Removes the specified {@code FormControllerFormListener} from this
     * controller. If the listener is not registered, this method has no effect.
     *
     * @param l the listener to be removed
     */
    public void removeFormListener(FormControllerFormListener l)
    {
        eventListeners.remove(FormControllerFormListener.class, l);
    }

    /**
     * Returns an array with all {@code FormControllerFormListener} objects
     * registered at this {@code FormController}.
     *
     * @return an array with all registered {@code FormControllerFormListener}
     *         objects
     */
    public FormControllerFormListener[] getFormListeners()
    {
        return eventListeners.getListeners(FormControllerFormListener.class);
    }

    /**
     * Returns the command to be executed when the form is closed in reaction of
     * the OK button.
     *
     * @return the command to be executed after OK was clicked
     */
    public Command getOkCommand()
    {
        return okCommand;
    }

    /**
     * Sets the command to be executed when the form is closed in reaction of
     * the OK button. When the user commits a form often some actions have to be
     * performed (e.g. saving some data in the database, triggering some other
     * components, etc.). These actions can be implemented as a
     * <code>{@link Command}</code> object and associated with this
     * controller. In its action handler for the OK event the controller will
     * check (after a successful validation) whether a command was set. If this
     * is the case, it will be passed to the current command queue.
     *
     * @param okCommand the command to be executed when the form is committed
     */
    public void setOkCommand(Command okCommand)
    {
        this.okCommand = okCommand;
    }

    /**
     * Returns the command to be executed when the form is canceled.
     *
     * @return the command to be executed after the cancel button was clicked
     */
    public Command getCancelCommand()
    {
        return cancelCommand;
    }

    /**
     * Sets the command to be executed when the form is canceled. This method is
     * similar to <code>{@link #setOkCommand(Command)}</code>, but it allows
     * to associate a <code>{@link Command}</code> object with the cancel
     * button (or any other close action that does not mean a commit). This way
     * it is possible to execute some action when the user decides to throw away
     * its input.
     *
     * @param cancelCommand the command to be executed when the form is canceled
     */
    public void setCancelCommand(Command cancelCommand)
    {
        this.cancelCommand = cancelCommand;
    }

    /**
     * Performs a validation of the associated form. After that the
     * {@link FormControllerValidationListener} objects registered at this
     * controller will be notified.
     *
     * @return a data object with information about the result of the validation
     */
    public FormValidatorResults validate()
    {
        FormValidatorResults results = getForm().validate(getFormBean());
        lastValidationResults = results;
        fireValidationEvent(results);
        return results;
    }

    /**
     * Performs a validation of the associated form and displays validation
     * messages if this is not successful. This method delegates to
     * {@link #validate()}. If validation results indicate errors, a message
     * window is displayed containing corresponding validation error messages.
     * This method is intended to do a validation in reaction on a user action,
     * e.g. when the user clicks an <em>apply</em> button.
     *
     * @return a data object with information about the result of the validation
     * @since 1.3.1
     */
    public FormValidatorResults validateAndDisplayMessages()
    {
        markFieldsAsVisited();
        FormValidatorResults results = validate();
        if (!results.isValid())
        {
            String msg =
                    fetchValidationMessageFormat().format(results, getForm());
            fetchMessageOutput().show(getWindow(), msg,
                    fetchValidationMessageBoxCaption(),
                    MessageOutput.MESSAGE_ERROR, MessageOutput.BTN_OK);
        }
        return results;
    }

    /**
     * Returns the results of the last validation operation. The object returned
     * by this method is the same as was returned by the last
     * {@link #validate()} call. This is useful for instance to determine which
     * input fields are currently invalid. If no validation has been performed
     * so far, a valid result object is returned.
     *
     * @return a {@code FormValidatorResults} object with the last validation
     *         results
     */
    public FormValidatorResults getLastValidationResults()
    {
        return (lastValidationResults != null) ? lastValidationResults
                : DefaultFormValidatorResults.validResultsForForm(getForm());
    }

    /**
     * Dummy implementation of this window event.
     *
     * @param event the received event
     */
    public void windowActivated(WindowEvent event)
    {
    }

    /**
     * The associated window was closed. This implementation checks whether
     * commands were registered for either the OK or the cancel button. If this
     * is the case, the correct command is executed. Also, registered
     * {@link FormControllerFormListener} objects are notified.
     *
     * @param event the received event
     */
    public void windowClosed(WindowEvent event)
    {
        Command cmd = isCommitted() ? getOkCommand() : getCancelCommand();
        if (cmd != null)
        {
            getBuilderData().getCommandQueue().execute(cmd);
        }

        fireFormEvent();
    }

    /**
     * Dummy implementation of this window event.
     *
     * @param event the received event
     */
    public void windowDeactivated(WindowEvent event)
    {
    }

    /**
     * Dummy implementation of this window event.
     *
     * @param event the received event
     */
    public void windowDeiconified(WindowEvent event)
    {
    }

    /**
     * Dummy implementation of this window event.
     *
     * @param event the received event
     */
    public void windowIconified(WindowEvent event)
    {
    }

    /**
     * Dummy implementation of this window event.
     *
     * @param event the received event
     */
    public void windowClosing(WindowEvent event)
    {
    }

    /**
     * The window containing the associated form was opened. This is the main
     * initialization method. The controller has to perform some setup here.
     *
     * @param event the event
     * @throws IllegalStateException if a required field is missing
     */
    public void windowOpened(WindowEvent event)
    {
        checkRequiredFields();

        getComponentBuilderData().getEventManager().addFocusListener(this);
        registerActionListener(getBtnOkName());
        registerActionListener(getBtnCancelName());

        if (getValidationTrigger() != null)
        {
            getValidationTrigger().initTrigger(this);
        }

        if (getFormValidator() != null)
        {
            getForm().setFormValidator(getFormValidator());
        }
        initFormFields();
        validate();
    }

    /**
     * A component of the associated window was given the focus.
     *
     * @param e the focus event
     */
    public void focusGained(FormFocusEvent e)
    {
    }

    /**
     * A component of the associated window lost the focus. We track this event
     * to mark the field as visited. This has an influence on validation. If the
     * visited status for this field has changed, the {@code FieldMarker} also
     * needs to be notified.
     *
     * @param e the focus event
     */
    public void focusLost(FormFocusEvent e)
    {
        if (visitedFields.add(e.getName()))
        {
            // the field was visited for the first time => status change
            fireFieldStatusEvent(e.getName());
        }
    }

    /**
     * Processes action events. This method tests whether the event was caused
     * by the OK or the cancel button. If this is the case, the corresponding
     * processing method is called.
     *
     * @param e the event
     * @see #okButtonClicked(FormActionEvent)
     * @see #cancelButtonClicked(FormActionEvent)
     */
    public void actionPerformed(FormActionEvent e)
    {
        if (e.getName() != null)
        {
            if (e.getName().equals(getBtnCancelName()))
            {
                cancelButtonClicked(e);
            }
            else if (e.getName().equals(getBtnOkName()))
            {
                okButtonClicked(e);
            }
        }
    }

    /**
     * Tests whether the field with the given name has already been visited by
     * the user.
     *
     * @param name the name of the field
     * @return a flag whether this field has already been visited
     */
    public boolean isFieldVisited(String name)
    {
        return visitedFields.contains(name);
    }

    /**
     * Returns the current <code>BuilderData</code> object. This object can be
     * used for gaining access to some application global objects and the
     * complete configuration of the builder.
     *
     * @return the <code>BuilderData</code> object
     */
    protected BuilderData getBuilderData()
    {
        return (BuilderData) getComponentBuilderData().getBeanContext()
                .getBean(ComponentBuilderData.KEY_BUILDER_DATA);
    }

    /**
     * Returns the current <code>MessageOutput</code> object. If an output
     * object was set explicitly using the <code>setMessageOutput()</code>
     * method, this object will be returned. Otherwise this method obtains the
     * <code>MessageOutput</code> object from the current
     * <code>{@link BuilderData}</code> object.
     *
     * @return the <code>MessageOutput</code> object to be used
     */
    protected MessageOutput fetchMessageOutput()
    {
        MessageOutput result = getMessageOutput();

        if (result == null)
        {
            result = getBuilderData().getMessageOutput();
        }

        return result;
    }

    /**
     * Obtains the <code>FormValidationMessageFormat</code> object to be used.
     * If a format object was set explicitly using the
     * <code>setValidationMessageFormat()</code> method, this object will be
     * returned. Otherwise this method queries the current
     * <code>BeanContext</code> for the default format object.
     *
     * @return the <code>FormValidationMessageFormat</code> object to be used
     */
    protected FormValidationMessageFormat fetchValidationMessageFormat()
    {
        FormValidationMessageFormat fmt = getValidationMessageFormat();

        if (fmt == null)
        {
            fmt = (FormValidationMessageFormat) getComponentBuilderData()
                    .getBeanContext().getBean(BEAN_VALIDATION_MESSAGE_FORMAT);
        }

        return fmt;
    }

    /**
     * Returns the caption for a message box for displaying validation error
     * messages. This method checks whether such a caption was explicitly set
     * (using the <code>setValidationMessageBoxCaption()</code>). If this is
     * the case, it is returned. Otherwise the resource key for the default
     * caption is resolved.
     *
     * @return the caption for the message box for validation error messages
     */
    protected String fetchValidationMessageBoxCaption()
    {
        String caption = getValidationMessageBoxCaption();

        if (caption == null)
        {
            TransformerContext tctx = getBuilderData().getTransformerContext();
            caption = tctx
                    .getResourceManager()
                    .getText(
                            tctx.getLocale(),
                            DefaultValidationMessageHandler.DEFAULT_RESOURCE_GROUP_NAME,
                            ValidationMessageConstants.ERR_MESSAGE_CAPTION);
        }

        return caption;
    }

    /**
     * The OK button was clicked. This method performs a validation. If this is
     * successful, the form is closed. Otherwise an error message is created
     * using the {@link FormValidationMessageFormat} object and
     * displayed to the user via the current {@link MessageOutput}
     * object. When the user clicks OK, he or she indicates that all fields have
     * been properly filled in; so they are marked as visited.
     *
     * @param event the event object that triggered this method call
     */
    protected void okButtonClicked(FormActionEvent event)
    {
        FormValidatorResults vres = validateAndDisplayMessages();

        if (vres.isValid())
        {
            committed = true;
            closeForm();
        }
    }

    /**
     * The cancel button was clicked. This will cause the form to be closed
     * without a validation.
     *
     * @param event the event object that triggered this method call
     */
    protected void cancelButtonClicked(FormActionEvent event)
    {
        closeForm();
    }

    /**
     * Closes the associated form. This method is called by both the action
     * handler of the OK and the cancel button when the form can be closed. It
     * invokes the <code>close()</code> method on the associated window
     * object.
     */
    protected void closeForm()
    {
        getWindow().close(true);
    }

    /**
     * Returns a flag whether the form was committed. This method can be used to
     * find out whether the form was closed using the OK button (in this case
     * the return value is <b>true</b>) or whether it was canceled. Of course,
     * calling this method makes only sense after the form was closed. It is
     * invoked by the handler for the window closed event to find out, which
     * command object is to be executed (if any).
     *
     * @return a flag whether the form was closed using the OK button
     */
    protected boolean isCommitted()
    {
        return committed;
    }

    /**
     * Notifies all registered validation listeners about a validation
     * operation.
     *
     * @param results the validation results
     */
    protected void fireValidationEvent(FormValidatorResults results)
    {
        FormControllerValidationEvent event = null;
        Object[] listeners = eventListeners.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == FormControllerValidationListener.class)
            {
                if (event == null)
                {
                    event = new FormControllerValidationEvent(this, results);
                }
                ((FormControllerValidationListener) listeners[i + 1])
                        .validationPerformed(event);
            }
        }
    }

    /**
     * Notifies all registered field status listeners about a change in the
     * status of a field.
     *
     * @param fieldName the name of the affected field
     */
    protected void fireFieldStatusEvent(String fieldName)
    {
        FormControllerFieldStatusEvent event = null;
        Object[] listeners = eventListeners.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == FormControllerFieldStatusListener.class)
            {
                if (event == null)
                {
                    event = new FormControllerFieldStatusEvent(this, fieldName);
                }
                ((FormControllerFieldStatusListener) listeners[i + 1])
                        .fieldStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all registered form listeners that the form has been closed.
     */
    protected void fireFormEvent()
    {
        FormControllerFormEvent event = null;
        Object[] listeners = eventListeners.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == FormControllerFormListener.class)
            {
                if (event == null)
                {
                    event = createFormEvent();
                }
                ((FormControllerFormListener) listeners[i + 1])
                        .formClosed(event);
            }
        }
    }

    /**
     * Tests whether all required fields are set. If this is not the case, an
     * exception will be thrown.
     *
     * @throws IllegalStateException if a required field is missing
     */
    private void checkRequiredFields()
    {
        if (getComponentBuilderData() == null)
        {
            throw new IllegalStateException("No component builder data is set!");
        }
        if (getWindowBuilderData() == null)
        {
            throw new IllegalStateException("No window builder data is set!");
        }
        if (getWindowBuilderData().getResultWindow() == null)
        {
            throw new IllegalStateException("No associated window found!");
        }
    }

    /**
     * Registers this controller as action listener at the specified component.
     * This is only done if the passed in name is not <b>null</b>.
     *
     * @param name the name of the component
     */
    private void registerActionListener(String name)
    {
        if (name != null)
        {
            getComponentBuilderData().getEventManager().addActionListener(name,
                    this);
        }
    }

    /**
     * Initializes the fields of the form with the data of the form bean. If no
     * form bean is specified, no initialization will be performed.
     */
    private void initFormFields()
    {
        getForm().initFields(getFormBean());
    }

    /**
     * Marks all form fields as visited. This method is called when the user
     * presses the OK button.
     */
    private void markFieldsAsVisited()
    {
        for (String fld : getForm().getFieldNames())
        {
            visitedFields.add(fld);
        }
    }

    /**
     * Helper method for adding an event listener. The listener is checked for
     * <b>null</b> and then added to the central event listener list.
     *
     * @param <T> the type of the listener
     * @param l the listener to be added (must not be <b>null</b>)
     * @param listenerClass the event listener class
     */
    private <T extends EventListener> void addEventListener(T l,
            Class<T> listenerClass)
    {
        if (l == null)
        {
            throw new IllegalArgumentException(
                    "Event listener must not be null!");
        }
        eventListeners.add(listenerClass, l);
    }

    /**
     * Creates a {@code FormControllerFormEvent} based on the current committed
     * status.
     *
     * @return the new event
     */
    private FormControllerFormEvent createFormEvent()
    {
        return new FormControllerFormEvent(this,
                isCommitted() ? FormControllerFormEvent.Type.FORM_COMMITTED
                        : FormControllerFormEvent.Type.FORM_CANCELED);
    }
}

/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.event.FormFocusEvent;
import net.sf.jguiraffe.gui.builder.event.FormFocusListener;

/**
 * <p>
 * A specialized {@code FormValidationTrigger} implementation that triggers a
 * validation operation whenever the user leaves an input field.
 * </p>
 * <p>
 * This validation trigger implementation registers itself as focus listener on
 * all input components of the form to monitor. Whenever it receives a focus
 * lost event, it triggers a validation operation.
 * </p>
 * <p>
 * This way the user gets fast feedback about invalid input. Invalid fields can
 * be marked while editing the form, and fresh validation error messages can be
 * displayed. Because validation happens only if the user switches to another
 * input field, the number of validations (a UI updates related to validation)
 * is limited. So this {@code FormValidationTrigger} is a good compromise
 * between real-time validation feedback and annoying the user with permanent
 * validation messages.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe. An instance should be
 * associated with a single {@link FormController} only and not be reused.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormValidationTriggerFocus.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormValidationTriggerFocus implements FormValidationTrigger,
        FormFocusListener
{
    /** Stores the associated form controller. */
    private FormController formController;

    /**
     * Returns a reference to the associated {@code FormController}.
     *
     * @return the {@code FormController}
     */
    public final FormController getFormController()
    {
        return formController;
    }

    /**
     * Initializes this {@code FormValidationTrigger}. This implementation
     * stores the {@code FormController} reference and registers itself as
     * global focus listener. Thus it can intercept every change in the focus of
     * the associated input form.
     *
     * @param controller the {@code FormController}
     */
    public void initTrigger(FormController controller)
    {
        assert controller != null : "No FormController passed!";
        formController = controller;

        formController.getComponentBuilderData().getEventManager()
                .addFocusListener(this);
    }

    /**
     * Notifies this object that an input component gained focus. This
     * implementation does nothing.
     *
     * @param e the focus event
     */
    public void focusGained(FormFocusEvent e)
    {
    }

    /**
     * Notifies this object that an input component lost focus. This
     * implementation triggers the {@code FormController} to perform another
     * validation.
     *
     * @param e the focus event
     */
    public void focusLost(FormFocusEvent e)
    {
        assert getFormController() != null : "Not initialized with form controller!";
        getFormController().validate();
    }
}

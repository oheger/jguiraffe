/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

/**
 * <p>
 * Definition of an interface for objects that can trigger the validation of a
 * form.
 * </p>
 * <p>
 * Validation of user input can make sense at different points of time, for
 * instance only when the user hits the OK button, after an input field was
 * left, or even while typing into an input field. It is up to a specific
 * application to decide when validation should be performed. The user should
 * get an early feedback about errors in the data entered, but on the other hand
 * being overwhelmed by a multitude of validation error messages does not
 * improve the usability of the software either.
 * </p>
 * <p>
 * Because there are many different approaches to this problem a
 * {@link FormController} does not determine itself when to
 * initiate a validation operation, but delegates this task to a
 * {@code FormValidationTrigger} implementation. So it is possible to
 * choose the validation strategy dynamically.
 * </p>
 * <p>
 * This interface only defines an initialization method. The idea is that a
 * concrete implementation registers itself as event listener for the
 * corresponding events. When it receives an event that should cause a
 * validation, it can trigger the controller. This approach frees the controller
 * from having to poll the trigger on a regular basis.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormValidationTrigger.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface FormValidationTrigger
{
    /**
     * Initializes this validation trigger and registers the associated form
     * controller. An implementation can access the controller and all its
     * dependent objects to establish a mechanism so that it gets notified when
     * a validation is to be performed. (It will probably register itself as
     * event listener on some or all of the form's components.) The reference to
     * the form controller should probably be saved so a validation can be
     * initiated.
     *
     * @param controller the associated form controller
     */
    void initTrigger(FormController controller);
}

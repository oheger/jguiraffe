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
package net.sf.jguiraffe.gui.builder.window.ctrl;

import java.util.EventListener;

/**
 * <p>
 * Definition of an interface to be implemented by objects that are interested
 * in validation operations performed by a {@link FormController}.
 * </p>
 * <p>
 * Each time a {@link FormController}'s {@link FormController#validate()} method
 * is called, event listeners of this type are notified. This gives them a
 * chance to react on validation results, e.g. UI components containing invalid
 * data can be marked, or validation messages can be displayed. This event
 * listener interface is just a generic way of participating in the validation
 * process of a {@link FormController}.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormControllerValidationListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface FormControllerValidationListener extends EventListener
{
    /**
     * Notifies this event listener that a validation operation was performed.
     * All information available about this operation is contained in the passed
     * in event object. This method is directly called after the validation has
     * finished - in the same thread (which is usually the event dispatch
     * thread).
     *
     * @param event the event object with information about the validation
     */
    void validationPerformed(FormControllerValidationEvent event);
}

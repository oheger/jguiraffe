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

import java.util.EventListener;

/**
 * <p>
 * A specialized event listener interface to be implemented by objects that are
 * interested in status updates of form fields.
 * </p>
 * <p>
 * Event listeners of this type are notified by a {@link FormController} when
 * the visited state of a form field changes, i.e. when the user leaves a form
 * field for the first time.
 * </p>
 * <p>
 * The visited state of form fields may be interesting for objects that display
 * validation results. If the data contained in a field is invalid, it may be
 * useful to distinguish whether the field has already been visited or not. So,
 * giving the user feedback about validation operations is the primary use case
 * for this interface.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormControllerFieldStatusListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface FormControllerFieldStatusListener extends EventListener
{
    /**
     * Notifies this event listener that the status of a form field has changed.
     * All information available about this change can be retrieved from the
     * passed in event object.
     *
     * @param event the event
     */
    void fieldStatusChanged(FormControllerFieldStatusEvent event);
}

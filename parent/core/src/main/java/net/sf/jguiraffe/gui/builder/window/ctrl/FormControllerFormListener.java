/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
 * An event listener for processing events of type
 * {@link FormControllerFormEvent}.
 * </p>
 * <p>
 * Event listeners of this type can be registered at a {@link FormController}.
 * They are notified once when the form associated with the controller is
 * closed. The {@link FormControllerFormEvent} object passed to the listener
 * method contains information whether the form was committed or canceled.
 * </p>
 * <p>
 * The idea behind this event listener type is that listeners registered at the
 * {@link FormController} can do some processing of the data entered into the
 * form when it is closed - for instance, if the form was committed, the data
 * can be saved somewhere. So their purpose is the same as for the command
 * objects that can be set for a {@link FormController}: one for the OK command
 * and one for the Cancel command. The difference is that event listeners are
 * directly executed in the event dispatch thread when the form is closed while
 * commands are added to the command queue and are then executed by a different
 * thread. Therefore event listeners are appropriate for short-running
 * operations, e.g. updating the UI or copying data into a domain object. More
 * sophisticated operations should be performed by commands in a background
 * thread.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormControllerFormListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface FormControllerFormListener extends EventListener
{
    /**
     * Notifies this listener that the form associated with the monitored
     * {@link FormController} was closed. All information available is provided
     * by the passed in event object.
     *
     * @param event the event object
     */
    void formClosed(FormControllerFormEvent event);
}

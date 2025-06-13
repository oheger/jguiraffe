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
 * A specialized {@code FormControllerEvent} class for events related to the
 * form associated with a {@link FormController}.
 * </p>
 * <p>
 * When the form associated with a {@link FormController} is closed the
 * controller fires an event of this type. From the properties of this event
 * listeners can find out whether the form was committed (e.g. the OK button was
 * clicked) or canceled. A listener could then do something with the data
 * entered by the user. For instance, if the form was committed, a listener
 * could save the data somewhere.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormControllerFormEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormControllerFormEvent extends FormControllerEvent
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20100619L;

    /** The type of this event. */
    private final Type type;

    /**
     * Creates a new instance of {@code FormControllerFormEvent} and initializes
     * it.
     *
     * @param ctrl the {@code FormController} which fired this event
     * @param t the type the event
     */
    public FormControllerFormEvent(FormController ctrl, Type t)
    {
        super(ctrl);
        type = t;
    }

    /**
     * Returns the type of this event.
     *
     * @return the type of this event
     */
    public Type getType()
    {
        return type;
    }

    /**
     * An enumeration class defining the possible types of {@code
     * FormControllerFormEvent} events. Basically, the type determines whether
     * the form was committed or canceled.
     */
    public static enum Type
    {
        /**
         * Indicates that the form was committed. This means that the user
         * closed the form with the intension to save the data that was entered.
         */
        FORM_COMMITTED,

        /**
         * Indicates that the form was canceled. The user hit the cancel button,
         * pressed escape or did something else to abort the edit operation.
         */
        FORM_CANCELED;
    }
}

/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
 * A specialized {@code FormControllerEvent} class that is generated whenever a
 * form field changes its visited status.
 * </p>
 * <p>
 * Event objects of this type are sent to
 * {@link FormControllerFieldStatusListener} objects registered at a
 * {@link FormController} when a field in the controller's form is visited for
 * the first time.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormControllerFieldStatusEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormControllerFieldStatusEvent extends FormControllerEvent
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20091210L;

    /** The name of the field affected by this event. */
    private final String fieldName;

    /**
     * Creates a new instance of {@code FormControllerFieldStatusEvent} and
     * initializes it with the {@code FormController} that is the source of this
     * event and the name of the field affected by this event.
     *
     * @param controller the {@code FormController} (must not be <b>null</b>)
     * @param field the name of the field whose status has changed (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is missing
     */
    public FormControllerFieldStatusEvent(FormController controller,
            String field)
    {
        super(controller);
        if (field == null)
        {
            throw new IllegalArgumentException("Field name must not be null!");
        }
        fieldName = field;
    }

    /**
     * Returns the name of the field whose status has changed.
     *
     * @return the name of the field affected by this event
     */
    public String getFieldName()
    {
        return fieldName;
    }
}

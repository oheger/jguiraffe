/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
 * A specialized implementation of a <code>FormValidationTrigger</code> that
 * does no triggering at all.
 * </p>
 * <p>
 * This is a very simplistic implementation of the
 * <code>FormValidationTrigger</code> interface. It literally does nothing,
 * i.e. no user event will cause an additional validation. This means that the
 * form is validated once when the window opens, and then every time the user
 * hits the OK button. (At these points in a form's life-cycle a validation is
 * always performed.)
 * </p>
 * <p>
 * If this validation trigger implementation is used, the user gets feedback
 * about the validity of the data entered only when the form is to be committed.
 * If then some fields contain invalid data, a message box is displayed, and the
 * form cannot be closed. This is not very spectacular. On the other hand, the
 * user is not overwhelmed by validation error messages while still editing the
 * form. So especially for classic data gathering applications this trigger
 * strategy is appropriate.
 * </p>
 * <p>
 * Applications that want to show a more interactive behavior can choose a
 * different trigger strategy, e.g. one that triggers a validation every time
 * the user leaves an input field.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormValidationTriggerNone.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormValidationTriggerNone implements FormValidationTrigger
{
    /**
     * Initializes this trigger and gives it the chance to register itself at
     * the specified form controller. This implementation does not do any
     * registration. It will never trigger an additional validation.
     *
     * @param controller the form controller
     */
    public void initTrigger(FormController controller)
    {
    }
}

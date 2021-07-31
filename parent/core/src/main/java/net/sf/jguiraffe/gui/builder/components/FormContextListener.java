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
package net.sf.jguiraffe.gui.builder.components;

import net.sf.jguiraffe.gui.forms.Form;

/**
 * <p>
 * An event listener interface to be implemented by components that are
 * interested in the creation of new form contexts during a builder operation.
 * </p>
 * <p>
 * Some complex components (e.g. tables) require the creation of sub forms
 * during their construction. Typically, such sub forms require special
 * treatment. Therefore, it may be important for affected components (like a
 * concrete {@link ComponentManager} implementation) to receive a notification
 * when a form context is created or closed. This interface defines a set of
 * callback methods allowing such notifications to be passed.
 * </p>
 * <p>
 * Objects implementing this interface can register themselves at a
 * {@link ComponentBuilderData} object. They are then notified about changes of
 * the current form context during a builder operation.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.3
 */
public interface FormContextListener
{
    /**
     * Notifies this object that a new form context was created. This new form
     * context becomes the current context; so all newly created components are
     * added to this form. The source object responsible for the context
     * creation is also passed to this method - it may be <b>null</b> if no
     * information about the source is available. In most cases, this source
     * will be a tag.
     *
     * @param form the {@code Form} object associated with the new context
     * @param source the source which created the new form context (if known)
     */
    void formContextCreated(Form form, Object source);

    /**
     * Notifies this object that the current form context was closed. Each call
     * to {@link #formContextCreated(Form, Object)} is followed eventually by an
     * invocation of this method.
     *
     * @param form the {@code Form} object associated with the new context
     * @param source the source responsible for this form context (if known)
     */
    void formContextClosed(Form form, Object source);
}

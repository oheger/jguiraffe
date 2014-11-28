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
package net.sf.jguiraffe.gui.builder.window.tags;

import net.sf.jguiraffe.gui.builder.components.tags.UseBeanBaseTag;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerFieldStatusListener;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerFormListener;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerValidationListener;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specialized tag handler class that can be used to register listeners at a
 * {@code FormController}.
 * </p>
 * <p>
 * {@code FormController} supports several types of event listeners that are
 * notified when specific operations are performed. With this tag listeners can
 * be defined and passed to a controller. Tags of this type can be placed in the
 * body of a {@link FormControllerTag}. By inheriting from
 * {@link UseBeanBaseTag} there are multiple ways of defining the listener bean.
 * The listener is obtained and passed to the {@link FormControllerTag} which
 * eventually performs the registration.
 * </p>
 * <p>
 * Per default, the type of event listener to register is determined by
 * inspecting the listener bean and the listener interfaces it implements. This
 * is typically the desired behavior as listeners are automatically registered
 * for the interfaces they support. However, there are some attributes that can
 * be used to alter the default behavior:
 * <dl>
 * <dt>suppressValidationListener</dt>
 * <dd>If this attribute is defined (with an arbitrary value), the listener bean
 * will not be registered as a {@code FormControllerValidationListener}, even if
 * it implements this interface.</dd>
 * <dt>suppressFieldStatusListener</dt>
 * <dd>If this attribute is defined (with an arbitrary value), the listener bean
 * will not be registered as a {@code FormControllerFieldStatusListener}, even
 * if it implements this interface.</dd>
 * <dt>suppressFormListener</dt>
 * <dd>If this attribute is defined (with an arbitrary value), the listener bean
 * will not be registered as a {@code FormControllerFormListener}, even if it
 * implements this interface.</dd>
 * </dl>
 * </p>
 * <p>
 * In addition to these attributes all standard attributes of
 * {@link UseBeanBaseTag} are available of course. Using these attributes
 * listener beans can either be created directly or referenced from a {@code
 * BeanContext}. The following example code fragment shows how this tag can be
 * used to register a {@code ColorFieldMarker} at a form controller:
 *
 * <pre>
 * &lt;!-- Definition of the field marker bean --&gt;
 * &lt;di:bean name="fieldMarker" singleton="false"
 *   beanClass="net.sf.jguiraffe.gui.builder.window.ColorFieldMarker"&gt;
 *   &lt;!-- Property definitions for the colors to use omitted --&gt;
 *   ...
 * &lt;/di:bean&gt;
 * &lt;!-- The frame definition including the controller --&gt;
 * &lt;w:frame title="Test frame"&gt;
 *   &lt;!-- GUI definitions omitted --&gt;
 *   ...
 *   &lt;w:formController beanName="controller"&gt;
 *     &lt;w:formControllerListener beanName="fieldMarker"/&gt;
 *   &lt;/w:formController&gt;
 * &lt;/w:frame&gt;
 * </pre>
 *
 * </p>
 * <p>
 * Note that with the standard tags for registering event listeners the same
 * effect can be achieved: arbitrary event listener objects could be registered
 * manually at a form controller bean. However, using this tag can be more
 * convenient, especially if the listener object implements multiple listener
 * interfaces as is the case for field markers for example.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormControllerListenerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormControllerListenerTag extends UseBeanBaseTag
{
    /** Constant for the name of the attribute to suppress validation listeners. */
    private static final String ATTR_SUPR_VALIDATION_LISTENER = "suppressValidationListener";

    /**
     * Constant for the name of the attribute to suppress field status
     * listeners.
     */
    private static final String ATTR_SUPR_FIELDSTATUS_LISTENER = "suppressFieldStatusListener";

    /** Constant for the name of the attribute to suppress form listeners. */
    private static final String ATTR_SUPR_FORM_LISTENER = "suppressFormListener";

    /**
     * Passes the bean to the owning object. This implementation expects that
     * this tag is nested inside a {@link FormControllerTag}. If this is the
     * case, the listener bean is evaluated and registered for the corresponding
     * listener interfaces.
     *
     * @param bean the listener bean
     * @return a flag whether the bean could be processed
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected boolean passResults(Object bean) throws JellyTagException
    {
        if (getParent() instanceof FormControllerTag)
        {
            FormControllerTag parent = (FormControllerTag) getParent();

            if (bean instanceof FormControllerValidationListener
                    && !getAttributes().containsKey(
                            ATTR_SUPR_VALIDATION_LISTENER))
            {
                parent
                        .addValidationListener((FormControllerValidationListener) bean);
            }

            if (bean instanceof FormControllerFieldStatusListener
                    && !getAttributes().containsKey(
                            ATTR_SUPR_FIELDSTATUS_LISTENER))
            {
                parent
                        .addFieldStatusListener((FormControllerFieldStatusListener) bean);
            }

            if (bean instanceof FormControllerFormListener
                    && !getAttributes().containsKey(ATTR_SUPR_FORM_LISTENER))
            {
                parent.addFormListener((FormControllerFormListener) bean);
            }

            return true;
        }

        return false;
    }
}

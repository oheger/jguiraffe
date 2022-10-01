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
package net.sf.jguiraffe.gui.builder.window.tags;

import java.util.LinkedList;
import java.util.List;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormController;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerFieldStatusListener;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerFormListener;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormControllerValidationListener;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormValidationTrigger;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specialized tag handler implementation for creating form controllers.
 * </p>
 * <p>
 * This tag handler class is pretty similar to {@link WindowControllerTag}, but
 * provides some additional functionality specific to form controllers. The main
 * difference is that it deals with objects of class (or derived from)
 * {@link FormController}. Such objects are able to fully control the life-cycle
 * of a form without requiring further intervention by the developer.
 * </p>
 * <p>
 * This tag creates or obtains a {@link FormController} object and performs the
 * necessary initialization steps. Especially the mandatory helper objects are
 * set as follows:
 * <ul>
 * <li>The {@code ComponentBuilderData} object is set from the current
 * Jelly context.</li>
 * <li>The {@code WindowBuilderData} object is set from the current Jelly
 * context.</li>
 * <li>If no {@link FormValidationTrigger} trigger was set, the default
 * {@code FormValidationTrigger} is obtained from the current
 * {@code BeanContext}, and the corresponding property is initialized with
 * this object.</li>
 * <li>If the {@code formBeanName} attribute is specified, a bean with this name
 * is obtained from the current {@code BeanContext} and set as the form bean in
 * the current {@code WindowBuilderData} object.</li>
 * </ul>
 * </p>
 * <p>
 * For the objects that must be initialized from the current
 * {@code BeanContext} constants are defined by this class. The default
 * bean definition file shipped with the framework contains default definitions
 * for all these objects. These can be overridden by applications so that all
 * controllers created by an application make use of application-specific
 * objects.
 * </p>
 * <p>
 * Usage of this tag is very similar to the {@link WindowControllerTag}.
 * Typically the controller is created as a bean using the full power of the
 * <em>dependency injection framework</em> to set associated objects. Then this
 * tag can be placed inside the body of a window tag for connecting the
 * controller bean with the window. This could look as in the following example:
 *
 * <pre>
 * &lt;!-- A command to be executed when OK is pressed --&gt;
 * &lt;di:bean name=&quot;okCommand&quot; singleton=&quot;false&quot;
 *   beanClass=&quot;...&quot;&gt;
 * &lt;/di:bean&gt;
 * &lt;!-- Define the bean for the controller --&gt;
 * &lt;di:bean name=&quot;controller&quot; singleton=&quot;false&quot;
 *   beanClass=&quot;com.mypackage.MyController&quot;&gt;
 *   &lt;di:setProperty property=&quot;okCommand&quot; refName=&quot;okCommand&quot;/&gt;
 *   &lt;!-- Set further properties if required --&gt;
 * &lt;/di:bean&gt;
 * &lt;!-- The frame definition including the controller --&gt;
 * &lt;w:frame title=&quot;Testwindow&quot;&gt;
 *   &lt;!-- Define the GUI here --&gt;
 *      ...
 *   <strong>&lt;w:formController beanName=&quot;controller&quot;/&gt;</strong>
 * &lt;/w:frame&gt;
 * </pre>
 *
 * </p>
 * <p>
 * In this example a controller bean is defined, which is initialized with an
 * &quot;OK command&quot; (i.e. a command to be executed when the OK button is
 * pressed). Note that the <code>singleton</code> attribute of the bean
 * definition is set to <b>false</b>; controllers should not be reused for
 * multiple forms. Alternatively it is possible to create and initialize the
 * controller directly using the <code>formController</code> tag. However, the
 * <code>&lt;di:bean&gt;</code> tag is much more powerful when it comes to
 * property initialization and dependency resolving.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormControllerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormControllerTag extends WindowControllerTag
{
    /** Constant for the name of the field marker bean. */
    public static final String BEAN_FIELD_MARKER = "jguiraffe.fieldMarker";

    /** Constant for the name of the validation trigger bean. */
    public static final String BEAN_VALIDATION_TRIGGER = "jguiraffe.formValidationTrigger";

    /** Constant for the form bean name attribute. */
    private static final String ATTR_FORM_BEAN_NAME = "formBeanName";

    /** A list with validation listeners to add to the controller. */
    private List<FormControllerValidationListener> validationListeners;

    /** A list with field status listeners to add to the controller. */
    private List<FormControllerFieldStatusListener> fieldStatusListeners;

    /** A list with the form listener to add to the controller. */
    private List<FormControllerFormListener> formListeners;

    /**
     * Creates a new instance of <code>FormControllerTag</code>.
     */
    public FormControllerTag()
    {
        super(FormController.class, FormController.class);
        addIgnoreProperty(ATTR_FORM_BEAN_NAME);
    }

    /**
     * Adds a {@code FormControllerValidationListener}. This listener will be
     * added to the form controller bean when it is initialized. This method is
     * intended to be called by tags in the body of this tag.
     *
     * @param l the listener to be added
     */
    public void addValidationListener(FormControllerValidationListener l)
    {
        if (validationListeners == null)
        {
            validationListeners = new LinkedList<FormControllerValidationListener>();
        }
        validationListeners.add(l);
    }

    /**
     * Adds a {@code FormControllerFieldStatusListener}. This listener will be
     * added to the form controller bean when it is initialized. This method is
     * intended to be called by tags in the body of this tag.
     *
     * @param l the listener to be added
     */
    public void addFieldStatusListener(FormControllerFieldStatusListener l)
    {
        if (fieldStatusListeners == null)
        {
            fieldStatusListeners = new LinkedList<FormControllerFieldStatusListener>();
        }
        fieldStatusListeners.add(l);
    }

    /**
     * Adds a {@code FormControllerFormListener}. This listener will be added to
     * the form controller bean when it is initialized. This method is intended
     * to be called by tags in the body of this tag.
     *
     * @param l the listener to be added
     */
    public void addFormListener(FormControllerFormListener l)
    {
        if (formListeners == null)
        {
            formListeners = new LinkedList<FormControllerFormListener>();
        }
        formListeners.add(l);
    }

    /**
     * Passes the bean to the owning component. This implementation expects that
     * the passed in bean is derived from the {@link FormController} class. It
     * performs some specific initializations, e.g. it sets the required data
     * objects (the component builder data and the window builder data) and
     * checks, which helper objects have to be set. Helper objects like the
     * validation trigger can be directly set at the controller bean when it is
     * created. In this case they won't be touched by this method. Objects that
     * have not yet been initialized are obtained from the current bean context
     * and passed to the controller.
     *
     * @param bean the bean
     * @return a flag whether the bean could be passed to its owner
     * @throws JellyTagException in case of an error
     */
    @Override
    protected boolean passResults(Object bean) throws JellyTagException
    {
        boolean result = super.passResults(bean);

        assert bean instanceof FormController : "Invalid type of bean!";
        FormController ctrl = (FormController) bean;

        ComponentBuilderData compData = ComponentBuilderData.get(getContext());
        WindowBuilderData wndData = WindowBuilderData.get(getContext());
        ctrl.setComponentBuilderData(compData);
        ctrl.setWindowBuilderData(wndData);

        // set missing helper objects
        if (ctrl.getValidationTrigger() == null)
        {
            ctrl.setValidationTrigger((FormValidationTrigger) compData
                    .getBeanContext().getBean(BEAN_VALIDATION_TRIGGER));
        }

        String formBeanName = getAttributeStr(ATTR_FORM_BEAN_NAME);
        if (formBeanName != null)
        {
            wndData
                    .setFormBean(compData.getBeanContext()
                            .getBean(formBeanName));
        }

        registerListeners(ctrl);
        return result;
    }

    /**
     * Registers event listeners at the controller.
     *
     * @param ctrl the controller
     */
    private void registerListeners(FormController ctrl)
    {
        if (validationListeners != null)
        {
            for (FormControllerValidationListener l : validationListeners)
            {
                ctrl.addValidationListener(l);
            }
        }

        if (fieldStatusListeners != null)
        {
            for (FormControllerFieldStatusListener l : fieldStatusListeners)
            {
                ctrl.addFieldStatusListener(l);
            }
        }

        if (formListeners != null)
        {
            for (FormControllerFormListener l : formListeners)
            {
                ctrl.addFormListener(l);
            }
        }
    }
}

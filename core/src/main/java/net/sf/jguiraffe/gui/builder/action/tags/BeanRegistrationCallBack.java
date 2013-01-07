/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action.tags;

import java.util.EventListener;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;

/**
 * <p>
 * A specialized registration callback dealing with beans from the current
 * {@code BeanContext}.
 * </p>
 * <p>
 * This implementation treats the target name as the name of a bean. In its
 * {@link #register(ComponentBuilderData, Object)} method it obtains this bean
 * from the current {@code BeanContext}. Then it asks the event manager to
 * register the listener at this bean. It is also possible to pass a parameter
 * object when this callback is registered at the {@link ComponentBuilderData}
 * object. In this case, this parameter object becomes the target of the
 * registration.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanRegistrationCallBack.java 205 2012-01-29 18:29:57Z oheger $
 */
class BeanRegistrationCallBack extends RegistrationCallBackBase
{
    /**
     * Creates a new instance of {@code BeanRegistrationCallBack} and
     * initializes it.
     *
     * @param beanName the name of the target bean from the {@code BeanContext}
     * @param type the listener type
     * @param l the listener
     * @param ignore the ignore failure flag
     */
    public BeanRegistrationCallBack(String beanName, String type,
            EventListener l, boolean ignore)
    {
        super(beanName, type, l, ignore);
    }

    /**
     * Performs the registration. This implementation obtains the bean from the
     * {@code BeanContext}. Then it delegates to the {@code FormEventManager} to
     * add the event listener. If a parameter object was passed, the listener is
     * registered at this object.
     *
     * @param builderData the {@code ComponentBuilderData}
     * @param params additional parameters passed to the callback
     * @return a flag whether the registration was successful
     * @throws Exception in case of an error
     */
    @Override
    protected boolean register(ComponentBuilderData builderData, Object params)
            throws Exception
    {
        Object bean = (params == null) ? builderData.getBeanContext().getBean(
                getTargetName()) : params;
        return builderData.getEventManager().addEventListenerToObject(bean,
                getListenerType(), getListener());
    }
}

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
package net.sf.jguiraffe.gui.builder.action.tags;

import java.util.EventListener;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;

/**
 * <p>
 * A specialized registration callback dealing with components.
 * </p>
 * <p>
 * This implementation treats the target name as a component name. In its
 * {@link #register(ComponentBuilderData, Object)} method it delegates to the
 * event manager to register the event listener at this component.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComponentRegistrationCallBack.java 205 2012-01-29 18:29:57Z oheger $
 */
class ComponentRegistrationCallBack extends RegistrationCallBackBase
{
    /**
     * Creates a new instance of {@code ComponentRegistrationCallBack} and
     * initializes it.
     *
     * @param componentName the name of the target component
     * @param type the listener type
     * @param l the listener
     * @param ignore the ignore failure flag
     */
    public ComponentRegistrationCallBack(String componentName, String type,
            EventListener l, boolean ignore)
    {
        super(componentName, type, l, ignore);
    }

    /**
     * Performs the registration. This implementation delegates to the {@code
     * FormEventManager} for registering a listener at a component.
     *
     * @param builderData the {@code ComponentBuilderData}
     * @param params additional parameters (ignored)
     * @return a flag whether the registration was successful
     * @throws Exception in case of an error
     */
    @Override
    protected boolean register(ComponentBuilderData builderData, Object params)
            throws Exception
    {
        return builderData.getEventManager().addEventListener(getTargetName(),
                getListenerType(), getListener()) >= 1;
    }
}

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
package net.sf.jguiraffe.gui.builder.enablers;

import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

/**
 * <p>
 * A specialized implementation of the {@code ElementEnabler} interface that can
 * change the enabled state of actions.
 * </p>
 * <p>
 * An instance of this class is initialized with the name of the action it
 * should manipulate. The implementation of the {@code setEnabledState()} method
 * obtains the action with this name from the {@code BeanContext} managed by the
 * passed in {@link ComponentBuilderData} object. (This will cause the action to
 * be searched in the current {@code ActionStore}.) Then the action's enabled
 * state is set accordingly.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionEnabler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ActionEnabler implements ElementEnabler
{
    /** Stores the name of the action to manipulate. */
    private final String actionName;

    /**
     * Stores the key of the action as needed for accessing it from the bean
     * context.
     */
    private final String actionKey;

    /**
     * Creates a new instance of {@code ActionEnabler} and initializes it with
     * the name of the action to manipulate.
     *
     * @param actName the name of the action (must not be <b>null</b>
     * @throws IllegalArgumentException if no action name is provided
     */
    public ActionEnabler(String actName)
    {
        if (actName == null)
        {
            throw new IllegalArgumentException("Action name must not be null!");
        }

        actionName = actName;
        actionKey = ActionBuilder.KEY_ACTION_PREFIX + actName;
    }

    /**
     * Returns the name of the action that is affected by this enabler.
     *
     * @return the name of the action to be manipulated
     */
    public String getActionName()
    {
        return actionName;
    }

    /**
     * Performs the change of the enabled state. Obtains the {@link FormAction}
     * specified by the name passed to the constructor. On this action {@code
     * setEnabled()} is invoked. If no action with this name can be found, an
     * exception is thrown.
     *
     * @param compData the {@code ComponentBuilderData} instance
     * @param state the new enabled state
     * @throws FormBuilderException if the action cannot be resolved
     */
    public void setEnabledState(ComponentBuilderData compData, boolean state)
            throws FormBuilderException
    {
        try
        {
            FormAction action = (FormAction) compData.getBeanContext().getBean(
                    actionKey);
            action.setEnabled(state);
        }
        catch (InjectionException inex)
        {
            throw new FormBuilderException(
                    "Cannot change enabled state of action " + getActionName(),
                    inex);
        }
    }
}

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
package net.sf.jguiraffe.gui.builder.enablers;

import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionStore;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

/**
 * <p>
 * A specialized implementation of the {@code ElementEnabler} interface that can
 * change the enabled state of action groups.
 * </p>
 * <p>
 * An instance of this class is initialized with the name of the action group it
 * should manipulate. The implementation of the {@code setEnabledState()} method
 * obtains the current {@link ActionStore} object from the {@code BeanContext}
 * managed by the passed in {@link ComponentBuilderData} object. This object is
 * then used to change the enabled state of the action group in question
 * accordingly.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionGroupEnabler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ActionGroupEnabler implements ElementEnabler
{
    /** Stores the name of the action group to manipulate. */
    private final String actionGroupName;

    /**
     * Creates a new instance of {@code ActionGroupEnabler} and initializes it
     * with the name of the action group it is responsible for.
     *
     * @param groupName the name of the action group (must not be <b>null</b>
     * @throws IllegalArgumentException if the group name is <b>null</b>
     */
    public ActionGroupEnabler(String groupName)
    {
        if (groupName == null)
        {
            throw new IllegalArgumentException(
                    "Action group name must not be null!");
        }

        actionGroupName = groupName;
    }

    /**
     * Returns the name of the action group that is manipulated by this enabler.
     *
     * @return the name of the affected action group
     */
    public String getActionGroupName()
    {
        return actionGroupName;
    }

    /**
     * Performs the change of the enabled state. Obtains the {@link ActionStore}
     * from the {@code BeanContext} maintained by specified
     * {@link ComponentBuilderData} object and uses it to enable or disable the
     * action group this enabler is responsible for.
     *
     * @param compData the {@code ComponentBuilderData} instance
     * @param state the new enabled state
     * @throws FormBuilderException if an error occurs
     */
    public void setEnabledState(ComponentBuilderData compData, boolean state)
            throws FormBuilderException
    {
        ActionStore store = (ActionStore) compData.getBeanContext().getBean(
                ActionBuilder.KEY_ACTION_STORE);
        store.enableGroup(getActionGroupName(), state);
    }
}

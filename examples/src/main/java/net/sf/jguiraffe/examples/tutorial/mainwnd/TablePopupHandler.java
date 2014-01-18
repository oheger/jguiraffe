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
package net.sf.jguiraffe.examples.tutorial.mainwnd;

import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionStore;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.builder.action.PopupMenuBuilder;
import net.sf.jguiraffe.gui.builder.action.PopupMenuHandler;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;

/**
 * <p>
 * A {@code PopupMenuHandler} implementation for creating the popup menu of the
 * directory table.
 * </p>
 * <p>
 * This is an example of creating dynamic context menus. This handler
 * implementation checks whether the action group with file-related actions is
 * enabled. If this is the case, the actions contained in this group are added
 * to the menu. Otherwise, only standard actions are added.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TablePopupHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TablePopupHandler implements PopupMenuHandler
{
    /** An array with the names of actions that are always present. */
    private static final String[] ACTIVE_ACTIONS = {
            "editDeleteAction", "editRefreshAction"
    };

    /**
     * {@inheritDoc} Dynamically creates the context menu based on the enabled
     * state of the actions affected.
     */
    @Override
    public void constructPopup(PopupMenuBuilder builder,
            ComponentBuilderData compData) throws FormActionException
    {
        ActionStore as = (ActionStore) compData.getBeanContext().getBean(
                ActionBuilder.KEY_ACTION_STORE);

        // Handle group with actions for a single file selection
        boolean enabled = false;
        for (String name : as
                .getActionNamesForGroup(MainWndController.ACTGRP_SINGLE_FILE))
        {
            FormAction action = as.getAction(name);
            if (action.isEnabled())
            {
                builder.addAction(action);
                enabled = true;
            }
        }

        if (enabled)
        {
            builder.addSeparator();
        }

        // Actions that are always contained in the menu
        for (String name : ACTIVE_ACTIONS)
        {
            builder.addAction(as.getAction(name));
        }

        // Display the menu
        builder.create();
    }
}

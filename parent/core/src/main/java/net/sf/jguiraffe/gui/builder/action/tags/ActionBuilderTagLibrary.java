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
package net.sf.jguiraffe.gui.builder.action.tags;

import org.apache.commons.jelly.TagLibrary;

/**
 * <p>The tag library for the action builder tags.</p>
 *
 * @author Oliver Heger
 * @version $Id: ActionBuilderTagLibrary.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ActionBuilderTagLibrary extends TagLibrary
{
    /**
     * Creates a new instance of {@code ActionBuilderTagLibrary}.
     */
    public ActionBuilderTagLibrary()
    {
        super();
        registerTag("action", ActionTag.class);
        registerTag("actionData", ActionDataTag.class);
        registerTag("actionEvent", ActionListenerTag.class);
        registerTag("andEventFilter", AndEventFilterTag.class);
        registerTag("changeEvent", ChangeListenerTag.class);
        registerTag("customEvent", FormEventListenerTag.class);
        registerTag("eventFilter", EventFilterTag.class);
        registerTag("eventListener", EventRegistrationTag.class);
        registerTag("focusEvent", FocusListenerTag.class);
        registerTag("listenerType", EventListenerTypeTag.class);
        registerTag("menu", MenuTag.class);
        registerTag("menubar", MenuBarTag.class);
        registerTag("menuitem", MenuItemTag.class);
        registerTag("mouseEvent", MouseListenerTag.class);
        registerTag("orEventFilter", OrEventFilterTag.class);
        registerTag("popup", PopupHandlerTag.class);
        registerTag("separator", SeparatorTag.class);
        registerTag("task", ActionTaskTag.class);
        registerTag("toolbar", ToolbarTag.class);
        registerTag("toolbutton", ToolButtonTag.class);
    }
}

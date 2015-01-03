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
package net.sf.jguiraffe.gui.builder.action;

import net.sf.jguiraffe.gui.builder.event.BuilderEvent;

/**
 * <p>
 * Definition of an interface for the task of an action.
 * </p>
 * <p>
 * Each <code>{@link FormAction}</code> object is associated with a target
 * object, which will be called when the action is triggered. The default
 * implementations support different types or target objects. One possibility is
 * that the target object implements this interface. It defines a single
 * <code>run()</code> method that expects the event object, which triggered
 * the action, as argument.
 * </p>
 * <p>
 * Action can also deal with plain <code>Runnable</code> objects. If the logic
 * behind an action does not care about the triggering event, using just a
 * <code>Runnable</code> may be easier.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionTask.java 205 2012-01-29 18:29:57Z oheger $
 * @see FormAction
 */
public interface ActionTask
{
    /**
     * Implements the logic behind an action. This method will be called when an
     * action is triggered (when its <code>execute()</code> method is
     * invoked). The passed in parameters can be used to further distinguish the
     * operations to perform.
     *
     * @param action the calling action
     * @param event the event that triggered the action
     */
    void run(FormAction action, BuilderEvent event);
}

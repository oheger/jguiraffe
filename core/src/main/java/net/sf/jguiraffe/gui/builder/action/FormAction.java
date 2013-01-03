/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
 * An interface describing an action.
 * </p>
 * <p>
 * An action in terms of this framework is a piece of code that must be executed
 * in reaction of a user action. Actions are associated with GUI controls like
 * menu items or toolbar buttons.
 * </p>
 * <p>
 * This definition of an action is compatible with similar concepts in typical
 * GUI libraries like Swing or SWT (jface). The purpose of this interface is to
 * provide an abstraction for specific action classes and/or interfaces used in
 * different GUI libraries. There will be adapter implementations for the
 * supported libraries.
 * </p>
 * <p>
 * From this framework's point of view an action has the following
 * characteristics:
 * <ul>
 * <li>It has a unique name by which it can be identified.</li>
 * <li>It can be disabled if the corresponding GUI controls should not be
 * available for the user because of the actual state of the application.</li>
 * <li>It has a task, which is an arbitrary object. This task will be invoked
 * when the action is executed. It is possible to set the task at runtime.</li>
 * <li>It has an <code>execute()</code> method for invoking the action.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormAction.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface FormAction
{
    /**
     * Returns the name of this action.
     *
     * @return this action's name
     */
    String getName();

    /**
     * Returns a flag whether this action is enabled at the moment.
     *
     * @return the enabled flag
     */
    boolean isEnabled();

    /**
     * Allows to set this action's enabled flag. If set to <b>false</b>, the
     * user is not allowed to do something with the associated GUI controls.
     *
     * @param f the value of the enabled flag
     */
    void setEnabled(boolean f);

    /**
     * Returns a flag whether this action is checked at the moment.
     *
     * @return the checked flag
     */
    boolean isChecked();

    /**
     * Allows to set this action's checked flag. The checked flag is important
     * for actions only if they are represented as checked menu items or toggle
     * buttons in a toolbar. They then act as a kind of switch.
     *
     * @param f the value of the checked flag
     */
    void setChecked(boolean f);

    /**
     * Executes this action. The corresponding code will be invoked. The event
     * that caused the action to be executed is passed as parameter. Note that
     * depending on the information available at the time the action is invoked,
     * some properties of the event may be undefined.
     *
     * @param event the event that caused the execution of this action
     */
    void execute(BuilderEvent event);

    /**
     * Returns the task of this action.
     *
     * @return the task of this action
     */
    Object getTask();

    /**
     * Sets the task of this action. The task will be executed when this action
     * is triggered. It can be changed at runtime to assign the action a
     * different behavior (however this feature should be used with care). Which
     * tasks an action supports, is up to a concrete implementation. Every
     * <code>FormAction</code> implementation should support objects
     * implementing one of these interfaces: <code>Runnable</code>,
     * <code>{@link ActionTask}</code>.
     *
     * @param task the task for this action
     * @throws IllegalArgumentException if the task is not supported by this
     * action
     */
    void setTask(Object task);
}

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
package net.sf.jguiraffe.gui.builder.action;

/**
 * <p>
 * Definition of an interface for describing properties of an action.
 * </p>
 * <p>
 * This interface is used in communication between a concrete implementation of
 * the <code>{@link ActionManager}</code> interface and tag handler classes
 * for defining action objects. The properties defined by this interface are
 * supported by the generic action classes.
 * </p>
 * <p>
 * The here specified properties are typical for GUI controls that represent
 * actions, e.g. a textual description, an icon, and a tool tip text.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionData.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ActionData
{
    /**
     * Returns the name of the represented action.
     *
     * @return the action's name
     */
    String getName();

    /**
     * Returns a text for this action. This is the text as displayed for the
     * user, e.g. the name of a menu.
     *
     * @return the action's text
     */
    String getText();

    /**
     * Returns the tool tip text for the represented action.
     *
     * @return the tool tip
     */
    String getToolTip();

    /**
     * Returns the mnemonic for the represented action. The mnemonic is the
     * letter in an action's text that be typed for triggering the action. For
     * instance, if the action is represented by a menu item, the text of the
     * item will display this character in a high-lighted way (e.g. underlined).
     * If the parent menu is open, the user can directly type this character
     * for selecting this menu item.
     *
     * @return the mnemonic
     */
    int getMnemonicKey();

    /**
     * Returns the <code>Accelerator</code> associated with this action. Through
     * an accelerator the action can be associated with a specific key
     * combination. By typing this key combination the action will be triggered.
     * An example for an accelerator is the well-known key combination CONTROL+C
     * for copying the current selection into the clipboard.
     *
     * @return the <code>Accelerator</code> for this action; can be <b>null</b>,
     *         then the action is not associated with an
     *         <code>Accelerator</code>
     */
    Accelerator getAccelerator();

    /**
     * Returns an icon for the represented action.
     *
     * @return an icon for the action
     */
    Object getIcon();

    /**
     * Returns the task object for the represented action. This object will be
     * invoked whenever the action is triggered. A task can be a <code>Runnable</code>
     * or an <code>{@link ActionTask}</code> object.
     *
     * @return the task for the action
     */
    Object getTask();
}

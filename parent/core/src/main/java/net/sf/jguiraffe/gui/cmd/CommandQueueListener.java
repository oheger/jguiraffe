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
package net.sf.jguiraffe.gui.cmd;

import java.util.EventListener;

/**
 * <p>
 * Definition of an interface for listeners that want to be notified about
 * changes in the state of a <code>{@link CommandQueue}</code> object.
 * </p>
 * <p>
 * This interface defines one single method for processing event objects of type
 * <code>CommandQueueEvent</code>. The event's <code>Type</code> property
 * can be used to determine what has happened.
 * </p>
 *
 * @see CommandQueueImpl
 * @see CommandQueueEvent
 *
 * @author Oliver Heger
 * @version $Id: CommandQueueListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface CommandQueueListener extends EventListener
{
    /**
     * Notifies this listener about a change in the state of a command queue.
     *
     * @param e the event object describing the change
     */
    void commandQueueChanged(CommandQueueEvent e);
}

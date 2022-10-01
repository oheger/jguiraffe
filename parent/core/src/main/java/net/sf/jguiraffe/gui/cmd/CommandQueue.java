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
package net.sf.jguiraffe.gui.cmd;

import net.sf.jguiraffe.gui.builder.utils.GUISynchronizer;

/**
 * <p>
 * Definition of an interface that describes a <em>command queue</em>.
 * </p>
 * <p>
 * A command queue can be used by an application to execute longer-running tasks
 * in the background without blocking the main event dispatching thread. This
 * way the application will stay responsive. The command pattern also provides a
 * suitable way of structuring the logic implemented in an application.
 * </p>
 * <p>
 * The most important method in this interface is of course the
 * <code>execute()</code> method, which allows scheduling new commands to be
 * executed. With <code>shutdown()</code> the queue can be gracefully closed
 * (commands that are contained in the queue or are currently executed will be
 * finished before the queue actually shuts down). Further methods are available
 * for checking the current status of the queue.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CommandQueue.java 127 2008-05-24 15:59:16Z oheger $
 */
public interface CommandQueue
{
    /**
     * Adds a new listener to this queue.
     *
     * @param l the event listener to add (must not be <b>null</b>)
     * @throws IllegalArgumentException if the listener is undefined
     */
    void addQueueListener(CommandQueueListener l);

    /**
     * Removes the specified event listener from this command queue.
     *
     * @param l the listener to remove
     */
    void removeQueueListener(CommandQueueListener l);

    /**
     * Returns the <code>GUISynchronizer</code> that is used by this command
     * queue.
     *
     * @return the GUI synchronizer
     */
    GUISynchronizer getGUISynchronizer();

    /**
     * Sets the <code>GUISynchronizer</code> to be used by this command queue.
     * This object will be used to ensure that GUI updates performed by commands
     * are done on the event dispatch thread.
     *
     * @param sync the GUI synchronizer
     */
    void setGUISynchronizer(GUISynchronizer sync);

    /**
     * Adds a new <code>Command</code> object to this queue. It will be
     * executed as soon as the next worker thread is available.
     *
     * @param cmd the command to be executed (must not be <b>null</b>)
     * @throws IllegalArgumentException if the command is <b>null</b>
     * @throws IllegalStateException if <code>shutdown()</code> has already
     *         been called
     */
    void execute(Command cmd);

    /**
     * Checks if there are commands to be executed or in execution. This method
     * can be called for instance if the user wants to exit the application to
     * check if there are still running command threads.
     *
     * @return a flag if there are pending commands
     */
    boolean isPending();

    /**
     * Returns a flag if <code>shutdown()</code> was called. After that no
     * commands can be executed any more.
     *
     * @return a flag if the queue is shut down
     */
    boolean isShutdown();

    /**
     * Initiates the shutdown sequence. New commands won't be accepted any more.
     * The boolean parameter determines the kind of shutdown: If set to <b>false</b>,
     * the commands contained in the queue will still be executed, and the
     * method blocks until everything is complete. A value of <b>true</b>
     * forces an immediate shutdown (as far as this is possible for a concrete
     * implementation).
     *
     * @param immediate a flag how the shutdown should be performed
     */
    void shutdown(boolean immediate);
}

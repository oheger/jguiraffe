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

import java.util.EventObject;

/**
 * <p>
 * An event class for notifying listeners about the state of a
 * {@link CommandQueue}.
 * </p>
 * <p>
 * Event objects of this class are sent to registered listeners if certain
 * changes in the command queue's life cycle occur. Registered listeners can
 * react on these changes, i.g. by displaying some signs if currently commands
 * are executed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CommandQueueEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class CommandQueueEvent extends EventObject
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 7434520871707897320L;

    /** Stores the <code>Command</code> object affected by this event. */
    private final transient Command command;

    /** Stores the event's type. */
    private final Type type;

    /**
     * Creates a new instance of <code>CommandQueueEvent</code> and fully
     * initializes it.
     *
     * @param q the affected command queue
     * @param c the affected command object
     * @param t the event's type
     */
    public CommandQueueEvent(CommandQueue q, Command c, Type t)
    {
        super(q);
        command = c;
        type = t;
    }

    /**
     * Returns the <code>Command</code> object affected by this event. This
     * may be <b>null</b> if this event is not related to a
     * <code>Command</code> object.
     *
     * @return the affected command
     */
    public Command getCommand()
    {
        return command;
    }

    /**
     * Returns the <code>CommandQueue</code> that caused this event.
     *
     * @return the command queue
     */
    public CommandQueue getCommandQueue()
    {
        return (CommandQueue) getSource();
    }

    /**
     * Returns the type of this event. This is one of the <code>QE_XXXX</code>
     * constants.
     *
     * @return the type of this event
     */
    public Type getType()
    {
        return type;
    }

    /**
     * <p>
     * An enumeration for the types supported by the
     * <code>CommandQueueEvent</code> class.
     * </p>
     */
    public static enum Type
    {
        /**
         * A command was added to the queue. Events of this type are generated
         * by the <code>execute()</code> method for each passed in
         * <code>Command</code> object.
         */
        COMMAND_ADDED,

        /**
         * A command is about to be executed. This event is triggered if a
         * worker thread fetches a command object and starts with its execution.
         */
        COMMAND_EXECUTING,

        /**
         * Execution of a command is finished. This event is triggered after a
         * worker thread has executed a command.
         */
        COMMAND_EXECUTED,

        /**
         * A command was added to an empty queue. This event is triggered if the
         * queue has been idle and then the processing of commands starts. After
         * all pending commands have been executed an event of type
         * <code>QUEUE_IDLE</code> is triggered. As long as the queue is in
         * the <em>BUSY</em> state, the <code>isPending()</code> method
         * returns <b>true</b>.
         */
        QUEUE_BUSY,

        /**
         * The last command contained in the queue was processed. This is the
         * opposite event of <code>QUEUE_BUSY</code>. It indicates that all
         * pending commands have been processed, and the queue is now empty.
         */
        QUEUE_IDLE
    }
}

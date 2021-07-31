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
package net.sf.jguiraffe.gui.cmd;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.event.EventListenerList;

import net.sf.jguiraffe.gui.builder.utils.GUISynchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A command queue implementation for GUI applications.
 * </p>
 * <p>
 * This class maintains a queue, in which <code>Command</code> objects can be
 * inserted by an application. At least one worker thread monitors the queue and
 * executes the commands.
 * </p>
 * <p>
 * In an application usually the <code>execute()</code> method is of most
 * importance. Here new <code>Command</code> objects are passed. Then the
 * application need not bother about execution of these commands in a background
 * thread. The other methods will be used by worker threads to obtain commands
 * and send notifications about executed commands.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CommandQueueImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class CommandQueueImpl implements CommandQueue
{
    /** The logger. */
    private final Log log = LogFactory.getLog(CommandQueueImpl.class);

    /** Stores the GUI synchronizer. */
    private volatile GUISynchronizer sync;

    /** Stores the executor service. */
    private final ExecutorService executorService;

    /** A list with the registered event listeners. */
    private final EventListenerList listeners;

    /** A counter for the commands that have been scheduled, but are not yet complete.*/
    private final AtomicInteger pendingCommands;

    /**
     * Creates a new instance of <code>CommandQueue</code> and initializes it
     * with the <code>GUISynchronizer</code>. A default
     * <code>ExecutorService</code> will be created.
     *
     * @param sync the GUI synchronizer object (must not be <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is missing
     */
    public CommandQueueImpl(GUISynchronizer sync)
    {
        this(sync, createDefaultExecutorService());
    }

    /**
     * Creates a new instance of <code>CommandQueueImpl</code> and initializes
     * it with the <code>GUISynchronizer</code> and the
     * <code>ExecutorService</code> to be used.
     *
     * @param sync the <code>GUISynchronizer</code> (must not be <b>null</b>)
     * @param execSrvc the <code>ExecutorService</code> (must not be <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is missing
     */
    public CommandQueueImpl(GUISynchronizer sync, ExecutorService execSrvc)
    {
        if (execSrvc == null)
        {
            throw new IllegalArgumentException(
                    "Executor service must not be null!");
        }

        setGUISynchronizer(sync);
        executorService = execSrvc;
        listeners = new EventListenerList();
        pendingCommands = new AtomicInteger();
    }

    /**
     * Returns the <code>GUISynchronizer</code>.
     *
     * @return the GUI synchronizer
     */
    public GUISynchronizer getGUISynchronizer()
    {
        return sync;
    }

    /**
     * Sets the <code>GUISynchronizer</code>.
     *
     * @param sync the GUI synchronizer (must not be <b>null</b>)
     * @throws IllegalArgumentException if the synchronizer is <b>null</b>
     */
    public void setGUISynchronizer(GUISynchronizer sync)
    {
        if (sync == null)
        {
            throw new IllegalArgumentException(
                    "GUISynchronizer must not be null!");
        }
        this.sync = sync;
    }

    /**
     * Returns the <code>ExecutorService</code> used by this command queue.
     *
     * @return the executor service
     */
    public ExecutorService getExecutorService()
    {
        return executorService;
    }

    /**
     * Adds an event listener to this queue.
     *
     * @param l the listener to add (must not be <b>null</b>)
     * @throws IllegalArgumentException if the listener is undefined
     */
    public void addQueueListener(CommandQueueListener l)
    {
        if (l == null)
        {
            throw new IllegalArgumentException(
                    "Event listener must not be null!");
        }

        listeners.add(CommandQueueListener.class, l);
    }

    /**
     * Removes the specified event listener from this queue.
     *
     * @param l the listener to remove
     */
    public void removeQueueListener(CommandQueueListener l)
    {
        listeners.remove(CommandQueueListener.class, l);
    }

    /**
     * Executes the specified command object. This implementation calls
     * <code>createTask()</code> to create a task object for actually
     * executing the command. This task is then passed to the
     * <code>ExecutorService</code>, so it will be processed by a background
     * thread.
     *
     * @param cmd the command to be executed (must not be <b>null</b>)
     * @throws IllegalArgumentException if the command is <b>null</b>
     * @throws IllegalStateException if <code>shutdown()</code> has already
     *         been called
     */
    public void execute(Command cmd)
    {
        if (cmd == null)
        {
            throw new IllegalArgumentException("Command must not be null!");
        }
        if (isShutdown())
        {
            throw new IllegalStateException(
                    "Cannot execute commands after shutdown!");
        }

        checkForBusyEvent();
        fireQueueEvent(cmd, CommandQueueEvent.Type.COMMAND_ADDED);

        if (cmd instanceof ScheduleAware)
        {
            ((ScheduleAware) cmd).commandScheduled(this);
        }
        getExecutorService().execute(createTask(cmd));
    }

    /**
     * This method is called by a worker thread if a command has been executed.
     *
     * @param cmd the command that has been processed
     */
    public void processingFinished(Command cmd)
    {
        fireQueueEvent(cmd, CommandQueueEvent.Type.COMMAND_EXECUTED);
        checkForIdleEvent();
    }

    /**
     * Tests whether there are commands that have not been executed. This
     * implementation uses a counter that keeps track of the scheduled
     * commands (i.e. the number of commands passed to the
     * {@code execute()} method) that have not yet been completed.
     *
     * @return a flag whether there are pending commands
     */
    public boolean isPending()
    {
        return pendingCommands.get() > 0;
    }

    /**
     * Tests whether this command queue was shutdown. This information can be
     * obtained from the underlying executor service.
     *
     * @return a flag whether <code>shutdown()</code> was called
     */
    public boolean isShutdown()
    {
        return getExecutorService().isShutdown();
    }

    /**
     * Shuts down this command queue. For this implementation, a shutdown means
     * that the underlying executor has to be shut down. Depending on the
     * boolean parameter this method will block until the executor service's
     * shutdown is complete.
     *
     * @param immediate a flag whether an immediate shutdown should be performed
     */
    public void shutdown(boolean immediate)
    {
        if (immediate)
        {
            getExecutorService().shutdownNow();
        }

        else
        {
            getExecutorService().shutdown();
            try
            {
                getExecutorService().awaitTermination(Long.MAX_VALUE,
                        TimeUnit.SECONDS);
            }
            catch (InterruptedException iex)
            {
                log.warn("Waiting for shutdown was interrupted.", iex);
            }
        }
    }

    /**
     * Notifies all registered listeners about a change in the state of this
     * queue.
     *
     * @param cmd the affected command
     * @param eventType the type of the event to fire
     */
    protected void fireQueueEvent(Command cmd, CommandQueueEvent.Type eventType)
    {
        CommandQueueEvent event = null;

        Object[] lst = listeners.getListenerList();
        for (int i = lst.length - 2; i >= 0; i -= 2)
        {
            if (lst[i] == CommandQueueListener.class)
            {
                if (event == null)
                {
                    // lazily create event
                    event = new CommandQueueEvent(this, cmd, eventType);
                }
                ((CommandQueueListener) lst[i + 1]).commandQueueChanged(event);
            }
        }
    }

    /**
     * Creates a task object for executing the passed in command. This task will
     * then be passed to the <code>ExecutorService</code>.
     *
     * @param cmd the command to be executed
     * @return the task for executing this command
     */
    protected Runnable createTask(Command cmd)
    {
        return new CommandExecutorTask(cmd);
    }

    /**
     * Fires a busy event if necessary. This method is called when a new command
     * is added. It updates the command counter and notifies the queue
     * listeners registered if the queue changes from the idle into the busy
     * state.
     */
    private void checkForBusyEvent()
    {
        if (pendingCommands.getAndIncrement() == 0)
        {
            fireQueueEvent(null, CommandQueueEvent.Type.QUEUE_BUSY);
        }
    }

    /**
     * Fires an idle event if necessary. This method is called when the
     * execution of a command is complete. It updates the command counter and
     * notifies the queue listeners registered if the queue changes from the
     * busy into the idle state.
     */
    private void checkForIdleEvent()
    {
        if (pendingCommands.decrementAndGet() == 0)
        {
            fireQueueEvent(null, CommandQueueEvent.Type.QUEUE_IDLE);
        }
    }

    /**
     * Creates a default <code>ExecutorService</code>. This method is called
     * by the constructor that does not accept an executor service. It creates a
     * thread pool executor service using a single thread with an unbounded
     * queue. This means that an arbitrary number of tasks can be scheduled,
     * which will be executed by a single worker thread only (so no
     * synchronization between the commands has to be performed).
     *
     * @return the default <code>ExecutorService</code>
     */
    static ExecutorService createDefaultExecutorService()
    {
        return new ThreadPoolExecutor(1, 1, Long.MAX_VALUE, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    /**
     * An internally used task class for executing a command. Instances of this
     * class are created for each scheduled command object and then passed to
     * the executor service. In the run() method the life-cycle methods of the
     * associated command object are called in the correct order.
     */
    private class CommandExecutorTask implements Runnable
    {
        /** The command to be executed. */
        private final Command cmd;

        /**
         * Creates a new instance of <code>CommandExecutorTask</code> and sets
         * the command to be executed.
         *
         * @param c the command
         */
        public CommandExecutorTask(Command c)
        {
            cmd = c;
        }

        /**
         * The main method of this task. Executes the associated command.
         */
        public void run()
        {
            log.debug("Executing command.");
            fireQueueEvent(cmd, CommandQueueEvent.Type.COMMAND_EXECUTING);

            try
            {
                cmd.execute();
            }
            catch (Throwable t)
            {
                cmd.onException(t);
            }
            finally
            {
                cmd.onFinally();
                handleGUIUpdate();
                processingFinished(cmd);
            }
        }

        /**
         * Cares for GUI updates after a command has been successfully executed.
         */
        protected void handleGUIUpdate()
        {
            Runnable r = cmd.getGUIUpdater();
            if (r != null)
            {
                getGUISynchronizer().syncInvoke(r);
            }
        }
    }
}

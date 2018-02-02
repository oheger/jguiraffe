/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.sf.jguiraffe.gui.builder.utils.GUISynchronizer;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for CommandQueueImpl and related classes.
 *
 * @author Oliver Heger
 * @version $Id: TestCommandQueueImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestCommandQueueImpl
{
    /** Stores a mock object for a synchronizer. */
    private GUISynchronizer mockSync;

    /** Stores a mock object for an executor service. */
    private ExecutorService mockExecutor;

    /**
     * Returns the synchronizer mock. It will be created on demand.
     *
     * @return the synchronizer mock
     */
    private GUISynchronizer getSync()
    {
        if (mockSync == null)
        {
            mockSync = EasyMock.createMock(GUISynchronizer.class);
        }
        return mockSync;
    }

    /**
     * Returns the executor mock. It will be created on demand.
     *
     * @return the executor mock
     */
    private ExecutorService getExecutor()
    {
        if (mockExecutor == null)
        {
            mockExecutor = EasyMock.createMock(ExecutorService.class);
        }
        return mockExecutor;
    }

    /**
     * Convenience method for replaying the defined mock objects.
     */
    private void replay()
    {
        if (mockSync != null)
        {
            EasyMock.replay(mockSync);
        }
        if (mockExecutor != null)
        {
            EasyMock.replay(mockExecutor);
        }
    }

    /**
     * Convenience method for verifying the defined mock objects.
     */
    private void verify()
    {
        if (mockSync != null)
        {
            EasyMock.verify(mockSync);
        }
        if (mockExecutor != null)
        {
            EasyMock.verify(mockExecutor);
        }
    }

    /**
     * Creates a test queue with the mock synchronizer and the mock executor.
     *
     * @return the test queue
     */
    private CommandQueueTestImpl setupQueue()
    {
        return new CommandQueueTestImpl(getSync(), getExecutor());
    }

    /**
     * Tests properties of a newly created queue.
     */
    @Test
    public void testInit()
    {
        CommandQueueImpl queue = setupQueue();
        assertEquals("Wrong synchronizer", getSync(), queue
                .getGUISynchronizer());
        assertEquals("Wrong executor", getExecutor(), queue
                .getExecutorService());
        assertFalse("Already commands waiting", queue.isPending());
    }

    /**
     * Tests creating a queue without specifying an executor service. In this
     * case a default executor should be created.
     */
    @Test
    public void testInitDefaultExecutor()
    {
        CommandQueueImpl queue = new CommandQueueImpl(getSync());
        ThreadPoolExecutor exec = (ThreadPoolExecutor) queue
                .getExecutorService();
        assertNotNull("No executor service was set", exec);
        assertEquals("Wrong core pool size", 1, exec.getCorePoolSize());
        assertEquals("Wrong max pool size", 1, exec.getMaximumPoolSize());
    }

    /**
     * Tests creating an instance without specifying a synchronizer. This should
     * cause an exception.
     */
    @Test
    public void testInitNoSynchronizer()
    {
        try
        {
            new CommandQueueImpl(null);
            fail("Could create queue without a synchronizer!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests creating an instance without specifying an executor. This should
     * cause an exception.
     */
    @Test
    public void testInitNoExecutor()
    {
        try
        {
            new CommandQueueImpl(getSync(), null);
            fail("Could create queue without executor!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests creating a task for a command and executing it.
     */
    @Test
    public void testCreateTaskExecute()
    {
        CommandQueueImpl queue = setupQueue();
        TestCommand cmd = new TestCommand(false);
        replay();
        Runnable task = queue.createTask(cmd);
        task.run();
        assertEquals("Wrong number of execute calls", 1, cmd.getExecuteCalled());
        assertEquals("Wrong number of exception calls", 0, cmd
                .getOnExceptionCalled());
        assertEquals("Wrong number of finally calls", 1, cmd
                .getOnFinallyCalled());
        verify();
    }

    /**
     * Tests a task for executing a command when an exception occurs during
     * execution.
     */
    @Test
    public void testCreateTaskExecuteWithException()
    {
        CommandQueueImpl queue = setupQueue();
        TestCommand cmd = new TestCommand(false);
        cmd.setThrowException(true);
        replay();
        Runnable task = queue.createTask(cmd);
        task.run();
        assertEquals("Wrong number of execute calls", 1, cmd.getExecuteCalled());
        assertEquals("Wrong number of exception calls", 1, cmd
                .getOnExceptionCalled());
        assertEquals("Wrong number of finally calls", 1, cmd
                .getOnFinallyCalled());
        verify();
    }

    /**
     * Tests a task for executing a command that needs to update the GUI.
     */
    @Test
    public void testCreateTaskGUIUpdate()
    {
        CommandQueueImpl queue = setupQueue();
        getSync().syncInvoke((Runnable) EasyMock.anyObject());
        TestCommand cmd = new TestCommand(true);
        replay();
        Runnable task = queue.createTask(cmd);
        task.run();
        assertEquals("Wrong number of execute calls", 1, cmd.getExecuteCalled());
        assertEquals("Wrong number of exception calls", 0, cmd
                .getOnExceptionCalled());
        assertEquals("Wrong number of finally calls", 1, cmd
                .getOnFinallyCalled());
        verify();
    }

    /**
     * Tests whether the GUI update is performed, even if an exception occurs.
     */
    @Test
    public void testCreateTaskExceptionGUIUpdate() {
        CommandQueueImpl queue = setupQueue();
        getSync().syncInvoke((Runnable) EasyMock.anyObject());
        TestCommand cmd = new TestCommand(true);
        cmd.setThrowException(true);
        replay();
        Runnable task = queue.createTask(cmd);
        task.run();
        assertEquals("Wrong number of execute calls", 1, cmd.getExecuteCalled());
        assertEquals("Wrong number of exception calls", 1, cmd
                .getOnExceptionCalled());
        assertEquals("Wrong number of finally calls", 1, cmd
                .getOnFinallyCalled());
        verify();
    }

    /**
     * Tests executing a command.
     */
    @Test
    public void testExecute()
    {
        Runnable r = EasyMock.createNiceMock(Runnable.class);
        Command cmd = EasyMock.createNiceMock(Command.class);
        CommandQueueTestImpl queue = setupQueue();
        queue.mockTask = r;
        EasyMock.expect(getExecutor().isShutdown()).andReturn(Boolean.FALSE);
        getExecutor().execute(r);
        replay();
        queue.execute(cmd);
        assertTrue("No commands pending", queue.isPending());
        verify();
    }

    /**
     * Tests executing a null command. This should cause an exception.
     */
    @Test
    public void testExecuteNull()
    {
        CommandQueueImpl queue = setupQueue();
        replay();
        try
        {
            queue.execute(null);
            fail("Could execute null command!");
        }
        catch (IllegalArgumentException iex)
        {
            verify();
        }
    }

    /**
     * Tests an immediate shutdown.
     */
    @Test
    public void testShutdownImmediately()
    {
        CommandQueueImpl queue = setupQueue();
        EasyMock.expect(getExecutor().shutdownNow()).andReturn(
                new ArrayList<Runnable>());
        replay();
        queue.shutdown(true);
        verify();
    }

    /**
     * Tests a graceful shutdown.
     */
    @Test
    public void testShutdown() throws InterruptedException
    {
        CommandQueueImpl queue = setupQueue();
        getExecutor().shutdown();
        EasyMock.expect(
                getExecutor()
                        .awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS))
                .andReturn(Boolean.TRUE);
        replay();
        queue.shutdown(false);
        verify();
    }

    /**
     * Tests a graceful shutdown that is interrupted. The interrupted exception
     * should be caught and ignored.
     */
    @Test
    public void testShutdownInterrupted() throws InterruptedException
    {
        CommandQueueImpl queue = setupQueue();
        getExecutor().shutdown();
        EasyMock.expect(
                getExecutor()
                        .awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS))
                .andThrow(new InterruptedException("A test exception"));
        replay();
        queue.shutdown(false);
        verify();
    }

    /**
     * Tests the isShutdown() method.
     */
    @Test
    public void testIsShutdown()
    {
        CommandQueueImpl queue = setupQueue();
        EasyMock.expect(getExecutor().isShutdown()).andReturn(Boolean.FALSE);
        EasyMock.expect(getExecutor().isShutdown()).andReturn(Boolean.TRUE);
        replay();
        assertFalse("Wrong value for isShutdown (1)", queue.isShutdown());
        assertTrue("Wrong value for isShutdown (2)", queue.isShutdown());
        verify();
    }

    /**
     * Tests executing a command after shutdown was called. This should cause an
     * exception.
     */
    @Test
    public void testExecuteAfterShutdown()
    {
        CommandQueueImpl queue = setupQueue();
        EasyMock.expect(getExecutor().isShutdown()).andReturn(Boolean.TRUE);
        replay();
        try
        {
            queue.execute(EasyMock.createNiceMock(Command.class));
            fail("Could execute command after shutdown!");
        }
        catch (IllegalStateException istex)
        {
            // ok
        }
    }

    /**
     * Tests the events generated by an added command.
     */
    @Test
    public void testEventCommandAdded()
    {
        Runnable r = EasyMock.createNiceMock(Runnable.class);
        Command cmd = EasyMock.createNiceMock(Command.class);
        CommandQueueTestImpl queue = setupQueue();
        EasyMock.expect(getExecutor().isShutdown()).andReturn(Boolean.FALSE);
        getExecutor().execute(r);
        replay();
        queue.mockTask = r;
        TestQueueListener l = new TestQueueListener();
        queue.addQueueListener(l);
        queue.execute(cmd);
        verify();
        assertEquals("Wrong number of added events", 1, l
                .getEventCount(CommandQueueEvent.Type.COMMAND_ADDED));
        assertEquals("Wrong number of busy events", 1, l
                .getEventCount(CommandQueueEvent.Type.QUEUE_BUSY));
        assertEquals("Command already executed", 0, l
                .getEventCount(CommandQueueEvent.Type.COMMAND_EXECUTING));
        assertEquals("Queue already idle", 0, l
                .getEventCount(CommandQueueEvent.Type.QUEUE_IDLE));
    }

    /**
     * Tests the events generated by the execution of a command.
     */
    @Test
    public void testEventCommandExecuting()
    {
        CommandQueueImpl queue = setupQueue();
        TestCommand cmd = new TestCommand(false);
        replay();
        TestQueueListener l = new TestQueueListener();
        queue.addQueueListener(l);
        Runnable task = queue.createTask(cmd);
        task.run();
        verify();
        assertEquals("Wrong number of executing events", 1, l
                .getEventCount(CommandQueueEvent.Type.COMMAND_EXECUTING));
        assertEquals("Wrong number of executed events", 1, l
                .getEventCount(CommandQueueEvent.Type.COMMAND_EXECUTED));
        assertEquals("Wrong command passed", cmd, l.getLastCommand());
    }

    /**
     * Tests whether the correct busy and idle events are generated.
     */
    @Test
    public void testEventBusyAndIdle()
    {
        Runnable r = EasyMock.createNiceMock(Runnable.class);
        Command cmd = EasyMock.createNiceMock(Command.class);
        CommandQueueTestImpl queue = setupQueue();
        EasyMock.expect(getExecutor().isShutdown()).andReturn(Boolean.FALSE)
                .anyTimes();
        getExecutor().execute(r);
        EasyMock.expectLastCall().times(3);
        replay();
        queue.mockTask = r;
        TestQueueListener l = new TestQueueListener();
        queue.addQueueListener(l);
        queue.execute(cmd);
        assertEquals("Wrong number of busy events (1)", 1, l
                .getEventCount(CommandQueueEvent.Type.QUEUE_BUSY));
        assertEquals("Queue already idle", 0, l
                .getEventCount(CommandQueueEvent.Type.QUEUE_IDLE));
        queue.processingFinished(cmd);
        assertEquals("Wrong number of busy events (2)", 1, l
                .getEventCount(CommandQueueEvent.Type.QUEUE_BUSY));
        assertEquals("Wrong number of idle events (1)", 1, l
                .getEventCount(CommandQueueEvent.Type.QUEUE_IDLE));
        queue.execute(cmd);
        queue.execute(cmd);
        queue.processingFinished(cmd);
        assertEquals("Wrong number of busy events (3)", 2, l
                .getEventCount(CommandQueueEvent.Type.QUEUE_BUSY));
        assertEquals("Wrong number of idle events (2)", 1, l
                .getEventCount(CommandQueueEvent.Type.QUEUE_IDLE));
        queue.processingFinished(cmd);
        assertEquals("Wrong number of idle events (3)", 2, l
                .getEventCount(CommandQueueEvent.Type.QUEUE_IDLE));
        verify();
    }

    /**
     * Tests adding a null listener to the queue. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddQueueListenerNull()
    {
        CommandQueueImpl queue = setupQueue();
        queue.addQueueListener(null);
    }

    /**
     * Tests whether a listener can be removed.
     */
    @Test
    public void testRemoveQueueListener()
    {
        Runnable r = EasyMock.createNiceMock(Runnable.class);
        Command cmd = EasyMock.createNiceMock(Command.class);
        CommandQueueTestImpl queue = setupQueue();
        EasyMock.expect(getExecutor().isShutdown()).andReturn(Boolean.FALSE);
        getExecutor().execute(r);
        replay();
        queue.mockTask = r;
        TestQueueListener l = new TestQueueListener();
        queue.addQueueListener(l);
        queue.execute(cmd);
        queue.removeQueueListener(l);
        queue.processingFinished(cmd);
        verify();
        assertEquals("Wrong number of busy events", 1, l
                .getEventCount(CommandQueueEvent.Type.QUEUE_BUSY));
        assertEquals("Received an idle event", 0, l
                .getEventCount(CommandQueueEvent.Type.QUEUE_IDLE));
    }

    /**
     * Tests whether the ScheduleAware interface is correctly handled when
     * executing a command.
     */
    @Test
    public void testExecuteScheduleAware()
    {
        Runnable r = EasyMock.createNiceMock(Runnable.class);
        CommandQueueTestImpl queue = setupQueue();
        queue.mockTask = r;
        EasyMock.expect(getExecutor().isShutdown()).andReturn(Boolean.FALSE);
        getExecutor().execute(r);
        replay();
        TestCommandScheduleAware cmd = new TestCommandScheduleAware();
        queue.execute(cmd);
        verify();
        assertEquals("Schedule callback not invoked", queue, cmd.scheduledQueue);
    }

    /**
     * An implementation of CommandQueueImpl that is easer to test.
     */
    private static class CommandQueueTestImpl extends CommandQueueImpl
    {
        /** A mock task to be returned by the createTask() method. */
        Runnable mockTask;

        public CommandQueueTestImpl(GUISynchronizer sync,
                ExecutorService execSrvc)
        {
            super(sync, execSrvc);
        }

        /**
         * Returns either a mock task or calls the super method.
         */
        @Override
        protected Runnable createTask(Command cmd)
        {
            return (mockTask != null) ? mockTask : super.createTask(cmd);
        }
    }

    /**
     * A test command that gets executed and keeps track of the methods that
     * have been called.
     */
    private static class TestCommand extends CommandBase
    {
        private int executeCalled;

        private int onExceptionCalled;

        private int onFinallyCalled;

        private boolean throwException;

        public TestCommand(boolean updateGUI)
        {
            super(updateGUI);
        }

        @Override
        public void onException(Throwable t)
        {
            super.onException(t);
            onExceptionCalled++;
        }

        @Override
        public void onFinally()
        {
            super.onFinally();
            onFinallyCalled++;
        }

        public void execute() throws Exception
        {
            executeCalled++;
            if (isThrowException())
            {
                throw new Exception("Exception in command!");
            }
        }

        public boolean isThrowException()
        {
            return throwException;
        }

        public void setThrowException(boolean throwException)
        {
            this.throwException = throwException;
        }

        public int getExecuteCalled()
        {
            return executeCalled;
        }

        public int getOnExceptionCalled()
        {
            return onExceptionCalled;
        }

        public int getOnFinallyCalled()
        {
            return onFinallyCalled;
        }
    }

    /**
     * A test command implementing the ScheduleAware interface.
     */
    private static class TestCommandScheduleAware extends TestCommand implements
            ScheduleAware
    {
        /** The queue passed to commandScheduled(). */
        CommandQueue scheduledQueue;

        public TestCommandScheduleAware()
        {
            super(false);
        }

        /**
         * Records this invocation.
         */
        public void commandScheduled(CommandQueue queue)
        {
            scheduledQueue = queue;
        }
    }

    /**
     * An implementation of the CommandQueueListener interface for testing. This
     * implementation allows recording the received events.
     */
    static class TestQueueListener implements CommandQueueListener
    {
        /** A mapping for the event types. */
        private static final CommandQueueEvent.Type[] EVENTS = {
                CommandQueueEvent.Type.COMMAND_ADDED,
                CommandQueueEvent.Type.COMMAND_EXECUTED,
                CommandQueueEvent.Type.COMMAND_EXECUTING,
                CommandQueueEvent.Type.QUEUE_BUSY,
                CommandQueueEvent.Type.QUEUE_IDLE
        };

        /** An array with the numbers of events received for the possible types. */
        private int[] eventCount;

        /** Stores the command of the last received event. */
        private Command lastCommand;

        public TestQueueListener()
        {
            eventCount = new int[EVENTS.length];
        }

        public void commandQueueChanged(CommandQueueEvent e)
        {
            eventCount[getEventIndex(e.getType())]++;
            lastCommand = e.getCommand();
        }

        /**
         * Returns the command of the last received event.
         *
         * @return the last command
         */
        public Command getLastCommand()
        {
            return lastCommand;
        }

        /**
         * Returns the number of received events for the specified event type.
         *
         * @param event the event type
         * @return the number of events received for this type
         */
        public int getEventCount(CommandQueueEvent.Type event)
        {
            return eventCount[getEventIndex(event)];
        }

        private static int getEventIndex(CommandQueueEvent.Type event)
        {
            for (int idx = 0; idx < EVENTS.length; idx++)
            {
                if (EVENTS[idx] == event)
                {
                    return idx;
                }
            }
            throw new IllegalArgumentException("Unknown event type!");
        }
    }
}

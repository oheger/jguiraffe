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
package net.sf.jguiraffe.gui.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.enablers.ElementEnabler;
import net.sf.jguiraffe.gui.builder.enablers.NullEnabler;
import net.sf.jguiraffe.gui.cmd.Command;
import net.sf.jguiraffe.gui.cmd.CommandQueue;
import net.sf.jguiraffe.gui.cmd.CommandWrapper;
import net.sf.jguiraffe.gui.cmd.ScheduleAware;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for CommandActionTask.
 *
 * @author Oliver Heger
 * @version $Id: TestCommandActionTask.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestCommandActionTask
{
    /** Constant for the test command object. */
    private static final Command COMMAND = EasyMock
            .createNiceMock(Command.class);

    /** Constant for the name of a command bean. */
    private static final String COMMAND_BEAN = "myCommandBean";

    /** The test application object. */
    private TestApplication app;

    /** The mock for the bean context. */
    private BeanContext beanContext;

    /** The action task to be tested. */
    private CommandActionTaskTestImpl task;

    @Before
    public void setUp() throws Exception
    {
        task = new CommandActionTaskTestImpl();
        app = new TestApplication();
        beanContext = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(beanContext.getBean(Application.BEAN_APPLICATION))
                .andStubReturn(app);
    }

    /**
     * Tests a newly created task object.
     */
    @Test
    public void testInit()
    {
        assertNull("BeanContext is set", task.getBeanContext());
        assertNull("Command object is set", task.getCommand());
        assertNull("Command bean name is set", task.getCommandBeanName());
    }

    /**
     * Tests querying the bean context when none is set. This should cause an
     * exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testFetchBeanContextNoContext()
    {
        task.fetchBeanContext();
    }

    /**
     * Tests querying the before enabler if none was set. In this case a dummy
     * should be returned.
     */
    @Test
    public void testGetBeforeEnablerUnspecified() throws FormBuilderException
    {
        assertSame("Wrong before enabler if unspecified", NullEnabler.INSTANCE,
                task.getBeforeEnabler());
        task.getBeforeEnabler().setEnabledState(new ComponentBuilderData(),
                true);
    }

    /**
     * Tests querying the after enabler if none was set. In this case a dummy
     * should be returned.
     */
    @Test
    public void testGetAfterEnablerUnspecified()
    {
        assertSame("Wrong after enabler if unspecified", NullEnabler.INSTANCE,
                task.getAfterEnabler());
    }

    /**
     * Tests whether specific enablers can be set.
     */
    @Test
    public void testSetEnablers()
    {
        ElementEnabler enBefore = EasyMock.createNiceMock(ElementEnabler.class);
        ElementEnabler enAfter = EasyMock.createNiceMock(ElementEnabler.class);
        task.setBeforeEnabler(enBefore);
        task.setAfterEnabler(enAfter);
        assertEquals("Wrong before enabler", enBefore, task.getBeforeEnabler());
        assertEquals("Wrong after enabler", enAfter, task.getAfterEnabler());
    }

    /**
     * Tests querying the after enabler if only a before enabler is set. In this
     * case the before enabler must be returned.
     */
    @Test
    public void testGetAfterEnablerBeforeOnly()
    {
        ElementEnabler enBefore = EasyMock.createNiceMock(ElementEnabler.class);
        task.setBeforeEnabler(enBefore);
        assertEquals("Wrong before enabler", enBefore, task.getBeforeEnabler());
        assertEquals("Wrong after enabler", enBefore, task.getAfterEnabler());
    }

    /**
     * Tests whether the application can be obtained from the bean context.
     */
    @Test
    public void testGetApplication()
    {
        EasyMock.replay(beanContext);
        task.setBeanContext(beanContext);
        assertSame("Wrong application", app, task.getApplication());
        EasyMock.verify(beanContext);
    }

    /**
     * Tests the fetchCommand() method when a command object is specified.
     */
    @Test
    public void testFetchCommandObject()
    {
        task.setCommand(COMMAND);
        assertEquals("Wrong command", COMMAND, task.fetchCommand());
    }

    /**
     * Tests fetching the command when a command bean name is specified.
     */
    @Test
    public void testFetchCommandBeanName()
    {
        Command cmd = EasyMock.createMock(Command.class);
        EasyMock.expect(beanContext.getBean(COMMAND_BEAN)).andReturn(cmd);
        EasyMock.replay(cmd, beanContext);
        task.setBeanContext(beanContext);
        task.setCommandBeanName(COMMAND_BEAN);
        assertEquals("Wrong command", cmd, task.fetchCommand());
        EasyMock.verify(cmd, beanContext);
    }

    /**
     * Tests fetching the command when both an object and a bean name are set.
     * In this case the object should be used.
     */
    @Test
    public void testFetchCommandObjectAndBeanName()
    {
        EasyMock.replay(beanContext);
        task.setBeanContext(beanContext);
        task.setCommand(COMMAND);
        task.setCommandBeanName(COMMAND_BEAN);
        assertEquals("Wrong command", COMMAND, task.fetchCommand());
        EasyMock.verify(beanContext);
    }

    /**
     * Tests fetching the command when it is undefined. This should cause an
     * exception.
     */
    @Test(expected = ApplicationRuntimeException.class)
    public void testFetchCommandUndefined()
    {
        task.fetchCommand();
    }

    /**
     * Tests the command wrapper.
     */
    @Test
    public void testCreateCommandWrapper()
    {
        Command wrapper = task.createCommandWrapper(COMMAND);
        assertTrue("Wrong wrapper class: " + wrapper,
                wrapper instanceof CommandWrapper);
        assertEquals("Wrong wrapped command", COMMAND,
                ((CommandWrapper) wrapper).getWrappedCommand());
    }

    /**
     * Tests whether the wrapper command invokes the before enabler when it is
     * scheduled.
     */
    @Test
    public void testCreateCommandWrapperCommandScheduled()
            throws FormBuilderException
    {
        ElementEnabler enBefore = EasyMock.createMock(ElementEnabler.class);
        ElementEnabler enAfter = EasyMock.createMock(ElementEnabler.class);
        CommandQueue queue = EasyMock.createMock(CommandQueue.class);
        ComponentBuilderData compData = new ComponentBuilderData();
        EasyMock
                .expect(
                        beanContext
                                .getBean(ComponentBuilderData.KEY_COMPONENT_BUILDER_DATA))
                .andReturn(compData);
        enBefore.setEnabledState(compData, false);
        EasyMock.replay(enBefore, enAfter, beanContext, queue);
        task.setBeanContext(beanContext);
        task.setBeforeEnabler(enBefore);
        task.setAfterEnabler(enAfter);
        Command wrapper = task.createCommandWrapper(COMMAND);
        assertTrue("Wrong wrapper class: " + wrapper,
                wrapper instanceof ScheduleAware);
        ScheduleAware schedAware = (ScheduleAware) wrapper;
        schedAware.commandScheduled(queue);
        EasyMock.verify(enBefore, enAfter, beanContext, queue);
    }

    /**
     * Tests the behavior of the command wrapper when the before enabler throws
     * an exception.
     */
    @Test
    public void testCreateCommandWrapperBeforeEnablerEx()
            throws FormBuilderException
    {
        ElementEnabler enBefore = EasyMock.createMock(ElementEnabler.class);
        CommandQueue queue = EasyMock.createMock(CommandQueue.class);
        ComponentBuilderData compData = new ComponentBuilderData();
        EasyMock
                .expect(
                        beanContext
                                .getBean(ComponentBuilderData.KEY_COMPONENT_BUILDER_DATA))
                .andReturn(compData);
        enBefore.setEnabledState(compData, false);
        FormBuilderException fbex = new FormBuilderException("Test exception!");
        EasyMock.expectLastCall().andThrow(fbex);
        EasyMock.replay(enBefore, beanContext, queue);
        task.setBeanContext(beanContext);
        task.setBeforeEnabler(enBefore);
        ScheduleAware cmd = (ScheduleAware) task.createCommandWrapper(COMMAND);
        try
        {
            cmd.commandScheduled(queue);
            fail("Exception not detected!");
        }
        catch (ApplicationRuntimeException arex)
        {
            assertEquals("Wrong nested exception", fbex, arex.getCause());
            EasyMock.verify(enBefore, beanContext, queue);
        }
    }

    /**
     * Tests updating of the UI after the command was executed. Here the after
     * enabler must be called. If the wrapped command provides a UI updater,
     * this object also must be invoked.
     */
    @Test
    public void testCreateCommandWrapperGetGUIUpdaterDefined()
            throws FormBuilderException
    {
        ElementEnabler enBefore = EasyMock.createMock(ElementEnabler.class);
        ElementEnabler enAfter = EasyMock.createMock(ElementEnabler.class);
        CommandQueue queue = EasyMock.createMock(CommandQueue.class);
        Command cmd = EasyMock.createMock(Command.class);
        Runnable updater = EasyMock.createMock(Runnable.class);
        ComponentBuilderData compData = new ComponentBuilderData();
        EasyMock
                .expect(
                        beanContext
                                .getBean(ComponentBuilderData.KEY_COMPONENT_BUILDER_DATA))
                .andReturn(compData);
        enAfter.setEnabledState(compData, true);
        EasyMock.expect(cmd.getGUIUpdater()).andReturn(updater);
        updater.run();
        EasyMock.replay(enBefore, enAfter, cmd, updater, beanContext, queue);
        task.setBeanContext(beanContext);
        task.setBeforeEnabler(enBefore);
        task.setAfterEnabler(enAfter);
        Command wrapper = task.createCommandWrapper(cmd);
        wrapper.getGUIUpdater().run();
        EasyMock.verify(enBefore, enAfter, cmd, updater, beanContext, queue);
    }

    /**
     * Tests the behavior of the GUI updater of the wrapper command when the
     * wrapped command does not need a GUI update.
     */
    @Test
    public void testCreateCommandWrapperGetGUIUpdaterUndefined()
            throws FormBuilderException
    {
        ElementEnabler enAfter = EasyMock.createMock(ElementEnabler.class);
        CommandQueue queue = EasyMock.createMock(CommandQueue.class);
        Command cmd = EasyMock.createMock(Command.class);
        ComponentBuilderData compData = new ComponentBuilderData();
        EasyMock
                .expect(
                        beanContext
                                .getBean(ComponentBuilderData.KEY_COMPONENT_BUILDER_DATA))
                .andReturn(compData);
        enAfter.setEnabledState(compData, true);
        EasyMock.expect(cmd.getGUIUpdater()).andReturn(null);
        EasyMock.replay(enAfter, cmd, beanContext, queue);
        task.setBeanContext(beanContext);
        task.setAfterEnabler(enAfter);
        Command wrapper = task.createCommandWrapper(cmd);
        wrapper.getGUIUpdater().run();
        EasyMock.verify(enAfter, cmd, beanContext, queue);
    }

    /**
     * Tests the run() method.
     */
    @Test
    public void testRun()
    {
        Command mockCmd = EasyMock.createMock(Command.class);
        EasyMock.replay(beanContext, mockCmd);
        task.setCommand(COMMAND);
        task.setBeanContext(beanContext);
        task.mockCommandWrapper = mockCmd;
        task.run();
        assertEquals("Command not executed", mockCmd, app.command);
        EasyMock.verify(beanContext, mockCmd);
    }

    /**
     * A specialized application implementation that checks which and how many
     * commands are passed to it.
     */
    private static class TestApplication extends Application
    {
        public Command command;

        @Override
        public void execute(Command cmd)
        {
            if (command != null)
            {
                // expect only one invocation
                fail("Only one call of execute() expected!");
            }
            command = cmd;
        }
    }

    /**
     * A specialized command task implementation for testing the correct
     * creation of wrapper commands.
     */
    private static class CommandActionTaskTestImpl extends CommandActionTask
    {
        /** The mock command to be returned by createCommandWrapper(). */
        Command mockCommandWrapper;

        /**
         * Either returns the mock command wrapper or calls the super method.
         */
        @Override
        protected Command createCommandWrapper(Command actualCommand)
        {
            return (mockCommandWrapper != null) ? mockCommandWrapper : super
                    .createCommandWrapper(actualCommand);
        }
    }
}

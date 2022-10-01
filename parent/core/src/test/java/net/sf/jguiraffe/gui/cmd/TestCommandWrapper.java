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

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link CommandWrapper}.
 *
 * @author Oliver Heger
 * @version $Id: TestCommandWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestCommandWrapper
{
    /** A mock object for the wrapped command. */
    private Command command;

    /** The wrapper to be tested. */
    private CommandWrapper wrapper;

    @Before
    public void setUp() throws Exception
    {
        command = EasyMock.createMock(Command.class);
        wrapper = new CommandWrapper(command);
    }

    /**
     * Tests creating an instance without a wrapped command. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoCommand()
    {
        new CommandWrapper(null);
    }

    /**
     * Tests a newly created wrapper.
     */
    @Test
    public void testInit()
    {
        assertEquals("Wrong wrapped command", command, wrapper
                .getWrappedCommand());
    }

    /**
     * Tests the execute() implementation.
     */
    @Test
    public void testExecute() throws Exception
    {
        command.execute();
        EasyMock.replay(command);
        wrapper.execute();
        EasyMock.verify(command);
    }

    /**
     * Tests the onException() implementation.
     */
    @Test
    public void testOnException()
    {
        final Throwable t = new RuntimeException("Test exception!");
        command.onException(t);
        EasyMock.replay(command);
        wrapper.onException(t);
        EasyMock.verify(command);
    }

    /**
     * Tests the onFinally() implementation.
     */
    @Test
    public void testOnFinally()
    {
        command.onFinally();
        EasyMock.replay(command);
        wrapper.onFinally();
        EasyMock.verify(command);
    }

    /**
     * Tests the getGUIUpdater() implementation.
     */
    @Test
    public void testGetGUIUpdater()
    {
        Runnable updater = EasyMock.createMock(Runnable.class);
        EasyMock.expect(command.getGUIUpdater()).andReturn(updater);
        EasyMock.replay(command, updater);
        assertEquals("Wrong updater", updater, wrapper.getGUIUpdater());
        EasyMock.verify(command, updater);
    }
}

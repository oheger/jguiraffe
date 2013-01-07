/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
import net.sf.jguiraffe.resources.Message;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ApplicationShutdownActionTask}.
 *
 * @author Oliver Heger
 * @version $Id: TestApplicationShutdownActionTask.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestApplicationShutdownActionTask
{
    /** Constant for the test message resource ID. */
    private static final String MSG_RES = "messageResourceID";

    /** Constant for the test title resource ID. */
    private static final String TIT_RES = "titleResourceID";

    /** The task to be tested. */
    private ApplicationShutdownActionTask task;

    @Before
    public void setUp() throws Exception
    {
        task = new ApplicationShutdownActionTask();
    }

    /**
     * Helper method for testing a resource ID object that is a Message.
     *
     * @param resID the object to be tested
     * @param expGrp the expected resource group
     * @param expID the expected resource ID
     */
    private static void checkResID(Object resID, Object expGrp, Object expID)
    {
        Message msg = (Message) resID;
        assertEquals("Wrong resource group", expGrp, msg.getResourceGroup());
        assertEquals("Wrong resource ID", expID, msg.getResourceKey());
        assertEquals("Wrong number of parameters", 0,
                msg.getParameters().length);
    }

    /**
     * Tests the default resource ID returned for the exit prompt message.
     */
    @Test
    public void testGetExitPromptMessageResourceDefault()
    {
        checkResID(task.getExitPromptMessageResource(),
                ApplicationResources.APPLICATION_RESOURCE_GROUP,
                ApplicationResources
                        .resourceID(ApplicationResources.Keys.EXIT_PROMPT_MSG));
    }

    /**
     * Tests the default resource ID returned for the exit prompt title.
     */
    @Test
    public void testGetExitPromptTitleResourceDefault()
    {
        checkResID(task.getExitPromptTitleResource(),
                ApplicationResources.APPLICATION_RESOURCE_GROUP,
                ApplicationResources
                        .resourceID(ApplicationResources.Keys.EXIT_PROMPT_TIT));
    }

    /**
     * Tests the run() method if no application was set. This should cause an
     * exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testRunNoApplication()
    {
        assertNull("Got an application", task.getApplication());
        task.run();
    }

    /**
     * Tests whether the task can be executed correctly.
     */
    @Test
    public void testRun()
    {
        ApplicationTestImpl app = new ApplicationTestImpl();
        task.setExitPromptMessageResource(MSG_RES);
        task.setExitPromptTitleResource(TIT_RES);
        task.setApplication(app);
        task.run();
        assertEquals("Wrong message for shutdown", MSG_RES, app.msgResID);
        assertEquals("Wrong title for shutdown", TIT_RES, app.titResID);
    }

    /**
     * A test Application implementation for testing whether the shutdown()
     * method is correctly invoked.
     */
    private static class ApplicationTestImpl extends Application
    {
        /** The resource ID for the message. */
        Object msgResID;

        /** The resource ID for the title. */
        Object titResID;

        /**
         * Records this invocation.
         */
        @Override
        public void shutdown(Object msgres, Object titleres)
        {
            msgResID = msgres;
            titResID = titleres;
        }
    }
}

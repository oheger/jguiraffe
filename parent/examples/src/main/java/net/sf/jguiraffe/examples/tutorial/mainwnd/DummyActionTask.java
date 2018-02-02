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
package net.sf.jguiraffe.examples.tutorial.mainwnd;

import net.sf.jguiraffe.gui.app.Application;
import net.sf.jguiraffe.gui.app.ApplicationClient;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;

/**
 * <p>
 * A dummy task class that is associated with actions for which no
 * implementation is provided.
 * </p>
 * <p>
 * This task simply opens a dialog box that displays a message stating that the
 * desired functionality is not yet implemented.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DummyActionTask.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DummyActionTask implements Runnable, ApplicationClient
{
    /** The resource ID for the message box title. */
    private static final String RES_TITLE = "task_notimplemented_title";

    /** The resource ID for the message box text. */
    private static final String RES_MESSAGE = "task_notimplemented_msg";

    /** A reference to the application. */
    private Application application;

    @Override
    public void setApplication(Application app)
    {
        application = app;
    }

    /**
     * Executes this task. Displays a message box.
     */
    @Override
    public void run()
    {
        application.getApplicationContext().messageBox(RES_MESSAGE, RES_TITLE,
                MessageOutput.MESSAGE_INFO, MessageOutput.BTN_OK);
    }
}

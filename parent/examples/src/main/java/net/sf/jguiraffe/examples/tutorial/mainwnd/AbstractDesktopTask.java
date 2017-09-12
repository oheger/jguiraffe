/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import java.awt.Desktop;
import java.io.File;
import java.util.List;

import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.cmd.CommandBase;
import net.sf.jguiraffe.resources.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * An abstract base class for action tasks that perform a desktop operation with
 * the currently selected file.
 * </p>
 * <p>
 * The tutorial application defines some actions for doing something with files
 * which is implemented by the {@code Desktop} class new in Java 1.6 (e.g.
 * opening a file or printing it). This abstract base class provides basic
 * functionality for implementing such functionality. It is initialized with a
 * reference to the main controller from which the currently selected file can
 * be obtained. It also takes care for exception handling. A concrete subclass
 * only has to implement the desired {@code Desktop} operation.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AbstractDesktopTask.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractDesktopTask implements Runnable
{
    /** The resource ID of the title of the error message box. */
    private static final String RESID_ERR_TITLE = "task_errdesktop_title";

    /** The resource ID of the text of the error message box. */
    private static final String RESID_ERR_TEXT = "task_errdesktop_msg";

    /** The logger. */
    protected final Log log = LogFactory.getLog(getClass());

    /** A reference to the main controller. */
    private final MainWndController controller;

    /**
     * Creates a new instance of {@code AbstractDesktopTask} and sets the
     * reference to the main controller.
     *
     * @param ctrl the main controller reference
     */
    protected AbstractDesktopTask(MainWndController ctrl)
    {
        controller = ctrl;
    }

    /**
     * Returns a reference to the main controller.
     *
     * @return the main controller
     */
    public MainWndController getController()
    {
        return controller;
    }

    /**
     * Returns the selected file.
     *
     * @return the selected file
     */
    public File getSelectedFile()
    {
        List<File> selection = controller.getSelectedFiles();
        assert selection.size() == 1 : "Wrong number of selected files!";
        return selection.get(0);
    }

    /**
     * Executes this task. This implementation creates a new command for
     * executing the desktop operation.
     */
    @Override
    public void run()
    {
        getController().getApplication().execute(new DesktopCommand());
    }

    /**
     * Performs the desired operation with the selected file. This method has to
     * be implemented by concrete subclasses.
     *
     * @param desktop the {@code Desktop} instance
     * @throws Exception if an error occurs
     */
    protected abstract void performDesktopOperation(Desktop desktop)
            throws Exception;

    /**
     * A specialized command implementation for executing the desktop operation
     * in a background thread.
     */
    private class DesktopCommand extends CommandBase
    {
        /**
         * Executes this command. This implementation calls the
         * {@code performDesktopOperation()} method.
         */
        @Override
        public void execute() throws Exception
        {
            performDesktopOperation(Desktop.getDesktop());
        }

        /**
         * Performs UI updates after executing the command. This implementation
         * checks whether an error occurred. If this is the case, an exception
         * message is displayed to the user.
         */
        @Override
        protected void performGUIUpdate()
        {
            if (getException() != null)
            {
                log.error("Error on Desktop operation", getException());
                getController()
                        .getApplication()
                        .getApplicationContext()
                        .messageBox(
                                new Message(null, RESID_ERR_TEXT,
                                        getSelectedFile().getName()),
                                RESID_ERR_TITLE, MessageOutput.MESSAGE_ERROR,
                                MessageOutput.BTN_OK);
            }
        }
    }
}

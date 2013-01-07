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
package net.sf.jguiraffe.examples.tutorial.bgtask;

import net.sf.jguiraffe.gui.builder.components.model.ProgressBarHandler;
import net.sf.jguiraffe.gui.builder.event.FormActionEvent;
import net.sf.jguiraffe.gui.builder.event.FormActionListener;
import net.sf.jguiraffe.gui.builder.utils.GUISynchronizer;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.cmd.CommandBase;

/**
 * <p>
 * A command class for executing the background task.
 * </p>
 * <p>
 * This command class simulates the execution of a long-running background task.
 * The {@link #execute()} only sleeps a configurable number of seconds, but it
 * demonstrates how an information dialog with a progress bar can be used to
 * give the user feedback about the progress of the background task. This visual
 * feedback is optional; it can be disabled, then the command only sleeps.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BgTaskCommand.java 205 2012-01-29 18:29:57Z oheger $
 */
public class BgTaskCommand extends CommandBase implements FormActionListener
{
    /** Constant for a second sleep time. */
    private static final long SLEEP_SECOND = 1000;

    /** Constant for the maximum of the progress bar. */
    private static final int PROGRESS_MAX = 100;

    /** The synchronizer. */
    private final GUISynchronizer synchronizer;

    /** Stores the information dialog. */
    private final Window infoDialog;

    /** The bean with information of the background task. */
    private final BgTaskData data;

    /** The component handler for the progress bar. */
    private final ProgressBarHandler progressHandler;

    /** A flag whether execution of the command should be canceled. */
    private volatile boolean cancelExecution;

    /**
     * Creates a new instance of {@code BgTaskCommand} and initializes it.
     *
     * @param sync the {@code GUISynchronizer}
     * @param infoWindow the reference to the information window
     * @param taskData the data object with information about the background
     *        task
     * @param handler the handler for the progress bar
     */
    public BgTaskCommand(GUISynchronizer sync, Window infoWindow,
            BgTaskData taskData, ProgressBarHandler handler)
    {
        super(taskData.isVisual());
        synchronizer = sync;
        infoDialog = infoWindow;
        data = taskData;
        progressHandler = handler;
    }

    /**
     * Notifies this object that the cancel button was pressed. This method sets
     * the cancel flag, so that the execution of the command is canceled.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(FormActionEvent e)
    {
        // disable button to signal the user that the click was received
        e.getHandler().setEnabled(false);

        cancelExecution = true;
    }

    /**
     * Executes this command. Sleeps the specified number of seconds. If visual
     * feedback is enabled, the progress bar is updated.
     */
    @Override
    public void execute() throws Exception
    {
        float step = 0;
        if (data.getDuration() > 0 && data.isVisual())
        {
            step = (float) PROGRESS_MAX / data.getDuration();
            infoDialog.open();
        }
        float progress = 0;

        for (int i = 0; i < data.getDuration() && !cancelExecution; i++)
        {
            Thread.sleep(SLEEP_SECOND);

            if (data.isVisual())
            {
                progress += step;
                final int progressValue = Math.round(progress);
                synchronizer.asyncInvoke(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        progressHandler.setValue(progressValue);
                    }
                });
            }
        }
    }

    /**
     * Performs UI-related updates after the execution of the command. This
     * implementation closes the information window. Note that this method is
     * only called if visual feedback is enabled.
     */
    @Override
    protected void performGUIUpdate()
    {
        infoDialog.close(true);
    }
}

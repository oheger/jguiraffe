/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

/**
 * <p>
 * A specialized action task for opening a file or directory.
 * </p>
 * <p>
 * This task is executed when the user triggers the open action. Its behavior
 * depends on the current selection in the table: If a file is selected, it is
 * opened using the {@code Desktop} class. If a directory is selected, the
 * controller is asked to navigate to this directory.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: OpenDesktopTask.java 205 2012-01-29 18:29:57Z oheger $
 */
public class OpenDesktopTask extends AbstractDesktopTask
{
    /**
     * Creates a new instance of {@code OpenDesktopTask} and initializes it with
     * the main controller.
     *
     * @param ctrl the main controller
     */
    public OpenDesktopTask(MainWndController ctrl)
    {
        super(ctrl);
    }

    /**
     * Executes this task. Here we check for the type of the selected element.
     * Directories need to be handled in a special way.
     */
    @Override
    public void run()
    {
        File f = getSelectedFile();
        if (f.isDirectory())
        {
            getController().selectSubDirectory(f);
        }
        else
        {
            super.run();
        }
    }

    /**
     * Performs the desktop operation. If this method is called, it is clear
     * that a file is selected. The corresponding method on the {@code Desktop}
     * instance is called.
     *
     * @param desktop the {@code Desktop} instance
     * @throws Exception if an error occurs
     */
    @Override
    protected void performDesktopOperation(Desktop desktop) throws Exception
    {
        desktop.open(getSelectedFile());
    }
}

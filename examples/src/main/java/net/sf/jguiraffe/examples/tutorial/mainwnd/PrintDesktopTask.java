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
package net.sf.jguiraffe.examples.tutorial.mainwnd;

import java.awt.Desktop;

/**
 * <p>
 * A specialized action task for printing a file.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PrintDesktopTask.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PrintDesktopTask extends AbstractDesktopTask
{
    /**
     * Creates a new instance of {@code PrintDesktopTask} and initializes the
     * controller.
     *
     * @param ctrl the main controller
     */
    public PrintDesktopTask(MainWndController ctrl)
    {
        super(ctrl);
    }

    /**
     * Performs the desktop operation. This implementation prints the selected
     * file.
     *
     * @param desktop the {@code Desktop} object
     * @throws Exception if an error occurs
     */
    @Override
    protected void performDesktopOperation(Desktop desktop) throws Exception
    {
        desktop.print(getSelectedFile());
    }
}

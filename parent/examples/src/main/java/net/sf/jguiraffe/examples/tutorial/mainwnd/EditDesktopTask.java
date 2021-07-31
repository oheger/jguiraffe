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
package net.sf.jguiraffe.examples.tutorial.mainwnd;

import java.awt.Desktop;

/**
 * <p>
 * A specialized action task for editing a selected file.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: EditDesktopTask.java 205 2012-01-29 18:29:57Z oheger $
 */
public class EditDesktopTask extends AbstractDesktopTask
{
    /**
     * Creates a new instance of {@code EditDesktopTask} and sets the main
     * controller.
     *
     * @param ctrl the main controller
     */
    public EditDesktopTask(MainWndController ctrl)
    {
        super(ctrl);
    }

    /**
     * Performs the desktop operation. This implementation asks the
     * {@code Desktop} object to edit the selected file.
     */
    @Override
    protected void performDesktopOperation(Desktop desktop) throws Exception
    {
        desktop.edit(getSelectedFile());
    }
}

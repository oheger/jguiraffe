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

import java.io.File;

import net.sf.jguiraffe.gui.builder.components.model.ListComponentHandler;
import net.sf.jguiraffe.gui.builder.event.FormChangeEvent;
import net.sf.jguiraffe.gui.builder.event.FormChangeListener;

/**
 * <p>
 * An event listener for change events fired by the combobox with the file
 * systems.
 * </p>
 * <p>
 * Whenever the selection of the combobox changes the main window controller is
 * notified.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FileSystemChangeListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FileSystemChangeListener implements FormChangeListener
{
    /** The controller of the main window. */
    private final MainWndController controller;

    /** The combobox. */
    private final ListComponentHandler handler;

    /**
     * Creates a new instance of {@code FileSystemChangeListener} and
     * initializes it.
     *
     * @param ctrl the controller
     * @param comboHandler the handler for the combobox
     */
    public FileSystemChangeListener(MainWndController ctrl,
            ListComponentHandler comboHandler)
    {
        controller = ctrl;
        handler = comboHandler;

        // initially select the first file system
        if (handler.getListModel().size() > 0)
        {
            controller.fileSystemChanged((File) handler.getListModel()
                    .getValueObject(0));
        }
    }

    /**
     * The selection of the combobox has changed.
     */
    @Override
    public void elementChanged(FormChangeEvent e)
    {
        controller.fileSystemChanged((File) e.getHandler().getData());
    }
}

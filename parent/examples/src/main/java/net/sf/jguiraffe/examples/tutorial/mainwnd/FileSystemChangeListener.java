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

        // initially select the first file system
        if (comboHandler.getListModel().size() > 0)
        {
            Object data = comboHandler.getListModel().getValueObject(0);
            comboHandler.setData(data);
            notifyControllerAboutChange(data);
        }
    }

    /**
     * The selection of the combo box has changed.
     */
    @Override
    public void elementChanged(FormChangeEvent e)
    {
        notifyControllerAboutChange(e.getHandler().getData());
    }

    /**
     * Notifies the main controller about a change in the selection of the file
     * system combo box.
     *
     * @param selection the currently selected object (a File)
     */
    private void notifyControllerAboutChange(Object selection)
    {
        controller.fileSystemChanged((File) selection);
    }
}

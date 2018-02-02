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

import net.sf.jguiraffe.gui.builder.components.model.TreeHandler;
import net.sf.jguiraffe.gui.builder.event.FormChangeEvent;
import net.sf.jguiraffe.gui.builder.event.FormChangeListener;

/**
 * <p>
 * An event listener for change events fired by the tree view.
 * </p>
 * <p>
 * Whenever the selection of the tree view changes the controller has to be
 * notified to update the content of the table with the files and sub
 * directories of the selected directory.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TreeChangeListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TreeChangeListener implements FormChangeListener
{
    /** The controller of the main window. */
    private final MainWndController controller;

    /**
     * Creates a new instance of {@code TreeChangeListener} and initializes it
     * with its dependencies.
     *
     * @param ctrl the main controller
     */
    public TreeChangeListener(MainWndController ctrl)
    {
        controller = ctrl;
    }

    /**
     * The selection of the tree view has changed. Notify the controller.
     *
     * @param e the change event
     */
    @Override
    public void elementChanged(FormChangeEvent e)
    {
        controller.treeSelectionChanged(((TreeHandler) e.getHandler())
                .getSelectedPath());
    }
}

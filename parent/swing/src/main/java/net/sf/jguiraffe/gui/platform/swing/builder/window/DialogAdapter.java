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
package net.sf.jguiraffe.gui.platform.swing.builder.window;

import javax.swing.JDialog;
import javax.swing.JRootPane;

/**
 * <p>
 * A window adapter implementation for Swing dialogs.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DialogAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
class DialogAdapter extends WindowAdapter
{
    /**
     * Creates a new instance of <code>DialogAdapter</code> and initializes it
     * with the given dialog.
     *
     * @param dialog the dialog to wrap
     * @param center a flag whether the dialog should be centered
     */
    public DialogAdapter(JDialog dialog, boolean center)
    {
        super(dialog, center);
        dialog.addWindowListener(new WindowListenerAdapter(getWindowHelper()));
    }

    /**
     * Returns the dialog object that is wrapped by this adapter.
     *
     * @return the wrapped dialog
     */
    public JDialog getDialog()
    {
        return (JDialog) getWindow();
    }

    /**
     * Returns the dialog's title.
     *
     * @return the title
     */
    public String getTitle()
    {
        return getDialog().getTitle();
    }

    /**
     * Sets the dialog's title.
     *
     * @param s the new title
     */
    public void setTitle(String s)
    {
        getDialog().setTitle(s);
    }

    /**
     * Returns the dialog's root container.
     *
     * @return the root container
     */
    public Object getRootContainer()
    {
        return getDialog().getContentPane();
    }

    /**
     * Returns the root pane. This method delegates to the wrapped dialog.
     *
     * @return the root pane
     */
    public JRootPane getRootPane()
    {
        return getDialog().getRootPane();
    }

    /**
     * Disposes this window. This implementation delegates to the wrapped
     * dialog.
     */
    public void dispose()
    {
        getDialog().dispose();
    }
}

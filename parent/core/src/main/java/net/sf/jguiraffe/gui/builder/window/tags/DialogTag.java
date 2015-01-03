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
package net.sf.jguiraffe.gui.builder.window.tags;

import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderException;
import net.sf.jguiraffe.gui.builder.window.WindowManager;

/**
 * <p>
 * A tag handler class for creating dialogs.
 * </p>
 * <p>
 * With this tag class modal and non modal dialogs can be created, i.e. typical
 * windows that gather user input. In addition to the attributes defined by the
 * base class, this class support the boolean <code>modal</code> attribute,
 * which specifies whether the resulting dialog window is modal or not. The
 * default value for this attribute is <b>true</b>.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DialogTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DialogTag extends WindowBaseTag
{
    /** Stores the modal attribute. */
    private boolean modal = true;

    /**
     * Creates a new instance of {@code DialogTag}.
     */
    public DialogTag()
    {
        setCloseOnEsc(true);
    }

    /**
     * Returns the modal flag.
     *
     * @return the modal flag
     */
    public boolean isModal()
    {
        return modal;
    }

    /**
     * Setter method for the modal attribute.
     *
     * @param modal the attribute value
     */
    public void setModal(boolean modal)
    {
        this.modal = modal;
    }

    /**
     * Creates the dialog window using the passed in window manager.
     *
     * @param manager the window manager
     * @param data the window builder data
     * @return the new window
     * @throws WindowBuilderException if an error occurs
     */
    @Override
    protected Window createWindow(WindowManager manager, WindowBuilderData data)
            throws WindowBuilderException
    {
        return manager.createDialog(data, this, isModal(), null);
    }

    /**
     * Initializes the dialog window.
     *
     * @param manager the window manager
     * @param data the window builder data
     * @param wnd the window to initialize
     * @return the initialized window
     * @throws WindowBuilderException if an error occurs
     */
    @Override
    protected Window initWindow(WindowManager manager, WindowBuilderData data,
            Window wnd) throws WindowBuilderException
    {
        return manager.createDialog(data, this, isModal(), wnd);
    }
}

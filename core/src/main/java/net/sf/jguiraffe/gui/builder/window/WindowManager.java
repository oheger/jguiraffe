/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.window;

/**
 * <p>
 * Definition of an interface for platform (or GUI library) specific window
 * manager implementations.
 * </p>
 * <p>
 * Analogous to other builder components the window builder uses an interface
 * for executing platform specific operations or creating specific objects (in
 * this case windows). Each supported platform (like Swing or SWT) must provide
 * an implementation of this interface hiding the specifics of this platform.
 * </p>
 * <p>
 * The methods defined by this interface in the first line deal with the
 * creation of several window types. Creating a window is a two step process:
 * First the object representing the window is created. In the second step it is
 * initialized. Both steps are performed by the same methods that can check
 * their arguments to determine whether they are in the creation or the
 * initialization phase.
 * </p>
 * <p>
 * <strong>Note:</strong> This interface is not intended to be directly
 * implemented by client code. It is subject to change even in minor releases as
 * new features are made available. Therefore if an application needs to provide
 * a custom implementation of this interface, it should extend an existing
 * implementation. For instance, the {@link WindowManagerWrapper} class is a
 * good candidate if only a subset of methods is to be modified.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface WindowManager
{
    /**
     * Creates a frame window (a main frame).
     *
     * @param builderData the builder data object
     * @param data the data defining the window to create
     * @param wnd the window object; if <b>null</b>, a new window is to be
     * created; otherwise this window object must be initialized
     * @return the new window
     * @throws WindowBuilderException if an error occurs
     */
    Window createFrame(WindowBuilderData builderData, WindowData data,
            Window wnd) throws WindowBuilderException;

    /**
     * Creates an internal frame window.
     *
     * @param builderData the builder data object
     * @param data the data defining the window to create
     * @param wnd the window object; if <b>null</b>, a new window is to be
     * created; otherwise this window object must be initialized
     * @return the new window
     * @throws WindowBuilderException if an error occurs
     */
    Window createInternalFrame(WindowBuilderData builderData, WindowData data,
            Window wnd) throws WindowBuilderException;

    /**
     * Creates a modal or non modal dialog.
     *
     * @param builderData the builder data object
     * @param data the data defining the window to create
     * @param modal the modal flag
     * @param wnd the window object; if <b>null</b>, a new window is to be
     * created; otherwise this window object must be initialized
     * @return the new window
     * @throws WindowBuilderException if an error occurs
     */
    Window createDialog(WindowBuilderData builderData, WindowData data,
            boolean modal, Window wnd) throws WindowBuilderException;
}

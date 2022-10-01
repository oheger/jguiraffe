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
package net.sf.jguiraffe.gui.builder.window;

/**
 * <p>
 * A base class for wrapper implementations of the {@link WindowManager}
 * interface.
 * </p>
 * <p>
 * An instance of this class is initialized with a reference to a
 * {@link WindowManager} object. All methods simply delegate to this object.
 * </p>
 * <p>
 * This class is especially useful if a custom implementation of
 * {@link WindowManager} is to be created based on an existing implementation.
 * Then only the methods to be customized have to be implemented. All other
 * methods can still delegate to the existing implementation.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowManagerWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class WindowManagerWrapper implements WindowManager
{
    /** The wrapped window manager. */
    private final WindowManager wrappedManager;

    /**
     * Creates a new instance of {@code WindowManagerWrapper} and initializes it
     * with the wrapped {@code WindowManager}. Note: This method does not check
     * whether the passed in {@code WindowManager} object is <b>null</b> because
     * we do not want to enforce this restriction on all subclasses. The passed
     * in wrapped window manager is stored in an internal field and can be
     * accessed using the {@link #getWrappedWindowManager()} method. If a
     * subclass needs another mechanism to access the wrapped manager, it has to
     * override the {@link #getWrappedWindowManager()} method.
     *
     * @param wrapped the wrapped {@code WindowManager}
     */
    protected WindowManagerWrapper(WindowManager wrapped)
    {
        wrappedManager = wrapped;
    }

    /**
     * Returns a reference to the wrapped {@code WindowManager}.
     *
     * @return the wrapped {@code WindowManager}
     */
    public WindowManager getWrappedWindowManager()
    {
        return wrappedManager;
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code WindowManager} object.
     */
    public Window createFrame(WindowBuilderData builderData, WindowData data,
            Window wnd) throws WindowBuilderException
    {
        return getWrappedWindowManager().createFrame(builderData, data, wnd);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code WindowManager} object.
     */
    public Window createInternalFrame(WindowBuilderData builderData,
            WindowData data, Window wnd) throws WindowBuilderException
    {
        return getWrappedWindowManager().createInternalFrame(builderData, data,
                wnd);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code WindowManager} object.
     */
    public Window createDialog(WindowBuilderData builderData, WindowData data,
            boolean modal, Window wnd) throws WindowBuilderException
    {
        return getWrappedWindowManager().createDialog(builderData, data, modal,
                wnd);
    }
}

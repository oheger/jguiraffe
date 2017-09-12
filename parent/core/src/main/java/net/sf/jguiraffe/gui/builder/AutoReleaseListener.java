/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder;

import net.sf.jguiraffe.gui.builder.window.WindowEvent;
import net.sf.jguiraffe.gui.builder.window.WindowListener;

/**
 * <p>
 * A specialized {@code WindowListener} implementation for implementing the
 * <em>auto release</em> mechanism supported by builders.
 * </p>
 * <p>
 * The results produced by a {@link Builder} usually need to be released when
 * they are no longer needed to free the resources used by them. This can be
 * done manually by invoking the {@link Builder#release(BuilderData)} method
 * with the {@link BuilderData} object used for specifying the parameters of the
 * builder operation.
 * </p>
 * <p>
 * When a window is generated by a builder script, there is an alternative: it
 * is possible to let the framework perform the {@code release()} invocation
 * automatically when the window is closed. This is often useful, for instance
 * when a dialog window is created from a builder script: when the user closes
 * the dialog all resources associated with it (including beans defined in the
 * script) are no more needed and can be released. To enable this mechanism the
 * {@code autoRelease} property of {@link BuilderData} has to be set to
 * <b>true</b>.
 * </p>
 * <p>
 * This class implements the functionality for automatically releasing a
 * {@link BuilderData} object when a window (i.e. the main window produced by
 * the builder script) is closed. It does not rely on a concrete {@link Builder}
 * implementation and thus can be used with arbitrary implementations. Concrete
 * implementations of the {@link Builder} interface only need to create an
 * instance and register it at the main window of the builder results.
 * </p>
 * <p>
 * Client code typically will not have to use this class directly. The framework
 * will create and initialize and instance behind the scenes when a
 * {@link BuilderData} object configured appropriately is passed to a
 * {@link Builder}.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AutoReleaseListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public class AutoReleaseListener implements WindowListener
{
    /** Stores a reference to the builder data object to be released. */
    private final BuilderData builderData;

    /** Stores the builder that must be called for releasing. */
    private final Builder builder;

    /**
     * Creates a new instance of {@code AutoReleaseListener} and sets the
     * {@code BuilderData} object to be released automatically. This object must
     * be fully initialized.
     *
     * @param data the {@code BuilderData} object (must not be <b>null</b>)
     * @throws IllegalArgumentException if the data object is <b>null</b> or
     *         properties required are not set
     */
    public AutoReleaseListener(BuilderData data)
    {
        if (data == null)
        {
            throw new IllegalArgumentException("BuilderData must not be null!");
        }
        builderData = data;
        builder = builderData.getBuilder();
        if (builder == null)
        {
            throw new IllegalArgumentException("Builder must not be null!");
        }
    }

    /**
     * Window activated event. This is just a dummy implementation.
     *
     * @param event the event
     */
    public void windowActivated(WindowEvent event)
    {
    }

    /**
     * The window was closed. This will cause the {@code BuilderData} object to
     * be released.
     *
     * @param event the event
     */
    public void windowClosed(WindowEvent event)
    {
        builder.release(builderData);
    }

    /**
     * Window deactivated event. This is just a dummy implementation.
     *
     * @param event the event
     */
    public void windowDeactivated(WindowEvent event)
    {
    }

    /**
     * Window deiconified event. This is just a dummy implementation.
     *
     * @param event the event
     */
    public void windowDeiconified(WindowEvent event)
    {
    }

    /**
     * Window iconified event. This is just a dummy implementation.
     *
     * @param event the event
     */
    public void windowIconified(WindowEvent event)
    {
    }

    /**
     * Window opened event. This is just a dummy implementation.
     *
     * @param event the event
     */
    public void windowOpened(WindowEvent event)
    {
    }

    /**
     * Window closing event. This is just a dummy implementation.
     *
     * @param event the event
     */
    public void windowClosing(WindowEvent event)
    {
    }
}

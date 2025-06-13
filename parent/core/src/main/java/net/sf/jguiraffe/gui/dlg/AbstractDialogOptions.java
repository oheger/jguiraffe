/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.dlg;

import net.sf.jguiraffe.gui.app.ApplicationContext;
import net.sf.jguiraffe.gui.app.TextResource;

/**
 * <p>
 * An abstract base class for options classes for standard dialogs.
 * </p>
 * <p>
 * This class defines a set of properties that are common for all kinds of
 * dialogs. These are the following:
 * </p>
 * <ul>
 * <li>A {@link DialogResultCallback} to be invoked with the result of the
 * dialog.</li>
 * <li>A title for the dialog (either as string or resource ID).</li>
 * <li>An optional {@link DialogCanceledCallback} to be invoked when the dialog
 * is canceled.</li>
 * </ul>
 * <p>
 * The class offers a fluent interface for setting the single options defining
 * the associated dialog. This can be extended by concrete sub classes. As
 * updates of properties happen in-place, instances are not thread-safe. The use
 * case assumed is that an instance is created, populated, and passed to a
 * dialog service - all by the same thread.
 * </p>
 * <p>
 * When invoking callbacks - either for the dialog result or the cancel
 * notification - it is possible to pass a data object with additional context
 * information. This class manages the data object and provides functionality to
 * invoke a callback without having it at hand. So implementation classes for
 * dialog services do not have to bother.
 * </p>
 *
 * @param <T> the type of the dialog result
 * @param <S> the self type returned by fluent methods
 * @since 1.4
 */
public abstract class AbstractDialogOptions<T, S extends AbstractDialogOptions<T, S>>
{
    /**
     * Constant for a cancel invoker that is returned if no canceled callback
     * has been set. This is a runnable with an empty run() method.
     */
    private static final Runnable DUMMY_CANCEL_INVOKER = new Runnable()
    {
        public void run()
        {
        }
    };

    /**
     * A callback to propagate the dialog result. This is not the original
     * callback, but a wrapper that passes the correct data object to the
     * callback.
     */
    private final DialogResultCallback<T, Void> resultPropagationCallback;

    /** A runnable to invoke a canceled callback. */
    private Runnable cancelInvoker = DUMMY_CANCEL_INVOKER;

    /** The title for the dialog as string. */
    private TextResource titleResource = TextResource.UNDEFINED;

    /**
     * Creates a new instance of {@code AbstractDialogOptions} and initializes
     * it with the result callback and a data object to be passed to the
     * callback when it is invoked.
     *
     * @param resultCallback the result callback
     * @param data the context data for the result callback
     * @param <D> the type of the data object for the result callback
     * @throws IllegalArgumentException if the result callback is
     *         <strong>null</strong>
     */
    protected <D> AbstractDialogOptions(
            final DialogResultCallback<T, D> resultCallback, final D data)
    {
        if (resultCallback == null)
        {
            throw new IllegalArgumentException(
                    "Result callback must not be null!");
        }

        resultPropagationCallback =
                createPropagationResultCallback(resultCallback, data);
    }

    /**
     * Creates a new instance of {@code AbstractDialogOptions} and initializes
     * it with the result callback. No data object will be passed to this
     * callback when it is invoked. (It is assumed that the callback can handle
     * a <strong>null</strong> data object.)
     *
     * @param resultCallback the result callback
     * @throws IllegalArgumentException if the result callback is
     *         <strong>null</strong>
     */
    protected AbstractDialogOptions(DialogResultCallback<T, ?> resultCallback)
    {
        this(resultCallback, null);
    }

    /**
     * Returns the callback to propagate the dialog result. Note that the
     * callback returned by this method is not the same as passed to the
     * constructor. It is a callback that wraps the original callback and takes
     * care that the correct data object is passed. Callers just have to provide
     * the result and pass <strong>null</strong> for the data object; the
     * implementation calls the underlying callback with the correct data
     * object.
     *
     * @return a callback for propagating the dialog result
     */
    public DialogResultCallback<T, Void> getResultCallback()
    {
        return resultPropagationCallback;
    }

    /**
     * Sets a callback to be invoked when the dialog is canceled with a context
     * data object. This object is passed to the callback when it is invoked.
     *
     * @param canceledCallback the canceled callback
     * @param data a data object to be passed to the callback
     * @param <D> the type of the data object
     * @return this object
     */
    public <D> S setCanceledCallback(
            final DialogCanceledCallback<D> canceledCallback, final D data)
    {
        cancelInvoker = (canceledCallback != null) ? new Runnable()
        {
            public void run()
            {
                canceledCallback.onDialogCanceled(data);
            }
        } : DUMMY_CANCEL_INVOKER;
        return getSelf();
    }

    /**
     * Sets a callback to be invoked when the dialog is canceled. The callback
     * does not expect a data object; it will be therefore invoked with a
     * <strong>null</strong> reference.
     *
     * @param canceledCallback the canceled callback
     * @return this object
     */
    public S setCanceledCallback(DialogCanceledCallback<?> canceledCallback)
    {
        return setCanceledCallback(canceledCallback, null);
    }

    /**
     * Returns a {@code Runnable} that invokes the canceled callback assigned to
     * this options instance. It is ensured that the correct data object is
     * passed to the callback. If no canceled callback has been set, a
     * {@code Runnable} is returned whose invocation does not have any effect.
     *
     * @return a {@code Runnable} to invoke the canceled callback
     */
    public Runnable getCancelInvoker()
    {
        return cancelInvoker;
    }

    /**
     * Allows setting a title for the dialog as string.
     *
     * @param title the dialog title
     * @return this object
     */
    public S setTitle(String title)
    {
        this.titleResource = TextResource.fromText(title);
        return getSelf();
    }

    /**
     * Returns the title that has been set for the dialog. This method returns
     * the string that has been set using {@link #setTitle(String)} or
     * <strong>null</strong> if no title has been set.
     *
     * @return the title string or <strong>null</strong>
     */
    public String getTitle()
    {
        return titleResource.getPlainText();
    }

    /**
     * Allows setting the title for the dialog as a resource ID. The passed in
     * object is resolved using
     * {@link ApplicationContext#getResourceText(Object)}.
     *
     * @param resource the title resource
     * @return this object
     */
    public S setTitleResource(Object resource)
    {
        titleResource = TextResource.fromResourceID(resource);
        return getSelf();
    }

    /**
     * Returns the title resource that has been set for the dialog. This method
     * returns the object that has been set using
     * {@link #setTitleResource(Object)} or <strong>null</strong> if not title
     * resource has been set.
     *
     * @return the title resource or <strong>null</strong>
     */
    public Object getTitleResource()
    {
        return titleResource.getResourceID();
    }

    /**
     * Resolves the title for the dialog using the given application context. If
     * the title has been set directly using the {@link #setTitle(String)}
     * method, it is returned directly. If it has been set as a resource, the
     * resource is resolved, and the result is returned. If no title has been
     * specified, result is <strong>null</strong>.
     *
     * @param applicationContext the application context
     * @return the resolved title (can be <strong>null</strong>)
     */
    public String resolveTitle(ApplicationContext applicationContext)
    {
        return titleResource.resolveText(applicationContext);
    }

    /**
     * Returns a self reference to this object. This is used to support method
     * chaining when setting multiple properties.
     *
     * @return a self reference
     */
    protected abstract S getSelf();

    /**
     * Creates the callback to propagate the dialog result. This callback wraps
     * the original callback passed to the constructor and makes sure that the
     * correct data object is forwarded.
     *
     * @param resultCallback the original result callback
     * @param data the data object for the callback
     * @param <D> the type of the data object
     * @return the propagation callback
     */
    private <D> DialogResultCallback<T, Void> createPropagationResultCallback(
            final DialogResultCallback<T, D> resultCallback, final D data)
    {
        return new DialogResultCallback<T, Void>()
        {
            public void onDialogResult(T result, Void ignoredData)
            {
                resultCallback.onDialogResult(result, data);
            }
        };
    }
}

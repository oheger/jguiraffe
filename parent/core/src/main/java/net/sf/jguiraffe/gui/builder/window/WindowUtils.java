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
package net.sf.jguiraffe.gui.builder.window;


/**
 * <p>
 * A static utility class providing useful functionality for dealing with
 * {@link Window} objects.
 * </p>
 * <p>
 * The methods defined in this utility class can be used to obtain information
 * about window objects. They support casts to the {@code Window}
 * interface and allow for accessing the underlying GUI library specific window
 * implementation.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowUtils.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class WindowUtils
{
    /**
     * Private constructor preventing instances from being created.
     */
    private WindowUtils()
    {
        // cannot be instantiated
    }

    /**
     * Tries to cast the specified object into a {@code Window}. If this is
     * possible, the resulting {@code Window} is returned. Otherwise the return
     * value is <b>null</b>.
     *
     * @param wnd the object to cast to a {@code Window}
     * @return the {@code Window} object or <b>null</b>
     */
    public static Window toWindow(Object wnd)
    {
        return (wnd instanceof Window) ? (Window) wnd : null;
    }

    /**
     * Tries to cast the specified object into a {@code Window}.
     * Different to <code>toWindow()</code>, this method will never return
     * <b>null</b>. Instead, if the passed in object cannot be cast to a
     * {@code Window}, an <code>IllegalArgumentException</code>
     * exception will be thrown.
     *
     * @param wnd the object to cast to a {@code Window}
     * @return the casted object
     * @throws IllegalArgumentException if casting fails
     */
    public static Window toWindowEx(Object wnd) throws IllegalArgumentException
    {
        Window result = toWindow(wnd);
        if (result == null)
        {
            throw new IllegalArgumentException(
                    "toWindowEx: Object is not a Window!");
        }
        return result;
    }

    /**
     * Tries to cast the source window from the specified event object into a
     * {@code Window} object. If this is possible, the {@code Window} object is
     * returned. Otherwise the return value is <b>null</b>. This method is
     * useful when dealing with
     * {@link WindowEvent} objects because it handles <b>null</b> input
     * gracefully.
     *
     * @param event the event object; if <b>null</b>, the return value will be
     * <b>null</b>, too
     * @return the extracted {@code Window} or <b>null</b>
     */
    public static Window windowFromEvent(WindowEvent event)
    {
        return (event == null) ? null : toWindow(event.getSourceWindow());
    }

    /**
     * Tries to cast the source window from the specified event object into a
     * {@code Window} object. Works like <code>windowFromEvent()</code>,
     * but throws an <code>IllegalArgumentException</code> exception if the
     * source window cannot be determined.
     *
     * @param event the event object
     * @return the casted window
     * @throws IllegalArgumentException if the source window cannot be obtained
     */
    public static Window windowFromEventEx(WindowEvent event)
            throws IllegalArgumentException
    {
        Window result = windowFromEvent(event);
        if (result == null)
        {
            throw new IllegalArgumentException(
                    "windowFromEventEx: Could not obtain source window!");
        }
        return result;
    }

    /**
     * Returns the platform (or GUI library) specific window that is represented
     * by the passed in {@code Window} object. This method checks if the
     * passed in object implements the <code>WindowWrapper</code> interface.
     * If this is the case, the wrapped window will be fetched and checked
     * again. Otherwise the window itself will be returned.
     *
     * @param window the window (must not be <b>null</b>)
     * @return the underlying platform specific window
     * @throws IllegalArgumentException if the parameter is <b>null</b>
     */
    public static Object getPlatformWindow(Window window)
            throws IllegalArgumentException
    {
        return resolveWindowWrapper(window);
    }

    /**
     * Helper method for retrieving the platform specific window. This method
     * performs the check with the window wrapper and returns the first object
     * that is no instance of <code>WindowWrapper</code>.
     *
     * @param obj the object to check
     * @return the underlying non wrapped window object
     * @throws IllegalArgumentException if a <b>null</b> argument is passed
     */
    private static Object resolveWindowWrapper(Object obj)
            throws IllegalArgumentException
    {
        if (obj == null)
        {
            throw new IllegalArgumentException(
                    "Cannot determine platform window: null!");
        }

        if (obj instanceof WindowWrapper)
        {
            return resolveWindowWrapper(((WindowWrapper) obj)
                    .getWrappedWindow());
        }
        else
        {
            return obj;
        }
    }
}

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
 * Definition of an interface for objects that wrap &quot;real&quot; windows.
 * </p>
 * <p>
 * This interface is used to access an underlying GUI library specific window
 * object, e.g. a <code>JFrame</code> in Swing. Windows created by the window
 * builder library are objects implementing the <code>{@link Window}</code>
 * interface. It is up to a concrete implementation of a window manager whether
 * the returned objects are directly derived from a window class of the
 * represented GUI library or if they are merely wrapper objects for those real
 * windows. In the first case, to access the underlying window object, the
 * <code>Window</code> instance can simply be casted to the base class. In the
 * latter case, the used wrapper should implement this interface to return the
 * underlying window.
 * </p>
 * <p>
 * Those window wrapper objects can be nested at an arbitrary depth. This also
 * supports different use cases, e.g. window interceptors.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface WindowWrapper
{
    /**
     * Returns the underlying window object that is wrapped by this object. The
     * returned object is either a real window implementation specific to a
     * concrete GUI library or another <code>WindowWrapper</code>
     * implementation.
     *
     * @return the wrapped window object
     */
    Object getWrappedWindow();
}

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
package net.sf.jguiraffe.gui.builder.utils;

/**
 * <p>
 * Definition of an interface that supports updating GUI components from
 * different threads.
 * </p>
 * <p>
 * Typical Java GUI libraries use a specific thread (e.g. event dispatch thread)
 * that is alone responsible for updating GUI components. When working with
 * multiple threads special care has to be taken if GUI components are to be
 * accessed; it has to be ensured that such operations are only performed on the
 * event dispatch thread.
 * </p>
 * <p>
 * This interface provides a generic way for such updates. It defines typical
 * operations that deal with an event dispatch thread: to invoke a code block
 * synchronously or asynchronously on this thread. Concrete, platform specific
 * implementations have to use the appropriate means provided by the platform
 * they encapsulate to ensure that this code gets executed on the event dispatch
 * thread.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: GUISynchronizer.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface GUISynchronizer
{
    /**
     * Invokes the given runnable object asynchronously on the event dispatch
     * thread. This method will no block, the code of the runnable will be
     * executed some time in the future.
     *
     * @param runnable the code to be executed on the event dispatch thread
     */
    void asyncInvoke(Runnable runnable);

    /**
     * Invokes the given runnable object synchronously on the event dispatch
     * thread. The calling thread will block until the runnable's execution is
     * finished.
     *
     * @param runnable the code to be executed on the event dispatch thread
     * @throws GUIRuntimeException if an error occurs
     */
    void syncInvoke(Runnable runnable) throws GUIRuntimeException;

    /**
     * Returns a flag whether the current thread is the event dispatch thread.
     *
     * @return a flag if the current thread is the event dispatch thread
     */
    boolean isEventDispatchThread();
}

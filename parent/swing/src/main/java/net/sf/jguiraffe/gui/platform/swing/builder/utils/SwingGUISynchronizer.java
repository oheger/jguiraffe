/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.swing.builder.utils;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import net.sf.jguiraffe.gui.builder.utils.GUIRuntimeException;
import net.sf.jguiraffe.gui.builder.utils.GUISynchronizer;

/**
 * <p>
 * The Swing specific implementation of the <code>GUISynchronizer</code>
 * interface.
 * </p>
 * <p>
 * This implementation makes uses of <code>java.awt.EventQueue</code> to
 * properly deal with the event dispatch thread.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingGUISynchronizer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingGUISynchronizer implements GUISynchronizer
{
    /**
     * Invokes the given runnable asynchronously on the event dispatch thread.
     * This is done using the <code>java.awt.EventQueue</code> class.
     *
     * @param runnable the runnable to be executed
     */
    public void asyncInvoke(Runnable runnable)
    {
        EventQueue.invokeLater(runnable);
    }

    /**
     * Invokes the given runnable synchronously on the event dispatch thread.
     * This is done using the <code>java.awt.EventQueue</code> class. It will
     * cause no harm if this method is invoked from the event dispatch thread;
     * then the runnable will be directly called.
     *
     * @param runnable the runnable to be executed
     */
    public void syncInvoke(Runnable runnable)
    {
        if (isEventDispatchThread())
        {
            runnable.run();
        }
        else
        {
            try
            {
                EventQueue.invokeAndWait(runnable);
            }
            catch (InterruptedException iex)
            {
                throw new GUIRuntimeException("Thread was interrupted!", iex);
            }
            catch (InvocationTargetException itex)
            {
                throw new GUIRuntimeException("Runnable threw exception", itex);
            }
        }
    }

    /**
     * Tests if the current thread is the event dispatch thread.
     *
     * @return a flag if this method is called on the event dispatch thread
     */
    public boolean isEventDispatchThread()
    {
        return EventQueue.isDispatchThread();
    }
}

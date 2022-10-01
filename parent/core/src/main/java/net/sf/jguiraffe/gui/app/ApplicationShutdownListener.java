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
package net.sf.jguiraffe.gui.app;

import java.util.EventListener;

/**
 * <p>
 * Definition of an interface for listeners that want to be notified when the
 * application terminates.
 * </p>
 * <p>
 * Objects implementing this interface can register itself at the global
 * {@link Application} object. They will then receive a notification before this
 * application terminates. So it is possible to perform some clean up before the
 * final shutdown.
 * </p>
 * <p>
 * A shutdown listeners has also the opportunity of canceling the shutdown
 * process. An example would be an application that shows a message box asking
 * whether the user really wants to exit. If the user here enters
 * &quot;No&quot;, the shutdown must be aborted.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ApplicationShutdownListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ApplicationShutdownListener extends EventListener
{
    /**
     * Checks if a shutdown is possible. This method is called by the
     * application in the beginning of the shutdown phase for all registered
     * listeners. Only if all listeners return <b>true</b>, shutdown process
     * will be continued.
     *
     * @param app the associated application
     * @return a flag if this listeners allows a shutdown
     */
    boolean canShutdown(Application app);

    /**
     * Notifies this listener that the associated application terminates now.
     * This method is called for all registered listeners after a call to
     * <code>canShutdown()</code> was successful, i.e. none of the listeners
     * canceled the process. An implementation could e.g. ensure that all
     * changes have been saved or perform clean up.
     *
     * @param app the associated application
     */
    void shutdown(Application app);
}

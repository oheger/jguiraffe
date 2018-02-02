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
package net.sf.jguiraffe.gui.platform.swing.builder.event;

import java.util.EventListener;

/**
 * <p>
 * Definition of an interface for receiving notifactions about changes at Swing
 * components.
 * </p>
 * <p>
 * The purpose of this interface is to combine different event listener
 * interfaces existing in Swing dealing with component changes. The form builder
 * library treates all these events the same way so they can be all processed
 * through this interface.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ChangeListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ChangeListener extends EventListener
{
    /**
     * Call back method for component changes. Analogous to the
     * <code>{@link net.sf.jguiraffe.gui.builder.event.FormChangeListener FormChangeListener}</code>
     * class there is only a single method in this interface that is invoked for
     * all type of component changes.
     *
     * @param event the original event object
     */
    void componentChanged(Object event);
}

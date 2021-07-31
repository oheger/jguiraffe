/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.model;

import net.sf.jguiraffe.gui.builder.event.FormEventListener;

/**
 * <p>
 * An event listener interface to be implemented by objects that need to be
 * notified before the expansion state of a tree's node changes.
 * </p>
 * <p>
 * This interface is very similar in usage and purpose as the
 * <code>{@link TreeExpansionListener}</code> interface. There are two main
 * differences:
 * <ul>
 * <li>Event listeners are notified before the affected node is actually
 * expanded or collapsed. This makes it possible for instance to implement a
 * dynamic loading mechanism: The first time a node is expanded the listener can
 * check whether further data is available. It can then add the data found to
 * the tree.</li>
 * <li>Event listeners have the opportunity to veto against the expand or
 * collapse operation. This can be done by throwing an exception in the listener
 * method.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TreePreExpansionListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface TreePreExpansionListener extends FormEventListener
{
    /**
     * A node of a tree is about to change its expansion state. The passed in
     * event contains all information available about the objects involved. The
     * listener can permit this operation or, by throwing an exception, abort
     * it.
     *
     * @param event the event
     * @throws TreeExpandVetoException thrown to indicate that this operation
     *         should be aborted
     */
    void beforeExpansionStateChange(TreeExpansionEvent event)
            throws TreeExpandVetoException;
}

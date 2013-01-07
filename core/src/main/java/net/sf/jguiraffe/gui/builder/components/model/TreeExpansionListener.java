/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
 * An event listener interface to be implemented by objects that are interested
 * in changes of the expansion state of tree nodes.
 * </p>
 * <p>
 * Listeners of this type are notified whenever a node of a tree is expanded or
 * collapsed. The single method defined by this interface tells the listener
 * that the expansion state of a node has changed. The <code>type</code>
 * property of the provided event object can be inspected to find out whether
 * the node was expanded or collapsed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TreeExpansionListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface TreeExpansionListener extends FormEventListener
{
    /**
     * The expansion state of a node has changed. The passed in event object
     * contains all information available. Especially the affected tree control,
     * the type of the event, and the path to the node in question can be
     * queried.
     *
     * @param event the event
     */
    void expansionStateChanged(TreeExpansionEvent event);
}

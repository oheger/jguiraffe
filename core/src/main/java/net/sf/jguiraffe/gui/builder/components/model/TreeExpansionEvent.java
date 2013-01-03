/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.event.FormEvent;

/**
 * <p>
 * An event class reporting a change in the expanded state of a tree's node.
 * </p>
 * <p>
 * Tree components support event listeners that are notified when a node of the
 * tree is expanded or collapsed. These listeners are passed an event object of
 * this type containing all data available.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TreeExpansionEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TreeExpansionEvent extends FormEvent
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1078903161051276615L;

    /** Stores the path to the modified node. */
    private final transient TreeNodePath path;

    /** The type of this event. */
    private final Type type;

    /**
     * Creates a new instance of <code>TreeExpansionEvent</code> and initializes
     * it.
     *
     * @param source the source of this event
     * @param handler the component handler affected (this is a handler for a
     *        tree component)
     * @param name the name of the component
     * @param type the type of this event
     * @param path the path to the node whose state has changed
     */
    public TreeExpansionEvent(Object source, TreeHandler handler, String name,
            Type type, TreeNodePath path)
    {
        super(source, handler, name);
        this.type = type;
        this.path = path;
    }

    /**
     * Returns the path to the node whose state has changed. This is the node
     * that triggered this event.
     *
     * @return the path of the node affected
     */
    public TreeNodePath getPath()
    {
        return path;
    }

    /**
     * Returns the handler for the tree component that is the source of this
     * event.
     *
     * @return the handler for the tree component
     */
    public TreeHandler getTreeHandler()
    {
        return (TreeHandler) getHandler();
    }

    /**
     * Returns the type of this event.
     *
     * @return the event type
     */
    public Type getType()
    {
        return type;
    }

    /**
     * An enumeration class defining the different types of a {@code
     * TreeExpansionEvent}.
     */
    public static enum Type
    {
        /** A node of the tree was collapsed. */
        NODE_COLLAPSE,

        /** A node of the tree was expanded. */
        NODE_EXPAND
    }
}

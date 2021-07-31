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

/**
 * <p>
 * An exception class to be thrown to prevent a tree from expanding or
 * collapsing a tree node.
 * </p>
 * <p>
 * Exceptions of this type are used by
 * <code>{@link TreePreExpansionListener}</code> objects. If such an exception
 * is thrown, the current expansion or collapse operation of the tree will be
 * aborted.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TreeExpandVetoException.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TreeExpandVetoException extends Exception
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 3962311592439197250L;

    /** Stores the event that triggered this exception. */
    private final TreeExpansionEvent event;

    /**
     * Creates a new instance of <code>TreeExpandVetoException</code> and
     * initializes it with the causing event and a message.
     *
     * @param ev the event that triggered this exception
     * @param msg the error message
     */
    public TreeExpandVetoException(TreeExpansionEvent ev, String msg)
    {
        super(msg);
        event = ev;
    }

    /**
     * Returns the event that caused this exception.
     *
     * @return the causing event
     */
    public TreeExpansionEvent getEvent()
    {
        return event;
    }
}

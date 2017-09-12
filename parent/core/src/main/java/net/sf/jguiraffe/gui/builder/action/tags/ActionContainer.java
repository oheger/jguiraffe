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
package net.sf.jguiraffe.gui.builder.action.tags;

import net.sf.jguiraffe.gui.builder.action.FormActionException;

/**
 * <p>
 * Definition of an interface for objects with container facilities.
 * </p>
 * <p>
 * Some of the objects involved in the action builder process act as containers,
 * e.g. menu bars or toolbars. For certain tags it is necessary to find these
 * containers so that new elements can be added to them. This is done through
 * this interface.
 * </p>
 * <p>
 * The interface itself is very simple. It only provides access to a container
 * object whose concrete type depends on the GUI library specific implementation
 * of the
 * <code>{@link net.sf.jguiraffe.gui.builder.action.ActionManager ActionManager}</code>
 * interface. It is passed to the action manager, which can add further elements
 * to it.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionContainer.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ActionContainer
{
    /**
     * Returns the container object. This method will be called when new
     * elements need to be added to this container.
     *
     * @return the container object
     */
    Object getContainer();

    /**
     * Adds a separator element to this container. This method will be invoked
     * when a <code>&lt;separator&gt;</code> tag is executed. An
     * implementation must ensure that the corresponding method of the current
     * <code>{@link net.sf.jguiraffe.gui.builder.action.ActionManager ActionManager}</code>
     * for adding the separator is called.
     * @throws FormActionException if an error occurs
     */
    void addSeparator() throws FormActionException;
}

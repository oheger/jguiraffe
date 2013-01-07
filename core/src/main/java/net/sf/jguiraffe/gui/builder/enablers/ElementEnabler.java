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
package net.sf.jguiraffe.gui.builder.enablers;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

/**
 * <p>
 * Definition of an interface to be implemented by components that can change
 * the enabled state of specific elements.
 * </p>
 * <p>
 * In a UI application it is frequently the case that some elements (e.g. UI
 * components, actions, or windows) become enabled or disabled based on certain
 * criteria defined by the application. For instance, when the user selects a
 * menu item that starts a long running action in a background task, typically
 * this menu item should be disabled so that the user cannot start a second
 * process before the first one terminates. On the other hand, maybe a menu item
 * for canceling the background process becomes active during its life time.
 * </p>
 * <p>
 * With this interface such status changes can be implemented in a generic way.
 * There will be specific implementations for dealing with different UI elements
 * that can be disabled and enabled. The interface itself is pretty lean. It
 * defines only a single generic method for changing the enabled state of an
 * element.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ElementEnabler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ElementEnabler
{
    /**
     * Changes the enabled state of the element this {@code ElementEnabler} is
     * responsible for. This method is always called when the enabled state of
     * elements has to be changed. The passed in {@code ComponentBuilderData}
     * object can be used for accessing relevant elements; especially the
     * {@code BeanContext} maintained by this object can be used for retrieving
     * all objects available in the current builder context. The boolean
     * argument determines whether the associated element should be enabled or
     * disabled. If the state of the element cannot be changed - for whatever
     * reason -, an implementation should throw a {@code FormBuilderException}.
     *
     * @param compData a reference to the current {@code ComponentBuilderData}
     *        object
     * @param state the new enabled state of the associated element: <b>true</b>
     *        if the element is to be enabled, <b>false</b> if it should be
     *        disabled
     * @throws FormBuilderException if the state of the element cannot be
     *         changed
     */
    void setEnabledState(ComponentBuilderData compData, boolean state)
            throws FormBuilderException;
}

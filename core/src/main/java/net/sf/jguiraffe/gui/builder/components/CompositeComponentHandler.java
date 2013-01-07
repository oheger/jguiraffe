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
package net.sf.jguiraffe.gui.builder.components;

import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * Definition of an interface for complex component handlers that can contain
 * other component handlers.
 * </p>
 * <p>
 * The form builder library supports component handlers that can combine other
 * handlers and let them appear as a single component. This can be useful e.g.
 * for radio buttons: Instead of storing the state of each button in the form's
 * data bean, a composite handler can be defined, which encapsulates the buttons
 * in the radio group and transforms their states into a number, describing the
 * index of the selected radio button.
 * </p>
 * <p>
 * There are some tag handler classes in the form builder tag library that
 * support the creation of such composite handler instances (and their
 * association with an input component). These tags make use of this interface
 * to add simple component handlers to the composite one.
 * </p>
 *
 * @author Oliver Heger
 * @param <T> the data type of this {@code ComponentHandler}
 * @param <S> the data type of the child handlers
 * @version $Id: CompositeComponentHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface CompositeComponentHandler<T, S> extends ComponentHandler<T>
{
    /**
     * Adds a simple <code>ComponentHandler</code> object to this composite
     * handler.
     *
     * @param name the name of the corresponding component
     * @param handler the handler to add
     */
    void addHandler(String name, ComponentHandler<S> handler);
}

/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.event;

import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A specific event class for change events.
 * </p>
 * <p>
 * Change events are caused by components that react on changes by the user,
 * e.g. a text field in which text was typed or a combo or list box from which an
 * item was selected. Because a wide variety of source events and components are
 * possible there is no default set of properties to describe the change. But by
 * using the provided component handler it should be possible to access the new
 * value of the affected component.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormChangeEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormChangeEvent extends FormEvent
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -8290185554329676174L;

    /**
     * Creates a new instance of <code>FormChangeEvent</code> and initializes
     * it.
     *
     * @param source the source event
     * @param handler the component handler
     * @param name the component's name
     */
    public FormChangeEvent(Object source, ComponentHandler<?> handler, String name)
    {
        super(source, handler, name);
    }
}

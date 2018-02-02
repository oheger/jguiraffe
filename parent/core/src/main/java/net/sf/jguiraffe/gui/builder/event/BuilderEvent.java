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
package net.sf.jguiraffe.gui.builder.event;

import java.util.EventObject;

/**
 * <p>
 * The base class of events used in the form and form builder framework.
 * </p>
 * <p>
 * The form framework provides an abstraction from specific GUI libraries like
 * Swing or SWT. This abstraction includes the event handling mechanism, too. To
 * achieve this, generic event classes are defined, to which library specific
 * event types have to be converted. This class is the base class for these
 * generic event classes. There are also corresponding event listener
 * interfaces.
 * </p>
 * <p>
 * The common denominator of all event classes in the builder framework is the
 * <code>source</code> property (which is already inherited from the base
 * class <code>EventObject</code>. Here the original, library specific event
 * object should be set allowing application code to access platform specific
 * information if necessary.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BuilderEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class BuilderEvent extends EventObject
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -8841052675925806622L;

    /**
     * Creates a new instance of <code>BuilderEvent</code> and initializes it.
     * The event's source is passed, which should be the the platform specific
     * event object that is wrapped by this generic object instance.
     *
     * @param source the source of this event
     */
    public BuilderEvent(Object source)
    {
        super(source);
    }
}

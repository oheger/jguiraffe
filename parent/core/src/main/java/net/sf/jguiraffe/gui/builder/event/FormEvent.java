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
package net.sf.jguiraffe.gui.builder.event;

import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.lang.ObjectUtils;

/**
 * <p>
 * An event class used in the form and form builder framework to deliver event
 * information related to form elements (controls or input components).
 * </p>
 * <p>
 * All input components in a {@link net.sf.jguiraffe.gui.forms.Form Form} object
 * (i.e. form elements with an associated
 * {@link net.sf.jguiraffe.gui.forms.ComponentHandler ComponentHandler} can act
 * as event sources. Therefore all event objects contain a reference to the
 * component handler of the event source and the name under which the
 * corresponding component was registered at the form. These properties can be
 * used by custom event listener implementations to find out, which component
 * has caused the event and to access the component's current input data.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormEvent extends BuilderEvent
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 9144620760327400752L;

    /**
     * Stores a reference to the component handler of the component that caused
     * this event.
     */
    private final transient ComponentHandler<?> handler;

    /** Stores the name of the component that caused this event. */
    private final String name;

    /**
     * Creates a new instance of <code>FormEvent</code> and initializes it.
     *
     * @param source the event source; this should be the platform specific
     * event object that is wrapped by this generic object instance
     * @param handler the component handler
     * @param name the component's name
     */
    public FormEvent(Object source, ComponentHandler<?> handler, String name)
    {
        super(source);
        this.handler = handler;
        this.name = name;
    }

    /**
     * Returns the handler object of the component, which caused this event.
     *
     * @return the component handler
     */
    public ComponentHandler<?> getHandler()
    {
        return handler;
    }

    /**
     * Returns the name of the component, which caused this event.
     *
     * @return the component's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code
     * @since 1.3
     */
    @Override
    public int hashCode()
    {
        final int factor = 37;
        int result = ObjectUtils.hashCode(getName());
        result = factor * result + ObjectUtils.hashCode(getHandler());
        result = factor * result + ObjectUtils.hashCode(getSource());
        return result;
    }

    /**
     * {@inheritDoc} This base implementation tests the handler and name
     * properties. It is implemented in a way that subclasses can override it to
     * add checks for additional properties.
     *
     * @since 1.3
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || !getClass().equals(obj.getClass()))
        {
            return false;
        }

        FormEvent c = (FormEvent) obj;
        return ObjectUtils.equals(getName(), c.getName())
                && ObjectUtils.equals(getHandler(), c.getHandler())
                && ObjectUtils.equals(getSource(), c.getSource());
    }
}

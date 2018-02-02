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
package net.sf.jguiraffe.gui.builder.action.tags;

import java.util.EventListener;

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.InputComponentTag;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A base class for event listener tag handler classes that deal with event
 * listeners for form events.
 * </p>
 * <p>
 * This base class provides functionality for registering event listeners at
 * input components (i.e. the typical form events like action events, focus
 * events, or change events). It performs all necessary steps for locating the
 * input component tag this tag is nested inside and registering the passed in
 * listener object. Because the event listener can only be registered after the
 * affected input component has been created, registration is performed by a
 * {@link net.sf.jguiraffe.gui.builder.components.ComponentBuilderCallBack
 * ComponentBuilderCallBack} object.
 * </p>
 * <p>
 * By specifying {@link EventListenerTypeTag} objects in the body of this tag
 * multiple event listeners for various event types can be added at once. This
 * allows mapping an action to multiple event types. Concrete sub classes only
 * have to provide the initial event listener type in the constructor call.
 * </p>
 * <p>
 * This class can also be used directly as tag handler implementation. In this
 * case there is no default event type. So {@link EventListenerTypeTag} tags
 * must be placed in the body of this tag.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormEventListenerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormEventListenerTag extends EventListenerTag
{
    /**
     * Creates a new instance of {@code FormEventListenerTag}. No event listener
     * type is set. This has to be done by nested tags.
     */
    public FormEventListenerTag()
    {
        super();
    }

    /**
     * Creates a new instance of {@code FormEventListenerTag} and sets the event
     * listener type.
     *
     * @param type the {@code FormListenerType}
     */
    public FormEventListenerTag(FormListenerType type)
    {
        this();
        addListenerType(type);
    }

    /**
     * Registers the specified event listener. This implementation checks
     * whether this tag is nested in an {@link InputComponentTag} tag. If this
     * is the case, the listener is registered at the component handler of this
     * component.
     *
     * @param listener the listener to be registered
     * @return a flag whether registration was possible
     * @throws JellyTagException if the tag is incorrectly used
     * @throws FormBuilderException if a tag-related error occurs
     */
    @Override
    protected boolean registerListener(EventListener listener)
            throws JellyTagException, FormBuilderException
    {
        InputComponentTag parent =
                (InputComponentTag) findAncestorWithClass(InputComponentTag.class);
        if (parent == null)
        {
            return false;
        }

        addComponentRegistrationCallbacks(listener, parent.getName());
        return true;
    }
}

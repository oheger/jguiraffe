/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * <p>
 * A specific event class for dealing with action events.
 * </p>
 * <p>
 * Action events are triggered by components like buttons. They usually cause
 * the application to perform a certain action. As an additional property a
 * command string is provided.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormActionEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormActionEvent extends FormEvent
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 626918826171020465L;

    /** Stores the command string. */
    private final String command;

    /**
     * Creates a new instance of <code>FormActionEvent</code> and initializes
     * it.
     *
     * @param source the original event
     * @param handler the component handler
     * @param name the component's name
     * @param cmd the action command string
     */
    public FormActionEvent(Object source, ComponentHandler<?> handler,
            String name, String cmd)
    {
        super(source, handler, name);
        command = cmd;
    }

    /**
     * Returns the action command string.
     *
     * @return the command
     */
    public String getCommand()
    {
        return command;
    }

    /**
     * {@inheritDoc} This implementation takes the additional fields into
     * account declared by this class.
     *
     * @since 1.3
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(getCommand()).toHashCode();
    }

    /**
     * {@inheritDoc} This implementation also checks the additional fields
     * declared by this class.
     *
     * @since 1.3
     */
    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj)
                && ObjectUtils.equals(getCommand(),
                        ((FormActionEvent) obj).getCommand());
    }
}

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
package net.sf.jguiraffe.gui.builder.window.tags;

import java.util.EventListener;

import net.sf.jguiraffe.gui.builder.action.tags.EventListenerTag;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.window.WindowListener;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specialized event listener tag that can be used to register window
 * listeners.
 * </p>
 * <p>
 * With this tag it is possible to link events triggered by a window to an
 * action. The name of the action to be invoked must be specified as parameter.
 * Optionally an event filter can be specified; the action is then only invoked
 * if the window event is matched by the filter. There are no additional
 * attributes other than the ones defined by the base class.
 * </p>
 * <p>
 * Tags of this type must be nested inside a window tag or alternatively specify
 * the {@code targetBean} attribute. The listener is registered at the window
 * created by this window tag or at the target bean.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowListenerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class WindowListenerTag extends EventListenerTag
{
    /** Constant for the name of the listener type supported by this tag. */
    private static final String LISTENER_TYPE_NAME = "Window";

    /**
     * Creates a new instance of {@code WindowListenerTag}. Sets the initial
     * listener type.
     */
    public WindowListenerTag()
    {
        addListenerType(LISTENER_TYPE_NAME, WindowListener.class);
    }

    /**
     * {@inheritDoc} This implementation checks whether this tag is nested
     * inside a {@link WindowBaseTag}. If this is the case, the window created
     * by this tag is obtained, and the listener is registered at this window.
     */
    @Override
    protected boolean registerListener(EventListener listener)
            throws JellyTagException, FormBuilderException
    {
        WindowBaseTag parent = (WindowBaseTag) findAncestorWithClass(WindowBaseTag.class);
        if (parent != null)
        {
            addBeanRegistrationCallbacks(listener, null, parent.getWindow());
            return true;
        }

        return false;
    }
}

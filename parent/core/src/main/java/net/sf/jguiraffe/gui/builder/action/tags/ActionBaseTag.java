/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import org.apache.commons.jelly.JellyContext;

import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionManager;
import net.sf.jguiraffe.gui.builder.components.tags.FormBaseTag;

/**
 * <p>
 * An abstract base class for all tag handler classes of the action builder.
 * </p>
 * <p>
 * This class provides some basic functionality used by all these tags.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionBaseTag.java 205 2012-01-29 18:29:57Z oheger $
 */
abstract class ActionBaseTag extends FormBaseTag
{
    /**
     * Returns a reference to the current <code>ActionBuilder</code> instance.
     * This instance is expected to be placed in the Jelly context.
     *
     * @return the current action builder
     */
    protected ActionBuilder getActionBuilder()
    {
        return getActionBuilder(getContext());
    }

    /**
     * Convenience method for obtaining a reference to the current
     * <code>ActionManager</code>.
     *
     * @return the current action manager
     */
    protected ActionManager getActionManager()
    {
        return getActionManager(getContext());
    }

    /**
     * Returns a reference to the current <code>ActionBuilder</code> instance
     * from the given Jelly context.
     *
     * @param context the Jelly context
     * @return the current action builder
     */
    static ActionBuilder getActionBuilder(JellyContext context)
    {
        return ActionBuilder.get(context);
    }

    /**
     * Convenience method for obtaining a reference to an
     * <code>ActionManager</code> object from the specified Jelly context.
     *
     * @param context the context
     * @return the action manager from this context
     */
    static ActionManager getActionManager(JellyContext context)
    {
        return getActionBuilder(context).getActionManager();
    }
}

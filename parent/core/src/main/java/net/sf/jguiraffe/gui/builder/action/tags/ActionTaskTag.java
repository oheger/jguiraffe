/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import org.apache.commons.jelly.JellyTagException;

import net.sf.jguiraffe.gui.builder.components.tags.UseBeanBaseTag;

/**
 * <p>
 * A simple tag for specifying task objects for actions.
 * </p>
 * <p>
 * This tag must be nested inside an <code>{@link ActionTag}</code> tag. It
 * creates a task object (which must implement the <code>Runnable</code>
 * interface) and assigns it to the nesting action tag.
 * </p>
 * <p>
 * By inheriting from <code>UseBeanBaseTag</code> this tag supports the
 * creation of new objects (by specifying the <code>class</code> attribute)
 * and the usage of existing beans stored in the Jelly context or the current
 * bean context as well.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionTaskTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ActionTaskTag extends UseBeanBaseTag
{
    /**
     * Passes the resulting bean to the nesting action tag. This implementation
     * checks whether this tag is nested inside an action tag. If this is not the
     * case, an exception will be thrown.
     *
     * @param bean the resulting bean
     * @return a flag whether the bean could be passed to a target
     * @exception JellyTagException if an error occurs
     */
    @Override
    protected boolean passResults(Object bean) throws JellyTagException
    {
        ActionTag tag = (ActionTag) findAncestorWithClass(ActionTag.class);
        if (tag != null)
        {
            tag.setTask(bean);
            return true;
        }

        return false;
    }
}

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

import net.sf.jguiraffe.gui.builder.event.FormListenerType;

/**
 * <p>
 * A specific form event listener tag implementation that deals with change
 * listeners.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ChangeListenerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ChangeListenerTag extends FormEventListenerTag
{
    /**
     * Creates a new instance of {@code ChangeListenerTag}.
     */
    public ChangeListenerTag()
    {
        super(FormListenerType.CHANGE);
    }
}

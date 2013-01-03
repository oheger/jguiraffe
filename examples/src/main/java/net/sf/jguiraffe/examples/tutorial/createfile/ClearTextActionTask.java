/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
package net.sf.jguiraffe.examples.tutorial.createfile;

import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A task for an action that clears the content of a text component.
 * </p>
 * <p>
 * This action task is used by the create new file dialog to clear the text
 * field for the content of the new file.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ClearTextActionTask.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ClearTextActionTask implements Runnable
{
    /** The handler for the text field. */
    private final ComponentHandler<String> textHandler;

    /**
     * Creates a new instance of {@code ClearTextActionTask} and initializes it.
     *
     * @param handler the handler to be cleared
     */
    public ClearTextActionTask(ComponentHandler<String> handler)
    {
        textHandler = handler;
    }

    /**
     * Executes this task. This implementation just writes null data into the
     * handler.
     */
    @Override
    public void run()
    {
        textHandler.setData(null);
    }
}

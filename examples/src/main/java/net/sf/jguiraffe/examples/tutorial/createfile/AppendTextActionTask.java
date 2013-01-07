/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
 * A task for an action that appends a configurable text to a text component.
 * </p>
 * <p>
 * This task class is used by actions of the create file dialog. It populates
 * the text field for the file's content with predefined text fragments.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AppendTextActionTask.java 205 2012-01-29 18:29:57Z oheger $
 */
public class AppendTextActionTask implements Runnable
{
    /** The handler for the text field. */
    private final ComponentHandler<String> textHandler;

    /** The text to be appended. */
    private final String text;

    /**
     * Creates a new instance of {@code AppendTextActionTask} and initializes
     * it.
     *
     * @param handler the handler to be updated
     * @param txt the text to be appended
     */
    public AppendTextActionTask(ComponentHandler<String> handler, String txt)
    {
        textHandler = handler;
        text = txt;
    }

    /**
     * Executes this task. This implementation obtains the current text of the
     * managed text handler. Then it appends the text and writes it back.
     */
    @Override
    public void run()
    {
        String currentText = textHandler.getData();
        textHandler.setData(currentText + text);
    }
}

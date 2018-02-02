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
package net.sf.jguiraffe.gui.builder.utils;

import net.sf.jguiraffe.gui.builder.window.Window;

/**
 * <p>
 * An implementation of the {@code MessageOutput} interface that does not output
 * a message.
 * </p>
 * <p>
 * This class provides an empty implementation of the {@code show()} method.
 * Therefore messages are effectively suppressed. An instance of this class can
 * be used when a {@link MessageOutput} object is required, but no message boxes
 * are to be displayed, e.g. because there is an alternative way of giving
 * feedback to the user.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: NullMessageOutput.java 205 2012-01-29 18:29:57Z oheger $
 */
public class NullMessageOutput implements MessageOutput
{
    /**
     * Outputs the specified message. This implementation does nothing; it
     * simply ignores the message. It returns always {@code RET_CANCEL}.
     *
     * @param parent the parent window
     * @param message specifies the message
     * @param title the title of the message box
     * @param messageType a type for the message
     * @param buttonType determines the buttons to be displayed
     * @return a value indicating the button clicked by the user
     */
    public int show(Window parent, Object message, String title,
            int messageType, int buttonType)
    {
        return RET_CANCEL;
    }
}

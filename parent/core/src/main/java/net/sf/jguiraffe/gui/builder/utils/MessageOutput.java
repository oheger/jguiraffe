/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
 * Definition of an interface for creating message boxes in a platform
 * independent way.
 * </p>
 * <p>
 * This interface defines a main {@code show()} method to display a message box
 * of a pre-defined type. The type is specified using one of the the constants
 * defined by this interface. The method expects a title (as string) and an
 * object representing the message to be displayed. From this object the
 * {@code toString()} is called in order to obtain the message text to be
 * displayed. Concrete implementations have to implement a certain amount of
 * processing on the message text:
 * <ul>
 * <li>The character '\n' should cause a newline in the message. That way
 * messages with multiple lines can be created.</li>
 * <li>A reasonable line wrapping should be performed to prevent that the
 * message window becomes too wide or that parts of the message text are cut
 * off.</li>
 * </ul>
 * In addition, this interface defines some constants for the buttons to be
 * displayed in the constructed message window. For instance, it is possible to
 * have just an OK button or specify that buttons for a YES/NO/CANCEL question
 * are generated. The return value of the {@code show()} method indicates the
 * pressed button.
 * </p>
 * <p>
 * Note that the {@code show()} method must be called in the UI thread! It lies
 * in the responsibility of the caller to use the current
 * {@link GUISynchronizer} to ensure that the invocation happens in the correct
 * thread.
 * </p>
 * <p>
 * Concrete implementations will map the functionality provided by this
 * interface to GUI library specific classes. A Swing related implementation for
 * instance could use Swing's {@code JOptionPane} to provide the required
 * functionality.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: MessageOutput.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface MessageOutput
{
    /** Constant for the message type ERROR. */
    int MESSAGE_ERROR = 1;

    /** Constant for the message type INFO. */
    int MESSAGE_INFO = 2;

    /** Constant for the message type WARNING. */
    int MESSAGE_WARNING = 3;

    /** Constant for the message type QUESTION. */
    int MESSAGE_QUESTION = 4;

    /** Constant for the message type PLAIN. */
    int MESSAGE_PLAIN = 5;

    /** Constant for the button type OK. */
    int BTN_OK = 1;

    /** Constant for the button type OK, CANCEL. */
    int BTN_OK_CANCEL = 2;

    /** Constant for the button type YES, NO. */
    int BTN_YES_NO = 3;

    /** Constant for the button type YES, NO, CANCEL. */
    int BTN_YES_NO_CANCEL = 4;

    /** Constant for the return value OK. */
    int RET_OK = 1;

    /** Constant for the return value CANCEL. */
    int RET_CANCEL = 2;

    /**
     * Constant for the return value YES. Note that this value is identical to
     * the {@code RET_OK} return value. This is analogous to Swing.
     */
    int RET_YES = RET_OK;

    /** Constant for the return value NO. */
    int RET_NO = 4;

    /**
     * Displays a message box based on the given options. Please refer to the
     * class comment for further details about the parameters and how they are
     * interpreted.
     *
     * @param parent the parent window
     * @param message the message itself; can be an arbitrary object whose
     * {@code toString()} method will be used to obtain the text to be
     * displayed
     * @param title the message box's title
     * @param messageType the type of the message; this must be one the
     * {@code MESSAGE_XXXX} constants
     * @param buttonType defines the buttons to be displayed; this must be one
     * of the {@code BTN_XXXX} constants
     * @return a flag for the button pressed by the user; this will be one of
     * the {@code RET_XXXX} constants
     */
    int show(Window parent, Object message, String title, int messageType,
            int buttonType);
}

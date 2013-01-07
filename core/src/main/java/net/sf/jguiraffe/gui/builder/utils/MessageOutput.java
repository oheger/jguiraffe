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
package net.sf.jguiraffe.gui.builder.utils;

import net.sf.jguiraffe.gui.builder.window.Window;

/**
 * <p>
 * Definition of an interface for creating message boxes in a platform
 * independent way.
 * </p>
 * <p>
 * This interface defines some constants representing the typical options
 * supported by message boxes, e.g. message type or available options. A main
 * <code>show()</code> method is used to display the message box. Its result
 * value indicates the pressed button.
 * </p>
 * <p>
 * Concrete implementations will map the functionality provided by this
 * interface to GUI library specific classes. A Swing related implementation for
 * instance could use Swing's <code>JOptionPane</code> to provide the required
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
     * the <code>RET_OK</code> return value. This is analogous to Swing.
     */
    int RET_YES = RET_OK;

    /** Constant for the return value NO. */
    int RET_NO = 4;

    /**
     * Displays a message box based on the given options.
     *
     * @param parent the parent window
     * @param message the message itself; can be an arbitrary object whose
     * <code>toString()</code> method will be used to obtain the text to be
     * displayed
     * @param title the message box's title
     * @param messageType the type of the message; this must be one the
     * <code>MESSAGE_XXXX</code> constants
     * @param buttonType defines the buttons to be displayed; this must be one
     * of the <code>BTN_XXXX</code> constants
     * @return a flag for the button pressed by the user; this will be one of
     * the <code>RET_XXXX</code> constants
     */
    int show(Window parent, Object message, String title, int messageType,
            int buttonType);
}

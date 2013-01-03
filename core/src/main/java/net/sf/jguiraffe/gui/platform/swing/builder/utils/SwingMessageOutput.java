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
package net.sf.jguiraffe.gui.platform.swing.builder.utils;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowUtils;

/**
 * <p>
 * A Swing specific implementation of the <code>MessageOutput</code>
 * interface.
 * </p>
 * <p>
 * This implementation makes use of <code>JOptionPane</code> for displaying
 * message boxes.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingMessageOutput.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingMessageOutput implements MessageOutput
{
    /** An array with the supported message types. */
    private static final int[] MESSAGE_TYPES = {
            MESSAGE_ERROR, MESSAGE_INFO, MESSAGE_PLAIN, MESSAGE_QUESTION,
            MESSAGE_WARNING
    };

    /** An array with the JOptionPane message types. */
    private static final int[] SWING_MESSAGE_TYPES = {
            JOptionPane.ERROR_MESSAGE, JOptionPane.INFORMATION_MESSAGE,
            JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE,
            JOptionPane.WARNING_MESSAGE
    };

    /** An array with the supported button types. */
    private static final int[] BUTTON_TYPES = {
            BTN_OK, BTN_OK_CANCEL, BTN_YES_NO, BTN_YES_NO_CANCEL
    };

    /** An array with the JOptionPane button types. */
    private static final int[] SWING_BUTTON_TYPES = {
            JOptionPane.DEFAULT_OPTION, JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_CANCEL_OPTION
    };

    /** An array with the supported return values. */
    private static final int[] RETURN_VALUES = {
            RET_CANCEL, RET_OK, RET_YES, RET_NO, RET_CANCEL
    };

    /** An array with the JOptionPane return values. */
    private static final int[] SWING_RETURN_VALUES = {
            JOptionPane.CANCEL_OPTION, JOptionPane.OK_OPTION,
            JOptionPane.YES_OPTION, JOptionPane.NO_OPTION,
            JOptionPane.CLOSED_OPTION
    };

    /**
     * Displays a message box.
     *
     * @param parent the parent window; this should be <b>null</b> or point to
     * a Swing window
     * @param message the message
     * @param title the message box's title
     * @param messageType the type of the message
     * @param buttonType specifies the buttons to be displayed
     * @return the pressed button
     */
    public int show(Window parent, Object message, String title,
            int messageType, int buttonType)
    {
        JOptionPane pane = createOptionPane(parent, message, title,
                convertMessageType(messageType), convertButtonType(buttonType));
        JDialog dlg = createDialog(pane, parent, title);
        Object result = showPane(pane, dlg);
        if (result == null || !(result instanceof Integer))
        {
            return RET_CANCEL;
        }
        else
        {
            return convertReturnValue(((Integer) result).intValue());
        }
    }

    /**
     * Converts the passed in message type into the corresponding type used by
     * <code>JOptionPane</code>.
     *
     * @param type the type to be converted
     * @return the corresponding Swing constant
     */
    protected int convertMessageType(int type)
    {
        return convert(type, MESSAGE_TYPES, SWING_MESSAGE_TYPES);
    }

    /**
     * Converts the passed in button type into the corresponding option type
     * used by <code>JOptionPane</code>.
     *
     * @param type the type to be converted
     * @return the corresponding Swing constant
     */
    protected int convertButtonType(int type)
    {
        return convert(type, BUTTON_TYPES, SWING_BUTTON_TYPES);
    }

    /**
     * Converts the passed in return value from the <code>JOptionPane</code>
     * to the corresponding <code>RET_XXXX</code> constant.
     *
     * @param value the return value from the option pane
     * @return the corresponding <code>RET_XXXX</code> constant
     */
    protected int convertReturnValue(int value)
    {
        return convert(value, SWING_RETURN_VALUES, RETURN_VALUES);
    }

    /**
     * Creates the option pane dialog for displaying the message box.
     *
     * @param parent the parent window
     * @param message the message
     * @param title the title
     * @param messageType the message type
     * @param optionType the option type
     * @return the option pane
     */
    protected JOptionPane createOptionPane(Window parent, Object message,
            String title, int messageType, int optionType)
    {
        return new JOptionPane(message, messageType, optionType);
    }

    /**
     * Displays the given option pane. This method is called after the pane has
     * been created and initialized.
     *
     * @param pane the pane to display
     * @param dialog the dialog obtained from the option pane
     * @return the return value of the pane (indicating the option selected by
     * the user)
     */
    protected Object showPane(JOptionPane pane, JDialog dialog)
    {
        dialog.setVisible(true);
        return pane.getValue();
    }

    /**
     * Creates the dialog from the option pane. This is the component that is to
     * be displayed.
     *
     * @param pane the option pane
     * @param parent the parent component
     * @param title the dialog's title
     * @return the dialog to display
     */
    protected JDialog createDialog(JOptionPane pane, Window parent, String title)
    {
        return pane.createDialog((parent != null) ? (Component) WindowUtils
                .getPlatformWindow(parent) : null, title);
    }

    /**
     * Converts a value from a source range into a destination range. If the
     * conversion fails, an exception is thrown.
     *
     * @param value the value
     * @param src the source values
     * @param dest the destination values
     * @return the converted value
     */
    private static int convert(int value, int[] src, int[] dest)
    {
        for (int i = 0; i < src.length; i++)
        {
            if (value == src[i])
            {
                return dest[i];
            }
        }

        throw new IllegalArgumentException("Unknown parameter " + value);
    }
}

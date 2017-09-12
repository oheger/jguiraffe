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
package net.sf.jguiraffe.gui.platform.swing.builder.utils;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.awt.Component;

import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

/**
 * <p>
 * A Swing specific implementation of the {@code MessageOutput}
 * interface.
 * </p>
 * <p>
 * This implementation makes use of {@code JOptionPane} for displaying
 * message boxes.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingMessageOutput.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingMessageOutput implements MessageOutput
{
    /**
     * Constant for a line length which disables line wrapping. If this value is
     * passed to the constructor, the message text is not wrapped into multiple
     * lines.
     *
     * @since 1.3
     */
    public static final int NO_LINE_WRAP = -1;

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

    /** Constant for a starting HTML tag. */
    private static final String HTML_START = "<html>";

    /** Constant for a closing HTML tag. */
    private static final String HTML_END = "</html>";

    /** Constant for the default maximum line length. */
    private static final int DEFAULT_MAX_LINE_LENGTH = 80;

    /** Constant for the initial length of the buffer for text processing. */
    private static final int BUF_LENGTH = 256;

    /** Constant for the newline character. */
    private static final String CR = "\n";

    /** The maximum length of a line for the message text. */
    private final int maximumLineLength;

    /**
     * Creates a new instance of {@code SwingMessageOutput} and sets a default
     * maximum line length.
     */
    public SwingMessageOutput()
    {
        this(DEFAULT_MAX_LINE_LENGTH);
    }

    /**
     * Creates a new instance of {@code SwingMessageOutput} with the specified
     * maximum message line length. Before the message is displayed, it is
     * ensured that single lines do not exceed this maximum length; if
     * necessary, the text is split into multiple lines. To disable line
     * wrapping, the value {@link #NO_LINE_WRAP} can be passed.
     *
     * @param maxLineLength the maximum length of a line for the message text
     *        (in characters); must be &gt; 0
     * @throws IllegalArgumentException if an invalid line length is passed in
     * @since 1.3
     */
    public SwingMessageOutput(int maxLineLength)
    {
        if (maxLineLength != NO_LINE_WRAP && maxLineLength < 1)
        {
            throw new IllegalArgumentException(
                    "Maximum line length must be > 0!");
        }
        maximumLineLength = maxLineLength;
    }

    /**
     * Returns the maximum line length for the messages to be displayed.
     *
     * @return the maximum line length
     * @since 1.3
     */
    public int getMaximumLineLength()
    {
        return maximumLineLength;
    }

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
     * {@code JOptionPane}.
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
     * used by {@code JOptionPane}.
     *
     * @param type the type to be converted
     * @return the corresponding Swing constant
     */
    protected int convertButtonType(int type)
    {
        return convert(type, BUTTON_TYPES, SWING_BUTTON_TYPES);
    }

    /**
     * Converts the passed in return value from the {@code JOptionPane}
     * to the corresponding {@code RET_XXXX} constant.
     *
     * @param value the return value from the option pane
     * @return the corresponding {@code RET_XXXX} constant
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
        return new JOptionPane(processMessage(message), messageType, optionType);
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
     * Processes the given message before it is displayed. This method
     * implements some conversions to ensure that a valid message is displayed
     * in a visually pleasant way. It does the following changes:
     * <ul>
     * <li>If a maximum line length is specified, line wrapping is performed.</li>
     * <li>If the message string is wrapped in HTML tags, they are removed.</li>
     * </ul>
     *
     * @param message the message
     * @return the processed message
     */
    private Object processMessage(Object message)
    {
        if (message == null)
        {
            return StringUtils.EMPTY;
        }

        String msg = removeHtmlTags(message);
        if (getMaximumLineLength() != NO_LINE_WRAP)
        {
            return lineWrap(msg);
        }
        return msg;
    }

    /**
     * Performs line wrapping for the specified message object. For each line in
     * the message string the maximum line length is enforced.
     *
     * @param message the message
     * @return the message with line wrapping performed
     */
    private String lineWrap(String message)
    {
        String[] lines = message.split(String.valueOf(CR));
        StringBuilder buf = new StringBuilder(BUF_LENGTH);
        boolean first = true;

        for (String line : lines)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                buf.append(CR);
            }
            buf.append(WordUtils.wrap(line, getMaximumLineLength(), CR, true));
        }
        return buf.toString();
    }

    /**
     * Removes leading and trailing html tags from the message string. HTML
     * formatting is not supported.
     *
     * @param message the message
     * @return the processed message
     */
    private static String removeHtmlTags(Object message)
    {
        String s = message.toString();
        return StringUtils.removeEndIgnoreCase(
                StringUtils.removeStartIgnoreCase(s, HTML_START), HTML_END);
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

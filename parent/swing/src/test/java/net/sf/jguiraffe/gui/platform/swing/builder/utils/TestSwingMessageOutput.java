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
package net.sf.jguiraffe.gui.platform.swing.builder.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowWrapper;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingMessageOutput.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingMessageOutput.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingMessageOutput
{
    /** Constant for the test message. */
    private static final Object MESSAGE = "This is a message";

    /** Constant for the message box's title. */
    private static final String TITLE = "A title";

    /** Stores the object to be tested. */
    private SwingMessageOutput output;

    @Before
    public void setUp() throws Exception
    {
        output = new SwingMessageOutput();
    }

    /**
     * Tests converting the button type.
     */
    @Test
    public void testConvertButtonType()
    {
        assertEquals("Wrong button type", JOptionPane.YES_NO_CANCEL_OPTION,
                output.convertButtonType(MessageOutput.BTN_YES_NO_CANCEL));
    }

    /**
     * Tries to convert an unknown button type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConvertButtonTypeIllegal()
    {
        output.convertButtonType(42);
    }

    /**
     * Tests converting the message type.
     */
    @Test
    public void testConvertMessageType()
    {
        assertEquals("Wrong message type", JOptionPane.WARNING_MESSAGE, output
                .convertMessageType(MessageOutput.MESSAGE_WARNING));
    }

    /**
     * Tries to convert an unknown message type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConvertMessageTypeIllegal()
    {
        output.convertMessageType(42);
    }

    /**
     * Tests converting the return value.
     */
    @Test
    public void testConvertReturnValue()
    {
        assertEquals("Wrong return value", MessageOutput.RET_NO, output
                .convertReturnValue(JOptionPane.NO_OPTION));
        assertEquals("CLOSED_OPTION not recognized", MessageOutput.RET_CANCEL,
                output.convertReturnValue(JOptionPane.CLOSED_OPTION));
    }

    /**
     * Tests creating the option pane.
     */
    @Test
    public void testCreateOptionPane()
    {
        JOptionPane op = output.createOptionPane(null, MESSAGE, TITLE,
                JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
        assertEquals("Wrong message", MESSAGE, op.getMessage());
        assertEquals("Wrong option type", JOptionPane.YES_NO_CANCEL_OPTION, op
                .getOptionType());
        assertEquals("Wrong message type", JOptionPane.WARNING_MESSAGE, op
                .getMessageType());
    }

    /**
     * Produces a pretty long text message.
     * @return the long text message
     */
    private static String longMessage()
    {
        final int count = 32;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < count; i++) {
            buf.append(MESSAGE);
        }
        return buf.toString();
    }

    /**
     * Tests whether long text messages are line wrapped.
     */
    @Test
    public void testMessageLineWrap()
    {
        String msg  = longMessage();
        JOptionPane op = output.createOptionPane(null, msg, TITLE,
                JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);

        checkLineWrapping(msg, op);
    }

    /**
     * Checks whether a message string is correctly wrapped into multiple lines.
     * @param msg the expected message string
     * @param op the option pane
     */
    private static void checkLineWrapping(String msg, JOptionPane op) {
        String[] lines = op.getMessage().toString().split("\n");
        StringBuilder buf = new StringBuilder();
        for (String line : lines)
        {
            assertTrue("Line is too long: " + line, line.length() <= 80);
            buf.append(' ').append(line);
        }
        assertEquals("Wrong message", msg, buf.toString().trim());
    }

    /**
     * Tests whether line wrapping can be disabled.
     */
    @Test
    public void testDisableLineWrap()
    {
        String msg = longMessage();
        output = new SwingMessageOutput(SwingMessageOutput.NO_LINE_WRAP);
        JOptionPane op = output.createOptionPane(null, msg, TITLE,
                JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);

        assertEquals("Line was wrapped", msg, op.getMessage());
    }

    /**
     * Tests whether the maximum line length passed to the constructor is validated.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitWithInvalidMaximumLineLength()
    {
        new SwingMessageOutput(0);
    }

    /**
     * Tests that newline characters are handled correctly when performing line wrapping.
     */
    @Test
    public void testLineWrapWithNewLines()
    {
        String msg = String.valueOf(MESSAGE) + '\n' + longMessage();

        JOptionPane op = output.createOptionPane(null, msg, TITLE,
                JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
        checkLineWrapping(msg.replace('\n', ' '), op);
    }

    /**
     * Tests whether a null message is handled correctly.
     */
    @Test
    public void testNullMessage()
    {
        JOptionPane op = output.createOptionPane(null, null, TITLE,
                JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
        assertEquals("Wrong message", "", op.getMessage());
    }

    /**
     * Tests that enclosing html tags are removed from a message.
     */
    @Test
    public void testRemoveHTMLTags()
    {
        final String msg = "<html>" + MESSAGE + "</hTML>";

        JOptionPane op = output.createOptionPane(null, msg, TITLE,
                JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
        assertEquals("Wrong message", MESSAGE, op.getMessage());
    }

    /**
     * Tests creating the dialog from the option pane when no parent window is
     * defined.
     */
    @Test
    public void testCreateDialogWithNullParent()
    {
        JOptionPane op = new JOptionPane(MESSAGE, JOptionPane.WARNING_MESSAGE);
        JDialog dlg = output.createDialog(op, null, TITLE);
        assertEquals("Wrong title", TITLE, dlg.getTitle());
    }

    /**
     * Tests creating the dialog from the option pane and providing a parent
     * window.
     */
    @Test
    public void testCreateDialogWithParent()
    {
        JOptionPane op = new JOptionPane(MESSAGE, JOptionPane.WARNING_MESSAGE);
        JFrame parent = new JFrame();
        WrappingWindow mockWnd = EasyMock.createMock(WrappingWindow.class);
        EasyMock.expect(mockWnd.getWrappedWindow()).andReturn(parent);
        EasyMock.replay(mockWnd);
        JDialog dlg = output.createDialog(op, mockWnd, null);
        assertEquals("Wrong parent", parent, dlg.getParent());
        assertNull("Dialog has a title", dlg.getTitle());
        EasyMock.verify(mockWnd);
    }

    /**
     * Tests making the pane visible.
     */
    @Test
    public void testShowPane()
    {
        JOptionPane op = new TestOptionPane();
        TestDialog dlg = new TestDialog();
        assertEquals("Wrong option returned",
                Integer.valueOf(JOptionPane.YES_OPTION),
                output.showPane(op, dlg));
        assertEquals("Show was not called once", 1, dlg.showCalls);
    }

    /**
     * Tests displaying a message box as a whole.
     */
    @Test
    public void testShow()
    {
        output = new SwingMessageOutput()
        {
            @Override
            protected JDialog createDialog(JOptionPane pane, Window parent,
                    String title)
            {
                return new TestDialog();
            }

            @Override
            protected JOptionPane createOptionPane(Window parent,
                    Object message, String title, int messageType,
                    int optionType)
            {
                assertEquals("Wrong message type", JOptionPane.WARNING_MESSAGE,
                        messageType);
                assertEquals("Wrong option type",
                        JOptionPane.YES_NO_CANCEL_OPTION, optionType);
                return new TestOptionPane();
            }
        };
        assertEquals(MessageOutput.RET_YES, output.show(null, MESSAGE, TITLE,
                MessageOutput.MESSAGE_WARNING, MessageOutput.BTN_YES_NO_CANCEL));
    }

    // Helper interface needed for easy mock to implement two interfaces at once
    static interface WrappingWindow extends Window, WindowWrapper
    {
    }

    /**
     * A test dialog class that checks if its show() method was called.
     */
    @SuppressWarnings("serial")
    static class TestDialog extends JDialog
    {
        int showCalls;

        @Override
        public void setVisible(boolean f)
        {
            if (f)
            {
                showCalls++;
            }
        }
    }

    /**
     * A test option pane that always returns a certain value.
     */
    @SuppressWarnings("serial")
    static class TestOptionPane extends JOptionPane
    {
        public TestOptionPane()
        {
            super(MESSAGE, JOptionPane.WARNING_MESSAGE,
                    JOptionPane.YES_NO_CANCEL_OPTION);
        }

        @Override
        public Object getValue()
        {
            return new Integer(JOptionPane.YES_OPTION);
        }
    }
}

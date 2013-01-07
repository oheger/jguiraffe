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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import net.sf.jguiraffe.gui.platform.swing.builder.event.ChangeListener;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingTextHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingTextHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingTextHandler
{
    /** Constant for a test text. */
    private static final String TEST_DATA = "A test text for testing";

    /** Constant for the selection start index. */
    private static final int SEL_START = 2;

    /** Constant for the selection end index. */
    private static final int SEL_END = TEST_DATA.length() - 12;

    /** Stores the handler to be tested. */
    private SwingTextHandler handler;

    /** Stores the wrapped text component. */
    private TextFieldTestImpl component;

    @Before
    public void setUp() throws Exception
    {
        component = new TextFieldTestImpl();
        handler = new SwingTextHandler(component);
    }

    /**
     * Tests accessing the wrapped text component.
     */
    @Test
    public void testGetTextComponent()
    {
        assertSame("Wrong wrapped component", component, handler
                .getTextComponent());
    }

    /**
     * Tests accessing the text field's content.
     */
    @Test
    public void testGetData()
    {
        component.setText(TEST_DATA);
        assertEquals("Wrong data returned", TEST_DATA, handler.getData());
    }

    /**
     * Tests accessing the text field's content when it is empty.
     */
    @Test
    public void testGetDataNull()
    {
        component.setText(null);
        assertEquals("Wrong data for empty text field", "", handler.getData());
    }

    /**
     * Tests setting the text field's content.
     */
    @Test
    public void testSetData()
    {
        handler.setData(TEST_DATA);
        assertEquals("Wrong text content", TEST_DATA, component.getText());
    }

    /**
     * Tests setting the text field's content to null.
     */
    @Test
    public void testSetDataNull()
    {
        component.setText(TEST_DATA);
        handler.setData(null);
        assertEquals("Text was not removed", "", component.getText());
    }

    /**
     * Tests adding a change listener.
     */
    @Test
    public void testAddChangeListener()
    {
        final DocumentEvent event = EasyMock.createMock(DocumentEvent.class);
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        mockListener.componentChanged(event);
        EasyMock.expectLastCall().times(3);
        EasyMock.replay(mockListener);
        handler.addChangeListener(mockListener);
        fireTestEvent(event);
        EasyMock.verify(mockListener);
    }

    /**
     * Tests whether a change listener can be removed.
     */
    @Test
    public void testRemoveChangeListener()
    {
        final DocumentEvent event = EasyMock.createMock(DocumentEvent.class);
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        mockListener.componentChanged(event);
        EasyMock.expectLastCall().times(3);
        EasyMock.replay(mockListener);
        handler.addChangeListener(mockListener);
        fireTestEvent(event);
        handler.removeChangeListener(mockListener);
        fireTestEvent(event);
        EasyMock.verify(mockListener);
    }

    /**
     * Helper method for firing different variants of the test event.
     *
     * @param event the event to fire
     */
    private void fireTestEvent(DocumentEvent event)
    {
        handler.changedUpdate(event);
        handler.insertUpdate(event);
        handler.removeUpdate(event);
    }

    /**
     * Tests the copy() method.
     */
    @Test
    public void testCopy()
    {
        handler.copy();
        component.verifyCopy();
    }

    /**
     * Tests the cut() method.
     */
    @Test
    public void testCut()
    {
        handler.cut();
        component.verifyCut();
    }

    /**
     * Tests the paste() method.
     */
    @Test
    public void testPaste()
    {
        handler.paste();
        component.verifyPaste();
    }

    /**
     * Tests hasSelection() if there is a selection.
     */
    @Test
    public void testHasSelectionTrue()
    {
        component.setText(TEST_DATA);
        component.select(0, TEST_DATA.length() - 5);
        assertTrue("No selection", handler.hasSelection());
    }

    /**
     * Tests hasSelection() if there is no selection.
     */
    @Test
    public void testHasSelectionFalse()
    {
        component.setText(TEST_DATA);
        assertFalse("Got a selection", handler.hasSelection());
    }

    /**
     * Tests whether the start index of the selection can be queried.
     */
    @Test
    public void testGetSelectionStart()
    {
        component.setText(TEST_DATA);
        component.select(SEL_START, SEL_END);
        assertEquals("Wrong selection start", SEL_START, handler
                .getSelectionStart());
    }

    /**
     * Tests whether the end index of the selection can be queried.
     */
    @Test
    public void testGetSelectionEnd()
    {
        component.setText(TEST_DATA);
        component.select(SEL_START, SEL_END);
        assertEquals("Wrong selection end", SEL_END, handler.getSelectionEnd());
    }

    /**
     * Tests whether selected text can be queried.
     */
    @Test
    public void testGetSelectedText()
    {
        component.setText(TEST_DATA);
        component.select(SEL_START, SEL_END);
        assertEquals("Wrong selected text", TEST_DATA.substring(SEL_START,
                SEL_END), handler.getSelectedText());
    }

    /**
     * Tests whether the selection can be cleared.
     */
    @Test
    public void testClearSelection()
    {
        component.setText(TEST_DATA);
        component.select(SEL_START, SEL_END);
        handler.clearSelection();
        assertFalse("Got a selection", handler.hasSelection());
        assertEquals("Wrong selection start", SEL_START, component
                .getSelectionStart());
    }

    /**
     * Tests whether text can be selected.
     */
    @Test
    public void testSelect()
    {
        component.setText(TEST_DATA);
        handler.select(SEL_START, SEL_END);
        assertEquals("Wrong selection start", SEL_START, component
                .getSelectionStart());
        assertEquals("Wrong selection end", SEL_END, component
                .getSelectionEnd());
    }

    /**
     * Tests whether select can deal with invalid values.
     */
    @Test
    public void testSelectInvalidValues()
    {
        component.setText(TEST_DATA);
        handler.select(-10, TEST_DATA.length() + 20);
        assertEquals("Wrong selection start", 0, component.getSelectionStart());
        assertEquals("Wrong selection end", TEST_DATA.length(), component
                .getSelectionEnd());
    }

    /**
     * Tests whether the whole text can be selected.
     */
    @Test
    public void testSelectAll()
    {
        component.setText(TEST_DATA);
        handler.selectAll();
        assertEquals("Wrong selection start", 0, component.getSelectionStart());
        assertEquals("Wrong selection end", TEST_DATA.length(), component
                .getSelectionEnd());
    }

    /**
     * Tests whether selected text can be replaced.
     */
    @Test
    public void testReplaceSelectedText()
    {
        component.setText(TEST_DATA);
        component.select(SEL_START, SEL_END);
        final String replacement = "*replaced text*";
        handler.replaceSelectedText(replacement);
        assertEquals("Wrong replaced text", TEST_DATA.substring(0, SEL_START)
                + replacement + TEST_DATA.substring(SEL_END), component
                .getText());
    }

    /**
     * Tests whether the selected text can be removed.
     */
    @Test
    public void testReplaceSelectedTextNull()
    {
        component.setText(TEST_DATA);
        component.select(SEL_START, SEL_END);
        handler.replaceSelectedText(null);
        assertEquals("Wrong replaced text", TEST_DATA.substring(0, SEL_START)
                + TEST_DATA.substring(SEL_END), component.getText());
    }

    /**
     * A specialized text field implementation that allows stubbing some
     * methods.
     */
    @SuppressWarnings("serial")
    private static class TextFieldTestImpl extends JTextField
    {
        /** The number of copy() invocations. */
        private int copyCount;

        /** The number of cut() invocations. */
        private int cutCount;

        /** The number of paste() invocations. */
        private int pasteCount;

        /**
         * Verifies that copy() was called once and no other clipboard-related
         * method.
         */
        public void verifyCopy()
        {
            verifyClipboardMethods(1, 0, 0);
        }

        /**
         * Verifies that cut() was called once and no other clipboard-related
         * method.
         */
        public void verifyCut()
        {
            verifyClipboardMethods(0, 1, 0);
        }

        /**
         * Verifies that paste() was called once and no other clipboard-related
         * method.
         */
        public void verifyPaste()
        {
            verifyClipboardMethods(0, 0, 1);
        }

        @Override
        public void copy()
        {
            copyCount++;
        }

        @Override
        public void cut()
        {
            cutCount++;
        }

        @Override
        public void paste()
        {
            pasteCount++;
        }

        /**
         * Verifies the number of invocations of the clipboard-related methods.
         *
         * @param expCopy expected copy count
         * @param expCut expected cut count
         * @param expPaste expected paste count
         */
        private void verifyClipboardMethods(int expCopy, int expCut,
                int expPaste)
        {
            assertEquals("Wrong number of copy() invocations", expCopy,
                    copyCount);
            assertEquals("Wrong number of cut() invocations", expCut, cutCount);
            assertEquals("Wrong number of paste() invocations", expPaste,
                    pasteCount);
        }
    }
}

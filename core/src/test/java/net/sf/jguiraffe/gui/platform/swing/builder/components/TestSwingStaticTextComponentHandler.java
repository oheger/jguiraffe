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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import junit.framework.TestCase;
import net.sf.jguiraffe.gui.builder.components.model.StaticTextData;
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment;

/**
 * Test class for SwingStaticTextComponentHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingStaticTextComponentHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingStaticTextComponentHandler extends TestCase
{
    /** Constant for a test label text. */
    private static final String TEST_TEXT = "A test text";

    /** Constant for a test icon. */
    private static final Icon TEST_ICON;

    /** Constant for the resource name of the test icon. */
    private static final String ICON_RES = "icon.gif";

    /** Stores the wrapped label. */
    private JLabel label;

    /** Stores the handler to be tested. */
    private SwingStaticTextComponentHandler handler;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        label = new JLabel();
        handler = new SwingStaticTextComponentHandler(label);
    }

    /**
     * Tests whehter the correct label gets returned.
     */
    public void testGetLabel()
    {
        assertSame("Wrong wrapped label", label, handler.getLabel());
    }

    /**
     * Tests querying the handler's data type.
     */
    public void testGetType()
    {
        assertEquals("Wrong type for handler", StaticTextData.class, handler
                .getType());
    }

    /**
     * Tests querying the alignment.
     */
    public void testGetAlignment()
    {
        label.setHorizontalAlignment(JLabel.LEFT);
        assertEquals("Wrong result for left alignment", TextIconAlignment.LEFT,
                handler.getAlignment());
        label.setHorizontalAlignment(JLabel.RIGHT);
        assertEquals("Wrong result for right alignment",
                TextIconAlignment.RIGHT, handler.getAlignment());
        label.setHorizontalAlignment(JLabel.CENTER);
        assertEquals("Wrong result for center alignment",
                TextIconAlignment.CENTER, handler.getAlignment());
    }

    /**
     * Tests setting the alignment through the handler.
     */
    public void testSetAlignment()
    {
        handler.setAlignment(TextIconAlignment.CENTER);
        assertEquals("Wrong label alignment for center", JLabel.CENTER, label
                .getHorizontalAlignment());
        handler.setAlignment(TextIconAlignment.LEFT);
        assertEquals("Wrong label alignment for left", JLabel.LEFT, label
                .getHorizontalAlignment());
        handler.setAlignment(TextIconAlignment.RIGHT);
        assertEquals("Wrong label alignment for right", JLabel.RIGHT, label
                .getHorizontalAlignment());
    }

    /**
     * Tests querying the label's icon through the handler.
     */
    public void testGetIcon()
    {
        label.setIcon(TEST_ICON);
        assertSame("Wrong icon", TEST_ICON, handler.getIcon());
    }

    /**
     * Tests setting the icon.
     */
    public void testSetIcon()
    {
        handler.setIcon(TEST_ICON);
        assertSame("Icon was not set", TEST_ICON, label.getIcon());
    }

    /**
     * Tests setting the icon to null. This should remove any icon.
     */
    public void testSetIconNull()
    {
        label.setIcon(TEST_ICON);
        handler.setIcon(null);
        assertNull("Label still has an icon", label.getIcon());
    }

    /**
     * Tests querying the label's text through the handler.
     */
    public void testGetText()
    {
        label.setText(TEST_TEXT);
        assertEquals("Wrong text", TEST_TEXT, handler.getText());
    }

    /**
     * Tests setting the label's text through the handler.
     */
    public void testSetText()
    {
        handler.setText(TEST_TEXT);
        assertEquals("Text was not set", TEST_TEXT, label.getText());
    }

    /**
     * Tests querying the label's properties after it has been created.
     */
    public void testGetDataInitial()
    {
        StaticTextData td = (StaticTextData) handler.getData();
        assertEquals("Label has a text", "", td.getText());
        assertNull("Label has an icon", td.getIcon());
        assertEquals("Wrong default alignment", TextIconAlignment.LEFT, td
                .getAlignment());
    }

    /**
     * Tests querying the label's properties.
     */
    public void testGetData()
    {
        label.setText(TEST_TEXT);
        label.setIcon(TEST_ICON);
        label.setHorizontalAlignment(JLabel.RIGHT);
        StaticTextData td = (StaticTextData) handler.getData();
        assertEquals("Wrong text data", TEST_TEXT, td.getText());
        assertEquals("Wrong icon data", TEST_ICON, td.getIcon());
        assertEquals("Wrong alignment data", TextIconAlignment.RIGHT, td
                .getAlignment());
    }

    /**
     * Tests that the returned data object is not connected to the component.
     * This means, changing its properties won't affect the label.
     */
    public void testGetDataUnmodifiable()
    {
        label.setText(TEST_TEXT);
        StaticTextData td = (StaticTextData) handler.getData();
        final String newText = "A new test text";
        td.setText(newText);
        assertEquals("New text not set", newText, td.getText());
        assertEquals("Label's text was changed", TEST_TEXT, label.getText());
    }

    /**
     * Tests setting all properties of the label.
     */
    public void testSetDataFullData()
    {
        StaticTextData td = (StaticTextData) handler.getData();
        td.setText(TEST_TEXT);
        td.setIcon(TEST_ICON);
        td.setAlignment(TextIconAlignment.RIGHT);
        handler.setData(td);
        assertEquals("Text was not set", TEST_TEXT, label.getText());
        assertEquals("Icon was not set", TEST_ICON, label.getIcon());
        assertEquals("Alignment was not set", JLabel.RIGHT, label
                .getHorizontalAlignment());
    }

    /**
     * Tests resetting all properties of the label.
     */
    public void testSetDataNull()
    {
        label.setHorizontalAlignment(JLabel.RIGHT);
        label.setIcon(TEST_ICON);
        label.setText(TEST_TEXT);
        handler.setData(null);
        assertEquals("Default alignment not set", JLabel.LEFT, label
                .getHorizontalAlignment());
        assertNull("Text not reset", label.getText());
        assertNull("Icon not reset", label.getIcon());
    }

    // static initializer; loads the test icon
    static
    {
        TEST_ICON = new ImageIcon(TestSwingStaticTextComponentHandler.class
                .getClassLoader().getResource(ICON_RES));
    }
}

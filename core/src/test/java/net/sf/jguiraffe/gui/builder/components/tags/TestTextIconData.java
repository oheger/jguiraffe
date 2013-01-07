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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment;

/**
 * Test class for TextIconData.
 *
 * @author Oliver Heger
 * @version $Id: TestTextIconData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTextIconData extends AbstractTagTest
{
    /** The object to be tested. */
    private TextIconData data;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        LabelTag tag = new LabelTag();
        tag.setContext(context);
        data = new TextIconData(tag);
    }

    /**
     * Tests whether a new instance is correctly initialized.
     */
    public void testNewInstance()
    {
        assertNull("An icon is set", data.getIcon());
        assertNull("A resource group is set", data.getResgrp());
        assertNull("A resource name is set", data.getTextres());
        assertNull("A text is set", data.getText());
        assertNull("A caption is available", data.getCaption());
        assertFalse("Data is defined", data.isDefined());
        assertEquals("Wrong default alignment", TextIconAlignment.LEFT,
                data.getAlignment());
        assertEquals("Wrong alignment name", TextIconAlignment.LEFT
                .name(), data.getAlignmentString());
    }

    /**
     * Tests setting the alignment.
     */
    public void testSetAlignmentString()
    {
        data.setAlignmentString(TextIconAlignment.CENTER.name());
        assertEquals(TextIconAlignment.CENTER, data.getAlignment());
        data.setAlignmentString(TextIconAlignment.RIGHT.name()
                .toLowerCase());
        assertEquals(TextIconAlignment.RIGHT, data.getAlignment());
    }

    /**
     * Tests setting the alignment to null. This should cause an exception.
     */
    public void testSetAlignmentNull()
    {
        try
        {
            data.setAlignment(null);
            fail("Could set a null alignment!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests setting the alignment string to null. This should cause an
     * exception.
     */
    public void testSetAlignmentStringNull()
    {
        try
        {
            data.setAlignmentString(null);
            fail("Could set a null alignment string!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tries to set an invalid alignment string. This should cause an exception.
     */
    public void testSetAlignmentStringInvalid()
    {
        try
        {
            data.setAlignmentString("invalid alignment string");
            fail("Could set an invalid alignment string!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests fetching the caption.
     */
    public void testGetCaption()
    {
        data.setText("A text");
        assertEquals("Wrong caption", "A text", data.getCaption());
    }

    /**
     * Tests fetching the caption when it is defined using resource IDs.
     */
    public void testGetCaptionResource()
    {
        data.setTextres("LABEL_CAPTION1");
        assertEquals("Wrong caption from resID", "Label1", data.getCaption());
        data.setResgrp(DEF_RES_GRP);
        assertEquals("Wrong caption from resID and group", "Label1", data
                .getCaption());
    }

    /**
     * Tests fetching the mnemonic.
     */
    public void testGetMnemonic()
    {
        data.setMnemonicKey("c");
        assertEquals("Wrong mnemonic", 'c', data.getMnemonic());
    }

    /**
     * Tests fetching the mnemonic when it is defined using resource IDs.
     */
    public void testGetMnemonicResource()
    {
        data.setMnemonicResID("LABEL_CAPTION1");
        assertEquals("Wrong mnemonic from resID", 'L', data.getMnemonic());
        data.setResgrp(DEF_RES_GRP);
        assertEquals("Wrong mnemonic from resID and group", 'L', data
                .getMnemonic());
    }
}

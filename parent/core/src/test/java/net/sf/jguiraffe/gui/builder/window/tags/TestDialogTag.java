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
package net.sf.jguiraffe.gui.builder.window.tags;

import net.sf.jguiraffe.gui.builder.window.WindowManagerImpl;

import org.apache.commons.jelly.JellyTagException;

/**
 * Test class for DialogTag.
 *
 * @author Oliver Heger
 * @version $Id: TestDialogTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDialogTag extends AbstractWindowTypeTest
{
    /** Constant for the non modal window builder.*/
    private static final String BUILDER_NONMODAL = "NONMODAL";

    /** Constant for the auto-close window builder. */
    private static final String BUILDER_AUTOCLOSE = "AUTOCLOSE";

    /** Constant for the close on escape window builder. */
    private static final String BUILDER_CLOSEESC = "CLOSEESC";

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        setWindowType(WindowManagerImpl.WINDOW_DIALOG);
    }

    /**
     * Tests creating a non modal dialog.
     */
    public void testNonModalDialog() throws Exception
    {
        executeWindowTest(BUILDER_NONMODAL, "NONMODAL ]" + CONTAINER_EMPTY);
    }

    /**
     * Tests the auto-close flag.
     */
    public void testAutoClose() throws Exception
    {
        executeWindowTest(BUILDER_AUTOCLOSE, "NOAUTOCLOSE ]" + CONTAINER_EMPTY);
    }

    /**
     * Tests whether the close on escape flag is set per default.
     */
    public void testDefaultCloseOnEsc() throws Exception
    {
        executeWindowTest(BUILDER_CLOSEESC, "CLOSEONESC ]" + CONTAINER_EMPTY);
    }

    /**
     * Tests whether the correct component builder data object is returned. This
     * is actually a test of the base tag handler class.
     */
    public void testGetComponentBuilderData() throws JellyTagException
    {
        DialogTag tag = new DialogTag();
        tag.setContext(context);
        assertSame("Wrong builder data", builderData, tag
                .getComponentBuilderData());
    }
}

/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action.tags;

import net.sf.jguiraffe.gui.builder.action.ActionManagerImpl;
import net.sf.jguiraffe.gui.builder.action.PopupMenuHandler;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;

import org.easymock.EasyMock;

/**
 * A test class for PopupHandlerTag that executes a Jelly script. This tests
 * whether the tag can be correctly accessed in a Jelly script.
 *
 * @author Oliver Heger
 * @version $Id: TestPopupHandlerTagScript.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestPopupHandlerTagScript extends AbstractActionTagTest
{
    /** The name of the test script. */
    private static final String SCRIPT = "popup";

    /** The name of the handler bean. */
    private static final String BEAN_HANDLER = "menuHandler";

    /**
     * Tests executing the tag in a script.
     */
    public void testExecuteTag() throws Exception
    {
        PopupMenuHandler handler = EasyMock.createMock(PopupMenuHandler.class);
        EasyMock.replay(handler);
        context.setVariable(BEAN_HANDLER, handler);
        executeScript(SCRIPT);
        builderData.invokeCallBacks();
        checkResult("Container: ROOT { TEXTAREA [ NAME = text SCROLLWIDTH = "
                + NumberWithUnit.ZERO + " SCROLLHEIGHT = "
                + NumberWithUnit.ZERO + " ]"
                + ActionManagerImpl.popupHandlerText(handler, builderData)
                + " }");
    }
}

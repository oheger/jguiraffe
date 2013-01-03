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
package net.sf.jguiraffe.gui.builder.utils;

import static org.junit.Assert.assertEquals;
import net.sf.jguiraffe.gui.builder.window.Window;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code NullMessageOutput}.
 *
 * @author Oliver Heger
 * @version $Id: TestNullMessageOutput.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestNullMessageOutput
{
    /** The instance to be tested. */
    private NullMessageOutput out;

    @Before
    public void setUp() throws Exception
    {
        out = new NullMessageOutput();
    }

    /**
     * Tests the method for displaying messages.
     */
    @Test
    public void testShow()
    {
        Window parent = EasyMock.createMock(Window.class);
        EasyMock.replay(parent);
        assertEquals("Wrong result", MessageOutput.RET_CANCEL, out.show(parent,
                "A message", "A title", MessageOutput.MESSAGE_INFO,
                MessageOutput.BTN_YES_NO));
        EasyMock.verify(parent);
    }
}

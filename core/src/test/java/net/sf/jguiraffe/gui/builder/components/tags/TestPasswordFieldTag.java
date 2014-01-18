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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.assertEquals;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code PasswordFieldTag}.
 *
 * @author Oliver Heger
 * @version $Id: TestPasswordFieldTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestPasswordFieldTag
{
    /** The tag to be tested. */
    private PasswordFieldTag tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new PasswordFieldTag();
    }

    /**
     * Tests whether the correct component handler is created.
     */
    @Test
    public void testCreateComponentHandler() throws FormBuilderException,
            JellyTagException
    {
        ComponentManager compMan = EasyMock.createMock(ComponentManager.class);
        ComponentHandler<?> handler1 = EasyMock
                .createMock(ComponentHandler.class);
        ComponentHandler<?> handler2 = EasyMock
                .createMock(ComponentHandler.class);
        compMan.createPasswordField(tag, true);
        EasyMock.expectLastCall().andReturn(handler1);
        compMan.createPasswordField(tag, false);
        EasyMock.expectLastCall().andReturn(handler2);
        EasyMock.replay(compMan, handler1, handler2);
        assertEquals("Wrong handler (1)", handler1, tag.createComponentHandler(
                compMan, true));
        assertEquals("Wrong handler (2)", handler2, tag.createComponentHandler(
                compMan, false));
        EasyMock.verify(compMan, handler1, handler2);
    }
}

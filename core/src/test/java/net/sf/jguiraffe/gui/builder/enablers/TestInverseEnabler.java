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
package net.sf.jguiraffe.gui.builder.enablers;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@link InverseEnabler}.
 *
 * @author Oliver Heger
 * @version $Id: TestInverseEnabler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestInverseEnabler
{
    /**
     * Tests creating an instance without a wrapped enabler. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoWrappedEnabler()
    {
        new InverseEnabler(null);
    }

    /**
     * Helper method for testing changes of the element state.
     *
     * @param state the new state
     */
    private void checkSetElementState(boolean state)
            throws FormBuilderException
    {
        ElementEnabler wrapped = EasyMock.createMock(ElementEnabler.class);
        ComponentBuilderData cmpData = new ComponentBuilderData();
        wrapped.setEnabledState(cmpData, !state);
        EasyMock.replay(wrapped);
        InverseEnabler en = new InverseEnabler(wrapped);
        en.setEnabledState(cmpData, state);
        EasyMock.verify(wrapped);
    }

    /**
     * Tests setting the enabled state to true.
     */
    @Test
    public void testSetElementStateTrue() throws FormBuilderException
    {
        checkSetElementState(true);
    }

    /**
     * Tests setting the enabled state to false.
     */
    @Test
    public void testSetElementStateFalse() throws FormBuilderException
    {
        checkSetElementState(false);
    }
}

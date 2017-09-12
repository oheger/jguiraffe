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
package net.sf.jguiraffe.gui.builder.enablers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@link ChainElementEnabler}.
 *
 * @author Oliver Heger
 * @version $Id: TestChainElementEnabler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestChainElementEnabler
{
    /** Constant for the number of child enablers. */
    private static final int CHILD_COUNT = 5;

    /** A list with child enablers. */
    private List<ElementEnabler> childEnablers;

    /**
     * Returns the child enablers. The list is created on first access.
     *
     * @return a list with mock objects for child enablers
     */
    private List<ElementEnabler> getChildEnablers()
    {
        if (childEnablers == null)
        {
            childEnablers = new ArrayList<ElementEnabler>(CHILD_COUNT);
            for (int i = 0; i < CHILD_COUNT; i++)
            {
                childEnablers.add(EasyMock.createMock(ElementEnabler.class));
            }
        }

        return childEnablers;
    }

    /**
     * Tests creating an instance without child elements. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoChildren()
    {
        new ChainElementEnabler(null);
    }

    /**
     * Tests creating an instance when one of the children is null. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullChild()
    {
        List<ElementEnabler> children = getChildEnablers();
        children.add(null);
        new ChainElementEnabler(children);
    }

    /**
     * Tests whether a defensive copy is created of the list of children.
     */
    @Test
    public void testInitDefensiveCopy()
    {
        List<ElementEnabler> children = getChildEnablers();
        ChainElementEnabler enabler = new ChainElementEnabler(children);
        children.add(EasyMock.createMock(ElementEnabler.class));
        assertEquals("Children modified", CHILD_COUNT, enabler
                .getChildEnablers().size());
    }

    /**
     * Tests that the collection returned by getChildEnablers() cannot be
     * modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetChildEnablersModify()
    {
        ChainElementEnabler enabler = new ChainElementEnabler(
                getChildEnablers());
        enabler.getChildEnablers().clear();
    }

    /**
     * Tests setting the enabled state.
     */
    @Test
    public void testSetEnabledState() throws FormBuilderException
    {
        List<ElementEnabler> children = getChildEnablers();
        ChainElementEnabler enabler = new ChainElementEnabler(children);
        ComponentBuilderData compData = new ComponentBuilderData();
        for (ElementEnabler en : children)
        {
            en.setEnabledState(compData, true);
        }
        EasyMock.replay(children.toArray());
        enabler.setEnabledState(compData, true);
        EasyMock.verify(children.toArray());
    }
}

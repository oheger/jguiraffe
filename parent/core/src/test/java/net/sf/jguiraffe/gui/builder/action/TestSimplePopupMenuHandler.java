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
package net.sf.jguiraffe.gui.builder.action;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;

import org.apache.commons.lang.mutable.MutableInt;
import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for SimplePopupMenuHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSimplePopupMenuHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSimplePopupMenuHandler
{
    /** Constant for the default number of test actions. */
    private static final int DEF_ACTION_COUNT = 10;

    /**
     * Creates the specified number of action mocks and adds them to the given
     * collection.
     *
     * @param elems the target collection
     * @param count the number of mocks to create
     */
    private void addActionMocks(Collection<Object> elems, int count)
    {
        for (int i = 0; i < count; i++)
        {
            elems.add(EasyMock.createNiceMock(FormAction.class));
        }
    }

    /**
     * Creates a collection with action mock objects.
     *
     * @param count the number of mocks to create
     * @return the collection with the action mocks
     */
    private List<Object> setUpElements(int count)
    {
        List<Object> mocks = new ArrayList<Object>();
        addActionMocks(mocks, count);
        return mocks;
    }

    /**
     * Tests creating an instance with no elements. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoElements()
    {
        new SimplePopupMenuHandler(null);
    }

    /**
     * Tests whether a defensive copy is performed on initialization.
     */
    @Test
    public void testInitDefensiveCopy()
    {
        Collection<Object> elems = setUpElements(DEF_ACTION_COUNT);
        SimplePopupMenuHandler handler = new SimplePopupMenuHandler(elems);
        addActionMocks(elems, 5);
        assertEquals("Collection was modified", DEF_ACTION_COUNT, handler
                .getMenuItems().size());
    }

    /**
     * Tests whether the menu items can be modified. This should not be the
     * case.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetMenuItemsModify()
    {
        SimplePopupMenuHandler handler = new SimplePopupMenuHandler(
                setUpElements(DEF_ACTION_COUNT));
        handler.getMenuItems().clear();
    }

    /**
     * Tests the checkMenuItems() method if only valid elements are involved.
     */
    @Test
    public void testCheckMenuItems() throws FormActionException
    {
        Collection<Object> elems = setUpElements(DEF_ACTION_COUNT);
        elems.add(null);
        elems.add(new SimplePopupMenuHandler(setUpElements(DEF_ACTION_COUNT)));
        final MutableInt checkElementCount = new MutableInt();
        SimplePopupMenuHandler handler = new SimplePopupMenuHandler(elems)
        {
            @Override
            protected void checkMenuElement(Object element)
                    throws FormActionException
            {
                checkElementCount.increment();
                super.checkMenuElement(element);
            }
        };
        handler.checkMenuItems();
        assertEquals("Wrong number of checkElement invocations", elems.size(),
                checkElementCount.intValue());
    }

    /**
     * Tests the checkMenuItems() method if an invalid element is contained.
     */
    @Test(expected = FormActionException.class)
    public void testCheckMenuItemsInvalid() throws FormActionException
    {
        Collection<Object> elems = setUpElements(DEF_ACTION_COUNT);
        elems.add(new SimplePopupMenuHandler(setUpElements(DEF_ACTION_COUNT)));
        elems.add(this);
        SimplePopupMenuHandler handler = new SimplePopupMenuHandler(elems);
        handler.checkMenuItems();
    }

    /**
     * Tests constructing the menu. This method checks whether the correct
     * addXXX() methods are called.
     */
    @Test
    public void testConstructPopup() throws FormActionException
    {
        Collection<Object> elems = setUpElements(DEF_ACTION_COUNT);
        elems.add(new SimplePopupMenuHandler(setUpElements(DEF_ACTION_COUNT)));
        elems.add(null);
        addActionMocks(elems, DEF_ACTION_COUNT);
        elems.add(new SimplePopupMenuHandler(setUpElements(DEF_ACTION_COUNT)));
        final PopupMenuBuilder popupBuilder = EasyMock
                .createMock(PopupMenuBuilder.class);
        final ComponentBuilderData componentData = new ComponentBuilderData();
        final MutableInt actionCount = new MutableInt();
        final MutableInt menuCount = new MutableInt();
        final MutableInt sepCount = new MutableInt();
        EasyMock.expect(popupBuilder.create()).andReturn(null);
        EasyMock.replay(popupBuilder);
        SimplePopupMenuHandler handler = new SimplePopupMenuHandler(elems)
        {
            @Override
            protected void addAction(PopupMenuBuilder builder,
                    ComponentBuilderData compData, FormAction action)
                    throws FormActionException
            {
                assertEquals("Wrong builder", popupBuilder, builder);
                assertEquals("Wrong compData", componentData, compData);
                actionCount.increment();
            }

            @Override
            protected void addSubMenu(PopupMenuBuilder builder,
                    ComponentBuilderData compData,
                    SimplePopupMenuHandler subHandler)
                    throws FormActionException
            {
                assertEquals("Wrong builder", popupBuilder, builder);
                assertEquals("Wrong compData", componentData, compData);
                menuCount.increment();
            }

            @Override
            protected void addSeparator(PopupMenuBuilder builder,
                    ComponentBuilderData compData) throws FormActionException
            {
                assertEquals("Wrong builder", popupBuilder, builder);
                assertEquals("Wrong compData", componentData, compData);
                sepCount.increment();
            }
        };
        handler.constructPopup(popupBuilder, componentData);
        assertEquals("Wrong action count", 2 * DEF_ACTION_COUNT, actionCount
                .intValue());
        assertEquals("Wrong menu count", 2, menuCount.intValue());
        assertEquals("Wrong separator count", 1, sepCount.intValue());
        EasyMock.verify(popupBuilder);
    }

    /**
     * Tests querying the constructed menu.
     */
    @Test
    public void testGetConstructedMenu() throws FormActionException
    {
        List<Object> elems = setUpElements(1);
        PopupMenuBuilder builder = EasyMock.createMock(PopupMenuBuilder.class);
        EasyMock.expect(builder.addAction((FormAction) elems.get(0)))
                .andReturn(builder);
        final Object menu = new Object();
        EasyMock.expect(builder.create()).andReturn(menu);
        EasyMock.replay(builder);
        SimplePopupMenuHandler handler = new SimplePopupMenuHandler(elems);
        handler.constructPopup(builder, new ComponentBuilderData());
        assertEquals("Wrong menu object", menu, handler.getConstructedMenu());
        EasyMock.verify(builder);
    }

    /**
     * Tests adding an unsupported menu element. This should cause an exception.
     */
    @Test(expected = FormActionException.class)
    public void testAddMenuElementInvalid() throws FormActionException
    {
        SimplePopupMenuHandler handler = new SimplePopupMenuHandler(
                setUpElements(DEF_ACTION_COUNT));
        handler.addMenuElement(EasyMock.createNiceMock(PopupMenuBuilder.class),
                new ComponentBuilderData(), this);
    }

    /**
     * Tests adding an action to the menu.
     */
    @Test
    public void testAddMenuElementAction() throws FormActionException
    {
        PopupMenuBuilder builder = EasyMock.createMock(PopupMenuBuilder.class);
        FormAction action = EasyMock.createMock(FormAction.class);
        EasyMock.expect(builder.addAction(action)).andReturn(builder);
        EasyMock.replay(builder, action);
        SimplePopupMenuHandler handler = new SimplePopupMenuHandler(
                setUpElements(1));
        handler.addMenuElement(builder, new ComponentBuilderData(), action);
        EasyMock.verify(builder, action);
    }

    /**
     * Tests adding a separator to the menu.
     */
    @Test
    public void testAddMenuElementSeparator() throws FormActionException
    {
        PopupMenuBuilder builder = EasyMock.createMock(PopupMenuBuilder.class);
        EasyMock.expect(builder.addSeparator()).andReturn(builder);
        EasyMock.replay(builder);
        SimplePopupMenuHandler handler = new SimplePopupMenuHandler(
                setUpElements(1));
        handler.addMenuElement(builder, new ComponentBuilderData(), null);
        EasyMock.verify(builder);
    }

    /**
     * Tests adding a sub menu to the menu.
     */
    @Test
    public void testAddMenuElementSubMenu() throws FormActionException
    {
        PopupMenuBuilder builder = EasyMock
                .createStrictMock(PopupMenuBuilder.class);
        PopupMenuBuilder subBuilder = EasyMock
                .createStrictMock(PopupMenuBuilder.class);
        SimplePopupMenuHandler subHandler = new SimplePopupMenuHandler(
                setUpElements(DEF_ACTION_COUNT));
        FormAction act1 = EasyMock.createMock(FormAction.class);
        FormAction act2 = EasyMock.createMock(FormAction.class);
        Collection<Object> elems = new ArrayList<Object>();
        elems.add(act1);
        elems.add(subHandler);
        elems.add(act2);
        EasyMock.expect(builder.addAction(act1)).andReturn(builder);
        EasyMock.expect(builder.subMenuBuilder(subHandler)).andReturn(
                subBuilder);
        for (Object elem : subHandler.getMenuItems())
        {
            EasyMock.expect(subBuilder.addAction((FormAction) elem)).andReturn(
                    subBuilder);
        }
        final Object subMenu = new Object();
        EasyMock.expect(subBuilder.create()).andReturn(subMenu);
        EasyMock.expect(builder.addSubMenu(subMenu)).andReturn(builder);
        EasyMock.expect(builder.addAction(act2)).andReturn(builder);
        EasyMock.expect(builder.create()).andReturn(null);
        EasyMock.replay(builder, subBuilder);
        SimplePopupMenuHandler handler = new SimplePopupMenuHandler(elems);
        handler.constructPopup(builder, new ComponentBuilderData());
        EasyMock.verify(builder, subBuilder);
    }
}

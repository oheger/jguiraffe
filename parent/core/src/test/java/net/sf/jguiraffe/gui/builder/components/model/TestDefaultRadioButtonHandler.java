/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code DefaultRadioButtonHandler}. The base class {@code
 * AbstractRadioButtonHandler} is tested as well.
 *
 * @author Oliver Heger
 * @version $Id: TestDefaultRadioButtonHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDefaultRadioButtonHandler
{
    /** Constant for the name prefix for radio buttons. */
    private static final String RADIO_PREFIX = "testRadioButton";

    /** Constant for the index of the selected radio button. */
    private static final Integer SELECTED_INDEX = 1;

    /** Constant for the number of radio buttons in the group. */
    private static final int RADIO_COUNT = 4;

    /** A list with mock handlers for the test radio buttons. */
    private List<ComponentHandler<Boolean>> radios;

    /** The handler to be tested. */
    private DefaultRadioButtonHandler handler;

    @Before
    public void setUp() throws Exception
    {
        handler = new DefaultRadioButtonHandler();
    }

    /**
     * Creates a mock object for a child handler.
     *
     * @return the mock
     */
    private static ComponentHandler<Boolean> createChildHandler()
    {
        @SuppressWarnings("unchecked")
        ComponentHandler<Boolean> child = EasyMock
                .createMock(ComponentHandler.class);
        return child;
    }

    /**
     * Creates mock objects for the radio button handlers and adds them to the
     * radio handler.
     */
    private void setUpRadios()
    {
        radios = new ArrayList<ComponentHandler<Boolean>>(RADIO_COUNT);
        for (int i = 0; i < RADIO_COUNT; i++)
        {
            ComponentHandler<Boolean> child = createChildHandler();
            radios.add(child);
            handler.addHandler(RADIO_PREFIX + i, child);
        }
    }

    /**
     * Replays the mocks for the radio buttons.
     */
    private void replayRadios()
    {
        EasyMock.replay(radios.toArray());
    }

    /**
     * Verifies the mocks for the radio buttons.
     */
    private void verifyRadios()
    {
        EasyMock.verify(radios.toArray());
    }

    /**
     * Prepares the radio button mocks to expect a getData() invocation.
     *
     * @param selectedIdx the index of the selected radio button
     */
    private void expectGetData(int selectedIdx)
    {
        for (int i = 0; i <= Math.min(selectedIdx, RADIO_COUNT - 1); i++)
        {
            EasyMock.expect(radios.get(i).getData()).andReturn(
                    Boolean.valueOf(i == selectedIdx));
        }
    }

    /**
     * Prepares the radio button mocks to expect a setData() invocation.
     *
     * @param selectedIdx the index of the selected radio button
     */
    private void expectSetData(int selectedIdx)
    {
        for (int i = 0; i < RADIO_COUNT; i++)
        {
            radios.get(i).setData(Boolean.valueOf(i == selectedIdx));
        }
    }

    /**
     * Tests whether the correct data type is returned.
     */
    @Test
    public void testGetType()
    {
        assertEquals("Wrong type", Integer.class, handler.getType());
    }

    /**
     * Tests whether data of the handler can be queried if a radio button is
     * selected.
     */
    @Test
    public void testGetDataSelected()
    {
        setUpRadios();
        expectGetData(SELECTED_INDEX.intValue());
        replayRadios();
        assertEquals("Wrong data", SELECTED_INDEX, handler.getData());
        verifyRadios();
    }

    /**
     * Tests getData() if none of the child radio buttons is selected.
     */
    @Test
    public void testGetDataUnselected()
    {
        setUpRadios();
        expectGetData(Integer.MAX_VALUE);
        replayRadios();
        assertNull("Wrong data", handler.getData());
        verifyRadios();
    }

    /**
     * Tests getData() if a child handler returns null.
     */
    @Test
    public void testGetDataNull()
    {
        setUpRadios();
        EasyMock.expect(radios.get(0).getData()).andReturn(null);
        EasyMock.expect(radios.get(1).getData()).andReturn(Boolean.TRUE);
        replayRadios();
        assertEquals("Wrong data", 1, handler.getData().intValue());
        verifyRadios();
    }

    /**
     * Tests setData() if a valid selected index is passed in.
     */
    @Test
    public void testSetDataIndex()
    {
        setUpRadios();
        expectSetData(SELECTED_INDEX.intValue());
        replayRadios();
        handler.setData(SELECTED_INDEX);
        verifyRadios();
    }

    /**
     * Tests setData() if an invalid index is passed in.
     */
    @Test
    public void testSetDataInvalidIndex()
    {
        setUpRadios();
        expectSetData(Integer.MAX_VALUE);
        replayRadios();
        handler.setData(-1);
        verifyRadios();
    }

    /**
     * Tests setData() for a null argument.
     */
    @Test
    public void testSetDataNull()
    {
        setUpRadios();
        expectSetData(Integer.MAX_VALUE);
        replayRadios();
        handler.setData(null);
        verifyRadios();
    }
}

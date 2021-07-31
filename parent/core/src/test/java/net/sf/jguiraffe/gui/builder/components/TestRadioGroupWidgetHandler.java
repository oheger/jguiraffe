/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@code RadioGroupWidgetHandler}.
 */
public class TestRadioGroupWidgetHandler
{
    /** Some object representing a button group. */
    private static final Object BUTTON_GROUP = new Object();

    /** Constant for a test tool tip separator. */
    private static final String TIP_SEPARATOR = "----";

    /**
     * Returns a list with mock widget handlers simulating radio buttons.
     *
     * @return the list with mock widget handlers
     */
    private static List<WidgetHandler> createMockRadioHandlers()
    {
        final int count = 3;
        List<WidgetHandler> handlers = new ArrayList<WidgetHandler>(count);
        for (int i = 0; i < count; i++)
        {
            handlers.add(EasyMock.createMock(WidgetHandler.class));
        }
        return handlers;
    }

    /**
     * Creates a test instance of the widget handler with default properties.
     *
     * @return the new test instance
     */
    private static RadioGroupWidgetHandler createGroupHandler()
    {
        return new RadioGroupWidgetHandler(BUTTON_GROUP,
                createMockRadioHandlers(), TIP_SEPARATOR);
    }

    /**
     * Convenience function to convert the collection of radio button handlers
     * to an array.
     *
     * @param handlers the collection of handlers
     * @return the corresponding array with handlers
     */
    private static WidgetHandler[] radioArray(List<WidgetHandler> handlers)
    {
        return handlers.toArray(new WidgetHandler[0]);
    }

    /**
     * Convenience function to return the first radio button handler in the
     * group managed by the given handler.
     *
     * @param groupHandler the handler
     * @return the handler of the first radio button
     */
    private static WidgetHandler firstRadio(
            RadioGroupWidgetHandler groupHandler)
    {
        return groupHandler.getRadioButtons().get(0);
    }

    /**
     * Replays all mock handlers in the given collection.
     *
     * @param handlers the collection with mock handlers
     */
    private static void replayRadioHandlers(List<WidgetHandler> handlers)
    {
        EasyMock.replay((Object[]) radioArray(handlers));
    }

    /**
     * Replays the mocks for the child radio buttons of the given handler.
     *
     * @param groupHandler the handler
     */
    private static void replayGroup(RadioGroupWidgetHandler groupHandler)
    {
        replayRadioHandlers(groupHandler.getRadioButtons());
    }

    /**
     * Verifies all mock handlers in the given collection.
     *
     * @param handlers the collection of handlers
     */
    private static void verifyRadioHandlers(List<WidgetHandler> handlers)
    {
        EasyMock.verify((Object[]) radioArray(handlers));
    }

    /**
     * Verifies the mocks for the child radio buttons of the given handler.
     *
     * @param groupHandler the handler
     */
    private static void verifyGroup(RadioGroupWidgetHandler groupHandler)
    {
        verifyRadioHandlers(groupHandler.getRadioButtons());
    }

    /**
     * Tests that the list with radio buttons cannot be manipulated.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testListOfRadioButtonsIsUnmodifiable()
    {
        RadioGroupWidgetHandler groupHandler = createGroupHandler();

        groupHandler.getRadioButtons().clear();
    }

    /**
     * Tests that a defensive copy from the list with radio buttons passed to
     * the constructor is made.
     */
    @Test
    public void testDefensiveCopyIsCreatedFromConstructorParameter()
    {
        List<WidgetHandler> radios = createMockRadioHandlers();
        List<WidgetHandler> orgRadios = new ArrayList<WidgetHandler>(radios);
        RadioGroupWidgetHandler groupHandler = new RadioGroupWidgetHandler(
                BUTTON_GROUP, radios, TIP_SEPARATOR);

        radios.add(EasyMock.createMock(WidgetHandler.class));
        assertEquals("List was manipulated", orgRadios,
                groupHandler.getRadioButtons());
    }

    /**
     * Tests whether the correct widget is returned.
     */
    @Test
    public void testGetWidget()
    {
        RadioGroupWidgetHandler groupHandler = createGroupHandler();

        assertEquals("Wrong widget", BUTTON_GROUP, groupHandler.getWidget());
    }

    /**
     * Tests whether the visibility state is correctly returned.
     */
    @Test
    public void testIsVisible()
    {
        RadioGroupWidgetHandler groupHandler = createGroupHandler();
        EasyMock.expect(firstRadio(groupHandler).isVisible())
                .andReturn(Boolean.TRUE);
        replayGroup(groupHandler);

        assertTrue("Wrong result", groupHandler.isVisible());
        verifyGroup(groupHandler);
    }

    /**
     * Tests whether the visibility state can be set.
     */
    @Test
    public void testSetVisible()
    {
        RadioGroupWidgetHandler groupHandler = createGroupHandler();
        for (WidgetHandler h : groupHandler.getRadioButtons())
        {
            h.setVisible(false);
        }
        replayGroup(groupHandler);

        groupHandler.setVisible(false);
        verifyGroup(groupHandler);
    }

    /**
     * Tests whether the background color can be queried.
     */
    @Test
    public void testGetBackgroundColor()
    {
        final Color color = Color.newRGBInstance(1, 2, 3);
        RadioGroupWidgetHandler groupHandler = createGroupHandler();
        EasyMock.expect(firstRadio(groupHandler).getBackgroundColor())
                .andReturn(color);
        replayGroup(groupHandler);

        assertEquals("Wrong color", color, groupHandler.getBackgroundColor());
        verifyGroup(groupHandler);
    }

    /**
     * Tests whether the background color can be changed.
     */
    @Test
    public void testSetBackgroundColor()
    {
        final Color color = Color.newRGBInstance(3, 2, 1);
        RadioGroupWidgetHandler groupHandler = createGroupHandler();
        for (WidgetHandler h : groupHandler.getRadioButtons())
        {
            h.setBackgroundColor(color);
        }
        replayGroup(groupHandler);

        groupHandler.setBackgroundColor(color);
        verifyGroup(groupHandler);
    }

    /**
     * Tests whether the foreground color can be queried.
     */
    @Test
    public void testGetForegroundColor()
    {
        final Color color = Color.newRGBInstance(1, 2, 3);
        RadioGroupWidgetHandler groupHandler = createGroupHandler();
        EasyMock.expect(firstRadio(groupHandler).getForegroundColor())
                .andReturn(color);
        replayGroup(groupHandler);

        assertEquals("Wrong color", color, groupHandler.getForegroundColor());
        verifyGroup(groupHandler);
    }

    /**
     * Tests whether the foreground color can be changed.
     */
    @Test
    public void testSetForegroundColor()
    {
        final Color color = Color.newRGBInstance(3, 2, 1);
        RadioGroupWidgetHandler groupHandler = createGroupHandler();
        for (WidgetHandler h : groupHandler.getRadioButtons())
        {
            h.setForegroundColor(color);
        }
        replayGroup(groupHandler);

        groupHandler.setForegroundColor(color);
        verifyGroup(groupHandler);
    }

    /**
     * Tests whether the font can be queried.
     */
    @Test
    public void testGetFont()
    {
        final Object font = "someFont";
        RadioGroupWidgetHandler groupHandler = createGroupHandler();
        EasyMock.expect(firstRadio(groupHandler).getFont()).andReturn(font);
        replayGroup(groupHandler);

        assertEquals("Wrong font", font, groupHandler.getFont());
        verifyGroup(groupHandler);
    }

    /**
     * Tests whether the font can be set.
     */
    @Test
    public void testSetFont()
    {
        final Object font = "fontToSet";
        RadioGroupWidgetHandler groupHandler = createGroupHandler();
        for (WidgetHandler h : groupHandler.getRadioButtons())
        {
            h.setFont(font);
        }
        replayGroup(groupHandler);

        groupHandler.setFont(font);
        verifyGroup(groupHandler);
    }

    /**
     * Tests whether two null tool tips can be combined.
     */
    @Test
    public void testCombineToolTipsBothNull()
    {
        assertNull("Wrong result", RadioGroupWidgetHandler.combineToolTips(null,
                null, TIP_SEPARATOR));
    }

    /**
     * Tests combining tool tips if the second one is null.
     */
    @Test
    public void testCombineToolTipsTip2Null()
    {
        final String tip = "ToolTip";

        String combinedTip = RadioGroupWidgetHandler.combineToolTips(tip, null,
                TIP_SEPARATOR);
        assertEquals("Wrong combined tip", tip, combinedTip);
    }

    /**
     * Tests combining tool tips if the first one is null.
     */
    @Test
    public void testCombineToolTipsTip1Null()
    {
        final String tip = "ToolTip";

        String combinedTip = RadioGroupWidgetHandler.combineToolTips(null, tip,
                TIP_SEPARATOR);
        assertEquals("Wrong combined tip", tip, combinedTip);
    }

    /**
     * Tests whether two tool tips can be combined.
     */
    @Test
    public void testCombineToolTipsNotNull()
    {
        final String tip1 = "firstTip";
        final String tip2 = "secondTip";

        String combinedTip = RadioGroupWidgetHandler.combineToolTips(tip1, tip2,
                TIP_SEPARATOR);
        assertEquals("Wrong combined tip", tip1 + TIP_SEPARATOR + tip2,
                combinedTip);
    }

    /**
     * Tests that a tool tip can be set for the group.
     */
    @Test
    public void testSetToolTipInitially()
    {
        final String tip = "newToolTip";
        WidgetHandler radio = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(radio.getToolTip()).andReturn(null);
        radio.setToolTip(tip);
        RadioGroupWidgetHandler groupHandler = new RadioGroupWidgetHandler(
                BUTTON_GROUP, Collections.singleton(radio), TIP_SEPARATOR);
        replayGroup(groupHandler);

        groupHandler.setToolTip(tip);
        assertEquals("Wrong group tip", tip, groupHandler.getToolTip());
        verifyGroup(groupHandler);
    }

    /**
     * Tests whether the group tool tip can be changed multiple times.
     */
    @Test
    public void testSetToolTipMultipleTimes()
    {
        final String radioTip = "This is my radio";
        final String groupTip1 = "First group tip";
        final String groupTip2 = "Second group tip";
        WidgetHandler radio = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(radio.getToolTip()).andReturn(radioTip).anyTimes();
        radio.setToolTip(radioTip + TIP_SEPARATOR + groupTip1);
        radio.setToolTip(radioTip + TIP_SEPARATOR + groupTip2);
        RadioGroupWidgetHandler groupHandler = new RadioGroupWidgetHandler(
                BUTTON_GROUP, Collections.singleton(radio), TIP_SEPARATOR);
        replayGroup(groupHandler);

        groupHandler.setToolTip(groupTip1);
        groupHandler.setToolTip(groupTip2);
        verifyGroup(groupHandler);
    }

    /**
     * Tests that a tool tip for the group can be set and removed again later.
     */
    @Test
    public void testGroupToolTipCanBeRemoved()
    {
        final String radioTip1 = "This is my radio";
        final String radioTip2 = "This is my other radio";
        final String groupTip = "First group tip";
        WidgetHandler radio1 = EasyMock.createMock(WidgetHandler.class);
        WidgetHandler radio2 = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(radio1.getToolTip()).andReturn(radioTip1).anyTimes();
        EasyMock.expect(radio2.getToolTip()).andReturn(radioTip2).anyTimes();
        radio1.setToolTip(radioTip1 + TIP_SEPARATOR + groupTip);
        radio1.setToolTip(radioTip1);
        radio2.setToolTip(radioTip2 + TIP_SEPARATOR + groupTip);
        radio2.setToolTip(radioTip2);
        RadioGroupWidgetHandler groupHandler = new RadioGroupWidgetHandler(
                BUTTON_GROUP, Arrays.asList(radio1, radio2), TIP_SEPARATOR);
        replayGroup(groupHandler);

        groupHandler.setToolTip(groupTip);
        groupHandler.setToolTip(null);
        verifyGroup(groupHandler);
    }

    /**
     * Tests whether a change in the tool tip of the radio button is detected
     * when setting the group tool tip.
     */
    @Test
    public void testSetToolTipWithRadioTipChanged()
    {
        final String radio1Tip = "This is my radio (1)";
        final String radio2Tip1 = "This is my other radio (1)";
        final String radio2Tip2 = "This is my other radio (2)";
        final String groupTip1 = "First group tip";
        final String groupTip2 = "Second group tip";
        WidgetHandler radio1 = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(radio1.getToolTip()).andReturn(radio1Tip).anyTimes();
        WidgetHandler radio2 = EasyMock.createMock(WidgetHandler.class);
        EasyMock.expect(radio2.getToolTip()).andReturn(radio2Tip1)
                .andReturn(radio2Tip2);
        radio1.setToolTip(radio1Tip + TIP_SEPARATOR + groupTip1);
        radio1.setToolTip(radio1Tip + TIP_SEPARATOR + groupTip2);
        radio2.setToolTip(radio2Tip1 + TIP_SEPARATOR + groupTip1);
        radio2.setToolTip(radio2Tip2 + TIP_SEPARATOR + groupTip2);
        RadioGroupWidgetHandler groupHandler = new RadioGroupWidgetHandler(
                BUTTON_GROUP, Collections.singleton(radio1), TIP_SEPARATOR);
        replayGroup(groupHandler);

        groupHandler.setToolTip(groupTip1);
        groupHandler.setToolTip(groupTip2);
        verifyGroup(groupHandler);
    }
}

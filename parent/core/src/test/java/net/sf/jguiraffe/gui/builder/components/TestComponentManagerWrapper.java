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
package net.sf.jguiraffe.gui.builder.components;

import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.model.StaticTextData;
import net.sf.jguiraffe.gui.builder.components.tags.BorderLayoutTag;
import net.sf.jguiraffe.gui.builder.components.tags.ButtonLayoutTag;
import net.sf.jguiraffe.gui.builder.components.tags.ButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.CheckboxTag;
import net.sf.jguiraffe.gui.builder.components.tags.ComboBoxTag;
import net.sf.jguiraffe.gui.builder.components.tags.DesktopPanelTag;
import net.sf.jguiraffe.gui.builder.components.tags.FontTag;
import net.sf.jguiraffe.gui.builder.components.tags.LabelTag;
import net.sf.jguiraffe.gui.builder.components.tags.ListBoxTag;
import net.sf.jguiraffe.gui.builder.components.tags.PanelTag;
import net.sf.jguiraffe.gui.builder.components.tags.PasswordFieldTag;
import net.sf.jguiraffe.gui.builder.components.tags.PercentLayoutTag;
import net.sf.jguiraffe.gui.builder.components.tags.ProgressBarTag;
import net.sf.jguiraffe.gui.builder.components.tags.RadioButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.SliderTag;
import net.sf.jguiraffe.gui.builder.components.tags.SplitterTag;
import net.sf.jguiraffe.gui.builder.components.tags.StaticTextTag;
import net.sf.jguiraffe.gui.builder.components.tags.TabbedPaneTag;
import net.sf.jguiraffe.gui.builder.components.tags.TextAreaTag;
import net.sf.jguiraffe.gui.builder.components.tags.TextFieldTag;
import net.sf.jguiraffe.gui.builder.components.tags.ToggleButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.TreeTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManager;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.locators.Locator;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ComponentManagerWrapper}.
 *
 * @author Oliver Heger
 * @version $Id: TestComponentManagerWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestComponentManagerWrapper
{
    /** The mock object for the wrapped manager. */
    private ComponentManager wrappedManager;

    /** The wrapper to be tested. */
    private ComponentManagerWrapper wrapper;

    @Before
    public void setUp() throws Exception
    {
        wrappedManager = EasyMock.createMock(ComponentManager.class);
        wrapper = new ComponentManagerWrapper(wrappedManager)
        {
        };
    }

    /**
     * Replays the specified mock objects including the mock for the wrapped
     * manager.
     *
     * @param mocks the mocks to be replayed
     */
    private void replay(Object... mocks)
    {
        EasyMock.replay(mocks);
        EasyMock.replay(wrappedManager);
    }

    /**
     * Verifies the specified mock objects including the mock for the wrapped
     * manager.
     *
     * @param mocks the mocks to be verified
     */
    private void verify(Object... mocks)
    {
        EasyMock.verify(mocks);
        EasyMock.verify(wrappedManager);
    }

    /**
     * Helper method for creating a mock for a component handler of a specific
     * type.
     *
     * @param <T> the type of the handler
     * @param cls the data class of the handler
     * @return the mock handler
     */
    private <T> ComponentHandler<T> componentHandlerMock(Class<T> cls)
    {
        @SuppressWarnings("unchecked")
        ComponentHandler<T> handler =
                EasyMock.createMock(ComponentHandler.class);
        return handler;
    }

    /**
     * Tests whether the correct wrapped component manager is returned.
     */
    @Test
    public void testGetWrappedComponentManager()
    {
        assertSame("Wrong wrapped component manager", wrappedManager,
                wrapper.getWrappedComponentManager());
    }

    /**
     * Tests the addContainerComponent() implementation.
     */
    @Test
    public void testAddContainerComponent()
    {
        final Object container = new Object();
        final Object component = new Object();
        final Object constraints = new Object();
        wrappedManager.addContainerComponent(container, component, constraints);
        replay();
        wrapper.addContainerComponent(container, component, constraints);
        verify();
    }

    /**
     * Tests the setContainerLayout() implementation.
     */
    @Test
    public void testSetContainerLayout()
    {
        final Object container = new Object();
        final Object layout = new Object();
        wrappedManager.setContainerLayout(container, layout);
        replay();
        wrapper.setContainerLayout(container, layout);
        verify();
    }

    /**
     * Tests the createEventManager() implementation.
     */
    @Test
    public void testCreateEventManager()
    {
        PlatformEventManager evMan =
                EasyMock.createMock(PlatformEventManager.class);
        EasyMock.expect(wrappedManager.createEventManager()).andReturn(evMan);
        replay(evMan);
        assertSame("Wrong event manager", evMan, wrapper.createEventManager());
        verify(evMan);
    }

    /**
     * Tests the getWidgetHandlerFor() implementation.
     */
    @Test
    public void testGetWidgetHandlerFor()
    {
        WidgetHandler wh = EasyMock.createMock(WidgetHandler.class);
        final Object component = new Object();
        EasyMock.expect(wrappedManager.getWidgetHandlerFor(component))
                .andReturn(wh);
        replay(wh);
        assertSame("Wrong widget handler", wh,
                wrapper.getWidgetHandlerFor(component));
        verify(wh);
    }

    /**
     * Tests the createLabel() implementation.
     */
    @Test
    public void testCreateLabel() throws FormBuilderException
    {
        LabelTag tag = new LabelTag();
        final Object label = new Object();
        EasyMock.expect(wrappedManager.createLabel(tag, false))
                .andReturn(label);
        replay();
        assertSame("Wrong label", label, wrapper.createLabel(tag, false));
        verify();
    }

    /**
     * Tests the linkLabel() implementation.
     */
    @Test
    public void testLinkLabel() throws FormBuilderException
    {
        final Object label = new Object();
        final Object component = new Object();
        String text = "testText";
        wrappedManager.linkLabel(label, component, text);
        replay();
        wrapper.linkLabel(label, component, text);
        verify();
    }

    /**
     * Tests the createIcon() implementation.
     */
    @Test
    public void testCreateIcon() throws FormBuilderException
    {
        Locator loc = EasyMock.createMock(Locator.class);
        final Object icon = new Object();
        EasyMock.expect(wrappedManager.createIcon(loc)).andReturn(icon);
        replay(loc);
        assertSame("Wrong icon", icon, wrapper.createIcon(loc));
        verify(loc);
    }

    /**
     * Tests the createFont() implementation.
     */
    @Test
    public void testCreateFont() throws FormBuilderException
    {
        FontTag tag = new FontTag();
        final Object font = new Object();
        EasyMock.expect(wrappedManager.createFont(tag)).andReturn(font);
        replay();
        assertSame("Wrong font", font, wrapper.createFont(tag));
        verify();
    }

    /**
     * Tests the createPercentLayout() implementation.
     */
    @Test
    public void testCreatePercentLayout() throws FormBuilderException
    {
        PercentLayoutTag tag = new PercentLayoutTag();
        final Object layout = new Object();
        EasyMock.expect(wrappedManager.createPercentLayout(tag)).andReturn(
                layout);
        replay();
        assertSame("Wrong layout", layout, wrapper.createPercentLayout(tag));
        verify();
    }

    /**
     * Tests the createButtonLayout() implementation.
     */
    @Test
    public void testCreateButtonLayout() throws FormBuilderException
    {
        ButtonLayoutTag tag = new ButtonLayoutTag();
        final Object layout = new Object();
        EasyMock.expect(wrappedManager.createButtonLayout(tag)).andReturn(
                layout);
        replay();
        assertSame("Wrong layout", layout, wrapper.createButtonLayout(tag));
        verify();
    }

    /**
     * Tests the createBorderLayout() implementation.
     */
    @Test
    public void testCreateBorderLayout() throws FormBuilderException
    {
        BorderLayoutTag tag = new BorderLayoutTag();
        final Object layout = new Object();
        EasyMock.expect(wrappedManager.createBorderLayout(tag)).andReturn(
                layout);
        replay();
        assertSame("Wrong layout", layout, wrapper.createBorderLayout(tag));
        verify();
    }

    /**
     * Tests the createPanel() implementation.
     */
    @Test
    public void testCreatePanel() throws FormBuilderException
    {
        PanelTag tag = new PanelTag();
        final Object panel = new Object();
        EasyMock.expect(wrappedManager.createPanel(tag, true)).andReturn(panel);
        replay();
        assertSame("Wrong panel", panel, wrapper.createPanel(tag, true));
        verify();
    }

    /**
     * Tests the createDesktopPanel() implementation.
     */
    @Test
    public void testCreateDesktopPanel() throws FormBuilderException
    {
        DesktopPanelTag tag = new DesktopPanelTag();
        final Object panel = new Object();
        EasyMock.expect(wrappedManager.createDesktopPanel(tag, false))
                .andReturn(panel);
        replay();
        assertSame("Wrong desktop panel", panel,
                wrapper.createDesktopPanel(tag, false));
        verify();
    }

    /**
     * Tests the createSplitter() implementation.
     */
    @Test
    public void testCreateSplitter() throws FormBuilderException
    {
        SplitterTag tag = new SplitterTag();
        final Object splitter = new Object();
        EasyMock.expect(wrappedManager.createSplitter(tag, true)).andReturn(
                splitter);
        replay();
        assertSame("Wrong splitter", splitter,
                wrapper.createSplitter(tag, true));
        verify();
    }

    /**
     * Tests the createRadioGroup() implementation.
     */
    @Test
    public void testCreateRadioGroup() throws FormBuilderException
    {
        Map<String, Object> buttons = new HashMap<String, Object>();
        final Object group = new Object();
        EasyMock.expect(wrappedManager.createRadioGroup(buttons)).andReturn(
                group);
        replay();
        assertSame("Wrong radio group", group,
                wrapper.createRadioGroup(buttons));
        verify();
    }

    /**
     * Tests the createButton() implementation.
     */
    @Test
    public void testCreateButton() throws FormBuilderException
    {
        ComponentHandler<Boolean> ch = componentHandlerMock(Boolean.class);
        ButtonTag tag = new ButtonTag();
        EasyMock.expect(wrappedManager.createButton(tag, false)).andReturn(ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createButton(tag, false));
        verify(ch);
    }

    /**
     * Tests the createToggleButton() implementation.
     */
    @Test
    public void testCreateToggleButton() throws FormBuilderException
    {
        ComponentHandler<Boolean> ch = componentHandlerMock(Boolean.class);
        ToggleButtonTag tag = new ToggleButtonTag();
        EasyMock.expect(wrappedManager.createToggleButton(tag, true))
                .andReturn(ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createToggleButton(tag, true));
        verify(ch);
    }

    /**
     * Tests the createTextField() implementation.
     */
    @Test
    public void testCreateTextField() throws FormBuilderException
    {
        ComponentHandler<String> ch = componentHandlerMock(String.class);
        TextFieldTag tag = new TextFieldTag();
        EasyMock.expect(wrappedManager.createTextField(tag, false)).andReturn(
                ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createTextField(tag, false));
        verify(ch);
    }

    /**
     * Tests the createTextArea() implementation.
     */
    @Test
    public void testCreateTextArea() throws FormBuilderException
    {
        ComponentHandler<String> ch = componentHandlerMock(String.class);
        TextAreaTag tag = new TextAreaTag();
        EasyMock.expect(wrappedManager.createTextArea(tag, false))
                .andReturn(ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createTextArea(tag, false));
        verify(ch);
    }

    /**
     * Tests the createPasswordField() implementation.
     */
    @Test
    public void testCreatePasswordField() throws FormBuilderException
    {
        ComponentHandler<String> ch = componentHandlerMock(String.class);
        PasswordFieldTag tag = new PasswordFieldTag();
        EasyMock.expect(wrappedManager.createPasswordField(tag, false))
                .andReturn(ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createPasswordField(tag, false));
        verify(ch);
    }

    /**
     * Tests the createCheckbox() implementation.
     */
    @Test
    public void testCreateCheckbox() throws FormBuilderException
    {
        ComponentHandler<Boolean> ch = componentHandlerMock(Boolean.class);
        CheckboxTag tag = new CheckboxTag();
        EasyMock.expect(wrappedManager.createCheckbox(tag, true)).andReturn(ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createCheckbox(tag, true));
        verify(ch);
    }

    /**
     * Tests the createRadioButton() implementation.
     */
    @Test
    public void testCreateRadioButton() throws FormBuilderException
    {
        ComponentHandler<Boolean> ch = componentHandlerMock(Boolean.class);
        RadioButtonTag tag = new RadioButtonTag();
        EasyMock.expect(wrappedManager.createRadioButton(tag, true)).andReturn(
                ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createRadioButton(tag, true));
        verify(ch);
    }

    /**
     * Tests the createComboBox() implementation.
     */
    @Test
    public void testCreateComboBox() throws FormBuilderException
    {
        ComponentHandler<Object> ch = componentHandlerMock(Object.class);
        ComboBoxTag tag = new ComboBoxTag();
        EasyMock.expect(wrappedManager.createComboBox(tag, false))
                .andReturn(ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createComboBox(tag, false));
        verify(ch);
    }

    /**
     * Tests the createListBox() implementation.
     */
    @Test
    public void testCreateListBox() throws FormBuilderException
    {
        ComponentHandler<Object> ch = componentHandlerMock(Object.class);
        ListBoxTag tag = new ListBoxTag();
        EasyMock.expect(wrappedManager.createListBox(tag, false)).andReturn(ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createListBox(tag, false));
        verify(ch);
    }

    /**
     * Tests the createTabbedPane() implementation.
     */
    @Test
    public void testCreateTabbedPane() throws FormBuilderException
    {
        ComponentHandler<Integer> ch = componentHandlerMock(Integer.class);
        TabbedPaneTag tag = new TabbedPaneTag();
        EasyMock.expect(wrappedManager.createTabbedPane(tag, true)).andReturn(
                ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createTabbedPane(tag, true));
        verify(ch);
    }

    /**
     * Tests the createStaticText() implementation.
     */
    @Test
    public void testCreateStaticText() throws FormBuilderException
    {
        ComponentHandler<StaticTextData> ch =
                componentHandlerMock(StaticTextData.class);
        StaticTextTag tag = new StaticTextTag();
        EasyMock.expect(wrappedManager.createStaticText(tag, false)).andReturn(
                ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createStaticText(tag, false));
        verify(ch);
    }

    /**
     * Tests the createProgressBar() implementation.
     */
    @Test
    public void testCreateProgressBar() throws FormBuilderException
    {
        ComponentHandler<Integer> ch = componentHandlerMock(Integer.class);
        ProgressBarTag tag = new ProgressBarTag();
        EasyMock.expect(wrappedManager.createProgressBar(tag, true)).andReturn(
                ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createProgressBar(tag, true));
        verify(ch);
    }

    /**
     * Tests the createSlider() implementation.
     */
    @Test
    public void testCreateSlider() throws FormBuilderException
    {
        ComponentHandler<Integer> ch = componentHandlerMock(Integer.class);
        SliderTag tag = new SliderTag();
        EasyMock.expect(wrappedManager.createSlider(tag, true)).andReturn(ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createSlider(tag, true));
        verify(ch);
    }

    /**
     * Tests the createTable() implementation.
     */
    @Test
    public void testCreateTable() throws FormBuilderException
    {
        ComponentHandler<Object> ch = componentHandlerMock(Object.class);
        TableTag tag = new TableTag();
        EasyMock.expect(wrappedManager.createTable(tag, false)).andReturn(ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createTable(tag, false));
        verify(ch);
    }

    /**
     * Tests the createTree() implementation.
     */
    @Test
    public void testCreateTree() throws FormBuilderException
    {
        ComponentHandler<Object> ch = componentHandlerMock(Object.class);
        TreeTag tag = new TreeTag();
        EasyMock.expect(wrappedManager.createTree(tag, false)).andReturn(ch);
        replay(ch);
        assertSame("Wrong handler", ch, wrapper.createTree(tag, false));
        verify(ch);
    }
}

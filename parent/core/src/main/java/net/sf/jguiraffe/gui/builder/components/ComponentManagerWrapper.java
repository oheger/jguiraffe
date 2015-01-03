/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

/**
 * <p>
 * A base class for wrapper implementations of the {@link ComponentManager}
 * interface.
 * </p>
 * <p>
 * An instance of this class is initialized with a reference to a
 * {@link ComponentManager} object. All methods simply delegate to this object.
 * </p>
 * <p>
 * This class is especially useful if a custom implementation of
 * {@link ComponentManager} is to be created based on an existing
 * implementation. Then only the methods to be customized have to be
 * implemented. All other methods can still delegate to the existing
 * implementation.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComponentManagerWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class ComponentManagerWrapper implements ComponentManager
{
    /** Stores the wrapped component manager. */
    private final ComponentManager wrappedManager;

    /**
     * Creates a new instance of {@code ComponentManagerWrapper} and initializes
     * it with the wrapped {@code ComponentManager}. Note: This method does not
     * check whether the passed in {@code ComponentManager} object is
     * <b>null</b> because we do not want to enforce this restriction on all
     * subclasses. The passed in wrapped component manager is stored in an
     * internal field and can be accessed using the
     * {@link #getWrappedComponentManager()} method. If a subclass needs another
     * mechanism to access the wrapped manager, it has to override the
     * {@link #getWrappedComponentManager()} method.
     *
     * @param wrapped the wrapped {@code ComponentManager}
     */
    protected ComponentManagerWrapper(ComponentManager wrapped)
    {
        wrappedManager = wrapped;
    }

    /**
     * Returns a reference to the wrapped {@code ComponentManager} object.
     *
     * @return the wrapped {@code ComponentManager} object
     */
    public ComponentManager getWrappedComponentManager()
    {
        return wrappedManager;
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public void addContainerComponent(Object container, Object component,
            Object constraints)
    {
        getWrappedComponentManager().addContainerComponent(container,
                component, constraints);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public void setContainerLayout(Object container, Object layout)
    {
        getWrappedComponentManager().setContainerLayout(container, layout);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public PlatformEventManager createEventManager()
    {
        return getWrappedComponentManager().createEventManager();
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public WidgetHandler getWidgetHandlerFor(Object component)
    {
        return getWrappedComponentManager().getWidgetHandlerFor(component);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public Object createLabel(LabelTag tag, boolean create)
            throws FormBuilderException
    {
        return getWrappedComponentManager().createLabel(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public void linkLabel(Object label, Object component, String text)
            throws FormBuilderException
    {
        getWrappedComponentManager().linkLabel(label, component, text);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public Object createIcon(Locator locator) throws FormBuilderException
    {
        return getWrappedComponentManager().createIcon(locator);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public Object createFont(FontTag tag) throws FormBuilderException
    {
        return getWrappedComponentManager().createFont(tag);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public Object createPercentLayout(PercentLayoutTag tag)
            throws FormBuilderException
    {
        return getWrappedComponentManager().createPercentLayout(tag);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public Object createButtonLayout(ButtonLayoutTag tag)
            throws FormBuilderException
    {
        return getWrappedComponentManager().createButtonLayout(tag);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public Object createBorderLayout(BorderLayoutTag tag)
            throws FormBuilderException
    {
        return getWrappedComponentManager().createBorderLayout(tag);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public Object createPanel(PanelTag tag, boolean create)
            throws FormBuilderException
    {
        return getWrappedComponentManager().createPanel(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public Object createDesktopPanel(DesktopPanelTag tag, boolean create)
            throws FormBuilderException
    {
        return getWrappedComponentManager().createDesktopPanel(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public Object createSplitter(SplitterTag tag, boolean create)
            throws FormBuilderException
    {
        return getWrappedComponentManager().createSplitter(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public Object createRadioGroup(Map<String, Object> radioButtons)
            throws FormBuilderException
    {
        return getWrappedComponentManager().createRadioGroup(radioButtons);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<Boolean> createButton(ButtonTag tag, boolean create)
            throws FormBuilderException
    {
        return getWrappedComponentManager().createButton(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<Boolean> createToggleButton(ToggleButtonTag tag,
            boolean create) throws FormBuilderException
    {
        return getWrappedComponentManager().createToggleButton(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<String> createTextField(TextFieldTag tag,
            boolean create) throws FormBuilderException
    {
        return getWrappedComponentManager().createTextField(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<String> createTextArea(TextAreaTag tag,
            boolean create) throws FormBuilderException
    {
        return getWrappedComponentManager().createTextArea(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<String> createPasswordField(PasswordFieldTag tag,
            boolean create) throws FormBuilderException
    {
        return getWrappedComponentManager().createPasswordField(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<Boolean> createCheckbox(CheckboxTag tag,
            boolean create) throws FormBuilderException
    {
        return getWrappedComponentManager().createCheckbox(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<Boolean> createRadioButton(RadioButtonTag tag,
            boolean create) throws FormBuilderException
    {
        return getWrappedComponentManager().createRadioButton(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<Object> createComboBox(ComboBoxTag tag,
            boolean create) throws FormBuilderException
    {
        return getWrappedComponentManager().createComboBox(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<Object> createListBox(ListBoxTag tag, boolean create)
            throws FormBuilderException
    {
        return getWrappedComponentManager().createListBox(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<Integer> createTabbedPane(TabbedPaneTag tag,
            boolean create) throws FormBuilderException
    {
        return getWrappedComponentManager().createTabbedPane(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<StaticTextData> createStaticText(StaticTextTag tag,
            boolean create) throws FormBuilderException
    {
        return getWrappedComponentManager().createStaticText(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<Integer> createProgressBar(ProgressBarTag tag,
            boolean create) throws FormBuilderException
    {
        return getWrappedComponentManager().createProgressBar(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<Integer> createSlider(SliderTag tag, boolean create)
            throws FormBuilderException
    {
        return getWrappedComponentManager().createSlider(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<Object> createTable(TableTag tag, boolean create)
            throws FormBuilderException
    {
        return getWrappedComponentManager().createTable(tag, create);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ComponentManager}
     * object.
     */
    public ComponentHandler<Object> createTree(TreeTag tag, boolean create)
            throws FormBuilderException
    {
        return getWrappedComponentManager().createTree(tag, create);
    }
}

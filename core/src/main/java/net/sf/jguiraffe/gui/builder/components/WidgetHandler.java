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
package net.sf.jguiraffe.gui.builder.components;

/**
 * <p>
 * Definition of an interface for dealing with widgets.
 * </p>
 * <p>
 * A <em>widget</em> is an arbitrary GUI element. It can be an input element
 * like a text field, or a checkbox, but also a simple graphical element like a
 * label, or a panel. Through the methods provided by this interface such
 * widgets can be manipulated; for instance they can be made invisible, or their
 * colors can be changed. This way it is possible to change the GUI dynamically.
 * </p>
 * <p>
 * {@link ComponentBuilderData} provides methods for obtaining
 * the widgets created during the latest builder operation by name. After a
 * <code>WidgetHandler</code> has been obtained this way, it can be used for
 * doing something with the corresponding widget.
 * </p>
 * <p>
 * <strong>Note:</strong> This interface is not intended to be directly
 * implemented by client code. It is subject to change even in minor releases as
 * new features are made available.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WidgetHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface WidgetHandler
{
    /**
     * Returns a reference to the underlying widget. This is the
     * platform-specific GUI control. For instance, if Swing was used as GUI
     * platform, a <code>Component</code> object would be returned.
     *
     * @return the underlying GUI control
     */
    Object getWidget();

    /**
     * Returns a flag whether the wrapped widget is currently visible.
     *
     * @return the visible flag of the wrapped widget
     */
    boolean isVisible();

    /**
     * Sets the visible flag of the wrapped widget. Using this method a widget
     * can be hidden and made visible again.
     *
     * @param f the visible flag of the wrapped widget
     */
    void setVisible(boolean f);

    /**
     * Returns the background color of the underlying widget.
     *
     * @return the background color of this widget
     */
    Color getBackgroundColor();

    /**
     * Sets the background color of the underlying widget.
     *
     * @param c the new background color (as a platform-independent
     * <code>Color</code> object); if the passed in color object is <b>null</b>,
     * this operation has no effect
     */
    void setBackgroundColor(Color c);

    /**
     * Returns the foreground color of the underlying widget.
     *
     * @return the foreground color of this widget
     */
    Color getForegroundColor();

    /**
     * Sets the foreground color of the underlying widget.
     *
     * @param c the new background color (as a platform-independent
     * <code>Color</code> object); if the passed in color object is <b>null</b>,
     * this operation has no effect
     */
    void setForegroundColor(Color c);

    /**
     * Returns the tool tip text of the underlying widget. This can be
     * <b>null</b> if no tool tip was set. Note: It is possible that an
     * implementation returns a different tool tip text than the one passed to
     * {@link #setToolTip(String)}. This is due to the fact that certain control
     * characters like line feeds may have to be converted by a concrete
     * implementation. To avoid confusion related to changed tool tips client
     * code should only interact with the {@link ToolTipManager} to manipulate
     * tool tips.
     *
     * @return the tool tip of this widget
     */
    String getToolTip();

    /**
     * Sets the tool tip text of the underlying widget. Note: Client code should
     * not call this method directly. Rather, the {@link ToolTipManager} should
     * be used for setting tool tips for widgets.
     *
     * @param tip the new tool tip text
     */
    void setToolTip(String tip);

    /**
     * Returns the font of this widget. This is a platform-specific object.
     *
     * @return the font of this widget
     */
    Object getFont();

    /**
     * Sets the font of this widget. The font is a platform-specific object. It
     * can be created in builder scripts using the {@code <f:font>} tag or
     * directly using the {@code createFont()} method of
     * {@link ComponentManager}. Concrete implementations may throw a runtime
     * exception if the font object passed to this method is invalid.
     *
     * @param font the font to be set
     */
    void setFont(Object font);
}

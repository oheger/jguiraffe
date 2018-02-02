/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import javax.swing.JComponent;

import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.builder.components.WidgetHandler;

/**
 * <p>
 * A Swing specific implementation of the <code>WidgetHandler</code> interface.
 * </p>
 * <p>
 * This implementation operates on an underlying <code>Component</code> object
 * and provides access to some of its fundamental properties.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingWidgetHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingWidgetHandler implements WidgetHandler
{

    /** Stores the underlying widget. */
    private final JComponent widget;

    /**
     * Creates a new instance of <code>SwingWidgetHandler</code> and initializes
     * it with the wrapped component.
     *
     * @param component the underlying component
     */
    public SwingWidgetHandler(JComponent component)
    {
        widget = component;
    }

    /**
     * Returns a reference to the underlying component.
     *
     * @return the underlying AWT component
     */
    public JComponent getComponent()
    {
        return widget;
    }

    /**
     * Returns the background color of the underlying widget.
     *
     * @return the background color of the underlying widget
     */
    public Color getBackgroundColor()
    {
        return SwingComponentUtils.getBackgroundColor(getComponent());
    }

    /**
     * Returns the foreground color of the underlying widget.
     *
     * @return the foreground color of the underlying widget
     */
    public Color getForegroundColor()
    {
        return SwingComponentUtils.getForegroundColor(getComponent());
    }

    /**
     * Returns the underlying widget.
     *
     * @return the underlying widget
     */
    public Object getWidget()
    {
        return getComponent();
    }

    /**
     * Returns a flag whether the underlying widget is visible.
     *
     * @return the visible flag of the underlying widget
     */
    public boolean isVisible()
    {
        return getComponent().isVisible();
    }

    /**
     * Sets the background color of the underlying widget. The passed in
     * platform independent <code>Color</code> object will be transformed into
     * an AWT color object.
     *
     * @param c the new background color
     */
    public void setBackgroundColor(Color c)
    {
        SwingComponentUtils.setBackgroundColor(getComponent(), c);
    }

    /**
     * Sets the foreground color of the underlying widget. The passed in
     * platform independent <code>Color</code> object will be transformed into
     * an AWT color object.
     *
     * @param c the new foreground color
     */
    public void setForegroundColor(Color c)
    {
        SwingComponentUtils.setForegroundColor(getComponent(), c);
    }

    /**
     * Sets the visible flag of the underlying widget.
     *
     * @param f the new visible flag
     */
    public void setVisible(boolean f)
    {
        getComponent().setVisible(f);
    }

    /**
     * Returns the tool tip of the associated component.
     *
     * @return the tool tip
     */
    public String getToolTip()
    {
        return SwingComponentUtils.getToolTip(getComponent());
    }

    /**
     * Sets the tool tip of the associated component.
     *
     * @param tip the new tool tip text
     */
    public void setToolTip(String tip)
    {
        SwingComponentUtils.setToolTip(getComponent(), tip);
    }

    /**
     * Returns the font of this widget. The font is directly obtained from the
     * underlying Swing component.
     *
     * @return the font of this widget
     */
    public Object getFont()
    {
        return SwingComponentUtils.getFont(getComponent());
    }

    /**
     * Sets the font of this widget. The passed in font object is passed to the
     * underlying Swing component. It must be of type {@code java.awt.Font}.
     *
     * @param font the new font
     */
    public void setFont(Object font)
    {
        SwingComponentUtils.setFont(getComponent(), font);
    }
}

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
package net.sf.jguiraffe.gui.builder.components;

/**
 * A simple dummy implementation of the <code>WidgetHandler</code> interface.
 * This class is used by <code>ComponentManagerImpl</code> for the
 * implementation of the <code>getWidgetHandlerFor()</code> method. Most of
 * the methods here simply operate on internal flags and do not have any further
 * effects. The class can be used for testing operations on widgets.
 *
 * @author Oliver Heger
 * @version $Id: WidgetHandlerImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class WidgetHandlerImpl implements WidgetHandler
{
    /** Stores the wrapped component. */
    private Object widget;

    /** Stores the foreground color. */
    private Color foregroundColor;

    /** Stores the background color. */
    private Color backgroundColor;

    /** The font object of this widget. */
    private Object font;

    /** The tool tip text. */
    private String toolTip;

    /** Stores the visible flag. */
    private boolean visible;

    /**
     * Creates a new instance of <code>WidgetHandlerImpl</code> and sets the
     * underlying widget object.
     *
     * @param component the widget
     */
    public WidgetHandlerImpl(Object component)
    {
        widget = component;
        visible = true;
    }

    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public Color getForegroundColor()
    {
        return foregroundColor;
    }

    public Object getWidget()
    {
        return widget;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setBackgroundColor(Color c)
    {
        backgroundColor = c;
    }

    public void setForegroundColor(Color c)
    {
        foregroundColor = c;
    }

    public void setVisible(boolean f)
    {
        visible = f;
    }

    public String getToolTip()
    {
        return toolTip;
    }

    public void setToolTip(String toolTip)
    {
        this.toolTip = toolTip;
    }

    public Object getFont()
    {
        return font;
    }

    public void setFont(Object font)
    {
        this.font = font;
    }
}

/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.util.Collection;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specific container tag implementation that creates a desktop panel.
 * </p>
 * <p>
 * Desktop panels can be used in MDI applications. They provide the background
 * on which internal frames are drawn.
 * </p>
 * <p>
 * Note that desktop panels may not be supported by all GUI libraries. They fit
 * well in Swing's concept of internal frames, but can be problematic in SWT
 * applications. So for really portable applications their usage is not
 * recommended.
 * </p>
 * <p>
 * The following attributes are supported by this tag handler class:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">dragmode</td>
 * <td>Defines the way windows on this panel are dragged. Two values are
 * supported: <em>live</em> means that the windows are fully drawn while they
 * are dragged. <em>outline</em> means that only a frame is drawn during
 * dragging; this is more efficient on slow systems. If this attribute is not
 * defined, <em>live</em> is used as default value.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DesktopPanelTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DesktopPanelTag extends ContainerTag
{
    /** Stores the drag mode for the desktop panel. */
    private DragMode dragMode;

    /** Stores the value of the drag mode attribute. */
    private String dragModeAttr;

    /**
     * Setter method of the dragmode attribute.
     *
     * @param s the attribute's value
     */
    public void setDragmode(String s)
    {
        dragModeAttr = s;
    }

    /**
     * Returns the desktop panel's drag mode.
     *
     * @return the drag mode
     */
    public DragMode getDragMode()
    {
        return (dragMode != null) ? dragMode : DragMode.LIVE;
    }

    /**
     * Sets the desktop panel's drag mode.
     *
     * @param dm the new drag mode
     */
    public void setDragMode(DragMode dm)
    {
        dragMode = dm;
    }

    /**
     * Creates the container component. This implementation creates a desktop
     * panel through the given component manager.
     *
     * @param manager the component manager
     * @param create the create flag
     * @param components a collection with the container's children
     * @return the new container
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected Object createContainer(ComponentManager manager, boolean create,
            Collection<Object[]> components) throws FormBuilderException,
            JellyTagException
    {
        if (create)
        {
            if (dragModeAttr != null)
            {
                DragMode mode = DragMode.fromString(dragModeAttr);
                if (mode == null)
                {
                    throw new FormBuilderException("Invalid drag mode: "
                            + dragModeAttr);
                }
                setDragMode(mode);
            }
        }

        return manager.createDesktopPanel(this, create);
    }

    /**
     * Enumeration class that defines the allowed values for the {@code
     * dragmode} attribute.
     */
    public enum DragMode
    {
        /**
         * Constant for the drag mode <em>live</em>. This means that windows are
         * fully painted while they are dragged on the desktop pane.
         */
        LIVE,

        /**
         * Constant for the drag mode <em>outline</em>. In this mode only a
         * frame is drawn representing the window dragged on the desktop pane.
         */
        OUTLINE;

        /**
         * Returns the enumeration literal whose name matches the given string.
         * The case does not matter. If no matching literal can be found,
         * <b>null</b> is returned.
         *
         * @param name the name of the literal
         * @return the instance representing this value or <b>null</b> if it
         *         cannot be found
         */
        public static DragMode fromString(String name)
        {
            for (DragMode dm : values())
            {
                if (dm.name().equalsIgnoreCase(name))
                {
                    return dm;
                }
            }

            return null;
        }
    }
}

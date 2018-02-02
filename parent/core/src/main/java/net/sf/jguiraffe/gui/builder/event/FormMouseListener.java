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
package net.sf.jguiraffe.gui.builder.event;

/**
 * <p>
 * Definition of interface for mouse listeners.
 * </p>
 * <p>
 * Objects implementing this interface can register themselves as mouse
 * listeners at components. They are then notified about mouse actions related
 * to those components.
 * </p>
 * <p>
 * This interface defines a bunch of methods that correspond to the event types
 * defined by {@link FormMouseEvent}. All methods are passed a
 * {@link FormMouseEvent} object with all information about the mouse event.
 * </p>
 * <p>
 * In contrast to other event listener interfaces like
 * {@link FormActionListener} or {@link FormChangeListener} , mouse listeners
 * are more low-level. They deal with physical input events rather than logic
 * events that have already been pre-processed by input components.
 * Implementations should be aware of this fact.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormMouseListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface FormMouseListener extends FormEventListener
{
    /**
     * Event notification method that is called when a mouse button is pressed.
     * This method corresponds to the {@code MOUSE_PRESSED} event type.
     *
     * @param event the mouse event
     */
    void mousePressed(FormMouseEvent event);

    /**
     * Event notification method that is called when a mouse button is released.
     * This method corresponds to the {@code MOUSE_RELEASED} event type.
     *
     * @param event the mouse event
     */
    void mouseReleased(FormMouseEvent event);

    /**
     * Event notification method that is called when a mouse button is clicked.
     * A click means that the button is pressed and then released. This method
     * corresponds to the {@code MOUSE_CLICKED} event type.
     *
     * @param event the mouse event
     */
    void mouseClicked(FormMouseEvent event);

    /**
     * Event notification method that is called when a mouse button is
     * double-clicked. This means that the button was clicked twice in an
     * OS-specific interval. This method corresponds to the {@code
     * MOUSE_DOUBLE_CLICKED} event type.
     *
     * @param event the mouse event
     */
    void mouseDoubleClicked(FormMouseEvent event);

    /**
     * Event notification method that is called when the mouse cursor enters the
     * space occupied by the monitored component. This method corresponds to the
     * {@code MOUSE_ENTERED} event type.
     *
     * @param event the mouse event
     */
    void mouseEntered(FormMouseEvent event);

    /**
     * Event notification method that is called when the mouse cursor leaves the
     * space occupied by the monitored component. This method corresponds to the
     * {@code MOUSE_EXITED} event type.
     *
     * @param event the mouse event
     */
    void mouseExited(FormMouseEvent event);
}

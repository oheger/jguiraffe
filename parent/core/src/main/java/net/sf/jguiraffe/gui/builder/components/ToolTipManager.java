/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
 * Definition of an interface for a component that manages the tool tips of UI
 * elements.
 * </p>
 * <p>
 * Each UI element can be associated with a tool tip that is displayed when the
 * mouse cursor is placed over the element. The tag handler classes for creating
 * UI element provide corresponding options.
 * </p>
 * <p>
 * Assigning a tool tip text to an element is standard functionality. However,
 * sometimes the texts for tool tips must be dynamically adapted depending on
 * the current status of the element. For instance, an input element contains
 * invalid data, and the tool tip is to be extended to display the validation
 * messages, too. Or the tool tip of a disabled button should display additional
 * information why this button cannot be pressed currently.
 * </p>
 * <p>
 * This interface provides functionality for dealing with tool tips for
 * components that can change dynamically. The basic idea is that each
 * component's tool tip consists of two parts:
 * <ul>
 * <li>a static part describing the functionality of this component</li>
 * <li>and a dynamic part containing information related to the current state of
 * the component.</li>
 * </ul>
 * Both parts are optional. Typically the static part remains constant while the
 * dynamic part may change, but this is not a requirement. This interface
 * defines methods to query and to set both parts of the tool tip for a given
 * component. Components can be specified as native objects or by their name
 * (the latter requires that a concrete implementation has access to a component
 * store).
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ToolTipManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ToolTipManager
{
    /**
     * Returns the tool tip of the specified component.
     *
     * @param component the component in question
     * @return the tool tip of this component (can be <b>null</b>)
     */
    String getToolTip(Object component);

    /**
     * Sets the tool tip of the specified component. This is the static part of
     * the component's tool tip.
     *
     * @param component the component in question
     * @param tip the static tool tip for this component
     */
    void setToolTip(Object component, String tip);

    /**
     * Returns the tool tip for the component with the specified name. If the
     * name cannot be resolved, an exception is thrown.
     *
     * @param componentName the name of the component in question
     * @return the tool tip of this component (can be <b>null</b>)
     * @throws net.sf.jguiraffe.gui.builder.utils.GUIRuntimeException if the
     *         component cannot be resolved
     */
    String getToolTip(String componentName);

    /**
     * Sets the tool tip of the component with the given name. This is the
     * static part of the component's tool tip. If the name cannot be resolved,
     * an exception is thrown.
     *
     * @param componentName the name of the component in question
     * @param tip the static tool tip for this component
     * @throws net.sf.jguiraffe.gui.builder.utils.GUIRuntimeException if the
     *         component name cannot be resolved
     */
    void setToolTip(String componentName, String tip);

    /**
     * Returns the additional (dynamic) tool tip for the specified component.
     *
     * @param component the component in question
     * @return the additional (dynamic) tool tip of this component (can be
     *         <b>null</b>)
     */
    String getAdditionalToolTip(Object component);

    /**
     * Sets the additional (dynamic) tool tip for the specified component. The
     * actual tool tip of this component is constructed from the static part
     * (set through the {@link #setToolTip(Object, String)} method) and this
     * part.
     *
     * @param component the component in question
     * @param tip the additional (dynamic) tool tip for this component
     */
    void setAdditionalToolTip(Object component, String tip);

    /**
     * Returns the additional (dynamic) tool tip for the component with the
     * given name. If the name cannot be resolved, an exception is thrown.
     *
     * @param componentName the name of the component in question
     * @return the additional (dynamic) tool tip of this component
     * @throws net.sf.jguiraffe.gui.builder.utils.GUIRuntimeException if the
     *         component name cannot be resolved
     */
    String getAdditionalToolTip(String componentName);

    /**
     * Sets the additional (dynamic) tool tip for the component with the given
     * name. This works like {@link #setAdditionalToolTip(Object, String)}, but
     * the component is specified using its name. If the name cannot be
     * resolved, an exception is thrown.
     *
     * @param componentName the name of the component in question
     * @param tip the additional (dynamic) tool tip
     * @throws net.sf.jguiraffe.gui.builder.utils.GUIRuntimeException if the
     *         component name cannot be resolved
     */
    void setAdditionalToolTip(String componentName, String tip);
}

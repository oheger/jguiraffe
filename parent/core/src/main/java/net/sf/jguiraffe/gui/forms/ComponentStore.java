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
package net.sf.jguiraffe.gui.forms;

import java.util.Set;

/**
 * <p>
 * Definition of an interface for objects that are able to manage entities
 * related to a <code>Form</code> object.
 * </p>
 * <p>Objects implementing this interface are able to store
 * <code>{@link FieldHandler}</code>s, <code>{@link ComponentHandler}</code>s,
 * and simple components. All these entities can also be accessed by name.
 * </p>
 * <p>This interface is especially useful when a <code>Form</code> object is
 * constructed. If this is done by a <em>builder</em>, all components created
 * during the builder operation must be collected. It is even possible that
 * multiple component stores are involved, e.g. if there are complex components,
 * which create their own sub forms. In such cases having multiple - even
 * different - implementations of the <code>ComponentStore</code> interface can
 * be helpful.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComponentStore.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ComponentStore
{
    /**
     * Adds the specified component to this component store under the given
     * name.
     *
     * @param name the name of the component
     * @param component the component itself
     */
    void add(String name, Object component);

    /**
     * Adds the specified component handler to this component store under the
     * given name.
     *
     * @param name the name of the component
     * @param handler the component handler
     */
    void addComponentHandler(String name, ComponentHandler<?> handler);

    /**
     * Adds the specified field handler to this component store. The caller is
     * responsible for adding the associated <code>ComponentHandler</code>
     * manually.
     *
     * @param name the name of the component
     * @param fldHandler the field handler to be added
     */
    void addFieldHandler(String name, FieldHandler fldHandler);

    /**
     * Returns the component with the given name. If no such component can be
     * found, <b>null</b> is returned.
     *
     * @param name the name of the desired component
     * @return the component
     */
    Object findComponent(String name);

    /**
     * Returns the component handler with the given name. This handler must have
     * been added before using
     * <code>{@link #addComponentHandler(String, ComponentHandler)}</code>.
     * If no such component handler can be found, <b>null</b> is returned.
     *
     * @param name the name of the desired component handler
     * @return the corresponding handler
     */
    ComponentHandler<?> findComponentHandler(String name);

    /**
     * Returns the field handler with the given name. This handler must have
     * been added before using
     * <code>{@link #addFieldHandler(String, FieldHandler)}</code>. If no
     * such field handler can be found, <b>null</b> is returned.
     *
     * @param name the name of the desired field handler
     * @return the corresponding handler
     */
    FieldHandler findFieldHandler(String name);

    /**
     * Returns a set with the names of all components that are contained in this
     * store.
     *
     * @return a set with the names of the components in this store
     */
    Set<String> getComponentNames();

    /**
     * Returns a set with the names of all component handlers that are contained
     * in this store.
     *
     * @return a set with the names of the component handlers in this store
     */
    Set<String> getComponentHandlerNames();

    /**
     * Returns a set with the names of all field handlers that are contained in
     * this store.
     *
     * @return a set with the names of the field handlers in this store
     */
    Set<String> getFieldHandlerNames();
}

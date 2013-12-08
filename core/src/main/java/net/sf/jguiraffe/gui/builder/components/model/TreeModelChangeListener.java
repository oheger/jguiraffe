/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.model;

import java.util.EventListener;

import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * <p>
 * Definition of an interface to be implemented by components interested in
 * changes on a tree model.
 * </p>
 * <p>
 * This interface is used by {@link TreeConfigurationChangeHandler}. Whenever a
 * change event from an associated {@code Configuration} is received the
 * affected {@code ConfigurationNode} is determined and passed to an
 * implementation of this interface. The implementation can then decide how to
 * react on this change.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: $
 * @since 1.3
 */
public interface TreeModelChangeListener extends EventListener
{
    /**
     * Notifies this object that a change of the model configuration was
     * detected. The passed in {@code ConfigurationNode} represents the sub tree
     * in the configuration affected by the change. (In the worst case, this is
     * the configuration's root node.)
     *
     * @param node the root {@code ConfigurationNode} of the sub tree affected
     *        by the change
     */
    void treeModelChanged(ConfigurationNode node);
}

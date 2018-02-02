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
 * <p>
 * A default implementation of the {@code ContainerSelector} interface.
 * </p>
 * <p>
 * This implementation simply returns the passed in container tag. This is
 * appropriate for most use cases because the components defined in the body of
 * a container tag have to be added to the data managed by this tag. Only in
 * very special circumstances - e.g. if a tag has to be executed in an
 * alternative context - a different behavior is needed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.3
 */
public class DefaultContainerSelector implements ContainerSelector
{
    /**
     * {@inheritDoc} This implementation returns the passed in
     * {@code ContainerTag}.
     */
    public Composite getComposite(Composite tag)
    {
        return tag;
    }
}

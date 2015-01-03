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

/**
 * <p>
 * An interface for a component responsible for obtaining a container object.
 * </p>
 * <p>
 * During the execution of a builder script it is often necessary to obtain a
 * {@link Composite} object in which newly created elements have to be stored.
 * Per default, these elements are directly added to the enclosing container
 * tag.
 * </p>
 * <p>
 * In rare cases, however, there is the need to use an alternative
 * {@link Composite} implementation as storage. This can be achieved with a
 * special implementation of this interface. An object implementing this
 * interface is contained in the central {@link ComponentBuilderData} object.
 * When searching for an enclosing container tag this object is invoked with the
 * found tag as parameter. The implementation can then decide which
 * {@code Composite} to use.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.3
 */
public interface ContainerSelector
{
    /**
     * Returns the {@code Composite} to be used for the specified tag
     * implementing the {@code Composite} interface.
     *
     * @param tag the tag implementing {@code Composite}
     * @return the {@code Composite} to be used for this tag
     */
    Composite getComposite(Composite tag);
}

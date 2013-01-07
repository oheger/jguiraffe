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
package net.sf.jguiraffe.gui.builder.components;

import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.builder.components.tags.InputComponentTag;

/**
 * <p>
 * Definition of an interface for objects that are able to create
 * <code>{@link net.sf.jguiraffe.gui.forms.FieldHandler FieldHandler}</code>
 * objects.
 * </p>
 * <p>
 * An implementation of this interface is used by input components creating tags
 * during a builder operation. It creates the handler objects that are stored in
 * the constructed <code>{@link net.sf.jguiraffe.gui.forms.Form Form}</code>
 * object.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FieldHandlerFactory.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface FieldHandlerFactory
{
    /**
     * Creates a new field handler object. The component handler to be
     * associated with the new field handler is directly passed. All other
     * necessary information can be obtained from the specified tag handler tag.
     *
     * @param tag the input component tag
     * @param componentHandler the component handler
     * @return the new field handler
     * @throws FormBuilderException if an error occurs
     */
    FieldHandler createFieldHandler(InputComponentTag tag,
            ComponentHandler<?> componentHandler) throws FormBuilderException;
}

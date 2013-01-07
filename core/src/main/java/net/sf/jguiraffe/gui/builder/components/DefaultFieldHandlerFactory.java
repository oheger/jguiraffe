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
import net.sf.jguiraffe.gui.forms.DefaultFieldHandler;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.builder.components.tags.InputComponentTag;

/**
 * <p>
 * A default implementation of the <code>FieldHandlerFactory</code> interface.
 * </p>
 * <p>
 * This implementation creates objects of class
 * <code>{@link net.sf.jguiraffe.gui.forms.DefaultFieldHandler DefaultFieldHandler}</code>.
 * All information needed to initialize such an object are obtained from the
 * passed in input component tag.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DefaultFieldHandlerFactory.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DefaultFieldHandlerFactory implements FieldHandlerFactory
{
    /**
     * Creates a new field handler object for the specified input component tag.
     *
     * @param tag the component tag
     * @param componentHandler the component handler
     * @return the new field handler object
     * @throws FormBuilderException if an error occurs
     */
    public FieldHandler createFieldHandler(InputComponentTag tag,
            ComponentHandler<?> componentHandler) throws FormBuilderException
    {
        DefaultFieldHandler fh = new DefaultFieldHandler();
        fh.setComponentHandler(componentHandler);
        fh.setSyntaxValidator(tag.getFieldValidator());
        fh.setLogicValidator(tag.getFormValidator());
        fh.setPropertyName(tag.getPropertyName());
        fh.setReadTransformer(tag.getReadTransformer());
        fh.setWriteTransformer(tag.getWriteTransformer());
        fh.setType(tag.getComponentType());
        fh.setDisplayName(tag.getDisplayName());
        return fh;
    }
}

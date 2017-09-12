/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import net.sf.jguiraffe.gui.forms.DummyWrapper;
import net.sf.jguiraffe.gui.forms.TransformerWrapper;

/**
 * <p>
 * An internally used helper class acting as a {@code TransformerWrapper}, which
 * only delegates to another transformer.
 * </p>
 * <p>
 * The components responsible for implementing tables support the possibility to
 * install transformers for specific elements of the form representing a table
 * row. This is used to support logic column types. This class is part of this
 * feature. Each element is assigned instances of this class for the read and
 * write transformer. This makes it possible to check whether real transformers
 * were assigned and to change them later if needed.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe. It is intended to be used
 * only in the construction phase of a UI.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 */
class TransformerReference implements TransformerWrapper
{
    /** The referenced transformer. */
    private TransformerWrapper transformer;

    /**
     * Creates a new instance of {@code TransformerReference} and initializes it
     * with the referenced transformer. The passed in value can be <b>null</b>.
     *
     * @param trans the referenced transformer (can be <b>null</b>)
     */
    public TransformerReference(TransformerWrapper trans)
    {
        setTransformer(trans);
    }

    /**
     * Returns the referenced transformer.
     *
     * @return the referenced {@code TransformerWrapper}
     */
    public final TransformerWrapper getTransformer()
    {
        return transformer;
    }

    /**
     * Sets the referenced transformer.
     *
     * @param transformer the referenced {@code TransformerWrapper}
     */
    public final void setTransformer(TransformerWrapper transformer)
    {
        this.transformer =
                (transformer != null) ? transformer : DummyWrapper.INSTANCE;
    }

    /**
     * {@inheritDoc} This implementation delegates to the referenced
     * {@code TransformerWrapper}.
     */
    public Object transform(Object o)
    {
        return getTransformer().transform(o);
    }
}

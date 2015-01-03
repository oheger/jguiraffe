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
package net.sf.jguiraffe.gui.forms;

import net.sf.jguiraffe.transform.Transformer;
import net.sf.jguiraffe.transform.TransformerContext;

/**
 * <p>
 * A default implementation of the {@code TransformerWrapper} interface.
 * </p>
 * <p>
 * An instance of this class is constructed with a {@code Transformer} and a
 * {@code TransformerContext}. A transformation is implemented by delegating to
 * the {@code Transformer} passing in the {@code TransformerContext}.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.3
 */
public class DefaultTransformerWrapper implements TransformerWrapper
{
    /** Stores the wrapped transformer. */
    private final Transformer transformer;

    /** Stores the context to use. */
    private final TransformerContext transformerContext;

    /**
     * Creates a new instance of {@code DefaultTransformerWrapper} and
     * initializes it.
     *
     * @param t the transformer (must not be <b>null</b>)
     * @param ctx the transformer context (must not be <b>null</b>)
     * @throws IllegalArgumentException if a required argument is missing
     */
    public DefaultTransformerWrapper(Transformer t, TransformerContext ctx)
    {
        if (t == null)
        {
            throw new IllegalArgumentException("Transformer must not be null!");
        }
        if (ctx == null)
        {
            throw new IllegalArgumentException(
                    "TransformerContext must not be null!");
        }

        transformer = t;
        transformerContext = ctx;
    }

    /**
     * Returns the wrapped transformer.
     *
     * @return the transformer
     */
    public Transformer getTransformer()
    {
        return transformer;
    }

    /**
     * Returns the transformer context to use.
     *
     * @return the transformer context
     */
    public TransformerContext getTransformerContext()
    {
        return transformerContext;
    }

    /**
     * Invokes the transformer.
     *
     * @param o the object to transform
     * @return the transformed object
     */
    public Object transform(Object o)
    {
        try
        {
            return getTransformer().transform(o, getTransformerContext());
        }
        catch (Exception ex)
        {
            throw new FormRuntimeException(ex);
        }
    }
}

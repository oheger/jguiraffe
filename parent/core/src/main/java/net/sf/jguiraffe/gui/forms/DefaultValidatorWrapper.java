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

import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.ValidationResult;
import net.sf.jguiraffe.transform.Validator;

/**
 * <p>
 * A default implementation of the {@code ValidatorWrapper} interface.
 * </p>
 * <p>
 * An instance of this class is constructed with a {@code Validator} and a
 * {@code TransformerContext}. A validation operation is implemented by
 * delegating to the {@code Validator} passing in the {@code TransformerContext}
 * .
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.3
 */
public class DefaultValidatorWrapper implements ValidatorWrapper
{
    /** Stores the wrapped validator. */
    private final Validator validator;

    /** Stores the transformer context. */
    private final TransformerContext transformerContext;

    /**
     * Creates a new instance of {@code DefaultValidatorWrapper} and initializes
     * it.
     *
     * @param v the wrapped validator (must not be <b>null</b>)
     * @param ctx the transformer context to use (must not be <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is missing
     */
    public DefaultValidatorWrapper(Validator v, TransformerContext ctx)
    {
        if (v == null)
        {
            throw new IllegalArgumentException("Validator must not be null!");
        }
        if (ctx == null)
        {
            throw new IllegalArgumentException(
                    "TransformerContext must not be null!");
        }

        validator = v;
        transformerContext = ctx;
    }

    /**
     * Returns the wrapped validator.
     *
     * @return the validator
     */
    public Validator getValidator()
    {
        return validator;
    }

    /**
     * Returns the {@code TransformerContext} to use.
     *
     * @return the transformer context
     */
    public TransformerContext getTransformerContext()
    {
        return transformerContext;
    }

    /**
     * Tests whether the specified object is valid.
     *
     * @param o the object to test
     * @return a result object with information about the object's validity
     */
    public ValidationResult isValid(Object o)
    {
        return getValidator().isValid(o, getTransformerContext());
    }
}

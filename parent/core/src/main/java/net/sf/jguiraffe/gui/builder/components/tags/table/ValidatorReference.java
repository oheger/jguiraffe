/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
import net.sf.jguiraffe.gui.forms.ValidatorWrapper;
import net.sf.jguiraffe.transform.ValidationResult;

/**
 * <p>
 * An internally used helper class acting as a {@code ValidatorWrapper}, which
 * only delegates to another validator.
 * </p>
 * <p>
 * This class serves the same purpose as {@link TransformerReference}, but
 * stores references to validators.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 */
class ValidatorReference implements ValidatorWrapper
{
    /** Stores the referenced validator wrapper. */
    private ValidatorWrapper validator;

    /**
     * Creates a new instance of {@code ValidatorReference} and sets the initial
     * value of this reference.
     *
     * @param validator the {@code ValidatorWrapper} to be referenced (can be
     *        <b>null</b>)
     */
    public ValidatorReference(ValidatorWrapper validator)
    {
        setValidator(validator);
    }

    /**
     * Returns the referenced {@code ValidatorWrapper}.
     *
     * @return the referenced {@code ValidatorWrapper}
     */
    public final ValidatorWrapper getValidator()
    {
        return validator;
    }

    /**
     * Sets the referenced {@code ValidatorWrapper}. This method ensures that
     * the value of this reference is never <b>null</b>. If <b>null</b> is
     * passed in, a dummy {@code ValidatorWrapper} is stored.
     *
     * @param validator the referenced {@code ValidatorWrapper}
     */
    public final void setValidator(ValidatorWrapper validator)
    {
        this.validator =
                (validator != null) ? validator : DummyWrapper.INSTANCE;
    }

    /**
     * {@inheritDoc} This implementation delegates to the referenced
     * {@code ValidatorWrapper}.
     */
    public ValidationResult isValid(Object o)
    {
        return getValidator().isValid(o);
    }
}

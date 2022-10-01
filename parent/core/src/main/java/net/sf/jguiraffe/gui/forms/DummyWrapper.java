/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.ValidationResult;

/**
 * <p>
 * A dummy implementation of the {@code TransformerWrapper} and the
 * {@code ValidatorWrapper} interfaces.
 * </p>
 * <p>
 * This class implements an identity transformation and a validation which
 * always returns <em>valid</em>. It can be used as an application of the
 * <em>null object pattern</em> when no specific transformers or validators are
 * set.
 * </p>
 * <p>
 * The class is an enumeration because it is not necessary to create instance;
 * the singleton instance can be shared by all components.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 */
public enum DummyWrapper implements TransformerWrapper, ValidatorWrapper
{
    /** The single instance of this class. */
    INSTANCE
    {
        /**
         * {@inheritDoc} This implementation returns the passed in object
         * without changes.
         */
        public Object transform(Object o)
        {
            return o;
        }

        /**
         * {@inheritDoc} This implementation always returns a valid result
         * object.
         */
        public ValidationResult isValid(Object o)
        {
            return DefaultValidationResult.VALID;
        }
    }
}

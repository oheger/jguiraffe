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
package net.sf.jguiraffe.transform;

/**
 * <p>
 * This class provides dummy implementations for the <code>Transformer</code>
 * and the <code>Validator</code> interfaces.
 * </p>
 * <p>
 * Validation is implemented by always returning a validation result object
 * indicating a successful validation. Transformation is implemented as a noop,
 * by simply returning the passed in object. The services of this class can be
 * obtained using a singleton instance.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DummyTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DummyTransformer implements Validator, Transformer
{
    /** Stores a reference to the singleton instance. */
    private static final DummyTransformer INSTANCE = new DummyTransformer();

    /**
     * Performs validation. Always returns a positive result.
     *
     * @param o the object to check
     * @param ctx the transformer context (ignored)
     * @return validation results
     */
    public ValidationResult isValid(Object o, TransformerContext ctx)
    {
        return DefaultValidationResult.VALID;
    }

    /**
     * Transforms the passed in object. This implementation simply returns the
     * same object.
     *
     * @param o the object to be transformed
     * @param ctx the transformer context (ignored)
     * @return the resulting object
     */
    public Object transform(Object o, TransformerContext ctx)
    {
        return o;
    }

    /**
     * Returns the singleton instance of this class.
     * @return the shared instance
     */
    public static DummyTransformer getInstance()
    {
        return INSTANCE;
    }
}

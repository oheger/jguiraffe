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
package net.sf.jguiraffe.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;

/**
 * <p>
 * A special {@link Validator} implementation that allows combining multiple
 * primitive validators.
 * </p>
 * <p>
 * An instance of this class can be initialized with an arbitrary number of
 * child validators. In its implementation of the <code>isValid()</code> method
 * the child validators are invoked one after the other. Configuration options
 * control whether validation should stop when the first validation error was
 * detected or whether in any case all child validators are to be invoked (in
 * the latter case really all error messages caused by validation errors can be
 * collected.)
 * </p>
 * <p>
 * In the {@link TransformerContext} passed to this validator's
 * <code>isValid()</code> method the {@link #PROP_SHORT_EVAL}
 * property will be checked. If it is defined, it overrides the flag set using
 * the {@link #setShortEvaluation(boolean)} method.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ChainValidator.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ChainValidator implements Validator
{
    /** Constant for the short evaluation property. */
    public static final String PROP_SHORT_EVAL = "shortEval";

    /** Stores a list with the child validators. */
    private List<ChildValidatorData> validators;

    /** The short evaluation flag. */
    private boolean shortEvaluation;

    /**
     * Creates a new instance of <code>ChainValidator</code>.
     */
    public ChainValidator()
    {
        validators = new ArrayList<ChildValidatorData>();
        setShortEvaluation(true);
    }

    /**
     * Returns the <code>shortEvaluation</code> flag.
     *
     * @return the flag for short evaluation
     */
    public boolean isShortEvaluation()
    {
        return shortEvaluation;
    }

    /**
     * Sets the <code>shortEvaluation</code> flag. This flag controls the
     * behavior of the <code>isValid()</code> method in case of validation
     * errors. If it is set, validation is aborted when the first child
     * validator detects an error. Otherwise all child validators will be
     * invoked and the results are collected.
     *
     * @param shortEvaluation the value of the flag
     */
    public void setShortEvaluation(boolean shortEvaluation)
    {
        this.shortEvaluation = shortEvaluation;
    }

    /**
     * Adds a child validator to this chain validator.
     *
     * @param child the child validator to be added (must not be <b>null</b>)
     * @throws IllegalArgumentException if the validator to be added is <b>null</b>
     */
    public void addChildValidator(Validator child)
    {
        addChildValidator(child, null);
    }

    /**
     * Adds a child validator to this chain validator and sets special
     * properties for the new child. If defined, these properties will be
     * available through the <code>TransformerContext</code> passed to the
     * <code>isValid()</code> method of the child.
     *
     * @param child the child validator to be added (must not be <b>null</b>)
     * @param props a map with properties for the new child validator
     * @throws IllegalArgumentException if the validator to be added is <b>null</b>
     */
    public void addChildValidator(Validator child, Map<String, Object> props)
    {
        if (child == null)
        {
            throw new IllegalArgumentException(
                    "Child validator must not be null!");
        }

        validators.add(new ChildValidatorData(child, props));
    }

    /**
     * Returns the number of child validators.
     *
     * @return the number of child validators
     */
    public int size()
    {
        return validators.size();
    }

    /**
     * Returns the child validator at the given index. The index is 0-based and
     * can be in the range 0 &lt;= <code>index</code> &lt; <code>size()</code>.
     *
     * @param index the index of the child validator
     * @return the child at this index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public Validator getChildValidator(int index)
    {
        return validators.get(index).getValidator();
    }

    /**
     * Validates the passed in object. This implementation delegates to the
     * child validators.
     *
     * @param o the object to be validated
     * @param ctx the transformer context
     * @return an object with the results of the validation
     */
    public ValidationResult isValid(Object o, TransformerContext ctx)
    {
        ValidationResult result = DefaultValidationResult.VALID;
        int cnt = size();

        if (cnt > 0)
        {
            boolean shortEval = doShortEvaluation(ctx);
            for (int i = 0; i < cnt; i++)
            {
                Validator v = getChildValidator(i);
                TransformerContext currentCtx = getContextForChildValidator(i,
                        ctx);
                ValidationResult vr = v.isValid(o, currentCtx);

                if (shortEval && !vr.isValid())
                {
                    result = vr;
                    break;
                }
                result = DefaultValidationResult.merge(result, vr);
            }
        }

        return result;
    }

    /**
     * Returns the <code>TransformerContext</code> to be used for the
     * specified child validator. If the child was added with custom properties,
     * a specialized context will be created allowing access to these
     * properties. Otherwise the default context will be returned.
     *
     * @param index the index of the child validator
     * @param ctx the original context
     * @return a context for this child validator
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    protected TransformerContext getContextForChildValidator(int index,
            TransformerContext ctx)
    {
        return validators.get(index).getContextForValidator(ctx);
    }

    /**
     * Checks whether short evaluation should be performed by the
     * <code>isValid()</code> method. This implementation checks whether the
     * {@link #PROP_SHORT_EVAL} property is defined in the passed
     * in context. If not, <code>isShortEvaluation()</code> will be called.
     *
     * @param ctx the current transformer context
     * @return a flag whether short evaluation should be performed
     */
    protected boolean doShortEvaluation(TransformerContext ctx)
    {
        Map<String, Object> props = ctx.properties();
        if (props.containsKey(PROP_SHORT_EVAL))
        {
            return BooleanUtils.toBoolean(String.valueOf(props
                    .get(PROP_SHORT_EVAL)));
        }
        else
        {
            return isShortEvaluation();
        }
    }

    /**
     * A helper class for storing information about a child validator.
     */
    private static class ChildValidatorData
    {
        /** Stores the validator. */
        private Validator validator;

        /** Stores properties for this validator. */
        private Map<String, Object> properties;

        /**
         * Creates a new instance of <code>ChildValidatorData</code> and
         * initializes it.
         *
         * @param v the validator
         * @param props the properties for this validator
         */
        public ChildValidatorData(Validator v, Map<String, Object> props)
        {
            validator = v;
            properties = props;
        }

        /**
         * Returns the child validator.
         *
         * @return the validator
         */
        public Validator getValidator()
        {
            return validator;
        }

        /**
         * Returns the transformer context to use for this child validator. If
         * properties are set, a wrapped context will be returned.
         *
         * @param ctx the current context
         * @return the context to use
         */
        public TransformerContext getContextForValidator(TransformerContext ctx)
        {
            return (properties != null) ? new TransformerContextPropertiesWrapper(
                    ctx, properties)
                    : ctx;
        }
    }
}

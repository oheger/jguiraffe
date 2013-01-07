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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.forms.ValidationPhase;
import net.sf.jguiraffe.gui.forms.ValidatorWrapper;
import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.ValidationResult;
import net.sf.jguiraffe.transform.Validator;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Tag;

/**
 * <p>
 * A base class for tag handler implementations that create validators for input
 * components.
 * </p>
 * <p>
 * There are two kinds of validators supported by the form builder framework:
 * <ul>
 * <li>plain (or primitive) validators</li>
 * <li>{@link net.sf.jguiraffe.transform.ChainValidator ChainValidator}s, which
 * can contain multiple plain validators</li>
 * </ul>
 * Sub classes of this class will deal with specific validator types. This base
 * class provides common functionality required in all cases.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ValidatorBaseTag.java 205 2012-01-29 18:29:57Z oheger $
 * @param <T> the type of validator handled by this base class
 */
public class ValidatorBaseTag<T extends Validator> extends
        TransformerBaseTag<T>
{
    /** Constant for the name of the phase attribute. */
    static final String ATTR_PHASE = "phase";

    /**
     * Creates a new instance of <code>ValidatorTag</code>.
     */
    public ValidatorBaseTag()
    {
        super();
        addIgnoreProperty(ATTR_PHASE);
    }

    /**
     * Creates a new instance of <code>ValidatorTag</code> and sets the
     * default class.
     *
     * @param defClass the default class to be used
     */
    public ValidatorBaseTag(Class<?> defClass)
    {
        super(defClass);
        addIgnoreProperty(ATTR_PHASE);
    }

    /**
     * Returns the validation phase for the current validator.
     *
     * @return the validation phase
     * @throws JellyTagException if the phase is not set or invalid
     */
    protected ValidationPhase getValidationPhase() throws JellyTagException
    {
        if (!getAttributes().containsKey(ATTR_PHASE))
        {
            if (getParent() instanceof ValidatorBaseTag<?>)
            {
                return ((ValidatorBaseTag<?>) getParent()).getValidationPhase();
            }
            return ValidationPhase.SYNTAX;
        }

        try
        {
            return ValidationPhase.valueOf(String.valueOf(
                    getAttributes().get(ATTR_PHASE)).toUpperCase());
        }
        catch (IllegalArgumentException iex)
        {
            throw new JellyTagException("Invalid ValidationPhase: "
                    + getAttributes().get(ATTR_PHASE));
        }
    }

    /**
     * Processes the specified input component tag. The validator will be passed
     * to this tag.
     *
     * @param tag the input component tag
     * @param bean the validator to pass
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected void handleInputComponentTag(InputComponentTag tag, T bean)
            throws JellyTagException
    {
        if (getValidationPhase() == ValidationPhase.SYNTAX)
        {
            tag.setFieldValidator(new ValidatorWrapperImpl(bean,
                    getTransformerContext()));
        }
        else
        {
            tag.setFormValidator(new ValidatorWrapperImpl(bean,
                    getTransformerContext()));
        }
    }

    /**
     * Processes the parent tag if it is no input component tag. This
     * implementation checks whether the tag is a
     * <code>{@link ValidatorsTag}</code>. If this is the case, the child
     * validator will be added to it.
     *
     * @param parent the parent tag
     * @param bean the validator bean
     * @return a flag whether the parent tag is supported
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected boolean handleOtherParent(Tag parent, T bean)
            throws JellyTagException
    {
        if (parent instanceof ValidatorsTag)
        {
            ((ValidatorsTag) parent).addChildValidator(bean, getProperties());
            return true;
        }

        return false;
    }

    /**
     * An implementation of the <code>ValidatorWrapper</code> interface that
     * wraps the validator created by this tag.
     */
    static class ValidatorWrapperImpl implements ValidatorWrapper
    {
        /** Stores the wrapped validator. */
        private Validator validator;

        /** Stores the transformer context. */
        private TransformerContext transformerContext;

        /**
         * Creates a new instance of <code>ValidatorWrapperImpl</code> and
         * initializes it.
         *
         * @param v the wrapped validator
         * @param ctx the transformer context to use
         */
        public ValidatorWrapperImpl(Validator v, TransformerContext ctx)
        {
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
         * Returns the <code>TransformerContext</code> to use.
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
}

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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.forms.DefaultValidatorWrapper;
import net.sf.jguiraffe.gui.forms.ValidationPhase;
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
            tag.setFieldValidator(new DefaultValidatorWrapper(bean,
                    getTransformerContext()));
        }
        else
        {
            tag.setFormValidator(new DefaultValidatorWrapper(bean,
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

}
